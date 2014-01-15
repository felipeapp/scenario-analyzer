/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/11/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.aquisicao.jsf.AssinaturaPeriodicoMBean;
import br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DadosTransferenciaSemAssinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.OperacaoAutoridazacaoTransferenciaFasciculos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAssociaAssinaturaTransferenciaFasciculos;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.FasciculoByBibliotecaAssinaturaAnoCronologicoNumeroComparator;

/**
 *
 *     Depois que o bibliotec�rio solicitou a transfer�ncia dos fasc�culos, se a transfer�ncia foi para outra biblioteca,
 *     eles n�o s�o transferidos de imediato, ficam pendentes. <br/>
 *     � necess�rio um bibliotec�rio da biblioteca destino da transfer�ncia conferir e autorizar a transfer�ncia
 * para que ela seja conclu�da.<br/><br/>
 *     Esse MBean gerencia esse caso de uso (de conferir e autorizar a transfer�ncia de fasc�culos entre bibliotecas).
 *
 * @author Jadson
 * @since 26/11/2009
 * @version 1.0 Cria��o da classe
 *
 */
@Component("autorizaTransferenciaFasciculosEntreAssinaturasMBean")
@Scope("request")
public class AutorizaTransferenciaFasciculosEntreAssinaturasMBean extends SigaaAbstractController<Assinatura> implements PesquisarAcervoAssinaturas{

	
	/** P�gina onde o usu�rio autoriza as pend�ncias */
	public static final String PAGINA_AUTORIZA_TRANSFERENCIA_FASCICULOS = "/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp";
	
	/** P�gina onde o usu�rio confirma a associa��o da assinatura selecionada com a transfer�ncia que estava sem a assinatura destino. */
	public static final String PAGINA_CONFIRMA_ASSOCIACAO_ASSINATURA_JA_EXISTENTE = "/biblioteca/processos_tecnicos/outras_operacoes/confirmaAssociacaoAssinaturaJaExistenteTransferenciaFasciculos.jsp";
	
	/**
	 * As biblioteca nas quais o usu�rio tem permiss�o para autorizar a transfer�ncia dos fasc�culos //
	 */
	private Collection <Biblioteca> bibliotecasDestinoTransferencia;
	
	/**
	 * As assinatura que possuem fasc�culos pendentes na biblioteca escolhida pelo usu�rio. //
	 */
	private List<Assinatura> assinaturasComFasciculosPendentes;
	
	/**
	 * Guarda os dados dos fasc�culos que foram transferidos sem assinaturas //
	 */
	private List<DadosTransferenciaSemAssinatura> dadosMovimentacaoSemAssinatura;
	
	/**
	 * Guarda os fasc�culos pendentes para o usu�rio autorizar //
	 */
	private List<Fasciculo> fasciculosPendente;
	
	/**
	 * Vari�vel que armazena a assinatura atualmente selecionada
	 */
	private Assinatura assinaturaSelecionada = null;
	
	
	/** 
	 * Utilizando quando a transfer�ncia ocorre sem o usu�rio informar a assinatura destino da transfer�ncia
	 * 
	 * Essa vari�vel cont�m a assinatura selecionada no momento da autoriza��o da transfer�ncia.
	 */
	private Assinatura assinaturaJaExistenteSelecionada = null;
	
	/** Biblioteca de destino da assinatura. */
	private int idBibliotecaDestino;
	/** T�tulo � qual a assinatura ser� associada. */
	private int idTituloDoFasciculos;
	/** Os dados da transferencia que ocorreu sem assinatura destino que foi selecionado pelo usu�rio para criar a assinatura. */
	private DadosTransferenciaSemAssinatura dadoTransferenciaSelecinado;
	
	
	/** <p>Informa se o c�digo de barras dos fasc�culos transferidos ser�o alterados para ficarem iguais aos c�digos de barras 
	 *  dos fasc�culos da assinatura para onde ele est�o sendo transferidos.</p>
	 *  
	 *  <p>Por padr�o vai permanecer com os c�digo de barras j� existentes. Ou seja, a assinatura destino 1234 pode possuir 
	 *  fasc�culos: <strong>1234-1, 1234-2, 1234-N,</strong> e os novos fasc�culos transferidos da assinatura origem 5678:  <strong>5678-1, 5678-2 5678-N</strong>. </p>
	 *  
	 *  <p>Caso o usu�rio escolhar mudar, os novos c�digos de barras dos fasc�culos da assinatura destino 1234 ser�o: <strong>1234-1, 1234-2, 1234-N, 1234-N+1, 1234-N+2, 1234-N+3</strong>.</p>
	 */
	private boolean codigoDeBarrasAcompanhaCodigoNovaAssinatura = false;
	
	
	
	/**
	 *    Inicia o caso de uso de autoriza��o da transfer�ncia dos fasc�culos. Para cada fasc�culo
	 * pendente o usu�rio tem que escolher se autoriza ou n�o a transfer�ncia. <br/>
	 *    Se n�o autorizar, vai informar o motivo que vai ser enviado por email ao usu�rio que fez
	 *  a transfer�ncia.<br/>
	 * 
	 *     Chamado a partir da p�gina: /sigaa.war/biblioteca/menu/processos_tecnicos.jsp
	 */
	public String iniciarAutorizacaoTranferencia() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		inicializaDados();
		
		return telaAutorizaTransferenciaFasciculos();
	}
	
	
	/**
	 *    Inicia o caso de uso de autoriza��o da transfer�ncia dos fasc�culos j� passando o
	 *  id da biblioteca em que as assinaturas v�o ser procuradas.
	 * 
	 *    <p><i> Geralmente usado quando o caso de uso � chamado de outro MBean, por exemplo,
	 *    depois que a assinatura dos fasc�culos que estavam sem assinatura � criada.</i><p>
	 * 
	 *     N�o chamado de nenhum a p�gina JSP.
	 */
	public String iniciarAutorizacaoTranferenciaAposCriacaoAssinatura(int idBiblioteca) throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		inicializaDados();
		
		obj.setUnidadeDestino(  getGenericDAO().refresh(  new Biblioteca(idBiblioteca) )  );
		
		buscarTransferenciasPendentesFasciculos(idBiblioteca);
		
		return telaAutorizaTransferenciaFasciculos();
	}
	
	
	
	
	/**
	 * Inicializa dos dados do formul�rio de pesquisa
	 */
	private void inicializaDados(){
		obj = new Assinatura();
		obj.setUnidadeDestino(new Biblioteca(-1));
	}
	
	
	
	/**
	 *   Obt�m as assinatura que possuem fasc�culos pendentes a partir da biblioteca escolhida
	 * pelo usu�rio.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
	 */
	public void verificaAssinaturaPendentesDaBibliotecaEscolhida(ValueChangeEvent evt) throws ArqException{
		
		Integer idBibliotecaEscolhida = (Integer) evt.getNewValue();
	
		buscarTransferenciasPendentesFasciculos(idBibliotecaEscolhida);
		
		assinaturaSelecionada = null;
		
	}
	
	
	
	
	/**
	 *     Busca as transfer�ncia pendentes de fasc�culos para a biblioteca passada.
	 */
	private void buscarTransferenciasPendentesFasciculos(Integer idBibliotecaEscolhida) throws ArqException{

		fasciculosPendente = new ArrayList<Fasciculo>();
		
		dadosMovimentacaoSemAssinatura = new ArrayList<DadosTransferenciaSemAssinatura>();
		
		if(  !idBibliotecaEscolhida.equals(new Integer(-1) ) ) {
			
			AssinaturaDao dao = getDAO(AssinaturaDao.class);
			
			// transfer�ncias feitas para uma assinatura espec�fica
			assinaturasComFasciculosPendentes = dao.encontraAssinaturasCujosFasciculosEstaoPendentesTransferencia(idBibliotecaEscolhida);
			
			// transfer�ncias feitas sem a biblioteca possuir a assinatura
			List<Object> dados = dao.encontraDadosMovimentacaoSemAssinaturaFasciculos(idBibliotecaEscolhida);
			
			for (Object object : dados) {
				Object[] array = (Object[]) object;
				
				CacheEntidadesMarc cache = BibliotecaUtil.obtemDadosTituloCache((Integer) array[8]);
				String descricaoTitulo = cache.getNumeroDoSistema()+" - "+ (cache.getAutor() != null ? cache.getAutor() :" ")  +" "+ (cache.getTitulo() != null ? cache.getTitulo() : " ");
				String titulo = (cache.getTitulo() != null ? cache.getTitulo() : " ");
				//reg.id, m.id, m.codigoBarras, m.anoCronologico, m.ano, m.volume, m.numero, m.edicao, reg.assinaturaOrigem.tituloCatalografico.id
				
				dadosMovimentacaoSemAssinatura.add(
						new DadosTransferenciaSemAssinatura((Integer) array[0], (Integer) array[1], (String)array[2],(String)array[3]
						 , (String)array[4],(String)array[5],(String)array[6],(String)array[7], (Integer)array[8], descricaoTitulo, titulo, (String) array[9])
				);
				
			}
			
			Collections.sort(dadosMovimentacaoSemAssinatura);
			
			prepareMovimento(SigaaListaComando.CONFIRMA_TRANSFERENCIA_FASCICULOS_ENTRE_BIBLIOTECAS);
			
			if(assinaturasComFasciculosPendentes.size() == 0) assinaturasComFasciculosPendentes = null;
			if(dadosMovimentacaoSemAssinatura.size() == 0) dadosMovimentacaoSemAssinatura = null;
		}else{
			assinaturasComFasciculosPendentes = null;
			dadosMovimentacaoSemAssinatura = null;
		}
	}
	
	
	
	
	
	/**
	 *   M�todo chamado depois que o usu�rio selecionou a biblioteca e a assinatura dos fasc�culos pendentes de transfer�ncia.<br/>
	 * 
	 *
	 *  Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
	 */
	public String visualizarFasciculosPendentesAutorizacao() throws ArqException{

		prepareMovimento(SigaaListaComando.CONFIRMA_TRANSFERENCIA_FASCICULOS_ENTRE_BIBLIOTECAS);

		int idAssinaturaSelecionada = getParameterInt("idAssinaturaSelecionada");
		
		if (assinaturaSelecionada != null && assinaturaSelecionada.getId() == idAssinaturaSelecionada && fasciculosPendente != null) {
			assinaturaSelecionada = null;
			fasciculosPendente = null;
			
			return telaAutorizaTransferenciaFasciculos();
		}
		
		fasciculosPendente = new ArrayList<Fasciculo>();
		
		for (Assinatura a : assinaturasComFasciculosPendentes) {
			if(a.getId() == idAssinaturaSelecionada){
				assinaturaSelecionada = a;
				break;
			}
		}
		
		FasciculoDao fasciculoDao = null;
		
		try {
			fasciculoDao = getDAO(FasciculoDao.class);
		
			// Object � um array
			// [0] tem um fasc�culo, na segunda
			// [1] tem id do registro de transfer�ncia
			// [2] tem o nome do usu�rio que solicitou a transfer�ncia
			List<Object> informacoesTransferencia = fasciculoDao
				.encontraFasciculosPendentesTraferenciaDaAssinaturaSelecionada(assinaturaSelecionada.getId(), obj.getUnidadeDestino().getId());
			
			for (Object object : informacoesTransferencia) {
				
				Object[] array = (Object[]) object;
				
				Fasciculo f = (Fasciculo) array[0];
				f.setIdentificador( (Integer) array[1]);
				f.setNomeUsuario( (String) array[2]);
				f.setOpcaoSelecao(OperacaoAutoridazacaoTransferenciaFasciculos.AGUARDAR.ordinal());
				fasciculosPendente.add(f);
				
			}
			
			/* ************************************************************
			 * Ordena os fasc�culos pela biblioteca, assinatura, anoCronologico e n�mero
			 * **************************************************************/
			Collections.sort(fasciculosPendente, new FasciculoByBibliotecaAssinaturaAnoCronologicoNumeroComparator());
			
		}finally{
			if(fasciculoDao != null) fasciculoDao.close();
		}
		
		

		return telaAutorizaTransferenciaFasciculos();
	}
	
	
	
	/**
	 *    M�todo chamado para iniciar o caso de uso de criar a assinatura para os fasc�culos que foi
	 * solicitado a transfer�ncia mas a biblioteca n�o tinha a assinatura para eles.
	 * 
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
	 */
	public String chamarCasoDeUsoCriacaoAssinaturaParaTransferencia()  throws ArqException{
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		
		int idRegistroMovimentacao =  getParameterInt("idRegistroMovimentacaoSelecionado", 0);
		
		String tituloDoTituloDoFasciculo = "";
		
		
		 for (DadosTransferenciaSemAssinatura dado : dadosMovimentacaoSemAssinatura) {
			
			 if(dado.getIdRegistroMovimentacao() == idRegistroMovimentacao){
				 	idTituloDoFasciculos = dado.getIdTitulo();
				 	tituloDoTituloDoFasciculo = dado.getTitulo();
				 	dadoTransferenciaSelecinado = dado;
				 	break;
			 }
			 
		}
		
		idBibliotecaDestino =  obj.getUnidadeDestino().getId();
		 
		return bean.iniciarPesquisaCriacaoAssinaturaFasciculosTransferidos(this, idBibliotecaDestino, idTituloDoFasciculos, tituloDoTituloDoFasciculo);
	
	}
	
	
	
	/**
	 *    Cancela a transfer�ncia dos fasc�culos cuja a transfer�ncia foi solicitada sem assinatura.
	 * 
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
	 */
	public String cancelarTransferenciaFasiculosSemAssinatura()  throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		
		/* *******************************************************************************************
		 *  Obt�m todos os fasc�culos pendentes para a mesma biblioteca destino e T�tulo do Registro *
		 * de Movimenta��o selecionado e cancela as suas transfer�ncias                              *
		 *                                                                                           *
		 * *******************************************************************************************/
		
		int idRegistroMovimentacao =  getParameterInt("idRegistroMovimentacaoSelecionado", 0);
		int idTituloDoFasciculos = 0;

		 for (DadosTransferenciaSemAssinatura dado : dadosMovimentacaoSemAssinatura) {
			
			 if(dado.getIdRegistroMovimentacao() == idRegistroMovimentacao){
				 	idTituloDoFasciculos = dado.getIdTitulo();
			 }
			 
		}
		
		List<Fasciculo> fasciculosPendentesCancelados = new ArrayList<Fasciculo>();
		 
		List<Object> objetosPendentesCancelados = getDAO(FasciculoDao.class)
			.encontraFasciculosPendentesTransferenciaByBiblioteANDTitulo(obj.getUnidadeDestino().getId(), idTituloDoFasciculos);
		
		for (Object object : objetosPendentesCancelados) {
			
			Object[] array = (Object[]) object;
			
			Fasciculo f = (Fasciculo) array[0];
			int idRegistro = (Integer) array[1];
			f.setIdentificador(idRegistro);
			f.setOpcaoSelecao(OperacaoAutoridazacaoTransferenciaFasciculos.NAO.ordinal());
			f.setInformacao(" Cancelado ");
			fasciculosPendente.add(f);
			
			fasciculosPendentesCancelados.add(f);
			
		}
		
		MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas mov = new
		MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas(fasciculosPendentesCancelados, false);
		mov.setCodMovimento(SigaaListaComando.CONFIRMA_TRANSFERENCIA_FASCICULOS_ENTRE_BIBLIOTECAS);
		
		try {
			
			List<String> codigoBarrasFasciculosTransferidos = execute(mov);
			
			addMensagemInformation(formataMensagemUsuario(codigoBarrasFasciculosTransferidos));
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return cancelar();
		
	}
	
	
	
	/**
	 *   M�todo que vai realizar a transfer�ncia definitiva dos fasc�culos.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
	 */
	public String realizarTransferenciaFasciculos() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Verifica o valor passado como par�metro, pois foi usado radio buttons comuns da p�gina //
		for (Fasciculo fasciculo : fasciculosPendente) {
			String valorRadioSelecionado = getCurrentRequest().getParameter(""+fasciculo.getId());
			if("SIM".equalsIgnoreCase(valorRadioSelecionado))
				fasciculo.setOpcaoSelecao(OperacaoAutoridazacaoTransferenciaFasciculos.SIM.ordinal());
			else
				if("NAO".equalsIgnoreCase(valorRadioSelecionado))
					fasciculo.setOpcaoSelecao(OperacaoAutoridazacaoTransferenciaFasciculos.NAO.ordinal());
				else
					fasciculo.setOpcaoSelecao(OperacaoAutoridazacaoTransferenciaFasciculos.AGUARDAR.ordinal());
		}
		
		MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas mov = new
			MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas(fasciculosPendente, codigoDeBarrasAcompanhaCodigoNovaAssinatura);
		mov.setCodMovimento(SigaaListaComando.CONFIRMA_TRANSFERENCIA_FASCICULOS_ENTRE_BIBLIOTECAS);
		
		try {
			
			List<String> codigoBarrasFasciculosTransferidos = execute(mov);
			
			addMensagemInformation(formataMensagemUsuario(codigoBarrasFasciculosTransferidos));
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return cancelar();
	}
	
	
	
	/**
	 * Formata a mensagem do usu�rio para mostra quais fasc�culos foram transferidos e quais n�o;
	 */
	private String formataMensagemUsuario(List<String> codigoBarrasFasciculosTransferidos){
		
		StringBuilder mensagemUsuario = new StringBuilder();
		
		if(codigoBarrasFasciculosTransferidos.size() > 0){
			
			mensagemUsuario.append("Fasc�culos: ");
			
			for (int ptr = 0 ; ptr < codigoBarrasFasciculosTransferidos.size(); ptr++) {
				if(ptr == codigoBarrasFasciculosTransferidos.size()-1)
					mensagemUsuario.append(codigoBarrasFasciculosTransferidos.get(ptr));
				else
					mensagemUsuario.append(codigoBarrasFasciculosTransferidos.get(ptr)+", ");
			}
			
			mensagemUsuario.append(" transferido(s) com sucesso. <br/>");
			
		}
		
		if( fasciculosPendente.size() > codigoBarrasFasciculosTransferidos.size() ){
			
			boolean contemTransferenciaRejeitada = false;
			
			for (int ptr = 0 ; ptr < fasciculosPendente.size(); ptr++) {
				if( ! codigoBarrasFasciculosTransferidos.contains(fasciculosPendente.get(ptr).getCodigoBarras())
						&& fasciculosPendente.get(ptr).getOpcaoSelecao().equals(OperacaoAutoridazacaoTransferenciaFasciculos.NAO.ordinal()) ){
					
					if( ! contemTransferenciaRejeitada){
						mensagemUsuario.append(" Fasc�culos: ");
						contemTransferenciaRejeitada = true;
					}
					
					if(ptr == fasciculosPendente.size()-1)
						mensagemUsuario.append(fasciculosPendente.get(ptr).getCodigoBarras());
					else
						mensagemUsuario.append(fasciculosPendente.get(ptr).getCodigoBarras()+", ");
				}
			}
			
			if(contemTransferenciaRejeitada)
				mensagemUsuario.append(" transfer�ncia(s) rejeitada(s). ");
			
		}
		
		if(StringUtils.isEmpty(mensagemUsuario.toString()))
			mensagemUsuario.append("Nenhum a��o foi realizada.");
		
		return mensagemUsuario.toString();
	}
	
	
	
	
	
	
	/**
	 *      <p>Retorna todas as bibliotecas internas ativas do sistema nas quais o usu�rio tem permiss�o
	 *    de autorizar a transfer�ncia de fasc�culos.</p>
	 *       <p>Se for um administrador geral do sistema, vai retornar todas as bibliotecas internas,
	 *       se n�o apenas as biblioteca onde tem permiss�o</p>
	 */
	public Collection <SelectItem> getBibliotecasInternasDoUsuario() throws DAOException{
		
		if(bibliotecasDestinoTransferencia == null){
			bibliotecasDestinoTransferencia = new ArrayList<Biblioteca>();
			
			if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
							getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
				
				bibliotecasDestinoTransferencia  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades);
				
			}else{
				bibliotecasDestinoTransferencia  = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
			}
		
		}
			
		return toSelectItems(bibliotecasDestinoTransferencia, "id", "descricaoCompleta");
	}
	
	
	
	///////////////////// Tela de Navega��o //////////////////////////
	
	/**
	 * M�todo n�o chamado por jsp's
	 */
	public String telaAutorizaTransferenciaFasciculos() {
		return forward(PAGINA_AUTORIZA_TRANSFERENCIA_FASCICULOS);
	}


	/**
	 * M�todo n�o chamado por jsp's
	 */
	public String telaConfirmaAssociacaoAssinaturaJaExistente() {
		return forward(PAGINA_CONFIRMA_ASSOCIACAO_ASSINATURA_JA_EXISTENTE);
	}

	
	
	
	//////////////////// gets e sets /////////////////
	
	public List<Assinatura> getAssinaturasComFasciculosPendentes() {
		return assinaturasComFasciculosPendentes;
	}


	public void setAssinaturaComFasciculosPendentes(List<Assinatura> assinaturasComFasciculosPendentes) {
		this.assinaturasComFasciculosPendentes = assinaturasComFasciculosPendentes;
	}

	public List<Fasciculo> getFasciculosPendente() {
		return fasciculosPendente;
	}

	public void setFasciculosPendente(List<Fasciculo> fasciculosPendente) {
		this.fasciculosPendente = fasciculosPendente;
	}

	public Integer getSIM() {
		return OperacaoAutoridazacaoTransferenciaFasciculos.SIM.ordinal();
	}

	public Integer getNAO() {
		return OperacaoAutoridazacaoTransferenciaFasciculos.NAO.ordinal();
	}

	public Integer getAGUARDAR() {
		return OperacaoAutoridazacaoTransferenciaFasciculos.AGUARDAR.ordinal();
	}

	public List<DadosTransferenciaSemAssinatura> getDadosMovimentacaoSemAssinatura() {
		return dadosMovimentacaoSemAssinatura;
	}
	
	public Assinatura getAssinaturaSelecionada() {
		return assinaturaSelecionada;
	}

	public void setDadosMovimentacaoSemAssinatura(List<DadosTransferenciaSemAssinatura> dadosMovimentacaoSemAssinatura) {
		this.dadosMovimentacaoSemAssinatura = dadosMovimentacaoSemAssinatura;
	}

	public Assinatura getAssinaturaJaExistenteSelecionada() {
		return assinaturaJaExistenteSelecionada;
	}

	public void setAssinaturaJaExistenteSelecionada(Assinatura assinaturaJaExistenteSelecionada) {
		this.assinaturaJaExistenteSelecionada = assinaturaJaExistenteSelecionada;
	}


	public DadosTransferenciaSemAssinatura getDadoTransferenciaSelecinado() {
		return dadoTransferenciaSelecinado;
	}

	public void setDadoTransferenciaSelecinado(DadosTransferenciaSemAssinatura dadoTransferenciaSelecinado) {
		this.dadoTransferenciaSelecinado = dadoTransferenciaSelecinado;
	}

	public boolean isCodigoDeBarrasAcompanhaCodigoNovaAssinatura() {
		return codigoDeBarrasAcompanhaCodigoNovaAssinatura;
	}

	public void setCodigoDeBarrasAcompanhaCodigoNovaAssinatura(boolean codigoDeBarrasAcompanhaCodigoNovaAssinatura) {
		this.codigoDeBarrasAcompanhaCodigoNovaAssinatura = codigoDeBarrasAcompanhaCodigoNovaAssinatura;
	}


	/**
	 * 
	 * M�todo onde o usu�rio confirm a associa��o entra a assinatura criada e a transfer�ncia que n�o tinha assinatura ainda.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String confirmaAssociacaoAssinaturaJaExistenteTransferencia() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		MovimentoAssociaAssinaturaTransferenciaFasciculos mov
		= new MovimentoAssociaAssinaturaTransferenciaFasciculos(assinaturaJaExistenteSelecionada, idBibliotecaDestino, idTituloDoFasciculos, assinaturaJaExistenteSelecionada.getId());
		
		mov.setCodMovimento(SigaaListaComando.ASSOCIA_ASSINATURA_TRANSFERENCIA_FASCIULOS);
		
		try {
			
			execute(mov);
			
			addMensagemInformation("Associa��o realizada com sucesso.");
			
			return iniciarAutorizacaoTranferenciaAposCriacaoAssinatura(idBibliotecaDestino);
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	

	////////////////  M�todos da interface de busca de assinaturas //////////////////////////
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.assinaturaJaExistenteSelecionada = assinatura;
		
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 *<br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#selecionaAssinatura()
	 */
	@Override
	public String selecionaAssinatura() throws ArqException {
		
		prepareMovimento(SigaaListaComando.ASSOCIA_ASSINATURA_TRANSFERENCIA_FASCIULOS);
		
		GenericDAO dao = null;
		try{ 
			dao = getGenericDAO();
			this.assinaturaJaExistenteSelecionada = dao.refresh(this.assinaturaJaExistenteSelecionada); 
		} finally{ if(dao != null) dao.close(); }
		
		return telaConfirmaAssociacaoAssinaturaJaExistente();
	}
	
	


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 *<br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return telaAutorizaTransferenciaFasciculos();
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return true;
	}
	
	/////////////////////////////////////////////////////////////////////////
	
}

