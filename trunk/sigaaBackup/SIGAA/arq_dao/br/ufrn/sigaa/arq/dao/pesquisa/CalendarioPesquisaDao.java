/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/06/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;

/**
 * Dao para controle do calendário de atividades de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class CalendarioPesquisaDao extends GenericSigaaDAO {

	public CalendarioPesquisa findVigente() throws DAOException {
        try {
        	return (CalendarioPesquisa) getSession().createCriteria(CalendarioPesquisa.class)
        		.add( Expression.eq("vigente", true))
        		.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

}
