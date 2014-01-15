/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe com m�todos de valida��o das regras de neg�cio da transfer�ncia de alunos
 * entre turmas de ensino m�dio.
 *
 * @author Rafael Gomes
 */
public class TransferenciaTurmasMedioValidator {

	/**
	 * Validar a situa��o da turma de origem dos alunos a serem transferidos
	 * 
	 * @param turma
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaTurmaSerieOrigem(TurmaSerie turmaSerieOrigem, ListaMensagens lista) throws DAOException{
		TurmaSerieDao tsDao = DAOFactory.getInstance().getDAO( TurmaSerieDao.class);
		
		try {
			turmaSerieOrigem.setQtdMatriculados(tsDao.findQtdeAlunosByTurma(turmaSerieOrigem));
			// Validar situa��o da turma
			if (!turmaSerieOrigem.isAtivo() ) {
				lista.addErro("Somente � poss�vel transferir alunos de turmas ativas.");
			}
			
			// Verificar se h� alunos matriculados ou com solicita��o pendente para a turma selecionada
			if( turmaSerieOrigem.getQtdMatriculados() == 0 ) {
				lista.addErro("A turma selecionada n�o possui alunos pass�veis de transfer�ncia.");
			}
			
		} finally {
			tsDao.close();
		}
	}

	/**
	 * Validar a situa��o da turma de destino dos alunos a serem transferidos
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
			
			// Validar situa��o da turma
			if( !turmaSerieDestino.isAtivo() ) {
				lista.addErro("S� � poss�vel realizar a transfer�ncia de alunos entre turmas ativas.");
			}
			
			// Validar capacidade da turma de destino em rela��o as matriculas ativas
			long totalMatriculasAtivas = turmaSerieDestino.getQtdMatriculados();
			
			if ( !ValidatorUtil.isEmpty(turmaSerieDestino.getCapacidadeAluno()) && (totalMatriculasAtivas >= turmaSerieDestino.getCapacidadeAluno()) ) {
				lista.addErro("N�o � poss�vel transferir alunos para turmas cuja capacidade de alunos j� foi atingida.");
			}
			
			// Verificar se a turma de destino � da mesma s�rie da turma de origem
			if( turmaSerieOrigem.getSerie().getId() != turmaSerieDestino.getSerie().getId() ){
				lista.addErro("A turma de destino deve ser da mesma s�rie da turma de origem");
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
				
				// Validar a quantidade de alunos especificada em rela��o a quantidade de matriculas na turma de origem 
				if(qtdMatriculados > turmaSerieOrigem.getQtdMatriculados() )
					lista.addErro("O n�mero de alunos a serem transferidos n�o pode ser maior" +
							" do que a quantidade de alunos matriculados na turma de origem.");
				
			} else {
				// Validar lista de discentes selecionados
				if ( (matriculas == null || matriculas.isEmpty()) ) {
					lista.addErro("� necess�rio selecionar pelo menos um discente para efetuar a transfer�ncia.");
				}
				
				qtdMatriculados = matriculas != null ? matriculas.size() : 0;
			}
			// long totalAlunosDestino = turmaDestino.getQtdMatriculados() + turmaDestino.getQtdEspera();
			
			// Validar a quantidade de alunos especificada em rela��o as vagas da turma de destino
			if( !ValidatorUtil.isEmpty(turmaSerieDestino.getCapacidadeAluno()) && qtdMatriculados > ( turmaSerieDestino.getCapacidadeAluno() - turmaSerieDestino.getQtdMatriculados() ) )
				lista.addErro("O n�mero de alunos a serem transferido n�o pode ser maior" +
						" do que a quantidade de vagas dispon�veis na turma de destino.");
			
		} finally {
			dao.close();
		}
	}
}
