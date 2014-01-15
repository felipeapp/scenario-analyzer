/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/11/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts.relatorios;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.form.CotaBolsasForm;

/**
 * Relatório quantitativo de solicitação de bolsas de iniciação científica
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioQuantitativoSolicitacaoCotasAction extends SigaaAbstractAction {

	/**
	 * Iniciar a operação e popular o formulário
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward iniciar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		req.setAttribute("cotas", getGenericDAO(req).findAll(CotaBolsas.class, "descricao", "desc"));
		return mapping.findForward("form");
	}

	/**
	 * Gerar o relatório
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward gerar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		CotaBolsasForm cotaForm = (CotaBolsasForm) form;

		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		Collection<?> resultado = planoDao.findResumoSolicitacoes(cotaForm.getObj());

		if (resultado.isEmpty()) {
			addMensagemErro("Não foram encontradas solicitações de bolsas para a cota selecionada.", req);
			req.setAttribute("cotas", getGenericDAO(req).findAll(CotaBolsas.class, "descricao", "desc"));
			return mapping.findForward("form");
		}

		planoDao.initialize(cotaForm.getObj());

		req.setAttribute("cota", cotaForm.getObj());
		req.setAttribute("resultado", resultado);
		return mapping.findForward("relatorio");
	}

}
