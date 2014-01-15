/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on May 30, 2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoTrancamentoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.TrancamentoMatriculaMov;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.jsf.PortalCoordenacaoStrictoMBean;
import br.ufrn.sigaa.portal.jsf.PortalCoordenadorGraduacaoMBean;

/**
 * MBean responsável pelo caso de uso de atender solicitações de
 * trancamento de matrícula realizado pelo coordenador do curso.
 * 
 * @author Victor Hugo
 * 
 */
@Component("atenderTrancamentoMatricula")
@Scope("session")
public class AtenderTrancamentoMatriculaMBean extends SigaaAbstractController<SolicitacaoTrancamentoMatricula> {

	/** Define que o orientador concorda com o trancamento. */
	public static final int CONCORDO = 1;
	/** Define que o orientador discorda com o trancamento. */
	public static final int ORIENTAR_NAO_TRANCAMENTO = 2;

	/** Link para o a lista de alunos com trancamento. */
	public static final String LISTA_ALUNOS = "/ensino/trancamento_matricula/atendimento_solicitacoes/lista_alunos.jsp";
	/** Link para o a lista de solicitações de trancamento dos alunos. */
	public static final String LISTA_SOLICITACOES = "/ensino/trancamento_matricula/atendimento_solicitacoes/solicitacao_aluno.jsp";

	/** Coleção de discente com trancamento pendentes.*/
	private Collection<Discente> discentes;

	/** Usuário do discente ao qual será enviado uma mensagem.*/
	private Usuario usuarioDiscente;

	/** Discente sob orientação.*/
	private DiscenteAdapter discente;

	/** Coleção de solicitações de trancamento de matrícula do discente.*/
	private Collection<SolicitacaoTrancamentoMatricula> solicitacoes;

	/** Coleção de solicitações de trancamento de matrícula de todos discentes.*/
	private Set<SolicitacaoTrancamentoMatricula> todasSolicitacoes;

	/** Indica se o curso é de Ensino à Distância.*/
	private boolean aDistancia;

	/** Indica se é pra mostrar apenas as solicitações de trancamento dos orientandos de graduação ou de pós. */
	private boolean graduacao;

	/**
	 * Inicia o atendimento de alunos de ensino a distância com solicitação de trancamento pendentes.
	 * Chamado por /ead/menu.jsp
	 * 			   /graduacao/menu_coordenador.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarAtendimentoSolicitacaoEad() throws SegurancaException, DAOException {
		aDistancia = true;
		return iniciarAtendimentoSolicitacao();
	}


	/**
	 * Inicia o atendimento do trancamento de orientandos de graduação.
	 * Deve trazer apenas as ORIENTACOES ACADEMICAS.
	 * Chamado por /portais/docente/menu_docente.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarAtendimentoSolicitacaoGraduacao() throws SegurancaException, DAOException {
		graduacao = true;
		return iniciarAtendimentoSolicitacao();
	}

	/**
	 * Inicia o atendimento  de orientandos de PÓS GRADUAÇÃO.
	 * Deve trazer apenas as ORIENTACOES DE PÓS.
	 * Chamado por /portais/docente/menu_docente.jsp
	 *             /stricto/coordenacao.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarAtendimentoSolicitacaoStricto() throws SegurancaException, DAOException {
		graduacao = false;
		return iniciarAtendimentoSolicitacao();
	}

	/**
	 * Inicia a orientação dos alunos que possuem solicitação de trancamento de matrícula pendentes.
	 * Chamado por /ead/menu.jsp
	 *             /ensino/trancamento_matricula/atendimento_solicitacoes/solicitacao_aluno.jsp
	 *             /graduacao/menus/aluno.jsp
	 *             /graduacao/coordenador.jsp
	 *             /graduacao/menu_coordenador.jsp
	 *             /portais/docente/menu_docente.jsp
	 *             /stricto/coordenacao.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarAtendimentoSolicitacao() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.DAE,
				SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.ORIENTADOR_ACADEMICO,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.ORIENTADOR_STRICTO);


		CalendarioAcademico calendario = getCalendarioVigente();

		// se for graduação e o calendário acadêmico não for graduação
		// força a busca pelo calendário da graduação
		if (graduacao && !NivelEnsino.isGraduacao(calendario.getNivel()))
			calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();

		// verificando período de trancamento
		// se for stricto não verifica		
		if (graduacao && (calendario.getInicioTrancamentoTurma() == null || calendario.getFimTrancamentoTurma() == null)) {
			addMensagemErro("O período de solicitações de trancamento de matrícula não está definido no calendário acadêmico para o período atual.");
			return null;
		}

		if( graduacao && !aDistancia && !calendario.isPeriodoTrancamentoTurmas()){
			addMensagemErro("O atendimento de solicitações de trancamento de matrícula só pode ser realizado dentro do prazo de trancamento.");
			return null;
		}

		SolicitacaoTrancamentoMatriculaDao daoSolicitacao = getDAO(SolicitacaoTrancamentoMatriculaDao.class);
		DiscenteDao daoDiscente = getDAO( DiscenteDao.class );
		discentes = new HashSet<Discente>();
		todasSolicitacoes = new LinkedHashSet<SolicitacaoTrancamentoMatricula>();
		if( (aDistancia || graduacao) && isUserInRole(SigaaPapeis.COORDENADOR_CURSO) || isUserInRole(SigaaPapeis.SECRETARIA_COORDENACAO) ){

			Curso curso = null;

			if( isUserInRole(SigaaPapeis.COORDENADOR_CURSO) ){
				
				if(isPortalCoordenadorGraduacao() && !isEmpty(getCursoAtualCoordenacao())) {
					curso = getCursoAtualCoordenacao();
				} else {				
					CoordenacaoCursoDao coordCursoDao = getDAO( CoordenacaoCursoDao.class );
					Collection<CoordenacaoCurso> coordenacoes = coordCursoDao.findByServidor(getServidorUsuario().getId(), 0, NivelEnsino.GRADUACAO, null);
	
					CoordenacaoCurso cc;
					if (coordenacoes != null && !coordenacoes.isEmpty()) {
						Iterator<CoordenacaoCurso> it = coordenacoes.iterator();
						cc = it.next();
					}else{
						addMensagemErro("Apenas coordenador de curso graduação pode realizar esta operação.");
						return null;
					}
					curso = cc.getCurso();				
				}
			} else if( isUserInRole(SigaaPapeis.SECRETARIA_COORDENACAO) ){
				curso = getCursoAtualCoordenacao();
			}

			discentes.addAll(daoSolicitacao.findBySolicitacoesTrancamentoPendentes( curso, null, calendario.getAno(), calendario.getPeriodo(), false ));

			todasSolicitacoes.addAll( daoSolicitacao.findByCursoSituacao(curso, null,
					calendario.getAno(), calendario.getPeriodo(),
					SolicitacaoTrancamentoMatricula.CANCELADO,
					SolicitacaoTrancamentoMatricula.RECUSADO,
					SolicitacaoTrancamentoMatricula.TRANCADO,
					SolicitacaoTrancamentoMatricula.VISTO) );
		}
		
		if(!graduacao && isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO) || isUserInRole(SigaaPapeis.SECRETARIA_POS) ){
			
			Unidade programa = getProgramaStricto() != null ? getProgramaStricto() : getUsuarioLogado().getVinculoAtivo().getUnidade();
			
			if ( programa != null ){
				discentes.addAll(daoSolicitacao.findBySolicitacoesTrancamentoPendentes( null, programa, calendario.getAno(), calendario.getPeriodo(), false ));
	
				todasSolicitacoes.addAll( daoSolicitacao.findByCursoSituacao(null, programa,
						calendario.getAno(), calendario.getPeriodo(),
						SolicitacaoTrancamentoMatricula.CANCELADO,
						SolicitacaoTrancamentoMatricula.RECUSADO,
						SolicitacaoTrancamentoMatricula.TRANCADO,
						SolicitacaoTrancamentoMatricula.VISTO) );
			}	

		}

		if( isUserInRole(SigaaPapeis.ORIENTADOR_ACADEMICO, SigaaPapeis.ORIENTADOR_STRICTO) ){

			Collection<SolicitacaoTrancamentoMatricula> solicitacoes = null;

			// Buscar solicitações
			if( graduacao ){
				solicitacoes = daoSolicitacao.findDiscenteByOrientadorAcademicoSituacao(getServidorUsuario(), getDocenteExternoUsuario(),
						NivelEnsino.GRADUACAO, SolicitacaoTrancamentoMatricula.SOLICITADO);
				todasSolicitacoes.addAll( daoSolicitacao.findAllSolicitacoesByOrientadorAcademicoSituacao(getServidorUsuario(),
						getDocenteExternoUsuario(), calendario.getAno(), calendario.getPeriodo(), NivelEnsino.GRADUACAO,
						SolicitacaoTrancamentoMatricula.CANCELADO,
						SolicitacaoTrancamentoMatricula.RECUSADO,
						SolicitacaoTrancamentoMatricula.TRANCADO,
						SolicitacaoTrancamentoMatricula.VISTO) );
			} else{
				solicitacoes = daoSolicitacao.findDiscenteByOrientadorAcademicoSituacao(getServidorUsuario(), getDocenteExternoUsuario(),
						NivelEnsino.STRICTO, SolicitacaoTrancamentoMatricula.SOLICITADO);
				todasSolicitacoes.addAll( daoSolicitacao.findAllSolicitacoesByOrientadorAcademicoSituacao(getServidorUsuario(),
						getDocenteExternoUsuario(), calendario.getAno(), calendario.getPeriodo(), NivelEnsino.STRICTO,
						SolicitacaoTrancamentoMatricula.CANCELADO,
						SolicitacaoTrancamentoMatricula.RECUSADO,
						SolicitacaoTrancamentoMatricula.TRANCADO,
						SolicitacaoTrancamentoMatricula.VISTO) );
			}

			if( !isEmpty( solicitacoes ) ){
				for( SolicitacaoTrancamentoMatricula stm : solicitacoes )
					discentes.add(stm.getMatriculaComponente().getDiscente().getDiscente());

			}

		} else if (isUserInRole(SigaaPapeis.COORDENADOR_GERAL_EAD)) {
			discentes.addAll( daoSolicitacao.findAllEad() );
			todasSolicitacoes.addAll( daoSolicitacao.findProcessadasEAD( calendario.getAno(), calendario.getPeriodo() ) );
		}

		if( isUserInRole(SigaaPapeis.DAE) && !getSubSistema().equals(SigaaSubsistemas.PORTAL_DOCENTE) ){
			discentes.addAll( daoDiscente.findBySolicitacoesTrancamentoPendentesEspecial( NivelEnsino.GRADUACAO ) );
		}

		return forward(LISTA_ALUNOS);
	}

	/** Seleciona um discente de Ensino à Distância para orientar.
	 * Chamado por /ensino/trancamento_matricula/atendimento_solicitacoes
	 *             /graduacao/coordenador.jsp
	 *             /stricto/coordenacao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String selecionarAlunoEad() throws ArqException {
		aDistancia = true;
		return selecionarAluno();
	}

	/**
	 * Carrega as solicitações pendentes do discente selecionado.
	 * Chamado por /ensino/trancamento_matricula/atendimento_solicitacoes/lista_alunos.jsp
	 *             /graduacao/coordenador.jsp
	 *             /stricto/coordenacao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String selecionarAluno() throws ArqException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.DAE,
				SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.ORIENTADOR_ACADEMICO,
				SigaaPapeis.ORIENTADOR_STRICTO, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS );

		Integer id = getParameterInt("idAluno");

		if( id == null || id == 0 ){
			addMensagemErro("Por favor selecione um aluno.");
			return null;
		}

		SolicitacaoTrancamentoMatriculaDao dao = getDAO( SolicitacaoTrancamentoMatriculaDao.class );
		UsuarioDao daoUsr = getDAO( UsuarioDao.class );

		discente = getDAO(DiscenteDao.class).findByPK(id);

		/**
		 * verificando período de trancamento
		 * se for stricto não verifica
		 */
		if( discente.isGraduacao() && !aDistancia && !CalendarioAcademicoHelper.getCalendario(discente).isPeriodoTrancamentoTurmas() ) {
			addMensagemErro("O atendimento de solicitações de trancamento de matrícula só pode ser realizado dentro do prazo de trancamento.");
			return null;
		}

		try {
			solicitacoes = dao.findByDiscente(id, SolicitacaoTrancamentoMatricula.SOLICITADO);
			if( solicitacoes == null || solicitacoes.isEmpty() ){
				addMensagemErro("Não há nenhuma solicitação de trancamento de matrícula pendente deste aluno;");
				return null;
			}

			usuarioDiscente = daoUsr.findByDiscente( discente.getDiscente() );

		} catch (DAOException e) {
			addMensagemErro("Não foi possível carregar as solicitações deste aluno. Contacte a administração do sistema.");
			notifyError(e);
			e.printStackTrace();
		}

		prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_TRANCAMENTO);

		return forward(LISTA_SOLICITACOES);
	}

	/**
	 * Persiste o atendimento das solicitações.
	 * Chamado por /ensino/trancamento_matricula/atendimento_solicitacoes
	 * @return
	 * @throws ArqException
	 */
	public String atenderSolicitacao() throws ArqException{

		//final String prefixo = "resultado_";
		final String prefixo_orientacao = "orientacao_";

		String[] aceitos = getParameterValues( "aceitos" );
		String[] orientacoes = getParameterValues( "orientacoes" );

		ArrayList<SolicitacaoTrancamentoMatricula> solicitacoesAtendidas = new ArrayList<SolicitacaoTrancamentoMatricula>();
		GenericDAO dao = getGenericDAO();

		if( aceitos != null ){
			for( String s : aceitos ){
				int id = Integer.parseInt(s);
				SolicitacaoTrancamentoMatricula solicitacao = dao.findByPrimaryKey(id, SolicitacaoTrancamentoMatricula.class);
				String orientacao = getParameter( prefixo_orientacao + solicitacao.getId() );
				solicitacao.setReplica(orientacao);
				solicitacao.setParecerFavoravelSolicitacao(true);
				solicitacoesAtendidas.add(solicitacao);
			}
		}

		if( orientacoes != null ){
			for( String s : orientacoes ){
				int id = Integer.parseInt(s);
				SolicitacaoTrancamentoMatricula solicitacao = dao.findByPrimaryKey(id, SolicitacaoTrancamentoMatricula.class);

				/**
				 * Se for marcado "Orientar não trancamento", o usuário deve informar uma orientação.
				 */
				String orientacao = getParameter( prefixo_orientacao + solicitacao.getId() );
				if( orientacao == null || orientacao.length() == 0 ){
					addMensagemErro("É necessário fornecer uma orientação para o aluno caso marque 'Orientar Não Trancamento'.");
					return null;
				}
				solicitacao.setReplica(orientacao);
				solicitacao.setParecerFavoravelSolicitacao(false);
				solicitacoesAtendidas.add(solicitacao);
			}
		}

		if( solicitacoesAtendidas == null || solicitacoesAtendidas.isEmpty() ){
			addMensagemErro("Você deve atender pelo menos uma das solicitações para poder confirmar o atendimento.");
			return null;
		}

		try {

			TrancamentoMatriculaMov mov = new TrancamentoMatriculaMov();
			mov.setCalendarioAcademicoAtual(getCalendarioVigente());
			mov.setDiscente(discente);
			mov.setSolicitacoes( solicitacoesAtendidas );
			mov.setADistancia(aDistancia);
			mov.setCodMovimento( SigaaListaComando.ANALISAR_SOLICITACAO_TRANCAMENTO );
			execute(mov, getCurrentRequest());

			addMessage("Orientação de Trancamento registrada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
			return null;
		}

		if ( getAcessoMenu().isCoordenadorCursoGrad() && getSubSistema().equals( SigaaSubsistemas.PORTAL_COORDENADOR ) ) {
			((PortalCoordenadorGraduacaoMBean) getMBean("portalCoordenadorGrad")).recarregarInformacoesPortal();
		}
		if ( getAcessoMenu().isCoordenadorCursoStricto() && getSubSistema().equals( SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO ) ) {
			((PortalCoordenacaoStrictoMBean) getMBean("portalCoordenacaoStrictoBean")).recarregarInformacoesPortal();
		}

		return iniciarAtendimentoSolicitacao();
	}

	/** Retorna a coleção de discente com trancamento pendentes.
	 * @return
	 */
	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	/** Seta a coleção de discente com trancamento pendentes.
	 * @param discentes
	 */
	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

	/** Retorna a coleção de solicitações de trancamento de matrícula do discente.
	 * @return
	 */
	public Collection<SolicitacaoTrancamentoMatricula> getSolicitacoes() {
		return solicitacoes;
	}

	/** Seta a coleção de solicitações de trancamento de matrícula do discente.
	 * @param solicitacoes
	 */
	public void setSolicitacoes(
			Collection<SolicitacaoTrancamentoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	/** Retorna o discente sob orientação.
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/** Seta o discente sob orientação.
	 * @param discente
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	/** Retorna o usuário do discente ao qual será enviado uma mensagem.
	 * @return
	 */
	public Usuario getUsuarioDiscente() {
		return usuarioDiscente;
	}

	/** Seta o usuário do discente ao qual será enviado uma mensagem.
	 * @param usuarioDiscente
	 */
	public void setUsuarioDiscente(Usuario usuarioDiscente) {
		this.usuarioDiscente = usuarioDiscente;
	}

	/** Indica se o curso é de Ensino à Distância.
	 * @return
	 */
	public boolean isDistancia() {
		return aDistancia;
	}

	/** Retorna a coleção de solicitações de trancamento de matrícula de todos discentes.
	 * @return
	 */
	public Set<SolicitacaoTrancamentoMatricula> getTodasSolicitacoes() {
		return todasSolicitacoes;
	}

	/** Seta a coleção de solicitações de trancamento de matrícula de todos discentes.
	 * @param todasSolicitacoes
	 */
	public void setTodasSolicitacoes(Set<SolicitacaoTrancamentoMatricula> todasSolicitacoes) {
		this.todasSolicitacoes = todasSolicitacoes;
	}

}
