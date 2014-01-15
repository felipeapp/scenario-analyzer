/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.espacofisico.jsf.GestorUnidadesMBean;

/**
 * Action que controla a entrada no menu da infra-estrutura f�sica
 * 
 * @author Gleydson
 * @author Henrique Andr�
 * 
 */
public class EntrarInfraEstruturaAction extends SigaaAbstractAction {

	/**
	 * Guarda em um MBean as unidades que este usu�rio � respons�vel
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		setSubSistemaAtual(req, SigaaSubsistemas.INFRA_ESTRUTURA_FISICA);
		checkRole(new int[] { SigaaPapeis.GESTOR_INFRA_ESTRUTURA_FISICA, SigaaPapeis.RESPONSAVEL_RESERVA_ESPACO_FISICO }, req);

		
		GestorUnidadesMBean gestorUnidadesMBean = getGestorUnidadesMBean(req, response);
		gestorUnidadesMBean.carregarEspacosUnidades();
		
		return mapping.findForward("sucesso");
	}

	/**
	 * Pega o MBean que guarda informa��es sobre as permiss�es que o gestor possui em suas unidades
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	private GestorUnidadesMBean getGestorUnidadesMBean(HttpServletRequest req, HttpServletResponse res) {
		return getMBean("gestorUnidadesMBean", req, res);
	}

}
