/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 26/01/2009
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.EditalBolsasReuni;

/**
 * DAO para consultas de editais de concess�o de bolsas
 * REUNI de assist�ncia ao ensino.
 * 
 * @author wendell
 *
 */
public class EditalBolsasReuniDao extends GenericSigaaDAO {

	/**
	 * Buscar todos os editais com per�odo de submiss�o aberto para novas propostas
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalBolsasReuni> findAbertosSubmissao() throws DAOException {
		Date hoje = CalendarUtils.descartarHoras(new Date());
			
		StringBuilder hql = new StringBuilder();
		hql.append(" from EditalBolsasReuni ");
		hql.append(" where dataInicioSubmissao <= ? and dataFimSubmissao >= ? ");
		hql.append(" order by dataFimSubmissao");
		
		return getHibernateTemplate().find(hql.toString(), new Object[] {hoje, hoje});
	}
	
	/**
	 * Buscar todos os editais com per�odo de sele��o de bolsistas aberto
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalBolsasReuni> findAbertosSelecao() throws DAOException {
		Date hoje = CalendarUtils.descartarHoras(new Date());
		
		StringBuilder hql = new StringBuilder();
		hql.append(" from EditalBolsasReuni ");
		hql.append(" where dataInicioSelecao <= ? and dataFimSelecao >= ? ");
		hql.append(" order by dataFimSelecao");
		
		return getHibernateTemplate().find(hql.toString(), new Object[] {hoje, hoje});
	}
	
}
