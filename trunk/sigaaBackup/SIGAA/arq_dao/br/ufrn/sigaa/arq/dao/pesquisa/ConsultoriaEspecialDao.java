/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '25/05/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.Collection;

import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ConsultoriaEspecial;

/**
 * Dao utilizado nas consultas relacionadas a consultorias especiais
 *
 * @author Ricardo Wendell
 *
 */
public class ConsultoriaEspecialDao extends GenericSigaaDAO {


	/**
	 * Buscar todas as consultorias cadastradas
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<ConsultoriaEspecial> findAll() throws DAOException {
		return findAll(ConsultoriaEspecial.class,
				new String[] {"consultor.areaConhecimentoCnpq.nome", "consultor.nome"},
				new String[] {"asc", "asc"});
	}

	public ConsultoriaEspecial findByConsultor(Consultor consultor) throws DAOException {
        try {
        	return (ConsultoriaEspecial) getSession().createCriteria(ConsultoriaEspecial.class)
        		.add( Expression.eq("consultor.id", consultor.getId()))
        		.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

}
