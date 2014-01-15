package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMatriculaGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorMatriculaGraduacao;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** Matricula um discente em um plano de matrículas.
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorMatriculaDiscentePlanoMatricula extends AbstractProcessador {

	/** Matricula um discente em um plano de matrículas.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		DiscenteGraduacao discente = ((MovimentoCadastro) mov).getObjMovimentado();
		try {
			if (discente.getStatus() == StatusDiscente.CADASTRADO) {
				if (isEmpty(discente.getMatricula())) {
					ProcessadorDiscente procDiscente = new ProcessadorDiscente();
					procDiscente.gerarMatricula(discente, dao);
					dao.updateDiscente(discente.getId(), "matricula", discente.getMatricula());
				}
				matriculaTurmas(mov, discente, dao);
				// verifica se o discente passou para ativo
				Discente d = dao.findByPrimaryKey(discente.getId(), Discente.class);
				// se ativo, cancela os vínculos anteriores.
				if (d.getStatus() == StatusDiscente.ATIVO)
					cancelaVinculosAnteriores(dao, discente, mov);
			}
		} finally {
			dao.close();
		}
		return discente;
	}

	/** Cancela os outros vínculos ativos que o discente possa ter. O vínculo será cancelado se o discente cadastrado estiver com status ATIVO
	 * @param dao
	 * @param discente
	 * @param movimento
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException 
	 */
	public void cancelaVinculosAnteriores(DiscenteDao dao, DiscenteGraduacao discente, Movimento movimento) throws NegocioException, ArqException, RemoteException {
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discente); 
		int[] tiposValidos = {Discente.REGULAR};
		int[] statusValidos = { StatusDiscente.ATIVO, StatusDiscente.FORMANDO,
				StatusDiscente.TRANCADO,  StatusDiscente.ATIVO_DEPENDENCIA };
		@SuppressWarnings("unchecked")
		Collection<DiscenteGraduacao> discentes = (Collection<DiscenteGraduacao>) dao.findOtimizado(discente.getPessoa().getCpf_cnpj(), null, null, null, null, null, statusValidos, tiposValidos, 0, NivelEnsino.GRADUACAO, false);
		if (!isEmpty(discentes)) {
			// remove do resultado o vínculo atual
			Iterator<? extends DiscenteAdapter> iterator = discentes.iterator();
			while (iterator.hasNext())
				if (iterator.next().getId() == discente.getId())
					iterator.remove();
			if (!isEmpty(discentes)) {
				for (DiscenteGraduacao vinculoAnterior : discentes) {
					MovimentacaoAluno obj = new MovimentacaoAluno();
					obj.setAnoOcorrencia(cal.getAno());
					obj.setPeriodoOcorrencia(cal.getPeriodo());
					obj.setAnoReferencia(cal.getAno());
					obj.setPeriodoReferencia(cal.getPeriodo());
					obj.setDiscente(vinculoAnterior);
					obj.setTipoMovimentacaoAluno(new TipoMovimentacaoAluno(TipoMovimentacaoAluno.CANCELAMENTO_POR_EFETIVACAO_NOVO_CADASTRO));
					MovimentoMovimentacaoAluno mov = new MovimentoMovimentacaoAluno();
					mov.setObjMovimentado(obj);
					mov.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
					mov.setUsuarioLogado(movimento.getUsuarioLogado());
					mov.setSistema(movimento.getSistema());
					ProcessadorMovimentacaoAluno proc = new ProcessadorMovimentacaoAluno();
					proc.execute(mov);
				}
			}
		}
	}

	/** Caso o usuário tenha selecionado um Plano de Matrícula de Ingressantes, o discente será matriculado nas turmas do plano.
	 * @param mov
	 * @param discente
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void matriculaTurmas(Movimento mov, DiscenteGraduacao discente, GenericDAO dao) throws DAOException, NegocioException, ArqException, RemoteException {
		PlanoMatriculaIngressantes planoMatricula = (PlanoMatriculaIngressantes) ((MovimentoCadastro) mov).getObjAuxiliar();
		if(!isEmpty(planoMatricula)) {
			try {
				planoMatricula = dao.findByPrimaryKeyLock(planoMatricula.getId(), PlanoMatriculaIngressantes.class);
				if (planoMatricula != null && planoMatricula.hasVagas()) {
					CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(discente);
					Collection<Turma> turmas = new LinkedList<Turma>();
					for (Turma turma : planoMatricula.getTurmas()) {
						turma.setMatricular(true);
						turmas.add(turma);
					}
					MovimentoMatriculaGraduacao movimento = new MovimentoMatriculaGraduacao();
					movimento.setDiscente(discente);
					movimento.setTurmas(turmas);
					movimento.setCalendarioAcademicoAtual(calendario);
					movimento.setMatriculaEAD(discente.isEAD());
					movimento.setMatriculaFerias(false);
					movimento.setRestricoes(null);
					movimento.setCodMovimento(SigaaListaComando.MATRICULAR_GRADUACAO);
					movimento.setUsuarioLogado(mov.getUsuarioLogado());
					movimento.setAtualizarStatusDiscenteETiposIntegralizacao(true);
					movimento.setSistema(mov.getSistema());
					movimento.setSituacao(SituacaoMatricula.MATRICULADO);
					movimento.setMatriculaIngressante(true);
					ProcessadorMatriculaGraduacao procMatricula = new ProcessadorMatriculaGraduacao();
					procMatricula.execute(movimento);
					// verifica se o discente já foi atendido anteriormente por este plano. 
					// É necesário porque o discente pode ter sido estornado e cadastrado novamente
					if (!planoMatricula.getDiscentesAtendidos().contains(discente.getDiscente())) {
						planoMatricula.addDiscente(discente.getDiscente());
						dao.update(planoMatricula);
					}
				}
			} finally {
				dao.close();
			}
		}
	}

	/** Valida os dados antes de executar.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
