/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 27/08/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.criterion.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.internacionalizacao.dao.TraducaoElementoDao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.IdiomasEnum;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducaoElementos;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.TraducaoElemento;

/**
 * MBean para a manutenção das traduções dos elementos e componentes de uma estrutura curricular.
 * 
 * @author Rafael Gomes
 *
 */
@Component
@Scope("session")
public class TraducaoCurriculoMBean extends AbstractTraducaoElementoMBean<Curriculo>{

	/** Lista de componente curricular pertencente ao histórico do aluno.*/
	List<ComponenteCurricular> listComponentesHistorico = new ArrayList<ComponenteCurricular>();
	/** Mapa utilizado para listagem de componentes curriculares pertencentes ao histórico do discente.*/
	Map<ComponenteCurricular, List<ItemTraducaoElementos>> mapaComponente = new HashMap<ComponenteCurricular, List<ItemTraducaoElementos>>();
	/** Mapa utilizado para listagem de identificadores dos componentes curriculares pertencentes ao histórico do discente.*/
	Map<Integer, List<ItemTraducaoElementos>> mapaComponenteId = new HashMap<Integer, List<ItemTraducaoElementos>>();
	
	// Filtros da consulta
	/** Indica se filtra a busca de currículos por programa. */
	private boolean filtroPrograma;
	/** Indica se filtra a busca de currículos por curso. */
	private boolean filtroCurso;
	/** Indica se filtra a busca de currículos por matriz curricular. */
	private boolean filtroMatriz;
	/** Indica se filtra a busca de currículos por código. */
	private boolean filtroCodigo;
	
	/** Unidade ao qual se restringe a busca de currículos de stricto sensu. */
	private Unidade programa;
	/** Indica se o filtro é ativo ou não. */
	private boolean somenteAtivas = true;
	
	/** Select a ser preenchido para escolha de uma matriz do currículo. */
	private List<SelectItem> possiveisMatrizes = new ArrayList<SelectItem>(0);
	
	/** 
	 * Construtor padrão. 
	 */
	public TraducaoCurriculoMBean() {
		initObj();
	}
	
	/**
	 * Inicializa os atributos do controller.
	 * <br>Método não invocado por JSP´s
	 * 
	 * @throws DAOException
	 */
	public void initObj() {
		obj = new Curriculo();
		obj.setCurso(new Curso());
		obj.getCurso().setUnidade(new Unidade());
		obj.setMatriz(new MatrizCurricular());
		obj.getMatriz().setCurso(new Curso());
		programa = new Unidade();
		if (resultadosBusca != null)
			resultadosBusca.clear();
		initFiltros();
	}	
	
	/**
	 * Limpa os filtros de busca
	 */
	private void initFiltros() {
		this.possiveisMatrizes.clear();
		this.filtroCodigo = false;
		this.filtroCurso = false;
		this.filtroMatriz = false;
	}
	
	@Override
	public String getDirBase() {
		return "/relacoes_internacionais/curriculo";
	}
	
	/**
	 * Inicia o caso de uso de atualização.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkChangeRole();
		initObj();
		prepareMovimento(SigaaListaComando.TRADUZIR_ELEMENTO);
		setConfirmButton("Cadastrar");
		return forward(getListPage());
	}
	
	/**
	 * Executa a busca de currículos.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/relacoes_internacionais/curriculo/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {
		
		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		Integer idCurso = null;
		Integer idMatriz = null;
		Integer idPrograma = null;
		String codigo = null;
		if (resultadosBusca != null)
			resultadosBusca.clear();
		
		if (filtroCurso) {
			ValidatorUtil.validateRequiredId(obj.getMatriz().getCurso().getId(), "Curso", erros);
			idCurso = obj.getMatriz().getCurso().getId(); 
		}
		if (filtroPrograma) {
			ValidatorUtil.validateRequiredId(programa.getId(), "Programa", erros);
			idPrograma = programa.getId();
		}
		if (filtroMatriz) {
			ValidatorUtil.validateRequiredId(obj.getMatriz().getId(), "Matriz Curricular", erros);
			idMatriz = obj.getMatriz().getId();
		}
		if (filtroCodigo) {
			ValidatorUtil.validateRequired(obj.getCodigo(), "Matriz Curricular", erros);
			codigo = obj.getCodigo();
		}
		if (isEmpty(idCurso) && isEmpty(idMatriz) && isEmpty(codigo) && isEmpty(idPrograma)) {
			addMensagemErro("Selecione pelo menos um filtro de busca e informe o parâmetro da busca");
		}

		if(hasErrors())
			return null;
		
		try {
			Collection<Curriculo> curriculos = dao.findCompletoAtivo(idCurso, idMatriz, idPrograma, codigo, somenteAtivas);
			
			if (isEmpty(curriculos))
				addMensagemErro("Nenhuma estrutura curricular encontrada de acordo com os critérios de busca informados.");
			else
				setResultadosBusca(curriculos);
		} catch (Exception e) {
			if(e.getCause() instanceof LimiteResultadosException) {
				addMensagemErro(e.getMessage());
			}
			else {
				notifyError(e);
				addMensagemErroPadrao();
				setResultadosBusca(new ArrayList<Curriculo>(0));
			}
		}
		return null;
	}
	
	/**
	 * Método responsável por instanciar o objeto selecionado na listagem 
	 * e prepará-lo para a tradução de seus atributos.
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/relacoes_internacionais/elemento/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionar() throws ArqException {
		setOperacaoAtiva(SigaaListaComando.TRADUZIR_ELEMENTO.getId());
		prepareMovimento(SigaaListaComando.TRADUZIR_ELEMENTO);
		
		int id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		obj = dao.findByPrimaryKey(id, obj.getClass());
		
		carregarComponentesTraducao();
		return forward(getFormPage());
	}
	
	/**
	 * Método utilizado para preparar e carregar as propriedades da entidade Componente Curricular 
	 * passada por parâmetros para serem traduzidos. 
	 * <br/>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>Não invocado por JSP</li>
	 * </ul>
	 * @param listComponentes
	 * @throws DAOException
	 */
	public void carregarComponentesTraducao() throws DAOException{
		
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class);
		
		listComponentesHistorico = new ArrayList<ComponenteCurricular>();
		listComponentesHistorico.addAll(obj.getComponentesCurriculares());
		
		qtdeIdiomas = IdiomasEnum.values().length;
		listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
		List<TraducaoElemento> listElementoFinal = new ArrayList<TraducaoElemento>();
		
		Map<Integer, List<ItemTraducaoElementos>> mapComponenteAtributosTraducao = 
				new HashMap<Integer, List<ItemTraducaoElementos>>();
		
		Collection<ItemTraducao> atributosTraducao = dao.findItensByClasse(ComponenteCurricular.class.getName(), Order.desc("nome"));
		mapComponenteAtributosTraducao = dao.findComponentesTraducao(listComponentesHistorico);

		mapaComponente = new HashMap<ComponenteCurricular, List<ItemTraducaoElementos>>();
		
		
		for (ComponenteCurricular cc : listComponentesHistorico) {
			
			List<ItemTraducaoElementos> lista = mapComponenteAtributosTraducao.get(cc.getId());
			
			boolean inputDisabled = false;
			for (ItemTraducao itemTraducao : atributosTraducao) {
				
				ItemTraducaoElementos itElementos = new ItemTraducaoElementos(itemTraducao, new ArrayList<TraducaoElemento>());
				if (lista != null){
					for (ItemTraducaoElementos ite : lista) {
						if (ite.getItemTraducao().getId() == itemTraducao.getId())
							itElementos = ite;
					}
				}	
				
				if (itElementos.getElementos() != null && itElementos.getElementos().isEmpty()){
					for (String str : IdiomasEnum.getAll()) {
						TraducaoElemento elemento = new TraducaoElemento(itemTraducao, str, cc.getId());
						elemento.setDescricaoIdioma(IdiomasEnum.getDescricaoIdiomas().get(str));
						if (IdiomasEnum.PORTUGUES.getId().equalsIgnoreCase(str)){
							elemento.setValor((String) ReflectionUtils.evalPropertyObj(cc, itemTraducao.getAtributo()));
							inputDisabled = ValidatorUtil.isEmpty(elemento.getValor());
						} 
						elemento.setInputDisabled(inputDisabled);
						listElementoFinal.add(elemento);
					}
				} else {
					boolean insereElemento = true;
					for (String str : IdiomasEnum.getAll()) {
						insereElemento = true;
						for (TraducaoElemento te : itElementos.getElementos()) {
							if (te.getIdioma().equals(str)){
								te.setDescricaoIdioma(IdiomasEnum.getDescricaoIdiomas().get(str));
								te.setInputDisabled(inputDisabled);
								listElementoFinal.add(te);
								insereElemento = false;
								break;
							}
						}
						if (insereElemento){
							listElementoFinal.add(new TraducaoElemento(itemTraducao, str, cc.getId(), IdiomasEnum.getDescricaoIdiomas().get(str)));
						}
					}
				}
				listaTraducaoElemento.add(new ItemTraducaoElementos(itemTraducao, listElementoFinal));
				listElementoFinal = new ArrayList<TraducaoElemento>();
				
			}
			mapaComponente.put(cc, listaTraducaoElemento);
			mapaComponenteId.put(cc.getId(), listaTraducaoElemento);
			listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
		}
	}

	/**
	 * Carrega as matrizes curriculares. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/relacoes_internacionsis/curriculo/lista.jsp </li>
	 * </ul>
	 */
	public void carregarMatrizes(ValueChangeEvent e) throws DAOException {
		carregarMatrizes(e, true);
	}
	
	/**
	 * Carrega matrizes curriculares.
	 */
	private void carregarMatrizes(ValueChangeEvent e, Boolean ativas)
			throws DAOException {

		if (e.getNewValue() != null && (isGraduacao() || !NivelEnsino.isValido(getNivelEnsino()) || getNivelEnsino() == NivelEnsino.TECNICO))
			carregarMatrizes(new Integer(e.getNewValue().toString()), ativas);
		if (e.getNewValue() != null && !isGraduacao()) {
			Integer id = (Integer) e.getNewValue();
			if (isEmpty(id)) {
				addMensagemErro("Selecione uma Estrutura Curricular.");
				return;
			}
			obj.setCurso(getGenericDAO().findByPrimaryKey(id, Curso.class));
			if (!NivelEnsino.isResidenciaMedica(getNivelEnsino())) // se não for Residência médica, faz o fluxo abaixo
				if (obj.getCurso().getTipoCursoStricto() == null && NivelEnsino.isValido(getNivelEnsino()) && getNivelEnsino() != NivelEnsino.TECNICO) {
					addMensagemErro("Não é possível cadastrar currículos para cursos sem tipo de stricto definido");
				}
		}
	}
	
	/**
	 * Carrega matrizes curriculares de acordo com o curso e se estão ativas.
	 * <br/><br/>
	 * Método não chamado por JSP.
	 */
	public void carregarMatrizes(Integer idCurso, Boolean ativas) throws DAOException {
		if (idCurso > 0) {
			MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
			possiveisMatrizes = toSelectItems(dao.findByCurso(idCurso, ativas), "id", "descricao");
		} else {
			possiveisMatrizes = new ArrayList<SelectItem>(0);
		}
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		listaTraducaoElemento = new ArrayList<ItemTraducaoElementos>();
		for (ComponenteCurricular c : listComponentesHistorico) {
			listaTraducaoElemento.addAll(mapaComponente.get(c));
		}
		return super.cadastrar();
	}

	/**
	 * Indica se o nível de ensino do currículo é graduação.
	 * 
	 * @return
	 */
	public boolean isGraduacao() {
		return getNivelEnsino() == NivelEnsino.GRADUACAO
				|| obj.getCurso().getNivel() == NivelEnsino.GRADUACAO;
	}
	
	public List<ComponenteCurricular> getListComponentesHistorico() {
		return listComponentesHistorico;
	}

	public void setListComponentesHistorico(
			List<ComponenteCurricular> listComponentesHistorico) {
		this.listComponentesHistorico = listComponentesHistorico;
	}

	public Map<ComponenteCurricular, List<ItemTraducaoElementos>> getMapaComponente() {
		return mapaComponente;
	}

	public void setMapaComponente(
			Map<ComponenteCurricular, List<ItemTraducaoElementos>> mapaComponente) {
		this.mapaComponente = mapaComponente;
	}

	public Map<Integer, List<ItemTraducaoElementos>> getMapaComponenteId() {
		return mapaComponenteId;
	}

	public void setMapaComponenteId(
			Map<Integer, List<ItemTraducaoElementos>> mapaComponenteId) {
		this.mapaComponenteId = mapaComponenteId;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public boolean isFiltroMatriz() {
		return filtroMatriz;
	}

	public void setFiltroMatriz(boolean filtroMatriz) {
		this.filtroMatriz = filtroMatriz;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public List<SelectItem> getPossiveisMatrizes() {
		return possiveisMatrizes;
	}

	public void setPossiveisMatrizes(List<SelectItem> possiveisMatrizes) {
		this.possiveisMatrizes = possiveisMatrizes;
	}

	public boolean isFiltroPrograma() {
		return filtroPrograma;
	}

	public void setFiltroPrograma(boolean filtroPrograma) {
		this.filtroPrograma = filtroPrograma;
	}

	public Unidade getPrograma() {
		return programa;
	}

	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	public boolean isSomenteAtivas() {
		return somenteAtivas;
	}

	public void setSomenteAtivas(boolean somenteAtivas) {
		this.somenteAtivas = somenteAtivas;
	}
}
