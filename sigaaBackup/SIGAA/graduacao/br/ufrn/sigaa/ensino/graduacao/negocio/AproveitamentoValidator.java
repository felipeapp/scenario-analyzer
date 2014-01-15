/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/01/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.tecnico.dao.EstruturaCurricularTecDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Validador de aproveitamento de componentes curriculares. 
 * 
 * @author Victor Hugo
 *
 */
public class AproveitamentoValidator {

	/**
	 * Valida o discente que irá realizar o aproveitamento automático
	 * REGRAS:
	 * APENAS ATIVOS E FORMANDOS PODEM REALIZAR O APROVEITAMENTO
	 * APENAS DISCENTES REGULARES PODEM REALZIAR O APROVEITAMENTO.
	 * @param discenteDestino
	 * @param lista
	 */
	public static void validarDiscenteDestino(DiscenteGraduacao discenteDestino, ListaMensagens lista){

		if( discenteDestino.getStatus() != StatusDiscente.ATIVO && discenteDestino.getStatus() != StatusDiscente.FORMANDO )
			lista.addErro("Apenas discente com o status ativo podem realizar aproveitamento automático.");

		if( discenteDestino.getTipo() != Discente.REGULAR )
			lista.addErro("Apenas discentes regulares podem realiza o aproveitamento automático.");

	}

	/**
	 * Verifica se o aproveitamento automático de componentes curriculares é possível para o discente, ou seja, 
	 * se os componentes curriculares pagos em um vínculo anterior podem ser aproveitados no vínculo atual do discente.
	 * 
	 * @param origem - Vínculo anterior do discente com a instituição
	 * @param destino - Vínculo atual do discente com a instituição
	 * @param lista
	 */
	public static void validaAproveitamentoAutomatico( DiscenteGraduacao origem, DiscenteGraduacao destino, ListaMensagens lista ){

		if( origem.getPessoa().getId() == destino.getPessoa().getId() ){
			/** se as pessoas dos dois discentes tiverem o mesmo ID pode realizar o aproveitamento */
			return;
		} else if( origem.getPessoa().getCpf_cnpj() != null &&
				origem.getPessoa().getCpf_cnpj().equals( destino.getPessoa().getCpf_cnpj() ) ){
			/** se as pessoas tiverem ID diferentes e tiverem o mesmo CPF pode realizar o aproveitamento */
			return;
		} else if ( origem.getPessoa().getNome().equalsIgnoreCase(destino.getPessoa().getNome()) &&
				origem.getPessoa().getDataNascimento().equals(destino.getPessoa().getDataNascimento()) ){
			/** se as pessoas tiverem ID e CPF diferente E tiverem o mesmo nome e mesma data de nascimento pode realizar o aproveitamento */
			return;
		} else{
			lista.addErro("Só é possível realizar o aproveitamento entre duas matrículas que pertençam a mesma pessoa.");
		}

	}

	/**
	 * Valida se é possível realizar o aproveitamento de um determinado componente curricular para o discente
	 * especificado. 
	 * 
	 * @param discente - Discente que solicitou o aproveitamento
	 * @param matricula - Matrícula do discente no componente curricular
	 * @param param - Parâmetros referente a unidade gestora do discente
	 * @param lista - Lista de mensagens de validação
	 * @throws ArqException
	 */
	public static void validaAproveitamento(DiscenteAdapter discente, MatriculaComponente matricula, ParametrosGestoraAcademica param,
			ListaMensagens lista, UsuarioGeral usuarioLogado) throws ArqException {

		// validando media mínima
		
		// Testa se o caso de uso atribui Nota e frequência ao aproveitamento de estudos.
		boolean atribuirNotaFrequencia = param.isExigeNotaAproveitamento();
		
		if (matricula.getComponente().isNecessitaMediaFinal() 
				&& !matricula.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_DISPENSADO) 
				&& !MetropoleDigitalHelper.isMetropoleDigital(discente) 
				&& atribuirNotaFrequencia && matricula.getMediaFinal() < param.getMediaMinimaAprovacao()) {
			lista.addErro("Esse discente não pode aproveitar o componente "+matricula.getComponenteDescricaoResumida()+".<br>"
					+ ( param.getMetodoAvaliacao() == br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao.CONCEITO ? " O conceito informado " : " A média final informada "  )
					+ " não é suficiente para aprovação.");
			return;
		}

		ComponenteCurricular componente = matricula.getComponente();

		// se o status do aluno permite um cadastro de
		// aproveitamento
		if (discente.getStatus() == StatusDiscente.CANCELADO //|| discente.getStatus() == StatusDiscente.CONCLUIDO
				|| discente.getStatus() == StatusDiscente.JUBILADO) {
			lista.addErro("Não é possível cadastrar um aproveitamento do componente "+matricula.getComponenteDescricaoResumida()+" para esse discente.<br>"
					+ "O aluno não possui mais vínculo com a instituição (Cancelado, Concluído ou Jubilado)");
			return;
		}


		// validando ano-período informado
		if ( matricula.getAno() != null && matricula.getPeriodo() != null ) {
			String msg = DiscenteHelper.validarPeriodoDiscente(discente, matricula.getAno(), matricula.getPeriodo());
			if (msg != null) {
				lista.addErro(msg);
				return;
			}
		}
		
		MatriculaComponenteDao matdao = getDAO(MatriculaComponenteDao.class);
		try {
			// pra ser dispensado o aluno não pode ter reprovado no componente
			if (matricula.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_DISPENSADO) 
					&& !usuarioLogado.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE)) {
				
				Collection<MatriculaComponente> matriculasReprovadasETrancadas = matdao.findByDiscenteEDisciplina(discente, componente, SituacaoMatricula.REPROVADO, SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO_MEDIA_FALTA, SituacaoMatricula.TRANCADO);
				
				if (matriculasReprovadasETrancadas != null && !matriculasReprovadasETrancadas.isEmpty()) {
					lista.addErro("Esse discente não pode aproveitar o componente " + matricula.getComponenteDescricaoResumida() + ".<br/>"
							+ "Ele já foi reprovado em turmas dessa disciplina ou já trancou o componente.");
					return;
				}
			}
			
		} finally {
			matdao.close();
		}
		
		
		if( !(componente.isConteudoVariavel() && discente.isStricto()))		
			validarPagoEquivalente(discente, componente, lista);
		
		
		// Validar solicitações de matrícula 
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
		try {
			Collection<SolicitacaoMatricula> solicitacoes =  solicitacaoDao.findEmEspera(discente, componente, matricula.getAno() != null ? new Integer(matricula.getAno()) : null, matricula.getPeriodo() != null ? new Integer(matricula.getPeriodo()) : null);
			if (!isEmpty(solicitacoes)) {
				lista.addErro("Não é possível cadastrar um aproveitamento para este componente pois existe uma solicitação de matrícula aguardando processamento." +
						" Por favor, tente novamente após o processamento das matrículas.");
			}
			
		} finally {
			solicitacaoDao.close();
		}
		
	}
	
	/**
	 * Retorna uma instância do do DAO.
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	
	
	/**
	 * Verifica se o discente já pagou a disciplina ou alguma outra equivalente. O aproveitamento só pode ser feito
	 * caso esta situação não tenha ocorrido.
	 * 
	 * @param discente
	 * @param componente
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarPagoEquivalente(DiscenteAdapter discente, ComponenteCurricular componente, ListaMensagens erros) throws ArqException {

		// verifica se a disciplina pertence ao currículo do curso do aluno
		if (discente.getNivel() == NivelEnsino.TECNICO) {
			
			EstruturaCurricularTecDao ecDao = getDAO(EstruturaCurricularTecDao.class);
			EstruturaCurricularTecnica curriculo = ecDao.findByDiscente(discente.getId());
			
			try {
			
				// se a disciplina não pertence à E.C.
				if (!ecDao.contemDisciplina(curriculo.getId(), componente.getId())) {
					
					erros.addErro("Esse discente não pode aproveitar essa disciplina "+componente.getDescricaoResumida()+".<br>"
							+ "Aproveitamentos só podem ser realizados para disciplinas pertencentes "
							+ "à estrutura curricular ativa do curso do aluno");
					return;
				}
				
			} finally {
				ecDao.close();
			}
		}
		/**
		 * verificando se é atividade
		 */
//		Regra de negócio inativada mediante solicitação da #37509.
//		if(componente.isAtividade() ){
//			erros.addMensagem(MensagensGraduacao.NEGAR_APROVEITAMENTO_COMPONENTE_TIPO_ATIVIDADE);
//			return;
//		}

		/*
		 * Só pode aproveitar uma disciplina caso ainda não tenha pago a
		 * disciplina (ou equivalente)
		 */
		DiscenteDao dao = getDAO(DiscenteDao.class);
		MatriculaComponenteDao matdao = getDAO(MatriculaComponenteDao.class);
		try {

			Collection<MatriculaComponente> matriculasMatriculadas = matdao.findByDiscenteEDisciplina(discente,
					componente, SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA);
			Collection<MatriculaComponente> matriculasPagas = matdao.findByDiscenteEDisciplina(discente,
					componente, SituacaoMatricula.getSituacoesPagasArray());
			
			int qtdMatriculadas = ( !isEmpty(matriculasMatriculadas) ? matriculasMatriculadas.size() : 0);
			int qtdPagas = (!isEmpty(matriculasPagas) ? matriculasPagas.size() : 0);

			String msgSubunidades = "";
			if ( componente.isBloco() ) {
				int qtdSubunidades = matdao.findMatriculadasSubUnidadesByBloco(discente, componente).size();
				if (qtdSubunidades > 0) {
					msgSubunidades = " (ou de suas sub-unidades)";
				}
				
				qtdMatriculadas += qtdSubunidades;
			}
			
			// testa se está matriculado
			if (!componente.isPermiteCancelarMatricula()){
				if (qtdMatriculadas > 0 && qtdPagas + qtdMatriculadas + 1 > componente.getQtdMaximaMatriculas()) {
					erros.addErro("Esse discente não pode aproveitar o componente "+componente.getDescricaoResumida()+", "
							+ "pois está matriculado em turmas desse componente" + msgSubunidades + ".");
					return;
				}				
			}

			// testa se já pagou
			if (qtdPagas > 0 && qtdPagas + qtdMatriculadas + 1 > componente.getQtdMaximaMatriculas()) {
				erros.addErro("Esse discente não pode aproveitar esse componente "+componente.getDescricaoResumida()+".<br>"
						+ "Ele já foi aprovado (ou aproveitado) em turmas desse componente. " +
								"O discente excedeu o limite de matrículas/aproveitamentos neste componente curricular.");
				return;
			}

			Collection<ComponenteCurricular> concluidos = dao.findComponentesCurricularesConcluidos(discente);

			// se NÃO pagou as disciplinas equivalentes
			if (componente.getEquivalencia() != null && !componente.getEquivalencia().trim().equals("")
					&& ExpressaoUtil.eval(componente.getEquivalencia(), concluidos)) {
				erros.addErro("Esse discente não pode aproveitar esse componente "+componente.getDescricaoResumida()+".<br>"
						+ "Ele já pagou todos os componentes equivalentes.");
				return;
			}
		} finally {
			matdao.close();
			dao.close();
		}
	}

}
