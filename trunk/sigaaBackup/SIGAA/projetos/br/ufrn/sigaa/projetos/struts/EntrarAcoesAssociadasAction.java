package br.ufrn.sigaa.projetos.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Efetua o acesso ao módulo de ações acadêmicas integradas. 
 * @author Ilueny
 *
 */
public class EntrarAcoesAssociadasAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (isUserInRole(request, SigaaPapeis.MEMBRO_COMITE_INTEGRADO, 
				SigaaPapeis.AVALIADOR_ACOES_ASSOCIADAS, SigaaPapeis.GESTOR_BOLSAS_ACOES_ASSOCIADAS)) {
			setSubSistemaAtual(request, SigaaSubsistemas.ACOES_ASSOCIADAS);
			request.getSession().setAttribute("tipoEdital", Edital.ASSOCIADO);
			request.setAttribute("hideSubsistema", Boolean.TRUE);
			return mapping.findForward("sucesso");
		} else {
			throw new SegurancaException();
		}

	}

}
