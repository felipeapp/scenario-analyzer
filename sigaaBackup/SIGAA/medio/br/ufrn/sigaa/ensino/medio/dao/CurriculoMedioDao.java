/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 02/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.medio.dominio.CurriculoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;

/**
 * Classe de Dao com consultas sobre os Currículos de ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class CurriculoMedioDao extends GenericSigaaDAO{

	/**
	 * Retornar os currículos com o curso e/ou série de ensino médio informados.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CurriculoMedio> findByCursoOrSerie(CursoMedio cursoMedio, Serie serie, String codigo, Boolean ativo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(CurriculoMedio.class);
			if ( cursoMedio != null && isNotEmpty(cursoMedio) ) 
				c.add(Restrictions.eq("cursoMedio", cursoMedio));
			if ( serie != null && isNotEmpty(serie) ) 
				c.add(Restrictions.eq("serie", serie));
			if ( codigo != null && isNotEmpty(codigo) ) 
				c.add(Restrictions.ilike("codigo", "%"+codigo+"%"));
			if ( ativo != null && isNotEmpty(ativo) ) 
				c.add(Restrictions.eq("ativo", ativo));
			
			
			c.addOrder(Order.asc("cursoMedio"));
			c.addOrder(Order.asc("serie"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retornar o currículo mais recente do curso e série de ensino médio informados.
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public CurriculoMedio findMaisRecenteByCursoOrSerie(CursoMedio cursoMedio, Serie serie) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(CurriculoMedio.class);
			
			if ( cursoMedio != null && isNotEmpty(cursoMedio) ) 
				c.add(Restrictions.eq("cursoMedio", cursoMedio));
			
			if ( serie != null && isNotEmpty(serie) ) 
				c.add(Restrictions.eq("serie", serie));
			
			c.add(Restrictions.eq("ativo", true));
			
			c.addOrder(Order.desc("anoEntradaVigor"));
			
			return (CurriculoMedio) c.setMaxResults(1).uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retornar as disciplinas vinculadas a um determinado currículo e/ou série do ensino médio.
	 * 
	 * @param curriculo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findDisciplinasByCurriculoSerie(CurriculoMedio curriculo, Serie serie) throws DAOException {
		
		try {
			String projecao = "distinct cc.id, cc.codigo, cc.detalhes.nome, cc.detalhes.chTotal, " +			
							  "cc.detalhes.crAula, cc.detalhes.crEstagio, cc.detalhes.crLaboratorio, cc.nivel, " +
							  "cc.tipoComponente.descricao, cc.ativo, cc.unidade.id, cc.unidade.nome, cc.unidade.sigla";
			
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select "+ projecao +
					" from CurriculoMedioComponente as ccm" +
					" inner join ccm.curriculoMedio as cm " +
					" inner join cm.serie as s " +
					" inner join ccm.componente as cc " +
					" inner join cc.unidade u " + 
					" where ccm.curriculoMedio.ativo = trueValue() " +
					" and ccm.componente = cc ");
					if ( curriculo != null && isNotEmpty(curriculo) ) 
						hql.append(" and ccm.curriculoMedio.id = " + curriculo.getId());
					if ( serie != null && isNotEmpty(serie) ) 
						hql.append(" and s.id = " + serie.getId());
					
					hql.append(" order by cc.detalhes.nome ");
					
			Query query = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
			List<ComponenteCurricular> lista = (List<ComponenteCurricular>) HibernateUtils.parseTo(query.list(), projecao, ComponenteCurricular.class, "cc");	
			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retornar as disciplinas com os ids informados.
	 * 
	 * @param idsComponentes
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByIds(List<Integer> idsComponentes,CurriculoMedio curriculo ) throws DAOException {

		try {
			String projecao = "cc.id, cc.codigo, cc.detalhes.nome, cc.detalhes.chTotal, " +
							  "cc.detalhes.crAula, cc.detalhes.crEstagio, cc.detalhes.crLaboratorio, cc.nivel, " +
							  "cc.tipoComponente.descricao, cc.ativo, cc.unidade.id, cc.unidade.nome, cc.unidade.sigla";
			
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select distinct "+ projecao +
					" from CurriculoMedioComponente as ccm" +
					" inner join ccm.curriculoMedio as cm " +
					" inner join cm.serie as s " +
					" inner join ccm.componente as cc " +
					" inner join cc.unidade u " + 
					" where ccm.curriculoMedio.ativo = trueValue() " +
					" and ccm.componente = cc " +
					" and ccm.curriculoMedio.id = " + curriculo.getId());
					
					if ( idsComponentes != null && isNotEmpty(idsComponentes) ) 
						hql.append(" and ccm.componente.id in " +  UFRNUtils.gerarStringIn(idsComponentes));
					hql.append(" order by cc.detalhes.nome ");
					
			Query query = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
			List<ComponenteCurricular> lista = (List<ComponenteCurricular>) HibernateUtils.parseTo(query.list(), projecao, ComponenteCurricular.class, "cc");	
			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	/**
	 * Retornar as disciplinas vinculadas a um determinado currículo do ensino médio.
	 * 
	 * @param curriculo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findDisciplinasByCurriculo(CurriculoMedio curriculo) throws DAOException {
		
		try {
			String projecao = "cc.id, cc.codigo, cc.detalhes.nome, cc.detalhes.chTotal, " +
							  "cc.detalhes.crAula, cc.detalhes.crEstagio, cc.detalhes.crLaboratorio, cc.nivel, " +
							  "cc.tipoComponente.descricao, cc.ativo, cc.unidade.id, cc.unidade.nome, cc.unidade.sigla";
			
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select "+ projecao +
					" from CurriculoMedioComponente as ccm" +
					" inner join ccm.curriculoMedio as cm " +
					" inner join cm.serie as s " +
					" inner join ccm.componente as cc " +
					" inner join cc.unidade u " + 
					" where ccm.curriculoMedio.ativo = trueValue() " +
					" and ccm.componente = cc " +
					" and ccm.curriculoMedio.id = " + curriculo.getId());
					
					hql.append(" order by cc.detalhes.nome ");
					
			Query query = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
			List<ComponenteCurricular> lista = (List<ComponenteCurricular>) HibernateUtils.parseTo(query.list(), projecao, ComponenteCurricular.class, "cc");	
			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Informa se existe outro currículo ativo cadastrado com mesmo código, série e curso.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public boolean existeCurriculoAtivoByCodigoSerieCurso(CurriculoMedio curriculo) throws DAOException {
		try {
			if (ValidatorUtil.isNotEmpty(curriculo.getCodigo())) { 
				Criteria c = getSession().createCriteria(CurriculoMedio.class);
				c.add(Restrictions.ne("id", curriculo.getId()));
				c.add(Restrictions.eq("codigo", curriculo.getCodigo()));
				c.add(Restrictions.eq("cursoMedio", curriculo.getCursoMedio()));
				c.add(Restrictions.eq("serie", curriculo.getSerie()));
				
				c.setProjection(Projections.rowCount());
				return (Integer) c.uniqueResult() > 0;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}
