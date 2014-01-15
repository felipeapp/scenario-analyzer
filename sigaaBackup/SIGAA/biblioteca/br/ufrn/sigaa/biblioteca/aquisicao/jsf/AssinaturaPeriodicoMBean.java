/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 19/01/2009
 */
package br.ufrn.sigaa.biblioteca.aquisicao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.aquisicao.dominio.CampoOrdenacaoConsultaAssinatura;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoCadastraAlteraAssinaturaDePeriodico;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FrequenciaPeriodicos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.AutorizaTransferenciaFasciculosEntreAssinaturasMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAssociaAssinaturaTransferenciaFasciculos;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *  <p>MBean que gerência o ciclo de vida de uma assinatura de periídicos do sistema (buscar, criar, alterar, remover).</p>
 *  
 *  <p>Responsável pela busca padrão das assinatura utilizada pelos demais casos de uso do sistema.</p>
 *
 * @author Jadson
 * @since 19/01/2009
 * @version 1.0 Criação da classe
 *
 */
@Component("assinaturaPeriodicoMBean")
@Scope("request")
public class AssinaturaPeriodicoMBean extends SigaaAbstractController<Assinatura> {
	
	/** Página com a busca/lista de assinaturas e o link para criação de uma nova assinatura.  */
	public static final String PAGINA_BUSCA_ASSINATURA_PERIODICO = "/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp";
	
	/** Página com o formulário para criação de uma nova assinatura. */
	public static final String PAGINA_CRIA_ASSINATURA_PERIODICO = "/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp";
	
	/** Se na hora de incluir um fascículo, o título não tiver nenhuma assinatura associada a ele, o usuário
	 * deverá buscar essa assinatura para associar ao título. */
	public static final String PAGINA_BUSCA_ASSINATURAS_INCLUSAO_FASCICULOS = "/biblioteca/processos_tecnicos/catalogacao/paginaBuscaAssinaturasInclusaoFasciculos.jsp";
	
	/** Assinatura usada como modelo para as buscas. */
	private Assinatura assinaturaModeloBuscas;
	
	/** Guarda o resultados da busca de assinatura padrão do sistema. */
	private List<Assinatura> assinaturasBuscadas = new ArrayList<Assinatura>();
	
	/** Indica se está alterando ou criando. pois tanto a alteração como a criação ocorrem no mesmo formulário. */
	private boolean alteracao = false;
	
	
	///////////////////////////////
	// Se a busca é para ser feita por alguma campo ou não. Os valores são determinados nos checkbox da página:
	
	/** Informa se deve filtrar por título. */
	private boolean buscarTitulo;
	
	/** Informa se deve filtrar por código da assinatura. */
	private boolean buscarCodigo;
	
	/** Informa se deve filtrar por unidade de destino. */
	private boolean buscarUnidadeDestino;
	
	/** Informa se deve filtrar por modalidade de aquisição. */
	private boolean buscarModalidadeAquisicao;
	
	/** Informa se deve filtrar pela Periodicidade de assinatura. */
	private boolean buscarPeriodicidade;
	
	/** Informa se deve filtrar por nacionalidade. */
	private boolean buscarInternacional;
	
	/** Informa se deve filtrar por ISSN. */
	private boolean buscarIssn;
	
	/////////////////////////////////
	
	/** Guarda o valor do campo escolhido pelo usuário para ordenar a consulta */
	protected int valorCampoOrdenacao;

	
	/*
	 * Usados no caso de uso em que o usuário vai criar uma assinatura para associar aos fascículos
	 * transferidos sem assinaturas.
	 */
	
	/** Biblioteca de destino da assinatura. */
	private int idBibliotecaDestino;
	/** Título à qual a assinatura será associada. */
	private int idTituloDoFasciculos;
	/** O título do título catalográfico associado ao fascículo. */
	private String tituloDoTituloDoFasciculo;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	 /** Algumas assinaturas mesmo sendo de compra o sistema deve gerar o código, pois ninguém tem esse código. */
	private boolean gerarCodigoAssinatuaCompra;

	
	
	/** Guarda a lista de bibliotecas que vão ser exibidas no campoBox*/
	private Collection <Biblioteca> bibliotecasCombo;
	
	/** Guarda a lista de frequências que vão ser exibidas no campoBox*/
	private Collection <FrequenciaPeriodicos> frequenciasCombo; 
	
	
	/**
	 * MBean que chamou a pesquisa no acervo para selecionar uma assinatura
	 */
	private PesquisarAcervoAssinaturas mbeanChamador;
	
	
	/**  A pesquisa mais básica sem nenhuma operações especial */
	public static final int PESQUISA_NORMAL = 0;
	
	/**
	 * Operação utilizada qual se deseja selecionar uma assinatura para utilizar em outro caso de uso.
	 * @see {@link RegistraChegadaFasciculoMBean}.
	 */
	public static final int PESQUISA_SELECIONA_UMA_ASSINATURA = 1; 
	
	
	/**
	 * Operação utilizada para selecionar uma assinatura de outro caso de uso, porém aqui só é realizado a busca em assinaturas sem Títulos associados
	 * @see {@link AssociaAssinaturaATituloMBean}.
	 */
	public static final int PESQUISA_SELECIONA_UMA_ASSINATURA_SEM_TITULO = 2; 
	
	
	/**
	 * Operação especial utilizada para criar uma asssinatura e ao mesmo tempo associa-la a uma transferência pedente de fascículos.
	 * @see {@link AutorizaTransferenciaFasciculosEntreAssinaturasMBean}.
	 */
	public static final int PESQUISA_SELECIONA_UMA_ASSINATURA_TRANSFERENCIA_FASCICULOS = 3;
	
	
	
	/** Define a opção que está sendo utilizada no momento*/
	private int operacao = -1;
	
	
	/** Indica se deve buscar as assinaturas somente na unidade onde o usuário tem permissão.*/
	private boolean apenasUnidadePermissaoUsuario = false;
	
	
	/**
	 * Construtor do Bean
	 */
	public AssinaturaPeriodicoMBean(){
		if (this.obj == null)   // Primeira vez
			iniciaDadosFormulario();
	}
	
	
	/**
	 *   Inicia os casos de uso de listar, alterar e criar assinatura.<br/><br/>
	 * 
	 *   Redireciona para a página de listagem na qual o usuário pode alterar as existentes ou
	 *   criar novas
	 *
	 *   Chamado a partir da página: /sigaa.war/biblioteca/aquisicao.jsp
	 */
	public String iniciarPesquisaAssinaturas() throws ArqException{
		
		operacao = PESQUISA_NORMAL;
		
		apenasUnidadePermissaoUsuario = false;
		
		return telaBuscaAssinaturas();
		
	}
	
	/**
	 * Inicia a pesquisa para escolher uma assunatura do acervo. Utilizado quando o caso de uso da busca de assinatura é chamado a partir de outro caso de uso
	 * 
	 * <br/><br/>   
	 * Método não invocado por nenhuma jsp.
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarPesquisaSelecionarAssinatura(PesquisarAcervoAssinaturas mbeanChamador){
	
		this.mbeanChamador = mbeanChamador;
		
		operacao = PESQUISA_SELECIONA_UMA_ASSINATURA;
		
		return telaBuscaAssinaturas();
		
	}
	
	
	/**
	 * Inicia a pesquisa para escolher uma assunatura do acervo. Utilizado quando o caso de uso da busca de assinatura é chamado a partir de outro caso de uso
	 * 
	 * <br/><br/>   
	 * Método não invocado por nenhuma jsp.
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarPesquisaSelecionarAssinaturaSemTitulos(PesquisarAcervoAssinaturas mbeanChamador){
	
		this.mbeanChamador = mbeanChamador;
		
		operacao = PESQUISA_SELECIONA_UMA_ASSINATURA_SEM_TITULO;
		
		return telaBuscaAssinaturas();
		
	}
	
	
	/**
	 *   Inicia o caso de uso de criar uma nova assinatura de periódicos para os fascículos que foram
	 * transferidos sem a biblioteca destino possuir a assinatura.
	 *   <p><i>(Ao terminar esse caso de uso deve-se retornar para a página na qual o usuário
	 *   autoriza a transferência dos fascículos)</i></p>
	 *
	 *   <br/><br/>   
	 *   Método não invocado por nenhuma jsp.
	 *   
	 *   @see AutorizaTransferenciaFasciculosEntreAssinaturasMBean
	 */
	public String iniciarPesquisaCriacaoAssinaturaFasciculosTransferidos(PesquisarAcervoAssinaturas mbeanChamador, 
			int idBibliotecaDestino, int idTituloDoFasciculos, String tituloDoTituloDoFasciculo) throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		this.mbeanChamador = mbeanChamador;
		operacao = PESQUISA_SELECIONA_UMA_ASSINATURA_TRANSFERENCIA_FASCICULOS;
		
		assinaturaModeloBuscas.setTitulo(tituloDoTituloDoFasciculo);
		assinaturaModeloBuscas.setUnidadeDestino(new Biblioteca(idBibliotecaDestino));
		
		buscarTitulo = true;
		buscarUnidadeDestino = true;
		
		
		this.idBibliotecaDestino = idBibliotecaDestino;
		this.idTituloDoFasciculos = idTituloDoFasciculos;
		this.tituloDoTituloDoFasciculo = tituloDoTituloDoFasciculo;
		
		
		return buscarAssinaturas();
	}
	
	
	
	
	
	///////// Implementa o fluxo para os outroas casos de uso que utilizam a busca de assinaturas //////////
	
	
	/**
	 *    <p>Retorna o fluxo do caso de uso para o mBean que chamou a busca no acervo </p>
	 *  
	 *    <p> O Mbean que deseja realizar essa operação tem que implementar {@link PesquisarAcervoAssinaturas} </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String selecionouAssinatura() throws ArqException{
		
		if(isPesquisaSelecionaAssinatura() || isPesquisaSelecionaAssinaturaSemTitulo() || isPesquisaSelecionaAssinaturaTransferenciaFasciculos() ){
		
			int idAssinatura  = getParameterInt("idAssinatura");
			Assinatura assinatura = new Assinatura(idAssinatura);
			mbeanChamador.setAssinatura(assinatura);
			return mbeanChamador.selecionaAssinatura();
		}
		
		return null; // Não era para chegar aqui, porque só existem duas opções de busca que seleciona a assinatura
	}
	
	
	/**
	 * Indica que o caso de uso que chamou a pesquisa de assinaturas vai permitir voltar da tela de 
	 * pesquisa de assinaturas para a tela anterior.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *    
	 *   </ul>
	 *
	 * @return
	 */
	public boolean isUtilizandoBotaoVoltar(){
		if(mbeanChamador != null)
			return mbeanChamador.isUtilizaVoltarBuscaAssinatura();
		else
			return false;
	}
	
	
	
	/**
	 *  <p> Realiza a função do botão voltar quando a busca é chamada por outro caso de uso do sistema. </p>
	 *  <p> <i> ( implementação desse método deve ser realizada por Mbean que chamar esse caso de uso, pois só ele sabe para onde deve voltar ) </i> </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *   </ul>
	 */
	public String voltarBusca() throws ArqException {
		if(mbeanChamador != null && isUtilizandoBotaoVoltar() )
			return mbeanChamador.voltarBuscaAssinatura();
		else
			return null;
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
//	/**
//	 *   Inicia os casos de uso de listar, alterar e criar assinatura.<br/><br/>
//	 * 
//	 *   Redireciona para a página de listagem na qual o usuário pode alterar as existentes ou
//	 *   criar novas
//	 *
//	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao.jsp
//	 */
//	public String cancelarCriacaoAssinatura() throws ArqException{
//		
//		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
//		
//		limpaOperacoesPaginaBuscaAssinatura();
//		//alteracao = true;
//		//buscaAssinaturaCriacaoAlteracao = true;
//		
//		return telaBuscaAssinaturas();
//		
//	}
	
	
	
	/**
	 * Limpa as operações que podem ser realizadas pelo usuário na busca de assinaturas
	 */
	private void limpaOperacoesPaginaBuscaAssinatura(){
		alteracao = false;
	}
	
	
	
	/**
	 *    Método que verifica se o usuário escolheu criar uma assinatura de compra ou doação porque
	 *    algumas validações devem ser feitas na tela.<br/><br/>
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
	 */
	public void verificaAlteracaoModalidadeAquisicao(ValueChangeEvent evt){
		
		this.obj.setModalidadeAquisicao( new Short(""+evt.getNewValue()) );
		
	}
	
	

	/**
	 *    Método que verifica se o usuário escolheu que vai digitar o código da assinatura ou
	 * o sistema vai gerar esse código, mesmo a assinatura sendo de compra.<br/><br/>
	 *
	 *    Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
	 */
	public void verificaUsuarioSelecionouGerarCodigo(ValueChangeEvent evt){
		
		this.gerarCodigoAssinatuaCompra = (Boolean) evt.getNewValue() ;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/* *********************************************************************************************
	 * 
	 *  Métodos especiais para criação de assinatura a partir da autorização da transferencia
	 *  a parte de criação de assinatura para transferencia de periodicos
	 *  
	 *************************************************************************************************/
	
	

	
	
	/**
	 * Inicia o caso de uso de criar uma nova a assinatura para atribuir aos fascículos transferidos sem assinatura.
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp
	 */
	public String preCriacaoAssinaturaParaTransferenciaFasciculos() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.ASSOCIA_ASSINATURA_TRANSFERENCIA_FASCIULOS);
		
		//iniciaDadosFormulario();
		
		if( ! isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			this.obj.setModalidadeAquisicao( Assinatura.MODALIDADE_DOACAO );
		}
		
		obj.setTitulo(tituloDoTituloDoFasciculo);
		obj.setUnidadeDestino( getGenericDAO().refresh(new Biblioteca(idBibliotecaDestino)));
		
		// Já cria a assinatura associando ela diretamente com o título dos fascículos
		obj.setTituloCatalografico(  getGenericDAO().refresh(new TituloCatalografico(idTituloDoFasciculos)) );
		
		gerarCodigoAssinatuaCompra = false;
		
		return telaCriaAssinaturaFascisculo();
	}
	
	
	/**
	 *  Cria uma nova assinatura de periódicos, associado os títulos dos fascículos e para a biblioteca destino da transferência.
	 *  <p><i> Usado no caso de use de verificar a transferência dos fascículos quando os fascículos são transferidos para uma biblioteca
	 *  que não possui uma assinatura para eles</i></p><br/><br/>
	 * 
	 *  Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
	 */
	public String criarAssinaturaTransferenciaPeriodico() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
			
		MovimentoAssociaAssinaturaTransferenciaFasciculos mov
			= new MovimentoAssociaAssinaturaTransferenciaFasciculos(obj, gerarCodigoAssinatuaCompra, idBibliotecaDestino, idTituloDoFasciculos);
		
		mov.setCodMovimento(SigaaListaComando.ASSOCIA_ASSINATURA_TRANSFERENCIA_FASCIULOS);
		
		
		try {
			
			// Salva a nova assinatura no banco e associa ela ao registro de movimentação da transferência // 
			String codigoAssinturaCriada = execute(mov);
			
			addMensagemInformation("Assinatura com o código: " + codigoAssinturaCriada +" criada com sucesso.");
			
			AutorizaTransferenciaFasciculosEntreAssinaturasMBean bean = getMBean("autorizaTransferenciaFasciculosEntreAssinaturasMBean");
			return bean.iniciarAutorizacaoTranferenciaAposCriacaoAssinatura(idBibliotecaDestino);
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	

//	/**
//	 *  Método chamado para apenas associar a assinatura selecionado pelo usuário aos fascículos e para a biblioteca destino da transferência.
//	 *  <p><i> Usado caso o assinatura já tenha sido criada depois que a associação foi solicitada, neste caso o biblioteca pode apenas seleciona
//	 *  a assinatura anteriormente criada, não precisa criar uma nova assinatura. </i></p><br/><br/>
//	 * 
//	 *  Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
//	 *
//	 * @return
//	 * @throws ArqException
//	 */
//	public String  associaAssinaturaAosFasciculosTransferidos()throws ArqException{
//	
//		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
//				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
//		
//		int idAssianturaAssociacao = getParameterInt("idAssinaturaAssociacaoFasciculosTransferencia", 0);
//		
//		
//		MovimentoAssociaAssinaturaTransferenciaFasciculos mov
//		= new MovimentoAssociaAssinaturaTransferenciaFasciculos(obj, gerarCodigoAssinatuaCompra, idBibliotecaDestino, idTituloDoFasciculos, idAssianturaAssociacao);
//	
//		mov.setCodMovimento(SigaaListaComando.ASSOCIA_ASSINATURA_TRANSFERENCIA_FASCIULOS);
//		
//		try {
//			
//			execute(mov);
//			
//			addMensagemInformation("Associação realizada com sucesso.");
//			
//			AutorizaTransferenciaFasciculosEntreAssinaturasMBean bean = getMBean("autorizaTransferenciaFasciculosEntreAssinaturasMBean");
//			
//			return bean.iniciarAutorizacaoTranferenciaAposCriacaoAssinatura(idBibliotecaDestino);
//			
//		} catch (NegocioException ne) {
//			ne.printStackTrace();
//			addMensagens(ne.getListaMensagens());
//			return null;
//		}
//		
//	}
	
	
	/* ************************************************************************************************ */
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////	
	////////////////////   A parte de criação e de alteração das assinaturas  no sistema //////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Inicia o caso de uso de criar uma nova a assinatura de periódicos
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/alteraAssinaturaFasciculo.jsp
	 */
	public String preCriarAssinatura() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.CADASTRA_ALTERA_ASSINATURAS_DE_PERIODICO);
		
		iniciaDadosFormulario();
		
		if( ! isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			this.obj.setModalidadeAquisicao( Assinatura.MODALIDADE_DOACAO );
		}
		
		limpaOperacoesPaginaBuscaAssinatura();
		gerarCodigoAssinatuaCompra = false;
		
		return telaCriaAssinaturaFascisculo();
	}
	
	
	
	/**
	 *  Cria uma nova assinatura de periódicos.<br/><br/>
	 *
	 *  Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
	 */
	public String criarAssinatura() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
			
		MovimentoCadastraAlteraAssinaturaDePeriodico mov = new MovimentoCadastraAlteraAssinaturaDePeriodico(obj, false, gerarCodigoAssinatuaCompra);
		
		mov.setCodMovimento(SigaaListaComando.CADASTRA_ALTERA_ASSINATURAS_DE_PERIODICO);
		
		try {
			
			String codigoAssinatura  = execute(mov);
			
			addMensagemInformation("Assinatura criada com sucesso.");
		
			limparResultadosBusca();
			limpaOperacoesPaginaBuscaAssinatura();
			
			assinaturaModeloBuscas.setCodigo(codigoAssinatura);
			
			//buscaAssinaturaCriacaoAlteracao = true;
			buscarCodigo = true;
			
			return buscarAssinaturas();
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	
	
	
	/**
	 * Carrega os dados da assinatura criada pelo usuário para alteração.
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/alteraAssinaturaFasciculo.jsp
	 */
	public String preAlterarAssinatura() throws ArqException{
		
		alteracao = true;
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		prepareMovimento(SigaaListaComando.CADASTRA_ALTERA_ASSINATURAS_DE_PERIODICO);
		
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("idAssinaturaAlteracao"), Assinatura.class);
		
		if(obj.getFrequenciaPeriodicos() == null)
			obj.setFrequenciaPeriodicos(new FrequenciaPeriodicos(-1));
		
		return telaCriaAssinaturaFascisculo();
	}
	
	
	
	
	/**
	 * Altera os dados da assinatura.
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
	 */
	public String alterarAssinatura() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
			
		MovimentoCadastraAlteraAssinaturaDePeriodico mov = new MovimentoCadastraAlteraAssinaturaDePeriodico(obj, true, false);
		mov.setCodMovimento(SigaaListaComando.CADASTRA_ALTERA_ASSINATURAS_DE_PERIODICO);
		    
		try {
			
			execute(mov);
			
			addMensagemInformation("Assinatura alterada com sucesso.");
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
			
		AssinaturaDao dao = getDAO(AssinaturaDao.class);
		assinaturaModeloBuscas.setCodigo(obj.getCodigo());
		assinaturasBuscadas = dao.findAssinaturasAtivasByExemplo(assinaturaModeloBuscas, null, null, null, null, CampoOrdenacaoConsultaAssinatura.getCampoOrdenacao(valorCampoOrdenacao));
		
		if(assinaturasBuscadas.size() == 0)
			addMensagemErro("Não foram encontradas assinaturas com as características buscadas.");
		
		return telaBuscaAssinaturas();
		
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////         A parte de buscas desse MBean   ////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * Apaga os dados da tela
	 */
	private void iniciaDadosFormulario(){
		obj = new Assinatura();
		obj.setUnidadeDestino(new Biblioteca());
		obj.setFrequenciaPeriodicos( new FrequenciaPeriodicos(-1));
		
		assinaturaModeloBuscas = new Assinatura();
		assinaturaModeloBuscas.setUnidadeDestino(new Biblioteca());
		assinaturaModeloBuscas.setModalidadeAquisicao((short) -1 );
		assinaturaModeloBuscas.setFrequenciaPeriodicos( new FrequenciaPeriodicos(-1));
		alteracao = false;
	}
	
	
	
	/**
	 * Retorna todas as assinaturas cadastras no sistema
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp
	 */
	public String buscarAssinaturas() throws DAOException{
		
		assinaturasBuscadas = new ArrayList<Assinatura>();
		
		
		if(StringUtils.isEmpty(assinaturaModeloBuscas.getTitulo())) buscarTitulo = false;
		if(StringUtils.isEmpty(assinaturaModeloBuscas.getCodigo())) buscarCodigo = false;
		if(assinaturaModeloBuscas.getIssn() == null) buscarIssn = false;
		if(assinaturaModeloBuscas.getUnidadeDestino().getId() == -1) buscarUnidadeDestino = false;
		if(assinaturaModeloBuscas.getModalidadeAquisicao() == -1) buscarModalidadeAquisicao = false;
		if(assinaturaModeloBuscas.getFrequenciaPeriodicos().getId() == -1) buscarPeriodicidade = false;
		if(assinaturaModeloBuscas.getInternacional() == null) buscarInternacional = false;
		
		if (! buscarTitulo) assinaturaModeloBuscas.setTitulo( null );
		if (! buscarIssn) assinaturaModeloBuscas.setIssn(null);
		if (! buscarCodigo) assinaturaModeloBuscas.setCodigo(null);
		if (! buscarUnidadeDestino) assinaturaModeloBuscas.getUnidadeDestino().setId(-1);
		if (! buscarModalidadeAquisicao) assinaturaModeloBuscas.setModalidadeAquisicao((short) -1);
		if (! buscarPeriodicidade) assinaturaModeloBuscas.getFrequenciaPeriodicos().setId(-1);
		if (! buscarInternacional) assinaturaModeloBuscas.setInternacional(null);
		
		if(
				! buscarModalidadeAquisicao &&  ! buscarIssn && ! buscarUnidadeDestino &&
				! buscarCodigo && ! buscarTitulo && ! buscarInternacional && ! buscarPeriodicidade ) {
			addMensagemWarning("Informe um critério de busca.");
			return telaBuscaAssinaturas();
		}
		
		AssinaturaDao dao = null;
		
		try{
			dao = getDAO(AssinaturaDao.class);
			
			if(isPesquisaNormal() || isPesquisaSelecionaAssinatura() || isPesquisaSelecionaAssinaturaTransferenciaFasciculos() ){
				buscarAssinaturas(dao);
			}else
			
				if(isPesquisaSelecionaAssinaturaSemTitulo()){
					buscaAssinaturasSemTitulo(dao);
				}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		if(assinaturasBuscadas.size() == 0)
			addMensagemErro("Não foram encontradas assinaturas com as características buscadas.");
		
		if( AssinaturaDao.LIMITE_RESULTADOS.compareTo( assinaturasBuscadas.size() ) <= 0){
			addMensagemWarning("A busca resultou em um número muito grande de resultados, somente os "+AssinaturaDao.LIMITE_RESULTADOS+" primeiros estão sendo mostrados.");
		}
		
		return telaBuscaAssinaturas();
	}
	
	
	/**
	 *   Realiza a busca normal das assinaturas, utilizada na maioria dos cados de uso.<br/><br/>
	 */
	private void buscarAssinaturas(AssinaturaDao dao) throws DAOException{
		
		// Se o caso de uso NÃO quer visualizar apenas as assinatura da sua unidade, ou o usuário é administrador geral, busca todas as assinatura //
		if(! apenasUnidadePermissaoUsuario || isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			assinaturasBuscadas = dao.findAssinaturasAtivasByExemplo(assinaturaModeloBuscas, null, null, null, null, CampoOrdenacaoConsultaAssinatura.getCampoOrdenacao(valorCampoOrdenacao));
		}else{
			
			List<Integer> idsBiblioteca = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO) );
			
			if(idsBiblioteca == null){
				
				idsBiblioteca = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO ) );
				
				if(idsBiblioteca == null){
					addMensagemErro("Usuário não tem permissão para atribuir um título a uma assinatura.");
				}
			}
			assinaturasBuscadas = dao.findAssinaturasAtivasByExemploByBibliotecas(assinaturaModeloBuscas, idsBiblioteca, CampoOrdenacaoConsultaAssinatura.getCampoOrdenacao(valorCampoOrdenacao));
			
		}
		
	}
	
	
	/**
	 *   Retorna todas as assinaturas cadastradas no sistema que não possui títulos ainda, usado no caso
	 * de uso quando o usuário deseja associar um título a uma assinatura.<br/><br/>
	 *
	 */
	private void  buscaAssinaturasSemTitulo(AssinaturaDao dao) throws DAOException{
		
		
		// Se o caso de uso NÃO quer visualizar apenas as assinatura da sua unidade, ou o usuário é administrador geral, busca todas as assinatura //
		if(! apenasUnidadePermissaoUsuario || isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			assinaturasBuscadas = dao.findAssinaturaAtivasByExemploNaoAssociadasATitulo(assinaturaModeloBuscas, CampoOrdenacaoConsultaAssinatura.getCampoOrdenacao(valorCampoOrdenacao));
		}else{
			
			List<Integer> idsBiblioteca = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO) );
			
			if(idsBiblioteca == null){
				
				idsBiblioteca = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO ) );
				
				if(idsBiblioteca == null){
					addMensagemErro("Usuário não tem permissão para atribuir um título a uma assinatura.");
				}
			}
			
			assinaturasBuscadas = dao.findAssinaturaAtivasByExemploNaoAssociadasATituloByBibliotecas(assinaturaModeloBuscas, idsBiblioteca, CampoOrdenacaoConsultaAssinatura.getCampoOrdenacao(valorCampoOrdenacao));
		}
		
	}
	
	
	/**
	 *     Apaga os resultados da busca de assinaturas.
	 *<p>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/buscaAssinaturaParaRegistroChegada.jsp"
	 */
	public String limparResultadosBusca() {
		
		iniciaDadosFormulario();
		
		buscarTitulo = false;
		buscarCodigo = false;
		buscarUnidadeDestino = false;
		buscarModalidadeAquisicao = false;
		buscarPeriodicidade = false;
		
		assinaturasBuscadas = new ArrayList<Assinatura>();
		
		return telaBuscaAssinaturas();
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  Retorna todas as bibliotecas internas ativas do sistema. São as únicas para as quais
	 *  pode-se criar assinaturas.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecasInternas() throws DAOException{
		
		if (bibliotecasCombo == null){
			BibliotecaDao dao = null;
			try{
				dao = getDAO(BibliotecaDao.class);
				bibliotecasCombo = dao.findAllBibliotecasInternasAtivas();
			}finally{
			  if (dao != null)  dao.close();
			}
		}
		
		return toSelectItems(bibliotecasCombo, "id", "descricaoCompleta");
	}

	
	/**
	 *  Retorna todas as frequências ativas para as assinaturas.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *   </ul>
	 */
	public Collection <SelectItem> getAllFrequenciasAtivasComboBox() throws DAOException{
		
		if (frequenciasCombo == null){
			GenericDAO dao = null;
			try{
				dao = getGenericDAO();
				frequenciasCombo = dao.findAllAtivos(FrequenciaPeriodicos.class, new String[]{"id",  "descricao", "unidadeTempoExpiracao", "tempoExpiracao"});
			
				for (FrequenciaPeriodicos frequencias : frequenciasCombo) {
					frequencias.converteTempoExpiracaoSalvo();
				}
				
			}finally{
			  if (dao != null)  dao.close();
			}
		}
		
		return toSelectItems(frequenciasCombo, "id", "descricao");
	}
	
	
	/**
	 * 
	 * Retorna os campos que o usuário vai pode escolher a ordenação no resultado da pesquisa.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public Collection<SelectItem> getCampoOrdenacaoResultadosComboBox(){
		
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		
		for(CampoOrdenacaoConsultaAssinatura campo : CampoOrdenacaoConsultaAssinatura.CAMPOS_ORDENACAO_BUSCA_PADRAO_ASSINATURA){
			lista.add(new SelectItem(campo.getValor(), campo.getDescricao()));
		}
		
		return lista;
	}
	
	
	
	

	//////////  Indicativo as operações que podem ser realizadas por esse Mbean /////////
	


	public boolean isPesquisaNormal() {
		return operacao == PESQUISA_NORMAL;
	}
	
	public boolean isPesquisaSelecionaAssinatura() {
		return operacao == PESQUISA_SELECIONA_UMA_ASSINATURA;
	}
	
	public boolean isPesquisaSelecionaAssinaturaSemTitulo() {
		return operacao == PESQUISA_SELECIONA_UMA_ASSINATURA_SEM_TITULO;
	}
	
	
	public boolean isPesquisaSelecionaAssinaturaTransferenciaFasciculos() {
		return operacao == PESQUISA_SELECIONA_UMA_ASSINATURA_TRANSFERENCIA_FASCICULOS;
	}
	
	
	
	
	//////////////////////////// Método de Navegação /////////////////////
	
	/**
	 *  Redireciona para a página de criação ou alteração das assinaturas.
	 * <br/><br/>
	 * Método invocado de nenhuma página jsp
	 */
	public String telaCriaAssinaturaFascisculo(){
		return forward(PAGINA_CRIA_ASSINATURA_PERIODICO);
	}

	/**
	 *  Redireciona para a página que o usuário busca as assinaturas.
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/incluiFasciculo.jsp
	 *
	 * @return
	 */
	public String telaBuscaAssinaturas(){
		return forward(PAGINA_BUSCA_ASSINATURA_PERIODICO);
	}

	/**
	 *  Redireciona para a página que o usuário busca assinatura para inclusão de fascículos
	 * <br/><br/>
	 * Método invocado de nenhuma página jsp
	 */
	public String telaBuscaAssinaturasInclusaoFasciculo(){
		return forward(PAGINA_BUSCA_ASSINATURAS_INCLUSAO_FASCICULOS);
	}
	
	
	///////////////////////////////////////////////////////////////////////
	
	
	
	//// gets e sets ////
	
	public boolean isAlteracao() {
		return alteracao;
	}

	public void setAlteracao(boolean alteracao) {
		this.alteracao = alteracao;
	}

	public List<Assinatura> getAssinaturasBuscadas() {
		return assinaturasBuscadas;
	}

	public void setAssinaturasBuscadas(List<Assinatura> assinaturasBuscadas) {
		this.assinaturasBuscadas = assinaturasBuscadas;
	}
	
	public Assinatura getAssinaturaModeloBuscas() {
		return assinaturaModeloBuscas;
	}


	public void setAssinaturaModeloBuscas(Assinatura assinaturaModeloBuscas) {
		this.assinaturaModeloBuscas = assinaturaModeloBuscas;
	}
	
	public boolean isBuscarTitulo() {
		return buscarTitulo;
	}

	public void setBuscarTitulo(boolean buscarTitulo) {
		this.buscarTitulo = buscarTitulo;
	}

	public boolean isBuscarCodigo() {
		return buscarCodigo;
	}

	public void setBuscarCodigo(boolean buscarCodigo) {
		this.buscarCodigo = buscarCodigo;
	}

	public boolean isBuscarUnidadeDestino() {
		return buscarUnidadeDestino;
	}

	public void setBuscarUnidadeDestino(boolean buscarUnidadeDestino) {
		this.buscarUnidadeDestino = buscarUnidadeDestino;
	}

	public boolean isBuscarModalidadeAquisicao() {
		return buscarModalidadeAquisicao;
	}

	public void setBuscarModalidadeAquisicao(boolean buscarModalidadeAquisicao) {
		this.buscarModalidadeAquisicao = buscarModalidadeAquisicao;
	}

	public boolean isGerarCodigoAssinatuaCompra() {
		return gerarCodigoAssinatuaCompra;
	}

	public void setGerarCodigoAssinatuaCompra(boolean gerarCodigoAssinatuaCompra) {
		this.gerarCodigoAssinatuaCompra = gerarCodigoAssinatuaCompra;
	}
	
	public void setBuscarInternacional(boolean buscarInternacional) {
		this.buscarInternacional = buscarInternacional;
	}

	public boolean isBuscarIssn() {
		return buscarIssn;
	}

	public void setBuscarIssn(boolean buscarIssn) {
		this.buscarIssn = buscarIssn;
	}

	public boolean isBuscarPeriodicidade() {
		return buscarPeriodicidade;
	}


	public void setBuscarPeriodicidade(boolean buscarPeriodicidade) {
		this.buscarPeriodicidade = buscarPeriodicidade;
	}
	
	public int getValorCampoOrdenacao() {
		return valorCampoOrdenacao;
	}

	public void setValorCampoOrdenacao(int valorCampoOrdenacao) {
		this.valorCampoOrdenacao = valorCampoOrdenacao;
	}
	
	public boolean isBuscarInternacional() {
		return buscarInternacional;
	}

	public boolean isApenasUnidadePermissaoUsuario() {
		return apenasUnidadePermissaoUsuario;
	}

	public void setApenasUnidadePermissaoUsuario(boolean apenasUnidadePermissaoUsuario) {
		this.apenasUnidadePermissaoUsuario = apenasUnidadePermissaoUsuario;
	}

	
	
}
