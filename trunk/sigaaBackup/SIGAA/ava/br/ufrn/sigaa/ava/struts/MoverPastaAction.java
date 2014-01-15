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

import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.ava.negocio.MovimentoPortaArquivos;

/**
 * Action para mover pastas do porta-arquivos
 * 
 * @author David Pereira
 *
 */
public class MoverPastaAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		int idOrigem = Integer.parseInt(req.getParameter("origem"));
		int idDestino = Integer.parseInt(req.getParameter("destino"));
		
		MovimentoPortaArquivos mov = new MovimentoPortaArquivos();
		mov.setCodMovimento(SigaaListaComando.MOVER_PASTA);
		mov.setOrigem(idOrigem);
		mov.setDestino(idDestino);
		
		prepareMovimento(SigaaListaComando.MOVER_PASTA, req);
		executeWithoutClosingSession(mov, req);
		
		return null;
	}
	
}
