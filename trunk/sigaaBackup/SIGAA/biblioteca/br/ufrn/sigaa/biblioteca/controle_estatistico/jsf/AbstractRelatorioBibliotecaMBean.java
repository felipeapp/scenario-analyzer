/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 11/11/2009
 */

package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dao.PessoaDAO;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.RelatoriosBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TipoEmprestimoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.ConstantesRelatorioBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.OrdenacaoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AreaConhecimentoCNPqBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * <p>Classe que serve de base para os relat�rios da biblioteca. Essa classe cont�m todos os filtros
 * a serem utilizados pelos relat�rios.</p> 
 * 
 * <p> <strong>TODOS os relat�rios da biblioteca devem extender dessa classe. Ao realizar essa 
 * heran�a deve deve implementar os m�todos <code>configurar()</code> e  <code>gerarRelatorio()</code></strong>.
 * </p>
 * 
 * <p>Tamb�m deve implementar os m�todos <code>getAgrupamentos1ComboBox()</code> e <code>getAgrupamentos2ComboBox()</code>, caso 
 * se v� a utilizar os filtro de agrupamento, indicar nesses m�todos quais agrupamentos deseja utilizar e adicionar o sql do agrupamento
 * no enum <code>AgrupamentoRelatoriosBiblioteca</code>. </p>
 * 
 * <p>No m�todo <code>configurar()</code> configura-se os filtros a serem usados, al�m do T�tulo e Descri��o do relat�rio.
 * J� no m�todo <code>gerarRelatorio()</code> deve-se implementar a consulta e redirecionar o usu�rio para o formul�rio espec�fico do relat�rio.
 * </p>
 * 
 * <p><strong>OBSERVA��O: Acessar os dados na p�gina de resultado com o alias "_abstractRelatorioBiblioteca" </strong> </p>
 * 
 * <p>Exemplo de como utilizar essa classe:  {@link RelatorioEmprestimosPorCursoMBean}, {@link RelatorioTotalTitulosMateriaisMBean} </p>
 * 
 * @author Br�ulio
 * @author Arlindo Rodrigues
 * @author Jadson
 * @author Felipe Rivas
 * @version 2.0 16/06/2011 Jadson - Acescentando alguns filtro e retirando a necessidade de se usar um outro MBean para acessar os dados do relat�rio.
 */
public abstract class AbstractRelatorioBibliotecaMBean extends SigaaAbstractController<Object> {
	
	
	//////// Dados b�sicos ////////
	
	/** O t�tulo do relat�rio que ir� aparecer em v�rios lugares. */
	protected String titulo;
	
	/** A descri��o referente ao que o relat�rio mostra. � opcional: deixar null se n�o tiver. */
	protected String descricao;
	
	/** Form JSP que mostra os filtros */
	private static final String PAGINA_PADRAO_DE_FILTROS = "/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp";
	
	
	/** Indica a o m�ximo de registros por p�gina nos relat�rios da biblioteca que usam pagina��o. O padr�o � 1000. */
	protected Integer quantidadeRegistrosPorPagina = 1000;
	
	/** Guarda os dados utilizados nos combobox para n�o ficar buscando sempre no banco */
	private Collection<Biblioteca> bibliotecasComboBox;
	/** Guarda os dados utilizados nos combobox para n�o ficar buscando sempre no banco */
	private Collection<SituacaoMaterialInformacional> situacaoesComboBox;
	/** Guarda os dados utilizados nos combobox para n�o ficar buscando sempre no banco */
	private Collection<TipoEmprestimo> tiposEmprestimosComboBox;
	
	
	// Constantes usadas no filtro de classifica��es bibliogr�ficas:

	/** Mant�m a cole��o de classificacoes em memor�ria para o relat�rio */
	protected Collection<SelectItem> classificacoesBibliograricas;
	
	/** Indica que ser� gerado um relat�rio sint�tico. */
	public static final int SINTETICO = 1;
	/** Indica que ser� gerado um relat�rio anal�tico. */
	public static final int ANALITICO = 2;
	
	// modalidade de aquisi��o diz qual fonte que forneceu os materiais que est�o no acervo //
	
	/** Recupera todos os materiais, n�o importa o motivo que ele foi inclu�do no acervo */
	public static final int MODALIDADE_AQUISICAO_TODAS = -1;
	/** N�o se sabe de onde veio o material. */
	public static final int MODALIDADE_AQUISICAO_INDEFINIDA =  -2;
	/**  O material foi inclu�do a partir de uma compra */
	public static final int MODALIDADE_AQUISICAO_COMPRA =  1;
	/** O material foi inclu�do a partir de uma doa��o */
	public static final int MODALIDADE_AQUISICAO_DOACAO = 2;
	/** O material foi inclu�do a partir da subistitui��o de outro que j� existia no acervo */
	public static final int MODALIDADE_AQUISICAO_SUBSITITUICAO = 3;
	
	// Categorias dos materiais a serem visualizados
	
	/** Categoria que engloba exemplares e fasc�culos. */
	public static final int CTGMAT_TODOS      = 0;
	/** Categoria para mostrar somente exemplares. */
	public static final int CTGMAT_EXEMPLARES = 1;
	/** Categoria para mostrar somente fasc�culos. */
	public static final int CTGMAT_FASCICULOS = 2;
	/** Categoria para mostrar somente materiais digitais. */
	public static final int CTGMAT_DIGITAIS = 3;
	
	// Tipos de tombamento de um material:
	
	/** Material tombado de qualquer tipo. */
	public static final int TOMBAMENTO_TODOS = -1;
	/** Material tombado por compra. */
	public static final int TOMBAMENTO_COMPRA = Assinatura.MODALIDADE_COMPRA;
	/** Material tombado por doa��o. */
	public static final int TOMBAMENTO_DOACAO = Assinatura.MODALIDADE_DOACAO;
	
	/** Guarda o valor do 1� agrupamento escolhido pelo usu�rio, caso o relat�rio v� usar agrupamentos */
	protected int valorAgrupamento1 = -1 ;
	
	/** Guarda o valor do 2� agrupamento escolhido pelo usu�rio, caso o relat�rio v� usar agrupamentos */
	protected int valorAgrupamento2 = -1 ;
	
	/** O 1� agrupamento escolhido pelo usu�rio, para agrupar os resultados da consulta */
	protected AgrupamentoRelatoriosBiblioteca agrupamento1 = AgrupamentoRelatoriosBiblioteca.getAgrupamento(AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.valor);
	
	/** O 2� agrupamento escolhido pelo usu�rio, para agrupar os resultados da consulta */
	protected AgrupamentoRelatoriosBiblioteca agrupamento2 = AgrupamentoRelatoriosBiblioteca.getAgrupamento(AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.valor);
	
	
	/** Mant�m a cole��o de agrupamentos 1 em memor�ria para o relat�rio */
	protected Collection<SelectItem> agrupamentos1;
	
	/** Mant�m a cole��o de agrupamentos 2 em memor�ria para o relat�rio */
	protected Collection<SelectItem> agrupamentos2;
	
	
	/** Garda o valor selecionado pelo usu�rio do campo ordena��o. O que fazer com esse valor vai depender do Mbean filho. */
	protected Integer ordenacao;
	
	/** Mant�m a cole��o dos campos de ordena��o em memor�ria para os relat�rios */
	protected Collection<SelectItem> ordenacaoItens;
	
	
	/**
	 * Indica a quantidade m�xima de bibliotecas escolhidas para o qual
	 * os nomes completos delas s�o mostrados no cabe�alho dos relat�rios.
	 * Se passar desse ponto, os identificadores s�o mostrados.
	 */
	private static final int MOSTRAR_NOMES_DAS_BIBLIOTECA_ATEH = 5;
	
	/**
	 * Indica a quantidade m�xima de pessoas escolhidas para o qual
	 * os nomes completos delas s�o mostrados no cabe�alho dos relat�rios.
	 * Se passar desse ponto, os identificadores s�o mostrados.
	 */
	private static final int MOSTRAR_NOMES_DAS_PESSOAS_ATEH = 5;
	
	
	//////// As flags que definem que filtros ser�o utilizados pelo relat�rio ///////
	
	
	/** Indica se o relat�rio ter� um filtro onde o usu�rio pode escolher v�rias bibliotecas. */
	protected boolean filtradoPorVariasBibliotecas         = false;

	/** Indica se o relat�rio ter� uma segunda op��o de filtro onde o usu�rio pode escolher v�rias bibliotecas. 
	 * Utilizado para relat�rios onde se envolve transfer�ncia de materiais entre bibliotecas. */
	protected boolean filtradoPorSegundaOpcaoVariasBiblioteca        = false;
	
	/** Indica se o relat�rio ter� um filtro onde o usu�rio pode escolher v�rias unidades. */
	protected boolean filtradoPorVariasUnidades = false;
	
	/** Indica se o relat�rio ter� um filtro por formas de documento.*/
	protected boolean filtradoPorFormasDocumento = false;
	
	/** Indica se o relat�rio ter� um filtro de curso. */
	protected boolean filtradoPorCurso                     = false;
	
	/** Indica se o relat�rio ter� um filtro de categoria de usu�rio (gradua��o, p�s, t�cnico, servidor, etc.). */
	protected boolean filtradoPorCategoriaDeUsuario        = false;
	
	/*/** Indica se o relat�rio ter� um filtro de situa��o de servidor (ativo, aposentado, cedido). *
	protected boolean filtradoPorSituacaoDeServidor        = false;*/
	
	/** Indica se o relat�rio ter� um filtro onde o usu�rio pode escolher v�rios tipos de usu�rios. */
	protected boolean filtradoPorVariasCategoriasDeUsuario = false;
	
	/** Indica se o relat�rio ter� um filtro onde o usu�rio pode escolher v�rios tipos de servidores. */
	protected boolean filtradoPorVariasSituacoesDeServidor = false;

	/** Indica se o relat�rio ter� um filtro onde o usu�rio pode escolher o tipo de opera��o sobre o acervo que deseja visualizar. */
	protected boolean filtradoPorTipoDeAcervo = false;
	
	/** Indica se o relat�rio ter� um filtro onde o usu�rio pode escolher v�rios tipos de material. */
	protected boolean filtradoPorTiposDeMaterial           = false;

	/** Indica se o relat�rio ter� um filtro de tipo de material. */
	protected boolean filtradoPorTipoDeMaterial            = false;

	/** Indica se o relat�rio ter� um filtro onde o usu�rio pode escolher v�rias tipos de material. */
	protected boolean filtradoPorSituacoesDoMaterial           = false;
	
	/** Indica se o relat�rio tem um seletor de filtro de per�odo ou ano. */
	protected boolean escolherEntrePeriodoEAno             = false;
	
	/** Indica se o relat�rio ter� um filtro de per�odo. */
	protected boolean filtradoPorPeriodo                   = false;
	
	/** Indica se o relat�rio ter� um filtro de ano. */
	protected boolean filtradoPorAno                       = false;
	
	/** Indica se o relat�rio ter� um filtro de m�s. */
	protected boolean filtradoPorMes                       = false;
	
	/** Indica se o relat�rio ter� um filtro de departamento. */
	protected boolean filtradoPorDepartamento              = false;
	
	/** Indica se o relat�rio ter� um filtro de servidor. */
	protected boolean filtradoPorServidor                  = false;
	
	/** Indica se o relat�rio ter� um filtro de pessoa. */
	protected boolean filtradoPorPessoa 				   = false;
	
	/** Indica se o relat�rio ter� um filtro onde o usu�rio pode escolher v�rias pessoas do sistema. */
	protected boolean filtradoPorVariasPessoas 			   = false;
	
	/** Indica se o relat�rio ter� um filtro de situa��o do usu�rio. */
	protected boolean filtradoPorSituacaoUsuario           = false;
	
	/** Indica se o relat�rio ter� um filtro de cole��o. */
	protected boolean filtradoPorColecao                   = false;
	
	/** Indica se o relat�rio ter� um filtro de v�rias cole��es. */
	protected boolean filtradoPorColecoes                   = false;

	/** Indica se o relat�rio ter� um filtro de c�digo de barras. */
	protected boolean filtradoPorCodigoDeBarras				= false;
	
	/** Indica se o relat�rio ter� um filtro de tipo de empr�stimo. */
	protected boolean filtradoPorTipoDeEmprestimo          = false;
	
	/** Indica se o relat�rio ter� um filtro que define se classifica��o utilizada �(CDU ou Black). */
	protected boolean filtradoPorTipoClassificacao                    = false;
	
	/** Indica se o relat�rio ter� um filtro de categoria de material mostrado: exemplares, fasc�culos, digitais ou todos. */
	protected boolean filtradoPorCtgMaterial               = false;

	/** Indica se o relat�rio ter� um filtro de turno. */
	protected boolean filtradoPorTurno				       = false;
	
	/** Indica se o relat�rio ter� um filtro de tipo de tombamento. */
	protected boolean filtradoPorTipoDeTombamento          = false;
	
	/** Indica se o relat�rio ter� um seletor que indica se o relat�rio � b�sico ou detalhado. */
	protected boolean filtradoPorBasicoVsDetalhado         = false;
	
	/** Indica que o no relat�rio vai aparecer uma op��o para o usu�rio agrupar os resultados*/
	protected boolean filtradoPorAgrupamento1 = false;
	
	/** Indica que o no relat�rio vai aparecer uma segunda op��o para o usu�rio agrupar os resultados*/
	protected boolean filtradoPorAgrupamento2 = false;
	
	/** Indica que o no relat�rio vai apresentar os campos para o usu�rio indicar a classifica��o inicial e final */
	protected boolean filtradoPorIntervaloClassificacao = false;
	
	/** Indica se o relat�rio ter� um filtro por formas diferentes de mostrar os dados (Anal�tico ou Sint�tico).*/
	protected boolean filtradoPorFormaRelatorio = false;
	
	/** Indica se o relat�rio ter� um filtro por modalidade de aquisi��o (Compra, Doa��o, N�o Identificado, Todas ).*/
	protected boolean filtradoPorModalidadeAquisicao = false;
	
	/** Indica que a p�gina ter� um filtro para ordenar os resultado definidos no MBean filho.*/
	protected boolean filtradoPorOrdenacao = false;
	
	/**
	 * Filtro de algum dado booleano gen�rico para o relat�rio. <br/>
	 * A descri��o que deve aparecer para o usu�rio nesse campo deve ser colocada na vari�vel descricaoDadoBooleano.
	 */
	protected boolean filtradoPorBooleano = false;
	
	/** Indica se o filtro de situa��o do usu�rio deve incluir o item <em>Usu�rio sem pend�ncias</em>*/
	protected boolean incluirUsuariosSemPendencias = true;
	
	/** Indica se � obrigat�rio escolher alguma biblioteca ou n�o*/
	protected boolean campoBibliotecaObrigatorio = true;
	
	/** Indica se � obrigat�rio informar a faixa de classifica��o. */
	protected boolean campoIntervaloClassificacaoObrigatorio  = true;
	
	/** Indica se � obrigat�rio escolher alguma biblioteca ou n�o no segundo filtro de bibliotecas*/
	protected boolean campo2BibliotecaObrigatorio = true;
	
	/** Indica se � obrigat�rio escolher alguma pessoa ou n�o*/
	protected boolean campoPessoaObrigatorio = true;
	
	/** Indica se � obrigat�rio escolher um per�odo ou n�o*/
	protected boolean campoPeriodoObrigatorio = true;
	
	/** Indica que o relat�rio vai utilizar pagina��o */
	protected boolean utilizandoPaginacao = false;
	
	/** Indica se deve exibir a informa��o de filtros obrigat�rios na p�gina de filtro ou n�o. 
	 * ( A estrela no rodap� da p�gina de filtros ) */
	protected boolean possuiFiltrosObrigatorios = true;
	
	/** Indica se o filtro de situa��o de servidorest� habilitado ou n�o */
	protected boolean desabilitaFiltroDependenteDeServidor = true;
	
	
	
	//////// Filtros ////////

	
	
	/** Indica se o item "Todas bibliotecas" pode ser escolhido pelo usu�rio. */
	protected boolean permiteTodasBibliotecas = true;
	
	/** Indica se o item "Todas bibliotecas setoriais" pode ser escolhido pelo usu�rio. */
	protected boolean permiteTodasBibliotecasSetoriais = true;
	
	/** Filtro que permite uma escolha mais granular das bibliotecas. */
	protected List<String> variasBibliotecas = new ArrayList<String>();
	
	/** Guarda os ids da biblioteca para o segundo filtro de biblioteca. Utilizado para relat�rio que envolvem tranfer�ncia de materiais entre bibliotecas. */
	protected List<String> variasBibliotecas2 = new ArrayList<String>();
	
	/** Filtro que permite uma escolha mais granular das unidades. */
	protected List<String> variasUnidades;
	
	/** Filtro que permite uma escolha mais granular das passoas. */
	protected List<String> variasPessoas = new ArrayList<String>();
	
	/** Filtro que permite uma escolha mais de uma forma de documento. */
	protected List<String> formasDocumento = new ArrayList<String>();
	
	/** Filtro que permite uma escolha mais de uma situa��o dos materiais. */
	protected List<String> situacoesMaterial = new ArrayList<String>();
	
	/** Filtro de curso */
	protected int curso;
	
	/** Filtro que permite que se escolha uma �nica categoria de usu�rio. */
	protected int valorVinculoDoUsuarioSelecionado = -1;
	
	/*/** Filtro que permite que se escolha uma �nica situa��o de servidor. *
	protected int valorSituacaoDoServidorSelecionado = -1;*/
	
	/** Filtro que permite que o usu�rio escolha v�rias categorias de usu�rio. */
	protected List<String> variasCategoriasDeUsuario;
	
	/** Filtro que permite que o usu�rio escolha v�rias situa��es de servidor. */
	protected List<String> variasSituacoesDeServidor;
	
	/** Filtro de tipos de material. Pode selecionar mais de um. */
	protected List<String> tiposDeMaterial = new ArrayList<String>();

	/** Filtro de colecoes. Pode selecionar mais de um. */
	protected List<String> colecoes = new ArrayList<String>();

	
	/** Filtro de tipo de opera��o sobre acervo onde pode selecionar ou todos ou somente um. */
	protected Integer tipoDeAcervo = 0;
	
	/** Filtro de tipo de material onde pode selecionar ou todos ou somente um. */
	protected Integer tipoDeMaterial = 0;
	
	/** Filtro de cole��o onde pode selecionar ou todos ou somente um. */
	protected Integer colecao = 0;
	
	/** Filtro de c�digo de barras. */
	protected String codigoDeBarras;
	
	/** Seletor que serve para escolher se a filtragem do tempo ser� por per�odo ou por ano. */
	protected boolean filtrarTempoPorAno = true;
	
	/** Filtro de per�odo: in�cio do per�odo. */
	protected Date inicioPeriodo = new Date();
	
	/** Filtro de per�odo: fim do per�odo. */
	protected Date fimPeriodo = new Date();
	
	/** Filtro de ano. */
	protected Integer ano = CalendarUtils.getAnoAtual();
	
	/** Filtro de m�s. */
	protected Integer mes = CalendarUtils.getMesAtual();

	/** Filtro de Departamento. */
	protected int departamento;
	
	/** Filtro de servidor. */
	protected int servidor;
	
	/** Filtro de pessoa. */
	protected int pessoa;
	
	
	/** Filtra pela situa��o do usu�rio: sem pend�ncia, suspenso, etc. */
	protected SituacaoUsuarioBiblioteca situacaoUsuarioBiblioteca;
	
	/// Campos adicionais/auxiliares ///
	
	/** Nome do servidor utilizado no filtro de servidor. */
	protected String nomeServidor;
	
	/** Nome da pessoa utilizada no filtro de pessoa. */
	protected String nomePessoa;
	
	/** Nome das pessoas utilizadas no filtro de pessoa. */
	protected List<String> nomeVariasPessoas = new ArrayList<String>();
	
	/** Cont�m o valor para o data booleano escolhido pelo usu�rio, em cada relat�rio esse valor 
	 * booleano vai ter um significado diferente. */
	protected boolean dadoBooleano;
	
	/** O significado da vari�vel booleana que vai ser mostrado ao usu�rio.
	 * Cada relat�rio pode ter um significado diferente */
	protected String descricaoDadoBooleano;
	
	/** Guarda o ID do tipo de empr�stimo escolhido. */
	protected int tipoDeEmprestimo;
	
	/** Guarda o valor do tipo de classifica��o bibliogr�ficas escolhida no filtro de classifica��o. */
	protected int tipoclassificacaoEscolhida;
	
	/** Guarda o tipo de classifica��o bibliogr�ficas escolhida no filtro de classifica��o. */
	protected FiltroClassificacoesRelatoriosBiblioteca classificacaoEscolhida = FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1;
	
	/** Garda a descri��o da classifica��o correspondete a classifica��o escolhida no filtro de classifica��o*/
	protected String descricaoClassificacaoEscolhida;
	
	/** Guarda a lista de classes princiais da classifica��es escolhida */
	protected List<String> siglasAreasCNPqBiblioteca = new ArrayList<String>();
	
	/** O tipo de modalidade de aquisi��o escolhida: Compra, Doa��o, Todas, etc....*/
	protected int tipoModalidadeEscolhida = MODALIDADE_AQUISICAO_TODAS;
	
	/** Indica se o relat�rio � anal�tico ou sint�tico */
	protected int formatoRelatorio = SINTETICO;
	
	/** In�cio da faixa de classes para filtrar os relat�rios. Vai contar os dados das classes CDU, Black, CDD, etc... */
	protected String classeInicial;
	
	/** Final da faixa de classes para filtrar os relat�rios. Vai contar os dados das classes CDU, Black, CDD, etc... */
	protected String classeFinal;
	
	/** A categoria de material retornada no relat�rio: exemplares, fasc�culos, digitais ou todos. */
	protected int ctgMaterial = CTGMAT_TODOS;
	
	/** O turno retornado no relat�rio: manh�, tarde, noite ou todos. */
	protected int turno = ConstantesRelatorioBiblioteca.Turnos.TODOS.getValor();
	
	/** O tipo de tombamento: compra ou doa��o. */
	protected int tipoDeTombamento = TOMBAMENTO_COMPRA;
	
	/** Flag que indica se o usu�rio pode escolher todos os tipos de tombamento. */
	protected boolean permitirTodosTiposDeTombamento = false;
	
	/** Indica se o relat�rio deve ser b�sico ou detalhado. */
	protected boolean mostrarDetalhado = false;
	
	/** Cache para as grandes �reas do CNPq. */
	//private Collection<String> cacheAreasCNPq;
	
	/** Indica se o valor 'Todos' estar� dispon�vel na categoria de material. */
	protected boolean permitirTodasCtgMaterial = true;
	
	/** Indica se o valor 'Digital' estar� dispon�vel na categoria de material. */
	protected boolean permitirDigitalCtgMaterial = false;
	
	/** DataModel para preenchimento da tabela de pessoas do filtro de v�rias pessoas */
	private DataModel nomeVariasPessoasDataModel = new ListDataModel(nomeVariasPessoas);
	
	/**
	 * Par�metros e valores escolhidos para o relat�rio.
	 * Utilizado no cabe�alho da p�gina JSP do relat�rio.
	 */
	private Map<String, String> parametrosEscolhidos;
	
	/**
	 * O dao utilizado na consulta dos relat�rios para ser fechado pela classe pai.
	 */
	private GenericDAO daoUtilizadoRelatorio;
	
	
	
	
	/** P�gina atual do relat�rio que est� sendo mostrada ao usu�rio nos relat�rios que usam pagina��o. */
	protected int paginaAtual;
	
	/** Indica a posi��o geral do registro inicial da p�gina atual nos relat�rios que usam pagina��o. */
	protected int registroInicial;
	
	/** Indica a posi��o  geral do �ltimo registro da p�gina atual nos relat�rios que usam pagina��o. */
	protected int registroFinal;
	
	
	/** Indica qual � o n�mero total de p�ginas que o relat�rio possui nos relat�rios que usam pagina��o. */
	protected int numeroTotalDePaginas;
	
	/** Indica a quantidade total de registros retornados pela consulta nos relat�rios que usam pagina��o. */
	protected int numeroTotalDeRegistros = 0;
	
	
	/**
	 * O nome do 1� filtro de bibliotecas que � mostrado ao usu�rio, esse nome pode ser sobreescrito em cada relat�rio.
	 */
	protected String descricaoFiltro1Bibliotecas = "Bibliotecas"; 
	
	/**
	 * O nome do 2� filtro de bibliotecas que � mostrado ao usu�rio, esse nome pode ser sobreescrito em cada relat�rio.
	 */
	protected String descricaoFiltro2Bibliotecas = "Bibliotecas";
	
	
	/**
	 * Inicializa o MBean.
	 */
	public AbstractRelatorioBibliotecaMBean() {
		if(ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao1())
			tipoclassificacaoEscolhida = FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1.getValor();
		else{
			if(ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao2())
				tipoclassificacaoEscolhida = FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2.getValor();
			else{
				if(ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao3())
					tipoclassificacaoEscolhida = FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3.getValor();
			}
		}
	}
	
	/**
	 * M�todo que deve ser chamado pelo relat�rio que est� usando o AbstractRelatorioBibliotecaMBean para 
	 * configurar um nome padr�o do bean que vai ser acessado da p�gina padr�o de filtros 
	 *
	 * @param mBeanFilho
	 */
	protected void configuraMBeanRelatorio(AbstractRelatorioBibliotecaMBean mBeanFilho){
		getCurrentRequest().setAttribute("_abstractRelatorioBiblioteca", mBeanFilho);
	}
	
	
	/**
	 * M�todo que pode ser chamado pelo relat�rio que est� usando o AbstractRelatorioBibliotecaMBean para 
	 * guardar uma refer�ncia ao Dao utilizada nos relat�rios, assim ele ser� fechado automaticamente, sem a necessidade
	 * de colocar em todo relat�rio blocos try e finally. 
	 *
	 * @param mBeanFilho
	 */
	protected void configuraDaoRelatorio(GenericDAO daoUtilizadoRelatorio){
		this.daoUtilizadoRelatorio = daoUtilizadoRelatorio;
	}
	
	
	/**
	 * <p>1� M�todo que � chamado por onde se inicia todos os casos de usos de relat�rios no sistema.</p>
	 * 
	 * <p>Faz a configura��o inicial do relat�rio e redireciona para o formul�rio de filtros padr�o.</p>
	 * 
	 * <p>Este m�todo � chamado, indiretamente, por praticamente todos os relat�rios na p�gina.
	 * <ul><li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li></ul>
	 */
	public final String iniciar() throws DAOException, SegurancaException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		
		configurar(); // Implementa��o do padr�o Template Method, obriga a configura��o dos filtros que o relat�rio vai usar
		
		return telaPadraoFiltrosRelatoriosBiblioteca();
	}
	
	
	
	
	/**
	 * <p>Configura as informa��es b�sicas e os filtros do relat�rio.</p>
	 * 
	 * <p>Deve ser implementado nos MBeans espec�ficos dos relat�rios porque s� eles sabem quais filtros desejam utilizar</p>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 */
	public abstract void configurar();
	
	
	/**
	 * <p>Faz a consulta ao banco e redireciona para a p�gina do relat�rio.</p>
	 * 
	 * <p>Deve ser implementado nos MBeans espec�ficos dos relat�rios porque a consulta e o resultado mostrado ao usu�rio � espec�fico de cada relat�rio.</p>
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public abstract String gerarRelatorio() throws DAOException, SegurancaException;
	

	/**
	 * <p>Retorna o primeiro grupo de agrupamentos que devem ser usados nos relat�rios.</p>
	 * 
	 * <p>Deve ser implementado nos MBeans dos relat�rios apenas caso o relat�rio vai utilizar agrupamento.</p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/controle_estatistico/paginaFilstrosPadraoRelatoriosBiblioteca.jsp</li>
	 * </ul>
	 */
	public  Collection<SelectItem> getAgrupamentos1ComboBox(){return null;};
	
	
	
	/**
	 * <p>Retorna segundo grupo de agrupamentos que devem ser usados nos relat�rios.</p>
	 * 
	 * <p>Deve ser implementado nos MBeans dos relat�rios apenas caso o relat�rio vai utilizar agrupamento.</p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/controle_estatistico/paginaFilstrosPadraoRelatoriosBiblioteca.jsp</li>
	 * </ul>
	 */
	public  Collection<SelectItem> getAgrupamentos2ComboBox(){return null;}
	
	
	/**
	 * Retorna os campos de ordena��o para o relat�rio, os campos devem ser definidos no relat�rio filhos, sobreescrevendo esse m�todo.
	 * @return
	 *  
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/controle_estatistico/paginaFilstrosPadraoRelatoriosBiblioteca.jsp</li>
	 * </ul>
	 *
	 */
	public Collection<SelectItem> getOrdenacaoComboBox(){return null;}
	
	
	
	/**
	 * <p>Definidos pelas classes filhas, redefine o m�todo respons�vel por gerar os pr�ximos resultados das consultas de relat�rios 
	 * paginados. Exemplo: {@link RelatorioPorFaixaDeClassificacaoMBean} </p>
	 * 
	 * <p>M�todo chamado nas jps de cada relat�rio espec�fico que implementa esse.</p>
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/controle_estatistico/relatorioPorFaixaClassificacaoAnalitico.jsp</li>
	 *   </ul>
	 * 
	 */
	public String gerarProximosResultadosConsultaPaginada()  throws DAOException { return null; }
	
	
	/**
	 * Valida, gera a descri��o dos par�metros (para colocar no cabe�alho do relat�rio),
	 * e chama o m�todo de gerar o relat�rio.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li>
	 *   </ul>
	 * 
	 */
	public final String gerar() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		long tempoInicial = System.currentTimeMillis();
		
		try{
			validarFiltros();
			
			if ( validacaoDados(erros) )
				return null;
			gerarDescricaoDosParametros();
			
			return gerarRelatorio(); // Implementa��o do padr�o Template Method, realiza a consulta e retorna para a p�gina implementada no MBean filho.
			
		}finally{
			System.out.println("...........................................................................................");
			System.out.println(" Consulta do relat�rio "+this.titulo+" demorou: "+(System.currentTimeMillis()-tempoInicial)+" ms.");
			System.out.println("...........................................................................................");
			
			System.out.println("Closing the report's dao of the library to you ");
			if(daoUtilizadoRelatorio != null) daoUtilizadoRelatorio.close(); 
		}
		
	}
	

	
	/**
	 *  <p>Valida o preenchimento dos filtros dos relat�rios da biblioteca.</p>
	 *  
	 *  <p> <strong> Sempre que adicionar um novo filtro, verificar esse m�todo. </strong> </p>
	 */
	private void validarFiltros() {
		
		if ( filtradoPorVariasBibliotecas )
			if ( variasBibliotecas.isEmpty() && campoBibliotecaObrigatorio)
				erros.addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Bibliotecas");
		
		if ( filtradoPorVariasPessoas )
			if ( variasPessoas.isEmpty() && campoPessoaObrigatorio)
				erros.addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Usu�rios");
		
		if ( filtradoPorVariasCategoriasDeUsuario )
			if ( variasCategoriasDeUsuario.isEmpty() )
				erros.addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categorias de Usu�rio");
		
		if (   (filtradoPorPeriodo && ! escolherEntrePeriodoEAno ) ||
				(filtradoPorPeriodo && escolherEntrePeriodoEAno && !!! filtrarTempoPorAno) ) {
			if (  ( inicioPeriodo == null || fimPeriodo == null ) && campoPeriodoObrigatorio )
				erros.addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Per�odo" );
		
			if ( inicioPeriodo != null && fimPeriodo != null )
				if ( inicioPeriodo.compareTo( fimPeriodo ) > 0 )
					erros.addMensagem( MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Per�odo" );
		}

		if ( filtradoPorServidor ) {
			if ( servidor <= 0 )
				erros.addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Servidor" );
		}

		if ( filtradoPorPessoa ) {
			if ( pessoa <= 0 )
				erros.addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Usu�rio" );
		}
		
		if ( filtradoPorSituacaoUsuario ) {
			if ( ! (   situacaoUsuarioBiblioteca != null ||
					SituacaoUsuarioBiblioteca.POSSIVEIS_SITUACAO_DO_USUARIO.contains(situacaoUsuarioBiblioteca ) )  )
				erros.addMensagem( MensagensArquitetura.CONTEUDO_INVALIDO, "Situa��o do Usu�rio");
		}
		
		if ( filtradoPorTipoClassificacao ) {
			if ( classificacaoEscolhida != FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1 
					&& classificacaoEscolhida != FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2 
					&& classificacaoEscolhida != FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3 )
				erros.addMensagem( MensagensArquitetura.CONTEUDO_INVALIDO, "Escolha uma classifica��o bibliogr�fica" );
		}
		
		if ( filtradoPorAgrupamento1 && filtradoPorAgrupamento2 ) {
			if ( agrupamento1 == agrupamento2 ) {
				erros.addErro("O  2� agrupamento deve ser diferente do 1� agrupamento. ");
			}
		}
		
		if ( filtradoPorCodigoDeBarras ) {
			if ( StringUtils.isEmpty(codigoDeBarras) )
				erros.addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "C�digo de Barras");
		}
		
		if ( filtradoPorIntervaloClassificacao ) {
			
			if(campoIntervaloClassificacaoObrigatorio){
				if ( StringUtils.isEmpty(classeInicial)) {
					erros.addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Classe Inicial" );
				}
				if ( StringUtils.isEmpty(classeFinal)) {
					erros.addMensagem( MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Classe Final" );
				}
			}
		}
		
	}
	
	/**
	 * <p>Gera uma descri��o para cada par�metro escolhido para serem utilizadas
	 * no cabe�alho de cada relat�rio.</p>
	 * 
	 *  <p> <strong> Sempre que adicionar um novo filtro, adicionar sua descri��o esse m�todo. </strong> </p>
	 *  
	 *  <p> <strong> No nome dos filtros, colocar a numera��o "XX - " de tamanho fixo 5, para a ordena��o correta dos mesmos. Na p�gina essa numera��o �
	 *  retirado com um substring(4, tamanho). </strong> </p>
	 * 
	 */
	protected final void gerarDescricaoDosParametros() throws DAOException {
		
		parametrosEscolhidos = new TreeMap<String, String>();
		
		
		
		// 01 - Biblioteca
		if ( filtradoPorVariasBibliotecas ) {
			
			if( variasBibliotecas.size() == 0 && campoBibliotecaObrigatorio == false){
				parametrosEscolhidos.put("01 - "+descricaoFiltro1Bibliotecas, "TODAS"); // se usu�rio n�o escolheu � porque est� buscando todas (quando o campo n�o � obrigat�rio)
			}else{
			
				if ( variasBibliotecas.size() == 1 )
					parametrosEscolhidos.put("01 - "+descricaoFiltro1Bibliotecas, getDAO( BibliotecaDao.class ).
							findDescricaoBibliotecaInternaAtiva(Integer.parseInt( variasBibliotecas.get(0) )) );
				else {
					BibliotecaDao dao = getDAO( BibliotecaDao.class );
					List<String> bibs = new ArrayList<String>();
	
					if ( variasBibliotecas.size() <= MOSTRAR_NOMES_DAS_BIBLIOTECA_ATEH ) {
						for ( String s : variasBibliotecas )
							bibs.add( dao.findDescricaoBibliotecaInternaAtiva(Integer.parseInt(s)) );
						parametrosEscolhidos.put("01 - "+descricaoFiltro1Bibliotecas, StringUtils.join(bibs, "<br/>\n"));
					} else {
						for ( String s : variasBibliotecas )
							bibs.add( dao.findByPrimaryKey(Integer.parseInt(s), Biblioteca.class).getIdentificador() );
						parametrosEscolhidos.put("01 - "+descricaoFiltro1Bibliotecas, StringUtils.transformaEmLista(bibs));
					}
					
				}
			}
		}
		
		// 02 - Segundo filtro de Bibliotecas 
		if ( filtradoPorSegundaOpcaoVariasBiblioteca ) {
			
			if( variasBibliotecas2.size() == 0 && campoBibliotecaObrigatorio == false){
				parametrosEscolhidos.put("02 - "+descricaoFiltro2Bibliotecas, "TODAS"); // se usu�rio n�o escolheu � porque est� buscando todas (quando o campo n�o � obrigat�rio)
			}else{
			
				if ( variasBibliotecas2.size() == 1 )
					parametrosEscolhidos.put("02 - "+descricaoFiltro2Bibliotecas, getDAO( BibliotecaDao.class ).
							findDescricaoBibliotecaInternaAtiva(Integer.parseInt( variasBibliotecas2.get(0) )) );
				else {
					BibliotecaDao dao = getDAO( BibliotecaDao.class );
					List<String> bibs = new ArrayList<String>();
	
					if ( variasBibliotecas2.size() <= MOSTRAR_NOMES_DAS_BIBLIOTECA_ATEH ) {
						for ( String s : variasBibliotecas2 )
							bibs.add( dao.findDescricaoBibliotecaInternaAtiva(Integer.parseInt(s)) );
						parametrosEscolhidos.put("02 - "+descricaoFiltro2Bibliotecas, StringUtils.join(bibs, "<br/>\n"));
					} else {
						for ( String s : variasBibliotecas2 )
							bibs.add( dao.findByPrimaryKey(Integer.parseInt(s), Biblioteca.class).getIdentificador() );
						parametrosEscolhidos.put("02 - "+descricaoFiltro2Bibliotecas, StringUtils.transformaEmLista(bibs));
					}
					
				}
			}
		}
		
		
		// 03  -  Pessoa
		if (filtradoPorPessoa) {
			if (pessoa > 0) {
				parametrosEscolhidos.put("03 - Usu�rio", getDAO(PessoaDAO.class).findByPrimaryKey(pessoa, Pessoa.class, "nome", "cpf_cnpj").getNomeCpfCnpj());
			}
		}
		
		if (filtradoPorVariasPessoas) {	
			if (variasPessoas.size() == 0 && campoPessoaObrigatorio == false) {
				parametrosEscolhidos.put("03 - Usu�rio", "TODOS"); // se usu�rio n�o escolheu � porque est� buscando todas (quando o campo n�o � obrigat�rio)
			} else {
				if (variasPessoas.size() == 1) {
					parametrosEscolhidos.put("03 - Usu�rio", getDAO(PessoaDAO.class).findByPrimaryKey(Integer.parseInt(variasPessoas.get(0)), Pessoa.class, "nome", "cpf_cnpj").getNomeCpfCnpj());
				} else {
					PessoaDAO dao = getDAO(PessoaDAO.class);
					List<String> pessoas = new ArrayList<String>();

					// Para mais de MOSTRAR_NOMES_DAS_PESSOAS_ATEH pessoas,
					// os identificadores s�o utilizados em vez da descri��o
					if (variasPessoas.size() <= MOSTRAR_NOMES_DAS_PESSOAS_ATEH) {
						for (String s : variasPessoas) {
							pessoas.add(dao.findByPrimaryKey(Integer.parseInt(s), Pessoa.class, "nome", "cpf_cnpj").getNomeCpfCnpj());
						}
						
						parametrosEscolhidos.put("03 - Usu�rios", StringUtils.join(pessoas, "<br/>\n"));
					} else {
						for (String s : variasPessoas) {
							pessoas.add(dao.findByPrimaryKey(Integer.parseInt(s), Pessoa.class, "cpf_cnpj").getCpfCnpjFormatado());
						}
						
						parametrosEscolhidos.put("03 - Usu�rios", StringUtils.transformaEmLista(pessoas));
					}
				}
			}
		}		
		
		// 04 - Colecao
		if ( filtradoPorColecao ) {
			if ( colecao == null || colecao <= 0 )
				parametrosEscolhidos.put( "04 - Cole��o", "Todas" );
			else
				parametrosEscolhidos.put( "04 - Cole��o", getGenericDAO().findByPrimaryKey(colecao, Colecao.class, "descricao").getDescricao());
		}
	
		if ( filtradoPorColecoes ) {
			if(colecoes != null && colecoes.size() > 0){
				String mats = "";
				boolean precederComVirgula = false;
				for ( String id : colecoes ) {
					if ( precederComVirgula )
						mats += ", ";
					else
						precederComVirgula = true;
					mats += getGenericDAO().findByPrimaryKey( Integer.parseInt(id), Colecao.class, "descricao" ).getDescricao();
				}
				
				parametrosEscolhidos.put( "04 - Cole��es ", mats );
			}else{
				parametrosEscolhidos.put( "04 - Cole��es", "Todas" );
			}
		}
		
		// 05 - Tipo de Material
		if ( filtradoPorTipoDeMaterial ) {
			if ( tipoDeMaterial == null || tipoDeMaterial <= 0 )
				parametrosEscolhidos.put( "05 - Tipo de Material", "Todos" );
			else
				parametrosEscolhidos.put( "05 - Tipo de Material", getGenericDAO().findByPrimaryKey(tipoDeMaterial, TipoMaterial.class, "descricao").getDescricao() );
		}
		
		if ( filtradoPorTiposDeMaterial ) {
			if(tiposDeMaterial != null && tiposDeMaterial.size() > 0){
				
				String mats = "";
				boolean precederComVirgula = false;
				for ( String m : tiposDeMaterial ) {
					if ( precederComVirgula )
						mats += ", ";
					else
						precederComVirgula = true;
					mats += getGenericDAO().findByPrimaryKey( Integer.parseInt(m), TipoMaterial.class, "descricao").getDescricao();
				}
				
				parametrosEscolhidos.put( "05 - Tipos de Material", mats );
			}else{
				parametrosEscolhidos.put( "05 - Tipos de Material", "Todos" );
			}
		}
		
		
		
		// 06 - Codigo de Barras
		if ( filtradoPorCodigoDeBarras ) {
			if ( StringUtils.isEmpty(codigoDeBarras) )
				parametrosEscolhidos.put( "06 - C�digo de Barras", "Todos" );
			else
				parametrosEscolhidos.put( "06 - C�digo de Barras", codigoDeBarras+"...");
		}
		
		// 6 - Situacao Material
		if ( filtradoPorSituacoesDoMaterial ) {
			if ( situacoesMaterial != null && situacoesMaterial.size() > 0 ) {
				GenericDAO dao = getGenericDAO();
				List<String> situacoes = new ArrayList<String>();
				for ( String s : situacoesMaterial ){
					situacoes.add( dao.findByPrimaryKey(Integer.parseInt(s), SituacaoMaterialInformacional.class, "descricao").getDescricao());
				}
				
				parametrosEscolhidos.put("06 - Situa��es ", StringUtils.transformaEmLista(situacoes));
			}else{
				parametrosEscolhidos.put("06 - Situa��o", "Todas");
			}
			
		}
		
		// 7 - Tipo de acervo
		if ( filtradoPorTipoDeAcervo ) {
			if ( tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TODOS )
				parametrosEscolhidos.put( "07 - Tipo de Acervo", "Todos" );
			else if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_TITULOS)
				parametrosEscolhidos.put( "07 - Tipo de Acervo", "T�tulos" );
			else if (tipoDeAcervo == ConstantesRelatorioBiblioteca.TIPO_ACERVO_MATERIAIS)
				parametrosEscolhidos.put( "07 - Tipo de Acervo", "Materiais" );
		}
		
		// 8 - Formas Documento
		if ( filtradoPorFormasDocumento ) {
			if ( formasDocumento != null && formasDocumento.size() > 0 ) {
				GenericDAO dao = getGenericDAO();
				List<String> formas = new ArrayList<String>();
				for ( String s : formasDocumento ){
					FormaDocumento forma =  dao.findByPrimaryKey(Integer.parseInt(s), FormaDocumento.class, "denominacao");
					if(forma != null) formas.add( forma.getDenominacao());
				}
	
				parametrosEscolhidos.put("08 - Formas do Documento ", StringUtils.transformaEmLista(formas));
			} else {
				parametrosEscolhidos.put("08 - Forma do Documento ", "Todas");
			}
			
		}
		
		// 9 - Categoria do usu�rio
		if ( filtradoPorCategoriaDeUsuario ) {
			if ( valorVinculoDoUsuarioSelecionado < 0 )
				parametrosEscolhidos.put("09 - Categoria do Usu�rio", "Todas");
			else
				parametrosEscolhidos.put("09 - Categoria do Usu�rio", VinculoUsuarioBiblioteca.getVinculo(valorVinculoDoUsuarioSelecionado ).getDescricao());
		}
		
		if ( filtradoPorVariasCategoriasDeUsuario ) {
			if ( variasCategoriasDeUsuario.size() == 1 )
				parametrosEscolhidos.put("09 - Categoria de Usu�rio",
						VinculoUsuarioBiblioteca.getVinculo(Integer.parseInt(variasCategoriasDeUsuario.get(0)) ).getDescricao() );
			else {
				List<String> cats = new ArrayList<String>();
				for (String s : variasCategoriasDeUsuario )
					cats.add( VinculoUsuarioBiblioteca.getVinculo( Integer.parseInt(s)).getDescricao() );
				parametrosEscolhidos.put("09 - Categorias de Usu�rio", StringUtils.transformaEmLista(cats));
			}
				
		}
		
		// 10 - Situa��o de Servidor
		/*if ( filtradoPorSituacaoDeServidor ) {
			if ( valorSituacaoDoServidorSelecionado < 0 )
				parametrosEscolhidos.put("10 - Situa��o do Servidor", "Todas");
			else if (valorVinculoDoUsuarioSelecionado == Ativo.SERVIDOR_ATIVO)
				parametrosEscolhidos.put("10 - Situa��o do Servidor", "Ativo");
			else if (valorVinculoDoUsuarioSelecionado == Ativo.APOSENTADO)
				parametrosEscolhidos.put("10 - Situa��o do Servidor", "Aposentado");
			else if (valorVinculoDoUsuarioSelecionado == Ativo.CEDIDO)
				parametrosEscolhidos.put("10 - Situa��o do Servidor", "Cedido");
		}*/
		
		if ( filtradoPorVariasSituacoesDeServidor ) {
			if (variasSituacoesDeServidor == null || desabilitaFiltroDependenteDeServidor) {
				parametrosEscolhidos.put("10 - Situa��o do servidor", "N/A");				
			} else if (variasSituacoesDeServidor.size() == 0 ){
				parametrosEscolhidos.put("10 - Situa��o do servidor", "Todas"); // se usu�rio n�o escolheu � porque est� buscando todas (quando o campo n�o � obrigat�rio)
			} else {
				if ( variasSituacoesDeServidor.size() == 1 ) {
					int valorSituacaoDeServidor = Integer.parseInt(variasSituacoesDeServidor.get(0));
	
					if (valorSituacaoDeServidor == Ativo.SERVIDOR_ATIVO)
						parametrosEscolhidos.put("10 - Situa��o de Servidor", "Ativo");
					else if (valorSituacaoDeServidor == Ativo.APOSENTADO)
						parametrosEscolhidos.put("10 - Situa��o de Servidor", "Aposentado");
					else if (valorSituacaoDeServidor == Ativo.CEDIDO)
						parametrosEscolhidos.put("10 - Situa��o de Servidor", "Cedido");
				} else {
					List<String> sits = new ArrayList<String>();
					int valorSituacaoDeServidor;
					
					for (String s : variasSituacoesDeServidor ) {
						valorSituacaoDeServidor = Integer.parseInt(s);
	
						if (valorSituacaoDeServidor == Ativo.SERVIDOR_ATIVO)
							sits.add("Ativo");
						else if (valorSituacaoDeServidor == Ativo.APOSENTADO)
							sits.add("Aposentado");
						else if (valorSituacaoDeServidor == Ativo.CEDIDO)
							sits.add("Cedido");
					}
					
					parametrosEscolhidos.put("10 - Situa��es de Servidor", StringUtils.transformaEmLista(sits));
				}
			}
		}
		
		// 11 - Unidades
		if ( filtradoPorVariasUnidades ) {			
			if (variasSituacoesDeServidor == null || desabilitaFiltroDependenteDeServidor) {
				parametrosEscolhidos.put("11 - Unidade Administrativa", "N/A");
			} else if( variasUnidades.size() == 0 ){
				parametrosEscolhidos.put("11 - Unidade Administrativa", "Todas"); // se usu�rio n�o escolheu � porque est� buscando todas (quando o campo n�o � obrigat�rio)
			}else{
			
				if ( variasUnidades.size() == 1 )
					parametrosEscolhidos.put("11 - Unidade Administrativa", getDAO( UnidadeDao.class ).
							findByPrimaryKey(Integer.parseInt(variasUnidades.get(0)), Unidade.class, "nome").getNome());
				else {
					UnidadeDao dao = getDAO( UnidadeDao.class );
					List<String> unids = new ArrayList<String>();
	
					if ( variasUnidades.size() <= MOSTRAR_NOMES_DAS_BIBLIOTECA_ATEH ) {
						for ( String s : variasUnidades )
							unids.add( dao.findByPrimaryKey(Integer.parseInt(s), Unidade.class, "nome").getNome() );
						parametrosEscolhidos.put("11 - Unidades Administrativas", StringUtils.join(unids, "<br/>\n"));
					} else {
						for ( String s : variasUnidades )
							unids.add( dao.findByPrimaryKey(Integer.parseInt(s), Unidade.class, "sigla").getSigla() );
						parametrosEscolhidos.put("11 - Unidades Administrativas", StringUtils.transformaEmLista(unids));
					}
					
				}
			}
		}
		
		if( ( StringUtils.notEmpty( classeInicial) && StringUtils.notEmpty( classeFinal) )  || campoIntervaloClassificacaoObrigatorio == true ){
			//10 - Tipo de Classifica��o
			if ( filtradoPorTipoClassificacao ) {
				parametrosEscolhidos.put("10 - Classifica��o", getDescricaoClassificacaoEscolhida());
			}
			
			// 11 e 12 -  Classe inicial e final
			if(filtradoPorIntervaloClassificacao ){
				parametrosEscolhidos.put("11 - Classe Inicial", classeInicial);
				parametrosEscolhidos.put("12 - Classe Final", classeFinal);
			}
		}
		
		// 13  -  Categoria do material
		if ( filtradoPorCtgMaterial ) {
			if ( ctgMaterial == CTGMAT_TODOS ) {
				if ( permitirDigitalCtgMaterial )
					parametrosEscolhidos.put("13 - Materiais Mostrados", "Exemplares, Fasc�culos e Digitais");
				else 
					parametrosEscolhidos.put("13 - Materiais Mostrados", "Exemplares e Fasc�culos");
			}
			else if ( ctgMaterial == CTGMAT_EXEMPLARES ) parametrosEscolhidos.put("13 - Materiais Mostrados", "Exemplares");
			else if ( ctgMaterial == CTGMAT_FASCICULOS ) parametrosEscolhidos.put("13 - Materiais Mostrados", "Fasc�culos");
			else if ( ctgMaterial == CTGMAT_DIGITAIS ) parametrosEscolhidos.put("13 - Materiais Mostrados", "Digitais");
		}
		
		// 14  -  Curso
		if ( filtradoPorCurso ) {
			if ( curso == 0 )
				parametrosEscolhidos.put("14 - Curso", "Todos");
			else
				parametrosEscolhidos.put("14 - Curso", getDAO( CursoDao.class ).
						findByPrimaryKey( curso, Curso.class ).getDescricaoCompleta() );
		}
		
//		// 13  -  Servidor
//		if ( filtradoPorServidor ) {
//			if ( servidor > 0 )
//				parametrosEscolhidos.put( "13 - Servidor", getDAO( ServidorDAO.class).findByPrimaryKey( servidor ).getNomeSiape() );
//		}
		
		// 15  -  Departamento
		if ( filtradoPorDepartamento ) {
			if ( departamento == 0 )
				parametrosEscolhidos.put( "15 - Departamento", "Todos" );
			else
				parametrosEscolhidos.put( "15 - Departamento", getDAO( UnidadeDao.class ).findByPrimaryKey( departamento, Unidade.class ).getNomeMunicipio() );
		}
		
		// 16  -  Situacao usu�rio
		if ( filtradoPorSituacaoUsuario ) {
			if ( situacaoUsuarioBiblioteca != null )
				parametrosEscolhidos.put("16 - Situa��o do Usu�rio", situacaoUsuarioBiblioteca.getDescricaoResumida());
			else
				parametrosEscolhidos.put("16 - Situa��o do Usu�rio", "Todas");
		}
		
		// 17  -  Tipo de Emprestimos
		if ( filtradoPorTipoDeEmprestimo ) {
			if ( tipoDeEmprestimo > 0 )
				parametrosEscolhidos.put("17 - Tipo de Empr�stimo", getDAO(TipoEmprestimoDao.class).findByPrimaryKey(tipoDeEmprestimo, TipoEmprestimo.class).getDescricao() );
			else
				parametrosEscolhidos.put("17 - Tipo de Empr�stimo", "Todos");
		}
		
		// 18 -  Ano
		if (( filtradoPorAno && !!! escolherEntrePeriodoEAno ) || ( filtradoPorAno && escolherEntrePeriodoEAno && filtrarTempoPorAno ) ) {
			parametrosEscolhidos.put( "18 - Ano", "" + ano );
		}
		
		// 19 -  Per�odo
		if ((filtradoPorPeriodo && ! escolherEntrePeriodoEAno) || (filtradoPorPeriodo && escolherEntrePeriodoEAno && ! filtrarTempoPorAno) ) {
			
			if(inicioPeriodo != null && fimPeriodo != null){
				String p = CalendarUtils.format( inicioPeriodo, "dd/MM/yyyy" ) + " at� " + CalendarUtils.format( fimPeriodo, "dd/MM/yyyy" );
				parametrosEscolhidos.put("19 - Per�odo", p);
			}
			
			if(inicioPeriodo == null && fimPeriodo != null){
				String p = " at� " +CalendarUtils.format( fimPeriodo, "dd/MM/yyyy" );
				parametrosEscolhidos.put("19 - Per�odo", p);
			}
			
			if(inicioPeriodo != null && fimPeriodo == null){
				String p = "A partir de : "+CalendarUtils.format( inicioPeriodo, "dd/MM/yyyy" );
				parametrosEscolhidos.put("19 - Per�odo", p);
			}
			
		}
		
		// 20 -  M�s
		if ( filtradoPorMes ) {
			parametrosEscolhidos.put( "20 - M�s", getMes(mes-1) );
		}
		
		// 21 -  Turno
		if ( filtradoPorTurno ) {
			if ( turno == ConstantesRelatorioBiblioteca.Turnos.TODOS.getValor() ) parametrosEscolhidos.put("21 - Turno", ConstantesRelatorioBiblioteca.Turnos.TODOS.getDescricao());
			else if ( turno == ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor() ) parametrosEscolhidos.put("21 - Turno", ConstantesRelatorioBiblioteca.Turnos.MANHA.getDescricao());
			else if ( turno == ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor() ) parametrosEscolhidos.put("21 - Turno", ConstantesRelatorioBiblioteca.Turnos.TARDE.getDescricao());
			else if ( turno == ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor() ) parametrosEscolhidos.put("21 - Turno", ConstantesRelatorioBiblioteca.Turnos.NOITE.getDescricao());
			else { throw new IllegalArgumentException("Turno inv�lido."); }
		}
		
		// 22 -  Tipo Tombamento
		if ( filtradoPorTipoDeTombamento ) {
			if ( tipoDeTombamento == TOMBAMENTO_COMPRA )
				parametrosEscolhidos.put("22 - Tipo de Tombamento", "Compra");
			else if ( tipoDeTombamento == TOMBAMENTO_DOACAO )
				parametrosEscolhidos.put("22 - Tipo de Tombamento", "Doa��o");
			else
				parametrosEscolhidos.put("22 - Tipo de Tombamento", "Todos");
		}
		
		// 23 -  Modalidade de aquisi��o 
		if ( filtradoPorModalidadeAquisicao ) {
			if ( tipoModalidadeEscolhida == MODALIDADE_AQUISICAO_COMPRA )
				parametrosEscolhidos.put("23 - Modalidade de Aquisi��o", "Compra");
			else if ( tipoModalidadeEscolhida == MODALIDADE_AQUISICAO_DOACAO )
				parametrosEscolhidos.put("23 - Modalidade de Aquisi��o", "Doa��o");
			else if ( tipoModalidadeEscolhida == MODALIDADE_AQUISICAO_SUBSITITUICAO )
				parametrosEscolhidos.put("23 - Modalidade de Aquisi��o", "Substitui��o");
			else if ( tipoModalidadeEscolhida == MODALIDADE_AQUISICAO_INDEFINIDA )
				parametrosEscolhidos.put("23 - Modalidade de Aquisi��o", "Indefinida");
			else
				parametrosEscolhidos.put("22 - Modalidade de Aquisi��o", "Todas");
		}
		
		// 24 -  Tipo Relat�rio
		if ( filtradoPorBasicoVsDetalhado ) {
			if ( mostrarDetalhado )
				parametrosEscolhidos.put("24 - Tipo de Relat�rio", "Detalhado");
			else
				parametrosEscolhidos.put("24 - Tipo de Relat�rio", "B�sico");
		}
		
		// 25 - Formato do Relat�rio
		if(filtradoPorFormaRelatorio){
			if ( formatoRelatorio == SINTETICO )
				parametrosEscolhidos.put("25 - Formato do Relat�rio", "Sint�tico");
			else
				parametrosEscolhidos.put("25 - Formato do Relat�rio", "Anal�tico");
		}
		
		
		// 26 -  Dado Booleano
		if ( filtradoPorBooleano ) {
			if(dadoBooleano)
				parametrosEscolhidos.put( "26 - "+descricaoDadoBooleano, "SIM" );
			else
				parametrosEscolhidos.put( "26 - "+descricaoDadoBooleano, "N�O" );
		}
		
		// 27 -  1� Agrupamento
		if(filtradoPorAgrupamento1){
			parametrosEscolhidos.put("27 - Agrupado por ", agrupamento1.alias);
		}
		
		// 27 -  2� Agrupamento
		if(filtradoPorAgrupamento2 && agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO){
			parametrosEscolhidos.put("27 - Agrupado por ", parametrosEscolhidos.get("27 - Agrupado por ")+" e "+agrupamento2.alias);
		}
		
		// 28 - Biblioteca
		if ( filtradoPorOrdenacao ) {
			parametrosEscolhidos.put("28 - Ordenado por", OrdenacaoRelatoriosBiblioteca.getOrdenacao(ordenacao).getAlias());
		}
		
		// 29 -  Informa��es pagina��o
		if(utilizandoPaginacao){
			parametrosEscolhidos.put("29 - Resultados ", "P�gina "+paginaAtual +" de "+ numeroTotalDePaginas +", registros "+registroInicial+" ao "+registroFinal+" de "+numeroTotalDeRegistros);
		}
		
	}
	
	
	/**
	 * <p>Calcula o n�mero total de p�gina a partir da quantidade de registros retornado pela consulta.<p>
	 * 
	 * <p>Utilizando nos relat�rios que utilizando pagina��o</p>
	 *  
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 */
	protected void calculaValoresPaginacao(){
		numeroTotalDePaginas =
			numeroTotalDeRegistros / quantidadeRegistrosPorPagina +
			(numeroTotalDeRegistros % quantidadeRegistrosPorPagina == 0 ? 0 : 1);
		
		paginaAtual = getParameterInt("pagina_atual_relatorio_biblioteca", 1);
		
		registroInicial = (paginaAtual - 1) * quantidadeRegistrosPorPagina + 1;
		registroFinal   = registroInicial + quantidadeRegistrosPorPagina - 1;
		if ( registroFinal > numeroTotalDeRegistros )
			registroFinal = numeroTotalDeRegistros;
		
	}
	
	
	
	
	/**
	 * 
	 * M�todo que verifica se o agrupamento 1 � obrigrat�rio
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca</li>
	 *   </ul>
	 *
	 * @return
	 */
    public final boolean isAgrupamento1Obrigatorio() throws DAOException{
    	if(filtradoPorAgrupamento1 && getAgrupamentos1ComboBox() != null){
    		
    		Collection<SelectItem> temp = getAgrupamentos1ComboBox();
    		
    		for (SelectItem selectItem : temp) {
    			if ( ((Integer)selectItem.getValue()).equals(AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.valor) ){
    				return false;
    			}
			}
    	
    		return true;
    		
    	}else
    		return false;
    }
	
    /**
	 * 
	 * M�todo que verifica se o agrupamento 2 � obrigrat�rio
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca</li>
	 *   </ul>
	 *
	 * @return
	 */
    public final boolean isAgrupamento2Obrigatorio() throws DAOException{
    	if(filtradoPorAgrupamento2 && getAgrupamentos2ComboBox() != null){
    		
    		Collection<SelectItem> temp = getAgrupamentos2ComboBox();
    		
    		for (SelectItem selectItem : temp) {
    			if ( ((Integer)selectItem.getValue()).equals(AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO.valor) ){
    				return false;
    			}
			}
    	
    		return true;
    		
    	}else
    		return false;
    }
	
    
	/**
	 * <p>Redireciona para a p�gina padr�o dos relat�rios.</p>
	 * 
	 * <p>Este m�todo <strong>n�o</strong> � chamado por JSPs.
	 */
	public String telaPadraoFiltrosRelatoriosBiblioteca(){
		return forward(PAGINA_PADRAO_DE_FILTROS);
	}
	
	
	
	
	/**
	 * <p>M�todo que retorna as bibliotecas do sistema em forma de SelectItem's.
	 * <p>M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li>
	 *   </ul>
	 */
	public Collection<SelectItem> getBibliotecasCombo () throws DAOException {
		
		if(bibliotecasComboBox == null){
			BibliotecaDao dao = null;
			try{
				dao = getDAO(BibliotecaDao.class);
				bibliotecasComboBox = dao.findAllBibliotecasInternasAtivas();
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return toSelectItems(bibliotecasComboBox, "id", "descricao");
		
	
	}
	
	/**
	 * <p>M�todo que retorna as bibliotecas do sistema em forma de SelectItem's.
	 * <p>M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li>
	 *   </ul>
	 */
	public Collection<SelectItem> getSituacoesMateriaisCombo () throws DAOException {
		
		if(situacaoesComboBox == null){
				SituacaoMaterialInformacionalDao dao = null;
			try{
				dao = getDAO(SituacaoMaterialInformacionalDao.class);
				situacaoesComboBox = dao.findAllSituacoesAtivasNaoBaixa();
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return toSelectItems(situacaoesComboBox, "id", "descricao");
		
		
	}
	
	

	/**
	 * Retorna as poss�veis categorias de usu�rio.
	 * <p>M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li>
	 *   </ul>
	 */
	public Collection<SelectItem> getVinculosDeUsuarioCombo() {
		
		Collection<SelectItem> vinculos = new ArrayList<SelectItem>();
		
		VinculoUsuarioBiblioteca[] arryVinculos =  VinculoUsuarioBiblioteca.getVinculosPodeRealizarEmprestimos();
		
		for (VinculoUsuarioBiblioteca vinculoUsuarioBiblioteca : arryVinculos) {
			vinculos.add( new SelectItem(vinculoUsuarioBiblioteca.getValor(), vinculoUsuarioBiblioteca.getDescricao()));
		}
		
		return vinculos;
	}
	
	/**
	 * Retorna as poss�veis situa��es de servidor.
	 * <p>M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li>
	 *   </ul>
	 */
	public Collection<SelectItem> getSituacoesDeServidorCombo() {
		
		Collection<SelectItem> vinculos = new ArrayList<SelectItem>();

		vinculos.add(new SelectItem(Ativo.SERVIDOR_ATIVO, "Ativo"));
		vinculos.add(new SelectItem(Ativo.APOSENTADO, "Aposentado"));
		vinculos.add(new SelectItem(Ativo.CEDIDO, "Cedido"));
		
		return vinculos;
	}
	
	
	/**
	 * Retorna as poss�veis situa��es de um usu�rio em rela��o a empr�stimo. Exclui
	 * <em>Usu�rios sem pend�ncias</em> se a flag for setada para tal.
	 * <p>M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li>
	 *   </ul>
	 */
	public Collection<SelectItem> getSituacoesDoUsuarioCombo() {
		Set<SituacaoUsuarioBiblioteca> s = new HashSet<SituacaoUsuarioBiblioteca>();
		s.addAll(SituacaoUsuarioBiblioteca.POSSIVEIS_SITUACAO_DO_USUARIO);
		
		if ( ! incluirUsuariosSemPendencias )
			s.remove( SituacaoUsuarioBiblioteca.SEM_PENDENCIA );
		
		return toSelectItems( s, "valor", "descricaoResumida");
	}
	
	/**
	 * Retorna todas as pol�ticas de empr�stimos ativas.
	 * <p>M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li>
	 *   </ul>
	 */
	public Collection<SelectItem> getTiposDeEmprestimoCombo() throws DAOException {
		
		if(tiposEmprestimosComboBox == null){
			TipoEmprestimoDao dao = null;
			try{
				dao = getDAO(TipoEmprestimoDao.class);
				tiposEmprestimosComboBox = dao.findAllAtivos(TipoEmprestimo.class, "descricao");
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return toSelectItems(tiposEmprestimosComboBox, "id", "descricao");
	}
	
	/// Constantes para serem usadas nos combo box das bibliotecas na p�gina de filtro dos relat�rios ///
	
	
	/**
	 * Retorna as classifica��es bibliogr�ficas utilizada na biblitoeca no formato de uma lista 
	 * de SelectItem para serem utilizados nos filtros dos relat�rios da biblioteca.
	 */
	public Collection<SelectItem> getClassificacoesBibliograficasComboBox() throws DAOException {
		if(classificacoesBibliograricas == null ){
			classificacoesBibliograricas = new ArrayList<SelectItem>();
			
			if(ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao1())
				classificacoesBibliograricas.add( new SelectItem(FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1.getValor(), ClassificacoesBibliograficasUtil.getDescricaoClassificacao1() ));
			
			if(ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao2())
				classificacoesBibliograricas.add( new SelectItem(FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2.getValor(), ClassificacoesBibliograficasUtil.getDescricaoClassificacao2() ));
			
			if(ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao3())
				classificacoesBibliograricas.add( new SelectItem(FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3.getValor(), ClassificacoesBibliograficasUtil.getDescricaoClassificacao3() ));
		}
		return classificacoesBibliograricas;
	}
	
	
	/**
	 * Retorna a descri��o classifica��o escolhida.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioMateriaisTrabalhadosOperadorSintetico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public final String getDescricaoClassificacaoEscolhida(){
		
		if ( classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1 )
			descricaoClassificacaoEscolhida = ClassificacoesBibliograficasUtil.getDescricaoClassificacao1();
		else if ( classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2  )
			descricaoClassificacaoEscolhida =  ClassificacoesBibliograficasUtil.getDescricaoClassificacao2();
		else if ( classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3  )
			descricaoClassificacaoEscolhida =  ClassificacoesBibliograficasUtil.getDescricaoClassificacao3();
		
		return descricaoClassificacaoEscolhida;
	}
	
	/**
	 * Retorna a classifica��o escolhida foi a primeira.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioMateriaisTrabalhadosOperadorSintetico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public final boolean isClassificacao1Escolhida(){
		if ( classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1 )
			return true;
		else
			return false;
	}
	
	
	/**
	 * Retorna a classifica��o escolhida foi a segunda.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioMateriaisTrabalhadosOperadorSintetico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public final boolean isClassificacao2Escolhida(){
		if ( classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2 )
			return true;
		else
			return false;
	}
	
	
	/**
	 * Retorna a classifica��o escolhida foi a terceira.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioMateriaisTrabalhadosOperadorSintetico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public final boolean isClassificacao3Escolhida(){
		if ( classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3 )
			return true;
		else
			return false;
	}
	
	
//	/**
//	 * Retorna as grandes �reas CNPq.
//	 * 
//	 * <p>Este m�todo � chamado pelas seguintes JSPs:
//	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorColecaoCNPq.jsp</li></ul>
//	 * </p>
//	 */
//	public Collection<String> getGrandesAreasCNPq() throws DAOException {
//		
//		if (cacheAreasCNPq == null){ // s� busca a primeira vez.
//			
//			
//			AreaConhecimentoCNPqBibliotecaDao dao = null;
//			try {
//				
//				dao = getDAO(AreaConhecimentoCNPqBibliotecaDao.class);
//				
//				
//				Collection <String> descricaoAreas = new ArrayList <String> ();
//				Collection<AreaConhecimentoCnpq> aresTemp = dao.findGrandesAreasCNPqBibliioteca();
//
//				for (AreaConhecimentoCnpq area : aresTemp){
//					descricaoAreas.add(area.getSigla());
//				}
//
//				cacheAreasCNPq = descricaoAreas;
//				
//			} finally {
//				if (dao != null)
//					dao.close();
//			}
//		}
//
//		return cacheAreasCNPq;
//	}

	/**
	 * M�todo que responde �s requisi��es de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas f�sicas e jur�dicas.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 * </p>
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<Pessoa> autocompleteNomePessoa(Object event) throws DAOException {
		String nome = event.toString(); //Nome do item digitado no autocomplete
		Long cpfCnpj = StringUtils.getNumerosIniciais(nome);
		if (cpfCnpj != null){
			nome = null; //consulta pelo cpf/cnpj apenas
		}

		List<Pessoa> lista = new ArrayList<Pessoa>();
	
		GenericDAO genDAO = null;
		
		try{
			genDAO = getGenericDAO();
		
			if (nome == null){
				//Consulta pelo c�digo
				Pessoa item = genDAO.findByExactField(Pessoa.class, "cpf_cnpj", cpfCnpj, true);
				if (item != null)
					lista.add(item);
			}else{
				lista = getDAO(RelatoriosBibliotecaDao.class).findPessoaByNome(nome);
			}
			
		}finally{
			if(genDAO != null) genDAO.close();
		}

		return lista;
	}

	/**
	 * AJAX - Adiciona uma nova pessoa do filtro de v�rias pessoas.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 * </p>
	 */
	public void adicionarPessoa(ActionEvent evt) {
		try {
			validarAdicionarPessoa();		
			
			variasPessoas.add(String.valueOf(pessoa));
			nomeVariasPessoas.add(nomePessoa);
		} catch (IllegalArgumentException iaex) {
			addMensagemErro(iaex.getMessage());
		}
	}

	/**
	 * M�todo que habilita ou n�o o filtro de situa��o de servidor, com base nas categorias marcadas 
	 * pelo usu�rio.
	 *
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 * </p>
	 * 
	 * @param evt
	 */
	public void selectCategoriaDeUsuario(ValueChangeEvent evt) {
		if (filtradoPorCategoriaDeUsuario) {
			VinculoUsuarioBiblioteca catValue = VinculoUsuarioBiblioteca.getVinculo((Integer)evt.getNewValue());

			if (catValue != VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO
					&& catValue != VinculoUsuarioBiblioteca.DOCENTE) {
				desabilitaFiltroDependenteDeServidor = true;
				
				return;
			}
		} else if (filtradoPorVariasCategoriasDeUsuario) {
			@SuppressWarnings("unchecked")
			List<String> novaLista = (List<String>)evt.getNewValue();
			
			if (!novaLista.isEmpty()) {
				for (String cat : novaLista) {
					VinculoUsuarioBiblioteca catValue = VinculoUsuarioBiblioteca.getVinculo(Integer.parseInt(cat));
					
					if (catValue != VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO
							&& catValue != VinculoUsuarioBiblioteca.DOCENTE) {
						desabilitaFiltroDependenteDeServidor = true;
						
						return;
					}
				}
			} else {
				desabilitaFiltroDependenteDeServidor = true;
				
				return;
			}
		}
		
		desabilitaFiltroDependenteDeServidor = false;
	}
	
	/**
	 * Valida a pessoa rec�m-adicionada.
	 */
	private void validarAdicionarPessoa() {
		if (pessoa == 0) {
			throw new IllegalArgumentException("Usu�rio inv�lido.");
		}
		else if (variasPessoas.contains(String.valueOf(pessoa))) {
			throw new IllegalArgumentException("Este usu�rio j� se encontra na lista.");
		}
	}

	/**
	 * AJAX - Remove uma pessoa do filtro de v�rias pessoas.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 */
	public void removerPessoa(ActionEvent evt) {
		variasPessoas.remove(nomeVariasPessoasDataModel.getRowIndex());
		nomeVariasPessoas.remove(nomeVariasPessoasDataModel.getRowIndex());
	}
	
	//////// Constantes a serem usadas no JSP /////////
	
	public int getClasseClassificacao1() { return FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1.getValor(); }
	public int getClasseClassificacao2() { return FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2.getValor(); }
	public int getClasseClassificacao3() { return FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3.getValor(); }
	
	public int getCtgMatTodos() { return CTGMAT_TODOS; }
	public int getCtgMatExemplares() { return CTGMAT_EXEMPLARES; }
	public int getCtgMatFasciculos() { return CTGMAT_FASCICULOS; }
	public int getCtgMatDigitais() { return CTGMAT_DIGITAIS; }
	
	public int getTombamentoCompra() { return TOMBAMENTO_COMPRA; }
	public int getTombamentoDoacao() { return TOMBAMENTO_DOACAO; }
	public int getTombamentoTodos() { return TOMBAMENTO_TODOS; }

	public int getModalidadeAquisicaoTodas() { return MODALIDADE_AQUISICAO_TODAS; }
	public int getModalidadeAquisicaoIndefinida() { return MODALIDADE_AQUISICAO_INDEFINIDA; }
	public int getModalidadeAquisicaoCompra() { return MODALIDADE_AQUISICAO_COMPRA; }
	public int getModalidadeAquisicaoDoacao() { return MODALIDADE_AQUISICAO_DOACAO; }
	public int getModalidadeAquisicaoSubstituicao() { return MODALIDADE_AQUISICAO_SUBSITITUICAO; }
	
	public int getTurnoTodos() { return ConstantesRelatorioBiblioteca.Turnos.TODOS.getValor(); }
	public int getTurnoManha() { return ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor(); }
	public int getTurnoTarde() { return ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor(); }
	public int getTurnoNoite() { return ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor(); }

	public int getTipoAcervoTodos() { return ConstantesRelatorioBiblioteca.TIPO_ACERVO_TODOS; }
	public int getTipoAcervoTitulos() { return ConstantesRelatorioBiblioteca.TIPO_ACERVO_TITULOS; }
	public int getTipoAcervoMateriais() { return ConstantesRelatorioBiblioteca.TIPO_ACERVO_MATERIAIS; }

	//////// Gets e Sets ////////
	
	public String getTitulo() { return titulo; }
	public void setTitulo(String titulo) { this.titulo = titulo; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }

	public int getCurso() { return curso; }
	public void setCurso(int curso) { this.curso = curso; }

	public List<String> getTiposDeMaterial() { return tiposDeMaterial; }
	public void setTiposDeMaterial(List<String> tiposDeMaterial) { this.tiposDeMaterial = tiposDeMaterial; }

	public List<String> getColecoes() {return colecoes;}
	public void setColecoes(List<String> colecoes) {this.colecoes = colecoes;}

	public Integer getTipoDeMaterial() { return tipoDeMaterial; }
	public void setTipoDeMaterial(Integer tipoDeMaterial) { this.tipoDeMaterial = tipoDeMaterial; }
	
	public Date getInicioPeriodo() { return inicioPeriodo; }
	public void setInicioPeriodo(Date inicioPeriodo) { this.inicioPeriodo = inicioPeriodo; }

	public Date getFimPeriodo() { return fimPeriodo; }
	
	/**
	 * Ajusta o fim do per�odo definido pelo usu�rio j� com o complemento de tempo final (fim do dia). 
	 * 
	 * @param fimPeriodo
	 */
	public void setFimPeriodo(Date fimPeriodo) { 
		if (fimPeriodo != null) { 
			this.fimPeriodo = CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999);
		} else {
			this.fimPeriodo = null;
		}
	}

	public boolean isFiltradoPorCurso() { return filtradoPorCurso; }
	public void setFiltradoPorCurso(boolean filtradoPorCurso) { this.filtradoPorCurso = filtradoPorCurso; }

	public boolean isFiltradoPorCategoriaDeUsuario() { return filtradoPorCategoriaDeUsuario; }
	public void setFiltradoPorCategoriaDeUsuario( boolean filtradoPorCategoriaDeUsuario) { this.filtradoPorCategoriaDeUsuario = filtradoPorCategoriaDeUsuario; }

	public boolean isFiltradoPorTiposDeMaterial() { return filtradoPorTiposDeMaterial; }
	public void setFiltradoPorTiposDeMaterial(boolean filtradoPorTiposDeMaterial) { this.filtradoPorTiposDeMaterial = filtradoPorTiposDeMaterial; }

	public boolean isFiltradoPorColecoes() {return filtradoPorColecoes;}
	public void setFiltradoPorColecoes(boolean filtradoPorColecoes) {this.filtradoPorColecoes = filtradoPorColecoes;}
	
	public boolean isFiltradoPorTipoDeMaterial() { return filtradoPorTipoDeMaterial; }
	public void setFiltradoPorTipoDeMaterial(boolean filtradoPorTipoDeMaterial) { this.filtradoPorTipoDeMaterial = filtradoPorTipoDeMaterial; }

	public boolean isFiltradoPorPeriodo() { return filtradoPorPeriodo; }
	public void setFiltradoPorPeriodo(boolean filtradoPorPeriodo) { this.filtradoPorPeriodo = filtradoPorPeriodo; }

	public int getValorVinculoDoUsuarioSelecionado() { return valorVinculoDoUsuarioSelecionado; }
	public void setValorVinculoDoUsuarioSelecionado(int valorVinculoDoUsuarioSelecionado) { this.valorVinculoDoUsuarioSelecionado = valorVinculoDoUsuarioSelecionado; }

	public boolean isFiltradoPorAno() { return filtradoPorAno; }
	public void setFiltradoPorAno(boolean filtradoPorAno) { this.filtradoPorAno = filtradoPorAno; }

	public boolean isFiltradoPorDepartamento() { return filtradoPorDepartamento; }
	public void setFiltradoPorDepartamento(boolean filtradoPorDepartamento) { this.filtradoPorDepartamento = filtradoPorDepartamento; }

	public Integer getAno() { return ano; }
	public void setAno(Integer ano) { this.ano = ano; }

	public int getDepartamento() { return departamento; }
	public void setDepartamento(int departamento) { this.departamento = departamento; }
	
	public Map<String, String> getParametrosEscolhidos() { return parametrosEscolhidos; }
	public void setParametrosEscolhidos(Map<String, String> parametrosEscolhidos) { this.parametrosEscolhidos = parametrosEscolhidos; }

	public boolean isFiltradoPorMes() { return filtradoPorMes; }
	public void setFiltradoPorMes(boolean filtradoPorMes) { this.filtradoPorMes = filtradoPorMes; }

	public boolean isFiltradoPorServidor() { return filtradoPorServidor; }
	public void setFiltradoPorServidor(boolean filtradoPorServidor) { this.filtradoPorServidor = filtradoPorServidor; }

	public Integer getMes() { return mes; }
	public void setMes(Integer mes) { this.mes = mes; }

	public int getServidor() { return servidor; }
	public void setServidor(int servidor) { this.servidor = servidor; }

	public String getNomeServidor() { return nomeServidor; }
	public void setNomeServidor(String nomeServidor) { this.nomeServidor = nomeServidor; }

	public boolean isFiltradoPorColecao() { return filtradoPorColecao; }
	public void setFiltradoPorColecao(boolean filtradoPorColecao) { this.filtradoPorColecao = filtradoPorColecao; }

	public Integer getColecao() { return colecao; }
	public void setColecao(Integer colecao) { this.colecao = colecao; }

	public boolean isFiltradoPorBooleano() { return filtradoPorBooleano; }
	public void setFiltradoPorBooleano(boolean filtradoPorBooleano) { this.filtradoPorBooleano = filtradoPorBooleano; }

	public boolean isDadoBooleano() { return dadoBooleano; }
	public void setDadoBooleano(boolean dadoBooleano) { this.dadoBooleano = dadoBooleano; }

	public String getDescricaoDadoBooleano() { return descricaoDadoBooleano; }
	public void setDescricaoDadoBooleano(String descricaoDadoBooleano) { this.descricaoDadoBooleano = descricaoDadoBooleano; }

	public boolean isPermiteTodasBibliotecas() { return permiteTodasBibliotecas; }
	public void setPermiteTodasBibliotecas(boolean permiteTodasBibliotecas) { this.permiteTodasBibliotecas = permiteTodasBibliotecas; }

	public boolean isPermiteTodasBibliotecasSetoriais() { return permiteTodasBibliotecasSetoriais; }
	public void setPermiteTodasBibliotecasSetoriais( boolean permiteTodasBibliotecasSetoriais) {this.permiteTodasBibliotecasSetoriais = permiteTodasBibliotecasSetoriais;}

	public boolean isFiltradoPorSituacaoUsuario() { return filtradoPorSituacaoUsuario; }
	public void setFiltradoPorSituacaoUsuario(boolean filtradoPorSituacaoUsuario) { this.filtradoPorSituacaoUsuario = filtradoPorSituacaoUsuario; }

	public SituacaoUsuarioBiblioteca getSituacaoUsuarioBiblioteca() { return situacaoUsuarioBiblioteca; }
	public void setSituacaoUsuarioBiblioteca(SituacaoUsuarioBiblioteca situacaoUsuarioBiblioteca) { this.situacaoUsuarioBiblioteca = situacaoUsuarioBiblioteca; }

	public List<String> getVariasBibliotecas() { return variasBibliotecas; }
	public void setVariasBibliotecas(List<String> variasBibliotecas) { this.variasBibliotecas = variasBibliotecas; }

	public List<String> getVariasUnidades() { return variasUnidades; }
	public void setVariasUnidades(List<String> variasUnidades) { this.variasUnidades = variasUnidades; }

	public boolean isFiltradoPorVariasBibliotecas() { return filtradoPorVariasBibliotecas; }
	public void setFiltradoPorVariasBibliotecas(boolean filtradoPorVariasBibliotecas) { this.filtradoPorVariasBibliotecas = filtradoPorVariasBibliotecas; }

	public boolean isFiltradoPorVariasUnidades() { return filtradoPorVariasUnidades; }
	public void setFiltradoPorVariasUnidades(boolean filtradoPorVariasUnidades) { this.filtradoPorVariasBibliotecas = filtradoPorVariasUnidades; }

	public boolean isFiltradoPorTipoDeEmprestimo() { return filtradoPorTipoDeEmprestimo; }
	public void setFiltradoPorTipoDeEmprestimo(boolean filtradoPorTipoDeEmprestimo) { this.filtradoPorTipoDeEmprestimo = filtradoPorTipoDeEmprestimo; }

	public int getTipoDeEmprestimo() { return tipoDeEmprestimo; }
	public void setTipoDeEmprestimo(int tipoDeEmprestimo) { this.tipoDeEmprestimo = tipoDeEmprestimo; }

	public List<String> getVariasCategoriasDeUsuario() { return variasCategoriasDeUsuario; }
	public void setVariasCategoriasDeUsuario(List<String> variasCategoriasDeUsuario) { this.variasCategoriasDeUsuario = variasCategoriasDeUsuario; }

	public List<String> getVariasSituacoesDeServidor() { return variasSituacoesDeServidor; }
	public void setVariasSituacoesDeServidor(List<String> variasSituacoesDeServidor) { this.variasSituacoesDeServidor = variasSituacoesDeServidor; }

	public boolean isFiltradoPorVariasCategoriasDeUsuario() { return filtradoPorVariasCategoriasDeUsuario; }
	public void setFiltradoPorVariasCategoriasDeUsuario( boolean filtradoPorVariasCategoriasDeUsuario) { this.filtradoPorVariasCategoriasDeUsuario = filtradoPorVariasCategoriasDeUsuario; }

	public boolean isFiltradoPorVariasSituacoesDeServidor() { return filtradoPorVariasSituacoesDeServidor; }
	public void setFiltradoPorVariasSituacoesDeServidor( boolean filtradoPorVariasSituacoesDeServidor) { this.filtradoPorVariasSituacoesDeServidor = filtradoPorVariasSituacoesDeServidor; }

	public boolean isIncluirUsuariosSemPendencias() { return incluirUsuariosSemPendencias; }
	public void setIncluirUsuariosSemPendencias(boolean incluirUsuariosSemPendencias) { this.incluirUsuariosSemPendencias = incluirUsuariosSemPendencias; }

	public boolean isCampoBibliotecaObrigatorio() { return campoBibliotecaObrigatorio; }
	public void setCampoBibliotecaObrigatorio(boolean campoBibliotecaObrigatorio) { this.campoBibliotecaObrigatorio = campoBibliotecaObrigatorio; }

	public boolean isFiltradoPorTipoClassificacao() { return filtradoPorTipoClassificacao; }
	public void setFiltradoPorTipoClassificacao(boolean filtradoPorTipoClassificacao) { this.filtradoPorTipoClassificacao = filtradoPorTipoClassificacao; }

	public int getTipoclassificacaoEscolhida() { return tipoclassificacaoEscolhida; }
	
	/** Seta o valor da classifica��o escolhida, passada na pelas p�gina JSF e j� configura a classifica��o escolhida a partir desse valor.*/
	public void setTipoclassificacaoEscolhida(int tipoclassificacaoEscolhida) { 
		this.tipoclassificacaoEscolhida = tipoclassificacaoEscolhida; 
		this.classificacaoEscolhida = FiltroClassificacoesRelatoriosBiblioteca.getFiltroClassificacao(tipoclassificacaoEscolhida);
	}

	public boolean isFiltradoPorCtgMaterial() { return filtradoPorCtgMaterial; }
	public void setFiltradoPorCtgMaterial(boolean filtradoPorCtgMaterial) { this.filtradoPorCtgMaterial = filtradoPorCtgMaterial; }

	public boolean isFiltradoPorTurno() { return filtradoPorTurno; }
	public void setFiltradoPorTurno(boolean filtradoPorTurno) { this.filtradoPorTurno = filtradoPorTurno; }
	
	public int getCtgMaterial() { return ctgMaterial; }
	public void setCtgMaterial(int ctgMaterial) { this.ctgMaterial = ctgMaterial; }
	
	public int getTurno() { return turno; }
	public void setTurno(int turno) { this.turno = turno; }

	public int getTipoDeTombamento() { return tipoDeTombamento; }
	public void setTipoDeTombamento(int tipoDeTombamento) { this.tipoDeTombamento = tipoDeTombamento; }

	public boolean isFiltradoPorTipoDeTombamento() { return filtradoPorTipoDeTombamento; }
	public void setFiltradoPorTipoDeTombamento(boolean filtradoPorTipoDeTombamento) { this.filtradoPorTipoDeTombamento = filtradoPorTipoDeTombamento; }

	public boolean isFiltradoPorBasicoVsDetalhado() { return filtradoPorBasicoVsDetalhado; }
	public void setFiltradoPorBasicoVsDetalhado(boolean filtradoPorBasicoVsDetalhado) { this.filtradoPorBasicoVsDetalhado = filtradoPorBasicoVsDetalhado; }

	public boolean isMostrarDetalhado() { return mostrarDetalhado; }
	public void setMostrarDetalhado(boolean mostrarDetalhado) { this.mostrarDetalhado = mostrarDetalhado; }

	public boolean isPermitirTodasCtgMaterial() { return permitirTodasCtgMaterial; }
	public void setPermitirTodasCtgMaterial(boolean permitirTodasCtgMaterial) { this.permitirTodasCtgMaterial = permitirTodasCtgMaterial; }

	public boolean isPermitirDigitalCtgMaterial() { return permitirDigitalCtgMaterial; }
	public void setPermitirDigitalCtgMaterial(boolean permitirDigitalCtgMaterial) { this.permitirDigitalCtgMaterial = permitirDigitalCtgMaterial; }

	public boolean isPermitirTodosTiposDeTombamento() { return permitirTodosTiposDeTombamento; }
	public void setPermitirTodosTiposDeTombamento( boolean permitirTodosTiposDeTombamento) {this.permitirTodosTiposDeTombamento = permitirTodosTiposDeTombamento;}

	public boolean isFiltrarTempoPorAno() { return filtrarTempoPorAno; }
	public void setFiltrarTempoPorAno(boolean filtrarTempoPorAno) { this.filtrarTempoPorAno = filtrarTempoPorAno; }

	public boolean isEscolherEntrePeriodoEAno() { return escolherEntrePeriodoEAno; }
	public void setEscolherEntrePeriodoEAno(boolean escolherEntrePeriodoEAno) { this.escolherEntrePeriodoEAno = escolherEntrePeriodoEAno; }

	public boolean isFiltradoPorAgrupamento1() {return filtradoPorAgrupamento1;}
	public void setFiltradoPorAgrupamento1(boolean filtradoPorAgrupamento1) {this.filtradoPorAgrupamento1 = filtradoPorAgrupamento1;}

	public boolean isFiltradoPorAgrupamento2() {return filtradoPorAgrupamento2;}
	public void setFiltradoPorAgrupamento2(boolean filtradoPorAgrupamento2) {this.filtradoPorAgrupamento2 = filtradoPorAgrupamento2;}

	public boolean isCampoPeriodoObrigatorio() {return campoPeriodoObrigatorio;}
	public void setCampoPeriodoObrigatorio(boolean campoPeriodoObrigatorio) {this.campoPeriodoObrigatorio = campoPeriodoObrigatorio;}

	public boolean isFiltradoPorFormasDocumento() {return filtradoPorFormasDocumento;}
	public void setFiltradoPorFormasDocumento(boolean filtradoPorFormasDocumento) {this.filtradoPorFormasDocumento = filtradoPorFormasDocumento;}

	public boolean isFiltradoPorSituacoesDoMaterial() {return filtradoPorSituacoesDoMaterial;}
	public void setFiltradoPorSituacoesDoMaterial(boolean filtradoPorSituacoesDoMaterial) {this.filtradoPorSituacoesDoMaterial = filtradoPorSituacoesDoMaterial;}

	
	public boolean isFiltradoPorIntervaloClassificacao() {return filtradoPorIntervaloClassificacao;}
	public void setFiltradoPorIntervaloClassificacao(boolean filtradoPorIntervaloClassificacao) {this.filtradoPorIntervaloClassificacao = filtradoPorIntervaloClassificacao;}

	public int getFormatoRelatorio() {return formatoRelatorio;}
	public void setFormatoRelatorio(int formatoRelatorio) {this.formatoRelatorio = formatoRelatorio;}

	public String getClasseInicial() {return classeInicial;}
	public void setClasseInicial(String classeInicial) {this.classeInicial = classeInicial;}

	public String getClasseFinal() {return classeFinal;}
	public void setClasseFinal(String classeFinal) {this.classeFinal = classeFinal;}

	public boolean isFiltradoPorFormaRelatorio() {return filtradoPorFormaRelatorio;}
	public void setFiltradoPorFormaRelatorio(boolean filtradoPorFormaRelatorio) {this.filtradoPorFormaRelatorio = filtradoPorFormaRelatorio;}

	
	/**
	 *  Para acesso dos valores das constantes nas p�gina JSP.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalCNPQ.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorClasse.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorClasseETipoDeMaterial.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorColecao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorColecaoCNPq.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorTipoMaterialCNPq.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 */
	public int getCtgmatExemplares() {
		return CTGMAT_EXEMPLARES;
	}

	/**
	 *  Para acesso dos valores das constantes nas p�gina JSP.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *    <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalCNPQ.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorClasse.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorClasseETipoDeMaterial.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorColecao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorColecaoCNPq.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioTotalPorTipoMaterialCNPq.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getCtgmatFasciculos() {
		return CTGMAT_FASCICULOS;
	}
	
	
	
	/**
	 * Retorna as classes princiais da classifica��o escolhida pelo usu�rio no filtro de classifica��o.
	 * 
	 * Utilizado para interar sobres as classes principais para montar os dados do relat�rio em algunas p�ginas do sistema.
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioCrescimentoPorClassificacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioPeriodicosClasseCrescimentoPeriodo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioConsultasLocaisPorAno.jsp</li>
	 *   </ul>
	 * 
	 */
	public List<String> getClassesPrincipaisClassificacaoEscolhida() throws DAOException{
		
		List<String> classesPrincipaisClassificacaoEscolhida = new ArrayList<String>();
		
		if(classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1){
			classesPrincipaisClassificacaoEscolhida.addAll(ClassificacoesBibliograficasUtil.getClassesPrincipaisClassificacao1());
		}
		
		if(classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2){
			classesPrincipaisClassificacaoEscolhida.addAll(ClassificacoesBibliograficasUtil.getClassesPrincipaisClassificacao2());
		}
		
		if(classificacaoEscolhida == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3){
			classesPrincipaisClassificacaoEscolhida.addAll(ClassificacoesBibliograficasUtil.getClassesPrincipaisClassificacao3());
		}
		
		classesPrincipaisClassificacaoEscolhida.add("Sem Classe");
		
		return classesPrincipaisClassificacaoEscolhida;
	}
	
	
	
	/** 
	 * Retorna as siglas das �reas do CNPq para a biblioteca para integerar nas p�ginas dos relat�rios 
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioCrescimentoPorCNPQ.jsp</li>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/relatorioPeriodicosCNPqCrescimentoPeriodo.jsp</li>
	 *   </ul>
	 * 
	 * @throws DAOException 
	 */
	public List<String> getSiglasAreasCNPqBiblioteca() throws DAOException{
		
		if(siglasAreasCNPqBiblioteca == null || siglasAreasCNPqBiblioteca.size() == 0){
			AreaConhecimentoCNPqBibliotecaDao dao  = null;
			try{
				dao = getDAO(AreaConhecimentoCNPqBibliotecaDao.class);
				siglasAreasCNPqBiblioteca = dao.findSiglasGrandesAreasCNPqBibliioteca();
				siglasAreasCNPqBiblioteca.add("Sem �rea");
			}finally{
				if(dao != null) dao.close();
			}
		}
		
		return siglasAreasCNPqBiblioteca;
	}
	
	

	/** Retorna a descri��o amig�vel do 1� agrupamento. 
	 * @throws DAOException */
	public String getDescricaoAgrupamento1() throws DAOException {
		
		if(this.agrupamento1.isAgrupamentoClassificacaoBibliografica()){
			if(this.agrupamento1.isAgrupamentoClassificacao1())
				return ClassificacoesBibliograficasUtil.getDescricaoClassificacao1();
			if(this.agrupamento1.isAgrupamentoClassificacao2())
				return ClassificacoesBibliograficasUtil.getDescricaoClassificacao2();
			if(this.agrupamento1.isAgrupamentoClassificacao3())
				return ClassificacoesBibliograficasUtil.getDescricaoClassificacao3();
		}else{
			return this.agrupamento1.alias;
		}
		
		return "";
	}
	
	/** Retorna a descri��o amig�vel do 2� agrupamento. 
	 * @throws DAOException */
	public String getDescricaoAgrupamento2() throws DAOException {
		if(this.agrupamento2.isAgrupamentoClassificacaoBibliografica()){
			if(this.agrupamento2.isAgrupamentoClassificacao1())
				return ClassificacoesBibliograficasUtil.getDescricaoClassificacao1();
			if(this.agrupamento2.isAgrupamentoClassificacao2())
				return ClassificacoesBibliograficasUtil.getDescricaoClassificacao2();
			if(this.agrupamento2.isAgrupamentoClassificacao3())
				return ClassificacoesBibliograficasUtil.getDescricaoClassificacao3();
		}else{
			return this.agrupamento2.alias;
		}
		
		return "";
	}

	
	public int getValorAgrupamento1() {
		return valorAgrupamento1;
	}

	/** Seta o valor do agrupamento 1 escolhido,  passado na pelas p�gina JSF e j� configura o agrupamento 1 escolhido a partir desse valor.*/
	public void setValorAgrupamento1(int valorAgrupamento1) {
		this.valorAgrupamento1 = valorAgrupamento1;
		agrupamento1  = AgrupamentoRelatoriosBiblioteca.getAgrupamento(valorAgrupamento1);
	}

	public int getValorAgrupamento2() {
		return valorAgrupamento2;
	}

	/** Seta o valor do agrupamento 1 escolhido,  passado na pelas p�gina JSF e j� configura o agrupamento 1 escolhido a partir desse valor.*/
	public void setValorAgrupamento2(int valorAgrupamento2) {
		this.valorAgrupamento2 = valorAgrupamento2;
		agrupamento2  = AgrupamentoRelatoriosBiblioteca.getAgrupamento(valorAgrupamento2);
	}

	public AgrupamentoRelatoriosBiblioteca getAgrupamento1() {
		return agrupamento1;
	}

	public void setAgrupamento1(AgrupamentoRelatoriosBiblioteca agrupamento1) {
		this.agrupamento1 = agrupamento1;
	}

	public AgrupamentoRelatoriosBiblioteca getAgrupamento2() {
		return agrupamento2;
	}

	public void setAgrupamento2(AgrupamentoRelatoriosBiblioteca agrupamento2) {
		this.agrupamento2 = agrupamento2;
	}

	

	public List<String> getFormasDocumento() {
		return formasDocumento;
	}

	/** Seta as formas de documento escolhidas pelo usu�rio eliminando os ids negativos que n�o s�o formas de documento v�lidas. */
	public void setFormasDocumento(List<String> formasDocumento) {
		
		this.formasDocumento = new ArrayList<String>();
		
		for (String string : formasDocumento) {
			if( Integer.parseInt(string) > 0 )
				this.formasDocumento.add(string);
		}
	}

	public List<String> getSituacoesMaterial() {
		return situacoesMaterial;
	}

	public void setSituacoesMaterial(List<String> situacoesMaterial) {
		this.situacoesMaterial = situacoesMaterial;
	}

	public int getRegistroInicial() {
		return registroInicial;
	}

	public void setRegistroInicial(int registroInicial) {
		this.registroInicial = registroInicial;
	}

	public int getRegistroFinal() {
		return registroFinal;
	}

	public void setRegistroFinal(int registroFinal) {
		this.registroFinal = registroFinal;
	}

	public int getPaginaAtual() {
		return paginaAtual;
	}

	public void setPaginaAtual(int paginaAtual) {
		this.paginaAtual = paginaAtual;
	}

	public int getNumeroTotalDePaginas() {
		return numeroTotalDePaginas;
	}

	public void setNumeroTotalDePaginas(int numeroTotalDePaginas) {
		this.numeroTotalDePaginas = numeroTotalDePaginas;
	}

	public int getNumeroTotalDeRegistros() {
		return numeroTotalDeRegistros;
	}

	public void setNumeroTotalDeRegistros(int numeroTotalDeRegistros) {
		this.numeroTotalDeRegistros = numeroTotalDeRegistros;
	}

	public boolean isPossuiFiltrosObrigatorios() {
		return possuiFiltrosObrigatorios;
	}

	public void setPossuiFiltrosObrigatorios(boolean possuiFiltrosObrigatorios) {
		this.possuiFiltrosObrigatorios = possuiFiltrosObrigatorios;
	}

	public String getCodigoDeBarras() {
		return codigoDeBarras;
	}

	public void setCodigoDeBarras(String codigoDeBarras) {
		this.codigoDeBarras = codigoDeBarras;
	}

	public boolean isFiltradoPorCodigoDeBarras() {
		return filtradoPorCodigoDeBarras;
	}

	public void setFiltradoPorCodigoDeBarras(boolean filtradoPorCodigoDeBarras) {
		this.filtradoPorCodigoDeBarras = filtradoPorCodigoDeBarras;
	}

	public boolean isFiltradoPorPessoa() {
		return filtradoPorPessoa;
	}

	public void setFiltradoPorPessoa(boolean filtradoPorPessoa) {
		this.filtradoPorPessoa = filtradoPorPessoa;
	}

	public int getPessoa() {
		return pessoa;
	}

	public void setPessoa(int pessoa) {
		this.pessoa = pessoa;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public boolean isFiltradoPorTipoDeAcervo() {
		return filtradoPorTipoDeAcervo;
	}

	public void setFiltradoPorTipoDeAcervo(boolean filtradoPorTipoDeAcervo) {
		this.filtradoPorTipoDeAcervo = filtradoPorTipoDeAcervo;
	}

	public Integer getTipoDeAcervo() {
		return tipoDeAcervo;
	}

	public void setTipoDeAcervo(Integer tipoDeAcervo) {
		this.tipoDeAcervo = tipoDeAcervo;
	}

	public boolean isFiltradoPorVariasPessoas() {
		return filtradoPorVariasPessoas;
	}

	public void setFiltradoPorVariasPessoas(boolean filtradoPorVariasPessoas) {
		this.filtradoPorVariasPessoas = filtradoPorVariasPessoas;
	}

	public List<String> getVariasPessoas() {
		return variasPessoas;
	}

	public void setVariasPessoas(List<String> variasPessoas) {
		this.variasPessoas = variasPessoas;
	}

	public boolean isCampoPessoaObrigatorio() {
		return campoPessoaObrigatorio;
	}

	public void setCampoPessoaObrigatorio(boolean campoPessoaObrigatorio) {
		this.campoPessoaObrigatorio = campoPessoaObrigatorio;
	}

	public List<String> getNomeVariasPessoas() {
		return nomeVariasPessoas;
	}

	public void setNomeVariasPessoas(List<String> nomeVariasPessoas) {
		this.nomeVariasPessoas = nomeVariasPessoas;
	}

	public DataModel getNomeVariasPessoasDataModel() {
		return nomeVariasPessoasDataModel;
	}

	public void setNomeVariasPessoasDataModel(DataModel nomeVariasPessoasDataModel) {
		this.nomeVariasPessoasDataModel = nomeVariasPessoasDataModel;
	}

	public boolean isFiltradoPorSegundaOpcaoVariasBiblioteca() {
		return filtradoPorSegundaOpcaoVariasBiblioteca;
	}

	public void setFiltradoPorSegundaOpcaoVariasBiblioteca(
			boolean filtradoPorSegundaOpcaoVariasBiblioteca) {
		this.filtradoPorSegundaOpcaoVariasBiblioteca = filtradoPorSegundaOpcaoVariasBiblioteca;
	}

	public boolean isCampo2BibliotecaObrigatorio() {
		return campo2BibliotecaObrigatorio;
	}

	public void setCampo2BibliotecaObrigatorio(boolean campo2BibliotecaObrigatorio) {
		this.campo2BibliotecaObrigatorio = campo2BibliotecaObrigatorio;
	}

	public List<String> getVariasBibliotecas2() {
		return variasBibliotecas2;
	}

	public void setVariasBibliotecas2(List<String> variasBibliotecas2) {
		this.variasBibliotecas2 = variasBibliotecas2;
	}

	public String getDescricaoFiltro1Bibliotecas() {
		return descricaoFiltro1Bibliotecas;
	}

	public void setDescricaoFiltro1Bibliotecas(String descricaoFiltro1Bibliotecas) {
		this.descricaoFiltro1Bibliotecas = descricaoFiltro1Bibliotecas;
	}

	public String getDescricaoFiltro2Bibliotecas() {
		return descricaoFiltro2Bibliotecas;
	}

	public void setDescricaoFiltro2Bibliotecas(String descricaoFiltro2Bibliotecas) {
		this.descricaoFiltro2Bibliotecas = descricaoFiltro2Bibliotecas;
	}

	public boolean isCampoIntervaloClassificacaoObrigatorio() {
		return campoIntervaloClassificacaoObrigatorio;
	}

	public void setCampoIntervaloClassificacaoObrigatorio(boolean campoIntervaloClassificacaoObrigatorio) {
		this.campoIntervaloClassificacaoObrigatorio = campoIntervaloClassificacaoObrigatorio;
	}

	public int getTipoModalidadeEscolhida() {
		return tipoModalidadeEscolhida;
	}

	public void setTipoModalidadeEscolhida(int tipoModalidadeEscolhida) {
		this.tipoModalidadeEscolhida = tipoModalidadeEscolhida;
	}

	public boolean isFiltradoPorModalidadeAquisicao() {
		return filtradoPorModalidadeAquisicao;
	}

	public void setFiltradoPorModalidadeAquisicao(boolean filtradoPorModalidadeAquisicao) {
		this.filtradoPorModalidadeAquisicao = filtradoPorModalidadeAquisicao;
	}

	public boolean isUtilizandoPaginacao() {
		return utilizandoPaginacao;
	}

	public void setUtilizandoPaginacao(boolean utilizandoPaginacao) {
		this.utilizandoPaginacao = utilizandoPaginacao;
	}

	public boolean isDesabilitaFiltroSituacaoDeServidor() {
		return (filtradoPorCategoriaDeUsuario || filtradoPorVariasCategoriasDeUsuario) && desabilitaFiltroDependenteDeServidor;
	}

	public FiltroClassificacoesRelatoriosBiblioteca getClassificacaoEscolhida() {
		return classificacaoEscolhida;
	}

	public void setClassificacaoEscolhida(FiltroClassificacoesRelatoriosBiblioteca classificacaoEscolhida) {
		this.classificacaoEscolhida = classificacaoEscolhida;
	}

	public boolean isFiltradoPorOrdenacao() {
		return filtradoPorOrdenacao;
	}

	public void setFiltradoPorOrdenacao(boolean filtradoPorOrdenacao) {
		this.filtradoPorOrdenacao = filtradoPorOrdenacao;
	}

	
	public Integer getOrdenacao() {return ordenacao;}
	public void setOrdenacao(Integer ordenacao) {this.ordenacao = ordenacao;}
	public Collection<SelectItem> getOrdenacaoItens() {return ordenacaoItens;}
	public void setOrdenacaoItens(Collection<SelectItem> ordenacaoItens) {this.ordenacaoItens = ordenacaoItens;}
	
	
}
