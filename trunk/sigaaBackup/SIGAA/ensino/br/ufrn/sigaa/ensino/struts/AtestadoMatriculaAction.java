/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.struts.AbstractWizardAction;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Action que trata do caso de uso que exibe o atestado de matrícula de um aluno
 */
public class AtestadoMatriculaAction extends AbstractWizardAction {

	/** Constantes dos forwards */
	private static final String MENU_TECNICO = "menuTecnico";

	private static final String SELECAO = "selecao";

	private static final String ATESTADO = "atestado";

	/**
	 * Checa o usuário logado para ir direto ao atestado, caso este seja um
	 * aluno, ou a uma busca de aluno, caso o usuário logado não seja aluno.
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward checaUsuario(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		// int papeis[] =
		// {SigaaPapeis.ADMINISTRADOR_SIGAA,SigaaPapeis.GESTOR_TECNICO};
		getUsuarioLogado(req).getPapeis();
		if ( /* isUserInRole(req,papeis) */true) { 
			// TODO Ver essa questão da checagem do usuário logado
			addSession("forward", "Atestado", req);
			return mapping.findForward(SELECAO);
		} else if (((Usuario) getUsuarioLogado(req)).getDiscenteAtivo() != null) {
			return carregarAtestado(mapping, form, req, res);
		}
		return mapping.findForward(MENU_TECNICO);
	}

	/**
	 * Carrega no request os dados necessários para exibição do atestado de
	 * matrícula
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws ArqException
	 */
	public ActionForward carregarAtestado(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res) throws Exception {

		MatriculaComponenteDao matdao = getDAO(MatriculaComponenteDao.class, req);
		HorarioDao hordao = getDAO(HorarioDao.class, req);

		int id = RequestUtils.getIntParameter(req, "id");
		if (id == 0)
			id = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo().getId();

		try {
			Discente discente = hordao.findByPrimaryKey(id, Discente.class);
			if (discente.getCurso() == null) {
				addMensagemErro("Não foi possível abrir o atestado do disente escolhido, pois ele não possui um curso associado.", req);
				return mapping.findForward(SELECAO);
			}
			List<MatriculaComponente> matriculas = (List<MatriculaComponente>) matdao.findMatriculadasByDiscente(discente);
			req.setAttribute("matriculasAtestado", matriculas);
			if( discente.getNivel() != NivelEnsino.LATO )
				req.setAttribute("horarios", hordao.findByUnidadeOtimizado(getUnidadeGestora(req), discente.getNivel()));
			else
				req.setAttribute("horarios", hordao.findByUnidadeOtimizado(-1, discente.getNivel()));
			
			String nomeCurso = "";
			if (discente.getNivel() == NivelEnsino.TECNICO)
				nomeCurso = discente.getCurso().getDescricao();
			if (discente.getNivel() == NivelEnsino.LATO)
				nomeCurso = discente.getCurso().getDescricao();
			
			req.setAttribute("nomeCurso", nomeCurso);
			if (matriculas != null && !matriculas.isEmpty())
				req.setAttribute("horariosTurmas", hordao.findByMatriculas(matriculas));
			req.setAttribute("discente", discente);
		} finally {
			hordao.close();
			matdao.close();
		}
		return mapping.findForward(ATESTADO);
	}

}
