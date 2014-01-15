/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '28/07/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.espacofisico;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.espacofisico.dominio.RecursoEspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.TipoRecursoEspacoFisico;

public class RecursoEspacoFisicoDao extends GenericSigaaDAO {

	/**
	 * Serve pra verificar se há alguma referência de tipo Recurso físico associado a recurso Espaço
	 * Físico.
	 * 
	 * @param obj
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<RecursoEspacoFisico> findbyTipoRecurso(
			TipoRecursoEspacoFisico tipo) throws DAOException {
		Query query = getSession().createQuery("select recurso_espaco from RecursoEspacoFisico " +
				"recurso_espaco where recurso_espaco.tipo.id=:tipo");
		query.setInteger("tipo", tipo.getId());
		return query.list();
	}

}