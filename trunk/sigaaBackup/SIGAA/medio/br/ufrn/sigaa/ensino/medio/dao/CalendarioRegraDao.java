/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 09/09/2011
 * Autor: Arlindo Rodrigues
 */
package br.ufrn.sigaa.ensino.medio.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.medio.dominio.CalendarioRegra;

/**
 * Dao responsável por realizar consultas de Calendário de regras de notas (nível médio).
 * 
 * @author Arlindo Rodrigues
 *
 */
public class CalendarioRegraDao extends GenericSigaaDAO {
	
	/**
	 * Busca os calendários das regras de notas do calendário acadêmico informado.
	 * @param calendario
	 * @return
	 * @throws DAOException
	 */
	public Collection<CalendarioRegra> findByCalendario(CalendarioAcademico calendario) throws DAOException {
		String projecao = " cr.id, cr.calendario.id, cr.regra.id, cr.regra.titulo, cr.regra.ordem, " +
				" cr.dataInicio, cr.dataFim ";

		StringBuffer hql = new StringBuffer("select "+projecao);
		hql.append(" from CalendarioRegra cr ");
		hql.append(" inner join cr.calendario cal ");
		hql.append(" inner join cr.regra regra ");
		
		hql.append(" where cal.id = "+calendario.getId());
		
		hql.append(" order by regra.ordem ");
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<CalendarioRegra> lista =  (List<CalendarioRegra>) HibernateUtils.parseTo(q.list(), projecao, CalendarioRegra.class, "cr");
		
		return lista;
	}	

}
