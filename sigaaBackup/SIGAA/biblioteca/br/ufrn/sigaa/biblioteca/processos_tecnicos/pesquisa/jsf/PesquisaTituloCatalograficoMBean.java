/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 21/08/2008
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.integracao.dto.biblioteca.InformacoesTombamentoMateriaisDTO;
import br.ufrn.sigaa.arq.dao.biblioteca.ArtigoDePeriodicoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.CatalogacaoMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.ClassificacaoBibliograficaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.MaterialInformacionalMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.TransfereExemplaresEntreTitulosMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoPesquisaAvancada;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoPesquisaAvancada.TipoCampoBuscaAvancada;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoPesquisaPorListas;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.GeraPesquisaTextualFactory;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;
import br.ufrn.sigaa.biblioteca.util.PaginacaoBibliotecaUtil;

/**
 *    <p>MBean que gerencia a página de pesquisa de Títulos no acervo da biblioteca.</p> 
 *    <p>Essa é a pesquisa principal de todo o sistema de bibliotecas, utilizadas pelos bibliotecários e funcionários da biblioteca.</p>
 *
 *    <p> Esse MBean é chamado por diversos casos de uso do sistema, sempre é existe a necessidade de realizar uma busca no acervo </p>
 *
 * @author Jadson
 * @since 21/08/2008
 * @version 1.0 Criação da classe
 * 
 * @version 1.5 13/10/2010 Alterando a lógica de criar novos casos de usos que utilizam a pesquisa para diminuir a quantiade testes nas páginas. 
 * A maneira correta de utilizar essa busca seria igual a classe {@link TransfereExemplaresEntreTitulosMBean}.
 * 
 * @version 1.7 07/11/2011 Adicionando a possibilizada de ordenação e páginação dos resultados da consulta do acervo. 
 *                         Reorganizando os icones das operações em um context menu para aproveitar melhor o espaço da página. 
 * 
 * @see {@link PesquisarAcervoBiblioteca}
 * @see {@link PesquisarAcervoMateriaisInformacionais}
 *
 */
@Component(value="pesquisaTituloCatalograficoMBean")
@Scope(value="request")
public class PesquisaTituloCatalograficoMBean extends PesquisarAcervoPaginadoBiblioteca {
	
	/**
	 * A página de pesquisa de Títulos no acervo.
	 * 
	 * Inclui outras página que possuem formulários diferentes para realizar os vários tipos de busca existentes no acervo.
	 * 
	 * <ul>
	 * 	 <li>sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultaCampoTituloCatalografico.jsp</li>
	 *   <li>sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *   <li>sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 * </ul>
	 * 
	 * A listagem de resultados é igual independente do tipo de busca.
	 */
	public static final String PAGINA_PESQUISA_TITULO = "/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaTituloCatalografico.jsp";
	
	/** Mostra o resultado da pesquisa de títulos em formatato de relatório (Para facilitar impressão) */
	public static final String PAGINA_RESULTADOS_PESQUISA_TITULO_RELATORIO = "/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaTitulosCatalograficosRelatorio.jsp";
	
	/**
	 *  usado para gerar consultas nativas do banco
	 */
	private GeraPesquisaTextual geradorPesquisa;
	
	
	
	/////////////////////       DADOS DA BUSCA MULTI CAMPO       /////////////////////
	
	/** Campos da busca multi campo   */
	private String titulo, assunto, autor, localPublicacao, editora, classificacao1, classificacao2, classificacao3;
	/** Campos da busca multi campo   */
	private Integer anoInicial, anoFinal;
	/** Campos da busca multi campo   */
	private Integer numeroDoSistema;
	
	
	/** Indica quais os campos deve ser buscar */
	private boolean buscarTitulo, buscarAssunto, buscarAutor, buscarLocalPublicacao, buscarEditora, buscarClassificacao1, buscarClassificacao2, buscarClassificacao3
				, buscarAno, buscarNumeroSistema, buscarBiblioteca, buscarColecao, buscarTipoMaterial, buscarStatus;

	
	/**  Traz os Títulos que possuem materiais em uma biblioteca específica  */
	private Integer idBiblioteca;     
	/** Traz os Títulos que possuem materiais em uma coleção específica  */
	private Integer idColecao;    
	/** Traz os Títulos que possuem materiais com um tipo específico */
	private Integer idTipoMaterial;     
	/** Traz os Títulos que possuem materiais com um satus específico */
	private Integer idStatus; 
	

	///////////////////////////////////////////////////////////////////////////////////

	

	
	
	
	
	
    ////////////////////////       DADOS DA BUSCA AVANÇADA    ////////////////////////
	
	/** O objeto que guarda os campos de busca avançada */
	private List<CampoPesquisaAvancada> campos = new ArrayList<CampoPesquisaAvancada>();
	
	
	///////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	////////////////////////DADOS DA BUSCA SIMPLES    ////////////////////////
	
	/** Os campos de busca simples */
	protected CampoPesquisaAvancada campoBuscaSimples;
	
	///////////////////////////////////////////////////////////////////////////
	
	
	//////////////////////////     DADOS DA BUSCA POR LISTAS //////////////////////////
	
	/** O objeto que guarda os  campos de busca por lista */
	CampoPesquisaPorListas campoPesquisaPorLista = new CampoPesquisaPorListas();
	
	///////////////////////////////////////////////////////////////////////////////////
	
	
	
	

	/**  Armazena os artigos resultantes das consultas */
	private List<CacheEntidadesMarc> artigos = new ArrayList<CacheEntidadesMarc>();
	
	/** Guarda os dados das bibliotecas do sistema para não ficar buscando toda vida que renderizar a página */
	private Collection<Biblioteca> bibliotecasAtivas;
	
	/** Guarda os dados das coleções do sistema para não ficar buscando toda vida que renderizar a página */
	private Collection<Colecao> colecoes;
	
	/** Guarda os dados dos tipos de materiais do sistema para não ficar buscando toda vida que renderizar a página */
	private Collection<TipoMaterial> tiposMateriais;
	
	/** Guarda os dados dos tipos de materiais do sistema para não ficar buscando toda vida que renderizar a página */
	private Collection<StatusMaterialInformacional> status;
	
	
	/** Atributo que precisa ser configurado caso se esteja utilizando a pesquisa com fins de realizar uma nova catalogação. 
	 * (opção de catalogar um novo título estarão habilitadas)
	 */
	public boolean pesquisaTituloParaCatalogacao = false;
	
	/** Atributo que precisa ser configurado caso se esteja utilizando a pesquisa a partir das informações dos livros cadastros no sipac.*/
	public boolean pesquisaTituloParaCatalogacaoComTombamento = false;
	
	/** Para o caso de uso de catalogação sem adição de materiais. */
	public boolean pesquisaTituloApenasCatalogacao = false;
	
	///////////// Constantes que indicam as operações que serão executadas na tela (Não criar novas constantes)////////////
	
	/**  A pesquisa mais básica sem nenhuma operações especial */
	public static final int PESQUISA_NORMAL = 0;
	
	/**
	 * Operação utilizada qual se deseja selecionar vários materiais de um mesmo Título para usar em outro caso de uso
	 * #see {@link TransfereExemplaresEntreTitulosMBean}.
	 */
	public static final int PESQUISA_SELECIONA_VARIOS_MATERIAIS = 1; 
	
	/**
	 * Operação utilizada qual se deseja selecionar um Título para utilizar em outro caso de uso.
	 * @see {@link TransfereExemplaresEntreTitulosMBean}.
	 */
	public static final int PESQUISA_SELECIONA_UM_TITULO = 2; 
	
	
	/** Define a opção que está sendo utilizada no momento*/
	private int operacao = -1;
	
	
	/**
	 *  Campos usado quando a tela de busca é chamada a partir da tela de busca de informações do SIPAC
	 * serve para pegar os dados vindo do livro e já realizar um busca prévia para o usuário não ter que 
	 * digitar tudo novamente.
	 */
	private String tituloSipac, autorSipac;
	
	
	/**
	 *  Indica se o usuário realizou a busca multi campo, no caso de voltar para a tela de busca 
	 * e ter que recarregar os dados da busca.
	 */
	private boolean realizouPesquisaMultiCampo = true;
	
	/**
	 * Guarda a aba que o usuário está utilizado para realizar a pesquisa
	 */
	private String valorAbaPesquisa = "";
	
	
	/** Indica quando o usuário selecionou a opção de visualizar os resultados em formato de relatório. */
	private boolean exitirDadosFormatoRelatorio = false;
	
	
	/** Indica se o bibliotecário quer utilizar a busca remissiva da base de autoridades */
	private boolean utilizarBuscaRemissiva = false;
	
	/**
	 * MBean que chamou a pesquisa no acervo para selecionar vários materiais
	 */
	private PesquisarAcervoMateriaisInformacionais mbeanChamadorSelecionaMaterais;
	
	
	/**
	 * MBean que chamou a pesquisa no acervo para selecionar um Título
	 */
	private PesquisarAcervoBiblioteca mbeanChamadoPesquisaTitulo;
	
	
	
	
	/**
	 * Construtor default
	 */
	public PesquisaTituloCatalograficoMBean(){ 
		
		geradorPesquisa = new GeraPesquisaTextualFactory().getGeradorPesquisaTextual();
		
		valorCampoOrdenacao = CampoOrdenacaoConsultaAcervo.TITULO.getValor();
		
		// A busca avançada sempre começa com três campos
		campos.add(new CampoPesquisaAvancada(campos.size(), false));
		campos.add(new CampoPesquisaAvancada(campos.size(), false));
		campos.add(new CampoPesquisaAvancada(campos.size(), false));
		
		campoBuscaSimples = new CampoPesquisaAvancada(0, "", CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.TODOS_OS_CAMPOS);
	}
	
	
	
	
	/**
	 *  <p>Inicia a pesquisa normal do acervo</p>
	 * 
	 *  <p> Deve passar a informação se é pesquisa é para catalogação ou não.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/processos_tecnicos.jsp</li>
	 *    <li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li> 
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/buscaInformacoesSipacAPartirNumeroPatrimonio.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws DAOException
	 */
	public String iniciarPesquisa() throws DAOException{
		limparDadosPesquisaMultiCampo();
		limparDadosPesquisaAvancada();
		
		/* ******** Configurações do tipo de pesquisa que se está realizando ******** */
		
		pesquisaTituloParaCatalogacao =  getParameterBoolean("isPesquisaTituloParaCatalogacao") ;
		pesquisaTituloParaCatalogacaoComTombamento = getParameterBoolean("isPesquisaTituloParaCatalogacaoComTombamento");
		pesquisaTituloApenasCatalogacao = getParameterBoolean("isPesquisaTituloApenasCatalogacao");
		
		
		operacao = PESQUISA_NORMAL;
		
		if(pesquisaTituloParaCatalogacaoComTombamento) { // faz uma pesquisa prévia no sistema com os dados vindos no sipac
			
			if(StringUtils.notEmpty(tituloSipac)){
				this.titulo = tituloSipac;
				buscarTitulo = true;
			}
			
			if(StringUtils.notEmpty(autorSipac)){
				this.autor = autorSipac;
				buscarAutor = true;
			}
			
			return pesquisaMultiCampo();
		}
		
		return telaPesquisaTitulo();
	}
	
	
	/**
	 *     Inicia a pesquisa para escolher um  Título do acervo
	 * 
	 * <br/><br/>   
	 * Chamado a partir de {@link TransfereExemplaresEntreTitulosMBean#preparaTransferirExemplaresSelecionadosEntreTitulos()}   
	 * Método não invocado por nenhuma jsp.
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarPesquisaSelecionarTitulo(PesquisarAcervoBiblioteca mbeanChamadoPesquisaTitulo) throws SegurancaException{
	
		this.mbeanChamadoPesquisaTitulo = mbeanChamadoPesquisaTitulo;
		
		operacao = PESQUISA_SELECIONA_UM_TITULO;
		
		limparDadosPesquisaMultiCampo();
		limparDadosPesquisaAvancada();
		
		return telaPesquisaTitulo();
		
	}
	
	
	/**
	 *  <p>Inicia o caso de uso genérico de pesquisa para selecionar vários materias no acervo a partir 
	 *  da busca de Títulos.</p> 
	 * <p> O que vai ser feito com esses materiais selecionados vai depender do Mbean que está chamando 
	 * essa pesquisa. </p>
	 * 
	 * 
	 * <br/><br/>
	 * Método não chamado por nenhuma página jsp.
	 * <br/><br/>
	 * 
	 * @see {@link PesquisarAcervoMateriaisInformacionais}
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarPesquisaSelecionaVariosMateriaisDoTitulo(PesquisarAcervoMateriaisInformacionais mBeanChamador, boolean apagaDadosBusca){
		
		if(apagaDadosBusca){
			limparDadosPesquisaMultiCampo();
			limparDadosPesquisaAvancada();
		}
		
		operacao = PESQUISA_SELECIONA_VARIOS_MATERIAIS;
		
		this.mbeanChamadorSelecionaMaterais = mBeanChamador;   // MBean para cujos materiais selecionados devem retornar
		
		return telaPesquisaTitulo();
	}
	
	
	
	/**
	 *    <p>Retorna o fluxo do caso de uso para o mBean que chamou a busca no acervo </p>
	 *  
	 *    <p> O Mbean que deseja realizar essa operação tem que implementar {@link PesquisarAcervoBiblioteca} </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaAcervo.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String selecionouTitulo() throws ArqException{
		
		if(operacao == PESQUISA_SELECIONA_UM_TITULO){
		
			int idTitulo  = getParameterInt("idTitulo");
			CacheEntidadesMarc titulo = new CacheEntidadesMarc();
			titulo.setIdTituloCatalografico(idTitulo);
			mbeanChamadoPesquisaTitulo.setTitulo(titulo);
			
			return mbeanChamadoPesquisaTitulo.selecionaTitulo();
		}else{
			if(operacao == PESQUISA_SELECIONA_VARIOS_MATERIAIS){
				DetalhesMateriaisDeUmTituloMBean bean = getMBean("detalhesMateriaisDeUmTituloMBean");
				bean.setMbeanChamador(mbeanChamadorSelecionaMaterais);
				return bean.visualizarMateriaisTitulo();
			}
		}
		
		return null; // Não era para chegar aqui, porque só existem duas opções de busca que seleciona o Título
	}
	
	
	/**
	 *  <p> Realiza a função do botão voltar quando a busca é chamada por outro caso de uso do sistema. </p>
	 *  <p> <i> ( implementação desse método deve ser realizada por Mbean que chamar esse caso de uso, pois só ele sabe para onde deve voltar ) </i> </p>
	 *  
	 *   <br/>
	 *    Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *   </ul>
	 */
	public String voltarBusca() throws ArqException {
		return mbeanChamadoPesquisaTitulo.voltarBuscaAcervo();
	}
	
	
	
	
	
	
	
	
	/**
	 *   Altera a forma do usuário digitar os dados na busca avançada para o campo 1 quando o usuário escolhe 
	 *   biblioteca, coleção ou tipo material.
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp
	 *
	 * @param evt
	 * @throws ArqException 
	 */
	public void verificaExibicaoTipoCampo(ValueChangeEvent evt) throws ArqException{
		
		confituraAbasPesquisa("buscaAvancada"); // esse método só é usado na busca avançada
		
		Integer valorEscolhido = (Integer) evt.getNewValue();

		/* A posição do campo na tela, já que podem existir vários campos de busca avançada na tela*/
		int posicaoCampoSelecionado = (Integer) evt.getComponent().getAttributes().get("campoSelecionado");
		
		CampoPesquisaAvancada campoSelecionado = campos.get(posicaoCampoSelecionado);
		
		campoSelecionado.setTipoCampo( TipoCampoBuscaAvancada.getTipoCampoBuscaAvancada(valorEscolhido) );
		
		campoSelecionado.verificaExibicaoTipoCampo(); 
		
	}

	
	/**
	 * 
	 *  Adiciona um novo campo à busca avançada
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String adicionaNovoCampoPesquisa(){
		
		confituraAbasPesquisa("buscaAvancada"); // esse método só é usado na busca avançada
		
		campos.add(new CampoPesquisaAvancada(campos.size(), false));
		
		return telaPesquisaTitulo();
	}
	
	
	/**
	 * 
	 *  Remover um campo de pesquisa da da busca avançada. 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String  removerCampo(){
	
		int posicaoSelecionada =  getParameterInt("campoSelecionado");
		
		campos.remove(posicaoSelecionada);
		
		// Na remoção precisa recalcular as posições porque o usuário pode remover um campo de 
		// qualquer posição da lista
		for (int posicao = 0; posicao < campos.size() ; posicao++) {
			campos.get(posicao).setPosicaoCampo(posicao);
		} 
		
		return telaPesquisaTitulo();
	}
	
	
	
	/**
	 * 
	 * Limpa os dados da pesquisa, para fazer uma nova, remove logo o bean inteiro da sessão
	 *
	 * 
	 */
	private void limparDadosPesquisaSimples(){
		
		confituraAbasPesquisa();
		
		campoBuscaSimples = new CampoPesquisaAvancada(0, "", CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.TODOS_OS_CAMPOS);
		
		exitirDadosFormatoRelatorio = false;
		
		limparFiltrosMaterial();
		limparResultadosPesquisa();
	}
	
	/**
	 * 
	 * Limpa os dados da pesquisa, para fazer uma nova, remove logo o bean inteiro da sessão
	 *
	 * 
	 */
	private void limparDadosPesquisaMultiCampo(){
		
		confituraAbasPesquisa();
		
		titulo ="";
		assunto ="";
		autor ="";
		localPublicacao="";
		editora="";
		anoInicial = null;
		anoFinal = null;
		numeroDoSistema = null;
		
		
		buscarTitulo = false; 
		buscarAssunto = false;
		buscarAutor = false;
		buscarLocalPublicacao = false; 
		buscarEditora = false;
		buscarAno = false;
		buscarNumeroSistema = false;
		
		exitirDadosFormatoRelatorio = false;
		
		limparFiltrosMaterial();
		limparResultadosPesquisa();
	}

	
	
	
	/**
	 * 
	 * Limpa os dados da pesquisa, para fazer uma nova, remove logo o bean inteiro da sessão
	 *
	 * 
	 */
	private void limparDadosPesquisaAvancada(){
		
		
		//titulos = new ArrayList<CacheEntidadesMarc>();
		
		
		for (CampoPesquisaAvancada campo :  campos) {
			campo.resetaDadosCampo();
		} 
		
		
		exitirDadosFormatoRelatorio = false;
		
		limparFiltrosMaterial();
		limparResultadosPesquisa();
		
	}
	
	
	/**
	 * 
	 * Limpa os dados da pesquisa, para fazer uma nova
	 *
	 * 
	 */
	private void limparDadosPesquisaPorListas(){
	
		exitirDadosFormatoRelatorio = false;
		
		campoPesquisaPorLista.reset();
		
		limparFiltrosMaterial();
		limparResultadosPesquisa();
	}
	
	
	
	/**
	 *  Limpa os dados do filtro da consulta
	 */
	private void limparFiltrosMaterial(){
		buscarColecao = false;
		buscarBiblioteca = false;
		buscarTipoMaterial = false;
		buscarStatus = false;
		
		idBiblioteca = -1;
		idColecao = -1;
		idTipoMaterial = -1;
		idStatus = -1;
		
	}
	
	/**
	 *  Limpa a lista de os resultados da pesquisa
	 */
	private void limparResultadosPesquisa(){
		paginaAtual = 1;
		quantidadeTotalResultados = 0;
		quantidadePaginas = 1;
		
		resultadosBuscados = new ArrayList<CacheEntidadesMarc>();
		resultadosPaginadosEmMemoria = new ArrayList<CacheEntidadesMarc>();
		artigos = new ArrayList<CacheEntidadesMarc>();
		
	}
	
	
	/**
	 * Usado pelo usuário na página para apagar os resultados da pesquisa
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaSimplesTituloCatalografico.jsp
	 * @return
	 */
	public String apagarDadosPesquisaSimples(){
		
		confituraAbasPesquisa();
		
		limparDadosPesquisaSimples();
		
		return telaPesquisaTitulo();
	}
	
	
	
	
	/**
	 * Usado pelo usuário na página para apagar os resultados da pesquisa
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp
	 * @return
	 */
	public String apagarDadosPesquisaMultiCampo(){
		
		confituraAbasPesquisa();
		
		limparDadosPesquisaMultiCampo();
		
		return telaPesquisaTitulo();
	}
	
	
	
	/**
	 * Usado pelo usuário para apagar os resultados da pesquisa
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp
	 * @return
	 */
	public String apagarDadosPesquisaAvancada(){
		confituraAbasPesquisa();
		
		limparDadosPesquisaAvancada();
		
		return telaPesquisaTitulo();
	}
	
	
	/**
	 * Usado pelo usuário para apagar os resultados da pesquisa
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp
	 * @return
	 */
	public String apagarDadosPesquisaPorListas(){
		confituraAbasPesquisa();
		
		limparDadosPesquisaPorListas();
		
		return telaPesquisaTitulo();
	}
	
	
	/**
	 * 
	 * <p> Gera o resultado da pesquia em um arquivo de referência da ABNT.</p> 
	 * <p> <i>Esse arquivo nada mais é do que um arquivo de texto com os formato de referência as obras buscadas 
	 * um abaixo do outro. </i>
	 * </p>
	 * <p><strong>Observação: Método também chamado das pesquisas públicas do sistema.</strong></p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *   </ul>
	 * @throws IOException 
	 * @throws DAOException 
	 *
	 */
	public void gerarResultadoPesquisaArquivoReferenciaFormatoABNT(ActionEvent evt) throws IOException, DAOException{
		
		if(resultadosPaginadosEmMemoria.size() > 0){
			new GeradorArquivoDeReferenciaABNT().gerarArquivoContextoAtualFaces(resultadosBuscados);
		}else{
			addMensagemErro("A consulta não possui resultados para gerar o arquivo de referência da ABNT");
		}
	}
	
	
	
	
	
	/**
	 * <p>Método que realiza a pesquisa simples das informações dos títulos do sistema.</p>
	 * 
	 * <p>A pesquisa simples o usuário digita um termo e o sistema busca por todos os campos, para isso faz usado da pesquisa avançada todos os campos.</p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaSimplesTituloCatalografico.jsp
	 */
	public String pesquisaSimplesAcervo() throws DAOException{

		confituraAbasPesquisa();
		
		String tituloBusca = null;
		String autorBusca = null;
		String assuntoBusca = null;
		
		TituloCatalograficoDao daoTitulo = null;
		ArtigoDePeriodicoDao artigoDao = null;
		
		limparResultadosPesquisa();
		
		try {
			
			daoTitulo = getDAO(TituloCatalograficoDao.class);
			artigoDao = getDAO(ArtigoDePeriodicoDao.class);	
				
			if(! campoBuscaSimples.isBuscarCampo()){
				campoBuscaSimples.setValorCampo(null);
			}else{ // se o usuário selecionou o campo
				if(! campoBuscaSimples.contemInformacoes()) {
					campoBuscaSimples.setBuscarCampo(false);
				}else {
					tituloBusca = campoBuscaSimples.getValorCampo();
					autorBusca = campoBuscaSimples.getValorCampo();
					assuntoBusca = campoBuscaSimples.getValorCampo();
				}
			}
			
			List<CampoPesquisaAvancada> camposPesquisaSimples = new ArrayList<CampoPesquisaAvancada>();
			camposPesquisaSimples.add(campoBuscaSimples);
			
			Integer idBibliotecaBusca = idBiblioteca;
			Integer idColecaoBusca = idColecao;
			Integer idTipoMaterialBusca = idTipoMaterial;
			Integer idStatusBusca = idStatus;
			
			if(! buscarBiblioteca) idBibliotecaBusca = -1;
			if(! buscarColecao) idColecaoBusca = -1;
			if(! buscarTipoMaterial) idTipoMaterialBusca = -1;
			if(! buscarStatus) idStatusBusca = -1;
			
			if(idBibliotecaBusca > 0){ // Se o usuário selecionou filtar por biblioteca 
				CampoPesquisaAvancada campoBuscaSimplesBiblioteca = new CampoPesquisaAvancada(1, String.valueOf(idBibliotecaBusca), CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.BIBLIOTECA);
				camposPesquisaSimples.add(campoBuscaSimplesBiblioteca);
			}
			
			if(idColecaoBusca > 0){ // Se o usuário selecionou filtar por coleção 
				CampoPesquisaAvancada campoBuscaSimplesColecao = new CampoPesquisaAvancada(2, String.valueOf(idColecaoBusca), CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.COLECAO);
				camposPesquisaSimples.add(campoBuscaSimplesColecao);
			}

			if(idTipoMaterialBusca > 0){ // Se o usuário selecionou filtar por tipo de material 
				CampoPesquisaAvancada campoBuscaSimplesTipoMaterial = new CampoPesquisaAvancada(3, String.valueOf(idTipoMaterialBusca), CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.TIPO_MATERIAL);
				camposPesquisaSimples.add(campoBuscaSimplesTipoMaterial);
			}
			
			if(idStatusBusca > 0){ // Se o usuário selecionou filtar por status 
				CampoPesquisaAvancada campoBuscaSimplesStatus = new CampoPesquisaAvancada(3, String.valueOf(idStatusBusca), CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.STATUS);
				camposPesquisaSimples.add(campoBuscaSimplesStatus);
			}

			
			
			CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
			
			resultadosBuscados = daoTitulo.buscaAvancada(geradorPesquisa, campoOrdenacao, camposPesquisaSimples, false, null, utilizarBuscaRemissiva);
			
			geraResultadosPaginacao();
			
			///////// REALIZA TAMBÉM A BUSCA DE ARTIGOS /////
			if (StringUtils.notEmpty(tituloBusca) || StringUtils.notEmpty(autorBusca) || StringUtils.notEmpty(assuntoBusca)) {
				artigos = artigoDao.buscaSimplesArtigo(geradorPesquisa, CampoOrdenacaoConsultaAcervo.isCampoOrdenacaoArtigo(campoOrdenacao) ? campoOrdenacao : null, tituloBusca, autorBusca, assuntoBusca, true, null);
			}
			
			verificaLimitesDaBusca();
			
			
		}finally{
			if(daoTitulo != null) daoTitulo.close();
			if(artigoDao != null) artigoDao.close();
		}
		
		return telaPesquisaTitulo();
		
	}
	
	
	
	
	
	/**
	 * Método que realiza a pesquisa multi-campo do título
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp
	 * 
	 * @throws Exception 
	 *
	 */
	public String pesquisaMultiCampo() throws DAOException{
		
		confituraAbasPesquisa();
		
		realizouPesquisaMultiCampo = true;
		
		TituloCatalograficoDao daoTitulo = null;
		ArtigoDePeriodicoDao artigoDao = null;
		
		limparResultadosPesquisa();
		
		CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
		
		try{
			
			daoTitulo = getDAO(TituloCatalograficoDao.class);
			artigoDao = getDAO(ArtigoDePeriodicoDao.class);
			
			if( buscarNumeroSistema) {

				
				if(numeroDoSistema == null){
					buscarNumeroSistema = false;
				}else{
					
					CacheEntidadesMarc titulo = daoTitulo.findByNumeroSistema(numeroDoSistema);
				
					if(titulo != null){
						
						resultadosBuscados.add(titulo);
						geraResultadosPaginacao();
						
					}else{
						addMensagemWarning("Não Foi Encontrado nenhum Título com o Número do Sistema Informado");
					}
				}
			}else{
				
				String tituloBusca = titulo;
				String assuntoBusca = assunto;
				String autorBusca = autor;
				String localPublicacaoBusca = localPublicacao;
				String editoraBusca = editora;
				String classificacao1Busca = classificacao1;
				String classificacao2Busca = classificacao2;
				String classificacao3Busca = classificacao3;
				Integer anoInicialBusca = anoInicial;
				Integer anoFinalBusca = anoFinal;
				
				Integer idBibliotecaBusca = idBiblioteca;
				Integer idColecaoBusca = idColecao;
				Integer idTipoMaterialBusca = idTipoMaterial;
				Integer idStatusBusca = idStatus;
				
				if(StringUtils.isEmpty(titulo) || titulo.length() <= 2) buscarTitulo = false;
				if(StringUtils.isEmpty(assunto) || assunto.length() <= 2) buscarAssunto = false;
				if(StringUtils.isEmpty(autor) || autor.length() <= 2) buscarAutor = false;
				if(StringUtils.isEmpty(localPublicacao) || localPublicacao.length() <= 2) buscarLocalPublicacao = false;
				if(StringUtils.isEmpty(editora) || editora.length() <= 2) buscarEditora = false;
				if(StringUtils.isEmpty(classificacao1)) buscarClassificacao1= false;
				if(StringUtils.isEmpty(classificacao2)) buscarClassificacao2= false;
				if(StringUtils.isEmpty(classificacao3)) buscarClassificacao3= false;
				if(anoInicialBusca == null && anoFinalBusca == null) buscarAno = false;
				if(new Integer(-1).equals(idBiblioteca)) buscarBiblioteca = false;
				if(new Integer(-1).equals(idColecao)) buscarColecao = false;
				if(new Integer(-1).equals(idTipoMaterial)) buscarTipoMaterial = false;
				if(new Integer(-1).equals(idStatus)) buscarStatus = false;
				
				if(! buscarTitulo ) tituloBusca = null;
				if(! buscarAssunto) assuntoBusca = null;
				if(! buscarAutor) autorBusca = null;
				if(! buscarLocalPublicacao) localPublicacaoBusca = null;
				if(! buscarEditora) editoraBusca = null;
				if(! buscarClassificacao1) classificacao1Busca = null;
				if(! buscarClassificacao2) classificacao2Busca = null;
				if(! buscarClassificacao3) classificacao3Busca = null;
				if(! buscarAno) { anoInicialBusca = null; anoFinalBusca = null; }
				if(! buscarBiblioteca) idBibliotecaBusca = -1;
				if(! buscarColecao) idColecaoBusca = -1;
				if(! buscarTipoMaterial) idTipoMaterialBusca = -1;
				if(! buscarStatus) idStatusBusca = -1;

				resultadosBuscados = daoTitulo.buscaMultiCampo(geradorPesquisa, campoOrdenacao, tituloBusca, assuntoBusca, autorBusca, localPublicacaoBusca, editoraBusca, classificacao1Busca, classificacao2Busca, classificacao3Busca
					, anoInicialBusca, anoFinalBusca, idBibliotecaBusca, idColecaoBusca, idTipoMaterialBusca, idStatusBusca, false, null, false, utilizarBuscaRemissiva);

				geraResultadosPaginacao();
				
				////////////// REALIZA TAMBÉM A BUSCA DE ARTIGOS //////////////
				if (! isPesquisaAcervoParaSelecao() && ( StringUtils.notEmpty(tituloBusca) || StringUtils.notEmpty(autorBusca) || StringUtils.notEmpty(assuntoBusca) ) ) {
					
					artigos = artigoDao.findAllArtigosResumidosAtivosByTituloAutorPalarvaChave(geradorPesquisa, CampoOrdenacaoConsultaAcervo.isCampoOrdenacaoArtigo(campoOrdenacao) ? campoOrdenacao : null, tituloBusca, autorBusca, assuntoBusca, false, null);
				}
				
				
				verificaLimitesDaBusca();
				
			
			}
			
		}finally{

			if(daoTitulo != null) daoTitulo.close();
			if(artigoDao != null) artigoDao.close();
		}
		
		return telaPesquisaTitulo();
		
	}
	

	
	
	/**
	 * 
	 * Método que realiza a pesquisa avançada das informações dos títulos do sistema.
	 * <br/>
	 * Chamado a partir da página: <ul><li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li></ul>
	 * @throws DAOException 
	 */
	public String pesquisaAvancada() throws DAOException{
		
		confituraAbasPesquisa();

		String titulo = null;
		String autor = null;
		String assunto = null;
		
		realizouPesquisaMultiCampo = false;
		
		TituloCatalograficoDao daoTitulo = null;
		ArtigoDePeriodicoDao artigoDao = null;
		
		limparResultadosPesquisa();
		
		CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
		
		try{
			
			daoTitulo = getDAO(TituloCatalograficoDao.class);
			artigoDao = getDAO(ArtigoDePeriodicoDao.class);
			
			for (CampoPesquisaAvancada campo : campos) {
								
				if(! campo.isBuscarCampo()){
					if(campo.isCampoBuscaInformacoesMateriais())
						campo.setValorCampo("-1");
					else
						campo.setValorCampo(null);
					
				}else{ // se o usuário selecionou o campo 
					
					if(! campo.contemInformacoes()) {
						campo.setBuscarCampo(false);
					}
					else {
						if (campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.TODOS_OS_CAMPOS) {
							titulo = campo.getValorCampo();
							autor = campo.getValorCampo();
							assunto = campo.getValorCampo();
						}
						else {
							if (campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.TITULO) {
								titulo = campo.getValorCampo();
							}
							else if (campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.AUTOR) {
								autor = campo.getValorCampo();
							}
							else if (campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.ASSUNTO) {
								assunto = campo.getValorCampo();
							}
						}
					}
				}
			
				if(campo.fazendoOuLogicoComInformacoesMateriais()){
					addMensagemErro("Não é permitido realizar a operação \"OU\" com informações dos materiais do acervo, pois a busca pode trazer uma quantidade muito grande de resultados.");
					return telaPesquisaTitulo();	
				}
			}
			
			resultadosBuscados = daoTitulo.buscaAvancada(geradorPesquisa, campoOrdenacao, campos, false, null, utilizarBuscaRemissiva);

			geraResultadosPaginacao();
			
			
			//////////////REALIZA TAMBÉM A BUSCA DE ARTIGOS //////////////
			if (! isPesquisaAcervoParaSelecao() && (StringUtils.notEmpty(titulo) || StringUtils.notEmpty(autor) || StringUtils.notEmpty(assunto) )) {
				artigos = artigoDao.findAllArtigosResumidosAtivosByTituloAutorPalarvaChave(geradorPesquisa, CampoOrdenacaoConsultaAcervo.isCampoOrdenacaoArtigo(campoOrdenacao) ? campoOrdenacao : null, titulo, autor, assunto, false, null);
			}
			
			verificaLimitesDaBusca();
			
		}finally{
			
			if(daoTitulo != null) daoTitulo.close();
			if(artigoDao != null) artigoDao.close();
		}
		
		return telaPesquisaTitulo();
		
	}
	
	
	
	
	
	
	/**
	 * 
	 * Método que realiza a pesquisa por listas dos títulos do acervo.
	 * <br/>
	 * Chamado a partir da página: <ul><li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li></ul>
	 * @throws DAOException 
	 */
	public String pesquisaPorListas() throws DAOException{
		
		confituraAbasPesquisa();

		String titulo = null;
		String autor = null;
		String assunto = null;
		
		TituloCatalograficoDao daoTitulo = null;
		ArtigoDePeriodicoDao artigoDao = null;
		
		limparResultadosPesquisa();
		
		if(  campoPesquisaPorLista.isVazio() ){
			campoPesquisaPorLista.setBuscarCampos(false);
		}
		
		if( ! campoPesquisaPorLista.selecionouCampo() ){
			campoPesquisaPorLista.setValorCampo("");
		}
		
		if( campoPesquisaPorLista.selecionouCampo() && ! campoPesquisaPorLista.selecionouTipoCampoEscolhido()){
			addMensagemWarning("Escolha o campo da busca.");
			return telaPesquisaTitulo();
		}
		
		Integer idBibliotecaBusca = idBiblioteca;
		Integer idColecaoBusca = idColecao;
		Integer idTipoMaterialBusca = idTipoMaterial;
		Integer idStatusBusca = idStatus;
		
		if(! buscarBiblioteca) idBibliotecaBusca = -1;
		if(! buscarColecao) idColecaoBusca = -1;
		if(! buscarTipoMaterial) idTipoMaterialBusca = -1;
		if(! buscarStatus) idStatusBusca = -1;
		
		if (campoPesquisaPorLista.isPesquisaListaTitulo()) {
			titulo = campoPesquisaPorLista.getValorCampo();
		}
		else if (campoPesquisaPorLista.isPesquisaListaAutor()) {
			autor = campoPesquisaPorLista.getValorCampo();
		}
		else if (campoPesquisaPorLista.isPesquisaListaAssunto()) {
			assunto = campoPesquisaPorLista.getValorCampo();
		}
		
		CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
		
		try{
			
			daoTitulo = getDAO(TituloCatalograficoDao.class);
			artigoDao = getDAO(ArtigoDePeriodicoDao.class);
			
			resultadosBuscados = daoTitulo.buscaPorListas(geradorPesquisa, campoOrdenacao, campoPesquisaPorLista, idBibliotecaBusca, idColecaoBusca, idTipoMaterialBusca, idStatusBusca, utilizarBuscaRemissiva);

			geraResultadosPaginacao();
			
			//////////////REALIZA TAMBÉM A BUSCA DE ARTIGOS //////////////
			if (! isPesquisaAcervoParaSelecao() &&  ( StringUtils.notEmpty(titulo) || StringUtils.notEmpty(autor) || StringUtils.notEmpty(assunto) )) {	
				artigos = artigoDao.findAllArtigosResumidosAtivosByTituloAutorPalarvaChave(geradorPesquisa, CampoOrdenacaoConsultaAcervo.isCampoOrdenacaoArtigo(campoOrdenacao) ? campoOrdenacao : null, titulo, autor, assunto, false, null);
			}
			
			
			for (CacheEntidadesMarc cache : resultadosBuscados) { // Percorre todos por causa da função de exibir em formato de relatório que sempre exibe todos
				cache.setTipoCampoBuscaPorListas(campoPesquisaPorLista.getTipoCampoEscolhido());
			}
			
			verificaLimitesDaBusca();
			
		}finally{
			
			if(daoTitulo != null) daoTitulo.close();
			if(artigoDao != null) artigoDao.close();
		}
	
		return telaPesquisaTitulo();
	}
	
	
	
	
	/** verifica se os limits das buscas foram alcançados para mostrar a mensagem ao usuária */
	private void verificaLimitesDaBusca(){
		
		if ( quantidadeTotalResultados == 0 && artigos.size() == 0)
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		if( TituloCatalograficoDao.LIMITE_BUSCA_TITULOS.compareTo( quantidadeTotalResultados ) <= 0){
			addMensagemWarning("A busca resultou em um número muito grande de resultados, somente os "+TituloCatalograficoDao.LIMITE_BUSCA_TITULOS+" primeiros estão sendo mostrados.");
		}
		
		if (  ArtigoDePeriodicoDao.LIMITE_BUSCA_ARTIGOS.compareTo( artigos.size() ) <= 0) {
			addMensagemWarning("A busca de artigos resultou em um número muito grande de resultados, somente os "+ArtigoDePeriodicoDao.LIMITE_BUSCA_ARTIGOS+" primeiros estão sendo mostrados.");
		}
	}
	
	
	/**
	 * Método que precisa ser implementado para o usuário "andar" entre as páginas.
	 *
	 * Esse método deve ser implementado porque tem um comportamente deferente do padrão.
	 *
	 * Chamado a partir da página: 
	 * 	<ul>
	 * 		<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPaginacaoConsultaAcervo.jsp</li>
	 *  </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoPaginadoBiblioteca#atualizaResultadosPaginacao(javax.faces.event.ActionEvent)
	 */
	@Override
	public void atualizaResultadosPaginacao(ActionEvent event) throws DAOException{
		
		int numeroPaginaAtual = getParameterInt("_numero_pagina_atual");
		
		paginaAtual = numeroPaginaAtual;
		
		if(paginaAtual > quantidadePaginas)
			paginaAtual = quantidadePaginas;
		
		if(paginaAtual <= 0)
			paginaAtual = 1;
		
		resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
		
		
		if(getValorAbaPesquisa() == "buscaPorListas"){ // Na pesquisa por listas precisa atualizar
			
			for (CacheEntidadesMarc cache : resultadosBuscados) {  // Percorre todos por causa da função de exibir em formato de relatório que sempre exibe todos
				cache.setTipoCampoBuscaPorListas(campoPesquisaPorLista.getTipoCampoEscolhido());
			}
		}
	
	}
	
	
	
	
	/**
	 *    Método que configura o valor das abas da tela de pesquisa de títulos.
	 *
	 */
	private void confituraAbasPesquisa(){
		if ( StringUtils.notEmpty(valorAbaPesquisa))
			getCurrentSession().setAttribute("abaPesquisa", valorAbaPesquisa);
	}
	
	
	/**
	 *    Método que configura o valor das abas da tela de pesquisa de títulos passando o valor da aba;
	 *
	 */
	private void confituraAbasPesquisa(String valorAbaPesquisa){
		this.valorAbaPesquisa = valorAbaPesquisa;
		confituraAbasPesquisa();
	}
	
	
	
	
	/**
	 *     Chamado da página que mostra os resultados da pesquisa de títulos. 
	 *     Pega o título que o usuário selecionou busca-o no banco e chama o bean que controla a 
	 * página onde os bibliotecários editam as informações do título.
	 * 
	 * <br/><br/>
	 * Chamado pela JSP: <ul><li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaTitulosCatalograficos.jsp</li></ul>
	 * @throws ArqException 
	 * 
	 */
	public String editarTitulo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		TituloCatalograficoDao tituloDao = null;
		TituloCatalografico titulo = null;
		CatalogacaoMBean bean = null;
		
		try{
			tituloDao =  getDAO(TituloCatalograficoDao.class);
		
			
			Boolean ativo = tituloDao.findSeTituloEstaAtivo(getParameterInt("idTituloParaEdicao"));
			
			if(! ativo ){
				addMensagemErro("O Título foi removido, não pode mais ser alterado.");
				return null;
			}
			
			// busca o Titulo para edição.
			titulo = tituloDao.findTituloByIdInicializandoDados(getParameterInt("idTituloParaEdicao"));
			
			bean = getMBean("catalogacaoMBean");
		
		}finally{
			if(tituloDao != null)
				tituloDao.close(); // Precisa fechar porque vai abrir outra sessão para o mesmo objeto e vai dar erro
		}
		
		if(titulo.isCatalogado())
			return bean.iniciarParaEdicao(titulo);
		else
			return bean.iniciarDuplicacao(titulo); // Não vai editar, o usuário vai continuar salvando até finalizar
	}
	
	
	
	
	/**
	 *  Copia as informações de um título para outro título e chama a página de catalogação
	 *  para o bibliotecário criar outro título copiando as informações do título selecionado. 
	 *  
	 *  <br/><br/>
	 *  Chamado a partir da página: <ul><li>/sigaa.war/biblioteca/procesos_tecnicos/pesquisa_acervo/resultadoPesquisaTitulosCatalograficos.jsp</li></ul>	 
	 *
	 * @return
	 * @throws DAOException 
	 * @throws ArqException 
	 */
	public String duplicarTituloCatalografico() throws ArqException{
		
		int idTitulo = getParameterInt("idTituloParaDuplicacao");
		
		TituloCatalografico tituloQueVaiSerDuplicado = new TituloCatalografico();
			
		GenericDAO dao = getGenericDAO();
		
		TituloCatalografico tituloCompleto = 
			dao.findByPrimaryKey(idTitulo, TituloCatalografico.class);

		tituloQueVaiSerDuplicado = CatalogacaoUtil.duplicarTitulo(tituloCompleto);
		
		if( tituloQueVaiSerDuplicado.getCamposControle() != null)
		for (CampoControle campo : tituloQueVaiSerDuplicado.getCamposControle()) {
			campo.setEtiqueta(dao.refresh(campo.getEtiqueta()));
		}
		
		if( tituloQueVaiSerDuplicado.getCamposDados() != null)
		for (CampoDados campo : tituloQueVaiSerDuplicado.getCamposDados()) {
			campo.setEtiqueta(dao.refresh(campo.getEtiqueta()));
		}
		
		CatalogacaoMBean bean = getMBean("catalogacaoMBean");
		
		return bean.iniciarDuplicacao(tituloQueVaiSerDuplicado);
	}
	
	
	
	
	/**
	 * 
	 *   <p>Vai direto para a página de inclusão de materiais. Para incluir exemplares ou fascículo no 
	 * título selecionado.</p> 
	 * 
	 *   <p><strong>Obs.: Os informações da compra já estão em sessão dentro de CatalogacaoMBean.</strong></p> 
	 * 
	 *   <p><strong> Se o usuário escolher um título que não foi finalizado ainda o sistema deve redirecionar 
	 * para a página de catalogação para o usuário finalizar a catalogação do Título, pois só podem
	 * ser adicionados Materiais a Títulos Finalizados.</strong></p> 
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/procesos_tecnicos/pesquisa_acervo/resultadoPesquisaTitulosCatalograficos.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String inserirMateriaisTituloSelecionado() throws ArqException{
		
		int idTituloParaInclusaoMaterial = getParameterInt("idTituloParaInclusaoMaterial");
		
		TituloCatalografico titulo =  getDAO(TituloCatalograficoDao.class).findByPrimaryKey(idTituloParaInclusaoMaterial, TituloCatalografico.class);
		
		if(titulo.isCatalogado()){ // ok o título foi finalizado e pode inserir materiais
		
			MaterialInformacionalMBean materialMBean = getMBean("materialInformacionalMBean");
			
			materialMBean.setIncluindoMateriaisApartirTelaBusca(true); // habilita o botão voltar correto
			
			if(pesquisaTituloParaCatalogacaoComTombamento){
				
				// Onde estão as informações dos dados da compra
				CatalogacaoMBean catalogacaoMBean =  getMBean("catalogacaoMBean"); 
				
				InformacoesTombamentoMateriaisDTO infomacoesTituloCompra =  catalogacaoMBean.getInfoTituloCompra();
				infomacoesTituloCompra.idTituloCatalograficoSigaa = titulo.getId();
				
				if(titulo.getFormatoMaterial().isFormatoPeriodico())		
					return materialMBean.iniciarParaAdicaoFasciculos(infomacoesTituloCompra);
				else
					return materialMBean.iniciarParaAdicaoExemplares(infomacoesTituloCompra, 
							PAGINA_PESQUISA_TITULO);
			}
			
			
			if( pesquisaTituloParaCatalogacao){
				
				if(titulo.getFormatoMaterial().isFormatoPeriodico())		
					return materialMBean.iniciarParaAdicaoFasciculosNaoTombados(titulo);
				else
					return materialMBean.iniciarParaAdicaoExemplaresNaoTombados(titulo, PAGINA_PESQUISA_TITULO);
			}
			
			// Nunca era para chegar aqui, só é possível chamar esse método se uma das opções 
			// anteriores estiverem habilitadas
			return null;
			
			
		}else{  
			
			// Título não finalizado, vai para a página para finalizar. As informações da compra
			// já foram colocadas no bean quando o usuário realizou a busca no SIPAC, bem como 
			// a informação de catalogação sem tombamento. Então quando finalizar a catalogação
			// vai existir a opção de ir para a página de adicionar materiais.
			
			CatalogacaoMBean bean = getMBean("catalogacaoMBean");
			
			// vai editar o título, então tem que carregar todas as suas informações.
			titulo = getDAO(TituloCatalograficoDao.class).findTituloByIdInicializandoDados(titulo.getId());
			
			return bean.iniciarDuplicacao(titulo);
		}
		
	}
	
	/**
	 *   Método chamado no botão voltar da tela de inclusão de exemplares. Deve voltar para a tela de 
	 *   pesquisa com as opções de catalogação sem tombamento habilitadas.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos_catalogacao/paginaInformacoesExemplares.jsp
	 * @return
	 * @throws DAOException 
	 */
	public String voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisSemTombamento(){
		
		operacao = PESQUISA_NORMAL ;
		
		pesquisaTituloParaCatalogacao = true;
		
		return telaPesquisaTitulo();
	}
	
	
	
	/**
	 *   Método chamado no botão voltar da tela de inclusão de exemplares. Deve voltar para a tela de 
	 *   pesquisa com as opções de catalogação com tombamento habilitadas.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos_catalogacao/paginaInformacoesExemplares.jsp
	 * @return
	 * @throws DAOException 
	 */
	public String voltarTelaPesquisaTituloVindoDaTelaInclusaoMateriaisComTombamento(){
		
		operacao = PESQUISA_NORMAL;
		
		pesquisaTituloParaCatalogacao = true;
		
		return telaPesquisaTitulo();
	}
	
	
	
	/**
	 *   Método chamado no botão voltar da tela de inclusão de exemplares. Deve voltar para a tela de 
	 *   pesquisa com as opções de catalogação habilitadas.
	 * <br/><br/>
	 * 	Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaDetalhesMateriais.jsp
	 * @return
	 * @throws DAOException 
	 */
	public String voltarTelaPesquisaTituloVindoDaTelaDetalhesMaterias(){
		
		// a operação permanece a que estava antes quando volta.
		// se era a pesquisa normal, pesquisa para catalogação por tombamento ou sem.
		
		return telaPesquisaTitulo();
	}
	
	
	
	
	/**
	 *   Método chamado no botão voltar da tela de detalhes dos materias, para voltar para a tela 
	 *   de pesquisa e manter a operação de pesquisa do título origem dos exemplares da transferência. <br/>
	 *   Nesse caso não para para mandar a última operação porque o caso de uso passo 2 vezes pela mesma tela.
	 *   
	 *   <br/><br/>
	 * 			Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaDetalhesMateriais.jsp
	 * @return
	 * @throws DAOException 
	 */
	public String voltarTelaPesquisaTituloVindoDaTelaDetalhesMateriasOperacaoBuscaTituloOriginal(){
		
		//operacao = PESQUISA_PRE_TRANSFERENCIA_DO_EXEMPLAR;
		
		return telaPesquisaTitulo();
	}
	
	
	
	
	
	
	/**
	 *   Método chamado por alguma botão voltar de alguma página do sistema para voltar à tela de 
	 *   pesquisa de títulos mantendo a operação anterior
	 *
	 *  <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/paginaConfirmaAssociacaoTituloAssinatura.jsp
	 *   
	 * @return
	 * @throws DAOException 
	 */
	public String voltarTelaPesquisaTitulo(){
		
		// a operação permanece a que estava antes quando volta.
		// se era a pesquisa normal, pesquisa para catalogação por tombamento ou sem.
		
		return telaPesquisaTitulo();
	}
	
	
	
	/////////////////////// Páginas de navegação //////////////////////////////
	
	/**
	 * Realiza um <i>Forward</i> para a tela de pesquisa de Títulos
	 * 
	 * <br/><br/>
	 * <li>
	 * Chamado a partir das páginas: 
	 * <ul>/sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica/formImportarTituloAutoridade.jsp</ul>
	 * <ul>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolheForamtoMaterial.jsp</ul>
	 * <ul>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaBibliografica.jsp</ul>
	 * <ul>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplares.jsp</ul>
	 * <ul>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoFasciculos.jsp</ul>
	 * <ul>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp</ul>
	 * </li>
	 */
	public String telaPesquisaTitulo(){
		
		if(exitirDadosFormatoRelatorio && quantidadeTotalResultados > 0){
			// Logo após exibir o relatório tem que desativar, por que como tem vários botões voltar    // 
			// nessa tela o usuário pode usar um botão voltar que chama esse método e ai voltar para    //
			// a página de relatório.                                                                   //
			exitirDadosFormatoRelatorio = false; 
			return forward(PAGINA_RESULTADOS_PESQUISA_TITULO_RELATORIO);
		}else
			return forward(PAGINA_PESQUISA_TITULO);
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 
	 * Retorna todas as Biblioteca internas do sistema para mostrar no formulário da busca
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecasInternasAtivas() throws DAOException{
		if ( bibliotecasAtivas == null )
			bibliotecasAtivas = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas(); 
		return toSelectItems(bibliotecasAtivas, "id", "descricaoCompleta");
	}
	
	/**
	 * 
	 * Retorna todas as coleções para mostrar no formulário da busca
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getColecoesAtivas() throws DAOException{
		if(colecoes == null)
			colecoes = getGenericDAO().findByExactField(Colecao.class, "ativo", true);
		return toSelectItems(colecoes, "id", "descricaoCompleta");
	}
	

	/**
	 * 
	 * Retorna todas os tipos do item catalográfico para mostrar no formulário da busca
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getTiposMateriaisAtivos() throws DAOException{
		if(tiposMateriais == null)
			tiposMateriais = getGenericDAO().findByExactField(TipoMaterial.class, "ativo", true);
		return toSelectItems(tiposMateriais, "id", "descricao");
	}
	
	/**
	 * 
	 * Retorna todas o status do item catalográfico para mostrar no formulário da busca
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tencicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getStatusAtivos() throws DAOException{
		if(status == null)
			status = getGenericDAO().findByExactField(StatusMaterialInformacional.class, "ativo", true);
		return toSelectItems(status, "id", "descricao");
	}
	
	
	/**
	 * 
	 * Retorna os campos da busca por listas.
	 *
	 * <li>
	 * Chamado a partir das páginas: 
	 * 		<ul>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</ul>
	 * </li>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getCamposBuscaPorLista() throws DAOException{	
		Collection<SelectItem> camposBuscaPorListas = new ArrayList<SelectItem>();
		
		ClassificacaoBibliograficaMBean beanClassificacao = getMBean("classificacaoBibliograficaMBean"); 
		
		camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.TITULO, CampoPesquisaPorListas.DESCRICAO_TITULO));
		camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.AUTOR, CampoPesquisaPorListas.DESCRICAO_AUTOR ));
		camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.ASSUNTO, CampoPesquisaPorListas.DESCRICAO_ASSUNTO ));
		camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.SERIE, CampoPesquisaPorListas.DESCRICAO_SERIE ));
		camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.LOCAL, CampoPesquisaPorListas.DESCRICAO_LOCAL ));
		camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.EDITORA, CampoPesquisaPorListas.DESCRICAO_EDITORA ));
		if(beanClassificacao.isSistemaUtilizandoClassificacao1())
			camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.CLASSIFICACAO_1, beanClassificacao.getDescricaoClassificacao1()));
		
		if(beanClassificacao.isSistemaUtilizandoClassificacao2())
			camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.CLASSIFICACAO_2, beanClassificacao.getDescricaoClassificacao2()));
		
		if(beanClassificacao.isSistemaUtilizandoClassificacao3())
			camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.CLASSIFICACAO_3, beanClassificacao.getDescricaoClassificacao3()));
		
		camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.NUMERO_CHAMADA, CampoPesquisaPorListas.DESCRICAO_NUMERO_CHAMADA ));
		camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.ISBN, CampoPesquisaPorListas.DESCRICAO_ISBN ));
		camposBuscaPorListas.add(new SelectItem(CampoPesquisaPorListas.ISSN, CampoPesquisaPorListas.DESCRICAO_ISSN ));
		
		return camposBuscaPorListas;
	}
	
	
	
	
	/**
	 *   Verifica se o usuário realizou a busca multi campo ou avançada.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaTitulosCatalograficosRelatorio.jsp
	 *
	 * @return
	 */
	public boolean isBuscaMultiCampo(){
		
		if(valorAbaPesquisa.equalsIgnoreCase("buscaMultiCampo"))
			return true;
		else
			return false;
	}
	
	/**
	 *  A descrição da biblioteca seleciona pelo usuário na busca multicampo 
	 *  Como as coleções já foram carregadas, não precisa busca no banco.<br/>
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaTitulosCatalograficosRelatorio.jsp
	 *
	 * @return
	 */
	public String getDescricaoBibliotecaBuscaMultiCampo(){
		
		for (Biblioteca bib : bibliotecasAtivas) {
		
			if(bib.getId() == idBiblioteca)
				return bib.getDescricao();
		}
		
		return "";
	}
	
	/**
	 * Retorna a descrição da coleção selecionada pelo usuário na busca multicampo 
	 *  Como as coleções já foram carregadas, não precisa busca no banco.<br/>
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaTitulosCatalograficosRelatorio.jsp
	 *
	 * @return
	 */
	public String getDescricaoColecaoBuscaMultiCampo(){
		for (Colecao col : colecoes) {
			
			if(col.getId() == idColecao)
				return col.getDescricao();
		}
		
		return "";
	}
	
	/**
	 * Retorna a descrição do tipo de material selecionado pelo usuário na busca multicampo<br/> 
	 * Como as coleções já foram carregadas, não precisa busca no banco.<br/>
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaTitulosCatalograficosRelatorio.jsp
	 *
	 * @return
	 */
	public String getDescricaoTipoMaterialBuscaMultiCampo(){
		for (TipoMaterial tipo : tiposMateriais) {
			
			if(tipo.getId() == idTipoMaterial)
				return tipo.getDescricao();
		}
		
		return "";
	}
	
	
	/**
	 * Retorna a descrição do tipo de material selecionado pelo usuário na busca multicampo<br/> 
	 * Como as coleções já foram carregadas, não precisa busca no banco.<br/>
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/resultadoPesquisaTitulosCatalograficosRelatorio.jsp
	 *
	 * @return
	 */
	public String getDescricaoStatusBuscaMultiCampo(){
		for (StatusMaterialInformacional tipo : status) {
			
			if(tipo.getId() == idStatus)
				return tipo.getDescricao();
		}
		
		return "";
	}
	
	
	/**
	 * 
	 * Retorna os campos que o usuário vai pode escolher a ordenação no resultado da pesquisa.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public Collection<SelectItem> getCampoOrdenacaoResultadosComboBox(){
		
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		
		for(CampoOrdenacaoConsultaAcervo campo : CampoOrdenacaoConsultaAcervo.CAMPOS_ORDENACAO_TITULO){
			lista.add(new SelectItem(campo.getValor(), campo.getDescricao()));
		}
		
		return lista;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	// set e gets
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getLocalPublicacao() {
		return localPublicacao;
	}

	public void setLocalPublicacao(String localPublicacao) {
		this.localPublicacao = localPublicacao;
	}

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora;
	}

	public Integer getAnoInicial() {
		return anoInicial;
	}

	public void setAnoInicial(Integer anoInicial) {
		this.anoInicial = anoInicial;
	}

	public Integer getAnoFinal() {
		return anoFinal;
	}

	public void setAnoFinal(Integer anoFinal) {
		this.anoFinal = anoFinal;
	}

	public List<CacheEntidadesMarc> getArtigos() {
		return artigos;
	}

	public void setArtigos(List<CacheEntidadesMarc> artigos) {
		this.artigos = artigos;
	}

	public boolean isPesquisaNormal() {
		return operacao == PESQUISA_NORMAL;
	}

	public Integer getNumeroDoSistema() {
		return numeroDoSistema;
	}

	public void setNumeroDoSistema(Integer numeroDoSistema) {
		this.numeroDoSistema = numeroDoSistema;
	}

	public boolean isBuscarTitulo() {
		return buscarTitulo;
	}

	public void setBuscarTitulo(boolean buscarTitulo) {
		this.buscarTitulo = buscarTitulo;
	}

	public boolean isBuscarAssunto() {
		return buscarAssunto;
	}

	public void setBuscarAssunto(boolean buscarAssunto) {
		this.buscarAssunto = buscarAssunto;
	}

	public boolean isBuscarAutor() {
		return buscarAutor;
	}

	public void setBuscarAutor(boolean buscarAutor) {
		this.buscarAutor = buscarAutor;
	}

	public boolean isBuscarLocalPublicacao() {
		return buscarLocalPublicacao;
	}

	public void setBuscarLocalPublicacao(boolean buscarLocalPublicacao) {
		this.buscarLocalPublicacao = buscarLocalPublicacao;
	}

	public boolean isBuscarEditora() {
		return buscarEditora;
	}

	public void setBuscarEditora(boolean buscarEditora) {
		this.buscarEditora = buscarEditora;
	}

	public boolean isBuscarAno() {
		return buscarAno;
	}

	public void setBuscarAno(boolean buscarAno) {
		this.buscarAno = buscarAno;
	}

	public boolean isBuscarNumeroSistema() {
		return buscarNumeroSistema;
	}

	public void setBuscarNumeroSistema(boolean buscarNumeroSistema) {
		this.buscarNumeroSistema = buscarNumeroSistema;
	}

	public String getTituloSipac() {
		return tituloSipac;
	}

	public void setTituloSipac(String tituloSipac) {
		this.tituloSipac = tituloSipac;
	}

	public String getAutorSipac() {
		return autorSipac;
	}

	public void setAutorSipac(String autorSipac) {
		this.autorSipac = autorSipac;
	}

	public boolean isRealizouPesquisaMultiCampo() {
		return realizouPesquisaMultiCampo;
	}

	public String getValorAbaPesquisa() {
		return valorAbaPesquisa;
	}

	public void setValorAbaPesquisa(String valorAbaPesquisa) {
		this.valorAbaPesquisa = valorAbaPesquisa;
	}

	public boolean isBuscarBiblioteca() {
		return buscarBiblioteca;
	}

	public void setBuscarBiblioteca(boolean buscarBiblioteca) {
		this.buscarBiblioteca = buscarBiblioteca;
	}

	public Integer getIdBiblioteca() {
		return idBiblioteca;
	}

	public void setIdBiblioteca(Integer idBiblioteca) {
		this.idBiblioteca = idBiblioteca;
	}

	public Integer getIdColecao() {
		return idColecao;
	}

	public void setIdColecao(Integer idColecao) {
		this.idColecao = idColecao;
	}

	public Integer getIdTipoMaterial() {
		return idTipoMaterial;
	}

	public void setIdTipoMaterial(Integer idTipoMaterial) {
		this.idTipoMaterial = idTipoMaterial;
	}
	
	public boolean isBuscarColecao() {
		return buscarColecao;
	}

	public void setBuscarColecao(boolean buscarColecao) {
		this.buscarColecao = buscarColecao;
	}
	
	public boolean isBuscarTipoMaterial() {
		return buscarTipoMaterial;
	}

	public void setBuscarTipoMaterial(boolean buscarTipoMaterial) {
		this.buscarTipoMaterial = buscarTipoMaterial;
	}

	public boolean isExitirDadosFormatoRelatorio() {
		return exitirDadosFormatoRelatorio;
	}

	public void setExitirDadosFormatoRelatorio(boolean exitirDadosFormatoRelatorio) {
		this.exitirDadosFormatoRelatorio = exitirDadosFormatoRelatorio;
	}
	
	/**
	 * <p>Se a pesquisa está sendo usada por outro caso de uso para selecionar o título ou os materiais do Título.</p>
	 * 
	 * <p>Neste caso as opções de editar, visualizar, remover, etc...  não estarão habilitadas, somente a opção de 
	 * selecionar 1 Título OU os vários materiais de 1 Título.</p>
	 * 
	 */
	public boolean isPesquisaAcervoParaSelecao() {
		return operacao ==  PESQUISA_SELECIONA_UM_TITULO || operacao ==  PESQUISA_SELECIONA_VARIOS_MATERIAIS;
	}
	
	/**
	 * <p>Retorna a quantidade de artigos encontrados</p>
	 * 
	 */
	public int getQuantidadeArtigosEncontados(){
		if(artigos != null)
			return artigos.size();
		return 0;
	}
	
	
	public List<CampoPesquisaAvancada> getCampos() {
		return campos;
	}

	public void setCampos(List<CampoPesquisaAvancada> campos) {
		this.campos = campos;
	}

	public CampoPesquisaPorListas getCampoPesquisaPorLista () {
		return campoPesquisaPorLista;
	}

	public void setCampoPesquisaPorLista (CampoPesquisaPorListas campoPesquisiPorLista) {
		this.campoPesquisaPorLista = campoPesquisiPorLista;
	}


	public int getOperacao() {
		return operacao;
	}
	
	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public boolean isUtilizarBuscaRemissiva() {
		return utilizarBuscaRemissiva;
	}

	public void setUtilizarBuscaRemissiva(boolean utilizarBuscaRemissiva) {
		this.utilizarBuscaRemissiva = utilizarBuscaRemissiva;
	}

	public String getClassificacao1() {
		return classificacao1;
	}

	public void setClassificacao1(String classificacao1) {
		this.classificacao1 = classificacao1;
	}

	public String getClassificacao2() {
		return classificacao2;
	}

	public void setClassificacao2(String classificacao2) {
		this.classificacao2 = classificacao2;
	}

	public String getClassificacao3() {
		return classificacao3;
	}


	public void setClassificacao3(String classificacao3) {
		this.classificacao3 = classificacao3;
	}




	public boolean isBuscarClassificacao1() {
		return buscarClassificacao1;
	}
	
	public void setBuscarClassificacao1(boolean buscarClassificacao1) {
		this.buscarClassificacao1 = buscarClassificacao1;
	}
	
	public boolean isBuscarClassificacao2() {
		return buscarClassificacao2;
	}

	public void setBuscarClassificacao2(boolean buscarClassificacao2) {
		this.buscarClassificacao2 = buscarClassificacao2;
	}

	public boolean isBuscarClassificacao3() {
		return buscarClassificacao3;
	}

	public void setBuscarClassificacao3(boolean buscarClassificacao3) {
		this.buscarClassificacao3 = buscarClassificacao3;
	}
	
	public PesquisarAcervoMateriaisInformacionais getMbeanChamadorSelecionaMaterais() {
		return mbeanChamadorSelecionaMaterais;
	}
	
	public void setMbeanChamadorSelecionaMaterais(PesquisarAcervoMateriaisInformacionais mbeanChamadorSelecionaMaterais) {
		this.mbeanChamadorSelecionaMaterais = mbeanChamadorSelecionaMaterais;
	}

	public PesquisarAcervoBiblioteca getMbeanChamadoPesquisaTitulo() {
		return mbeanChamadoPesquisaTitulo;
	}

	public void setMbeanChamadoPesquisaTitulo(PesquisarAcervoBiblioteca mbeanChamadoPesquisaTitulo) {
		this.mbeanChamadoPesquisaTitulo = mbeanChamadoPesquisaTitulo;
	}

	public boolean isPesquisaTituloParaCatalogacao() {
		return pesquisaTituloParaCatalogacao;
	}

	public void setPesquisaTituloParaCatalogacao(boolean pesquisaTituloParaCatalogacao) {
		this.pesquisaTituloParaCatalogacao = pesquisaTituloParaCatalogacao;
	}

	public boolean isPesquisaTituloParaCatalogacaoComTombamento() {
		return pesquisaTituloParaCatalogacaoComTombamento;
	}

	public void setPesquisaTituloParaCatalogacaoComTombamento(boolean pesquisaTituloParaCatalogacaoComTombamento) {
		this.pesquisaTituloParaCatalogacaoComTombamento = pesquisaTituloParaCatalogacaoComTombamento;
	}


	public boolean isPesquisaTituloApenasCatalogacao() {
		return pesquisaTituloApenasCatalogacao;
	}

	public void setPesquisaTituloApenasCatalogacao(boolean pesquisaTituloApenasCatalogacao) {
		this.pesquisaTituloApenasCatalogacao = pesquisaTituloApenasCatalogacao;
	}

	public CampoPesquisaAvancada getCampoBuscaSimples() {
		return campoBuscaSimples;
	}

	public void setCampoBuscaSimples(CampoPesquisaAvancada campoBuscaSimples) {
		this.campoBuscaSimples = campoBuscaSimples;
	}




	public boolean isBuscarStatus() {
		return buscarStatus;
	}




	public void setBuscarStatus(boolean buscarStatus) {
		this.buscarStatus = buscarStatus;
	}




	public Integer getIdStatus() {
		return idStatus;
	}




	public void setIdStatus(Integer idStatus) {
		this.idStatus = idStatus;
	}

	
	
	
	
	
}
