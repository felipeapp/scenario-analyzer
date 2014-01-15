/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validaInt;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.ensino.dao.PlanoMatriculaIngressantesDao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;

/**
 * Classe com métodos de validação das regras de negócio da transferência de alunos
 * entre turmas de graduação
 *
 * @author Leonardo
 * @author Ricardo Wendell
 */
public class TransferenciaTurmasValidator {

	/**
	 * Validar a situação da turma de origem dos alunos a serem transferidos
	 * 
	 * @param turma
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaTurmaOrigem(Turma turmaOrigem, ListaMensagens lista, boolean transfByDiscente, boolean gestor) throws DAOException{
		TurmaDao turmaDao = DAOFactory.getInstance().getDAO( TurmaDao.class);
		SolicitacaoMatriculaDao solicitacaoDao =  DAOFactory.getInstance().getDAO( SolicitacaoMatriculaDao.class);
		
		try {
			turmaOrigem.setQtdEspera( solicitacaoDao.countByTurma(turmaOrigem, SolicitacaoMatricula.getStatusSolicitacoesPendentes(), true ));
			turmaOrigem.setQtdMatriculados(turmaDao.findQtdAlunosPorTurma( turmaOrigem.getId(), 
					SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA) );
			
			// Validar situação da turma
			if (!turmaOrigem.isAberta() ) {
				lista.addErro("Somente é possível transferir alunos de turmas abertas.");
				if (transfByDiscente) lista.addErro("Erro na turma de origem " + turmaOrigem.getDescricaoSemDocente() + ".");
			}
			
			// Validar tipo da turma
			if ( !turmaOrigem.isTurmaRegular() && !turmaOrigem.isTurmaEnsinoIndividual()) {
				lista.addErro("Só é possível transferir alunos entre turmas regulares ou de ensino individual");
				if (transfByDiscente) lista.addErro("Erro na turma de origem " + turmaOrigem.getDescricaoSemDocente() + ".");
			}

			// Verificar se há alunos matriculados ou com solicitação pendente para a turma selecionada
			if( turmaOrigem.getQtdEspera() + turmaOrigem.getQtdMatriculados() == 0 ) {
				lista.addErro("A turma selecionada não possui alunos passíveis de transferência.");
				if (transfByDiscente) lista.addErro("Erro na turma de origem " + turmaOrigem.getDescricaoSemDocente() + ".");
			}
			
			// se for gestor não valida
			if (!gestor) {
				PlanoMatriculaIngressantesDao dao = DAOFactory.getInstance().getDAO(PlanoMatriculaIngressantesDao.class);
				
				List<PlanoMatriculaIngressantes> planos = dao.findByTurma(turmaOrigem);
				
				if (isNotEmpty(planos)) {
					
					StringBuilder sb = new StringBuilder();
					
					sb.append("Não é possível transferir porque esta turma de origem esta vinculada a um plano de matrícula para aluno ingressante. <br>Planos:");
					
					for (PlanoMatriculaIngressantes p : planos) {
						sb.append("<br>");
						sb.append(p);
					}
					
					lista.addErro(sb.toString());
				}			
			}
			
			
		} finally {
			turmaDao.close();
			solicitacaoDao.close();
		}
	}

	/**
	 * Validar a situação da turma de destino dos alunos a serem transferidos
	 * 
	 * @param turmaOrigem
	 * @param turmaDestino
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaTurmaDestino(Turma turmaOrigem, Turma turmaDestino, ListaMensagens lista, boolean transfByDiscente, boolean administrador)  throws DAOException {
		TurmaDao turmaDao = DAOFactory.getInstance().getDAO( TurmaDao.class );
		SolicitacaoMatriculaDao solicitacaoDao =  DAOFactory.getInstance().getDAO( SolicitacaoMatriculaDao.class );
		
		try {
			turmaDestino.setQtdEspera( solicitacaoDao.countByTurma(turmaDestino, SolicitacaoMatricula.getStatusSolicitacoesPendentes(), true ));
			turmaDestino.setQtdMatriculados(turmaDao.findQtdAlunosPorTurma( turmaDestino.getId(), 
					SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA));
			
			// Validar situação da turma
			if( !turmaDestino.isAberta() ) {
				lista.addErro("Só é possível realizar a transferência de alunos entre turmas abertas.");
				if (transfByDiscente) lista.addErro("Erro na turma de destino " + turmaDestino.getDescricaoSemDocente() + ".");
			}
			
			// Validar tipo da turma
			if ( !turmaDestino.isTurmaRegular() && !turmaDestino.isTurmaEnsinoIndividual()) {
				lista.addErro("Só é possível transferir alunos entre turmas regulares ou de ensino individual");
				if (transfByDiscente) lista.addErro("Erro na turma de destino " + turmaDestino.getDescricaoSemDocente() + ".");
			}
			
			// Validar capacidade da turma de destino em relação as matriculas ativas e as solicitações pendentes
			long totalMatriculasAtivas = turmaDestino.getQtdMatriculados();
			//long totalSolicitacoesPendentes = turmaDestino.getQtdEspera();
			
			if ( !ValidatorUtil.isEmpty(turmaDestino.getCapacidadeAluno()) && (totalMatriculasAtivas >= turmaDestino.getCapacidadeAluno()) ) {
				lista.addErro("Não é possível transferir alunos para turmas cuja capacidade de alunos já foi atingida.");
				if (transfByDiscente) lista.addErro("Erro na turma de destino " + turmaDestino.getDescricaoSemDocente() + ".");
			}
			
			// Verificar se a turma de destino é do mesmo componente curricular da turma de origem
			if(!turmaOrigem.getDisciplina().equals( turmaDestino.getDisciplina() ) && !administrador){
				lista.addErro("A turma de destino deve ser do mesmo componente curricular da turma de origem");
				if (transfByDiscente) lista.addErro("Erro na turma de destino " + turmaDestino.getDescricaoSemDocente() + ".");
			}	
		} finally {
			turmaDao.close();
			solicitacaoDao.close();
		}
	}

	/**
	 * Validar a lista de alunos a serem transferidos entre as turmas
	 * 
	 * @param automatica
	 * @param qtdMatriculados
	 * @param turmaOrigem
	 * @param turmaDestino
	 * @param matriculas
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaAlunos(boolean automatica, Integer qtdMatriculados, Integer qtdSolicitacoes, 
			Turma turmaOrigem, Turma turmaDestino, 
			Collection<MatriculaComponente> matriculas, Collection<SolicitacaoMatricula> solicitacoes, 
			ListaMensagens lista) throws DAOException{
		TurmaDao dao = DAOFactory.getInstance().getDAO( TurmaDao.class );

		try {
			
			if ( automatica ){
				// Validar quantidade de alunos a ser transferidos
				validaInt(qtdMatriculados + qtdSolicitacoes, "Quantidade de alunos a transferir", lista);
				
				// Validar a quantidade de alunos especificada em relação a quantidade de matriculas na turma de origem 
				if(qtdMatriculados > turmaOrigem.getQtdMatriculados() )
					lista.addErro("O número de alunos a serem transferidos não pode ser maior" +
							" do que a quantidade de alunos matriculados na turma de origem.");
				if(qtdSolicitacoes > turmaOrigem.getQtdEspera() )
					lista.addErro("O número de alunos a serem transferidos não pode ser maior" +
							" do que a quantidade de solicitações para a turma de origem.");
				
			} else {
				// Validar lista de discentes selecionados
				if ( (matriculas == null || matriculas.isEmpty()) && (solicitacoes == null || solicitacoes.isEmpty() ) ) {
					lista.addErro("É necessário selecionar pelo menos um discente para efetuar a transferência.");
				}
				
				qtdMatriculados = matriculas != null ? matriculas.size() : 0;
				qtdMatriculados += solicitacoes != null ? solicitacoes.size() : 0;
			}
			// long totalAlunosDestino = turmaDestino.getQtdMatriculados() + turmaDestino.getQtdEspera();
			
			// Validar a quantidade de alunos especificada em relação as vagas da turma de destino
			if( !ValidatorUtil.isEmpty(turmaDestino.getCapacidadeAluno()) && qtdMatriculados > ( turmaDestino.getCapacidadeAluno() - turmaDestino.getQtdMatriculados() ) )
				lista.addErro("O número de alunos a serem transferido não pode ser maior" +
						" do que a quantidade de vagas disponíveis na turma de destino.");
			
		} finally {
			dao.close();
		}
	}
}
