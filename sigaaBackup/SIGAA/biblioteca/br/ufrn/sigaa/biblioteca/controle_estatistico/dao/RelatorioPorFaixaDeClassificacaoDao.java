/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 *
 * <p>Relat�rio exclusivo para o relat�rio por faixa de classifica��o</p>
 *
 * <p> <i> Possui dois m�todos porque podem ser emitidos dois tipos de relat�rios diferentes: a anal�tica e o sint�tico.</i> </p>
 * 
 * @author jadson
 *
 */
public class RelatorioPorFaixaDeClassificacaoDao extends GenericSigaaDAO{

	
	
	/**
	 * <p>M�todo que retorna a quantidade real de T�tulos no acervo. </p>  
	 * <p>Agrupado pela classifica��o do agrupamento 1.  Caso seja uma agrupamento de classifica��o. </p> 
	 *
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<Object[]> countTotalTitulosPorFaixaAcervo(
			Collection<Integer> bibliotecas, 
			Collection<Integer> idsTiposMateriais,
			Collection<Integer> idsColecoes,
			Collection<Integer> situacoesMaterial,
			Collection<Integer> formasDocumento,
			String classeInicial, String classeFinal,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean contarExemplares, boolean contarFasciculos) throws DAOException {
			
			StringBuilder sqlExemplares = new StringBuilder();
			StringBuilder sqlFasciculos = new StringBuilder();
			
			if ( contarExemplares ) {
				
				sqlExemplares.append(" SELECT ");
				sqlExemplares.append(" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe'  ) AS classificacao , ");
				sqlExemplares.append(" COUNT(DISTINCT titulo.id_titulo_catalografico) AS qtdTitulos ");
				sqlExemplares.append(" FROM biblioteca.titulo_catalografico AS titulo ");
				sqlExemplares.append(" INNER JOIN biblioteca.exemplar AS exe ON exe.id_titulo_catalografico = titulo.id_titulo_catalografico ");
				sqlExemplares.append(" INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exe.id_exemplar ");
				sqlExemplares.append(" INNER JOIN biblioteca.situacao_material_informacional AS sit ON sit.id_situacao_material_informacional = material.id_situacao_material_informacional ");
				
				if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
					sqlExemplares.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
					" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
				}
				
				
				sqlExemplares.append(" WHERE titulo.ativo = trueValue()	AND sit.situacao_de_baixa = falseValue() AND material.ativo = trueValue() ");
				
				if ( bibliotecas != null && ! bibliotecas.isEmpty() )
					sqlExemplares.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
				
				if ( idsTiposMateriais != null && ! idsTiposMateriais.isEmpty() )
					sqlExemplares.append(" AND material.id_tipo_material IN ("+ StringUtils.join(idsTiposMateriais, ',') +") ");
				
				if ( idsColecoes != null && ! idsColecoes.isEmpty() )
					sqlExemplares.append(" AND material.id_colecao IN ("+ StringUtils.join(idsColecoes, ',') +") ");
				
				if ( situacoesMaterial != null && ! situacoesMaterial.isEmpty() )
					sqlExemplares.append(" AND material.id_situacao_material_informacional IN ("+ StringUtils.join(situacoesMaterial, ',') +") ");
				
				if ( formasDocumento != null && ! formasDocumento.isEmpty() )
					sqlExemplares.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
				
				sqlExemplares.append(" AND titulo."+classificacao.getColunaClassificacao()+" >= :classeInicial AND titulo."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
					
				sqlExemplares.append(" GROUP BY  titulo."+classificacao.getColunaClassePrincipal());
			}
			
			if ( contarFasciculos ) {
				
				sqlFasciculos.append(" SELECT ");
				sqlFasciculos.append(" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe'  ) AS classificacao , ");
				sqlFasciculos.append(" COUNT(DISTINCT titulo.id_titulo_catalografico) AS qtdTitulos ");
				sqlFasciculos.append(" FROM biblioteca.titulo_catalografico AS titulo ");
				sqlFasciculos.append(" INNER JOIN biblioteca.assinatura AS ass ON ass.id_titulo_catalografico = titulo.id_titulo_catalografico " +
									 " INNER JOIN biblioteca.fasciculo AS fas ON fas.id_assinatura = ass.id_assinatura ");
				sqlFasciculos.append(" INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fas.id_fasciculo ");
				sqlFasciculos.append(" INNER JOIN biblioteca.situacao_material_informacional AS sit ON sit.id_situacao_material_informacional = material.id_situacao_material_informacional ");
				
				if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
					sqlFasciculos.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
					" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
				}
				
				
				sqlFasciculos.append(" WHERE titulo.ativo = trueValue()	AND sit.situacao_de_baixa = falseValue() AND material.ativo = trueValue() ");
				
				if ( bibliotecas != null && ! bibliotecas.isEmpty() )
					sqlFasciculos.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
				
				if ( idsTiposMateriais != null && ! idsTiposMateriais.isEmpty() )
					sqlFasciculos.append(" AND material.id_tipo_material IN ("+ StringUtils.join(idsTiposMateriais, ',') +") ");
				
				if ( idsColecoes != null && ! idsColecoes.isEmpty() )
					sqlFasciculos.append(" AND material.id_colecao IN ("+ StringUtils.join(idsColecoes, ',') +") ");
				
				if ( situacoesMaterial != null && ! situacoesMaterial.isEmpty() )
					sqlFasciculos.append(" AND material.id_situacao_material_informacional IN ("+ StringUtils.join(situacoesMaterial, ',') +") ");
				
				if ( formasDocumento != null && ! formasDocumento.isEmpty() )
					sqlFasciculos.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
				
				sqlFasciculos.append(" AND titulo."+classificacao.getColunaClassificacao()+" >= :classeInicial AND titulo."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
					
				sqlFasciculos.append(" GROUP BY  titulo."+classificacao.getColunaClassePrincipal());
			}
			
			
			StringBuilder sql = new StringBuilder();
			
			if(contarExemplares && ! contarFasciculos){
				sql.append(sqlExemplares.toString());
			}
			
			if(contarFasciculos && ! contarExemplares){
				sql.append(sqlFasciculos.toString());
			}
			
			if(contarExemplares && contarFasciculos){
				sql.append("SELECT sub.classificacao, sum(sub.qtdTitulos) as qtdTitulosExterno FROM (");
				sql.append(sqlExemplares.toString() );
				sql.append(" UNION ALL ( ");
				sql.append(sqlFasciculos.toString() );
				sql.append(" ) ");
				sql.append(" ) as sub GROUP BY sub.classificacao ");
				sql.append(" ORDER BY  sub.classificacao ");
			}
			
			Query q = getSession().createSQLQuery(sql.toString());
			q.setParameter("classeInicial", classeInicial);
			q.setParameter("classeFinal", classeFinal);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			return lista;
	
		
	}
	
	
	
	/**
	 * Consulta que gera os dados do relat�rio sintetico (apenas quantidades) de T�tulos e Exemplares por Faixa de classifica��o.
	 * 
	 */
	public List<Object[]> findPorFaixaDeClassificacaoSintetico(
			  Collection<Integer> idsBibliotecas
			, Collection<Integer> idsTiposMateriais
			, Collection<Integer> idsColecoes
			, Collection<Integer> idsSituacoesMateriais
			, Collection<Integer> idsFormasDocumento
			, String classeInicial, String classeFinal
			, FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos
			, AgrupamentoRelatoriosBiblioteca agrupamento1, AgrupamentoRelatoriosBiblioteca agrupamento2) throws DAOException {
		
		if ( ! retornarExemplares && ! retornarFasciculos )
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido."); // Erro do programador
		
		
		StringBuilder sqlExemplares = new StringBuilder();
		StringBuilder sqlFasciculos = new StringBuilder();
		
		sqlExemplares.append("SELECT  ");
		sqlFasciculos.append("SELECT  ");
		
		sqlExemplares.append(" count(DISTINCT titulo.id_titulo_catalografico) AS titulos, ");
		sqlFasciculos.append(" count(DISTINCT titulo.id_titulo_catalografico) AS titulos, ");
		
		sqlExemplares.append( " count(DISTINCT exe.id_exemplar) AS exemplares, 0 AS fasciculos, ");
		sqlFasciculos.append( "	 0 AS exemplares, count(DISTINCT fas.id_fasciculo) AS fasciculos, ");
		
		if(agrupamento1 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO){
			sqlExemplares.append(agrupamento1.campoAgrupamento+" AS "+agrupamento1.nomeCampo+",");
			sqlFasciculos.append(agrupamento1.campoAgrupamento+" AS "+agrupamento1.nomeCampo+",");
		}
		
		if(agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO){
			sqlExemplares.append(agrupamento2.campoAgrupamento+" AS "+agrupamento2.nomeCampo+",");
			sqlFasciculos.append(agrupamento2.campoAgrupamento+" AS "+agrupamento2.nomeCampo+",");
		}
		
		sqlExemplares.delete(sqlExemplares.length()-1, sqlExemplares.length()); /// Retira a �ltima virgula colocada nas proje��es
		sqlExemplares.append(" ");
		
		sqlFasciculos.delete(sqlFasciculos.length()-1, sqlFasciculos.length()); /// Retira a �ltima virgula colocada nas proje��es
		sqlFasciculos.append(" ");
		
		
		sqlExemplares.append("FROM biblioteca.titulo_catalografico AS titulo ");
		sqlFasciculos.append("FROM biblioteca.titulo_catalografico AS titulo ");
		
		sqlExemplares.append( retornarExemplares ? "	 INNER JOIN biblioteca.exemplar AS exe ON exe.id_titulo_catalografico = titulo.id_titulo_catalografico " : "");
		sqlFasciculos.append( retornarFasciculos ? "	 INNER JOIN biblioteca.assinatura AS ass ON ass.id_titulo_catalografico = titulo.id_titulo_catalografico " +
										 "	 INNER JOIN biblioteca.fasciculo AS fas ON fas.id_assinatura = ass.id_assinatura "   : " " );
					
		sqlExemplares.append(" INNER JOIN biblioteca.material_informacional   AS material ON material.id_material_informacional = exe.id_exemplar  ");
		sqlFasciculos.append(" INNER JOIN biblioteca.material_informacional   AS material ON material.id_material_informacional  = fas.id_fasciculo  ");
		
		
		sqlExemplares.append("INNER JOIN biblioteca.situacao_material_informacional    AS sit ON (   sit.id_situacao_material_informacional = material.id_situacao_material_informacional  )   ");
		sqlFasciculos.append("INNER JOIN biblioteca.situacao_material_informacional    AS sit ON (   sit.id_situacao_material_informacional = material.id_situacao_material_informacional  )   ");
		
		if ( idsFormasDocumento != null && ! idsFormasDocumento.isEmpty() ) {
			sqlExemplares.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
			sqlFasciculos.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}		
		
		sqlExemplares.append(agrupamento1.join);
		sqlExemplares.append(agrupamento2.join);
		sqlFasciculos.append(agrupamento1.join);
		sqlFasciculos.append(agrupamento2.join);
		
		sqlExemplares.append(" WHERE material.ativo = trueValue() AND sit.situacao_de_baixa = falseValue() ");
		sqlFasciculos.append(" WHERE material.ativo = trueValue() AND sit.situacao_de_baixa = falseValue() ");
		
		
		if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
			sqlExemplares.append(" AND material.id_biblioteca IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
			sqlFasciculos.append(" AND material.id_biblioteca IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
		}
		
		if ( idsTiposMateriais != null && ! idsTiposMateriais.isEmpty() ) {
			sqlExemplares.append(" AND material.id_tipo_material IN ("+ StringUtils.join(idsTiposMateriais, ',') +") ");
			sqlFasciculos.append(" AND material.id_tipo_material IN ("+ StringUtils.join(idsTiposMateriais, ',') +") ");
		}
		
		if ( idsColecoes != null && ! idsColecoes.isEmpty() ) {
			sqlExemplares.append(" AND material.id_colecao IN ("+ StringUtils.join(idsColecoes, ',') +") ");
			sqlFasciculos.append(" AND material.id_colecao IN ("+ StringUtils.join(idsColecoes, ',') +") ");
		}
		
		if ( idsSituacoesMateriais != null && ! idsSituacoesMateriais.isEmpty() ) {
			sqlExemplares.append(" AND material.id_situacao_material_informacional IN ("+ StringUtils.join(idsSituacoesMateriais, ',') +") ");
			sqlFasciculos.append(" AND material.id_situacao_material_informacional IN ("+ StringUtils.join(idsSituacoesMateriais, ',') +") ");
		}
		
		if ( idsFormasDocumento != null && ! idsFormasDocumento.isEmpty() ) {
			sqlExemplares.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(idsFormasDocumento, ',') +") ");
			sqlFasciculos.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(idsFormasDocumento, ',') +") ");
		}
		
		
		sqlExemplares.append(" AND titulo."+classificacao.getColunaClassificacao()+" >= :classeInicial AND titulo."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
		sqlFasciculos.append(" AND titulo."+classificacao.getColunaClassificacao()+" >= :classeInicial AND titulo."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
		
		sqlExemplares.append(" GROUP BY " +agrupamento1.campoAgrupamento+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento2.campoAgrupamento : "" )  );
		
		if( ! retornarExemplares ||  ! retornarFasciculos)
			sqlExemplares.append(" ORDER BY " +agrupamento1.campoAgrupamento+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento2.campoAgrupamento : "" )   );
				
		sqlFasciculos.append(" GROUP BY " +agrupamento1.campoAgrupamento+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento2.campoAgrupamento : "" )  );
		
		if( ! retornarExemplares ||  ! retornarFasciculos)
			sqlFasciculos.append(" ORDER BY " +agrupamento1.campoAgrupamento+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento2.campoAgrupamento : "" )   );
	
		if(retornarExemplares && ! retornarFasciculos){		
			Query q = getSession().createSQLQuery(sqlExemplares.toString());
			q.setParameter("classeInicial", classeInicial);
			q.setParameter("classeFinal", classeFinal);
			
			@SuppressWarnings("unchecked")
			List <Object []> rs = q.list();
			return rs;
		}
		
		
		if(! retornarExemplares &&  retornarFasciculos){		
			Query q = getSession().createSQLQuery(sqlFasciculos.toString());
			q.setParameter("classeInicial", classeInicial);
			q.setParameter("classeFinal", classeFinal);
			
			@SuppressWarnings("unchecked")
			List <Object []> rs = q.list();
			return rs;
		}
		
		
		if( retornarExemplares &&  retornarFasciculos){		
			
			StringBuilder sql = new StringBuilder();
			
			sql.append("SELECT  sum(sub.titulos) as qtdTitulos, sum(sub.exemplares)  as qtdExemplares, sum(sub.fasciculos)  as qtdFasciculos "+", sub."+agrupamento1.nomeCampo+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ?  ", sub."+agrupamento2.nomeCampo: "")+" FROM (");
			sql.append(sqlExemplares.toString() );
			sql.append(" UNION ALL ( ");
			sql.append(sqlFasciculos.toString() );
			sql.append(" ) ");
			sql.append(" ) as sub GROUP BY sub."+agrupamento1.nomeCampo+( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ?  ", sub."+agrupamento2.nomeCampo : "" ) +" ");
			sql.append(" ORDER BY " +agrupamento1.nomeCampo+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento2.nomeCampo : "" )   );
			
			
			Query q = getSession().createSQLQuery(sql.toString());
			q.setParameter("classeInicial", classeInicial);
			q.setParameter("classeFinal", classeFinal);
			
			@SuppressWarnings("unchecked")
			List <Object []> rs = q.list();
			return rs;
		}
		
		return new ArrayList<Object[]>();
	}
	
	
	
	
	
	
	/**
	 * <p>Retorna a quantidade de materiais dentro de uma faixa de classe CDU ou Black, obedecendo aos filtros passados.</p>
	 *
	 * <p>Necess�rio para calcular a p�gina do relat�rio</p>
	 *
	 */
	public int countPorFaixaDeClassificacaoAnalitico(  
			Collection<Integer> idsBibliotecas
			, Collection<Integer> idsTiposMateriais
			, Collection<Integer> idsColecoes
			, Collection<Integer> idsSituacoesMateriais
			, Collection<Integer> idsFormasDocumento, 
			String classeInicial, String classeFinal, 
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos )
			throws DAOException {
		
		if ( ! retornarExemplares && ! retornarFasciculos )
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido.");
		
		int total =  ((BigInteger) executaConsultaPadraoPorFaixaDeClassificacaoAnalitico(idsBibliotecas, idsTiposMateriais, idsColecoes
				, idsSituacoesMateriais, idsFormasDocumento, classeInicial, classeFinal, classificacao, retornarExemplares, retornarFasciculos
				, 0, 0, true)).intValue();
		
		return total;	
	}
	
	
	
	
	/**
	 * <p>Retorna, <strong>de forma paginada</strong>, todos os materiais que sejam do tipo e biblioteca informados e que possuam t�tulos
	 * catalogr�ficos cujas classes est�o dentro da faixa informada; e se a faixa ser� Black ou CDU. Um limite m�ximo
	 * de materiais por p�gina deve ser passado, al�m da p�gina (iniciando em 1). </p>
	 * 
	 * @param pagina a partir de 1
	 */
	public List<Object[]> findPorFaixaDeClassificacaoAnalitico(Collection<Integer> idsBibliotecas
			, Collection<Integer> idsTiposMateriais
			, Collection<Integer> idsColecoes
			, Collection<Integer> idsSituacoesMateriais
			, Collection<Integer> idsFormasDocumento, 
			String classeInicial, String classeFinal, 
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos, int pagina, int limite )
			throws DAOException {
		
		if ( ! retornarExemplares && ! retornarFasciculos )
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido.");
		
		if ( limite <= 0 )
			throw new IllegalArgumentException("Limite deve ser maior que zero.");
		if ( pagina < 0 )
			throw new IllegalArgumentException("P�gina deve ser maior que zero.");
		
		@SuppressWarnings("unchecked")
		List<Object[]> retorno = (List<Object[]>) executaConsultaPadraoPorFaixaDeClassificacaoAnalitico(idsBibliotecas, idsTiposMateriais, idsColecoes
				, idsSituacoesMateriais, idsFormasDocumento, classeInicial, classeFinal, classificacao, retornarExemplares, retornarFasciculos
				, pagina, limite, false);
		return retorno;
	}

	
	
	
	
	/**
	 * <p> M�todo que cont�m a consulta padr�o do relat�rio por faixa de classifica��o anal�tico </p>
	 * 
	 * <p> S� � preciso passar a proje��o que ser� realizada, se um "count" ou a consulta normal. </p>
	 * 
	 * @param pagina a partir de 1
	 */
	private Object executaConsultaPadraoPorFaixaDeClassificacaoAnalitico(Collection<Integer> idsBibliotecas
			, Collection<Integer> idsTiposMateriais
			, Collection<Integer> idsColecoes
			, Collection<Integer> idsSituacoesMateriais
			, Collection<Integer> idsFormasDocumento, 
			String classeInicial, String classeFinal,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos, int pagina, int limite , boolean contarQuantidadeResulatdos)
			throws DAOException {
		
		StringBuilder sql = new StringBuilder(" SELECT ");
		
		if(contarQuantidadeResulatdos){
			sql.append( " count(DISTINCT material.id_material_informacional) " );
		}else{
			sql.append("	material.codigo_barras, "+
					"	CASE WHEN cache.autor  IS NULL THEN '' ELSE cache.autor  || '. '  END || " +
					"	CASE WHEN cache.titulo IS NULL THEN '' ELSE cache.titulo || '.- ' END || " +
					"	CASE WHEN cache.edicao IS NULL THEN '' ELSE cache.edicao || '. '  END || " +
					"	CASE WHEN cache.ano    IS NULL THEN '' ELSE cache.ano             END, "   +
					"	titulo."+classificacao.getColunaClassificacao() );
		}
		
		sql.append(" FROM biblioteca.titulo_catalografico AS titulo "+
				   " INNER JOIN biblioteca.cache_entidades_marc AS cache ON cache.id_titulo_catalografico = titulo.id_titulo_catalografico ");
		
		if( retornarExemplares ){
			sql.append(" LEFT  JOIN biblioteca.exemplar AS exemplar ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
		}
		
		if( retornarFasciculos ){
			sql.append("LEFT JOIN biblioteca.assinatura AS assinatura ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("LEFT JOIN biblioteca.fasciculo AS fasciculo ON fasciculo.id_assinatura = assinatura.id_assinatura  ");
			
		}
		
		sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional =  ");
	
		
		if ( retornarExemplares && retornarFasciculos )
			sql.append(" COALESCE( exemplar.id_exemplar, fasciculo.id_fasciculo )");
		else if ( retornarExemplares )
			sql.append(" exemplar.id_exemplar ");
		else
			sql.append(" fasciculo.id_fasciculo ");
		
		sql.append(" INNER JOIN biblioteca.situacao_material_informacional AS situacao " +
		"		ON (situacao.id_situacao_material_informacional = material.id_situacao_material_informacional) ");
		
		
		if ( idsFormasDocumento != null && ! idsFormasDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}		
		
		sql.append(" WHERE ");
		
		sql.append(" titulo.ativo = trueValue() ");
		sql.append(" AND situacao.situacao_de_baixa = falseValue() ");
		sql.append(" AND material.ativo = trueValue() ");
		
		if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
		}
		
		if ( idsTiposMateriais != null && ! idsTiposMateriais.isEmpty() ) {
			sql.append(" AND material.id_tipo_material IN ("+ StringUtils.join(idsTiposMateriais, ',') +") ");
		}
		
		if ( idsColecoes != null && ! idsColecoes.isEmpty() ) {
			sql.append(" AND material.id_colecao IN ("+ StringUtils.join(idsColecoes, ',') +") ");
		}
		
		if ( idsSituacoesMateriais != null && ! idsSituacoesMateriais.isEmpty() ) {
			sql.append(" AND material.id_situacao_material_informacional IN ("+ StringUtils.join(idsSituacoesMateriais, ',') +") ");
		}
		
		if ( idsFormasDocumento != null && ! idsFormasDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(idsFormasDocumento, ',') +") ");
		}
		
		sql.append(" AND titulo."+classificacao.getColunaClassificacao()+" >= :classeInicial AND titulo."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
		
		if(! contarQuantidadeResulatdos)
		sql.append("ORDER BY titulo."+classificacao.getColunaClassificacao()); // Se o relat�rio � de classifica��o, o mais l�gico que seja ordenado por classifica��o

		Query q = getSession().createSQLQuery( sql.toString());
		q.setParameter("classeInicial", classeInicial);
		q.setParameter("classeFinal", classeFinal);
		
		if(!contarQuantidadeResulatdos){
			q.setFirstResult((pagina - 1) * limite);
			q.setMaxResults(limite);
		}
		
		if(contarQuantidadeResulatdos){
			return  q.uniqueResult();
			
		}else{
			return  q.list();
		}
		
	}
}
