/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * DAO utilizado para consultas específicas aos Processos Seletivos.
 * 
 * @author Édipo Elder
 * 
 */
public class ProcessoSeletivoVestibularDao extends GenericSigaaDAO {

	/** Construtor padrão. */
	public ProcessoSeletivoVestibularDao() {
	}

	/**
	 * Retorna uma coleção de Processos Seletivos que têm a inscrição de fiscais
	 * aberta na data atual.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivoVestibular> findInscricaoFiscalAtivo()
			throws DAOException {
		Date hoje = new Date();
		Criteria criteria = getSession().createCriteria(
				ProcessoSeletivoVestibular.class);
		criteria.add(Restrictions.le("inicioInscricaoFiscal", hoje));
		criteria.add(Restrictions.ge("fimInscricaoFiscal", hoje));
		return criteria.list();
	}

	/** Retorna uma coleção de Processos Seletivos que têm a inscrição de candidatos
	 * aberta na data atual.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivoVestibular> findByInscricaoCandidatoAtivo()
			throws DAOException {
		Date hoje = new Date();
		Criteria criteria = getSession().createCriteria(
				ProcessoSeletivoVestibular.class);
		criteria.add(Restrictions.le("inicioInscricaoCandidato", hoje));
		criteria.add(Restrictions.ge("fimInscricaoCandidato", hoje));
		return criteria.list();
	}

	/** Retorna o Processo Seletivo que teve o último período de inscrição de fiscais.
	 * @return
	 * @throws DAOException
	 */
	public ProcessoSeletivoVestibular findUltimoPeriodoInscricaoFiscal()
			throws DAOException {
		Criteria criteria = getSession().createCriteria(
				ProcessoSeletivoVestibular.class);
		criteria.add(Restrictions.isNotNull("fimInscricaoFiscal"));
		criteria.addOrder(Order.desc("fimInscricaoFiscal"));
		return (ProcessoSeletivoVestibular) criteria.setMaxResults(1)
				.uniqueResult();
	}

	/** Retorna todos Processos Seletivos ordenados por ano/período de aplicação decrescente. 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivoVestibular> findAllOrderByAnoPeriodo() throws DAOException {
		String hql = "from ProcessoSeletivoVestibular" +
				" order by ano desc, periodo desc, nome asc";
		Query q = getSession().createQuery(hql);
		return q.list();
	}
}
