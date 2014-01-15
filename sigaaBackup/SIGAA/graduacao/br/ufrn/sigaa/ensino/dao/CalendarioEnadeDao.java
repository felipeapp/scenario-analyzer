/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/11/2011
 *
 */
package br.ufrn.sigaa.ensino.dao;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.CalendarioEnade;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoENADE;

/** Classe responsável por consultas específicas ao Calendário Enade.
 * @author Édipo Elder F. de Melo
 *
 */
public class CalendarioEnadeDao extends GenericSigaaDAO {

	/**
	 * @param class1
	 * @param paginacao
	 * @param ano
	 * @param tipoEnade
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<CalendarioEnade> findAll(PagingInformation paginacao, int ano, TipoENADE tipoEnade) throws HibernateException, DAOException {
		Criteria c = getCriteria(CalendarioEnade.class);
		if (ano > 0)
			c.add(Restrictions.eq("ano", ano));
		if (tipoEnade != null)
			c.add(Restrictions.eq("tipoEnade", tipoEnade));
		
		c.setCacheable(true);
		if (paginacao != null) {
			c.setFirstResult(paginacao.getPaginaAtual() * paginacao.getTamanhoPagina());
			c.setMaxResults(paginacao.getTamanhoPagina());
		}
		@SuppressWarnings("unchecked")
		Collection<CalendarioEnade> lista = c.list();
		return lista;
	}

}
