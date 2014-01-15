package br.ufrn.sigaa.biblioteca.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Action que controla a entrada no menu da biblioteca
 * 
 * @author Gleydson
 * @author Jadson
 * @version 1.1 - Adicionando a liberação automática de entrada no módulo para servidores.
 */
public class EntrarBibliotecaAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {

		setSubSistemaAtual(req, SigaaSubsistemas.BIBLIOTECA);

		// Servidor pode entrar direto no subsistema da biblioteca porque o módulo do servidor fica dentro dele  //
		if(( (Usuario) getUsuarioLogado(req)).getServidor() == null ) {
			
			checkRole(
				new int[] {
						SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO,
						SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS,
						SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,
						SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_EMITE_DECLARACAO_QUITACAO,
						SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO,
						SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
						SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
						SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO,
						SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL,
						SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
						SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA
				}, req);
		}
		
		return mapping.findForward("sucesso");
	}

}
