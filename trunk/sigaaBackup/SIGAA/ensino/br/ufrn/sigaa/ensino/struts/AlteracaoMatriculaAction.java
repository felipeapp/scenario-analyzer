/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.struts;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.form.AlteracaoMatriculaForm;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * @author eric
 *
 */
public class AlteracaoMatriculaAction extends AbstractCrudAction {

	private final String MATRICULAS = "matriculas";
	private final String CONFIRMACAO = "confirmacao";
	private final String DISCENTES = "discentes";

	public static final String TRANCAMENTO = "Trancar Matrícula de Turma";
	public static final String CANCELAMENTO = "Cancelar Matrícula de Turma";

	public ActionForward carregarMatriculas(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, req);
		int idDiscente = RequestUtils.getIntParameter(req, "id");
		Discente discente = dao.findByPrimaryKey(idDiscente,Discente.class);
		req.setAttribute("discente",discente);
		Collection<MatriculaComponente> matriculados;
		try {
			matriculados = dao.findMatriculadasByDiscente(discente);
		} finally {
			dao.close();
		}
		req.setAttribute("listaMatriculados",matriculados);
		if (matriculados == null) {
			addMensagem(new MensagemAviso("Esse aluno não possui matrícula em nenhuma turma no momento.",
					TipoMensagemUFRN.INFORMATION), req);
		}
		return mapping.findForward(MATRICULAS);
	}

	public ActionForward confirmaDados(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AlteracaoMatriculaForm alteracaoMatriculaForm = (AlteracaoMatriculaForm) form;
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, req);

		int idMatriculaDisciplina = RequestUtils.getIntParameter(req, "id");
		MatriculaComponente matriculaDisciplina = dao.findByPrimaryKey(idMatriculaDisciplina,MatriculaComponente.class);

		alteracaoMatriculaForm.getObj().setMatricula(matriculaDisciplina);
		dao.close();
		return mapping.findForward(CONFIRMACAO);
	}

	@Override
	public ActionForward persist(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AlteracaoMatriculaForm aMForm = (AlteracaoMatriculaForm) form;

		getForm(form).validate(req);

		if (flushErros(req)) {
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}
		Object tipo = getFromSession("tipoAlteracao", req);
		if (TRANCAMENTO.equals(tipo)) {
			prepareMovimento(SigaaListaComando.TRANCAR_MAT_DISCIPLINA, req);
		} else if (CANCELAMENTO.equals(tipo)) {
			prepareMovimento(SigaaListaComando.CANCELAR_MAT_DISCIPLINA, req);
		}
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(aMForm.getObj().getMatricula());
		mov.setCodMovimento(getUltimoComando(req));
		try {
			execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}
		getForm(form).clear();

		if (SigaaListaComando.TRANCAR_MAT_DISCIPLINA.equals(getUltimoComando(req))) {
			addMessage(req, "Matrícula trancada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
		} else if (SigaaListaComando.CANCELAR_MAT_DISCIPLINA.equals(getUltimoComando(req))) {
			addMessage(req, "Matrícula cancelada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
		}
		return discentes(mapping, aMForm, req, res);
	}

	public ActionForward discentes(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		String tipo = req.getParameter("tipo");
		if (tipo != null & "trancar".equalsIgnoreCase(tipo)) {
			addSession("tipoAlteracao", TRANCAMENTO, req);
		} else if (tipo != null & "cancelar".equalsIgnoreCase(tipo)) {
			addSession("tipoAlteracao", CANCELAMENTO, req);
		}

		getForm(form).checkRole(req);
		getForm(form).clear();
		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, getForm(form).customSearch(req));

		return mapping.findForward(DISCENTES);
	}

	@Override
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		return discentes(mapping, form, req, res);
	}

}