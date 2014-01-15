/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 05/10/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.medio.dao.RelatoriosMedioDao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean respons�vel por gerar relat�rios do Ensino M�dio.
 * 
 * @author Rafael Gomes
 *
 */
@SuppressWarnings("unchecked")
@Component("relatoriosMedio") @Scope("request")
public class RelatoriosMedioMBean extends SigaaAbstractController{

	/** Constante de visualiza��o da view de sele��o do quantitativo de matriculados  */
	public final static String JSP_SELECIONAQ_MATRICULADOS = "/medio/relatorios/selecionaq_matriculados.jsp";
	/** Constante de visualiza��o da view de sele��o dos ingressantes  */
	public final static String JSP_SELECIONA_MATRICULADOS = "/medio/relatorios/seleciona_matriculados.jsp";
	/** Constante de visualiza��o da view de sele��o dos aprovados / Reprovados  */
	public final static String JSP_SELECIONA_APROVADOS_REPROVADOS = "/medio/relatorios/seleciona_aprovados_reprovados.jsp";
	/** Constante de visualiza��o da view da lista do quantitativo de matriculados */
	public final static String JSP_QUANT_MATRICULADOS = "/medio/relatorios/quant_matriculados.jsp";
	/** Constante de visualiza��o da view da lista dos matriculados */
	public final static String JSP_LISTA_MATRICULADOS = "/medio/relatorios/lista_matriculados.jsp";
	/** Constante de visualiza��o da view da listagem dos alunos por curso */
	public final static String JSP_LISTAGEM_ALUNOS_CURSO = "/medio/relatorios/alunos_curso.jsp";
	/** Constante de visualiza��o da view da lista do quantitativo ano ingresso */
	public final static String JSP_QUANT_ANO_INGRESSO = "/medio/relatorios/quant_ano_ingresso.jsp";
	/** Constante com o codigo da PR�-REITORIA DE PLANEJAMENTO E COORDENA��O GERAL */
	public final static Integer PROPLAN = 1449;
	
	/** List que armazena o resultado das consultas */
	private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();
	/** Informa��o do ano e do per�odo para ser usado nas consultas */
	private Integer ano, periodo;
	/** Informa��o do curso para ser usado nas consultas */
	private Integer idCurso;
	/** Armazena um combo com todos os curso desejados */
	private Collection<SelectItem> cursosCombo = new ArrayList<SelectItem>();
	/** Informa��o do formato, do nome e do t�tulo do relat�rio */
	private String formato, nomeRelatorio, tituloRelatorio;
	/** Armazena a informa��o da unidade */
	private Unidade unidade;
	/** Armazena um cole��o de discentes */
	private Collection<Discente> discentes = new ArrayList<Discente>();
	/** Map que armazena o resultado das consultas */
	private Map relatorio;
	
	
	/**
	 * Construtor padr�o.
	 */
	public RelatoriosMedioMBean() {
		unidade = new Unidade();
		formato = "pdf";
	}
	
	/**
	 * M�todo auxiliar para valida��o de par�metros submetidos aos relat�rios. 
	 */
	private void validateAno(){
		ValidatorUtil.validateRequired(ano, "Ano", erros);
	}
	
	/**
	 * M�todo auxiliar para valida��o de par�metros submetidos aos relat�rios. 
	 * @throws ArqException 
	 */
	private void validateUnidade() throws ArqException{
		erros = new ListaMensagens();
		if(isProplan())
		 ValidatorUtil.validateRequiredId(unidade.getId(), "Unidade", erros);
	}
	
	/**
	 * verifica se a unidade do usiario logado � proplan 
	 * @throws ArqException
	 * @return 
	 */
	public boolean isProplan() throws ArqException{
		if(getUsuarioLogado().getUnidade().getId() == PROPLAN)
			return true;
		return false;
	}
	
	/**
	 * Popula os dados e redireciona para o formul�rio com os par�metros do relat�rio de alunos matriculados.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/medio/menus/relatorios.jsp</li>
	 *	</ul>
	 */
	public String iniciarRelatorioListaAlunosMatriculados() throws DAOException{
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		return forward(JSP_SELECIONA_MATRICULADOS);
	}

	/**
	 * Gera o relat�rio de alunos matriculados
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/medio/relatorios/seleciona_matriculados.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRelatorioListaAlunosMatriculados() throws DAOException, ArqException {
		validateUnidade();
		validateAno();
		if(hasErrors()){
			addMensagens(erros);
			return null;
		}
		UnidadeDao uDao = getDAO(UnidadeDao.class);
		RelatoriosMedioDao dao =  getDAO(RelatoriosMedioDao.class);
		if(unidade.getId() == 0)
			unidade.setId(getUnidadeGestora());
			
		unidade= uDao.findById(unidade.getId());
		
		lista = dao.findListaAlunosMatriculados(ano,unidade.getId());
		return forward(JSP_LISTA_MATRICULADOS);
	}

	/**
	 * lista das unidade gestoras
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/medio/relatorios/seleciona_matriculados.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException 
	 */
	public List<SelectItem> getUnidades() throws DAOException {
		List<SelectItem> unidades = new ArrayList<SelectItem>(0);
		UnidadeDao dao = getDAO(UnidadeDao.class);
		unidades = toSelectItems(dao.findAllGestorasAcademicas(NivelEnsino.MEDIO), "id", "nome");
		return unidades;
	}
	/**
	 * Gera o relat�rio quantitativo de alunos por ano de ingresso.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/medio/menus/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String gerarRelatorioQuantitativoAlunosAnoIngresso() throws DAOException, ArqException {
		RelatoriosMedioDao dao = getDAO(RelatoriosMedioDao.class);
		lista = dao.findQuantitativoAlunosByAnoIngresso(getUnidadeGestora());
		return forward(JSP_QUANT_ANO_INGRESSO);
	}
	
	/**
	 * Retorna a quantidade de registros encontrados na lista
	 * 
	 * @return
	 */
	public int getNumeroRegistrosEncontrados() {
		if(lista!=null)
			return lista.size();
		else
			return 0;
	}
	
	/* Getters and Setters */
	
	public List<Map<String, Object>> getLista() {
		return lista;
	}

	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Integer getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Integer idCurso) {
		this.idCurso = idCurso;
	}

	public Collection<SelectItem> getCursosCombo() {
		return cursosCombo;
	}

	public void setCursosCombo(Collection<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public String getTituloRelatorio() {
		return tituloRelatorio;
	}

	public void setTituloRelatorio(String tituloRelatorio) {
		this.tituloRelatorio = tituloRelatorio;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

	public Map getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(Map relatorio) {
		this.relatorio = relatorio;
	}
	

	
	
}
