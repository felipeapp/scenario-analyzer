/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 14/12/2011
 *
 */
package br.ufrn.sigaa.avaliacao.dao;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/** Classe responsável por consultas específicas ao {@link CalendarioAvaliacao Calendário de Avaliação}
 * @author Édipo Elder F. de Melo
 *
 */
public class CalendarioAvaliacaoDao extends GenericSigaaDAO {
	
	
	/** Verifica se há calendário ativo para responder para o tipo de avaliação informado.
	 * @param tipoAvaliacaoInstitucional
	 * @param ead
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean hasCalendarioAtivo(int tipoAvaliacaoInstitucional, Boolean ead) throws HibernateException, DAOException {
		String hql = "select count(*)" +
				" from CalendarioAvaliacao" +
				" where inicio <= :hoje" +
				" and fim >= :hoje" +
				" and ativo = true" +
				" and formulario.tipoAvaliacao = :tipoAvaliacao" +
				(ead != null ? " and formulario.ead = :ead" : "");
		Query q = getSession().createQuery(hql);
		q.setDate("hoje", new Date());
		q.setInteger("tipoAvaliacao", tipoAvaliacaoInstitucional);
		if (ead != null)
			q.setBoolean("ead", ead);
		long total = (Long) q.uniqueResult();
		if (total > 0 )
			return true;
		else
			return false;
	}
	
	/** Retorna o calendário ativo para responder para o tipo de avaliação informado.
	 * @param tipoAvaliacaoInstitucional
	 * @param ead
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public CalendarioAvaliacao findCalendarioAtivo(int tipoAvaliacaoInstitucional, boolean ead) throws HibernateException, DAOException {
		Date hoje = new Date();
		Criteria c = getSession().createCriteria(CalendarioAvaliacao.class);
		c.createCriteria("formulario")
				.add(Restrictions.eq("tipoAvaliacao", tipoAvaliacaoInstitucional))
				.add(Restrictions.eq("ead", ead))
				.createCriteria("grupoPerguntas").createCriteria("perguntas");
		c.add(Restrictions.le("inicio", hoje))
				.add(Restrictions.ge("fim", hoje))
				.add(Restrictions.eq("ativo", true));
		c.addOrder(Order.desc("inicio"));
		return (CalendarioAvaliacao) c.setMaxResults(1).uniqueResult();
	}

	/** Retorna os calendários em que o discente preencheu o formulário.
	 * @param discente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<CalendarioAvaliacao> findAllPreenchidoByDiscente(DiscenteAdapter discente) throws HibernateException, DAOException {
		String hql ="select calendario" +
				" from CalendarioAvaliacao calendario, AvaliacaoInstitucional avaliacao" +
				" where calendario.formulario.id = avaliacao.formulario.id" +
				" and calendario.ano = avaliacao.ano" +
				" and calendario.periodo = avaliacao.periodo" +
				" and avaliacao.discente.id = :idDiscente" +
				" order by calendario.ano desc, calendario.periodo desc, calendario.inicio desc, calendario.formulario.tipoAvaliacao";
		Query q = getSession().createQuery(hql);
		q.setInteger("idDiscente", discente.getId());
		@SuppressWarnings("unchecked")
		Collection<CalendarioAvaliacao> lista = q.list();
		return lista;
	}

	/** Retorna uma coleção de calendários que estão no período de preenchimento
	 * @param tipoAvaliacaoInstitucional
	 * @param ead
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<CalendarioAvaliacao> findAllPeriodoPreenchimentoAtivo(int tipoAvaliacaoInstitucional, Boolean ead) throws HibernateException, DAOException {
		String hql = " from CalendarioAvaliacao" +
				" where inicio <= :hoje" +
				" and fim >= :hoje" +
				" and ativo = true" +
				(tipoAvaliacaoInstitucional > 0 ? " and formulario.tipoAvaliacao = :tipoAvaliacao" : "") +
				(ead != null ? " and formulario.ead = :ead" : "") +
				" order by inicio desc";
		Query q = getSession().createQuery(hql);
		q.setDate("hoje", new Date());
		if (tipoAvaliacaoInstitucional > 0)
			q.setInteger("tipoAvaliacao", tipoAvaliacaoInstitucional);
		if (ead != null)
			q.setBoolean("ead", ead);
		@SuppressWarnings("unchecked")
		Collection<CalendarioAvaliacao> lista = q.list();
		return lista;
	}
}
