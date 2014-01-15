/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.ConsultorDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.form.NotificacaoConsultoresForm;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoNotificacaoConsultores;

/**
 * Action respons�vel por realizar a notifica��o dos consultores
 * que possuem projetos pendentes de avalia��o
 *
 * @author Ricardo Wendell
 *
 */
public class NotificarConsultoresAction extends AbstractCrudAction {

	/**
	 * Mostrar tela de confirma��o
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		NotificacaoConsultoresForm notificacaoForm = (NotificacaoConsultoresForm) form;

		// Buscar total de consultores que precisam ser notificados
		ConsultorDao consultorDao = getDAO(ConsultorDao.class, req);
		req.setAttribute( "totalPendentes", consultorDao.findTotalPendentesNotificacao() );

		// Buscar template de email previamente armazenado
		if ( !flushErros(req) ) {
			notificacaoForm.setTemplate( ParametroHelper.getInstance().getParametro( ConstantesParametro.TEMPLATE_NOTIFICACAO_CONSULTOR ) );
			req.setAttribute(mapping.getName(), notificacaoForm);
		}

		// Redirecionar para a tela de confirma��o
		return mapping.findForward("confirmacao");
	}

	/**
	 * Realizar notifica��o de consultores
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward notificar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		NotificacaoConsultoresForm notificacaoForm = (NotificacaoConsultoresForm) form;

		try {
			// Chamar processador
			prepareMovimento(SigaaListaComando.NOTIFICAR_CONSULTORES, req);
			MovimentoNotificacaoConsultores notificacaoMov = new MovimentoNotificacaoConsultores();

			notificacaoMov.setCodMovimento(SigaaListaComando.NOTIFICAR_CONSULTORES);
			notificacaoMov.setTemplate( notificacaoForm.getTemplate() );
			notificacaoMov.setHost( ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_ACESSO) +
					req.getContextPath() );

			Integer total = (Integer) execute(notificacaoMov, req);

			addInformation("Notifica��o de consultores realizada com sucesso!", req);
			req.setAttribute("total", total);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return popular(mapping, form, req, res);
		}

		// Redirecionar para a tela de comprovante
		return mapping.findForward("comprovante");
	}

	/**
	 * Exerce a fun��o de notificar os consultores Especiais
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward notificarConsultoresEspeciais(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		try {
			// Chamar processador
			prepareMovimento(SigaaListaComando.NOTIFICAR_CONSULTORES_ESPECIAIS, req);
			MovimentoNotificacaoConsultores notificacaoMov = new MovimentoNotificacaoConsultores();

			notificacaoMov.setCodMovimento(SigaaListaComando.NOTIFICAR_CONSULTORES_ESPECIAIS);
			notificacaoMov.setHost( ParametroHelper.getInstance().getParametro(ConstantesParametro.ENDERECO_ACESSO) +
					req.getContextPath() );

			Integer total = (Integer) execute(notificacaoMov, req);

			addInformation("Notifica��o de consultores especiais realizada com sucesso!", req);
			req.setAttribute("total", total);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return popular(mapping, form, req, res);
		}

		// Redirecionar para a tela de comprovante
		return mapping.findForward("comprovante");
	}
}
