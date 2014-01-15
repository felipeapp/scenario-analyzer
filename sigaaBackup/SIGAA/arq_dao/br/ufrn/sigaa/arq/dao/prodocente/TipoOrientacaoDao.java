/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/11/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacao;

public class TipoOrientacaoDao extends GenericSigaaDAO {


	public TipoOrientacao findByNivelEnsino(char nivel) throws DAOException  {
		try {
			Criteria c = getSession().createCriteria(TipoOrientacao.class);
			c.add(Expression.eq("nivelEnsino", nivel));
			Collection<TipoOrientacao> tipos = c.list();
			if (tipos != null) {
				return tipos.iterator().next();
			}
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

}
