package br.ufrn.sigaa.ava.jsf;

import java.io.IOException;

import net.sf.jasperreports.engine.JRException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.forum.jsf.ForumTurmaMBean;
import br.ufrn.sigaa.ava.questionarios.jsf.CategoriaPerguntaQuestionarioTurmaMBean;
import br.ufrn.sigaa.ava.questionarios.jsf.QuestionarioTurmaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.PlanoCursoMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.ProgramaComponenteCurricularMBean;
import br.ufrn.sigaa.ensino.infantil.jsf.DiarioClasseInfantilMBean;
import br.ufrn.sigaa.ensino.infantil.jsf.FormularioEvolucaoCriancaMBean;
import br.ufrn.sigaa.ensino.infantil.jsf.RegistroEvolucaoCriancaMBean;
import br.ufrn.sigaa.ensino.jsf.ConsolidarTurmaMBean;
import br.ufrn.sigaa.ensino.jsf.DiarioClasseMBean;
import br.ufrn.sigaa.ensino.jsf.RelatorioConsolidacaoMBean;
import br.ufrn.sigaa.ensino.medio.jsf.ConsolidarDisciplinaMBean;
import br.ufrn.sigaa.ensino.medio.jsf.DiarioClasseMedioMBean;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;
import br.ufrn.sigaa.twitter.jsf.TwitterMBean;

/**
 * Managed bean para acesso dos casos de uso da Turma Virtual
 *
 */
@Scope("session")
@Component("menuTurma")
public class MenuTurmaMBean extends ControllerTurmaVirtual {
	
	/** Identifica qual sub-menu est� aberto no menu da turma virtual */
	private String menuExpandido = "menuTurma";
	
	//Menu Turma
	/**
	 * D� acesso a p�gina principal da turma virtual.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String acessarPrincipal () throws ArqException {
		menuExpandido = "menuTurma";

		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		return tBean.entrar();
	}
	
	/**
	 * D� acesso ao aluno a visualiza��o do plano de curso.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String acessarVisualizarPlanoCurso () throws ArqException {
		menuExpandido = "menuTurma";
		
		PlanoCursoMBean pBean = getMBean ("planoCurso");
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		return pBean.visualizarPlanoCurso(tBean.getTurma());
	}
	
	/**
	 * D� acesso ao professor ao gerenciamento do plano de curso.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException
	 */
	public String acessarPlanoCurso () throws ArqException {
		menuExpandido = "menuTurma";
		
		// Registra acesso ao plano de curso. 
		registrarAcao(null, EntidadeRegistroAva.PLANO_CURSO, AcaoAva.ACESSAR, turma().getId() );
		
		PlanoCursoMBean pBean = getMBean ("planoCurso");
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		return pBean.gerenciarPlanoCurso(tBean.getTurma());
	}
	
	/**
	 * D� acesso ao gerenciamento do perfil.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String acessarGerenciarPerfil () throws ArqException {
		menuExpandido = "menuTurma";
		
		PerfilUsuarioAvaMBean pBean = getMBean ("perfilUsuarioAva");
		return pBean.iniciar();
	}
	
	/**
	 * D� acesso ao professor ao gerenciamento dos t�picos de aula.
	* <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException
	 */
	public String acessarConteudoProgramado () throws ArqException {
		menuExpandido = "menuTurma";
		TopicoAulaMBean t = getMBean ("topicoAula");
		return t.iniciarGerenciaEmLote ();
	}
	
	/**
	 * D� acesso a visualiza��o dos participates da turma.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String acessarParticipantes () {
		
		// Registra acesso a p�gina de participantes. 
		registrarAcao(null, EntidadeRegistroAva.PARTICIPANTES, AcaoAva.ACESSAR, turma().getId() );
		
		menuExpandido = "menuTurma";
		return forward ("/ava/participantes.jsp");
	}
	
	/**
	 * D� acesso a visualiza��o do programa.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String acessarVisualizarPrograma () throws DAOException {
		
		menuExpandido = "menuTurma";
		TurmaVirtualMBean t = getMBean ("turmaVirtual");
			
		int idComponente = t.getTurma().getDisciplina().getId();
		
		// Registra acesso ao programa. 
		registrarAcao(null, EntidadeRegistroAva.PROGRAMA_COMPONENTE_CURRICULAR, AcaoAva.ACESSAR, idComponente);
		
		ProgramaComponenteCurricularMBean p = getMBean ("programaComponente");
		return p.gerarRelatorioPrograma (idComponente);
	}
	
	/**
	 * Faz o professor visualizar como docente caso ele esteja visualizando como discente.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String acessarVisualizarComoDocente () throws DAOException {
		menuExpandido = "menuTurma";
		TurmaVirtualMBean t = getMBean("turmaVirtual");
		return t.visualizarComoDiscente();
	}
	
	/**
	 * D� acesso ao f�rum.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	@Deprecated
	public String acessarForum () {
		
		// Registra acesso ao f�rum. 
		registrarAcao(null, EntidadeRegistroAva.FORUM, AcaoAva.ACESSAR, turma().getId() );
		
		menuExpandido = "menuTurma";
		TurmaVirtualMBean t = getMBean ("turmaVirtual");
		ForumMensagemMBean f = getMBean ("forumMensagem");
		return f.listar(t.getMural().getId());
	}
	
	/**
	 * D� acesso ao f�rum.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException 
	 */
	public String acessarForuns () throws ArqException {		
		registrarAcao(null, EntidadeRegistroAva.FORUM, AcaoAva.LISTAR);
		menuExpandido = "menuTurma";
		ForumTurmaMBean f = getMBean ("forumTurmaBean");
		f.setTurma(turma());
		return f.listar();
	}
	
	
	/**
	 * D� acesso as not�cias da turma.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public String acessarNoticias () {
		registrarAcao(null, EntidadeRegistroAva.NOTICIA, AcaoAva.LISTAR);
		
		menuExpandido = "menuTurma";
		NoticiaTurmaMBean n = getMBean ("noticiaTurma");
		return n.listar();
	}
	
	/**
	 * D� acesso ao gerenciamento de aulas extras.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public String acessarRegistrarAulaExtra () {
		registrarAcao(null, EntidadeRegistroAva.AULA_EXTRA, AcaoAva.LISTAR);
		menuExpandido = "menuTurma";
		AulaExtraMBean a = getMBean ("aulaExtra");
		return a.listar();
	}
	
	/**
	 * D� acesso ao gerenciamento de aulas para ensino individual.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public String acessarRegistrarAulaEnsinoIndividual () {
		registrarAcao(null, EntidadeRegistroAva.AULA_ENSINO_INDIVIDUAL, AcaoAva.LISTAR);
		menuExpandido = "menuTurma";
		AulaEnsinoIndividualMBean a = getMBean ("aulaEnsinoIndividual");
		return a.listar();
	}
	
	/**
	 * Faz o professor visualizar como discente.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String acessarVisualizarComoDiscente () throws DAOException {
		menuExpandido = "menuTurma";
		TurmaVirtualMBean t = getMBean ("turmaVirtual");
		return t.visualizarComoDiscente();
	}
	
	/**
	 * D� acesso ao cadastro do Twitter.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String acessarTwitterDocente () throws DAOException {
		// Registra acesso ao twitter. 
		registrarAcao(null, EntidadeRegistroAva.TWITTER, AcaoAva.ACESSAR, turma().getId() );
		TwitterMBean tBean = getMBean ("twitterMBean");
		return tBean.acessarConfiguracoes();
	}
	
	/**
	 * D� acesso ao cadastro do Twitter.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String acessarTwitterDiscente () throws DAOException {
		// Registra acesso ao twitter. 
		registrarAcao(null, EntidadeRegistroAva.TWITTER, AcaoAva.ACESSAR, turma().getId() );
		TwitterMBean tBean = getMBean ("twitterMBean");
		return tBean.visualizarDiscente();
	}
	
	
	// Menu "Alunos"
	/**
	 * D� acesso a visualiza��o dos alunos trancados.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String acessarAlunosTrancados () throws ArqException, NegocioException {
		menuExpandido = "menuAlunos";
		registrarAcao(null, EntidadeRegistroAva.ALUNOS_TRANCADOS, AcaoAva.ACESSAR, turma().getId() );
		TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
		return tBean.visualizarDiscentesTrancados();
		
	}
	
	/**
	 * D� acesso ao gerenciamento de grupos de alunos.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String acessarGerenciarGrupos () throws ArqException, NegocioException {
		registrarAcao(null, EntidadeRegistroAva.GRUPO, AcaoAva.ACESSAR, turma().getId() );
		menuExpandido = "menuAlunos";
		GrupoDiscentesMBean gDBean = getMBean("grupoDiscentes");
		return gDBean.preparar();
	}
	
	/**
	 * D� acesso ao aluno a visualizar sua frequ�ncia.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String acessarFrequencia () throws DAOException {
		
		if (getDiscenteUsuario() != null)
			registrarAcao(null, EntidadeRegistroAva.FREQUENCIA, AcaoAva.ACESSAR, getDiscenteUsuario().getId());
		
		menuExpandido = "menuAlunos";
		FrequenciaAlunoMBean f = getMBean("frequenciaAluno");
		return f.frequenciaAluno();
	}
	
	/**
	 * D� acesso ao aluno a visualiza��o seu grupo. 
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String acessarVerGrupo () throws ArqException, NegocioException {
		if (getDiscenteUsuario() != null)
			registrarAcao(null, EntidadeRegistroAva.GRUPO, AcaoAva.ACESSAR, getDiscenteUsuario().getId());
		else
			registrarAcao(null, EntidadeRegistroAva.GRUPO, AcaoAva.ACESSAR, getUsuarioLogado().getId());

		menuExpandido = "menuAlunos";
		GrupoDiscentesMBean gDBean = getMBean("grupoDiscentes");
		return gDBean.listar();
	}
	
	/**
	 * D� acesso ao aluno a visualizar suas notas.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String acessarVerNotas () throws ArqException, NegocioException {
		
		registrarAcao(null, EntidadeRegistroAva.NOTAS, AcaoAva.ACESSAR, turma().getId() );
		
		menuExpandido = "menuAlunos";
		RelatorioConsolidacaoMBean r = getMBean ("relatorioConsolidacao");
		return r.notasDiscente();
	}
	
	/**
	 * D� acesso ao lan�amento de frequ�ncias em subturmas.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarLancarFrequenciaST () {
		menuExpandido = "menuAlunos";
		FrequenciaAlunoMBean f = getMBean ("frequenciaAluno");
		return f.listaSubTurmas();
	}
	
	/**
	 * D� acesso ao lan�amento de frequ�ncias.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String acessarLancarFrequencia () throws DAOException {
		menuExpandido = "menuAlunos";
		FrequenciaAlunoMBean f = getMBean ("frequenciaAluno");
		return f.lancar();
	}
	
	/**
	 * D� acesso a planilha de frequ�ncias.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String acessarLancarFrequenciaEmPlanilha () throws ArqException {
		menuExpandido = "menuAlunos";
		FrequenciaAlunoMBean f = getMBean ("frequenciaAluno");
		return f.lancarPlanilha();
	}
	
	/**
	 * D� acesso a planilha de frequ�ncias em subturmas.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarLancarFrequenciaEmPlanilhaST () {
		menuExpandido = "menuAlunos";
		FrequenciaAlunoMBean f = getMBean ("frequenciaAluno");
		return f.listaSubTurmasPlanilha();
	}
	
	/**
	 * D� acesso ao lan�amento de notas.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException
	 */
	public String acessarLancarNotas () throws ArqException {
		menuExpandido = "menuAlunos";
		registrarAcao(null, EntidadeRegistroAva.NOTAS, AcaoAva.ACESSAR, turma().getId() );
		
		if ( turma()!= null && turma().isMedio() ){
			ConsolidarDisciplinaMBean c = getMBean ("consolidarDisciplinaMBean");
			return c.consolidaTurmaPortal();
		}
		
		ConsolidarTurmaMBean c = getMBean ("consolidarTurma");
		return c.consolidaTurmaPortal ();
	}
	
	/**
	 * D� acesso ao formul�rio de evolu��o da crian�a.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException
	 */
	public String acessarFormularioEvolucao () throws DAOException, ArqException {
		menuExpandido = "menuAlunos";

		FormularioEvolucaoCriancaMBean f = getMBean("formularioEvolucaoCriancaMBean");
		return f.iniciarFormularioTurma(turma().getId(), turma().getDisciplina().getId(),true);
	}
	
	/**
	 * D� acesso ao formul�rio de evolu��o da crian�a.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws ArqException
	 */
	public String acessarRegistrarEvolucao () throws DAOException, ArqException {
		menuExpandido = "menuAlunos";
		RegistroEvolucaoCriancaMBean r = getMBean("registroEvolucaoCriancaMBean");
		return r.iniciarTurmaVirtual(turma().getId());
	}
	
	/**
	 * D� acesso a planilha de frequ�ncias em subturmas.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public String acessarLancarNotasST () {
		menuExpandido = "menuAlunos";
		registrarAcao(null, EntidadeRegistroAva.NOTAS, AcaoAva.ACESSAR, turma().getId() );

		ConsolidarTurmaMBean c = getMBean ("consolidarTurma");
		return c.listaSubTurmas();
	}
	
	
	// Menu "Di�rio Eletr�nico"
	/**
	 * D� acesso ao gerenciamento de t�picos de aula.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarConteudoProgramadoDiario () {
		registrarAcao(null, EntidadeRegistroAva.TOPICO_AULA, AcaoAva.LISTAR);
		menuExpandido = "menuDiario";
		TopicoAulaMBean t = getMBean ("topicoAula");
		return t.listar();
	}
	
	/** 
	 * D� acesso ao di�rio da turma.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws JRException
	 * @throws IOException
	 */
	 
	public String acessarDiarioDeTurma () throws DAOException, JRException, IOException {
		menuExpandido = "menuDiario";
		
		if ( turma()!= null && turma().isMedio() ){
			DiarioClasseMedioMBean d = getMBean ("diarioClasseMedio");
			return d.gerarDiarioClasse();
		}
		
		if ( turma()!= null && turma().isInfantil() ){
			DiarioClasseInfantilMBean d = getMBean ("diarioClasseInfantil");
			return d.gerarDiarioClasse();
		}
		
		DiarioClasseMBean d = getMBean ("diarioClasse");
		return d.gerarDiarioClasse();
	}
	
	/**
	 * D� acesso a lista de presen�a.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws Exception
	 */
	
	public String acessarListaDePresenca () throws Exception {
		menuExpandido = "menuDiario";
		TurmaVirtualMBean t = getMBean ("turmaVirtual");
		return t.visualizaListaPresenca(); 
	}
	
	/**
	 * D� acesso a planilha de frequ�ncias em subturmas.
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/ava/menu.jsp</li> 
     * </ul>
	 * 
	 * @return
	 */
	public String acessarListaDePresencaST () {
		menuExpandido = "menuDiario";
		TurmaVirtualMBean t = getMBean ("turmaVirtual");
		return t.listaSubTurmasListaPresenca();
	}
	
	/**
	 * D� acesso ao relat�rio de frequ�ncia.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws Exception
	 */
	public String acessarMapaDeFrequencia () throws Exception {
		menuExpandido = "menuDiario";
		FrequenciaAlunoMBean f = getMBean ("frequenciaAluno");
		
		if ( turma()!= null && turma().isInfantil() ){
			DiarioClasseInfantilMBean d = getMBean ("diarioClasseInfantil");
			return d.gerarMapaFrequencia(turma());
		}
		
		return f.mapaFrequencia();
	}
	
	/**
	 * D� acesso ao total de faltas por unidade.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String acessarTotalDeFaltasPorUnidade () throws DAOException {
		menuExpandido = "menuDiario";
		FrequenciaAlunoMBean f = getMBean ("frequenciaAluno");
		return f.totalDeFaltasPorUnidade();
	}
	
	
	// Menu "Materiais"
	/**
	 * D� acesso aos cont�udos da turma virtual.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public String acessarConteudo () {
		registrarAcao(null, EntidadeRegistroAva.CONTEUDO, AcaoAva.LISTAR);
		menuExpandido = "menuMateriais";
		ConteudoTurmaMBean c = getMBean ("conteudoTurma");
		c.setListagem(null);
		return c.listar();
	}
	
	/**
	 * D� ao professor o acesso ao seu porta arquivos.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public String acessarPortaArquivos () {
		menuExpandido = "menuMateriais";
		return forward ("/ava/PortaArquivos/view.jsp");
	}
	
	/**
	 * Permite ao professor inserir um arquivo na turma.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarInserirArquivoNaTurma () {
		menuExpandido = "menuMateriais";
		ArquivoUsuarioMBean a = getMBean ("arquivoUsuario");
		return a.inserirArquivoTurma();
	}
	
	/**
	 * Permite ao professor inserir varios arquivos na turma.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarInserirVariosArquivoNaTurma() {
		menuExpandido = "menuMateriais";
		ArquivoUsuarioMBean a = getMBean ("arquivoUsuario");
		return a.inserirVariosArquivosTurma();
	}
	
	/**
	 * D� acesso ao professor ao gerenciamento de refer�ncias e aos aluno a visualiza��o das refer�ncias cadastradas. 
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarReferencias () {
		registrarAcao(null, EntidadeRegistroAva.INDICACAO_REFERENCIA, AcaoAva.LISTAR);
		menuExpandido = "menuMateriais";
		IndicacaoReferenciaMBean i = getMBean ("indicacaoReferencia");
		return i.listar();
	}
	
	/**
	 * Acessa o cadastro de v�deos.
	 * 
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/ava/menu.jsp</li> 
     * </ul>
	 */
	public String acessarCadastrarVideoAula () throws ArqException {
		registrarAcao(null, EntidadeRegistroAva.VIDEO, AcaoAva.LISTAR);
		
		menuExpandido = "menuMateriais";
		VideoTurmaMBean vBean = getMBean ("videoTurma");
		return vBean.listarVideos();
	}
	
	/**
	 * Acessa a listagem de v�deos.
	 * Utilizado pelos discentes da turma.
	 * 
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/ava/menu_horizontal.jsp</li>
     *  <li>sigaa.war/ava/menu_vertical.jsp</li> 
     * </ul>
	 */
	public String acessarListarVideos() throws ArqException {
		menuExpandido = "menuMateriais";
		VideoTurmaMBean vBean = getMBean ("videoTurma");
		return vBean.listarVideosDiscente();
	}
	
	
	/**
	 * Acessa a listagem de arquivos.
	 * Utilizado pelos discentes da turma.
	 * 
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/ava/menu_horizontal.jsp</li>
     *  <li>sigaa.war/ava/menu_vertical.jsp</li> 
     * </ul>
	 */
	public String acessarListarArquivos() throws ArqException {
		menuExpandido = "menuMateriais";
		ArquivoUsuarioMBean aBean = getMBean ("arquivoUsuario");
		return aBean.listarArquivosDiscente();
	}

	
	
	// Menu "Atividades"
	/**
	 * D� acesso ao gerenciamento das datas das avalia��es.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarAvaliacoes () {
		registrarAcao(null, EntidadeRegistroAva.AVALIACAO, AcaoAva.LISTAR);
		menuExpandido = "menuAtividades";
		DataAvaliacaoMBean d = getMBean ("dataAvaliacao");
		return d.listar();
	}

	/**
	 * D� acesso ao gerenciamento das enquetes.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarEnquetes () {
		registrarAcao(null, EntidadeRegistroAva.ENQUETE, AcaoAva.LISTAR);
		menuExpandido = "menuAtividades";
		EnqueteMBean e = getMBean ("enquete");
		return e.listar();
	}
	
	/**
	 * D� acesso ao gerenciamento das tarefas.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public String acessarTarefas () {
		registrarAcao(null, EntidadeRegistroAva.TAREFA, AcaoAva.LISTAR);
		menuExpandido = "menuAtividades";
		TarefaTurmaMBean t = getMBean("tarefaTurma");
		return t.listar();
	}
	
	/**
	 * Permite ao professor sortear participantes.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String acessarSortearParticipantes () throws DAOException {
		menuExpandido = "menuAtividades";
		SorteioParticipantesMBean s = getMBean ("sorteioParticipantesMBean");
		return s.iniciar();
	}
	
	/**
	 * Acessa o Question�rio de Docentes.
	 * 
	 * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/ava/menu.jsp</li> 
     * </ul>
     *  
	 */
	public String acessarQuestionariosDocente () throws ArqException {
		registrarAcao(null, EntidadeRegistroAva.QUESTIONARIO, AcaoAva.LISTAR);
		menuExpandido = "menuAtividades";
		QuestionarioTurmaMBean q = getMBean ("questionarioTurma");
		
		return q.listarQuestionariosDocente();
	}
	
	/**
	 * Acessa o Question�rio de Dicentes.
	 * 
	 * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/ava/menu.jsp</li> 
     * </ul>
     *  
	 */
	public String acessarQuestionariosDiscente () throws ArqException {
		registrarAcao(null, EntidadeRegistroAva.QUESTIONARIO, AcaoAva.LISTAR);
		menuExpandido = "menuAtividades";
		QuestionarioTurmaMBean q = getMBean ("questionarioTurma");
		q.setQuestionarios(null);
		
		return q.listarQuestionariosDiscente();
	}
	
	/**
	 * Acessa o Banco de Quest�es.
	 * 
	 * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/ava/menu.jsp</li> 
     * </ul>
     *  
	 */
	public String acessarBancoDeQuestoes () throws ArqException {
		menuExpandido = "menuAtividades";
		CategoriaPerguntaQuestionarioTurmaMBean cBean = getMBean ("categoriaPerguntaQuestionarioTurma");
		cBean.setAll(null);
		cBean.setCategoriasCompartilhadas(null);
		return cBean.listar();
	}
	
	/**
	 * Acessa o Novo Question�rio.
	 * 
	 * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/ava/menu.jsp</li> 
     * </ul>
     *  
	 */
	public String acessarNovoQuestionario () throws ArqException {
		menuExpandido = "menuAtividades";
		QuestionarioTurmaMBean q = getMBean ("questionarioTurma");
		
		return q.novoQuestionario();
	}
	
	// Menu "Configura��es"
	/**
	 * D� acesso as configura��es da turma virtual.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * 
	 */
	public String acessarConfigurarTurma () throws DAOException {
		menuExpandido = "menuConfiguracoes";
		ConfiguracoesAvaMBean c = getMBean ("configuracoesAva");
		c.setOpcaoCursosAbertos(false);
		return c.configurar();
	}
	
	/**
	 * D� acesso ao professor a importa��o de dados de turmas anteriores.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarImportacaoDeDados () {
		
		getUsuarioLogado().getVinculoAtivo().getServidor();
		getUsuarioLogado().getVinculoAtivo().getDocenteExterno();
		
		menuExpandido = "menuConfiguracoes";
		ImportacaoDadosTurmasAnterioresMBean i = getMBean ("importacaoDadosTurma");
		return i.iniciar();
	}
	
	/**
	 * D� acesso ao professor ao gerenciamento de permiss�es.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarPermissoes () {
		menuExpandido = "menuConfiguracoes";
		PermissaoAvaMBean p = getMBean ("permissaoAva");
		return p.listar();
	}
	
	/**
	 * Permite o professor tornar p�blico o acessoa turma virtual.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarPublicarTurmaVirtual () throws DAOException {
		menuExpandido = "menuConfiguracoes";
		ConfiguracoesAvaMBean c = getMBean ("configuracoesAva");
		c.setOpcaoCursosAbertos(true);
		return c.configurar();
	}
	
	/**
	 * D� acesso ao professor a visualiza��o do gr�fico da situa��o dos discentes.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarEstatisticas () {
		menuExpandido = "menuEstatistica";
		return forward ("/ava/estatisticas.jsp");
	}
	
	/**
	 * D� acesso ao professor a visualiza��o das estat�sticas de acesso.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String acessarEstatisticasDeAcesso () {
		menuExpandido = "menuEstatistiva";
		EstatisticasTurmaMBean eBean = getMBean("estatisticasTurma");
		return eBean.iniciarEstatisticas ();
	}
	
	/**
	 * D� acesso ao professor e alunos � visualiza��o das estat�sticas de notas dos discentes.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String acessarEstatisticasNotas() throws ArqException {
		menuExpandido = "menuEstatistica";
		EstatisticasNotasMBean e = getMBean ("estatisticasNotasMBean");
		return e.iniciar();
	}
	
	/**
	 * D� acesso ao professor a visualiza��o do gr�fico das notas dos discentes das subturmas.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String acessarEstatisticasNotasST () throws ArqException {
		menuExpandido = "menuEstatistica";
		EstatisticasNotasMBean e = getMBean ("estatisticasNotasMBean");
		return e.listaSubTurmas();
	}
	
	/**
	 * D� acesso ao professor a visualiza��o do relat�rio de acessos.
	 * M�todo n�o invocado por JSPs
	 * 
	 * @throws DAOException
	 * @return
	 */
	public String acessarRelatorioAcesso () throws DAOException {
		menuExpandido = "menuEstatistica";
		RelatorioAcessoTurmaVirtualMBean r = getMBean ("relatorioAcessoTurmaVirtualMBean");
		return r.gerarRelatorioSintetico();
	}
	
	/**
	 * D� acesso ao professor a visualiza��o do relat�rio de acessos.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @return
	 */
	public String acessarRelatorioDeAcesso () throws DAOException {
		menuExpandido = "menuEstatistica";
		RegistroAcaoAvaMBean r = getMBean ("registroAcaoAva");
		return r.relatorioAcessos();
	}
	
	/**
	 * D� acesso ao professor a visualiza��o do relat�rio de acessos.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @return
	 */
	public String acessarRelatorioAcessos () throws DAOException {
		RegistroAcaoAvaMBean r = getMBean ("registroAcaoAva");
		return r.exibirRelatorio();
	}
		
	/**
	 * D� acesso ao professor a visualiza��o do gr�fico de acessos.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @throws DAOException
	 * @return
	 */
	public String acessarGraficoDeAcesso () {
		menuExpandido = "menuEstatistica";
		RelatorioAcessoTurmaVirtualMBean r = getMBean ("relatorioAcessoTurmaVirtualMBean");
		return r.graficoAcessoTurmaVirtual();
	}
	
	// Menu "Ajuda"
	/**
	 * D� acesso ao manual de utiliza��o da turma virtual.
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @throws DAOException
	 * @return
	 */
	public String acessarManualDaTurmaVirtual () {
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		return redirect (ParametroHelper.getInstance().getParametro(
			tBean.isDocente()?
				ParametrosTurmaVirtual.LINK_MANUAL_TURMA_VIRTUAL_DOCENTE
			:
				ParametrosTurmaVirtual.LINK_MANUAL_TURMA_VIRTUAL_DISCENTE
		));
	}
	
	// Gets e Sets
	public String getMenuExpandido() {
		return menuExpandido;
	}

	public void setMenuExpandido(String menuExpandido) {
		this.menuExpandido = menuExpandido;
	}
}