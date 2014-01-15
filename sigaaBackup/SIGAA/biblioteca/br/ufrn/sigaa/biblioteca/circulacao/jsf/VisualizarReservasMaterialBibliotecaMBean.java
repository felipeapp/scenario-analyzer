/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> MBean para gerenciar a visualiza��o, cancelamento e cria��o de novas reservas, tanto pelos usu�rios, quanto pelos bibliotec�rios.</p>
 *
 * <p>implementa PesquisarAcervoBiblioteca para poder utilizar a busca padr�o de t�tulo no acervo.</p> 
 * <p>implementa PesquisarUsuarioBiblioteca para poder utilizar a nova busca padr�o de usu�rio da biblioteca.</p> 
 * @author jadson
 *
 */
@Component("visualizarReservasMaterialBibliotecaMBean")
@Scope("request")
public class VisualizarReservasMaterialBibliotecaMBean  extends SigaaAbstractController <ReservaMaterialBiblioteca> implements PesquisarAcervoBiblioteca, PesquisarUsuarioBiblioteca{

	
	
	/** 
	 * <p>P�gina para os usu�rios acompanharem as reservas realizadas no sistema. Podem cancelar ou realiza uma nova reserva.</p>
	 *
	 * <p>No caso dos usu�rios, apenas visualizar�o as suas reservas, no caso dos bibliotec�rios todas as reservas, do usu�rio escolhido anteriormente usu�rio. </p>
	 *  
	 */
	public static final String PAGINA_VISUALIZA_RESERVAS_MATERIAL_USUARIO = "/biblioteca/circulacao/visualizaReservasMaterialBibliotecaDeUmUsuario.jsp";
	
	/** 
	 * <p>P�gina para o bibliotec�rio informar o motivo do cancelamento da reservas.</p>
	 */
	public static final String PAGINA_INFORMA_MOTIVO_CANCELAMENTO_RESERVA = "/biblioteca/circulacao/informaMotivoCancelamentoReserva.jsp";
	
	
	/** 
	 * <p>P�gina para os bibliotec�rios visualizarem as reservad um determinado material (T�tulo). Essa visualiza��o n�o � mostrada aos usu�rios 
	 * da biblioteca, apesar de eles poderem obter essa informa��o quando v�o solicitar um nova reserva.</p>  
	 */
	public static final String PAGINA_VISUALIZA_RESERVAS_MATERIAL_TITULO = "/biblioteca/circulacao/visualizaReservasMateriaisBibliotecaDeUmTitulo.jsp";
	
	/** Informa��es sobre o v�nculo ativo do usu�rio para empr�stimos da biblioteca. */
	private InformacoesUsuarioBiblioteca informacaoUsuario;
	
	/**
	 * O usu�rio que possui as reservas, no caso de se estar visualizando as pr�prias reservas ser� o usu�rio logado, caso seja 
	 * chamado a partir da busca de usu�rios interna do sistema utilizada pelos bibliotec�rios, ser� o usu�rio passado como par�metro 
	 * da requisia��o.
	 */
	private UsuarioBiblioteca usuarioDasReservas;
	
	/**
	 * A pessoa selecionada pra ver as reservas, utilizado apenas quando o bibliotec�rio est� 
	 * visualizando as reservas dos usu�rios.
	 */
	private Pessoa pessoaSelecionada;
	
	
	/**
	 *  O cache selecionado pelo usu�rio, quando o bibliotec�rio que ver as reservas de um t�tulo. 
	 */
	private CacheEntidadesMarc cacheSelecionado;
	
	/**
	 * Informa��es do t�tulo das reservas, no caso da visualiza��o por t�tulo
	 */
	private String formatoReferenciaTitulo;
	
	/**
	 * A lista de reservas 
	 */
	private List<ReservaMaterialBiblioteca> reservasAtivas = new ArrayList<ReservaMaterialBiblioteca>();
	
	/**
	 * A lista de reservas que podem ser canceladas. Se for o pr�prio usu�rio da reserva s� pode escolher no m�ximo usa.
	 */
	private List<ReservaMaterialBiblioteca> reservasCanceladas;
	
	/**
	 * Indica se � o usu�rio que est� vendo suas pr�prias reservas ou n�o.
	 */
	private boolean visualizacaoProprioUsuario = true;
	
	/**
	 * Indica se � o bibliotec�rio estar visualizado as reserva por usu�rio. Se for false estamos no caso de uso de visualizar as reservas por T�tulo.
	 */
	private boolean visualizacaoReservaPorUsuario = true;
	
	/** O motivo informado pelo bibliotec�rio ao cancelar uma reserva. */
	private String motivoCancelamento;
	
	/** Indica se o bibliotec�rio est� cancelando todas as suas reservas. 
	 * Importante para no processador saber se precisa ativar a pr�xima reserva da fila e  avisar o usu�rio.*/
	private boolean cancelandoTodasReservas = false;
	
	/** P�gina de retorno caso seja chamado de outro caso de uso. */
	private String paginaRetornoCasoUso;
	
	
	/**
	 *  Inicia a visualiza��o das reservas de um usu�rio espec�fico. Utilizado pelos bibliotec�rios. 
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
		return pBean.iniciar(this, true, true, false, false, "Visualizar Reservas de um Usu�rio", null);
		
	}
	
	
	/**
	 * 
	 * Inicia a visualiza��o das reservas de um t�tulo selecionado na busca padr�o do sistema. Utilizado pelos bibliotec�rios. 
	 *  
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Inicia a visualiza��o das reservas de um t�tulo espec�fico. Geramente esse m�todo � usado para iniciar o caso de 
	 * uso a partir de outros casos de uso do sistema. Por exemplo, na comunica��o de perda para o usu�rio poder cancelar 
	 * as reservas existentes para o T�tulo passado quando ocorre o caso de todos os seus materiais serem perdidos.</p> 
	 * 
	 * <p>Para n�o acontecer o caso dos usu�rios ficarem esperando e n�o poderem realizar as reservas.</p>
	 * 
	 * <p>Utilizado pelos bibliotec�rios.</p> 
	 *  
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
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
	 * Inicia a visualiza��o apenas das reservas do usu�rio atualmente logado. Utilizado quando o 
	 * usu�rio deseja ver as suas reservas.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	
	

	
	//////////////////////////////  M�todos da interface de pesquisa de T�tulos no acervo ////////////////////////
	
	
	/**
	 * Ver coment�rio na classe pai
	 * <p> M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return false;
	}

	/**
	 * Ver coment�rio na classe pai
	 * <p> M�todo n�o chamado por nenhuma p�gina jsp.</p>
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
				    " N�mero de Registro no Sistema : <strong>"+dao.findByPrimaryKey(cacheSelecionado.getIdTituloCatalografico(), TituloCatalografico.class, "numeroDoSistema").getNumeroDoSistema()+"</strong> <br/> "
					+new FormatosBibliograficosUtil().gerarFormatoReferencia(new TituloCatalografico(cacheSelecionado.getIdTituloCatalografico()), true);
			
			return telaVisualizaReservasDeUmTitulo();
			
		}finally{
			if(dao != null) dao.close();
		}
	}

	/**
	 * Ver coment�rio na classe pai
	 * <p> M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	@Override
	public void setTitulo(CacheEntidadesMarc titulo) throws ArqException {
		this.cacheSelecionado = titulo;
	}

	/**
	 * Ver coment�rio na classe pai
	 * <p> M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#voltarBuscaAcervo()
	 */
	@Override
	public String voltarBuscaAcervo() throws ArqException {
		return null;
	}

	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	
	
	
	
	
	////////////////////////////////////  M�todos das interface de busca de usu�rios da biblioteca  ////////////////////
	
	
	/**
	 * Ver coment�rio na classe pai
	 * <p> M�todo n�o chamado por nenhuma p�gina jsp.</p>
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
	 * Ver coment�rio na classe pai
	 * <p> M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		
	}

	/**
	 * Ver coment�rio na classe pai
	 * <p> M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa, String... parametros) {
		
		
	}

	/**
	 * Ver coment�rio na classe pai
	 * <p> M�todo n�o chamado por nenhuma p�gina jsp.</p>
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
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
				// retorna para a busca o usu�rio
				BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
				return pBean.iniciar(this, true, true, false, false, "Visualizar Reservas de um Usu�rio", null);
			}
		}else{  // visualizando por t�tulo, retorna para a p�gina de pesquisa.
			PesquisaInternaBibliotecaMBean pBean = getMBean("pesquisaInternaBibliotecaMBean");
			return pBean.iniciarBusca(this, null);
		}
	}
	
	

	
	
	
	/**
	 * Chama o processador para realizar o cancelamento da reserva.
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Chama o processador para realizar o cancelamento de todas as reservas do t�tulo atual.</p>
	 *
	 * <p>Utilizado apenas pelos bibliotec�rios.</p>
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul> 
	 *   	<li>/sigaa.war/biblioteca/circulacao/visualizaReservasMateriaisBibliotecaDeUmTitulo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String preCancelarTodasReservasTitulo() throws ArqException{
	
		if(visualizacaoProprioUsuario){ // O usu�rio dono da reserva n�o pode realizar essa opera��o.
		
			addMensagemErro("Voc� n�o tem permiss�o para cancelar todas as reservas de um T�tulo do sistema.");
			return null;
		}
		
		cancelandoTodasReservas = true;
		
		reservasCanceladas = new ArrayList<ReservaMaterialBiblioteca>();
		reservasCanceladas.addAll(reservasAtivas);
		
		// Mensagem padr�o do sistema, pode ser alterada na p�gina pelo bibliotec�rio //
		motivoCancelamento = " ";
		
		return telaInformaMotivoCancelamentoReserva();
		
	}
	

	/**
	 * Chama o processador para realizar o cancelamento da reserva.
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
		if(visualizacaoProprioUsuario){ // O usu�rio cancelando a sua pr�pria reserva, n�o precisa informa o motivo
		
			int idReservaSelecionada = getParameterInt("idReservaCancelamento");
			
			reservasCanceladas = new ArrayList<ReservaMaterialBiblioteca>();
			
			for (ReservaMaterialBiblioteca reserva : reservasAtivas) {
				if(reserva.getId() == idReservaSelecionada){
					reserva.setMotivoCancelamento("CANCELADA PELO PR�PRIO USU�RIO");
					reservasCanceladas.add(reserva);
					break;
				}
			}
		}else{
			
			// Se n�o � o pr�prio usu�rio, passa o motivo informado pelo usu�rio para as reservas // 
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
	 * Redireciona para a tela de visualizar as reservas de usu�rio.
	 * 
	 *  M�todo n�o chamado por nenhuma p�gina jsp.
	 */
	public String telaVisualizaReservasDeUmUsuario(){
		return forward(PAGINA_VISUALIZA_RESERVAS_MATERIAL_USUARIO);
	}
	
	/**
	 * Redireciona para a tela de visualizar as reservas de material.
	 * 
	 *  M�todo n�o chamado por nenhuma p�gina jsp.
	 */
	public String telaVisualizaReservasDeUmTitulo(){
		return forward(PAGINA_VISUALIZA_RESERVAS_MATERIAL_TITULO);
	}
	
	/**
	 * Redireciona para a tela onde o bibliotec�rio vai cancelar a reserva
	 * 
	 *  M�todo n�o chamado por nenhuma p�gina jsp.
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
