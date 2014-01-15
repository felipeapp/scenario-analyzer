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
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;

/**
 * Action para mover um arquivo entre pastas 
 * 
 * @author David Pereira
 *
 */
public class MoverArquivoAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		PortaArquivosForm arqForm = (PortaArquivosForm) form;
		
		int idArquivo = arqForm.getArquivo().getId();
		int idDestino = arqForm.getPasta().getId();
		
		GenericDAO dao = getGenericDAO(req);
		ArquivoUsuario arquivo = dao.findByPrimaryKey(idArquivo, ArquivoUsuario.class);
		
		int idOrigem = -1;
		if (arquivo.getPasta() != null)
			idOrigem = arquivo.getPasta().getId();
		arquivo.setPasta(new PastaArquivos());
		arquivo.getPasta().setId(idDestino);
		
		prepareMovimento(SigaaListaComando.MOVER_ARQUIVO, req);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.MOVER_ARQUIVO);
		mov.setObjMovimentado(arquivo);
		
		executeWithoutClosingSession(mov, req);
		
		res.getWriter().write(String.valueOf(idOrigem));
		
		return null;
	}
	
}
