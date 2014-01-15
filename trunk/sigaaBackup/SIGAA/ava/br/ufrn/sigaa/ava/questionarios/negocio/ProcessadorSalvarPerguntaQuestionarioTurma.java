/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/01/2011
 *
 */
package br.ufrn.sigaa.ava.questionarios.negocio;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.questionarios.dominio.AlternativaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.CategoriaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;

/**
 * Processador que salva uma pergunta de question�rio da turma virtual, removendo suas alternativas marcadas para serem removida.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorSalvarPerguntaQuestionarioTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws DAOException  {

		MovimentoSalvarPerguntaQuestionarioTurma pMov = (MovimentoSalvarPerguntaQuestionarioTurma) mov;
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO(pMov);
			
			// Se o docente est� salvando uma pergunta no question�rio e optou por adicion�-la em uma categoria, salva uma c�pia da pergunta na categoria.
			if (pMov.getPergunta().getCategoria().getId() > 0 && !pMov.isAdicionarEmCategoria()){
				
				// Clona a pergunta
				PerguntaQuestionarioTurma p = new PerguntaQuestionarioTurma();
				BeanUtils.copyProperties(p, pMov.getPergunta());
				
				p.setId(0);
				p.setQuestionario(null);
				
				// Clona as alternativas da pergunta
				if (pMov.getPergunta().getAlternativas() != null){
					p.setAlternativas(new ArrayList <AlternativaPerguntaQuestionarioTurma> ());
					
					for (AlternativaPerguntaQuestionarioTurma alternativa : pMov.getPergunta().getAlternativas()){
						AlternativaPerguntaQuestionarioTurma a = new AlternativaPerguntaQuestionarioTurma();
						BeanUtils.copyProperties(a, alternativa);
						
						a.setId(0);
						a.setPergunta(p);
						p.getAlternativas().add(a);
					}
				
				}
				
				// Salva a nova pergunta na categoria selecionada.
				dao.create(p);
			}
			
			// Se n�o for o caso de uso de adicionar em categoria, indica que a pergunta n�o tem categoria
			if (!pMov.isAdicionarEmCategoria()){
				pMov.getPergunta().setCategoria(null);
				pMov.getPergunta().setDono(null);
			}
			else {
				CategoriaPerguntaQuestionarioTurma c = dao.findByPrimaryKey(pMov.getPergunta().getCategoria().getId(), CategoriaPerguntaQuestionarioTurma.class);
				pMov.getPergunta().setCategoria(c);
			}
			// Salva a pergunta
			dao.createOrUpdate(pMov.getPergunta());
			
			// Remove (desativa) as alternativas marcadas para remo��o
			if (pMov.getAlternativasRemover() != null)
				for (AlternativaPerguntaQuestionarioTurma a : pMov.getAlternativasRemover())
					dao.updateField(AlternativaPerguntaQuestionarioTurma.class, a.getId(), "ativo", false);
			
		} catch (IllegalAccessException e) {
			throw new DAOException (e);
		} catch (InvocationTargetException e) {
			throw new DAOException (e);
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;

	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
