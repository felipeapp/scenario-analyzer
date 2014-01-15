/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/07/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.OrientacaoMatriculaDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.OrientacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoSolicitacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.portal.jsf.PortalCoordenacaoStrictoMBean;
import br.ufrn.sigaa.portal.jsf.PortalCoordenadorGraduacaoMBean;

/**
 * MBean responsável pela operação de analisar as solicitações de matrículas
 * @author Andre M Dantas
 * @author Ricardo Wendell
 */
@Component("analiseSolicitacaoMatricula") @Scope("session")
public class AnaliseSolicitacaoMatriculaMBean extends SigaaAbstractController<Object> implements OperadorDiscente {


	/** Tamanho máximo do texto da orientação de matrícula */
	private static final int TAMANHO_MAXIMO = 1000;
	/** Constante de visualização das turmas */
	private static final String JSP_TURMAS = "/graduacao/solicitacao_matricula/turmas.jsp";
	/** Constante de visualização dos discentes */
	private static final String JSP_DISCENTES = "/graduacao/solicitacao_matricula/discentes.jsp";
	/** Constante de visualização das solicitações */
	private static final String JSP_SOLICITACOES = "/graduacao/solicitacao_matricula/solicitacoes.jsp";
	/** Constante de visualização do relatório */
	private static final String JSP_RELATORIO = "/graduacao/solicitacao_matricula/relatorio_analises.jsp";
	/** Coleção que armazena todas as solicitações de matrícula do Discente */
	private Collection<SolicitacaoMatricula> solicitacoes;
	/** Coleção que armazena todos os discente */
	private Collection<Discente> discentes;
	/** Coleção que armazena todos os discente Pendente*/
	private Collection<Discente> discentesPendentes;
	/** Armazena o discente a ser utilizado na Analise de Solicitação de Matrícula */
	private DiscenteAdapter discente;
	/** Orientação de matrícula para um discente */
	private OrientacaoMatricula orientacao = new OrientacaoMatricula();

	/** Objeto para armazenar o curso ou unidade que o docente possui orientandos */
	private List<DadosCursoUnidade> dadosCursoUnidade = new ArrayList<AnaliseSolicitacaoMatriculaMBean.DadosCursoUnidade>();
	
	/** Determina se a análise é apenas um visto (cursos presenciais)
	 * ou se é necessário a negação ou atendimento do coordenador (EAD)
	 * essa flag é setada na seleção do discente 
	 */
	private Boolean visto;

	/**
	 * Usada para determinar o tipo de ordenação do resultado da busca
	 */
	private Boolean orderByNome = Boolean.TRUE;
	
	/** Calendário Acadêmico do discente */
	private CalendarioAcademico calendario;
	
	/** Esta flag diz se é uma analise que está sendo realizada de solicitações de matrículas em turmas de outros programas
	 * o programa só deve ter acesso as solicitações de disciplina do seu programa */
	private boolean analiseOutroPrograma = false;
	
	/** Esta flag indica se a operação de análise está sendo realizada sobre alunos especiais,
	 * permitindo que certas comportamentos específicos para tais alunos sejam aplicados. 
	 */
	private boolean alunoEspecial = false;
	
	private Integer filtroAlunoEspecial = null;
	
	/**
	 * Iniciar a operação de análise de solicitações
	 * de matrícula
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *  	<li>sigaa.war/graduacao/coordenador.jsp</li>
	 * 		<li>sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * 		<li>sigaa.war/stricto/coordenacao.jsp</li>
	 * 		<li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 * 		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/coordenacao.jsp</li>
	 * 		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 *   </ul>

	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO,SigaaPapeis.GESTOR_TECNICO, 
				SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.ORIENTADOR_ACADEMICO, SigaaPapeis.ORIENTADOR_STRICTO, 
				SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
				SigaaPapeis.TUTOR_EAD);

		if (!NivelEnsino.isAlgumNivelStricto(getNivelEnsino()) && !getCalendarioVigente().isPeriodoAnaliseCoordenador()) {
			addMensagemErro(UFRNUtils.getMensagem(MensagensStrictoSensu.ANALISE_SOLICITACAO_MATRICULA_MEDIANTE_CALENDARIO_PROGRAMA_DISCENTE).getMensagem());
			return null;
		}
		prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);

		try {
			// Carregar discentes do curso do coordenador que solicitaram matriculas
			carregarDiscentesSolicitantes();
		} catch (Exception e) {
			addMensagemErro("Erro ao carregar discentes");
			notifyError(e);
			return null;
		}
		return telaDiscentes();
	}
	
	/**
	 * Inicia a operação de análise de solicitações de matrícula de alunos especiais.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *  	<li>sigaa.war/graduacao/menus/programa.jsp</li>
	 * 		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *   </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlunoEspecial() throws ArqException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO);
		prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);
		
		setAlunoEspecial(true);
		carregarDiscentesEspeciaisSolicitantes();
		return telaDiscentes();
	}
	
	/**
	 * Abre tela de busca de discentes
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	 <ul>
	 * 		<li> sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * 	 <ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaDiscente() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO,SigaaPapeis.GESTOR_TECNICO, 
				SigaaPapeis.ORIENTADOR_ACADEMICO, SigaaPapeis.ORIENTADOR_STRICTO, 
				SigaaPapeis.COORDENADOR_CURSO_STRICTO );
		if (!NivelEnsino.isAlgumNivelStricto(getNivelEnsino()) && !getCalendarioVigente().isPeriodoAnaliseCoordenador()) {
			addMensagemErro(UFRNUtils.getMensagem(MensagensStrictoSensu.ANALISE_SOLICITACAO_MATRICULA_MEDIANTE_CALENDARIO_PROGRAMA_DISCENTE).getMensagem());
			return null;
		}
		prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ANALISE_SOLICITACAO_MATRICULA);
		return buscaDiscenteMBean.popular();
	}

	public void selecionaCategoriaAlunoEspecial(ValueChangeEvent evt) throws ArqException {
		
		filtroAlunoEspecial = (Integer) evt.getNewValue();

		if (filtroAlunoEspecial == 0) 
			filtroAlunoEspecial = null;
		
		carregarDiscentesEspeciaisSolicitantes();
	}
	
	/**
	 * Carrega discentes do curso do coordenador que solicitaram a matricula
	 * <br />
	 * JSP: Não invocado por JSP.
	 * 
	 * @throws ArqException
	 */
	private void carregarDiscentesSolicitantes() throws ArqException {
		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);
		CalendarioAcademico cal = getCalendarioVigente();

		if (SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema())) {
			Curso curso = getCursoAtualCoordenacao();
			discentes = dao.findByCursoAnoPeriodo(curso, cal.getAno(), cal.getPeriodo(), false, true, orderByNome );
		} else if(SigaaSubsistemas.PORTAL_TUTOR.equals(getSubSistema())){
			discentes = dao.findByTutorAnoPeriodo(getUsuarioLogado().getPessoa(), cal.getAno(), cal.getPeriodo(), false, false, orderByNome );
		}else if (SigaaSubsistemas.TECNICO.equals(getSubSistema()) && isUserInRole(SigaaPapeis.GESTOR_TECNICO)) {
			discentes = dao.findByUnidadeAnoPeriodo(getUnidadeGestora(), cal.getAno(), cal.getPeriodo(), true, orderByNome);
		} else if (SigaaSubsistemas.TECNICO.equals(getSubSistema()) && isUserInRole(SigaaPapeis.COORDENADOR_TECNICO)) {
			Curso curso = getCursoAtualCoordenacao();
			discentes = dao.findByCursoAnoPeriodo(curso, cal.getAno(), cal.getPeriodo(), false, !curso.isADistancia(), orderByNome );
		} else if (SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema())) {
			discentes = dao.findByUnidadeAnoPeriodo(getProgramaStricto().getId(), cal.getAno(), cal.getPeriodo(), false, orderByNome);
			discentesPendentes = dao.findByUnidadeAnoPeriodo(getProgramaStricto().getId(), cal.getAno(), cal.getPeriodo(), true, orderByNome);
		} else if (isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.SECRETARIA_DEPARTAMENTO)) {
			carregarDiscentesEspeciaisSolicitantes();
		}
	}
	
	private void carregarDiscentesEspeciaisSolicitantes() throws ArqException {
		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);
		CalendarioAcademico cal = getCalendarioVigente();
				
		Unidade unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
		if(isUserInRole(SigaaPapeis.DAE) && getSubSistema().equals(SigaaSubsistemas.GRADUACAO))
			unidade = null;
		
		ArrayList<Character> niveis = new ArrayList<Character>();	
		niveis.add(NivelEnsino.GRADUACAO);
		
		discentes = dao.findByAlunoEspecialAnoPeriodo(unidade, cal.getAno(), cal.getPeriodo(), orderByNome, niveis, filtroAlunoEspecial, isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO));
	}
	
	/**
	 * Caso o docente tenha orientandos em mais de um curso/programa será
	 * redirecionado para escolher qual curso/programa irá analisar.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * <ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String mostrarCursosOrientandos() throws ArqException {
		dadosCursoUnidade = new ArrayList<AnaliseSolicitacaoMatriculaMBean.DadosCursoUnidade>();
		checkRole(SigaaPapeis.ORIENTADOR_STRICTO);
		boolean redirecionaFormAnaliseMatriculaStricto = false;
		redirecionaFormAnaliseMatriculaStricto = carregaCursosOrientandosStricto(redirecionaFormAnaliseMatriculaStricto);
		if (redirecionaFormAnaliseMatriculaStricto) {
			if ( !discentes.isEmpty() || !discentesPendentes.isEmpty() ) {
				prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);
				return forward("/stricto/matricula/analise/discentes.jsp");
			} else {
				addMensagemErro("Não foram encontrados solicitações de matrícula pendentes realizadas por discentes sob sua orientação.");
				return null;
			}
		} else
			return forward("/stricto/matricula/analise/lista_cursos_programas.jsp");
	}

	/** Carrega os cursos dos discentes que o orientador orienta.
	 * @param redirecionaFormAnaliseMatriculaStricto
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	private boolean carregaCursosOrientandosStricto(
			boolean redirecionaFormAnaliseMatriculaStricto)
			throws DAOException, ArqException {
		OrientacaoAcademicaDao d = getDAO(OrientacaoAcademicaDao.class);
		List<Curso> cursosOrientandos = null;
		if (ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO)) {
			if (getUsuarioLogado().getVinculoAtivo().isVinculoServidor())
				cursosOrientandos = d.findCursosOrientandosByServidor(getServidorUsuario());
			else
				cursosOrientandos = d.findCursosOrientandosByDocenteExterno(getDocenteExternoUsuario());
			if (cursosOrientandos.size() == 1) {
				buscarDiscentesPendentesAndAnalisados(cursosOrientandos.iterator().next().getId());
				redirecionaFormAnaliseMatriculaStricto = true;
			} else {
				for (Curso curso : cursosOrientandos) {
					DadosCursoUnidade dcu = new DadosCursoUnidade(curso);
					dadosCursoUnidade.add(dcu);
				}
			}
		} else {
			List<Unidade> unidadesOrientandos = null;
			if (getUsuarioLogado().getVinculoAtivo().isVinculoServidor())
				unidadesOrientandos = d.findProgramaOrientandosByServidor(getServidorUsuario());
			else
				unidadesOrientandos = d.findProgramaOrientandosByDocenteExterno(getDocenteExternoUsuario());
			if (unidadesOrientandos.size() == 1) {
				buscarDiscentesPendentesAndAnalisados(unidadesOrientandos.iterator().next().getId());
				redirecionaFormAnaliseMatriculaStricto = true;
			} else {
				for (Unidade u : unidadesOrientandos) {
					DadosCursoUnidade dcu = new DadosCursoUnidade(u);
					dadosCursoUnidade.add(dcu);
				}
			}
		}
		return redirecionaFormAnaliseMatriculaStricto;
	}

	private CalendarioAcademico getCalendarioStricto(int idParam, SolicitacaoMatriculaDao dao) throws DAOException {
		CalendarioAcademico cal;
		if (ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO)) {
			Curso curso = dao.findByPrimaryKey(idParam, Curso.class);
			
			if (isEmpty(curso)) {
				addMensagemErro("Não é possível carregar o curso");
				return null;
			}
			
			cal = CalendarioAcademicoHelper.getCalendario(curso);
		} else {		
			Unidade u = dao.findByPrimaryKey(idParam, Unidade.class);
			
			if (isEmpty(u)) {
				addMensagemErro("Não é possível carregar o Programa Stricto.");
				return null;
			}
			
			cal = CalendarioAcademicoHelper.getCalendario(u);
		}
		return cal;
	}

	/**
	 * Inicia o caso de uso quando o usuário é orientador acadêmico
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	 <ul>
	 * 		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * 	 </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarOrientadorAcademico() throws ArqException {
		checkRole(SigaaPapeis.ORIENTADOR_ACADEMICO);

		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);
		CalendarioAcademico cal = getCalendarioVigente();

		discentes = dao.findByOrientadorAnoPeriodoStatus( getServidorUsuario().getId(), cal.getAno(), cal.getPeriodo(),
				new Character[] {NivelEnsino.GRADUACAO, NivelEnsino.TECNICO},
				SolicitacaoMatricula.ORIENTADO,
				SolicitacaoMatricula.SOLICITADA_COORDENADOR,
				SolicitacaoMatricula.VISTA);

		discentesPendentes = dao.findByOrientadorAnoPeriodoStatus( getServidorUsuario().getId(), cal.getAno(), cal.getPeriodo(), 
				new Character[] {NivelEnsino.GRADUACAO, NivelEnsino.TECNICO},
				SolicitacaoMatricula.SUBMETIDA);

		if (!discentes.isEmpty() || !discentesPendentes.isEmpty()) {
			prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);
			return telaDiscentes();
		} else {
			addMensagemErro("Não foram encontrados solicitações de matrícula realizadas por discentes sob sua orientação");
			return null;
		}
	}

	/**
	 * Inicia o caso de uso quando o usuário é orientador de pós-graduação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	 <ul>
	 * 		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * 	 </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarOrientadorStricto() throws ArqException {
		checkRole(SigaaPapeis.ORIENTADOR_STRICTO);
		Integer idParam = getParameterInt("id");
		buscarDiscentesPendentesAndAnalisados(idParam);
		if ( !discentes.isEmpty() || !discentesPendentes.isEmpty() ) {
			prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);
			return forward("/stricto/matricula/analise/discentes.jsp");
		} else {
			addMensagemErro("Não foram encontrados solicitações de matrícula pendentes realizadas por discentes sob sua orientação.");
			return null;
		}
	}

	/**
	 * Localiza os discentes pendentes de análise e os que já foram analisados
	 * 
	 * @param idParam
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	private void buscarDiscentesPendentesAndAnalisados(Integer idParam) throws DAOException, ArqException {
		
		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);
		
		CalendarioAcademico cal = getCalendarioStricto(idParam, dao);
		
		/** Carregar discentes que são orientandos do usuário logado */
		int ano = cal.getAno();
		int periodo = cal.getPeriodo();
		
		List<Integer> solAnalisadas = new ArrayList<Integer>();
		solAnalisadas.add(SolicitacaoMatricula.ATENDIDA);
		solAnalisadas.add(SolicitacaoMatricula.NEGADA);
		solAnalisadas.add(SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA);
		
		List<Integer> solPendentes = new ArrayList<Integer>();
		solPendentes.add(SolicitacaoMatricula.SUBMETIDA);
		solPendentes.add(SolicitacaoMatricula.EXCLUSAO_SOLICITADA);
		
		if (ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO)) {
			if ( getServidorUsuario() != null ) {			
				discentes = dao.findByOrientadorAndCursoStricto(ano, periodo, getServidorUsuario(), null, new Curso(idParam), solAnalisadas);
				discentesPendentes = dao.findByOrientadorAndCursoStricto(ano, periodo, getServidorUsuario(), null, new Curso(idParam), solPendentes);
			} else if (getDocenteExternoUsuario() != null ){
				discentes = dao.findByOrientadorAndCursoStricto(ano, periodo, null, getDocenteExternoUsuario(), new Curso(idParam), solAnalisadas);
				discentesPendentes = dao.findByOrientadorAndCursoStricto(ano, periodo, null, getDocenteExternoUsuario(), new Curso(idParam), solPendentes);
			}

		} else {
			if ( getServidorUsuario() != null ) {			
				discentes = dao.findByOrientadorAndProgramaStricto(ano, periodo, getServidorUsuario(), null, new Unidade(idParam), solAnalisadas);
				discentesPendentes = dao.findByOrientadorAndProgramaStricto(ano, periodo, getServidorUsuario(), null, new Unidade(idParam), solPendentes);
			} else if (getDocenteExternoUsuario() != null ){
				discentes = dao.findByOrientadorAndProgramaStricto(ano, periodo, null, getDocenteExternoUsuario(), new Unidade(idParam), solAnalisadas);
				discentesPendentes = dao.findByOrientadorAndProgramaStricto(ano, periodo, null, getDocenteExternoUsuario(), new Unidade(idParam), solPendentes);
			}
		}

		// Para evitar que o discente aparece nas duas listagens
		for (Discente discentePendentes : discentesPendentes) {
			if (discentes.contains(discentePendentes)) {
				discentes.remove(discentePendentes);
			}
		}
		
	}
	
	/**
	 * Inicia o caso de uso de análise de solicitações de matrícula realizada por discentes de outro programa
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	 <ul>
	 * 		<li>sigaa.war/strcto/menu_coordenador.jsp</li>
	 * 	 </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDiscentesOutrosProgramas() throws ArqException{
		checkRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO );
		
		/** Carregar discentes que são orientandos do usuário logado */
		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);
		
		discentes = dao.findDiscentesOutrosProgramasByProgramaSituacao(getProgramaStricto().getId(), getCalendarioVigente(), SolicitacaoMatricula.ATENDIDA, SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA);
		discentesPendentes = dao.findDiscentesOutrosProgramasByProgramaSituacao(getProgramaStricto().getId(), getCalendarioVigente(), SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA);
		
		analiseOutroPrograma = true;
		
		if ( !discentes.isEmpty() || !discentesPendentes.isEmpty() ) {
			prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);
			return forward("/stricto/matricula/analise/discentes.jsp");
		} else {
			addMensagemErro("Não foram encontrados solicitações de matrícula pendentes realizadas por discentes de outros programas em discilpinas do seu programa.");
			return null;
		}
	}

	/**
	 * Recarrega a lista de alunos para ordenar por nome ou matrícula
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	 <ul>
	 * 		<li>sigaa.war/graduacao/solicitacao_matricula/discentes.jsp</li>
	 * 	 </ul>
	 * 
	 * @param evt
	 * @throws ArqException
	 */
	public void ordenarSolicitacoes(ActionEvent evt) throws ArqException {
		carregarDiscentesSolicitantes();
	}
	
	/**
	 * Seleciona o discente e redireciona para a tela de solicitações.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	 <ul>
	 * 		<li>sigaa.war/graduacao/solicitacao_matricula/discentes.jsp</li>
	 *  	<li>sigaa.war/graduacao/solicitacao_matricula/coordenador.jsp</li>
	 *  	<li>sigaa.war/stricto/matricula/analise/discente.jsp</li>
	 *  	<li>sigaa.war/stricto/matricula/coordenacao.jsp</li>  
	 * 	 </ul>
	 * 
	 * @throws ArqException
	 */
	public String selecionaDiscente() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO,
				SigaaPapeis.ORIENTADOR_ACADEMICO, SigaaPapeis.ORIENTADOR_STRICTO, 
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.TUTOR_EAD, SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO );
		
		DiscenteDao ddao = getDAO(DiscenteDao.class);

		Integer id = getParameterInt("id");
		if ( id != null ) {
			discente = ddao.findByPK(id);
		}
		
		analiseOutroPrograma = getParameterBoolean("outrosProgramas");

		if (discente.isTecnico() || discente.isStricto() || (discente.isGraduacao() && !discente.isRegular())) {
			visto = false;
		} else {
			visto = true;
		}

		if( visto )
			setConfirmButton( "Cadastrar orientações" );
		else
			setConfirmButton( "Confirmar matrículas" );

		SolicitacaoMatriculaDao dao = getDAO(SolicitacaoMatriculaDao.class);
		
		if (analiseOutroPrograma) 
			calendario = CalendarioAcademicoHelper.getCalendario(getProgramaStricto());
		else
			calendario = CalendarioAcademicoHelper.getCalendario(discente);

		if (!discente.isStricto() && !calendario.isPeriodoAnaliseCoordenador()) {
			addMensagemErro(UFRNUtils.getMensagem(MensagensStrictoSensu.ANALISE_SOLICITACAO_MATRICULA_MEDIANTE_CALENDARIO_PROGRAMA_DISCENTE).getMensagem());
			return null;
		}

		if (discente.isGraduacao()) {
			
			Integer idUnidade = null;
			
			if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) && isPortalCoordenadorGraduacao())
				idUnidade = null;
			else if (isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO))
				idUnidade = getUsuarioLogado().getVinculoAtivo().getUnidade().getId();
			
			solicitacoes = dao.findByDiscenteAnoPeriodo(discente, calendario.getAno(), calendario.getPeriodo(), idUnidade,
					SolicitacaoMatricula.SUBMETIDA,
					SolicitacaoMatricula.ORIENTADO,
					SolicitacaoMatricula.ATENDIDA,
					SolicitacaoMatricula.NEGADA,
					SolicitacaoMatricula.VISTA,
					SolicitacaoMatricula.EXCLUSAO_SOLICITADA,
					SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA, 
					SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA);
		}
		else
			solicitacoes = dao.findValidasByDiscenteAnoPeriodo(discente, calendario.getAno(), calendario.getPeriodo(), 
					analiseOutroPrograma == true ? getProgramaStricto().getId() : null, analiseOutroPrograma);
		// verifica solicitações que não foram atendidas, mas que o discente está matriculado
		if (!isEmpty(solicitacoes)){
			MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
			Collection<MatriculaComponente> matriculadas = matriculaDao.findByDiscente(discente, calendario.getAno(), calendario.getPeriodo(), SituacaoMatricula.MATRICULADO);
			if (!isEmpty(matriculadas)) {
				for (SolicitacaoMatricula sol : solicitacoes) {
					for (MatriculaComponente matricula : matriculadas)
						if (!sol.isAtendida() && sol.getComponente().getId() == matricula.getComponente().getId()) {
							sol.setStatus(SolicitacaoMatricula.ATENDIDA);
							sol.setMatriculaGerada(matricula);
						}
				}
			}
		}
		/** se for analise de matrícula em disciplina de outro programa então só deve aparecer as solicitações das
		 * disciplinas que  são do programa que está realizando a análise */
		if( !solicitacoes.isEmpty() && isAnaliseOutroPrograma() ){
			for (Iterator<SolicitacaoMatricula> it = solicitacoes.iterator(); it.hasNext();) {
				SolicitacaoMatricula solicitacao = it.next();
				if( solicitacao.getComponente().getUnidade().getId() != getProgramaStricto().getId() 
						|| solicitacao.isNegada())
					it.remove();
			}
		}
		
		if (solicitacoes.isEmpty()) {
			if( isAnaliseOutroPrograma() )
				addMensagemWarning("Não foram encontradas solicitações de matrícula em disciplinas do seu programa realizadas pelo discente selecionado");
			else
				addMensagemWarning("Não foram encontradas solicitações de matrícula realizadas pelo discente selecionado");
			return null;
		}

		prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);
		// Buscar orientação geral de matricula, se cadastrada
		OrientacaoMatriculaDao orientacaoDao = getDAO(OrientacaoMatriculaDao.class);
		OrientacaoMatricula orientacao = orientacaoDao.findByDiscente(discente, calendario.getAno(), calendario.getPeriodo());

		if (orientacao == null) {
			this.orientacao = new OrientacaoMatricula();
		} else {
			this.orientacao = orientacao;
		}

		return telaSolicitacoes();
	}

	/**
	 * Chama o processador para persistir a análise da solicitação de matricula.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	 <ul>
	 * 		<li>sigaa.war//graduacao/solicitacao_matricula/solicitacoes.jsp</li>
	 * 	 </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		if (isEmpty(discente)) {
			addMensagemErro("Esta operação deve ser reiniciada para que o discente seja corretamente selecionado");
			return null;
		}

		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(discente);
		if (!discente.isStricto() && !calendario.isPeriodoAnaliseCoordenador()) {
			addMensagemErro(UFRNUtils.getMensagem(MensagensStrictoSensu.ANALISE_SOLICITACAO_MATRICULA_MEDIANTE_CALENDARIO_PROGRAMA_DISCENTE).getMensagem());
			return null;
		}

		Collection<SolicitacaoMatricula> solicitacoesAtendidas = new ArrayList<SolicitacaoMatricula>();

		List<String> negados = new ArrayList<String>();
		List<String> aceitos = new ArrayList<String>();
		String[] arrayNegados = getCurrentRequest().getParameterValues("negados");
		if (arrayNegados != null) {
			negados = Arrays.asList(arrayNegados);
		}

		String[] arrayAceitos = getCurrentRequest().getParameterValues("aceitos");
		if (arrayAceitos != null) {
			aceitos = Arrays.asList(arrayAceitos);
		}

		if( isEmpty( negados ) && isEmpty( aceitos ) && isEmpty(orientacao.getOrientacao()) ){
			addMensagemErro("É necessário efetuar a análise para confirmar.");
			return null;
		}

		Unidade programa = discente.getGestoraAcademica();
		for (SolicitacaoMatricula solicitacao : solicitacoes) {
			if (aceitos.contains(solicitacao.getId()+"")){
				
				if( !discente.isStricto() || isAnaliseOutroPrograma() || solicitacao.getComponente().getUnidade().getId() == programa.getId()  )
					solicitacao.setStatus( visto ? SolicitacaoMatricula.VISTA : SolicitacaoMatricula.ATENDIDA );
				else
					solicitacao.setStatus( visto ? SolicitacaoMatricula.VISTA : SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA );
				
				// solicitacao.setStatus( SolicitacaoMatricula.VISTA );
				solicitacoesAtendidas.add(solicitacao);
			} else if (negados.contains(solicitacao.getId()+"") ){
				String obs = getCurrentRequest().getParameter("obs_"+solicitacao.getId());
				if (isEmpty(obs)) {
					addMensagemErro("É preciso informar o motivo para todas as orientações de \"Não Matricular\"");
					return null;
				}
				solicitacao.setObservacao(obs);
				if( isAnaliseOutroPrograma() )
					solicitacao.setStatus(visto ? SolicitacaoMatricula.ORIENTADO : SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA);
				else
					solicitacao.setStatus(visto ? SolicitacaoMatricula.ORIENTADO : 
						(solicitacao.isExclusaoSolicitada() ? SolicitacaoMatricula.EXCLUIDA : SolicitacaoMatricula.NEGADA));
				solicitacoesAtendidas.add(solicitacao);
			}
		}

		/**
		 * O usuário não pode submeter uma solicitação de matricula que JÁ FOI atendida sem selecionar nenhuma opção (MATRICULAR OU CANCELAR)
		 */
		Collection<SolicitacaoMatricula> solicitacoesJaAtendidas = new ArrayList<SolicitacaoMatricula>();
		for( SolicitacaoMatricula sm : solicitacoes ){
			if( sm.isAtendida() || sm.isNegada() || sm.isOrientada() || sm.isVista() )
				solicitacoesJaAtendidas.add(sm);
		}

		if( !solicitacoesAtendidas.containsAll(solicitacoesJaAtendidas) ){
			for( SolicitacaoMatricula sm : solicitacoesJaAtendidas ){
				if( !solicitacoesAtendidas.contains(sm) ){
					addMensagemErro( "Você deve selecionar umas das opções da análise para a solicitação " + sm.getComponente().getDescricaoResumida() + " pois ela já havia sido analisada."  );
				}
			}
			return null;
		}

		if ( !StringUtils.isEmpty(orientacao.getOrientacao()) && orientacao.getOrientacao().length() > TAMANHO_MAXIMO ) {
			addMensagemErro("A orientação geral de matrícula deve conter no máximo " + TAMANHO_MAXIMO + " caracteres.");
			return null;
		}		

		MovimentoSolicitacaoMatricula movimento = new MovimentoSolicitacaoMatricula();
		movimento.setCodMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);
		movimento.setSolicitacoes(solicitacoesAtendidas);
		movimento.setDiscente(discente);
		movimento.setCalendarioAcademicoAtual(calendario);
		movimento.setOrientacao(orientacao);
		
		// Chamar processador
		try {
			executeWithoutClosingSession(movimento, getCurrentRequest());
			addMessage("Análise de solicitações de matrícula do discente " + discente.getNome()
					+ " realizada com sucesso!", TipoMensagemUFRN.INFORMATION);
			if (SigaaSubsistemas.PORTAL_COORDENADOR.equals(getSubSistema())) {
				PortalCoordenadorGraduacaoMBean portal = (PortalCoordenadorGraduacaoMBean) getMBean("portalCoordenadorGrad");
				portal.recarregarInformacoesPortal();
				return iniciar();
			} else if (SigaaSubsistemas.TECNICO.equals(getSubSistema())) {
				return iniciar();
			} else if (discente.isStricto()) {
				if (isPortalCoordenadorStricto())
					((PortalCoordenacaoStrictoMBean) getMBean("portalCoordenacaoStrictoBean")).recarregarInformacoesPortal();
				if( (SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema())
						|| SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema()) )
						&& isUserInRole(SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO) && isAnaliseOutroPrograma() )
					return iniciarDiscentesOutrosProgramas();
				else if( (SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals(getSubSistema())
						|| SigaaSubsistemas.STRICTO_SENSU.equals(getSubSistema()) )
						&& isUserInRole(SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO) && !isAnaliseOutroPrograma() )
					return iniciar();
				else {
					dadosCursoUnidade = new ArrayList<AnaliseSolicitacaoMatriculaMBean.DadosCursoUnidade>();
					boolean redirecionaFormAnaliseMatriculaStricto = false;
					redirecionaFormAnaliseMatriculaStricto = carregaCursosOrientandosStricto(redirecionaFormAnaliseMatriculaStricto);
					if ( !discentes.isEmpty() || !discentesPendentes.isEmpty() ) {
						prepareMovimento(SigaaListaComando.ANALISAR_SOLICITACAO_MATRICULA);
						return forward("/stricto/matricula/analise/discentes.jsp");
					} else
						return cancelar();
				}
			} else if( discente.isGraduacao() && discente.isDiscenteEad() ) {
				return iniciar();
			} else if( discente.isGraduacao() && !discente.isRegular() ) {
				return iniciarAlunoEspecial();
			} else {
				return iniciarOrientadorAcademico();
			}
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} 


	}

	/**
	 * Gerar um relatório contendo todas as solicitações de matrícula e suas análises
	 * e as orientações gerais cadastradas para um discente
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	 <ul>
	 * 		<li>sigaa.war//graduacao/solicitacao_matricula/solicitacoes.jsp</li>
	 * 	 </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAnalises() throws DAOException {
		
		// Buscar solicitações de matrícula
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
		Collection<SolicitacaoMatricula> solicitacoes = solicitacaoDao.findByDiscente(discente);
		
		for (SolicitacaoMatricula solicitacao : solicitacoes) {
			if (solicitacao.getMatriculaGerada() != null) {
				SituacaoMatricula situacao = solicitacaoDao.initializeClone(solicitacao.getMatriculaGerada().getSituacaoMatricula());
				solicitacao.getMatriculaGerada().setSituacaoMatricula(situacao);
			}
		}
		
		// Buscar orientações cadastradas
		OrientacaoMatriculaDao orientacaoDao = getDAO(OrientacaoMatriculaDao.class);
		Collection<OrientacaoMatricula> orientacoes = orientacaoDao.findByDiscente(discente);
		
		Map<String, OrientacaoMatricula> mapaOrientacoes = new HashMap<String, OrientacaoMatricula>();
		for (OrientacaoMatricula orientacao : orientacoes) {
			mapaOrientacoes.put(orientacao.getAnoPeriodo(), orientacao);
		}
		
		getCurrentRequest().setAttribute("solicitacoes", solicitacoes);
		getCurrentRequest().setAttribute("orientacoes", mapaOrientacoes);
		
		return forward(JSP_RELATORIO);
	}
	
	
	/**
	 * Tela de busca de outras turmas para coordenador matricular o aluno.
	 * O acesso a essa busca só permitido aos coordenadores de cursos
	 *  a distância.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 *
	 * @return
	 */
	public String telaBuscaTurmas() {
		Curso curso = getCursoAtualCoordenacao();
		if (curso == null || !curso.isADistancia()) {
			addMensagemErro("O usuário não tem permissão para essa operação");
			return null;
		}
		return forward(JSP_TURMAS);
	}

	/**
	 * Direciona para a tela das solicitações
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public String telaSolicitacoes() {
		return forward(JSP_SOLICITACOES);
	}

	/**
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/solicitacao_matricula/solicitacoes.jsp</li>
	 *	</ul>
	 *
	 * @return
	 */
	public String telaDiscentes() {
		return forward(JSP_DISCENTES);
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public Collection<SolicitacaoMatricula> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Collection<SolicitacaoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Boolean getVisto() {
		return visto;
	}

	public void setVisto(Boolean visto) {
		this.visto = visto;
	}

	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

	public OrientacaoMatricula getOrientacao() {
		return this.orientacao;
	}

	public void setOrientacao(OrientacaoMatricula orientacao) {
		this.orientacao = orientacao;
	}

	public Collection<Discente> getDiscentesPendentes() {
		return discentesPendentes;
	}

	public void setDiscentesPendentes(Collection<Discente> discentesPendentes) {
		this.discentesPendentes = discentesPendentes;
	}

	public CalendarioAcademico getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	public boolean isAnaliseOutroPrograma() {
		return analiseOutroPrograma;
	}

	public void setAnaliseOutroPrograma(boolean analiseOutroPrograma) {
		this.analiseOutroPrograma = analiseOutroPrograma;
	}

	public Boolean getOrderByNome() {
		return orderByNome;
	}

	public void setOrderByNome(Boolean orderByNome) {
		this.orderByNome = orderByNome;
	}

	/**
	 * Classe para guardar o programa e curso que o docente possui orientandos
	 * 
	 * @author henrique
	 */
	public class DadosCursoUnidade {
		
		/**
		 * id da entidade curso ou programa
		 */
		private int id;
		
		/**
		 * nome descritivo do curso ou programa
		 */
		private String nome;
		
		public DadosCursoUnidade(Curso curso) {
			id = curso.getId();
			nome = curso.getNomeCursoStricto();
		}
		
		public DadosCursoUnidade(Unidade unidade) {
			id = unidade.getId();
			nome = unidade.getNome();
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}
		
	}

	public List<DadosCursoUnidade> getDadosCursoUnidade() {
		return dadosCursoUnidade;
	}

	public void setDadosCursoUnidade(List<DadosCursoUnidade> dadosCursoUnidade) {
		this.dadosCursoUnidade = dadosCursoUnidade;
	}

	public boolean isAlunoEspecial() {
		return alunoEspecial;
	}

	public void setAlunoEspecial(boolean alunoEspecial) {
		this.alunoEspecial = alunoEspecial;
	}

	public Integer getFiltroAlunoEspecial() {
		return filtroAlunoEspecial;
	}

	public void setFiltroAlunoEspecial(Integer filtroAlunoEspecial) {
		this.filtroAlunoEspecial = filtroAlunoEspecial;
	}

}
