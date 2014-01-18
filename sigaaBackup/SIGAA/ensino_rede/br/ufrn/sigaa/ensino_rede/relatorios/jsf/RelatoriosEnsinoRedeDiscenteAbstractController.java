package br.ufrn.sigaa.ensino_rede.relatorios.jsf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino_rede.dominio.ComponenteCurricularRede;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;
import br.ufrn.sigaa.ensino_rede.relatorios.dao.RelatorioEnsinoRedeDao;
import br.ufrn.sigaa.ensino_rede.relatorios.helper.RelatoriosEnsinoRedeHelper;
import br.ufrn.sigaa.ensino_rede.relatorios.helper.RelatoriosEnsinoRedeValidate;

@SuppressWarnings("serial")
public class RelatoriosEnsinoRedeDiscenteAbstractController<T> extends EnsinoRedeAbstractController<T> {

	/** Filtros */
	private boolean filtrarUnidade;
	private boolean filtrarStatus;
	private boolean filtrarAnoPeriodo;
	private boolean filtrarDisciplina;
	private boolean filtrarTaxa;
	
	/** Auxiliar */
	private String projecao; 
	private String ordenacao;
	private String transiente;
	private Map<String, List<?>> dados;
	private String urlDestino;
	private String validacao;
	
	/** View's */
	public static final String FORM_DISCENTE = "/ensino_rede/relatorios/form_relatorios/form_discente.jsp";
	public static final String FORM_DOCENTE = "/ensino_rede/relatorios/form_relatorios/form_docente.jsp";
	public static final String FORM_TURMAS = "/ensino_rede/relatorios/form_relatorios/form_turmas.jsp";
	public static final String FORM_MATRICULA = "/ensino_rede/relatorios/form_relatorios/form_matricula.jsp";
	
	private void limparFiltros() {
		filtrarStatus = false;
		filtrarUnidade = false;
		filtrarAnoPeriodo = false;
		filtrarDisciplina = false;
	}
	
	protected void limparDados() {
		limparFiltros();
		projecao = "";
		ordenacao = "";
		urlDestino = "";
		transiente = "";
		validacao = "";
		dados = new HashMap<String, List<?>>();
	}
	
	public String iniciarRelatorioDiscente() throws Exception {
		montarDadosSelecaoCampos();
		if ( !haFiltro() )
			return gerarRelatorioDiscente();
		return forward(FORM_DISCENTE);
	}

	public String iniciarRelatorioDocentes() throws Exception {
		montarDadosSelecaoCampos();
		if ( !haFiltro() )
			return gerarRelatorioDocente();
		return forward(FORM_DOCENTE);
	}

	public String iniciarRelatorioTurmas() throws Exception {
		montarDadosSelecaoCampos();
		if ( !haFiltro() )
			return gerarRelatorioTurma();
		return forward(FORM_TURMAS);
	}

	public String iniciarRelatorioMatricula() throws Exception {
		montarDadosSelecaoCampos();
		if ( !haFiltro() )
			return gerarRelatorioMatricula();
		return forward(FORM_MATRICULA);
	}

	private boolean haFiltro() {
		boolean haFiltro = false;
		Set<String> chaves = dados.keySet();  
        for (String chave : chaves) {  
            if ( dados.get(chave).isEmpty() )
            	haFiltro = true;
        }
        return haFiltro;
	}
	
	private void montarDadosSelecaoCampos() {
		if ( isCoordenadorUnidadeRede() ) {
			dados.put("filtrarUnidade", new ArrayList<Integer>() {{ add(getCampusIes().getId()); }});
			setFiltrarUnidade(false);
		}
		if ( filtrarStatus )
			dados.put("filtrarStatus", new ArrayList<String>());
		if ( filtrarUnidade )
			dados.put("filtrarUnidade", new ArrayList<Integer>());
		if ( filtrarDisciplina )
			dados.put("filtrarDisciplina", new ArrayList<Integer>());
		if ( filtrarTaxa )
			dados.put("filtrarTaxa", new ArrayList<Integer>());
		if ( filtrarAnoPeriodo ) {
			int semestre = CalendarUtils.getMesAtual1a12() < 7 ? 1 : 2;
			String periodo = CalendarUtils.getAnoAtual() + "." + semestre;
			ArrayList<String> anoPeriodo = new ArrayList<String>();
			anoPeriodo.add(periodo);
			dados.put("filtrarAnoPeriodo", anoPeriodo);
		}
	}

	@SuppressWarnings("unchecked")
	public String gerarRelatorioDiscente() throws Exception {
	    RelatorioEnsinoRedeDao dao = getDAO(RelatorioEnsinoRedeDao.class);
	    try {
	    	validate();
			if ( hasErrors() ) {
				addMensagens(erros);
				return null;
			}

	    	setResultadosBusca( (Collection<T>) dao.findByDiscentes(dados, projecao, ordenacao, transiente, getProgramaRede().getId()) );
	    	if (resultadosBusca.isEmpty() ) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			dao.close();
		}
	    
		return forward(urlDestino);
	}

	@SuppressWarnings("unchecked")
	public String gerarRelatorioDocente() throws Exception {
	    RelatorioEnsinoRedeDao dao = getDAO(RelatorioEnsinoRedeDao.class);
	    try {
	    	validate();
			if ( hasErrors() ) {
				addMensagens(erros);
				return null;
			}

	    	setResultadosBusca( (Collection<T>) dao.findByDocentes(dados, projecao, ordenacao, transiente, getProgramaRede().getId()) );
	    	if (resultadosBusca.isEmpty() ) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			dao.close();
		}
	    
		return forward(urlDestino);
	}
	
	@SuppressWarnings("unchecked")
	public String gerarRelatorioTurma() throws Exception {
	    RelatorioEnsinoRedeDao dao = getDAO(RelatorioEnsinoRedeDao.class);
	    try {
	    	validate();
			if ( hasErrors() ) {
				addMensagens(erros);
				return null;
			}
	    	setResultadosBusca( (Collection<T>) dao.findByTurmas(dados, projecao, ordenacao, transiente, getProgramaRede().getId()) );
	    	if (resultadosBusca.isEmpty() ) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			dao.close();
		}
	    
		return forward(urlDestino);
	}

	@SuppressWarnings("unchecked")
	public String gerarRelatorioMatricula() throws Exception {
	    RelatorioEnsinoRedeDao dao = getDAO(RelatorioEnsinoRedeDao.class);
	    try {
	    	validate();
			if ( hasErrors() ) {
				addMensagens(erros);
				return null;
			}
	    	setResultadosBusca( (Collection<T>) dao.findByMatricula(dados, projecao, ordenacao, transiente, getProgramaRede().getId()) );
	    	if (resultadosBusca.isEmpty() ) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			} else if ( dados.get("filtrarTaxa") != null && !dados.get("filtrarTaxa").isEmpty() ) {
				setResultadosBusca( (Collection<T>) RelatoriosEnsinoRedeHelper.calculoTaxa(resultadosBusca, getDados().get("filtrarTaxa")) );
			}
		} finally {
			dao.close();
		}
	    
		return forward(urlDestino);
	}

	private void validate() throws IllegalAccessException, InvocationTargetException, InstantiationException {
		if ( !validacao.isEmpty() ) {
			Method method = ReflectionUtils.getMethod(RelatoriosEnsinoRedeValidate.class, validacao);
			method.invoke((RelatoriosEnsinoRedeValidate) RelatoriosEnsinoRedeValidate.class.newInstance(), 
						new Object[]{dados, erros});
		}
	}
	
	public List<SelectItem> getUnidades() throws DAOException {
		RelatorioEnsinoRedeDao dao = getDAO(RelatorioEnsinoRedeDao.class);
		try {
			return dao.findUnidadesPrograma(getProgramaRede().getId(), getCampusIes());
		} finally {
			dao.close();
		}
	}

	public List<SelectItem> getStatusDiscente() throws DAOException {
		return toSelectItems(getGenericDAO().findAll(StatusDiscenteAssociado.class), "id", "descricao");
	}
	
	public List<SelectItem> getComponentesPrograma() throws DAOException {
		return toSelectItems(getGenericDAO().findByExactField(ComponenteCurricularRede.class, 
				"programa.id", getProgramaRede().getId(), "asc", "nome"), "id", "descricaoResumida");
	}

	public List<SelectItem> getSituacoesMatriculas() throws DAOException {
		return toSelectItems(getGenericDAO().findAllAtivos(SituacaoMatricula.class, "descricao"), "id", "descricao");
	}

	public boolean isFiltrarUnidade() {
		return filtrarUnidade;
	}

	public void setFiltrarUnidade(boolean filtrarUnidade) {
		this.filtrarUnidade = filtrarUnidade;
	}

	public boolean isFiltrarStatus() {
		return filtrarStatus;
	}

	public void setFiltrarStatus(boolean filtrarStatus) {
		this.filtrarStatus = filtrarStatus;
	}

	public String getProjecao() {
		return projecao;
	}

	public void setProjecao(String projecao) {
		this.projecao = projecao;
	}

	public Map<String, List<?>> getDados() {
		return dados;
	}

	public void setDados(Map<String, List<?>> dados) {
		this.dados = dados;
	}

	public String getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(String ordenacao) {
		this.ordenacao = ordenacao;
	}

	public String getUrlDestino() {
		return urlDestino;
	}

	public void setUrlDestino(String urlDestino) {
		this.urlDestino = urlDestino;
	}

	public boolean isFiltrarAnoPeriodo() {
		return filtrarAnoPeriodo;
	}

	public void setFiltrarAnoPeriodo(boolean filtrarAnoPeriodo) {
		this.filtrarAnoPeriodo = filtrarAnoPeriodo;
	}

	public String getTransiente() {
		return transiente;
	}

	public void setTransiente(String transiente) {
		this.transiente = transiente;
	}

	public boolean isFiltrarDisciplina() {
		return filtrarDisciplina;
	}

	public void setFiltrarDisciplina(boolean filtrarDisciplina) {
		this.filtrarDisciplina = filtrarDisciplina;
	}

	public boolean isFiltrarTaxa() {
		return filtrarTaxa;
	}

	public void setFiltrarTaxa(boolean filtrarTaxa) {
		this.filtrarTaxa = filtrarTaxa;
	}

	public String getValidacao() {
		return validacao;
	}

	public void setValidacao(String validacao) {
		this.validacao = validacao;
	}

}