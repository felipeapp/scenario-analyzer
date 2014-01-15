/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.dao.ensino.TipoMovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioDiscenteSqlDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioDocenteSqlDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.MotivoTrancamento;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** Classe com métodos para os Mbeans que geram os relatórios personalizados os quais não possuem um domínio único
 *
 * @author Eric Moura
 *
 */
public abstract class AbstractRelatorioGraduacaoMBean extends
		SigaaAbstractController<Integer> {

	/** Atributo do Objeto Unidade a ser utilizado no filtro centro do relatório. */
	protected Unidade centro = new Unidade();
	/** Atributo do Objeto Unidade a ser utilizado no filtro departamento do relatório. */
	protected Unidade departamento = new Unidade();
	/** Atributo do Objeto Curso a ser utilizado no filtro do relatório. */
	protected Curso curso = new Curso();
	/** Atributo do Objeto Componente a ser utilizado no filtro disciplina do relatório. */
	protected ComponenteCurricular disciplina = new ComponenteCurricular();
	/** Atributo do Objeto Discente a ser utilizado no filtro do relatório. */
	protected Discente discente = new Discente();
	/** Atributo do Objeto Tipo Motivação a ser utilizado no filtro do relatório. */
	protected TipoMovimentacaoAluno tipoMovimentacaoAluno = new TipoMovimentacaoAluno();
	/** Atributo utilizado para armazenar o valor do filtro ano e semestre atual do relatório. */
	protected int anoSemestreAtual;
	/** Atributo utilizado para armazenar o valor do filtro prazo conclusão do relatório. */
	protected int prazoConclusao;
	/** Atributo utilizado para armazenar o valor do filtro ano do relatório. */
	protected Integer ano;
	/** Atributo utilizado para armazenar o valor do filtro ano final do relatório. */
	protected Integer anoFim;
	/** Atributo utilizado para armazenar o valor do filtro período do relatório. */
	protected Integer periodo;
	/** Atributo utilizado para armazenar o valor do filtro período final do relatório. */
	protected Integer periodoFim;
	/** Atributo utilizado para armazenar o identificador do curso do relatório. */
	protected Integer idCurso;
	/** Atributo utilizado para armazenar o identificador da Matriz Curricular do relatório. */
	protected Integer idMatrizCurricular;
	/** Atributo utilizado para armazenar o código do componente do relatório. */
	protected String codigo;
	/** Atributo utilizado para armazenar o valor do filtro status final do relatório. */
	protected int status;
	/** Atributo do Objeto Matriz Curricular a ser utilizado como filtro para o relatório. */
	protected MatrizCurricular matrizCurricular = new MatrizCurricular();
	/** Atributo do Objeto Situação de Matricula a ser utilizado como filtro para o relatório. */
	protected SituacaoMatricula situacaoMatricula = new SituacaoMatricula();
	/** Atributo do Objeto Forma de Ingresso a ser utilizado como filtro para o relatório. */
	protected FormaIngresso formaIngresso = new FormaIngresso();
	/** Atributo do Objeto Situação da Turma a ser utilizado como filtro para o relatório. */
	protected SituacaoTurma situacaoTurma = new SituacaoTurma();
	/** Atributo do Objeto Motivo de Trancamento a ser utilizado como filtro para o relatório. */
	protected MotivoTrancamento motivoTrancamento = new MotivoTrancamento();
	/** Atributo utilizado para armazenar o valor do filtro nível de ensino do relatório. */
	protected char nivel = NivelEnsino.GRADUACAO;
	/** Atributo utilizado para armazenar o valor do filtro campus do relatório. */
	protected CampusIes campus;
	
	/** Mapa utilizado para armazenar os filtros selecionado para a construção do relatório.*/ 
	protected HashMap<Integer,Boolean> filtros = new HashMap<Integer,Boolean>();

	/** Coleção com todos os anos cadastrados no sistema*/
	private List<SelectItem> anos = new ArrayList<SelectItem>();

	/** Coleção com todos os períodos cadastrados no sistema */
	private List<SelectItem> periodos = new ArrayList<SelectItem>();

	/** Atributo para controlar o âmbito.*/
	private String ambito;

	/** Atributo para controlar o relatório a ser utilizado.*/
	private String relatorio;

	/** Contexto de endereçamento dos arquivos dos relatórios.*/
	public final String CONTEXTO ="/graduacao/relatorios/";

	/** Atributo para controlar se o filtro Ativo foi selecionado. */
	private boolean filtroAtivo = false;
	/** Atributo para controlar se o filtro Ano e Período foi selecionado. */
	private boolean filtroAnoPeriodo = false;
	/** Atributo para controlar se o filtro Unidade foi selecionado. */
	private boolean filtroUnidade = false;
	/** Atributo para controlar se o filtro Curso foi selecionado. */
	private boolean filtroCurso = false;
	/** Atributo para controlar se o filtro Matriz foi selecionado. */
	private boolean filtroMatriz = false;
	/** Atributo para controlar se o filtro Código foi selecionado. */
	private boolean filtroCodigo = false;
	/** Atributo para controlar se o filtro Ingresso foi selecionado. */
	private boolean filtroIngresso = false;
	/** Atributo para controlar se o filtro Ano de Saída foi selecionado. */
	private boolean filtroAnoSaida = false;
	/** Atributo para controlar se o filtro Egresso foi selecionado. */
	private boolean filtroEgresso = false;
	/** Atributo para controlar se o filtro Matriculado foi selecionado. */
	private boolean filtroMatriculado = false;
	/** Atributo para controlar se o filtro Centro foi selecionado. */
	private boolean filtroCentro = false;
	/** Atributo para controlar se o filtro Departamento foi selecionado. */
	private boolean filtroDepartamento = false;
	/** Atributo para controlar se o filtro Situação de Turma foi selecionado. */
	private boolean filtroSituacaoTurma = false;
	/** Atributo para controlar se o filtro Reserva de Curso foi selecionado. */
	private boolean filtroReservaCurso = false;
	/** Atributo para controlar se o filtro Motivo de Trancamento foi selecionado. */
	private boolean filtroMotivoTrancamento = false;
	/** Atributo para controlar se o filtro Afastamento Permanente foi selecionado. */
	private boolean filtroAfastamentoPermanente = false;
	/** Atributo para controlar se o filtro Pré-Requisitos foi selecionado. */
	private boolean filtroPreRequisitos = false;
	
	/** Atributo para controlar se o filtro Ativo foi selecionado. */
	private boolean todos = false;
	/** Atributo para controlar se o filtro de exibição de orientador foi selecionado. */
	private boolean exibirOrientador = false;


	/** Método de inicialização do objeto.*/
	protected void initObj() {
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual() ;
		idCurso = 0 ;
		idMatrizCurricular = 0;
		departamento = new Unidade(0);
		departamento.setUnidadeResponsavel(new Unidade(0));
		centro = new Unidade(0);
		curso = new Curso(0);
		curso.setUnidade(new Unidade(0));
		disciplina = new ComponenteCurricular(0);
		discente = new Discente(0);
		tipoMovimentacaoAluno = new TipoMovimentacaoAluno(0);
		matrizCurricular = new MatrizCurricular(0);
		matrizCurricular.setCurso(new Curso(0));
		matrizCurricular.getCurso().setUnidade(new Unidade(0));
		situacaoTurma = new SituacaoTurma();
		motivoTrancamento = new MotivoTrancamento();
		campus = new CampusIes();
	}

	/** Método responsável por carregar o título do relatório. 
	 * <br>
	 * Método não invocado por JSP.
	 * 
	 * */
	public void carregarTituloRelatorio() throws DAOException {
    	filtros.put(AbstractRelatorioSqlDao.ANO_PERIODO, isFiltroAnoPeriodo());
    	filtros.put(AbstractRelatorioSqlDao.ANO_SAIDA, isFiltroAnoSaida());
    	filtros.put(AbstractRelatorioSqlDao.ATIVO, isFiltroAtivo());
    	filtros.put(AbstractRelatorioSqlDao.CURSO, isFiltroCurso());
    	filtros.put(AbstractRelatorioSqlDao.EGRESSO, isFiltroEgresso());
    	filtros.put(AbstractRelatorioSqlDao.INGRESSO, isFiltroIngresso());
    	filtros.put(AbstractRelatorioSqlDao.MATRIZ_CURRICULAR, isFiltroMatriz());
    	filtros.put(AbstractRelatorioSqlDao.UNIDADE, isFiltroUnidade());
    	filtros.put(AbstractRelatorioSqlDao.MATRICULADO, isFiltroMatriculado());
    	filtros.put(AbstractRelatorioSqlDao.CENTRO, isFiltroCentro());
    	filtros.put(AbstractRelatorioSqlDao.DEPARTAMENTO, isFiltroDepartamento());
    	filtros.put(AbstractRelatorioSqlDao.SITUACAO_TURMA, isFiltroSituacaoTurma());
    	filtros.put(AbstractRelatorioSqlDao.RESERVA_CURSO, isFiltroReservaCurso());
    	filtros.put(AbstractRelatorioSqlDao.MOTIVO_TRANCAMENTO, isFiltroMotivoTrancamento());
    	filtros.put(AbstractRelatorioSqlDao.CODIGO, isFiltroCodigo());
    	filtros.put(AbstractRelatorioSqlDao.AFASTAMENTO_PERMANENTE, isFiltroAfastamentoPermanente());
    	filtros.put(AbstractRelatorioSqlDao.PRE_REQUISITOS, isFiltroPreRequisitos());


    	//Popular os objetos para ficar no título do relatório
    	if(isFiltroMatriz()){

    		MatrizCurricularDao matrizDao = getDAO(MatrizCurricularDao.class);
    		MatrizCurricular matriz = matrizDao.findByPrimaryKey(idMatrizCurricular, MatrizCurricular.class);
    		if( matriz != null ){
    			matrizCurricular = matriz;
    			centro = matrizCurricular.getCurso().getUnidade();
    			curso = matrizCurricular.getCurso();
    		}

    	} else if(isFiltroCurso() || isFiltroReservaCurso()){

    		CursoDao cursoDao = getDAO(CursoDao.class);

    		if(curso.getId()!=0 && curso !=null ){
    			curso = cursoDao.findByPrimaryKey(curso.getId(), Curso.class);
    			matrizCurricular.setCurso(curso);
    		}

    		else if(matrizCurricular.getCurso().getId() != 0 && matrizCurricular.getCurso() != null){
    			matrizCurricular.setCurso(cursoDao.findByPrimaryKey(matrizCurricular.getCurso().getId(), Curso.class));
    			curso = matrizCurricular.getCurso();
    		}

    		else
    			curso = new Curso();

    	} else if(isFiltroDepartamento()){

        		UnidadeDao unidadeDao = getDAO(UnidadeDao.class);

        		if(departamento.getId() != 0 && departamento!= null){
        			departamento = unidadeDao.findByPrimaryKey(departamento.getId(), Unidade.class);
        			centro = departamento.getUnidadeResponsavel();
        		}else
        			departamento = new Unidade();

        } else if(isFiltroCentro()){

    		UnidadeDao unidadeDao = getDAO(UnidadeDao.class);

    		if(centro.getId() != 0 && centro!= null){
    			centro = unidadeDao.findByPrimaryKey(centro.getId(), Unidade.class);
    			curso.setUnidade(centro);
    			departamento.setUnidadeResponsavel(centro);
    			matrizCurricular.getCurso().setUnidade(centro);
    		}

    		else if( curso != null && curso.getUnidade()!=null && curso.getUnidade().getId()!=0 ){
    			curso.setUnidade(unidadeDao.findByPrimaryKey(curso.getUnidade().getId(), Unidade.class));
    			centro = curso.getUnidade();
    			departamento.setUnidadeResponsavel(curso.getUnidade());
    			matrizCurricular.getCurso().setUnidade(curso.getUnidade());
    		}

    		else if(departamento !=null && departamento.getUnidadeResponsavel().getId() !=0 && departamento.getUnidadeResponsavel() !=null){
    			departamento.setUnidadeResponsavel(unidadeDao.findByPrimaryKey(departamento.getUnidadeGestora().getId() , Unidade.class));
    			curso.setUnidade(departamento.getUnidadeResponsavel());
    			centro = departamento.getUnidadeResponsavel();
    			matrizCurricular.getCurso().setUnidade(centro);
    		} else if(matrizCurricular !=null && matrizCurricular.getCurso()!=null &&
    				matrizCurricular.getCurso().getUnidade() !=null && matrizCurricular.getCurso().getUnidade().getId() !=0 ){
    			departamento.setUnidadeResponsavel(unidadeDao.findByPrimaryKey(matrizCurricular.getCurso().getUnidade().getId() , Unidade.class));
    			curso.setUnidade(departamento.getUnidadeResponsavel());
    			centro = departamento.getUnidadeResponsavel();
    			matrizCurricular.getCurso().setUnidade(centro);
    		}

    		else
    			centro = new Unidade();

    	}


    	if(isFiltroMotivoTrancamento()){

    		GenericDAO motivoTrancamentoDao = getDAO(GenericDAOImpl.class);

    		if(motivoTrancamento.getId()!=0 && motivoTrancamento !=null)
    			motivoTrancamento = motivoTrancamentoDao.findByPrimaryKey(motivoTrancamento.getId(), MotivoTrancamento.class);
    		else
    			motivoTrancamento = new MotivoTrancamento();
    	}


    	if(isFiltroEgresso()){

    		TipoMovimentacaoAlunoDao tipoMovimentacaoAlunoDao = getDAO(TipoMovimentacaoAlunoDao.class);

    		if(tipoMovimentacaoAluno.getId() != 0 && tipoMovimentacaoAluno != null)
    			tipoMovimentacaoAluno = tipoMovimentacaoAlunoDao.findByPrimaryKey(tipoMovimentacaoAluno.getId(), TipoMovimentacaoAluno.class);

    		else
    			tipoMovimentacaoAluno = new TipoMovimentacaoAluno();
    	}


    	if(isFiltroIngresso()){

    		FormaIngressoDao formaIngressoDao = getDAO(FormaIngressoDao.class);

    		if(formaIngresso.getId()!=0 && formaIngresso !=null)
    			formaIngresso = formaIngressoDao.findByPrimaryKey(formaIngresso.getId(), FormaIngresso.class);

    		else
    			formaIngresso = new FormaIngresso();
    	}

    	if(isFiltroSituacaoTurma()){
    		GenericDAO situacaoTurmaDao = getDAO(GenericDAOImpl.class);

    		if(situacaoTurma.getId()!=0 && situacaoTurma !=null)
    			situacaoTurma = situacaoTurmaDao.findByPrimaryKey(situacaoTurma.getId(), SituacaoTurma.class);
    		else
    			situacaoTurma = new SituacaoTurma();

    	}

	}


    /**
	 * @return the filtroAnoPeriodo
	 */
	public boolean isFiltroAnoPeriodo() {
		return filtroAnoPeriodo;
	}

	/**
	 * @param filtroAnoPeriodo the filtroAnoPeriodo to set
	 */
	public void setFiltroAnoPeriodo(boolean filtroAnoPeriodo) {
		this.filtroAnoPeriodo = filtroAnoPeriodo;
	}

	/**
	 * @return the filtroAtivo
	 */
	public boolean isFiltroAtivo() {
		return filtroAtivo;
	}

	/**
	 * @param filtroAtivo the filtroAtivo to set
	 */
	public void setFiltroAtivo(boolean filtroAtivo) {
		this.filtroAtivo = filtroAtivo;
	}

	/**
	 * @return the filtroCurso
	 */
	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	/**
	 * @param filtroCurso the filtroCurso to set
	 */
	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	/**
	 * @return the filtroMatriz
	 */
	public boolean isFiltroMatriz() {
		return filtroMatriz;
	}

	/**
	 * @param filtroMatriz the filtroMatriz to set
	 */
	public void setFiltroMatriz(boolean filtroMatriz) {
		this.filtroMatriz = filtroMatriz;
	}

	/**
	 * @return the filtroUnidade
	 */
	public boolean isFiltroUnidade() {
		return filtroUnidade;
	}

	/**
	 * @param filtroUnidade the filtroUnidade to set
	 */
	public void setFiltroUnidade(boolean filtroUnidade) {
		this.filtroUnidade = filtroUnidade;
	}

	/**
	 * @return the todos
	 */
	public boolean isTodos() {
		return todos;
	}

	/**
	 * @param todos the todos to set
	 */
	public void setTodos(boolean todos) {
		this.todos = todos;
	}

	/**
	 * @return the ambito
	 */
	public String getAmbito() {
		return ambito;
	}

	/**
	 * @param ambito the ambito to set
	 */
	public void setAmbito(String ambito) {
		this.ambito = ambito;
	}

	/**
	 * @return the relatorio
	 */
	public String getRelatorio() {
		return relatorio;
	}

	/**
	 * @param relatorio the relatorio to set
	 */
	public void setRelatorio(String relatorio) {
		this.relatorio = relatorio;
	}


	/**
	 * Retorna todos os anos que tem Turmas
	 *
	 * @return
	 * @throws DAOException
	 */
	@Override
	public List<SelectItem> getAnos() throws DAOException {
		if (anos == null || anos.isEmpty()) {
			RelatorioDocenteSqlDao dao = getDAO(RelatorioDocenteSqlDao.class);
			Collection<Integer> anosBD = dao.getAnos();
			anos = new ArrayList<SelectItem>();
			for (Integer ano : anosBD) {
				if (ano != null && ano!=0 ) {
					SelectItem item = new SelectItem(ano.toString(), ano
							.toString());
					anos.add(item);
				}
			}
		}

		return this.anos;
	}


	/**
	 * Retorna todos os anos que tem prazo de conclusão do aluno
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getAnosConclusao() throws DAOException {
		if (anos == null || anos.isEmpty()) {
			RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
			Collection<String> anosBD = dao.getAnosConclusao();
			anos = new ArrayList<SelectItem>();
			for (String ano : anosBD) {
				if (ano != null && !ano.equals("") ) {
					SelectItem item = new SelectItem(ano, ano);
					anos.add(item);
				}
			}
		}

		return this.anos;
	}

	/**
	 * retorna os 'status'de um discente
	 * @return
	 */
	public Collection<SelectItem> getAllStatusDiscente() {

			return toSelectItems(StatusDiscente.getStatusTodos(), "id", "descricao");

	}

	/**
	 * Retorna todos os períodos que tem turmas
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPeriodos() throws DAOException {
		if (periodos == null || periodos.isEmpty()) {
			RelatorioDocenteSqlDao dao = getDAO(RelatorioDocenteSqlDao.class);
			Collection<Integer> periodosBD = dao.getPeriodos();
			periodos = new ArrayList<SelectItem>();
			for (Integer periodo : periodosBD) {
				if (periodo != null && periodo!=0) {
					SelectItem item = new SelectItem(periodo.toString(), periodo
							.toString());
					periodos.add(item);
				}
			}
		}

		return this.periodos;
	}



	/**
	 * Retorna todos os períodos que tem prazo de conclusão do aluno
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getPeriodosConclusao() throws DAOException {
		if (periodos == null || periodos.isEmpty()) {
			RelatorioDiscenteSqlDao dao = getDAO(RelatorioDiscenteSqlDao.class);
			Collection<String> periodoDB = dao.getPeriodosConclusao();
			periodos = new ArrayList<SelectItem>();
			for (String periodo : periodoDB) {
				if (periodo != null && !periodo.equals("") ) {
					SelectItem item = new SelectItem(periodo,periodo);
					periodos.add(item);
				}
			}
		}

		return this.periodos;
	}


	/**
	 * Método para chamar a pagina de inicial do relatório
	 * <br>
	 * Método não invocado por JSP.
	 * @return String
	 */
	public String carregarSelecaoRelatorio(){
		return carregarSelecaoRelatorio(getParameter("relatorio"));
	}
	
	/** Reporta a seleção do relatório conforme o contexto. */
	protected String carregarSelecaoRelatorio(String relatorio) {
		CalendarioAcademico cal = getCalendarioVigente();
		if( cal != null ){
			ano = cal.getAno();
			periodo = cal.getPeriodo();
		}
		this.relatorio = relatorio;
		return forward(CONTEXTO+ambito+relatorio);
	}

	/**
	 * @return the filtroAnoSaida
	 */
	public boolean isFiltroAnoSaida() {
		return filtroAnoSaida;
	}

	/**
	 * @param filtroAnoSaida the filtroAnoSaida to set
	 */
	public void setFiltroAnoSaida(boolean filtroAnoSaida) {
		this.filtroAnoSaida = filtroAnoSaida;
	}

	/**
	 * @return the filtroEgresso
	 */
	public boolean isFiltroEgresso() {
		return filtroEgresso;
	}

	/**
	 * @param filtroEgresso the filtroEgresso to set
	 */
	public void setFiltroEgresso(boolean filtroEgresso) {
		this.filtroEgresso = filtroEgresso;
	}

	/**
	 * @return the filtroIngresso
	 */
	public boolean isFiltroIngresso() {
		return filtroIngresso;
	}

	/**
	 * @param filtroIngresso the filtroIngresso to set
	 */
	public void setFiltroIngresso(boolean filtroIngresso) {
		this.filtroIngresso = filtroIngresso;
	}

	/**
	 * @return the ano
	 */
	public Integer getAno() {
		return ano;
	}

	/**
	 * @param ano the ano to set
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/**
	 * @return the anoSemestreAtual
	 */
	public int getAnoSemestreAtual() {
		return anoSemestreAtual;
	}

	/**
	 * @param anoSemestreAtual the anoSemestreAtual to set
	 */
	public void setAnoSemestreAtual(int anoSemestreAtual) {
		this.anoSemestreAtual = anoSemestreAtual;
	}

	/**
	 * @return the centro
	 */
	public Unidade getCentro() {
		return centro;
	}

	/**
	 * @param centro the centro to set
	 */
	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	/**
	 * @return the curso
	 */
	public Curso getCurso() {
		return curso;
	}

	/**
	 * @param curso the curso to set
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/**
	 * @return the discente
	 */
	public Discente getDiscente() {
		return discente;
	}

	/**
	 * @param discente the discente to set
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/**
	 * @return the disciplina
	 */
	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	/**
	 * @param disciplina the disciplina to set
	 */
	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	/**
	 * @return the formaIngresso
	 */
	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	/**
	 * @param formaIngresso the formaIngresso to set
	 */
	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}

	/**
	 * @return the idCurso
	 */
	public Integer getIdCurso() {
		return idCurso;
	}

	/**
	 * @param idCurso the idCurso to set
	 */
	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	/**
	 * @return the idMatrizCurricular
	 */
	public Integer getIdMatrizCurricular() {
		return idMatrizCurricular;
	}

	/**
	 * @param idMatrizCurricular the idMatrizCurricular to set
	 */
	public void setIdMatrizCurricular(Integer idMatrizCurricular) {
		this.idMatrizCurricular = idMatrizCurricular;
	}

	/**
	 * @return the matrizCurricular
	 */
	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	/**
	 * @param matrizCurricular the matrizCurricular to set
	 */
	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	/**
	 * @return the periodo
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/**
	 * @param periodo the periodo to set
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	/**
	 * @return the prazoConclusao
	 */
	public int getPrazoConclusao() {
		return prazoConclusao;
	}

	/**
	 * @param prazoConclusao the prazoConclusao to set
	 */
	public void setPrazoConclusao(int prazoConclusao) {
		this.prazoConclusao = prazoConclusao;
	}

	/**
	 * @return the situacaoMatricula
	 */
	public SituacaoMatricula getSituacaoMatricula() {
		return situacaoMatricula;
	}

	/**
	 * @param situacaoMatricula the situacaoMatricula to set
	 */
	public void setSituacaoMatricula(SituacaoMatricula situacaoMatricula) {
		this.situacaoMatricula = situacaoMatricula;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the tipoMovimentacaoAluno
	 */
	public TipoMovimentacaoAluno getTipoMovimentacaoAluno() {
		return tipoMovimentacaoAluno;
	}

	/**
	 * @param tipoMovimentacaoAluno the tipoMovimentacaoAluno to set
	 */
	public void setTipoMovimentacaoAluno(TipoMovimentacaoAluno tipoMovimentacaoAluno) {
		this.tipoMovimentacaoAluno = tipoMovimentacaoAluno;
	}

	/**
	 * @return the departamento
	 */
	public Unidade getDepartamento() {
		return departamento;
	}

	/**
	 * @param departamento the departamento to set
	 */
	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	public boolean isFiltroMatriculado() {
		return filtroMatriculado;
	}

	public void setFiltroMatriculado(boolean filtroMatriculado) {
		this.filtroMatriculado = filtroMatriculado;
	}

	public boolean isFiltroCentro() {
		return filtroCentro;
	}

	public void setFiltroCentro(boolean filtroCentro) {
		this.filtroCentro = filtroCentro;
	}

	public boolean isFiltroDepartamento() {
		return filtroDepartamento;
	}

	public void setFiltroDepartamento(boolean filtroDepartamento) {
		this.filtroDepartamento = filtroDepartamento;
	}

	public boolean isFiltroSituacaoTurma() {
		return filtroSituacaoTurma;
	}

	public void setFiltroSituacaoTurma(boolean filtroSituacaoTurma) {
		this.filtroSituacaoTurma = filtroSituacaoTurma;
	}

	public boolean isFiltroReservaCurso() {
		return filtroReservaCurso;
	}

	public void setFiltroReservaCurso(boolean filtroReservaCurso) {
		this.filtroReservaCurso = filtroReservaCurso;
	}

	public SituacaoTurma getSituacaoTurma() {
		return situacaoTurma;
	}

	public void setSituacaoTurma(SituacaoTurma situacaoTurma) {
		this.situacaoTurma = situacaoTurma;
	}

	public MotivoTrancamento getMotivoTrancamento() {
		return motivoTrancamento;
	}

	public void setMotivoTrancamento(MotivoTrancamento motivoTrancamento) {
		this.motivoTrancamento = motivoTrancamento;
	}

	public boolean isFiltroMotivoTrancamento() {
		return filtroMotivoTrancamento;
	}

	public void setFiltroMotivoTrancamento(boolean filtroMotivoTrancamento) {
		this.filtroMotivoTrancamento = filtroMotivoTrancamento;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public boolean isExibirOrientador() {
		return exibirOrientador;
	}

	public void setExibirOrientador(boolean exibirOrientador) {
		this.exibirOrientador = exibirOrientador;
	}

	public boolean isFiltroAfastamentoPermanente() {
		return filtroAfastamentoPermanente;
	}

	public void setFiltroAfastamentoPermanente(boolean filtroAfastamentoPermanente) {
		this.filtroAfastamentoPermanente = filtroAfastamentoPermanente;
	}
	
	public Integer getAnoFim() {
		return anoFim;
	}

	public void setAnoFim(Integer anoFim) {
		this.anoFim = anoFim;
	}

	public Integer getPeriodoFim() {
		return periodoFim;
	}

	public void setPeriodoFim(Integer periodoFim) {
		this.periodoFim = periodoFim;
	}

	public CampusIes getCampus() {
		return campus;
	}

	public void setCampus(CampusIes campus) {
		this.campus = campus;
	}

	/**
	 * @return the filtroPreRequisitos
	 */
	public boolean isFiltroPreRequisitos() {
		return filtroPreRequisitos;
	}

	/**
	 * @param filtroPreRequisitos the filtroPreRequisitos to set
	 */
	public void setFiltroPreRequisitos(boolean filtroPreRequisitos) {
		this.filtroPreRequisitos = filtroPreRequisitos;
	}

}
