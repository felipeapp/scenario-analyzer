/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 26/09/2006
 *
 */
package br.ufrn.sigaa.ensino.struts;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractWizardAction;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.form.MatriculaTecnicoForm;
import br.ufrn.sigaa.ensino.negocio.dominio.MatriculaMov;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Action para cadastro de matr�culas
 *
 * @author David Ricardo
 *
 */
public class CadastroMatriculaAction extends AbstractWizardAction {

	public static final String INDEX = "index";
	
	public static final String TIPO_MATRICULA = "tipo_matricula";

	public static final String SELECIONA_DISCIPLINA = "seleciona_disciplinas";

	public static final String CONFIRMAR_DADOS = "confirmar";

	public ActionForward index(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		req.getSession().removeAttribute("matriculaTecnicoForm");

		// limpa os passos armazenados em sess�o
		clearSteps(req);

		// Adiciona passos do caso de uso
		addStep(req, "Selecionar", "/ensino/matriculaDisciplina", TIPO_MATRICULA);
		addStep(req, "Selecionar Aluno ou Turma", "/ensino/matriculaDisciplina", TIPO_MATRICULA);
		addStep(req, "Escolher Disciplinas", "/ensino/matriculaDisciplina", SELECIONA_DISCIPLINA);
		addStep(req, "Confirmar Dados", "/ensino/matriculaDisciplina", CONFIRMAR_DADOS);

		// informa o passo atual
		setStep(req, 1);

		prepareMovimento(SigaaListaComando.MATRICULAR_TECNICO, req);
		
		return mapping.findForward(INDEX);
	}
	
	/**
	 * Inicia o processo de matr�cula indo para o passo de escolha de um aluno ou
	 * uma turma de entrada.
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward escolheTipo(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		

		return mapping.findForward(req.getParameter("tipo"));
	}

	/**
	 * Ap�s a escolha do aluno ou turma de entrada, passa para o passo
	 * de escolha das disciplinas que ir�o fazer parte da matr�cula.
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward selecionaDisciplina(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		MatriculaTecnicoForm mForm = (MatriculaTecnicoForm) form;
		ModuloDao dao = new ModuloDao();
		MatriculaComponenteDao mDao = new MatriculaComponenteDao();

		try {
			Curso curso = null;

			if ("aluno".equals(mForm.getType())) { // Matr�cula de um aluno
				Discente discente = dao.findByPrimaryKey(mForm.getId(), Discente.class);
				if (SigaaSubsistemas.TECNICO.equals(getSubSistemaCorrente(req)) ||
						SigaaSubsistemas.MEDIO.equals(getSubSistemaCorrente(req))) {
					DiscenteTecnico tec = dao.findByPrimaryKey(discente.getId(), DiscenteTecnico.class);
					curso = tec.getCurso();
				}

				// Busca turmas em que o aluno j� se matriculou
				if (!"removerDisciplina".equals(req.getParameter("dispatch")) && !"adicionarDisciplina".equals(req.getParameter("dispatch"))) {
					Collection<Turma> turmas = mDao.findTurmasMatriculadasByDiscente(discente);
					if (turmas != null && !turmas.isEmpty())
						mForm.getTurmas().addAll(turmas);
					initHorarios(mForm.getTurmas());
				}

				mForm.setDiscente(discente);
				mForm.getTurma().setId(0);

			} else { // Matr�cula de toda uma turma de entrada
				TurmaEntradaTecnico turma = dao.findByPrimaryKey(mForm.getId(), TurmaEntradaTecnico.class);
				turma.getDiscentesTecnicos().iterator();

				if (turma.getDiscentesTecnicos().isEmpty()) {
					addMensagemErro("A turma selecionada n�o tem nenhum aluno cadastrado", req);
					return escolheTipo(mapping, form, req, res);
				}

				curso = turma.getCursoTecnico();

				mForm.getDiscente().setId(0);
				mForm.setTurma(turma);
			}

			// Popula todos os m�dulos do curso do aluno ou turma de entrada
			if (curso != null)
				addSession("modulos", dao.findByCursoTecnico(curso.getId()), req);

		} finally {
			dao.close();
			mDao.close();
		}

		setStep(req, 2);

		return mapping.findForward(SELECIONA_DISCIPLINA);
	}

	private void initHorarios(Collection<Turma> turmas) {
		if (turmas != null && !turmas.isEmpty()) {
			for (Turma turma : turmas) {
				turma.getHorarios().iterator();
			}
		}
	}

	/**
	 * Adiciona uma disciplina ou todas as disciplinas de um m�dulo � matr�cula
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward adicionarDisciplina(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		MatriculaTecnicoForm mForm = (MatriculaTecnicoForm) form;
		ModuloDao mDao = new ModuloDao();
		TurmaDao tDao = new TurmaDao();

		try {

			if (mForm.isDisciplina()) { // Apenas uma disciplina
				Turma turma = tDao.findByPrimaryKey(mForm.getObj().getTurma().getId(), Turma.class);
				if (!mForm.getTurmas().contains(turma)) {
					turma.getHorarios().iterator();
					mForm.getTurmas().add(turma);
				} else {
					addMensagemErro("A disciplina selecionada j� foi inserida na matr�cula.", req);
				}
			} else { // Todas as disciplinas de um m�dulo
				Collection<Turma> turmas = mDao.findTurmasAtivasByModulo(mForm.getModulo().getId());
				if (turmas == null || turmas.isEmpty()) {
					addMensagemErro("N�o existem turmas abertas para as disciplinas do m�dulo selecionado.", req);
				} else {
					if (mForm.getTurmas().containsAll(turmas)) {
						addMensagemErro("As disciplinas com turmas abertas j� foram inseridas na matr�cula.", req);
					}
					
					for (Turma turma : turmas) {
						if (!mForm.getTurmas().contains(turma)) {
							mForm.getTurmas().add(turma);
						}
					}
					
					initHorarios(mForm.getTurmas());
				}
			}
		} finally {
			mDao.close();
			tDao.close();
		}

		return selecionaDisciplina(mapping, form, req, res);
	}

	/**
	 * Remove uma disciplina da matr�cula
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward removerDisciplina(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		MatriculaTecnicoForm mForm = (MatriculaTecnicoForm) form;
		int index = RequestUtils.getIntParameter(req, "index");
		try {
			mForm.getTurmas().remove(index);
		} catch (IndexOutOfBoundsException e) { }

		return selecionaDisciplina(mapping, form, req, res);
	}

	/**
	 * Passo de confirma��o dos dados
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward confirmarDados(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		MatriculaTecnicoForm mForm = (MatriculaTecnicoForm) form;
		mForm.validateDisciplinas(req);
		if (flushErros(req)) {
			return selecionaDisciplina(mapping, form, req, res);
		}

		setStep(req, 3);

		return mapping.findForward(CONFIRMAR_DADOS);
	}


	/**
	 * Finaliza a matr�cula. E volta ao primeiro passo (escolha de aluno ou turma).
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward finalizar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		MatriculaTecnicoForm mForm = (MatriculaTecnicoForm) form;

		MatriculaMov mov = new MatriculaMov();
		mov.setTurmas(mForm.getTurmas());
		//mov.setDiscente(mForm.getDiscente());
		//mov.setTurmaEntrada(mForm.getTurma());
		mov.setCodMovimento(SigaaListaComando.MATRICULAR_TECNICO);

		try {
			execute(mov, req);
		} catch(NegocioException e) {
			addMensagemErro(e.getMessage(), req);
			return mapping.findForward(CONFIRMAR_DADOS);
		}

		addMensagem(new MensagemAviso("Matr�cula realizada com sucesso.", TipoMensagemUFRN.INFORMATION), req);
		req.getSession().removeAttribute("matriculaTecnicoForm");
		return escolheTipo(mapping, form, req, res);
	}

}