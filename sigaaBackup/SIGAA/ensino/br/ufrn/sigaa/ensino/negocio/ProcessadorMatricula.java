/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 26/09/2006
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.MatriculaMov;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.tecnico.negocio.MatriculaTecnicoValidator;
import br.ufrn.sigaa.ensino.util.TurmaUtil;

/**
 * Processador para cadastrar matr�culas de alunos em turmas.
 *
 * @author David Ricardo
 *
 */
public class ProcessadorMatricula extends ProcessadorCadastro {
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

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

						if (NivelEnsino.isEnsinoBasico(d.getNivel()) || d.getNivel() == NivelEnsino.LATO) {
							matricula.setSituacaoMatricula(SituacaoMatricula.MATRICULADO);
							// incrementa total matriculados
//							dao.updateField(Turma.class, turma.getId(), "totalMatriculados", turma.getTotalMatriculados() + 1);
						} else {
							matricula.setSituacaoMatricula(SituacaoMatricula.EM_ESPERA);
						}
						dao.create(matricula); // Cadastra aluno em turma
						
						if (NivelEnsino.isEnsinoBasico(d.getNivel()) || d.getNivel() == NivelEnsino.LATO) {
							
							// Carrega a turma, para pegar os n�meros da unidade
							matricula.setTurma(dao.findByPrimaryKey(turma.getId(), Turma.class));
							if (matricula.getTurma().getDisciplina().getPrograma() != null)
								matricula.getTurma().getDisciplina().getPrograma().getNumUnidades();
							
							MatriculaComponenteHelper.cadastrarAvaliacoesMatriculaCompulsoria(matricula, TurmaUtil.getNumUnidadesDisciplina(matricula.getTurma()), turmaDao, avaDao);
						}
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
			throw new NegocioException("� necess�rio escolher ao menos uma turma.");

		if (mMov.getDiscentes() == null || mMov.getDiscentes().isEmpty())
			throw new NegocioException("� necess�rio escolher ao menos um discente.");

		DiscenteDao dDao = getDAO(DiscenteDao.class, mMov);
		MatriculaComponenteDao matDao = getDAO(MatriculaComponenteDao.class, mMov);
		TurmaDao tDao = getDAO(TurmaDao.class, mMov);

		Collection<? extends DiscenteAdapter> discentes = mMov.getDiscentes();
		Collection<Turma> turmas = mMov.getTurmas();

		try {
			boolean erro = false;
			StringBuilder sb = new StringBuilder();
			ListaMensagens erros = new ListaMensagens();

			for (DiscenteAdapter d : discentes) {
				
				d = dDao.findByPK(d.getId());
				
				// Componentes pagos OU aproveitados pelo aluno
				Collection<ComponenteCurricular> componentesConcluidos  = dDao.findComponentesCurricularesConcluidos(d);

				// Turmas j� matriculadas no semestre
				Collection<Turma> turmasMatriculadasSemestre = dDao.findTurmasMatriculadas(d.getId());
				
				// cole��o de turmas do semestre (as que j� se matriculou e as que est� tentando agora)
				Collection<Turma> turmasSemestre = dDao.findTurmasMatriculadas(d.getId());
				Collection<ComponenteCurricular> todosComponentes = componentesConcluidos != null ? new TreeSet<ComponenteCurricular>(componentesConcluidos) : new TreeSet<ComponenteCurricular>();
				for (Turma turma : turmas) {
					if (turma.isMatricular()){
						todosComponentes.add(turma.getDisciplina());
						turmasSemestre.add(turma);
					}
				}

				for (Turma t : turmas) {
					
					// valida capacidade (somente para alunos de cursos presenciais)
					if (d.getCurso().isPresencial() && tDao.findTotalMatriculados(t.getId()) + discentes.size() > t.getCapacidadeAluno()) {
						sb.append("A turma "+ t.getCodigo() +" da disciplina " + t.getDisciplina().getNome()+ " excedeu a sua capacidade de "+ t.getCapacidadeAluno() +" vaga(s). N�o � poss�vel realizar sua matr�cula.<br/>");
						erro = true;
					}
					
					if (t.isMatricular()) {

						// caso seja aluno de tecnico, � testado se a turma corresponde � especialidade da turma de entrada
						if (d.getNivel() == NivelEnsino.TECNICO && t.getEspecializacao() != null) {
							DiscenteTecnico dt = (DiscenteTecnico) d;

							if (dt.getTurmaEntradaTecnico() == null) {
								sb.append("O Aluno " + d.getPessoa().getNome() + " est� sem turma de entrada definida. N�o � poss�vel realizar sua matr�cula.<br/>");
								erro = true;
							} else if (dt.getTurmaEntradaTecnico().getEspecializacao() == null) {
								sb.append("O Aluno " + d.getPessoa().getNome() + " n�o pode cursar a disciplina " + t.getDisciplina().getNome() +
								" pois essa turma � reservada somente para alunos da(s) " +
								"especialidade(s) " +t.getEspecializacao().getDescricao()+  ".<br/>");
								erro = true;
							} else if(dt.getTurmaEntradaTecnico().getEspecializacao() != null){
								t = tDao.findByPrimaryKey(t.getId(), Turma.class);
								if(t.getEspecializacao() != null && t.getEspecializacao().getEspecializacoes() != null && t.getEspecializacao().getEspecializacoes().size() > 0){
									boolean achou = false;
									for(EspecializacaoTurmaEntrada e: t.getEspecializacao().getEspecializacoes()){
										if(e.getId() == dt.getTurmaEntradaTecnico().getEspecializacao().getId())
											achou = true;
									}
									if(!achou){
										sb.append("O Aluno " + d.getPessoa().getNome() + " n�o pode cursar a disciplina " + t.getDisciplina().getNome() +
										" pois essa turma � reservada somente para alunos da(s) " +
										"especialidade(s) " +t.getEspecializacao().getDescricao()+  ".<br/>");
										erro = true;
									}
								}else if(dt.getTurmaEntradaTecnico().getEspecializacao().getId() != t.getEspecializacao().getId()){
									sb.append("O Aluno " + d.getPessoa().getNome() + " n�o pode cursar a disciplina " + t.getDisciplina().getNome() +
									" pois essa turma � reservada somente para alunos da " +
									"especialidade " +t.getEspecializacao().getDescricao()+  ".<br/>");
									erro = true;
								}
							}
						}

						// Verificar pr�-requisitos
						if (t.getDisciplina().getPreRequisito() != null &&
								!ExpressaoUtil.eval(t.getDisciplina().getPreRequisito(), componentesConcluidos)) {
							sb.append("O Aluno " + d.getPessoa().getNome() + " n�o pode cursar a disciplina " + t.getDisciplina().getNome() +
									" pois n�o possui os pr�-requisitos necess�rios.<br/>");
							erro = true;
						}

						// Verificar co-requisitos
						if (t.getDisciplina().getCoRequisito() != null && !ExpressaoUtil.eval(t.getDisciplina().getCoRequisito(), todosComponentes)) {
							sb.append("O Aluno " + d.getPessoa().getNome() + " n�o pode cursar a disciplina " + t.getDisciplina().getNome() +
									" pois n�o possui os co-requisitos necess�rios.<br/>");
							erro = true;
						}

						// Verificar se o aluno j� cursou a disciplina
						// ou se o aluno est� se matriculando em mais turmas do mesmo componente do que o permitido
						Collection<MatriculaComponente> turmasPagas = matDao.findByDiscenteEDisciplina(d, t.getDisciplina(), SituacaoMatricula.getSituacoesPagasArray());
						int qtdTurmasPagas = (turmasPagas == null? 0 : turmasPagas.size()); 
						int qtdTurmasPretendidas = getNumeroRepetidas(t.getDisciplina(), turmas);
						int qtdTurmasMatriculadas = getNumeroRepetidas(t.getDisciplina(), turmasMatriculadasSemestre);
						if (qtdTurmasPagas >= t.getDisciplina().getQtdMaximaMatriculas()) {
							erro = true;
							sb.append("O Aluno " + d.getPessoa().getNome() + " j� cursou a disciplina " + t.getDisciplina().getNome() + "<br/>");
						} else if( qtdTurmasPagas + qtdTurmasMatriculadas + qtdTurmasPretendidas > t.getDisciplina().getQtdMaximaMatriculas() ){
							erro = true;
							sb.append("N�o � poss�vel se matricular mais de "+t.getDisciplina().getQtdMaximaMatriculas()+" vez(es) na disciplina "+ t.getDisciplina().getNome() + "<br/>");
						}

						// Verificar se o aluno j� est� matriculado na disciplina
						for (Turma ts : turmasMatriculadasSemestre) {
							if (ts.getId() == t.getId()){
								erro = true;
								sb.append("O Aluno " + d.getPessoa().getNome() + " j� est� matriculado na turma "+ t.getCodigo() +" da disciplina "+ t.getDisciplina().getNome() +". <br/>");
								break;
							}
							if (ts.getDisciplina().equals(t.getDisciplina()) && qtdTurmasMatriculadas + qtdTurmasPretendidas > t.getDisciplina().getQtdMaximaMatriculas()) {
								erro = true;
								sb.append("O Aluno " + d.getPessoa().getNome() + " j� est� matriculado na disciplina " + t.getDisciplina().getNome() + "<br/>");
								break;
							}
						}
						
						// validar choque de hor�rio entre as turmas do semestre (EXCETO para alunos do lato sensu E alunos de cursos modalidade a dist�ncia)
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
