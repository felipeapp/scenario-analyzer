/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 17/04/2012
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ead.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dao.RelatorioDocenteDisciplinasDao;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.jsf.UnidadeMBean;
import br.ufrn.sigaa.pessoa.dominio.Docente;

/**
 * Managed bean para gerar o relatório de docentes e suas disciplinas ministradas por semestre.
 * 
 * @author Rafael Gomes
 */
@SuppressWarnings("unchecked")
@Component("relatorioDocenteDisciplinasMBean") @Scope("request")
public class RelatorioDocenteDisciplinasMBean extends SigaaAbstractController {
	
	/** Filtro de Ano utilizado para construir o relatório de docentes com disciplinas por semestre.*/
	private Integer ano;
	
	/** Filtro de Período utilizado para construir o relatório de docentes com disciplinas por semestre.*/
	private Integer periodo;
	
	/** Objeto para armazenar o centro ou unidade acadêmica especializada. */
	private Unidade unidadeResponsavel;
	
	/** Filtro do Departamento utilizado para construir o relatório de docentes com disciplinas por semestre.*/
	private Unidade departamento; 
	
	/** Filtro do Pólo utilizado para construir o relatório de docentes com disciplinas por semestre.*/
	private Polo polo;

	/** Mapa com o resultado para a construção do relatório de docentes com disciplinas por semestre.*/
	private Map<Unidade,Map<Docente,Set<ComponenteCurricular>>> resultado;
	
	/** Itens utilizados na caixa de seleção do filtro departamento.*/
	private Collection<SelectItem> departamentoCombo = new ArrayList<SelectItem>();
	
	/** Jsp do formulário para o relatório. */
	public static final String JSP_RELATORIO_FORM = "/ead/relatorios/relatorio_docente_disciplinas_form.jsp";
	/** Jsp de exibição do relatório. */
	public static final String JSP_RELATORIO_LIST = "/ead/relatorios/relatorio_docente_disciplinas_list.jsp";
	/** Mapa destinado a armazenar e exibir os parA^metros utilizados para a construção do relatório.*/
	private Map<String,Object> mapParametrosRelatotio;
	
	
	/**
	 * Construtor padrão.
	 */
	public RelatorioDocenteDisciplinasMBean() {
		this.unidadeResponsavel = new Unidade();
		this.departamento = new Unidade();
		this.polo = new Polo();
	}


	/**
	 * Método inicial para a execução do relatório de docentes por semestre.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs: 
	 * <ul>
	 *   <li>/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciar() throws SegurancaException{
		checkChangeRole();
		carregarDepartamentoCombo();
		return forward(JSP_RELATORIO_FORM);
	}
	
	
	/**
	 * Retorna a combo com todas as unidades gestoras acadêmicas 
	 * do tipo CENTRO e UNID_ACADEMICA_ESPECIALIZADA dos níveis abrangentes 
	 * pelo Ensino A Distância. 
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs: 
	 * <ul>
	 *   <li>/sigaa.war/ead/relatorios/relatorio_docente_disciplinas_form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCentrosUnidAcademicaCombo() throws DAOException {
		Collection<SelectItem> allCentrosUnidAcademicaCombo = new ArrayList<SelectItem>(); 
		Collection<Integer> tiposAcademicas = new ArrayList<Integer>();
		if (allCentrosUnidAcademicaCombo.isEmpty()){
			tiposAcademicas.add(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA);
			tiposAcademicas.add(TipoUnidadeAcademica.CENTRO);
			Collection<Unidade> centrosUnidAcademica = getDAO(UnidadeDao.class).findByTipoUnidadeAcademicaNivel(tiposAcademicas,null);
			allCentrosUnidAcademicaCombo.addAll(toSelectItems(centrosUnidAcademica, "id", "nome"));
		}	
		return allCentrosUnidAcademicaCombo;
	}
	
	/** Retorna uma coleção de selectItem de unidades do tipo Departamento.
	 * @return
	 */
	private void carregarDepartamentoCombo() {
		UnidadeMBean unidadeMBean = getMBean("unidade");
		if (departamentoCombo.isEmpty())
			departamentoCombo.addAll(unidadeMBean.getAllDepartamentoCombo());
	}
	
	/**
	 * Altera o centro no select e carrega os departamentos do centro selecionado.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs: 
	 * <ul>
	 *   <li>/sigaa.war/ead/relatorios/relatorio_docente_disciplinas_form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void changeCentro(ValueChangeEvent e) throws DAOException{
		Integer id = Integer.valueOf( e.getNewValue().toString() );
		if (id > 0 ) {
			Unidade pai = new Unidade(id);
			UnidadeDao dao = getDAO(UnidadeDao.class);
			departamentoCombo.addAll(toSelectItems(dao.findBySubUnidades(pai, TipoUnidadeAcademica.DEPARTAMENTO),"id", "nome"));

		} else {
			carregarDepartamentoCombo();
		}
	}
	
	/**
	 * Retorna o selectItem de pólos
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs: 
	 * <ul>
	 *   <li>/sigaa.war/ead/relatorios/relatorio_docente_disciplinas_form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public  Collection<SelectItem> getPoloCombo() throws DAOException {
		PoloDao dao = getDAO(PoloDao.class);
		return toSelectItems(dao.findAllPolos(), "id", "cidade.nomeUF");
	}
	
	/**
	 * Gera  um relatório com os docentes com disciplinas por semestre. 
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs: 
	 * <ul>
	 *   <li>/sigaa.war/ead/relatorios/relatorio_docente_disciplinas_form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	@SuppressWarnings("null")
	public String gerar() throws DAOException, SegurancaException {
		checkChangeRole();
		RelatorioDocenteDisciplinasDao dao = getDAO(RelatorioDocenteDisciplinasDao.class);
		
		validate();
		if( hasErrors() ) return null;
		
		List<DocenteTurma> listDocenteTurma = dao.findListByDocenteDisciplinasByAnoPeriodo(ano, periodo, unidadeResponsavel, departamento, polo);
		
		resultado = new LinkedHashMap<Unidade, Map<Docente,Set<ComponenteCurricular>>>();
		Map<Docente, Set<ComponenteCurricular>> mapDocenteDisciplinas = new LinkedHashMap<Docente, Set<ComponenteCurricular>>();
		Set<ComponenteCurricular> listComponente = new HashSet<ComponenteCurricular>();
		
		DocenteTurma docenteTurma = null;
		DocenteTurma nextDocenteTurma = null;
		Unidade centro = null;
		Unidade nextCentro = null;
		Docente docente = null;
		Docente nextDocente = null;
		for (int i = 0; i < listDocenteTurma.size(); i++) {
			docenteTurma = listDocenteTurma.get(i);
			if (i < listDocenteTurma.size()-1)
				nextDocenteTurma = listDocenteTurma.get(i+1);
			else 
				nextDocenteTurma = null;
				
			docente = docenteTurma.getDocente().getId() > 0 ? docenteTurma.getDocente() : docenteTurma.getDocenteExterno();
			centro  = docenteTurma.getTurma().getDisciplina().getUnidade().getUnidadeResponsavel();
			
			if ( nextDocenteTurma != null ){
				nextCentro = nextDocenteTurma.getTurma().getDisciplina().getUnidade().getUnidadeResponsavel();
				nextDocente = nextDocenteTurma.getDocente() != null ? nextDocenteTurma.getDocente() : nextDocenteTurma.getDocenteExterno();
			} else {
				nextCentro = null;
				nextDocente = null;
			}
						
			if (nextDocenteTurma != null && docente.getId() == nextDocente.getId()) {
				listComponente.add(docenteTurma.getTurma().getDisciplina());
			} else {	
				listComponente.add(docenteTurma.getTurma().getDisciplina());
				mapDocenteDisciplinas.put(docente, listComponente);
				listComponente = new HashSet<ComponenteCurricular>();
			}	
			
			if (nextCentro != null){
				if (nextCentro.getId() != centro.getId()) {
					resultado.put(centro, mapDocenteDisciplinas);
					mapDocenteDisciplinas = new LinkedHashMap<Docente, Set<ComponenteCurricular>>();
				}	
			} else {
				resultado.put(centro, mapDocenteDisciplinas);
				mapDocenteDisciplinas = new LinkedHashMap<Docente, Set<ComponenteCurricular>>();
			}
		}	
		popularParametrosRelatorio();
		return forward(JSP_RELATORIO_LIST);
	}
	
	/** Método responsável por montar o mapa com os parâmetros utilizados para construir o relatório.
	 * @throws DAOException */
	private void popularParametrosRelatorio() throws DAOException{
		RelatorioDocenteDisciplinasDao dao = getDAO(RelatorioDocenteDisciplinasDao.class);
		mapParametrosRelatotio = new LinkedHashMap<String, Object>();
		mapParametrosRelatotio.put("Ano-Período:", new String(ano.toString()+'.'+periodo.toString()));
		if (ValidatorUtil.isNotEmpty(unidadeResponsavel) && unidadeResponsavel.getId() > 0 ){
			mapParametrosRelatotio.put("Centro/Unidade Acadêmica Especializada:", 
						dao.findByPrimaryKey(unidadeResponsavel.getId(), Unidade.class, "id", "nome").getNome());
		}
		if (ValidatorUtil.isNotEmpty(departamento) && departamento.getId() > 0 ){
			mapParametrosRelatotio.put("Departamento:", 
						dao.findByPrimaryKey(departamento.getId(), Unidade.class, "id", "nome").getNome());
		}
		if (ValidatorUtil.isNotEmpty(polo) && polo.getId() > 0 ){
			mapParametrosRelatotio.put("Pólo:", 
						dao.findByPrimaryKey(polo.getId(), Polo.class, "id", "cidade.id", "cidade.nome").getCidade().getNome());
		}
			
	}
	
	/**
	 * Valida filtros obrigatórios para geração do relatório.
	 * @return
	 */
	private void validate() {
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Período", erros);
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(getUsuarioLogado().getUnidade(), SigaaPapeis.SEDIS);
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


	public Unidade getUnidadeResponsavel() {
		return unidadeResponsavel;
	}


	public void setUnidadeResponsavel(Unidade unidadeResponsavel) {
		this.unidadeResponsavel = unidadeResponsavel;
	}


	public Unidade getDepartamento() {
		return departamento;
	}


	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}


	public Map<Unidade, Map<Docente, Set<ComponenteCurricular>>> getResultado() {
		return resultado;
	}


	public void setResultado(
			Map<Unidade, Map<Docente, Set<ComponenteCurricular>>> resultado) {
		this.resultado = resultado;
	}

	public Collection<SelectItem> getDepartamentoCombo() {
		if (departamentoCombo.isEmpty())
			carregarDepartamentoCombo();
		return departamentoCombo;
	}


	public void setDepartamentoCombo(Collection<SelectItem> listDepartamentoCombo) {
		this.departamentoCombo = listDepartamentoCombo;
	}


	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}


	public Map<String, Object> getMapParametrosRelatotio() {
		return mapParametrosRelatotio;
	}


	public void setMapParametrosRelatotio(Map<String, Object> mapParametrosRelatotio) {
		this.mapParametrosRelatotio = mapParametrosRelatotio;
	}
}
