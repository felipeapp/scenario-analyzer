/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/10/2011'
 *
 */
package br.ufrn.sigaa.mobile.commons;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.comum.jsf.VerTelaAvisoLogonMBean;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.mobile.jsf.DadosAcessoMobile;

/***
 * Classe base para todos os managed-bens do SIGAA Touch.
 * 
 * @author ilueny santos
 *
 */
public class SigaaTouchAbstractController<T> extends SigaaAbstractController<T> {

	/** Representa mensagem de erro no módulo mobile. */
	public static final String MENSAGEM_ERRO_MOBILE = "mensagensMobileErro";
	/** Representa mensagem de aviso no módulo mobile. */
	public static final String MENSAGEM_WARNING_MOBILE = "mensagensMobileWarning";
	/** Representa mensagem de informação no módulo mobile. */
	public static final String MENSAGEM_INFORMATION_MOBILE = "mensagensMobileInformations";

	
	/**
	 * Retorna a entidade DadosAcessoMobile, que contém as informações da sessão do usuário, com informações de contexto.
	 * @return
	 */
	public DadosAcessoMobile getAcessoMenuMobile() {
		return (DadosAcessoMobile) getCurrentSession().getAttribute("acesso");
	}
	
	
	/** 
	 * Define o modo de exibição para mobile.
	 * 
	 * @return
	 * 
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * 	<li>/sigaa.war/ava/index.jsp</li>
	 * </ul>
	 */
	public String alterarParaModoClassico() throws DAOException {
		getCurrentSession().setAttribute("modo", "classico");
		
		if (getUsuarioLogado() == null) {
			if (getCurrentRequest().getParameter("paginaLogin") != null)
				return redirect("/sigaa/verTelaLogin.do");
			else
				return redirect("/sigaa/public/home.jsf");
		}
		
		Usuario usuario = (Usuario) getCurrentRequest().getSession().getAttribute("usuario");
		boolean multiplosVinculos = usuario.getVinculosPrioritarios().size() > 1;
		
		if (multiplosVinculos){
			setSubSistemaAtual(null);
			return redirect("/sigaa/vinculos.jsf");
		} else {
			TurmaVirtualMBean tvBean = getMBean("turmaVirtual");
			tvBean.setModoExibicao(TurmaVirtualMBean.MODO_CLASSICO);
		
			VerTelaAvisoLogonMBean telaAviso = getMBean("verTelaAvisoLogonMBean");
			telaAviso.iniciar("/paginaInicial.do", getUsuarioLogado(), getSistema(), getCurrentRequest(), getCurrentResponse());
			return null;
		}
	}
	
	/**  
	 * Adiciona uma mensagem de erro para ser exibida no sistema. 
	 * @param mensagem Mensagem que será adicionada.
	 * @return
	 * 
	 * Este Método não é Chamado por JSPs.
 	 */
	public void addMensagemErroJSF(String mensagem){
		FacesMessage msg = new FacesMessage( mensagem);
		FacesContext.getCurrentInstance().addMessage("errorTag", msg);
	}
	
	/** 
	 * Adiciona um objeto ListaMensagem para que as mensagens sejam exibidas no sistema.
	 * 
	 * @param listaMensagens Mensagem que será adicionada.
	 * @return
	 * 
	 * Este Método não é Chamado por JSPs.
	 * 
	 */
	public boolean addMensagens(ListaMensagens listaMensagens) {
		for (MensagemAviso msg : listaMensagens.getMensagens()) {
			addMensagemErro(msg.getMensagem());
		}
		return true; //FIXME: retornar true somente se alguma pensagem for adicionada.
	}

	/** 
	 * Remove todas as mensagens de erro.
	 * 
	 * Este Método não é Chamado por JSPs.
	 */
	public void limparMensagensErro() {
	     FacesContext facesContext = FacesContext.getCurrentInstance();
	     if (facesContext != null) {
	         Iterator<FacesMessage> iter = facesContext.getMessages();
	         while (iter.hasNext()) {
	             iter.remove(); 
	         }
	      }
	 }
	
	
	
	/**
	 * Adiciona uma mensagem de erro para ser exibida no sistema. 
	 * @param mensagem Mensagem que será adicionada.
	 * @return
	 * 
	 * Este Método não é Chamado por JSPs.
	 * @see br.ufrn.arq.web.jsf.AbstractController#addMensagemErro(java.lang.String)
	 */
	@Override
	public void addMensagemErro(String mensagem) {
		addMensagem(MENSAGEM_ERRO_MOBILE, mensagem);
	}

	/**
	 * Adiciona uma mensagem de erro padrão para ser exibida no sistema. 
	 * @param mensagem Mensagem que será adicionada.
	 * @return
	 * 
	 * Este Método não é Chamado por JSPs.
	 * @see br.ufrn.arq.web.jsf.AbstractController#addMensagemErroPadrao()
	 */
	@Override
	public void addMensagemErroPadrao() {
		addMensagem(MENSAGEM_ERRO_MOBILE, "Um erro ocorreu durante a execução desta operação. "
				+ "Se o problema persistir contacte o suporte através do \"Abrir Chamado\".");
	}

	/**
	 * Adiciona uma mensagem de informação para ser exibida no sistema. 
	 * @param mensagem Mensagem que será adicionada.
	 * @return
	 * 
	 * Este Método não é Chamado por JSPs.
	 * @see br.ufrn.arq.web.jsf.AbstractController#addMensagemInformation(java.lang.String)
	 */
	@Override
	public void addMensagemInformation(String mensagem) {
		addMensagem(MENSAGEM_INFORMATION_MOBILE, mensagem);
	}

	/**
	 * Adiciona uma mensagem de aviso para ser exibida no sistema. 
	 * @param mensagem Mensagem que será adicionada.
	 * @return
	 * 
	 * Este Método não é Chamado por JSPs.
	 * @see br.ufrn.arq.web.jsf.AbstractController#addMensagemWarning(java.lang.String)
	 */
	@Override
	public void addMensagemWarning(String mensagem) {
		addMensagem(MENSAGEM_WARNING_MOBILE, mensagem);
	}

	
	/**
	 * Adiciona a mensagem com um parametro na requisicao que eh lido pela pagina cabecalho.jsp
	 * dos sistemas mobile.
	 * 
	 * Este Método não é Chamado por JSPs. 
	 */
	private void addMensagem(String tipo, String mensagem){
		getCurrentRequest().setAttribute(tipo, mensagem);
	}
	
}
