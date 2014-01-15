/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
	 * Serve pra verificar se h� alguma refer�ncia de tipo Recurso f�sico associado a recurso Espa�o
	 * F�sico.
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