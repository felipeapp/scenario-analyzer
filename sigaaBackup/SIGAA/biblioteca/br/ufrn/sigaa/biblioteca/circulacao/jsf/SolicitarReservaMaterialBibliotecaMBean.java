/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Mbean que gerencia a parte de solicitação de reservas de materiais da biblioteca </p>
 * 
 * @author jadson
 *
 */
@Component("solicitarReservaMaterialBibliotecaMBean")
@Scope("request")
public class SolicitarReservaMaterialBibliotecaMBean  extends SigaaAbstractController <ReservaMaterialBiblioteca> implements PesquisarAcervoBiblioteca{

	/** Página que lista todas as reservas que o título selecionado possui, calcula a previsão de quando o material estará disponível e
	 * disponibilizará um botão para o usuário confirmar a reserva. 
	 * */
	public static final String PAGINA_CONFIRMA_RESERVA_MATERIAL = "/biblioteca/circulacao/confirmaReservaMaterialBiblioteca.jsp";
	
	
	
	
	/** 
	 * <p>Guarda as informações da catalogação no acervo da biblioteca escolhida pelos usuário.</p> 
	 * <p>A reserva vai ser feita para os materiais dessa catalogaçã. </p>
	 */
	private CacheEntidadesMarc cache;
	
	/**
	 * Guarda a descrição to Título em formato de referência para mostrar ao usuário.
	 */
	private String tituloEmFormatoReferencia;
	
	/**
	 * O usuário que vai solicitar a reserva
	 */
	private UsuarioBiblioteca usuarioSolicitador;
	
	
	/** Informações sobre o vínculo ativo do usuário que está realizando a reserva, utilizando quando o operador está realizar a reserva para um usuário. */
	private InformacoesUsuarioBiblioteca informacaoUsuario;
	
	/**
	 * Guarda a lista de reservas já existentes para o título selecionado pelo usuário
	 */
	private List<ReservaMaterialBiblioteca> reservasJaExistentes;
	
	
	/**
	 * <p>Guarda a data de previsão para retirada do material.</p> 
	 * <p>Que vai ser calculada com base na quantidade de reservas, no prazo do empréstimos e na quantidade de renovações permitidas no sistema. <p>
	 */
	private Date dataPrevisaoEmprestimoMaterial;
	
	/**
	 * Diz se vai habilitar o botão de voltar na página de busca do acervo.  Neste caso só usado quando o usuário 
	 * solicitou a reserva a partir da página de visualização das reservas, deve voltar para essa página. 
	 */
	private boolean utilizaBotaoVoltarPaginaBusca = false;
	
	
	/** Guarda se o biblioteca ou o próprio usuário está solicitando a reserva*/
	private boolean solicitandoPropriaReserva = false;
	
	
	/**
	 * <p>Inicia o caso de uso de um usuário solicitar uma reserva.</p>
	 *
	 * <p><strong>Realiza as verificações em relação ao usuário antes de permitir a reserva</strong></p> 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Inicia o caso de uso de um usuário solicitar uma reserva na página onde ele visualiza as suas reservas</p>
	 *
	 * 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Inicia o caso de uso de para um operador de circulação realizar a reserva para o usuário selecionado na pesquisa.
	 *
	 * 
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Inicia o caso de uso de um usuário logado solicitar uma reserva.</p>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private String configuraReservaParaUsuarioLogado() throws DAOException{
		
		int idPessoa = getUsuarioLogado().getPessoa().getId();
		
		try {
			
			usuarioSolicitador = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(idPessoa, null);
			
			// Usuários bloqueados não pode solicitar reservas, não precisa testar as demais condições
			String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario(usuarioSolicitador);
			if(motivoBloqueio != null){
				addMensagemErro("Caro usuário, você está bloqueado para utilizar os serviços de circulação da biblioteca, motivo: "+motivoBloqueio);
				return null;
			}
			
			// Usuários suspensos ou  com multas não pagas não podem solicitar reservas
			List<SituacaoUsuarioBiblioteca> situacoes = VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(
					usuarioSolicitador.getIdentificadorPessoa() , usuarioSolicitador.getIdentificadorBiblioteca() );
			
			if(situacoes.size() > 0){
				
				addMensagemErro(ProcessadorSolicitaReservaMaterialBiblioteca.MENAGEM_PADRAO_USUARIO_COM_PEDENCIAS_PARA_SOLICITAR_RESERVA);
				return null;
			}
			
			// Verifica a quantiade de reservas que o usuário já tem
			ReservaMaterialBibliotecaUtil.verificaQuantidadeMaximaDeReservasDoUsuario(usuarioSolicitador.getId());
			
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		PesquisaInternaBibliotecaMBean pBean = getMBean("pesquisaInternaBibliotecaMBean");
		return pBean.iniciarBusca(this, null);
	}
	
	
	
	/**
	 * <p>Inicia o caso de uso de um usuário logado solicitar uma reserva.</p>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	private String configuraReservaParaUsuarioPesquisado() throws DAOException{
		
		try {
			
			/* 
			 * a variável "usuarioSolicitador" é setado via f:setPropertyActionListener 
			 * na página visualizaReservasMateriaisBibliotecaDeUmUsuario.jsp
			 * com o valor VisualizarReservasMaterialBibliotecaMBean.usuarioDasReservas
			 */
			
			informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioSolicitador, null, null);
			
			// Usuários bloqueados não pode solicitar reservas, não precisa testar as demais condições
			String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario(usuarioSolicitador);
			if(motivoBloqueio != null){
				addMensagemErro("Caro usuário, você está bloqueado para utilizar os serviços de circulação da biblioteca, motivo: "+motivoBloqueio);
				return null;
			}
			
			// Usuários suspensos ou  com multas não pagas não podem solicitar reservas
			List<SituacaoUsuarioBiblioteca> situacoes = VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(	
					usuarioSolicitador.getIdentificadorPessoa() , usuarioSolicitador.getIdentificadorBiblioteca());
			
			if(situacoes.size() > 0){
				
				addMensagemErro(ProcessadorSolicitaReservaMaterialBiblioteca.MENAGEM_PADRAO_USUARIO_COM_PEDENCIAS_PARA_SOLICITAR_RESERVA);
				return null;
			}
			
			// Verifica a quantiade de reservas que o usuário já tem
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
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return utilizaBotaoVoltarPaginaBusca;
	}

	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	@Override
	public String selecionaTitulo() throws ArqException {
		
		prepareMovimento(SigaaListaComando.SOLICITA_RESERVA_MATERIAL_BIBLIOTECA);
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Realiza as verificações em relação ao título escolhido antes de permitir a reserva
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
				 " Número de Registro no Sistema : <strong>"+dao.findByPrimaryKey(cache.getIdTituloCatalografico(), TituloCatalografico.class, "numeroDoSistema").getNumeroDoSistema()+"</strong> <br/> "
				+ new FormatosBibliograficosUtil().gerarFormatoReferencia(new TituloCatalografico(cache.getIdTituloCatalografico()), true);
			
			return telaConfirmaReserva();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			if(dao != null) dao.close();
			
			System.out.println("Verificações demoraram:  "+(System.currentTimeMillis()-tempo)+" ms");
		}
		
	}
	

	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	@Override
	public void setTitulo(CacheEntidadesMarc titulo) throws ArqException {
		this.cache = titulo;
	}

	/**
	 *  Ver comentários da classe pai.<br/>
	 * 
	 *   <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Método chamado quando o usuário realiza a ação de confirmar a reserva. Vai chamar o processador 
	 * para validar todos as regras de negócio de uma reserva e se não houve nehum impedimento cria-a.</p>
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemInformation("Sua reserva foi confirmada com sucesso. Será enviada uma comunicação eletrônica quando a reserva solicitada estiver disponível ");
				return beanVisualizaReserva.iniciaVisualizacaoMinhasReservas();
			}else{
				addMensagemInformation("A reserva foi confirmada com sucesso. Será enviada uma comunicação eletrônica para o usuário quando a reserva solicitada estiver disponível ");
				beanVisualizaReserva.setTitulo(cache);
				return beanVisualizaReserva.selecionaTitulo();
			}
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		} 
		
		
	}
	
	
	
	/**
	 * Verifica se o sistema está trabalhando com reservas. Chamado a partir das páginas do sistema para habilitar a solicitação de reservas.
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Redireciona o usuário para a página onde ele irá confirmar a reserva.</p>
	 *
	 * <p>Método não chamado por nenhuma página jsp.</p>
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
