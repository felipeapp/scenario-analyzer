/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/11/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.site;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.sites.dominio.SecaoExtraSite;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;
/**
 * Classe responsável em realizar as consultas relacionadas as seções extras de um portal público. 
 * @author Mário Rizzi Rocha
 *
 */
public class SecaoExtraSiteDao extends GenericSiteDao{

	
	/**
	 * Retorna uma coleção de todas as seções extras de um portal público conforme o idioma
	 * passado por parâmetro.
	 * @param id
	 * @param tipoPortalPublico
	 * @param publicada
	 * @param locale
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SecaoExtraSite> findByIdioma(Integer id, 
			TipoPortalPublico tipoPortalPublico, Boolean publicada, String locale) throws DAOException {
		try{
			
			StringBuffer hql = new StringBuffer();
			hql.append(" SELECT s.id, s.titulo, s.descricao, s.linkExterno, s.locale,s.publicada,s.unidade.id,s.curso.id, s.dataCadastro FROM SecaoExtraSite s ");
			hql.append("WHERE " + getCondicaoHql(id, tipoPortalPublico) );
			if(!isEmpty(locale))
				hql.append(" AND s.locale = '" + locale.trim() + "'");
			if(!isEmpty(locale))
				hql.append(" AND s.publicada is " + publicada);
			hql.append(" ORDER BY s.publicada, s.titulo ASC ");
			
			Query q = getSession().createQuery(hql.toString());
			
			List lista = q.list();
			Iterator it = lista.iterator();
			List<SecaoExtraSite> result = new ArrayList<SecaoExtraSite>();
			while(it.hasNext()){
				
				Object[] colunas = (Object[]) it.next();
				SecaoExtraSite secao = new SecaoExtraSite();
				secao.setId((Integer) colunas[0]);
				secao.setTitulo((String) colunas[1]);
				secao.setDescricao((String) colunas[2]);
				
				if(colunas[3]!= null)
					secao.setLinkExterno((String) colunas[3]);

				
				secao.setLocale((String) colunas[4]);
				secao.setPublicada((Boolean) colunas[5]);
				
				if(colunas[7]!= null){
					secao.setCurso(new Curso());
					secao.getCurso().setId((Integer) colunas[7]);
				}	
				
				if(colunas[6]!= null){
					secao.setUnidade(new Unidade());
					secao.getUnidade().setId((Integer) colunas[6]);
				}
				
				secao.setDataCadastro((Date) colunas[8]);
				result.add(secao);
				
			}
			
			return result;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	
}
