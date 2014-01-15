/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * DAO utilizado para consultas espec�ficas aos Processos Seletivos.
 * 
 * @author �dipo Elder
 * @author Fred_Castro
 * 
 */
public class ProcessoSeletivoTecnicoDao extends GenericSigaaDAO {

	/** Construtor padr�o. */
	public ProcessoSeletivoTecnicoDao() {
	}

	/**
	 * Retorna uma cole��o de Processos Seletivos que t�m a inscri��o de fiscais
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

	/** Retorna uma cole��o de Processos Seletivos que t�m a inscri��o de candidatos
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

	/** Retorna o Processo Seletivo que teve o �ltimo per�odo de inscri��o de fiscais.
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

	/** Retorna todos Processos Seletivos ordenados por ano/per�odo de aplica��o decrescente. 
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
