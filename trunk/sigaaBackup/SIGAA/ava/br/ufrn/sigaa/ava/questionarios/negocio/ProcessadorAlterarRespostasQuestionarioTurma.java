/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/01/2011
 *
 */
package br.ufrn.sigaa.ava.questionarios.negocio;

import java.util.ArrayList;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.questionario.dao.QuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.ConjuntoRespostasQuestionarioAluno;
import br.ufrn.sigaa.ava.questionarios.dominio.EnvioRespostasQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;

/**
 * Processador que realiza uma alteração no envio das respostas de um discente a um questionário, indicando que o mesmo ficará com pendência de publicação de notas.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorAlterarRespostasQuestionarioTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException  {
		
		if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_RESPOSTAS_QUESTIONARIO_TURMA)) {
			return atualizarRespostas(mov);
		} else {
			return removerRespostas(mov);
		}	
	}

	public Object atualizarRespostas (Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro pMov = (MovimentoCadastro) mov;
		
		EnvioRespostasQuestionarioTurma resposta = pMov.getObjMovimentado();
		
		QuestionarioTurmaDao dao = null;
		
		try {
			dao = getDAO(QuestionarioTurmaDao.class, pMov);
			
			dao.update(resposta);
			// Desmarca o questionário para o docente saber que tem que publicar as notas novamente.
			dao.updateField(QuestionarioTurma.class, resposta.getQuestionario().getId(), "notasPublicadas", false);
			
			ConjuntoRespostasQuestionarioAluno conjunto = dao.findConjuntoRespostas(resposta.getConjuntoRespostas().getId());
			
			if (conjunto == null){
				conjunto = new ConjuntoRespostasQuestionarioAluno();
				conjunto.setQuestionario(resposta.getQuestionario());
				conjunto.setUsuarioEnvio((Usuario) pMov.getUsuarioLogado());
				conjunto.setRespostas(new ArrayList<EnvioRespostasQuestionarioTurma>());
				
				dao.create(conjunto);
				
				resposta.setConjuntoRespostas(conjunto);
				resposta.setAtivo(true);
				
				dao.update(resposta);
			}
			
			// Atualiza a resposta
			if ( conjunto.getRespostas() != null ) {
				
				if (conjunto.getRespostas().isEmpty() )
					conjunto.getRespostas().add(resposta);
				else {
					conjunto.getRespostas().remove(resposta);
					conjunto.getRespostas().add(resposta);
				}
			}
			
			resposta.setConjuntoRespostas(conjunto);
			resposta.getConjuntoRespostas().calcularNotas();
			dao.updateField(ConjuntoRespostasQuestionarioAluno.class, resposta.getConjuntoRespostas().getId(), "porcentagem", resposta.getConjuntoRespostas().getPorcentagem());
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}
	
	private Object removerRespostas(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro rMov = (MovimentoCadastro) mov;
		
		EnvioRespostasQuestionarioTurma resposta = rMov.getObjMovimentado();
		
		QuestionarioTurmaDao dao = null;
		AvaliacaoDao aDao = null;
		
		try {
			dao = getDAO(QuestionarioTurmaDao.class, rMov);		
			aDao = getDAO(AvaliacaoDao.class, rMov);	
			
			ArrayList<Avaliacao> avaliacoes = aDao.findByRespostaQuestionario(resposta);
			
			// Desmarca o questionário para o docente saber que tem que publicar as notas novamente.
			dao.updateField(QuestionarioTurma.class, resposta.getQuestionario().getId(), "notasPublicadas", false);
			if ( avaliacoes != null )
				for ( Avaliacao avaliacao : avaliacoes )
					dao.updateField(Avaliacao.class, avaliacao.getId(), "nota", null);
			
			dao.update(resposta);
		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
}
