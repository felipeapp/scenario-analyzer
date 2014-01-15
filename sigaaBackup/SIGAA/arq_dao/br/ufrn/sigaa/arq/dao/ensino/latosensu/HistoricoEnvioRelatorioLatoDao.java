package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.latosensu.dominio.HistoricoEnvioRelatorioLato;

public class HistoricoEnvioRelatorioLatoDao extends GenericSigaaDAO {

	public HistoricoEnvioRelatorioLato findByLastHistorico( int idRelatorio ) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(HistoricoEnvioRelatorioLato.class);
			c.add( Expression.eq("relatorio.id", idRelatorio));
			c.addOrder(Order.desc("dataCadastro"));
			c.setMaxResults(1);
			return (HistoricoEnvioRelatorioLato) c.uniqueResult();
		} catch( Exception e){
			throw new DAOException(e.getMessage(), e);
		}
	}
	
}