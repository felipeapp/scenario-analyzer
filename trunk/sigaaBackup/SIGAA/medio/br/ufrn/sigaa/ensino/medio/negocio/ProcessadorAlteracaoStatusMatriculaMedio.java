/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Processador respons�vel pela altera��o dos status de matr�culas de discentes do ensino m�dio em s�rie
 *
 * @author Arlindo
 * @author Rafael Gomes
 */
public class ProcessadorAlteracaoStatusMatriculaMedio extends AbstractProcessador {

	/**
	 * Executa a altera��o de status de matr�culas dos discente selecionados
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
	 * Este m�todo executa a altera��o de status da matr�cula do componente curricular.
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
				/* Atualiza a situa��o mas matr�culas dos componentes curriculares */
				validaMatriculaComponente(mat, sitMatricula, matMov, dao);
				MatriculaComponenteHelper.alterarSituacaoMatricula(mat, sitMatricula, matMov, dao);
			}
			
		} finally {
			if (dao!= null)
				dao.close();
		}
	}
	
	/**
	 * Este m�todo executa a altera��o de status da matr�cula.
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
				throw new NegocioException( "Nenhuma matr�cula localizada para o(s) discente(s) seleciondo(s)." );
			
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
						/* Atualiza a situa��o do discente na s�rie */
						MatriculaDiscenteSerieHelper.alterarSituacaoMatriculaSerie(
								matriculaSerie, matriculaSerie.getNovaSituacaoMatricula(),matMov, dao);
						
						/* Realiza a altera��o do status do discente quando a mudan�a da situa��o da turma solicitar. */
						if (!discentesAlterados.contains(matriculaSerie.getDiscenteMedio().getId())){
							MatriculaDiscenteSerieHelper.alterarStatusDiscente(matriculaSerie, matriculaSerie.getNovaSituacaoMatricula(), matMov, dao);
							discentesAlterados.add(matriculaSerie.getDiscenteMedio().getId());
						}
						
						/* Atualiza a situa��o nas matr�culas dos componentes curriculares */
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
	 * Este m�todo executa a altera��o de status da matr�cula do discente na s�rie, quando o aluno for aprovado em depend�ncia.
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
				throw new NegocioException( "Nenhuma matr�cula localizada para o(s) discente(s) seleciondo(s)." );
			
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
						/* Atualiza a situa��o do discente na s�rie */
						MatriculaDiscenteSerieHelper.alterarSituacaoMatriculaSerie(
								matriculaSerie, matriculaSerie.getNovaSituacaoMatricula(),matMov, dao);
						
						/* Realiza a altera��o do status do discente quando a mudan�a da situa��o da turma solicitar. */
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
	 * Valida se � poss�vel alterar o status de uma matr�cula
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
			throw new NegocioException("Erro ao registrar altera��o de status de matr�culas.<br>"
					+ "N�o � permitido alterar situa��o da matr�cula dessa turma para a situa��o escolhida,<br>"
					+ " pois j� existem registros de matr�culas dessa turma com a mesma situa��o para o discente " + matriculaAtual.getDiscente().getNome());
		}
	}
	
}
