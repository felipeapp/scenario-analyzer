/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 13/08/2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.jsf.UnidadeMBean;

/**
 * MBean respons�vel pela consulta de bancas de defesas/qualifica��o de p�s-gradua��o.
 * @see BancaPos
 * 
 * @author agostinho
 */
@Component("consultarDefesaMBean")
@Scope("session")
public class ConsultarDefesaMBean extends SigaaAbstractController<BancaPos> {

	// constantes que definem a ordena��o da lista de bancas
	/** Constante que define a ordena��o de banca por discente. */
	public static final int ORDENAR_BANCAS_POR_DISCENTE = 1;
	/** Constante que define a ordena��o de banca por data de defesa. */
	public static final int ORDENAR_BANCAS_POR_DATA = 2;
	/** Constante que define a ordena��o de banca por ano-per�odo de ingresso do discente. */
	public static final int ORDENAR_BANCAS_ANO_PERIODO_INGRESSO = 3;

	// par�metros da busca
	/** Tipo de banca: qualifica��o ou defesa. */
	private Integer tipoBanca;
	/** Data a partir da qual a busca por banca se restringir�. */
	private Date dataInicio;
	/** Data at� a qual a busca por banca se restringir�. */
	private Date dataFim;
	/** T�tulo do trabalho, utilizado na consulta. */
	private String tituloTrabalho;
	/** Nome do docente, utilizado na consulta. */
	private String docente;
	/** Nome do discente, utilizado na consulta. */
	private String discente;
	
	public boolean isCheckNivelEnsino() {
		return checkNivelEnsino;
	}

	public void setCheckNivelEnsino(boolean checkNivelEnsino) {
		this.checkNivelEnsino = checkNivelEnsino;
	}

	// binding din�mico
	/** Select, utilizado no formul�rio de busca, com as unidades gestoras dos programas. */
	private HtmlSelectOneMenu selectUnidadeGestora = new HtmlSelectOneMenu();

	//checks tela busca
	/** Indica que a busca deve restringir o tipo de banca. */
	private boolean checkTipoBanca = false;
	/** Indica que a busca deve restringir o programa. */
	private boolean checkPrograma = false;
	/** Indica que a busca deve restringir a banca com data posterior a indicada. */
	private boolean checkDataInicio = false;
	/** Indica que a busca deve restringir a banca com data anterior a indicada. */
	private boolean checkDataFim = false;
	/** Indica que a busca deve restringir o programa. */
	private boolean checkTituloTrabalho = false;
	/** Indica que a busca deve restringir pelo docente informado. */
	private boolean checkDocente = false;
	/** Indica que a busca deve restringir pelo discente informado. */
	private boolean checkDiscente = false;
	/** Indica que a busca deve restringir o tipo de banca. */
	private boolean checkNivelEnsino = false;

	/** Lista de bancas encontradas na busca. */
	private Collection<BancaPos> listaBancaPos;
	/** Lista de SelectItens de unidades gestoras, utilizadas no formul�rio. */
	private Collection<SelectItem> unidades = new ArrayList<SelectItem>();
	/** ID do arquivo de dados da banca, quando se tratar de banca antiga. */
	private Integer idArquivoDadosDefesa;
	/** Tipo de ordena��o do resultado da busca de bancas. */
	private int tipoOrdenacao;
	
	/**
	 * Indica se a consulta de defesas � realizada para iniciar o caso de uso de cataloga��o.
	 * Utilizado para reduzir o trabalho da cataloga��o pr�-populando com os dados da defesa cadastrada.
	 */
	private boolean ehCatalogacao = false;
	
	/**
	 * Indica se a consulta de defesas � realizada para associar uma defesa a uma cataloga��o no sistema.
	 * Utilizado para associar, ou alterar associa��es entre cataloga��es da biblioteca com defesas do salva no sistema. 
	 * J� que todas as defesas devem catalogadas.
	 */
	private boolean associacaoComCatalogacao = false;
	
	/**
	 * Guarda o n�vel de ensindo do discente que deve ser buscado.
	 */
	private Character nivelBusca;
	
	/**
	 * Construtor padr�o.
	 */
	public ConsultarDefesaMBean() {
	}
	
	/**
	 * <p>Inicia o caso de uso de consultar defesas de p�s gradua��o.<p>
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 *   <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {
		obj = new BancaPos();
		checkTipoBanca = false;
		checkPrograma = false;
		checkDataInicio = false;
		checkDataFim = false;
		checkTituloTrabalho = false;
		checkDocente = false;
		checkDiscente = false;
		checkNivelEnsino = false;
		tipoBanca = null;
		dataInicio = null;
		dataFim = null;
		tituloTrabalho = null;
		docente = null;
		discente = null;
		nivelBusca = null;
		listaBancaPos = new ArrayList<BancaPos>();
		tipoOrdenacao = ORDENAR_BANCAS_POR_DISCENTE;
		popularComboUnidadesGestoras();
		selectUnidadeGestora = new HtmlSelectOneMenu();
		if (getProgramaStricto() != null)
			selectUnidadeGestora.setValue(getProgramaStricto().getId());
		
		// se o par�metro catalogar for true ent�o deve exibir o link para iniciar a cataloga��o da defesa!
		ehCatalogacao = getParameterBoolean("catalogar");
		
		// se o par�metro associacaoComCatalogacao for true ent�o deve exibir o link voltar para a tela de cataloga��o
		// associando a cataloga��o escolhida com a cataloga��o que estava sendo editada
		associacaoComCatalogacao = getParameterBoolean("associacaoCatalogacao");
		
		return telaConsulta();
	}
	
	/**
	 * Popula a combo de unidades.
	 */
	private void popularComboUnidadesGestoras() {
		UnidadeMBean unidadeMBean = getMBean("unidade");
		unidades = unidadeMBean.getAllGestorasAcademicasCombo();
	}
	
	/**
	 * Executa a busca pelas defesas de acordo com os par�metros informados na jsp.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/banca_pos/consulta_defesas.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws DAOException {
		
		if ( validarParametros() ) {
			Integer programa  = null;
			int paramTipoBanca = 0;
			Date paramDataInicio = null;
			Date paramDataFim = null;
			String paramTituloTrabalho = null;
			String paramDocente = null;
			String paramDiscente = null;
			Character paramNivelEnsino = null;
			
			if (checkPrograma)
				programa = Integer.parseInt(selectUnidadeGestora.getValue().toString());
			if (checkTipoBanca)
				paramTipoBanca = tipoBanca;
			if (checkDataInicio)
				paramDataInicio = dataInicio;
			if (checkDataFim)
				paramDataFim = dataFim;
			if (checkTituloTrabalho)
				paramTituloTrabalho = tituloTrabalho;
			
			if (checkDocente)
				paramDocente = docente;
			
			if (checkDiscente)
				paramDiscente = discente;
		
			if (checkNivelEnsino)
				paramNivelEnsino = nivelBusca;
			BancaPosDao bancaPosDao = getDAO(BancaPosDao.class);
			if (checkDataFim)
				paramDataFim = CalendarUtils.configuraTempoDaData(paramDataFim, 23, 59, 59, 59);
			listaBancaPos = bancaPosDao.consultarDefesas(paramTipoBanca, programa, paramDataInicio, paramDataFim, 
					 paramTituloTrabalho, paramDocente, paramDiscente, tipoOrdenacao, paramNivelEnsino, BancaPos.ATIVO);				
			if(isEmpty(listaBancaPos)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Valida os dados do formul�rio.
	 */
	private boolean validarParametros() {
		boolean paramsValido = true;
		
		int idUnidade = Integer.parseInt(String.valueOf(selectUnidadeGestora.getValue()));
		if (checkPrograma == true && idUnidade == 0 ) {
			addMensagemErro("� necess�rio informar o Programa, pois voc� especificou esse crit�rio na busca.");
			paramsValido = false;
		}
		
		if (checkTipoBanca == true) {
			if (tipoBanca == null) {
				addMensagemErro("� necess�rio informar o Tipo da Banca, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		} else {
			tipoBanca = null;
		}
		
		if (checkDataInicio == true) {
			if ( dataInicio == null ) {
				addMensagemErro("� necessario informar a Data In�cio, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		}
		
		if (checkDataFim == true) {
			if ( dataFim == null ) {
				addMensagemErro("� necessario informar a Data Fim, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		}
		
		if (checkTituloTrabalho == true ) {
				if (tituloTrabalho == null || tituloTrabalho.trim().isEmpty() ) {
					addMensagemErro("� necess�rio informar o T�tulo do trabalho, pois voc� especificou esse crit�rio na busca.");
					paramsValido = false;
				}
		} else {
			tituloTrabalho = "";
		}
		
		if (!checkDataInicio) {
			dataInicio = null;
		}
		if (!checkDataFim) {
			dataFim = null;
		}
		if(checkDataInicio && checkDataFim
				&& dataFim != null && dataInicio != null
				&& dataFim.before(dataInicio)) {
			addMensagemErro("A data inicial n�o pode ser posterior � data final.");
			paramsValido = false;
		}
		
		if (checkDocente){
			if (docente == null || docente.trim().isEmpty()){
				addMensagemErro("� necess�rio informar o Docente, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		}
		
		if (checkDiscente){
			if (discente == null || discente.trim().isEmpty()){
				addMensagemErro("� necess�rio informar o Discente, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		}
		
		if (checkNivelEnsino){
			if (nivelBusca == null){
				addMensagemErro("� necess�rio informar o N�vel de Ensino, pois voc� especificou esse crit�rio na busca.");
				paramsValido = false;
			}
		}
		
		if (!checkNivelEnsino &&
				!checkTipoBanca &&
				!checkPrograma &&
				!checkDataInicio &&
				!checkDataFim &&
				!checkTituloTrabalho &&
				!checkDiscente &&
				!checkDocente) {
			addMensagemErro("� necess�rio informar pelo menos um crit�rio na busca.");
			paramsValido = false;
		}
		
		hasErrors();
		
		return paramsValido;
	}
	
	/**
	 * Redireciona para a visualiza��o do arquivo da defesa cadastrada.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/banca_pos/view_defesas.jsp</li>
	 * </ul>
	 * @return
	 */
	public String visualizarArquivo() {
		String path = getContextPath() + "/verProducao?idProducao=";
		if (idArquivoDadosDefesa != null)
			path = redirectJSF( path + idArquivoDadosDefesa + "&key=" + UFRNUtils.generateArquivoKey(idArquivoDadosDefesa));
		else
			addMensagemWarning("N�o existe arquivo anexado");
			
		return path;
	}
	
	/**
	 * Mostra a defesa do aluno.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/stricto/banca_pos/consulta_defesas.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String visualizarDadosDefesa() throws DAOException {
		if (obj == null) obj = new BancaPos();
		int idBancaPos = getParameterInt("idBancaPos", obj.getId());
		obj = getDAO(BancaPosDao.class).findByPrimaryKey(idBancaPos, BancaPos.class);
		if (obj == null) {
			obj = new BancaPos();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return cancelar();
		}
		idArquivoDadosDefesa = obj.getDadosDefesa().getIdArquivo();
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		int idDiscente = obj.getDadosDefesa().getDiscente().getId();
		obj.getDadosDefesa().getDiscente().setOrientacao(dao.findUltimoOrientadorByDiscente(idDiscente));
		obj.getDadosDefesa().getDiscente().setCoOrientacao(dao.findUltimoCoOrientadorByDiscente(idDiscente));
		return forward("/stricto/banca_pos/view_defesas.jsp");
	}
	
	/**
	 * M�todos de navega��o.
	 * JSP: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 */
	public String telaConsulta(){
		return forward( "/stricto/banca_pos/consulta_defesas.jsp" );
	}
	
	// gets e sets
	
	/** Retorna o tipo de banca: qualifica��o ou defesa.
	 * @return
	 */
	public Integer getTipoBanca() {
		return tipoBanca;
	}

	/** Seta o tipo de banca: qualifica��o ou defesa.
	 * @param tipoBanca
	 */
	public void setTipoBanca(Integer tipoBanca) {
		this.tipoBanca = tipoBanca;
	}

	/** Retorna a data a partir da qual a busca por banca se restringir�.
	 * @return
	 */
	public Date getDataInicio() {
		return dataInicio;
	}

	/** Seta a data a partir da qual a busca por banca se restringir�.
	 * @param dataInicio
	 */
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	/** Retorna a data at� a qual a busca por banca se restringir�.
	 * @return
	 */
	public Date getDataFim() {
		return dataFim;
	}

	/** Seta a data at� a qual a busca por banca se restringir�.
	 * @param dataFim
	 */
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	/** Retorna o t�tulo do trabalho, utilizado na consulta.
	 * @return
	 */
	public String getTituloTrabalho() {
		return tituloTrabalho;
	}

	/** Seta o t�tulo do trabalho, utilizado na consulta.
	 * @param tituloTrabalho
	 */
	public void setTituloTrabalho(String tituloTrabalho) {
		this.tituloTrabalho = tituloTrabalho;
	}

	/** Indica que a busca deve restringir o tipo de banca.
	 * @return
	 */
	public boolean isCheckTipoBanca() {
		return checkTipoBanca;
	}

	/** Seta que a busca deve restringir o tipo de banca.
	 * @param checkTipoBanca
	 */
	public void setCheckTipoBanca(boolean checkTipoBanca) {
		this.checkTipoBanca = checkTipoBanca;
	}

	/** Indica que a busca deve restringir o programa.
	 * @return
	 */
	public boolean isCheckPrograma() {
		return checkPrograma;
	}

	/** Seta que a busca deve restringir o programa.
	 * @param checkPrograma
	 */
	public void setCheckPrograma(boolean checkPrograma) {
		this.checkPrograma = checkPrograma;
	}

	/** Indica que a busca deve restringir a banca com data posterior a indicada.
	 * @return
	 */
	public boolean isCheckDataInicio() {
		return checkDataInicio;
	}

	/** Seta que a busca deve restringir a banca com data posterior a indicada.
	 * @param checkDataInicio
	 */
	public void setCheckDataInicio(boolean checkDataInicio) {
		this.checkDataInicio = checkDataInicio;
	}

	/** Indica que a busca deve restringir a banca com data anterior a indicada.
	 * @return
	 */
	public boolean isCheckDataFim() {
		return checkDataFim;
	}

	/** Seta que a busca deve restringir a banca com data anterior a indicada.
	 * @param checkDataFim
	 */
	public void setCheckDataFim(boolean checkDataFim) {
		this.checkDataFim = checkDataFim;
	}

	/** Indica que a busca deve restringir o programa.
	 * @return
	 */
	public boolean isCheckTituloTrabalho() {
		return checkTituloTrabalho;
	}

	/** Seta que a busca deve restringir o programa.
	 * @param checkTituloTrabalho
	 */
	public void setCheckTituloTrabalho(boolean checkTituloTrabalho) {
		this.checkTituloTrabalho = checkTituloTrabalho;
	}

	/** Retorna a lista de SelectItens de unidades gestoras, utilizadas no formul�rio.
	 * @return
	 */
	public Collection<SelectItem> getUnidades() {
		return unidades;
	}

	/** Seta a lista de SelectItens de unidades gestoras, utilizadas no formul�rio.
	 * @param unidades
	 */
	public void setUnidades(Collection<SelectItem> unidades) {
		this.unidades = unidades;
	}

	/** Retorna o select, utilizado no formul�rio de busca, com as unidades gestoras dos programas.
	 * @return
	 */
	public HtmlSelectOneMenu getSelectUnidadeGestora() {
		return selectUnidadeGestora;
	}

	/** Seta o select, utilizado no formul�rio de busca, com as unidades gestoras dos programas.
	 * @param selectUnidadeGestora
	 */
	public void setSelectUnidadeGestora(HtmlSelectOneMenu selectUnidadeGestora) {
		this.selectUnidadeGestora = selectUnidadeGestora;
	}

	/** Retorna a lista de bancas encontradas na busca.
	 * @return
	 */
	public Collection<BancaPos> getListaBancaPos() {
		return listaBancaPos;
	}

	/** Seta a lista de bancas encontradas na busca.
	 * @param listaBancaPos
	 */
	public void setListaBancaPos(Collection<BancaPos> listaBancaPos) {
		this.listaBancaPos = listaBancaPos;
	}

	/**
	 * Indica se a consulta de defesas � realizada para iniciar o caso de uso de cataloga��o.
	 * Utilizado para reduzir o trabalho da cataloga��o pr�-populando com os dados da defesa cadastrada.
	 */
	public boolean isEhCatalogacao() {
		return ehCatalogacao;
	}

	/**
	 * Seta se a consulta de defesas � realizada para iniciar o caso de uso de cataloga��o.
	 * Utilizado para reduzir o trabalho da cataloga��o pr�-populando com os dados da defesa cadastrada.
	 */
	public void setEhCatalogacao(boolean ehCatalogacao) {
		this.ehCatalogacao = ehCatalogacao;
	}

	/** Retorna o id do arquivo de dados da banca, quando se tratar de banca antiga.
	 * @return
	 */
	public Integer getIdArquivoDadosDefesa() {
		return idArquivoDadosDefesa;
	}

	/** Seta o id do arquivo de dados da banca, quando se tratar de banca antiga.
	 * @param idArquivoDadosDefesa
	 */
	public void setIdArquivoDadosDefesa(Integer idArquivoDadosDefesa) {
		this.idArquivoDadosDefesa = idArquivoDadosDefesa;
	}

	/** Retorna o tipo de ordena��o do resultado da busca de bancas.
	 * @return
	 */
	public int getTipoOrdenacao() {
		return tipoOrdenacao;
	}

	/** Seta o tipo de ordena��o do resultado da busca de bancas.
	 * @param tipoOrdenacao
	 */
	public void setTipoOrdenacao(int tipoOrdenacao) {
		this.tipoOrdenacao = tipoOrdenacao;
	}
	
	/** Indica se a ordena��o do resultado da busca � por discente.
	 * @return
	 */
	public boolean isOrdenadoPorDiscente() {
		return tipoOrdenacao == ORDENAR_BANCAS_POR_DISCENTE;
	}
	
	/** Indica se a ordena��o do resultado da busca � por data da banca.
	 * @return
	 */
	public boolean isOrdenadoPorData() {
		return tipoOrdenacao == ORDENAR_BANCAS_POR_DATA;
	}
	
	/** Indica se a ordena��o do resultado da busca � por data da banca.
	 * @return
	 */
	public boolean isOrdenadoPorAnoPeriodoIngresso() {
		return tipoOrdenacao == ORDENAR_BANCAS_ANO_PERIODO_INGRESSO;
	}

	public String getDocente() {
		return docente;
	}

	public void setDocente(String docente) {
		this.docente = docente;
	}

	public String getDiscente() {
		return discente;
	}

	public void setDiscente(String discente) {
		this.discente = discente;
	}

	public boolean isCheckDocente() {
		return checkDocente;
	}

	public void setCheckDocente(boolean checkDocente) {
		this.checkDocente = checkDocente;
	}

	public boolean isCheckDiscente() {
		return checkDiscente;
	}

	public void setCheckDiscente(boolean checkDiscente) {
		this.checkDiscente = checkDiscente;
	}

	public Character getNivelBusca() {
		return nivelBusca;
	}

	public void setNivelBusca(Character nivelBusca) {
		this.nivelBusca = nivelBusca;
	}

	public boolean isAssociacaoComCatalogacao() {
		return associacaoComCatalogacao;
	}

	public void setAssociacaoComCatalogacao(boolean associacaoComCatalogacao) {
		this.associacaoComCatalogacao = associacaoComCatalogacao;
	}
	
	
}