/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe responsável por consultas específicas à {@link AreaConhecimentoCnpq
 * Área de Conhecimento CNPq}.
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
	 * Retorna as áreas de conhecimento do CNPq.
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
	 * Retorna as sub-áreas de conhecimento do CNPq.
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
	 * <p>Retorna todas as grande áreas do CNPq com projeção.</p>
	 * 
	 * <p>Caso não seja passado nenhuma projeção, faz a projeção padrão é que sobre os campos "id" e "nome"</p>
	 * 
	 * <p><strong>Definição:</strong> Retorna todas as áreas cujo código, sub área, e especialidade são nulos</p>
	 *
	 *
	 * @param projecao  o campos para fazer a projeção separados por vírgula.
	 * @return  a  grandes áreas do CNPq cadastradas no sistema.
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
		criteria.add(Restrictions.isNull("subarea"));       // todas as áreas cujo códigos são nulos
		criteria.add(Restrictions.isNull("especialidade")); // e  sub área são nulas
		criteria.add(Restrictions.isNull("codigo"));        // e especialidade são nulas
		
		criteria.add(Restrictions.gt("id", 0));
		criteria.add(Restrictions.eq("excluido", false));   // E não foi excluído do sistema
		criteria.addOrder(Order.asc("nome"));
		
		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista =  new ArrayList<AreaConhecimentoCnpq>(
					HibernateUtils.parseTo(criteria.list(), juncaoProjecao.toString(), AreaConhecimentoCnpq.class));
		return lista;
	}
	
	
	/**
	 * <p>Retorna todas as áreas do CNPq com projeção.</p>
	 * 
	 * <p>Caso não seja passado nenhuma projeção, faz a projeção padrão é que sobre os campos "id" e "nome"</p>
	 * 
	 * <p><strong>Definição:</strong>  Retorna todas as áreas cuja grande áreas é igual a grande área passada, sem ser ela própria e não possui sub área nem especialidade.</p>
	 * 
	 * @param projecao  o campos para fazer a projeção separados por vírgula.
	 * @return  a  áreas do CNPq cadastradas no sistema.
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
		criteria.add(Restrictions.eq("grandeArea.id", grandeArea.getId()));  // cuja grande áreas é igual a grande área passada
		criteria.add(Restrictions.ne("id", grandeArea.getId()));             // sem ser ela própria
		criteria.add(Restrictions.isNull("subarea"));                        // e não possui sub área
		criteria.add(Restrictions.isNull("especialidade"));                  // e não possui especialidade
		
		criteria.add(Restrictions.gt("id", 0));
		criteria.add(Restrictions.eq("excluido", false));    // E não foi excluído do sistema
		criteria.addOrder(Order.asc("nome"));
		
		
		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista =  new ArrayList<AreaConhecimentoCnpq>(
					HibernateUtils.parseTo(criteria.list(), juncaoProjecao.toString(), AreaConhecimentoCnpq.class));
		return lista;
	}
	
	
	
	/**
	 * <p>Retorna todas as sub áreas do CNPq com projeção.</p>
	 * 
	 * <p>Caso não seja passado nenhuma projeção, faz a projeção padrão é que sobre os campos "id" e "nome"</p>
	 * 
	 * <p><strong>Definição:</strong> Retorna todas as áreas cujo código é igual os id da área passada, sem ser ela própria.</p>
	 *  
	 * @param projecao  o campos para fazer a projeção separados por vírgula.
	 * @param areaCnpq a área "pai" das sub áreas
	 * @return  a  sub áreas do CNPq cadastradas no sistema.
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
		+" WHERE codigo = :idArea "    // o código é igual os id da área passada
		+" AND id <> :idArea "         // sem ser ela própria
		+" AND especialidade IS NULL " // E não é especialidade
		+" AND id > 0 "
		+" AND excluido = :false "     // E não foi excluído do sistema

		+"ORDER BY nome";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idArea", areaCnpq.getId());
		q.setBoolean("false", false);
		
		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista =  new ArrayList<AreaConhecimentoCnpq>(HibernateUtils.parseTo(q.list(), juncaoProjecao.toString(), AreaConhecimentoCnpq.class));
		
		return lista;
	}
	
	
	
	/**
	 * <p>Retorna todas as especialidades das áreas do CNPq com projeção.</p>
	 * 
	 * <p>Caso não seja passado nenhuma projeção, faz a projeção padrão é que sobre os campos "id" e "nome"</p>
	 * 
	 * <p><strong>Definição:</strong> Retorna todas as áreas da sub área passada, sem ser ela mesma</p>
	 * 
	 * @param projecao  o campos para fazer a projeção separados por vírgula.
	 * @param subArea  a sub área "pai" das especialidades
	 * @return  a  especialidades das áreas do CNPq cadastradas no sistema.
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
		criteria.add(Restrictions.eq("subarea.id", subArea.getId())); // onde id da subarea é igual a subarea passado
		criteria.add(Restrictions.ne("id", subArea.getId()));         // sem ser ela mesma
		 
		criteria.add(Restrictions.gt("id", 0));
		criteria.add(Restrictions.eq("excluido", false));              // E não foi excluído do sistema
		criteria.addOrder(Order.asc("nome"));
		
		@SuppressWarnings("unchecked")
		Collection<AreaConhecimentoCnpq> lista =  new ArrayList<AreaConhecimentoCnpq>(
					HibernateUtils.parseTo(criteria.list(), juncaoProjecao.toString(), AreaConhecimentoCnpq.class));
		return lista;
	}
	
	/** <p>Retorna todas as especialidades das áreas do CNPq cujo nome foi informado.</p> */
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
