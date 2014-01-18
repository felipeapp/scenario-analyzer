package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;

public class CalendarioMonitoriaDao extends GenericSigaaDAO {

	public Collection<CalendarioMonitoria> findCalendariosAtivosMonitoria() throws DAOException {
		Criteria c = getSession().createCriteria(CalendarioMonitoria.class);
		c.add(Restrictions.eq("ativo", true));
		c.addOrder(Order.desc("anoReferencia"));
		return c.list();
	}
	
}