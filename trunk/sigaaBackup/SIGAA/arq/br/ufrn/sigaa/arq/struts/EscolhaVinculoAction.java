/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 *  Created on 25/10/2007
 */
package br.ufrn.sigaa.arq.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.comum.jsf.VerTelaAvisoLogonMBean;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.portal.jsf.PortalDiscenteMBean;
import br.ufrn.sigaa.portal.jsf.PortalDocenteMBean;

/**
 * Action para que o usu�rio possa escolher, dentre os seus v�nculos
 * com a Institui��o, qual ele deseja utilizar.
 *
 * @author David Pereira
 *
 */
public class EscolhaVinculoAction extends SigaaAbstractAction {

	/** Representa a p�gina da caixa postal */
	public static final String CAIXA_POSTAL = "caixaPostal";

	/** Representa uma p�gina quando uma opera��o foi conclu�da com sucesso */
	public static final String SUCESSO = "sucesso";
	
	/** Representa uma p�gina quando uma opera��o foi conclu�da com falha */
	public static final String FALHA = "falha";


	/** Representa uma p�gina de v�nculos do usu�rio */
	public static final String VINCULOS = "vinculos";

	/**
	 * Listar os v�nculos do usu�rio para ele escolher um.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\ava\PortaArquivos\cabecalho.jsp</li>
	 *    <li>sigaa.war\ava\PortaArquivos\menu.jsp</li>
	 *    <li>sigaa.war\WEB-INF\jsp\include\cabecalho.jsp</li>
	 *   </ul>
	 *
	 * @throws Exception
	 */
	public ActionForward listar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		Usuario usuario = (Usuario) getUsuarioLogado(req);

		if (usuario.getVinculos() == null || usuario.getVinculos().size() <= 1) {
			throw new NegocioException("O usu�rio n�o possui m�ltiplos v�nculos. N�o � poss�vel alterar o v�nculo ativo.");
		}

		return mapping.findForward(VINCULOS);
	}

	/**
	 * Pegar o v�nculo escolhido e seta como v�nculo ativo do usu�rio.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\vinculos.jsp</li>
	 *   </ul>
	 *
	 * @throws Exception
	 */
	public ActionForward escolher(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		MensagemDAO dao = getDAO(MensagemDAO.class, req);

		// V�nculo escolhido pelo usu�rio
		String vinculoStr = req.getParameter("vinculo");
		if (isEmpty(vinculoStr)) throw new NegocioException("Nenhum v�nculo foi escolhido.");

		int vinculo = Integer.parseInt(vinculoStr);

		try {
			Usuario usuario = (Usuario) getUsuarioLogado(req);
			usuario.setVinculoAtivo(vinculo);

			VinculoUsuario.popularVinculoAtivo(usuario);
			
			if (usuario.getVinculoAtivo().isVinculoServidor()) {
				usuario.setServidor(usuario.getVinculoAtivo().getServidor());
			} else if (usuario.getVinculoAtivo().isVinculoDiscente()) {
				usuario.setDiscente(usuario.getVinculoAtivo().getDiscente().getDiscente());
			} else if (usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
				usuario.setDocenteExterno(usuario.getVinculoAtivo().getDocenteExterno());
			} else if (usuario.getVinculoAtivo().isVinculoTutorOrientador()) {
				usuario.setTutor(usuario.getVinculoAtivo().getTutor());
			} else if (usuario.getVinculoAtivo().getTipoVinculo().isFamiliar()){
				usuario.setDiscente(usuario.getVinculoAtivo().getFamiliar().getDiscenteMedio().getDiscente());
			}

			req.getSession().setAttribute("usuario", usuario);
			
			if (isAcessoMobile(req)) { 
				if (usuario.getVinculoAtivo().isVinculoDiscente()) {
					return mapping.findForward(SUCESSO);
				}else {
					return mapping.findForward(FALHA);
				}
			}
			
			PortalDocenteMBean portal = (PortalDocenteMBean) req.getSession().getAttribute("portalDocente");
			if( portal != null ) {
				portal.getPerfilLogado(req);
				portal.setTurmasAbertas( null );
			}

			PortalDiscenteMBean portalDiscente = (PortalDiscenteMBean) req.getSession().getAttribute("portalDiscente");
			if( portalDiscente != null )
				portalDiscente.clear();
			
			VerTelaAvisoLogonMBean telaAviso = getBean(req, "verTelaAvisoLogonMBean");
			telaAviso.iniciar("/paginaInicial.do", getUsuarioLogado(req), getSistema(req), req, res);
			//telaAviso.iniciar("/progresso.jsf", getUsuarioLogado(req), getSistema(req), req, res);
			
			return null;
		} finally {
			dao.close();
		}
	}
	
	/** Verifica se o usu�rio est� acessando a partir de um dispositivo m�vel. */
	private boolean isAcessoMobile(HttpServletRequest request) throws Exception {
		String reqUrl = request.getRequestURI();		
		reqUrl = reqUrl.substring(request.getContextPath().length());
		return (reqUrl.indexOf("/escolhaVinculoMobile.do") > -1);
	}

}
