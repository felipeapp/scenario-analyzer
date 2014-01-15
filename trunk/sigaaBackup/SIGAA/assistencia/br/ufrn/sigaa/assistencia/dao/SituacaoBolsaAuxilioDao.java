package br.ufrn.sigaa.assistencia.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;

/**
 * Dao responsável pelas consultas das situações de bolsa auxílio 
 *  
 * @author Jean Guerethes
 */
public class SituacaoBolsaAuxilioDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as situações a qual foi solicitada. 
	 * 
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<SituacaoBolsaAuxilio> findSituacoes(Integer... situacoes) throws DAOException {
		Criteria c = getSession().createCriteria(SituacaoBolsaAuxilio.class);
		c.add(Restrictions.in("id", situacoes));
		return c.list();
	}

}