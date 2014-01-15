/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/04/2007
 *
 */
package br.ufrn.sigaa.arq.dao.eleicao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.eleicao.dominio.Voto;

/**
 * 
 * @author Victor Hugo
 * 
 */
public class VotoDao extends GenericSigaaDAO {

	/**
	 * retorna um voto dado um discente e uma eleição
	 * 
	 * @param eleicao
	 * @param discente
	 * @return
	 * @throws DAOException 
	 */
	public Voto findByEleicaoDiscente(int idEleicao, int idDiscente) throws DAOException {

		try {

			Criteria c = getSession().createCriteria(Voto.class);
			c.add(Expression.eq("eleicao.id", idEleicao ));
			c.add(Expression.eq("discente.id", idDiscente ));

			return (Voto) c.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e);
		}

	}
}
