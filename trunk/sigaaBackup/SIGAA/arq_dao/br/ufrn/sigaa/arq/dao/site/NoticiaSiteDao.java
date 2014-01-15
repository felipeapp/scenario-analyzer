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
import br.ufrn.sigaa.sites.dominio.NoticiaSite;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;
/**
 * Classe responsável em realizar as consultas relacionadas as notícias de um portal público.
 * @author Mário Rizzi Rocha
 *
 */
public class NoticiaSiteDao extends GenericSiteDao {

	/**
	 * Retorna uma coleção de noticias de um determinado Unidade(Departamento, Programa ou
	 * Centro) em ordem decrescente por data 
	 * @param unidade
	 * @param locale
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<NoticiaSite> findByIdioma(Integer id, TipoPortalPublico tipo, 
			String locale,Boolean publicada, Integer limit) throws DAOException{
			
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT n.id, n.titulo, n.descricao, n.idFoto, n.idArquivo, n.locale, n.publicada, n.unidade.id,");
			hql.append(" n.curso.id,n.dataCadastro FROM NoticiaSite n WHERE " + getCondicaoHql(id, tipo) );
			
			if(!isEmpty(publicada))
				hql.append(" AND n.publicada = " + publicada);
			if(!isEmpty(locale))
				hql.append(" AND n.locale = '" + locale.trim() + "'");
			
			hql.append(" ORDER BY n.publicada, n.dataCadastro DESC ");
			Query q = getSession().createQuery(hql.toString());
	
			if(limit != null)
				q.setMaxResults(limit);
			
			List<Object> lista = q.list();
			Iterator<Object> it = lista.iterator();
			List<NoticiaSite> result = new ArrayList<NoticiaSite>();
			while(it.hasNext()){
				
				Object[] colunas = (Object[]) it.next();
				NoticiaSite noticia = new NoticiaSite();
				noticia.setId((Integer) colunas[0]);
				noticia.setTitulo((String) colunas[1]);
				noticia.setDescricao((String) colunas[2]);
				
				if(colunas[3]!= null)
					noticia.setIdFoto((Integer) colunas[3]);
				if(colunas[4]!= null)
				noticia.setIdArquivo((Integer) colunas[4]);
				
				noticia.setLocale((String) colunas[5]);
				noticia.setPublicada((Boolean) colunas[6]);
				
				if(colunas[8]!= null){
					noticia.setCurso(new Curso());
					noticia.getCurso().setId((Integer) colunas[8]);
				}	
				
				if(colunas[7]!= null){
					noticia.setUnidade(new Unidade());
					noticia.getUnidade().setId((Integer) colunas[7]);
				}
				noticia.setDataCadastro((Date) colunas[9]);
				
				result.add(noticia);
				
			}
			
			return result;
	
	}
	
	
}
