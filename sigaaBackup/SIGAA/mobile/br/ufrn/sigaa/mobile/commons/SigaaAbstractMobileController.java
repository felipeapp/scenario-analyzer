/*
 * SigaaAbstractMobileController.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.mobile.commons;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 *
 *      Classe de controle para usar na parte mobile. Cont�m alguns m�todos comuns a todos na camada 
 * da view mobile.
 *
 * @author jadson
 * @since 03/12/2008
 * @version 1.0 criacao da classe
 *
 */
public class SigaaAbstractMobileController<T> extends SigaaAbstractController<T>{

	public static final String MENSAGEM_ERRO_MOBILE = "mensagensMobileErro";
	public static final String MENSAGEM_WARNING_MOBILE = "mensagensMobileWarning";
	public static final String MENSAGEM_INFORMATION_MOBILE = "mensagensMobileInformations";
	
	/**
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractController#addMensagemErro(java.lang.String)
	 */
	@Override
	public void addMensagemErro(String mensagem) {
		addMensagem(MENSAGEM_ERRO_MOBILE, mensagem);
	}

	/**
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractController#addMensagemErroPadrao()
	 */
	@Override
	public void addMensagemErroPadrao() {
		addMensagem(MENSAGEM_ERRO_MOBILE, "Um erro ocorreu durante a execu��o desta opera��o. "
				+ "Se o problema persistir contacte o suporte atrav�s do \"Abrir Chamado\".");
	}

	/**
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractController#addMensagemInformation(java.lang.String)
	 */
	@Override
	public void addMensagemInformation(String mensagem) {
		addMensagem(MENSAGEM_INFORMATION_MOBILE, mensagem);
	}

	/**
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractController#addMensagemWarning(java.lang.String)
	 */
	@Override
	public void addMensagemWarning(String mensagem) {
		addMensagem(MENSAGEM_WARNING_MOBILE, mensagem);
	}

	
	/*
	 * Adiciona a mensagem com um parametro na requisicao que eh lido pela pagina cabecalho.jsp
	 * dos sistemas mobile
	 */
	private void addMensagem(String tipo, String mensagem){
		getCurrentRequest().setAttribute(tipo, mensagem);
	}
	
}
