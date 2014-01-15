/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/09/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil.recuperaEtiquetaTitulo;
import static br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil.retornaGrupoEtiqueta;
import static br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil.tituloContemCampoDados;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.integracao.dto.biblioteca.InformacoesTombamentoMateriaisDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoBibliotecaException;
import br.ufrn.integracao.interfaces.BibliotecaComprasRemoteService;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EtiquetaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DadosDefesaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AreaConhecimentoCNPqBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CategoriaMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ConfiguracoesTelaCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DadosTabelaCutter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorSubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Editora;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.GrupoEtiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.PlanilhaCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TipoCatalogacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorPadraoCampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.CatalogacaoValidatorFactory;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAtualizaAutoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAtualizaTitulo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoCatalogaAutoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoCatalogaTitulo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CampoControleByEtiquetaComparator;
import br.ufrn.sigaa.biblioteca.util.CampoDadosByEtiquetaPosicaoComparator;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;
import br.ufrn.sigaa.biblioteca.util.SubCampoByCodigoComparator;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *    MBean respons�vel por realizar cataloga��o de um T�tulo e Autoridade na biblioteca.
 * 
 *    Gerencia a p�gina principal de cataloga��o <code>formDadosTituloCatalografico.jsp</code>
 *    e as outras p�ginas de edi��o dos campos de controle
 *    <code>formDadosCampo001.jsp</code> ... <code>formDadosCampo008.jsp</code>
 * 
 * @author Victor Hugo
 * @version 1.0 cria��o da classe
 * @version 1.1 Jadson Adi��o dos m�todos que manipulam os campos de controle e edi��o/remo��o do T�tulo
 * @version 1.2 Jadson Adicionando a parte de cataloga��o de autoridades que vai usar a mesma tela.
 * @version 1.3 Jadson Adicionando a ajuda, busca por autoridades e obra digitalizada.
 * @version 1.4 Jadson Adicionando a op��o de alterar a ordem os campos e subcampos.
 * @version 1.5 Br�ulio Implementa��o do caso de uso de cataloga��o de defesas.
 * @version 1.6 Jadson Otimizando a busca de informa��o de etiquetas. Guardando em mem�ria as etiquetas 
 *       j� buscadas para diminuir as consultas ao banco durante a cataloga��o. Utilizando uma fila de requisi��es ajax para tentar 
 *       concertar o problema de falta de sincromismo na p�gina.
 * @version 1.7 Jadson Generalizando as classifica��es bibliogr�ficas utilizadas na biblioteca
 * @version 2.0 20/06/2012 - Jadson - Adicionando a possibilidade de se ter 2 telas de cataloga��o diferentes, uma simplificada 
 * e a antida onde o usu�rio pode mexer em toda a codifica��o MARC.
 */
@Component("catalogacaoMBean")
@Scope("request")
public class CatalogacaoMBean extends SigaaAbstractController<TituloCatalografico> {
	
	/** Constantes de navega��o das p�ginas */
	private static final String BASE = "/biblioteca/processos_tecnicos/catalogacao";
	
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_ESCOLHE_FORMATO_MATERIAL = BASE + "/escolheFormatoMaterial.jsp";
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_ESCOLHE_PLANILHA_BIBLIOGRAFICA = BASE + "/escolhePlanilhaBibliografica.jsp";
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_ESCOLHE_PLANILHA_AUTORIDADES = BASE + "/escolhePlanilhaAutoridades.jsp";
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_PREENCHE_DADOS_TITULO_CATALOGRAFICO = BASE + "/formDadosTituloCatalografico.jsp";
	
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_CAMPO_LIDER = BASE + "/formDadosCampoLider.jsp";
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_CAMPO_001 = BASE + "/formDadosCampo001.jsp";
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_CAMPO_003 = BASE + "/formDadosCampo003.jsp";
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_CAMPO_005 = BASE + "/formDadosCampo005.jsp";
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_CAMPO_006 = BASE + "/formDadosCampo006.jsp";
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_CAMPO_007 = BASE + "/formDadosCampo007.jsp";
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_CAMPO_008 = BASE + "/formDadosCampo008.jsp";
	
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_BUSCA_SIPAC_A_PARTIR_NUMERO_PATRIMONIO = BASE + "/buscaInformacoesSipacAPartirNumeroPatrimonio.jsp";
	
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_BUSCA_EDITORAS_CADASTRADAS_SIPAC = BASE + "/buscaEditoraCadastradasSipac.jsp";
	
	/** Constantes de navega��o das p�ginas */
	public static final String PAGINA_BUSCA_TABELA_CUTTER = BASE + "/buscaTabelaCutter.jsp";
	
	
	//  VALORES PADR�O � PARA MONTAR AS P�GINA DE ALTERA��O DE CADA UM DOS CAMPO DE CONTROLE  //
	
	
	/** Valores  do campo l�der que vem preenchido por padr�o na tela ao tentar incluir um novo campo. S�o buscados apenas uma vez, o usu�rio n�o os altera. */
	private List<ValorPadraoCampoControle> valoresPadraoLider;
	/** Valores  do campo 006 que vem preenchido por padr�o na tela ao tentar incluir um novo campo. S�o buscados apenas uma vez, o usu�rio n�o os altera. */
	private List<ValorPadraoCampoControle> valoresPadrao006;
	/** Valores  do campo 007 que vem preenchido por padr�o na tela ao tentar incluir um novo campo. S�o buscados apenas uma vez, o usu�rio n�o os altera. */
	private List<ValorPadraoCampoControle> valoresPadrao007;
	/** Valores  do campo 008 que vem preenchido por padr�o na tela ao tentar incluir um novo campo. S�o buscados apenas uma vez, o usu�rio n�o os altera. */
	private List<ValorPadraoCampoControle> valoresPadrao008;
	
	/* *****************************************************************************************************************
	 * Os seguintes objetos s�o respons�veis por guardar o que o usu�rio digitar na p�gina                            
	 * Obs.:  Apenas para visualizar os dados em duas colunas tive que para cada uma criar "valorPar" e "valorImpar"  
	 * Obs2.: Tem que ter um por p�gina sen�o d� erro ao usar o bot�o voltar no navegador                            
	 ****************************************************************************************************************** */
	
	/** Guarda os valores do campo l�der digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valoresLider;
	/** Guarda os valores pares do campo l�der digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valoresParLider = new ArrayList<ValorPadraoCampoControle>();
	/** Guarda os valores impares do campo l�der digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valoresImparLider = new ArrayList<ValorPadraoCampoControle>();
	
	/** Guarda os valores do campo 006 digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valores006;
	/** Guarda os valores pares do campo 006 digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valoresPar006 = new ArrayList<ValorPadraoCampoControle>();
	/** Guarda os valores impares do campo 006 digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valoresImpar006 = new ArrayList<ValorPadraoCampoControle>();
	
	/** Guarda os valores do campo 007 digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valores007;
	/** Guarda os valores pares do campo 007 digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valoresPar007 = new ArrayList<ValorPadraoCampoControle>();
	/** Guarda os valores impares do campo 007 digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valoresImpar007 = new ArrayList<ValorPadraoCampoControle>();
	
	/** Guarda os valores do campo 008 digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valores008;
	/** Guarda os valores pares do campo 008 digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valoresPar008 = new ArrayList<ValorPadraoCampoControle>();
	/** Guarda os valores pares do campo 008 digitado pelo usu�rio na p�gina */
	private List<ValorPadraoCampoControle> valoresImpar008 = new ArrayList<ValorPadraoCampoControle>();
	

	/** O tipo de cataloga��o realizada no momento. Por padr�o � bibliogr�fica */
	private int tipoCatagalocao = TipoCatalogacao.BIBLIOGRAFICA; 
	
	/** Para saber qual etiqueta o usu�rio est� editando nesse momento */
	private Etiqueta etiquetaControleAtual;  
	
	
	/**
	 * Guarda um cache das etiquetas buscadas pelo usu�rio, para n�o precisar sempre ir no banco
	 */
	private List<Etiqueta> etiquetasBuscadas = new ArrayList<Etiqueta>(); 
	
	
	/** Guarda em cache a lista de classifica��es bibliogr�fica utilizadas no sistema para n�o precisar busca sempre no banco*/
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>(); 
	
	
	/**
	 * Vari�vel que guarda o valor da etiqueta do campo na busca de um campo de dados completo.
	 */
	private String tagEtiquetaCampoDeDadosCompleto;
	
	/**
	 * Guarda o campo de dados que foi buscado pelo busca completa (a busca com subcampos)
	 * 
	 * O usu�rio deve escolher quais os sub campos que ele quer antes desse campos  ser adicionado ao T�tulo;
	 */
	private CampoDados campoDadosBuscaCompleta;
	
	/**
	 * Mensagens para serem mostradas nos models panel das p�ginas.
	 */
	private List<String> mensagensModelPanel;
	
	
	/**
	 * Usado para redenrizar as informa��es sobre os materiais do T�tulo que est� sendo catalogado
	 */
	private TreeNodeImpl<String> rootNode;
	
	/** Para exibi��o para o usu�rio da quantidade de Materiais de um T�tulo */
	private Long quantidadeMateriaisTitulo = 0l;
	
	/** Indica se a listagem de materiais do T�tulo deve ser mostrada por padr�o ou n�o. Em alguns casos 
	 * como peri�dico a quantidade � muito grande e o sistema vai ocultar por padr�o para n�o ficar muito demorado carregar a p�gina.*/
	private boolean visualizaListaMateriaisTitulo = false;
	
	/**
	 * Guarda os dados dos campo de controle 001, 003, 005 que � s� uma string
	 * simples e n�o precisa desses objetos "valores"
	 */
	private String dadosCamposControle = "";
	
	
	/**
	 * Encapsula a cole��o de campos de dados para acess�-los nas p�ginas JSF
	 * 
	 * Observa��o: S� vai conter os campos n�o reservados do sistema, s�o os campos que o catalogador pode mexer
	 */
	private DataModel dataModelCampos;
	
	
	/** Guarda os campos que s�o N�O reservados no sistema que o catalogador pode alterar */
	private List<CampoDados> camposNaoReservados;
	
	/** Guarda os campos que s�o reservados no sistema e o catalogador n�o pode alterar */
	private List<CampoDados> camposReservados;
	
	/**
	 *    Atributo para indicar que o usu�rio clicou no bot�o de finalizar cataloga��o.
	 */
	private String finalizarCatalogacao = "false";
	
	
	
	/**
	 *    Se veio da p�gina de t�tulos/autoridades n�o finalizados, vai ser setado esse valor para "true", para quando o
	 * usu�rio terminar a cataloga��o e a inclus�o de materiais habilitar um bot�o para voltar � tela t�tulos
     * n�o finalizados e escolher outro para trabalhar.
     * 
     *    No caso de autoridades vai aparecer um bot�o para o usu�rio voltar para a tela de autoridades
     * n�o finalizadas e escolher a pr�xima para trabalhar.
     * 
	 */
	private boolean possuiEntiadesNaoFinalizados = false;
	
	
	/**
	 *  Indica se o usu�rio selecionou o bot�o de catalogar a pr�xima autoridade incompleta ou n�o
	 */
	private String redirecionaProximaAutoriadeIncompleta ="false";
	
	
	/**
	 *    Esse atributo controla se o usu�rio deseja adicionar materiais informacionais ap�s a cataloga��o do T�tulo,
	 * utilizado para redirecionar � p�gina de adi��o de materiais, no final do fluxo de cataloga��o.
	 * 
	 *  Implicitamente indica que o usu�rio est� finalizando a cataloga��o pois s� pode adicionar
	 *  materiais a t�tulos finalizados.
	 *
	 * 
	 */
	private String adicionarMaterialInformacional = "false";
	
	
	
	
	/** Usado nas p�ginas do campo de controle para saber se est�o sendo digitados os campos pela
	 * primeira vez (o bot�o pr�ximo vai estar habilitado), ou se est� editando esses campos que
	 * j� foram criados (ai vai existir outro bot�o que vai redirecionar direto para a p�gina de
	 * cataloga��o). 
	 */
	private boolean editandoCamposDeControle = false;
	/** Usado nas p�ginas do campo de controle para saber se est�o sendo digitados os campos pela
	 * primeira vez (o bot�o pr�ximo vai estar habilitado), ou se est� editando esses campos que
	 * j� foram criados (ai vai existir outro bot�o que vai redirecionar direto para a p�gina de
	 * cataloga��o). 
	 */
	private boolean adicionandoCamposDeControle = false;
	/** errosValidacaoTeladeControle indica que ocorreram erros de valida��o, para deixar os mesmos
	 * dados que o usu�rio digitou na tela. Impedir que ele recarregue os valores padr�o do banco novamente.
	 * */
	private boolean errosValidacaoTeladeControle = false;
	
	
	/** Usado para saber a posi��o do campo de controle que foi selecionada para edi��o.
	 * N�o dava para pegar s� pela tag pois alguns campos de controle podem se repetir. */
	private int posicaoCampoControleSelecionado  = -1;
	
	
	
	////////////////  DADOS UTILIZADOS QUANDO SE EST� CATALOGANDO USANDO UMA PLANILHA  /////////////
	
	/**  O id da planilha escolhida a partir da qual os dados da cataloga��o v�o ser montados */
	private int idPlanilhaEscolhida = -1;
	
	/**  Guarda os dados que vieram da planilha temporariamente*/
	private TituloCatalografico tituloTemp;
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/** Usa APENAS quanto o usu�rio seleciona o campo 007, pois l� os valores padr�o que v�o aparecer
	 * na tela dependem da categoria do material, n�o do formato do material como os campos L�der, 006 e 008 */
	private String codigoCategoriaMaterial;
	
	
	/** Essa vari�vel diz se est� editando ou salvando o t�tulo, porque vai ser a mesma p�gina, 
	 * a diferen�a � que vai habilitar um bot�o diferente que apenas atualiza o T�tulo na base */
	private boolean editando = false;
	
	
	/** Essa vari�vel indica que a cataloga��o sem tombamento. O usu�rio vai poder adicionar materiais   
	 * n�o tombados ao T�tulo.                                                                          */
	private boolean catalogacaoMateriaisSemTombamento = false;
	
	/** Indica se a cataloga��o est� sendo feita a partir de uma tese ou monografia. */
	private boolean catalogacaoDeDefesa = false;
	
	
	
	///////////// Informa��es que vem do SIPAC usado na cataloga��o por tombamento  //////////////
	
	/** Dtos com as informa��es dos bens tombados para os materiais que v�o ser catalogados */
	private InformacoesTombamentoMateriaisDTO infoTituloCompra;
	
	/**  N�mero de patrim�nio digitado pelo usu�rio para ser busca no sistema de patrim�nio*/
	private Long numeroPatrimonio;
	
	/** Interface de busca no patrim�nio */
	@Resource(name = "bibliotecaComprasInvoker")
	private BibliotecaComprasRemoteService controller;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/** o arquivo da obra digitalizada */
	private UploadedFile arquivo;  
	
	/** Na edi��o de t�tulos, indica que o arquivo digital que estava salvo deve ser apagado.
	 * Isso ocorre quando o usu�rio submete outro aquivo. */
	private boolean apagarArquivoDigitalSalvo = false;
	
	
	/** Guarda a ajuda dos campos em formato HTML para serem mostrados mas p�ginas para o usu�rio. */
	private String ajudaCampo = "";
	
	
	/** Guarda o campo e sub campo que o usu�rio selecionou na busca da entra autorizada     
	 * na volta da pesquisa � preciso colocar todos os subcampo do campo selecionado da autoridade
	 * no T�tulo e impedir a entrada autorizada nesse sub campo  */
	private CampoDados campoSelecionadoParaDadosAutoridade;
	
	
	/** flag que indica se o usu�rio clicou no bot�o voltar da tela de edi��o do campo l�der.*/
	private boolean voltandoPaginaLider = false;
	/** flag que indica se o usu�rio clicou no bot�o voltar da tela de edi��o do campo 008.*/
	private boolean voltandoPagina008 = false;
	
	/** Usado nos bot�es das p�ginas de edi��o dos campo de controle para indicar que o usu�rio
	 * s� quer voltar para a p�gina de cataloga��o, n�o vai adicionar nem editar o campo de controle. */
	private String voltarPaginaCatalogacao = "false";
	
	
	/** Usados na p�gina de cataloga��o para habilitar o bot�o "voltar" correto. */
	private boolean catalogacaoDoZero = false;
	/** Usados na p�gina de cataloga��o para habilitar o bot�o "voltar" correto. */
	private boolean catalogacaoImportacao = false;
	/** Usados na p�gina de cataloga��o para habilitar o bot�o "voltar" correto. */
	private boolean catalogacaoPlanilha = false;
	/** Usados na p�gina de cataloga��o para habilitar o bot�o "voltar" correto. */
	private boolean catalogacaoDuplicacao = false;
	
	
	
	/**
	 * Guarda a lista de categorias de material para n�o ficar buscando toda a vida que for
	 * entrar na p�gina. Como n�o existem cadastro para isso, dificilmente essa informa��o mudar�.
	 */
	private List<CategoriaMaterial> listaCategoriaMaterial = null;
	
	
	/** Usando quando o usu�rio vai utilizar a busca das editoras cadastradas no SIPAC
	 * Guarda o nome a editora escolhida e os �ndices do sub campo 260$b selecionado onde deve
	 * ser colocado o valor da editora selecionada */
	private String nomeEditora;
	
	/**
	 *  Guarda �ndices do campo selecionado. <br/>
	 *  Usado na busca cutter 090$b e na busca de editoras 260$b */
	int indiceCampoSelecionado = -1;
	
	/**
	 *  Guarda �ndices do sub campo selecionado. <br/>
	 *  Usado na busca cutter 090$b e na busca de editoras 260$b
	 */
	int indiceSubCampoSelecionado = -1;
	
	
	/**
	 * Guarda temporariamente as sugest�es encontradas na tabela cutter para serem mostras ao usu�rio
	 * e ele escolher qual o mais adequado.
	 */
	private List<DadosTabelaCutter> suguestoesTabelaCuter;
	
	/**
	 * Tempo que a cataloga��o vai ser automaticamente salva pelo sistema. Esse valor � um par�metro do sistema.
	 */
	private int tempoSalvamentoCatalogacacao = 0;
	
	/**
	 * Guardas as grandes �reas de conhecimento CNPq para associar a um T�tulo.
	 */
	private List<AreaConhecimentoCnpq> grandesAreasCNPq;
	
	/**
	 * Guarda o id do material selecionado na rich:tree para ser editado
	 */
	private Integer idMaterialSelecionadoEdicao;
	
	/** Flag que indica se o usu�rio deseja que o painel lateral seja exibido. O painel lateral cont�m informa��o como as classifica��es e materiais da cataloga��o.
	 * Por�m diminui a �rea para digitar o texto. */
	private boolean exibirPainelLateral = true;
	
	
	
	/** Indica se a tela de cataloga��o a ser usado � a completa ou a simplificado. 
	 *  Por padr�o vai ser a completa, mas essa configura��o vai ser salva para cada pessoa.
	 */
	private boolean usarTelaCatalogacaoCompleta = true;
	
	/** Cont�m as configura��es para o usu�rio para a tela de cataloga��o.  
	 * Personaliza��es que cada usu�rio pode fazer da maneira que gosta de catalogar.*/
	private ConfiguracoesTelaCatalogacao configuracoesTela;
	
	
	/** O id da planilha de cataloga��o simplificada escolhida pelo usu�rio. � a partir dessa planilha que a tela de cataloga��o 
	 * simplificada � montada. Os detalhes t�cnicos do MARC como campos de controle, indicadores, c�digos de sub campos s�o t
	 * odos retirados dessa planilha, simplificando a cataloga��o para o usu�rio.*/
	private int idPlanilhaCatalogacaoSimplificada = -1;
	
	/** Cont�m a planilha de cataloga��o simplificada utilizada para mantar a tela de cataloga��o simplificada*/
	private PlanilhaCatalogacao planilhaCatalogacaoSimplificada = null;
	
	/**
	 * Construtor padr�o (Chamado sempre que tiver um keep alive na p�gina por isso n�o � recomendado colocar c�digo aqui)
	 */
	public CatalogacaoMBean() {
		obj = new TituloCatalografico();
	}
	
	
	/**
	 * Inicia o dados que s�o contantes e que devem permanecer em mem�ria durante o processo de cataloga��o. 
	 * Deve ser chamado SEMPRE ao iniciar caso de uso de cataloga��o.
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public void configuraDadosInicializacaoCatalogacao() throws DAOException{
		
		
		tempoSalvamentoCatalogacacao = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.TEMPO_SALVAMENTO_AUTOMATICO_CATALOGACAO);
		
		idPlanilhaCatalogacaoSimplificada = -1; // para carregar os dados corretamente o usu�rio deve primeiro escolher uma planilha
		
		GenericDAO daoGenerico = null;
		
		try{
			daoGenerico = getGenericDAO();
			
			// S� existe 1 por pessoa.
			configuracoesTela =  daoGenerico.findByExactField(ConfiguracoesTelaCatalogacao.class, "pessoa.id", getUsuarioLogado().getPessoa().getId(), true);
			
			if(configuracoesTela != null){ // se o usu�rio salvou alguma configura��o, altera os valores padr�es.
				this.exibirPainelLateral = configuracoesTela.isExibirPainelLateral();
				this.usarTelaCatalogacaoCompleta = configuracoesTela.isUsarTelaCatalogacaoCompleta();
			}else{
				// cria um configura��o com os valores padr�es para se o usu�rio desejar salvar no banco.
				configuracoesTela = new ConfiguracoesTelaCatalogacao(getUsuarioLogado().getPessoa(), this.exibirPainelLateral, this.usarTelaCatalogacaoCompleta);
			}
			
		}finally{
			if(daoGenerico != null) daoGenerico.close();
		}
		
		
		inicializaEtiquetasPersistidas();
		
		
		if(isTipoCatalogacaoBibliografica() ){
		
			// CONFIGURA AS CLASSIFICA��ES //
			
			if ( classificacoesUtilizadas == null || classificacoesUtilizadas.size() == 0 ){
				ClassificacaoBibliograficaDao dao = null;
				try{
					 dao = getDAO(ClassificacaoBibliograficaDao.class);
					 classificacoesUtilizadas = dao.findAllClassificacoesParaValidacao();
				}finally{
					if(dao != null) dao.close();
				}
			} 
			
			/////////// INICIA OS DADOS TRANSIENTES DO TIULO //////////
			
			if( obj.getIdDadosDefesa() != null && obj.getIdDadosDefesa() > 0 ){
				DadosDefesaDao dadosDefesaDao = null;
				
				try{
					dadosDefesaDao = getDAO(DadosDefesaDao.class);
					if(obj.getIdDadosDefesa() != null){ 
						obj.setDadosDefesa(   dadosDefesaDao.findTituloNomeDiscente(obj.getIdDadosDefesa())   );  
					}
					
				}finally{
					if(dadosDefesaDao != null) dadosDefesaDao.close();
				}
			}
			
			if( obj.getId() > 0 ){
			
				AreaConhecimentoCNPqBibliotecaDao areaDao = null;
				
				try{
					areaDao = getDAO(AreaConhecimentoCNPqBibliotecaDao.class);
					
					obj.setAreaConhecimentoCNPQClassificacao1(areaDao.findDadosGrandeAreaCNPqBibliotecaComProjecao(obj.getAreaConhecimentoCNPQClassificacao1().getId()));
					obj.setAreaConhecimentoCNPQClassificacao2(areaDao.findDadosGrandeAreaCNPqBibliotecaComProjecao(obj.getAreaConhecimentoCNPQClassificacao2().getId()));
					obj.setAreaConhecimentoCNPQClassificacao3(areaDao.findDadosGrandeAreaCNPqBibliotecaComProjecao(obj.getAreaConhecimentoCNPQClassificacao3().getId()));
				
				}finally{
					if(areaDao != null) areaDao.close();
				}
				
				// CONFIGURA A �RVORE DE MATERIAIS //
				
				rootNode = new TreeNodeImpl<String>();
				visualizaListaMateriaisTitulo = false;
				
				MaterialInformacionalDao dao = null;
				try {
					dao = getDAO(MaterialInformacionalDao.class);
				
					quantidadeMateriaisTitulo = dao.countMateriaisAtivosByTitulo(obj.getId());
					
					if(quantidadeMateriaisTitulo <= 20){
						mostrarListaMateriaisTitulo(null); // At� vinte materiais, mostra por padr�o, maior que isso s� se o usu�rio solicitar
					}
					
				} finally {
					if (dao != null)dao.close();
				}
			}
				
		} // isTipoCatalogacaoBibliografica() && obj.getId() > 0
		
	}
	
	
	/**
	 * Inicia o caso de uso catalogar um T�tulo.
	 * Aquele onde o usu�rio escolhe todos os dados do T�tulo.
	 * 
	 * @return
	 * @throws ArqException
	 *
	 *  <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecncios/pesquisas_acervo/pesquisaTituloCatalografico.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		
		tipoCatagalocao = TipoCatalogacao.BIBLIOGRAFICA;
		
		zeraTiposCatalogacao();
		
		
		catalogacaoDoZero = true;
		
		obj = new TituloCatalografico();
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
		editando = false;
		
		//CatalogacaoUtil.IncluiCampoLOC(obj);
		
		configuraDadosInicializacaoCatalogacao();
		ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacoesUtilizadas.toArray( new ClassificacaoBibliografica[classificacoesUtilizadas.size()]) );
		
		return  telaEscolheFormatoMaterial();
	}
	
	
	
	
	/**
	 *       M�todo que inicia o caso de uso j� na tela da cataloga��o com os dados
	 *  vindos da importa��o.
	 * 
	 *      <br/><br/>
	 *      Chamado do Mbean: {@link CooperacaoTecnicaImportacaoMBean#realizarInterpretacaoDados()}
	 *      M�todo n�o invocado por nenhuma JSP.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarImportacao(TituloCatalografico obj) throws ArqException{
		
		tipoCatagalocao = TipoCatalogacao.BIBLIOGRAFICA;
		
		zeraTiposCatalogacao();
		
		
		catalogacaoImportacao = true;
		 
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
		
		this.obj = obj;

		if(obj.getCamposDados() != null){	
			// S� pode ordenar 1 vez sen�o o dataModel n�o funciona bem
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
			
		    for (CampoDados dados : camposNaoReservados) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
		    
		}else{
			dataModelCampos = new ListDataModel();
		}
	    
	    editando = false;
	   
	    configuraDadosInicializacaoCatalogacao();
		ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacoesUtilizadas.toArray( new ClassificacaoBibliografica[classificacoesUtilizadas.size()]));
		
		// Enquanto o formato de material estiver nulo redireciona para escolher o formato
		if(obj.getFormatoMaterial() == null){
			getCurrentRequest().setAttribute("escolhendoFormatoMaterialTituloSemFormato", true);
			return telaEscolheFormatoMaterial();
		}
		
		
		return telaDadosTituloCatalografico();
	}
	
	
	
	/**
	 *      <p> M�todo que inicia o caso de uso j� na tela da cataloga��o com os dados
	 *  vindos da duplica��o. </p>
	 * 
	 *      <br/><br/>
	 *  	Chamado do Mbean: {@link PesquisaTituloCatalograficoMBean#duplicarTituloCatalografico()}
	 *     <br/>
	 *      M�todo n�o invocado por nenhuma JSP
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarDuplicacao(TituloCatalografico obj) throws ArqException{
		
		tipoCatagalocao = TipoCatalogacao.BIBLIOGRAFICA;
		
		zeraTiposCatalogacao();
		
		
		catalogacaoDuplicacao = true;
		 
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
		
		this.obj = obj;

		CatalogacaoUtil.incluiCampoDadosLOC(obj, true);
		
		if(obj.getCamposDados() != null){
			// S� pode ordenar 1 vez sen�o o dataModel n�o funciona bem
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // inicia os sub campos
		    for (CampoDados dados : camposNaoReservados) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
		    
		}else{
			dataModelCampos = new ListDataModel();
		}
	    
	    editando = false;
	    
	    configuraDadosInicializacaoCatalogacao();
	    ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacoesUtilizadas.toArray( new ClassificacaoBibliografica[classificacoesUtilizadas.size()]) );
	    
		// Enquanto o formato de material estiver nulo redireciona para escolher o formato
		if(obj.getFormatoMaterial() == null){
			getCurrentRequest().setAttribute("escolhendoFormatoMaterialTituloSemFormato", true);
			return telaEscolheFormatoMaterial();
		}
		
		return telaDadosTituloCatalografico();
	}
	
	
	
	/**
	 *    M�todo chamado da p�gina na qual o usu�rio escolhe a planilha. Os dados da planilha
	 * escolhida v�o estar em tituloTemp, a� joga no t�tulo verdadeiro e chama a p�gina de cataloga��o.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecncios/catalogacao/escolhePlanilhabibliografica.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarPlanilha() throws ArqException{
		
		tipoCatagalocao = TipoCatalogacao.BIBLIOGRAFICA;
		
		zeraTiposCatalogacao();
		
		
		catalogacaoPlanilha = true;
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if(isCatalogacaoComTombamento())
			CatalogacaoUtil.mergeInformacoesPlanilhaECompra(infoTituloCompra, tituloTemp);
		
		
		this.obj = CatalogacaoUtil.duplicarTitulo(tituloTemp);
		
		
		// Deve ordenar antes de ser inclu�do no dataModel
		
		if( obj.getCamposDados() != null ){
			CatalogacaoUtil.incluiCampoDadosLOC(obj, true);
			
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar ordena os sub campos depois fica do jeito que o usu�rio digitou
		    for (CampoDados dados : camposNaoReservados) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
	    
		}else{
			
			CatalogacaoUtil.criaCamposDeDadosIniciais(obj, true);
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		}
	    
	    editando = false;
	    
	    prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
	    
		
		// Enquanto o formato de material estiver nulo redireciona para escolher o formato
		if(obj.getFormatoMaterial() == null){
			getCurrentRequest().setAttribute("escolhendoFormatoMaterialTituloSemFormato", true);
			return telaEscolheFormatoMaterial();
		}
		
		configuraDadosInicializacaoCatalogacao();
		ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacoesUtilizadas.toArray( new ClassificacaoBibliografica[classificacoesUtilizadas.size()]) );
		
		// Se o usu�rio come�ou a cataloga��o por planilha e est� utilizado a cataloga��o simplificada 
		// ent�o j� marca para planilha da cataloga��o simplificada como sendo a planilha escolhida
		if(! isUsarTelaCatalogacaoCompleta()){
			this.idPlanilhaCatalogacaoSimplificada = this.idPlanilhaEscolhida;
			alterouPlanilhaCatalogacao(null);
		}
			
		return telaDadosTituloCatalografico();
	}
	
	
	
	/**
	 *     <p>Inicia o caso de uso de cataloga��o a partir da sele��o de uma defesa,
	 * pr�-populando os dados da cataloga��o a partir dos dados existentes na
	 * defesa cadastrada. </p>
	 * 
	 * 	<p>Se a defesa j� foi catalogada, ent�o o t�tulo referente a ela � exibido para
	 * edi��o</p>
	 * 
	 * <p>Chamado a partir da p�gina: sigaa.war/stricto/banca_pos/consulta_defesas.jsp<p>
	 */
	public String iniciarDefesa() throws NegocioException, ArqException {
		
		tipoCatagalocao = TipoCatalogacao.BIBLIOGRAFICA;
		
		catalogacaoDeDefesa = true;
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		zeraTiposCatalogacao();
		
		
		int idBancaPos = getParameterInt("idBancaPos", 0);
		BancaPos banca = getDAO(BancaPosDao.class).findByPrimaryKey(idBancaPos, BancaPos.class);
		
		// Verifica se a defesa j� tem um t�tulo catalogado. Se sim, deixa o usu�rio edit�-lo, em vez
		// de criar  um novo.
		
		TituloCatalograficoDao titDao = null;
		
		try{
			titDao = getDAO(TituloCatalograficoDao.class);
			Integer idTitulo = titDao.findTituloReferenteADefesa( banca.getDadosDefesa().getId() );
			
			if ( idTitulo != null ) {
				addMensagemInformation("A defesa escolhida j� tem um t�tulo catalogado. " +
						"Ele foi aberto para que voc� possa verificar e fazer altera��es, se necess�rio.");
				
				prepareMovimento(SigaaListaComando.ATUALIZA_TITULO);
				
				// recupera todos dados do t�tulo, j� que ele vai ser editado
				obj = titDao.findTituloByIdInicializandoDados( idTitulo );
				
				return iniciarParaEdicao(obj);
			}
			
			// Se a defesa ainda n�o foi catalogada, cria um novo t�tulo
			
			obj = new TituloCatalografico();
			obj.setFormatoMaterial( new FormatoMaterial(ParametroHelper.getInstance().getParametroInt(
					ParametrosBiblioteca.FORMATO_MATERIAL_LIVRO)) );
			getGenericDAO().initialize(obj.getFormatoMaterial());
			
			// Monta campos LIDER e 008 com valores padr�es.
			
			Etiqueta etiquetaLider = getDAO(EtiquetaDao.class).findEtiquetaInicializandoDados(
					Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO);
			new CampoControle(CatalogacaoUtil.montaDadosCampoControle (
					getValoresPadraoLider( etiquetaLider), etiquetaLider ), etiquetaLider, -1, obj);
			
			Etiqueta etiqueta008 = getDAO(EtiquetaDao.class).findEtiquetaInicializandoDados(
					Etiqueta.CAMPO_008_BIBLIOGRAFICO);
			CampoControle campo008 = new CampoControle(CatalogacaoUtil.montaDadosCampoControle(
					getValoresPadrao008(etiqueta008), etiqueta008 ), etiqueta008, -1, obj);
			
			// Local de publica��o (15, 17): bl - Brasil
			campo008.setDado( campo008.getDado().substring(0, 15) + "bl " + campo008.getDado().substring(18) );
			// Natureza do conte�do (24, 27): m - Teses e disserta��es
			campo008.setDado( campo008.getDado().substring(0, 24) + "m   " + campo008.getDado().substring(28) );
			// Forma liter�ria (33): 0 - N�o � uma obra liter�ria
			campo008.setDado( campo008.getDado().substring(0, 33) + "0" + campo008.getDado().substring(34) );
			// Idioma (35, 37): por - portugu�s
			campo008.setDado( campo008.getDado().substring(0, 35) + "por" + campo008.getDado().substring(38) );
			
			CatalogacaoUtil.criarTituloCatalograficoAPartirDeDefesa(banca, obj);
	
			CatalogacaoUtil.incluiCampoDadosLOC(obj, true);
			
			// Deve ordenar antes de ser inclu�do no data model //
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
		   
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar ordena os sub campos depois fica do jeito que o usu�rio digitou
		    for (CampoDados dados : obj.getCamposDados()) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
		    
		    editando = false;
		    
			prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
			
			configuraDadosInicializacaoCatalogacao();
			ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacoesUtilizadas.toArray( new ClassificacaoBibliografica[classificacoesUtilizadas.size()]) );
			
			// Enquanto o formato de material estiver nulo redireciona para escolher o formato
			if (obj.getFormatoMaterial() == null) {
				getCurrentRequest().setAttribute("escolhendoFormatoMaterialTituloSemFormato", true);
				return telaEscolheFormatoMaterial();
			}
			
			
			
		}finally{
			if(titDao != null) titDao.close();
		}
		
		return telaDadosTituloCatalografico();
	}
	
	
	
	
	
	
	/**
	 *    M�todo que inicia o caso de uso de edi��o de T�tulos.
	 * 
	 *      <br/><br/>
	 *    Chamado a partir de: {@link PesquisaTituloCatalograficoMBean#editarTitulo}
	 *    <br/>
	 *    M�todo n�o invocado por nenhuma JSP.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarParaEdicao(TituloCatalografico obj) throws ArqException{
		
		tipoCatagalocao = TipoCatalogacao.BIBLIOGRAFICA;
		
		zeraTiposCatalogacao();
		
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.ATUALIZA_TITULO);
		
		this.obj = obj;
		
		if(obj.getCamposDados() != null){
		
			EtiquetaDao dao = null;
			
			try{
				dao = getDAO(EtiquetaDao.class);
				
				for (CampoDados c : obj.getCamposDados()) {
					
					if(c == null)
						continue;
					
					if(! c.getEtiqueta().isAtiva()){  // se a etiqueta do campo n�o est� mais ativa
						
						Etiqueta e =  dao.findEtiquetaPorTagETipoAtivaInicializandoDados(
								c.getEtiqueta().getTag(), c.getEtiqueta().getTipo());
						
						if(e != null){ // criaram a mesma etiqueta novamente, ent�o atribui ao campo
							c.setEtiqueta(e);
							
							if(! etiquetasBuscadas.contains(e))  // Otimiza as busca das etiquetas
								etiquetasBuscadas.add(e);
						}
						// ELSE fica com a etiqueta desativa que j� possu�a e o sistema n�o vai deixar
						// salvar o t�tulo com a etiqueta desativada
						
					}
				}
			
			}finally{
				if(dao != null ) dao.close();
			}
			
			// Deve ordenar antes de ser inclu�do no dataModel
			Collections.sort(obj.getCamposDadosNaoReservados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar, ordena os sub campos depois ajusta conforme o usu�rio digitou.
		    for (CampoDados dados : camposNaoReservados) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
	   
		}
	    
		editando = true;
		
		
		configuraDadosInicializacaoCatalogacao();
		
		// Enquanto o formato de material estiver nulo, redireciona para escolher o formato
		if(obj.getFormatoMaterial() == null){
			getCurrentRequest().setAttribute("escolhendoFormatoMaterialTituloSemFormato", true);
			return telaEscolheFormatoMaterial();
		}
		
		
		return telaDadosTituloCatalografico();
	}
	
	
	
	
	/**
	 * 
	 * Inicia o caso de cadastro de autoridades onde o usu�rio informa todos os dados.
	 *
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaAutoridades.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAutoridades() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		zeraTiposCatalogacao();
		
		
		catalogacaoDoZero = true;
		
		tipoCatagalocao = TipoCatalogacao.AUTORIDADE;
		
		etiquetaControleAtual = Etiqueta.CAMPO_LIDER_AUTORIDADE;
		
		prepareMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
		
		montaDadosAjudaControle(etiquetaControleAtual);
		
		editando = false;
		
		configuraDadosInicializacaoCatalogacao();
		
		return telaCampoControleLider();
		
	}
	
	
	
	/**
	 * Inicia o caso de cadastro de autoridades onde o usu�rio utiliza alguma planilha de
	 *   cataloga��o de autoridades previamente cadastrada no sistema.<br/>
	 * 
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaAutoridades.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAutoridadesComPlanilha() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		zeraTiposCatalogacao();
		
		
		catalogacaoPlanilha = true;
		
		tipoCatagalocao = TipoCatalogacao.AUTORIDADE;
		
		this.obj = CatalogacaoUtil.duplicarTitulo(tituloTemp);

		CatalogacaoUtil.incluiCampoDadosLOC(obj, false);
		
		if(obj.getCamposDados() != null){
		
			// Deve ordenar antes de ser inclu�do no dataModel
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar ordena os sub campos depois fica do jeito que o usu�rio digitou
		    for (CampoDados dados : camposNaoReservados) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
	    
		}else{
			dataModelCampos = new ListDataModel();
		}
	    
	    prepareMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
		
		editando = false;
		
		configuraDadosInicializacaoCatalogacao();
		
		// Se o usu�rio come�ou a cataloga��o por planilha e est� utilizado a cataloga��o simplificada 
		// ent�o j� marca para planilha da cataloga��o simplificada como sendo a planilha escolhida
		if(! isUsarTelaCatalogacaoCompleta()){
			this.idPlanilhaCatalogacaoSimplificada = this.idPlanilhaEscolhida;
			alterouPlanilhaCatalogacao(null);
		}
		
		return telaDadosTituloCatalografico();
		
	}
	
	
	/**
	 * 
	 * Inicia o caso de cadastro de uma nova autoridade que j� possua dados.
	 *
	 *       <br/><br/>
	 *     Chamado a partir do MBean: {@link CooperacaoTecnicaImportacaoMBean#realizarInterpretacaoDados}
	 *     <br/>
	 *     M�todo n�o invocado por nenhuma JSP.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAutoridadesDuplicacao(TituloCatalografico obj) throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		zeraTiposCatalogacao();

		
		prepareMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
		
		this.obj = obj;
		
		tipoCatagalocao = TipoCatalogacao.AUTORIDADE;
		
		editando = false;
		catalogacaoDuplicacao = true;

		CatalogacaoUtil.incluiCampoDadosLOC(obj, false);
		
		// Deve ordenar antes de ser colocado no dataModel
		if(obj.getCamposDados() != null){
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
		
			atribuiCamposDoTituloAosCampoUsadosNaTela();
	    	
		    // ao iniciar ordenar os sub campos depois fica do jeito que o usu�rio digitou
	    	for (CampoDados dados : camposNaoReservados) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
		}else{
			dataModelCampos = new ListDataModel();
		}
		
		configuraDadosInicializacaoCatalogacao();
		
		return telaDadosTituloCatalografico();
		
	}
	
	
	
	/**
	 * 
	 *    Inicia o caso de cadastro de autoridades com dados vindo do arquivo importado.
	 *
	 *     Pode ser usado tamb�m para iniciar a cataloga��o de uma autoridade que j� tenha dados
	 *  passando importa��o = false
	 *
	 *      <br/><br/>
	 *     Chamado a partir do MBean: {@link CooperacaoTecnicaImportacaoMBean#realizarInterpretacaoDados}
	 *     <br/>
	 *     M�todo n�o invocado por nenhuma JSP
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAutoridadesImportacao(TituloCatalografico obj) throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		zeraTiposCatalogacao();

		
		prepareMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
		
		this.obj = obj;
		
		tipoCatagalocao = TipoCatalogacao.AUTORIDADE;
		
		editando = false;
		catalogacaoImportacao = true;

		// Deve ordenar antes de ser colocado no dataModel
		if(obj.getCamposDados() != null){
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
		
			atribuiCamposDoTituloAosCampoUsadosNaTela();
	    	
		    // ao iniciar ordena os sub campos depois fica do jeito que o usu�rio digitou
		    for (CampoDados dados : camposNaoReservados) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
		}else{
			dataModelCampos = new ListDataModel();
		}
	    
		configuraDadosInicializacaoCatalogacao();
		
		return telaDadosTituloCatalografico();
		
	}
	
	
	
	
	/**
	 *    Inicia o caso de editar uma autoridade.
	 *
	 *   OBS.: Recebe um objeto TituloCatalografico porque o MBean inteiro trabalha com um objeto
	 *   do tipo TituloCatalografico. Na hora de salvar a autoridade, os campos de dados e controle
	 *   do objeto TituloCatalografico s�o copiados para um objeto do tipo Autoridade.
	 *
	 *     <br/><br/>
	 *   Chamado a partir do MBean: {@link CatalogaAutoridadesMBean#editarAutoridade}
	 *   <br/>
	 *   M�todo n�o invocado por nenhuma JSP
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarParaEdicaoAutoridades(TituloCatalografico titulo) throws ArqException{
		
		zeraTiposCatalogacao();
	
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.ATUALIZA_AUTORIDADE);
		
		tipoCatagalocao = TipoCatalogacao.AUTORIDADE;
		
		this.obj = titulo;
		
		if(obj.getCamposDados() != null){
			
			EtiquetaDao dao = null;
			
			try{
				dao = getDAO(EtiquetaDao.class);
			
				for (CampoDados c : obj.getCamposDados()) {
					
					if(! c.getEtiqueta().isAtiva()){  // se a etiqueta do campo n�o est� mais ativa
						
						Etiqueta e = dao.findEtiquetaPorTagETipoAtiva(c.getEtiqueta().getTag(),
								c.getEtiqueta().getTipo());
						
						if(e != null){ // criaram a mesma etiqueta novamente, ent�o atribui ao campo
							c.setEtiqueta(e);
						}
						// ELSE fica com a etiqueta desativa que j� possu�a e o sistema n�o vai deixar
						// salvar o t�tulo com a etiqueta desativada
						
					}
				}
			
			}finally{
				if(dao != null) dao.close();
			}
			
			// Deve ser ordenado antes de ser colocado no dataModel
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar ordena os sub campos depois ajusta conforme o usu�rio digitou.
		    for (CampoDados dados : camposNaoReservados) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
	    
		}else{
			 dataModelCampos = new ListDataModel();
		}
	    
		editando = true;
		
		configuraDadosInicializacaoCatalogacao();
		
		return telaDadosTituloCatalografico();
	}
	
	
	/**
	 * <p>M�todo que deve ser chando quando se est� editando algum T�tulo/Autoridade que j� tenha 
	 * algum campo de dados persistido.</p>
	 * 
	 * <p>Porque caso 2 ou mais campos possuam a mesma etiqueta � recuperado o mesmo 
	 * objeto do banco, ent�o ao alterar a "tag" de uma etiqueta no formul�rio o sistema automaticamente 
	 * altera as outras etiquetas com a mesma "tag".</p>
	 * 
	 * <p>Esse erro n�o ocorria com novos campos adicionados porque sempre s�o retornados objetos 
	 * etiquetas c�pias das originais. Esse m�todo percorre todas os campos de dados e substitui as 
	 * etiquetas por novos objetos c�pias do objeto recuperado.</p>
	 * 
	 * 
	 *   <p>Deve ser chamado ainda de iniciar qualquer altera��o.</p>
	 *
	 * @throws DAOException
	 */
	private void inicializaEtiquetasPersistidas() throws DAOException{
		
		if(camposNaoReservados != null)
		for(CampoDados cd : camposNaoReservados){
			cd.setEtiqueta( recuperaEtiquetaTitulo(cd.getEtiqueta(), etiquetasBuscadas) );
		}
	}
	
	
	
	/**
	 *    M�todo chamado quando o usu�rio digitar algo nos campos de classifica��o bibliografica(080...089)
	 *
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException 
	 */
	public void configurarClassificacoes(ActionEvent e) throws DAOException{
		
		CampoDados c = (CampoDados) dataModelCampos.getRowData();
		
		if(c != null && c.getEtiqueta() != null && c.getEtiqueta().getTag() != null) {
			
			ClassificacaoBibliografica classificacaoAlteracao = ClassificacoesBibliograficasUtil
				.encontraClassificacaoByCampo(classificacoesUtilizadas, new String[]{ c.getEtiqueta().getTag() } );
		
			ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacaoAlteracao);
			
		}
		
	}
	
	
	/**
	 * 
	 * Associa ou altera a associa��o da cataloga��o que est� sendo catalogada com uma defesa do sistema.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/stricto/banca_pos/resultado_ordem_discente.jsp</li>
	 *   <li>/sigaa.war/stricto/banca_pos/resultado_ordem_data.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String associarCatalogacaoDefesa() throws DAOException{
		
		int idBancaPos = getParameterInt("idBancaPos", 0);
		
		BancaPosDao bancaDao = null;
		
		try{
			bancaDao = getDAO(BancaPosDao.class);
			BancaPos banca = bancaDao.findByPrimaryKey(idBancaPos, BancaPos.class);
			
			obj.setIdDadosDefesa(banca.getDadosDefesa().getId() );
			obj.setDadosDefesa( banca.getDadosDefesa());
			
			
			addMensagemWarning(" A simples associa��o n�o preenche nem altera os dados da cataloga��o. Para preencher os " +
					" dados da cataloga��o com os dados de uma defesa, utilize a op��o de iniciar a cataloga��o a partir de uma defesa.");
			addMensagemInformation("Associa��o realizada com sucesso. ");
			
			return telaDadosTituloCatalografico();
		}finally{
			if(bancaDao != null) bancaDao.close();
		}
		
		
	}
	
	
	/**
	 * 
	 * Remove a associa��o da cataloga��o com a defese de tese ou disserta��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String removerAssociadoDefesa() throws DAOException{
		obj.setIdDadosDefesa(null);
		obj.setDadosDefesa(null);
		return telaDadosTituloCatalografico();
	}
	
	
	
	
	/**
	 * Zera os outros tipo antes de come�ar um novo tipo de cataloga��o.
	 */
	private void zeraTiposCatalogacao(){
		catalogacaoDoZero = false;
		catalogacaoImportacao = false;
		catalogacaoPlanilha = false;
		catalogacaoDuplicacao = false;
	}
	
	
	
	
	
	/**
	 *    M�todo chamado quando o usu�rio deseja ordenar os campos de dados MARC da p�gina de cataloga��o.
	 *
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 */
	public void ordenaCamposDados(ActionEvent evt){
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		CatalogacaoUtil.ordenaCampoDados(obj);
		 
		if(obj.getCamposDados() != null){
			camposNaoReservados = obj.getCamposDadosNaoReservados();
			camposReservados = obj.getCamposDadosReservados();
			dataModelCampos = new ListDataModel(camposNaoReservados);
		}
		
	}
	
	
	
	/**
	 *    M�todo chamado quando o usu�rio deseja validar as informa��es MARC de um t�tulo ou autoridade mas
	 * sem precisar salv�-la no banco.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public void validaInformacoesMARCCampos(ActionEvent evt) throws DAOException{
		
		ListaMensagens lista = new ListaMensagens();
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		try {
			
			if(isTipoCatalogacaoBibliografica())
				CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcTitulo(obj, obj.getFormatoMaterial(), lista);
			if(isTipoCatalogacaoAutoridade()){
				Autoridade autoridade = CatalogacaoUtil.criaAutoridaeAPartirTitulo(obj);
				CatalogacaoValidatorFactory.getRegrasValidacao().validaCamposMarcAutoridade(autoridade, lista);
			}
			
		} catch (NegocioException e1) {
			addMensagens(e1.getListaMensagens());
		}
		
		if (lista.size() > 0)
			addMensagensAjax(lista);
		else{
			if(isTipoCatalogacaoBibliografica())
				addMensagemInfoAjax("Informa��es MARC do T�tulo est�o corretas.");
			if(isTipoCatalogacaoAutoridade())
				addMensagemInfoAjax("Informa��es MARC da Autoridade est�o corretas.");
		}
		
	}
	
	
	
	
	/**
	 *   M�todo chamado quando o usu�rio deseja remover os campos vazios que n�o ser�o preenchidos.
	 *
	 *  <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 *
	 * @return
	 */
	public void removerCamposVazios(ActionEvent evt){
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		CatalogacaoUtil.retiraCamposDadosVazios(obj);
		CatalogacaoUtil.configuraGruposEtiquetaVisiveis(obj);
		
		atribuiCamposDoTituloAosCampoUsadosNaTela();
		
	}
	
	
	
	
	
	/**
	 * 
	 *    M�todo que inicia a cataloga��o na qual podem ser inclu�dos materiais que n�o est�o tombados
	 * no patrim�nio da Institui��o.
	 * 
	 *    Esse caso de uso � uma exce��o para contemplar exemplares e assinaturas antigas que n�o
	 * foram tombados no sistema.
	 * 
	 *      <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 *
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String preparaCatalogacaoSemTombamento() throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		///////////////////////////////////////////////////////////////////////////////////////////////
		// Seta a informa��o que a cataloga��o � sem tombamento e vai para a tela de busca.          //
		// quando voltar para esse bean por um dos caminhos poss�vel j� vai ser saber qual o tipo de //
		// cataloga��o.                                                                              //
		///////////////////////////////////////////////////////////////////////////////////////////////
		infoTituloCompra = null;
		catalogacaoMateriaisSemTombamento = true;
		
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		bean.setPesquisaTituloParaCatalogacaoComTombamento(true);
		return bean.iniciarPesquisa();
	}
	
	
	
	
	
	/**
	 *    Busca as informa��es dos materiais da biblioteca no SIPAC pelo n�mero de patrim�nio do bem.
	 * 
	 *    Usado no in�cio da cataloga��o quando envolve a cataloga��o de materiais que precisam de
	 * n�mero do tombamento dos bens no SIPAC.
	 * 
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/buscaInformacoesSipacAPartirNumeroPatrimonio.jsp
	 * @param evt
	 * @throws ArqException
	 */
	public String buscaInformacoesSipacAPartirNumeroPatrimonio() throws ArqException{
	
		
		catalogacaoMateriaisSemTombamento = false;
		
		
		//controller = BibliotecaDelegate.getInstance().getController();
		
		try {
			
			if(numeroPatrimonio != null && numeroPatrimonio > 0){
				infoTituloCompra = controller.buscaInformacoesFinanceirasPorNumeroPatrimonio(numeroPatrimonio);
			}else{
				addMensagemWarning("Informe o N�mero do Patrim�nio de um Bem. ");
				return null;
			}
		
		}catch(RemoteAccessException rae){
			rae.printStackTrace();
			notifyError(rae);      // IMPORTANTE :  mandar email para em caso de erro descobrir a causa.
			addMensagemErro("Erro ao tentar busca informa��es no "+ RepositorioDadosInstitucionais.get("siglaSipac") );
			return null;
		}catch (Exception ex) {  // O sistema em produ��o n�o est� conseguindo lan�ar diretamente NegocioRemotoBibliotecaException
			
			if( ex instanceof NegocioRemotoBibliotecaException ){
				
				NegocioRemotoBibliotecaException nre = (NegocioRemotoBibliotecaException) ex;
				
				nre.printStackTrace();
				addMensagemErro( nre.getMessage());
				infoTituloCompra = null;
				return null;
			
			}else{
				if(ex != null && ex.getCause() != null && ex.getCause().getMessage() != null && ex.getCause().getMessage().contains("404")){
					ex.printStackTrace();
					notifyError(ex);      // IMPORTANTE :  mandar email para em caso de erro descobrir a causa.
					addMensagemErro("O "+ RepositorioDadosInstitucionais.get("siglaSipac")+" est� indispon�vel no momento, tente novamente em alguns minutos. " );
					return null;
					
				}else{
					ex.printStackTrace();
					notifyError(ex);      // IMPORTANTE :  mandar email para em caso de erro descobrir a causa.
					addMensagemErro("Erro ao tentar busca informa��es no "+ RepositorioDadosInstitucionais.get("siglaSipac") );
					return null;
				}
			}
		}
		
		if( infoTituloCompra == null ){
			addMensagemErro("Bem com o n�mero do patrim�nio: "+ numeroPatrimonio+" n�o encontrado.");
			infoTituloCompra = null;
			return null;
		}else{
		
			// Com dados de compra sempre � exemplar, nunca assinatura.
			
			CatalogacaoUtil.calculaInformacoesExemplaresNoAcerco(infoTituloCompra);
			
			if(infoTituloCompra.getNumeroMateriaisInformacionaisNaoUsados() == 0
					&& infoTituloCompra.getTitulo() != null
					&& infoTituloCompra.getDescricaoTermoResponsabibliodade() != null){
				if (infoTituloCompra.getTitulo() != null && infoTituloCompra.getDescricaoTermoResponsabibliodade() != null){
					addMensagemWarning("Todos os materiais para o T�tulo \" "+infoTituloCompra.getTitulo()+" \" do Termo de Responsabilidade N� "
							+infoTituloCompra.getDescricaoTermoResponsabibliodade()+" foram inclu�dos no acervo.");
				}
				else{
					addMensagemWarning("Todos os materiais para do T�tulo foram inclu�dos no acervo. N�o foi poss�vel recuperar informa��o do termo de responsabilidade");
				}	
			}
			
		}
		
		return telaBuscaInformacoesSipacAPartirNumeroPatrimonio();
	
	}
	
	
	
	
	/**
	 *   <p>M�todo chamado toda vez que a cataloga��o � realizada a partir de uma compra.</p>
	 *
	 *   <p>Guarda as informa��es dos n�meros de patrim�nio gerados, porque na tela de inclus�o
	 * de exemplares essas informa��es devem vir preenchidas. O usu�rio n�o vai digitar os n�meros
	 * de patrim�nio.</p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/buscaInformacoesSipacAPartirNumeroPatrimonio.jsp
	 * 
	 * @param numerosPatrimonioNaoUsados
	 * @throws ArqException
	 */
	public void configuraInformacoesExemplaresCompra(InformacoesTombamentoMateriaisDTO infoTituloCompra)
			throws ArqException{
		
		prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
		
		this.infoTituloCompra = infoTituloCompra;
		
	}
	
	
	
	/**
	 *     Configura as informa��es do t�tulo com os dados vindos do livro do SIPAC. Usado quando
	 * se est� usando a forma mais "simples" de cataloga��o. Sem usar planilha, importa��o, etc..
	 *
	 * 		 <br/><br/>
	 *      Chamado a partir do MBean: CatalogaAPartirTombamentoMBean
	 *      <br/>
	 *      M�todo n�o invocado por nenhuma JSP.
	 * @param titulo
	 */
	public void configuraInformacoesTitulo(TituloCatalografico titulo){
		this.obj = titulo;
	}
	
	
	
	
	/**
	 *    Chamado quando o usu�rio vai voltar a p�gina de edi��o do campo lider.
	 * 
	 *      <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
	 * 
	 * @throws DAOException
	 */
	public String voltarPaginaLider() throws DAOException{
		
		voltandoPaginaLider = true;
			
		if(isTipoCatalogacaoBibliografica())
			etiquetaControleAtual = Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO;
		if(isTipoCatalogacaoAutoridade())
			etiquetaControleAtual = Etiqueta.CAMPO_LIDER_AUTORIDADE;
		
		montaDadosAjudaControle(etiquetaControleAtual);
		return telaCampoControleLider();
		
	}
	
	
	/**
	 * 
	 * Chamado quando o usu�rio vai voltar a p�gina de edi��o do campo 008.
	 *
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @return
	 * @throws DAOException
	 */
	public String voltarPagina008() throws DAOException{
		
		voltandoPagina008 = true;

		if(isTipoCatalogacaoBibliografica())
			etiquetaControleAtual = Etiqueta.CAMPO_008_BIBLIOGRAFICO;
		if(isTipoCatalogacaoAutoridade())
			etiquetaControleAtual = Etiqueta.CAMPO_008_AUTORIDADE;
		
		montaDadosAjudaControle(etiquetaControleAtual);
		
		return telaCampoControle008();
	}
	
	
	
	/**
	 *      Quando um usu�rio seleciona um formato de material esse m�todo encaminha para o pr�ximo passo
	 * do caso de uso que � a tela de preenchimento do campo lider.
	 * 
	 *    <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolheFormatoMaterial.jsp
	 * @throws ArqException
	 */
	public String submeteFormatoMaterialIniciandoCatalogacao() throws ArqException{
		
		prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
		
		catalogacaoDoZero = true;
		
		int idFormatoMaterial = getParameterInt("idFormatoMaterial");
		obj.setFormatoMaterial( new FormatoMaterial(idFormatoMaterial) );
		
		getGenericDAO().initialize(obj.getFormatoMaterial());
		
		etiquetaControleAtual = Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO; // formato s� tem bibliogr�fica
		
		montaDadosAjudaControle(etiquetaControleAtual);
		
		// se escolher outro formato tem que recarregar as informa��es
		voltandoPaginaLider = false;
		voltandoPagina008 = false;
		
		errosValidacaoTeladeControle = false;
		
		return telaCampoControleLider();
	}
	
	
	
	
	/**
	 *     <p>Esse m�todo � chamado na mesma p�gina que o usu�rio escolhe o formato do material.</p>
	 *     <p>Sendo que nesse caso foi porque se chegou na tela de cataloga��o e o t�tulo n�o possu�a
	 * um formato de material. Seja porque ele foi importado e salvo sem formato ou seja por
	 * causa de algum erro do sistema que chegou at� a tela de cataloga��o sem o formato.</p>
	 *     <p>Esse m�todo s� atribui o formato ao t�tulo.</p>
	 * 
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolheFormatoMaterial.jsp
	 * <br/>
	 * @throws ArqException
	 * 
	 */
	public String submeteFormatoMaterialTituloSemFormato() throws ArqException{
		
		if(editando)
			prepareMovimento(SigaaListaComando.ATUALIZA_TITULO);
		else
			prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
		
		int idFormatoMaterial = getParameterInt("idFormatoMaterial");
		
		//if(obj.getId() > 0 )// O objeto j� tinha sido salvo
		//	obj = getDAO().findByPrimaryKey(obj.getId(), TituloCatalografico.class);
		
		obj.setFormatoMaterial( getGenericDAO().refresh(new FormatoMaterial(idFormatoMaterial)) );
		
		if(obj.getCamposDados() != null){
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
			
		}else
			dataModelCampos = new ListDataModel();
		
		configuraDadosInicializacaoCatalogacao();
		
		ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj,  classificacoesUtilizadas.toArray( new ClassificacaoBibliografica[classificacoesUtilizadas.size()]  ) );
		
		return telaDadosTituloCatalografico();
	}
	
	
	
	
	
	
	
	
	/**
	 *     Pega os valores que est�o na p�gina, copia para a lista "valoresLider", cria um campo de
	 *  controle com esses valores, valida os dados,
	 *  remove algum campo lider que por acaso exista no t�tulo pois s� pode existir apenas 1, e adiciona
	 *  esse novo ao t�tulo.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampoLider.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws NegocioException
	 */
	public String submeterCampoLider() throws DAOException{
		
		voltandoPaginaLider = false;
		
		if(voltarPaginaCatalogacao.equals("false")){
		
			montaValoresDigitadosPeloUsuario();
			
			Etiqueta etiquetaLider = null;
			
			if(isTipoCatalogacaoBibliografica()){
				etiquetaLider = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(
						Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag(),
						Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTipo());
				
				// Como o usu�rio vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso j� tenha um campo l�der.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO);
				
				// J� cria e adiciona ao T�tulo
				CampoControle lider = null;
				ListaMensagens listaErros = new ListaMensagens();
				
				
				try{
					lider = new CampoControle(CatalogacaoUtil.montaDadosCampoControle( valoresLider,
							Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO ), etiquetaLider, -1, obj);
					listaErros = CatalogacaoValidatorFactory.getRegrasValidacao().validaCampoMarcBibliografico(lider,
							obj.getFormatoMaterial());
				
				} catch (NegocioException ne) {
					ne.printStackTrace();
					addMensagemErro(ne.getMessage());
					return telaCampoControleLider();
				}
				
				
				if(listaErros.size() > 0 ){
					addMensagens(listaErros);
					errosValidacaoTeladeControle = true;
					return null;
				}else{
					errosValidacaoTeladeControle = false;
				}
				
			}
			
			if(isTipoCatalogacaoAutoridade()){
				etiquetaLider = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(
						Etiqueta.CAMPO_LIDER_AUTORIDADE.getTag(),
						Etiqueta.CAMPO_LIDER_AUTORIDADE.getTipo());
				
				// Como o bean est� em sess�o e o usu�rio vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso j� tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_LIDER_AUTORIDADE);
				
				// J� cria e adiciona ao t�tulo
				CampoControle lider;
				ListaMensagens listaErros =  new ListaMensagens();
				
				try {
					
					lider = new CampoControle(CatalogacaoUtil.montaDadosCampoControle( valoresLider,
							Etiqueta.CAMPO_LIDER_AUTORIDADE ), etiquetaLider, -1, obj);
					listaErros = CatalogacaoValidatorFactory.getRegrasValidacao().validaCampoMarcAutoridade(lider);
			
				} catch (NegocioException ne) {
					ne.printStackTrace();
					addMensagemErro(ne.getMessage());
					return telaCampoControleLider();
				}
				
				if(listaErros.size() > 0 ){
					addMensagens(listaErros);
					errosValidacaoTeladeControle = true;
					return null;
				}else{
					errosValidacaoTeladeControle = false;
				}
			}
			
			
			
		}else{
			errosValidacaoTeladeControle = false;
		}
		
		voltarPaginaCatalogacao = "false";
		
		if(editandoCamposDeControle || adicionandoCamposDeControle){
			// estava apenas editando ent�o volta direto para a p�gina de cataloga��o
			adicionandoCamposDeControle = false;
			editandoCamposDeControle = false;
			return telaDadosTituloCatalografico();
		}else{
			// criando pela primeira vez ent�o vai para a p�gina do 008
			if(tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
				etiquetaControleAtual = Etiqueta.CAMPO_008_BIBLIOGRAFICO;
				montaDadosAjudaControle(etiquetaControleAtual);
			}
			
			if(tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
				etiquetaControleAtual = Etiqueta.CAMPO_008_AUTORIDADE;
				montaDadosAjudaControle(etiquetaControleAtual);
			}
			
			return telaCampoControle008();
		}
	}
	
	
	
	/**
	 *   Cria o campo de controle 001 e adiciona ao t�tulo com os dados que o usu�rio digitou na p�gina.
	 *
	 *   <br/><br/>
	 *	 Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo001.jsp
	 *
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String submeterCampo001() throws DAOException{
		
		adicionandoCamposDeControle = false;
		editandoCamposDeControle = false;
		
		
		if(voltarPaginaCatalogacao.equals("false")){
			
			
			Etiqueta etiqueta001 = null;
			
			if(isTipoCatalogacaoBibliografica()){
				etiqueta001 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_001_BIBLIOGRAFICO.getTag(),
																				  Etiqueta.CAMPO_001_BIBLIOGRAFICO.getTipo());
				
				// Como o bean est� em sess�o e o usu�rio vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso j� tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_001_BIBLIOGRAFICO);
				
				// J� cria e adiciona ao t�tulo
				// Aqui n�o precisa montar os dados s� que vem da tela, pois j� s�o os dados dos campos de controle.
				new CampoControle(dadosCamposControle, etiqueta001, -1, obj);
				
				// 001 POR ENQUANTO N�O TEM VALIDA��O
				
			}
			
			if(isTipoCatalogacaoAutoridade()){
				etiqueta001 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_001_AUTORIDADE.getTag(),
						  														Etiqueta.CAMPO_001_AUTORIDADE.getTipo());
				
				// Como o bean est� em sess�o e o usu�rio vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso j� tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_001_AUTORIDADE);
				
				// j� cria e adiciona ao t�tulo
				// aqui n�o precisa montar os dado so que vem da tela j� � os dados dos campos de controle
				new CampoControle(dadosCamposControle, etiqueta001, -1, obj);
				
				// 001 POR ENQUANTO N�O TEM VALIDA��O
				
			}
		
		}else{
			errosValidacaoTeladeControle = false;
		}
		
		voltarPaginaCatalogacao = "false";
		errosValidacaoTeladeControle = false;
		
		dadosCamposControle = "";
		return telaDadosTituloCatalografico();
	}
	
	
	
	/**
	 *	 Cria o campo de controle 003 e adiciona ao t�tulo com os dados que o usu�rio digitou na p�gina
	 *
	 *     <br/><br/>
	 *	 Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo003.jsp
	 *
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String submeterCampo003() throws DAOException{
		adicionandoCamposDeControle = false;
		editandoCamposDeControle = false;
		
		
		if(voltarPaginaCatalogacao.equals("false")){
			
			Etiqueta etiqueta003 = null;
			
			if(isTipoCatalogacaoBibliografica()){
				etiqueta003 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_003_BIBLIOGRAFICO.getTag(),
																				  Etiqueta.CAMPO_003_BIBLIOGRAFICO.getTipo());
				
				// Como o bean est� em sess�o e o usu�rio vai poder ficar indo e voltando a essa tela
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_003_BIBLIOGRAFICO);
				
				// J� cria e adiciona ao T�tulo.
				// Aqui n�o precisa montar os dado so que vem da tela j� � os dados dos campos de controle
				new CampoControle(dadosCamposControle, etiqueta003, -1, obj);
			
				
			}
			
			if(isTipoCatalogacaoAutoridade()){
				etiqueta003 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_003_AUTORIDADE.getTag(),
						  														Etiqueta.CAMPO_003_AUTORIDADE.getTipo());
				
				// Como o bean est� em sess�o e o usu�rio vai poder ficar indo e voltando a essa tela
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_003_AUTORIDADE);
				
				// J� cria e adiciona ao t�tulo.
				// Aqui n�o precisa montar os dado s� que vem da tela, j� s�o os dados dos campos de controle
				new CampoControle(dadosCamposControle, etiqueta003, -1, obj);
				
			}
			
		}else{
			errosValidacaoTeladeControle = false;
		}
		
		voltarPaginaCatalogacao = "false";
		dadosCamposControle = "";
		errosValidacaoTeladeControle = false;
		return telaDadosTituloCatalografico();
	}
	
	
	
	
	/**
	 *   Cria o campo de controle 005 e adiciona ao t�tulo com os dados que o usu�rio digitou na p�gina
	 *
	 *     <br/><br/>
	 *	 Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo005.jsp
	 *
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String submeterCampo005() throws DAOException{
		
		adicionandoCamposDeControle = false;
		editandoCamposDeControle = false;
		
		
		if(voltarPaginaCatalogacao.equals("false")){
			
			Etiqueta etiqueta005 = null;
			
			if(isTipoCatalogacaoBibliografica()){
				etiqueta005 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTag(),
																				  Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTipo());
				
				// Como o bean est� em sess�o e o usu�rio vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso j� tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_005_BIBLIOGRAFICO);
			
				// J� cria e adiciona ao t�tulo.
				// Aqui n�o precisa montar os dado so que vem da tela j� � os dados dos campos de controle.
				new CampoControle(dadosCamposControle, etiqueta005, -1, obj);
				
			}
			
			if(isTipoCatalogacaoAutoridade()){
				etiqueta005 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_005_AUTORIDADE.getTag(),
						  														Etiqueta.CAMPO_005_AUTORIDADE.getTipo());
				
				// Como o bean est� em sess�o e o usu�rio vai poder ficar indo e voltando a essa tela
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_005_AUTORIDADE);
				
				// J� cria e adiciona ao t�tulo.
				// Aqui n�o precisa montar os dado so que vem da tela j� � os dados dos campos de controle.
				new CampoControle(dadosCamposControle, etiqueta005, -1, obj);
				
			}
			
		}else{
			errosValidacaoTeladeControle = false;
		}
		
		voltarPaginaCatalogacao = "false";
		dadosCamposControle = "";
		
		
		return telaDadosTituloCatalografico();
	}
	
	
	
	
	/**
	 *    <p>M�todo chamado na p�gina de edi��o dos dados do campo 006: formDadosCampo006.jsp</p>
	 *    <p>Dependendo da a��o selecionada pelo usu�rio, apenas volta para a p�gina de cataloga��o
	 *    ou adiciona um novo campo de controle 006.</p>
	 *	
	 *     <br/><br/>
	 *	  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo006.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String submeterCampo006() throws DAOException{
		
		
		if(voltarPaginaCatalogacao.equals("false")){
		
			montaValoresDigitadosPeloUsuario();
			
			// s� existe na bibliogr�fica
			Etiqueta etiqueta006 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_006_BIBLIOGRAFICO.getTag(),
																				Etiqueta.CAMPO_006_BIBLIOGRAFICO.getTipo());
			
			
			if(editandoCamposDeControle){
				CampoControle campo006 = obj.getCamposControle().get(posicaoCampoControleSelecionado);
				
				ListaMensagens listaErros =  new ListaMensagens();
				
				try{
					
					campo006.setDado(CatalogacaoUtil.montaDadosCampoControle( valores006, Etiqueta.CAMPO_006_BIBLIOGRAFICO ));
					
					listaErros = CatalogacaoValidatorFactory.getRegrasValidacao().validaCampoMarcBibliografico(campo006, obj.getFormatoMaterial());
				
				} catch (NegocioException ne) {
					ne.printStackTrace();
					addMensagemErro(ne.getMessage());
					return telaCampoControle006();
				}
				
				if(listaErros.size() > 0 ){
					addMensagens(listaErros);
					errosValidacaoTeladeControle = true;
					return null;
				}else{
					errosValidacaoTeladeControle = false;
				}
				
			}
			
			if(adicionandoCamposDeControle){
				// J� cria e adiciona ao t�tulo
				ListaMensagens listaErros =  new ListaMensagens();
				
				CampoControle c006 = null;
				
				try{
				
					c006 = new CampoControle(CatalogacaoUtil.montaDadosCampoControle( valores006 , Etiqueta.CAMPO_006_BIBLIOGRAFICO), etiqueta006, -1);
					listaErros = CatalogacaoValidatorFactory.getRegrasValidacao().validaCampoMarcBibliografico(c006, obj.getFormatoMaterial());
					
				} catch (NegocioException ne) {
					ne.printStackTrace();
					addMensagemErro(ne.getMessage());
					return telaCampoControle006();
				}
				
				if(listaErros.size() > 0 ){
					addMensagens(listaErros);
					errosValidacaoTeladeControle = true;
					return null;
				}else{
					
					// Se n�o tem erros adiciona ao T�tulo:
					
					c006.adicionaTitulo(obj);
					
					errosValidacaoTeladeControle = false;
				}
				
			}
			
		
		}else{
			errosValidacaoTeladeControle = false;
		}
			
		voltarPaginaCatalogacao = "false";
		adicionandoCamposDeControle = false;
		editandoCamposDeControle = false;
		
		
		return telaDadosTituloCatalografico();
	}
	
	
	
	/**
	 *    M�todo chamado para submeter os dados do campo 007.
	 * 
	 *   <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo003.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String submeterCampo007() throws DAOException{
		
		
		if(voltarPaginaCatalogacao.equals("false")){
		
			montaValoresDigitadosPeloUsuario();
			
			// s� tem na bibliogr�fica
			Etiqueta etiqueta007 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(
					Etiqueta.CAMPO_007_BIBLIOGRAFICO.getTag(), Etiqueta.CAMPO_007_BIBLIOGRAFICO.getTipo());
			
			if(editandoCamposDeControle){
				
				ListaMensagens listaErros =  new ListaMensagens();
				
				try{
					CampoControle campo007 = obj.getCamposControle().get(posicaoCampoControleSelecionado);
					campo007.setDado(CatalogacaoUtil.montaDadosCampoControle( valores007,
							Etiqueta.CAMPO_007_BIBLIOGRAFICO ));
					
					
					listaErros = CatalogacaoValidatorFactory.getRegrasValidacao().validaCampoMarcBibliografico(campo007,
							obj.getFormatoMaterial());
				
				} catch (NegocioException ne) {
					ne.printStackTrace();
					addMensagemErro(ne.getMessage());
					return telaCampoControle007();
				}
				
				if(listaErros.size() > 0 ){
					addMensagens(listaErros);
					errosValidacaoTeladeControle = true;
					return null;
				}else{
					errosValidacaoTeladeControle = false;
				}
				
			}
			
			if(adicionandoCamposDeControle){
				// j� cria e adiciona ao T�tulo
				ListaMensagens listaErros =  new ListaMensagens();
				
				CampoControle c007 = null;
				
				try{
					
					c007 = new CampoControle(CatalogacaoUtil.montaDadosCampoControle( valores007, Etiqueta.CAMPO_007_BIBLIOGRAFICO ), etiqueta007, -1);
					listaErros = CatalogacaoValidatorFactory.getRegrasValidacao().validaCampoMarcBibliografico(c007, obj.getFormatoMaterial());
				
				} catch (NegocioException ne) {
					ne.printStackTrace();
					addMensagemErro(ne.getMessage());
					return telaCampoControle007();
				}
				
				if(listaErros.size() > 0 ){
					addMensagens(listaErros);
					errosValidacaoTeladeControle = true;
					return null;
				}else{
					
					// Se n�o teve erro na valida��o � que pode adicionar o campo ao t�tulo
					
					c007.adicionaTitulo(obj);
					
					errosValidacaoTeladeControle = false;
				}
				
			}
		
		}else{
			errosValidacaoTeladeControle = false;
		}
		
		voltarPaginaCatalogacao = "false";
		adicionandoCamposDeControle = false;
		editandoCamposDeControle = false;
		codigoCategoriaMaterial = "";
		
		return telaDadosTituloCatalografico();
	}
	
	
	
	/**
	 * 
	 * Busca a etiqueta do campo 008 e valida os dados desse campo,
	 * se tudo estiver correto, adiciona ao t�tulo e segue para a tela onde � adicionado os outros campos,
	 * sen�o fica na mesma p�gina mostrando as mensagens de erro na valida��o.
	 * 
	 *    Esse m�todo pega os valores do campo 008 (que pode ser os padr�es vindo do banco
	 * ou se o usu�rio alterou ficou aqui tamb�m) e monta a String de dados do campo 008
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
	 * 
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String submeterCampo008() throws DAOException{
		
		voltandoPagina008 = false;
		
		if(voltarPaginaCatalogacao.equals("false")){
		
			montaValoresDigitadosPeloUsuario();
			
			Etiqueta etiqueta008 = null;
			
			if(isTipoCatalogacaoBibliografica()){
				etiqueta008 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_008_BIBLIOGRAFICO.getTag(),
																				  Etiqueta.CAMPO_008_BIBLIOGRAFICO.getTipo());
				
				// Como o bean est� em sess�o e o usu�rio vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso j� tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_008_BIBLIOGRAFICO);
				
				ListaMensagens listaErros =  new ListaMensagens();
				
				try{
					// J� cria e adiciona ao T�tulo
					CampoControle campo008 = new CampoControle( CatalogacaoUtil.montaDadosCampoControle( valores008, Etiqueta.CAMPO_008_BIBLIOGRAFICO ), etiqueta008, -1, obj);
				
					listaErros = CatalogacaoValidatorFactory.getRegrasValidacao().validaCampoMarcBibliografico(campo008, obj.getFormatoMaterial());
				
				} catch (NegocioException ne) {
					ne.printStackTrace();
					addMensagemErro(ne.getMessage());
					return telaCampoControle008();
				}
				
				if(listaErros.size() > 0 ){
					addMensagens(listaErros);
					errosValidacaoTeladeControle = true;
					return null;
				}else{
					errosValidacaoTeladeControle = false;
				}
			}
			
			if(isTipoCatalogacaoAutoridade()){
				etiqueta008 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_008_AUTORIDADE.getTag(),
						  														Etiqueta.CAMPO_008_AUTORIDADE.getTipo());
				
				// Como o bean est� em sess�o e o usu�rio vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso j� tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_008_AUTORIDADE);
			
				ListaMensagens listaErros =  new ListaMensagens();
				
				try{
					// J� cria e adiciona ao T�tulo
					CampoControle campo008 = new CampoControle( CatalogacaoUtil.montaDadosCampoControle( valores008, Etiqueta.CAMPO_008_AUTORIDADE), etiqueta008, -1, obj);
					listaErros = CatalogacaoValidatorFactory.getRegrasValidacao().validaCampoMarcAutoridade(campo008);
			
				} catch (NegocioException ne) {
					ne.printStackTrace();
					addMensagemErro(ne.getMessage());
					return telaCampoControle008();
				}
				
				if(listaErros.size() > 0 ){
					addMensagens(listaErros);
					errosValidacaoTeladeControle = true;
					return null;
				}else{
					errosValidacaoTeladeControle = false;
				}
			}
			
			
			
			
			
			
			/* Se for a primeira vez que vai entrar na tela de cataloga��o */
			
			if( isEmpty(obj.getCamposDados()) && ! editandoCamposDeControle ){
				CatalogacaoUtil.criaCamposDeDadosIniciais(obj, isTipoCatalogacaoBibliografica());
			}
			
			if(obj.getCamposDados() != null){
				camposNaoReservados = obj.getCamposDadosNaoReservados();
				camposReservados = obj.getCamposDadosReservados();
				dataModelCampos = new ListDataModel(camposNaoReservados);
			}else
				dataModelCampos = new ListDataModel();
			
		}else{
			errosValidacaoTeladeControle = false;
		}
		
		//   Nesse caso usado somente para habilitar e desabilitar o bot�o voltar da p�gina de edi��o do
		// campo 008,
		//   Se o usu�rio est� editando o campo apenas a op��o de voltar para a p�gina de cataloga��o
		// deve est� habilitada.
		if(editandoCamposDeControle || adicionandoCamposDeControle){
			adicionandoCamposDeControle = false;
			editandoCamposDeControle = false;
			
		}
		
		voltarPaginaCatalogacao = "false";
		return telaDadosTituloCatalografico();
		
	}
	
	
	/**
	 *    <p>M�todo que implementa a fun��o voltar da tela de cataloga��o.</p>
	 *    <p>Como essa p�gina � chamada de v�rios lugares, existe uma l�gica para determinar para onde
	 * deve-se voltar.</p>.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String voltarDaPaginaCatalogacao() throws ArqException{
		
		if(editando){
			if(isTipoCatalogacaoBibliografica())
			 ((PesquisaTituloCatalograficoMBean) getMBean("pesquisaTituloCatalograficoMBean")).telaPesquisaTitulo();
		
			if(isTipoCatalogacaoAutoridade())
				((CatalogaAutoridadesMBean) getMBean("catalogaAutoridadesMBean")).telaPesquisaAutoridades();
		}
		
		
		if(catalogacaoPlanilha){
			if(isTipoCatalogacaoBibliografica())
				return telaEscolhePlanilhaBibliografica();
			
			if(isTipoCatalogacaoAutoridade())
				return telaEscolhePlanilhaAutoridades();
			
		}
		
		if(catalogacaoImportacao){
			if(isTipoCatalogacaoBibliografica()){
				
				if(possuiEntiadesNaoFinalizados){
					BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
					bean.setTitulosIncompletos(null); // para o bean buscar novamente
					return bean.iniciarBuscaTitulosIncompletosMantendoOperacao();
				}else{
					return ((CooperacaoTecnicaImportacaoMBean) getMBean("cooperacaoTecnicaImportacaoMBean")).iniciarImportacaoBibliografica();
				}
			}
				
			if(isTipoCatalogacaoAutoridade()){
				
				if(possuiEntiadesNaoFinalizados){
					BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
					bean.setAutoridadesIncompletas(null); // para o bean buscar novamente
					return bean.iniciarBuscaAutoridadesIncompletosMantendoOperacao();
				}else
					return ((CooperacaoTecnicaImportacaoMBean) getMBean("cooperacaoTecnicaImportacaoMBean")).iniciarImportacaoAutoridades();
			}
		}
		
		
		if(catalogacaoDuplicacao){
			if(isTipoCatalogacaoBibliografica())
				return ((PesquisaTituloCatalograficoMBean) getMBean("pesquisaTituloCatalograficoMBean")).telaPesquisaTitulo();
			
			if(isTipoCatalogacaoAutoridade())
				return ((CatalogaAutoridadesMBean) getMBean("catalogaAutoridadesMBean")).iniciarPesquisaNormal();
			
		}
		
		return null; // Nunca era para chegar aqui.
	}
	
	
	
	
	/**
	 * 		 M�todo chamado na p�gina principal da cataloga��o para validar, salvar ou atualizar
	 *  o t�tulo ou autoridade ou tudo mais que for usar essa tela de cataloga��o MARC.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String submeterDadosTituloCatalografico() throws ArqException{
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		if( ! isUsarTelaCatalogacaoCompleta() && isTipoCatalogacaoBibliografica()){
			if(planilhaCatalogacaoSimplificada != null
					&& planilhaCatalogacaoSimplificada.getIdFormato() != obj.getFormatoMaterial().getId()){
				addMensagemErro("O formato de Formato de Material da planilha escolhida n�o � igual ao Formato de Material do T�tulo");
				return null;
			}
		}
		
		// o usu�rio clicou no bot�o voltar do navegador
		if( obj.getCamposDados() == null && obj.getCamposControle() == null){
			addMensagemErro("Opera��o j� processada. Isso ocorreu provavelmente porque voc� usou o bot�o voltar no navegador.");
			return cancelar();
		}
		
		switch(tipoCatagalocao){
		
			case TipoCatalogacao.BIBLIOGRAFICA:
				return realizaOperacoesTitulo();
				
			case TipoCatalogacao.AUTORIDADE:
				return realizaOperacoesAutoridades();
				
			default:
				return null;  // nunca era para cair aqui.
		}
		
	}
	
	
	
	
	/**
	 * 	M�todo que realiza a cria��o ou altera��o quando a cataloga��o � de um T�tulo
	 */
	private String realizaOperacoesTitulo() throws ArqException{
		
		if(editando){
		
			MovimentoAtualizaTitulo mov = new MovimentoAtualizaTitulo(obj, arquivo, apagarArquivoDigitalSalvo, false, classificacoesUtilizadas);
			mov.setCodMovimento(SigaaListaComando.ATUALIZA_TITULO);
			
			try{
				execute(mov);
			}catch(NegocioException e){           // outro erro de negocio sem ser do padr�o MARC
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			addMensagemInformation("T�tulo Atualizado com Sucesso.");
				
			
		}else{   // CRIANDO O T�TULO
			
			MovimentoCatalogaTitulo mov = null;
			
			if("true".equals(finalizarCatalogacao) || "true".equals(adicionarMaterialInformacional) )
				mov = new MovimentoCatalogaTitulo(obj, arquivo, true, classificacoesUtilizadas);
			else{
				mov = new MovimentoCatalogaTitulo(obj, arquivo, false, classificacoesUtilizadas);
			}
			
			mov.setCodMovimento(SigaaListaComando.CATALOGA_TITULO);
			
			try{
				execute(mov);
			}catch(NegocioException e){
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			
			// Coloca um mensagem de sucesso para o usu�rio  //
			if("true".equals(finalizarCatalogacao) || "true".equals(adicionarMaterialInformacional)){
				addMensagemInformation("T�tulo Catalogado com Sucesso. N� do sistema : "+obj.getNumeroDoSistema());
			}else{
				addMensagemInformation("T�tulo Salvo com Sucesso. N� do sistema : "+obj.getNumeroDoSistema());
			}
			
		}
		
		return redirecionaProximaPaginaDepoisCatalogacao();
		
	}
	
	
	
	/**
	 * M�todo que realiza a cria��o ou altera��o quando a cataloga��o � de uma autoridade.
	 */
	private String realizaOperacoesAutoridades() throws ArqException{

		/* *****************************************************************************************
		 * FEZ TODAS AS OPERA��ES EM CIMA DE UM T�TULO PORQUE A ESTRUTURA � A MESMA E APROVEITA    *
		 * MUITA COISA, AGORA COPIA OS CAMPOS DE DADOS E CONTROLE PARA UMA AUTORIDADE E SALVA      *
		 *******************************************************************************************/
		
		Autoridade autoridade = CatalogacaoUtil.criaAutoridaeAPartirTitulo(obj);
		
		if(editando){
			
			autoridade.setId(obj.getId()); // o id da autoridade estava no t�tulo.
			
			MovimentoAtualizaAutoridade mov = new MovimentoAtualizaAutoridade(autoridade, false, classificacoesUtilizadas);
			mov.setCodMovimento(SigaaListaComando.ATUALIZA_AUTORIDADE);
			
			try{
				execute(mov);
			}catch(NegocioException e){           // outro erro de negocio sem ser do padr�o MARC
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			addMensagemInformation("Autoridade Atualizada com Sucesso.");
			
			if( "true".equals(finalizarCatalogacao)){
				return cancelar(); // p�gina inicial do sistema
			}else{
				prepareMovimento(SigaaListaComando.ATUALIZA_AUTORIDADE);
				
				ordenaCamposDados(null); // para ordenar na p�gina para o usu�rio, antes de salvar ele atualiza mas n�o se reflete na p�gina para o usu�rio
				return telaDadosTituloCatalografico();
			}
			
			
		}else{
			
			MovimentoCatalogaAutoridade mov = null;
			
			if("true".equals(finalizarCatalogacao)){
				mov = new MovimentoCatalogaAutoridade(autoridade, true);
			}else{
				mov = new MovimentoCatalogaAutoridade(autoridade, false);
			}
			
			mov.setCodMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
			
			
			try{
				execute(mov);
			}catch(NegocioException e){           // outro erro de neg�cio sem ser do padr�o MARC
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			
			obj = CatalogacaoUtil.criaTituloAPartirAutoridade(autoridade);  // IMPORTANTE
			
			
			if("true".equals(finalizarCatalogacao) ){
				
				addMensagemInformation("Autoridade Cadastrada com Sucesso. N� do sistema : "+autoridade.getNumeroDoSistema());
				
				if(possuiEntiadesNaoFinalizados){
					
					if("true".equals(redirecionaProximaAutoriadeIncompleta)){
						BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
						bean.setAutoridadesIncompletas(null); // para o bean buscar novamente
						return bean.iniciarBuscaAutoridadesIncompletosMantendoOperacao();
					}
				}
				
				return cancelar(); // p�gina inicial do sistema
				
			}else{
				//return iniciarAutoridadesDuplicacao(obj);
				
				addMensagemInformation("Autoridade Salva com Sucesso. N� do sistema : "+autoridade.getNumeroDoSistema());
				prepareMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
				
				ordenaCamposDados(null); // para ordenar na p�gina para o usu�rio, antes de salvar ele atualiza mas n�o se reflete na p�gina para o usu�rio
				
				return telaDadosTituloCatalografico();
			}
			
		}
		
	}
	
	
	
	
	/**
	 * M�todo que cont�m a l�gica para qual p�gina vai o caso de uso dependendo da a��o selecionada
	 * pelo usu�rio.
	 */
	private String redirecionaProximaPaginaDepoisCatalogacao() throws ArqException{
		
		// se o usu�rio clicou no bot�o de adicionar material informacional
		if(adicionarMaterialInformacional.equals("true") && ( isCatalogacaoComTombamento() || isCatalogacaoMateriaisSemTombamento())){
			MaterialInformacionalMBean materialMBean = getMBean("materialInformacionalMBean");
			
			if(possuiEntiadesNaoFinalizados)
				materialMBean.setAbilitaBotaoVoltarTelaTitulosNaoFinalizados(true);
			else
				materialMBean.setAbilitaBotaoVoltarTelaTitulosNaoFinalizados(false);
			
			if(! isCatalogacaoMateriaisSemTombamento())
				infoTituloCompra.idTituloCatalograficoSigaa = obj.getId();
			
			
			materialMBean.setIncluindoMateriaisApartirTelaCatalogacao(true);
			
			
			if(! isCatalogacaoMateriaisSemTombamento()){
				if(obj.getFormatoMaterial().isFormatoPeriodico())
					return materialMBean.iniciarParaAdicaoFasciculos(infoTituloCompra);
				else
					return materialMBean.iniciarParaAdicaoExemplares(infoTituloCompra,
							PAGINA_PREENCHE_DADOS_TITULO_CATALOGRAFICO);
				
			}else{
				if(obj.getFormatoMaterial().isFormatoPeriodico())
					return materialMBean.iniciarParaAdicaoFasciculosNaoTombados(obj);
				else
					return materialMBean.iniciarParaAdicaoExemplaresNaoTombados(obj,
							PAGINA_PREENCHE_DADOS_TITULO_CATALOGRAFICO);
			}
			
		}else{
			// se est� apenas finalizando o t�tulo ent�o volta para a tela principal do sistema.
			if("true".equals(finalizarCatalogacao)){
				
				if(possuiEntiadesNaoFinalizados){ // volta para catalogar o pr�ximo
					
					BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
					bean.setTitulosIncompletos(null);  // para buscar novamente
					return bean.iniciarBuscaTitulosIncompletos();
					
				}else
				
				return cancelar(); // P�GINA PRINCIPAL
				
			}else{
				// se o usu�rio n�o finalizou nem adicionou materiais � porque
				//quer apenas salvar ou atualizar o t�tulo, ent�o fica na mesma p�gina.
				
				ordenaCamposDados(null); // para ordenar na p�gina para o usu�rio, antes de salvar ele atualiza mas n�o se reflete na p�gina para o usu�rio
				
				if(editando){
					//return iniciarParaEdicao(obj);
					prepareMovimento(SigaaListaComando.ATUALIZA_TITULO); // se vai ficar na mesma p�gina prepara o proximo movimento
					return telaDadosTituloCatalografico();
				}else{
					//return iniciarDuplicacao(obj);
					prepareMovimento(SigaaListaComando.CATALOGA_TITULO); // se vai ficar na mesma p�gina prepara o proximo movimento
					return telaDadosTituloCatalografico();
				}
				
				
			}
		}
		
	}
	
	
	
	/**
	 *   Adiciona um novo campo de controle.
	 *   Como os campos de controle seguem regras pr�prias v�o ter suas pr�prias p�gina como
	 * os campos LDR e 008.
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void adicionarNovoCampoControle(ActionEvent e) throws DAOException{
	
		
		adicionandoCamposDeControle = true;
		
		String tag = getCurrentRequest().getParameter("tagEscolhidaAdicaoCampoControle");
		
		if(tag != null){ // qualquer um, menos o 007
		
			if(tag.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
				etiquetaControleAtual = Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO;
				montaDadosAjudaControle(etiquetaControleAtual);
				
				telaCampoControleLider();
			}
			
			if(tag.equals(Etiqueta.CAMPO_001_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
				etiquetaControleAtual = Etiqueta.CAMPO_001_BIBLIOGRAFICO;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControle001();
			}
			
			if(tag.equals(Etiqueta.CAMPO_003_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
				etiquetaControleAtual = Etiqueta.CAMPO_003_BIBLIOGRAFICO;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControle003();
			}
			
			if(tag.equals(Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
				etiquetaControleAtual = Etiqueta.CAMPO_005_BIBLIOGRAFICO;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControle005();
			}
			
			if(tag.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
				etiquetaControleAtual = Etiqueta.CAMPO_006_BIBLIOGRAFICO;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControle006();
			}
			
			if(tag.equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
				etiquetaControleAtual = Etiqueta.CAMPO_008_BIBLIOGRAFICO;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControle008();
			}
		
			
			if(tag.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
				etiquetaControleAtual = Etiqueta.CAMPO_LIDER_AUTORIDADE;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControleLider();
			}
			
			if(tag.equals(Etiqueta.CAMPO_001_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
				etiquetaControleAtual = Etiqueta.CAMPO_001_AUTORIDADE;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControle001();
			}
			
			if(tag.equals(Etiqueta.CAMPO_003_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
				etiquetaControleAtual = Etiqueta.CAMPO_003_AUTORIDADE;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControle003();
			}
			
			if(tag.equals(Etiqueta.CAMPO_005_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
				etiquetaControleAtual = Etiqueta.CAMPO_005_AUTORIDADE;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControle005();
			}
			
			if(tag.equals(Etiqueta.CAMPO_008_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
				etiquetaControleAtual = Etiqueta.CAMPO_008_AUTORIDADE;
				montaDadosAjudaControle(etiquetaControleAtual);
				telaCampoControle008();
			}
			
		}else{  // escolheu o 007
		
			codigoCategoriaMaterial = getCurrentRequest().getParameter("codigoCategoriaMaterial");
		
			etiquetaControleAtual  = Etiqueta.CAMPO_007_BIBLIOGRAFICO; // 007 s� tem bibliogr�fico
			montaDadosAjudaControle(etiquetaControleAtual);
			telaCampoControle007();
		
		}
		
	}
	
	
	/**
	 *   Edita um campo de controle que j� esteja na p�gina quando o usu�rio clicar em cima dele.
	 * 
	 *   Vai pegar a tag do campo e com base nela vai chamar a p�gina correta de edi��o.
	 * 
	 *   So existem 6 possibilidades no padr�o MARC:  'LDR', '001', '003', '005', '007', '008'
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public String editarCampoControle() throws DAOException{
		
		String dados = getParameter("conteudoCampoControleAlteracao");
		posicaoCampoControleSelecionado = getParameterInt("posicaoCampoControleSelecionado");
		
		// posicaoCampoControleSelecionado <- setado na p�gina de cataloga��o ao clicar no link da etiqueta de controle
		String tag = obj.getCamposControle().get(posicaoCampoControleSelecionado).getEtiqueta().getTag();
		
		if(tag.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			etiquetaControleAtual = Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_LIDER_BIBLIOGRAFICO.length()){
				addMensagemErro("O tamanho do campo L�der est� inv�lido por isso n�o pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			
			return telaCampoControleLider();
		}
		
		if(tag.equals(Etiqueta.CAMPO_001_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			dadosCamposControle = dados;
			etiquetaControleAtual = Etiqueta.CAMPO_001_BIBLIOGRAFICO;
			
			montaDadosAjudaControle(etiquetaControleAtual);
			
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle001();
		}
		
		if(tag.equals(Etiqueta.CAMPO_003_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			dadosCamposControle = dados;
			etiquetaControleAtual = Etiqueta.CAMPO_003_BIBLIOGRAFICO;
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle003();
		}
		
		if(tag.equals(Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			dadosCamposControle = dados;
			etiquetaControleAtual = Etiqueta.CAMPO_005_BIBLIOGRAFICO;
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle005();
		}
		
		if(tag.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			etiquetaControleAtual = Etiqueta.CAMPO_006_BIBLIOGRAFICO;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_006_BIBLIOGRAFICO.length()){
				addMensagemErro("O tamanho do campo 006 est� inv�lido por isso n�o pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle006();
		}
		
		if(tag.equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			codigoCategoriaMaterial = String.valueOf( obj.getCamposControle().get(posicaoCampoControleSelecionado).getDado().charAt(0) ) ;
			etiquetaControleAtual = Etiqueta.CAMPO_007_BIBLIOGRAFICO;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_007_BIBLIOGRAFICO.length()){
				addMensagemErro("O tamanho do campo 007 est� inv�lido por isso n�o pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle007();
		}
		
		if(tag.equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			etiquetaControleAtual = Etiqueta.CAMPO_008_BIBLIOGRAFICO;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_008_BIBLIOGRAFICO.length()){
				addMensagemErro("O tamanho do campo 008 est� inv�lido por isso n�o pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle008();
		}
		
		/////////////// Autoridades
		
		if(tag.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_LIDER_AUTORIDADE;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_LIDER_AUTORIDADE.length()){
				addMensagemErro("O tamanho do campo L�der para autoridades est� inv�lido por isso n�o pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControleLider();
		}
		
		if(tag.equals(Etiqueta.CAMPO_001_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_001_AUTORIDADE;
			dadosCamposControle = dados;
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle001();
		}
		
		if(tag.equals(Etiqueta.CAMPO_003_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_003_AUTORIDADE;
			dadosCamposControle = dados;
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle003();
		}
		
		if(tag.equals(Etiqueta.CAMPO_005_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_005_AUTORIDADE;
			dadosCamposControle = dados;
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle005();
		}
		
		if(tag.equals(Etiqueta.CAMPO_008_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_008_AUTORIDADE;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_008_AUTORIDADE.length()){
				addMensagemErro("O tamanho do campo 008 para autoridades est� inv�lido por isso n�o pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, ent�o N�O deve pegar os valores padr�o do banco
			return telaCampoControle008();
		}
		
		return ""; // nunca era para chegar aqui
	}
	
	
	
	
	
	
	/**
	 * Adiciona um novo campo de dados
	 * Adiciona j� com 1 sub campo, porque todo campo de dados tem que ter pelo menos um sub campo.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 */
	public void adicionarNovoCampoDados(ActionEvent e){
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		CampoDados c = new CampoDados(new Etiqueta("", (short) -1), obj, -1);
		new SubCampo(c); // cria um sub campo vazio e adiciona a lista de subcampos
		
		atribuiCamposDoTituloAosCampoUsadosNaTela();
		
	}
	
	
	
	/**
	 * Adiciona ao T�tulo ou Autoridade o Campo de dados contido na planilha de cataloga��o que 
	 * est� sendo usado com todos os seus sub campos.
	 *
	 * As informa��es dos indicadores, c�digos dos sub campo j� v�o vim da planilha, o usu�rio n�o precisa conhecer....
	 *
	 * @void
	 */
	public void  adicionarCampoPlanilha (ActionEvent e) throws DAOException{
		
		String idCampoTemp = getCurrentRequest().getParameter("identificadorCampoDadosTemp");
		
		forExterno:
		for (CampoDados campo : tituloTemp.getCamposDadosNaoReservados()){
			
			if(campo.getIdentificadorTemp() == Integer.parseInt(idCampoTemp)){
				
				obj.setCamposDados(atribuiCamposDadosAoTitulo());
				
				CampoDados c = new CampoDados(recuperaEtiquetaTitulo(campo.getEtiqueta(), etiquetasBuscadas) , obj, -1);
				c.setIndicador1(campo.getIndicador1());
				c.setIndicador2(campo.getIndicador2());
				
				c.getEtiqueta().setGrupo(retornaGrupoEtiqueta(c.getEtiqueta()));
				
				for(SubCampo sub :  campo.getSubCampos()){ // cria os sub campo contidos na planilha
					SubCampo s = new SubCampo(sub.getCodigo(), c, -1);
					s.setDescricaoSubCampo(CatalogacaoUtil.retornaDescritorSubCampo(c.getEtiqueta(), s.getCodigo()));
				}
				
				CatalogacaoUtil.configuraGruposEtiquetaVisiveis(obj);
				
				atribuiCamposDoTituloAosCampoUsadosNaTela();
				
				break forExterno;
			}
		}
	}
	
	
	/**
	 * <p>Duplica o campo de dados selecionado pelo usu�rio.</p>
	 * 
	 * <p>Utilizado na cataloga��o simplificado, onde o usu�rio s� pode adicionar os campos que j� existem na planilha.</p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException 
	 * 
	 */
	public void duplicarCampo(ActionEvent e) throws DAOException{
		
		CampoDados cSelecionado = camposNaoReservados.get(dataModelCampos.getRowIndex());
		
		if ( !cSelecionado.getEtiqueta().isRepetivel() ){
			addMensagemErroAjax("Campo n�o pode ser repetido.");
			return;
		}
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		Etiqueta nova = CatalogacaoUtil.recuperaEtiquetaTitulo(cSelecionado.getEtiqueta(), etiquetasBuscadas);
		
		GrupoEtiqueta novo = cSelecionado.getEtiqueta().getGrupo();
		
		try {
			novo = (GrupoEtiqueta) cSelecionado.getEtiqueta().getGrupo().clone(); // tem que colocar um novo objeto
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
		
		nova.setGrupo(  novo );
		
		CampoDados c = new CampoDados(nova , obj, -1);
		
		c.setIndicador1(cSelecionado.getIndicador1());
		c.setIndicador2(cSelecionado.getIndicador2());
		
		for(SubCampo sub : cSelecionado.getSubCampos()){
			SubCampo sNovo = new SubCampo(sub.getCodigo(), null, c, null, -1); // duplica o sub campo selecionado pelo usu�rio
			sNovo.setDescricaoSubCampo(sub.getDescricaoSubCampo());
		}
		
		CatalogacaoUtil.configuraGruposEtiquetaVisiveis(obj); // na adi��o de um novo campo precisa reconfigurar os grupos
		
		atribuiCamposDoTituloAosCampoUsadosNaTela();
		
	}
	
	
	/**
	 * <p>Duplica o sub Campo selecionado pelo usu�rio se esse campo poder ser duplicado.</p>
	 * 
	 * <p>Usado apenas na cataloga��o simplificada, quando o usu�rio s� pode adicionar campo e sub campos presentes na planilha.</p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException 
	 * 
	 */
	public void duplicarSubCampo(ActionEvent e) throws DAOException{
		
		CampoDados c = camposNaoReservados.get(dataModelCampos.getRowIndex());
		
		SubCampo s = c.getSubCampos().get(((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex() );
		
		GenericDAO dao = null;
		
		try{
			
			dao = getGenericDAO();
			
			Collection<DescritorSubCampo> descritores = dao.findByExactField(DescritorSubCampo.class, "etiqueta.id", c.getEtiqueta().getId());
		
			for(DescritorSubCampo d : descritores){
				if( d.getCodigo().equals(s.getCodigo() )){
					if( ! d.isRepetivel()){
						addMensagemErroAjax("Sub campo n�o pode ser repetido.");
						return;
					}else{
						break;
					}
					
				} 
			}
		}finally{
			if(dao != null) dao.close();
		}
		
		SubCampo sNovo = new SubCampo(s.getCodigo(), null, c, null, -1); // duplica o sub campo selecionado pelo usu�rio
		sNovo.setDescricaoSubCampo(s.getDescricaoSubCampo());
		
	}
	
	
	/**
	 * 
	 * <p>M�todo que adiciona um novo campo de dados j� completo, isto �, com os seus sub campo.</p>
	 * 
	 * <p>A busca bai trazer todos os sub campo e o usu�rio vai remover aqueles que n�o desejar utilizar, em vez de 
	 * adicionar o campo de dados e ter que adicionar os sub campos um por um.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/modelPanelsFormCatalogacao.jsp</li>
	 *   </ul>
	 *
	 *
	 * @param e
	 * @throws DAOException 
	 */
	public void buscarCampoDeDadosCompleto(ActionEvent e) throws DAOException{
		
		campoDadosBuscaCompleta = new CampoDados();
		mensagensModelPanel = new ArrayList<String>();
		
		EtiquetaDao  dao = null;
		Etiqueta etiqueta = null;
		
		if(tagEtiquetaCampoDeDadosCompleto !=null && tagEtiquetaCampoDeDadosCompleto.length() == 3){
			try{
				dao = getDAO(EtiquetaDao.class);
				
				etiqueta = new Etiqueta(tagEtiquetaCampoDeDadosCompleto, tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA ?  TipoCatalogacao.BIBLIOGRAFICA: TipoCatalogacao.AUTORIDADE);
				
				etiqueta = recuperaEtiquetaTitulo(etiqueta, etiquetasBuscadas); 
			
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		if(etiqueta != null ){
			
			// N�O busca as etiqueta reservadas do sistema, sen�o dar erro na p�gina //
			if( etiqueta.equals(Etiqueta.CODIGO_DA_BIBLIOTECA)
					||  etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES)
					||  etiqueta.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO)
					||  etiqueta.equals(Etiqueta.CODIGO_DA_BIBLIOTECA_AUTORIDADES)
					||  etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES_AUTORIDADES)
					||  etiqueta.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO_AUTORIDADES)){
				
				// N�o � uma etiqueta de uso reservado porque o tipo (-1) � invalido
				mensagensModelPanel.add("CAMPO DE USO RESERVADO");
				
			}else{
			
				
				campoDadosBuscaCompleta.setEtiqueta(etiqueta);
				
				if(etiqueta.getDescritorSubCampo() != null && etiqueta.getDescritorSubCampo().size() > 0 ){
					for (DescritorSubCampo descritor  : etiqueta.getDescritorSubCampo()) {
						SubCampo  s = new SubCampo(descritor.getCodigo(), campoDadosBuscaCompleta, -1);
						s.setDescricaoSubCampo(descritor.getDescricao());
					}
					
					Collections.sort(campoDadosBuscaCompleta.getSubCampos(), new SubCampoByCodigoComparator()); 
					
				}else{
					mensagensModelPanel.add("INFORMA��ES DO CAMPO N�O EST�O CORRETAMENTE CADASTRADAS");
					campoDadosBuscaCompleta = null;
				}
				
				
			}
			
		}else{
			mensagensModelPanel.add("CAMPO N�O CADASTRADO");
			campoDadosBuscaCompleta = null;
		}
		
	}
	
	/**
	 * <p>M�todo que remove os sub campos que o usu�rio n�o deseja adicionar. </p>
	 * 
	 * <p>Utilizado na adi��o de campo de dados com subcampos j� adicionados.</p> 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/modelPanelsFormCatalogacao.jsp</li>
	 *   </ul>
	 *
	 *
	 * @param e
	 * @throws DAOException 
	 */
	public void removeSubCampoDadosCompleto(ActionEvent e){
		
		Character codigo = (Character) e.getComponent().getAttributes().get("codigoSubCampoCompleto");
		
		int indiceSubCampoRemotido = -1;
		
		
		for (int index = 0 ; index < campoDadosBuscaCompleta.getSubCampos().size() ; index++) {    // Cria os subcampos com os c�digos escolhidos
			
			if(campoDadosBuscaCompleta.getSubCampos().get(index).getCodigo().equals(codigo)){
				indiceSubCampoRemotido = index;
				break;
			}
			
		}
		
		if(indiceSubCampoRemotido != -1){
			campoDadosBuscaCompleta.getSubCampos().remove(indiceSubCampoRemotido);
		}
		
	}
	
	
	/**
	 * <p>M�todo que adiciona um novo campo de dados j� completo ao T�tulo Ou Autoridade, isto �, com os seus sub campo.</p>
	 * <p>Adiciona no final da lista dos campo de dados. </p>
	 *  
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/modelPanelsFormCatalogacao.jsp</li>
	 *   </ul>
	 *
	 *
	 * @param e
	 * @throws DAOException 
	 */
	public void adicionaNovoCampoDadosCompleto(ActionEvent e){
		
		
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		if(campoDadosBuscaCompleta.getEtiqueta() != null 
				&& campoDadosBuscaCompleta.getSubCampos() != null && campoDadosBuscaCompleta.getSubCampos().size() > 0){
			CampoDados c = new CampoDados(campoDadosBuscaCompleta.getEtiqueta(), obj, -1);
			for (SubCampo subTemp : campoDadosBuscaCompleta.getSubCampos()) {    // Cria os subcampos com os c�digos escolhidos
				new SubCampo(subTemp.getCodigo(), c, -1);
			}
		
		}
		
		camposNaoReservados = obj.getCamposDadosNaoReservados();
		camposReservados = obj.getCamposDadosReservados();
		dataModelCampos = new ListDataModel(camposNaoReservados);
		
		tagEtiquetaCampoDeDadosCompleto = "";
		campoDadosBuscaCompleta = null;
		
	}
	
	/**
	 * <p>Adiciona os os campos de dados obrigat�rios para uma cataloga��o de T�tulos pode ser salva. </p>
	 * <p>No caso s�o os campos 080, 084, 090, 245</p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException 
	 * 
	 */
	public void adicionarCampoDadosObrigatorios(ActionEvent e) throws DAOException{
		
		Etiqueta etiquetaClassificacao = null;
		
		Etiqueta etiquetaChamada = null;
		Etiqueta etiquetaTitulo = null;
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		// Cria um campo para cada classifica��es utilizada de forma gen�rica //
		for (ClassificacaoBibliografica classificacao : classificacoesUtilizadas) {
			
			etiquetaClassificacao = new Etiqueta(classificacao.getCampoMARC().getCampo(), tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA ?  TipoCatalogacao.BIBLIOGRAFICA: TipoCatalogacao.AUTORIDADE);
		
			if(! tituloContemCampoDados(obj, etiquetaClassificacao, classificacao.getCampoMARC().getSubCampo(), false)){
				
				etiquetaClassificacao = recuperaEtiquetaTitulo(etiquetaClassificacao, etiquetasBuscadas); 
				
				if(etiquetaClassificacao != null){
					CampoDados c = new CampoDados(etiquetaClassificacao, obj, -1);
					new SubCampo(classificacao.getCampoMARC().getSubCampo(), c, -1); // cria um sub campo vazio e adiciona a lista de subcampos
				}
			}
		
		}
		
		
		
		etiquetaChamada = new Etiqueta(Etiqueta.NUMERO_CHAMADA.getTag(), tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA ?  TipoCatalogacao.BIBLIOGRAFICA: TipoCatalogacao.AUTORIDADE);
		etiquetaTitulo = new Etiqueta(Etiqueta.TITULO.getTag(), tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA ?  TipoCatalogacao.BIBLIOGRAFICA: TipoCatalogacao.AUTORIDADE);
		
		
		if(! tituloContemCampoDados(obj, etiquetaChamada, SubCampo.SUB_CAMPO_A, false)){
					
				etiquetaChamada = recuperaEtiquetaTitulo(etiquetaChamada, etiquetasBuscadas); 
					
					if(etiquetaChamada != null){
						CampoDados c = new CampoDados(etiquetaChamada, obj, -1);
						new SubCampo(SubCampo.SUB_CAMPO_A, c, -1); // cria um sub campo vazio e adiciona a lista de subcampos
					}
				}
		
		if(! tituloContemCampoDados(obj, etiquetaTitulo, SubCampo.SUB_CAMPO_A, false)){
			
			etiquetaTitulo = recuperaEtiquetaTitulo(etiquetaTitulo, etiquetasBuscadas); 
			
			if(etiquetaTitulo != null){
				CampoDados c = new CampoDados(etiquetaTitulo, obj, -1);
				new SubCampo(SubCampo.SUB_CAMPO_A, c, -1); // cria um sub campo vazio e adiciona a lista de subcampos
			}
		}
		
		camposNaoReservados = obj.getCamposDadosNaoReservados();
		camposReservados = obj.getCamposDadosReservados();
		dataModelCampos = new ListDataModel(camposNaoReservados);
		
	}
	
	
	
	
	

	
	/**
	 * 
	 * M�todo que seleciona o id do material clicado na rich:tree.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painelLateralFormCatalogacao.jsp</li>
	 *   </ul>
	 *
	 *
	 * @param evt
	 * @throws ArqException 
	 */
	public void selecinouMaterialEdicao(NodeSelectedEvent evt){
		
		HtmlTree tree = (HtmlTree) evt.getComponent();
		if(tree.getRowKey() != null){
			idMaterialSelecionadoEdicao  = (Integer.parseInt(  tree.getRowKey().toString() )  );
		}else{
			// n�o faz nada, geralmente esse m�todo � chamado duas vezes quando o usu�rio clica em alterar material, na segunda vez vem nulo.
		}
	}
	
	/**
	 * 
	 * M�todo que iniciar a edi��o das informa��es de um material selecionado a partir da p�gina de cataloga��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painelLateralFormCatalogacao.jsp</li>
	 *   </ul>
	 *
	 * @param evt
	 * @throws ArqException 
	 */
	public String redirecionaPaginaEdicaoMaterial() throws ArqException{
		
		if(obj.getFormatoMaterial() != null && idMaterialSelecionadoEdicao != null){
			
			if(! obj.getFormatoMaterial().isFormatoPeriodico()){
				
				EditaMaterialInformacionalMBean mbean = getMBean("editaMaterialInformacionalMBean");
				return mbean.iniciarParaEdicaoExemplaresVindoPaginaCatalogacao(idMaterialSelecionadoEdicao);
				
			}else{
				
				EditaMaterialInformacionalMBean mbean = getMBean("editaMaterialInformacionalMBean");
				return mbean.iniciarParaEdicaoFasciculosVindoPaginaCatalogaca(idMaterialSelecionadoEdicao);
			}
			
		}
		
		addMensagemErro("N�o foi poss�vel editar as informa��es do material selecionado");
		return null;
	}
	
	
	
	/**
	 * Remove um campo de dados.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 * @throws DAOException 
	 */
	public void removerCampoDados(ActionEvent e) throws DAOException{
		
		// remove da posi��o que o usu�rio selecionou na p�gina
		
		ClassificacaoBibliografica classificacaoRemovida = null;
		
		if(camposNaoReservados != null){
		
			CampoDados c = ( (CampoDados) dataModelCampos.getRowData() );
			
			if ( c.getEtiqueta() != null && c.getEtiqueta().getTag() != null ){
				classificacaoRemovida = ClassificacoesBibliograficasUtil.encontraClassificacaoByCampo(classificacoesUtilizadas, c.getEtiqueta().getTag());
			}
			
			c = null;
			
			camposNaoReservados.get(dataModelCampos.getRowIndex()).setTituloCatalografico(null); // remove o T�tulo do objeto
			camposNaoReservados.remove(dataModelCampos.getRowIndex());  // remove objeto do T�tulo
			
			dataModelCampos = new ListDataModel(camposNaoReservados);
			
		}
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		CatalogacaoUtil.configuraGruposEtiquetaVisiveis(obj); // na remo��o precisa reconfigurar os grupos
		
		// SE O CAMPO REMOVIDO ERA DE UMA CLASSIFICA��O TEM QUE ATUALIZAR ESSA INFORMA��O //
		if(classificacaoRemovida != null)		
			ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacaoRemovida);
		
	}
	
	
	
	/**
	 * Remove um campo de controle
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void removerCampoControle(ActionEvent e){
		// remove da posi��o que o usu�rio selecionou na p�gina
		int posicao = getParameterInt("posicaoCampoControleRemover");
		
		if(obj.getCamposControle() != null){
		
		// LIDER e 008 s�o obrigat�rios e n�o podem ser removidos
			if(obj.getCamposControle().get(posicao).getEtiqueta().getTag().equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag())
					|| obj.getCamposControle().get(posicao).getEtiqueta().getTag().equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO.getTag())){
				return;
			}
			
			obj.getCamposControle().remove(posicao);
		}
	}
	
	
	/**
	 * Adiciona um sub Campo vazio ao campo de dados para a hora da cataloga��o.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 */
	public void adicionarNovoSubCampo(ActionEvent e){
		CampoDados c = (CampoDados) dataModelCampos.getRowData();
		new SubCampo(c); // cria um sub campo vazio e adiciona a lista de subcampos
	}
	
	
	
	
	
	
	/**
	 * M�todo que remove o subcampo selecionado pelo usu�rio
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void removeSubCampoDados(ActionEvent e){
		// Remove da cole��o de subcampos do campo selecionado o subcampos que o usu�rio selecionou
		
		if(camposNaoReservados != null){
		
			// Remove o subcampo do campo e o campo do subcampo
			camposNaoReservados.get(dataModelCampos.getRowIndex()).getSubCampos().get(
					((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex() ).setCampoDados(null);
			
			camposNaoReservados.get(dataModelCampos.getRowIndex()).getSubCampos().remove(
					( (CampoDados)  dataModelCampos.getRowData() ).getDataModelSubCampos().getRowIndex() );
		
		}
	}
	
	
	
	
	/**
	 * M�todo que move o campo MARC selecionado para uma posi��o acima (trocando de posi��o com o campo acima dele se existir)
	 * 
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void moverCampoDadosCima(ActionEvent e){
		
		if(camposNaoReservados != null){
			
			int posicaoCampoSelecionado = dataModelCampos.getRowIndex();
			
			if(posicaoCampoSelecionado > 0){ // Verifica se n�o � o primeiro campo, ser for n�o d� para subir mais
				
				CampoDados cTempSelecionado = camposNaoReservados.get( posicaoCampoSelecionado);
				
				CampoDados cTempProximo = camposNaoReservados.get(posicaoCampoSelecionado-1);
				
				/* Troca as posi��es, mesmo que o campo n�o v� ser movido por causa da ordena��o da  *
				 * tag do campo que � priorit�ria, pode trocar as posi��es que antes de ser salvo, o *
				 * sistema calcula as posi��es novamente de acordo com a posi��o na lista            */
				int posicaoTemp = cTempSelecionado.getPosicao();
				
				cTempSelecionado.setPosicao(  cTempProximo.getPosicao() );
				cTempProximo.setPosicao( posicaoTemp );
				
				camposNaoReservados.set(posicaoCampoSelecionado, cTempProximo);
				camposNaoReservados.set(posicaoCampoSelecionado-1, cTempSelecionado);
				
				// E ordena novamente
				Collections.sort(camposNaoReservados, new CampoDadosByEtiquetaPosicaoComparator());
				dataModelCampos = new ListDataModel(camposNaoReservados);
			}
		}
	}
	
		
	/**
	 * M�todo que move o campo MARC selecionado uma posi��o abaixo (trocando de posi��o com o campo abaixo dele se existir)
	 * 
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void moverCampoDadosBaixo(ActionEvent e){
		
		if(camposNaoReservados != null){
			
			int posicaoCampoSelecionado = dataModelCampos.getRowIndex();
			
			int posicaoMaxima = dataModelCampos.getRowCount();
			
			if(posicaoCampoSelecionado < posicaoMaxima-1){ // Verifica se n�o � o �ltimo campo, ser for n�o pode ir mais para baixo
				
				CampoDados cTempSelecionado = camposNaoReservados.get( posicaoCampoSelecionado);
				
				CampoDados cTempProximo = camposNaoReservados.get(posicaoCampoSelecionado+1);
				
				/* Troca as posi��es, mesmo que o campo n�o v� ser movido por causa da ordena��o da  *
				 * tag do campo que � priorit�ria, pode trocar as posi��es que antes de ser salvo, o *
				 * sistema calcula as posi��es novamente de acordo com a posi��o na lista            */
				
				int posicaoTemp = cTempSelecionado.getPosicao();
				
				cTempSelecionado.setPosicao(  cTempProximo.getPosicao() );
				cTempProximo.setPosicao( posicaoTemp );
				
				camposNaoReservados.set(posicaoCampoSelecionado, cTempProximo);
				camposNaoReservados.set(posicaoCampoSelecionado+1, cTempSelecionado);
				
				// E ordena novamente
				Collections.sort(camposNaoReservados, new CampoDadosByEtiquetaPosicaoComparator());
				dataModelCampos = new ListDataModel(camposNaoReservados);
			}
		}
		
	}
	
	
	/**
	 * M�todo que move o sub campo MARC selecionado uma posi��o acima (trocando de posi��o com o sub campo acima dele se existir)
	 * 
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void moverSubCampoCima(ActionEvent e){
		
		
		if(camposNaoReservados != null){
			
			int posicaoSubCampoSelecionado = ( (CampoDados)  dataModelCampos.getRowData() ).getDataModelSubCampos().getRowIndex();
			
			if(posicaoSubCampoSelecionado > 0){ // Verifica se n�o � o primeiro campo, ser for n�o d� para subir mais
				
				CampoDados campoSelecionado = (CampoDados)  dataModelCampos.getRowData();
				
				if(campoSelecionado != null){
				
					SubCampo subTempSelecionado = campoSelecionado.getSubCampos().get(posicaoSubCampoSelecionado);
					
					SubCampo subTempSelecionadoProximo = campoSelecionado.getSubCampos().get(posicaoSubCampoSelecionado-1);
					
					// Troca as posi��es
					int posicaoTemp = subTempSelecionado.getPosicao();
					
					subTempSelecionado.setPosicao(  subTempSelecionadoProximo.getPosicao() );
					subTempSelecionadoProximo.setPosicao( posicaoTemp );
					
					campoSelecionado.getSubCampos().set(posicaoSubCampoSelecionado, subTempSelecionadoProximo);
					campoSelecionado.getSubCampos().set(posicaoSubCampoSelecionado-1, subTempSelecionado);
				}
			}
		}
		
	}
	
	
	/**
	 * M�todo que move o sub campo MARC selecionado uma posi��o abaixo (trocando de posi��o com o sub campo abaixo dele se existir)
	 * 
	 *   <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void moverSubCampoBaixo(ActionEvent e){
		
		if(camposNaoReservados != null){
			
			int posicaoSubCampoSelecionado = ( (CampoDados)  dataModelCampos.getRowData() ).getDataModelSubCampos().getRowIndex();
			
			int posicaoMaxima = ( (CampoDados)  dataModelCampos.getRowData() ).getDataModelSubCampos().getRowCount();
			
			if(posicaoSubCampoSelecionado < posicaoMaxima-1){ // Verifica se n�o � o �ltimo campo, ser for n�o pode ir mais para baixo
				
				CampoDados campoSelecionado = (CampoDados)  dataModelCampos.getRowData();
				
				if(campoSelecionado != null){
				
					SubCampo subTempSelecionado = campoSelecionado.getSubCampos().get(posicaoSubCampoSelecionado);
					
					SubCampo subTempSelecionadoProximo = campoSelecionado.getSubCampos().get(posicaoSubCampoSelecionado+1);
					
					// Troca as posi��es
					int posicaoTemp = subTempSelecionado.getPosicao();
					
					subTempSelecionado.setPosicao(  subTempSelecionadoProximo.getPosicao() );
					subTempSelecionadoProximo.setPosicao( posicaoTemp );
					
					campoSelecionado.getSubCampos().set(posicaoSubCampoSelecionado, subTempSelecionadoProximo);
					campoSelecionado.getSubCampos().set(posicaoSubCampoSelecionado+1, subTempSelecionado);
				}
			}
		}
	}
	
	
	
	
	
	/**
	 *    M�todo que busca a etiqueta de acordo com a tag digitada pelo usu�rio e adiciona essa
	 * tag ao campo dela.
	 * 
	 *    Se a tipo de cataloga��o for BIBLIOGRAFICA apenas etiqueta bibliogr�ficas devem ser buscas,
	 * se for AUTORIDADES apenas etiquetas de AUTORIDADES e assim at� quantos tipo de cataloga��o
	 * essa tela for suportar.
	 * 
	 *     <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 *   quando o usu�rio clica digita alguma coisa no campo da etiqueta.
	 * @throws DAOException
	 */
	public void buscaEtiqueta(ActionEvent e) throws DAOException{
		
		CampoDados c = (CampoDados) dataModelCampos.getRowData();
		
		// Verifica se o usu�rio n�o digitou o c�digo de um campo de controle, porque aqui � s� dados
		if(c.getEtiqueta().getTag().length() == 3 && ! c.getEtiqueta().getTag().startsWith("00")
				&& ! c.getEtiqueta().getTag().equals("LDR")){
			
			Etiqueta etiqueta = null;
			
			etiqueta = new Etiqueta(c.getEtiqueta().getTag(), tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA ?  TipoCatalogacao.BIBLIOGRAFICA: TipoCatalogacao.AUTORIDADE);
			
			etiqueta = recuperaEtiquetaTitulo(etiqueta, etiquetasBuscadas); 
			
			
			if(etiqueta != null ){
				
				// N�O busca as etiqueta reservadas do sistema, sen�o dar erro na p�gina //
				if( etiqueta.equals(Etiqueta.CODIGO_DA_BIBLIOTECA)
						||  etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES)
						||  etiqueta.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO)
						||  etiqueta.equals(Etiqueta.CODIGO_DA_BIBLIOTECA_AUTORIDADES)
						||  etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES_AUTORIDADES)
						||  etiqueta.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO_AUTORIDADES)){
					
					// N�o � uma etiqueta de uso reservado porque o tipo (-1) � invalido
					c.setEtiqueta(new Etiqueta(etiqueta.getTag(), (short) -1, "CAMPO DE USO RESERVADO"));
					
				}else{
					
					c.setEtiqueta(etiqueta);
					
					// Atualiza campo classifica��o, caso o usu�rio altera uns dos campos de classifica��o. //
					obj.setCamposDados(atribuiCamposDadosAoTitulo());
					
					// Se o Campo anterior alterado ou o novo campo era um campo utilizado na classicaaco classifica��o, pricisa atualizar
					if(  ClassificacoesBibliograficasUtil.isCampoUtilizadoClassificacao(new String[]{ etiqueta.getTag() } ) ){
					
						ClassificacaoBibliografica classificacaoAlteracao = ClassificacoesBibliograficasUtil
							.encontraClassificacaoByCampo(classificacoesUtilizadas, new String[]{ etiqueta.getTag() } );
						
						ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacaoAlteracao);
						
					}
				
				}
				
				// se mudar o campo, apaga as refer�ncia dos subcampos das autoridades que por acaso estiverem nos subcampos.
				 // se o usu�rio mudar tem que mudar a descri��o transiente
				if(c.getSubCampos() != null)  {
					for (SubCampo s : c.getSubCampos()) {
						s.setSubCampoAutoridade(null);
						s.setDescricaoSubCampo(CatalogacaoUtil.retornaDescritorSubCampo(c.getEtiqueta(), s.getCodigo()));
					}
				}
				
			}else{
				c.setEtiqueta(new Etiqueta(c.getEtiqueta().getTag(), (short) -1, "CAMPO N�O CADASTRADO"));
			}
			
		}else{
			c.setEtiqueta(new Etiqueta(c.getEtiqueta().getTag(), (short) -1));
		}
	}
	
	
	
	
	/**
	 *    <p>Chamado automaticamente para salva a cataloga��o na base </p>
	 *    <p>Chamado automaticamente na p�gina de cataloga��o a cada 2mim com o
	 *    <code> a4j:poll </code> para salvar a cataloga��o automaticamente
	 *    para o usu�rio n�o perder as suas informa��o caso ocorra algum problema no sistema
	 *     e n�o deixa a sess�o do usu�rio expirar.</p>
	 *
	 *   <br/><br/>
	 * 		Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 *   <br/><br/>
	 * @throws ArqException
	 * 
	 */
	public void salvaCatalogacaoAutomaticamente(ActionEvent evt) throws ArqException{
		
		long tempo  = System.currentTimeMillis();
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		// apaga as mensagens em request, porque fica aparecendo duplicado //
		getCurrentRequest().setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, new ListaMensagens());
		
		try{
			switch(tipoCatagalocao){
			
				case TipoCatalogacao.BIBLIOGRAFICA:
					

					if(editando){
					
						prepareMovimento(SigaaListaComando.ATUALIZA_TITULO);
						
						MovimentoAtualizaTitulo mov = new MovimentoAtualizaTitulo(obj, arquivo, apagarArquivoDigitalSalvo, true, classificacoesUtilizadas);
						mov.setCodMovimento(SigaaListaComando.ATUALIZA_TITULO);
						
						execute(mov);
							
						prepareMovimento(SigaaListaComando.ATUALIZA_TITULO);
						
					}else{   // CRIANDO O T�TULO
						
						MovimentoCatalogaTitulo mov = null;
						prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
						mov = new MovimentoCatalogaTitulo(obj, arquivo, false, classificacoesUtilizadas);
						mov.setCodMovimento(SigaaListaComando.CATALOGA_TITULO);
						
						execute(mov);
						
						prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
					}
					
					addMensagemInfoAjax("T�tulo salvo automaticamente pelo sistema ");
					
				break;
				case TipoCatalogacao.AUTORIDADE:

					Autoridade autoridade = CatalogacaoUtil.criaAutoridaeAPartirTitulo(obj);
					
					if(editando){
						
						autoridade.setId(obj.getId()); // o id da autoridade estava no t�tulo.
						
						MovimentoAtualizaAutoridade mov = new MovimentoAtualizaAutoridade(autoridade, true, classificacoesUtilizadas);
						mov.setCodMovimento(SigaaListaComando.ATUALIZA_AUTORIDADE);
						prepareMovimento(SigaaListaComando.ATUALIZA_AUTORIDADE);
						
						execute(mov);
						
						prepareMovimento(SigaaListaComando.ATUALIZA_AUTORIDADE);
						
					}else{
						
						MovimentoCatalogaAutoridade mov = null;
						prepareMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
						mov = new MovimentoCatalogaAutoridade(autoridade, false);
						mov.setCodMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);

						execute(mov);
						
						prepareMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
						
						// atualiza o T�tulo para pegar o n�mero do sistema que foi gerado
						//autoridade = getGenericDAO().refresh(autoridade);
						obj = CatalogacaoUtil.criaTituloAPartirAutoridade(autoridade);  // IMPORTANTE
					}
					
					addMensagemInfoAjax("Autoridade salva automaticamente pelo sistema ");
					
				break;
				
			}
		}catch(Exception ex){
			
			switch(tipoCatagalocao){
				case TipoCatalogacao.BIBLIOGRAFICA:
					addMensagemErroAjax("N�o foi poss�vel salvar a cataloga��o. ");
				break;
				case TipoCatalogacao.AUTORIDADE:
					addMensagemErroAjax("N�o foi poss�vel salvar a autoridade. ");
				break;
			}
		}
		
		System.out.println("Salvar automaticamente demorou>>>>> "+((System.currentTimeMillis()-tempo)/1000)+" ms.");
		
	}
	
	
	
	
	
	/**
	 *     Chamado quando o usu�rio mudou de subcampo. Deve-se apagar as refer�ncia dos subcampos das autoridades.
	 *
	 *     <br/><br/>
	 *     Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void mudarSubCampo(ActionEvent e) throws DAOException{
		
		CampoDados d = camposNaoReservados.get(dataModelCampos.getRowIndex());
		
		SubCampo subCampoAlterado = d.getSubCampos().get(((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex() );
		
		for (SubCampo s: d.getSubCampos()) {
			s.setSubCampoAutoridade(null);
			
			if( s.equals(subCampoAlterado) ){
				s.setDescricaoSubCampo(CatalogacaoUtil.retornaDescritorSubCampo(d.getEtiqueta(), s.getCodigo())); // se o usu�rio mudar tem que mudar a descri��o transiente
			}
		}
		
	}
	
	
	
	
	/**
	 * Monta a ajuda do campo de controle com base no campo de controle que o usu�rio clicou.
	 * 
	 * <br/><br/>
	 * Chamado no m�todo que edita os campos de controle <code>#editarCampoControle()</code>
	 * 
	 */
	private void montaDadosAjudaControle(Etiqueta etiquetaControleAtual) throws DAOException{
		long tempo = System.currentTimeMillis();
		
		String tag = etiquetaControleAtual.getTag();
		
		if( StringUtils.isEmpty(tag) || tag.length() != 3){
			return;
		}
		
		ajudaCampo = BibliotecaUtil.montaAjudaCamposControle(etiquetaControleAtual, obj.getFormatoMaterial(), codigoCategoriaMaterial);
	
		System.out.println(">>>>>>>> Montar Ajuda Controle demorou: "+((System.currentTimeMillis()-tempo)/1000)+"ms");
	}
	
	
	
	/**
	 *   Monta a ajuda do campo de dados com base no campo que o usu�rio clicou.
	 *
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void montaDadosAjudaDados(ActionEvent e) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		
		CampoDados c = (CampoDados) dataModelCampos.getRowData();
		
		String tag = c.getEtiqueta().getTag();
		
		if( StringUtils.isEmpty(tag) || tag.length() != 3){
			return;
		}
		
		Etiqueta etiqueta = null;
		
		etiqueta = new Etiqueta(tag, tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA ?  TipoCatalogacao.BIBLIOGRAFICA: TipoCatalogacao.AUTORIDADE);
		
		etiqueta = recuperaEtiquetaTitulo(etiqueta, etiquetasBuscadas); 
		
		ajudaCampo = CatalogacaoUtil.montaAjudaCamposDados(etiqueta, usarTelaCatalogacaoCompleta);
		
		System.out.println(">>>>>>>> Montar Ajuda demorou: "+((System.currentTimeMillis()-tempo)/1000)+"ms");
		
	}
	
	
	
	
	
	
	/**
	 *   <p> M�todo chamado para confirma o cutter correto da cataloga��o entre os cutter sugeridos pelo sistema.</p>
	 *   <p> <i> Caso este m�todo receba com par�metro o id de algum cutter, � porque o usu�rio selecionou outro diferente,
	 *   caso n�o recebe nenhum par�metro � porque o usu�rio confirmou a escolha do sistema. </i> </p>
	 * 
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/buscaTabelaCutter.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String confirmaCutter(){
		
		Integer idTabelaCutterSelecionada = getParameterInt("idTabelaCutterEscolhida");
		
		if(idTabelaCutterSelecionada != null){
			
			for (DadosTabelaCutter dadoCutter : suguestoesTabelaCuter) {
				if( idTabelaCutterSelecionada.equals( dadoCutter.getIdTabelaCutter()) ){
					if(indiceCampoSelecionado >= 0 && indiceSubCampoSelecionado >= 0){
						camposNaoReservados.get(indiceCampoSelecionado).getSubCampos().get( indiceSubCampoSelecionado ).
								setDado(dadoCutter.getCodigoCutterCompleto());
						break;
					}
				}
			}
			
		}else{  // usu�rio confirmou o sugerido pelo sistema
			for (DadosTabelaCutter dadoCutter : suguestoesTabelaCuter) {
				if( dadoCutter.isCodigoCalculado() ){
					if(indiceCampoSelecionado >= 0 && indiceSubCampoSelecionado >= 0){
						camposNaoReservados.get(indiceCampoSelecionado).getSubCampos().get( indiceSubCampoSelecionado ).
								setDado(dadoCutter.getCodigoCutterCompleto());
						break;
					}
				}
			}
		}
		
		zeraIndicesCamposSelecionados();
		suguestoesTabelaCuter = new ArrayList<DadosTabelaCutter>(); // limpa os dados sugeridos
		
		return telaDadosTituloCatalografico();
	}
	
	
	
	/**
	 *     <p>M�todo chamado quando o usu�rio clica no link de buscar autoridades.</p>
	 *     <p>O m�todo que chama o MBean de busca de autoridades para o usu�rio buscar a autoridade
	 *  que deve preencher os dados do campo selecionado.</p>
	 * 
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @return
	 * @throws DAOException
	 */
	public String buscarAutoridadesPorNomeAutorizadoAutor() throws DAOException{
		
		CampoDados c = camposNaoReservados.get(dataModelCampos.getRowIndex());
		
		campoSelecionadoParaDadosAutoridade = c;
		
		//SubCampo sub = c.getSubCampos().get( c.getDataModelSubCampos().getRowIndex());
		/*
		 * No caso de assunto o sistema deve buscar pelos dados que est�o n�o apenas no sub campo "a"
		 * Mas concatenar todos os dados dos subcampos do campo selecionado
		 */
		
		String entradaAutorizada = "";
		
		for (SubCampo sub : c.getSubCampos()) {
			
			if(sub != null){
				entradaAutorizada += sub.getDado()+" ";
			}
		}
		
		CatalogaAutoridadesMBean bean = getMBean("catalogaAutoridadesMBean");
		
		return bean.iniciarPesquisaSelecaoAutoridadeAutor(entradaAutorizada);
		
	}
	
	
	/**
	 *     M�todo chamado quando o usu�rio clica no link de buscar autoridades.
	 *     O m�todo chama o MBean de busca de autoridades para o usu�rio buscar a autoridade de assunto
	 * cujas informa��es v�o preencher o campo selecionado do T�tulo
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @return
	 * @throws DAOException
	 */
	public String buscarAutoridadesPorNomeAutorizadoAssunto() throws DAOException{
		
		CampoDados c = camposNaoReservados.get(dataModelCampos.getRowIndex());
		
		campoSelecionadoParaDadosAutoridade = c;
		
		/*
		 * No caso de assunto o sistema deve buscar pelos dados que est�o n�o apenas no sub campo "a"
		 * Mas concatenar todos os dados dos subcampos do campo selecionado
		 */
		
		String entradaAutorizada = "";
		
		for (SubCampo sub : c.getSubCampos()) {
			
			if(sub != null){
				entradaAutorizada += sub.getDado()+" ";
			}
		}
		
	
		
		CatalogaAutoridadesMBean bean = getMBean("catalogaAutoridadesMBean");
		
		return bean.iniciarPesquisaSelecaoAutoridadeAssunto(entradaAutorizada);
		
	}
	
	
	
	/**
	 *      Coloca os dados da autoridade de autor selecionada no respectivo subcampo e salva
	 * uma refer�ncia a essa autoridade.
	 * 
	 *     <br/><br/>
	 *     Chamado a partir do MBean: {@link CatalogaAutoridadesMBean#selecionarAutoridade}
	 *     <br/>
	 *     M�todo n�o invocado por nenhuma JSP.
	 * 
	 * @param entradaAutorizada
	 * @return
	 * @throws DAOException
	 */
	public String configuraEntradaAutorizadaAutor(Integer idAutoridade) throws DAOException{
		
		if(idAutoridade != null){
		
			AutoridadeDao dao = null;
			
			try{
				
				dao = getDAO(AutoridadeDao.class);
				
				Autoridade autoridade = dao.findAutoriadeByIdInicializandoDados(idAutoridade);
				
				if(autoridade.getCamposDados() != null)
				for (CampoDados campo : autoridade.getCamposDados()) {
					
					// 100, 110 ou 111
					if(campo.getEtiqueta().equals(Etiqueta.NOME_PESSOAL)
							|| campo.getEtiqueta().equals(Etiqueta.NOME_CORPORATIVO)
							|| campo.getEtiqueta().equals(Etiqueta.NOME_EVENTO)){
						
						/* Guarda as possi��es j� adicionadas do sub campo, para n�o adicionar sempre no mesmo sub campo. */
						Map<Character, Integer> posicoesJaRetornadas = new HashMap<Character, Integer>();
						
						for (SubCampo subCampoAutoridade : campo.getSubCampos()) {
							
							Integer ultimaPosicaoUsada = posicoesJaRetornadas.get(subCampoAutoridade.getCodigo());
							
							if(ultimaPosicaoUsada == null)  ultimaPosicaoUsada = -1;
							
							// como um subcampo pode ser repetido, pega o primeiro subcampo do campo ainda n�o retornado para  //
							// jogar o valor do subcampo da autoridade                                     //
							int posicaoSubCampoAindaNaoUsado = campoSelecionadoParaDadosAutoridade
									 .getMenorPosicaoSubCampo(subCampoAutoridade.getCodigo(), ultimaPosicaoUsada);
							
							if(posicaoSubCampoAindaNaoUsado >= 0){ // j� existe subcampo, apenas atualiza dos dados
								
								SubCampo sub = campoSelecionadoParaDadosAutoridade.getSubCampos().get(posicaoSubCampoAindaNaoUsado);
								sub.setDado(subCampoAutoridade.getDado());
								sub.setSubCampoAutoridade(subCampoAutoridade);
							
								posicoesJaRetornadas.put(subCampoAutoridade.getCodigo(), posicaoSubCampoAindaNaoUsado); // J� usou at� essa posi��o
								
							}else{ // ainda n�o existe o subcampo no t�tulo, cria um novo subcampo para o campo de dados com os dados da autoridade
								
								new SubCampo(subCampoAutoridade.getCodigo(), subCampoAutoridade.getDado(),
										campoSelecionadoParaDadosAutoridade, subCampoAutoridade, -1);
								
								posicoesJaRetornadas.put(subCampoAutoridade.getCodigo(), campoSelecionadoParaDadosAutoridade.getSubCampos().size()); // J� usou at� essa posi��o
								
								campoSelecionadoParaDadosAutoridade.setDataModelSubCampos(
										new ListDataModel(campoSelecionadoParaDadosAutoridade.getSubCampos()));
								
							}
							
						}
						
						break;
					}
					
				}
			}finally{
				if(dao != null) dao.close();
			}
		
		}
		
		return telaDadosTituloCatalografico();
		
	}
	
	
	
	
	/**
	 *      Coloca os dados da autoridade de assunto selecionada no respectivo subcampo e salva
	 * uma refer�ncia a essa autoridade.
	 * 
	 *      <br/><br/>
	 *      Chamado a partir do MBean: {@link CatalogaAutoridadesMBean#selecionarAutoridade}
	 *      <br/>
	 *      M�todo n�o invocado por nenhuma JSP.
	 * @param entradaAutorizada
	 * @return
	 * @throws DAOException
	 */
	public String configuraEntradaAutorizadaAssunto(Integer idAutoridade) throws DAOException{
		
		if(idAutoridade != null){
		
			AutoridadeDao dao = null;
			
			try{
				
				dao = getDAO(AutoridadeDao.class);
			
				Autoridade autoridade = dao.findAutoriadeByIdInicializandoDados(idAutoridade);
				
				if(autoridade.getCamposDados() != null)
				for (CampoDados campoAutoridade : autoridade.getCamposDados()) {
					
					// 150, 151 ou 180
					if(campoAutoridade.getEtiqueta().equals(Etiqueta.CABECALHO_TOPICOS)
							|| campoAutoridade.getEtiqueta().equals(Etiqueta.CABECALHO_NOME_GEOGRAFICO)
							|| campoAutoridade.getEtiqueta().equals(Etiqueta.CABECALHO_GERAL_SUBDIVISAO)){
						
						/* Guarda as possi��es j� adicionadas do sub campo, para n�o adicionar sempre no mesmo sub campo. */
						Map<Character, Integer> posicoesJaRetornadas = new HashMap<Character, Integer>();
						
						for (SubCampo subCampoAutoridade : campoAutoridade.getSubCampos()) {
											
							Integer ultimaPosicaoUsada = posicoesJaRetornadas.get(subCampoAutoridade.getCodigo());
							
							if(ultimaPosicaoUsada == null)  ultimaPosicaoUsada = -1;
							
							// como um subcampo pode ser repetido, pega o primeiro subcampo do campo ainda n�o retornado para  //
							// jogar o valor do subcampo da autoridade                                     //
							int posicaoSubCampoAindaNaoUsado = campoSelecionadoParaDadosAutoridade
									.getMenorPosicaoSubCampo(subCampoAutoridade.getCodigo(), ultimaPosicaoUsada);
							
							if(posicaoSubCampoAindaNaoUsado >= 0 ){ // j� existe subcampo, apenas atualiza dos dados dele com os dados da autoridade
								
								SubCampo sub = campoSelecionadoParaDadosAutoridade.getSubCampos().get(posicaoSubCampoAindaNaoUsado);
								sub.setDado(subCampoAutoridade.getDado());
								sub.setSubCampoAutoridade(subCampoAutoridade);
								
								posicoesJaRetornadas.put(subCampoAutoridade.getCodigo(), posicaoSubCampoAindaNaoUsado); // J� usou at� essa posi��o
								
							}else{ // ainda n�o existe o subcampo no t�tulo, cria um novo subcampo para o campo de dados com os dados da autoridade
								
								new SubCampo(subCampoAutoridade.getCodigo(), subCampoAutoridade.getDado(),
										campoSelecionadoParaDadosAutoridade, subCampoAutoridade, -1);
								
								posicoesJaRetornadas.put(subCampoAutoridade.getCodigo(), campoSelecionadoParaDadosAutoridade.getSubCampos().size()); // J� usou at� essa posi��o
								
							}
							
						}
						
						
						break;
					}
					
				}
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return telaDadosTituloCatalografico();
		
	}
	
	
	
	
	/**
	 *      Quando o usu�rio escolhe a planilha de cataloga��o, o sistema chama esse m�todo para
	 * carregar os dados da planilha escolhida no objeto  <code>tituloTemp</code> e mostrar ao
	 * usu�rio, ainda na p�gina que escolhe a planilha.
	 * 
	 *    <p> Obs.:  S� depois que o usu�rio confirmar a planilha escolhida � que a cataloga��o
	 * vai realmente come�ar pelo m�todo, <code>CatalogacaoMBean#iniciarPlanilha()</code></p>
	 * 
	 *    <br/><br/>
	 * 	  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaCatalogacao.jsp
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public void carregaDadosPlanilha(ValueChangeEvent event) throws DAOException{
		
		idPlanilhaEscolhida = (Integer) event.getNewValue();
		
		if(idPlanilhaEscolhida != -1){
			
			// a planilha escolhida //
			PlanilhaCatalogacao planilha = getGenericDAO().findByPrimaryKey(
					idPlanilhaEscolhida, PlanilhaCatalogacao.class);
			
			
			
			if(isTipoCatalogacaoBibliografica()){
			
				// pego o formato do T�tulo que vem da planilha //
				FormatoMaterial formatoMaterial =  getGenericDAO().findByPrimaryKey(
					planilha.getIdFormato(), FormatoMaterial.class);
				
				tituloTemp = new TituloCatalografico(formatoMaterial);
			}
			
			
			if(isTipoCatalogacaoAutoridade()){
				tituloTemp = new TituloCatalografico();
			}
			
			// Autoridades n�o tem formato material
			
		
				
			CatalogacaoUtil.criaCamposDeControle(planilha, tituloTemp, etiquetasBuscadas);
			CatalogacaoUtil.criaCamposDeDados(planilha, tituloTemp, etiquetasBuscadas);
			
			if(tituloTemp.getCamposControle() != null)
				Collections.sort(tituloTemp.getCamposControle(), new CampoControleByEtiquetaComparator());
			
			if(tituloTemp.getCamposDados() != null)
				Collections.sort(tituloTemp.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
				
		}
	}
	
	
	
	/**
	 * <p>M�todo chamado para carregar os campos da planilha utilizada na cataloga��o e o usu�rio pode adicionar algum desses campos.</p>
	 * <p>O usu�rio s� pode adicionar campos presentes na planilha utilizada.</p>
	 * @void
	 */
	public void carregaCamposPlanilhaSimplificada(ActionEvent evt) throws DAOException{
		tituloTemp = CatalogacaoUtil.carregaCamposPlanilhaCatalogacaoSimplificada(idPlanilhaCatalogacaoSimplificada, isTipoCatalogacaoBibliografica(), etiquetasBuscadas);
		
		for (CampoDados campo : tituloTemp.getCamposDadosNaoReservados()) {
			campo.getEtiqueta().setGrupo(CatalogacaoUtil.retornaGrupoEtiqueta(campo.getEtiqueta()));
		}
		
		CatalogacaoUtil.configuraGruposEtiquetaVisiveis(tituloTemp);
		
	}
	
	
	/* *********************** m�todos de redirecionamento para as paginas ******************* */
	
	/**
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 *                              /biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp
	 *                              /biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp
	 */
	public String telaBuscaInformacoesSipacAPartirNumeroPatrimonio(){
		return forward(PAGINA_BUSCA_SIPAC_A_PARTIR_NUMERO_PATRIMONIO);
	}
	
	
	/**
	 * M�todo invocado de nenhuma p�gina JSP.
	 */
	public String telaEscolheFormatoMaterial(){
		return forward(PAGINA_ESCOLHE_FORMATO_MATERIAL);
	}
	
	/**
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaTituloCatalografico.jsp
	 */
	public String telaEscolhePlanilhaBibliografica(){
		return forward(PAGINA_ESCOLHE_PLANILHA_BIBLIOGRAFICA);
	}
	
	/**
	 *  Chamado a partir da p�gina:  sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaAutoridade.jsp
	 */
	public String telaEscolhePlanilhaAutoridades(){
		return forward(PAGINA_ESCOLHE_PLANILHA_AUTORIDADES);
	}
	
	
	/**
	 *  M�todo invocado de nenhuma p�gina JSP.
	 */
	public String telaDadosTituloCatalografico(){
		return forward(PAGINA_PREENCHE_DADOS_TITULO_CATALOGRAFICO);
	}

	
	
	/**
	 * 	  <p>Redireciona para a tela na qual o usu�rio vai escolher entre os poss�veis c�digos da tabela cutter a que ele
	 * acha que mais se a pr�xima do sobre nome do autor. </p>
	 * 
	 *    <p>Antes de redirecionar o usu�rio para a p�gina, calculo as sugest�es de c�digo cutter.</p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException
	 */
	public String telaBuscaTabelaCutter() throws DAOException{
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		suguestoesTabelaCuter  = CatalogacaoUtil.gerarCodigoCutter(obj);
		
		if(suguestoesTabelaCuter == null || suguestoesTabelaCuter.size() == 0){
			addMensagemWarning("O C�digo Cutter n�o p�de ser gerado.");
			return telaDadosTituloCatalografico(); // fica na mesma p�gina de edi��o dos campos MARC
		}else{
		
			zeraIndicesCamposSelecionados();
			
			// Guarda os indices do campo selecionado pelo usu�rio para saber onde colocar o c�digo cutter selecionado //
			indiceCampoSelecionado = dataModelCampos.getRowIndex();
			indiceSubCampoSelecionado =  ((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex();
			
			return forward(PAGINA_BUSCA_TABELA_CUTTER);
			
		}
	}
	
	/**
	 * 	  <p>Guarda o sub campo 090$d selecionado </p>
	 * 
	 *    <p>Antes de abrir o model panel para o usu�rio escolher os dados que v�o preencher o sub campo 090$d.</p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException
	 */
	public void selecionaCampo090D(ActionEvent e){
		
		zeraIndicesCamposSelecionados();
		
		// Guarda os indices do campo selecionado pelo usu�rio para saber onde colocar o c�digo cutter selecionado //
		indiceCampoSelecionado = dataModelCampos.getRowIndex();
		indiceSubCampoSelecionado =  ((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex();
	}
	
	
	
	/**
	 * 	  <p>Retorna os dados que o usu�rio pode utilizar pra preencher o sub campo 090$d </p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/modalPanelFormCatalogacao.jsp
	 * @throws DAOException
	 */
	public List<String> getDadosCampo090D(){
		
		String siglasTrabalhosAcademicos = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.SIGLAS_IDENTIFICAO_TRABALHOS_ACADEMICOS);
		
		if(StringUtils.notEmpty( siglasTrabalhosAcademicos )){
			siglasTrabalhosAcademicos = siglasTrabalhosAcademicos.replace(" ", ""); 
			return Arrays.asList(siglasTrabalhosAcademicos.split(","));
		}else
			return new ArrayList<String>();
		
	}
	
	/**
	 *  <p>Atribuir o valor selecionado pelo usu�rio ao sub campo 090$d</p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/modalPanelFormCatalogacao.jsp
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void atribuiValorCampo090D(ActionEvent e) throws DAOException{		
		String valorEscolhido = getCurrentRequest().getParameter("valorCampo090D");
		if(indiceCampoSelecionado >= 0 && indiceSubCampoSelecionado >= 0)
			camposNaoReservados.get(indiceCampoSelecionado).getSubCampos().get( indiceSubCampoSelecionado ).setDado(valorEscolhido);
	}
	
	
	
	/**
	 * 	  Redireciona para a tela na qual o usu�rio pode buscar as editoras cadastradas no SIPAC.
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 */
	public String telaBuscaEditorasCadastradasSipac(){
		
		zeraIndicesCamposSelecionados();
		
		// Guarda os indices do campo selecionado pelo usu�rio para saber onde colocar a editora selecionada //
		indiceCampoSelecionado = dataModelCampos.getRowIndex();
		indiceSubCampoSelecionado =  ((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex();
		
		return forward(PAGINA_BUSCA_EDITORAS_CADASTRADAS_SIPAC );
	}
	
	
	
	
	/**
	 *  M�todo que atribui novamente ao t�tulo os campos de dados que est�o sendo integrados na p�gina por duas cole��es diferentes
	 */
	private List<CampoDados> atribuiCamposDadosAoTitulo(){
		List<CampoDados> camposObjeto = new ArrayList<CampoDados>();
		
		if(camposReservados != null)
			camposObjeto.addAll(camposReservados);
		
		if(camposNaoReservados != null)
			camposObjeto.addAll(camposNaoReservados);
		
		return camposObjeto;
	}
	
	/**
	 * Separa a lista de campos de dados do T�tulo ou autoridade nas listas que ser�o usasdas na tela. 
	 * Antes de salvar tem que atribuir esses campos ao T�tulo ou Autoridade novamente. @link{this#atribuiCamposDadosAoTitulo}
	 * 
	 * Somente os campos de dados n�o reservados s�o alter�veis pelo usu�rio.
	 *
	 * @void
	 */
	private void atribuiCamposDoTituloAosCampoUsadosNaTela(){
		camposNaoReservados = obj.getCamposDadosNaoReservados();
		camposReservados = obj.getCamposDadosReservados();
		dataModelCampos = new ListDataModel(camposNaoReservados);
	}
	
	
	/**
	 * Zera os indices sempre antes de iniciar uma nova busca
	 */
	private void zeraIndicesCamposSelecionados(){
		indiceCampoSelecionado = -1;
		indiceSubCampoSelecionado = -1;
	}
	
	
	/**
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
	 */
	public String telaCampoControleLider(){
		return forward(PAGINA_CAMPO_LIDER);
	}
	
	
	/**
	 *  M�todo invocado de nenhuma p�gina JSP.
	 */
	public String telaCampoControle001(){
		return forward(PAGINA_CAMPO_001);
	}
	
	/**
	 * M�todo invocado de nenhuma p�gina JSP.
	 */
	public String telaCampoControle003(){
		return forward(PAGINA_CAMPO_003);
	}
	
	/**
	 * M�todo invocado de nenhuma p�gina JSP.
	 */
	public String telaCampoControle005(){
		return forward(PAGINA_CAMPO_005);
	}
	
	/**
	 * M�todo invocado de nenhuma p�gina JSP.
	 */
	public String telaCampoControle006(){
		return forward(PAGINA_CAMPO_006);
	}
	
	/**
	 * M�todo invocado de nenhuma p�gina JSP.
	 */
	public String telaCampoControle007(){
		return forward(PAGINA_CAMPO_007);
	}
	
	/**
	 * M�todo invocado de nenhuma p�gina JSP.
	 */
	public String telaCampoControle008(){
		return forward(PAGINA_CAMPO_008);
	}
	
	/* ************************************************************************************* */
	

	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// parte do arquivo em formato digital ///////////////////////////////
	
	/**
	 * 
	 *   Valida o arquivo que o usu�rio informou.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @param vce
	 */
	public void validaFormatoArquivo(ValueChangeEvent vce){
		
		if(getParameterBoolean("apagarArquivoSumetido")){

			// se o usu�rio clicou no bot�o remover arquivo n�o faz nada nesse m�todo.

		}else{
		
			UploadedFile arquivoNovo = (UploadedFile) vce.getNewValue();
			
			if(arquivoNovo != null){               // entra aqui quando o usu�rio submete um novo arquivo
				arquivo = arquivoNovo;
			}else{                        // a passa aqui quando o usu�rio submeto todo o formul�rio do t�tulo
				arquivo = (UploadedFile) vce.getOldValue();
			}
				
			
			
			if( arquivo != null ) {
				
				try{
					CatalogacaoUtil.validaFormatoArquivoCatalogacao(arquivo);
				}catch(NegocioException ne){
					addMensagens(ne.getListaMensagens());
					arquivo = null;
				}
			}
			
		}
	}
	
	
	/**
	 *  Pega o nome do arquivo salvo no banco.
	 */
	private String getNomeArquivoDigitalDoTitulo() throws ArqException{
		
		try {
			
			if(obj.getIdObraDigitalizada() != null)
				return EnvioArquivoHelper.recuperaNomeArquivo(obj.getIdObraDigitalizada());
			else
				return null;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(" N�o foi poss�vel recuperar o nome do arquivo ");
		}
		
	}
	
	
	/**
	 * Obt�m o nome do arquivo que o usu�rio submeteu para mostrar na p�gina para ele
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @throws ArqException
	 */
	public String getNomeArquivoTemporarioDoTitulo() throws ArqException{
		
		String nomeTemp = null;
		
		if(arquivo != null){                 // usu�rio submeteu novo arquivo
			nomeTemp = arquivo.getName();
		}else{
			
			if(! apagarArquivoDigitalSalvo){  // se o usu�rio n�o clicou em apagar o arquivo salvo na base
				
				nomeTemp = getNomeArquivoDigitalDoTitulo(); // pega o que estiver salvo se tiver
			}
		}
		
		if(nomeTemp != null && nomeTemp.length() > 30 )
			nomeTemp = nomeTemp.substring(0, 15) +" ... "+ nomeTemp.substring(nomeTemp.length()-15, nomeTemp.length());
		
		return nomeTemp;
	}
	
	
	/**
	 *    Chamado para apagar o arquivo submetido quando o usu�rio clica no respectivo bot�o.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void apagarArquivo(ActionEvent e){
		if(arquivo != null){
			arquivo = null; // apaga o arquivo submetido agora
			apagarArquivoDigitalSalvo = false;
		}else
			apagarArquivoDigitalSalvo = true;
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Retorna as planilhas cadastradas para o usu�rio escolher qual ser� usada para montar a tela da cataloga��o simplificada.
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/telaCatalogacaoSimplificada.jsp</li>
	 *  </ul>
	 */
	public Collection<SelectItem> getPlanilhasCatalogacaoSimplificadaComboBox() throws DAOException{
		if(isTipoCatalogacaoBibliografica())
			return getAllPlanilhaBibliograficaComboBox();
		else
			return getAllPlanilhaAutoridadesComboBox();
	}
	
	
	/**
	 * 
	 * M�todo que busca todos as planilhas cadastradas e monta uma cole��o de selecItem para mostrar
	 * para o usu�rio escolher.
	 * 
	 *  <br/><br/>
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaBibliografica.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem>  getAllPlanilhaBibliograficaComboBox() throws DAOException{
		return  toSelectItems(getGenericDAO().findByExactField(PlanilhaCatalogacao.class,
				"tipo", TipoCatalogacao.BIBLIOGRAFICA), "id", "nome") ;
	}


	/**
	 * 
	 *     M�todo que busca todas as planilhas cadastradas e monta uma cole��o
	 * de selecItem para mostrar para o usu�rio escolher.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaAutoridades.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem>  getAllPlanilhaAutoridadesComboBox() throws DAOException{
		return  toSelectItems(getGenericDAO().findByExactField(PlanilhaCatalogacao.class,
				"tipo", TipoCatalogacao.AUTORIDADE), "id", "nome") ;
	}

	
	
	/***
	 * <p>Inicia a classifica��o simplificada.</p>
	 * 
	 * <p>Sempre ao iniciar a classifica��o simplificada, o usu�rio deve ter escolhido uma planilha de cataloga��o. 
	 * A partir dessa planilha, os campos</p> 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/telaCatalogacaoSimplificada.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public void alterouPlanilhaCatalogacao(ActionEvent event) throws DAOException{
		
		if(idPlanilhaCatalogacaoSimplificada <= 0 ){
			addMensagemErroAjax("Escolha uma Planilha de Cataloga��o");
			return;
		}
		
		GenericDAO dao  = null;
		try{
			dao = getGenericDAO();
			planilhaCatalogacaoSimplificada = dao.findByPrimaryKey(idPlanilhaCatalogacaoSimplificada, PlanilhaCatalogacao.class);
			
			CatalogacaoUtil.montaCamposDadosCalalogacaoSimplificada(planilhaCatalogacaoSimplificada, obj, etiquetasBuscadas, editando);
			atribuiCamposDoTituloAosCampoUsadosNaTela();
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	
	/**
	 *   M�todo que tr�s todos os formatos de materiais de um T�tulo
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolheFormatoMaterial.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<FormatoMaterial> getAllFormatoMaterialComboBox() throws DAOException{
		return  (List<FormatoMaterial>) getGenericDAO().findAll(FormatoMaterial.class);
	}
	
	
	
	/**
	 * 
	 * Retorna as grandes �reas CNPq para serem exibidas em combox na p�gina de cataloga��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getGrandesAreasCNPqComboBox() throws DAOException{
		List<AreaConhecimentoCnpq> listaTemp = new ArrayList<AreaConhecimentoCnpq>();
		
		listaTemp.add( new AreaConhecimentoCnpq(-1, " -- Selecione -- ")); // N�o � salva no banco.
		for (AreaConhecimentoCnpq areaConhecimentoCnpq : getGrandesAreasCNPq()) {
			AreaConhecimentoCnpq area = new AreaConhecimentoCnpq(areaConhecimentoCnpq.getId(), areaConhecimentoCnpq.getNome());
			area.setCodigo(areaConhecimentoCnpq.getCodigo()); // Tem que colocar o c�digo sen�o n�o diferencia as �reas CNPq. 
			listaTemp.add( area );
		}
		
		Collections.sort(listaTemp, new Comparator<AreaConhecimentoCnpq>() {
			@Override
			public int compare(AreaConhecimentoCnpq a1, AreaConhecimentoCnpq a2) {
				return new Integer(a1.getId()).compareTo(a2.getId());
			}
		});
		
		return toSelectItems(listaTemp, "id", "nome");
	}
	
	/**
	 * Retorna as grandes �reas CNPq para associar com o T�tulo que est� sendo catalogado
	 *
	 * @return
	 * @throws DAOException
	 */
	private List<AreaConhecimentoCnpq> getGrandesAreasCNPq()throws DAOException{
		if(grandesAreasCNPq == null){
			AreaConhecimentoCNPqBibliotecaDao daoAreaConhecimentoCnpq = null;
			try{
				daoAreaConhecimentoCnpq = DAOFactory.getInstance().getDAO(AreaConhecimentoCNPqBibliotecaDao.class);		
				grandesAreasCNPq  = (List<AreaConhecimentoCnpq>) daoAreaConhecimentoCnpq.findGrandesAreasCNPqBibliotecaComProjecao();
			}finally{
				if(daoAreaConhecimentoCnpq != null) daoAreaConhecimentoCnpq.close();
			}
		}
		return grandesAreasCNPq;
	}
	
	
	/**
	 * 
	 * Pega todas as categorias de materiais ativas no banco para adi��o ou edi��o do campo 007
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<CategoriaMaterial> getAllCategoriasMateriaisAtivas()throws DAOException{
		
		if( listaCategoriaMaterial == null){
			listaCategoriaMaterial = (List<CategoriaMaterial>) getGenericDAO().findByExactField(CategoriaMaterial.class, "ativo", true);
	
			Collections.sort(listaCategoriaMaterial, new Comparator<CategoriaMaterial>(){
				@Override
				public int compare(CategoriaMaterial o1, CategoriaMaterial o2) {
					return o1.getCodigo().compareTo(o2.getCodigo());
				}
			});
		}
		
		
		
		return  listaCategoriaMaterial;
	}
	
	
	
	
	/**
	 *        M�todo que Busca as informa��es de altera��o de uma  Autoridade para mostra ao
	 * usu�rio na hora da cataloga��o. Serve para ter uma id�ia de quem mexeu na entidade.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracaoCatalogacao.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public List <Object []> getHistoricoAlteracaoTitulo()throws DAOException{
		
		List <Object []> listaHistoricoAlteracoes = new ArrayList<Object[]>();
		
		int idEntidade = getParameterInt("idEntidadeMarcVisualizarHistorico", 0);
		int limiteResultados = 30;
		
		if (idEntidade != 0){
			// retorna as 30 �ltima altera��es 
			listaHistoricoAlteracoes = getDAO(TituloCatalograficoDao.class).findAlteracoesByTituloPeriodo(idEntidade, null, null, limiteResultados);
		}
		
		for (Object[] object : listaHistoricoAlteracoes)
			object[1] = object[1].toString().replaceAll("\n", "<br/>");
		
		return listaHistoricoAlteracoes;
	}
	
	
	/**
	 *        M�todo que Busca as informa��es de altera��o de um T�tulo para mostra ao
	 * usu�rio na hora da cataloga��o. Serve para ter uma id�ia de quem mexeu na entidade.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracaoCatalogacao.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public List <Object []> getHistoricoAlteracaoAutoridade()throws DAOException{
		
		List <Object []> listaHistoricoAlteracoes = new ArrayList<Object[]>();
		
		int idEntidade = getParameterInt("idEntidadeMarcVisualizarHistorico", 0);
		int limiteResultados = 30;
		
		if (idEntidade != 0){
			// retorna as 10 �ltima altera��es 
			listaHistoricoAlteracoes = getDAO(AutoridadeDao.class).findAlteracoesByAutoridadePeriodo(idEntidade, null, null, limiteResultados);
		}
		
		for (Object[] object : listaHistoricoAlteracoes)
			object[1] = object[1].toString().replaceAll("\n", "<br/>");
		
		return listaHistoricoAlteracoes;
	}
	
	
	
	
	
	
	
	//////////// M�todos que cont�m a l�gica de montar as telas dos campos de controle  ///////////
	
	
	
	/**
	 *    Os valores dos campos de controle v�o ser atribu�dos nas cole��es  "valoresPar" e "valoresImpar".
	 * nas p�ginas de edi��o de campos de controle.
	 * 
	 *    Esse m�todo junta essas valores em uma �nica cole��o novamente.
	 */
	private void montaValoresDigitadosPeloUsuario(){
		
		if(etiquetaControleAtual.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO)
				|| etiquetaControleAtual.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE)){
		
			// Da p�gina ele vai jogar em "valoresPar" e "valoresImpar"
			// Ent�o preciso fazer o inverso e copiar para "valores"
			int i = 1;
			for (ValorPadraoCampoControle valor : valoresParLider) {
				valoresLider.get(i).setValorPadrao(valor.getValorPadrao());
				i=i+2;
			}
			
			i = 0;
			for (ValorPadraoCampoControle valor : valoresImparLider) {
				valoresLider.get(i).setValorPadrao(valor.getValorPadrao());
				i=i+2;
			}
		}
		
		if(etiquetaControleAtual.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO)){
			
			// Da p�gina ele vai jogar em "valoresPar" e "valoresImpar"
			// Ent�o preciso fazer o inverso e copiar para "valores"
			int i = 1;
			for (ValorPadraoCampoControle valor : valoresPar006) {
				valores006.get(i).setValorPadrao(valor.getValorPadrao());
				i=i+2;
			}
			
			i = 0;
			for (ValorPadraoCampoControle valor : valoresImpar006) {
				valores006.get(i).setValorPadrao(valor.getValorPadrao());
				i=i+2;
			}
		}
		
		if(etiquetaControleAtual.equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO)){
			
			// Da p�gina ele vai jogar em "valoresPar" e "valoresImpar"
			// Ent�o preciso fazer o inverso e copiar para "valores"
			int i = 1;
			for (ValorPadraoCampoControle valor : valoresPar007) {
				valores007.get(i).setValorPadrao(valor.getValorPadrao());
				i=i+2;
			}
			
			i = 0;
			for (ValorPadraoCampoControle valor : valoresImpar007) {
				valores007.get(i).setValorPadrao(valor.getValorPadrao());
				i=i+2;
			}
		}
		
		if(etiquetaControleAtual.equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO)
				|| etiquetaControleAtual.equals(Etiqueta.CAMPO_008_AUTORIDADE)){
			
			// Da p�gina ele vai jogar em "valoresPar" e "valoresImpar"
			// Ent�o preciso fazer o inverso e copiar para "valores"
			int i = 1;
			for (ValorPadraoCampoControle valor : valoresPar008) {
				valores008.get(i).setValorPadrao(valor.getValorPadrao());
				i=i+2;
			}
			
			i = 0;
			for (ValorPadraoCampoControle valor : valoresImpar008) {
				valores008.get(i).setValorPadrao(valor.getValorPadrao());
				i=i+2;
			}
		}
		
	}
	
	/**
	 *   Obt�m do banco os valores padr�o do campo de controle LIDER. Esses valores padr�o servem para montar
	 *   a p�gina de edi��o do campo.
	 */
	private List<ValorPadraoCampoControle> getValoresPadraoLider(Etiqueta e) throws DAOException {
		
		
		if(e.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO)){
			valoresPadraoLider = CatalogacaoUtil.getValoresPadraoLiderBibliografico(obj.getFormatoMaterial());
		}
		
		if(e.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE)){
			valoresPadraoLider = CatalogacaoUtil.getValoresPadraoAutoridades(Etiqueta.CAMPO_LIDER_AUTORIDADE);
		}
	
		return valoresPadraoLider;
	}

	/**
	 *   Obt�m do banco os valores padr�o do campo de controle 006. Esses valores padr�o servem para montar
	 *   a p�gina de edi��o do campo.
	 */
	private List<ValorPadraoCampoControle> getValoresPadrao006(Etiqueta e) throws DAOException {
		
		if(e.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO)){
			valoresPadrao006 = CatalogacaoUtil.getValoresPadrao006008Bibliografico(obj.getFormatoMaterial(), e);
		}
		
		return valoresPadrao006;
	}
	
	/**
	 * Obt�m do banco os valores padr�o do campo de controle 007. Esses valores padr�o servem para montar
	 *   a p�gina de edi��o do campo.
	 */
	private List<ValorPadraoCampoControle> getValoresPadrao007(Etiqueta e) throws DAOException {
		
		if(e.equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO)){
			valoresPadrao007 = CatalogacaoUtil.getValoresPadrao007Bibliografico(codigoCategoriaMaterial);
		}
		
		return valoresPadrao007;
	}
	
	/**
	 * Obt�m do banco os valores padr�o do campo de controle 008. Esses valores padr�o servem para montar
	 *   a p�gina de edi��o do campo.
	 */
	private List<ValorPadraoCampoControle> getValoresPadrao008(Etiqueta e) throws DAOException {
		
		
		if(e.equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO)){
			valoresPadrao008 = CatalogacaoUtil.getValoresPadrao006008Bibliografico(obj.getFormatoMaterial(), e);
		}
		
		if(e.equals(Etiqueta.CAMPO_008_AUTORIDADE)){
			valoresPadrao008 = CatalogacaoUtil.getValoresPadraoAutoridades(Etiqueta.CAMPO_008_AUTORIDADE);
		}
		
		
		return valoresPadrao008;
	}
	
	
	
	
	/**
	 * Guarda os valores que o usu�rio digitar nas p�gina de altera��o dos campos de controle
	 */
	private List<ValorPadraoCampoControle> getValoresLider(Etiqueta e) throws DAOException {
		
		if(e == null){
			valoresLider = new ArrayList<ValorPadraoCampoControle>();
			return  valoresLider;
		}
		
		if( ! editandoCamposDeControle){ // vai adicionar um NOVO CAMPO
			
			// os valores que aparecem para o usu�rio s�o os valores padr�o vindo do banco
			
			
			if(! errosValidacaoTeladeControle && ! voltandoPaginaLider ){
				
				valoresLider = CatalogacaoUtil.cloneValorPadrao(getValoresPadraoLider(e));
				return valoresLider;
				
			}
			
			// sen�o fica com os valores que j� estavam na tela.
			
		}else{   /// clicou em cima do campo e vai EDITAR
			
			
			if(! errosValidacaoTeladeControle  ){
				
				CampoControle c = obj.getCamposControle().get(posicaoCampoControleSelecionado);
			
				// em submeter pega o valores digitados que v�o estar em valorespadrao e monta os dados do campo
				// aqui pega os dados do campo e monta o valor padr�o com esses dados para mostrar no banco
				
				if(! errosValidacaoTeladeControle  ){
						valoresLider = CatalogacaoUtil.montaValoresCampo(c.getDado(), getValoresPadraoLider(e));
					
				}
			}
			
			// sen�o fica com os valores que j� estavam na tela.
		}
			
		return valoresLider;
	}
	
	/**
	 * Guarda os valores que o usu�rio digitar nas p�gina de altera��o dos campos de controle
	 */
	private List<ValorPadraoCampoControle> getValores006(Etiqueta e) throws DAOException {

		if(e == null){
			valores006 = new ArrayList<ValorPadraoCampoControle>();
			return  valores006;
		}
		
		if( ! editandoCamposDeControle){ // vai adicionar um NOVO CAMPO
			
			// os valores que aparecem para o usu�rio s�o os valores padr�o vindo do banco
			
			if(! errosValidacaoTeladeControle  ){
				valores006 = CatalogacaoUtil.cloneValorPadrao(getValoresPadrao006(e));
			}
			
			// fica o que estava antes
			
		}else{   /// clicou em cima do campo e vai EDITAR
			
			
			if(! errosValidacaoTeladeControle  ){
				
				CampoControle c = obj.getCamposControle().get(posicaoCampoControleSelecionado);
				
				if(! errosValidacaoTeladeControle  ){
					valores006 = CatalogacaoUtil.montaValoresCampo(c.getDado(), getValoresPadrao006(e));
				}
			}
			
			// fica o que estava antes
			
		}
		
		return valores006;
	}
	
	/**
	 * Guarda os valores que o usu�rio digitar nas p�gina de altera��o dos campos de controle
	 */
	private List<ValorPadraoCampoControle> getValores007(Etiqueta e) throws DAOException {
		
		if(e == null){
			valores007 = new ArrayList<ValorPadraoCampoControle>();
			return  valores007;
		}
		
		if( ! editandoCamposDeControle){ // vai adicionar um NOVO CAMPO
			
			// os valores que aparecem para o usu�rio s�o os valores padr�o vindo do banco
			
			if(! errosValidacaoTeladeControle  ){
				valores007 = CatalogacaoUtil.cloneValorPadrao(getValoresPadrao007(e));
			}
			
			// fica o que estava antes
			
		}else{   /// clicou em cima do campo e vai EDITAR
			
			
			if(! errosValidacaoTeladeControle  ){
				
				CampoControle c = obj.getCamposControle().get(posicaoCampoControleSelecionado);
				
				if(! errosValidacaoTeladeControle  ){
					valores007 = CatalogacaoUtil.montaValoresCampo(c.getDado(), getValoresPadrao007(e));
				}
			}
			
			// fica o que estava antes
			
		}
		
		return valores007;
	}
	
	/**
	 * Guarda os valores que o usu�rio digitar nas p�gina de altera��o dos campos de controle
	 */
	private List<ValorPadraoCampoControle> getValores008(Etiqueta e) throws DAOException {
		
		if(e == null){
			valores008 = new ArrayList<ValorPadraoCampoControle>();
			return  valores008;
		}
		
		if( ! editandoCamposDeControle){ // vai adicionar um NOVO CAMPO
			
			// os valores que aparecem para o usu�rio s�o os valores padr�o vindo do banco
			
			if(! errosValidacaoTeladeControle && ! voltandoPagina008  ){
				valores008 = CatalogacaoUtil.cloneValorPadrao(getValoresPadrao008(e));
			}
			
			// fica o que estava antes
			
		}else{   /// clicou em cima do campo e vai EDITAR
			
			
			if(! errosValidacaoTeladeControle  ){
				
				CampoControle c = obj.getCamposControle().get(posicaoCampoControleSelecionado);
				
				if(! errosValidacaoTeladeControle  ){
					valores008 = CatalogacaoUtil.montaValoresCampo(c.getDado(), getValoresPadrao008(e));
				}
			}
			
			// fica o que estava antes
			
		}
		
		return valores008;
	}
	
	
	
	/**
	 *   Dividi a lista de valores pares do campo L�DER apenas para mostrar na p�gina em duas colunas
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampoLider.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<ValorPadraoCampoControle> getValoresParLider() throws DAOException{
		
		List<ValorPadraoCampoControle> valoresTemp = getValoresLider(etiquetaControleAtual);
		
		List<ValorPadraoCampoControle> valoresLocal = CatalogacaoUtil.cloneValorPadrao(valoresTemp);
		
		valoresParLider = new ArrayList<ValorPadraoCampoControle>();
		
		for (int i = 1; i < valoresLocal.size(); i=i+2) {
			valoresParLider.add(valoresTemp.get(i));
		}
		return valoresParLider;
	}
	
	
	/**
	 *  Divide a lista de valores �mpares do campo L�DER apenas para mostrar na p�gina em duas colunas
	 *  <br/><br/>
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampoLider.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<ValorPadraoCampoControle> getValoresImparLider() throws DAOException{
		
		List<ValorPadraoCampoControle> valoresTemp = getValoresLider(etiquetaControleAtual);
		
		List<ValorPadraoCampoControle> valoresLocal = CatalogacaoUtil.cloneValorPadrao(valoresTemp);
		
		valoresImparLider = new ArrayList<ValorPadraoCampoControle>();
		
		for (int i = 0; i < valoresLocal.size(); i=i+2) {
			valoresImparLider.add(valoresTemp.get(i));
		}
		return valoresImparLider;
	}
	
	
	/**
	 *   Divide a lista de valores pares do campo 006 apenas para mostrar na p�gina em duas colunas
	 *
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo006.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<ValorPadraoCampoControle> getValoresPar006() throws DAOException{
		
		List<ValorPadraoCampoControle> valoresTemp = getValores006(etiquetaControleAtual);
		
		List<ValorPadraoCampoControle> valoresLocal = CatalogacaoUtil.cloneValorPadrao(valoresTemp);
		
		valoresPar006 = new ArrayList<ValorPadraoCampoControle>();
		
		for (int i = 1; i < valoresLocal.size(); i=i+2) {
			valoresPar006.add(valoresTemp.get(i));
		}
		return valoresPar006;
	}
	
	
	/**
	 *  Divide a lista de valores �mpares do campo 006 apenas para mostrar na p�gina em duas colunas
	 *
	 *  <br/><br/>
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo006.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<ValorPadraoCampoControle> getValoresImpar006() throws DAOException{
		
		List<ValorPadraoCampoControle> valoresTemp = getValores006(etiquetaControleAtual);
		
		List<ValorPadraoCampoControle> valoresLocal = CatalogacaoUtil.cloneValorPadrao(valoresTemp);
		
		valoresImpar006 = new ArrayList<ValorPadraoCampoControle>();
		
		for (int i = 0; i < valoresLocal.size(); i=i+2) {
			valoresImpar006.add(valoresTemp.get(i));
		}
		return valoresImpar006;
	}
	
	/**
	 *   Divide a lista de valores pares do campo 007 apenas para mostrar na p�gina em duas colunas
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo007.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<ValorPadraoCampoControle> getValoresPar007() throws DAOException{
		
		List<ValorPadraoCampoControle> valoresTemp = getValores007(etiquetaControleAtual);
		
		List<ValorPadraoCampoControle> valoresLocal = CatalogacaoUtil.cloneValorPadrao(valoresTemp);
		
		valoresPar007 = new ArrayList<ValorPadraoCampoControle>();
		
		for (int i = 1; i < valoresLocal.size(); i=i+2) {
			valoresPar007.add(valoresTemp.get(i));
		}
		return valoresPar007;
	}
	
	
	/**
	 *  Divide a lista de valores �mpares do campo 007 apenas para mostrar na p�gina em duas colunas
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo007.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<ValorPadraoCampoControle> getValoresImpar007() throws DAOException{
		
		List<ValorPadraoCampoControle> valoresTemp = getValores007(etiquetaControleAtual);
		
		List<ValorPadraoCampoControle> valoresLocal = CatalogacaoUtil.cloneValorPadrao(valoresTemp);
		
		valoresImpar007 = new ArrayList<ValorPadraoCampoControle>();
		
		for (int i = 0; i < valoresLocal.size(); i=i+2) {
			valoresImpar007.add(valoresTemp.get(i));
		}
		return valoresImpar007;
	}
	
	/**
	 *   Divide a lista de valores pares do campo 008 apenas para mostrar na p�gina em duas colunas
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<ValorPadraoCampoControle> getValoresPar008() throws DAOException{
		
		List<ValorPadraoCampoControle> valoresTemp = getValores008(etiquetaControleAtual);
		
		List<ValorPadraoCampoControle> valoresLocal = CatalogacaoUtil.cloneValorPadrao(valoresTemp);
		
		valoresPar008 = new ArrayList<ValorPadraoCampoControle>();
		
		for (int i = 1; i < valoresLocal.size(); i=i+2) {
			valoresPar008.add(valoresTemp.get(i));
		}
		return valoresPar008;
	}
	
	
	/**
	 *  Divide a lista de valores �mpares do campo 008 apenas para mostrar na p�gina em duas colunas
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<ValorPadraoCampoControle> getValoresImpar008() throws DAOException{
		
		List<ValorPadraoCampoControle> valoresTemp = getValores008(etiquetaControleAtual);
		
		List<ValorPadraoCampoControle> valoresLocal = CatalogacaoUtil.cloneValorPadrao(valoresTemp);
		
		valoresImpar008 = new ArrayList<ValorPadraoCampoControle>();
		
		for (int i = 0; i < valoresLocal.size(); i=i+2) {
			valoresImpar008.add(valoresTemp.get(i));
		}
		return valoresImpar008;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 *  M�todo para ser usado no campo 260$b, para realizar um autocomplete das editoras, para o
	 * usu�rio n�o ter que digit�-las.
	 *
	 *  <br/><br/>
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * 
	 */
	public List<Editora> autocompleteEditora(Object suggest) {
	
		TituloCatalograficoDao dao = null;
		
		try{
		
			String denominacaoEditora = (String)suggest;
			
			dao = getDAO(TituloCatalograficoDao.class);
			
			return dao.buscaEditorasSIPAC(denominacaoEditora);
			
		}catch (Exception ex) {
			ex.printStackTrace();
			
			// N�o deve dar erro na cataloga��o se por acaso a busca n�o funcionar
			// A busca de editora no SIPAC � apenas uma sugest�o para facilitar a cataloga��o
			return new ArrayList<Editora>();
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	

	/**
	 *  Adiciona ao subcampo 256$b selecionado a descri��o da editora selecionada.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/buscaEditoraCadastradasSipac.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String adicionarEditora(){
		
		if(indiceCampoSelecionado >= 0 && indiceSubCampoSelecionado >= 0)
			camposNaoReservados.get(indiceCampoSelecionado).getSubCampos().get( indiceSubCampoSelecionado ).setDado(nomeEditora);
		
		zeraIndicesCamposSelecionados();
		nomeEditora = "";
		return telaDadosTituloCatalografico();
		
	}
	
	/**
	 * 
	 * Redireciona o usu�rio para a p�gina na qual ele pode escolher o novo formado do material do T�tulo.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp</li>
	 *  </ul>
	 *
	 *
	 * @return
	 */
	public String alterarFormatoMaterialCatalogacao(){
		getCurrentRequest().setAttribute("alterandoFormatoMaterialTitulo", true);
		return telaEscolheFormatoMaterial();
	}
	
	/**
	 *  Altera o formato da cataloga��o para o novo formato escolhido.<br/>
	 *  Esta troca s� pode ocorrer se n�o influenciar nos materiais que o t�tulo possui, isto �,
	 *  caso o t�tulo possua fasc�culos catalogados, o �nico formato que o usu�rio pode escolher �
	 *  "PERI�DICO". Caso o t�tulo possua exemplares, o usu�rio n�o pode escolher o formato de "PERI�DICO".
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolheFormatoMaterial.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public String confirmaAlterarFormatoMaterialCatalogacao() throws DAOException{
		
		FormatoMaterial novoFormato = new FormatoMaterial(getParameterInt("idNovoFormatoMaterial"));
		
		ExemplarDao daoExemplar = getDAO(ExemplarDao.class);
		FasciculoDao daoFasciculo = getDAO(FasciculoDao.class);
		
		// Trocou para peri�dicos, precisa verificar
		if( novoFormato.isFormatoPeriodico()){
			
			novoFormato = daoExemplar.refresh(novoFormato) ;
			
			Integer qtd = daoExemplar.countExemplaresAtivosNoAcervoByDoTitulo(obj.getId());
			
			if(new Integer(0).compareTo(qtd) == 0){
				obj.setFormatoMaterial(novoFormato );
				return telaDadosTituloCatalografico();
			}else{
				addMensagemErro("O T�tulo possui exemplares no acervo, por isso o seu formato n�o pode " +
						"ser alterado para "+novoFormato.getDescricaoCompleta());
				getCurrentRequest().setAttribute("alterandoFormatoMaterialTitulo", true);
				return telaEscolheFormatoMaterial();
			}
			
		}else{ // N�o � o formato de peri�dico
			
			novoFormato = daoFasciculo.refresh(novoFormato) ;
			
			Long qtd = daoFasciculo.countFasciculosAtivosNoAcervoDoTitulo(obj.getId());
			
			if(new Long(0).compareTo(qtd) == 0){
				obj.setFormatoMaterial(novoFormato  );
				return telaDadosTituloCatalografico();
			}else{
				addMensagemErro("O T�tulo possui fasc�culos no acervo, por isso o seu formato n�o pode " +
						"ser alterado para "+novoFormato.getDescricaoCompleta());
				getCurrentRequest().setAttribute("alterandoFormatoMaterialTitulo", true);
				return telaEscolheFormatoMaterial();
			}
		}
	}
	
	
	/**
	 * Retorna a texto a ser exibido no caption da p�gina.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp</li>
	 *  </ul>
	 *
	 * @String
	 */
	public String getCaptionFormularioCatalogacao(){
		if(isTipoCatalogacaoBibliografica()){
			return " "+ ( obj.getNumeroDoSistema() > 0 ? obj.getNumeroDoSistema()+" - ": " " )+"  T�tulo Bibliogr�fico | Formato do Material  ";
		}else{
			return " "+ ( obj.getNumeroDoSistema() > 0 ? obj.getNumeroDoSistema()+" - ": " " )+"  Autoridade ";
		}
	}
	
	
	/** Atualiza a prefer�ncia do usu�rio em exibir ou n�o o painel lateral na cataloga��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painelLateralFormCatalogacao.jsp</li>
	 *  </ul>
	 */
	public void atualizaExibirPainelLateral(ActionEvent evt){
		exibirPainelLateral = ! exibirPainelLateral;
	}
	
	
	/** Atualiza a prefer�ncia do usu�rio em usar a cataloga��o convencional ou a simplificada
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painelConfiguracoesFormCatalogacao.jsp</li>
	 *  </ul>
	 */
	public void alterarEntreTelasCatalogacao(ActionEvent evt){
		
		usarTelaCatalogacaoCompleta = ! usarTelaCatalogacaoCompleta;
		
		if(! usarTelaCatalogacaoCompleta){ // se est� passando para cataloga��o simplificada, tem que ordenar o que pode ter sido adicionado na outra tela.
			
			obj.setCamposDados(atribuiCamposDadosAoTitulo());
			CatalogacaoUtil.configuraGruposEtiquetaVisiveis(obj);
			atribuiCamposDoTituloAosCampoUsadosNaTela();
			
		}
	
	}
	
	
	/** Atualiza as prefer�ncias do usu�rio em rela��o � tela de cataloga��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/modelPanelsFormCatalogacao.jsp</li>
	 *  </ul>
	 * @throws ArqException 
	 * @throws  
	 */
	public void salvarConfiguracoesTelasCatalogacao(ActionEvent evt) throws ArqException{
	
		
		
		MovimentoCadastro movimento = new MovimentoCadastro(configuracoesTela);
		if(configuracoesTela.getId() > 0){
			prepareMovimento(ArqListaComando.ALTERAR);
			movimento.setCodMovimento(ArqListaComando.ALTERAR);
		}else{
			prepareMovimento(ArqListaComando.CADASTRAR);
			movimento.setCodMovimento(ArqListaComando.CADASTRAR);
		}
		
		try {
			execute(movimento);
			addMensagemInfoAjax("Configura��es salvas com sucesso ! ");
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagensAjax(ne.getListaMensagens());
		}
		
	}
	
	
	
	
	/** 
     * Busca e carrega a arvore de materiais do Titulo
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painelLateralFormCatalogacao.jsp</li>
	 *  </ul>
	 * @throws DAOException 
	 */
	public void mostrarListaMateriaisTitulo(ActionEvent evt) throws DAOException{
		
		if(isTipoCatalogacaoBibliografica() && quantidadeMateriaisTitulo > 0){
			
			visualizaListaMateriaisTitulo = true;
			
			rootNode = new TreeNodeImpl<String>();
		
			MaterialInformacionalDao dao = null;
			try {
				dao = getDAO(MaterialInformacionalDao.class);
			
				List<MaterialInformacional> materiaisTitulo = dao.findInfoMateriaisAtivosByTitulo(obj.getId());
				
				for (MaterialInformacional material : materiaisTitulo) {
					
					TreeNodeImpl<String> nodeFilho = new TreeNodeImpl<String>();  
					nodeFilho.setData(material.getCodigoBarras()+" [    "+material.getSituacao().getDescricao()+"    ] "); 
					rootNode.addChild(material.getId(), nodeFilho);
				}
				
			} finally {
				if (dao != null)dao.close();
			}
			
		}
	}
	
	
	
	// sets e gets
	
	/**
	 * Retorna os campos de controle do T�tulo ordenados pela etiqueta.
	 * @return
	 */
	public List<CampoControle> getCamposControleOrdenados(){
		return (List<CampoControle>) obj.getCamposControleOrdenadosByEtiqueta();
	}

	/**
	 * Indica se o tipo da cataloga��o corrente � cataloga��o de t�tulo bibliogr�fico.
	 * 
	 * @return
	 */
	public boolean isTipoCatalogacaoBibliografica(){
		if(tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA)
			return true;
		else
			return false;
	}
	
	/**
	 * Indica se o tipo da cataloga��o corrente � cataloga��o de autoridade.
	 * 
	 * @return
	 */
	public boolean isTipoCatalogacaoAutoridade(){
		if(tipoCatagalocao == TipoCatalogacao.AUTORIDADE)
			return true;
		else
			return false;
	}
	
	/**
	 * Indica se a cataloga��o corrente � com tombamento.
	 * 
	 * @return
	 */
	public boolean isCatalogacaoComTombamento(){
		if(infoTituloCompra != null)
			return true;
		else
			return false;
	}
	
	
	public DataModel getDataModelCampos() {
		return dataModelCampos;
	}

	public void setDataModelCampos(DataModel dataModelCampos) {
		this.dataModelCampos = dataModelCampos;
	}

	public String getAdicionarMaterialInformacional() {
		return adicionarMaterialInformacional;
	}

	public void setAdicionarMaterialInformacional(String adicionarMaterialInformacional) {
		this.adicionarMaterialInformacional = adicionarMaterialInformacional;
	}

	public int getIdPlanilhaEscolhida() {
		return idPlanilhaEscolhida;
	}

	public void setIdPlanilhaEscolhida(int idPlanilhaEscolhida) {
		this.idPlanilhaEscolhida = idPlanilhaEscolhida;
	}

	public TituloCatalografico getTituloTemp() {
		return tituloTemp;
	}

	public boolean isEditandoCamposDeControle() {
		return editandoCamposDeControle;
	}

	public void setEditandoCamposDeControle(boolean editandoCamposDeControle) {
		this.editandoCamposDeControle = editandoCamposDeControle;
	}

	public String getDadosCamposControle() {
		return dadosCamposControle;
	}

	public void setDadosCamposControle(String dadosCamposControle) {
		this.dadosCamposControle = dadosCamposControle;
	}

	public boolean isAdicionandoCamposDeControle() {
		return adicionandoCamposDeControle;
	}

	public void setAdicionandoCamposDeControle(boolean adicionandoCamposDeControle) {
		this.adicionandoCamposDeControle = adicionandoCamposDeControle;
	}

	public String getVoltarPaginaCatalogacao() {
		return voltarPaginaCatalogacao;
	}

	public void setVoltarPaginaCatalogacao(String voltarPaginaCatalogacao) {
		this.voltarPaginaCatalogacao = voltarPaginaCatalogacao;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}
	
	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public String getAjudaCampo(){
		return ajudaCampo;
	}

	public InformacoesTombamentoMateriaisDTO getInfoTituloCompra() {
		return infoTituloCompra;
	}

	public void setInfoTituloCompra(InformacoesTombamentoMateriaisDTO infoTituloCompra) {
		this.infoTituloCompra = infoTituloCompra;
	}

	public Long getNumeroPatrimonio() {
		return numeroPatrimonio;
	}

	public void setNumeroPatrimonio(Long numeroPatrimonio) {
		this.numeroPatrimonio = numeroPatrimonio;
	}

	public boolean isCatalogacaoDoZero() {
		return catalogacaoDoZero;
	}

	public void setCatalogacaoDoZero(boolean catalogacaoDoZero) {
		this.catalogacaoDoZero = catalogacaoDoZero;
	}

	public boolean isCatalogacaoImportacao() {
		return catalogacaoImportacao;
	}

	public void setCatalogacaoImportacao(boolean catalogacaoImportacao) {
		this.catalogacaoImportacao = catalogacaoImportacao;
	}

	public boolean isCatalogacaoPlanilha() {
		return catalogacaoPlanilha;
	}

	public void setCatalogacaoPlanilha(boolean catalogacaoPlanilha) {
		this.catalogacaoPlanilha = catalogacaoPlanilha;
	}

	public boolean isCatalogacaoDuplicacao() {
		return catalogacaoDuplicacao;
	}

	public void setCatalogacaoDuplicacao(boolean catalogacaoDuplicacao) {
		this.catalogacaoDuplicacao = catalogacaoDuplicacao;
	}

	public String getFinalizarCatalogacao() {
		return finalizarCatalogacao;
	}

	public void setFinalizarCatalogacao(String finalizarCatalogacao) {
		this.finalizarCatalogacao = finalizarCatalogacao;
	}

	public boolean isPossuiEntiadesNaoFinalizados() {
		return possuiEntiadesNaoFinalizados;
	}

	public void setPossuiEntiadesNaoFinalizados(boolean possuiEntiadesNaoFinalizados) {
		this.possuiEntiadesNaoFinalizados = possuiEntiadesNaoFinalizados;
	}

	public String getRedirecionaProximaAutoriadeIncompleta() {
		return redirecionaProximaAutoriadeIncompleta;
	}

	public void setRedirecionaProximaAutoriadeIncompleta(String redirecionaProximaAutoriadeIncompleta) {
		this.redirecionaProximaAutoriadeIncompleta = redirecionaProximaAutoriadeIncompleta;
	}

	public boolean isCatalogacaoMateriaisSemTombamento() {
		return catalogacaoMateriaisSemTombamento;
	}

	public void setCatalogacaoMateriaisSemTombamento(boolean catalogacaoMateriaisSemTombamento) {
		this.catalogacaoMateriaisSemTombamento = catalogacaoMateriaisSemTombamento;
	}

	public CampoDados getCampoSelecionadoParaDadosAutoridade() {
		return campoSelecionadoParaDadosAutoridade;
	}

	public void setCampoSelecionadoParaDadosAutoridade(CampoDados campoSelecionadoParaDadosAutoridade) {
		this.campoSelecionadoParaDadosAutoridade = campoSelecionadoParaDadosAutoridade;
	}

	public String getNomeEditora() {
		return nomeEditora;
	}

	public void setNomeEditora(String nomeEditora) {
		this.nomeEditora = nomeEditora;
	}
	
	public String getNomeSistemaBuscaEditoras(){
		return  RepositorioDadosInstitucionais.get("siglaSipac");
	}

	public List<CampoDados> getCamposReservados() {
		return camposReservados;
	}

	public void setCamposReservados(List<CampoDados> camposReservados) {
		this.camposReservados = camposReservados;
	}

	public List<DadosTabelaCutter> getSuguestoesTabelaCuter() {
		return suguestoesTabelaCuter;
	}

	public void setSuguestoesTabelaCuter(List<DadosTabelaCutter> suguestoesTabelaCuter) {
		this.suguestoesTabelaCuter = suguestoesTabelaCuter;
	}

	public int getTempoSalvamentoCatalogacacao() {
		return tempoSalvamentoCatalogacacao;
	}

	public void setTempoSalvamentoCatalogacacao(int tempoSalvamentoCatalogacacao) {
		this.tempoSalvamentoCatalogacacao = tempoSalvamentoCatalogacacao;
	}

	public boolean isCatalogacaoDeDefesa() {
		return catalogacaoDeDefesa;
	}

	public void setCatalogacaoDeDefesa(boolean catalogacaoDeDefesa) {
		this.catalogacaoDeDefesa = catalogacaoDeDefesa;
	}

	public String getTagEtiquetaCampoDeDadosCompleto() {
		return tagEtiquetaCampoDeDadosCompleto;
	}

	public void setTagEtiquetaCampoDeDadosCompleto(String tagEtiquetaCampoDeDadosCompleto) {
		this.tagEtiquetaCampoDeDadosCompleto = tagEtiquetaCampoDeDadosCompleto;
	}

	public CampoDados getCampoDadosBuscaCompleta() {
		return campoDadosBuscaCompleta;
	}

	public void setCampoDadosBuscaCompleta(CampoDados campoDadosBuscaCompleta) {
		this.campoDadosBuscaCompleta = campoDadosBuscaCompleta;
	}

	public List<String> getMensagensModelPanel() {
		return mensagensModelPanel;
	}

	public void setMensagensModelPanel(List<String> mensagensModelPanel) {
		this.mensagensModelPanel = mensagensModelPanel;
	}
	
	public TreeNode<String>  getArvoreMateriaisTitulo() {
		return rootNode;
	}

	public boolean isExibirPainelLateral() {
		return exibirPainelLateral;
	}

	public void setExibirPainelLateral(boolean exibirPainelLateral) {
		this.exibirPainelLateral = exibirPainelLateral;
	}


	public Long getQuantidadeMateriaisTitulo() {
		return quantidadeMateriaisTitulo;
	}


	public void setQuantidadeMateriaisTitulo(Long quantidadeMateriaisTitulo) {
		this.quantidadeMateriaisTitulo = quantidadeMateriaisTitulo;
	}


	public boolean isVisualizaListaMateriaisTitulo() {
		return visualizaListaMateriaisTitulo;
	}


	public void setVisualizaListaMateriaisTitulo(boolean visualizaListaMateriaisTitulo) {
		this.visualizaListaMateriaisTitulo = visualizaListaMateriaisTitulo;
	}

	public boolean isUsarTelaCatalogacaoCompleta() {
		return usarTelaCatalogacaoCompleta;
	}
	
	public void setUsarTelaCatalogacaoCompleta(boolean usarTelaCatalogacaoCompleta) {
		this.usarTelaCatalogacaoCompleta = usarTelaCatalogacaoCompleta;
	}

	public ConfiguracoesTelaCatalogacao getConfiguracoesTela() {
		return configuracoesTela;
	}

	public void setConfiguracoesTela(ConfiguracoesTelaCatalogacao configuracoesTela) {
		this.configuracoesTela = configuracoesTela;
	}


	public int getIdPlanilhaCatalogacaoSimplificada() {
		return idPlanilhaCatalogacaoSimplificada;
	}

	public void setIdPlanilhaCatalogacaoSimplificada(int idPlanilhaCatalogacaoSimplificada) {
		this.idPlanilhaCatalogacaoSimplificada = idPlanilhaCatalogacaoSimplificada;
	}

	public PlanilhaCatalogacao getPlanilhaCatalogacaoSimplificada() {
		return planilhaCatalogacaoSimplificada;
	}

	public void setPlanilhaCatalogacaoSimplificada(PlanilhaCatalogacao planilhaCatalogacaoSimplificada) {
		this.planilhaCatalogacaoSimplificada = planilhaCatalogacaoSimplificada;
	}
	
}
