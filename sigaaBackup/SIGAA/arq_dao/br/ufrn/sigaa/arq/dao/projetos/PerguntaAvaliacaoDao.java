/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2010'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.PerguntaAvaliacao;
/**
 * Dao responsável por consultas de perguntas de avaliações
 * @author geyson
 *
 */
public class PerguntaAvaliacaoDao extends GenericSigaaDAO{
	
	/**
	 * Verifica se uma pergunta já esta ligada a algum questionario de avaliação.
	 * @param pergunta
	 * @return
	 * @throws DAOException
	 */
	public PerguntaAvaliacao findPerguntaExistenteQuestionario(PerguntaAvaliacao pergunta) throws DAOException {
		
		String hql = " select p from ItemAvaliacaoProjeto it " +
		" inner join it.pergunta p " +
		" inner join it.questionario quest" +
		" where  it.ativo = trueValue() " +
		" and p.id = " + pergunta.getId() + " ";

		Query query = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<PerguntaAvaliacao> lista = query.list();
		
		for (PerguntaAvaliacao perguntaAvaliacao : lista) {
			if(perguntaAvaliacao != null)
				return perguntaAvaliacao;
		}
		
		return null;
	}
	

}
