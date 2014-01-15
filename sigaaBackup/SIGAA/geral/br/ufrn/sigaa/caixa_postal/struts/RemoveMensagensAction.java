/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.caixa_postal.struts;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.arq.web.struts.ConstantesActionGeral;
import br.ufrn.sigaa.caixa_postal.dominio.Mensagem;

/**
 * 
 * Action para remover as mensagens
 * 
 * @author Gleydson Lima
 * 
 */
public class RemoveMensagensAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		switch (getAcao(req)) {

		// remover 
		case ConstantesActionGeral.REMOVER:

			String[] idsRemover = req.getParameterValues("idMsg");

			GenericDAO dao = getGenericDAO(req);
			ArrayList mensagens = new ArrayList();

			try {

				for (int a = 0; a < idsRemover.length; a++) {
					mensagens.add(dao.findByPrimaryKey(Integer
							.parseInt(idsRemover[a]), Mensagem.class));

				}

				return mapping.findForward("conf_remover");

			} catch (Exception e) {
				e.printStackTrace();
				return mapping.findForward("tela_inicial");
			}

		}

		return null;
	}

}
