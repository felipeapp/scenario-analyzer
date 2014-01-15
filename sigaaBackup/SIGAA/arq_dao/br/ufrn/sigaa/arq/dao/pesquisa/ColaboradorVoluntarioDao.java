/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '02/04/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.ColaboradorVoluntario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao para consultas a Colaboradores Voluntários
 *
 * @author leonardo
 *
 */
public class ColaboradorVoluntarioDao extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public Collection<ColaboradorVoluntario> findColaboradoresAtivos() throws DAOException {
        try {
        	return getSession().createCriteria(ColaboradorVoluntario.class)
        		.add( Expression.eq("ativo", true)).list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	public boolean isColaboradorVoluntario(Servidor servidor) throws DAOException {
		Criteria c = getSession().createCriteria(ColaboradorVoluntario.class);
		c.add(Expression.eq("ativo", true));
		c.add(Expression.eq("servidor", servidor));
		return !c.list().isEmpty();
	}
}
