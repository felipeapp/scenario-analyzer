/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;

/**
 *
 * <p>Dao exclusivo para a consulta do relatório</p>
 * 
 * @author jadson
 *
 */
public class RelatorioTotalTitulosMateriaisDao extends GenericSigaaDAO{

	
	/**
	 * <p>Método que retorna a quantidade real de Títulos no acervo. </p>  
	 * <p>Agrupado pela classificação do agrupamento 1.  Caso seja uma agrupamento de classificação. </p> 
	 *
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<Object[]> countTotalTitulosAcervo(Collection<Integer> bibliotecas, 
			Collection<Integer> colecoes, Collection<Integer> tiposDeMaterial, Collection<Integer> situacoesMaterial, Collection<Integer> formasDocumento,
			Date inicioPeriodo, Date fimPeriodo, boolean contarExemplares, boolean contarFasciculos, AgrupamentoRelatoriosBiblioteca agrupamento1, AgrupamentoRelatoriosBiblioteca agrupamento2) throws DAOException {
		
		// só faz sentido mostrar se o agrupamento 1 for classificação e o agrupamento 2 for escolhido
		if( ( agrupamento1 == AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_1 
				|| agrupamento1 == AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_2
				|| agrupamento1 == AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_3) && ! agrupamento2.isSemAgrupamento() ){
			
			StringBuilder sqlExemplares = new StringBuilder();
			StringBuilder sqlFasciculos = new StringBuilder();
			
			if ( contarExemplares ) {
				
				sqlExemplares.append(" SELECT ");
				sqlExemplares.append(" COALESCE( " + agrupamento1.campoAgrupamento + ", '" + agrupamento1.substituiValoresNull + "' ) AS "+agrupamento1.nomeCampo+" , ");
				sqlExemplares.append(" COUNT(DISTINCT titulo.id_titulo_catalografico) AS qtdTitulos ");
				sqlExemplares.append(" FROM biblioteca.titulo_catalografico AS titulo ");
				sqlExemplares.append(" INNER JOIN biblioteca.exemplar AS exe ON exe.id_titulo_catalografico = titulo.id_titulo_catalografico ");
				sqlExemplares.append(" INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exe.id_exemplar ");
				sqlExemplares.append(" INNER JOIN biblioteca.situacao_material_informacional AS sit ON sit.id_situacao_material_informacional = material.id_situacao_material_informacional ");
				
				if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
					sqlExemplares.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
					" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
				}
				
				if ( colecoes != null && ! colecoes.isEmpty() ) {
					sqlExemplares.append(" INNER JOIN biblioteca.colecao AS colecao ON colecao.id_colecao = material.id_colecao ");
				}
				
				if ( tiposDeMaterial != null && ! tiposDeMaterial.isEmpty() ) {
					sqlExemplares.append(" INNER JOIN biblioteca.tipo_material AS tipo ON tipo.id_tipo_material = material.id_tipo_material ");
				}
				
				
				sqlExemplares.append(" WHERE titulo.ativo = trueValue()	AND sit.situacao_de_baixa = falseValue() AND material.ativo = trueValue() ");
				
				
				
				if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
					sqlExemplares.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
					
				}
				
				if ( colecoes != null && ! colecoes.isEmpty() ) {
					sqlExemplares.append(" AND material.id_colecao IN ("+ StringUtils.join(colecoes, ',') +") ");
				}
				
				if ( tiposDeMaterial != null && ! tiposDeMaterial.isEmpty() ) {
					sqlExemplares.append(" AND material.id_tipo_material IN ("+ StringUtils.join(tiposDeMaterial, ',') +") ");
				}
				
				
				if ( situacoesMaterial != null && ! situacoesMaterial.isEmpty() ) {
					sqlExemplares.append(" AND material.id_situacao_material_informacional IN ("+ StringUtils.join(situacoesMaterial, ',') +") ");
				}
				
				if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
					sqlExemplares.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
				}
				
				if(inicioPeriodo != null && fimPeriodo != null){
					sqlExemplares.append(" AND ( titulo.data_criacao BETWEEN :dataInicio AND :dataFim ) ");
				}
				if(inicioPeriodo != null && fimPeriodo == null){
					sqlExemplares.append(" AND ( titulo.data_criacao >= :dataInicio ) ");	
				}
				if(inicioPeriodo == null && fimPeriodo != null){
					sqlExemplares.append(" AND ( titulo.data_criacao <= :dataFim ) ");
				}
					
				sqlExemplares.append(" GROUP BY " +agrupamento1.campoAgrupamento);
			}
			
			if ( contarFasciculos ) {
				
				sqlFasciculos.append(" SELECT ");
				sqlFasciculos.append(" COALESCE( " + agrupamento1.campoAgrupamento + ", '" + agrupamento1.substituiValoresNull + "' ) AS "+agrupamento1.nomeCampo+" , ");
				sqlFasciculos.append(" COUNT(DISTINCT titulo.id_titulo_catalografico) AS qtdTitulos ");
				sqlFasciculos.append(" FROM biblioteca.titulo_catalografico AS titulo ");
				sqlFasciculos.append(" INNER JOIN biblioteca.assinatura AS ass ON ass.id_titulo_catalografico = titulo.id_titulo_catalografico " +
									 " INNER JOIN biblioteca.fasciculo AS fas ON fas.id_assinatura = ass.id_assinatura ");
				sqlFasciculos.append(" INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fas.id_fasciculo ");
				sqlFasciculos.append(" INNER JOIN biblioteca.situacao_material_informacional AS sit ON sit.id_situacao_material_informacional = material.id_situacao_material_informacional ");
				
				if ( colecoes != null && ! colecoes.isEmpty() ) {
					sqlFasciculos.append(" INNER JOIN biblioteca.colecao AS colecao ON colecao.id_colecao = material.id_colecao ");
				}
				
				if ( tiposDeMaterial != null && ! tiposDeMaterial.isEmpty() ) {
					sqlFasciculos.append(" INNER JOIN biblioteca.tipo_material AS tipo ON tipo.id_tipo_material = material.id_tipo_material ");
				}
				
				if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
					sqlFasciculos.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
					" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
				}
				
				
				sqlFasciculos.append(" WHERE titulo.ativo = trueValue()	AND sit.situacao_de_baixa = falseValue() AND material.ativo = trueValue() ");
				
				if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
					sqlFasciculos.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
					
				}
				
				if ( colecoes != null && ! colecoes.isEmpty() ) {
					sqlFasciculos.append(" AND material.id_colecao IN ("+ StringUtils.join(colecoes, ',') +") ");
				}
				
				if ( tiposDeMaterial != null && ! tiposDeMaterial.isEmpty() ) {
					sqlFasciculos.append(" AND material.id_tipo_material IN ("+ StringUtils.join(tiposDeMaterial, ',') +") ");
				}
				
				if ( situacoesMaterial != null && ! situacoesMaterial.isEmpty() ) {
					sqlFasciculos.append(" AND material.id_situacao_material_informacional IN ("+ StringUtils.join(situacoesMaterial, ',') +") ");
				}
				
				if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
					sqlFasciculos.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
				}
				
				if(inicioPeriodo != null && fimPeriodo != null){
					sqlFasciculos.append(" AND ( titulo.data_criacao BETWEEN :dataInicio AND :dataFim ) ");
				}
				if(inicioPeriodo != null && fimPeriodo == null){
					sqlFasciculos.append(" AND ( titulo.data_criacao >= :dataInicio ) ");	
				}
				if(inicioPeriodo == null && fimPeriodo != null){
					sqlFasciculos.append(" AND ( titulo.data_criacao <= :dataFim ) ");
				}
					
				sqlFasciculos.append(" GROUP BY " +agrupamento1.campoAgrupamento);
			}
			
			
			StringBuilder sql = new StringBuilder();
			
			if(contarExemplares && ! contarFasciculos){
				sql.append(sqlExemplares.toString());
			}
			
			if(contarFasciculos && ! contarExemplares){
				sql.append(sqlFasciculos.toString());
			}
			
			if(contarExemplares && contarFasciculos){
				sql.append("SELECT sub."+agrupamento1.nomeCampo+", sum(sub.qtdTitulos) as qtdTitulosExterno FROM (");
				sql.append(sqlExemplares.toString() );
				sql.append(" UNION ALL  ( ");
				sql.append(sqlFasciculos.toString() );
				sql.append(" ) ");
				sql.append(" ) as sub GROUP BY sub."+agrupamento1.nomeCampo);
				sql.append(" ORDER BY " +agrupamento1.nomeCampo);
			}
			
			Query q = getSession().createSQLQuery(sql.toString() );
			
			if(inicioPeriodo != null && fimPeriodo != null){
				q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
				q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
			}
			
			if(inicioPeriodo != null && fimPeriodo == null)
				q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
			
			if(inicioPeriodo == null && fimPeriodo != null)
				q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			return lista;
			
		}else{
			return new ArrayList<Object[]>();
		}
		
	}
	
	
	/**
	 * Retorna a quantidade de títulos, exemplares e fascículos. Os dados são agrupados de acordo
	 * com o parâmetro <em>agrupamentos</em>.
	 * 
	 * @return { agrupamento1, [ agrupamento2, [ ... ] ], títulos, [exemplares], [fascículos] }
	 */
	public List< Object[] > countTotalTitulosEMateriaisAcervo(Collection<Integer> bibliotecas, Date inicio, Date fim,
			Collection<Integer> colecoes, Collection<Integer> tiposDeMaterial,
			Collection<Integer> situacoesMaterial, Collection<Integer> formasDocumento,
			boolean exemplares, boolean fasciculos, AgrupamentoRelatoriosBiblioteca agrupamento1, AgrupamentoRelatoriosBiblioteca agrupamento2 ) throws DAOException {
		
		//// Validação dos argumentos ////
		
		if ( ! exemplares && ! fasciculos )
			throw new DAOException(new IllegalArgumentException(
					"Pelo menos um entre 'exemplares' e 'fasciculos' deve ser escolhido."));
		
		
		//// SELECT ////
		
		StringBuilder selectExemplares = new StringBuilder("SELECT ");
		StringBuilder selectFasciculos = new StringBuilder("SELECT ");
		
		
		selectExemplares.append("	COALESCE( " + agrupamento1.campoAgrupamento + ", '" + agrupamento1.substituiValoresNull + "' ) AS "+agrupamento1.nomeCampo+" , ");
		
		if(agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO )
			selectExemplares.append("	COALESCE( " + agrupamento2.campoAgrupamento + ", '" + agrupamento2.substituiValoresNull + "' ) AS "+agrupamento2.nomeCampo+" , ");
		
		selectFasciculos.append("	COALESCE( " + agrupamento1.campoAgrupamento + ", '" + agrupamento1.substituiValoresNull + "' ) AS "+agrupamento1.nomeCampo+" , ");
		
		if(agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO )
			selectFasciculos.append("	COALESCE( " + agrupamento2.campoAgrupamento + ", '" + agrupamento2.substituiValoresNull + "' ) AS "+agrupamento2.nomeCampo+" , ");
		
		
		selectExemplares.append("	COUNT(DISTINCT titulo.id_titulo_catalografico) AS titulos_com_materiais ");
		selectFasciculos.append("	COUNT(DISTINCT titulo.id_titulo_catalografico) AS titulos_com_materiais ");
		
		if ( exemplares ) {
			selectExemplares.append("	,COUNT(DISTINCT exe.id_exemplar) AS exemplares ");
			selectExemplares.append("	,0 AS fasiculos ");
		}
		
		if ( fasciculos ) {
			selectFasciculos.append("	,0 AS exemplares ");
			selectFasciculos.append("	,COUNT(DISTINCT fas.id_fasciculo) AS fasciculos ");
		}
		
		//// FROM ////
		
		selectExemplares.append(" FROM biblioteca.titulo_catalografico AS titulo ");
		selectFasciculos.append(" FROM biblioteca.titulo_catalografico AS titulo ");
		
		if ( exemplares ) {
			selectExemplares.append(" INNER JOIN biblioteca.exemplar AS exe ON exe.id_titulo_catalografico = titulo.id_titulo_catalografico ");
		}
		
		if ( fasciculos ) {
			selectFasciculos.append("	INNER JOIN biblioteca.assinatura AS ass ON ass.id_titulo_catalografico = titulo.id_titulo_catalografico " +
									"	INNER JOIN biblioteca.fasciculo AS fas 	ON fas.id_assinatura = ass.id_assinatura ");
		}
		
		/* INNER JOIN materiais */ 
		selectExemplares.append("	INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exe.id_exemplar ");
		selectFasciculos.append("	INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fas.id_fasciculo ");
			

		
		
		selectExemplares.append("	INNER JOIN biblioteca.situacao_material_informacional AS sit ON sit.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		selectFasciculos.append("	INNER JOIN biblioteca.situacao_material_informacional AS sit ON sit.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		
		if ( colecoes != null && ! colecoes.isEmpty()  && ! agrupamento1.isAgrupamentoColecao() && ! agrupamento2.isAgrupamentoColecao() ) {
			selectExemplares.append(" INNER JOIN biblioteca.colecao AS colecao ON colecao.id_colecao = material.id_colecao ");
			selectFasciculos.append(" INNER JOIN biblioteca.colecao AS colecao ON colecao.id_colecao = material.id_colecao ");
		}
		
		if ( tiposDeMaterial != null && ! tiposDeMaterial.isEmpty() && ! agrupamento1.isAgrupamentoTipoMaterial() && ! agrupamento2.isAgrupamentoTipoMaterial()) {
			selectExemplares.append(" INNER JOIN biblioteca.tipo_material AS tipo ON tipo.id_tipo_material = material.id_tipo_material  ");
			selectFasciculos.append(" INNER JOIN biblioteca.tipo_material AS tipo ON tipo.id_tipo_material = material.id_tipo_material  ");
		}
		
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			selectExemplares.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
			selectFasciculos.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		
		selectExemplares.append(agrupamento1.join);
		selectExemplares.append(agrupamento2.join);
		
		selectFasciculos.append(agrupamento1.join);
		selectFasciculos.append(agrupamento2.join);
		
		
		//// WHERE ////
		selectExemplares.append(" WHERE titulo.ativo = trueValue()	AND sit.situacao_de_baixa = falseValue() AND material.ativo = trueValue() ");
		selectFasciculos.append(" WHERE titulo.ativo = trueValue()	AND sit.situacao_de_baixa = falseValue() AND material.ativo = trueValue() AND fas.incluido_acervo = trueValue() ");
		
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
			selectExemplares.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
			selectFasciculos.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
		}
		
		if ( colecoes != null && ! colecoes.isEmpty() ) {
			selectExemplares.append(" AND material.id_colecao IN ("+ StringUtils.join(colecoes, ',') +") ");
			selectFasciculos.append(" AND material.id_colecao IN ("+ StringUtils.join(colecoes, ',') +") ");
		}
		
		if ( tiposDeMaterial != null && ! tiposDeMaterial.isEmpty() ) {
			selectExemplares.append(" AND material.id_tipo_material IN ("+ StringUtils.join(tiposDeMaterial, ',') +") ");
			selectFasciculos.append(" AND material.id_tipo_material IN ("+ StringUtils.join(tiposDeMaterial, ',') +") ");
		}
		
		if ( situacoesMaterial != null && ! situacoesMaterial.isEmpty() ) {
			selectExemplares.append(" AND material.id_situacao_material_informacional IN ("+ StringUtils.join(situacoesMaterial, ',') +") ");
			selectFasciculos.append(" AND material.id_situacao_material_informacional IN ("+ StringUtils.join(situacoesMaterial, ',') +") ");
		}
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			selectExemplares.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
			selectFasciculos.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
		}
		
		if(inicio != null && fim != null){
			selectExemplares.append(" AND ( ( material.data_criacao BETWEEN :dataInicio AND :dataFim ) OR material.data_criacao IS NULL  )");
			selectFasciculos.append(" AND ( ( material.data_criacao BETWEEN :dataInicio AND :dataFim ) OR material.data_criacao IS NULL  )");
		}
		
		if(inicio != null && fim == null){
			selectExemplares.append(" AND ( ( material.data_criacao >= :dataInicio ) OR material.data_criacao IS NULL ) ");
			selectFasciculos.append(" AND ( ( material.data_criacao >= :dataInicio ) OR material.data_criacao IS NULL ) ");
		}
		
		if(inicio == null && fim != null){
			selectExemplares.append(" AND ( ( material.data_criacao <= :dataFim ) OR material.data_criacao IS NULL ) ");
			selectFasciculos.append(" AND ( ( material.data_criacao <= :dataFim ) OR material.data_criacao IS NULL ) ");
		}
			
		
		selectExemplares.append(" GROUP BY " +agrupamento1.campoAgrupamento+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento2.campoAgrupamento : "" )  );
		selectFasciculos.append(" GROUP BY " +agrupamento1.campoAgrupamento+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento2.campoAgrupamento : "" )  );
		
		StringBuilder sql = new StringBuilder();
		
		if(exemplares && ! fasciculos){
			sql.append(selectExemplares.toString());
		}
		
		if(fasciculos && ! exemplares){
			sql.append(selectFasciculos.toString());
		}
		
		if(exemplares && fasciculos){
			sql.append("SELECT sub."+agrupamento1.nomeCampo+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ?  ", sub."+agrupamento2.nomeCampo: "")+", sum(sub.titulos_com_materiais) as qtdTitulos, sum(sub.exemplares)  as qtdExemplares, sum(sub.fasiculos)  as qtdFasciculos FROM (");
			sql.append(selectExemplares.toString() );
			sql.append(" UNION ALL  ( ");
			sql.append(selectFasciculos.toString() );
			sql.append(" ) ");
			sql.append(" ) as sub GROUP BY sub."+agrupamento1.nomeCampo+( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ?  ", sub."+agrupamento2.nomeCampo : "" ) +" ");
			sql.append(" ORDER BY " +agrupamento1.nomeCampo+ ( agrupamento2 != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", "+agrupamento2.nomeCampo : "" )   );
		}
		
		Query q = getSession().createSQLQuery(sql.toString() );
		
		if(inicio != null && fim != null){
			q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicio, 0, 0, 0, 0));
			q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fim, 23, 59, 59, 999));
		}
		
		if(inicio != null && fim == null)
			q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicio, 0, 0, 0, 0));
		
		if(inicio == null && fim != null)
			q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fim, 23, 59, 59, 999));
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return lista;
		
	}

	
}
