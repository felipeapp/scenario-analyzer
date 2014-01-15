/*
 * SIGAA - Sistema Integrado de Gest�o Acad�mica
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Criado em 24/10/2006
 *
 */
package br.ufrn.sigaa.arq.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.NegocioException;

/**
 * Action utilizada para recuperar arquivos armazenados no sistema
 *
 * @author Victor Hugo
 * @author Ricardo Wendell
 *
 */
public class EnviarArquivoAction extends SigaaAbstractAction {

	/**
	 * M�todo respons�vel pela recupera��o dos arquivos.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward enviar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		// Verificar id do arquivo informado
		String idArquivoString = req.getParameter("idarquivo");
		
		if ( idArquivoString == null || "".equals(idArquivoString) 
				|| EnvioArquivoHelper.recuperaContentTypeArquivo(Integer.parseInt(idArquivoString)) == null) {
			throw new NegocioException("N�o existe um arquivo para download para a op��o selecionada");
		}

		int idArquivo = Integer.parseInt( idArquivoString );
		// Recuperar arquivo
		EnvioArquivoHelper.recuperaArquivo( res, idArquivo, true );
		return null;
	}

}