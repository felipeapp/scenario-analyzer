package br.ufrn.sigaa.assistencia.dao;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;

public class AnoPeriodoReferenciaSAEDao extends GenericSigaaDAO {

	public AnoPeriodoReferenciaSAE anoPeriodoVigente() throws DAOException {
		String hql = "from AnoPeriodoReferenciaSAE anoPeriodo" +
				" where anoPeriodo.vigente = " + Boolean.TRUE;
		Query q = getSession().createQuery(hql);
		return (AnoPeriodoReferenciaSAE) q.uniqueResult();
	}
	
	public AnoPeriodoReferenciaSAE findCalendarioAnoPeriodo( int ano, int periodo ) throws DAOException {
		String hql = "from AnoPeriodoReferenciaSAE " +
				" where ativo = " + Boolean.TRUE + 
				" and ano = " + ano + "and periodo = " + periodo;
		Query q = getSession().createQuery(hql);
		return (AnoPeriodoReferenciaSAE) q.uniqueResult();
	}

}
