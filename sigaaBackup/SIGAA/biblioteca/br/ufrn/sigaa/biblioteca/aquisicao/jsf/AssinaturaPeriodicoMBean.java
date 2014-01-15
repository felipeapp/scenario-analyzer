/*
 * Universidade Federal do Rio Grande no Norte
 * Superintend�ncia de Inform�tica
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
 *  <p>MBean que ger�ncia o ciclo de vida de uma assinatura de peri�dicos do sistema (buscar, criar, alterar, remover).</p>
 *  
 *  <p>Respons�vel pela busca padr�o das assinatura utilizada pelos demais casos de uso do sistema.</p>
 *
 * @author Jadson
 * @since 19/01/2009
 * @version 1.0 Cria��o da classe
 *
 */
@Component("assinaturaPeriodicoMBean")
@Scope("request")
public class AssinaturaPeriodicoMBean extends SigaaAbstractController<Assinatura> {
	
	/** P�gina com a busca/lista de assinaturas e o link para cria��o de uma nova assinatura.  */
	public static final String PAGINA_BUSCA_ASSINATURA_PERIODICO = "/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp";
	
	/** P�gina com o formul�rio para cria��o de uma nova assinatura. */
	public static final String PAGINA_CRIA_ASSINATURA_PERIODICO = "/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp";
	
	/** Se na hora de incluir um fasc�culo, o t�tulo n�o tiver nenhuma assinatura associada a ele, o usu�rio
	 * dever� buscar essa assinatura para associar ao t�tulo. */
	public static final String PAGINA_BUSCA_ASSINATURAS_INCLUSAO_FASCICULOS = "/biblioteca/processos_tecnicos/catalogacao/paginaBuscaAssinaturasInclusaoFasciculos.jsp";
	
	/** Assinatura usada como modelo para as buscas. */
	private Assinatura assinaturaModeloBuscas;
	
	/** Guarda o resultados da busca de assinatura padr�o do sistema. */
	private List<Assinatura> assinaturasBuscadas = new ArrayList<Assinatura>();
	
	/** Indica se est� alterando ou criando. pois tanto a altera��o como a cria��o ocorrem no mesmo formul�rio. */
	private boolean alteracao = false;
	
	
	///////////////////////////////
	// Se a busca � para ser feita por alguma campo ou n�o. Os valores s�o determinados nos checkbox da p�gina:
	
	/** Informa se deve filtrar por t�tulo. */
	private boolean buscarTitulo;
	
	/** Informa se deve filtrar por c�digo da assinatura. */
	private boolean buscarCodigo;
	
	/** Informa se deve filtrar por unidade de destino. */
	private boolean buscarUnidadeDestino;
	
	/** Informa se deve filtrar por modalidade de aquisi��o. */
	private boolean buscarModalidadeAquisicao;
	
	/** Informa se deve filtrar pela Periodicidade de assinatura. */
	private boolean buscarPeriodicidade;
	
	/** Informa se deve filtrar por nacionalidade. */
	private boolean buscarInternacional;
	
	/** Informa se deve filtrar por ISSN. */
	private boolean buscarIssn;
	
	/////////////////////////////////
	
	/** Guarda o valor do campo escolhido pelo usu�rio para ordenar a consulta */
	protected int valorCampoOrdenacao;

	
	/*
	 * Usados no caso de uso em que o usu�rio vai criar uma assinatura para associar aos fasc�culos
	 * transferidos sem assinaturas.
	 */
	
	/** Biblioteca de destino da assinatura. */
	private int idBibliotecaDestino;
	/** T�tulo � qual a assinatura ser� associada. */
	private int idTituloDoFasciculos;
	/** O t�tulo do t�tulo catalogr�fico associado ao fasc�culo. */
	private String tituloDoTituloDoFasciculo;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////

	
	
	 /** Algumas assinaturas mesmo sendo de compra o sistema deve gerar o c�digo, pois ningu�m tem esse c�digo. */
	private boolean gerarCodigoAssinatuaCompra;

	
	
	/** Guarda a lista de bibliotecas que v�o ser exibidas no campoBox*/
	private Collection <Biblioteca> bibliotecasCombo;
	
	/** Guarda a lista de frequ�ncias que v�o ser exibidas no campoBox*/
	private Collection <FrequenciaPeriodicos> frequenciasCombo; 
	
	
	/**
	 * MBean que chamou a pesquisa no acervo para selecionar uma assinatura
	 */
	private PesquisarAcervoAssinaturas mbeanChamador;
	
	
	/**  A pesquisa mais b�sica sem nenhuma opera��es especial */
	public static final int PESQUISA_NORMAL = 0;
	
	/**
	 * Opera��o utilizada qual se deseja selecionar uma assinatura para utilizar em outro caso de uso.
	 * @see {@link RegistraChegadaFasciculoMBean}.
	 */
	public static final int PESQUISA_SELECIONA_UMA_ASSINATURA = 1; 
	
	
	/**
	 * Opera��o utilizada para selecionar uma assinatura de outro caso de uso, por�m aqui s� � realizado a busca em assinaturas sem T�tulos associados
	 * @see {@link AssociaAssinaturaATituloMBean}.
	 */
	public static final int PESQUISA_SELECIONA_UMA_ASSINATURA_SEM_TITULO = 2; 
	
	
	/**
	 * Opera��o especial utilizada para criar uma asssinatura e ao mesmo tempo associa-la a uma transfer�ncia pedente de fasc�culos.
	 * @see {@link AutorizaTransferenciaFasciculosEntreAssinaturasMBean}.
	 */
	public static final int PESQUISA_SELECIONA_UMA_ASSINATURA_TRANSFERENCIA_FASCICULOS = 3;
	
	
	
	/** Define a op��o que est� sendo utilizada no momento*/
	private int operacao = -1;
	
	
	/** Indica se deve buscar as assinaturas somente na unidade onde o usu�rio tem permiss�o.*/
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
	 *   Redireciona para a p�gina de listagem na qual o usu�rio pode alterar as existentes ou
	 *   criar novas
	 *
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao.jsp
	 */
	public String iniciarPesquisaAssinaturas() throws ArqException{
		
		operacao = PESQUISA_NORMAL;
		
		apenasUnidadePermissaoUsuario = false;
		
		return telaBuscaAssinaturas();
		
	}
	
	/**
	 * Inicia a pesquisa para escolher uma assunatura do acervo. Utilizado quando o caso de uso da busca de assinatura � chamado a partir de outro caso de uso
	 * 
	 * <br/><br/>   
	 * M�todo n�o invocado por nenhuma jsp.
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
	 * Inicia a pesquisa para escolher uma assunatura do acervo. Utilizado quando o caso de uso da busca de assinatura � chamado a partir de outro caso de uso
	 * 
	 * <br/><br/>   
	 * M�todo n�o invocado por nenhuma jsp.
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
	 *   Inicia o caso de uso de criar uma nova assinatura de peri�dicos para os fasc�culos que foram
	 * transferidos sem a biblioteca destino possuir a assinatura.
	 *   <p><i>(Ao terminar esse caso de uso deve-se retornar para a p�gina na qual o usu�rio
	 *   autoriza a transfer�ncia dos fasc�culos)</i></p>
	 *
	 *   <br/><br/>   
	 *   M�todo n�o invocado por nenhuma jsp.
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
	 *    <p> O Mbean que deseja realizar essa opera��o tem que implementar {@link PesquisarAcervoAssinaturas} </p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
		
		return null; // N�o era para chegar aqui, porque s� existem duas op��es de busca que seleciona a assinatura
	}
	
	
	/**
	 * Indica que o caso de uso que chamou a pesquisa de assinaturas vai permitir voltar da tela de 
	 * pesquisa de assinaturas para a tela anterior.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 *  <p> Realiza a fun��o do bot�o voltar quando a busca � chamada por outro caso de uso do sistema. </p>
	 *  <p> <i> ( implementa��o desse m�todo deve ser realizada por Mbean que chamar esse caso de uso, pois s� ele sabe para onde deve voltar ) </i> </p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
//	 *   Redireciona para a p�gina de listagem na qual o usu�rio pode alterar as existentes ou
//	 *   criar novas
//	 *
//	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao.jsp
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
	 * Limpa as opera��es que podem ser realizadas pelo usu�rio na busca de assinaturas
	 */
	private void limpaOperacoesPaginaBuscaAssinatura(){
		alteracao = false;
	}
	
	
	
	/**
	 *    M�todo que verifica se o usu�rio escolheu criar uma assinatura de compra ou doa��o porque
	 *    algumas valida��es devem ser feitas na tela.<br/><br/>
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
	 */
	public void verificaAlteracaoModalidadeAquisicao(ValueChangeEvent evt){
		
		this.obj.setModalidadeAquisicao( new Short(""+evt.getNewValue()) );
		
	}
	
	

	/**
	 *    M�todo que verifica se o usu�rio escolheu que vai digitar o c�digo da assinatura ou
	 * o sistema vai gerar esse c�digo, mesmo a assinatura sendo de compra.<br/><br/>
	 *
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
	 */
	public void verificaUsuarioSelecionouGerarCodigo(ValueChangeEvent evt){
		
		this.gerarCodigoAssinatuaCompra = (Boolean) evt.getNewValue() ;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	/* *********************************************************************************************
	 * 
	 *  M�todos especiais para cria��o de assinatura a partir da autoriza��o da transferencia
	 *  a parte de cria��o de assinatura para transferencia de periodicos
	 *  
	 *************************************************************************************************/
	
	

	
	
	/**
	 * Inicia o caso de uso de criar uma nova a assinatura para atribuir aos fasc�culos transferidos sem assinatura.
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp
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
		
		// J� cria a assinatura associando ela diretamente com o t�tulo dos fasc�culos
		obj.setTituloCatalografico(  getGenericDAO().refresh(new TituloCatalografico(idTituloDoFasciculos)) );
		
		gerarCodigoAssinatuaCompra = false;
		
		return telaCriaAssinaturaFascisculo();
	}
	
	
	/**
	 *  Cria uma nova assinatura de peri�dicos, associado os t�tulos dos fasc�culos e para a biblioteca destino da transfer�ncia.
	 *  <p><i> Usado no caso de use de verificar a transfer�ncia dos fasc�culos quando os fasc�culos s�o transferidos para uma biblioteca
	 *  que n�o possui uma assinatura para eles</i></p><br/><br/>
	 * 
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
	 */
	public String criarAssinaturaTransferenciaPeriodico() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
			
		MovimentoAssociaAssinaturaTransferenciaFasciculos mov
			= new MovimentoAssociaAssinaturaTransferenciaFasciculos(obj, gerarCodigoAssinatuaCompra, idBibliotecaDestino, idTituloDoFasciculos);
		
		mov.setCodMovimento(SigaaListaComando.ASSOCIA_ASSINATURA_TRANSFERENCIA_FASCIULOS);
		
		
		try {
			
			// Salva a nova assinatura no banco e associa ela ao registro de movimenta��o da transfer�ncia // 
			String codigoAssinturaCriada = execute(mov);
			
			addMensagemInformation("Assinatura com o c�digo: " + codigoAssinturaCriada +" criada com sucesso.");
			
			AutorizaTransferenciaFasciculosEntreAssinaturasMBean bean = getMBean("autorizaTransferenciaFasciculosEntreAssinaturasMBean");
			return bean.iniciarAutorizacaoTranferenciaAposCriacaoAssinatura(idBibliotecaDestino);
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	

//	/**
//	 *  M�todo chamado para apenas associar a assinatura selecionado pelo usu�rio aos fasc�culos e para a biblioteca destino da transfer�ncia.
//	 *  <p><i> Usado caso o assinatura j� tenha sido criada depois que a associa��o foi solicitada, neste caso o biblioteca pode apenas seleciona
//	 *  a assinatura anteriormente criada, n�o precisa criar uma nova assinatura. </i></p><br/><br/>
//	 * 
//	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
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
//			addMensagemInformation("Associa��o realizada com sucesso.");
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
	////////////////////   A parte de cria��o e de altera��o das assinaturas  no sistema //////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Inicia o caso de uso de criar uma nova a assinatura de peri�dicos
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/alteraAssinaturaFasciculo.jsp
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
	 *  Cria uma nova assinatura de peri�dicos.<br/><br/>
	 *
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
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
	 * Carrega os dados da assinatura criada pelo usu�rio para altera��o.
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/alteraAssinaturaFasciculo.jsp
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
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/criaAssinaturaPeriodico.jsp
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
			addMensagemErro("N�o foram encontradas assinaturas com as caracter�sticas buscadas.");
		
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
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/buscaAssinaturaPeriodico.jsp
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
			addMensagemWarning("Informe um crit�rio de busca.");
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
			addMensagemErro("N�o foram encontradas assinaturas com as caracter�sticas buscadas.");
		
		if( AssinaturaDao.LIMITE_RESULTADOS.compareTo( assinaturasBuscadas.size() ) <= 0){
			addMensagemWarning("A busca resultou em um n�mero muito grande de resultados, somente os "+AssinaturaDao.LIMITE_RESULTADOS+" primeiros est�o sendo mostrados.");
		}
		
		return telaBuscaAssinaturas();
	}
	
	
	/**
	 *   Realiza a busca normal das assinaturas, utilizada na maioria dos cados de uso.<br/><br/>
	 */
	private void buscarAssinaturas(AssinaturaDao dao) throws DAOException{
		
		// Se o caso de uso N�O quer visualizar apenas as assinatura da sua unidade, ou o usu�rio � administrador geral, busca todas as assinatura //
		if(! apenasUnidadePermissaoUsuario || isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			assinaturasBuscadas = dao.findAssinaturasAtivasByExemplo(assinaturaModeloBuscas, null, null, null, null, CampoOrdenacaoConsultaAssinatura.getCampoOrdenacao(valorCampoOrdenacao));
		}else{
			
			List<Integer> idsBiblioteca = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO) );
			
			if(idsBiblioteca == null){
				
				idsBiblioteca = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO ) );
				
				if(idsBiblioteca == null){
					addMensagemErro("Usu�rio n�o tem permiss�o para atribuir um t�tulo a uma assinatura.");
				}
			}
			assinaturasBuscadas = dao.findAssinaturasAtivasByExemploByBibliotecas(assinaturaModeloBuscas, idsBiblioteca, CampoOrdenacaoConsultaAssinatura.getCampoOrdenacao(valorCampoOrdenacao));
			
		}
		
	}
	
	
	/**
	 *   Retorna todas as assinaturas cadastradas no sistema que n�o possui t�tulos ainda, usado no caso
	 * de uso quando o usu�rio deseja associar um t�tulo a uma assinatura.<br/><br/>
	 *
	 */
	private void  buscaAssinaturasSemTitulo(AssinaturaDao dao) throws DAOException{
		
		
		// Se o caso de uso N�O quer visualizar apenas as assinatura da sua unidade, ou o usu�rio � administrador geral, busca todas as assinatura //
		if(! apenasUnidadePermissaoUsuario || isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			assinaturasBuscadas = dao.findAssinaturaAtivasByExemploNaoAssociadasATitulo(assinaturaModeloBuscas, CampoOrdenacaoConsultaAssinatura.getCampoOrdenacao(valorCampoOrdenacao));
		}else{
			
			List<Integer> idsBiblioteca = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO) );
			
			if(idsBiblioteca == null){
				
				idsBiblioteca = BibliotecaUtil.obtemIdsBibliotecasPermisaoUsuario(getUsuarioLogado(), new Papel(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO ) );
				
				if(idsBiblioteca == null){
					addMensagemErro("Usu�rio n�o tem permiss�o para atribuir um t�tulo a uma assinatura.");
				}
			}
			
			assinaturasBuscadas = dao.findAssinaturaAtivasByExemploNaoAssociadasATituloByBibliotecas(assinaturaModeloBuscas, idsBiblioteca, CampoOrdenacaoConsultaAssinatura.getCampoOrdenacao(valorCampoOrdenacao));
		}
		
	}
	
	
	/**
	 *     Apaga os resultados da busca de assinaturas.
	 *<p>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/buscaAssinaturaParaRegistroChegada.jsp"
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
	 *  Retorna todas as bibliotecas internas ativas do sistema. S�o as �nicas para as quais
	 *  pode-se criar assinaturas.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 *  Retorna todas as frequ�ncias ativas para as assinaturas.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna os campos que o usu�rio vai pode escolher a ordena��o no resultado da pesquisa.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	
	
	

	//////////  Indicativo as opera��es que podem ser realizadas por esse Mbean /////////
	


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
	
	
	
	
	//////////////////////////// M�todo de Navega��o /////////////////////
	
	/**
	 *  Redireciona para a p�gina de cria��o ou altera��o das assinaturas.
	 * <br/><br/>
	 * M�todo invocado de nenhuma p�gina jsp
	 */
	public String telaCriaAssinaturaFascisculo(){
		return forward(PAGINA_CRIA_ASSINATURA_PERIODICO);
	}

	/**
	 *  Redireciona para a p�gina que o usu�rio busca as assinaturas.
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/incluiFasciculo.jsp
	 *
	 * @return
	 */
	public String telaBuscaAssinaturas(){
		return forward(PAGINA_BUSCA_ASSINATURA_PERIODICO);
	}

	/**
	 *  Redireciona para a p�gina que o usu�rio busca assinatura para inclus�o de fasc�culos
	 * <br/><br/>
	 * M�todo invocado de nenhuma p�gina jsp
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
