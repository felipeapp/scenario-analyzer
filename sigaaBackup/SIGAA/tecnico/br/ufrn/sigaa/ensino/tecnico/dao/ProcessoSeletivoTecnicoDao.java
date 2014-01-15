/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;

/**
 * DAO utilizado para consultas específicas aos Processos Seletivos.
 * 
 * @author Édipo Elder
 * @author Fred_Castro
 * 
 */
public class ProcessoSeletivoTecnicoDao extends GenericSigaaDAO {

	/** Construtor padrão. */
	public ProcessoSeletivoTecnicoDao() {
	}

	/**
	 * Retorna uma coleção de Processos Seletivos que têm a inscrição de fiscais
	 * aberta na data atual.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivoTecnico> findInscricaoFiscalAtivo()
			throws DAOException {
		Date hoje = new Date();
		Criteria criteria = getSession().createCriteria(
				ProcessoSeletivoTecnico.class);
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
	public Collection<ProcessoSeletivoTecnico> findByInscricaoCandidatoAtivo()
			throws DAOException {
		Date hoje = new Date();
		Criteria criteria = getSession().createCriteria(
				ProcessoSeletivoTecnico.class);
		criteria.add(Restrictions.le("inicioInscricaoCandidato", hoje));
		criteria.add(Restrictions.ge("fimInscricaoCandidato", hoje));
		return criteria.list();
	}

	/** Retorna o Processo Seletivo que teve o último período de inscrição de fiscais.
	 * @return
	 * @throws DAOException
	 */
	public ProcessoSeletivoTecnico findUltimoPeriodoInscricaoFiscal()
			throws DAOException {
		Criteria criteria = getSession().createCriteria(
				ProcessoSeletivoTecnico.class);
		criteria.add(Restrictions.isNotNull("fimInscricaoFiscal"));
		criteria.addOrder(Order.desc("fimInscricaoFiscal"));
		return (ProcessoSeletivoTecnico) criteria.setMaxResults(1)
				.uniqueResult();
	}

	/** Retorna todos Processos Seletivos ordenados por ano/período de aplicação decrescente. 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProcessoSeletivoTecnico> findAllOrderByAnoPeriodo() throws DAOException {
		String hql = "from ProcessoSeletivoTecnico order by nome asc";
		Query q = getSession().createQuery(hql);
		return q.list();
	}
}
