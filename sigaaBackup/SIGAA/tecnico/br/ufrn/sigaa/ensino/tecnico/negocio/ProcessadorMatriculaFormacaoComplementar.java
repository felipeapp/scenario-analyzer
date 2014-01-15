/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 17/01/2012
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponenteOrdem;
import br.ufrn.sigaa.ensino.dominio.SequenciaMatriculaTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorMatricula;
import br.ufrn.sigaa.ensino.negocio.dominio.MatriculaMov;
import br.ufrn.sigaa.ensino.tecnico.dao.MatriculaFormacaoComplementarDao;

/**
 * Processador responsável por realizar as matrículas dos alunos de formação complementar.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorMatriculaFormacaoComplementar extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MatriculaFormacaoComplementarDao dao = getDAO(MatriculaFormacaoComplementarDao.class, mov);
		
		try {
			MatriculaMov matriculaMov = (MatriculaMov) mov;
			Turma t = matriculaMov.getTurmas().iterator().next();
			
			SequenciaMatriculaTurma sequencia = dao.getSequenciaMatriculaTurma(t);

			validate(mov);

			MatriculaComponente matricula = matricular(matriculaMov);
			
			sequencia.incrementarSequencia();
			
			MatriculaComponenteOrdem mco = new MatriculaComponenteOrdem();
			mco.setMatriculaComponente(matricula);
			mco.setOrdem(sequencia.getSequencia());
			dao.create(mco);
			
			// Cria uma nova ou atualiza a sequência existente
			dao.createOrUpdate(sequencia);
		} finally {
			dao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ProcessadorMatricula processador = new ProcessadorMatricula();
		processador.validate(mov);
	}

	/**
	 * Persiste as matrículas do discente nas turmas passadas no movimento.
	 * também é passado a situação da matrícula em que deve ser cadastrada a matrícula
	 * @param matriculaMov
	 * @return
	 * @throws ArqException
	 */
	private MatriculaComponente matricular(MatriculaMov matriculaMov) throws ArqException  {
		DiscenteAdapter discente = matriculaMov.getDiscentes().iterator().next();

		DiscenteDao dao = getDAO(DiscenteDao.class, matriculaMov);
		TurmaDao turmaDao = getDAO(TurmaDao.class, matriculaMov);

		try {
			// Buscar as turmas já matriculadas neste semestre para o discente
			Collection<Turma> turmasMatriculadas = dao.findTurmasMatriculadas(discente.getId(), true);

			MatriculaComponente matricula = null;
			
			// Gravar as matriculas
			for (Turma turma : matriculaMov.getTurmas()) {
				if (turma.isMatricular() && !turmasMatriculadas.contains(turma)) {
					matricula = persistirMatricula(matriculaMov, turma);
					if (turma.getDisciplina().isBloco()) {
						Collection<Turma> turmasSubunidades = turmaDao.findTurmasSubUnidadesByBloco(turma);
						if (turmasSubunidades != null) {
							for (Turma turmaSubUnidade : turmasSubunidades) {
								persistirMatricula(matriculaMov, turmaSubUnidade);
							}
						}
					}
				}
			}

			return matricula;
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
	private MatriculaComponente persistirMatricula(MatriculaMov matriculaMov, Turma turma) throws ArqException {
		GenericDAO dao = getGenericDAO(matriculaMov);
		try {
			DiscenteAdapter discente = matriculaMov.getDiscentes().iterator().next();
			SituacaoMatricula situacao = matriculaMov.getSituacao();
	
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
			return matricula;
		} finally {
			dao.close();
		}
	}
}
