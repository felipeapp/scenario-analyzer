/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts.relatorios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.sigaa.arq.dao.pesquisa.CotaDocenteDao;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.form.CotaBolsasForm;

/**
 *
 * Relat�rio de acompanhamento de solicita��o e distribui��o de cotas de
 * bolsas e indica��es
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioAcompanhamentoCotasAction extends AbstractCrudAction {

	/**
	 * Popular formul�rio de consulta
	 * Chamado na JSP: /WEB-INF/jsp/pesquisa/menu/relatorio.jsp
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
	 * Gerar relat�rio de acordo com os crit�rios selecionados
	 * Chamado na JSP: /WEB-INF/jsp/pesquisa/relatorios/form_acompanhamento_cotas.jsp
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward gerar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		CotaBolsasForm cotaForm = (CotaBolsasForm) form;

		// Buscar dados do relat�rio
		CotaDocenteDao cotaDocenteDao = getDAO(CotaDocenteDao.class, req);
		Collection relatorio = cotaDocenteDao.findResumoDistribuicaoCota( cotaForm.getObj() );

		// Verificar se algum resultado foi encontrado
		if (relatorio == null ||  relatorio.isEmpty()) {
			addMensagemErro("N�o foram encontrados dados referentes � cota selecionada", req);
			return iniciar(mapping, form, req, res);
		}

		HashMap mapa = (HashMap) relatorio.iterator().next();
		int tamanho = Integer.parseInt((String)mapa.get("tamanho"));
		ArrayList tiposBolsa = new ArrayList();
		for(int i = 0; i < tamanho; i++){
			tiposBolsa.add(mapa.get("tipo"+i));
		}
		
		req.setAttribute("cota", cotaDocenteDao.findByPrimaryKey(cotaForm.getObj().getId(), CotaBolsas.class));
		req.setAttribute("tamanho", tamanho);
		req.setAttribute("tiposBolsa", tiposBolsa);
		req.setAttribute("relatorio", relatorio);
		return mapping.findForward("relatorio");
	}

}
