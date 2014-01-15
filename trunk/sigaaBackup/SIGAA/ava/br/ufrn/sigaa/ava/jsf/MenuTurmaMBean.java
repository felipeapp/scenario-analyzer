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
	
	/** Identifica qual sub-menu está aberto no menu da turma virtual */
	private String menuExpandido = "menuTurma";
	
	//Menu Turma
	/**
	 * Dá acesso a página principal da turma virtual.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao aluno a visualização do plano de curso.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor ao gerenciamento do plano de curso.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao gerenciamento do perfil.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor ao gerenciamento dos tópicos de aula.
	* <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso a visualização dos participates da turma.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public String acessarParticipantes () {
		
		// Registra acesso a página de participantes. 
		registrarAcao(null, EntidadeRegistroAva.PARTICIPANTES, AcaoAva.ACESSAR, turma().getId() );
		
		menuExpandido = "menuTurma";
		return forward ("/ava/participantes.jsp");
	}
	
	/**
	 * Dá acesso a visualização do programa.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao fórum.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/menu.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	@Deprecated
	public String acessarForum () {
		
		// Registra acesso ao fórum. 
		registrarAcao(null, EntidadeRegistroAva.FORUM, AcaoAva.ACESSAR, turma().getId() );
		
		menuExpandido = "menuTurma";
		TurmaVirtualMBean t = getMBean ("turmaVirtual");
		ForumMensagemMBean f = getMBean ("forumMensagem");
		return f.listar(t.getMural().getId());
	}
	
	/**
	 * Dá acesso ao fórum.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso as notícias da turma.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao gerenciamento de aulas extras.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao gerenciamento de aulas para ensino individual.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao cadastro do Twitter.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao cadastro do Twitter.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso a visualização dos alunos trancados.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao gerenciamento de grupos de alunos.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao aluno a visualizar sua frequência.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao aluno a visualização seu grupo. 
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao aluno a visualizar suas notas.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao lançamento de frequências em subturmas.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao lançamento de frequências.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso a planilha de frequências.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso a planilha de frequências em subturmas.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao lançamento de notas.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao formulário de evolução da criança.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao formulário de evolução da criança.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso a planilha de frequências em subturmas.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	
	
	// Menu "Diário Eletrônico"
	/**
	 * Dá acesso ao gerenciamento de tópicos de aula.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao diário da turma.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso a lista de presença.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso a planilha de frequências em subturmas.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Dá acesso ao relatório de frequência.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao total de faltas por unidade.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso aos contéudos da turma virtual.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá ao professor o acesso ao seu porta arquivos.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor ao gerenciamento de referências e aos aluno a visualização das referências cadastradas. 
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Acessa o cadastro de vídeos.
	 * 
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Acessa a listagem de vídeos.
	 * Utilizado pelos discentes da turma.
	 * 
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
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
     * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Dá acesso ao gerenciamento das datas das avaliações.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao gerenciamento das enquetes.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao gerenciamento das tarefas.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Acessa o Questionário de Docentes.
	 * 
	 * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Acessa o Questionário de Dicentes.
	 * 
	 * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Acessa o Banco de Questões.
	 * 
	 * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Acessa o Novo Questionário.
	 * 
	 * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	// Menu "Configurações"
	/**
	 * Dá acesso as configurações da turma virtual.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor a importação de dados de turmas anteriores.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor ao gerenciamento de permissões.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Permite o professor tornar público o acessoa turma virtual.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor a visualização do gráfico da situação dos discentes.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor a visualização das estatísticas de acesso.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor e alunos à visualização das estatísticas de notas dos discentes.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor a visualização do gráfico das notas dos discentes das subturmas.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor a visualização do relatório de acessos.
	 * Método não invocado por JSPs
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
	 * Dá acesso ao professor a visualização do relatório de acessos.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor a visualização do relatório de acessos.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao professor a visualização do gráfico de acessos.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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
	 * Dá acesso ao manual de utilização da turma virtual.
	 * <br/><br/>Método chamado pela(s) JSP(s):
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