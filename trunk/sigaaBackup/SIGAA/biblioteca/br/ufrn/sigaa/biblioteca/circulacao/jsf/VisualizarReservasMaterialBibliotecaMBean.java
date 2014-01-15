/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.util.ArrayList;
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
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoCancelaReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaInternaBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.FormatosBibliograficosUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * <p> MBean para gerenciar a visualização, cancelamento e criação de novas reservas, tanto pelos usuários, quanto pelos bibliotecários.</p>
 *
 * <p>implementa PesquisarAcervoBiblioteca para poder utilizar a busca padrão de título no acervo.</p> 
 * <p>implementa PesquisarUsuarioBiblioteca para poder utilizar a nova busca padrão de usuário da biblioteca.</p> 
 * @author jadson
 *
 */
@Component("visualizarReservasMaterialBibliotecaMBean")
@Scope("request")
public class VisualizarReservasMaterialBibliotecaMBean  extends SigaaAbstractController <ReservaMaterialBiblioteca> implements PesquisarAcervoBiblioteca, PesquisarUsuarioBiblioteca{

	
	
	/** 
	 * <p>Página para os usuários acompanharem as reservas realizadas no sistema. Podem cancelar ou realiza uma nova reserva.</p>
	 *
	 * <p>No caso dos usuários, apenas visualizarão as suas reservas, no caso dos bibliotecários todas as reservas, do usuário escolhido anteriormente usuário. </p>
	 *  
	 */
	public static final String PAGINA_VISUALIZA_RESERVAS_MATERIAL_USUARIO = "/biblioteca/circulacao/visualizaReservasMaterialBibliotecaDeUmUsuario.jsp";
	
	/** 
	 * <p>Página para o bibliotecário informar o motivo do cancelamento da reservas.</p>
	 */
	public static final String PAGINA_INFORMA_MOTIVO_CANCELAMENTO_RESERVA = "/biblioteca/circulacao/informaMotivoCancelamentoReserva.jsp";
	
	
	/** 
	 * <p>Página para os bibliotecários visualizarem as reservad um determinado material (Título). Essa visualização não é mostrada aos usuários 
	 * da biblioteca, apesar de eles poderem obter essa informação quando vão solicitar um nova reserva.</p>  
	 */
	public static final String PAGINA_VISUALIZA_RESERVAS_MATERIAL_TITULO = "/biblioteca/circulacao/visualizaReservasMateriaisBibliotecaDeUmTitulo.jsp";
	
	/** Informações sobre o vínculo ativo do usuário para empréstimos da biblioteca. */
	private InformacoesUsuarioBiblioteca informacaoUsuario;
	
	/**
	 * O usuário que possui as reservas, no caso de se estar visualizando as próprias reservas será o usuário logado, caso seja 
	 * chamado a partir da busca de usuários interna do sistema utilizada pelos bibliotecários, será o usuário passado como parâmetro 
	 * da requisiação.
	 */
	private UsuarioBiblioteca usuarioDasReservas;
	
	/**
	 * A pessoa selecionada pra ver as reservas, utilizado apenas quando o bibliotecário está 
	 * visualizando as reservas dos usuários.
	 */
	private Pessoa pessoaSelecionada;
	
	
	/**
	 *  O cache selecionado pelo usuário, quando o bibliotecário que ver as reservas de um título. 
	 */
	private CacheEntidadesMarc cacheSelecionado;
	
	/**
	 * Informações do título das reservas, no caso da visualização por título
	 */
	private String formatoReferenciaTitulo;
	
	/**
	 * A lista de reservas 
	 */
	private List<ReservaMaterialBiblioteca> reservasAtivas = new ArrayList<ReservaMaterialBiblioteca>();
	
	/**
	 * A lista de reservas que podem ser canceladas. Se for o próprio usuário da reserva só pode escolher no máximo usa.
	 */
	private List<ReservaMaterialBiblioteca> reservasCanceladas;
	
	/**
	 * Indica se é o usuário que está vendo suas próprias reservas ou não.
	 */
	private boolean visualizacaoProprioUsuario = true;
	
	/**
	 * Indica se é o bibliotecário estar visualizado as reserva por usuário. Se for false estamos no caso de uso de visualizar as reservas por Título.
	 */
	private boolean visualizacaoReservaPorUsuario = true;
	
	/** O motivo informado pelo bibliotecário ao cancelar uma reserva. */
	private String motivoCancelamento;
	
	/** Indica se o bibliotecário está cancelando todas as suas reservas. 
	 * Importante para no processador saber se precisa ativar a próxima reserva da fila e  avisar o usuário.*/
	private boolean cancelandoTodasReservas = false;
	
	/** Página de retorno caso seja chamado de outro caso de uso. */
	private String paginaRetornoCasoUso;
	
	
	/**
	 *  Inicia a visualização das reservas de um usuário específico. Utilizado pelos bibliotecários. 
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String iniciaVisualizacaoReservasDeUmUsuario() throws SegurancaException, DAOException{
		
		visualizacaoProprioUsuario = false;
		visualizacaoReservaPorUsuario = true;
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO);
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, false, false, "Visualizar Reservas de um Usuário", null);
		
	}
	
	
	/**
	 * 
	 * Inicia a visualização das reservas de um título selecionado na busca padrão do sistema. Utilizado pelos bibliotecários. 
	 *  
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciaVisualizacaoReservasDeUmTitulo() throws SegurancaException{
		
		visualizacaoProprioUsuario = false;
		visualizacaoReservaPorUsuario = false;
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF);
		
		PesquisaInternaBibliotecaMBean pBean = getMBean("pesquisaInternaBibliotecaMBean");
		return pBean.iniciarBusca(this, null);
	}
	
	
	/**
	 * <p>Inicia a visualização das reservas de um título específico. Geramente esse método é usado para iniciar o caso de 
	 * uso a partir de outros casos de uso do sistema. Por exemplo, na comunicação de perda para o usuário poder cancelar 
	 * as reservas existentes para o Título passado quando ocorre o caso de todos os seus materiais serem perdidos.</p> 
	 * 
	 * <p>Para não acontecer o caso dos usuários ficarem esperando e não poderem realizar as reservas.</p>
	 * 
	 * <p>Utilizado pelos bibliotecários.</p> 
	 *  
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String iniciaVisualizacaoReservasDeUmTituloEspecifico(int idTitulo, String paginaRetorno) throws ArqException{
		
		visualizacaoProprioUsuario = false;
		visualizacaoReservaPorUsuario = false;
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF);
		
		this.paginaRetornoCasoUso = paginaRetorno;
		
		CacheEntidadesMarc c = new CacheEntidadesMarc();
		c.setIdTituloCatalografico(idTitulo);
		setTitulo(c);
		return selecionaTitulo();
	}
	
	
	
	/**
	 * 
	 * Inicia a visualização apenas das reservas do usuário atualmente logado. Utilizado quando o 
	 * usuário deseja ver as suas reservas.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *    <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 *    <li>/sigaa.war/biblioteca/menus/modulo_bilioteca_servidor.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String iniciaVisualizacaoMinhasReservas() throws ArqException{
		
		int idPessoa = getUsuarioLogado().getPessoa().getId();
		
		visualizacaoProprioUsuario = true;
		
		prepareMovimento(SigaaListaComando.CANCELA_RESERVA_MATERIAL_BIBLIOTECA);
		
		ReservaMaterialBibliotecaDao dao = null;
		
		try {
			
			usuarioDasReservas = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(idPessoa, null);
			informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioDasReservas, null, null);
			
			
			dao = getDAO(ReservaMaterialBibliotecaDao.class);
			
			reservasAtivas = dao.buscasReservasAtivasUsuarioEmOrdem(usuarioDasReservas.getId());
			
			for (ReservaMaterialBiblioteca reserva : reservasAtivas) {
				reserva.setInfoTitulo(BibliotecaUtil.obtemDadosResumidosTitulo(reserva.getTituloReservado().getId()));
			}
			
			return telaVisualizaReservasDeUmUsuario();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			if(dao != null) dao.close();
			
		}
		
		
	}
	
	
	

	
	//////////////////////////////  Métodos da interface de pesquisa de Títulos no acervo ////////////////////////
	
	
	/**
	 * Ver comentário na classe pai
	 * <p> Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return false;
	}

	/**
	 * Ver comentário na classe pai
	 * <p> Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	@Override
	public String selecionaTitulo() throws ArqException {
	
		visualizacaoProprioUsuario = false;
		
		prepareMovimento(SigaaListaComando.CANCELA_RESERVA_MATERIAL_BIBLIOTECA);
		
		ReservaMaterialBibliotecaDao dao = null;
		
		try {
			
			dao = getDAO(ReservaMaterialBibliotecaDao.class);
			
			reservasAtivas = dao.buscasReservasAtivasTituloEmOrdem(cacheSelecionado.getIdTituloCatalografico());
			
			formatoReferenciaTitulo = 
				    " Número de Registro no Sistema : <strong>"+dao.findByPrimaryKey(cacheSelecionado.getIdTituloCatalografico(), TituloCatalografico.class, "numeroDoSistema").getNumeroDoSistema()+"</strong> <br/> "
					+new FormatosBibliograficosUtil().gerarFormatoReferencia(new TituloCatalografico(cacheSelecionado.getIdTituloCatalografico()), true);
			
			return telaVisualizaReservasDeUmTitulo();
			
		}finally{
			if(dao != null) dao.close();
		}
	}

	/**
	 * Ver comentário na classe pai
	 * <p> Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	@Override
	public void setTitulo(CacheEntidadesMarc titulo) throws ArqException {
		this.cacheSelecionado = titulo;
	}

	/**
	 * Ver comentário na classe pai
	 * <p> Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#voltarBuscaAcervo()
	 */
	@Override
	public String voltarBuscaAcervo() throws ArqException {
		return null;
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	
	
	
	
	
	////////////////////////////////////  Métodos das interface de busca de usuários da biblioteca  ////////////////////
	
	
	/**
	 * Ver comentário na classe pai
	 * <p> Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
	
		visualizacaoProprioUsuario = false;
		visualizacaoReservaPorUsuario = true;
		
		prepareMovimento(SigaaListaComando.CANCELA_RESERVA_MATERIAL_BIBLIOTECA);
		
		ReservaMaterialBibliotecaDao dao = null;
		
		try {
			
			usuarioDasReservas = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(pessoaSelecionada.getId(), null);
			informacaoUsuario = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioDasReservas, null, null);
			
			dao = getDAO(ReservaMaterialBibliotecaDao.class);
			
			reservasAtivas = dao.buscasReservasAtivasUsuarioEmOrdem(usuarioDasReservas.getId());
			
			for (ReservaMaterialBiblioteca reserva : reservasAtivas) {
				reserva.setInfoTitulo(BibliotecaUtil.obtemDadosResumidosTitulo(reserva.getTituloReservado().getId()));
			}
			
			return telaVisualizaReservasDeUmUsuario();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}finally{
			if(dao != null) dao.close();
			
		}
		
	}

	/**
	 * Ver comentário na classe pai
	 * <p> Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		
	}

	/**
	 * Ver comentário na classe pai
	 * <p> Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
		
		
	}

	/**
	 * Ver comentário na classe pai
	 * <p> Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		this.pessoaSelecionada = p;
		
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Volta pra a tela de busca
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/visualizaReservasMaterialBibliotecaDeUmUsuario.jsp</li>
	 *   </ul>
	 */
	public String  voltaTelaBusca() throws SegurancaException, DAOException{
		
		// retorna para o caso de uso que chamou //
		if(br.ufrn.arq.util.StringUtils.notEmpty(paginaRetornoCasoUso)){
			return forward(paginaRetornoCasoUso);
		}
		
		if(visualizacaoReservaPorUsuario){
			
			if(isVisualizacaoProprioUsuario())
				return cancelar();                 // retorna para o portal de quem acessou
			else{
				// retorna para a busca o usuário
				BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
				return pBean.iniciar(this, true, true, false, false, "Visualizar Reservas de um Usuário", null);
			}
		}else{  // visualizando por título, retorna para a página de pesquisa.
			PesquisaInternaBibliotecaMBean pBean = getMBean("pesquisaInternaBibliotecaMBean");
			return pBean.iniciarBusca(this, null);
		}
	}
	
	

	
	
	
	/**
	 * Chama o processador para realizar o cancelamento da reserva.
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/visualizaReservasMaterialBibliotecaDeUmUsuario.jsp</li>
	 *    <li>/sigaa.war/biblioteca/circulacao/visualizaReservasMateriaisBibliotecaDeUmTitulo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String preCancelarReserva(){
	
		int idReservaSelecionada = getParameterInt("idReservaCancelamento");
		
		cancelandoTodasReservas = false;
		
		reservasCanceladas = new ArrayList<ReservaMaterialBiblioteca>();
		
		for (ReservaMaterialBiblioteca reserva : reservasAtivas) {
			if(reserva.getId() == idReservaSelecionada){
				reservasCanceladas.add(reserva);
				break;
			}
		}
		
		return telaInformaMotivoCancelamentoReserva();
	}
	
	/**
	 * <p>Chama o processador para realizar o cancelamento de todas as reservas do título atual.</p>
	 *
	 * <p>Utilizado apenas pelos bibliotecários.</p>
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> 
	 *   	<li>/sigaa.war/biblioteca/circulacao/visualizaReservasMateriaisBibliotecaDeUmTitulo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String preCancelarTodasReservasTitulo() throws ArqException{
	
		if(visualizacaoProprioUsuario){ // O usuário dono da reserva não pode realizar essa operação.
		
			addMensagemErro("Você não tem permissão para cancelar todas as reservas de um Título do sistema.");
			return null;
		}
		
		cancelandoTodasReservas = true;
		
		reservasCanceladas = new ArrayList<ReservaMaterialBiblioteca>();
		reservasCanceladas.addAll(reservasAtivas);
		
		// Mensagem padrão do sistema, pode ser alterada na página pelo bibliotecário //
		motivoCancelamento = " ";
		
		return telaInformaMotivoCancelamentoReserva();
		
	}
	

	/**
	 * Chama o processador para realizar o cancelamento da reserva.
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/visualizaReservasMaterialBibliotecaDeUmUsuario.jsp</li>
	 *    <li>/sigaa.war/biblioteca/circulacao/visualizaReservasMateriaisBibliotecaDeUmTitulo.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String cancelarReserva() throws ArqException{
	
		if(visualizacaoProprioUsuario){ // O usuário cancelando a sua própria reserva, não precisa informa o motivo
		
			int idReservaSelecionada = getParameterInt("idReservaCancelamento");
			
			reservasCanceladas = new ArrayList<ReservaMaterialBiblioteca>();
			
			for (ReservaMaterialBiblioteca reserva : reservasAtivas) {
				if(reserva.getId() == idReservaSelecionada){
					reserva.setMotivoCancelamento("CANCELADA PELO PRÓPRIO USUÁRIO");
					reservasCanceladas.add(reserva);
					break;
				}
			}
		}else{
			
			// Se não é o próprio usuário, passa o motivo informado pelo usuário para as reservas // 
			for (ReservaMaterialBiblioteca reserva : reservasCanceladas) {
				reserva.setMotivoCancelamento(motivoCancelamento);
			}
			
		}
		
		try{
			
			
			MovimentoCancelaReservaMaterialBiblioteca mov 
				= new MovimentoCancelaReservaMaterialBiblioteca(reservasCanceladas, visualizacaoProprioUsuario, cancelandoTodasReservas);
			
			execute(mov);
		
			addMensagemInformation("Reserva cancelada com sucesso.");
			
			if(visualizacaoProprioUsuario)
				return iniciaVisualizacaoMinhasReservas();
			else{
				if(visualizacaoReservaPorUsuario)
					return selecionouUsuarioBuscaPadrao();
				else
					return selecionaTitulo();
			}
			
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	
	
	
	
	
	
	/**
	 * Redireciona para a tela de visualizar as reservas de usuário.
	 * 
	 *  Método não chamado por nenhuma página jsp.
	 */
	public String telaVisualizaReservasDeUmUsuario(){
		return forward(PAGINA_VISUALIZA_RESERVAS_MATERIAL_USUARIO);
	}
	
	/**
	 * Redireciona para a tela de visualizar as reservas de material.
	 * 
	 *  Método não chamado por nenhuma página jsp.
	 */
	public String telaVisualizaReservasDeUmTitulo(){
		return forward(PAGINA_VISUALIZA_RESERVAS_MATERIAL_TITULO);
	}
	
	/**
	 * Redireciona para a tela onde o bibliotecário vai cancelar a reserva
	 * 
	 *  Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 */
	public String telaInformaMotivoCancelamentoReserva(){
		return forward(PAGINA_INFORMA_MOTIVO_CANCELAMENTO_RESERVA);
	}
	
	
	//////  sets e gets ///////
	
	
	public int getQuantidadeReservas(){
		if(reservasAtivas != null)
			return reservasAtivas.size();
		else
			return 0;
	}
	
	public UsuarioBiblioteca getUsuarioDasReservas() {
		return usuarioDasReservas;
	}

	public void setUsuarioDasReservas(UsuarioBiblioteca usuarioDasReservas) {
		this.usuarioDasReservas = usuarioDasReservas;
	}

	public List<ReservaMaterialBiblioteca> getReservasAtivas() {
		return reservasAtivas;
	}

	public void setReservasAtivas(List<ReservaMaterialBiblioteca> reservasAtivas) {
		this.reservasAtivas = reservasAtivas;
	}


	public InformacoesUsuarioBiblioteca getInformacaoUsuario() {
		return informacaoUsuario;
	}


	public void setInformacaoUsuario(InformacoesUsuarioBiblioteca informacaoUsuario) {
		this.informacaoUsuario = informacaoUsuario;
	}

	public List<ReservaMaterialBiblioteca> getReservasCanceladas() {
		return reservasCanceladas;
	}


	public void setReservasCanceladas(
			List<ReservaMaterialBiblioteca> reservasCanceladas) {
		this.reservasCanceladas = reservasCanceladas;
	}


	public boolean isVisualizacaoProprioUsuario() {
		return visualizacaoProprioUsuario;
	}


	public void setVisualizacaoProprioUsuario(boolean visualizacaoProprioUsuario) {
		this.visualizacaoProprioUsuario = visualizacaoProprioUsuario;
	}


	public String getFormatoReferenciaTitulo() {
		return formatoReferenciaTitulo;
	}


	public void setFormatoReferenciaTitulo(String formatoReferenciaTitulo) {
		this.formatoReferenciaTitulo = formatoReferenciaTitulo;
	}


	public boolean isVisualizacaoReservaPorUsuario() {
		return visualizacaoReservaPorUsuario;
	}


	public void setVisualizacaoReservaPorUsuario(boolean visualizacaoReservaPorUsuario) {
		this.visualizacaoReservaPorUsuario = visualizacaoReservaPorUsuario;
	}


	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}


	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	
	
	
}
