/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 17/01/2013
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.internacionalizacao.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ConstanteTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.EntidadeTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.TraducaoElemento;

/**
 * DAO responsável por gerenciar o acesso e consulta a dados do {@link ConstanteTraducao}.
 * 
 * @author Rafael Gomes
 *
 */
public class ConstanteTraducaoDao extends GenericSigaaDAO{

	/**
	 * Retorna uma coleção de traduções de constantes por entidade e idioma.
	 * @param entidade
	 * @param idioma
	 * @return
	 * @throws DAOException
	 */
	public ConstanteTraducao findByEntidadeIdiomaConstante( String idioma, String constante, String classe) throws DAOException {
		
		Criteria c = getSession().createCriteria(ConstanteTraducao.class);
		Criteria entidadeTraducao = c.createCriteria("entidade");
		entidadeTraducao.add(Restrictions.eq("classe", classe));
		c.add(Restrictions.eq("idioma", idioma));
		c.add(Restrictions.eq("constante", constante));
		
		return (ConstanteTraducao) c.setMaxResults(1).uniqueResult();
	}
	
	/**
	 * Retorna traduções de constantes por entidade e idioma.
	 * @param entidade
	 * @param idioma
	 * @param constante
	 * @return
	 * @throws DAOException
	 */
	public Collection<ConstanteTraducao> findByEntidadeIdioma( String idioma, String... classe) throws DAOException {
		
		try {
			
			Criteria c = getSession().createCriteria(ConstanteTraducao.class);
			Criteria entidadeTraducao = c.createCriteria("entidade");
			entidadeTraducao.add(Restrictions.in("classe", classe));
			c.add(Restrictions.eq("idioma", idioma));
			
			@SuppressWarnings("unchecked")
			List<ConstanteTraducao> lista = c.list();
			
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna uma coleção geral de traduções de constantes, conforme os parâmetros informados.
	 * @param entidade
	 * @param idioma
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public  Collection<ConstanteTraducao> findGeral( String idioma, String constante, EntidadeTraducao entidade) throws DAOException {
		
		Criteria c = getSession().createCriteria(ConstanteTraducao.class);
		Criteria entidadeTraducao = c.createCriteria("entidade");
		if (ValidatorUtil.isNotEmpty(entidade))
			entidadeTraducao.add(Restrictions.eq("id", entidade.getId()));
		if (ValidatorUtil.isNotEmpty(idioma))
			c.add(Restrictions.eq("idioma", idioma));
		if (ValidatorUtil.isNotEmpty(constante))
			c.add(Restrictions.eq("constante", constante));
		entidadeTraducao.addOrder(Order.asc("nome"));
		c.addOrder(Order.asc("constante"));
		
		
		return c.list();

	}
}
