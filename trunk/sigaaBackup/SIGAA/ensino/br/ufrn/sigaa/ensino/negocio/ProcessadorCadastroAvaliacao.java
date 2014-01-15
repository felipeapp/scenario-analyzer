/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 22/09/2006
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.negocio.dominio.AvaliacaoMov;

/**
 * Processador para cadastrar avaliações de alunos
 * em uma turma.
 *
 * @author David Ricardo
 *
 */
public class ProcessadorCadastroAvaliacao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		AvaliacaoMov aMov = (AvaliacaoMov) mov;
		TurmaDao dao = null;
		AvaliacaoDao avaliacaoDao = null;
		Avaliacao avaliacao = aMov.getAvaliacao();

		try {
			
			dao = getDAO(TurmaDao.class, mov);
			avaliacaoDao = getDAO(AvaliacaoDao.class, mov);

			if (SigaaListaComando.CADASTRAR_AVALIACAO.equals(aMov.getCodMovimento())) {
				
				if (!avaliacaoDao.isNomeAvaliacaoDisponivel(aMov.getTurma(), avaliacao.getDenominacao(), avaliacao.getAbreviacao()))
					throw new NegocioException ("Esta turma já possui uma avaliação com descrição \""+avaliacao.getDenominacao()+"\" e abreviação \""+avaliacao.getAbreviacao()+"\".");
				
				/* Cadastro da avaliação para cada aluno matriculado */
				Collection<MatriculaComponente> matriculas = dao.findMatriculasAConsolidar(aMov.getTurma());
				for (MatriculaComponente matricula : matriculas) {
					NotaUnidade nota = matricula.getNotaByIndice(aMov.getUnidade());
					
					Avaliacao aval = new Avaliacao();
					aval.setNota(null);
					aval.setPeso(avaliacao.getPeso());
					aval.setAbreviacao(avaliacao.getAbreviacao());
					aval.setDenominacao(StringUtils.abbreviate(avaliacao.getDenominacao(), 100));
					aval.setUnidade(nota);
					aval.setNotaMaxima(avaliacao.getNotaMaxima());
					dao.create(aval);
				}
			} else {
				if ( avaliacao.getAtividadeQueGerou() != null && avaliacao.getEntidade() == 1)
					dao.updateField(TarefaTurma.class, avaliacao.getAtividadeQueGerou().getId(), "possuiNota", false);
				else if ( avaliacao.getAtividadeQueGerou() != null && avaliacao.getEntidade() == 2)
					dao.updateField(QuestionarioTurma.class, avaliacao.getAtividadeQueGerou().getId(), "possuiNota", false);
				// Busca avaliações de toda a turma de acordo com o id da primeira avaliação da lista
				List <Integer> avaliacoes = dao.findAvaliacoesByAvaliacaoInicial(avaliacao);
				avaliacaoDao.removerAvaliacoes(avaliacoes);
			}
			
		} finally {
			if (dao != null)
				dao.close();
			
			if (avaliacaoDao != null)
				avaliacaoDao.close();
		}

		return null;
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {


	}

}
