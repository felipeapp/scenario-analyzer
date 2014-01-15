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

/**
 * Action para remoção de arquivos do porta arquivos via AJAX.
 * 
 * @author David Pereira
 *
 */
public class RemoverArquivoAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		PortaArquivosForm arqForm = (PortaArquivosForm) form;
		ArquivoUsuario arquivo = arqForm.getArquivo();
		
		GenericDAO dao = getGenericDAO(req);
		arquivo = dao.findByPrimaryKey(arquivo.getId(), ArquivoUsuario.class);
		
		prepareMovimento(SigaaListaComando.REMOVER_ARQUIVO, req);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.REMOVER_ARQUIVO);
		mov.setObjMovimentado(arquivo);
		
		executeWithoutClosingSession(mov, req);
		
		res.setContentType("text/plain");
		if (arquivo == null || arquivo.getPasta() == null)
			res.getWriter().write("-1");
		else
			res.getWriter().write(String.valueOf(arquivo.getPasta().getId()));
		res.getWriter().flush();
		return null;
	}
	
}
