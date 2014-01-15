package br.ufrn.sigaa.arq.dao.sae;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioAtleta;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioCreche;

/**
 * DAO auxiliar responsável pela consulta de Bolsa Auxílio. 
 *
 */
public class BolsaAuxilioAuxiliarDao  extends GenericSigaaDAO {
	
	/**
	 * Método que busca uma Bolsa Auxílio do tipo Creche a partir do id da bolsa.
	 * @param idBolsaAuxilio Id da bolsa auxílio.
	 * @return Retorna (se existir) a bolsa auxílio.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public BolsaAuxilioCreche findBolsaAuxilioCrecheByIdBolsaAuxilio(int idBolsaAuxilio) throws HibernateException, DAOException {
		Query q = getSession().createQuery("from BolsaAuxilioCreche where bolsaAuxilio.id = ?");		
		return (BolsaAuxilioCreche) q.setInteger(0, idBolsaAuxilio).uniqueResult();	
	}

	/**
	 * Método que retorna uma Bolsa Auxílio do tipo Atleta a partir do id da bolsa.
	 * @param idBolsaAuxilio Id da bolsa auxílio.
	 * @return Retorna (se existir) a bolsa auxílio
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public BolsaAuxilioAtleta findBolsaAuxilioAtletaByIdBolsaAuxilio(int idBolsaAuxilio) throws HibernateException, DAOException {
		Query q = getSession().createQuery("from BolsaAuxilioAtleta where bolsaAuxilio.id = ?");		
		return (BolsaAuxilioAtleta) q.setInteger(0, idBolsaAuxilio).uniqueResult();	
	}

}