/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/03/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.form.CotaBolsasForm;

/**
 * Action responsável pelo cadastro de períodos de cotas de bolsas
 *
 * @author Ricardo Wendell
 *
 */
public class CotaBolsasAction extends AbstractCrudAction {

	/**
	 * Serve para popular as Cotas de bolsas.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		CotaBolsasForm cotaForm = (CotaBolsasForm) form;
		GenericDAO dao = getGenericDAO(req);

		if( req.getParameter("id") != null )
			cotaForm.setObj( dao.findByPrimaryKey( Integer.parseInt( req.getParameter("id") ), CotaBolsas.class) );
		
		
		// Buscar todas as cotas cadastradas
		req.setAttribute("cotas", dao.findAll(CotaBolsas.class,"descricao", "desc"));

		prepareMovimento(SigaaListaComando.CADASTRAR_PERIODO_COTAS, req);
		return mapping.findForward("form");
	}

	/***
	 * Consiste na realização do cadastro de uma cota de bolsa
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward cadastrar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		CotaBolsasForm cotaForm = (CotaBolsasForm) form;
		cotaForm.validate(req);
		if (!flushErros(req)) {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PERIODO_COTAS);
			mov.setObjMovimentado(cotaForm.getObj());

			try {
				execute(mov, req);
				addMensagem(
						new MensagemAviso("Informações da Cota " + cotaForm.getObj().getDescricao() + " registradas com sucesso!",
								TipoMensagemUFRN.INFORMATION)
						, req);
			} catch (NegocioException e) {
				e.printStackTrace();
				addMensagens(e.getListaMensagens().getMensagens(), req);
			}

		}
		return popular(mapping, form, req, res);
	}

}
