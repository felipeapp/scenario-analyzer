/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * 31/05/2007
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatoriosCoordenadorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.jsf.OperacoesCoordenadorGeralEadMBean;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatriculaEquivalentes;
import br.ufrn.sigaa.ensino.graduacao.relatorios.dominio.RelatorioCurso;


/**
 * MBean para gerar os relat�rios do coordenador de curso de gradua��o
 * @author leonardo
 *
 */
@Component("relatoriosCoordenador") @Scope("request")
public class RelatoriosCoordenadorMBean extends SigaaAbstractController<Object> {

	// Constantes das views
	/** Link para o formul�rio de par�metros: ano/per�odo. */
	public static final String JSP_ANO_PERIODO = "/graduacao/relatorios/coordenador/ano_periodo.jsp";
	/** Link para o relat�rio de trancamentos. */
	public static final String JSP_REL_TRANCAMENTOS = "/graduacao/relatorios/coordenador/trancamentos.jsp";
	/** Link para o relat�rio de discentes ativos. */
	public static final String JSP_REL_ATIVOS = "/graduacao/relatorios/coordenador/alunos_ativos.jsp";
	/** Link para o relat�rio de discentes com matr�cula pendente. */
	public static final String JSP_REL_PENDENTE_MATRICULA = "/graduacao/relatorios/coordenador/alunos_pendente_matricula.jsp";
	/** Link para o relat�rio de matr�culas n�o atendidas. */
	public static final String JSP_REL_MATRICULAS_NAO_ATENDIDAS = "/graduacao/relatorios/coordenador/matriculas_nao_atendidas.jsp";
	/** Link para o relat�rio de turmas consolidadas. */
	public static final String JSP_REL_TURMAS_CONSOLIDADAS = "/graduacao/relatorios/turmas_consolidadas.jsp";
	/** Link para o relat�rio de reprova��es nas disciplinas. */
	public static final String JSP_REL_REPROVACOES_DISCIPLINAS = "/graduacao/relatorios/reprovacoes_disciplinas.jsp";
	/** Link para o relat�rio de percentual de carga hor�ria cumprida. */
	public static final String JSP_PERCENTUAL_CH = "/graduacao/relatorios/discente/lista_percentual_ch.jsp";
	/** Link para o relat�rio de disciplinas/ementas por curso. */
	public static final String JSP_DISCIPLINAS_CURSO = "/graduacao/relatorios/curso/ementas_curso.jsp";
	/** Link para o formul�rio de par�metros do relat�rio de equival�ncias de curr�culo. */
	public static final String JSP_EQUIVALENCIAS = "/graduacao/relatorios/coordenador/form_equivalencias_curriculo.jsp";
	/** Link para o relat�rio de equival�ncias de curr�culo. */
	public static final String JSP_REL_EQUIVALENCIAS = "/graduacao/relatorios/coordenador/equivalencias_curriculo.jsp";

	// Constantes dos tipos de relat�rios 
	/** Define a gera��o do relat�rio de trancamentos. */
	public static final int REL_TRANCAMENTOS = 1;
	/** Define a gera��o do relat�rio de discentes ativos. */
	public static final int REL_ATIVOS = 2;
	/** Define a gera��o do relat�rio de discentes com matr�cula pendente. */
	public static final int REL_PENDENTE_MATRICULA = 3;
	/** Define a gera��o do relat�rio de matr�culas n�o atendidas. */
	public static final int REL_MATRICULAS_NAO_ATENDIDAS = 4;
	/** Define a gera��o do relat�rio de turmas consolidadas. */
	public static final int REL_TURMAS_CONSOLIDADAS = 5;
	/** Define a gera��o do relat�rio de turmas consolidadas por departamento. */
	public static final int REL_TURMAS_CONSOLIDADAS_DEPTO = 8;
	/** Define a gera��o do relat�rio de reprova��es nas disciplinas. */
	public static final int REL_REPROVACOES_DISCIPLINAS = 6;
	/** Define a gera��o do relat�rio de reprova��es nas disciplinas por departamento. */
	public static final int REL_REPROVACOES_DISCIPLINAS_DEPTO = 7;
	/** Define a gera��o do relat�rio de disciplinas/ementas por curso. */
	public static final int REL_DISCIPLINAS_CURSO = 9;
	/** Define a gera��o do relat�rio de equival�ncias de um curr�culo. */
	public static final int REL_EQUIVALENCIAS = 10;
	
	/** Dados do relat�rio. */
	private Map<?, ?> relatorio;
	
	/** Armazena as equival�ncias de um determinado curr�culo. */
	private Collection<SugestaoMatriculaEquivalentes> equivalentes;

	/** Ano base para gera��o do relat�rio */
	private int ano;

	/** Per�odo base para gera��o do relat�rio */
	private int periodo;

	/** Ano ingresso dos discente */
	private Integer anoIngresso;

	/** Per�odo ingresso dos discente */
	private Integer periodoIngresso;	
	
	/** Tipo do relat�rio a ser gerado */
	private int tipoRelatorio;
	
	/** Indica se deve o formul�rio tem o campo "curso". */
	private boolean exibeCurso = true;
	
	/** Indica se deve o formul�rio tem o campo "Ano/Per�odo de Ingresso". */
	private boolean exibeAnoPeriodoIngresso = true;
	
	/** Matriz Curricular do relatorio de equivalencias. */
	private MatrizCurricular matriz = new MatrizCurricular();
	
	/** Curriculo do relatorio de equivalencias. */
	private Curriculo curriculo = new Curriculo();
	
	/** Select de matrizes carregado de acordo com o curso. */
	List<SelectItem> matrizesCurriculares = new ArrayList<SelectItem>();
	
	/** Select de curriculos que � carregado de acordo com a matriz selecionada. */
	List<SelectItem> curriculos = new ArrayList<SelectItem>();
	
	/** Armazena os t�tulos dos relat�rios */
	static private Map<Integer, String> titulosRelatorio;
	static {titulosRelatorio = new HashMap<Integer, String>();
		titulosRelatorio.put(REL_TRANCAMENTOS, "Relat�rio de Trancamentos");
		titulosRelatorio.put(REL_ATIVOS, "Alunos Ativos no Curso");
		titulosRelatorio.put(REL_PENDENTE_MATRICULA, "Relat�rio de Alunos Pendentes de Matr�cula");
		titulosRelatorio.put(REL_MATRICULAS_NAO_ATENDIDAS, "Matr�culas n�o atendidas");
		titulosRelatorio.put(REL_TURMAS_CONSOLIDADAS, "Relat�rio de Turmas Consolidadas");
		titulosRelatorio.put(REL_TURMAS_CONSOLIDADAS_DEPTO, "Relat�rio de Turmas Consolidadas");
		titulosRelatorio.put(REL_REPROVACOES_DISCIPLINAS, "Relat�rio de Disciplinas com mais Reprova��es");
		titulosRelatorio.put(REL_REPROVACOES_DISCIPLINAS_DEPTO, "Relat�rio de Disciplinas com Reprova��es");
		titulosRelatorio.put(REL_DISCIPLINAS_CURSO, "Relat�rio de Disciplinas com Ementas");
		titulosRelatorio.put(REL_EQUIVALENCIAS, "Relat�rio de Equival�ncias");
	}
	
	/** Indica se o relat�rio � de um curso a dist�ncia. */
	private boolean ead;
	
	/** Curso base do relat�rio */
	private Curso curso = new Curso();
	
	private ComponenteCurricular componente = null;
	
	/** Lista de componentes curriculares para o relat�rio. */
    private List<ComponenteCurricular> listaDisciplinasEmenta;
    
	
    /** Recebe o ano/per�odo e chama o m�todo correspondente ao tipo de relat�rio para preenchimento dos dados.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/coordenador/ano_periodo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterAnoPeriodo() throws DAOException {
		validacaoDados(erros);
		if (hasErrors()) {
			return null;
		}
		RelatoriosCoordenadorDao dao = getDAO(RelatoriosCoordenadorDao.class);
		ead = (getCursoAtualCoordenacao() == null ? false : getCursoAtualCoordenacao().getModalidadeEducacao().isADistancia());

		ead = true;		
		
		if(tipoRelatorio == REL_TRANCAMENTOS){
			if( curso.getId() != 0 ) curso = getGenericDAO().findByPrimaryKey(curso.getId(), Curso.class);
			else curso = getCursoAtualCoordenacao();
			relatorio = dao.findTrancamentosByCurso(curso, ano, periodo);
			return forward(JSP_REL_TRANCAMENTOS);

		} else if(tipoRelatorio == REL_PENDENTE_MATRICULA){
			relatorio = dao.findAlunosPendenteMatricula(getCursoAtualCoordenacao(), ano, periodo, anoIngresso, periodoIngresso);
			return forward(JSP_REL_PENDENTE_MATRICULA);

		} else if(tipoRelatorio == REL_REPROVACOES_DISCIPLINAS){
			if( curso.getId() != 0 ) curso = getGenericDAO().findByPrimaryKey(curso.getId(), Curso.class);
			else curso = getCursoAtualCoordenacao();
			relatorio = dao.findReprovacoesDisciplinas(ano, periodo, curso, componente, null, ead);
			return forward(JSP_REL_REPROVACOES_DISCIPLINAS);

		} else if(tipoRelatorio == REL_REPROVACOES_DISCIPLINAS_DEPTO){
			relatorio = dao.findReprovacoesDisciplinas(ano, periodo, null, componente, getUsuarioLogado().getVinculoAtivo().getUnidade(), ead);
			return forward(JSP_REL_REPROVACOES_DISCIPLINAS);

		}else if(tipoRelatorio == REL_TURMAS_CONSOLIDADAS){
			if( curso.getId() != 0 ) curso = getGenericDAO().findByPrimaryKey(curso.getId(), Curso.class);
			else curso = getCursoAtualCoordenacao();
			relatorio = dao.findTurmasConsolidadas(ano, periodo, curso, null);
			return forward(JSP_REL_TURMAS_CONSOLIDADAS);

		}else if(tipoRelatorio == REL_TURMAS_CONSOLIDADAS_DEPTO){
			relatorio = dao.findTurmasConsolidadas(ano, periodo, null, getUsuarioLogado().getVinculoAtivo().getUnidade());
			if(relatorio == null || relatorio.size() == 0) {
				addMensagemErro("N�o foram encontrados resultados de acordo com os par�metros informados.");
				return null;
			}
			return forward(JSP_REL_TURMAS_CONSOLIDADAS);
		}
		else if(tipoRelatorio == REL_DISCIPLINAS_CURSO) {
		    if ( curso.getId() != 0 ) curso = getGenericDAO().findByPrimaryKey(curso.getId(), Curso.class);
		        else curso = getCursoAtualCoordenacao();
		    
		    
	    listaDisciplinasEmenta = getDAO(ComponenteCurricularDao.class).findComponentesPorCursoGraduacao(curso, ano, periodo, ead);
		    
        return forward(JSP_DISCIPLINAS_CURSO);
        }
		return null;
	}
	
	/**
	 * Efetua a gera��o do relat�rio de equival�ncias.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/coordenador/form_equivalencias_curriculo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioEquivalencias() throws DAOException{
		validarRelatorioEquivalencias();
		
		if(hasErrors())
			return null;
		
		GenericDAO genericDAO = getGenericDAO();
		
		try {
			curriculo = genericDAO.refresh(curriculo);
			matriz = genericDAO.refresh(matriz);
			curso = genericDAO.refresh(curso);
		} finally {
			genericDAO.close();
		}
		
		RelatoriosCoordenadorDao dao = getDAO(RelatoriosCoordenadorDao.class);
		
		equivalentes = dao.findEquivalentesByCurriculo(curso.getId(), matriz.getId(), curriculo.getId());
		
		return forward(JSP_REL_EQUIVALENCIAS);
	}

	/**
	 * Efetua a valida��o dos dados do relat�rio de equival�ncias e, caso encontre algum erro, adiciona a respectiva mensagem.
	 */
	private void validarRelatorioEquivalencias() {
		if(isEmpty(curriculo))
			addMensagemErro("Selecione um Curr�culo.");
		if(isEmpty(matriz))
			addMensagemErro("Selecione uma Matriz Curricular.");
		if(isEmpty(curso))
			addMensagemErro("Selecione um Curso.");
	}

	/**
	 * Redireciona para o relat�rio de alunos concluintes do curso.
	 *
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp </li>
	 * </ul>
	 * 
	 * @return @see RelatorioPorCursoMBean#gerarRelatorioAlunosFormandos()
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String relatorioAlunosFormandos() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);

		RelatorioPorCursoMBean relatorioMBean = (RelatorioPorCursoMBean) getMBean( "relatorioPorCurso" );
		relatorioMBean.setRelatorio( RelatorioCurso.getRelatorio(RelatorioCurso.ALUNOS_FORMANDOS));
		relatorioMBean.getRelatorio().setCurso(getCursoAtualCoordenacao());

		return relatorioMBean.gerarRelatorioAlunosFormandos();
	}
	
	/**
	 * Redireciona para o relat�rio de alunos concluintes do curso.
	 *
	 * chamado por /graduacao/menu_coordenador.jsp
	 * 
	 * @return @see RelatorioPorCursoMBean#gerarRelatorioAlunosConcluintes()
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String relatorioAlunosConcluintes() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.COORDENADOR_GERAL_EAD);

		RelatorioPorCursoMBean relatorioMBean = (RelatorioPorCursoMBean) getMBean( "relatorioPorCurso" );
		relatorioMBean.setRelatorio( RelatorioCurso.getRelatorio(RelatorioCurso.ALUNOS_CONCLUINTES));
		relatorioMBean.getRelatorio().setCurso(getCursoAtualCoordenacao());

		return relatorioMBean.gerarRelatorioAlunosConcluintes();
	}

	/** Gera o relat�rio de trancamentos.
	 * 
	 * chamado por /graduacao/menus/coordenacao.jsp
	 * chamado por /graduacao/menu_coordenador.jsp
	 * 
	 * @return {@value #JSP_ANO_PERIODO}
	 * @throws DAOException
	 */
	public String relatorioTrancamentos() throws DAOException {
		tipoRelatorio = REL_TRANCAMENTOS;
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		if(ead) getCurrentSession().setAttribute("cursoAtual", getCursoAtualCoordenacao());
		return forward(JSP_ANO_PERIODO);
	}

	/** Gera o relat�rio de alunos ativos no curso.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return {@value #JSP_REL_ATIVOS}
	 * @throws DAOException
	 */
	public String relatorioAlunosAtivosCurso() throws DAOException {
		RelatoriosCoordenadorDao dao = getDAO(RelatoriosCoordenadorDao.class);
		relatorio = dao.findAlunosAtivosCurso(getCursoAtualCoordenacao());
		return forward(JSP_REL_ATIVOS);
	}

	/** Gera o relat�rio de alunos pendentes de matr�cula no ano/semestre.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/lato/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return {@value #JSP_ANO_PERIODO}
	 * @throws DAOException
	 */
	public String relatorioAlunosPendenteMatricula() throws DAOException {
		tipoRelatorio = REL_PENDENTE_MATRICULA;
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		if(ead) getCurrentSession().setAttribute("cursoAtual", getCursoAtualCoordenacao());
		return forward(JSP_ANO_PERIODO);
	}

	/** Gera o relat�rio de matr�culas n�o atendidas no curso.
	 * 
	 * Chamado por /graduacao/menu_coordenador.jsp
	 * Chamado por /lato/menu_coordenador.jsp 
	 * 
	 * @return {@value #JSP_REL_MATRICULAS_NAO_ATENDIDAS}
	 * @throws DAOException
	 */
	public String relatorioMatriculasNaoAtendidas() throws DAOException {
		RelatoriosCoordenadorDao dao = getDAO(RelatoriosCoordenadorDao.class);
		setCurso(getCursoAtualCoordenacao());		
		relatorio = dao.findMatriculasOnlineNaoAtendidas(getCursoAtualCoordenacao());
		return forward(JSP_REL_MATRICULAS_NAO_ATENDIDAS);
	}

	/** Gera o relat�rio de turmas n�o consolidadas.
	 *  
	 *  Chamado por /graduacao/menus/coordenacao.jsp
	 *  Chamado por /graduacao/menu_coordenador.jsp
	 *  
	 * @return {@value #JSP_ANO_PERIODO}
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String relatorioTurmasConsolidadas() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.COORDENADOR_GERAL_EAD);
		tipoRelatorio = REL_TURMAS_CONSOLIDADAS;
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		if(ead) getCurrentSession().setAttribute("cursoAtual", getCursoAtualCoordenacao());
		return forward(JSP_ANO_PERIODO);
	}

	/** Gera o relat�rio de turmas consolidadas no departamento.
	 * 
	 * Chamado por /graduacao/departamento.jsp
	 * Chamado por /portais/docente/menu_docente.jsp
	 * 
	 * @return {@value #JSP_ANO_PERIODO}
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String relatorioTurmasConsolidadasDepartamento() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO);
		tipoRelatorio = REL_TURMAS_CONSOLIDADAS_DEPTO;
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		return forward(JSP_ANO_PERIODO);
	}

	/** Gera o relat�rio de reprova��es em disciplinas.
	 * 
	 * Chamado por /graduacao/menus/coordenacao.jsp
	 * Chamado por /graduacao/menu_coordenador.jsp
	 * 
	 * @return {@value #JSP_ANO_PERIODO}
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String relatorioReprovacoesDisciplinas() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.COORDENADOR_GERAL_EAD);
		tipoRelatorio = REL_REPROVACOES_DISCIPLINAS;
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		ead = getCursoAtualCoordenacao().getModalidadeEducacao().isADistancia();
		if(ead) getCurrentSession().setAttribute("cursoAtual", getCursoAtualCoordenacao());
		return forward(JSP_ANO_PERIODO);
	}

	/** Gera o relat�rio de reprova��es por disciplinas no departamento.
	 * 
	 * Chamado por /portais/docente/menu_docente.jsp
	 * 
	 * @return {@value #JSP_ANO_PERIODO}
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String relatorioReprovacoesDisciplinasDepartamento() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO);
		tipoRelatorio = REL_REPROVACOES_DISCIPLINAS_DEPTO;
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		exibeCurso = false;
		exibeAnoPeriodoIngresso = false;
		return forward(JSP_ANO_PERIODO);
	}
	
	/** Gera o relat�rio com percentual de carga hor�ria conclu�da pelos discentes do curso. 
	 * 
	 * Chamado por /graduacao/menu_coordenador.jsp
	 * 
	 * @return @see RelatorioPorCursoMBean#gerarRelatorioPercentualCHAluno()
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String relatorioPercentualCHAluno() throws DAOException, SegurancaException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);

		RelatorioPorCursoMBean relatorioMBean = (RelatorioPorCursoMBean) getMBean( "relatorioPorCurso" );
		relatorioMBean.setRelatorio( RelatorioCurso.getRelatorio(RelatorioCurso.ALUNOS_COM_PERCENTUAL_CH_CUMPRIDA));
		relatorioMBean.getRelatorio().setCurso(getCursoAtualCoordenacao());

		return relatorioMBean.gerarRelatorioPercentualCHAluno();
	}

	/**
	 * Gera relat�rio exibindo disciplinas do Curso/Ementa
	 * Chamado por /graduacao/menu_coordenador.jsp
	 */
    public String relatorioEmentaDisciplinasCurso() throws DAOException, SegurancaException{
        checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
        tipoRelatorio = REL_DISCIPLINAS_CURSO;
        ano = getCalendarioVigente().getAno();
        periodo = getCalendarioVigente().getPeriodo();
        return forward(JSP_ANO_PERIODO);
    }
    
    /**
	 * Inicia a gera��o do relat�rio de equival�ncias de um curr�culo.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 */
    public String relatorioEquivalencias() throws DAOException, SegurancaException{
    	if(getAcessoMenu().isCoordenadorCursoGrad() || getAcessoMenu().isSecretarioGraduacao()) {
    		curso = getCursoAtualCoordenacao();
    		carregarMatrizes(curso.getId());
    	}
        tipoRelatorio = REL_EQUIVALENCIAS;
        return forward(JSP_EQUIVALENCIAS);
    }
    
	/** Retorna os dados do relat�rio.
	 * @return
	 */
	public Map<?, ?> getRelatorio() {
		return relatorio;
	}

	/** Seta os dados do relat�rio.
	 * @param relatorio
	 */
	public void setRelatorio(Map<?, ?> relatorio) {
		this.relatorio = relatorio;
	}

	/** Retorna o ano para gera��o do relat�rio.
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano para gera��o do relat�rio
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o per�odo para gera��o do relat�rio.
	 * @return
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o per�odo para gera��o do relat�rio.
	 * @param periodo
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna o tipo de relat�rio
	 * @return
	 */
	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	/** Seta o tipo de relat�rio
	 * @param tipoRelatorio
	 */
	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
 
	/** Retorna o t�tulo do relat�rio.
	 * @return
	 */
	public String getTituloRelatorio() {
		return titulosRelatorio.get(this.tipoRelatorio);
	}

	/** Seta se o relat�rio � de um curso EAD.
	 * @param ead
	 */
	public void setEad(boolean ead) {
		this.ead = ead;
	}
	
	/** Informa se o curso � EAD.
	 * @return
	 */
	public boolean isEad() {
		return ead;
	}
	
	/** Retorna a lista de componentes curriculares para o relat�rio. 
	 * @return
	 */
	public List<ComponenteCurricular> getListaDisciplinasEmenta() {
        return listaDisciplinasEmenta;
    }

	/** Seta a lista de componentes curriculares para o relat�rio.
     * @param listaDisciplinasEmenta
     */
    public void setListaDisciplinasEmenta(
            List<ComponenteCurricular> listaDisciplinasEmenta) {
        this.listaDisciplinasEmenta = listaDisciplinasEmenta;
    }
	
	/**
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/coordenador/ano_periodo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getCursoAtualCoordenacao()
	 */
	@Override
	public Curso getCursoAtualCoordenacao() {
		if (ead) {
			OperacoesCoordenadorGeralEadMBean bean = (OperacoesCoordenadorGeralEadMBean) getMBean("opCoordenadorGeralEad");
			return bean.getCurso() == null ? super.getCursoAtualCoordenacao() : bean.getCurso() ;
		} else {
			return super.getCursoAtualCoordenacao();
		}
	}

	/** Retorna o curso do relat�rio.
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso do relat�rio.
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Valida os dados: ano, per�odo, e anoIngresso.
	 * M�todo n�o invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		ValidatorUtil.validateRequired(ano, "Ano", mensagens);
		ValidatorUtil.validateRequired(periodo, "Per�odo", mensagens);
		ValidatorUtil.validateMinValue(ano, 1900, "Ano", mensagens);
		if (!isEmpty(ano))
			ValidatorUtil.validateMaxValue(anoIngresso, ano, "Ano Ingresso", mensagens);
		
		if(tipoRelatorio == REL_TURMAS_CONSOLIDADAS_DEPTO) {
			if(this.periodo > 4)
				mensagens.addErro("Per�odo Inv�lido.");
		} else {
			if (this.periodo != 1 && this.periodo != 2)
				mensagens.addErro("Somente Per�odos regulares (1� ou 2�)");
			if (this.ano > getCalendarioVigente().getAno() 
					|| this.ano == getCalendarioVigente().getAno() && this.periodo > getCalendarioVigente().getPeriodo()) 
				mensagens.addErro("O Ano-Per�odo n�o deve ser maior que o atual");
		}
		return mensagens.size() > 0;
	}

	/** Seta o ano ingresso dos discente
	 * @return
	 */
	public Integer getAnoIngresso() {
		return anoIngresso;
	}

	/** Retorna o ano ingresso dos discente 
	 * @param anoIngresso
	 */
	public void setAnoIngresso(Integer anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	/** Retorna o per�odo ingresso dos discente
	 * @return
	 */
	public Integer getPeriodoIngresso() {
		return periodoIngresso;
	}

	/** Seta o per�odo ingresso dos discente 
	 * @param periodoIngresso
	 */
	public void setPeriodoIngresso(Integer periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	/** Indica se deve o formul�rio tem o campo "curso". 
	 * @return
	 */
	public boolean isExibeCurso() {
		return exibeCurso;
	}

	/** Seta se deve o formul�rio tem o campo "curso". 
	 * @param exibeCurso
	 */
	public void setExibeCurso(boolean exibeCurso) {
		this.exibeCurso = exibeCurso;
	}

	/** Indica se deve o formul�rio tem o campo "Ano/Per�odo de Ingresso". 
	 * @return
	 */
	public boolean isExibeAnoPeriodoIngresso() {
		return exibeAnoPeriodoIngresso;
	}

	/** Ssta se deve o formul�rio tem o campo "Ano/Per�odo de Ingresso". 
	 * @param exibeAnoPeriodoIngresso
	 */
	public void setExibeAnoPeriodoIngresso(boolean exibeAnoPeriodoIngresso) {
		this.exibeAnoPeriodoIngresso = exibeAnoPeriodoIngresso;
	}

	public MatrizCurricular getMatriz() {
		return matriz;
	}

	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}

	public Curriculo getCurriculo() throws DAOException {
		return curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}
	
	/**
	 * Realiza a chamada do metodo de carregar as matrizes curriculares passando o novo valor selecionado na p�gina.
	 * <br>Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/coordenador/form_equivalencias_curriculo.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarMatrizes(ValueChangeEvent evt) throws DAOException {
		carregarMatrizes((Integer)evt.getNewValue());
	}

	/**
	 * Carrega as matrizes curriculares de acordo com o curso cujo id foi passado como par�metro.
	 * @param idCurso
	 * @throws DAOException
	 */
	private void carregarMatrizes(Integer idCurso) throws DAOException {
		curriculos = new ArrayList<SelectItem>();
		
		if (!isEmpty(idCurso)) {
			Collection<MatrizCurricular> col = getDAO(MatrizCurricularDao.class).findAtivasByCurso(idCurso);
			if(!isEmpty(col))
				matrizesCurriculares = toSelectItems(col, "id", "descricaoMin");
		}
	}

	/**
	 * Retorna as matrizes curriculares de acordo com o curso escolhido.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getMatrizesCurriculares() throws DAOException {
		return matrizesCurriculares;
	}
	
	/**
	 * Carrega os curr�culos de acordo com a matriz curricular selecionada na p�gina.
	 * <br>Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/coordenador/form_equivalencias_curriculo.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void carregarCurriculos(ValueChangeEvent evt) throws DAOException {
		curriculos = new ArrayList<SelectItem>();
		
		Integer idMatriz = (Integer) evt.getNewValue();
		
		if (!isEmpty(idMatriz)) {
			Collection<Curriculo> col = getDAO(EstruturaCurricularDao.class).findByMatriz(idMatriz);
			if (!isEmpty(col))
				curriculos.addAll(toSelectItems(col, "id", "descricao"));
		}
	}
	
	/**
	 * Retorna os curr�culos de acordo com a matriz curricular escolhida.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getCurriculos() throws DAOException {
		return curriculos;
	}

	public Collection<SugestaoMatriculaEquivalentes> getEquivalentes() {
		return equivalentes;
	}

	public void setEquivalentes(
			Collection<SugestaoMatriculaEquivalentes> equivalentes) {
		this.equivalentes = equivalentes;
	}
	
	/**
	 * Retorna uma cole��o de {@link SelectItem} com os cursos de gradua��o.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/coordenador/form_equivalencias_curriculo.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCursos() throws DAOException {
		CursoDao dao = getDAO(CursoDao.class);
		
		Collection<Curso> cursos = dao.findByNivel( NivelEnsino.GRADUACAO );
		return toSelectItems(cursos, "id", "descricao");
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}
	
}
