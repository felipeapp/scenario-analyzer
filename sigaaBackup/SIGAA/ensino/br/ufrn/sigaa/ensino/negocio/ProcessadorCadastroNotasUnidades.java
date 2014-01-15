/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/01/2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ensino.dao.CadastroNotasUnidadesDao;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.util.TurmaUtil;

/**
 * Processador para cadastro de notas para cada matr�cula em uma turma.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorCadastroNotasUnidades extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException {

		CadastroNotasUnidadesDao cadastroNUDao = getDAO(CadastroNotasUnidadesDao.class, mov);

		try {
			MovimentoCadastro cMov = (MovimentoCadastro) mov;
			Turma turma = (Turma) cMov.getObjMovimentado();
			@SuppressWarnings("unchecked")
			Collection<MatriculaComponente> matriculas = (Collection<MatriculaComponente>) cMov.getColObjMovimentado();
			
			List<Integer> listaNumeroUnidades = cadastroNUDao.countNotaUnidadeByTurma(turma);

			// Se a turma n�o estiver aberta ou se possuir matr�culas consolidados, n�o deve alterar a quantidade de unidades e avalia��es
			// Caso n�o exista unidades na turma e exista matr�culas consolidadas � necess�rio cadastrar as unidades para turma poder ser consolidada
			if (!turma.isAberta() || (cadastroNUDao.existeMatriculasConsolidadas(turma) && !listaNumeroUnidades.contains(0)))
				return matriculas;
						
			Integer numUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
			
			List<MatriculaComponente> notasARemover = new ArrayList<MatriculaComponente>();
			List<MatriculaComponente> notasACadastrar = new ArrayList<MatriculaComponente>();
			
			// Se a lista contem mais de 1 elemento � porque na turma existem alunos com diferentes quantidades de avalia��es
			// Se a lista n�o cont�m o n�mero de unidades corretos � porque todos na turma est�o com a quantidade de unidades diferentes do correto.
			if (listaNumeroUnidades.size() > 1 || !listaNumeroUnidades.contains(numUnidades)) {
				for (MatriculaComponente matricula : matriculas) {
					
					if (!matricula.isConsolidada() && matricula.getNotas().size() > numUnidades) 
						notasARemover.add(matricula);
					else if (!matricula.isConsolidada() && matricula.getNotas().size() < numUnidades) 
						notasACadastrar.add(matricula);
				}

			}
			
			if (!isEmpty(notasARemover))
				inativarNotasAndAtividades(mov, numUnidades, notasARemover);
			
			if (!isEmpty(notasACadastrar))
				criarNotas(mov, numUnidades, notasACadastrar, turma);
			
			return matriculas;
		} finally {
			cadastroNUDao.close();
		}
		
	}

	/**
	 * Remove as notas excedentes com base no n�mero de unidades informados
	 * 
	 * @param notaUnidadeDao
	 * @param numUnidades
	 * @param matriculas2 
	 * @param matricula
	 * @throws DAOException
	 */
	private void inativarNotasAndAtividades(Movimento mov, Integer numUnidades, List<MatriculaComponente> notasARemover) throws DAOException {
		// deleta do banco
		CadastroNotasUnidadesDao nuDao = getDAO(CadastroNotasUnidadesDao.class, mov);
		AvaliacaoDao avalDao = getDAO(AvaliacaoDao.class, mov);
		
		try {
			nuDao.inativarNotasUnidadesByMatriculaAndUnidade(notasARemover, numUnidades);

			
			for (MatriculaComponente matricula : notasARemover) {
				// deleta da cole��o
				for (Iterator<NotaUnidade> it = matricula.getNotas().iterator(); it.hasNext();) {
					NotaUnidade nota = it.next();
					
					if (nota.getUnidade() > numUnidades) {
						if (nota.getAvaliacoes() != null) {
							for (Avaliacao aval : nota.getAvaliacoes()) {
								if (aval.getAtividadeQueGerou() != null && aval.getAtividadeQueGerou().isPossuiNota()) {
									aval.getAtividadeQueGerou().setPossuiNota(false);
									nuDao.updateField(aval.getAtividadeQueGerou().getClass(), aval.getAtividadeQueGerou().getId(), "possuiNota", Boolean.FALSE);
								}
							}
						}
						it.remove();
					}
				}
			}
			
			avalDao.deleteAvaliacaoByMatricula(notasARemover, numUnidades);
			
		} finally {
			nuDao.close();
			avalDao.close();
		}
		
	}

	/**
	 * Cria as notas para uma matricula de acordo com o n�mero de unidades informado
	 * 
	 * @param notaUnidadeDao
	 * @param numUnidades
	 * @param matricula
	 * @param turma
	 * @throws DAOException
	 */
	private void criarNotas(Movimento mov, Integer numUnidades, List<MatriculaComponente> notasACadastrar, Turma turma) throws DAOException {
		
		CadastroNotasUnidadesDao nuDao = getDAO(CadastroNotasUnidadesDao.class, mov);
		AvaliacaoDao aDao = getDAO(AvaliacaoDao.class, mov);
		
		try {
			
			ArrayList<Avaliacao> avaliacoesDistintas = (ArrayList<Avaliacao>) aDao.findAvaliacoesDistintasByTurma(turma);
			
			for (MatriculaComponente matricula : notasACadastrar) {
				for (int unidade = matricula.getNotas().size() + 1; unidade <= numUnidades; unidade++) {
					NotaUnidade notaUnidade = new NotaUnidade();
					notaUnidade.setMatricula(matricula);
					notaUnidade.setUnidade((byte) (unidade));
					notaUnidade.setAtivo(true);
					nuDao.create(notaUnidade);
					
					matricula.getNotas().add(notaUnidade);
					
					criarAvaliacoes(aDao, avaliacoesDistintas, unidade, notaUnidade);						
				}
			}
		} finally {
			nuDao.close();
			aDao.close();
		}
		
		
	}

	/**
	 * Cria as avalia��es de uma unidade que est� sendo criada.
	 * 
	 * @param aDao
	 * @param avaliacoesDistintas
	 * @param unidade
	 * @param notaUnidade
	 * @throws DAOException
	 */
	private void criarAvaliacoes(AvaliacaoDao aDao,	ArrayList<Avaliacao> avaliacoesDistintas, int unidade, NotaUnidade notaUnidade) throws DAOException {
		
		if (!isEmpty(avaliacoesDistintas))
			for ( Avaliacao avaliacaoOutroAluno : avaliacoesDistintas )
				if ( unidade == avaliacaoOutroAluno.getUnidade().getUnidade() ){
									
					Avaliacao a = UFRNUtils.deepCopy(avaliacaoOutroAluno);
					a.setId(0);
					a.setUnidade(notaUnidade);
					if ( avaliacaoOutroAluno.getAtividadeQueGerou() != null )
						a.getAtividadeQueGerou().setId(avaliacaoOutroAluno.getAtividadeQueGerou().getId());
					aDao.create(a);

				}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		

	}
	
}
