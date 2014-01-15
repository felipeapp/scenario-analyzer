/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/07/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOperacaoMatricula;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaComponenteMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;

/**
 * Processador responsável pela alteração dos status de matrículas de discentes do ensino médio em série
 *
 * @author Arlindo
 * @author Rafael Gomes
 */
public class ProcessadorAlteracaoStatusMatriculaMedio extends AbstractProcessador {

	/**
	 * Executa a alteração de status de matrículas dos discente selecionados
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		if (mov.getCodMovimento().equals(SigaaListaComando.ALTERACAO_STATUS_DISCIPLINA)) {
			MovimentoOperacaoMatricula matMov = (MovimentoOperacaoMatricula) mov;
			processarAlteracaoStatusMatriculaComponente(matMov);
			return matMov.getMatriculas();
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_STATUS_MATRICULA_MEDIO)) {
			MovimentoAlteracaoStatusMatriculaMedio matMov = (MovimentoAlteracaoStatusMatriculaMedio) mov;
			processarAlteracaoStatus(matMov);
			return matMov.getMatriculas();
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_STATUS_SERIE_DEPENDENCIA)) {
			MovimentoAlteracaoStatusMatriculaMedio matMov = (MovimentoAlteracaoStatusMatriculaMedio) mov;
			processarAlteracaoStatusDependencia(matMov);
			return matMov.getMatriculas();
		}
		return null;
	}
	
	/**
	 * Este método executa a alteração de status da matrícula do componente curricular.
	 * 
	 * @param matMov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void processarAlteracaoStatusMatriculaComponente(MovimentoOperacaoMatricula mov) throws NegocioException, ArqException {

		MovimentoOperacaoMatricula matMov = mov;
		Collection<MatriculaComponente> matriculas = matMov.getMatriculas();
		SituacaoMatricula sitMatricula = matMov.getNovaSituacao();
		MatriculaComponenteDao dao = null;
		
		try {
			
			dao = getDAO(MatriculaComponenteDao.class, matMov);
			
			for (MatriculaComponente mat : matriculas) {
				/* Atualiza a situação mas matrículas dos componentes curriculares */
				validaMatriculaComponente(mat, sitMatricula, matMov, dao);
				MatriculaComponenteHelper.alterarSituacaoMatricula(mat, sitMatricula, matMov, dao);
			}
			
		} finally {
			if (dao!= null)
				dao.close();
		}
	}
	
	/**
	 * Este método executa a alteração de status da matrícula.
	 * 
	 * @param matMov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void processarAlteracaoStatus(MovimentoAlteracaoStatusMatriculaMedio matMov) throws NegocioException, ArqException {
		Collection<MatriculaDiscenteSerie> matDiscenteSerie = matMov.getMatriculas();
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class, matMov);
		MatriculaComponenteDao daoMat = getDAO(MatriculaComponenteDao.class, matMov);
		MatriculaComponenteMedioDao mcmDao = getDAO(MatriculaComponenteMedioDao.class, matMov);
		try {
			
			List<Integer> discentesAlterados = new ArrayList<Integer>();
						
			//Collection<MatriculaComponente> matriculas = dao.findMatriculasByTurmaSerie(matMov.getTurmaSerie(), discentes,  null);
			Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();  
			matriculas = mcmDao.findMatriculasByMatriculaDiscenteSerie(matDiscenteSerie, false,  null);
			
			if (ValidatorUtil.isEmpty(matriculas))
				throw new NegocioException( "Nenhuma matrícula localizada para o(s) discente(s) seleciondo(s)." );
			
			for (MatriculaComponente mat : matriculas) {
				
				MatriculaDiscenteSerie matriculaSerie = null;
				for (MatriculaDiscenteSerie matSerie : matDiscenteSerie){
					if (matSerie.getDiscenteMedio().getId() == mat.getDiscente().getId()){
						matriculaSerie = UFRNUtils.deepCopy(matSerie);
						break;
					}
				}
				
				if (matriculaSerie != null && matriculaSerie.getNovaSituacaoMatricula() != null){
					SituacaoMatricula sitMatricula = dao.refresh(matriculaSerie.getNovaSituacaoMatricula()).getSituacaoMatricula();
					if (sitMatricula != null){
						/* Atualiza a situação do discente na série */
						MatriculaDiscenteSerieHelper.alterarSituacaoMatriculaSerie(
								matriculaSerie, matriculaSerie.getNovaSituacaoMatricula(),matMov, dao);
						
						/* Realiza a alteração do status do discente quando a mudança da situação da turma solicitar. */
						if (!discentesAlterados.contains(matriculaSerie.getDiscenteMedio().getId())){
							MatriculaDiscenteSerieHelper.alterarStatusDiscente(matriculaSerie, matriculaSerie.getNovaSituacaoMatricula(), matMov, dao);
							discentesAlterados.add(matriculaSerie.getDiscenteMedio().getId());
						}
						
						/* Atualiza a situação nas matrículas dos componentes curriculares */
						MatriculaComponenteHelper.alterarSituacaoMatricula(mat, sitMatricula, matMov, daoMat);
					}
				}
				
			}
			
		} finally {
			dao.close();
			daoMat.close();
			mcmDao.close();
		}

	}

	/**
	 * Este método executa a alteração de status da matrícula do discente na série, quando o aluno for aprovado em dependência.
	 * 
	 * @param matMov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void processarAlteracaoStatusDependencia(MovimentoAlteracaoStatusMatriculaMedio matMov) throws NegocioException, ArqException {
		Collection<MatriculaDiscenteSerie> matDiscenteSerie = matMov.getMatriculas();
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class, matMov);
		MatriculaComponenteDao daoMat = getDAO(MatriculaComponenteDao.class, matMov);
		MatriculaComponenteMedioDao mcmDao = getDAO(MatriculaComponenteMedioDao.class, matMov);
		try {
			
			List<Integer> discentesAlterados = new ArrayList<Integer>();
						
			//Collection<MatriculaComponente> matriculas = dao.findMatriculasByTurmaSerie(matMov.getTurmaSerie(), discentes,  null);
			Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();  
			matriculas = mcmDao.findMatriculasByMatriculaDiscenteSerie(matDiscenteSerie, false,  null);
			
			if (ValidatorUtil.isEmpty(matriculas))
				throw new NegocioException( "Nenhuma matrícula localizada para o(s) discente(s) seleciondo(s)." );
			
			for (MatriculaComponente mat : matriculas) {
				
				MatriculaDiscenteSerie matriculaSerie = null;
				for (MatriculaDiscenteSerie matSerie : matDiscenteSerie){
					if (matSerie.getDiscenteMedio().getId() == mat.getDiscente().getId()){
						matriculaSerie = UFRNUtils.deepCopy(matSerie);
						break;
					}
				}
				
				if (matriculaSerie != null && matriculaSerie.getNovaSituacaoMatricula() != null){
					SituacaoMatricula sitMatricula = dao.refresh(matriculaSerie.getNovaSituacaoMatricula()).getSituacaoMatricula();
					if (sitMatricula != null){
						/* Atualiza a situação do discente na série */
						MatriculaDiscenteSerieHelper.alterarSituacaoMatriculaSerie(
								matriculaSerie, matriculaSerie.getNovaSituacaoMatricula(),matMov, dao);
						
						/* Realiza a alteração do status do discente quando a mudança da situação da turma solicitar. */
						if (!discentesAlterados.contains(matriculaSerie.getDiscenteMedio().getId())){
							MatriculaDiscenteSerieHelper.alterarStatusDiscente(matriculaSerie, matriculaSerie.getNovaSituacaoMatricula(), matMov, dao);
							discentesAlterados.add(matriculaSerie.getDiscenteMedio().getId());
						}
						
					}
				}
				
			}
			
		} finally {
			dao.close();
			daoMat.close();
			mcmDao.close();
		}

	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}	
	

	/**
	 * Valida se é possível alterar o status de uma matrícula
	 * 
	 * @param matriculaAtual
	 * @param novaSituacao
	 * @param mov
	 * @param dao
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void validaMatriculaComponente(MatriculaComponente matriculaAtual, SituacaoMatricula novaSituacao,
			Movimento mov, MatriculaComponenteDao dao) throws NegocioException, ArqException{
		if (matriculaAtual.getTurma() != null 
				&& novaSituacao.getId() != SituacaoMatricula.CANCELADO.getId()
				&& novaSituacao.getId() != SituacaoMatricula.EXCLUIDA.getId()
				&& dao.countByDiscenteTurmas(matriculaAtual.getDiscente(), matriculaAtual.getTurma(), novaSituacao) > 0) {
			throw new NegocioException("Erro ao registrar alteração de status de matrículas.<br>"
					+ "Não é permitido alterar situação da matrícula dessa turma para a situação escolhida,<br>"
					+ " pois já existem registros de matrículas dessa turma com a mesma situação para o discente " + matriculaAtual.getDiscente().getNome());
		}
	}
	
}
