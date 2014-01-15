/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/09/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * Classe respons�vel por consultas espec�ficas � {@link AreaConhecimentoCnpq
 * �rea de Conhecimento CNPq}.
 * 
 * @author Victor Hugo
 * 
 */
public class AreaConhecimentoCnpqDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as grande areas do CNPq.
	 */
	public Collection<AreaConhecimentoCnpq> findGrandeAreasConhecimentoCnpq() throws DAOException {
		Criteria criteria = getCriteria(AreaConhecimentoCnpq.class);
		criteria.add(Restrictions.isNull("codigo"));
		criteria.add(Restrictions.gt("id", 0));
		criteria.add(Restrictions.eq("excluido", false));
		criteria.addOrder(Order.asc("nome"));
		
		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista = criteria.list();
		return lista;
	}

	/**
	 * Retorna as �reas de conhecimento do CNPq.
	 */
	public Collection<AreaConhecimentoCnpq> findAreas(AreaConhecimentoCnpq grande) throws DAOException {
		Criteria criteria = getCriteria(AreaConhecimentoCnpq.class);
		if (!isEmpty(grande)) {
			criteria.add(Restrictions.eq("grandeArea", grande));
			criteria.add(Restrictions.ne("id", grande.getId()));
		} else {
			criteria.add(Restrictions.neProperty("id", "grandeArea.id"));
		}
		criteria.add(Restrictions.isNull("subarea"));
		criteria.addOrder(Order.asc("nome"));

		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista = criteria.list();
		return lista;
	}

	/**
	 * Retorna as sub-�reas de conhecimento do CNPq.
	 */
	public Collection<AreaConhecimentoCnpq> findSubAreas(int codigo) throws DAOException {
		Query q = getSession().createQuery(
				"select a from AreaConhecimentoCnpq a where id in ( select a2.subarea.id from AreaConhecimentoCnpq a2 where a2.codigo = :codigo)");
		q.setInteger("codigo", codigo);

		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista = q.list();
		return lista;
	}

	/**
	 * Retorna as especialidades de conhecimento definidas pelo CNPq.
	 */
	public Collection<AreaConhecimentoCnpq> findEspecialidade(AreaConhecimentoCnpq subarea) throws DAOException {
		Criteria criteria = getCriteria(AreaConhecimentoCnpq.class);
		criteria.add(Restrictions.eq("subarea", subarea));
		criteria.add(Restrictions.ne("id", subarea.getId()));
		criteria.addOrder(Order.asc("nome"));

		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista = criteria.list();
		return lista;
	}
	
	
	
	/**
	 * <p>Retorna todas as grande �reas do CNPq com proje��o.</p>
	 * 
	 * <p>Caso n�o seja passado nenhuma proje��o, faz a proje��o padr�o � que sobre os campos "id" e "nome"</p>
	 * 
	 * <p><strong>Defini��o:</strong> Retorna todas as �reas cujo c�digo, sub �rea, e especialidade s�o nulos</p>
	 *
	 *
	 * @param projecao  o campos para fazer a proje��o separados por v�rgula.
	 * @return  a  grandes �reas do CNPq cadastradas no sistema.
	 * @throws DAOException
	 */
	public Collection<AreaConhecimentoCnpq> findGrandeAreasConhecimentoCnpqComProjecao(String... projecao) throws DAOException {
		
		if(projecao ==  null || projecao.length == 0){
			projecao = new String[]{"id", "nome"};
		}
		
		Criteria criteria = getCriteria(AreaConhecimentoCnpq.class);
		ProjectionList projecoes = Projections.projectionList();
		
		StringBuilder juncaoProjecao = new StringBuilder();
		
		for (String string : projecao) {
			projecoes.add(Projections.property(string));
			
			if(StringUtils.isEmpty(juncaoProjecao.toString()))
				juncaoProjecao.append(string);
			else
				juncaoProjecao.append(", "+string);
		}
		criteria.setProjection(projecoes);
		criteria.add(Restrictions.isNull("subarea"));       // todas as �reas cujo c�digos s�o nulos
		criteria.add(Restrictions.isNull("especialidade")); // e  sub �rea s�o nulas
		criteria.add(Restrictions.isNull("codigo"));        // e especialidade s�o nulas
		
		criteria.add(Restrictions.gt("id", 0));
		criteria.add(Restrictions.eq("excluido", false));   // E n�o foi exclu�do do sistema
		criteria.addOrder(Order.asc("nome"));
		
		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista =  new ArrayList<AreaConhecimentoCnpq>(
					HibernateUtils.parseTo(criteria.list(), juncaoProjecao.toString(), AreaConhecimentoCnpq.class));
		return lista;
	}
	
	
	/**
	 * <p>Retorna todas as �reas do CNPq com proje��o.</p>
	 * 
	 * <p>Caso n�o seja passado nenhuma proje��o, faz a proje��o padr�o � que sobre os campos "id" e "nome"</p>
	 * 
	 * <p><strong>Defini��o:</strong>  Retorna todas as �reas cuja grande �reas � igual a grande �rea passada, sem ser ela pr�pria e n�o possui sub �rea nem especialidade.</p>
	 * 
	 * @param projecao  o campos para fazer a proje��o separados por v�rgula.
	 * @return  a  �reas do CNPq cadastradas no sistema.
	 */
	public Collection<AreaConhecimentoCnpq> findAreasConhecimentoCnpqComProjecao(AreaConhecimentoCnpq grandeArea, String... projecao) throws DAOException {
		
		
		if(projecao ==  null || projecao.length == 0){
			projecao = new String[]{"id", "nome"};
		}
		
		Criteria criteria = getCriteria(AreaConhecimentoCnpq.class);
		ProjectionList projecoes = Projections.projectionList();
		
		
		StringBuilder juncaoProjecao = new StringBuilder();
		
		for (String string : projecao) {
			projecoes.add(Projections.property(string));
			
			if(StringUtils.isEmpty(juncaoProjecao.toString()))
				juncaoProjecao.append(string);
			else
				juncaoProjecao.append(", "+string);
		}
		
		criteria.setProjection(projecoes);
		criteria.add(Restrictions.eq("grandeArea.id", grandeArea.getId()));  // cuja grande �reas � igual a grande �rea passada
		criteria.add(Restrictions.ne("id", grandeArea.getId()));             // sem ser ela pr�pria
		criteria.add(Restrictions.isNull("subarea"));                        // e n�o possui sub �rea
		criteria.add(Restrictions.isNull("especialidade"));                  // e n�o possui especialidade
		
		criteria.add(Restrictions.gt("id", 0));
		criteria.add(Restrictions.eq("excluido", false));    // E n�o foi exclu�do do sistema
		criteria.addOrder(Order.asc("nome"));
		
		
		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista =  new ArrayList<AreaConhecimentoCnpq>(
					HibernateUtils.parseTo(criteria.list(), juncaoProjecao.toString(), AreaConhecimentoCnpq.class));
		return lista;
	}
	
	
	
	/**
	 * <p>Retorna todas as sub �reas do CNPq com proje��o.</p>
	 * 
	 * <p>Caso n�o seja passado nenhuma proje��o, faz a proje��o padr�o � que sobre os campos "id" e "nome"</p>
	 * 
	 * <p><strong>Defini��o:</strong> Retorna todas as �reas cujo c�digo � igual os id da �rea passada, sem ser ela pr�pria.</p>
	 *  
	 * @param projecao  o campos para fazer a proje��o separados por v�rgula.
	 * @param areaCnpq a �rea "pai" das sub �reas
	 * @return  a  sub �reas do CNPq cadastradas no sistema.
	 */
	public Collection<AreaConhecimentoCnpq> findSubAreasConhecimentoCnpqComProjecao(AreaConhecimentoCnpq areaCnpq, String... projecao) throws DAOException {
		
		if(projecao ==  null || projecao.length == 0){
			projecao = new String[]{"id", "nome"};
		}
		
		StringBuilder juncaoProjecao = new StringBuilder();
		
		for (String string : projecao) {
			if(StringUtils.isEmpty(juncaoProjecao.toString()))
				juncaoProjecao.append(string);
			else
				juncaoProjecao.append(", "+string);
		}
		
		
		String hql = " SELECT "+juncaoProjecao.toString()+" FROM AreaConhecimentoCnpq area "
		+" WHERE codigo = :idArea "    // o c�digo � igual os id da �rea passada
		+" AND id <> :idArea "         // sem ser ela pr�pria
		+" AND especialidade IS NULL " // E n�o � especialidade
		+" AND id > 0 "
		+" AND excluido = :false "     // E n�o foi exclu�do do sistema

		+"ORDER BY nome";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idArea", areaCnpq.getId());
		q.setBoolean("false", false);
		
		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista =  new ArrayList<AreaConhecimentoCnpq>(HibernateUtils.parseTo(q.list(), juncaoProjecao.toString(), AreaConhecimentoCnpq.class));
		
		return lista;
	}
	
	
	
	/**
	 * <p>Retorna todas as especialidades das �reas do CNPq com proje��o.</p>
	 * 
	 * <p>Caso n�o seja passado nenhuma proje��o, faz a proje��o padr�o � que sobre os campos "id" e "nome"</p>
	 * 
	 * <p><strong>Defini��o:</strong> Retorna todas as �reas da sub �rea passada, sem ser ela mesma</p>
	 * 
	 * @param projecao  o campos para fazer a proje��o separados por v�rgula.
	 * @param subArea  a sub �rea "pai" das especialidades
	 * @return  a  especialidades das �reas do CNPq cadastradas no sistema.
	 * 
	 */
	public Collection<AreaConhecimentoCnpq> findEspecializadadesAreasConhecimentoCnpqComProjecao(AreaConhecimentoCnpq subArea, String... projecao) throws DAOException {
		
		
		if(projecao ==  null || projecao.length == 0){
			projecao = new String[]{"id", "nome"};
		}
		
		Criteria criteria = getCriteria(AreaConhecimentoCnpq.class);
		ProjectionList projecoes = Projections.projectionList();
		
		StringBuilder juncaoProjecao = new StringBuilder();
		
		for (String string : projecao) {
			projecoes.add(Projections.property(string));
			
			if(StringUtils.isEmpty(juncaoProjecao.toString()))
				juncaoProjecao.append(string);
			else
				juncaoProjecao.append(", "+string);
		}
		
		criteria.setProjection(projecoes);
		criteria.add(Restrictions.eq("subarea.id", subArea.getId())); // onde id da subarea � igual a subarea passado
		criteria.add(Restrictions.ne("id", subArea.getId()));         // sem ser ela mesma
		 
		criteria.add(Restrictions.gt("id", 0));
		criteria.add(Restrictions.eq("excluido", false));              // E n�o foi exclu�do do sistema
		criteria.addOrder(Order.asc("nome"));
		
		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista =  new ArrayList<AreaConhecimentoCnpq>(
					HibernateUtils.parseTo(criteria.list(), juncaoProjecao.toString(), AreaConhecimentoCnpq.class));
		return lista;
	}
	
	/** <p>Retorna todas as especialidades das �reas do CNPq cujo nome foi informado.</p> */
	public Collection<AreaConhecimentoCnpq> findByNome(String nome, boolean ativo) throws DAOException {
		try {
			StringBuilder juncaoProjecao = new StringBuilder("id, nome");
			StringBuffer hql = new StringBuffer("SELECT " + juncaoProjecao.toString());
			hql.append(" from AreaConhecimentoCnpq where");
			hql.append(" excluido = " + ativo+ " and");
			if (nome != null && !nome.trim().equals("")) {
				hql.append(" upper(nome) like :nome");
			}
			hql.append(" order by nome asc");

			Query q = getSession().createQuery(hql.toString());
			if (nome != null && !nome.trim().equals("")) {
				q.setString("nome", "%"+ StringUtils.toAscii(nome.trim().toUpperCase()) + "%");
			}
			
			@SuppressWarnings("unchecked")
			Collection<AreaConhecimentoCnpq> lista =  new ArrayList<AreaConhecimentoCnpq>(
				HibernateUtils.parseTo(q.list(), juncaoProjecao.toString(), AreaConhecimentoCnpq.class));
			
			return lista;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}
	
}
