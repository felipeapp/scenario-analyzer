/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/06/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;

/**
 *
 *           Dao para as situações dos materiais informacionais.
 *
 * @author jadson
 * @since 09/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class SituacaoMaterialInformacionalDao extends GenericSigaaDAO {

	/**
	 * <p>Busca todas as situações que são consideradas que não foram removidas do sistema.</p>
	 * 
	 * <p>Incluíndo-se nesse caso a situação de baixa<p>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SituacaoMaterialInformacional> findAllSituacoesAtivas() throws DAOException{
		
		Criteria c = getSession().createCriteria(SituacaoMaterialInformacional.class);
		
		c.add( Restrictions.eq("ativo", true) );
		
		c.addOrder( Order.desc("situacaoDisponivel") );
		c.addOrder( Order.desc("situacaoEmprestado") );
		c.addOrder( Order.desc("situacaoDeBaixa") );
		c.addOrder( Order.asc("descricao") );
		
		@SuppressWarnings("unchecked")
		List<SituacaoMaterialInformacional> lista = c.list();
		return lista;
	}
	
	
	/**
	 * 
	 * Busca todas as situações que o usuário pode manualmente atribuir a um material.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SituacaoMaterialInformacional> findSituacoesUsuarioPodeAtribuirMaterial() throws DAOException{
		
		Criteria c = getSession().createCriteria(SituacaoMaterialInformacional.class);
		c.add( Restrictions.eq("situacaoEmprestado", false) );
		c.add( Restrictions.eq("situacaoDeBaixa", false) );
		
		c.add( Restrictions.eq("ativo", true) );
		
		c.addOrder( Order.desc("situacaoDisponivel") );
		c.addOrder( Order.asc("descricao") );
		
		@SuppressWarnings("unchecked")
		List<SituacaoMaterialInformacional> lista = c.list();
		return lista;
	}
	
	
	/**
	 * 
	 * Busca todas as situações que o usuário pode pesquisar por materiais.
	 * 
	 * Aqui só não é para retorna a situação de baixa. Materiais com situação de baixa o usuário 
	 * não pode pesquisar. Só devem aparecer em relatórios
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SituacaoMaterialInformacional> findSituacoesUsuarioPodeVerNaHoraDaPesquisa() throws DAOException{
		
		Criteria c = getSession().createCriteria(SituacaoMaterialInformacional.class);
		
		c.add( Restrictions.eq("ativo", true) );
		
		c.addOrder( Order.desc("situacaoDisponivel") );
		c.addOrder( Order.desc("situacaoEmprestado") );
		c.addOrder( Order.desc("situacaoDeBaixa") );
		c.addOrder( Order.asc("descricao") );
		
		@SuppressWarnings("unchecked")
		List<SituacaoMaterialInformacional> lista = c.list();
		return lista;
	}
	
	/**
	 * Busca todas as situações de material que indicam que o material foi baixado.
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<SituacaoMaterialInformacional> findSituacoesDeBaixa() throws DAOException {
		
		Criteria c = getSession().createCriteria(SituacaoMaterialInformacional.class);
		
		c.add( Restrictions.eq("situacaoDeBaixa", true) );
		c.add( Restrictions.eq("ativo", true) );
		
		c.addOrder( Order.asc("descricao") );
		
		@SuppressWarnings("unchecked")
		List<SituacaoMaterialInformacional> lista = c.list();
		
		return lista;
		
	}
	
	
	/**
	 * Busca todas as situações ativas que não seja a situação de baixa.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<SituacaoMaterialInformacional> findAllSituacoesAtivasNaoBaixa() throws DAOException{
		
		Criteria c = getSession().createCriteria(SituacaoMaterialInformacional.class);
		c.add( Restrictions.eq("situacaoDeBaixa", false) );
		c.add( Restrictions.eq("ativo", true) );
		
		c.addOrder( Order.desc("situacaoDisponivel") );
		c.addOrder( Order.desc("situacaoEmprestado") );
		c.addOrder( Order.desc("situacaoDeBaixa") );
		c.addOrder( Order.asc("descricao") );
		
		@SuppressWarnings("unchecked")
		List<SituacaoMaterialInformacional> lista = c.list();
		return lista;
	}

	
	
	
	/**
	 * Busca a descrição de situações de materiais ativas a partir de seus id's.
	 * 
	 * @param idSituacaoMaterialList Lista de id's de situações de materiais
	 * @return Lista das descrições das situações de materiais
	 * @throws DAOException
	 */
	public List<String> findDescricaoSituacoesAtivas(Collection<Integer> idSituacaoMaterialList) throws DAOException {
		
		Criteria c = getSession().createCriteria(SituacaoMaterialInformacional.class);
		c.setProjection(Projections.property("descricao"));
		
		c.add(Restrictions.eq("situacaoDeBaixa", false));
		c.add( Restrictions.eq("ativo", true) );
		
		if (idSituacaoMaterialList != null && idSituacaoMaterialList.size() > 0) {
			c.add(Restrictions.in("id", idSituacaoMaterialList));
		}
		
		c.addOrder( Order.desc("situacaoDisponivel") );
		c.addOrder( Order.desc("situacaoEmprestado") );
		c.addOrder( Order.desc("situacaoDeBaixa") );
		c.addOrder( Order.asc("descricao") );
		
		@SuppressWarnings("unchecked")
		List<String> lista = c.list();
		
		return lista;
	}
	
	
	/**
	 * Busca a descrição de situações de materiais ativas a partir de seus id's.
	 * 
	 * @param idSituacaoMaterialList Lista de id's de situações de materiais
	 * @return Lista das descrições das situações de materiais
	 * @throws DAOException
	 */
	public SituacaoMaterialInformacional findSituacaoAtualMaterial(int idMaterial) throws DAOException {
		
		String hql = " SELECT s.id, s.descricao, s.situacaoDisponivel, s.situacaoEmprestado, s.situacaoDeBaixa, s.visivelPeloUsuario "
			+" FROM MaterialInformacional m "
			+" INNER JOIN m.situacao s "
			+" WHERE m.id = :idMaterial";
		
		Query q = getSession().createQuery(hql);
		q.setParameter("idMaterial", idMaterial);
		
		Object[] dados = (Object[]) q.uniqueResult();
		
		if(dados == null)
			return new SituacaoMaterialInformacional(); // para fascículos que ainda não foram incluídos no acervo, eles não possuem situação ainda.
		
		return new SituacaoMaterialInformacional((Integer)dados[0],  (String)dados[1], (Boolean) dados[2], (Boolean) dados[3], (Boolean) dados[4]);
		
	}
	
	
	
	/**
	 *       Encontra as situações ativas que possuem a mesma descrição da situação.
	 *
	 * @param biblioteca
	 * @param tipoUsuario
	 * @return
	 * @throws DAOException
	 */
	public SituacaoMaterialInformacional findSituacaoMaterialAtivoByDescricao(String descricao) throws DAOException{
		
		Criteria c = getSession().createCriteria( SituacaoMaterialInformacional.class );
		c.setProjection(Projections.projectionList().add(Projections.property("id")).add(Projections.property("descricao")) );
		c.add( Restrictions.eq( "ativo" , true ) );
		c.add( Restrictions.ilike( "descricao" , descricao ) );
		c.addOrder( Order.asc("descricao") );
		c.setMaxResults(1);
		
		Object[] object = (Object[]) c.uniqueResult();
		
		if(object != null)
			return new SituacaoMaterialInformacional((Integer)object[0], (String) object[1]);
		else
			return null;
	}
	
	
	
	/**
	 * Verifica se entre os materiais passados existe algum que está emprestado no momento.
	 * 
	 * @param idsMateriais Lista de id's dos materiais
	 * @return Se algum dos materiais passados está emprestado
	 * @throws DAOException
	 */
	public boolean verificaSeExisteAlgumaMaterialEmprestado(List<Integer> idsMateriais) throws DAOException {
		
		if(idsMateriais == null || idsMateriais.size() == 0)
			return false;
		
		String hql = " SELECT COUNT ( DISTINCT m.id ) "
			+" FROM MaterialInformacional m "
			+" INNER JOIN m.situacao s "
			+" WHERE m.id IN ( :idsMateriais ) AND s.situacaoEmprestado = :true";
		
		Query q = getSession().createQuery(hql);
		q.setParameterList("idsMateriais", idsMateriais);
		q.setParameter("true", true);
		
		Long quantidade = (Long) q.uniqueResult();
		
		if(quantidade != null && quantidade > 0)
			return true;
		else 
			return false;
		
	}
	
}
