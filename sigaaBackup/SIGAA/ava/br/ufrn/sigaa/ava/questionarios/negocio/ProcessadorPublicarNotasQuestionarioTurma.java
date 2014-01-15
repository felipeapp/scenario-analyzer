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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.questionario.dao.QuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.ConjuntoRespostasQuestionarioAluno;
import br.ufrn.sigaa.ava.questionarios.dominio.EnvioRespostasQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;

/**
 * Processador que envia as notas dos questionários à planilha de notas.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorPublicarNotasQuestionarioTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException  {
		
		MovimentoPublicarNotasQuestionarioTurma pMov = (MovimentoPublicarNotasQuestionarioTurma) mov;
		
		QuestionarioTurma questionario = pMov.getQuestionario();
		
		QuestionarioTurmaDao dao = null;
		TurmaVirtualDao tDao = null;
		
		try {
			dao = getDAO(QuestionarioTurmaDao.class, pMov);
			tDao = getDAO(TurmaVirtualDao.class, pMov);
			
			List<ConjuntoRespostasQuestionarioAluno> conjuntos = dao.findConjuntoRespostasByQuestionario(questionario);
			
			// Carrega as respostas do conjunto
			for ( ConjuntoRespostasQuestionarioAluno c : conjuntos ) {
				
				c.setQuestionario(questionario);
				List <EnvioRespostasQuestionarioTurma> todas = c.getRespostas();
				c.setRespostas(new ArrayList <EnvioRespostasQuestionarioTurma> ());

				boolean possuiFinalizadas = false;
				// Adiciona as respostas finalizadas.
				for (EnvioRespostasQuestionarioTurma e : todas){
					if ( e.isFinalizado()  ){
							c.getRespostas().add(e);
							possuiFinalizadas = true;
					}		
				}
				// Adiciona a última resposta não finalizada de cada usuário.
				// Deve ser a última, para não pegar respostas salvas devido o pool.
				if ( !possuiFinalizadas ){
					EnvioRespostasQuestionarioTurma ultima = null;
					
					for (EnvioRespostasQuestionarioTurma e : todas){
						// Verifica se o questionário ainda está sendo respondido					
						if ( !e.isFinalizado() && new Date().getTime() > e.getDataCadastro().getTime() + questionario.getDuracao() * 60 * 1000 ) {
							// Caso a duração do questionário tenha acabado, pega o último questionário
							if (ultima == null || (e.getDataCadastro().after(ultima.getDataCadastro())))
									ultima = e;
						}
					}
					if (ultima != null)
						c.getRespostas().add(ultima);					
				}
			}
			
			List <Avaliacao> notas = dao.findAvaliacoesByQuestionario(questionario);
					
			dao.updateField(QuestionarioTurma.class, questionario.getId(), "notasPublicadas", true);
			
			Map <Integer, Avaliacao> avaliacoesAAtualizar = new HashMap <Integer, Avaliacao> ();
			
			for (Avaliacao a : notas)
				avaliacoesAAtualizar.put(a.getUnidade().getMatricula().getDiscente().getPessoa().getId(), a);
			
			ConfiguracoesAva config = tDao.findConfiguracoes(questionario.getTurma());
			boolean somaNotas = config.isAvaliacoesSoma(questionario.getUnidade());	
			
			for (ConjuntoRespostasQuestionarioAluno c : conjuntos){
				
				c.calcularNotas();
				
				if (c.isDissertativasPendentes())
					throw new NegocioException ("É necessário corrigir todas as questões dissertativas do questionário");
				
				for ( EnvioRespostasQuestionarioTurma r : c.getRespostas() )
					dao.updateField(EnvioRespostasQuestionarioTurma.class, r.getId(), "porcentagem", r.getPorcentagem());
				dao.updateField(ConjuntoRespostasQuestionarioAluno.class, c.getId(), "porcentagem", c.getPorcentagem());

				Avaliacao a = avaliacoesAAtualizar.get(c.getUsuarioEnvio().getPessoa().getId());

				Double notaMaxima = null;
				if (a != null && somaNotas)
					notaMaxima = a.getNotaMaxima();
				else
					notaMaxima = 10.0;
					
				if (a != null){
					a.setNota((notaMaxima > 10 ? notaMaxima / 10 : notaMaxima) * c.getPorcentagem());
					dao.updateField(Avaliacao.class, a.getId(), "nota", a.getNota());
				}
			}
			
		} finally {
			if (dao != null)
				dao.close();
			if (tDao != null)
				tDao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
}
