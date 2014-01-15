package br.ufrn.sigaa.apedagogica.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

/**
 * Classe verifica os pap�is que possuem acesso ao m�dulo do Programam de Atualiza��o Pedag�gica(PAP)
 * @author M�rio Rizzi
 *
 */
public class EntrarProgramaAtualizacaoPedagogicaAction extends SigaaAbstractAction  {

	/**
	 * Executa o processamento de verifica��o de permiss�o no m�dulo
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		setSubSistemaAtual(req, SigaaSubsistemas.PROGRAMA_ATUALIZACAO_PEDAGOGICA);

		checkRole(new int[] { SigaaPapeis.GESTOR_PAP}, req);

		return mapping.findForward("sucesso");
	}		
	
}
