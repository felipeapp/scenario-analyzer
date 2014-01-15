/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/04/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.site;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.sites.dominio.DocumentoSite;
import br.ufrn.sigaa.sites.dominio.TipoDocumentoSite;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;
/**
 * Classe responsável em realizar as consultas relacionadas aos documentos de um portal público.
 * @author Mário Rizzi
 *
 */
public class DocumentoSiteDao extends GenericSiteDao {

	
	/**
	 * Retorna uma número limitado de documentos associados um curso ou unidade
	 *  de acordo com o parâmetro tipo de portal
	 * @param id
	 * @param tipoPortal
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocumentoSite> findByUnidadeCurso(Integer id, TipoPortalPublico tipoPortal, Integer limit)throws DAOException{
		return findGeral(id, null, tipoPortal, limit);
	}
	
	/**
	 * Retorna uma coleção de documentos ordenados por data de cadastro
	 * @param unidade
	 * @param locale
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DocumentoSite> findGeral(Integer id, Integer idTipoDoc, TipoPortalPublico tipoPortal, Integer limit) throws DAOException{
		try{
			
			StringBuffer hql = new StringBuffer();
			hql.append(" SELECT n.id, n.nome,t.id,n.unidade.id,n.curso.id, n.idArquivo, t.nome, t.nomeEn, n.dataCadastro ");
			hql.append(" FROM DocumentoSite n LEFT JOIN n.tipoDocumentoSite t ");
			hql.append(" WHERE n." + getCondicaoHql(id, tipoPortal));
			
			if( idTipoDoc != null)
				hql.append(" AND t.id = " +  idTipoDoc);

			hql.append(" ORDER BY n.tipoDocumentoSite.nome ASC,n.nome ASC, n.dataCadastro DESC ");

			if(limit != null)
				hql.append(" " + BDUtils.limit(limit));
			
			Query q = getSession().createQuery(hql.toString());
			
			List<Object> lista = q.list();
			Iterator<Object> it = lista.iterator();
			List<DocumentoSite> result = new ArrayList<DocumentoSite>();
			while(it.hasNext()){
				
				Object[] colunas = (Object[]) it.next();
				DocumentoSite documento = new DocumentoSite();
				documento.setId((Integer) colunas[0]);
				if(colunas[4]!= null){
					documento.setCurso(new Curso());
					documento.getCurso().setId((Integer) colunas[4]);
				}	
				
				if(colunas[3]!= null){
					documento.setUnidade(new Unidade());
					documento.getUnidade().setId((Integer) colunas[3]);
				}
				
				documento.setIdArquivo((Integer) colunas[5]);
				documento.setTipoDocumentoSite(new TipoDocumentoSite());
				if(colunas[2] != null)
					documento.getTipoDocumentoSite().setId((Integer) colunas[2]);
				documento.getTipoDocumentoSite().setNome((String) colunas[6]);
				documento.getTipoDocumentoSite().setNomeEn((String) colunas[7]);
				documento.setNome((String) colunas[1]);
				documento.setDataCadastro((Date) colunas[8]);
				result.add(documento);
			}
			
			return result;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna uma coleção de documentos ordenados por data de cadastro
	 * @param unidade
	 * @param locale
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoDocumentoSite> findTiposByUnidadeCurso(Integer id, TipoPortalPublico tipo, Integer limit) throws DAOException{
		try{
			
			StringBuffer hql = new StringBuffer();
			hql.append(	" SELECT DISTINCT t.nome,t.nomeEn,t.id  FROM DocumentoSite n LEFT JOIN n.tipoDocumentoSite t ");
			hql.append(	" WHERE n." + getCondicaoHql(id, tipo) + "	ORDER BY t.nome ASC ");

			if(limit != null)
				hql.append(" " + BDUtils.limit(limit));
			
			Query q = getSession().createQuery(hql.toString());
			
			List<Object> lista = q.list();
			Iterator<Object> it = lista.iterator();
			Set<TipoDocumentoSite> result = new HashSet<TipoDocumentoSite>();
			
			while(it.hasNext()){
				
				Object[] colunas = (Object[]) it.next();
				TipoDocumentoSite tp = new TipoDocumentoSite();
				tp.setNome((String) colunas[0]);
				tp.setNomeEn((String) colunas[1]);
				tp.setId((Integer) colunas[2]);
				result.add(tp);
				
			}
			
			return result;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
}
