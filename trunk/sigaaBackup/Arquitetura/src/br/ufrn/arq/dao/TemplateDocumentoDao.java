/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/11/2009
 */
package br.ufrn.arq.dao;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.templates.TemplateDocumento;
import br.ufrn.arq.util.UFRNUtils;

/**
 * DAO para buscas relacionadas a Templates de Documentos
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class TemplateDocumentoDao extends GenericSharedDBDao {

	/* Tempo máximo default para o cache dos templates de documentos. */
	private static final int CACHE_EXPIRATION_TIME = 5*60*1000;
	
	private static final Map<String, TemplateDocumento> CACHE = new HashMap<String, TemplateDocumento>();
	private static final Map<String, Long> CACHE_TIME = new HashMap<String, Long>();
	
	/** Atualiza ou cria novo template */
	public void salvar(TemplateDocumento template) {
		if(getJdbcTemplate().queryForInt("select count(*) from comum.template_documento where codigo = ?", new Object[] { template.getCodigo() }) > 0)
			getJdbcTemplate().update("update comum.template_documento set titulo = ?, texto = ?, id_tipo_documento = ? where codigo = ?", 
					new Object[] { template.getTitulo(), template.getTexto(), template.getTipo().getId(), template.getCodigo() });
		else
			getJdbcTemplate().update("insert into comum.template_documento (titulo, texto, id_tipo_documento, codigo) values " +
					"(?, ?, ?, ?)", new Object[] { template.getTitulo(), template.getTexto(), template.getTipo().getId(), template.getCodigo() });
		
		CACHE.put(template.getCodigo(), null);
		CACHE_TIME.put(template.getCodigo(), null);
	}
	
	/**
	 * Busca um template de documento por código. Retorna null
	 * se não encontrar nada.
	 * 
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public TemplateDocumento findByCodigo(String codigo) throws DAOException {
		if (CACHE.get(codigo) != null) {
			Long tempo = CACHE_TIME.get(codigo);
			
			if ( System.currentTimeMillis() - tempo > CACHE_EXPIRATION_TIME ) {
				CACHE.put(codigo, null);
			}
		}

		if (CACHE.get(codigo) == null) {
			Query q = getSession().createQuery("select t from TemplateDocumento t where t.codigo = ?").setString(0, codigo);
			TemplateDocumento temp = (TemplateDocumento) q.uniqueResult();
			
			if (temp == null) {
				return null;
			}
			
			CACHE.put(codigo, temp);
			CACHE_TIME.put(codigo, System.currentTimeMillis());
		} 
		
		return UFRNUtils.deepCopy(CACHE.get(codigo)); 
	}

	/**
	 * Remove um template de documento de acordo com o código passado como parâmetro.
	 * @param codigo
	 */
	public void delete(String codigo) {
		getJdbcTemplate().update("delete from comum.template_documento where codigo = ?", new Object[] { codigo });
		CACHE.put(codigo, null);
		CACHE_TIME.put(codigo, null);
	}
	
}
