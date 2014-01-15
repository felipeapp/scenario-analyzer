/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 17/01/2008
 */
package br.ufrn.sigaa.ead.jsf;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.graduacao.jsf.AutorizacaoCadastroComponenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.ComponenteCurricularMBean;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.RelatorioTurmaMBean;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.RelatoriosCoordenadorMBean;

/**
 * Managed bean para adaptar casos de uso de coordenação ao papel de coordenador
 * geral de ensino a distância, que possui poder de coordenador para todos os
 * cursos.
 * 
 * Inicialmente, o usuário será direcionado a uma tela de escolha de cursos e,
 * após a escolha, ele será direcionado ao caso de uso normal.
 * 
 * @author David Pereira
 *
 */
public class OperacoesCoordenadorGeralEadMBean extends AbstractController {

	private DataModel cursos;
	
	private int operacao;
	
	private Curso curso;
	
	/** Atividades */
	public static final int SOLICITAR_CADASTRO_ATIVIDADE = 1;
	public static final int MINHAS_SOLICITACOES_CADASTRO_ATIVIDADE = 2;
	
	/** Relatórios */
	public static final int ALUNOS_ATIVOS = 3;
	public static final int ALUNOS_PENDENTES_MATRICULA = 4;
	public static final int MATRICULAS_NAO_ATENDIDAS = 5;
	public static final int TRANCAMENTOS = 6;
	public static final int TURMAS_CONSOLIDADAS = 7;
	public static final int TURMAS_OFERTADAS = 8;
	public static final int REPROVACOES_DISCIPLINAS = 9;
	public static final int ALUNOS_CONCLUINTES = 10;
	public static final int ALUNOS_TIPO_SAIDA = 11;
	public static final int ALUNOS_ELEICAO = 12;
	
	/**
	 * Método para retornar ao caso de uso normal
	 * após a escolha do curso.
	 */
	public String realizarOperacao() throws Exception {
		curso = (Curso) cursos.getRowData();
		curso = getDAO(CursoDao.class).findByPrimaryKey(curso.getId(), Curso.class);
		
		switch (operacao) {
		// Atividades
		case SOLICITAR_CADASTRO_ATIVIDADE:
			return getComponenteCurricularMBean().preCadastrar();
		case MINHAS_SOLICITACOES_CADASTRO_ATIVIDADE:
			return getAutorizacaoCadastroComponenteMBean().verMinhasSolicitacoes();
			
		// Relatórios
		case ALUNOS_ATIVOS:
			return getRelatoriosCoordenadorMBean().relatorioAlunosAtivosCurso();
		case ALUNOS_PENDENTES_MATRICULA:
			return getRelatoriosCoordenadorMBean().relatorioAlunosPendenteMatricula();
		case MATRICULAS_NAO_ATENDIDAS:
			return getRelatoriosCoordenadorMBean().relatorioMatriculasNaoAtendidas();
		case TRANCAMENTOS:
			return getRelatoriosCoordenadorMBean().relatorioTrancamentos();
		case TURMAS_CONSOLIDADAS:
			return getRelatoriosCoordenadorMBean().relatorioTurmasConsolidadas();
		case TURMAS_OFERTADAS:
			RelatorioTurmaMBean rel = getRelatorioTurmaMBean();
			rel.setCurso(curso);
			return rel.iniciarRelatorioListaTurmasOfertadasCurso();
		case REPROVACOES_DISCIPLINAS:
			return getRelatoriosCoordenadorMBean().relatorioReprovacoesDisciplinas();
		case ALUNOS_CONCLUINTES:
			return getRelatoriosCoordenadorMBean().relatorioAlunosConcluintes();
			
		default:
			return null;
		}
		
	}
	
	/**
	 * Redireciona para a página de cursos do EAD  para cadastro de atividades
	 * @return
	 */
	public String solicitarCadastroAtividade() {
		return selecionarCurso(SOLICITAR_CADASTRO_ATIVIDADE);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD para as solicitações do usuário
	 * @return
	 */
	public String verMinhasSolicitacoes() {
		return selecionarCurso(MINHAS_SOLICITACOES_CADASTRO_ATIVIDADE);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD com os alunos ativos do curso
	 * @return
	 */
	public String relatorioAlunosAtivosCurso() {
		return selecionarCurso(ALUNOS_ATIVOS);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD com os alunos pendentes de matrícula
	 * @return
	 */
	public String relatorioAlunosPendenteMatricula() {
		return selecionarCurso(ALUNOS_PENDENTES_MATRICULA);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD com as matrículas não atendidas
	 * @return
	 */
	public String relatorioMatriculasNaoAtendidas() {
		return selecionarCurso(MATRICULAS_NAO_ATENDIDAS);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD com os trancamentos  
	 * @return
	 */
	public String relatorioTrancamentos() {
		return selecionarCurso(TRANCAMENTOS);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD com as turmas consolidadas
	 * @return
	 */
	public String relatorioTurmasConsolidadas() {
		return selecionarCurso(TURMAS_CONSOLIDADAS);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD com as reprovações de disciplina  
	 * @return
	 */
	public String relatorioReprovacoesDisciplinas() {
		return selecionarCurso(REPROVACOES_DISCIPLINAS);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD com os alunos concluintes
	 * @return
	 */
	public String relatorioAlunosConcluintes() {
		return selecionarCurso(ALUNOS_CONCLUINTES);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD  
	 * @return
	 */
	public String iniciarRelatorioListaTurmasOfertadasCurso() {
		return selecionarCurso(TURMAS_OFERTADAS);
	}
	
	/**
	 * Redireciona para a página de cursos do EAD de acordo com o parâmetro informado  
	 * @return
	 */
	private String selecionarCurso(int operacao) {
		this.curso = null;
		this.operacao = operacao;
		return forward("/ead/cursos_ead.jsp");
	}

	/**
	 * Retorna o bean que gera relatórios do coordenador setado para EAD
	 * @return
	 * @throws DAOException
	 */
	private RelatoriosCoordenadorMBean getRelatoriosCoordenadorMBean() throws DAOException {
		RelatoriosCoordenadorMBean bean = (RelatoriosCoordenadorMBean) getMBean("relatoriosCoordenador");
		bean.setEad(true);
		return bean;
	}
	
	/**
	 * Retorna o bean que de Componente Curricular setado para EAD
	 * @return
	 * @throws DAOException
	 */
	private ComponenteCurricularMBean getComponenteCurricularMBean() throws DAOException {
		ComponenteCurricularMBean bean = (ComponenteCurricularMBean) getMBean("componenteCurricular");
		bean.setEad(true);
		return bean;
	}
	
	/**
	 * Retorna o bean de autorização de cadastro de componentes setado para EAD
	 * @return
	 * @throws DAOException
	 */
	private AutorizacaoCadastroComponenteMBean getAutorizacaoCadastroComponenteMBean() throws DAOException {
		AutorizacaoCadastroComponenteMBean bean = (AutorizacaoCadastroComponenteMBean) getMBean("autorizacaoComponente");
		bean.setEad(true);
		return bean;
	}
	
	/**
	 * Retorna o bean de relatório de turmas setado para EAD 
	 * @return
	 */
	private RelatorioTurmaMBean getRelatorioTurmaMBean() {
		RelatorioTurmaMBean bean = (RelatorioTurmaMBean) getMBean("relatorioTurma");
		bean.setEad(true);
		return bean;
	}

	/**
	 * Retorna um DataModel com todos os cursos a distância
	 * @return
	 * @throws DAOException
	 */
	public DataModel getCursos() throws DAOException {
		if (cursos == null) {
			CursoDao dao = getDAO(CursoDao.class);
			cursos = new ListDataModel(dao.findAllCursosADistancia());
		}			
		return cursos;
	}

	public void setCursos(DataModel cursos) {
		this.cursos = cursos;
	}
	
	public Curso getCurso() {
		return curso;
	}
	
}
