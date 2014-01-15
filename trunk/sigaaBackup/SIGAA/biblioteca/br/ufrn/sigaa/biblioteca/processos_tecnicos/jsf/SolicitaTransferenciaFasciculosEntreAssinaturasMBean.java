/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/10/2009
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

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.aquisicao.jsf.AssinaturaPeriodicoMBean;
import br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoTransfereFasciculosEntreBibliotecas;
import br.ufrn.sigaa.biblioteca.util.AssinaturaByTituloComparator;

/**
 *        <p>MBean que gerencia a parte de transfer�ncia de fasc�culos entre assinaturas,
 *  transferindo os fasc�culos para uma assinatura de outra biblioteca, ir� consequentemente
 *  transferir o fasc�culo de biblioteca. Pois o fasc�culo deve possuir a mesma biblioteca da
 *  sua assinatura.</p>
 * 
 *      <p>Para serem transferidos de fato, a transfer�ncia precisa ser autorizada pelo bibliotec�rio da biblioteca destino. </p>
 *
 * @author Jadson
 * @since 22/10/2009
 * @version 1.0 cria��o da classe
 * @see AutorizaTransferenciaFasciculosEntreAssinaturasMBean
 */
@Component("solicitaTransferenciaFasciculosEntreAssinaturasMBean")
@Scope("request")
public class SolicitaTransferenciaFasciculosEntreAssinaturasMBean extends SigaaAbstractController<Assinatura> implements PesquisarAcervoAssinaturas{
	
	
	/** P�gina para sele��o de quais fasc�culos ser�o transferidos. */
	public static final String PAGINA_SELECIONA_FASCICULOS_TRANSFERENCIA =
			"/biblioteca/processos_tecnicos/outras_operacoes/paginaSelecionaFasciculosParaTransferencia.jsp";
	/** P�gina para confirma��o da transfer�ncia/pedido de transfer�ncia. */
	public static final String PAGINA_CONFIRMA_TRANSFERENCIA_FASCICULOS =
			"/biblioteca/processos_tecnicos/outras_operacoes/paginaConfirmaTransferenciaFasciculos.jsp";
	

	
	/** Private assinatura selecionado pelo usu�rio, cujos fasc�culos ele deseja transferir */
	private Assinatura assinaturaSelecionada;
	
	/** Guarda os fasc�culos que o usu�rio vai selecionar para transferir */
	private List<Fasciculo> fasciculosDaAssinaturaSelecionada = new ArrayList<Fasciculo>();
	
	
	/** A biblioteca para onde os fasc�culos v�o. Escolhida pelo usu�rio na tela de confirma��o. */
	private Biblioteca bibliotecaDestino = new Biblioteca(-1);
	
	
	/** Assinaturas resultado da busca das poss�veis assinaturas destino dos fasc�culos. */
	private List<Assinatura> assinaturasDestino = new ArrayList<Assinatura>();
	
	
	/**
	 * A biblioteca destino n�o possui assinatura para os fasc�culos, e o bibliotec�rio que vai transferir
	 * os fasc�culos solicitou que ela seja criada no momento em que for ser feita a autoriza��o da transfer�ncia.
	 * Para n�o ter que parar o processo e pedir a cria��o da assinatura e depois ter que voltar ao ponto que parou.
	 */
	private boolean solicitarCriacaoAssinatura = false;
	
	
	/** Usado para filtrar os fasc�culos pelo ano, pois em alguns casos,  a quantidade � muito grande */
	protected Integer anoPesquisaFasciculos = CalendarUtils.getAnoAtual();     
	
	
	/**
	 *    Inicia o caso de uso de transfer�ncia dos fasc�culos redirecionando o usu�rio para a tela onde
	 *  ele ir� escolher a assinatura origem dos fasc�culos.
	 * 
	 *     Chamado a partir da p�gina: /sigaa.war/biblioteca/menu/processos_tecnicos.jsp
	 */
	public String iniciarTransferencia() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		bean.setApenasUnidadePermissaoUsuario(true);
		return bean.iniciarPesquisaSelecionarAssinatura(this);
		
	}

	
	
	/**
	 *   Verifica qual de qual ano o usu�rio que visualizar os fasc�culos do T�tulo 
	 *
	 * Chamado a partir da p�gina:  /sigaa.war/biblioteca/processos_tecnicos/paginaSelecionaFasciculosParaTransferencia.jsp
	 *
	 * @param evt
	 * @throws ArqException 
	 */
	public String verificaAlteracaoFiltroAno() throws ArqException{
		System.out.println("----> "+anoPesquisaFasciculos);
		realizaAcoesAoSelecionarAssinatura();
		return null;
	}
	
	
	
	/**
	 * Realiza as opera��es quando o usu�rio seleciona uma assinatura, sem redicionar para nenhuma p�gina.
	 *
	 * @void
	 */
	private void realizaAcoesAoSelecionarAssinatura() throws DAOException{
		
		FasciculoDao fasciculoDao = getDAO(FasciculoDao.class);
		fasciculosDaAssinaturaSelecionada = fasciculoDao.findTodosFasciculosAtivosDaAssinatura(assinaturaSelecionada.getId(), anoPesquisaFasciculos);
		
		if(fasciculosDaAssinaturaSelecionada.size() > 0){
			
			MaterialInformacionalDao materialDao = getDAO(MaterialInformacionalDao.class);
			
			// Verifica se o fasc�culo j� n�o se encontra pendente em outra transfer�ncia,
			// porque se sim, n�o pode ser transferido novamente.
			for (Fasciculo f : fasciculosDaAssinaturaSelecionada) {
				
				if(materialDao.materialEstaPendenteDeTransferenciaEntreBibliotecas(f.getId()))
					f.setDesabilitado(true);
			}
		}
	}
	
	
	////////////////////  M�todos da interface de busca //////////////////////////
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.assinaturaSelecionada = assinatura;
		
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#selecionaAssinatura()
	 */
	@Override
	public String selecionaAssinatura() throws ArqException {
		realizaAcoesAoSelecionarAssinatura();
		return telaSelecionaFasciculosTransferencia();
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return null;
	}



	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return false;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 *      Vai para a p�gina onde o usu�rio vai escolher a assinatura destino dos fasc�culos para a
	 *   realiza��o da transfer�ncia.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/paginaSelecionaFasciculosParaTransferencia.jsp
	 */
	public String preTransferirFasciculos() throws ArqException{
		
		prepareMovimento(SigaaListaComando.TRANSFERE_FASCICULOS_ENTRE_BIBLIOTECAS);
		
		
		// Para o usu�rio sempre selecionar a biblioteca destino quando entra nessa p�gina //
		bibliotecaDestino = new Biblioteca(-1);
		                                       
		assinaturasDestino = new ArrayList<Assinatura>();
		
		boolean selecionouAlgumFasciculo = false;
		
		for (Fasciculo fasciculo : fasciculosDaAssinaturaSelecionada) {
			if(fasciculo.isSelecionado()){
				selecionouAlgumFasciculo = true;
				break;
			}
		}
		
		if(selecionouAlgumFasciculo)
			return telaConfirmaTransfeenciaFasciculos();
		else{
			addMensagemErro("Selecione os fasc�culos para a transfer�ncia.");
			return telaSelecionaFasciculosTransferencia();
		}
		
	}
	
	
	
	
	
	/**
	 *    Realiza a transfer�ncia dos fasc�culos entre as assinaturas selecionadas
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/paginaConfirmaTransferenciaFasciculos.jsp
	 */
	public String transferirFasciculos() throws ArqException{
		
		Assinatura assinaturaDestino = null;
		
		for (Assinatura a : assinaturasDestino) {
			if(a.isSelecionada())
				assinaturaDestino = a;
		}
		
		
		// os fasc�culos que o usu�rio selecionou para a transfer�ncia
		List<Fasciculo> fasciculosTransferencia = new ArrayList<Fasciculo>();
		
		
		for (Fasciculo fasciculo : fasciculosDaAssinaturaSelecionada) {
			if(fasciculo.isSelecionado())
				fasciculosTransferencia.add(fasciculo);
		}
		
		MovimentoTransfereFasciculosEntreBibliotecas movimento
			= new MovimentoTransfereFasciculosEntreBibliotecas(fasciculosTransferencia,
					assinaturaSelecionada, assinaturaDestino, solicitarCriacaoAssinatura, bibliotecaDestino);
		
		movimento.setCodMovimento(SigaaListaComando.TRANSFERE_FASCICULOS_ENTRE_BIBLIOTECAS);
		
		try {
		
			String mensagemUsuario =  execute(movimento);
			
			addMensagemInformation(mensagemUsuario);
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		
		return cancelar();
	}
	
	
	
	
	
	/**
	 *    <p> Retorna todas as assinaturas da biblioteca destino selecionada pelo usu�rio que possuam o
	 * mesmo t�tulo na assinatura origem dos fasc�culos.</p>
	 *    
	 *    <p> S�o as assinaturas para as quais os fasc�culos podem ser transferidos. </p>
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/alteraAssinaturaFasciculo.jsp
	 */
	public void buscaAllAssinaturasDaBibliotecaSelecionadaQuePossuamMesmoTitulo(ValueChangeEvent evt) throws DAOException{
		
		assinaturasDestino = new ArrayList<Assinatura>();
		
		bibliotecaDestino.setId( (Integer) evt.getNewValue());
		
		if(bibliotecaDestino.getId() == -1){
			return;
		}
		
		AssinaturaDao dao = getDAO(AssinaturaDao.class);
		
		assinaturaSelecionada = dao.refresh(assinaturaSelecionada);
		
		assinaturasDestino = dao.findAssinaturasPossiveisTransferenciaFasciculosByUnidadeDestino
				(assinaturaSelecionada.getTituloCatalografico().getId(), bibliotecaDestino.getId());
		
		for (Assinatura a : assinaturasDestino) {
			a.setNomeCriador( dao.findNomeCriadorAssinatura(a.getId()));
		}
		
		if(assinaturasDestino.size() == 0)
			addMensagemWarning("N�o Foram Encontradas Assinaturas para a biblioteca seleciona pertencendo ao mesmo T�tulo dos Fasc�culos.");
		
		Collections.sort(assinaturasDestino, new AssinaturaByTituloComparator());
		
		
	}
	
	
	
	
	

	/**
	 *    Retorna todas as bibliotecas internas ativas do sistema. S�o as �nicas para as quais
	 *  pode-se criar assinaturas.
	 */
	public Collection <SelectItem> getBibliotecasInternas() throws DAOException{
		
		Collection <Biblioteca> b = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		return toSelectItems(b, "id", "descricaoCompleta");
	}
	

	/**
	 *   Retorna os fasc�culos que o usu�rio selecionou e v�o ser transferidos, para o usu�rio conferir.
	 *
	 *	 Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/paginaConfirmaTransferenciaFasciculos.jsp
	 */
	public List<Fasciculo> getFasciculosTransferencia() {
		
		List<Fasciculo> fasciculosTransferencia = new ArrayList<Fasciculo>();
		
		for (Fasciculo fasciculo : fasciculosDaAssinaturaSelecionada) {
			if(fasciculo.isSelecionado())
				fasciculosTransferencia.add(fasciculo);
		}
		
		return fasciculosTransferencia;
		
	}
	
	/**
	 * Retorna a quantidade de fasc�culos retornados para a assintura selecionado e o ano.
	 *
	 * @int
	 */
	public int getQuantidadeFasciculosDaAssinaturaSelecionada(){
		if(fasciculosDaAssinaturaSelecionada != null)
			return fasciculosDaAssinaturaSelecionada.size();
		else
			return 0;
	}
	
	
	//////////////////// Tela de navega��o       ////////////////////////



	/**
	 * Vai para a tela de sele��o do fasc�culo que ser� transferido.
	 * <p>
	 * N�o � chamado por nenhuma JSP.
	 */
	public String telaSelecionaFasciculosTransferencia() {
		return forward(PAGINA_SELECIONA_FASCICULOS_TRANSFERENCIA);
	}
	
	/**
	 * Vai para a tela de confirma��o da transfer�ncia.
	 * <p>
	 * N�o � chamado por nenhuma JSP.
	 */
	public String telaConfirmaTransfeenciaFasciculos() {
		return forward(PAGINA_CONFIRMA_TRANSFERENCIA_FASCICULOS);
	}

	
	/////////////////////////////////////////////////////////////////////
	
	
	
	
	/// sets e gets ///

	public Assinatura getAssinaturaSelecionada() {
		return assinaturaSelecionada;
	}

	public void setAssinaturaSelecionada(Assinatura assinaturaSelecionada) {
		this.assinaturaSelecionada = assinaturaSelecionada;
	}

	public List<Fasciculo> getFasciculosDaAssinaturaSelecionada() {
		return fasciculosDaAssinaturaSelecionada;
	}

	public void setFasciculosDaAssinaturaSelecionada(List<Fasciculo> fasciculosDaAssinaturaSelecionada) {
		this.fasciculosDaAssinaturaSelecionada = fasciculosDaAssinaturaSelecionada;
	}

	public Biblioteca getBibliotecaDestino() {
		return bibliotecaDestino;
	}

	public void setBibliotecaDestino(Biblioteca bibliotecaDestino) {
		this.bibliotecaDestino = bibliotecaDestino;
	}

	public List<Assinatura> getAssinaturasDestino() {
		return assinaturasDestino;
	}

	public void setAssinaturasDestino(List<Assinatura> assinaturasDestino) {
		this.assinaturasDestino = assinaturasDestino;
	}

	public boolean isSolicitarCriacaoAssinatura() {
		return solicitarCriacaoAssinatura;
	}

	public void setSolicitarCriacaoAssinatura(boolean solicitarCriacaoAssinatura) {
		this.solicitarCriacaoAssinatura = solicitarCriacaoAssinatura;
	}

	public Integer getAnoPesquisaFasciculos() {
		return anoPesquisaFasciculos;
	}

	public void setAnoPesquisaFasciculos(Integer anoPesquisaFasciculos) {
		this.anoPesquisaFasciculos = anoPesquisaFasciculos;
	}
	
}
