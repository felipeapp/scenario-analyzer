/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractWizardAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricularPrograma;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.form.DisciplinaForm;
import br.ufrn.sigaa.ensino.latosensu.form.CursoLatoForm;
import br.ufrn.sigaa.ensino.negocio.dominio.ComponenteCurricularMov;

/**
 * Action para cadastrar, editar, listar ou visualizar a(s) disciplinas.
 * 
 * @author Andre M Dantas
 */
public class DisciplinaAction extends AbstractWizardAction {

	private static final String RESUMO = "resumo";

	/**
	 * O método abaixo é responsável por editar uma disciplina previamente escolhida.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp
	 * 		/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/medio/menu/disciplina.jsp
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		DisciplinaForm dForm = (DisciplinaForm) form;
		ParametrosGestoraAcademicaDao pdao = getDAO(ParametrosGestoraAcademicaDao.class, req);
		try {
			ParametrosGestoraAcademica param = pdao.findByUnidade(getUnidadeGestora(req), getNivelEnsino(req));
			int max = param.getQtdAvaliacoes();
			dForm.setMaxAvaliacoes(new ArrayList<Integer>(max));
			for (int i = max; i > 0; i--) {
				dForm.getMaxAvaliacoes().add(i);
			}
		} finally {
			pdao.close();
		}
		ActionForward ret = super.edit(mapping, form, req, res);
		ComponenteCurricular cc = dForm.getObj();
		if (cc.getPrograma() == null) {
			cc.setPrograma(new ComponenteCurricularPrograma());
		}
		if (cc.getId() > 0) {
			formatarExpressoesRequisitos(cc, req);
		}

		return ret;
	}
	

	/**
	 * Esse método abaixo tem a função de formatar as expressões que são requisitos, presente 
	 * na hora de editar e visualizar.
	 * 
	 * @param cc
	 * @param req
	 */
	private void formatarExpressoesRequisitos(ComponenteCurricular cc, HttpServletRequest req) {
		try {
			ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class, req);
			ExpressaoUtil.buildExpressaoFromDB(cc, dao);
		} catch (Exception e) {
			addMensagemErro("Não foi possível carregar os requisitos dessa disciplina", req);
		}
	}

	/**
	 * O método tem como função listar todas as disciplinas
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/curso.jsp
	 * 		/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/medio/menu/disciplina.jsp
	 * 		/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp     
	 * 		/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/menus/menu_lato_coordenador.jsp
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		DisciplinaDao dao = getDAO(DisciplinaDao.class, req);

		getForm(form).checkRole(req);
		getForm(form).referenceData(req);
		populateRequest(req, getForm(form).getReferenceData());

		Collection lista = null;

		/* Popular formulário de Busca */
		SigaaForm sForm = getForm(form);
		Map<String, String> searchMap = sForm.getSearchMap(req);

		/* Retirar dados da busca de sessão */
		if (getForm(form).isUnregister()) {
			searchMap = null;
			getForm(form).unregisterSearchData(req);
		}

		if (searchMap != null) {
			Set<Entry<String, String>> entrySet = searchMap.entrySet();
			for (Entry<String, String> entry : entrySet)
				BeanUtils.setProperty(sForm, entry.getKey(), entry.getValue());
			req.setAttribute(mapping.getName(), sForm);
		}

		if (getPaging(req) == null) {
			req.setAttribute("pagingInformation", new PagingInformation(0));
		}

		/* Realiza buscas */
		if (getForm(form).isBuscar())
			if(getSubSistemaCorrente(req).equals(SigaaSubsistemas.LATO_SENSU))
				lista = new CursoLatoForm().customSearch(req);
			else {
				String tipoBusca = getForm(form).getSearchItem(req, "tipoBusca");
				String codNome = null;//Todos
				if ("3".equals(tipoBusca)){//Curso
					lista = getForm(form).customSearch(req);
				} else {
					if ("1".equals(tipoBusca))//Código				
						codNome = getForm(form).getSearchItem(req, "obj.codigo");
					else if ("2".equals(tipoBusca))//Nome		
						codNome = getForm(form).getSearchItem(req, "obj.nome");
					lista = dao.findByNomeOuCodigo(codNome, ((Usuario)getUsuarioLogado(req)).getVinculoAtivo().getUnidade().getId(), getNivelEnsino(req));
				}
			}
		else
			lista = getCustomList();

		if (lista == null){
			if( getSubSistemaCorrente(req).equals(SigaaSubsistemas.LATO_SENSU) )
					lista = dao.findAll(0, getNivelEnsino(req), getPaging(req));
		}

		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, lista);

		return mapping.findForward(LISTAR);
	}

	/**
	 * Exerce a função de persistir uma nova disciplina. 
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/disciplina/form.jsp
	 */
	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		getForm(form).checkRole(req);

		ComponenteCurricular disciplina = ((DisciplinaForm) form).getObj();
		disciplina.setNivel(getNivelEnsino(req));
		getForm(form).validate(req);
		
		if (disciplina.validate().size() >= 1) {
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}
		
		
		try {
			ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class, req);
			ExpressaoUtil.buildExpressaoToDB(disciplina, dao, true);
		} catch (Exception e) {
			addMensagemErro("Expressões mal formadas: informe as expressões entre parêntesis.", req);
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}

		if (flushErros(req)) {
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}
		disciplina.getDetalhes().setComponente(disciplina.getId());
		disciplina.getTipoComponente().setId(TipoComponenteCurricular.DISCIPLINA);
		disciplina.setTipoAtividade(null);
		disciplina.setFormaParticipacao(null);
		disciplina.setTipoAtividadeComplementar(null);
		disciplina.setComponentesCursoLato(null);
		disciplina.setSubUnidades(null);
		disciplina.getPrograma().setComponenteCurricular(disciplina);

		if (disciplina.getId() == 0) {
			disciplina.setNivel(getNivelEnsino(req));
			prepareMovimento(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR, req);
		} else
			prepareMovimento(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR, req);

		ComponenteCurricularMov mov = new ComponenteCurricularMov();
		mov.setProcessarExpressoes(true);
		mov.setObjMovimentado(disciplina);
		mov.setCodMovimento(getUltimoComando(req));
		ComponenteCurricular volta = null;
		try {
			volta = (ComponenteCurricular) execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			req.setAttribute(mapping.getName(), form);
			return mapping.findForward(FORM);
		}
		getForm(form).clear();

		if (SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR.equals(getUltimoComando(req))) {
			addMessage(req, "Disciplina "+volta.getCodigo()+" cadastrada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
			return edit(mapping, form, req, res);
		} else {
			addMessage(req, "Disciplina "+volta.getCodigo()+" alterada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
			return list(mapping, form, req, res);
		}
	}

	/**
	 * Exerce a função de cancelar uma operação na qual está executando.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/disciplina/form.jsp
	 * 		/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/disciplina/lista.jsp     
	 */
	@Override
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ActionForward ret = super.cancel(mapping, form, req, res);
		if (ArqListaComando.ALTERAR.equals(getUltimoComando(req)) ||
				ArqListaComando.REMOVER.equals(getUltimoComando(req)))
			return list(mapping, form, req, res);
		return ret;
	}

	/**
	 * O método tem como objetivo exibir os dados da disciplina selecionada.
	 * 
	 *  JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/disciplina/lista.jsp
	 */
	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		int id = RequestUtils.getIntParameter(req, "id");
		GenericDAO dao = getGenericDAO(req);
		ComponenteCurricular cc = null;
		try {
			cc = dao.findByPrimaryKey(id, ComponenteCurricular.class);
			req.setAttribute("disciplina", cc);
		} finally {
			dao.close();
		}
		formatarExpressoesRequisitos(cc, req);
		return mapping.findForward(RESUMO);
	}

	/**
	 * Tem como função excluir uma disciplina selecionada.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/disciplina/lista.jsp
	 */
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		SigaaForm sigaaForm = (SigaaForm) form;

		getForm(form).checkRole(req);

		if (getForm(form).isConfirm()) {
			GenericDAO dao = getGenericDAO(req);
			PersistDB obj = null;
			try {
				obj = dao.findByPrimaryKey(sigaaForm.getObjAsPersistDB().getId(),
						sigaaForm.getCommandClass());
			} finally {
				dao.close();
			}

			ComponenteCurricularMov mov = new ComponenteCurricularMov();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR);

			try {
				execute(mov, req);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens().getMensagens(), req);
				req.setAttribute(mapping.getName(), form);
				return edit(mapping, form, req, res);
			}

			addMessage(req, "Disciplina removida com sucesso!",
					TipoMensagemUFRN.INFORMATION);

			// Limpar formulário, caso ele esteja em sessão
			req.getSession(false).removeAttribute(mapping.getName());

			return list(mapping, form, req, res);
		} else {
			req.setAttribute("remove", true);
			prepareMovimento(SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR, req);
			return edit(mapping, form, req, res);
		}

	}
}
