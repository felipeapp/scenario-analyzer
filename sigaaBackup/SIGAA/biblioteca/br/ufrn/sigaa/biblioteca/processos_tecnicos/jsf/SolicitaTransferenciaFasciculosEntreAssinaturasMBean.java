/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 *        <p>MBean que gerencia a parte de transferência de fascículos entre assinaturas,
 *  transferindo os fascículos para uma assinatura de outra biblioteca, irá consequentemente
 *  transferir o fascículo de biblioteca. Pois o fascículo deve possuir a mesma biblioteca da
 *  sua assinatura.</p>
 * 
 *      <p>Para serem transferidos de fato, a transferência precisa ser autorizada pelo bibliotecário da biblioteca destino. </p>
 *
 * @author Jadson
 * @since 22/10/2009
 * @version 1.0 criação da classe
 * @see AutorizaTransferenciaFasciculosEntreAssinaturasMBean
 */
@Component("solicitaTransferenciaFasciculosEntreAssinaturasMBean")
@Scope("request")
public class SolicitaTransferenciaFasciculosEntreAssinaturasMBean extends SigaaAbstractController<Assinatura> implements PesquisarAcervoAssinaturas{
	
	
	/** Página para seleção de quais fascículos serão transferidos. */
	public static final String PAGINA_SELECIONA_FASCICULOS_TRANSFERENCIA =
			"/biblioteca/processos_tecnicos/outras_operacoes/paginaSelecionaFasciculosParaTransferencia.jsp";
	/** Página para confirmação da transferência/pedido de transferência. */
	public static final String PAGINA_CONFIRMA_TRANSFERENCIA_FASCICULOS =
			"/biblioteca/processos_tecnicos/outras_operacoes/paginaConfirmaTransferenciaFasciculos.jsp";
	

	
	/** Private assinatura selecionado pelo usuário, cujos fascículos ele deseja transferir */
	private Assinatura assinaturaSelecionada;
	
	/** Guarda os fascículos que o usuário vai selecionar para transferir */
	private List<Fasciculo> fasciculosDaAssinaturaSelecionada = new ArrayList<Fasciculo>();
	
	
	/** A biblioteca para onde os fascículos vão. Escolhida pelo usuário na tela de confirmação. */
	private Biblioteca bibliotecaDestino = new Biblioteca(-1);
	
	
	/** Assinaturas resultado da busca das possíveis assinaturas destino dos fascículos. */
	private List<Assinatura> assinaturasDestino = new ArrayList<Assinatura>();
	
	
	/**
	 * A biblioteca destino não possui assinatura para os fascículos, e o bibliotecário que vai transferir
	 * os fascículos solicitou que ela seja criada no momento em que for ser feita a autorização da transferência.
	 * Para não ter que parar o processo e pedir a criação da assinatura e depois ter que voltar ao ponto que parou.
	 */
	private boolean solicitarCriacaoAssinatura = false;
	
	
	/** Usado para filtrar os fascículos pelo ano, pois em alguns casos,  a quantidade é muito grande */
	protected Integer anoPesquisaFasciculos = CalendarUtils.getAnoAtual();     
	
	
	/**
	 *    Inicia o caso de uso de transferência dos fascículos redirecionando o usuário para a tela onde
	 *  ele irá escolher a assinatura origem dos fascículos.
	 * 
	 *     Chamado a partir da página: /sigaa.war/biblioteca/menu/processos_tecnicos.jsp
	 */
	public String iniciarTransferencia() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		bean.setApenasUnidadePermissaoUsuario(true);
		return bean.iniciarPesquisaSelecionarAssinatura(this);
		
	}

	
	
	/**
	 *   Verifica qual de qual ano o usuário que visualizar os fascículos do Título 
	 *
	 * Chamado a partir da página:  /sigaa.war/biblioteca/processos_tecnicos/paginaSelecionaFasciculosParaTransferencia.jsp
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
	 * Realiza as operações quando o usuário seleciona uma assinatura, sem redicionar para nenhuma página.
	 *
	 * @void
	 */
	private void realizaAcoesAoSelecionarAssinatura() throws DAOException{
		
		FasciculoDao fasciculoDao = getDAO(FasciculoDao.class);
		fasciculosDaAssinaturaSelecionada = fasciculoDao.findTodosFasciculosAtivosDaAssinatura(assinaturaSelecionada.getId(), anoPesquisaFasciculos);
		
		if(fasciculosDaAssinaturaSelecionada.size() > 0){
			
			MaterialInformacionalDao materialDao = getDAO(MaterialInformacionalDao.class);
			
			// Verifica se o fascículo já não se encontra pendente em outra transferência,
			// porque se sim, não pode ser transferido novamente.
			for (Fasciculo f : fasciculosDaAssinaturaSelecionada) {
				
				if(materialDao.materialEstaPendenteDeTransferenciaEntreBibliotecas(f.getId()))
					f.setDesabilitado(true);
			}
		}
	}
	
	
	////////////////////  Métodos da interface de busca //////////////////////////
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.assinaturaSelecionada = assinatura;
		
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#selecionaAssinatura()
	 */
	@Override
	public String selecionaAssinatura() throws ArqException {
		realizaAcoesAoSelecionarAssinatura();
		return telaSelecionaFasciculosTransferencia();
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return null;
	}



	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return false;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 *      Vai para a página onde o usuário vai escolher a assinatura destino dos fascículos para a
	 *   realização da transferência.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/paginaSelecionaFasciculosParaTransferencia.jsp
	 */
	public String preTransferirFasciculos() throws ArqException{
		
		prepareMovimento(SigaaListaComando.TRANSFERE_FASCICULOS_ENTRE_BIBLIOTECAS);
		
		
		// Para o usuário sempre selecionar a biblioteca destino quando entra nessa página //
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
			addMensagemErro("Selecione os fascículos para a transferência.");
			return telaSelecionaFasciculosTransferencia();
		}
		
	}
	
	
	
	
	
	/**
	 *    Realiza a transferência dos fascículos entre as assinaturas selecionadas
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/paginaConfirmaTransferenciaFasciculos.jsp
	 */
	public String transferirFasciculos() throws ArqException{
		
		Assinatura assinaturaDestino = null;
		
		for (Assinatura a : assinaturasDestino) {
			if(a.isSelecionada())
				assinaturaDestino = a;
		}
		
		
		// os fascículos que o usuário selecionou para a transferência
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
	 *    <p> Retorna todas as assinaturas da biblioteca destino selecionada pelo usuário que possuam o
	 * mesmo título na assinatura origem dos fascículos.</p>
	 *    
	 *    <p> São as assinaturas para as quais os fascículos podem ser transferidos. </p>
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/alteraAssinaturaFasciculo.jsp
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
			addMensagemWarning("Não Foram Encontradas Assinaturas para a biblioteca seleciona pertencendo ao mesmo Título dos Fascículos.");
		
		Collections.sort(assinaturasDestino, new AssinaturaByTituloComparator());
		
		
	}
	
	
	
	
	

	/**
	 *    Retorna todas as bibliotecas internas ativas do sistema. São as únicas para as quais
	 *  pode-se criar assinaturas.
	 */
	public Collection <SelectItem> getBibliotecasInternas() throws DAOException{
		
		Collection <Biblioteca> b = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		return toSelectItems(b, "id", "descricaoCompleta");
	}
	

	/**
	 *   Retorna os fascículos que o usuário selecionou e vão ser transferidos, para o usuário conferir.
	 *
	 *	 Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/paginaConfirmaTransferenciaFasciculos.jsp
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
	 * Retorna a quantidade de fascículos retornados para a assintura selecionado e o ano.
	 *
	 * @int
	 */
	public int getQuantidadeFasciculosDaAssinaturaSelecionada(){
		if(fasciculosDaAssinaturaSelecionada != null)
			return fasciculosDaAssinaturaSelecionada.size();
		else
			return 0;
	}
	
	
	//////////////////// Tela de navegação       ////////////////////////



	/**
	 * Vai para a tela de seleção do fascículo que será transferido.
	 * <p>
	 * Não é chamado por nenhuma JSP.
	 */
	public String telaSelecionaFasciculosTransferencia() {
		return forward(PAGINA_SELECIONA_FASCICULOS_TRANSFERENCIA);
	}
	
	/**
	 * Vai para a tela de confirmação da transferência.
	 * <p>
	 * Não é chamado por nenhuma JSP.
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
