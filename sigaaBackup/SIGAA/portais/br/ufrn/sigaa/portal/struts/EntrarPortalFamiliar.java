/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/09/2011
 *
 */
package br.ufrn.sigaa.portal.struts;

import static br.ufrn.sigaa.arq.jsf.SigaaAbstractController.CALENDARIO_SESSAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.portal.jsf.PortalFamiliarMBean;

/**
 * Action que verifica se o usuário possui acesso ao Portal do Familiar
 *  
 * @author Arlindo Rodrigues
 *
 */
public class EntrarPortalFamiliar  extends SigaaAbstractAction {
	
	/**
	 * Executa o processamento de verificação de permissão no portal
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		setSubSistemaAtual(req, SigaaSubsistemas.PORTAL_FAMILIAR);

		checkRole(new int[] { SigaaPapeis.FAMILIAR_MEDIO}, req);
		
		/** Se o usuário está associado a alguma unidade, pega o calendário vigente, 
		 * caso contrário pega o global vigente */
		CalendarioAcademico cal = null;
		
		Usuario usr = (Usuario) getUsuarioLogado(req);
		
		cal = CalendarioAcademicoHelper.getCalendario(null, null, 
				usr.getVinculoAtivo().getUnidade(), usr.getNivelEnsino(), null, null, null);
			
		req.getSession().setAttribute(CALENDARIO_SESSAO, cal);
		
		/** Carrega os dados do discente do vinculo ativo */
		DiscenteMedio discente = usr.getVinculoAtivo().getFamiliar().getDiscenteMedio();
		PortalFamiliarMBean bean = getBean(req, "portalFamiliar");
		bean.setDiscente(discente);

		return mapping.findForward("sucesso");
	}		

}
