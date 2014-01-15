/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/01/2010
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.jsf.PesquisaMateriaisInformacionaisMBean;
import br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAlteraVariosMateriais;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 *     <p>MBean que gerencia o caso de uso no qual o usuário pode alterar as informações de vários 
 * materiais de uma única vez.<p>
 *	  <p><i>A criação desse caso de uso foi necessária porque algumas vezes a quantidade de materiais 
 * a ser alterada é muito grande o que inviabiliza alterar individualmente. Ocorre principalmente com fascículos.</i></p>.
 * 
 * @author jadson
 * @since 29/01/2010
 * @version 1.0 criacao da classe
 *
 */
@Component("alteraDadosVariosMateriaisMBean")
@Scope("request")
public class AlteraDadosVariosMateriaisMBean extends SigaaAbstractController<MaterialInformacional> implements PesquisarMateriaisInformacionais {

	/**  Página para onde o fluxo retorna ao final da operação */
	public static final String PAGINA_VOLTAR = "/biblioteca/index.jsp";

	/**  Página onde o usuário seleciona qual campo quer alterar */
	public static final String PAGINA_SELECIONA_CAMPOS_DOS_MATERIAIS_ALTERACAO = "/biblioteca/processos_tecnicos/outras_operacoes/paginaSelecionaCampoDosMateriaisAlteracao.jsp";
	
	/**  Página onde o usuário altera os campos dos materiais selecionaos */
	public static final String PAGINA_ALTERA_DADOS_VARIOS_MATERIAIS = "/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp";
	

	
	/* ********************************************************************************************
	 *     Os campos que podem ser alterados nesse caso de uso                                   *
	 * ********************************************************************************************/
	
	/** descrição do campo número de chamada */
	public final static String NUMERO_CHAMADA = "Número de Chamada";
	
	/** descrição do campo segunda localização */
	public final static String SEGUNDA_LOCALIZACAO = "Segunda Localização";
	
	/** descrição do campo colecao */
	public final static String COLECAO = "Coleção";
	
	/** descrição do campo situação */
	public final static String SITUACAO = "Situação";
	
	/** descrição do campo status */
	public final static String STATUS = "Status";
	
	/** descrição do campo tipo material */
	public final static String TIPO_MATERIAL = "Tipo de Material";
	
	/** descrição do campo nota geral */
	public final static String NOTA_GERAL = "Nota Geral";
	
	/** descrição do campo nota usuário */
	public final static String NOTA_USUARIO = "Nota do Usuário";
	
	
	// Campos de exemplares //
	/** descrição do campo nota de tese e dissertação */
	public final static String NOTA_TESE_DISSERTACAO = "Nota de Tese e Dissertação";
	
	/** descrição do campo nota de conteúdo */
	public final static String NOTA_CONTEUDO = "Nota de Conteúdo";
	
	/** descrição do campo número do volume */
	public final static String NUMERO_VOLUME = "Número do Volume";
	
	// Campos de fascículos //
	/** descrição do campo ano cronológico */
	public final static String ANO_CRONOLOGICO = "Ano Cronológio";
	
	/** descrição do campo ano */
	public final static String ANO = "Ano";
	
	/** descrição do campo volume */
	public final static String VOLUME ="Volume";
	
	/** descrição do campo número  */
	public final static String NUMERO = "Número";
	
	/** descrição do campo edição */
	public final static String EDICAO = "Edição";
	
	/** descrição do campo descrição do suplemento */
	public final static String DESCRICAO_SUPLEMENTO = "Descrição do Suplemento";
	
	
	/**********************************************************************************************/
	
	
	
	
	/** Guarda os materiais que vão ser alterados */
	private List<MaterialInformacional> materiaisParaAlteracao = new ArrayList<MaterialInformacional>(); 

	///** Se os dados que vão ser alterados são de exemplares e fascículos */
	//private boolean ambosMateriais = false; 
	///** Se os dados que vão ser alterados são de exemplares ou fascículos */
	//private boolean periodicos = false; 
	
	/** Se deve salvar as alteração e ficar na mesma página ou voltar a tela inicial da biblioteca.*/
	private String finalizarAlteracao;
	
	/** A lista de campos que serão mostrados ao usuário para ele selecionar qual quer alterar */
	private List<String> camposDoMaterial;
	
	/** A lista dos títulos dos materiais selecionados */
	private Set<String> titulosMateriais;
	
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarNumeroChamada;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarSegundaLocalizacao;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarNotaGeral;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarNotaUsuario;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarColecao;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarSituacao;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarStatus;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarTipoMaterial;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarNotaTeseDissertacao;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarNotaConteudo;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarNumeroVolume;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarAnoCronologico;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarAno;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarVolume;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarNumero;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarEdicao;
	/** Indica que o campo descrito pelo nome desta variável será alterado */
	private boolean alterarDescricaoSuplemento;
	
	
	/** 
	 *  Cada posição dessa lista guarda o valor escolhido pelo usuário em um combox na página na 
	 *  mesma posição em que os combobox aparecem na tela e estão posicionados na lista de materiais 
	 *  que vão ser alterados. <br/>
	 *  
	 *  A criação dessa lista foi necessário porque como os combobox são desenhados dentro de um for
	 *  o JSF esta se perdendo e sobre escrevendo os valores. Em campos de texto normal esse problema 
	 *  não ocorre e essa lista não é usada.<br/>
	 *  
	 *  O combobox depende do campo que se está editando e a quantidade depende da quantidade de 
	 *  materiais selecionada pelo usuário.<br/>
	 *  
	 *  Os campos cujos valores são alterados utilizando combobox são: Coleção, Status, Situação, 
	 *  Tipo do Material.<br/>
	 */
	private List<Integer> valoresComboBox = new ArrayList<Integer>();
	
	
	/** Informações do título do material */
	private CacheEntidadesMarc titulo;
	
	
	/**
	 * Inicia a seleção dos materiais que o usuário deseja alterar com uma pesquisa do acervo
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/processos_tecnicos.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarPesquisaAlteracaoDadosDeVariosMateriais(){
		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		return pBean.iniciarBusca(this, "Pesquisar Materiais para Alterar Dados", 
				" <p>Caro usuário,</p> <p>Por favor, busque os materiais os quais se deseja alterar os dados. </p> "
				, "Alterar Dados");
		/*PesquisaTituloCatalograficoMBean beanPesquisa = getMBean("pesquisaTituloCatalograficoMBean");
		return beanPesquisa.iniciarPesquisaSelecionaVariosMateriaisDoTitulo(this, true);*/
	}
	


	
	
	
	/**
	 *   Método chamado depois que o usuário selecionou qual campo deseja alterar.<br/>
	 *   O sistema vai carregar o campo dos materiais selecionados e redirecionar para a página de alteração. <br/>
	 *
	 *   <p>
	 *   Chamado a partir da página: sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaSelecionaCampoDosMateriaisAlteracao.jsp
	 *   </p>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String verificaCampoAlteracaoEscolhido() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
		
		/*if (!ambosMateriais) {
			if(! periodicos ){
				prepareMovimento(SigaaListaComando.ATUALIZA_EXEMPLAR);  
			}else{
				prepareMovimento(SigaaListaComando.ATUALIZA_FASCICULO);  
			}
		}*/
		
		String valorRadioSelecionado = getCurrentRequest().getParameter("campoAlteracao");
		
		zeraCamposSelecionados();
		
		
		if(valorRadioSelecionado.equalsIgnoreCase(NUMERO_CHAMADA))
			alterarNumeroChamada = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(SEGUNDA_LOCALIZACAO))
			alterarSegundaLocalizacao = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(COLECAO))
			alterarColecao = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(SITUACAO))
			alterarSituacao = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(STATUS))
			alterarStatus = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(TIPO_MATERIAL))
			alterarTipoMaterial = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(NOTA_GERAL))
			alterarNotaGeral = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(NOTA_USUARIO))
			alterarNotaUsuario = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(ANO_CRONOLOGICO))
			alterarAnoCronologico = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(ANO))
			alterarAno = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(VOLUME))
			alterarVolume = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(NUMERO))
			alterarNumero = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(EDICAO))
			alterarEdicao = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(DESCRICAO_SUPLEMENTO))
			alterarDescricaoSuplemento = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(NOTA_TESE_DISSERTACAO))
			alterarNotaTeseDissertacao = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(NOTA_CONTEUDO))
			alterarNotaConteudo = true;
		
		if(valorRadioSelecionado.equalsIgnoreCase(NUMERO_VOLUME))
			alterarNumeroVolume = true;
		
		
		
		/////  Atribui os valores iniciais da lista de valores  dos combobox  ///
		
		if(alterarColecao || alterarStatus || alterarSituacao || alterarTipoMaterial)
			valoresComboBox = new ArrayList<Integer>();
		
		if(alterarColecao){
			for (int qtdMaterial = 0; qtdMaterial < materiaisParaAlteracao.size(); qtdMaterial++) {
				valoresComboBox.add(new Integer( materiaisParaAlteracao.get(qtdMaterial).getColecao().getId() ));
			}
		}
		
		if(alterarStatus){
			for (int qtdMaterial = 0; qtdMaterial < materiaisParaAlteracao.size(); qtdMaterial++) {
				valoresComboBox.add(new Integer( materiaisParaAlteracao.get(qtdMaterial).getStatus().getId() ));
			}
		}
		
		if(alterarSituacao){
			for (int qtdMaterial = 0; qtdMaterial < materiaisParaAlteracao.size(); qtdMaterial++) {
				valoresComboBox.add(new Integer( materiaisParaAlteracao.get(qtdMaterial).getSituacao().getId() ));
			}
		}
		
		if(alterarTipoMaterial){
			for (int qtdMaterial = 0; qtdMaterial < materiaisParaAlteracao.size(); qtdMaterial++) {
				valoresComboBox.add(new Integer( materiaisParaAlteracao.get(qtdMaterial).getTipoMaterial().getId() ));
			}
		}
		
		return telaPaginaAlteraDadosVariosMateriais();
	}
	
	
	
	
	/**
	 *   Método que copia o valor digitado no campo selecionado para os demais abaixo que vêem 
	 *   depois dele na lista. <br/> 
	 *
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp
	 * @throws DAOException 
	 *
	 */
	public void copiaValorCamposAbaixo(ActionEvent evt) throws DAOException {
			
			int idMaterialOrigemDado = getParameterInt("idMaterialOrigemDado");
			
			boolean encontrouMaterial = false;
			MaterialInformacional materialSelecionado = null;
			
			int indexSelecionado = -1;
			
			SituacaoMaterialInformacional situacaoEmprestado = getGenericDAO().findByExactField(
					SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
			
			for (int ptr = 0; ptr < materiaisParaAlteracao.size();  ptr ++) {
				
				if(encontrouMaterial == false){  // enquando não encontra o material na lista, não faz nada
					if(materiaisParaAlteracao.get(ptr).getId() == idMaterialOrigemDado){
						encontrouMaterial = true;
						materialSelecionado = materiaisParaAlteracao.get(ptr);
					}
					
					indexSelecionado ++;
					
				}else{  // quando encontrou
					
					if( alterarNumeroChamada && materialSelecionado != null) {
						materiaisParaAlteracao.get(ptr).setNumeroChamada( materialSelecionado.getNumeroChamada());
					}
					if( alterarSegundaLocalizacao && materialSelecionado != null) {
						materiaisParaAlteracao.get(ptr).setSegundaLocalizacao( materialSelecionado.getSegundaLocalizacao() );
					}
					if( alterarNotaGeral && materialSelecionado != null) {
						materiaisParaAlteracao.get(ptr).setNotaGeral( materialSelecionado.getNotaGeral());
					}
					if( alterarNotaUsuario && materialSelecionado != null) {
						materiaisParaAlteracao.get(ptr).setNotaUsuario( materialSelecionado.getNotaUsuario());
					}
					
					
					if( (alterarTipoMaterial || alterarStatus || alterarSituacao || alterarColecao) && materialSelecionado != null) {
						
						if(alterarSituacao){
							
							
							/* Se a situação do material selecionado não for emprestado e a situação do material *
							 * para onde o valor vai ser copiado também não for emprestado, pode copiar o valor  */  
							if( ! valoresComboBox.get( indexSelecionado ).equals( situacaoEmprestado.getId() )){
							
								if( ! valoresComboBox.get(ptr).equals(  situacaoEmprestado.getId() )){
									valoresComboBox.set( ptr, valoresComboBox.get( indexSelecionado ));	
								}
							}
						}else{
							valoresComboBox.set( ptr, valoresComboBox.get( indexSelecionado ));	
						}
						
						
					}
					
					
					if(materialSelecionado != null && materialSelecionado instanceof Exemplar && materiaisParaAlteracao.get(ptr) instanceof Exemplar){
					
						Exemplar temp = (Exemplar) materiaisParaAlteracao.get(ptr);
						Exemplar tempSelecionado = (Exemplar) materialSelecionado;
						
						if( alterarNotaTeseDissertacao ) {
							temp.setNotaTeseDissertacao( tempSelecionado.getNotaTeseDissertacao());
						}
						if( alterarNotaConteudo) {
							temp.setNotaConteudo( tempSelecionado.getNotaConteudo());
						}
						if( alterarNumeroVolume) {
							temp.setNumeroVolume( tempSelecionado.getNumeroVolume());
						}
					}
					
					if(materialSelecionado != null && materialSelecionado instanceof Fasciculo && materiaisParaAlteracao.get(ptr) instanceof Fasciculo){
						
						Fasciculo temp = (Fasciculo) materiaisParaAlteracao.get(ptr);
						Fasciculo tempSelecionado = (Fasciculo) materialSelecionado;
						
						if( alterarAnoCronologico) {
							temp.setAnoCronologico( tempSelecionado.getAnoCronologico());
						}
						if( alterarAno ) {
							temp.setAno( tempSelecionado.getAno());
						}
						if( alterarVolume ) {
							temp.setVolume( tempSelecionado.getVolume());
						}
						if( alterarNumero ) {
							temp.setNumero( tempSelecionado.getNumero());
						}
						if( alterarEdicao ) {
							temp.setEdicao( tempSelecionado.getEdicao());
						}
						if( alterarDescricaoSuplemento ) {
							temp.setDescricaoSuplemento( tempSelecionado.getDescricaoSuplemento());
						}
					}
	
				}
				
			}
		
	}
	
	
	/////////////////////////////// Métodos da interface de busca ///////////////////////////////////

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 * 
	 *  Método não chamado por nenhuma JSP.
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais#selecionouMateriaisPesquisaPadraoMateriais()
	 */
	@Override
	public String selecionouMateriaisPesquisaPadraoMateriais()
			throws ArqException {
		prepareMovimento(SigaaListaComando.ALTERA_DADOS_VARIOS_MATERIAIS);

		boolean ambosMateriais = false;
		boolean temExemplar = false;
		boolean temFasciculo = false;
		
		valoresComboBox = new ArrayList<Integer>();		
		titulosMateriais = new HashSet<String>();
		
		for (int qtdMaterial = 0; qtdMaterial < materiaisParaAlteracao.size(); qtdMaterial++) {
			valoresComboBox.add(new Integer(-1));
		}
		
		for (MaterialInformacional mi : materiaisParaAlteracao) {
			titulosMateriais.add(BibliotecaUtil.obtemDadosResumidosTituloDoMaterial(mi.getId()));

			temExemplar |= mi.isExemplar();
			temFasciculo |= !mi.isExemplar();
		}
		
		ambosMateriais = temExemplar && temFasciculo;
		
		/*if(materiaisParaAlteracao.size() > 0){
			this.titulo =  BibliotecaUtil.obtemDadosTituloCache( 
					getDAO(MaterialInformacionalDao.class).findIdTituloMaterial(materiaisParaAlteracao.get(0).getId() ) );
			
			// Pode retornar nulo para títulos não catalogados
			FormatoMaterial formatoMaterial = getDAO(FormatoMaterialDao.class).findFormatoMaterialDoTitulo(titulo.getIdTituloCatalografico()); 
			idTituloDosMateriais = titulo.getIdTituloCatalografico();
			
			// porém esses título nunca deveriam possuir materiais, então teoricamente o NullPointerException não deveria ocorrer aqui. Teste apenas para garantir
			if(formatoMaterial != null) 
				periodicos = formatoMaterial.isFormatoPeriodico();
		}*/
	
		
		camposDoMaterial = new ArrayList<String>();
		
		camposDoMaterial.add(NUMERO_CHAMADA);
		camposDoMaterial.add(SEGUNDA_LOCALIZACAO);
		camposDoMaterial.add(COLECAO);
		camposDoMaterial.add(SITUACAO);
		camposDoMaterial.add(STATUS);
		camposDoMaterial.add(TIPO_MATERIAL);
		camposDoMaterial.add(NOTA_GERAL);
		camposDoMaterial.add(NOTA_USUARIO);
		
		if (!ambosMateriais) {
			if(temFasciculo){
				camposDoMaterial.add(ANO_CRONOLOGICO);
				camposDoMaterial.add(ANO);
				camposDoMaterial.add(VOLUME);
				camposDoMaterial.add(NUMERO);
				camposDoMaterial.add(EDICAO);
				camposDoMaterial.add(DESCRICAO_SUPLEMENTO);
			}else{
				camposDoMaterial.add(NOTA_TESE_DISSERTACAO );
				camposDoMaterial.add(NOTA_CONTEUDO);
				camposDoMaterial.add(NUMERO_VOLUME);
			}
		}
		else {
			addMensagemWarning("A lista contém exemplares e periódicos, portanto apenas os campos comum a ambos poderão ser alterados.");
		}
		
		return telaPaginaSelecionaCamposDosMateriaisAlteracao();
	}

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais#setMateriaisPesquisaPadraoMateriais()
	 */
	@Override
	public void setMateriaisPesquisaPadraoMateriais(
			List<MaterialInformacional> materiais) throws ArqException {
		this.materiaisParaAlteracao = materiais;
	}

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa/biblioteca/pesquisaPadraoMaterialInformacional.jsp</li>
	 * </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais#voltarBuscaPesquisaPadraoMateriais()
	 */
	@Override
	public String voltarBuscaPesquisaPadraoMateriais() throws ArqException {
		return forward(PAGINA_VOLTAR);
	}
	
	/**
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaSelecionaCampoDosMateriaisAlteracao.jsp
	 * 
	 */
	public String voltarTelaSelecionaMateriais() throws ArqException {
		PesquisaMateriaisInformacionaisMBean bean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		bean.setMbeanChamador(this);
		return bean.telaPesquisaPadraoMateriaisInformacionais();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 *    Realiza a ação de atualizar os materiais que foram selecionados pelo usuário.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String realizarAlteracaoMateriais() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
		
		GenericDAO dao = null;
		
		try{
		
			dao = getGenericDAO();
			
			/* **************************************************************************************
			 * Atribui o valores escolhidos pelo usuário que estavam em uma lista separada, novamente os materiais 
			 * Somento utilizado para os campos que não são de texto
			 */
			
			for (int ptr = 0 ; ptr < valoresComboBox.size(); ptr++) {
				if(alterarColecao){
					materiaisParaAlteracao.get(ptr).setColecao( new Colecao( valoresComboBox.get(ptr)));
				}
				
				if(alterarStatus){
					materiaisParaAlteracao.get(ptr).setStatus( new StatusMaterialInformacional( valoresComboBox.get(ptr)));
				}
				
				if(alterarSituacao){
					
					// Importante: No caso da alteração da sitação é imprescindível mantar as informações se ela   //
					// é uma situação de baixa ou empréstado, porque não pode deixa o usuário alterar              //
					
					SituacaoMaterialInformacional  novaSituacao = new SituacaoMaterialInformacional
							( valoresComboBox.get(ptr)
									, materiaisParaAlteracao.get(ptr).getSituacao().getDescricao()
									, materiaisParaAlteracao.get(ptr).getSituacao().isSituacaoDisponivel()
									, materiaisParaAlteracao.get(ptr).getSituacao().isSituacaoEmprestado()
									, materiaisParaAlteracao.get(ptr).getSituacao().isSituacaoDeBaixa());
					materiaisParaAlteracao.get(ptr).setSituacao( novaSituacao );
				}
				
				if(alterarTipoMaterial){
					materiaisParaAlteracao.get(ptr).setTipoMaterial( new TipoMaterial( valoresComboBox.get(ptr)));
				}	
			}
	
		
		
			try{
				
				MovimentoAlteraVariosMateriais mov = new MovimentoAlteraVariosMateriais(materiaisParaAlteracao);
				mov.setAlterarAno(alterarAno);
				mov.setAlterarAnoCronologico(alterarAnoCronologico);
				mov.setAlterarDescricaoSuplemento(alterarDescricaoSuplemento);
				mov.setAlterarColecao(alterarColecao);
				mov.setAlterarEdicao(alterarEdicao);
				mov.setAlterarNotaConteudo(alterarNotaConteudo);
				mov.setAlterarNotaGeral(alterarNotaGeral);
				mov.setAlterarNotaTeseDissertacao(alterarNotaTeseDissertacao);
				mov.setAlterarNotaUsuario(alterarNotaUsuario);
				mov.setAlterarNumero(alterarNumero);
				mov.setAlterarNumeroChamada(alterarNumeroChamada);
				mov.setAlterarNumeroVolume(alterarNumeroVolume);
				mov.setAlterarSegundaLocalizacao(alterarSegundaLocalizacao);
				mov.setAlterarSituacao(alterarSituacao);
				mov.setAlterarStatus(alterarStatus);
				mov.setAlterarTipoMaterial(alterarTipoMaterial);
				mov.setAlterarVolume(alterarVolume);
				mov.setCodMovimento(SigaaListaComando.ALTERA_DADOS_VARIOS_MATERIAIS);
				execute(mov);
					
				addMensagemInformation("Alteração realizada com sucesso.");
				
			}catch (NegocioException ne) {
				addMensagens(ne.getListaMensagens());
				ne.printStackTrace();
				return telaPaginaAlteraDadosVariosMateriais(); // continua na página
			}
			
			
			if("true".equalsIgnoreCase(finalizarAlteracao))
				return cancelar();  // volta tela inicial da biblioteca
			else{
				prepareMovimento(SigaaListaComando.ALTERA_DADOS_VARIOS_MATERIAIS);
				return telaPaginaAlteraDadosVariosMateriais(); // continua na página
			}
		
		
		}finally{
			if(dao != null ) dao.close();
		}
		
	}
	
	
	
	
	/**
	 *   Zera os campos que por acaso a usuário tenha selecionado. 
	 *
	 */
	private void zeraCamposSelecionados(){
		alterarNumeroChamada = false;
		alterarSegundaLocalizacao = false;
		alterarNotaGeral = false;
		alterarNotaUsuario = false;
		alterarColecao = false;
		alterarSituacao = false;
		alterarStatus = false;
		alterarTipoMaterial = false;
		alterarNotaTeseDissertacao = false;
		alterarNotaConteudo = false;
		alterarNumeroVolume = false;
		alterarAnoCronologico = false;
		alterarAno = false;
		alterarVolume = false;
		alterarNumero = false;
		alterarEdicao = false;
		alterarDescricaoSuplemento = false;
	}
	
	
	
	
	/**
	 * Retorna todos os status do material dependendo da coleção escolhida
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getStatusAtivos() throws DAOException{
		
		Collection <StatusMaterialInformacional> si 
			= getGenericDAO().findByExactField(StatusMaterialInformacional.class, "ativo", true);
		
		return toSelectItems(si, "id", "descricao");
	}

	
	/**
	 * Retorna todos as situações que o usuário pode atribuir a um material
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getSituacoes() throws DAOException{
		Collection <SituacaoMaterialInformacional> si = getDAO(SituacaoMaterialInformacionalDao.class)
									.findSituacoesUsuarioPodeAtribuirMaterial();
		return toSelectItems(si, "id", "descricao");
	}
	
	

	/**
	 *    Retorna o id da situação emprestado para gerar um combobox na página apenas com esta situação.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public Integer getIdSituacaoEmprestado() throws DAOException{
		SituacaoMaterialInformacional situacaoEmprestado = getGenericDAO().findByExactField(
				SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
		return situacaoEmprestado.getId();
	}
	
	
	/**
	 * Retorna todas as coleções cadastradas
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getColecoes() throws DAOException{
		Collection <Colecao> c = getGenericDAO().findByExactField(Colecao.class, "ativo", true);
		return toSelectItems(c, "id", "descricaoCompleta");
	}
	
	/**
	 * Retorna os tipos de materiais
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getTiposMaterial() throws DAOException{
		Collection <TipoMaterial> t = getGenericDAO().findByExactField(TipoMaterial.class, "ativo", true);
		return toSelectItems(t, "id", "descricao");
	}
	
	
	
	///////////// Telas de Navegação ////////////////
	
	/**
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp
	 */
	public String telaPaginaAlteraDadosVariosMateriais(){
		return forward(PAGINA_ALTERA_DADOS_VARIOS_MATERIAIS);
	}
	
	
	/**
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlteraDadosVariosMateriais.jsp
	 *
	 * @return
	 */
	public String telaPaginaSelecionaCamposDosMateriaisAlteracao(){
		return forward(PAGINA_SELECIONA_CAMPOS_DOS_MATERIAIS_ALTERACAO);
	}
	
	////////////////////////////////////////////////
	
	
	
	
	///// Sets e Gets /////
	
	public List<MaterialInformacional> getMateriaisParaAlteracao() {
		return materiaisParaAlteracao;
	}

	public void setMateriaisParaAlteracao(List<MaterialInformacional> materiaisParaAlteracao) {
		this.materiaisParaAlteracao = materiaisParaAlteracao;
	}

	public List<String> getCamposDoMaterial() {
		return camposDoMaterial;
	}
	
	public void setCamposDoMaterial(List<String> camposDoMaterial) {
		this.camposDoMaterial = camposDoMaterial;
	}
	
	public boolean isAlterarNumeroChamada() {
		return alterarNumeroChamada;
	}

	public void setAlterarNumeroChamada(boolean alterarNumeroChamada) {
		this.alterarNumeroChamada = alterarNumeroChamada;
	}
	
	public boolean isAlterarSegundaLocalizacao() {
		return alterarSegundaLocalizacao;
	}

	public void setAlterarSegundaLocalizacao(boolean alterarSegundaLocalizacao) {
		this.alterarSegundaLocalizacao = alterarSegundaLocalizacao;
	}

	public boolean isAlterarNotaGeral() {
		return alterarNotaGeral;
	}
	
	public void setAlterarNotaGeral(boolean alterarNotaGeral) {
		this.alterarNotaGeral = alterarNotaGeral;
	}

	public boolean isAlterarNotaUsuario() {
		return alterarNotaUsuario;
	}

	public void setAlterarNotaUsuario(boolean alterarNotaUsuario) {
		this.alterarNotaUsuario = alterarNotaUsuario;
	}

	public boolean isAlterarColecao() {
		return alterarColecao;
	}

	public void setAlterarColecao(boolean alterarColecao) {
		this.alterarColecao = alterarColecao;
	}

	public boolean isAlterarSituacao() {
		return alterarSituacao;
	}

	public void setAlterarSituacao(boolean alterarSituacao) {
		this.alterarSituacao = alterarSituacao;
	}

	public boolean isAlterarStatus() {
		return alterarStatus;
	}

	public void setAlterarStatus(boolean alterarStatus) {
		this.alterarStatus = alterarStatus;
	}

	public boolean isAlterarTipoMaterial() {
		return alterarTipoMaterial;
	}

	public void setAlterarTipoMaterial(boolean alterarTipoMaterial) {
		this.alterarTipoMaterial = alterarTipoMaterial;
	}

	public boolean isAlterarNotaTeseDissertacao() {
		return alterarNotaTeseDissertacao;
	}

	public void setAlterarNotaTeseDissertacao(boolean alterarNotaTeseDissertacao) {
		this.alterarNotaTeseDissertacao = alterarNotaTeseDissertacao;
	}

	public boolean isAlterarNotaConteudo() {
		return alterarNotaConteudo;
	}

	public void setAlterarNotaConteudo(boolean alterarNotaConteudo) {
		this.alterarNotaConteudo = alterarNotaConteudo;
	}

	public boolean isAlterarNumeroVolume() {
		return alterarNumeroVolume;
	}

	public void setAlterarNumeroVolume(boolean alterarNumeroVolume) {
		this.alterarNumeroVolume = alterarNumeroVolume;
	}

	public boolean isAlterarAnoCronologico() {
		return alterarAnoCronologico;
	}

	public void setAlterarAnoCronologico(boolean alterarAnoCronologico) {
		this.alterarAnoCronologico = alterarAnoCronologico;
	}
	
	public boolean isAlterarAno() {
		return alterarAno;
	}

	public void setAlterarAno(boolean alterarAno) {
		this.alterarAno = alterarAno;
	}
	
	public boolean isAlterarVolume() {
		return alterarVolume;
	}

	public void setAlterarVolume(boolean alterarVolume) {
		this.alterarVolume = alterarVolume;
	}

	public boolean isAlterarNumero() {
		return alterarNumero;
	}

	public void setAlterarNumero(boolean alterarNumero) {
		this.alterarNumero = alterarNumero;
	}

	public boolean isAlterarEdicao() {
		return alterarEdicao;
	}

	public void setAlterarEdicao(boolean alterarEdicao) {
		this.alterarEdicao = alterarEdicao;
	}

	public boolean isAlterarDescricaoSuplemento() {
		return alterarDescricaoSuplemento;
	}

	public void setAlterarDescricaoSuplemento(boolean alterarDescricaoSuplemento) {
		this.alterarDescricaoSuplemento = alterarDescricaoSuplemento;
	}

	public String getFinalizarAlteracao() {
		return finalizarAlteracao;
	}

	public void setFinalizarAlteracao(String finalizarAlteracao) {
		this.finalizarAlteracao = finalizarAlteracao;
	}

	public List<Integer> getValoresComboBox() {
		return valoresComboBox;
	}

	public void setValoresComboBox(List<Integer> valoresComboBox) {
		this.valoresComboBox = valoresComboBox;
	}

	public CacheEntidadesMarc getTitulo() {
		return titulo;
	}

	public void setTitulo(CacheEntidadesMarc titulo) {
		this.titulo = titulo;
	}

	public Set<String> getTitulosMateriais() {
		return titulosMateriais;
	}
	
	
}
