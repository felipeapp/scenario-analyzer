/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.struts;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.form.MovimentacaoAlunoForm;

public class MovimentacaoAlunoAction extends AbstractCrudAction {

	final String CADASTRAR_RETORNO_AFASTADOS = "form_afastados";

	public ActionForward dadosRetorno(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		GenericDAO dao = getGenericDAO(req);

		int id_afastamento =RequestUtils.getIntParameter(req,"id");

		int tipoLista = RequestUtils.getIntParameter(req,"tipoLista");

		MovimentacaoAluno alunoAfastado = dao.findByPrimaryKey(id_afastamento,MovimentacaoAluno.class);

		MovimentacaoAlunoForm afastamentoForm = (MovimentacaoAlunoForm) form;

		afastamentoForm.setObj(alunoAfastado);
		afastamentoForm.setTipoLista(tipoLista);

		prepareMovimento(SigaaListaComando.RETORNAR_ALUNO_AFASTADO, req);

		return mapping.findForward(CADASTRAR_RETORNO_AFASTADOS);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		int AFASTAMENTOS_SEM_RETORNO = 2;


		MovimentacaoAlunoDao dao = new MovimentacaoAlunoDao();

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
			lista = getForm(form).customSearch(req);

		int tipoLista = RequestUtils.getIntParameter(req, "tipoLista");
		boolean todos = true;
		if (tipoLista == AFASTAMENTOS_SEM_RETORNO)
			todos = false;

		try {
			if (lista == null){
				if( getNivelEnsino(req) != NivelEnsino.LATO )
					lista = dao.findByDiscenteOrTipoMovimentacao(
							0, 0, todos, getUnidadeGestora(req), getNivelEnsino(req), getPaging(req));
				else
					lista = dao.findByDiscenteOrTipoMovimentacao(
							0, 0, todos, 0, getNivelEnsino(req), getPaging(req));
			}
		} finally {
			dao.close();
		}
		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, lista);

		return mapping.findForward(LISTAR);
	}


	public ActionForward cadastrarRetorno(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		getForm(form).checkRole(req);
		getForm(form).validate(req);

		MovimentacaoAlunoForm aaForm = (MovimentacaoAlunoForm) form;
		MovimentacaoAluno obj = aaForm.getObj();
		GenericDAO dao = getGenericDAO(req);
		MovimentacaoAluno afast = null;
		try {
			afast = dao.findByPrimaryKey(obj.getId(), obj.getClass());
		} finally {
			dao.close();
		}
		if (flushErros(req)) {
			getForm(aaForm).setObj(afast);
			return mapping.findForward(CADASTRAR_RETORNO_AFASTADOS);
		}

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(afast);

		getForm(form).clear();
		removeSession(mapping.getName(), req);

		mov.setCodMovimento(getUltimoComando(req));
		try {
			execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward(CADASTRAR_RETORNO_AFASTADOS);
		}
		addMessage(req, "Retorno do Afastamento registrado com sucesso",
				TipoMensagemUFRN.INFORMATION);
		req.setAttribute("voltando", true);
		return list(mapping, form, req, res);
	}

	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ActionForward ret = super.edit(mapping, form, req, res);
		if (getForm(form).getObjAsPersistDB().getId() == 0) {
			prepareMovimento(SigaaListaComando.AFASTAR_ALUNO, req);
		} else {
			prepareMovimento(SigaaListaComando.ALTERAR_AFASTAMENTO_ALUNO, req);
		}
		return ret;
	}

	public ActionForward desativar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ActionForward ret = super.edit(mapping, form, req, res);
		prepareMovimento(SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO, req);
		return ret;
	}

	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		getForm(form).checkRole(req);

		if (!SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO.equals(getUltimoComando(req)))
			getForm(form).validate(req);

		if (flushErros(req)) {
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}
		PersistDB obj = getForm(form).getObjAsPersistDB();
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(getUltimoComando(req));
		try {
			execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}
		getForm(form).clear();

		if (SigaaListaComando.AFASTAR_ALUNO.equals(getUltimoComando(req))) {
			addMessage(req, "Afastamento cadastrado com sucesso!",
					TipoMensagemUFRN.INFORMATION);
			return edit(mapping, form, req, res);
		} else {
			if (SigaaListaComando.ALTERAR_AFASTAMENTO_ALUNO.equals(getUltimoComando(req)))
				addMessage(req, "Afastamento alterado com sucesso!",
						TipoMensagemUFRN.INFORMATION);
			else if (SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO.equals(getUltimoComando(req)))
				addMessage(req, "Afastamento desativado com sucesso!",
						TipoMensagemUFRN.INFORMATION);
			return list(mapping, form, req, res);
		}
	}

	@Override
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (getUltimoComando(req).equals(SigaaListaComando.ALTERAR_AFASTAMENTO_ALUNO) ||
				getUltimoComando(req).equals(SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO))
			return list(mapping, form, req, res);
		return super.cancelar(mapping, form, req, res);
	}

	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		return persist(mapping, form, req, res);
	}
}
