/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoSolicitaReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ProcessadorSolicitaReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaInternaBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;

/**
 *
 * <p>Mbean que gerencia a parte de solicita��o de reservas de materiais da biblioteca </p>
 * 
 * @author jadson
 *
 */
@Component("solicitarReservaMaterialBibliotecaMBean")
@Scope("request")
public class SolicitarReservaMaterialBibliotecaMBean  extends SigaaAbstractController <ReservaMaterialBiblioteca> implements PesquisarAcervoBiblioteca{

	/** P�gina que lista todas as reservas que o t�tulo selecionado possui, calcula a previs�o de quando o material estar� dispon�vel e
	 * disponibilizar� um bot�o para o usu�rio confirmar a reserva. 
	 * */
	public static final String PAGINA_CONFIRMA_RESERVA_MATERIAL = "/biblioteca/circulacao/confirmaReservaMaterialBiblioteca.jsp";
	
	
	
	
	/** 
	 * <p>Guarda as informa��es da cataloga��o no acervo da biblioteca escolhida pelos usu�rio.</p> 
	 * <p>A reserva vai ser feita para os materiais dessa cataloga��. </p>
	 */
	private CacheEntidadesMarc cache;
	
	/**
	 * Guarda a descri��o to T�tulo em formato de refer�ncia para mostrar ao usu�rio.
	 */
	private String tituloEmFormatoReferencia;
	
	/**
	 * O usu�rio que vai solicitar a reserva
	 */
	private UsuarioBiblioteca usuarioSolicitador;
	
	
	/** Informa��es sobre o v�nculo ativo do usu�rio que est� realizando a reserva, utilizando quando o operador est� realizar a reserva para um usu�rio. */
	private InformacoesUsuarioBiblioteca informacaoUsuario;
	
	/**
	 * Guarda a lista de reservas j� existentes para o t�tulo selecionado pelo usu�rio
	 */
	private List<ReservaMaterialBiblioteca> reservasJaExistentes;
	
	
	/**
	 * <p>Guarda a data de previs�o para retirada do material.</p> 
	 * <p>Que vai ser calculada com base na quantidade de reservas, no prazo do empr�stimos e na quantidade de renova��es permitidas no sistema. <p>
	 */
	private Date dataPrevisaoEmprestimoMaterial;
	
	/**
	 * Diz se vai habilitar o bot�o de voltar na p�gina de busca do acervo.  Neste caso s� usado quando o usu�rio 
	 * solicitou a reserva a partir da p�gina de visualiza��o das reservas, deve voltar para essa p�gina. 
	 */
	private boolean utilizaBotaoVoltarPaginaBusca = false;
	
	
	/** Guarda se o biblioteca ou o pr�prio usu�rio est� solicitando a reserva*/
	private boolean solicitandoPropriaReserva = false;
	
	
	/**
	 * <p>Inicia o caso de uso de um usu�rio solicitar uma reserva.</p>
	 *
	 * <p><strong>Realiza as verifica��es em rela��o ao usu�rio antes de permitir a reserva</strong></p> 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *    <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 *    <li>/sigaa.war/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarReservaPeloUsuario() throws DAOException{
		
		utilizaBotaoVoltarPaginaBusca = false;
		solicitandoPropriaReserva = true;
		
		return configuraReservaParaUsuarioLogado();
	}

	
	/**
	 * <p>Inicia o caso de uso de um usu�rio solicitar uma reserva na p�gina onde ele visualiza as suas reservas</p>
	 *
	 * 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/visualizaReservasMultarialBibliotecaDeUmUsuario.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarReservaPeloUsuarioDaPaginaVisualizacao() throws DAOException{
		
		utilizaBotaoVoltarPaginaBusca = true;
		solicitandoPropriaReserva = true;
		
		return configuraReservaParaUsuarioLogado();
	}
	
	
	
	/**
	 * <p>Inicia o caso de uso de para um operador de circula��o realizar a reserva para o usu�rio selecionado na pesquisa.
	 *
	 * 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/visualizaReservasMultarialBibliotecaDeUmUsuario.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public String iniciarReservaParaUsuarioSelecionado() throws DAOException, SegurancaException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO);
		
		
		utilizaBotaoVoltarPaginaBusca = true;
		solicitandoPropriaReserva = false;
		
		return configuraReservaParaUsuarioPesquisado();
	}
	
	
	
	
	/**
	 * <p>Inicia o caso de uso de um usu�rio logado solicitar uma reserva.</p>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private String configuraReservaParaUsuarioLogado() throws DAOException{
		
		int idPessoa = getUsuarioLogado().getPessoa().getId();
		
		try {
			
			usuarioSolicitador = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(idPessoa, null);
			
			// Usu�rios bloqueados n�o pode solicitar reservas, n�o precisa testar as demais condi��es
			String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario(usuarioSolicitador);
			if(motivoBloqueio != null){
				addMensagemErro("Caro usu�rio, voc� est� bloqueado para utilizar os servi�os de circula��o da biblioteca, motivo: "+motivoBloqueio);
				return null;
			}
			
			// Usu�rios suspensos ou  com multas n�o pagas n�o podem solicitar reservas
			List<SituacaoUsuarioBiblioteca> situacoes = VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(
					usuarioSolicitador.getIdentificadorPessoa() , usuarioSolicitador.getIdentificadorBiblioteca() );
			
			if(situacoes.size() > 0){
				
				addMensagemErro(ProcessadorSolicitaReservaMaterialBiblioteca.MENAGEM_PADRAO_USUARIO_COM_PEDENCIAS_PARA_SOLICITAR_RESERVA);
				return null;
			}
			
			// Verifica a quantiade de reservas que o usu�rio j� tem
			ReservaMaterialBibliotecaUtil.verificaQuantidadeMaximaDeReservasDoUsuario(usuarioSolicitador.getId());
			
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		PesquisaInternaBibliotecaMBean pBean = getMBean("pesquisaInternaBibliotecaMBean");
		return pBean.iniciarBusca(this, null);
	}
	
	
	
	/**
	 * <p>Inicia o caso de uso de um usu�rio logado solicitar uma reserva.</p>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private String configuraReservaParaUsuarioPesquisado() throws DAOException{
		
		try {
			
			/* 
			 * a vari�vel "usuarioSolicitador" � setado via f:setPropertyActionListener 
			 * na p�gina visualizaReservasMateriaisBibliotecaDeUmUsuario.jsp
			 * com o valor VisualizarReservasMaterialBibliotecaMBean.usuarioDasReservas
			 */
			
			informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioSolicitador, null, null);
			
			// Usu�rios bloqueados n�o pode solicitar reservas, n�o precisa testar as demais condi��es
			String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario(usuarioSolicitador);
			if(motivoBloqueio != null){
				addMensagemErro("Caro usu�rio, voc� est� bloqueado para utilizar os servi�os de circula��o da biblioteca, motivo: "+motivoBloqueio);
				return null;
			}
			
			// Usu�rios suspensos ou  com multas n�o pagas n�o podem solicitar reservas
			List<SituacaoUsuarioBiblioteca> situacoes = VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(	
					usuarioSolicitador.getIdentificadorPessoa() , usuarioSolicitador.getIdentificadorBiblioteca());
			
			if(situacoes.size() > 0){
				
				addMensagemErro(ProcessadorSolicitaReservaMaterialBiblioteca.MENAGEM_PADRAO_USUARIO_COM_PEDENCIAS_PARA_SOLICITAR_RESERVA);
				return null;
			}
			
			// Verifica a quantiade de reservas que o usu�rio j� tem
			ReservaMaterialBibliotecaUtil.verificaQuantidadeMaximaDeReservasDoUsuario(usuarioSolicitador.getId());
			
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		PesquisaInternaBibliotecaMBean pBean = getMBean("pesquisaInternaBibliotecaMBean");
		return pBean.iniciarBusca(this, null);
	}
	
	
	////////////////////////////// Metodos da interface de busca  ///////////////////////////
	
	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return utilizaBotaoVoltarPaginaBusca;
	}

	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	@Override
	public String selecionaTitulo() throws ArqException {
		
		prepareMovimento(SigaaListaComando.SOLICITA_RESERVA_MATERIAL_BIBLIOTECA);
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Realiza as verifica��es em rela��o ao t�tulo escolhido antes de permitir a reserva
		////////////////////////////////////////////////////////////////////////////////////////
		
		ReservaMaterialBibliotecaDao dao = null;
		
		long tempo = System.currentTimeMillis();
		
		try {
			ReservaMaterialBibliotecaUtil.verificaExisteMaterialParaSolicitarReservaNoAcervo(cache.getIdTituloCatalografico());
			
			ReservaMaterialBibliotecaUtil.verificaExisteQuantidadeMinimaParaSolicitarReserva(cache.getIdTituloCatalografico());
			
			// Se tudo ok
			
			dao = getDAO(ReservaMaterialBibliotecaDao.class);
			
			reservasJaExistentes = dao.buscasReservasAtivasTituloEmOrdem(cache.getIdTituloCatalografico());
		
			dataPrevisaoEmprestimoMaterial = ReservaMaterialBibliotecaUtil.calculaPrevisaoEntregaMaterial(cache.getIdTituloCatalografico(), reservasJaExistentes);
			
			tituloEmFormatoReferencia =
				 " N�mero de Registro no Sistema : <strong>"+dao.findByPrimaryKey(cache.getIdTituloCatalografico(), TituloCatalografico.class, "numeroDoSistema").getNumeroDoSistema()+"</strong> <br/> "
				+ new FormatosBibliograficosUtil().gerarFormatoReferencia(new TituloCatalografico(cache.getIdTituloCatalografico()), true);
			
			return telaConfirmaReserva();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			if(dao != null) dao.close();
			
			System.out.println("Verifica��es demoraram:  "+(System.currentTimeMillis()-tempo)+" ms");
		}
		
	}
	

	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	@Override
	public void setTitulo(CacheEntidadesMarc titulo) throws ArqException {
		this.cache = titulo;
	}

	/**
	 *  Ver coment�rios da classe pai.<br/>
	 * 
	 *   <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#voltarBuscaAcervo()
	 */
	@Override
	public String voltarBuscaAcervo() throws ArqException {
		return forward(VisualizarReservasMaterialBibliotecaMBean.PAGINA_VISUALIZA_RESERVAS_MATERIAL_USUARIO);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	/**
	 * <p>M�todo chamado quando o usu�rio realiza a a��o de confirmar a reserva. Vai chamar o processador 
	 * para validar todos as regras de neg�cio de uma reserva e se n�o houve nehum impedimento cria-a.</p>
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/confirmaReservaMaterialBiblioteca.jsp</li>
	 *   </ul>
	 */
	public String solicitarReserva() throws ArqException{
		
		
		MovimentoSolicitaReservaMaterialBiblioteca mov 
			= new MovimentoSolicitaReservaMaterialBiblioteca(cache.getIdTituloCatalografico(), usuarioSolicitador, reservasJaExistentes, dataPrevisaoEmprestimoMaterial);
		
		try {
			
			execute(mov);
			
			
			VisualizarReservasMaterialBibliotecaMBean beanVisualizaReserva = (VisualizarReservasMaterialBibliotecaMBean) getMBean("visualizarReservasMaterialBibliotecaMBean"); 
			
			if(solicitandoPropriaReserva){
				addMensagemInformation("Sua reserva foi confirmada com sucesso. Ser� enviada uma comunica��o eletr�nica quando a reserva solicitada estiver dispon�vel ");
				return beanVisualizaReserva.iniciaVisualizacaoMinhasReservas();
			}else{
				addMensagemInformation("A reserva foi confirmada com sucesso. Ser� enviada uma comunica��o eletr�nica para o usu�rio quando a reserva solicitada estiver dispon�vel ");
				beanVisualizaReserva.setTitulo(cache);
				return beanVisualizaReserva.selecionaTitulo();
			}
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} 
		
		
	}
	
	
	
	/**
	 * Verifica se o sistema est� trabalhando com reservas. Chamado a partir das p�ginas do sistema para habilitar a solicita��o de reservas.
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li> /sigaa.war/portais/discente/menu_discente.jsp </li> 
	 *     <li> /sigaa.war/portais/docente/menu_docente.jsp </li> 
	 *     <li> /sigaa.war/biblioteca/menus/modulo_biblioteca.jsp </li>   
	 *   </ul>
	 *
	 * @return
	 */
	public boolean isSistemaTrabalhaComReservas(){
		return ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas();
	}
	
	
	/**
	 * <p>Redireciona o usu�rio para a p�gina onde ele ir� confirmar a reserva.</p>
	 *
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaConfirmaReserva(){
		return forward(PAGINA_CONFIRMA_RESERVA_MATERIAL);
	}


	public List<ReservaMaterialBiblioteca> getReservasJaExistentes() {
		return reservasJaExistentes;
	}


	public void setReservasJaExistentes(List<ReservaMaterialBiblioteca> reservasJaExistentes) {
		this.reservasJaExistentes = reservasJaExistentes;
	}


	public Date getDataPrevisaoEmprestimoMaterial() {
		return dataPrevisaoEmprestimoMaterial;
	}


	public void setDataPrevisaoEmprestimoMaterial(Date dataPrevisaoEmprestimoMaterial) {
		this.dataPrevisaoEmprestimoMaterial = dataPrevisaoEmprestimoMaterial;
	}


	public String getTituloEmFormatoReferencia() {
		return tituloEmFormatoReferencia;
	}


	public void setTituloEmFormatoReferencia(String tituloEmFormatoReferencia) {
		this.tituloEmFormatoReferencia = tituloEmFormatoReferencia;
	}

	public UsuarioBiblioteca getUsuarioSolicitador() {
		return usuarioSolicitador;
	}

	public void setUsuarioSolicitador(UsuarioBiblioteca usuarioSolicitador) {
		this.usuarioSolicitador = usuarioSolicitador;
	}

	public boolean isSolicitandoPropriaReserva() {
		return solicitandoPropriaReserva;
	}

	public void setSolicitandoPropriaReserva(boolean solicitandoPropriaReserva) {
		this.solicitandoPropriaReserva = solicitandoPropriaReserva;
	}

	public InformacoesUsuarioBiblioteca getInformacaoUsuario() {
		return informacaoUsuario;
	}
	public void setInformacaoUsuario(InformacoesUsuarioBiblioteca informacaoUsuario) {
		this.informacaoUsuario = informacaoUsuario;
	}
	
	
	
}
