/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.form.GrupoPesquisaForm;

/**
 * Action responsável por realizar buscas por grupos vinculados a projetos de pesquisa 
 * 
 * @author leonardo
 */
public class BuscarMembrosGrupoPesquisaAction extends AbstractCrudAction {
	
	/**
	 * Buscar os grupos associados aos grupos de pesquisa de acordo com os critérios de busca.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class, req);
		GrupoPesquisaForm grupoForm = (GrupoPesquisaForm) form;
		
		if (req.getParameter("popular") != null) {
			req.setAttribute(mapping.getName(), grupoForm);

			return mapping.findForward("listar");
		}
		
		String nome = null;
		int idCoordenador = 0;		
		
		Collection<GrupoPesquisa> bases = null;
		ListaMensagens erros = new ListaMensagens();
		
		// Definição dos filtros e validações
		for(int filtro : grupoForm.getFiltros() ){
			switch(filtro) {
			case GrupoPesquisaForm.GRUPO_PESQUISA_NOME:
				nome = grupoForm.getObj().getNome();
				ValidatorUtil.validateRequired(nome, "Nome", erros);
				break;
			case GrupoPesquisaForm.COORDENADOR:
				idCoordenador = grupoForm.getObj().getCoordenador().getId();
				ValidatorUtil.validateRequiredId(idCoordenador, "Coordenador", erros);
				break;
			}
		}
		
		// Verificar se foram detectados erros
		if (erros.isEmpty()) {
			bases = dao.findOtimizado(nome, idCoordenador);
		} else {
			addMensagens(erros.getMensagens(), req);
		}
		
		req.setAttribute("lista", bases);
		req.setAttribute(mapping.getName(), grupoForm);
		return mapping.findForward("listar");
	}
	
	/**
	 * Emissão de uma declaração feita pelo coordenador de acordo com os critérios de busca.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward emitirDeclaracaoCoordenadorBase(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		GrupoPesquisaForm grupoForm = (GrupoPesquisaForm) form;
		
		GrupoPesquisa base = getGenericDAO(req).findByPrimaryKey(grupoForm.getObj().getId(), GrupoPesquisa.class);
		
		if (base != null) {
			req.setAttribute("base", base);
			
			
			return mapping.findForward("declaracaoCoordenadorBase");
		} else {
			addMensagemErro("É necessário informar um docente para e emissão da declaração", req);
			return mapping.findForward("listar");
		}
		
	}
}
