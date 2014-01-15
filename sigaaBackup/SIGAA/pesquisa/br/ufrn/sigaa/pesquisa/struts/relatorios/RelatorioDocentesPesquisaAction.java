/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts.relatorios;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.pesquisa.form.relatorios.RelatorioDocentesPesquisaForm;
import br.ufrn.sigaa.pesquisa.relatorios.ParticipacaoDocenteProjetos;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Action respons�vel pela gera��o do relat�rio de docentes
 * participantes em projetos de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioDocentesPesquisaAction extends SigaaAbstractAction {

	/**
	 * Iniciar a opera��o e popular o formul�rio
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward iniciar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		req.setAttribute("situacoesProjeto", getGenericDAO(req).findByExactField(TipoSituacaoProjeto.class, "contexto", "P", "asc", "id" ));
		return mapping.findForward("form");
	}

	/**
	 * Gerar o relat�rio
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward gerar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		RelatorioDocentesPesquisaForm relatorioForm = (RelatorioDocentesPesquisaForm) form;

		// Popular os dados do relat�rio
		MembroProjetoDao membroDao = getDAO(MembroProjetoDao.class, req);
		Collection<ParticipacaoDocenteProjetos> participacoes = membroDao.findPartipacaoProjetos(
				relatorioForm.getAno(),
				relatorioForm.getSituacao().getId() != -1 ? relatorioForm.getSituacao() : null );

		req.setAttribute(mapping.getName(), relatorioForm);

		// Verificar se algum dado foi retornado
		if ( participacoes != null && !participacoes.isEmpty() ) {
			req.setAttribute("participacoes", participacoes);
			return mapping.findForward("relatorio");
		} else {
			addWarning("N�o foram encontrados docentes de acordo com os crit�rios selecionados", req);
			return iniciar(mapping, relatorioForm, req, res);
		}
	}

}
