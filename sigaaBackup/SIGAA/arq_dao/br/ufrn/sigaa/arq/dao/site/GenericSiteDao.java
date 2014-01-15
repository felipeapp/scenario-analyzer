/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/04/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.site;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;
/**
 * 
 * @author Mário Rizzi Rocha
 *
 */
public class GenericSiteDao extends GenericSigaaDAO {

	protected String getCondicaoHql(Integer id, TipoPortalPublico tipoPortalPublico) {
			
		String hql = null;
		if(!ValidatorUtil.isEmpty(tipoPortalPublico)){
			switch (tipoPortalPublico) {
				case UNIDADE: hql = " unidade.id = "; break;
				case CURSO: hql = " curso.id = ";  break;
				default: break;
			}
		}
		return hql + id;
	}
	
	public static String getProjecaoHql(TipoPortalPublico tipoPortalPublico) {
			
		String hql = null;
		if(!ValidatorUtil.isEmpty(tipoPortalPublico)){
			switch (tipoPortalPublico) {
				case UNIDADE: hql = " unidade.id"; break;
				case CURSO: hql = " curso.id ";  break;
				default: break;
			}
		}
		return hql;
	}
	
	protected Criteria getCondicaoCriteria(Criteria c, Integer id, TipoPortalPublico tipoPortalPublico) {
	
		if(!ValidatorUtil.isEmpty(tipoPortalPublico)){
			switch (tipoPortalPublico) {
				case UNIDADE: return c.add(Expression.eq("unidade.id", id)); 
				case CURSO: return c.add(Expression.eq("curso.id", id)); 
				default: break;
			}
		}
		
		return c;
		
	}
	
	
}
