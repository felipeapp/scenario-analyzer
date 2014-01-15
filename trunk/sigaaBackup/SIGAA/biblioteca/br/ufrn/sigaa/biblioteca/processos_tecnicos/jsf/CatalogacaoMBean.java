/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 *    MBean responsável por realizar catalogação de um Título e Autoridade na biblioteca.
 * 
 *    Gerencia a página principal de catalogação <code>formDadosTituloCatalografico.jsp</code>
 *    e as outras páginas de edição dos campos de controle
 *    <code>formDadosCampo001.jsp</code> ... <code>formDadosCampo008.jsp</code>
 * 
 * @author Victor Hugo
 * @version 1.0 criação da classe
 * @version 1.1 Jadson Adição dos métodos que manipulam os campos de controle e edição/remoção do Título
 * @version 1.2 Jadson Adicionando a parte de catalogação de autoridades que vai usar a mesma tela.
 * @version 1.3 Jadson Adicionando a ajuda, busca por autoridades e obra digitalizada.
 * @version 1.4 Jadson Adicionando a opção de alterar a ordem os campos e subcampos.
 * @version 1.5 Bráulio Implementação do caso de uso de catalogação de defesas.
 * @version 1.6 Jadson Otimizando a busca de informação de etiquetas. Guardando em memória as etiquetas 
 *       já buscadas para diminuir as consultas ao banco durante a catalogação. Utilizando uma fila de requisições ajax para tentar 
 *       concertar o problema de falta de sincromismo na página.
 * @version 1.7 Jadson Generalizando as classificações bibliográficas utilizadas na biblioteca
 * @version 2.0 20/06/2012 - Jadson - Adicionando a possibilidade de se ter 2 telas de catalogação diferentes, uma simplificada 
 * e a antida onde o usuário pode mexer em toda a codificação MARC.
 */
@Component("catalogacaoMBean")
@Scope("request")
public class CatalogacaoMBean extends SigaaAbstractController<TituloCatalografico> {
	
	/** Constantes de navegação das páginas */
	private static final String BASE = "/biblioteca/processos_tecnicos/catalogacao";
	
	/** Constantes de navegação das páginas */
	public static final String PAGINA_ESCOLHE_FORMATO_MATERIAL = BASE + "/escolheFormatoMaterial.jsp";
	/** Constantes de navegação das páginas */
	public static final String PAGINA_ESCOLHE_PLANILHA_BIBLIOGRAFICA = BASE + "/escolhePlanilhaBibliografica.jsp";
	/** Constantes de navegação das páginas */
	public static final String PAGINA_ESCOLHE_PLANILHA_AUTORIDADES = BASE + "/escolhePlanilhaAutoridades.jsp";
	/** Constantes de navegação das páginas */
	public static final String PAGINA_PREENCHE_DADOS_TITULO_CATALOGRAFICO = BASE + "/formDadosTituloCatalografico.jsp";
	
	/** Constantes de navegação das páginas */
	public static final String PAGINA_CAMPO_LIDER = BASE + "/formDadosCampoLider.jsp";
	/** Constantes de navegação das páginas */
	public static final String PAGINA_CAMPO_001 = BASE + "/formDadosCampo001.jsp";
	/** Constantes de navegação das páginas */
	public static final String PAGINA_CAMPO_003 = BASE + "/formDadosCampo003.jsp";
	/** Constantes de navegação das páginas */
	public static final String PAGINA_CAMPO_005 = BASE + "/formDadosCampo005.jsp";
	/** Constantes de navegação das páginas */
	public static final String PAGINA_CAMPO_006 = BASE + "/formDadosCampo006.jsp";
	/** Constantes de navegação das páginas */
	public static final String PAGINA_CAMPO_007 = BASE + "/formDadosCampo007.jsp";
	/** Constantes de navegação das páginas */
	public static final String PAGINA_CAMPO_008 = BASE + "/formDadosCampo008.jsp";
	
	/** Constantes de navegação das páginas */
	public static final String PAGINA_BUSCA_SIPAC_A_PARTIR_NUMERO_PATRIMONIO = BASE + "/buscaInformacoesSipacAPartirNumeroPatrimonio.jsp";
	
	/** Constantes de navegação das páginas */
	public static final String PAGINA_BUSCA_EDITORAS_CADASTRADAS_SIPAC = BASE + "/buscaEditoraCadastradasSipac.jsp";
	
	/** Constantes de navegação das páginas */
	public static final String PAGINA_BUSCA_TABELA_CUTTER = BASE + "/buscaTabelaCutter.jsp";
	
	
	//  VALORES PADRÃO É PARA MONTAR AS PÁGINA DE ALTERAÇÃO DE CADA UM DOS CAMPO DE CONTROLE  //
	
	
	/** Valores  do campo líder que vem preenchido por padrão na tela ao tentar incluir um novo campo. São buscados apenas uma vez, o usuário não os altera. */
	private List<ValorPadraoCampoControle> valoresPadraoLider;
	/** Valores  do campo 006 que vem preenchido por padrão na tela ao tentar incluir um novo campo. São buscados apenas uma vez, o usuário não os altera. */
	private List<ValorPadraoCampoControle> valoresPadrao006;
	/** Valores  do campo 007 que vem preenchido por padrão na tela ao tentar incluir um novo campo. São buscados apenas uma vez, o usuário não os altera. */
	private List<ValorPadraoCampoControle> valoresPadrao007;
	/** Valores  do campo 008 que vem preenchido por padrão na tela ao tentar incluir um novo campo. São buscados apenas uma vez, o usuário não os altera. */
	private List<ValorPadraoCampoControle> valoresPadrao008;
	
	/* *****************************************************************************************************************
	 * Os seguintes objetos são responsáveis por guardar o que o usuário digitar na página                            
	 * Obs.:  Apenas para visualizar os dados em duas colunas tive que para cada uma criar "valorPar" e "valorImpar"  
	 * Obs2.: Tem que ter um por página senão dá erro ao usar o botão voltar no navegador                            
	 ****************************************************************************************************************** */
	
	/** Guarda os valores do campo líder digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valoresLider;
	/** Guarda os valores pares do campo líder digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valoresParLider = new ArrayList<ValorPadraoCampoControle>();
	/** Guarda os valores impares do campo líder digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valoresImparLider = new ArrayList<ValorPadraoCampoControle>();
	
	/** Guarda os valores do campo 006 digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valores006;
	/** Guarda os valores pares do campo 006 digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valoresPar006 = new ArrayList<ValorPadraoCampoControle>();
	/** Guarda os valores impares do campo 006 digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valoresImpar006 = new ArrayList<ValorPadraoCampoControle>();
	
	/** Guarda os valores do campo 007 digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valores007;
	/** Guarda os valores pares do campo 007 digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valoresPar007 = new ArrayList<ValorPadraoCampoControle>();
	/** Guarda os valores impares do campo 007 digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valoresImpar007 = new ArrayList<ValorPadraoCampoControle>();
	
	/** Guarda os valores do campo 008 digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valores008;
	/** Guarda os valores pares do campo 008 digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valoresPar008 = new ArrayList<ValorPadraoCampoControle>();
	/** Guarda os valores pares do campo 008 digitado pelo usuário na página */
	private List<ValorPadraoCampoControle> valoresImpar008 = new ArrayList<ValorPadraoCampoControle>();
	

	/** O tipo de catalogação realizada no momento. Por padrão é bibliográfica */
	private int tipoCatagalocao = TipoCatalogacao.BIBLIOGRAFICA; 
	
	/** Para saber qual etiqueta o usuário está editando nesse momento */
	private Etiqueta etiquetaControleAtual;  
	
	
	/**
	 * Guarda um cache das etiquetas buscadas pelo usuário, para não precisar sempre ir no banco
	 */
	private List<Etiqueta> etiquetasBuscadas = new ArrayList<Etiqueta>(); 
	
	
	/** Guarda em cache a lista de classificações bibliográfica utilizadas no sistema para não precisar busca sempre no banco*/
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>(); 
	
	
	/**
	 * Variável que guarda o valor da etiqueta do campo na busca de um campo de dados completo.
	 */
	private String tagEtiquetaCampoDeDadosCompleto;
	
	/**
	 * Guarda o campo de dados que foi buscado pelo busca completa (a busca com subcampos)
	 * 
	 * O usuário deve escolher quais os sub campos que ele quer antes desse campos  ser adicionado ao Título;
	 */
	private CampoDados campoDadosBuscaCompleta;
	
	/**
	 * Mensagens para serem mostradas nos models panel das páginas.
	 */
	private List<String> mensagensModelPanel;
	
	
	/**
	 * Usado para redenrizar as informações sobre os materiais do Título que está sendo catalogado
	 */
	private TreeNodeImpl<String> rootNode;
	
	/** Para exibição para o usuário da quantidade de Materiais de um Título */
	private Long quantidadeMateriaisTitulo = 0l;
	
	/** Indica se a listagem de materiais do Título deve ser mostrada por padrão ou não. Em alguns casos 
	 * como periódico a quantidade é muito grande e o sistema vai ocultar por padrão para não ficar muito demorado carregar a página.*/
	private boolean visualizaListaMateriaisTitulo = false;
	
	/**
	 * Guarda os dados dos campo de controle 001, 003, 005 que é só uma string
	 * simples e não precisa desses objetos "valores"
	 */
	private String dadosCamposControle = "";
	
	
	/**
	 * Encapsula a coleção de campos de dados para acessá-los nas páginas JSF
	 * 
	 * Observação: Só vai conter os campos não reservados do sistema, são os campos que o catalogador pode mexer
	 */
	private DataModel dataModelCampos;
	
	
	/** Guarda os campos que são NÃO reservados no sistema que o catalogador pode alterar */
	private List<CampoDados> camposNaoReservados;
	
	/** Guarda os campos que são reservados no sistema e o catalogador não pode alterar */
	private List<CampoDados> camposReservados;
	
	/**
	 *    Atributo para indicar que o usuário clicou no botão de finalizar catalogação.
	 */
	private String finalizarCatalogacao = "false";
	
	
	
	/**
	 *    Se veio da página de títulos/autoridades não finalizados, vai ser setado esse valor para "true", para quando o
	 * usuário terminar a catalogação e a inclusão de materiais habilitar um botão para voltar à tela títulos
     * não finalizados e escolher outro para trabalhar.
     * 
     *    No caso de autoridades vai aparecer um botão para o usuário voltar para a tela de autoridades
     * não finalizadas e escolher a próxima para trabalhar.
     * 
	 */
	private boolean possuiEntiadesNaoFinalizados = false;
	
	
	/**
	 *  Indica se o usuário selecionou o botão de catalogar a próxima autoridade incompleta ou não
	 */
	private String redirecionaProximaAutoriadeIncompleta ="false";
	
	
	/**
	 *    Esse atributo controla se o usuário deseja adicionar materiais informacionais após a catalogação do Título,
	 * utilizado para redirecionar à página de adição de materiais, no final do fluxo de catalogação.
	 * 
	 *  Implicitamente indica que o usuário está finalizando a catalogação pois só pode adicionar
	 *  materiais a títulos finalizados.
	 *
	 * 
	 */
	private String adicionarMaterialInformacional = "false";
	
	
	
	
	/** Usado nas páginas do campo de controle para saber se estão sendo digitados os campos pela
	 * primeira vez (o botão próximo vai estar habilitado), ou se está editando esses campos que
	 * já foram criados (ai vai existir outro botão que vai redirecionar direto para a página de
	 * catalogação). 
	 */
	private boolean editandoCamposDeControle = false;
	/** Usado nas páginas do campo de controle para saber se estão sendo digitados os campos pela
	 * primeira vez (o botão próximo vai estar habilitado), ou se está editando esses campos que
	 * já foram criados (ai vai existir outro botão que vai redirecionar direto para a página de
	 * catalogação). 
	 */
	private boolean adicionandoCamposDeControle = false;
	/** errosValidacaoTeladeControle indica que ocorreram erros de validação, para deixar os mesmos
	 * dados que o usuário digitou na tela. Impedir que ele recarregue os valores padrão do banco novamente.
	 * */
	private boolean errosValidacaoTeladeControle = false;
	
	
	/** Usado para saber a posição do campo de controle que foi selecionada para edição.
	 * Não dava para pegar só pela tag pois alguns campos de controle podem se repetir. */
	private int posicaoCampoControleSelecionado  = -1;
	
	
	
	////////////////  DADOS UTILIZADOS QUANDO SE ESTÁ CATALOGANDO USANDO UMA PLANILHA  /////////////
	
	/**  O id da planilha escolhida a partir da qual os dados da catalogação vão ser montados */
	private int idPlanilhaEscolhida = -1;
	
	/**  Guarda os dados que vieram da planilha temporariamente*/
	private TituloCatalografico tituloTemp;
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/** Usa APENAS quanto o usuário seleciona o campo 007, pois lá os valores padrão que vão aparecer
	 * na tela dependem da categoria do material, não do formato do material como os campos Líder, 006 e 008 */
	private String codigoCategoriaMaterial;
	
	
	/** Essa variável diz se está editando ou salvando o título, porque vai ser a mesma página, 
	 * a diferença é que vai habilitar um botão diferente que apenas atualiza o Título na base */
	private boolean editando = false;
	
	
	/** Essa variável indica que a catalogação sem tombamento. O usuário vai poder adicionar materiais   
	 * não tombados ao Título.                                                                          */
	private boolean catalogacaoMateriaisSemTombamento = false;
	
	/** Indica se a catalogação está sendo feita a partir de uma tese ou monografia. */
	private boolean catalogacaoDeDefesa = false;
	
	
	
	///////////// Informações que vem do SIPAC usado na catalogação por tombamento  //////////////
	
	/** Dtos com as informações dos bens tombados para os materiais que vão ser catalogados */
	private InformacoesTombamentoMateriaisDTO infoTituloCompra;
	
	/**  Número de patrimônio digitado pelo usuário para ser busca no sistema de patrimônio*/
	private Long numeroPatrimonio;
	
	/** Interface de busca no patrimônio */
	@Resource(name = "bibliotecaComprasInvoker")
	private BibliotecaComprasRemoteService controller;
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/** o arquivo da obra digitalizada */
	private UploadedFile arquivo;  
	
	/** Na edição de títulos, indica que o arquivo digital que estava salvo deve ser apagado.
	 * Isso ocorre quando o usuário submete outro aquivo. */
	private boolean apagarArquivoDigitalSalvo = false;
	
	
	/** Guarda a ajuda dos campos em formato HTML para serem mostrados mas páginas para o usuário. */
	private String ajudaCampo = "";
	
	
	/** Guarda o campo e sub campo que o usuário selecionou na busca da entra autorizada     
	 * na volta da pesquisa é preciso colocar todos os subcampo do campo selecionado da autoridade
	 * no Título e impedir a entrada autorizada nesse sub campo  */
	private CampoDados campoSelecionadoParaDadosAutoridade;
	
	
	/** flag que indica se o usuário clicou no botão voltar da tela de edição do campo líder.*/
	private boolean voltandoPaginaLider = false;
	/** flag que indica se o usuário clicou no botão voltar da tela de edição do campo 008.*/
	private boolean voltandoPagina008 = false;
	
	/** Usado nos botões das páginas de edição dos campo de controle para indicar que o usuário
	 * só quer voltar para a página de catalogação, não vai adicionar nem editar o campo de controle. */
	private String voltarPaginaCatalogacao = "false";
	
	
	/** Usados na página de catalogação para habilitar o botão "voltar" correto. */
	private boolean catalogacaoDoZero = false;
	/** Usados na página de catalogação para habilitar o botão "voltar" correto. */
	private boolean catalogacaoImportacao = false;
	/** Usados na página de catalogação para habilitar o botão "voltar" correto. */
	private boolean catalogacaoPlanilha = false;
	/** Usados na página de catalogação para habilitar o botão "voltar" correto. */
	private boolean catalogacaoDuplicacao = false;
	
	
	
	/**
	 * Guarda a lista de categorias de material para não ficar buscando toda a vida que for
	 * entrar na página. Como não existem cadastro para isso, dificilmente essa informação mudará.
	 */
	private List<CategoriaMaterial> listaCategoriaMaterial = null;
	
	
	/** Usando quando o usuário vai utilizar a busca das editoras cadastradas no SIPAC
	 * Guarda o nome a editora escolhida e os índices do sub campo 260$b selecionado onde deve
	 * ser colocado o valor da editora selecionada */
	private String nomeEditora;
	
	/**
	 *  Guarda índices do campo selecionado. <br/>
	 *  Usado na busca cutter 090$b e na busca de editoras 260$b */
	int indiceCampoSelecionado = -1;
	
	/**
	 *  Guarda índices do sub campo selecionado. <br/>
	 *  Usado na busca cutter 090$b e na busca de editoras 260$b
	 */
	int indiceSubCampoSelecionado = -1;
	
	
	/**
	 * Guarda temporariamente as sugestões encontradas na tabela cutter para serem mostras ao usuário
	 * e ele escolher qual o mais adequado.
	 */
	private List<DadosTabelaCutter> suguestoesTabelaCuter;
	
	/**
	 * Tempo que a catalogação vai ser automaticamente salva pelo sistema. Esse valor é um parâmetro do sistema.
	 */
	private int tempoSalvamentoCatalogacacao = 0;
	
	/**
	 * Guardas as grandes áreas de conhecimento CNPq para associar a um Título.
	 */
	private List<AreaConhecimentoCnpq> grandesAreasCNPq;
	
	/**
	 * Guarda o id do material selecionado na rich:tree para ser editado
	 */
	private Integer idMaterialSelecionadoEdicao;
	
	/** Flag que indica se o usuário deseja que o painel lateral seja exibido. O painel lateral contém informação como as classificações e materiais da catalogação.
	 * Porém diminui a área para digitar o texto. */
	private boolean exibirPainelLateral = true;
	
	
	
	/** Indica se a tela de catalogação a ser usado é a completa ou a simplificado. 
	 *  Por padrão vai ser a completa, mas essa configuração vai ser salva para cada pessoa.
	 */
	private boolean usarTelaCatalogacaoCompleta = true;
	
	/** Contém as configurações para o usuário para a tela de catalogação.  
	 * Personalizações que cada usuário pode fazer da maneira que gosta de catalogar.*/
	private ConfiguracoesTelaCatalogacao configuracoesTela;
	
	
	/** O id da planilha de catalogação simplificada escolhida pelo usuário. É a partir dessa planilha que a tela de catalogação 
	 * simplificada é montada. Os detalhes técnicos do MARC como campos de controle, indicadores, códigos de sub campos são t
	 * odos retirados dessa planilha, simplificando a catalogação para o usuário.*/
	private int idPlanilhaCatalogacaoSimplificada = -1;
	
	/** Contém a planilha de catalogação simplificada utilizada para mantar a tela de catalogação simplificada*/
	private PlanilhaCatalogacao planilhaCatalogacaoSimplificada = null;
	
	/**
	 * Construtor padrão (Chamado sempre que tiver um keep alive na página por isso não é recomendado colocar código aqui)
	 */
	public CatalogacaoMBean() {
		obj = new TituloCatalografico();
	}
	
	
	/**
	 * Inicia o dados que são contantes e que devem permanecer em memória durante o processo de catalogação. 
	 * Deve ser chamado SEMPRE ao iniciar caso de uso de catalogação.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public void configuraDadosInicializacaoCatalogacao() throws DAOException{
		
		
		tempoSalvamentoCatalogacacao = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.TEMPO_SALVAMENTO_AUTOMATICO_CATALOGACAO);
		
		idPlanilhaCatalogacaoSimplificada = -1; // para carregar os dados corretamente o usuário deve primeiro escolher uma planilha
		
		GenericDAO daoGenerico = null;
		
		try{
			daoGenerico = getGenericDAO();
			
			// Só existe 1 por pessoa.
			configuracoesTela =  daoGenerico.findByExactField(ConfiguracoesTelaCatalogacao.class, "pessoa.id", getUsuarioLogado().getPessoa().getId(), true);
			
			if(configuracoesTela != null){ // se o usuário salvou alguma configuração, altera os valores padrões.
				this.exibirPainelLateral = configuracoesTela.isExibirPainelLateral();
				this.usarTelaCatalogacaoCompleta = configuracoesTela.isUsarTelaCatalogacaoCompleta();
			}else{
				// cria um configuração com os valores padrões para se o usuário desejar salvar no banco.
				configuracoesTela = new ConfiguracoesTelaCatalogacao(getUsuarioLogado().getPessoa(), this.exibirPainelLateral, this.usarTelaCatalogacaoCompleta);
			}
			
		}finally{
			if(daoGenerico != null) daoGenerico.close();
		}
		
		
		inicializaEtiquetasPersistidas();
		
		
		if(isTipoCatalogacaoBibliografica() ){
		
			// CONFIGURA AS CLASSIFICAÇÕES //
			
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
				
				// CONFIGURA A ÁRVORE DE MATERIAIS //
				
				rootNode = new TreeNodeImpl<String>();
				visualizaListaMateriaisTitulo = false;
				
				MaterialInformacionalDao dao = null;
				try {
					dao = getDAO(MaterialInformacionalDao.class);
				
					quantidadeMateriaisTitulo = dao.countMateriaisAtivosByTitulo(obj.getId());
					
					if(quantidadeMateriaisTitulo <= 20){
						mostrarListaMateriaisTitulo(null); // Até vinte materiais, mostra por padrão, maior que isso só se o usuário solicitar
					}
					
				} finally {
					if (dao != null)dao.close();
				}
			}
				
		} // isTipoCatalogacaoBibliografica() && obj.getId() > 0
		
	}
	
	
	/**
	 * Inicia o caso de uso catalogar um Título.
	 * Aquele onde o usuário escolhe todos os dados do Título.
	 * 
	 * @return
	 * @throws ArqException
	 *
	 *  <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecncios/pesquisas_acervo/pesquisaTituloCatalografico.jsp
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
	 *       Método que inicia o caso de uso já na tela da catalogação com os dados
	 *  vindos da importação.
	 * 
	 *      <br/><br/>
	 *      Chamado do Mbean: {@link CooperacaoTecnicaImportacaoMBean#realizarInterpretacaoDados()}
	 *      Método não invocado por nenhuma JSP.
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
			// Só pode ordenar 1 vez senão o dataModel não funciona bem
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
	 *      <p> Método que inicia o caso de uso já na tela da catalogação com os dados
	 *  vindos da duplicação. </p>
	 * 
	 *      <br/><br/>
	 *  	Chamado do Mbean: {@link PesquisaTituloCatalograficoMBean#duplicarTituloCatalografico()}
	 *     <br/>
	 *      Método não invocado por nenhuma JSP
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
			// Só pode ordenar 1 vez senão o dataModel não funciona bem
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
	 *    Método chamado da página na qual o usuário escolhe a planilha. Os dados da planilha
	 * escolhida vão estar em tituloTemp, aí joga no título verdadeiro e chama a página de catalogação.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecncios/catalogacao/escolhePlanilhabibliografica.jsp
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
		
		
		// Deve ordenar antes de ser incluído no dataModel
		
		if( obj.getCamposDados() != null ){
			CatalogacaoUtil.incluiCampoDadosLOC(obj, true);
			
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar ordena os sub campos depois fica do jeito que o usuário digitou
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
		
		// Se o usuário começou a catalogação por planilha e está utilizado a catalogação simplificada 
		// então já marca para planilha da catalogação simplificada como sendo a planilha escolhida
		if(! isUsarTelaCatalogacaoCompleta()){
			this.idPlanilhaCatalogacaoSimplificada = this.idPlanilhaEscolhida;
			alterouPlanilhaCatalogacao(null);
		}
			
		return telaDadosTituloCatalografico();
	}
	
	
	
	/**
	 *     <p>Inicia o caso de uso de catalogação a partir da seleção de uma defesa,
	 * pré-populando os dados da catalogação a partir dos dados existentes na
	 * defesa cadastrada. </p>
	 * 
	 * 	<p>Se a defesa já foi catalogada, então o título referente a ela é exibido para
	 * edição</p>
	 * 
	 * <p>Chamado a partir da página: sigaa.war/stricto/banca_pos/consulta_defesas.jsp<p>
	 */
	public String iniciarDefesa() throws NegocioException, ArqException {
		
		tipoCatagalocao = TipoCatalogacao.BIBLIOGRAFICA;
		
		catalogacaoDeDefesa = true;
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		zeraTiposCatalogacao();
		
		
		int idBancaPos = getParameterInt("idBancaPos", 0);
		BancaPos banca = getDAO(BancaPosDao.class).findByPrimaryKey(idBancaPos, BancaPos.class);
		
		// Verifica se a defesa já tem um título catalogado. Se sim, deixa o usuário editá-lo, em vez
		// de criar  um novo.
		
		TituloCatalograficoDao titDao = null;
		
		try{
			titDao = getDAO(TituloCatalograficoDao.class);
			Integer idTitulo = titDao.findTituloReferenteADefesa( banca.getDadosDefesa().getId() );
			
			if ( idTitulo != null ) {
				addMensagemInformation("A defesa escolhida já tem um título catalogado. " +
						"Ele foi aberto para que você possa verificar e fazer alterações, se necessário.");
				
				prepareMovimento(SigaaListaComando.ATUALIZA_TITULO);
				
				// recupera todos dados do título, já que ele vai ser editado
				obj = titDao.findTituloByIdInicializandoDados( idTitulo );
				
				return iniciarParaEdicao(obj);
			}
			
			// Se a defesa ainda não foi catalogada, cria um novo título
			
			obj = new TituloCatalografico();
			obj.setFormatoMaterial( new FormatoMaterial(ParametroHelper.getInstance().getParametroInt(
					ParametrosBiblioteca.FORMATO_MATERIAL_LIVRO)) );
			getGenericDAO().initialize(obj.getFormatoMaterial());
			
			// Monta campos LIDER e 008 com valores padrões.
			
			Etiqueta etiquetaLider = getDAO(EtiquetaDao.class).findEtiquetaInicializandoDados(
					Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO);
			new CampoControle(CatalogacaoUtil.montaDadosCampoControle (
					getValoresPadraoLider( etiquetaLider), etiquetaLider ), etiquetaLider, -1, obj);
			
			Etiqueta etiqueta008 = getDAO(EtiquetaDao.class).findEtiquetaInicializandoDados(
					Etiqueta.CAMPO_008_BIBLIOGRAFICO);
			CampoControle campo008 = new CampoControle(CatalogacaoUtil.montaDadosCampoControle(
					getValoresPadrao008(etiqueta008), etiqueta008 ), etiqueta008, -1, obj);
			
			// Local de publicação (15, 17): bl - Brasil
			campo008.setDado( campo008.getDado().substring(0, 15) + "bl " + campo008.getDado().substring(18) );
			// Natureza do conteúdo (24, 27): m - Teses e dissertações
			campo008.setDado( campo008.getDado().substring(0, 24) + "m   " + campo008.getDado().substring(28) );
			// Forma literária (33): 0 - Não é uma obra literária
			campo008.setDado( campo008.getDado().substring(0, 33) + "0" + campo008.getDado().substring(34) );
			// Idioma (35, 37): por - português
			campo008.setDado( campo008.getDado().substring(0, 35) + "por" + campo008.getDado().substring(38) );
			
			CatalogacaoUtil.criarTituloCatalograficoAPartirDeDefesa(banca, obj);
	
			CatalogacaoUtil.incluiCampoDadosLOC(obj, true);
			
			// Deve ordenar antes de ser incluído no data model //
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
		   
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar ordena os sub campos depois fica do jeito que o usuário digitou
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
	 *    Método que inicia o caso de uso de edição de Títulos.
	 * 
	 *      <br/><br/>
	 *    Chamado a partir de: {@link PesquisaTituloCatalograficoMBean#editarTitulo}
	 *    <br/>
	 *    Método não invocado por nenhuma JSP.
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
					
					if(! c.getEtiqueta().isAtiva()){  // se a etiqueta do campo não está mais ativa
						
						Etiqueta e =  dao.findEtiquetaPorTagETipoAtivaInicializandoDados(
								c.getEtiqueta().getTag(), c.getEtiqueta().getTipo());
						
						if(e != null){ // criaram a mesma etiqueta novamente, então atribui ao campo
							c.setEtiqueta(e);
							
							if(! etiquetasBuscadas.contains(e))  // Otimiza as busca das etiquetas
								etiquetasBuscadas.add(e);
						}
						// ELSE fica com a etiqueta desativa que já possuía e o sistema não vai deixar
						// salvar o título com a etiqueta desativada
						
					}
				}
			
			}finally{
				if(dao != null ) dao.close();
			}
			
			// Deve ordenar antes de ser incluído no dataModel
			Collections.sort(obj.getCamposDadosNaoReservados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar, ordena os sub campos depois ajusta conforme o usuário digitou.
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
	 * Inicia o caso de cadastro de autoridades onde o usuário informa todos os dados.
	 *
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaAutoridades.jsp
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
	 * Inicia o caso de cadastro de autoridades onde o usuário utiliza alguma planilha de
	 *   catalogação de autoridades previamente cadastrada no sistema.<br/>
	 * 
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaAutoridades.jsp
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
		
			// Deve ordenar antes de ser incluído no dataModel
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar ordena os sub campos depois fica do jeito que o usuário digitou
		    for (CampoDados dados : camposNaoReservados) {
		    	dados.setDataModelSubCampos(new ListDataModel(dados.getSubCampos()));
			}
	    
		}else{
			dataModelCampos = new ListDataModel();
		}
	    
	    prepareMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
		
		editando = false;
		
		configuraDadosInicializacaoCatalogacao();
		
		// Se o usuário começou a catalogação por planilha e está utilizado a catalogação simplificada 
		// então já marca para planilha da catalogação simplificada como sendo a planilha escolhida
		if(! isUsarTelaCatalogacaoCompleta()){
			this.idPlanilhaCatalogacaoSimplificada = this.idPlanilhaEscolhida;
			alterouPlanilhaCatalogacao(null);
		}
		
		return telaDadosTituloCatalografico();
		
	}
	
	
	/**
	 * 
	 * Inicia o caso de cadastro de uma nova autoridade que já possua dados.
	 *
	 *       <br/><br/>
	 *     Chamado a partir do MBean: {@link CooperacaoTecnicaImportacaoMBean#realizarInterpretacaoDados}
	 *     <br/>
	 *     Método não invocado por nenhuma JSP.
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
	    	
		    // ao iniciar ordenar os sub campos depois fica do jeito que o usuário digitou
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
	 *     Pode ser usado também para iniciar a catalogação de uma autoridade que já tenha dados
	 *  passando importação = false
	 *
	 *      <br/><br/>
	 *     Chamado a partir do MBean: {@link CooperacaoTecnicaImportacaoMBean#realizarInterpretacaoDados}
	 *     <br/>
	 *     Método não invocado por nenhuma JSP
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
	    	
		    // ao iniciar ordena os sub campos depois fica do jeito que o usuário digitou
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
	 *   do objeto TituloCatalografico são copiados para um objeto do tipo Autoridade.
	 *
	 *     <br/><br/>
	 *   Chamado a partir do MBean: {@link CatalogaAutoridadesMBean#editarAutoridade}
	 *   <br/>
	 *   Método não invocado por nenhuma JSP
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
					
					if(! c.getEtiqueta().isAtiva()){  // se a etiqueta do campo não está mais ativa
						
						Etiqueta e = dao.findEtiquetaPorTagETipoAtiva(c.getEtiqueta().getTag(),
								c.getEtiqueta().getTipo());
						
						if(e != null){ // criaram a mesma etiqueta novamente, então atribui ao campo
							c.setEtiqueta(e);
						}
						// ELSE fica com a etiqueta desativa que já possuía e o sistema não vai deixar
						// salvar o título com a etiqueta desativada
						
					}
				}
			
			}finally{
				if(dao != null) dao.close();
			}
			
			// Deve ser ordenado antes de ser colocado no dataModel
			Collections.sort(obj.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
			
			atribuiCamposDoTituloAosCampoUsadosNaTela();
		    
		    // ao iniciar ordena os sub campos depois ajusta conforme o usuário digitou.
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
	 * <p>Método que deve ser chando quando se está editando algum Título/Autoridade que já tenha 
	 * algum campo de dados persistido.</p>
	 * 
	 * <p>Porque caso 2 ou mais campos possuam a mesma etiqueta é recuperado o mesmo 
	 * objeto do banco, então ao alterar a "tag" de uma etiqueta no formulário o sistema automaticamente 
	 * altera as outras etiquetas com a mesma "tag".</p>
	 * 
	 * <p>Esse erro não ocorria com novos campos adicionados porque sempre são retornados objetos 
	 * etiquetas cópias das originais. Esse método percorre todas os campos de dados e substitui as 
	 * etiquetas por novos objetos cópias do objeto recuperado.</p>
	 * 
	 * 
	 *   <p>Deve ser chamado ainda de iniciar qualquer alteração.</p>
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
	 *    Método chamado quando o usuário digitar algo nos campos de classificação bibliografica(080...089)
	 *
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
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
	 * Associa ou altera a associação da catalogação que está sendo catalogada com uma defesa do sistema.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			
			
			addMensagemWarning(" A simples associação não preenche nem altera os dados da catalogação. Para preencher os " +
					" dados da catalogação com os dados de uma defesa, utilize a opção de iniciar a catalogação a partir de uma defesa.");
			addMensagemInformation("Associação realizada com sucesso. ");
			
			return telaDadosTituloCatalografico();
		}finally{
			if(bancaDao != null) bancaDao.close();
		}
		
		
	}
	
	
	/**
	 * 
	 * Remove a associação da catalogação com a defese de tese ou dissertação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Zera os outros tipo antes de começar um novo tipo de catalogação.
	 */
	private void zeraTiposCatalogacao(){
		catalogacaoDoZero = false;
		catalogacaoImportacao = false;
		catalogacaoPlanilha = false;
		catalogacaoDuplicacao = false;
	}
	
	
	
	
	
	/**
	 *    Método chamado quando o usuário deseja ordenar os campos de dados MARC da página de catalogação.
	 *
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
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
	 *    Método chamado quando o usuário deseja validar as informações MARC de um título ou autoridade mas
	 * sem precisar salvá-la no banco.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
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
				addMensagemInfoAjax("Informações MARC do Título estão corretas.");
			if(isTipoCatalogacaoAutoridade())
				addMensagemInfoAjax("Informações MARC da Autoridade estão corretas.");
		}
		
	}
	
	
	
	
	/**
	 *   Método chamado quando o usuário deseja remover os campos vazios que não serão preenchidos.
	 *
	 *  <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
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
	 *    Método que inicia a catalogação na qual podem ser incluídos materiais que não estão tombados
	 * no patrimônio da Instituição.
	 * 
	 *    Esse caso de uso é uma exceção para contemplar exemplares e assinaturas antigas que não
	 * foram tombados no sistema.
	 * 
	 *      <br/><br/>
	 *    Chamado a partir da página: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
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
		// Seta a informação que a catalogação é sem tombamento e vai para a tela de busca.          //
		// quando voltar para esse bean por um dos caminhos possível já vai ser saber qual o tipo de //
		// catalogação.                                                                              //
		///////////////////////////////////////////////////////////////////////////////////////////////
		infoTituloCompra = null;
		catalogacaoMateriaisSemTombamento = true;
		
		PesquisaTituloCatalograficoMBean bean = getMBean("pesquisaTituloCatalograficoMBean");
		bean.setPesquisaTituloParaCatalogacaoComTombamento(true);
		return bean.iniciarPesquisa();
	}
	
	
	
	
	
	/**
	 *    Busca as informações dos materiais da biblioteca no SIPAC pelo número de patrimônio do bem.
	 * 
	 *    Usado no início da catalogação quando envolve a catalogação de materiais que precisam de
	 * número do tombamento dos bens no SIPAC.
	 * 
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/buscaInformacoesSipacAPartirNumeroPatrimonio.jsp
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
				addMensagemWarning("Informe o Número do Patrimônio de um Bem. ");
				return null;
			}
		
		}catch(RemoteAccessException rae){
			rae.printStackTrace();
			notifyError(rae);      // IMPORTANTE :  mandar email para em caso de erro descobrir a causa.
			addMensagemErro("Erro ao tentar busca informações no "+ RepositorioDadosInstitucionais.get("siglaSipac") );
			return null;
		}catch (Exception ex) {  // O sistema em produção não está conseguindo lançar diretamente NegocioRemotoBibliotecaException
			
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
					addMensagemErro("O "+ RepositorioDadosInstitucionais.get("siglaSipac")+" está indisponível no momento, tente novamente em alguns minutos. " );
					return null;
					
				}else{
					ex.printStackTrace();
					notifyError(ex);      // IMPORTANTE :  mandar email para em caso de erro descobrir a causa.
					addMensagemErro("Erro ao tentar busca informações no "+ RepositorioDadosInstitucionais.get("siglaSipac") );
					return null;
				}
			}
		}
		
		if( infoTituloCompra == null ){
			addMensagemErro("Bem com o número do patrimônio: "+ numeroPatrimonio+" não encontrado.");
			infoTituloCompra = null;
			return null;
		}else{
		
			// Com dados de compra sempre é exemplar, nunca assinatura.
			
			CatalogacaoUtil.calculaInformacoesExemplaresNoAcerco(infoTituloCompra);
			
			if(infoTituloCompra.getNumeroMateriaisInformacionaisNaoUsados() == 0
					&& infoTituloCompra.getTitulo() != null
					&& infoTituloCompra.getDescricaoTermoResponsabibliodade() != null){
				if (infoTituloCompra.getTitulo() != null && infoTituloCompra.getDescricaoTermoResponsabibliodade() != null){
					addMensagemWarning("Todos os materiais para o Título \" "+infoTituloCompra.getTitulo()+" \" do Termo de Responsabilidade Nº "
							+infoTituloCompra.getDescricaoTermoResponsabibliodade()+" foram incluídos no acervo.");
				}
				else{
					addMensagemWarning("Todos os materiais para do Título foram incluídos no acervo. Não foi possível recuperar informação do termo de responsabilidade");
				}	
			}
			
		}
		
		return telaBuscaInformacoesSipacAPartirNumeroPatrimonio();
	
	}
	
	
	
	
	/**
	 *   <p>Método chamado toda vez que a catalogação é realizada a partir de uma compra.</p>
	 *
	 *   <p>Guarda as informações dos números de patrimônio gerados, porque na tela de inclusão
	 * de exemplares essas informações devem vir preenchidas. O usuário não vai digitar os números
	 * de patrimônio.</p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/buscaInformacoesSipacAPartirNumeroPatrimonio.jsp
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
	 *     Configura as informações do título com os dados vindos do livro do SIPAC. Usado quando
	 * se está usando a forma mais "simples" de catalogação. Sem usar planilha, importação, etc..
	 *
	 * 		 <br/><br/>
	 *      Chamado a partir do MBean: CatalogaAPartirTombamentoMBean
	 *      <br/>
	 *      Método não invocado por nenhuma JSP.
	 * @param titulo
	 */
	public void configuraInformacoesTitulo(TituloCatalografico titulo){
		this.obj = titulo;
	}
	
	
	
	
	/**
	 *    Chamado quando o usuário vai voltar a página de edição do campo lider.
	 * 
	 *      <br/><br/>
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
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
	 * Chamado quando o usuário vai voltar a página de edição do campo 008.
	 *
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
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
	 *      Quando um usuário seleciona um formato de material esse método encaminha para o próximo passo
	 * do caso de uso que é a tela de preenchimento do campo lider.
	 * 
	 *    <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolheFormatoMaterial.jsp
	 * @throws ArqException
	 */
	public String submeteFormatoMaterialIniciandoCatalogacao() throws ArqException{
		
		prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
		
		catalogacaoDoZero = true;
		
		int idFormatoMaterial = getParameterInt("idFormatoMaterial");
		obj.setFormatoMaterial( new FormatoMaterial(idFormatoMaterial) );
		
		getGenericDAO().initialize(obj.getFormatoMaterial());
		
		etiquetaControleAtual = Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO; // formato só tem bibliográfica
		
		montaDadosAjudaControle(etiquetaControleAtual);
		
		// se escolher outro formato tem que recarregar as informações
		voltandoPaginaLider = false;
		voltandoPagina008 = false;
		
		errosValidacaoTeladeControle = false;
		
		return telaCampoControleLider();
	}
	
	
	
	
	/**
	 *     <p>Esse método é chamado na mesma página que o usuário escolhe o formato do material.</p>
	 *     <p>Sendo que nesse caso foi porque se chegou na tela de catalogação e o título não possuía
	 * um formato de material. Seja porque ele foi importado e salvo sem formato ou seja por
	 * causa de algum erro do sistema que chegou até a tela de catalogação sem o formato.</p>
	 *     <p>Esse método só atribui o formato ao título.</p>
	 * 
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolheFormatoMaterial.jsp
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
		
		//if(obj.getId() > 0 )// O objeto já tinha sido salvo
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
	 *     Pega os valores que estão na página, copia para a lista "valoresLider", cria um campo de
	 *  controle com esses valores, valida os dados,
	 *  remove algum campo lider que por acaso exista no título pois só pode existir apenas 1, e adiciona
	 *  esse novo ao título.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampoLider.jsp
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
				
				// Como o usuário vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso já tenha um campo líder.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO);
				
				// Já cria e adiciona ao Título
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
				
				// Como o bean está em sessão e o usuário vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso já tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_LIDER_AUTORIDADE);
				
				// Já cria e adiciona ao título
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
			// estava apenas editando então volta direto para a página de catalogação
			adicionandoCamposDeControle = false;
			editandoCamposDeControle = false;
			return telaDadosTituloCatalografico();
		}else{
			// criando pela primeira vez então vai para a página do 008
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
	 *   Cria o campo de controle 001 e adiciona ao título com os dados que o usuário digitou na página.
	 *
	 *   <br/><br/>
	 *	 Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo001.jsp
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
				
				// Como o bean está em sessão e o usuário vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso já tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_001_BIBLIOGRAFICO);
				
				// Já cria e adiciona ao título
				// Aqui não precisa montar os dados só que vem da tela, pois já são os dados dos campos de controle.
				new CampoControle(dadosCamposControle, etiqueta001, -1, obj);
				
				// 001 POR ENQUANTO NÃO TEM VALIDAÇÃO
				
			}
			
			if(isTipoCatalogacaoAutoridade()){
				etiqueta001 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_001_AUTORIDADE.getTag(),
						  														Etiqueta.CAMPO_001_AUTORIDADE.getTipo());
				
				// Como o bean está em sessão e o usuário vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso já tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_001_AUTORIDADE);
				
				// já cria e adiciona ao título
				// aqui não precisa montar os dado so que vem da tela já é os dados dos campos de controle
				new CampoControle(dadosCamposControle, etiqueta001, -1, obj);
				
				// 001 POR ENQUANTO NÃO TEM VALIDAÇÃO
				
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
	 *	 Cria o campo de controle 003 e adiciona ao título com os dados que o usuário digitou na página
	 *
	 *     <br/><br/>
	 *	 Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo003.jsp
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
				
				// Como o bean está em sessão e o usuário vai poder ficar indo e voltando a essa tela
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_003_BIBLIOGRAFICO);
				
				// Já cria e adiciona ao Título.
				// Aqui não precisa montar os dado so que vem da tela já é os dados dos campos de controle
				new CampoControle(dadosCamposControle, etiqueta003, -1, obj);
			
				
			}
			
			if(isTipoCatalogacaoAutoridade()){
				etiqueta003 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_003_AUTORIDADE.getTag(),
						  														Etiqueta.CAMPO_003_AUTORIDADE.getTipo());
				
				// Como o bean está em sessão e o usuário vai poder ficar indo e voltando a essa tela
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_003_AUTORIDADE);
				
				// Já cria e adiciona ao título.
				// Aqui não precisa montar os dado só que vem da tela, já são os dados dos campos de controle
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
	 *   Cria o campo de controle 005 e adiciona ao título com os dados que o usuário digitou na página
	 *
	 *     <br/><br/>
	 *	 Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo005.jsp
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
				
				// Como o bean está em sessão e o usuário vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso já tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_005_BIBLIOGRAFICO);
			
				// Já cria e adiciona ao título.
				// Aqui não precisa montar os dado so que vem da tela já é os dados dos campos de controle.
				new CampoControle(dadosCamposControle, etiqueta005, -1, obj);
				
			}
			
			if(isTipoCatalogacaoAutoridade()){
				etiqueta005 = getDAO(EtiquetaDao.class).findEtiquetaPorTagETipoAtiva(Etiqueta.CAMPO_005_AUTORIDADE.getTag(),
						  														Etiqueta.CAMPO_005_AUTORIDADE.getTipo());
				
				// Como o bean está em sessão e o usuário vai poder ficar indo e voltando a essa tela
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_005_AUTORIDADE);
				
				// Já cria e adiciona ao título.
				// Aqui não precisa montar os dado so que vem da tela já é os dados dos campos de controle.
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
	 *    <p>Método chamado na página de edição dos dados do campo 006: formDadosCampo006.jsp</p>
	 *    <p>Dependendo da ação selecionada pelo usuário, apenas volta para a página de catalogação
	 *    ou adiciona um novo campo de controle 006.</p>
	 *	
	 *     <br/><br/>
	 *	  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo006.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String submeterCampo006() throws DAOException{
		
		
		if(voltarPaginaCatalogacao.equals("false")){
		
			montaValoresDigitadosPeloUsuario();
			
			// só existe na bibliográfica
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
				// Já cria e adiciona ao título
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
					
					// Se não tem erros adiciona ao Título:
					
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
	 *    Método chamado para submeter os dados do campo 007.
	 * 
	 *   <br/><br/>
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo003.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public String submeterCampo007() throws DAOException{
		
		
		if(voltarPaginaCatalogacao.equals("false")){
		
			montaValoresDigitadosPeloUsuario();
			
			// só tem na bibliográfica
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
				// já cria e adiciona ao Título
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
					
					// Se não teve erro na validação é que pode adicionar o campo ao título
					
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
	 * se tudo estiver correto, adiciona ao título e segue para a tela onde é adicionado os outros campos,
	 * senão fica na mesma página mostrando as mensagens de erro na validação.
	 * 
	 *    Esse método pega os valores do campo 008 (que pode ser os padrões vindo do banco
	 * ou se o usuário alterou ficou aqui também) e monta a String de dados do campo 008
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
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
				
				// Como o bean está em sessão e o usuário vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso já tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_008_BIBLIOGRAFICO);
				
				ListaMensagens listaErros =  new ListaMensagens();
				
				try{
					// Já cria e adiciona ao Título
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
				
				// Como o bean está em sessão e o usuário vai poder ficar indo e voltando a essa tela
				// tem que tomar esse cuidado de remover caso já tenha um campo lider.
				CatalogacaoUtil.removeCampoControle(obj, Etiqueta.CAMPO_008_AUTORIDADE);
			
				ListaMensagens listaErros =  new ListaMensagens();
				
				try{
					// Já cria e adiciona ao Título
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
			
			
			
			
			
			
			/* Se for a primeira vez que vai entrar na tela de catalogação */
			
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
		
		//   Nesse caso usado somente para habilitar e desabilitar o botão voltar da página de edição do
		// campo 008,
		//   Se o usuário está editando o campo apenas a opção de voltar para a página de catalogação
		// deve está habilitada.
		if(editandoCamposDeControle || adicionandoCamposDeControle){
			adicionandoCamposDeControle = false;
			editandoCamposDeControle = false;
			
		}
		
		voltarPaginaCatalogacao = "false";
		return telaDadosTituloCatalografico();
		
	}
	
	
	/**
	 *    <p>Método que implementa a função voltar da tela de catalogação.</p>
	 *    <p>Como essa página é chamada de vários lugares, existe uma lógica para determinar para onde
	 * deve-se voltar.</p>.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
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
	 * 		 Método chamado na página principal da catalogação para validar, salvar ou atualizar
	 *  o título ou autoridade ou tudo mais que for usar essa tela de catalogação MARC.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String submeterDadosTituloCatalografico() throws ArqException{
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		if( ! isUsarTelaCatalogacaoCompleta() && isTipoCatalogacaoBibliografica()){
			if(planilhaCatalogacaoSimplificada != null
					&& planilhaCatalogacaoSimplificada.getIdFormato() != obj.getFormatoMaterial().getId()){
				addMensagemErro("O formato de Formato de Material da planilha escolhida não é igual ao Formato de Material do Título");
				return null;
			}
		}
		
		// o usuário clicou no botão voltar do navegador
		if( obj.getCamposDados() == null && obj.getCamposControle() == null){
			addMensagemErro("Operação já processada. Isso ocorreu provavelmente porque você usou o botão voltar no navegador.");
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
	 * 	Método que realiza a criação ou alteração quando a catalogação é de um Título
	 */
	private String realizaOperacoesTitulo() throws ArqException{
		
		if(editando){
		
			MovimentoAtualizaTitulo mov = new MovimentoAtualizaTitulo(obj, arquivo, apagarArquivoDigitalSalvo, false, classificacoesUtilizadas);
			mov.setCodMovimento(SigaaListaComando.ATUALIZA_TITULO);
			
			try{
				execute(mov);
			}catch(NegocioException e){           // outro erro de negocio sem ser do padrão MARC
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			addMensagemInformation("Título Atualizado com Sucesso.");
				
			
		}else{   // CRIANDO O TÍTULO
			
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
			
			
			// Coloca um mensagem de sucesso para o usuário  //
			if("true".equals(finalizarCatalogacao) || "true".equals(adicionarMaterialInformacional)){
				addMensagemInformation("Título Catalogado com Sucesso. Nº do sistema : "+obj.getNumeroDoSistema());
			}else{
				addMensagemInformation("Título Salvo com Sucesso. Nº do sistema : "+obj.getNumeroDoSistema());
			}
			
		}
		
		return redirecionaProximaPaginaDepoisCatalogacao();
		
	}
	
	
	
	/**
	 * Método que realiza a criação ou alteração quando a catalogação é de uma autoridade.
	 */
	private String realizaOperacoesAutoridades() throws ArqException{

		/* *****************************************************************************************
		 * FEZ TODAS AS OPERAÇÕES EM CIMA DE UM TÍTULO PORQUE A ESTRUTURA É A MESMA E APROVEITA    *
		 * MUITA COISA, AGORA COPIA OS CAMPOS DE DADOS E CONTROLE PARA UMA AUTORIDADE E SALVA      *
		 *******************************************************************************************/
		
		Autoridade autoridade = CatalogacaoUtil.criaAutoridaeAPartirTitulo(obj);
		
		if(editando){
			
			autoridade.setId(obj.getId()); // o id da autoridade estava no título.
			
			MovimentoAtualizaAutoridade mov = new MovimentoAtualizaAutoridade(autoridade, false, classificacoesUtilizadas);
			mov.setCodMovimento(SigaaListaComando.ATUALIZA_AUTORIDADE);
			
			try{
				execute(mov);
			}catch(NegocioException e){           // outro erro de negocio sem ser do padrão MARC
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			addMensagemInformation("Autoridade Atualizada com Sucesso.");
			
			if( "true".equals(finalizarCatalogacao)){
				return cancelar(); // página inicial do sistema
			}else{
				prepareMovimento(SigaaListaComando.ATUALIZA_AUTORIDADE);
				
				ordenaCamposDados(null); // para ordenar na página para o usuário, antes de salvar ele atualiza mas não se reflete na página para o usuário
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
			}catch(NegocioException e){           // outro erro de negócio sem ser do padrão MARC
				e.printStackTrace();
				addMensagens(e.getListaMensagens());
				return null;
			}
			
			
			obj = CatalogacaoUtil.criaTituloAPartirAutoridade(autoridade);  // IMPORTANTE
			
			
			if("true".equals(finalizarCatalogacao) ){
				
				addMensagemInformation("Autoridade Cadastrada com Sucesso. Nº do sistema : "+autoridade.getNumeroDoSistema());
				
				if(possuiEntiadesNaoFinalizados){
					
					if("true".equals(redirecionaProximaAutoriadeIncompleta)){
						BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
						bean.setAutoridadesIncompletas(null); // para o bean buscar novamente
						return bean.iniciarBuscaAutoridadesIncompletosMantendoOperacao();
					}
				}
				
				return cancelar(); // página inicial do sistema
				
			}else{
				//return iniciarAutoridadesDuplicacao(obj);
				
				addMensagemInformation("Autoridade Salva com Sucesso. Nº do sistema : "+autoridade.getNumeroDoSistema());
				prepareMovimento(SigaaListaComando.CATALOGA_AUTORIDADE);
				
				ordenaCamposDados(null); // para ordenar na página para o usuário, antes de salvar ele atualiza mas não se reflete na página para o usuário
				
				return telaDadosTituloCatalografico();
			}
			
		}
		
	}
	
	
	
	
	/**
	 * Método que contém a lógica para qual página vai o caso de uso dependendo da ação selecionada
	 * pelo usuário.
	 */
	private String redirecionaProximaPaginaDepoisCatalogacao() throws ArqException{
		
		// se o usuário clicou no botão de adicionar material informacional
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
			// se está apenas finalizando o título então volta para a tela principal do sistema.
			if("true".equals(finalizarCatalogacao)){
				
				if(possuiEntiadesNaoFinalizados){ // volta para catalogar o próximo
					
					BuscaCatalogacoesIncompletasMBean bean = getMBean("buscaCatalogacoesIncompletasMBean");
					bean.setTitulosIncompletos(null);  // para buscar novamente
					return bean.iniciarBuscaTitulosIncompletos();
					
				}else
				
				return cancelar(); // PÁGINA PRINCIPAL
				
			}else{
				// se o usuário não finalizou nem adicionou materiais é porque
				//quer apenas salvar ou atualizar o título, então fica na mesma página.
				
				ordenaCamposDados(null); // para ordenar na página para o usuário, antes de salvar ele atualiza mas não se reflete na página para o usuário
				
				if(editando){
					//return iniciarParaEdicao(obj);
					prepareMovimento(SigaaListaComando.ATUALIZA_TITULO); // se vai ficar na mesma página prepara o proximo movimento
					return telaDadosTituloCatalografico();
				}else{
					//return iniciarDuplicacao(obj);
					prepareMovimento(SigaaListaComando.CATALOGA_TITULO); // se vai ficar na mesma página prepara o proximo movimento
					return telaDadosTituloCatalografico();
				}
				
				
			}
		}
		
	}
	
	
	
	/**
	 *   Adiciona um novo campo de controle.
	 *   Como os campos de controle seguem regras próprias vão ter suas próprias página como
	 * os campos LDR e 008.
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
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
		
			etiquetaControleAtual  = Etiqueta.CAMPO_007_BIBLIOGRAFICO; // 007 só tem bibliográfico
			montaDadosAjudaControle(etiquetaControleAtual);
			telaCampoControle007();
		
		}
		
	}
	
	
	/**
	 *   Edita um campo de controle que já esteja na página quando o usuário clicar em cima dele.
	 * 
	 *   Vai pegar a tag do campo e com base nela vai chamar a página correta de edição.
	 * 
	 *   So existem 6 possibilidades no padrão MARC:  'LDR', '001', '003', '005', '007', '008'
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public String editarCampoControle() throws DAOException{
		
		String dados = getParameter("conteudoCampoControleAlteracao");
		posicaoCampoControleSelecionado = getParameterInt("posicaoCampoControleSelecionado");
		
		// posicaoCampoControleSelecionado <- setado na página de catalogação ao clicar no link da etiqueta de controle
		String tag = obj.getCamposControle().get(posicaoCampoControleSelecionado).getEtiqueta().getTag();
		
		if(tag.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			etiquetaControleAtual = Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_LIDER_BIBLIOGRAFICO.length()){
				addMensagemErro("O tamanho do campo Líder está inválido por isso não pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			
			return telaCampoControleLider();
		}
		
		if(tag.equals(Etiqueta.CAMPO_001_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			dadosCamposControle = dados;
			etiquetaControleAtual = Etiqueta.CAMPO_001_BIBLIOGRAFICO;
			
			montaDadosAjudaControle(etiquetaControleAtual);
			
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle001();
		}
		
		if(tag.equals(Etiqueta.CAMPO_003_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			dadosCamposControle = dados;
			etiquetaControleAtual = Etiqueta.CAMPO_003_BIBLIOGRAFICO;
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle003();
		}
		
		if(tag.equals(Etiqueta.CAMPO_005_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			dadosCamposControle = dados;
			etiquetaControleAtual = Etiqueta.CAMPO_005_BIBLIOGRAFICO;
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle005();
		}
		
		if(tag.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			etiquetaControleAtual = Etiqueta.CAMPO_006_BIBLIOGRAFICO;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_006_BIBLIOGRAFICO.length()){
				addMensagemErro("O tamanho do campo 006 está inválido por isso não pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle006();
		}
		
		if(tag.equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			codigoCategoriaMaterial = String.valueOf( obj.getCamposControle().get(posicaoCampoControleSelecionado).getDado().charAt(0) ) ;
			etiquetaControleAtual = Etiqueta.CAMPO_007_BIBLIOGRAFICO;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_007_BIBLIOGRAFICO.length()){
				addMensagemErro("O tamanho do campo 007 está inválido por isso não pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle007();
		}
		
		if(tag.equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO.getTag()) && tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA){
			etiquetaControleAtual = Etiqueta.CAMPO_008_BIBLIOGRAFICO;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_008_BIBLIOGRAFICO.length()){
				addMensagemErro("O tamanho do campo 008 está inválido por isso não pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle008();
		}
		
		/////////////// Autoridades
		
		if(tag.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_LIDER_AUTORIDADE;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_LIDER_AUTORIDADE.length()){
				addMensagemErro("O tamanho do campo Líder para autoridades está inválido por isso não pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControleLider();
		}
		
		if(tag.equals(Etiqueta.CAMPO_001_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_001_AUTORIDADE;
			dadosCamposControle = dados;
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle001();
		}
		
		if(tag.equals(Etiqueta.CAMPO_003_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_003_AUTORIDADE;
			dadosCamposControle = dados;
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle003();
		}
		
		if(tag.equals(Etiqueta.CAMPO_005_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_005_AUTORIDADE;
			dadosCamposControle = dados;
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle005();
		}
		
		if(tag.equals(Etiqueta.CAMPO_008_AUTORIDADE.getTag()) && tipoCatagalocao == TipoCatalogacao.AUTORIDADE){
			etiquetaControleAtual = Etiqueta.CAMPO_008_AUTORIDADE;
			
			if( dados.length() != CampoControle.DADOS_CAMPO_008_AUTORIDADE.length()){
				addMensagemErro("O tamanho do campo 008 para autoridades está inválido por isso não pode ser editado");
				editandoCamposDeControle = false;
				return null;
			}
			
			montaDadosAjudaControle(etiquetaControleAtual);
			editandoCamposDeControle = true;  //indica que vai editar o campo de controle, então NÃO deve pegar os valores padrão do banco
			return telaCampoControle008();
		}
		
		return ""; // nunca era para chegar aqui
	}
	
	
	
	
	
	
	/**
	 * Adiciona um novo campo de dados
	 * Adiciona já com 1 sub campo, porque todo campo de dados tem que ter pelo menos um sub campo.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 */
	public void adicionarNovoCampoDados(ActionEvent e){
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		CampoDados c = new CampoDados(new Etiqueta("", (short) -1), obj, -1);
		new SubCampo(c); // cria um sub campo vazio e adiciona a lista de subcampos
		
		atribuiCamposDoTituloAosCampoUsadosNaTela();
		
	}
	
	
	
	/**
	 * Adiciona ao Título ou Autoridade o Campo de dados contido na planilha de catalogação que 
	 * está sendo usado com todos os seus sub campos.
	 *
	 * As informações dos indicadores, códigos dos sub campo já vão vim da planilha, o usuário não precisa conhecer....
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
	 * <p>Duplica o campo de dados selecionado pelo usuário.</p>
	 * 
	 * <p>Utilizado na catalogação simplificado, onde o usuário só pode adicionar os campos que já existem na planilha.</p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException 
	 * 
	 */
	public void duplicarCampo(ActionEvent e) throws DAOException{
		
		CampoDados cSelecionado = camposNaoReservados.get(dataModelCampos.getRowIndex());
		
		if ( !cSelecionado.getEtiqueta().isRepetivel() ){
			addMensagemErroAjax("Campo não pode ser repetido.");
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
			SubCampo sNovo = new SubCampo(sub.getCodigo(), null, c, null, -1); // duplica o sub campo selecionado pelo usuário
			sNovo.setDescricaoSubCampo(sub.getDescricaoSubCampo());
		}
		
		CatalogacaoUtil.configuraGruposEtiquetaVisiveis(obj); // na adição de um novo campo precisa reconfigurar os grupos
		
		atribuiCamposDoTituloAosCampoUsadosNaTela();
		
	}
	
	
	/**
	 * <p>Duplica o sub Campo selecionado pelo usuário se esse campo poder ser duplicado.</p>
	 * 
	 * <p>Usado apenas na catalogação simplificada, quando o usuário só pode adicionar campo e sub campos presentes na planilha.</p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
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
						addMensagemErroAjax("Sub campo não pode ser repetido.");
						return;
					}else{
						break;
					}
					
				} 
			}
		}finally{
			if(dao != null) dao.close();
		}
		
		SubCampo sNovo = new SubCampo(s.getCodigo(), null, c, null, -1); // duplica o sub campo selecionado pelo usuário
		sNovo.setDescricaoSubCampo(s.getDescricaoSubCampo());
		
	}
	
	
	/**
	 * 
	 * <p>Método que adiciona um novo campo de dados já completo, isto é, com os seus sub campo.</p>
	 * 
	 * <p>A busca bai trazer todos os sub campo e o usuário vai remover aqueles que não desejar utilizar, em vez de 
	 * adicionar o campo de dados e ter que adicionar os sub campos um por um.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			
			// NÃO busca as etiqueta reservadas do sistema, senão dar erro na página //
			if( etiqueta.equals(Etiqueta.CODIGO_DA_BIBLIOTECA)
					||  etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES)
					||  etiqueta.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO)
					||  etiqueta.equals(Etiqueta.CODIGO_DA_BIBLIOTECA_AUTORIDADES)
					||  etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES_AUTORIDADES)
					||  etiqueta.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO_AUTORIDADES)){
				
				// Não é uma etiqueta de uso reservado porque o tipo (-1) é invalido
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
					mensagensModelPanel.add("INFORMAÇÕES DO CAMPO NÃO ESTÃO CORRETAMENTE CADASTRADAS");
					campoDadosBuscaCompleta = null;
				}
				
				
			}
			
		}else{
			mensagensModelPanel.add("CAMPO NÃO CADASTRADO");
			campoDadosBuscaCompleta = null;
		}
		
	}
	
	/**
	 * <p>Método que remove os sub campos que o usuário não deseja adicionar. </p>
	 * 
	 * <p>Utilizado na adição de campo de dados com subcampos já adicionados.</p> 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		
		for (int index = 0 ; index < campoDadosBuscaCompleta.getSubCampos().size() ; index++) {    // Cria os subcampos com os códigos escolhidos
			
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
	 * <p>Método que adiciona um novo campo de dados já completo ao Título Ou Autoridade, isto é, com os seus sub campo.</p>
	 * <p>Adiciona no final da lista dos campo de dados. </p>
	 *  
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			for (SubCampo subTemp : campoDadosBuscaCompleta.getSubCampos()) {    // Cria os subcampos com os códigos escolhidos
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
	 * <p>Adiciona os os campos de dados obrigatórios para uma catalogação de Títulos pode ser salva. </p>
	 * <p>No caso são os campos 080, 084, 090, 245</p>
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException 
	 * 
	 */
	public void adicionarCampoDadosObrigatorios(ActionEvent e) throws DAOException{
		
		Etiqueta etiquetaClassificacao = null;
		
		Etiqueta etiquetaChamada = null;
		Etiqueta etiquetaTitulo = null;
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		// Cria um campo para cada classificações utilizada de forma genérica //
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
	 * Método que seleciona o id do material clicado na rich:tree.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			// não faz nada, geralmente esse método é chamado duas vezes quando o usuário clica em alterar material, na segunda vez vem nulo.
		}
	}
	
	/**
	 * 
	 * Método que iniciar a edição das informações de um material selecionado a partir da página de catalogação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		addMensagemErro("Não foi possível editar as informações do material selecionado");
		return null;
	}
	
	
	
	/**
	 * Remove um campo de dados.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 * @throws DAOException 
	 */
	public void removerCampoDados(ActionEvent e) throws DAOException{
		
		// remove da posição que o usuário selecionou na página
		
		ClassificacaoBibliografica classificacaoRemovida = null;
		
		if(camposNaoReservados != null){
		
			CampoDados c = ( (CampoDados) dataModelCampos.getRowData() );
			
			if ( c.getEtiqueta() != null && c.getEtiqueta().getTag() != null ){
				classificacaoRemovida = ClassificacoesBibliograficasUtil.encontraClassificacaoByCampo(classificacoesUtilizadas, c.getEtiqueta().getTag());
			}
			
			c = null;
			
			camposNaoReservados.get(dataModelCampos.getRowIndex()).setTituloCatalografico(null); // remove o Título do objeto
			camposNaoReservados.remove(dataModelCampos.getRowIndex());  // remove objeto do Título
			
			dataModelCampos = new ListDataModel(camposNaoReservados);
			
		}
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		CatalogacaoUtil.configuraGruposEtiquetaVisiveis(obj); // na remoção precisa reconfigurar os grupos
		
		// SE O CAMPO REMOVIDO ERA DE UMA CLASSIFICAÇÃO TEM QUE ATUALIZAR ESSA INFORMAÇÃO //
		if(classificacaoRemovida != null)		
			ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacaoRemovida);
		
	}
	
	
	
	/**
	 * Remove um campo de controle
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void removerCampoControle(ActionEvent e){
		// remove da posição que o usuário selecionou na página
		int posicao = getParameterInt("posicaoCampoControleRemover");
		
		if(obj.getCamposControle() != null){
		
		// LIDER e 008 são obrigatórios e não podem ser removidos
			if(obj.getCamposControle().get(posicao).getEtiqueta().getTag().equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag())
					|| obj.getCamposControle().get(posicao).getEtiqueta().getTag().equals(Etiqueta.CAMPO_008_BIBLIOGRAFICO.getTag())){
				return;
			}
			
			obj.getCamposControle().remove(posicao);
		}
	}
	
	
	/**
	 * Adiciona um sub Campo vazio ao campo de dados para a hora da catalogação.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 */
	public void adicionarNovoSubCampo(ActionEvent e){
		CampoDados c = (CampoDados) dataModelCampos.getRowData();
		new SubCampo(c); // cria um sub campo vazio e adiciona a lista de subcampos
	}
	
	
	
	
	
	
	/**
	 * Método que remove o subcampo selecionado pelo usuário
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void removeSubCampoDados(ActionEvent e){
		// Remove da coleção de subcampos do campo selecionado o subcampos que o usuário selecionou
		
		if(camposNaoReservados != null){
		
			// Remove o subcampo do campo e o campo do subcampo
			camposNaoReservados.get(dataModelCampos.getRowIndex()).getSubCampos().get(
					((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex() ).setCampoDados(null);
			
			camposNaoReservados.get(dataModelCampos.getRowIndex()).getSubCampos().remove(
					( (CampoDados)  dataModelCampos.getRowData() ).getDataModelSubCampos().getRowIndex() );
		
		}
	}
	
	
	
	
	/**
	 * Método que move o campo MARC selecionado para uma posição acima (trocando de posição com o campo acima dele se existir)
	 * 
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void moverCampoDadosCima(ActionEvent e){
		
		if(camposNaoReservados != null){
			
			int posicaoCampoSelecionado = dataModelCampos.getRowIndex();
			
			if(posicaoCampoSelecionado > 0){ // Verifica se não é o primeiro campo, ser for não dá para subir mais
				
				CampoDados cTempSelecionado = camposNaoReservados.get( posicaoCampoSelecionado);
				
				CampoDados cTempProximo = camposNaoReservados.get(posicaoCampoSelecionado-1);
				
				/* Troca as posições, mesmo que o campo não vá ser movido por causa da ordenação da  *
				 * tag do campo que é prioritária, pode trocar as posições que antes de ser salvo, o *
				 * sistema calcula as posições novamente de acordo com a posição na lista            */
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
	 * Método que move o campo MARC selecionado uma posição abaixo (trocando de posição com o campo abaixo dele se existir)
	 * 
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void moverCampoDadosBaixo(ActionEvent e){
		
		if(camposNaoReservados != null){
			
			int posicaoCampoSelecionado = dataModelCampos.getRowIndex();
			
			int posicaoMaxima = dataModelCampos.getRowCount();
			
			if(posicaoCampoSelecionado < posicaoMaxima-1){ // Verifica se não é o último campo, ser for não pode ir mais para baixo
				
				CampoDados cTempSelecionado = camposNaoReservados.get( posicaoCampoSelecionado);
				
				CampoDados cTempProximo = camposNaoReservados.get(posicaoCampoSelecionado+1);
				
				/* Troca as posições, mesmo que o campo não vá ser movido por causa da ordenação da  *
				 * tag do campo que é prioritária, pode trocar as posições que antes de ser salvo, o *
				 * sistema calcula as posições novamente de acordo com a posição na lista            */
				
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
	 * Método que move o sub campo MARC selecionado uma posição acima (trocando de posição com o sub campo acima dele se existir)
	 * 
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void moverSubCampoCima(ActionEvent e){
		
		
		if(camposNaoReservados != null){
			
			int posicaoSubCampoSelecionado = ( (CampoDados)  dataModelCampos.getRowData() ).getDataModelSubCampos().getRowIndex();
			
			if(posicaoSubCampoSelecionado > 0){ // Verifica se não é o primeiro campo, ser for não dá para subir mais
				
				CampoDados campoSelecionado = (CampoDados)  dataModelCampos.getRowData();
				
				if(campoSelecionado != null){
				
					SubCampo subTempSelecionado = campoSelecionado.getSubCampos().get(posicaoSubCampoSelecionado);
					
					SubCampo subTempSelecionadoProximo = campoSelecionado.getSubCampos().get(posicaoSubCampoSelecionado-1);
					
					// Troca as posições
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
	 * Método que move o sub campo MARC selecionado uma posição abaixo (trocando de posição com o sub campo abaixo dele se existir)
	 * 
	 *   <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * 
	 * @param e
	 */
	public void moverSubCampoBaixo(ActionEvent e){
		
		if(camposNaoReservados != null){
			
			int posicaoSubCampoSelecionado = ( (CampoDados)  dataModelCampos.getRowData() ).getDataModelSubCampos().getRowIndex();
			
			int posicaoMaxima = ( (CampoDados)  dataModelCampos.getRowData() ).getDataModelSubCampos().getRowCount();
			
			if(posicaoSubCampoSelecionado < posicaoMaxima-1){ // Verifica se não é o último campo, ser for não pode ir mais para baixo
				
				CampoDados campoSelecionado = (CampoDados)  dataModelCampos.getRowData();
				
				if(campoSelecionado != null){
				
					SubCampo subTempSelecionado = campoSelecionado.getSubCampos().get(posicaoSubCampoSelecionado);
					
					SubCampo subTempSelecionadoProximo = campoSelecionado.getSubCampos().get(posicaoSubCampoSelecionado+1);
					
					// Troca as posições
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
	 *    Método que busca a etiqueta de acordo com a tag digitada pelo usuário e adiciona essa
	 * tag ao campo dela.
	 * 
	 *    Se a tipo de catalogação for BIBLIOGRAFICA apenas etiqueta bibliográficas devem ser buscas,
	 * se for AUTORIDADES apenas etiquetas de AUTORIDADES e assim até quantos tipo de catalogação
	 * essa tela for suportar.
	 * 
	 *     <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 *   quando o usuário clica digita alguma coisa no campo da etiqueta.
	 * @throws DAOException
	 */
	public void buscaEtiqueta(ActionEvent e) throws DAOException{
		
		CampoDados c = (CampoDados) dataModelCampos.getRowData();
		
		// Verifica se o usuário não digitou o código de um campo de controle, porque aqui é só dados
		if(c.getEtiqueta().getTag().length() == 3 && ! c.getEtiqueta().getTag().startsWith("00")
				&& ! c.getEtiqueta().getTag().equals("LDR")){
			
			Etiqueta etiqueta = null;
			
			etiqueta = new Etiqueta(c.getEtiqueta().getTag(), tipoCatagalocao == TipoCatalogacao.BIBLIOGRAFICA ?  TipoCatalogacao.BIBLIOGRAFICA: TipoCatalogacao.AUTORIDADE);
			
			etiqueta = recuperaEtiquetaTitulo(etiqueta, etiquetasBuscadas); 
			
			
			if(etiqueta != null ){
				
				// NÃO busca as etiqueta reservadas do sistema, senão dar erro na página //
				if( etiqueta.equals(Etiqueta.CODIGO_DA_BIBLIOTECA)
						||  etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES)
						||  etiqueta.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO)
						||  etiqueta.equals(Etiqueta.CODIGO_DA_BIBLIOTECA_AUTORIDADES)
						||  etiqueta.equals(Etiqueta.CODIGO_DAS_BIBLIOTECAS_COOPERANTES_AUTORIDADES)
						||  etiqueta.equals(Etiqueta.INFORMACOES_DE_MOVIMENTO_AUTORIDADES)){
					
					// Não é uma etiqueta de uso reservado porque o tipo (-1) é invalido
					c.setEtiqueta(new Etiqueta(etiqueta.getTag(), (short) -1, "CAMPO DE USO RESERVADO"));
					
				}else{
					
					c.setEtiqueta(etiqueta);
					
					// Atualiza campo classificação, caso o usuário altera uns dos campos de classificação. //
					obj.setCamposDados(atribuiCamposDadosAoTitulo());
					
					// Se o Campo anterior alterado ou o novo campo era um campo utilizado na classicaaco classificação, pricisa atualizar
					if(  ClassificacoesBibliograficasUtil.isCampoUtilizadoClassificacao(new String[]{ etiqueta.getTag() } ) ){
					
						ClassificacaoBibliografica classificacaoAlteracao = ClassificacoesBibliograficasUtil
							.encontraClassificacaoByCampo(classificacoesUtilizadas, new String[]{ etiqueta.getTag() } );
						
						ClassificacoesBibliograficasUtil.configuraClassificacoesEAreasCNPQTitulo(obj, classificacaoAlteracao);
						
					}
				
				}
				
				// se mudar o campo, apaga as referência dos subcampos das autoridades que por acaso estiverem nos subcampos.
				 // se o usuário mudar tem que mudar a descrição transiente
				if(c.getSubCampos() != null)  {
					for (SubCampo s : c.getSubCampos()) {
						s.setSubCampoAutoridade(null);
						s.setDescricaoSubCampo(CatalogacaoUtil.retornaDescritorSubCampo(c.getEtiqueta(), s.getCodigo()));
					}
				}
				
			}else{
				c.setEtiqueta(new Etiqueta(c.getEtiqueta().getTag(), (short) -1, "CAMPO NÃO CADASTRADO"));
			}
			
		}else{
			c.setEtiqueta(new Etiqueta(c.getEtiqueta().getTag(), (short) -1));
		}
	}
	
	
	
	
	/**
	 *    <p>Chamado automaticamente para salva a catalogação na base </p>
	 *    <p>Chamado automaticamente na página de catalogação a cada 2mim com o
	 *    <code> a4j:poll </code> para salvar a catalogação automaticamente
	 *    para o usuário não perder as suas informação caso ocorra algum problema no sistema
	 *     e não deixa a sessão do usuário expirar.</p>
	 *
	 *   <br/><br/>
	 * 		Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
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
						
					}else{   // CRIANDO O TÍTULO
						
						MovimentoCatalogaTitulo mov = null;
						prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
						mov = new MovimentoCatalogaTitulo(obj, arquivo, false, classificacoesUtilizadas);
						mov.setCodMovimento(SigaaListaComando.CATALOGA_TITULO);
						
						execute(mov);
						
						prepareMovimento(SigaaListaComando.CATALOGA_TITULO);
					}
					
					addMensagemInfoAjax("Título salvo automaticamente pelo sistema ");
					
				break;
				case TipoCatalogacao.AUTORIDADE:

					Autoridade autoridade = CatalogacaoUtil.criaAutoridaeAPartirTitulo(obj);
					
					if(editando){
						
						autoridade.setId(obj.getId()); // o id da autoridade estava no título.
						
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
						
						// atualiza o Título para pegar o número do sistema que foi gerado
						//autoridade = getGenericDAO().refresh(autoridade);
						obj = CatalogacaoUtil.criaTituloAPartirAutoridade(autoridade);  // IMPORTANTE
					}
					
					addMensagemInfoAjax("Autoridade salva automaticamente pelo sistema ");
					
				break;
				
			}
		}catch(Exception ex){
			
			switch(tipoCatagalocao){
				case TipoCatalogacao.BIBLIOGRAFICA:
					addMensagemErroAjax("Não foi possível salvar a catalogação. ");
				break;
				case TipoCatalogacao.AUTORIDADE:
					addMensagemErroAjax("Não foi possível salvar a autoridade. ");
				break;
			}
		}
		
		System.out.println("Salvar automaticamente demorou>>>>> "+((System.currentTimeMillis()-tempo)/1000)+" ms.");
		
	}
	
	
	
	
	
	/**
	 *     Chamado quando o usuário mudou de subcampo. Deve-se apagar as referência dos subcampos das autoridades.
	 *
	 *     <br/><br/>
	 *     Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void mudarSubCampo(ActionEvent e) throws DAOException{
		
		CampoDados d = camposNaoReservados.get(dataModelCampos.getRowIndex());
		
		SubCampo subCampoAlterado = d.getSubCampos().get(((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex() );
		
		for (SubCampo s: d.getSubCampos()) {
			s.setSubCampoAutoridade(null);
			
			if( s.equals(subCampoAlterado) ){
				s.setDescricaoSubCampo(CatalogacaoUtil.retornaDescritorSubCampo(d.getEtiqueta(), s.getCodigo())); // se o usuário mudar tem que mudar a descrição transiente
			}
		}
		
	}
	
	
	
	
	/**
	 * Monta a ajuda do campo de controle com base no campo de controle que o usuário clicou.
	 * 
	 * <br/><br/>
	 * Chamado no método que edita os campos de controle <code>#editarCampoControle()</code>
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
	 *   Monta a ajuda do campo de dados com base no campo que o usuário clicou.
	 *
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
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
	 *   <p> Método chamado para confirma o cutter correto da catalogação entre os cutter sugeridos pelo sistema.</p>
	 *   <p> <i> Caso este método receba com parâmetro o id de algum cutter, é porque o usuário selecionou outro diferente,
	 *   caso não recebe nenhum parâmetro é porque o usuário confirmou a escolha do sistema. </i> </p>
	 * 
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			
		}else{  // usuário confirmou o sugerido pelo sistema
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
	 *     <p>Método chamado quando o usuário clica no link de buscar autoridades.</p>
	 *     <p>O método que chama o MBean de busca de autoridades para o usuário buscar a autoridade
	 *  que deve preencher os dados do campo selecionado.</p>
	 * 
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @return
	 * @throws DAOException
	 */
	public String buscarAutoridadesPorNomeAutorizadoAutor() throws DAOException{
		
		CampoDados c = camposNaoReservados.get(dataModelCampos.getRowIndex());
		
		campoSelecionadoParaDadosAutoridade = c;
		
		//SubCampo sub = c.getSubCampos().get( c.getDataModelSubCampos().getRowIndex());
		/*
		 * No caso de assunto o sistema deve buscar pelos dados que estão não apenas no sub campo "a"
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
	 *     Método chamado quando o usuário clica no link de buscar autoridades.
	 *     O método chama o MBean de busca de autoridades para o usuário buscar a autoridade de assunto
	 * cujas informações vão preencher o campo selecionado do Título
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @return
	 * @throws DAOException
	 */
	public String buscarAutoridadesPorNomeAutorizadoAssunto() throws DAOException{
		
		CampoDados c = camposNaoReservados.get(dataModelCampos.getRowIndex());
		
		campoSelecionadoParaDadosAutoridade = c;
		
		/*
		 * No caso de assunto o sistema deve buscar pelos dados que estão não apenas no sub campo "a"
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
	 * uma referência a essa autoridade.
	 * 
	 *     <br/><br/>
	 *     Chamado a partir do MBean: {@link CatalogaAutoridadesMBean#selecionarAutoridade}
	 *     <br/>
	 *     Método não invocado por nenhuma JSP.
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
						
						/* Guarda as possições já adicionadas do sub campo, para não adicionar sempre no mesmo sub campo. */
						Map<Character, Integer> posicoesJaRetornadas = new HashMap<Character, Integer>();
						
						for (SubCampo subCampoAutoridade : campo.getSubCampos()) {
							
							Integer ultimaPosicaoUsada = posicoesJaRetornadas.get(subCampoAutoridade.getCodigo());
							
							if(ultimaPosicaoUsada == null)  ultimaPosicaoUsada = -1;
							
							// como um subcampo pode ser repetido, pega o primeiro subcampo do campo ainda não retornado para  //
							// jogar o valor do subcampo da autoridade                                     //
							int posicaoSubCampoAindaNaoUsado = campoSelecionadoParaDadosAutoridade
									 .getMenorPosicaoSubCampo(subCampoAutoridade.getCodigo(), ultimaPosicaoUsada);
							
							if(posicaoSubCampoAindaNaoUsado >= 0){ // já existe subcampo, apenas atualiza dos dados
								
								SubCampo sub = campoSelecionadoParaDadosAutoridade.getSubCampos().get(posicaoSubCampoAindaNaoUsado);
								sub.setDado(subCampoAutoridade.getDado());
								sub.setSubCampoAutoridade(subCampoAutoridade);
							
								posicoesJaRetornadas.put(subCampoAutoridade.getCodigo(), posicaoSubCampoAindaNaoUsado); // Já usou até essa posição
								
							}else{ // ainda não existe o subcampo no título, cria um novo subcampo para o campo de dados com os dados da autoridade
								
								new SubCampo(subCampoAutoridade.getCodigo(), subCampoAutoridade.getDado(),
										campoSelecionadoParaDadosAutoridade, subCampoAutoridade, -1);
								
								posicoesJaRetornadas.put(subCampoAutoridade.getCodigo(), campoSelecionadoParaDadosAutoridade.getSubCampos().size()); // Já usou até essa posição
								
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
	 * uma referência a essa autoridade.
	 * 
	 *      <br/><br/>
	 *      Chamado a partir do MBean: {@link CatalogaAutoridadesMBean#selecionarAutoridade}
	 *      <br/>
	 *      Método não invocado por nenhuma JSP.
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
						
						/* Guarda as possições já adicionadas do sub campo, para não adicionar sempre no mesmo sub campo. */
						Map<Character, Integer> posicoesJaRetornadas = new HashMap<Character, Integer>();
						
						for (SubCampo subCampoAutoridade : campoAutoridade.getSubCampos()) {
											
							Integer ultimaPosicaoUsada = posicoesJaRetornadas.get(subCampoAutoridade.getCodigo());
							
							if(ultimaPosicaoUsada == null)  ultimaPosicaoUsada = -1;
							
							// como um subcampo pode ser repetido, pega o primeiro subcampo do campo ainda não retornado para  //
							// jogar o valor do subcampo da autoridade                                     //
							int posicaoSubCampoAindaNaoUsado = campoSelecionadoParaDadosAutoridade
									.getMenorPosicaoSubCampo(subCampoAutoridade.getCodigo(), ultimaPosicaoUsada);
							
							if(posicaoSubCampoAindaNaoUsado >= 0 ){ // já existe subcampo, apenas atualiza dos dados dele com os dados da autoridade
								
								SubCampo sub = campoSelecionadoParaDadosAutoridade.getSubCampos().get(posicaoSubCampoAindaNaoUsado);
								sub.setDado(subCampoAutoridade.getDado());
								sub.setSubCampoAutoridade(subCampoAutoridade);
								
								posicoesJaRetornadas.put(subCampoAutoridade.getCodigo(), posicaoSubCampoAindaNaoUsado); // Já usou até essa posição
								
							}else{ // ainda não existe o subcampo no título, cria um novo subcampo para o campo de dados com os dados da autoridade
								
								new SubCampo(subCampoAutoridade.getCodigo(), subCampoAutoridade.getDado(),
										campoSelecionadoParaDadosAutoridade, subCampoAutoridade, -1);
								
								posicoesJaRetornadas.put(subCampoAutoridade.getCodigo(), campoSelecionadoParaDadosAutoridade.getSubCampos().size()); // Já usou até essa posição
								
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
	 *      Quando o usuário escolhe a planilha de catalogação, o sistema chama esse método para
	 * carregar os dados da planilha escolhida no objeto  <code>tituloTemp</code> e mostrar ao
	 * usuário, ainda na página que escolhe a planilha.
	 * 
	 *    <p> Obs.:  Só depois que o usuário confirmar a planilha escolhida é que a catalogação
	 * vai realmente começar pelo método, <code>CatalogacaoMBean#iniciarPlanilha()</code></p>
	 * 
	 *    <br/><br/>
	 * 	  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaCatalogacao.jsp
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
			
				// pego o formato do Título que vem da planilha //
				FormatoMaterial formatoMaterial =  getGenericDAO().findByPrimaryKey(
					planilha.getIdFormato(), FormatoMaterial.class);
				
				tituloTemp = new TituloCatalografico(formatoMaterial);
			}
			
			
			if(isTipoCatalogacaoAutoridade()){
				tituloTemp = new TituloCatalografico();
			}
			
			// Autoridades não tem formato material
			
		
				
			CatalogacaoUtil.criaCamposDeControle(planilha, tituloTemp, etiquetasBuscadas);
			CatalogacaoUtil.criaCamposDeDados(planilha, tituloTemp, etiquetasBuscadas);
			
			if(tituloTemp.getCamposControle() != null)
				Collections.sort(tituloTemp.getCamposControle(), new CampoControleByEtiquetaComparator());
			
			if(tituloTemp.getCamposDados() != null)
				Collections.sort(tituloTemp.getCamposDados(), new CampoDadosByEtiquetaPosicaoComparator());
				
		}
	}
	
	
	
	/**
	 * <p>Método chamado para carregar os campos da planilha utilizada na catalogação e o usuário pode adicionar algum desses campos.</p>
	 * <p>O usuário só pode adicionar campos presentes na planilha utilizada.</p>
	 * @void
	 */
	public void carregaCamposPlanilhaSimplificada(ActionEvent evt) throws DAOException{
		tituloTemp = CatalogacaoUtil.carregaCamposPlanilhaCatalogacaoSimplificada(idPlanilhaCatalogacaoSimplificada, isTipoCatalogacaoBibliografica(), etiquetasBuscadas);
		
		for (CampoDados campo : tituloTemp.getCamposDadosNaoReservados()) {
			campo.getEtiqueta().setGrupo(CatalogacaoUtil.retornaGrupoEtiqueta(campo.getEtiqueta()));
		}
		
		CatalogacaoUtil.configuraGruposEtiquetaVisiveis(tituloTemp);
		
	}
	
	
	/* *********************** métodos de redirecionamento para as paginas ******************* */
	
	/**
	 *  Chamado a partir da página: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 *                              /biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp
	 *                              /biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp
	 */
	public String telaBuscaInformacoesSipacAPartirNumeroPatrimonio(){
		return forward(PAGINA_BUSCA_SIPAC_A_PARTIR_NUMERO_PATRIMONIO);
	}
	
	
	/**
	 * Método invocado de nenhuma página JSP.
	 */
	public String telaEscolheFormatoMaterial(){
		return forward(PAGINA_ESCOLHE_FORMATO_MATERIAL);
	}
	
	/**
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaTituloCatalografico.jsp
	 */
	public String telaEscolhePlanilhaBibliografica(){
		return forward(PAGINA_ESCOLHE_PLANILHA_BIBLIOGRAFICA);
	}
	
	/**
	 *  Chamado a partir da página:  sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaAutoridade.jsp
	 */
	public String telaEscolhePlanilhaAutoridades(){
		return forward(PAGINA_ESCOLHE_PLANILHA_AUTORIDADES);
	}
	
	
	/**
	 *  Método invocado de nenhuma página JSP.
	 */
	public String telaDadosTituloCatalografico(){
		return forward(PAGINA_PREENCHE_DADOS_TITULO_CATALOGRAFICO);
	}

	
	
	/**
	 * 	  <p>Redireciona para a tela na qual o usuário vai escolher entre os possíveis códigos da tabela cutter a que ele
	 * acha que mais se a próxima do sobre nome do autor. </p>
	 * 
	 *    <p>Antes de redirecionar o usuário para a página, calculo as sugestões de código cutter.</p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException
	 */
	public String telaBuscaTabelaCutter() throws DAOException{
		
		obj.setCamposDados(atribuiCamposDadosAoTitulo());
		
		suguestoesTabelaCuter  = CatalogacaoUtil.gerarCodigoCutter(obj);
		
		if(suguestoesTabelaCuter == null || suguestoesTabelaCuter.size() == 0){
			addMensagemWarning("O Código Cutter não pôde ser gerado.");
			return telaDadosTituloCatalografico(); // fica na mesma página de edição dos campos MARC
		}else{
		
			zeraIndicesCamposSelecionados();
			
			// Guarda os indices do campo selecionado pelo usuário para saber onde colocar o código cutter selecionado //
			indiceCampoSelecionado = dataModelCampos.getRowIndex();
			indiceSubCampoSelecionado =  ((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex();
			
			return forward(PAGINA_BUSCA_TABELA_CUTTER);
			
		}
	}
	
	/**
	 * 	  <p>Guarda o sub campo 090$d selecionado </p>
	 * 
	 *    <p>Antes de abrir o model panel para o usuário escolher os dados que vão preencher o sub campo 090$d.</p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @throws DAOException
	 */
	public void selecionaCampo090D(ActionEvent e){
		
		zeraIndicesCamposSelecionados();
		
		// Guarda os indices do campo selecionado pelo usuário para saber onde colocar o código cutter selecionado //
		indiceCampoSelecionado = dataModelCampos.getRowIndex();
		indiceSubCampoSelecionado =  ((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex();
	}
	
	
	
	/**
	 * 	  <p>Retorna os dados que o usuário pode utilizar pra preencher o sub campo 090$d </p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/modalPanelFormCatalogacao.jsp
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
	 *  <p>Atribuir o valor selecionado pelo usuário ao sub campo 090$d</p>
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/modalPanelFormCatalogacao.jsp
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
	 * 	  Redireciona para a tela na qual o usuário pode buscar as editoras cadastradas no SIPAC.
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 */
	public String telaBuscaEditorasCadastradasSipac(){
		
		zeraIndicesCamposSelecionados();
		
		// Guarda os indices do campo selecionado pelo usuário para saber onde colocar a editora selecionada //
		indiceCampoSelecionado = dataModelCampos.getRowIndex();
		indiceSubCampoSelecionado =  ((CampoDados)  dataModelCampos.getRowData()).getDataModelSubCampos().getRowIndex();
		
		return forward(PAGINA_BUSCA_EDITORAS_CADASTRADAS_SIPAC );
	}
	
	
	
	
	/**
	 *  Método que atribui novamente ao título os campos de dados que estão sendo integrados na página por duas coleções diferentes
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
	 * Separa a lista de campos de dados do Título ou autoridade nas listas que serão usasdas na tela. 
	 * Antes de salvar tem que atribuir esses campos ao Título ou Autoridade novamente. @link{this#atribuiCamposDadosAoTitulo}
	 * 
	 * Somente os campos de dados não reservados são alteráveis pelo usuário.
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
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
	 */
	public String telaCampoControleLider(){
		return forward(PAGINA_CAMPO_LIDER);
	}
	
	
	/**
	 *  Método invocado de nenhuma página JSP.
	 */
	public String telaCampoControle001(){
		return forward(PAGINA_CAMPO_001);
	}
	
	/**
	 * Método invocado de nenhuma página JSP.
	 */
	public String telaCampoControle003(){
		return forward(PAGINA_CAMPO_003);
	}
	
	/**
	 * Método invocado de nenhuma página JSP.
	 */
	public String telaCampoControle005(){
		return forward(PAGINA_CAMPO_005);
	}
	
	/**
	 * Método invocado de nenhuma página JSP.
	 */
	public String telaCampoControle006(){
		return forward(PAGINA_CAMPO_006);
	}
	
	/**
	 * Método invocado de nenhuma página JSP.
	 */
	public String telaCampoControle007(){
		return forward(PAGINA_CAMPO_007);
	}
	
	/**
	 * Método invocado de nenhuma página JSP.
	 */
	public String telaCampoControle008(){
		return forward(PAGINA_CAMPO_008);
	}
	
	/* ************************************************************************************* */
	

	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// parte do arquivo em formato digital ///////////////////////////////
	
	/**
	 * 
	 *   Valida o arquivo que o usuário informou.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp
	 * @param vce
	 */
	public void validaFormatoArquivo(ValueChangeEvent vce){
		
		if(getParameterBoolean("apagarArquivoSumetido")){

			// se o usuário clicou no botão remover arquivo não faz nada nesse método.

		}else{
		
			UploadedFile arquivoNovo = (UploadedFile) vce.getNewValue();
			
			if(arquivoNovo != null){               // entra aqui quando o usuário submete um novo arquivo
				arquivo = arquivoNovo;
			}else{                        // a passa aqui quando o usuário submeto todo o formulário do título
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
			throw new ArqException(" Não foi possível recuperar o nome do arquivo ");
		}
		
	}
	
	
	/**
	 * Obtém o nome do arquivo que o usuário submeteu para mostrar na página para ele
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
	 * @throws ArqException
	 */
	public String getNomeArquivoTemporarioDoTitulo() throws ArqException{
		
		String nomeTemp = null;
		
		if(arquivo != null){                 // usuário submeteu novo arquivo
			nomeTemp = arquivo.getName();
		}else{
			
			if(! apagarArquivoDigitalSalvo){  // se o usuário não clicou em apagar o arquivo salvo na base
				
				nomeTemp = getNomeArquivoDigitalDoTitulo(); // pega o que estiver salvo se tiver
			}
		}
		
		if(nomeTemp != null && nomeTemp.length() > 30 )
			nomeTemp = nomeTemp.substring(0, 15) +" ... "+ nomeTemp.substring(nomeTemp.length()-15, nomeTemp.length());
		
		return nomeTemp;
	}
	
	
	/**
	 *    Chamado para apagar o arquivo submetido quando o usuário clica no respectivo botão.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
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
	 * Retorna as planilhas cadastradas para o usuário escolher qual será usada para montar a tela da catalogação simplificada.
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método que busca todos as planilhas cadastradas e monta uma coleção de selecItem para mostrar
	 * para o usuário escolher.
	 * 
	 *  <br/><br/>
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaBibliografica.jsp
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
	 *     Método que busca todas as planilhas cadastradas e monta uma coleção
	 * de selecItem para mostrar para o usuário escolher.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolhePlanilhaAutoridades.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem>  getAllPlanilhaAutoridadesComboBox() throws DAOException{
		return  toSelectItems(getGenericDAO().findByExactField(PlanilhaCatalogacao.class,
				"tipo", TipoCatalogacao.AUTORIDADE), "id", "nome") ;
	}

	
	
	/***
	 * <p>Inicia a classificação simplificada.</p>
	 * 
	 * <p>Sempre ao iniciar a classificação simplificada, o usuário deve ter escolhido uma planilha de catalogação. 
	 * A partir dessa planilha, os campos</p> 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/telaCatalogacaoSimplificada.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public void alterouPlanilhaCatalogacao(ActionEvent event) throws DAOException{
		
		if(idPlanilhaCatalogacaoSimplificada <= 0 ){
			addMensagemErroAjax("Escolha uma Planilha de Catalogação");
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
	 *   Método que trás todos os formatos de materiais de um Título
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/escolheFormatoMaterial.jsp
	 * @return
	 * @throws DAOException
	 */
	public List<FormatoMaterial> getAllFormatoMaterialComboBox() throws DAOException{
		return  (List<FormatoMaterial>) getGenericDAO().findAll(FormatoMaterial.class);
	}
	
	
	
	/**
	 * 
	 * Retorna as grandes áreas CNPq para serem exibidas em combox na página de catalogação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getGrandesAreasCNPqComboBox() throws DAOException{
		List<AreaConhecimentoCnpq> listaTemp = new ArrayList<AreaConhecimentoCnpq>();
		
		listaTemp.add( new AreaConhecimentoCnpq(-1, " -- Selecione -- ")); // Não é salva no banco.
		for (AreaConhecimentoCnpq areaConhecimentoCnpq : getGrandesAreasCNPq()) {
			AreaConhecimentoCnpq area = new AreaConhecimentoCnpq(areaConhecimentoCnpq.getId(), areaConhecimentoCnpq.getNome());
			area.setCodigo(areaConhecimentoCnpq.getCodigo()); // Tem que colocar o código senão não diferencia as áreas CNPq. 
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
	 * Retorna as grandes áreas CNPq para associar com o Título que está sendo catalogado
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
	 * Pega todas as categorias de materiais ativas no banco para adição ou edição do campo 007
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
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
	 *        Método que Busca as informações de alteração de uma  Autoridade para mostra ao
	 * usuário na hora da catalogação. Serve para ter uma idéia de quem mexeu na entidade.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracaoCatalogacao.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public List <Object []> getHistoricoAlteracaoTitulo()throws DAOException{
		
		List <Object []> listaHistoricoAlteracoes = new ArrayList<Object[]>();
		
		int idEntidade = getParameterInt("idEntidadeMarcVisualizarHistorico", 0);
		int limiteResultados = 30;
		
		if (idEntidade != 0){
			// retorna as 30 última alterações 
			listaHistoricoAlteracoes = getDAO(TituloCatalograficoDao.class).findAlteracoesByTituloPeriodo(idEntidade, null, null, limiteResultados);
		}
		
		for (Object[] object : listaHistoricoAlteracoes)
			object[1] = object[1].toString().replaceAll("\n", "<br/>");
		
		return listaHistoricoAlteracoes;
	}
	
	
	/**
	 *        Método que Busca as informações de alteração de um Título para mostra ao
	 * usuário na hora da catalogação. Serve para ter uma idéia de quem mexeu na entidade.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracaoCatalogacao.jsp
	 *
	 * @return
	 * @throws DAOException
	 */
	public List <Object []> getHistoricoAlteracaoAutoridade()throws DAOException{
		
		List <Object []> listaHistoricoAlteracoes = new ArrayList<Object[]>();
		
		int idEntidade = getParameterInt("idEntidadeMarcVisualizarHistorico", 0);
		int limiteResultados = 30;
		
		if (idEntidade != 0){
			// retorna as 10 última alterações 
			listaHistoricoAlteracoes = getDAO(AutoridadeDao.class).findAlteracoesByAutoridadePeriodo(idEntidade, null, null, limiteResultados);
		}
		
		for (Object[] object : listaHistoricoAlteracoes)
			object[1] = object[1].toString().replaceAll("\n", "<br/>");
		
		return listaHistoricoAlteracoes;
	}
	
	
	
	
	
	
	
	//////////// Métodos que contém a lógica de montar as telas dos campos de controle  ///////////
	
	
	
	/**
	 *    Os valores dos campos de controle vão ser atribuídos nas coleções  "valoresPar" e "valoresImpar".
	 * nas páginas de edição de campos de controle.
	 * 
	 *    Esse método junta essas valores em uma única coleção novamente.
	 */
	private void montaValoresDigitadosPeloUsuario(){
		
		if(etiquetaControleAtual.equals(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO)
				|| etiquetaControleAtual.equals(Etiqueta.CAMPO_LIDER_AUTORIDADE)){
		
			// Da página ele vai jogar em "valoresPar" e "valoresImpar"
			// Então preciso fazer o inverso e copiar para "valores"
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
			
			// Da página ele vai jogar em "valoresPar" e "valoresImpar"
			// Então preciso fazer o inverso e copiar para "valores"
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
			
			// Da página ele vai jogar em "valoresPar" e "valoresImpar"
			// Então preciso fazer o inverso e copiar para "valores"
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
			
			// Da página ele vai jogar em "valoresPar" e "valoresImpar"
			// Então preciso fazer o inverso e copiar para "valores"
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
	 *   Obtém do banco os valores padrão do campo de controle LIDER. Esses valores padrão servem para montar
	 *   a página de edição do campo.
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
	 *   Obtém do banco os valores padrão do campo de controle 006. Esses valores padrão servem para montar
	 *   a página de edição do campo.
	 */
	private List<ValorPadraoCampoControle> getValoresPadrao006(Etiqueta e) throws DAOException {
		
		if(e.equals(Etiqueta.CAMPO_006_BIBLIOGRAFICO)){
			valoresPadrao006 = CatalogacaoUtil.getValoresPadrao006008Bibliografico(obj.getFormatoMaterial(), e);
		}
		
		return valoresPadrao006;
	}
	
	/**
	 * Obtém do banco os valores padrão do campo de controle 007. Esses valores padrão servem para montar
	 *   a página de edição do campo.
	 */
	private List<ValorPadraoCampoControle> getValoresPadrao007(Etiqueta e) throws DAOException {
		
		if(e.equals(Etiqueta.CAMPO_007_BIBLIOGRAFICO)){
			valoresPadrao007 = CatalogacaoUtil.getValoresPadrao007Bibliografico(codigoCategoriaMaterial);
		}
		
		return valoresPadrao007;
	}
	
	/**
	 * Obtém do banco os valores padrão do campo de controle 008. Esses valores padrão servem para montar
	 *   a página de edição do campo.
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
	 * Guarda os valores que o usuário digitar nas página de alteração dos campos de controle
	 */
	private List<ValorPadraoCampoControle> getValoresLider(Etiqueta e) throws DAOException {
		
		if(e == null){
			valoresLider = new ArrayList<ValorPadraoCampoControle>();
			return  valoresLider;
		}
		
		if( ! editandoCamposDeControle){ // vai adicionar um NOVO CAMPO
			
			// os valores que aparecem para o usuário são os valores padrão vindo do banco
			
			
			if(! errosValidacaoTeladeControle && ! voltandoPaginaLider ){
				
				valoresLider = CatalogacaoUtil.cloneValorPadrao(getValoresPadraoLider(e));
				return valoresLider;
				
			}
			
			// senão fica com os valores que já estavam na tela.
			
		}else{   /// clicou em cima do campo e vai EDITAR
			
			
			if(! errosValidacaoTeladeControle  ){
				
				CampoControle c = obj.getCamposControle().get(posicaoCampoControleSelecionado);
			
				// em submeter pega o valores digitados que vão estar em valorespadrao e monta os dados do campo
				// aqui pega os dados do campo e monta o valor padrão com esses dados para mostrar no banco
				
				if(! errosValidacaoTeladeControle  ){
						valoresLider = CatalogacaoUtil.montaValoresCampo(c.getDado(), getValoresPadraoLider(e));
					
				}
			}
			
			// senão fica com os valores que já estavam na tela.
		}
			
		return valoresLider;
	}
	
	/**
	 * Guarda os valores que o usuário digitar nas página de alteração dos campos de controle
	 */
	private List<ValorPadraoCampoControle> getValores006(Etiqueta e) throws DAOException {

		if(e == null){
			valores006 = new ArrayList<ValorPadraoCampoControle>();
			return  valores006;
		}
		
		if( ! editandoCamposDeControle){ // vai adicionar um NOVO CAMPO
			
			// os valores que aparecem para o usuário são os valores padrão vindo do banco
			
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
	 * Guarda os valores que o usuário digitar nas página de alteração dos campos de controle
	 */
	private List<ValorPadraoCampoControle> getValores007(Etiqueta e) throws DAOException {
		
		if(e == null){
			valores007 = new ArrayList<ValorPadraoCampoControle>();
			return  valores007;
		}
		
		if( ! editandoCamposDeControle){ // vai adicionar um NOVO CAMPO
			
			// os valores que aparecem para o usuário são os valores padrão vindo do banco
			
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
	 * Guarda os valores que o usuário digitar nas página de alteração dos campos de controle
	 */
	private List<ValorPadraoCampoControle> getValores008(Etiqueta e) throws DAOException {
		
		if(e == null){
			valores008 = new ArrayList<ValorPadraoCampoControle>();
			return  valores008;
		}
		
		if( ! editandoCamposDeControle){ // vai adicionar um NOVO CAMPO
			
			// os valores que aparecem para o usuário são os valores padrão vindo do banco
			
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
	 *   Dividi a lista de valores pares do campo LÍDER apenas para mostrar na página em duas colunas
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampoLider.jsp
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
	 *  Divide a lista de valores Ímpares do campo LÍDER apenas para mostrar na página em duas colunas
	 *  <br/><br/>
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampoLider.jsp
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
	 *   Divide a lista de valores pares do campo 006 apenas para mostrar na página em duas colunas
	 *
	 *   <br/><br/>
	 *   Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo006.jsp
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
	 *  Divide a lista de valores Ímpares do campo 006 apenas para mostrar na página em duas colunas
	 *
	 *  <br/><br/>
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo006.jsp
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
	 *   Divide a lista de valores pares do campo 007 apenas para mostrar na página em duas colunas
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo007.jsp
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
	 *  Divide a lista de valores ímpares do campo 007 apenas para mostrar na página em duas colunas
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo007.jsp
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
	 *   Divide a lista de valores pares do campo 008 apenas para mostrar na página em duas colunas
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
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
	 *  Divide a lista de valores ímpares do campo 008 apenas para mostrar na página em duas colunas
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosCampo008.jsp
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
	 *  Método para ser usado no campo 260$b, para realizar um autocomplete das editoras, para o
	 * usuário não ter que digitá-las.
	 *
	 *  <br/><br/>
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadostituloCatalografico.jsp
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
			
			// Não deve dar erro na catalogação se por acaso a busca não funcionar
			// A busca de editora no SIPAC é apenas uma sugestão para facilitar a catalogação
			return new ArrayList<Editora>();
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	

	/**
	 *  Adiciona ao subcampo 256$b selecionado a descrição da editora selecionada.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Redireciona o usuário para a página na qual ele pode escolher o novo formado do material do Título.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *  Altera o formato da catalogação para o novo formato escolhido.<br/>
	 *  Esta troca só pode ocorrer se não influenciar nos materiais que o título possui, isto é,
	 *  caso o título possua fascículos catalogados, o único formato que o usuário pode escolher é
	 *  "PERIÓDICO". Caso o título possua exemplares, o usuário não pode escolher o formato de "PERIÓDICO".
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		// Trocou para periódicos, precisa verificar
		if( novoFormato.isFormatoPeriodico()){
			
			novoFormato = daoExemplar.refresh(novoFormato) ;
			
			Integer qtd = daoExemplar.countExemplaresAtivosNoAcervoByDoTitulo(obj.getId());
			
			if(new Integer(0).compareTo(qtd) == 0){
				obj.setFormatoMaterial(novoFormato );
				return telaDadosTituloCatalografico();
			}else{
				addMensagemErro("O Título possui exemplares no acervo, por isso o seu formato não pode " +
						"ser alterado para "+novoFormato.getDescricaoCompleta());
				getCurrentRequest().setAttribute("alterandoFormatoMaterialTitulo", true);
				return telaEscolheFormatoMaterial();
			}
			
		}else{ // Não é o formato de periódico
			
			novoFormato = daoFasciculo.refresh(novoFormato) ;
			
			Long qtd = daoFasciculo.countFasciculosAtivosNoAcervoDoTitulo(obj.getId());
			
			if(new Long(0).compareTo(qtd) == 0){
				obj.setFormatoMaterial(novoFormato  );
				return telaDadosTituloCatalografico();
			}else{
				addMensagemErro("O Título possui fascículos no acervo, por isso o seu formato não pode " +
						"ser alterado para "+novoFormato.getDescricaoCompleta());
				getCurrentRequest().setAttribute("alterandoFormatoMaterialTitulo", true);
				return telaEscolheFormatoMaterial();
			}
		}
	}
	
	
	/**
	 * Retorna a texto a ser exibido no caption da página.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/formDadosTituloCatalografico.jsp</li>
	 *  </ul>
	 *
	 * @String
	 */
	public String getCaptionFormularioCatalogacao(){
		if(isTipoCatalogacaoBibliografica()){
			return " "+ ( obj.getNumeroDoSistema() > 0 ? obj.getNumeroDoSistema()+" - ": " " )+"  Título Bibliográfico | Formato do Material  ";
		}else{
			return " "+ ( obj.getNumeroDoSistema() > 0 ? obj.getNumeroDoSistema()+" - ": " " )+"  Autoridade ";
		}
	}
	
	
	/** Atualiza a preferência do usuário em exibir ou não o painel lateral na catalogação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painelLateralFormCatalogacao.jsp</li>
	 *  </ul>
	 */
	public void atualizaExibirPainelLateral(ActionEvent evt){
		exibirPainelLateral = ! exibirPainelLateral;
	}
	
	
	/** Atualiza a preferência do usuário em usar a catalogação convencional ou a simplificada
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *  <ul>
	 *       <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painelConfiguracoesFormCatalogacao.jsp</li>
	 *  </ul>
	 */
	public void alterarEntreTelasCatalogacao(ActionEvent evt){
		
		usarTelaCatalogacaoCompleta = ! usarTelaCatalogacaoCompleta;
		
		if(! usarTelaCatalogacaoCompleta){ // se está passando para catalogação simplificada, tem que ordenar o que pode ter sido adicionado na outra tela.
			
			obj.setCamposDados(atribuiCamposDadosAoTitulo());
			CatalogacaoUtil.configuraGruposEtiquetaVisiveis(obj);
			atribuiCamposDoTituloAosCampoUsadosNaTela();
			
		}
	
	}
	
	
	/** Atualiza as preferências do usuário em relação à tela de catalogação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemInfoAjax("Configurações salvas com sucesso ! ");
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagensAjax(ne.getListaMensagens());
		}
		
	}
	
	
	
	
	/** 
     * Busca e carrega a arvore de materiais do Titulo
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna os campos de controle do Título ordenados pela etiqueta.
	 * @return
	 */
	public List<CampoControle> getCamposControleOrdenados(){
		return (List<CampoControle>) obj.getCamposControleOrdenadosByEtiqueta();
	}

	/**
	 * Indica se o tipo da catalogação corrente é catalogação de título bibliográfico.
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
	 * Indica se o tipo da catalogação corrente é catalogação de autoridade.
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
	 * Indica se a catalogação corrente é com tombamento.
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
