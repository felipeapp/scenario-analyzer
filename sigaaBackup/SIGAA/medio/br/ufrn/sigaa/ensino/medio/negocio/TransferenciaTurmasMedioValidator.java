/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 13/07/2011
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validaInt;

import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;

/**
 * Classe com métodos de validação das regras de negócio da transferência de alunos
 * entre turmas de ensino médio.
 *
 * @author Rafael Gomes
 */
public class TransferenciaTurmasMedioValidator {

	/**
	 * Validar a situação da turma de origem dos alunos a serem transferidos
	 * 
	 * @param turma
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaTurmaSerieOrigem(TurmaSerie turmaSerieOrigem, ListaMensagens lista) throws DAOException{
		TurmaSerieDao tsDao = DAOFactory.getInstance().getDAO( TurmaSerieDao.class);
		
		try {
			turmaSerieOrigem.setQtdMatriculados(tsDao.findQtdeAlunosByTurma(turmaSerieOrigem));
			// Validar situação da turma
			if (!turmaSerieOrigem.isAtivo() ) {
				lista.addErro("Somente é possível transferir alunos de turmas ativas.");
			}
			
			// Verificar se há alunos matriculados ou com solicitação pendente para a turma selecionada
			if( turmaSerieOrigem.getQtdMatriculados() == 0 ) {
				lista.addErro("A turma selecionada não possui alunos passíveis de transferência.");
			}
			
		} finally {
			tsDao.close();
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
	public static void validaTurmaSerieDestino(TurmaSerie turmaSerieOrigem, TurmaSerie turmaSerieDestino, ListaMensagens lista)  throws DAOException {
		TurmaSerieDao tsDao = DAOFactory.getInstance().getDAO( TurmaSerieDao.class );
		
		try {
			turmaSerieDestino.setQtdMatriculados(tsDao.findQtdeAlunosByTurma(turmaSerieDestino));
			
			// Validar situação da turma
			if( !turmaSerieDestino.isAtivo() ) {
				lista.addErro("Só é possível realizar a transferência de alunos entre turmas ativas.");
			}
			
			// Validar capacidade da turma de destino em relação as matriculas ativas
			long totalMatriculasAtivas = turmaSerieDestino.getQtdMatriculados();
			
			if ( !ValidatorUtil.isEmpty(turmaSerieDestino.getCapacidadeAluno()) && (totalMatriculasAtivas >= turmaSerieDestino.getCapacidadeAluno()) ) {
				lista.addErro("Não é possível transferir alunos para turmas cuja capacidade de alunos já foi atingida.");
			}
			
			// Verificar se a turma de destino é da mesma série da turma de origem
			if( turmaSerieOrigem.getSerie().getId() != turmaSerieDestino.getSerie().getId() ){
				lista.addErro("A turma de destino deve ser da mesma série da turma de origem");
			}	
		} finally {
			tsDao.close();
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
	public static void validaAlunos(boolean automatica, Integer qtdMatriculados, 
			TurmaSerie turmaSerieOrigem, TurmaSerie turmaSerieDestino, Collection<MatriculaDiscenteSerie> matriculas, 
			ListaMensagens lista) throws DAOException{TurmaSerieDao dao = DAOFactory.getInstance().getDAO( TurmaSerieDao.class );

		try {
			
			if ( automatica ){
				// Validar quantidade de alunos a ser transferidos
				validaInt(qtdMatriculados, "Quantidade de alunos a transferir", lista);
				
				// Validar a quantidade de alunos especificada em relação a quantidade de matriculas na turma de origem 
				if(qtdMatriculados > turmaSerieOrigem.getQtdMatriculados() )
					lista.addErro("O número de alunos a serem transferidos não pode ser maior" +
							" do que a quantidade de alunos matriculados na turma de origem.");
				
			} else {
				// Validar lista de discentes selecionados
				if ( (matriculas == null || matriculas.isEmpty()) ) {
					lista.addErro("É necessário selecionar pelo menos um discente para efetuar a transferência.");
				}
				
				qtdMatriculados = matriculas != null ? matriculas.size() : 0;
			}
			// long totalAlunosDestino = turmaDestino.getQtdMatriculados() + turmaDestino.getQtdEspera();
			
			// Validar a quantidade de alunos especificada em relação as vagas da turma de destino
			if( !ValidatorUtil.isEmpty(turmaSerieDestino.getCapacidadeAluno()) && qtdMatriculados > ( turmaSerieDestino.getCapacidadeAluno() - turmaSerieDestino.getQtdMatriculados() ) )
				lista.addErro("O número de alunos a serem transferido não pode ser maior" +
						" do que a quantidade de vagas disponíveis na turma de destino.");
			
		} finally {
			dao.close();
		}
	}
}
