/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 24/07/2006
 *
 */
package br.ufrn.sigaa.caixa_postal.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.caixa_postal.dominio.Mensagem;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Action para realizar a leitura de uma mensagem
 *
 * @author David Ricardo
 *
 */
public class LerMensagemAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		MensagemDAO msgDAO = new MensagemDAO();

		try {
			int id = Integer.parseInt(req.getParameter("id"));

			br.ufrn.arq.caixa_postal.Mensagem msg = (br.ufrn.arq.caixa_postal.Mensagem) msgDAO.findByPrimaryKey(id, br.ufrn.arq.caixa_postal.Mensagem.class);

			Mensagem mensagem = MensagensHelper.decorateMensagem(msg, true);			
			
			res.setContentType("application/xml");
			res.setHeader("Cache-Control", "no-cache");
			res.setHeader("charset", "UTF-8");

			res.getWriter().write("<MENSAGEM>");
			res.getWriter().write("<ID>" + mensagem.getId() + "</ID>");
			res.getWriter().write("<ASSUNTO>" + mensagem.getTitulo() + "</ASSUNTO>");
			res.getWriter().write("<USUARIO>" + mensagem.getUsuario().getLogin() + "/" + ((Usuario)mensagem.getUsuario()).getVinculoAtivo().getUnidade().getSigla() + "</USUARIO>");
			res.getWriter().write("<REMETENTE>" + mensagem.getRemetente().getLogin() + "/" + ((Usuario)mensagem.getUsuario()).getVinculoAtivo().getUnidade().getSigla() + "</REMETENTE>");
			res.getWriter().write("<TEXTO>" + UFRNUtils.escapeHTML(mensagem.getMensagem()) + "</TEXTO>");
			res.getWriter().write("</MENSAGEM>");

		} finally {
			msgDAO.close();
		}

		return null;
	}

}
