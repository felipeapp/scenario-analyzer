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

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;

/**
 * Action para remoção de uma pasta do porta-arquivos
 * via Ajax
 * @author David Pereira
 *
 */
public class RemocaoPastaAction extends SigaaAbstractAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		PortaArquivosForm pForm = (PortaArquivosForm) form;
		PastaArquivos pasta = pForm.getPasta();
		
		GenericDAO dao = getGenericDAO(req);
		pasta = dao.findByPrimaryKey(pasta.getId(), PastaArquivos.class);
		
		prepareMovimento(SigaaListaComando.REMOVER_PASTA, req);

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.REMOVER_PASTA);
		mov.setObjMovimentado(pasta);

		executeWithoutClosingSession(mov, req);
			
		if (pasta != null)
			res.getWriter().write("{ text : '" + pasta.getNome() + "', id : " + pasta.getId() + ", pai : " + pasta.getIdPai() + "}");
		
		return null;

	}	
	
}
