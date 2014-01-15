/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '17/10/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.site;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.sites.dominio.DetalhesSite;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;
/**
 * Classe de acesso aos dados do site, podendo ser uma 
 * unidade (centro, departamento, programa) ou um curso (graduação, latos e técnico) 
 * 
 * @author Mário Rizzi
 *
 */

public class DetalhesSiteDao extends GenericSiteDao{

	/**
	 * Retorna o site com o id da Unidade ou do Curso passado no parâmetro
	 * 
	 * @param url, id
	 * @return
	 * @throws DAOException
	 */
	public Integer findBySite(String url, Integer... id) throws DAOException {

		return findBySite(url, TipoPortalPublico.UNIDADE, id) ;

	}
	
	/**
	 * Retorna o id da Unidade ou  doCurso
	 * 
	 * @param url, tipoPortal, id
	 * @return
	 * @throws DAOException
	 */
	public Integer findBySite(String url, TipoPortalPublico tipoPortal, Integer... id) throws DAOException {

		StringBuffer hql = new StringBuffer();
		
		hql.append(" SELECT " + GenericSiteDao.getProjecaoHql(tipoPortal) + " FROM DetalhesSite d WHERE 1=1");
		
		if(!isEmpty(url))
			hql.append(" AND d.url = :url ");
		
		if(!isEmpty(id))
			hql.append(" AND d.unidade.id NOT IN "+UFRNUtils.gerarStringIn(id));

		Query query = getSession().createQuery(hql.toString());
		
		if(!isEmpty(url))
			query.setString("url", url);


		return (Integer) query.setMaxResults(1).uniqueResult();

	}
	
	/**
	 * Retorna o detalhe da url especificada
	 * 
	 * @param url, tipoPortal, id
	 * @return
	 * @throws DAOException
	 */
	public DetalhesSite findByUrl(String url) throws DAOException {

		StringBuffer hql = new StringBuffer();
		
		hql.append(" SELECT d FROM DetalhesSite d WHERE 1=1");
		
		if(!isEmpty(url))
			hql.append(" AND d.url = :url ");
		
		Query query = getSession().createQuery(hql.toString());
		
		if(!isEmpty(url))
			query.setString("url", url);


		return (DetalhesSite) query.setMaxResults(1).uniqueResult();

	}	
	
	/**
	 * Retorna uma lista de mapas contendo os nomes dos centros e departamentos.
	 * 
	 * @param url, tipoPortal, id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findAllCentro() throws DAOException {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT d.site_extra as siteExtra, u.id_unidade as idDepartamento, u.nome as nomeDepartamento, ur.id_unidade as idCentro, ");
		sql.append(" ur.nome as nomeCentro, ur.sigla as siglaCentro, m.nome as municipioCentro  FROM site.detalhes_site d ");
		sql.append(" RIGHT JOIN comum.unidade u ON u.id_gestora = d.id_unidade INNER JOIN comum.unidade ur ON u.unidade_responsavel = ur.id_unidade ");
		sql.append(" RIGHT JOIN comum.municipio m ON ur.id_municipio = m.id_municipio ");
		sql.append(" WHERE u.ativo = trueValue() AND  u.tipo_academica = ? AND ur.tipo_academica = ? ORDER BY ur.nome, u.nome ");

		return getJdbcTemplate().queryForList( sql.toString(), new Object[]{ TipoUnidadeAcademica.DEPARTAMENTO, TipoUnidadeAcademica.CENTRO });
		
	}
	
	/**
	 * Retorna uma lista de mapas contendo as unidades especializada e o seus sites.
	 * 
	 * @param url, tipoPortal, id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findAllUnidadesEspecializada() throws DAOException {

		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT d.site_extra as siteExtra, u.id_unidade as idUnidade, u.nome as nomeUnidade ");
		sql.append(" , u.sigla as siglaUnidade, m.nome as municipioUnidade  FROM site.detalhes_site d ");
		sql.append(" RIGHT JOIN comum.unidade u ON u.id_unidade = d.id_unidade ");
		sql.append(" RIGHT JOIN comum.municipio m ON u.id_municipio = m.id_municipio ");
		sql.append(" WHERE u.ativo = trueValue() AND  u.tipo_academica = ?  ORDER BY u.nome ");

		return getJdbcTemplate().queryForList( sql.toString(), new Object[]{ TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA});
		
	}
	
	
	/**
	 * Retorna o id da Unidade ou  doCurso
	 * 
	 * @param id da unidade ou curso
	 * @return
	 * @throws DAOException
	 */
	public DetalhesSite findByIdSite(Integer id, TipoPortalPublico tipo ) throws DAOException {

		StringBuffer hql = new StringBuffer();
		
		hql.append("FROM DetalhesSite d WHERE " + getCondicaoHql(id, tipo));
		Query query = getSession().createQuery(hql.toString());


		return (DetalhesSite) query.uniqueResult();

	}


}