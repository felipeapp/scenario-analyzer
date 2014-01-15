/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/04/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatoriosPesquisaDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.pesquisa.form.RelatoriosPesquisaForm;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioSinteticoFinanciamentos;

/**
 * Action respons�vel pela gera��o de relat�rios de pesquisa
 *
 * @author ricardo
 */
public class RelatoriosPesquisaAction extends SigaaAbstractAction {

	/**
	 * Esse m�todo � respons�vel por popular o financiamento sint�tico dos relat�rios de pesquisa
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularFinanciamentosSintetico(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		RelatoriosPesquisaForm relatoriosForm = (RelatoriosPesquisaForm) form;

		relatoriosForm.setAnoFim(getAnoAtual());
		req.setAttribute(mapping.getName(), relatoriosForm);

		return mapping.findForward("formFinanciamentosSintetico");
	}

	/**
	 * Respons�vel por realizar o financiamento sint�tico
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward financiamentosSintetico(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		RelatoriosPesquisaForm relatoriosForm = (RelatoriosPesquisaForm) form;
		RelatoriosPesquisaDao relatoriosDao = getDAO(RelatoriosPesquisaDao.class, req);

		// Validar formul�rio
		ListaMensagens erros = new ListaMensagens();
		relatoriosForm.validarAnos(erros);
		if ( !erros.isEmpty() ) {
			addMensagens(erros.getMensagens(), req);
			return mapping.findForward("formFinanciamentosSintetico");
		}

		// Realizar a consulta
		Map<Integer, LinhaRelatorioSinteticoFinanciamentos> relatorio;
		relatorio = relatoriosDao.findFinanciamentosSintetico(relatoriosForm.getAnoInicio(), relatoriosForm.getAnoFim());

		if ( relatorio == null || relatorio.isEmpty() ) {
			addMensagemErro(" N�o foram encontrados projetos para os crit�rios informados", req);
			return mapping.findForward("formFinanciamentosSintetico");
		} else {
			

		}

		req.setAttribute("form", relatoriosForm);
		req.setAttribute("relatorio", relatorio);
		return mapping.findForward("financiamentosSintetico");
	}

}
