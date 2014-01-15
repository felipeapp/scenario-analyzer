/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 27/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dominio.MovimentoAcademico;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dao.CurriculoMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.DiscenteMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.DisciplinaMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaComponenteMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaComponenteDependencia;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaComponenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;

/**
 * Processador responsável pelas operações
 * de matrícula de Ensino Médio.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorMatriculaMedio extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoAcademico movimento = (MovimentoAcademico) mov;

		validate(movimento);

		if (SigaaListaComando.MATRICULAR_DISCENTE_MEDIO.equals(mov.getCodMovimento())) {
			matricular(movimento);
		} else if (SigaaListaComando.REMATRICULAR_DISCENTE_MEDIO.equals(mov.getCodMovimento())) {
			//rematricular(movimento);
		} else if (SigaaListaComando.MATRICULAR_DEPENDENCIA_MEDIO.equals(mov.getCodMovimento())) {
			matricularDependencias(movimento);
		}
		return null;
	}
	
	/**
	 * Método responsável por realizar a matrícula de um discente de ensino médio em uma série e turma, 
	 * assim como nas disciplinas pertencentes a série.
	 * 
	 * @param matriculaMov
	 * @return
	 * @throws ArqException
	 */
	private Object matricularDiscenteNaSerie(MovimentoAcademico matriculaMov) throws ArqException  {
		
		CurriculoMedioDao cDao = getDAO(CurriculoMedioDao.class, matriculaMov);
		MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class, matriculaMov);
		try {
			DiscenteMedio discente = matriculaMov.getObjMovimentado();
			TurmaSerie turmaSerie = (TurmaSerie) matriculaMov.getObjAuxiliar();
			
			MatriculaDiscenteSerie matriculaSerie = new MatriculaDiscenteSerie();
			if ( !mdsDao.discenteJaMatriculadoEmSerie(discente, turmaSerie) ) {
				
				matriculaSerie.setCurriculoMedio( cDao.findMaisRecenteByCursoOrSerie(
						turmaSerie.getSerie().getCursoMedio(), turmaSerie.getSerie()) );
				matriculaSerie.setDataCadastro(new Date());
				matriculaSerie.setDiscenteMedio(discente);
				matriculaSerie.setSituacaoMatriculaSerie(SituacaoMatriculaSerie.MATRICULADO);
				matriculaSerie.setTurmaSerie(turmaSerie);
				
				cDao.create(matriculaSerie);
			} 
			return matriculaSerie;
		
		} finally {
			if ( cDao != null ) 	cDao.close(); 
			if ( mdsDao != null ) 	mdsDao.close(); 
		}
		
	}
	
	/**
	 * Método responsável por realizar a matrícula de disciplinas de dependência de um discente 
	 * de ensino médio em uma série e turma, assim como, nas disciplinas pertencentes a série.
	 * 
	 * @param matriculaMov
	 * @return
	 * @throws ArqException
	 */
	private Object matricularDiscenteNaSerieDependencia(TurmaSerie turmaSerie, MovimentoAcademico matriculaMov) throws ArqException  {
		
		CurriculoMedioDao cDao = getDAO(CurriculoMedioDao.class, matriculaMov);
		MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class, matriculaMov);
		try {
			DiscenteMedio discente = matriculaMov.getObjMovimentado();
			
			MatriculaDiscenteSerie matriculaSerie = mdsDao.findMatriculaSerieAtivasByTurmaSerie(discente, turmaSerie);
			if ( ValidatorUtil.isEmpty(matriculaSerie) ) {
				matriculaSerie = new MatriculaDiscenteSerie();
				matriculaSerie.setCurriculoMedio( cDao.findMaisRecenteByCursoOrSerie(
						turmaSerie.getSerie().getCursoMedio(), turmaSerie.getSerie()) );
				matriculaSerie.setDataCadastro(new Date());
				matriculaSerie.setDiscenteMedio(discente);
				matriculaSerie.setDependencia(true);
				matriculaSerie.setSituacaoMatriculaSerie(SituacaoMatriculaSerie.MATRICULADO);
				matriculaSerie.setTurmaSerie(turmaSerie);
				
				cDao.create(matriculaSerie);
				
				//update statusDiscente para ATIVO_EM_DEPENDENCIA
				ParametrosGestoraAcademicaDao pdao = getDAO(ParametrosGestoraAcademicaDao.class, matriculaMov);
				try {
					DiscenteHelper.alterarStatusDiscente(discente, StatusDiscente.ATIVO_DEPENDENCIA, matriculaMov, pdao);
				} finally {
					pdao.close();
				}
			} 
			return matriculaSerie;
		
		} finally {
			if ( cDao != null ) 	cDao.close(); 
			if ( mdsDao != null ) 	mdsDao.close(); 
		}
		
	}
	
	/**
	 * Persiste as matrículas do discente nas turmas passados no movimento.
	 * também é passado a situação da matrícula em que deve ser cadastrada a matrícula
	 * @param matriculaMov
	 * @return
	 * @throws ArqException
	 */
	private Object matricular(MovimentoAcademico matriculaMov) throws ArqException  {
		DiscenteAdapter discente = matriculaMov.getObjMovimentado();
		TurmaSerie turmaSerie = (TurmaSerie) matriculaMov.getObjAuxiliar();
		boolean matriculouDiscenteEmDisciplina = false;
		
		DiscenteMedioDao dao = getDAO(DiscenteMedioDao.class, matriculaMov);
		TurmaDao turmaDao = getDAO(TurmaDao.class, matriculaMov);
		DisciplinaMedioDao dmDao = getDAO(DisciplinaMedioDao.class, matriculaMov);
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class, matriculaMov);

		try {
			// Buscar as disciplinas já matriculadas neste semestre para o discente
			Collection<Turma> disciplinasJaMatriculadas = dao.findDisciplinasMatriculadas(discente.getId(),true);
			
			//Popular os docentes por disciplina
			List<Integer> turmaIds = new ArrayList<Integer>();
			for (Iterator<TurmaSerieAno> it = turmaSerie.getDisciplinas().iterator(); it.hasNext(); ) {
				TurmaSerieAno tsa = it.next();
				turmaIds.add(tsa.getTurma().getId());
			}
			
			Map<Integer, Set<DocenteTurma>> mapDocentesTurma = new HashMap<Integer, Set<DocenteTurma>>();
			mapDocentesTurma = tsDao.findDocentesByTurmaSerie(turmaSerie.getDisciplinas()); 
			List<Turma> turmas = dmDao.findByPrimaryKeyOtimizado(turmaIds);
			Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
			MatriculaDiscenteSerie matriculaSerie = new MatriculaDiscenteSerie();
			
			// Gravar as matriculas
			for (TurmaSerieAno disciplina : turmaSerie.getDisciplinas()) {
				for (Turma t : turmas) {
					disciplina.setTurma(disciplina.getTurma().getId() == t.getId() ? t : disciplina.getTurma());
				}
				Turma turma = disciplina.getTurma();
//				if (isEmpty(turma.getReservasRecursoFisico())) {
//					turma.setReservasEspacoFisico(new ArrayList<ReservaEspacoFisico>());
//				}
				if (!disciplinasJaMatriculadas.contains(turma)) {
					matriculas.add( persistirMatricula(matriculaMov, turma, turmaSerie) );
					matriculouDiscenteEmDisciplina = true;
				}
				if ( isNotEmpty( mapDocentesTurma.get(turma.getId()) ) ){
					turma.setDocentesTurmas(mapDocentesTurma.get(turma.getId()));
				} else {
					turma.setDocentesTurmas(new HashSet<DocenteTurma>());
				}
			}
			if ( matriculouDiscenteEmDisciplina ) {
				matriculaSerie = (MatriculaDiscenteSerie) matricularDiscenteNaSerie(matriculaMov);

				//Gravar o relacionamento de Matrícula da Disciplina com a Matrícula do discente na série.
				for (MatriculaComponente mc : matriculas) {
					persistirMatriculaComponenteSerie(matriculaMov, mc, matriculaSerie, false);
				}
			}	
			
			return matriculaMov;
		} catch (Exception e) {
			throw new ArqException(e);
		} finally {
			turmaDao.close();
			dao.close();
			dmDao.close();
			tsDao.close();
		}
	}

	/**
	 * Persiste as matrículas de disciplinas em dependência do discente nas disciplinas enviadas no movimento.
	 * também é passado a situação da matrícula em que deve ser cadastrada a matrícula
	 * @param matriculaMov
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("unchecked")
	private Object matricularDependencias(MovimentoAcademico matriculaMov) throws ArqException  {
		DiscenteAdapter discente = matriculaMov.getObjMovimentado();
		List<TurmaSerieAno> disciplinas = (List<TurmaSerieAno>) matriculaMov.getObjAuxiliar();
		
		boolean matriculouDiscenteEmDisciplina = false;
		
		DiscenteMedioDao dao = getDAO(DiscenteMedioDao.class, matriculaMov);
		TurmaDao turmaDao = getDAO(TurmaDao.class, matriculaMov);

		try {
			// Buscar as disciplinas já matriculadas neste semestre para o discente
			Collection<Turma> disciplinasJaMatriculadas = dao.findDisciplinasMatriculadas(discente.getId(),true);
			MatriculaComponente mc = new MatriculaComponente();
			MatriculaDiscenteSerie matriculaSerie = new MatriculaDiscenteSerie();
			
			// Gravar as matriculas
			for (TurmaSerieAno disc : disciplinas) {
				Turma turma = disc.getTurma();
//				if (isEmpty(turma.getReservasRecursoFisico())) {
//					turma.setReservasEspacoFisico(new ArrayList<ReservaEspacoFisico>());
//				}
				if (!disciplinasJaMatriculadas.contains(turma)) {
					mc = persistirMatricula(matriculaMov, turma, disc.getTurmaSerie());
					matriculouDiscenteEmDisciplina = true;
				}
				if ( matriculouDiscenteEmDisciplina ) {
					matriculaSerie = (MatriculaDiscenteSerie) matricularDiscenteNaSerieDependencia(disc.getTurmaSerie(), matriculaMov);
					
					persistirMatriculaComponenteSerie(matriculaMov, mc, matriculaSerie, true);
				}	
			}

			// Atualizar as solicitações de matricula que o aluno havia realizado
			// solicitacaoDao.atualizarSolicitacoes(discente.getId());
			return matriculaMov;
		} catch (Exception e) {
			throw new ArqException(e);
		} finally {
			turmaDao.close();
			dao.close();
		}
	}

	/**
	 * Persiste a matrícula do discente na turma passada como argumento.
	 * @param matriculaMov
	 * @param turma
	 * @throws ArqException
	 */
	private MatriculaComponente persistirMatricula(MovimentoAcademico matriculaMov, Turma turma, TurmaSerie turmaSerie) throws ArqException {
		GenericDAO dao = getGenericDAO(matriculaMov);
		try {
			DiscenteAdapter discente = matriculaMov.getObjMovimentado();
			SituacaoMatricula situacao = SituacaoMatricula.MATRICULADO;
	
			MatriculaComponente matricula = new MatriculaComponente();
			matricula.setDataCadastro(new Date());
			matricula.setRegistroEntrada(matriculaMov.getUsuarioLogado().getRegistroEntrada());
			matricula.setDiscente(discente.getDiscente());
			matricula.setTurma(turma);
			matricula.setAno((short)turma.getAno());
			matricula.setPeriodo((byte)turma.getPeriodo());
			matricula.setComponente(turma.getDisciplina());
			matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());
			matricula.setRecuperacao(null);
			matricula.setSituacaoMatricula(situacao);
			matricula.setSerie(turmaSerie.getSerie()); 
			matricula.setAnoInicio(turma.getAno());
			matricula.setMes( CalendarUtils.getMesByData(turma.getDataInicio()) );
		
			if (situacao.equals(SituacaoMatricula.MATRICULADO) && discente.getStatus() == StatusDiscente.CADASTRADO) {
				// passar status do discente para ATIVO se for CADASTRADO
				ParametrosGestoraAcademicaDao pdao = getDAO(ParametrosGestoraAcademicaDao.class, matriculaMov);
				try {
					DiscenteHelper.alterarStatusDiscente(discente, StatusDiscente.ATIVO, matriculaMov, pdao);
				} finally {
					pdao.close();
				}
			}
	
			dao.create(matricula);
			try {
				if (SigaaListaComando.MATRICULAR_DEPENDENCIA_MEDIO.equals(matriculaMov.getCodMovimento()))
					registrarMatriculaComponenteDependencia(matricula, turma, turmaSerie, matriculaMov);
			} catch (NegocioException e) {
				e.printStackTrace();
			}
			return matricula;
		} finally {
			dao.close();
		}
	}

	/**
	 * Método responsável pela persistência do objeto de relacionamento da matrícula de dependência 
	 * com a sua equivalente do tipo regular.
	 * @param matricula
	 * @param turmaSerie
	 * @param matriculaMov
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private void registrarMatriculaComponenteDependencia(MatriculaComponente matriculaDependencia, 
			Turma turma, TurmaSerie turmaSerie, MovimentoAcademico matriculaMov) throws DAOException, NegocioException{
		
		GenericDAO dao = getGenericDAO(matriculaMov);
		MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class, matriculaMov);
		MatriculaComponenteMedioDao mcmDao = getDAO(MatriculaComponenteMedioDao.class, matriculaMov);
		try {
			DiscenteAdapter discenteMedio = matriculaMov.getObjMovimentado();
			
			MatriculaDiscenteSerie serieAnterior =  mdsDao.findSerieAnteriorDiscente((DiscenteMedio) discenteMedio, new Integer(turma.getAno()));
			
			if ( isNotEmpty(serieAnterior) ) {
				Collection<MatriculaComponente> matriculasReprovadas = mcmDao.findMatriculasByMatriculaDiscenteSerie( discenteMedio, serieAnterior, true, 
						SituacaoMatricula.REPROVADO.getId(), SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId() );
				
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(discenteMedio);
				MatriculaDiscenteSerie matriculaRegular = mdsDao.findSerieAtualDiscente((DiscenteMedio) discenteMedio, cal.getAno());
				for (MatriculaComponente mReprovada : matriculasReprovadas) {
					if ( mReprovada.getTurma().getDisciplina().equals(matriculaDependencia.getTurma().getDisciplina())){
						MatriculaComponenteDependencia registroMatriculaDependencia = new MatriculaComponenteDependencia();
						registroMatriculaDependencia.setMatriculaRegular(mReprovada);
						registroMatriculaDependencia.setMatriculaDependencia(matriculaDependencia);
						registroMatriculaDependencia.setMatriculaSerieRegular(matriculaRegular);
						
						dao.create(registroMatriculaDependencia);
						break;
					}	
				}
			}		
			
		} finally {
			dao.close();
			mdsDao.close();
			mcmDao.close();
		}	
	}
	
	/**
	 * Persiste o relacionamento da matrícula do discente na disciplina com a matrícula deste na série.
	 * @param matriculaMov
	 * @param turma
	 * @throws ArqException
	 */
	private void persistirMatriculaComponenteSerie(MovimentoAcademico matriculaMov, MatriculaComponente matComponente, 
			MatriculaDiscenteSerie matDiscenteSerie, boolean dependencia) throws ArqException {
		GenericDAO dao = getGenericDAO(matriculaMov);
		try {
			MatriculaComponenteSerie mcSerie = new MatriculaComponenteSerie();
			mcSerie.setDataCadastro(new Date());
			mcSerie.setRegistroEntrada(matriculaMov.getUsuarioLogado().getRegistroEntrada());
			mcSerie.setMatriculaComponente(matComponente);
			mcSerie.setMatriculaDiscenteSerie(matDiscenteSerie);
			mcSerie.setAtivo(true);
			mcSerie.setDependencia(dependencia);
			
			dao.create(mcSerie);
		} finally {
			dao.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	
		MovimentoAcademico matriculaMov = (MovimentoAcademico) mov;
		DiscenteMedio discenteMedio = matriculaMov.getObjMovimentado();
		
		//Verificar se o discente está matriculado em outra turma na mesma série e ano.
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class, mov);
		try {
			if (SigaaListaComando.MATRICULAR_DISCENTE_MEDIO.equals(mov.getCodMovimento())) {
				TurmaSerie turmaSerie = (TurmaSerie) matriculaMov.getObjAuxiliar();
				
				if( turmaSerie.getDisciplinas().isEmpty() ){ 
					throw new NegocioException("Não é possível matricular aluno em Turma sem haver disciplinas vinculadas a mesma.");
				}
				
				Collection<TurmaSerie> turmas = tsDao.findByDiscenteSerieAno(discenteMedio, turmaSerie.getSerie(), turmaSerie.getAno());
				for (TurmaSerie ts : turmas) {
					if( ts.getId() == turmaSerie.getId() )
						throw new NegocioException("O aluno já está matriculado na turma solicitada.");
				}
				if( turmas.size() > 0 ){
					throw new NegocioException("O aluno já está matriculado em outra turma da mesma série solicitada.");
				}
				
				if( tsDao.existeMatriculaSerieDiferenteByAno(discenteMedio, turmaSerie.getSerie(), turmaSerie.getAno()) ){
					throw new NegocioException("O aluno já está matriculado em outra série.");
				}
			}
			if (SigaaListaComando.MATRICULAR_DEPENDENCIA_MEDIO.equals(mov.getCodMovimento())) {
				MatriculaDiscenteSerieDao mdsDao = getDAO(MatriculaDiscenteSerieDao.class, mov);
				
				List<TurmaSerieAno> disciplinas = (List<TurmaSerieAno>) matriculaMov.getObjAuxiliar();
				MatriculaDiscenteSerie matriculaSerieAtual = mdsDao.findSerieAtualDiscente(discenteMedio, CalendarioAcademicoHelper.getCalendarioUnidadeGlobalMedio().getAno());
				if ( ValidatorUtil.isNotEmpty(matriculaSerieAtual) ){
					for (TurmaSerieAno disciplina : disciplinas) {
						if ( disciplina.getTurmaSerie().equals(matriculaSerieAtual.getTurmaSerie()) ) {
							throw new NegocioException("Não é permitido matricular dependência(s) de disciplina(s) situadas(s) na série atual do aluno.");
						}
						if ( disciplina.getTurmaSerie().getSerie().getNumero() >= matriculaSerieAtual.getTurmaSerie().getSerie().getNumero() ) {
							throw new NegocioException("É permitido matricular dependência(s) somente para disciplina(s) de série(s) anteriore(s).");
						}
					}
				}	
			}
		} finally {
			tsDao.close();
		}
	}

}
