/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 03/11/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.ensino.latosensu.dominio.TrabalhoFinalLato;
import br.ufrn.sigaa.ensino.latosensu.form.TrabalhoFinalLatoForm;
import br.ufrn.sigaa.ensino.latosensu.negocio.MovimentoTrabalhoFinalLato;
import br.ufrn.sigaa.ensino.latosensu.negocio.TrabalhoFinalLatoValidator;

/**
 * Action responsável pelo cadastramento de trabalhos finais do lato sensu
 *
 * @author Leonardo
 *
 */
public class TrabalhoFinalLatoAction extends AbstractCrudAction {

	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(new int[]{SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO}, req);
		
		TrabalhoFinalLatoForm trabalhoForm = (TrabalhoFinalLatoForm) form;
		GenericDAO dao = getGenericDAO(req);
		
		if( req.getParameter("id") != null ){
			trabalhoForm.setObj( dao.findByPrimaryKey( Integer.parseInt( req.getParameter("id") ), TrabalhoFinalLato.class) );
			if( req.getAttribute("remove") == null )
				addMensagem(new MensagemAviso("Ao alterar o Trabalho Final, você pode submeter um novo arquivo <br>" +
										"ou deixar o arquivo que foi submetido anteriormente.", TipoMensagemUFRN.WARNING), req);
		}
		
		
		if( req.getAttribute("remove") == null )
			prepareMovimento(SigaaListaComando.SUBMETER_TRABALHO_FINAL_LATO, req);
			
		return mapping.findForward(FORM);
	}
	
	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(new int[]{SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO}, req);
		
		TrabalhoFinalLatoForm trabalhoForm = (TrabalhoFinalLatoForm) form;
		
		// Validar dados
		TrabalhoFinalLatoValidator.validaTrabalhoFinal(trabalhoForm, trabalhoForm.getObj(), newListMensagens(req));
		if (flushErros(req)) {
			return edit(mapping, form, req, res);
		}
		
		// Popular movimento
		MovimentoTrabalhoFinalLato movTrabalhoFinal = new MovimentoTrabalhoFinalLato();
		
		movTrabalhoFinal.setCodMovimento(SigaaListaComando.SUBMETER_TRABALHO_FINAL_LATO);
		movTrabalhoFinal.setTrabalhoFinal(trabalhoForm.getObj());
		
		if(trabalhoForm.getArquivoTrabalhoFinal() != null){
			FormFile arquivoTrabalhoFinal = trabalhoForm.getArquivoTrabalhoFinal();
			if (arquivoTrabalhoFinal.getFileSize() != 0) {
				movTrabalhoFinal.setNome(arquivoTrabalhoFinal.getFileName());
				movTrabalhoFinal.setContentType(arquivoTrabalhoFinal.getContentType());
				movTrabalhoFinal.setDadosArquivo(arquivoTrabalhoFinal.getFileData());
			}
		}
		
		try {
			execute(movTrabalhoFinal, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}
		
		addInformation("Trabalho Final cadastrado com sucesso!", req);
		
		return edit(mapping, form, req, res);
	}
	
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(new int[]{SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO}, req);
		
		TrabalhoFinalLatoForm trabalhoForm = (TrabalhoFinalLatoForm) form;

		if (getForm(form).isConfirm()) {
			GenericDAO dao = getGenericDAO(req);
			TrabalhoFinalLato obj = null;
			try {
				obj = dao.findByPrimaryKey(trabalhoForm.getObj().getId(), TrabalhoFinalLato.class);
			} finally {
				dao.close();
			}

			MovimentoTrabalhoFinalLato movTrabalhoFinal = new MovimentoTrabalhoFinalLato();
			movTrabalhoFinal.setCodMovimento(SigaaListaComando.REMOVER_TRABALHO_FINAL_LATO);
			movTrabalhoFinal.setTrabalhoFinal(obj);

			try {
				execute(movTrabalhoFinal, req);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens().getMensagens(), req);
				req.setAttribute(mapping.getName(), form);
				return edit(mapping, form, req, res);
			}

			addMessage(req, "Remoção realizada com sucesso!",
					TipoMensagemUFRN.INFORMATION);

			// Limpar formulário, caso ele esteja em sessão
			req.getSession(false).removeAttribute(mapping.getName());

			return edit(mapping, form, req, res);
		} else {
			req.setAttribute("remove", true);
			prepareMovimento(SigaaListaComando.REMOVER_TRABALHO_FINAL_LATO, req);
			return edit(mapping, form, req, res);
		}
	}
}
