/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '20/04/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.espacofisico;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.espacofisico.dominio.TipoEspacoFisico;

public class TipoEspacoDAO extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public Collection<TipoEspacoFisico> findAtivos() throws DAOException {
		String hql = "FROM TipoEspacoFisico WHERE (ativo = trueValue() or ativo is null)"+
				" order by denominacao asc";

		return getSession().createQuery(hql).list();
	}

}
