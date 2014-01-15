/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/10/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.LevantamentoBibliograficoInfra;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.Linguas;

/**
 * DAO responsável pelas buscas de Solicitações de Levantamento Bibliográfico
 * 
 * @author agostinho
 *
 */
public class LevantamentoBibliograficoInfraDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as solicitações de levantamento bibliográfico, de acordo com os parâmetros de entrada.
	 * 
	 * @param biblioteca - A biblioteca responsável pelas solicitações.
	 * @param situacao - A situação atual da solicitação. ZERO para ignorar. -1 para AGUARDANDO_VALIDACAO ou VALIDADA
	 * @param ifra - Se as solicitações são de infra-estrutura ou não. 1 para infra; 2 para bibliográfico; ZERO para ignorar.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <LevantamentoBibliograficoInfra> findAllSolicitacoesLevantamento(
			Biblioteca biblioteca, int situacao, int infra) throws HibernateException, DAOException {
		
		Criteria c = getSession().createCriteria(LevantamentoBibliograficoInfra.class);
		
		if (biblioteca != null && biblioteca.getId() > 0)
			c.add(Restrictions.eq("bibliotecaResponsavel.id", biblioteca.getId()));
		
		if (situacao > 0)
			c.add(Restrictions.eq("situacao", situacao));
		if (situacao == -1)
			c.add(Restrictions.ne("situacao", LevantamentoBibliograficoInfra.SITUACAO_FINALIZADA));
		
		if (infra == 1)
			c.add(Restrictions.eq("infra", true));
		else if (infra == 2)
			c.add(Restrictions.eq("infra", false));
		
		c.addOrder(Order.desc("dataSolicitacao"));
		
		c.setFetchMode("pessoa", FetchMode.JOIN);
		c.setFlushMode(FlushMode.ALWAYS);
		
		@SuppressWarnings("unchecked")
		List <LevantamentoBibliograficoInfra> lista = c.list();
		
		return lista;
	}
	
	public LevantamentoBibliograficoInfra findSolicitacoesLevantamentoByID(int levantamentoBibliografico)
			throws HibernateException, DAOException {
		
		String hql =
					"SELECT levant " +
					"FROM " +
					"	LevantamentoBibliograficoInfra levant " +
					"	LEFT JOIN levant.linguasSelecionadas lng " +
					"	INNER JOIN levant.pessoa p " +
					"WHERE levant.id = " + levantamentoBibliografico + " " +
					"ORDER BY levant.dataSolicitacao DESC";
		
		return (LevantamentoBibliograficoInfra) getSession().createQuery(hql).uniqueResult();
	}

	public List<Linguas> findAllLinguas() throws HibernateException, DAOException {
		String hql = "SELECT linguas FROM Linguas linguas ORDER BY linguas.id";
		
		@SuppressWarnings("unchecked")
		List <Linguas> lista = getSession().createQuery(hql).list();
		return lista;
	}

	
	/**
	 * Exibe todas as solicitações de levantamento individual da pessoa passada.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<LevantamentoBibliograficoInfra> findAllSolicitacoesLevantamentoByIDPessoa(int idPessoa)
			throws DAOException {
		
		Criteria c = getSession().createCriteria(LevantamentoBibliograficoInfra.class);
		c.add(Restrictions.eq("pessoa.id", idPessoa));
		c.addOrder(Order.asc("dataSolicitacao"));
		
		@SuppressWarnings("unchecked")
		List <LevantamentoBibliograficoInfra> lista = c.list();
		return lista;
	}

}
