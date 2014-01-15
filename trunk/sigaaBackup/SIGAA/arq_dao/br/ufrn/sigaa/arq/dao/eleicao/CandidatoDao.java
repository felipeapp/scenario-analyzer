/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/04/2007
 *
 */
package br.ufrn.sigaa.arq.dao.eleicao;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.eleicao.dominio.Candidato;

/**
 * 
 * @author Victor Hugo
 *
 */
public class CandidatoDao extends GenericSigaaDAO {

	/**
	 * retorna um candidato dado a chapa e a eleição
	 * @param chapa
	 * @param idEleicao
	 * @return
	 * @throws DAOException
	 */
	public Candidato findByChapaEleicao(Integer chapa, Integer idEleicao) throws DAOException{
		
		Criteria c = getSession().createCriteria(Candidato.class);
		c.add(Expression.eq("chapa", chapa));
		c.add(Expression.eq("eleicao.id", idEleicao));

		return (Candidato) c.uniqueResult();
	}
	
    public Collection<Candidato> findAll() throws DAOException {
    	Criteria c = getSession().createCriteria(Candidato.class);
    	
    	@SuppressWarnings("unused")
		Criteria c1 = c.createCriteria("eleicao");
    	
    	@SuppressWarnings("unchecked")
    	Collection<Candidato> lista = c.list();
    	return lista;

    }

}