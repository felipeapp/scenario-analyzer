/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dao.CurriculoMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.negocio.ProcessadorTurma;
import br.ufrn.sigaa.ensino.negocio.TurmaHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;

/**
 * Processador para cadastrar turmas de série de ensino médio.
 * Neste processador serão cadastradas as turmas virtuais das disciplinas pertencentes a uma turma da série,
 * assim como, o vinculo das disciplinas com a turma, esta nomeada por A, B, C, etc...
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorTurmaSerie extends ProcessadorCadastro{

	@Override
	public Object execute(Movimento movimento) throws NegocioException,	ArqException, RemoteException {
		MovimentoCadastro mc = (MovimentoCadastro) movimento;
		validate(movimento);
		
		/* Cadastrar / Alterar TurmaSerie */
		if (mc.getCodMovimento().equals(SigaaListaComando.CADASTRAR_TURMA_SERIE)) {
			return criarTurmaSerie(mc);
		} else if (mc.getCodMovimento().equals(SigaaListaComando.ALTERAR_TURMA_SERIE)) {
			return alterarTurmaSerie(mc);
		} else if (mc.getCodMovimento().equals(SigaaListaComando.REMOVER_TURMA_SERIE)) {
			return removerTurmaSerie(mc);
		}		
		return null;
	}
	
	/**
	 * Método responsável pela persistência da TurmaSerie 
	 * @param mc
	 * @return
	 */
	private List<TurmaSerieAno> criarTurmaSerie( MovimentoCadastro mc ) throws ArqException, NegocioException, RemoteException {
		
		List<TurmaSerieAno> listaDisciplinas = new ArrayList<TurmaSerieAno>(); 
		CurriculoMedioDao cmDao = getDAO(CurriculoMedioDao.class, mc);
		TurmaSerie ts = (TurmaSerie) mc.getObjMovimentado();
		try {
			Collection<ComponenteCurricular> disciplinas = cmDao.findDisciplinasByCurriculo(ts.getCurriculo());
			if ( disciplinas.isEmpty() ){ 
				throw new NegocioException("Não é possível cadastrar Turma com Estrutura Curricular, que não tenha Disciplinas vinculadas.");
			} else {
				criar(mc);
				for (ComponenteCurricular disciplina : disciplinas) {
					listaDisciplinas.add( criarTurmaDisciplina(disciplina, mc) );
				}
			}	
			return listaDisciplinas;
		} finally {
			cmDao.close();
		}
	}
	
	/**
	 * Método responsável pela alteração dos dados da TurmaSerie 
	 * @param mc
	 * @return
	 */
	private List<TurmaSerieAno> alterarTurmaSerie( MovimentoCadastro mc ) throws ArqException, NegocioException, RemoteException {
		List<TurmaSerieAno> listaDisciplinas = new ArrayList<TurmaSerieAno>();
		CurriculoMedioDao cmDao = getDAO(CurriculoMedioDao.class, mc);
		TurmaSerie ts = (TurmaSerie) mc.getObjMovimentado();
		Boolean alterarCurriculo = (Boolean) mc.getObjAuxiliar();
		try {
			ts.setDisciplinas(cmDao.findByExactField(TurmaSerieAno.class, "turmaSerie", ts.getId()));
			if( alterarCurriculo ){
				Collection<ComponenteCurricular> disciplinasNovoCurriculo = cmDao.findDisciplinasByCurriculo(ts.getCurriculo());
				if ( disciplinasNovoCurriculo.isEmpty() ){ 
					throw new NegocioException("Não é possível Alterar Turma para uma Estrutura Curricular sem haver Disciplinas vinculadas.");
				}else{
					List<TurmaSerieAno> disciplinasExcluir = new ArrayList<TurmaSerieAno>();
					disciplinasExcluir.addAll( ts.getDisciplinas() );
					ts.setDisciplinas(new ArrayList<TurmaSerieAno>());
					for (TurmaSerieAno tsa : disciplinasExcluir) {
						 removerDisciplina(tsa.getTurma(), tsa, mc);
					}
					for (ComponenteCurricular disciplina : disciplinasNovoCurriculo) {
						listaDisciplinas.add( criarTurmaDisciplina(disciplina, mc) );
					}
				}
			}
		} finally {
			cmDao.close();
		}
			
		// Alteração dos dados da disciplinas, conforme alteração da TurmaSerie.
		for (TurmaSerieAno tsa : ts.getDisciplinas()) {
			alterarDisciplina(tsa.getTurma(), ts, mc);
		}
		
		alterar(mc);
		
		return listaDisciplinas;
	}
	
	/**
	 * Método responsável pela remoção de TurmaSerie 
	 * @param mc
	 * @return
	 */
	private Object removerTurmaSerie( MovimentoCadastro mc ) throws ArqException, NegocioException, RemoteException {
		TurmaSerie ts = (TurmaSerie) mc.getObjMovimentado();
		ts = getGenericDAO(mc).findByPrimaryKey(ts.getId(), TurmaSerie.class);
		for (TurmaSerieAno tsa : ts.getDisciplinas()) {
			removerDisciplina(tsa.getTurma(), tsa, mc);
		}
		remover(mc);
		return null;
	}
	
	/**
	 * Método responsável pela alteração da situação da disciplina para EXCLUÍDA, nos casos de remoção da turma.
	 * @param disciplina
	 * @param mc
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 */
	public Object removerDisciplina(Turma disciplina, TurmaSerieAno turmaSerieAno, MovimentoCadastro mc) throws ArqException, NegocioException, RemoteException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mc);
		try{
			disciplina = dao.refresh(disciplina);
			long totalMatriculasAtivas = dao.findTotalMatriculasByTurmaSituacao(disciplina, 
					SituacaoMatricula.MATRICULADO.getId(), SituacaoMatricula.APROVADO.getId(), 
					SituacaoMatricula.REPROVADO.getId(), SituacaoMatricula.REPROVADO_FALTA.getId(), 
					SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId());
			
			if( totalMatriculasAtivas > 0) {
				if (mc.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MEDIO)) {
					List<MatriculaComponente> matriculas =dao.findAtivasByTurma(disciplina);
					for (MatriculaComponente matricula : matriculas) {
						matricula.setSituacaoMatricula(SituacaoMatricula.EXCLUIDA);
						dao.updateField(MatriculaComponente.class, matricula.getId(), "situacaoMatricula", SituacaoMatricula.EXCLUIDA);
					}
				} else {
					throw new NegocioException("Não é possível excluir esta turma pois ela possui alunos matriculados.");
				}
			}
			
			long totalMatriculas = dao.findTotalMatriculasByTurma( disciplina, true);
			
			if( totalMatriculas == 0 )
				disciplina.setSituacaoTurma( new SituacaoTurma(SituacaoTurma.EXCLUIDA) );
			else
				disciplina.setSituacaoTurma( new SituacaoTurma(SituacaoTurma.INTERROMPIDA) );
	
			TurmaHelper.criarAlteracaoStatusTurma(disciplina, mc);
			dao.remove(turmaSerieAno);
			dao.update(disciplina);
		} finally {
			dao.close();
		}
		
		return null;
	}
	
	/** Persiste a turma da disciplina, caso seja uma nova.
	 * @param mov
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public TurmaSerieAno criarTurmaDisciplina(ComponenteCurricular disciplina, MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class, mov);
		TurmaSerie ts = (TurmaSerie) mov.getObjMovimentado();
		validateDisciplina(disciplina, ts);
		TurmaSerieAno tsa = new TurmaSerieAno();
		
		try {
			/* Cadastrar TurmaSerieAno (vinculo de turma com turmaSerie) */
			tsa.setTurmaSerie(ts);
			tsa.setTurma(populeAndCreateTurma(disciplina, mov));
			dao.create(tsa);
		} finally {
			dao.close();
		}
		return tsa;
	}
	
	/**
	 * Método responsável por popular o objeto referente a turma da disciplina.
	 * 
	 * @param ts
	 * @param disciplina
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private Turma populeAndCreateTurma(ComponenteCurricular disciplina, MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException{
		TurmaSerie ts = (TurmaSerie) mov.getObjMovimentado();
		TurmaDao dao = getDAO(TurmaDao.class, mov);
		Turma turma = new Turma();
		/* Cadastrar Turma (Utilizar disciplinas inseridas no currículo) */
		try {
			turma.setAno(ts.getAno());
			turma.setPeriodo(0);
			turma.setCapacidadeAluno(ts.getCapacidadeAluno());
			turma.setCodigo(ts.getNome());
			turma.setCurso(ts.getSerie().getCursoMedio());
			turma.setDisciplina(disciplina);
			turma.setSituacaoTurma( new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE) );
			turma.setTipo(Turma.REGULAR);
			turma.setDataInicio(ts.getDataInicio());
			turma.setDataFim(ts.getDataFim());
			turma.setLocal(ts.getLocal());
			turma.setAgrupadora(false);
			
			if (turma != null && turma.getId() == 0) {
				ProcessadorTurma processadorTurna = new ProcessadorTurma();
				TurmaMov turmaMov = new TurmaMov();
				turmaMov.setTurma(turma);
				turmaMov.setCodMovimento(SigaaListaComando.CADASTRAR_TURMA);
				turma = (Turma) processadorTurna.execute(turmaMov);
			}
		} finally {
			dao.close();
		}
		return turma;
	}
	
	/**
	 * Método responsável pela persistência das alterações de disciplinas, devido alterações em TurmaSerie.
	 * 
	 * @param ts
	 * @param disciplina
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void alterarDisciplina(Turma disciplina, TurmaSerie turmaSerie, MovimentoCadastro mc) throws NegocioException, ArqException, RemoteException{
		TurmaDao dao = getDAO(TurmaDao.class, mc);
		try {
			disciplina.setAno(turmaSerie.getAno());
			disciplina.setCapacidadeAluno(turmaSerie.getCapacidadeAluno());
			disciplina.setCodigo(turmaSerie.getNome());
			disciplina.setCurso(turmaSerie.getSerie().getCursoMedio());
			disciplina.setDataInicio(turmaSerie.getDataInicio());
			disciplina.setDataFim(turmaSerie.getDataFim());
			
			dao.updateNoFlush(disciplina);
			
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Método Responsável pela validação da Disciplinas da Turma de ensino médio.
	 * @param disciplina
	 * @param ts
	 */
	private void validateDisciplina(ComponenteCurricular disciplina, TurmaSerie ts) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro mc = (MovimentoCadastro) mov;
		TurmaSerie ts = (TurmaSerie) mc.getObjMovimentado();
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class, mov);
		
		/* Validação de Remoção de turmas de ensino médio. */
		if ( mc.getCodMovimento().equals(SigaaListaComando.REMOVER_TURMA_SERIE) ) {
			try{
			if ( tsDao.findQtdeAlunosByTurma(ts) > 0 )
				throw new NegocioException("Não é possível excluir esta turma, pois há alunos relacionados a ela.");
			}finally{
				tsDao.close();
			}
		}
	}
}
