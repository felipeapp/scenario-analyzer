/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;

/**
 * Action que chama o processador para se cadastrar uma pasta no porta-arquivos.
 * 
 * @author davidpereira
 */
public class CadastroPastaAction extends SigaaAbstractAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		if ("cadastrar".equals(req.getParameter("acao")) || "alterar".equals(req.getParameter("acao"))) {
			PortaArquivosForm pForm = (PortaArquivosForm) form;
			PastaArquivos pasta = pForm.getPasta();
			
			prepareMovimento(SigaaListaComando.CADASTRAR_PASTA, req);

			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PASTA);
			mov.setObjMovimentado(pasta);

			executeWithoutClosingSession(mov, req);
			
			res.getWriter().write("{ text : '" + pasta.getNome() + "', id : " + pasta.getId() + ", pai : " + pasta.getIdPai() + "}");
			
			return null;
		} 
		
		return mapping.findForward("form");
	}	
	
}
