/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/01/2011
 *
 */
package br.ufrn.sigaa.ava.questionarios.negocio;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.questionario.dao.PerguntaQuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.AlternativaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;

/**
 * Processador que adiciona ao questionário todas as perguntas selecionadas do banco.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorAdicionarPerguntasDoBanco extends AbstractProcessador {

	public Object execute(Movimento mov) throws DAOException, NegocioException  {

		MovimentoAdicionarPerguntasDoBanco pMov = (MovimentoAdicionarPerguntasDoBanco) mov;
		
		QuestionarioTurma questionario = pMov.getQuestionario();
		
		PerguntaQuestionarioTurmaDao dao = null;
		
		try {
			dao = getDAO(PerguntaQuestionarioTurmaDao.class, pMov);
			
			int ordem = questionario.getPerguntas().size() + 1;
			
			for (PerguntaQuestionarioTurma p : pMov.getPerguntas()){
				
				PerguntaQuestionarioTurma pClone = new PerguntaQuestionarioTurma();
				
				try {
					BeanUtils.copyProperties(pClone, p);
				
					pClone.setId(0);
					pClone.setQuestionario(questionario);
					pClone.setOrdem(ordem++);
					pClone.setDono(null);
					pClone.setCategoria(null);
					pClone.setDataCadastro(null);
					pClone.setRegistroCadastro(null);
					
					if (pClone.getAlternativas() != null){
						List <AlternativaPerguntaQuestionarioTurma> alternativas = new ArrayList<AlternativaPerguntaQuestionarioTurma>();

						for (AlternativaPerguntaQuestionarioTurma a : pClone.getAlternativas()){
							AlternativaPerguntaQuestionarioTurma aux = new AlternativaPerguntaQuestionarioTurma();
							BeanUtils.copyProperties(aux,a);
							aux.setId(0);
							aux.setPergunta(pClone);
							alternativas.add(aux);
						}
						
						pClone.setAlternativas(alternativas);
					}
				
				} catch (IllegalAccessException e) {
					throw new NegocioException (e);
				} catch (InvocationTargetException e) {
					throw new NegocioException (e);
				}
				
				dao.create(pClone);
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}