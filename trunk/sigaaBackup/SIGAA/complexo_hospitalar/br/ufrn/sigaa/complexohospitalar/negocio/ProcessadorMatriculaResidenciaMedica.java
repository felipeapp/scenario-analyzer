package br.ufrn.sigaa.complexohospitalar.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.MatriculaMov;
import br.ufrn.sigaa.ensino.tecnico.negocio.MatriculaTecnicoValidator;

/**
 * Processador específico para matrícula do nível de residência médica
 * 
 * @author bernardo
 *
 */
public class ProcessadorMatriculaResidenciaMedica extends AbstractProcessador {

		@Override
		public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
			//Somente processar movimento SigaaListaComando.MATRICULAR_ALUNOS_RESIDENCIA_EM_LOTE
			if(!mov.getCodMovimento().equals(SigaaListaComando.MATRICULAR_ALUNOS_RESIDENCIA_EM_LOTE)) {
				return null;
			}

			validate(mov);

			MatriculaMov mMov = (MatriculaMov) mov;
			DiscenteDao dao = getDAO(DiscenteDao.class, mMov);
			TurmaDao turmaDao = getDAO(TurmaDao.class, mov);
			AvaliacaoDao avaDao = getDAO(AvaliacaoDao.class, mov);

			try {

				Collection<? extends DiscenteAdapter> discentes = mMov.getDiscentes();
				Collection<Turma> turmasMatriculadas = mMov.getTurmas();

				for (DiscenteAdapter d : discentes) {
					d = dao.findByPK(d.getId());

					MatriculaComponente matricula = null;
					Collection<Turma> turmas = dao.findTurmasMatriculadas(d.getId());
					
					if(turmasMatriculadas == null){
						turmasMatriculadas = new ArrayList<Turma>();
					}

					for (Turma turma : turmasMatriculadas) {
						if (!turmas.contains(turma)) {
							matricula = new MatriculaComponente();
							matricula.setDiscente(d.getDiscente());
							matricula.setTurma(turma);
							matricula.setNotas(null);
							matricula.setRecuperacao(null);
							matricula.setDataCadastro(new Date());
							matricula.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
							if (turma.getAno() > 0)
								matricula.setAno( (short) turma.getAno());
							else
								matricula.setAno( (short) 0 );
							if (turma.getPeriodo() > 0)
								matricula.setPeriodo((byte) turma.getPeriodo());
							else
								matricula.setPeriodo((byte) 0);
							matricula.setComponente(turma.getDisciplina());
							matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());

							matricula.setSituacaoMatricula(SituacaoMatricula.MATRICULADO);
							
							dao.create(matricula); // Cadastra aluno em turma
						}
					}
					// se tiver sido matriculado em alguma coisa,
					if (turmasMatriculadas != null && !turmasMatriculadas.isEmpty() && d.getStatus() != StatusDiscente.ATIVO) {
						dao.updateDiscente(d.getId(), "status", StatusDiscente.ATIVO);
					}
				}
			} finally {
				dao.close();
				turmaDao.close();
				avaDao.close();
			}

			return mMov;
		}

		@Override
		public void validate(Movimento mov) throws NegocioException, ArqException {
			MatriculaMov mMov = (MatriculaMov) mov;
			if (mMov.getTurmas() == null || mMov.getTurmas().isEmpty())
				throw new NegocioException("É necessário escolher ao menos uma turma.");

			if (mMov.getDiscentes() == null || mMov.getDiscentes().isEmpty())
				throw new NegocioException("É necessário escolher ao menos um discente.");

			DiscenteDao dDao = getDAO(DiscenteDao.class, mMov);
			MatriculaComponenteDao matDao = getDAO(MatriculaComponenteDao.class, mMov);
			TurmaDao tDao = getDAO(TurmaDao.class, mMov);

			Collection<? extends DiscenteAdapter> discentes = mMov.getDiscentes();
			Collection<Turma> turmas = mMov.getTurmas();

			try {
				boolean erro = false, excedeuVaga = false;
				StringBuilder sb = new StringBuilder();
				ListaMensagens erros = new ListaMensagens();

				for (DiscenteAdapter d : discentes) {
					// Componentes pagos OU aproveitados pelo aluno
					Collection<ComponenteCurricular> componentesConcluidos  = dDao.findComponentesCurricularesConcluidos(d);

					// Turmas já matriculadas no semestre
					Collection<Turma> turmasMatriculadasSemestre = dDao.findTurmasMatriculadas(d.getId());
					
					// coleção de turmas do semestre (as que já se matriculou e as que está tentando agora)
					Collection<Turma> turmasSemestre = dDao.findTurmasMatriculadas(d.getId());
					Collection<ComponenteCurricular> todosComponentes = componentesConcluidos != null ? new TreeSet<ComponenteCurricular>(componentesConcluidos) : new TreeSet<ComponenteCurricular>();
					for (Turma turma : turmas) {
						if (turma.isMatricular()){
							todosComponentes.add(turma.getDisciplina());
							turmasSemestre.add(turma);
						}
					}

					for (Turma t : turmas) {
						if (t.isMatricular()) {
							// valida capacidade (somente para alunos de cursos presenciais)
							int matriculados = tDao.findTotalMatriculados(t.getId());
							
							if (d.getCurso().isPresencial() && matriculados + discentes.size() > t.getCapacidadeAluno() && !excedeuVaga) {
								String mensagemExcedeuVagas = "A turma "+ t.getCodigo() +" da disciplina " + t.getDisciplina().getNome()+ 
										" possui capacidade de "+ t.getCapacidadeAluno() +" vaga(s) ";
								if(matriculados > 0)
									mensagemExcedeuVagas += "e " + matriculados + " aluno(s) matriculado(s). ";
								
								mensagemExcedeuVagas += discentes.size() > 1 ? "Não é possível realizar a(s) " + discentes.size() + " matrículas desejadas.<br/>" : "Não é possível realizar a matrícula escolhida.<br/>";
								
								sb.append(mensagemExcedeuVagas);
								
								erro = true;
								excedeuVaga = true;
							}

							// Verificar pré-requisitos
							if (t.getDisciplina().getPreRequisito() != null &&
									!ExpressaoUtil.eval(t.getDisciplina().getPreRequisito(), componentesConcluidos)) {
								sb.append("O Aluno " + d.getPessoa().getNome() + " não pode cursar a disciplina " + t.getDisciplina().getNome() +
										" pois não possui os pré-requisitos necessários.<br/>");
								erro = true;
							}
							
							Collection<ComponenteCurricular> componentesMatriculados = new ArrayList<ComponenteCurricular>();
							
							if(isNotEmpty(turmasMatriculadasSemestre)) {
								for (Turma tMat : turmasMatriculadasSemestre) {
									componentesMatriculados.add(tMat.getDisciplina());
								}
							}

							// Verificar co-requisitos
							if (t.getDisciplina().getCoRequisito() != null && !ExpressaoUtil.eval(t.getDisciplina().getCoRequisito(), todosComponentes) && !ExpressaoUtil.eval(t.getDisciplina().getCoRequisito(), componentesMatriculados)) {
								sb.append("O Aluno " + d.getPessoa().getNome() + " não pode cursar a disciplina " + t.getDisciplina().getNome() +
										" pois não possui os co-requisitos necessários.<br/>");
								erro = true;
							}

							// Verificar se o aluno já cursou a disciplina
							// ou se o aluno está se matriculando em mais turmas do mesmo componente do que o permitido
							Collection<MatriculaComponente> turmasPagas = matDao.findByDiscenteEDisciplina(d, t.getDisciplina(), SituacaoMatricula.getSituacoesPagasArray());
							int qtdTurmasPagas = (turmasPagas == null? 0 : turmasPagas.size()); 
							int qtdTurmasPretendidas = getNumeroRepetidas(t.getDisciplina(), turmas);
							int qtdTurmasMatriculadas = getNumeroRepetidas(t.getDisciplina(), turmasMatriculadasSemestre);
							if (qtdTurmasPagas >= t.getDisciplina().getQtdMaximaMatriculas()) {
								erro = true;
								sb.append("O Aluno " + d.getPessoa().getNome() + " já cursou a disciplina " + t.getDisciplina().getNome() + "<br/>");
							} else if( qtdTurmasPagas + qtdTurmasMatriculadas + qtdTurmasPretendidas > t.getDisciplina().getQtdMaximaMatriculas() ){
								erro = true;
								sb.append("Não é possível se matricular mais de "+t.getDisciplina().getQtdMaximaMatriculas()+" vez(es) na disciplina "+ t.getDisciplina().getNome() + "<br/>");
							}

							// Verificar se o aluno já está matriculado na disciplina
							for (Turma ts : turmasMatriculadasSemestre) {
								if (ts.getId() == t.getId()){
									erro = true;
									sb.append("O Aluno " + d.getPessoa().getNome() + " já está matriculado na turma "+ t.getCodigo() +" da disciplina "+ t.getDisciplina().getNome() +". <br/>");
									break;
								}
								if (ts.getDisciplina().equals(t.getDisciplina()) && qtdTurmasMatriculadas + qtdTurmasPretendidas > t.getDisciplina().getQtdMaximaMatriculas()) {
									erro = true;
									sb.append("O Aluno " + d.getPessoa().getNome() + " já está matriculado na disciplina " + t.getDisciplina().getNome() + "<br/>");
									break;
								}
							}
							
							// validar choque de horário entre as turmas do semestre (EXCETO para alunos do lato sensu E alunos de cursos modalidade a distância)
							if( d.getCurso().isPresencial() ){
								t = tDao.findAndFetch(t.getId(), Turma.class, "horarios");
								MatriculaGraduacaoValidator.validarChoqueHorarios(t, turmasSemestre, erros);
							}
						}
					}
					
					MatriculaTecnicoValidator.validarReprovacoes(d.getDiscente(), erros);
				}
				
				if(!erros.isEmpty()){
					erro = true;
					for(MensagemAviso m: erros.getMensagens())
						sb.append(m.getMensagem());
				}

				if (erro) {
					throw new NegocioException("Os seguintes erros aconteceram: <br/>" + sb.toString());
				}

			} finally {
				matDao.close();
				dDao.close();
				tDao.close();
			}

		}

		/**
		 * Retorna a quantidade de turmas da mesma disciplina passadas no movimento
		 * @param disciplina
		 * @param turmas
		 * @return
		 */
		private int getNumeroRepetidas(ComponenteCurricular disciplina, Collection<Turma> turmas) {
			int qtd = 0;
			for(Turma t: turmas){
				if(t.getDisciplina().equals(disciplina))
					qtd++;
			}
			return qtd;
		}
}
