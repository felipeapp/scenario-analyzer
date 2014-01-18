/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/03/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.projetos.dominio.Edital;


/**
 * Dao para acesso aos dados dos editais de pesquisa.
 * @author Ricardo Wendell
 *
 */
public class EditalPesquisaDao extends GenericSigaaDAO {


	/**
	 * Busca todos os editais de pesquisa cujo período de submissão está aberto.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findAllAbertos() throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);

			Criteria c = getSession().createCriteria(EditalPesquisa.class);
			c.add(Restrictions.eq("distribuicaoCotas", Boolean.TRUE));
			c.createCriteria("edital")
				.add(Restrictions.le("inicioSubmissao", hoje))
				.add(Restrictions.ge("fimSubmissao", hoje))
				.add(Restrictions.eq("ativo", Boolean.TRUE))
				.addOrder(Order.desc("dataCadastro"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todos os editais de pesquisa ativos de acordo com o ano informado.
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findByAno(int ano) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(EditalPesquisa.class);
			c.createCriteria("edital")
			.add(Restrictions.ge("ano", ano))
			.add(Restrictions.eq("ativo", Boolean.TRUE));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todos os editais de pesquisa associados ao edital base informado.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findAllAssociados(Edital edital) throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);
			
			Criteria c = getSession().createCriteria(EditalPesquisa.class);
			c.createCriteria("edital")
			.add(Restrictions.eq("id", edital.getId()))
			.add(Restrictions.le("inicioSubmissao", hoje))
			.add(Restrictions.ge("fimSubmissao", hoje))
			.add(Restrictions.eq("ativo", Boolean.TRUE))
			.addOrder(Order.desc("dataCadastro"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca todos os editais voluntários de pesquisa cujo período de submissão está aberto.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findAllVoluntariosAbertos() throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);
			
			Criteria c = getSession().createCriteria(EditalPesquisa.class);
			c.add(Restrictions.eq("voluntario", Boolean.TRUE));
			c.createCriteria("edital")
				.add(Restrictions.le("inicioSubmissao", hoje))
				.add(Restrictions.ge("fimSubmissao", hoje))
				.add(Restrictions.eq("ativo", Boolean.TRUE));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna o edital de pesquisa aberto mais recentemente.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public EditalPesquisa findUltimoEditalDistribuicaoCotas() throws DAOException {

		Criteria c = getSession().createCriteria(EditalPesquisa.class);
		c.add(Restrictions.eq("distribuicaoCotas", true));
		c.createCriteria("edital")
			.add(Restrictions.eq("ativo", Boolean.TRUE))
			.addOrder(Order.desc("inicioSubmissao"));
		c.setMaxResults(1);

		List l = c.list();
		if (l.size() > 0) {
			return (EditalPesquisa) l.get(0);
		} else {
			return null;
		}

	}

	/**
	 * Busca todos os editais de pesquisa ativos (ou seja, os que não foram removidos na aplicação).
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findAllAtivos() throws DAOException {
		try {
			Criteria c = getSession().createCriteria(EditalPesquisa.class);
			c.createCriteria("edital").add(Restrictions.eq("ativo", Boolean.TRUE)).addOrder(Order.desc("dataCadastro"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todos os editais de pesquisa.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findAllEditais() throws DAOException{
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select id_edital_pesquisa, descricao from pesquisa.edital_pesquisa join projetos.edital using (id_edital)");
			hql.append(" order by data_cadastro desc");
			
			Query query = getSession().createSQLQuery(hql.toString());
			
			List<EditalPesquisa> editais = new ArrayList<EditalPesquisa>();
			
			List resultado = query.list();
			Iterator it = resultado.iterator();
			int indice = 0;
			
			while(it.hasNext()){
				EditalPesquisa edital = new EditalPesquisa();
				edital.setEdital(new Edital());
				indice++;
				Object[] colunas = (Object[]) it.next();
				edital.setId((Integer)colunas[0]);
				edital.getEdital().setDescricao((String)colunas[1]);
				editais.add(edital);
			}
			
			return editais;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	

	/**
	 * Busca todos os editais de pesquisa.
	 * @return
	 * @throws DAOException
	 */
	public Collection<EditalPesquisa> findByDataFimExecucao(int idEdital) throws DAOException{
			String projecao = "id, pessoa.nome, pessoa.email, matricula, curso.id, anoIngresso, periodoIngresso, status, nivel";
			Query q = getSession().createQuery("select " + projecao + " FROM EditalPesquisa ep" +
					" WHERE ep.id =: id ");
			q.setInteger("id", idEdital);
			
			List<Object[]> lista = q.list();
			return HibernateUtils.parseTo(lista, projecao, EditalPesquisa.class);
	}

	/**
	 * Busca todos os editais ativos, a partir do ano informado e de acordo com o tipo de edital 
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findByAtivoAnoTipoEdital(Integer ano, Boolean bolsa) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(EditalPesquisa.class);
			
			if (bolsa != null) 
				c.add(Restrictions.eq("distribuicaoCotas", bolsa));
			
			Criteria cEdital = c.createCriteria("edital");
			cEdital.add(Restrictions.eq("ativo", Boolean.TRUE));
			
			if (ano != null) 
				cEdital.add(Restrictions.eq("ano", ano));
			
			cEdital.addOrder(Order.desc("dataCadastro"));
			return c.list();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Serve para conferir se o código informado já existe.
	 * 
	 */
	public boolean hasCodigo(String codigo) throws DAOException{
		
		String hql = " SELECT COUNT(*) " +
					 " FROM  EditalPesquisa ep" +
					 " WHERE ep.codigo = :codigo" +
					 " AND ep.edital.ativo is true";
		
		Query q = getSession().createQuery(hql.toString());
		q.setString("codigo", codigo);
		
		Long qntDisciplinas = (Long) q.uniqueResult();

		return qntDisciplinas != 0;
	}

	/**
	 * Busca todos os editais de pesquisa cujo período de submissão está aberto e se é de distribuição de cota ou não.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findAllAbertosTipo(boolean distribuicaoCota) throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);
			
			Criteria c = getSession().createCriteria(EditalPesquisa.class)
					.add(Restrictions.eq("distribuicaoCotas", distribuicaoCota));
			c.createCriteria("edital")
			.add(Restrictions.le("inicioSubmissao", hoje))
			.add(Restrictions.ge("fimSubmissao", hoje))
			.add(Restrictions.eq("ativo", Boolean.TRUE))
			.addOrder(Order.desc("dataCadastro"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todos os editais de pesquisa de distribuição de cotas.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findAllDistribuicaoCotas() throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);
			
			Criteria c = getSession().createCriteria(EditalPesquisa.class)
					.add(Restrictions.eq("distribuicaoCotas", Boolean.TRUE));
			c.createCriteria("edital")
			.add(Restrictions.eq("ativo", Boolean.TRUE))
			.addOrder(Order.desc("dataCadastro"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todos os editais de pesquisa de apoio financeiro.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findAllApoioFinanceiro() throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);

			Criteria c = getSession().createCriteria(EditalPesquisa.class)
			.add(Restrictions.eq("distribuicaoCotas", Boolean.FALSE));
			c.createCriteria("edital")
				.add(Restrictions.eq("ativo", Boolean.TRUE))
				.addOrder(Order.desc("dataCadastro"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca um edital de pesquisa pelo identificador do edital mais geral.
	 * 
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 */
	public EditalPesquisa findByIdEdital(int idEdital) throws DAOException {
		Criteria c = getSession().createCriteria(EditalPesquisa.class);
		c.createCriteria("edital").add(Restrictions.eq("id", idEdital));
		c.setFetchMode("cota", FetchMode.JOIN);
		c.setFetchMode("cotas", FetchMode.JOIN);
		return (EditalPesquisa) c.uniqueResult();
	}
	
	/**
	 * Retorna os IDs das unidades de lotação de servidores docentes dos Campi Regionais (CERES e FACISA).
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findIdsUnidadesCampiRegionais() throws DAOException {
		return getSession()
				.createSQLQuery(
						"select distinct id_unidade " +
						"from rh.servidor join comum.unidade using (id_unidade) " +
						"where id_gestora = 1482 or id_unidade = 4890")
				.list();
	}
}