/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 06/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;

/**
 * Processa a entrada do usuário no módulo Relações Internacionais.
 * 
 * @author Rafael Gomes
 * 
 */
public class EntrarRelacoesInternacionaisAction extends SigaaAbstractAction {

	/**
	 * Verifica se o usuário possui permissão e redireciona-o ao menu de operações correto.
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse response)
			throws Exception {
		verificarPermissoes(req);
		setSubSistemaAtual(req, SigaaSubsistemas.RELACOES_INTERNACIONAIS);

		if (isUserInRole(req, SigaaPapeis.GESTOR_TRADUCAO_DOCUMENTOS))
			return mapping.findForward("sucesso");
		else if (isUserInRole(req, SigaaPapeis.TRADUTOR_DADOS_ACADEMICOS))
			return mapping.findForward("sucessoTradutor");
		else
			return null;
	}

	/**
	 * Verifica se o usuário tem os papéis necessários para continuar a
	 * navegação
	 * 
	 * @param req
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	private void verificarPermissoes(HttpServletRequest req)
			throws SegurancaException, ArqException {
		checkRole(new int[] { SigaaPapeis.GESTOR_TRADUCAO_DOCUMENTOS,
				SigaaPapeis.TRADUTOR_DADOS_ACADEMICOS }, req);
	}
}
