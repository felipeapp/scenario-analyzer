/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;

/**
 *
 * @author amdantas
 *
 */
public class MenuAction extends SigaaAbstractAction {

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute("hideSubsistema", Boolean.TRUE);
		
		if (Sistema.isCaixaPostalAtiva(Sistema.SIGAA)) {
			setarExibicaoCxPostalEAtualizarMsgNaoLidas(request);
		}
		
		if (request.getParameter("dialog") == null) {
			return mapping.findForward("sucesso");
		} else {
			return mapping.findForward("popup");
		}
	}

	private void setarExibicaoCxPostalEAtualizarMsgNaoLidas(HttpServletRequest request) throws DAOException, ArqException {
		
		boolean acessoCxPostal = MensagensHelper.acessoCaixaPostal(getUsuarioLogado(request).getId()) ? false : true;
		
		// Adicionando atributo em sessão para determinar se o usuário verá ou não o link para caixa postal
		request.getSession().setAttribute("acessarCaixaPostal", acessoCxPostal);

		if(acessoCxPostal) {
			
			String modoCxPostal = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.MODO_OPERACAO_CAIXA_POSTAL);
			int qtdNaoLidas = MensagensHelper.qtdNaoLidaCaixaEntrada(getUsuarioLogado(request).getId());
			
			if ("MOSTRAR_NAO_LIDAS".equals(modoCxPostal)) {
				request.getSession().setAttribute("qtdMsgsNaoLidasCxPostal", qtdNaoLidas);
			}
		}
	}
}