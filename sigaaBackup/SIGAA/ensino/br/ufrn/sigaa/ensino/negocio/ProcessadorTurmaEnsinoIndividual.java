/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 18/02/2013
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoMatriculaGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorMatriculaGraduacao;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;

/**
 * Processador para manutenção das turmas de ensino individual.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorTurmaEnsinoIndividual extends ProcessadorCadastro{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		TurmaMov tMov = (TurmaMov) mov;
		tMov.setObjMovimentado(tMov.getTurma());
		TurmaDao dao = getDAO(TurmaDao.class, mov);
		
		try{
			if ( mov.getCodMovimento().equals(SigaaListaComando.MATRICULAR_ALUNO_TURMA_ENSINO_INDIVIDUAL) ) {
				validate(mov);
				
				if( tMov.getSolicitacaoEnsinoIndividualOuFerias() != null ){
					matricularAlunos(tMov, dao);
				}
				
			} 
		} finally {
			dao.close();
		}
		
		return null;
	}
	
	/**
	 * Matricula os alunos da solicitação na turma criada
	 * utilizado no caso de turma de ensino individual onde existe uma lista de
	 * discentes interessados na solicitação da turma
	 *
	 * ESTE MÉTODO TAMBÉM ASSOCIA A TURMA CRIADA COM A ENTIDADE SolicitacaoEnsinoIndividual, CRIADO A PARTIR DA SOLICITAÇÃO INICIAL DO DISCENTE
	 * @param mov
	 * @param dao
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void matricularAlunos(TurmaMov mov, GenericDAO dao) throws NegocioException, ArqException, RemoteException{
		
		if( !mov.getTurma().isTurmaEnsinoIndividual() )
			return;
		
		MovimentoMatriculaGraduacao matriculaMov = new MovimentoMatriculaGraduacao();
		matriculaMov.setCodMovimento( SigaaListaComando.MATRICULA_TURMA_FERIAS_ENSINO_INDIVIDUAL );
		matriculaMov.setSistema( mov.getSistema() );
		matriculaMov.setSituacao( SituacaoMatricula.MATRICULADO );
		matriculaMov.setSubsistema( mov.getSubsistema() );
		matriculaMov.setTurmas( new ArrayList<Turma>() );
		matriculaMov.getTurmas().add( mov.getTurma() );
		matriculaMov.setUsuarioLogado( mov.getUsuarioLogado() );
		matriculaMov.setRestricoes( new RestricoesMatricula() );
		mov.getTurma().setMatricular(true);


		for(  DiscentesSolicitacao ds : mov.getSolicitacaoEnsinoIndividualOuFerias().getDiscentes() ){

			dao.lock( ds.getDiscenteGraduacao() );
			matriculaMov.setDiscente( ds.getDiscenteGraduacao() );
			matriculaMov.setCalendarioAcademicoAtual( CalendarioAcademicoHelper.getCalendario( ds.getDiscenteGraduacao() ) );

			ProcessadorMatriculaGraduacao procMatricula = new ProcessadorMatriculaGraduacao();
			procMatricula.execute(matriculaMov);

		}

		dao.updateField(Turma.class, mov.getTurma().getId(), "capacidadeAluno", mov.getTurma().getCapacidadeAluno());
		dao.updateField(SolicitacaoTurma.class, mov.getSolicitacaoEnsinoIndividualOuFerias().getId(), "situacao", SolicitacaoTurma.ATENDIDA);
		
		// Vinculando as matrículas geradas à SolicitacaoEnsinoIndividual criada pelo discente
		MatriculaComponenteDao daoMat = getDAO( MatriculaComponenteDao.class, mov);
		
		try {
			Collection<MatriculaComponente> matriculasGeradas = daoMat.findMatriculadosAleatoriosByTurma(mov.getTurma(), null);
	
			Collection<SolicitacaoEnsinoIndividual> solicitacoesAlunos = dao.findByExactField(SolicitacaoEnsinoIndividual.class, "solicitacaoTurma.id", mov.getSolicitacaoEnsinoIndividualOuFerias().getId());
			for( MatriculaComponente matGerada : matriculasGeradas ){
				for( SolicitacaoEnsinoIndividual sei : solicitacoesAlunos ){
					if( sei.getDiscente().equals( matGerada.getDiscente() ) ){
						dao.updateField(SolicitacaoEnsinoIndividual.class, sei.getId(), "matriculaGerada", matGerada.getId());
					}
				}
			}
		} finally {
			daoMat.close();
		}
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		super.validate(mov);
	}
}
