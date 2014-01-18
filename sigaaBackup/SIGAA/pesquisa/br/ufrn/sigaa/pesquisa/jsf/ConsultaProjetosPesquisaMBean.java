/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 06/07/2007
 */
package br.ufrn.sigaa.pesquisa.jsf;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.FinanciamentoProjetoPesq;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.ClassificacaoFinanciadora;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Managed Bean para efetuar consultas a projetos de pesquisas a partir
 * da área pública do sigaa.
 * 
 * @author Leonardo Campos
 *
 */
@Component("consultaProjetos")
@Scope("session")
public class ConsultaProjetosPesquisaMBean extends SigaaAbstractController<ProjetoPesquisa> {

	public final String JSP_CONSULTA_PROJETOS = "/public/pesquisa/consulta_projetos.jsp";
	public final String JSP_DETALHES_PROJETOS = "/public/pesquisa/view.jsp";

	// Atributos utilizados como filtros na Consulta de projetos de pesquisa.
	private boolean filtroTipo;
	private boolean filtroCodigo;
	private boolean filtroAno;
	private boolean filtroPesquisador;
	private boolean filtroCentro;
	private boolean filtroUnidade;
	private boolean filtroTitulo;
	private boolean filtroObjetivos;
	private boolean filtroArea;
	private boolean filtroGrupo;
	private boolean filtroAgencia;

	private FinanciamentoProjetoPesq financiamentoProjetoPesq;
	private String codigo;
	private Servidor pesquisador;
	
	private Integer codigoUnidade;
	
	/**      
     * Construtor Padrão.
     */
	public ConsultaProjetosPesquisaMBean() {
		clear();
	}
	
	private void clear() {
		obj = new ProjetoPesquisa();
		obj.setCodigo(new CodigoProjetoPesquisa());

		obj.setCentro(new Unidade());
		obj.setEdital(new EditalPesquisa());

		obj.setLinhaPesquisa( new LinhaPesquisa() );
		obj.getLinhaPesquisa().setGrupoPesquisa( new GrupoPesquisa() );

		obj.setSituacaoProjeto(new TipoSituacaoProjeto());
		obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());

		financiamentoProjetoPesq = new FinanciamentoProjetoPesq();
		financiamentoProjetoPesq.setProjetoPesquisa( obj );
		financiamentoProjetoPesq.getEntidadeFinanciadora()
		.setClassificacaoFinanciadora(new ClassificacaoFinanciadora());

		pesquisador = new Servidor();
		
		codigoUnidade = 0;
	}
	/**
	 * Usado para buscas de projetos de pesquisa 
	 * <br><br>
	 * Método chamado pelas seguintes páginas jsp:
	 * <ul><li>sigaa.war/public/departamento/pesquisa.jsp</li>
	 * </li>sigaa.war/public/pesquisa/consulta_projetos.jsp</li></ul>
	 * 
	 * @return
	 * @throws DAOException
	 *  
	 */
	public String buscar() throws DAOException {
		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class);
		Collection<ProjetoPesquisa> lista;
		
		/* Analisando filtros selecionados */
		Boolean tipo = null;
		CodigoProjetoPesquisa codigoProjeto = null;
		Integer ano = getParameterInt("ano");
		String palavraChave = null;
		String nomePesquisador = getParameter("nomePesquisador");
		Integer idCentro = getParameterInt("idCentro");
		Integer idUnidade = getParameterInt("idUnidade");
		String titulo = getParameter("titulo");
		String objetivos = null;
		Integer idArea = null;
		Integer idGrupoPesquisa = null;
		Integer idAgenciaFinanciadora = null;

		ListaMensagens erros = new ListaMensagens();
		
		// Definição dos filtros e validações
		if(filtroTipo)
			tipo = obj.isInterno();
		if(filtroCodigo)
			codigoProjeto = prepararCodigoProjeto(codigo);
		if(filtroAno || !isEmpty(ano)){
			ano = isEmpty(ano)?obj.getAno():ano;
			obj.setAno(ano);
			ValidatorUtil.validaInt(ano, "Ano do Projeto", erros);
		}
		if(filtroPesquisador || !isEmpty(nomePesquisador)){
			nomePesquisador = isEmpty(nomePesquisador)?pesquisador.getPessoa().getNome():nomePesquisador;
			getPesquisador().getPessoa().setNome(nomePesquisador);
			ValidatorUtil.validateRequired(nomePesquisador, "Pesquisador", erros);
		}
		if(filtroCentro ||  !isEmpty(idCentro)){
			idCentro = isEmpty(idCentro)?obj.getCentro().getId():idCentro;
			obj.getCentro().setId(idCentro);
			ValidatorUtil.validateRequiredId(idCentro, "Centro", erros);
		}
		if(filtroUnidade || !isEmpty(idUnidade)){
			idUnidade = isEmpty(idUnidade)?obj.getUnidade().getId():idUnidade;
			obj.getUnidade().setId(idUnidade);
			ValidatorUtil.validateRequiredId(idUnidade, "Departamento", erros);
		}
		if(filtroTitulo || !isEmpty(titulo)){
			titulo = isEmpty(titulo)?obj.getTitulo():titulo;
			obj.setTitulo(titulo);
			ValidatorUtil.validateRequired(titulo, "Título", erros);
		}
		if(filtroObjetivos){
			objetivos = obj.getObjetivos();
			ValidatorUtil.validateRequired(objetivos, "Objetivos", erros);
		}
		if(filtroArea){
			idArea = obj.getAreaConhecimentoCnpq().getId();
			ValidatorUtil.validateRequiredId(idArea, "Área de Conhecimento", erros);
		}
		if(filtroGrupo){
			idGrupoPesquisa = obj.getLinhaPesquisa().getGrupoPesquisa().getId();
			ValidatorUtil.validateRequiredId(idGrupoPesquisa, "Grupo de Pesquisa", erros);
		}
		if(filtroAgencia){
			idAgenciaFinanciadora = financiamentoProjetoPesq.getEntidadeFinanciadora().getId();
			ValidatorUtil.validateRequiredId(idAgenciaFinanciadora, "Agência Financiadora", erros);
		}

		try {

			if (erros.isEmpty()) {
				lista = projetoDao.filter(
						tipo,
						codigoProjeto,
						ano,
						null,
						nomePesquisador,
						palavraChave,
						idCentro,
						idUnidade,
						titulo,
						objetivos,
						null,
						idArea,
						idGrupoPesquisa,
						idAgenciaFinanciadora,
						null, true);

				if (!lista.isEmpty()) {
					setResultadosBusca(lista);
				} else {
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				}

			} else {
				addMensagens(erros);
			}

		} catch (LimiteResultadosException e) {
			addMensagemErro(e.getMessage());
		}

		return forward(JSP_CONSULTA_PROJETOS);
	}
	
	/**
	 * Método que popula projeto de pesquisa e prepara MBeans para visualização.
	 * <br><br>
	 * JSP: \public\pesquisa\view.jsp
	 * @throws ArqException 
	 * 
	 */
	public String view() throws ArqException {

		setId();	
		if (obj.getId() > 0){ 
			obj = getGenericDAO().findByPrimaryKey(obj.getId(),ProjetoPesquisa.class);
		}else
			addMensagemErro("Projeto de Pesquisa não selecionado");
		
		return forward(JSP_DETALHES_PROJETOS);
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	private CodigoProjetoPesquisa prepararCodigoProjeto(String cod) {
		CodigoProjetoPesquisa codigo = new CodigoProjetoPesquisa();

		if ( cod.length() < 11) {
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Código");
		}
		else {
			codigo.setPrefixo( cod.substring(0, 3).toUpperCase() );

			int hifen = cod.indexOf('-');
			if (hifen == -1) {
				addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Código");
				return null;
			}

			codigo.setNumero( Integer.valueOf(cod.substring(3, hifen)) );
			codigo.setAno( Integer.valueOf(cod.substring(hifen + 1, cod.length()).trim()) );
		}

		if (codigo.getPrefixo() == null || codigo.getNumero() == 0 || codigo.getAno() == 0) {
			codigo = null;
		}

		return codigo;
	}

	public boolean isFiltroAgencia() {
		return filtroAgencia;
	}

	public void setFiltroAgencia(boolean filtroAgencia) {
		this.filtroAgencia = filtroAgencia;
	}

	public boolean isFiltroAno() {
		return filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public boolean isFiltroArea() {
		return filtroArea;
	}

	public void setFiltroArea(boolean filtroArea) {
		this.filtroArea = filtroArea;
	}

	public boolean isFiltroCentro() {
		return filtroCentro;
	}

	public void setFiltroCentro(boolean filtroCentro) {
		this.filtroCentro = filtroCentro;
	}

	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public boolean isFiltroGrupo() {
		return filtroGrupo;
	}

	public void setFiltroGrupo(boolean filtroGrupo) {
		this.filtroGrupo = filtroGrupo;
	}

	public boolean isFiltroObjetivos() {
		return filtroObjetivos;
	}

	public void setFiltroObjetivos(boolean filtroObjetivos) {
		this.filtroObjetivos = filtroObjetivos;
	}

	public boolean isFiltroTipo() {
		return filtroTipo;
	}

	public void setFiltroTipo(boolean filtroTipo) {
		this.filtroTipo = filtroTipo;
	}

	public boolean isFiltroTitulo() {
		return filtroTitulo;
	}

	public void setFiltroTitulo(boolean filtroTitulo) {
		this.filtroTitulo = filtroTitulo;
	}

	public boolean isFiltroUnidade() {
		return filtroUnidade;
	}

	public void setFiltroUnidade(boolean filtroUnidade) {
		this.filtroUnidade = filtroUnidade;
	}

	public FinanciamentoProjetoPesq getFinanciamentoProjetoPesq() {
		return financiamentoProjetoPesq;
	}

	public void setFinanciamentoProjetoPesq(
			FinanciamentoProjetoPesq financiamentoProjetoPesq) {
		this.financiamentoProjetoPesq = financiamentoProjetoPesq;
	}

	public Servidor getPesquisador() {
		return pesquisador;
	}

	public void setPesquisador(Servidor pesquisador) {
		this.pesquisador = pesquisador;
	}

	public boolean isFiltroPesquisador() {
		return filtroPesquisador;
	}

	public void setFiltroPesquisador(boolean filtroPesquisador) {
		this.filtroPesquisador = filtroPesquisador;
	}

	/**
	 * Retorna os grupos de pesquisa possíveis 
	 * 
 	 * @return
 	 */
	public Collection<SelectItem> getAllGruposCombo(){
		try {
			return toSelectItems(getGenericDAO().findAllProjection(GrupoPesquisa.class, "nome", "asc", new String[]{"id", "nome"}), "id", "nomeCompacto");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		}
	}
	/**
	 * Retorna as situações possíveis de projetos
	 * 
 	 * @return
 	 */
	public Collection<SelectItem> getAllSituacoesCombo(){
		try {
			return toSelectItems(getGenericDAO().findByExactField(TipoSituacaoProjeto.class, "contexto", "P", "asc", "id" ), "id", "descricao");
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SelectItem>();
		}
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		super.cancelar();
		return forward("/public/home.jsp");
	}

	/**
	 * Realiza a busca de projetos de pesquisa a partir da unidade.
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public String consultarProjetosUnidade() throws DAOException, NegocioException {
		Integer idUnidade = getDAO(UnidadeDao.class).findIdByCodigo(codigoUnidade);
		if(ValidatorUtil.isEmpty(idUnidade))
			throw new NegocioException("Unidade não localizada.");
		obj.getCentro().setId( idUnidade );
		setFiltroCentro(true);
		buscar();
		return redirect("/public/pesquisa/consulta_projetos.jsf");
	}

	public Integer getCodigoUnidade() {
		return codigoUnidade;
	}

	public void setCodigoUnidade(Integer codigoUnidade) {
		this.codigoUnidade = codigoUnidade;
	}
}