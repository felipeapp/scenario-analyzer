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
 * Action que chama o processador para se renomear um arquivo.
 * 
 * @author davidpereira
 *
 */
public class RenomearArquivoAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
	
		PortaArquivosForm arqForm = (PortaArquivosForm) form;
		ArquivoUsuario arquivo = arqForm.getArquivo();
		
		String nome = arquivo.getNome();

		GenericDAO dao = getGenericDAO(req);
		arquivo = dao.findByPrimaryKey(arquivo.getId(), ArquivoUsuario.class);
		arquivo.setNome(nome);
		
		prepareMovimento(SigaaListaComando.RENOMEAR_ARQUIVO, req);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.RENOMEAR_ARQUIVO);
		mov.setObjMovimentado(arquivo);
		
		executeWithoutClosingSession(mov, req);
		
		if (arquivo.getPasta() == null)
			res.getWriter().write("-1");
		else
			res.getWriter().write(String.valueOf(arquivo.getPasta().getId()));
		
		return null;
		
	}
	
}
