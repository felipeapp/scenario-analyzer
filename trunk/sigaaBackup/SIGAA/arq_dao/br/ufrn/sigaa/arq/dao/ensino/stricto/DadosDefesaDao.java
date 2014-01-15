/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/03/2008
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p>Dao para recuperar informações sobre dados de defesa </p>
 * 
 * @author jadson
 *
 */
public class DadosDefesaDao extends GenericSigaaDAO {


	/**
	 * Retorna todos os dados das defesas do discente passado ordenado por título.
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<DadosDefesa> findByDiscente(DiscenteStricto discente) throws DAOException  {
		try {
			Criteria c = getSession().createCriteria(DadosDefesa.class);
			c.add(Expression.eq("discente", discente));
			c.addOrder(Order.desc("titulo"));
			return  c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todos os dados da defesa mais recente do discente passado 
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public DadosDefesa findMaisRecenteByDiscente(DiscenteStricto discente) throws DAOException  {
		try {
			Criteria c = getSession().createCriteria(DadosDefesa.class);
			c.add(Expression.eq("discente", discente));
			c.addOrder(Order.desc("dataCadastro"));
			c.setMaxResults(1);
			return  (DadosDefesa) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna todos os dados das bancos de pós-graduação a partir do discente 
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public ArrayList<BancaPos> findDadosDefesaByDiscente(DiscenteAdapter discente) throws DAOException  {
		try {
			String hql = "select bancaPos from BancaPos bancaPos " +
						 "inner join bancaPos.dadosDefesa dadosDefesa " +
						 "where dadosDefesa.discente.id = " + discente.getId();
			
			return (ArrayList<BancaPos>) getSession().createQuery(hql).list();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/**
	 *  Recupera <strong>apenas</strong> o título da defesa e nome de quem a  defedeu.
	 *
	 * @param idDadosDefesa
	 * @return
	 * @throws DAOException
	 */
	public DadosDefesa findTituloNomeDiscente(int idDadosDefesa) throws DAOException  {
		try {
			String hql = " SELECT dadosDefesa.titulo, dadosDefesa.discente.discente.pessoa.nome " +
						 " FROM DadosDefesa dadosDefesa " +
						 " WHERE dadosDefesa.id = " + idDadosDefesa;
			Query q = getSession().createQuery(hql);
			
			Object[] temp = (Object[] ) q.uniqueResult();
			
			DadosDefesa dadosDefesa = new DadosDefesa();
			dadosDefesa.setId(idDadosDefesa);
			dadosDefesa.setTitulo((String)temp[0]);
			
			DiscenteStricto ds = new DiscenteStricto();
			Discente d = new Discente();
			Pessoa p = new Pessoa(0, (String) temp[1]);
			d.setPessoa( p);
			ds.setDiscente(d );
			dadosDefesa.setDiscente( ds );
			
			return dadosDefesa;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

}

