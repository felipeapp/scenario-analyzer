/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 *     Depois que o bibliotecário solicitou a transferência dos fascículos, se a transferência foi para outra biblioteca,
 *     eles não são transferidos de imediato, ficam pendentes. <br/>
 *     É necessário um bibliotecário da biblioteca destino da transferência conferir e autorizar a transferência
 * para que ela seja concluída.<br/><br/>
 *     Esse MBean gerencia esse caso de uso (de conferir e autorizar a transferência de fascículos entre bibliotecas).
 *
 * @author Jadson
 * @since 26/11/2009
 * @version 1.0 Criação da classe
 *
 */
@Component("autorizaTransferenciaFasciculosEntreAssinaturasMBean")
@Scope("request")
public class AutorizaTransferenciaFasciculosEntreAssinaturasMBean extends SigaaAbstractController<Assinatura> implements PesquisarAcervoAssinaturas{

	
	/** Página onde o usuário autoriza as pendências */
	public static final String PAGINA_AUTORIZA_TRANSFERENCIA_FASCICULOS = "/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp";
	
	/** Página onde o usuário confirma a associação da assinatura selecionada com a transferência que estava sem a assinatura destino. */
	public static final String PAGINA_CONFIRMA_ASSOCIACAO_ASSINATURA_JA_EXISTENTE = "/biblioteca/processos_tecnicos/outras_operacoes/confirmaAssociacaoAssinaturaJaExistenteTransferenciaFasciculos.jsp";
	
	/**
	 * As biblioteca nas quais o usuário tem permissão para autorizar a transferência dos fascículos //
	 */
	private Collection <Biblioteca> bibliotecasDestinoTransferencia;
	
	/**
	 * As assinatura que possuem fascículos pendentes na biblioteca escolhida pelo usuário. //
	 */
	private List<Assinatura> assinaturasComFasciculosPendentes;
	
	/**
	 * Guarda os dados dos fascículos que foram transferidos sem assinaturas //
	 */
	private List<DadosTransferenciaSemAssinatura> dadosMovimentacaoSemAssinatura;
	
	/**
	 * Guarda os fascículos pendentes para o usuário autorizar //
	 */
	private List<Fasciculo> fasciculosPendente;
	
	/**
	 * Variável que armazena a assinatura atualmente selecionada
	 */
	private Assinatura assinaturaSelecionada = null;
	
	
	/** 
	 * Utilizando quando a transferência ocorre sem o usuário informar a assinatura destino da transferência
	 * 
	 * Essa variável contém a assinatura selecionada no momento da autorização da transferência.
	 */
	private Assinatura assinaturaJaExistenteSelecionada = null;
	
	/** Biblioteca de destino da assinatura. */
	private int idBibliotecaDestino;
	/** Título à qual a assinatura será associada. */
	private int idTituloDoFasciculos;
	/** Os dados da transferencia que ocorreu sem assinatura destino que foi selecionado pelo usuário para criar a assinatura. */
	private DadosTransferenciaSemAssinatura dadoTransferenciaSelecinado;
	
	
	/** <p>Informa se o código de barras dos fascículos transferidos serão alterados para ficarem iguais aos códigos de barras 
	 *  dos fascículos da assinatura para onde ele estão sendo transferidos.</p>
	 *  
	 *  <p>Por padrão vai permanecer com os código de barras já existentes. Ou seja, a assinatura destino 1234 pode possuir 
	 *  fascículos: <strong>1234-1, 1234-2, 1234-N,</strong> e os novos fascículos transferidos da assinatura origem 5678:  <strong>5678-1, 5678-2 5678-N</strong>. </p>
	 *  
	 *  <p>Caso o usuário escolhar mudar, os novos códigos de barras dos fascículos da assinatura destino 1234 serão: <strong>1234-1, 1234-2, 1234-N, 1234-N+1, 1234-N+2, 1234-N+3</strong>.</p>
	 */
	private boolean codigoDeBarrasAcompanhaCodigoNovaAssinatura = false;
	
	
	
	/**
	 *    Inicia o caso de uso de autorização da transferência dos fascículos. Para cada fascículo
	 * pendente o usuário tem que escolher se autoriza ou não a transferência. <br/>
	 *    Se não autorizar, vai informar o motivo que vai ser enviado por email ao usuário que fez
	 *  a transferência.<br/>
	 * 
	 *     Chamado a partir da página: /sigaa.war/biblioteca/menu/processos_tecnicos.jsp
	 */
	public String iniciarAutorizacaoTranferencia() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		inicializaDados();
		
		return telaAutorizaTransferenciaFasciculos();
	}
	
	
	/**
	 *    Inicia o caso de uso de autorização da transferência dos fascículos já passando o
	 *  id da biblioteca em que as assinaturas vão ser procuradas.
	 * 
	 *    <p><i> Geralmente usado quando o caso de uso é chamado de outro MBean, por exemplo,
	 *    depois que a assinatura dos fascículos que estavam sem assinatura é criada.</i><p>
	 * 
	 *     Não chamado de nenhum a página JSP.
	 */
	public String iniciarAutorizacaoTranferenciaAposCriacaoAssinatura(int idBiblioteca) throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		inicializaDados();
		
		obj.setUnidadeDestino(  getGenericDAO().refresh(  new Biblioteca(idBiblioteca) )  );
		
		buscarTransferenciasPendentesFasciculos(idBiblioteca);
		
		return telaAutorizaTransferenciaFasciculos();
	}
	
	
	
	
	/**
	 * Inicializa dos dados do formulário de pesquisa
	 */
	private void inicializaDados(){
		obj = new Assinatura();
		obj.setUnidadeDestino(new Biblioteca(-1));
	}
	
	
	
	/**
	 *   Obtém as assinatura que possuem fascículos pendentes a partir da biblioteca escolhida
	 * pelo usuário.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
	 */
	public void verificaAssinaturaPendentesDaBibliotecaEscolhida(ValueChangeEvent evt) throws ArqException{
		
		Integer idBibliotecaEscolhida = (Integer) evt.getNewValue();
	
		buscarTransferenciasPendentesFasciculos(idBibliotecaEscolhida);
		
		assinaturaSelecionada = null;
		
	}
	
	
	
	
	/**
	 *     Busca as transferência pendentes de fascículos para a biblioteca passada.
	 */
	private void buscarTransferenciasPendentesFasciculos(Integer idBibliotecaEscolhida) throws ArqException{

		fasciculosPendente = new ArrayList<Fasciculo>();
		
		dadosMovimentacaoSemAssinatura = new ArrayList<DadosTransferenciaSemAssinatura>();
		
		if(  !idBibliotecaEscolhida.equals(new Integer(-1) ) ) {
			
			AssinaturaDao dao = getDAO(AssinaturaDao.class);
			
			// transferências feitas para uma assinatura específica
			assinaturasComFasciculosPendentes = dao.encontraAssinaturasCujosFasciculosEstaoPendentesTransferencia(idBibliotecaEscolhida);
			
			// transferências feitas sem a biblioteca possuir a assinatura
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
	 *   Método chamado depois que o usuário selecionou a biblioteca e a assinatura dos fascículos pendentes de transferência.<br/>
	 * 
	 *
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
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
		
			// Object é um array
			// [0] tem um fascículo, na segunda
			// [1] tem id do registro de transferência
			// [2] tem o nome do usuário que solicitou a transferência
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
			 * Ordena os fascículos pela biblioteca, assinatura, anoCronologico e número
			 * **************************************************************/
			Collections.sort(fasciculosPendente, new FasciculoByBibliotecaAssinaturaAnoCronologicoNumeroComparator());
			
		}finally{
			if(fasciculoDao != null) fasciculoDao.close();
		}
		
		

		return telaAutorizaTransferenciaFasciculos();
	}
	
	
	
	/**
	 *    Método chamado para iniciar o caso de uso de criar a assinatura para os fascículos que foi
	 * solicitado a transferência mas a biblioteca não tinha a assinatura para eles.
	 * 
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
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
	 *    Cancela a transferência dos fascículos cuja a transferência foi solicitada sem assinatura.
	 * 
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
	 */
	public String cancelarTransferenciaFasiculosSemAssinatura()  throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		
		/* *******************************************************************************************
		 *  Obtém todos os fascículos pendentes para a mesma biblioteca destino e Título do Registro *
		 * de Movimentação selecionado e cancela as suas transferências                              *
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
	 *   Método que vai realizar a transferência definitiva dos fascículos.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAutorizaTransferenciaFasciculos.jsp
	 */
	public String realizarTransferenciaFasciculos() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		// Verifica o valor passado como parâmetro, pois foi usado radio buttons comuns da página //
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
	 * Formata a mensagem do usuário para mostra quais fascículos foram transferidos e quais não;
	 */
	private String formataMensagemUsuario(List<String> codigoBarrasFasciculosTransferidos){
		
		StringBuilder mensagemUsuario = new StringBuilder();
		
		if(codigoBarrasFasciculosTransferidos.size() > 0){
			
			mensagemUsuario.append("Fascículos: ");
			
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
						mensagemUsuario.append(" Fascículos: ");
						contemTransferenciaRejeitada = true;
					}
					
					if(ptr == fasciculosPendente.size()-1)
						mensagemUsuario.append(fasciculosPendente.get(ptr).getCodigoBarras());
					else
						mensagemUsuario.append(fasciculosPendente.get(ptr).getCodigoBarras()+", ");
				}
			}
			
			if(contemTransferenciaRejeitada)
				mensagemUsuario.append(" transferência(s) rejeitada(s). ");
			
		}
		
		if(StringUtils.isEmpty(mensagemUsuario.toString()))
			mensagemUsuario.append("Nenhum ação foi realizada.");
		
		return mensagemUsuario.toString();
	}
	
	
	
	
	
	
	/**
	 *      <p>Retorna todas as bibliotecas internas ativas do sistema nas quais o usuário tem permissão
	 *    de autorizar a transferência de fascículos.</p>
	 *       <p>Se for um administrador geral do sistema, vai retornar todas as bibliotecas internas,
	 *       se não apenas as biblioteca onde tem permissão</p>
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
	
	
	
	///////////////////// Tela de Navegação //////////////////////////
	
	/**
	 * Método não chamado por jsp's
	 */
	public String telaAutorizaTransferenciaFasciculos() {
		return forward(PAGINA_AUTORIZA_TRANSFERENCIA_FASCICULOS);
	}


	/**
	 * Método não chamado por jsp's
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
	 * Método onde o usuário confirm a associação entra a assinatura criada e a transferência que não tinha assinatura ainda.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
			
			addMensagemInformation("Associação realizada com sucesso.");
			
			return iniciarAutorizacaoTranferenciaAposCriacaoAssinatura(idBibliotecaDestino);
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
	}
	
	

	////////////////  Métodos da interface de busca de assinaturas //////////////////////////
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.assinaturaJaExistenteSelecionada = assinatura;
		
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 *<br><br>
	 * Método não chamado por nenhuma página jsp.
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
	 * Ver comentários da classe pai.<br/>
	 *
	 *<br><br>
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return telaAutorizaTransferenciaFasciculos();
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return true;
	}
	
	/////////////////////////////////////////////////////////////////////////
	
}

