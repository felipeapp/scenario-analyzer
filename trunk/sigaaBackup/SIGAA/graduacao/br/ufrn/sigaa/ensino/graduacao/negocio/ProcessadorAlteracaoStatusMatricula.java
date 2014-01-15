/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/01/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador responsável pela alteração dos status de matrículas de discentes
 * em componentes curriculares
 *
 * @author David Pereira
 * @author André Dantas
 */
public class ProcessadorAlteracaoStatusMatricula extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoOperacaoMatricula matMov = (MovimentoOperacaoMatricula) mov;
		GenericDAO dao = getGenericDAO(matMov);
		
		try {
			if (!matMov.isAutomatico()) {
				// no caso de ser só uma matrícula
				if ( matMov.getMatriculas() == null && matMov.getMatricula() != null) {
					matMov.setMatriculas(new ArrayList<MatriculaComponente>(0));
					matMov.getMatriculas().add(matMov.getMatricula());
				}
		
				if ( matMov.getMatriculas() == null ) {
					throw new NegocioException("É necessário selecionar pelo menos uma matrícula para realizar a alteração de status");
				}
			}
		
			validate(mov);
		
			// se este movimento estiver sendo invocado automaticamente pelo TrancamentoTimer deve considerar as solicitações e não as matriculas
			if( matMov.isAutomatico() ){
				processarTrancamentoAutomatico(matMov);
		
			} else{ // se não for automático realiza o processamento normal
				processarAlteracaoStatus(matMov);
			}
			
			Set<Discente> discentes = new HashSet<Discente>(); 
			if (matMov.isAutomatico()){
				for( SolicitacaoTrancamentoMatricula sol : matMov.getSolicitacoesTrancamento() ){
					Discente d = sol.getMatriculaComponente().getDiscente().getDiscente();
					if (d != null)
						discentes.add( d );
				}
			}
			else{
				for( MatriculaComponente m : matMov.getMatriculas() ){
					if (m.getDiscente() != null)
						discentes.add( m.getDiscente().getDiscente() );
				}
			}
		
			for( Discente discente : discentes ){
				if (discente.isGraduacao())
					dao.updateField(DiscenteGraduacao.class, discente.getId(), "ultimaAtualizacaoTotais", null);
				else
					dao.updateField(DiscenteStricto.class, discente.getId(), "ultimaAtualizacaoTotais", null);
			}	
		
			return matMov.getMatriculas();
		} finally {
			dao.close();
		}

	}

	/**
	 * Este método executa a alteração de status da matrícula, ele só é invocado quando a alteração decorre de alguma operação executada pelo usuário. 
	 * Quando a alteração decorre do processamento das solicitações de trancamentos pendentes então o método chamado é processarTrancamentoAutomatico.
	 * @param matMov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void processarAlteracaoStatus(MovimentoOperacaoMatricula matMov) throws NegocioException, ArqException {
		Collection<MatriculaComponente> matriculas = matMov.getMatriculas();
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, matMov);
		try {
			for (MatriculaComponente mat : matriculas) {
				// Se for alteração geral de status da turma atribui o novo status a ser alterado.
				if (matMov.getCodMovimento().equals(SigaaListaComando.ALTERAR_STATUS_GERAL_TURMA))
					matMov.setNovaSituacao(mat.getNovaSituacaoMatricula());				
				
				// Atualizar o status da matrícula e gravar alteração											
				// Atualizar o status da matrícula e gravar alteração									
				if ( mat.getComponente().isSubUnidade() && !MetropoleDigitalHelper.isMetropoleDigital(mat.getComponente()) ) {
					ComponenteCurricular componente = dao.findByPrimaryKey(mat.getComponente().getId(), ComponenteCurricular.class);
					List<MatriculaComponente> subUnidades = dao.findMatriculasSubUnidadesByBloco(mat.getDiscente(), mat.getAno(), mat.getPeriodo(), componente.getBlocoSubUnidade());
									
					// no caso de trancamento, exclusão, cancelamento, etc, setar o mesmo status para todas as outras sub-unidades
					if (SituacaoMatricula.getSituacoesNegativas().contains(matMov.getNovaSituacao())) {
						for (MatriculaComponente mc : subUnidades) {
							MatriculaComponenteHelper.alterarSituacaoMatricula(mc, matMov.getNovaSituacao(), matMov, dao);
						}
					} else {
						MatriculaComponenteHelper.alterarSituacaoMatricula(mat, matMov.getNovaSituacao(), matMov, dao);
					}
					
				} else {
					MatriculaComponenteHelper.alterarSituacaoMatricula(mat, matMov.getNovaSituacao(),
							matMov, dao);
				}
			}			
		} finally {
			dao.close();
		}

	}
	
	/**
	 * Este método executa a alteração de status da matrícula, ele só é invocado quando a 
	 * alteração decorre do processamento das solicitações de trancamentos pendentes. 
	 * Quando a alteração decorre de alguma operação executada pelo usuário então o método chamado é processarAlteracaoStatus.
	 * @param matMov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void processarTrancamentoAutomatico(MovimentoOperacaoMatricula matMov) throws NegocioException, ArqException {

		SituacaoMatricula novaSituacao = matMov.getNovaSituacao();
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, matMov);
		try {
			//se as solicitações forem nulas ou a nova situação for diferente de TRANCAR
			if( matMov.getSolicitacoesTrancamento() == null || matMov.getSolicitacoesTrancamento().isEmpty() || !novaSituacao.equals(SituacaoMatricula.TRANCADO)){
				throw new NegocioException( "Só é possível realizar alteração automática de situações de matrículas para o caso de trancamento. Nesta operação as solicitações de trancamento não podem estar nulas." );
			}

			Collection<SolicitacaoTrancamentoMatricula> solicitacoes = matMov.getSolicitacoesTrancamento();

			for (SolicitacaoTrancamentoMatricula sol : solicitacoes) {
				if( !matMov.isADistancia() && (sol.getSituacao() == SolicitacaoTrancamentoMatricula.CANCELADO || sol.getSituacao() == SolicitacaoTrancamentoMatricula.TRANCADO) )
					throw new NegocioException("Não é possível alterar trancar matriculas cujo solicitação está com a situação TRANCADA ou CANCELADA.");

				// Atualizar o status da matrícula e gravar alteração
				AlteracaoMatricula alteracao = MatriculaComponenteHelper.alterarSituacaoMatricula(sol.getMatriculaComponente(), novaSituacao, matMov, dao);			
				dao.updateFields(SolicitacaoTrancamentoMatricula.class, sol.getId(), new String[] {"situacao", "alteracaoMatricula"}, new Object[] {SolicitacaoTrancamentoMatricula.TRANCADO, alteracao.getId()});

			}
		} finally {
			dao.close();
		}

	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

		ListaMensagens listaErros = new ListaMensagens();
		
		MovimentoOperacaoMatricula matMov = (MovimentoOperacaoMatricula) mov;

		if (matMov.isAutomatico()) {
			return;
		}
		
		if (!matMov.isTrancamentoProgramaPosteriori())
			checkRole(new int[] { SigaaPapeis.ADMINISTRADOR_DAE , SigaaPapeis.DAE , SigaaPapeis.CDP,
					SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_CURSO
					,SigaaPapeis.PPG ,SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.GESTOR_LATO,
					SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO, 
					SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.GESTOR_INFANTIL}, mov);

		// se o usuário for chefe ou secretário de departamento quando ele remove turma de ensino individual
		// automaticamente são canceladas as matriculas da turma.
		// Se não for este caso o chefe não pode ter permissão de alterar matriculas em turmas
		if( matMov.getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO)
				&& !matMov.isCancelamentoMatriculasEnsinoIndividual() 
				&& matMov.getSubsistema() == SigaaSubsistemas.GRADUACAO.getId()){
			throw new SegurancaException();
		}

		DiscenteAdapter discente = matMov.getMatriculas().iterator().next().getDiscente();
		
		SituacaoMatricula situacaoOriginal = new SituacaoMatricula();
		if( matMov.getMatriculas() != null ){
			GenericDAO dao = getGenericDAO(mov);
			try {
				situacaoOriginal = dao.findByPrimaryKey(matMov.getMatriculas().iterator().next().getId(), MatriculaComponente.class).getSituacaoMatricula();
				// Evitar Lazy
				situacaoOriginal.toString();
			} finally {
				dao.close();
			}
		}

		ListaMensagens erros = new ListaMensagens();
		// validações específicas (não precisa validar se for ADM do DAE)
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) &&
				!( situacaoOriginal != null && SituacaoMatricula.getSituacoesAproveitadas().contains( situacaoOriginal ) && mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE) ) ) {
			MatriculaComponenteHelper.validarMatriculaComponente(discente, matMov.getMatriculas(), matMov.getNovaSituacao() , erros);
		}
		
		listaErros.addAll(erros);
		
		checkValidation(listaErros);

	}

	/**
	 * Verifica se o trancamento pode ser executado de acordo com as regras de negócio existentes.
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void validarTracamento(Movimento mov) throws NegocioException, ArqException {

		MovimentoOperacaoMatricula movTrancamento = (MovimentoOperacaoMatricula) mov;
		if( !movTrancamento.isAutomatico() ) //se o movimento tiver sido invocado automaticamente não deve executar o checkRole pois não tem usuário logado
			checkRole(new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
					SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.DAE , SigaaPapeis.GESTOR_TECNICO}, mov);

		ListaMensagens erros = new ListaMensagens();

		MatriculaComponente matricula = movTrancamento.getMatricula();

		Collection<MatriculaComponente> matriculas = movTrancamento.getMatriculas();

		// Realizar validações
		if( movTrancamento.isAutomatico() ){
			matriculas = new ArrayList<MatriculaComponente>();

			if( movTrancamento.getSolicitacoesTrancamento() == null || movTrancamento.getSolicitacoesTrancamento().isEmpty()  )
				throw new NegocioException("Para consolidar as solicitações de trancamento é necessário setar as solicitações.");

			for( SolicitacaoTrancamentoMatricula sol : movTrancamento.getSolicitacoesTrancamento() ){
				matriculas.add(sol.getMatriculaComponente());
			}

		} else if (matriculas == null){
			matriculas = new ArrayList<MatriculaComponente>(0);
			if (matricula != null) {
				matriculas.add(matricula);
			}
		}

		Discente d = null;
		for (MatriculaComponente mat : matriculas) {
			TrancamentoMatriculaValidator.validar(mat, erros);
			if (d == null)
				d = mat.getDiscente().getDiscente();
		}
		List<MatriculaComponente> listaCorequisitos = TrancamentoMatriculaValidator.verificarCorequisitos(d, matriculas);
		if( listaCorequisitos != null && !listaCorequisitos.isEmpty() ){
			StringBuffer msg = new StringBuffer("Não é possível efetuar o trancamento. Você está matriculado(a) no(s) seguinte(s) " +
					"componentes co-requisitos do(s) componente(s) selecionado(s) para ser(em) trancado(s): <br>" );
			for( MatriculaComponente mc : listaCorequisitos ){
				msg.append( mc.getComponente().getDescricaoResumida() + ";<br>" );
			}
			erros.addErro(msg.toString());
		}

		Collection<MatriculaComponente> listasubunidades = TrancamentoMatriculaValidator.verificarSubunidades(matriculas);
		if( !isEmpty(listasubunidades) ){
			StringBuffer msg = new StringBuffer("Não é possível efetuar o trancamento. Você está matriculado(a) no(s) seguinte(s) " +
					"subunidades do(s) componente(s) de bloco selecionado(s) para ser(em) trancado(s): <br>" );
			for( MatriculaComponente mc : listasubunidades ){
				msg.append( mc.getComponente().getDescricaoResumida() + ";<br>" );
			}
			erros.addErro(msg.toString());
		}

	}

}