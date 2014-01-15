/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 06/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.arq.dao.monitoria.RelatorioProjetoMonitorDao;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * 
 * Respons�vel pela visualiza��o de avisos de desligamento de discentes com relat�rios pendentes de valida��o.
 * @autor Gleydson
 */
public class EntrarMonitoriaAction extends AbstractAction {


	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		checkRole(SigaaPapeis.GESTOR_MONITORIA, request);
		request.setAttribute("hideSubsistema", Boolean.TRUE);
		setSubSistemaAtual(request, SigaaSubsistemas.MONITORIA);
		request.getSession().setAttribute("tipoEdital", Edital.MONITORIA);
		//Informa o total de relat�rios de desligamento pendentes de valida��o da prograd.
		//Utilizado na exibi��o de mensagem de aviso no menu principal de monitoria.
		int total = getDAO(RelatorioProjetoMonitorDao.class, request).totalRelatoriosDesligamentoPendentesValidacaoPrograd();
		if ( total > 0) {
			addWarning("Existe(m) " + total + " Relat�rio(s) de Desligamento de Monitor Pendente(s) de Valida��o.", request);
		}
		
		return mapping.findForward("sucesso");

	}


}
