/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
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
 * <p>Relatório exclusivo para o relatório por faixa de código de barras.</p>
 *
 * <p> <i> Possui dois métodos porque podem ser emitidos dois tipos de relatórios diferentes: a analítica e o sintético.</i> </p>
 * 
 * @author felipe
 *
 */
public class RelatorioListagemPorFaixaCodigoBarraDAO extends GenericSigaaDAO {

	
	/**
	 * Consulta que gera os dados do relatório sintetico (apenas quantidades) de Títulos e Exemplares por Faixa de Código de Barras.
	 * 
	 */
	public List<Object[]> findPorFaixaCodigoBarraSintetico(Collection<Integer> idsBibliotecas
			, Date inicioPeriodo, Date fimPeriodo, String codigoBarra 
			, boolean retornarExemplares, boolean retornarFasciculos
			, AgrupamentoRelatoriosBiblioteca agrupamento) throws DAOException {

		if ( ! retornarExemplares && ! retornarFasciculos )
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido."); // Erro do programador
		
		if ( agrupamento == null )
			throw new IllegalArgumentException("Escolha o agrupamento do relatório."); // Erro do programador

		List<Object[]> retorno = executaConsultaPadraoPorFaixaCodigoBarraSintetico(idsBibliotecas, inicioPeriodo, 
				fimPeriodo, codigoBarra, retornarExemplares, retornarFasciculos, agrupamento, false);
		return retorno;
	}
	
	/**
	 * <p> Método que contém a consulta padrão do relatório por faixa de código de barras sintético </p>
	 * 
	 * <p> Só é preciso passar a projeção que será realizada, se um "count" ou a consulta normal. </p>
	 * 
	 * @param pagina a partir de 1
	 * 
	 * @param idsBibliotecas
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param codigoBarra
	 * @param retornarExemplares
	 * @param retornarFasciculos
	 * @param agrupamento
	 * @param contarQuantidadeResultados
	 * @return
	 * @throws DAOException
	 */
	private List<Object[]> executaConsultaPadraoPorFaixaCodigoBarraSintetico(Collection<Integer> idsBibliotecas, 
			Date inicioPeriodo, Date fimPeriodo, String codigoBarra, boolean retornarExemplares, boolean retornarFasciculos, 
			AgrupamentoRelatoriosBiblioteca agrupamento, boolean contarQuantidadeResultados)
			throws DAOException {

		StringBuilder sqlGeral = null;
		StringBuilder sqlExemplar = montarSQLEspecificoSintetico(idsBibliotecas, inicioPeriodo, fimPeriodo, codigoBarra, agrupamento, contarQuantidadeResultados, true);
		StringBuilder sqlFasciculo = montarSQLEspecificoSintetico(idsBibliotecas, inicioPeriodo, fimPeriodo, codigoBarra, agrupamento, contarQuantidadeResultados, false);
		
		if (retornarExemplares && retornarFasciculos) {
			if (contarQuantidadeResultados) {
				sqlGeral = new StringBuilder(" SELECT sub.classificacao, sum(sub.qtdTitulos) as qtdTitulosExterno FROM (" + sqlExemplar.toString() + " UNION ALL  " + sqlFasciculo.toString() + " ) as sub GROUP BY sub.classificacao ORDER BY sub.classificacao ");
			}
			else {
				sqlGeral = new StringBuilder(" SELECT sum(sub.titulos) as qtdTitulos, sum(sub.exemplares)  as qtdExemplares, sum(sub.fasciculos)  as qtdFasciculos " + ", sub."+agrupamento.nomeCampo + " FROM (" + sqlExemplar.toString() + " UNION ALL  " + sqlFasciculo.toString() + " ) as sub GROUP BY sub." + agrupamento.nomeCampo + " ORDER BY " + agrupamento.nomeCampo);
			}
		}
		else if (retornarExemplares) {
			if (!contarQuantidadeResultados) {
				sqlExemplar.append(" ORDER BY " + agrupamento.campoAgrupamento);
			}
			
			sqlGeral = sqlExemplar;
		}
		else if (retornarFasciculos) {
			if (!contarQuantidadeResultados) {
				sqlFasciculo.append(" ORDER BY " + agrupamento.campoAgrupamento);
			}
			
			sqlGeral = sqlFasciculo;
		}
		else {
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido.");
		}
		
		Query q = getSession().createSQLQuery(sqlGeral.toString());

		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		q.setString("codigoBarra", codigoBarra + "%");
		
		@SuppressWarnings("unchecked")
		List<Object[]> retorno = q.list();

		return retorno;
		
	}

	/**
	 * Monta o SQL específico do tipo de material (exemplar ou fascículo) da consulta padrão por faixa de código de barras sintética. O tipo
	 * de material é definido pelo parâmetro <i>isExemplar</i>, onde o valor <i>true</i> indica <i>Exemplar</i>, e <i>false</i> indica 
	 * <i>Fascículo</i>. Também é necessário indicar o tipo de projeção (count ou consulta normal) através do parâmetro 
	 * <i>contarQuantidadeResultados</i>.
	 * 
	 * @param idsBibliotecas
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param codigoBarra
	 * @param agrupamento
	 * @param contarQuantidadeResultados
	 * @param isExemplar
	 * @return
	 */
	private StringBuilder montarSQLEspecificoSintetico(Collection<Integer> idsBibliotecas, 
			Date inicioPeriodo, Date fimPeriodo, String codigoBarra,
			AgrupamentoRelatoriosBiblioteca agrupamento, 
			boolean contarQuantidadeResultados, boolean isExemplar) {

		StringBuilder sqlEspecifico = new StringBuilder(" SELECT ");
		
		if (contarQuantidadeResultados) {
			sqlEspecifico.append(" COALESCE( "+agrupamento.campoAgrupamento+", 'Sem Classe'  ) AS classificacao , ");
			sqlEspecifico.append(" COUNT(DISTINCT titulo.id_titulo_catalografico) AS qtdTitulos ");
		} else {
			sqlEspecifico.append(" COUNT(DISTINCT titulo.id_titulo_catalografico) AS titulos, ");
			
			if (isExemplar) {
				sqlEspecifico.append(" COUNT(DISTINCT exe.id_exemplar) AS exemplares, 0 AS fasciculos, ");
			} else {
				sqlEspecifico.append(" 0 AS exemplares, count(DISTINCT fas.id_fasciculo) AS fasciculos, ");
			}
			
			sqlEspecifico.append(" COALESCE( "+agrupamento.campoAgrupamento+", 'Sem Classe'  ) AS "+agrupamento.nomeCampo);
		}
		
		sqlEspecifico.append(" FROM biblioteca.titulo_catalografico AS titulo ");
		
		if (isExemplar) {
			sqlEspecifico.append(" INNER JOIN biblioteca.exemplar AS exe ON exe.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sqlEspecifico.append(" INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exe.id_exemplar ");
		} else {
			sqlEspecifico.append(" INNER JOIN biblioteca.assinatura AS ass ON ass.id_titulo_catalografico = titulo.id_titulo_catalografico " +
								 " INNER JOIN biblioteca.fasciculo AS fas ON fas.id_assinatura = ass.id_assinatura ");
			sqlEspecifico.append(" INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fas.id_fasciculo ");
		}
		
		sqlEspecifico.append(" INNER JOIN biblioteca.situacao_material_informacional AS sit ON sit.id_situacao_material_informacional = material.id_situacao_material_informacional ");

		if (!contarQuantidadeResultados) {
			sqlEspecifico.append(agrupamento.join);
		}
		
		sqlEspecifico.append(" WHERE titulo.ativo = trueValue()	AND sit.situacao_de_baixa = falseValue() AND material.ativo = trueValue() ");
		
		if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
			sqlEspecifico.append(" AND material.id_biblioteca IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
		}
		
		if ( ! StringUtils.isEmpty(codigoBarra) ) {
			sqlEspecifico.append(" AND material.codigo_barras ILIKE :codigoBarra ");
		}
		
		if ( inicioPeriodo != null ) {
			sqlEspecifico.append(" AND material.data_criacao >= :inicioPeriodo ");
		}
		
		if ( fimPeriodo != null ) {
			sqlEspecifico.append(" AND material.data_criacao <= :fimPeriodo ");			
		}
			
		sqlEspecifico.append(" GROUP BY "+agrupamento.campoAgrupamento);
		
		return sqlEspecifico;
		
	}
	
	
	/**
	 * <p>Retorna a quantidade de materiais dentro de uma faixa de código de barras, obedecendo aos filtros passados.</p>
	 *
	 * <p>Necessário para calcular a página do relatório analítico</p>
	 *
	 */
	public int countPorCodigoBarraAnalitico(Collection<Integer> idsBibliotecas, 
			Date inicioPeriodo, Date fimPeriodo, String codigoBarra,
			boolean retornarExemplares, boolean retornarFasciculos )
			throws DAOException {
		
		if ( ! retornarExemplares && ! retornarFasciculos )
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido.");
		
		Object totalObj = executaConsultaPadraoPorFaixaCodigoBarraAnalitico(idsBibliotecas, inicioPeriodo, fimPeriodo, 
				codigoBarra, retornarExemplares, retornarFasciculos, 0, 0, true);
		
		int total =  (totalObj instanceof BigDecimal ? (BigDecimal) totalObj : (BigInteger) totalObj).intValue();
		
		return total;	
	}
	
	
	
	
	/**
	 * <p>Retorna, <strong>de forma paginada</strong>, todos os materiais cujos cadastros tenham sido efetuados no período e na biblioteca 
	 * informados e que tenham o código de barras dentro da faixa informada. Um limite máximo de materiais por página deve ser passado, 
	 * além da página (iniciando em 1). </p>
	 * 
	 * @param pagina a partir de 1
	 */
	public List<Object[]> findPorFaixaCodigoBarraAnalitico(Collection<Integer> idsBibliotecas,
			Date inicioPeriodo, Date fimPeriodo, String codigoBarra,
			boolean retornarExemplares, boolean retornarFasciculos, int pagina, int limite )
			throws DAOException {
		
		if ( ! retornarExemplares && ! retornarFasciculos )
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido.");
		
		if ( limite <= 0 )
			throw new IllegalArgumentException("Limite deve ser maior que zero.");
		if ( pagina < 0 )
			throw new IllegalArgumentException("Página deve ser maior que zero.");
		
		@SuppressWarnings("unchecked")
		List<Object[]> retorno = (List<Object[]>) executaConsultaPadraoPorFaixaCodigoBarraAnalitico(idsBibliotecas, inicioPeriodo, 
				fimPeriodo, codigoBarra, retornarExemplares, retornarFasciculos, pagina, limite, false);
		return retorno;
	}

	
	
	
	
	/**
	 * <p> Método que contém a consulta padrão do relatório por faixa de código de barras analítico </p>
	 * 
	 * <p> Só é preciso passar a projeção que será realizada, se um "count" ou a consulta normal. </p>
	 * 
	 * @param pagina a partir de 1
	 */
	private Object executaConsultaPadraoPorFaixaCodigoBarraAnalitico(Collection<Integer> idsBibliotecas, 
			Date inicioPeriodo, Date fimPeriodo, String codigoBarra,
			boolean retornarExemplares, boolean retornarFasciculos, int pagina, int limite , boolean contarQuantidadeResultados)
			throws DAOException {
		
		StringBuilder sqlGeral = null;
		StringBuilder sqlExemplar = montarSQLEspecificoAnalitico(idsBibliotecas, inicioPeriodo, fimPeriodo, codigoBarra, contarQuantidadeResultados, true);
		StringBuilder sqlFasciculo = montarSQLEspecificoAnalitico(idsBibliotecas, inicioPeriodo, fimPeriodo, codigoBarra, contarQuantidadeResultados, false);
		
		if (retornarExemplares && retornarFasciculos) {
			if (contarQuantidadeResultados) {
				sqlGeral = new StringBuilder(" SELECT SUM(carrie) FROM (" + sqlExemplar.toString() + " UNION ALL  " + sqlFasciculo.toString() + ") unido ");
			}
			else {
				sqlGeral = new StringBuilder(" SELECT * FROM (" + sqlExemplar.toString() + " UNION ALL  " + sqlFasciculo.toString() + ") unido ORDER BY codigo_barras ");
			}
		}
		else if (retornarExemplares) {
			if (!contarQuantidadeResultados) {
				sqlExemplar.append(" ORDER BY material.codigo_barras ");
			}
			
			sqlGeral = sqlExemplar;
		}
		else if (retornarFasciculos) {
			if (!contarQuantidadeResultados) {
				sqlFasciculo.append(" ORDER BY material.codigo_barras ");
			}
			
			sqlGeral = sqlFasciculo;
		}
		else {
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido.");
		}
		
		Query q = getSession().createSQLQuery(sqlGeral.toString());

		q.setTimestamp("inicioPeriodo", inicioPeriodo);
		q.setTimestamp("fimPeriodo", fimPeriodo);
		q.setString("codigoBarra", codigoBarra + "%");
				
		if(!contarQuantidadeResultados){
			q.setFirstResult((pagina - 1) * limite);
			q.setMaxResults(limite);
		}
		
		if(contarQuantidadeResultados) {
			return q.uniqueResult();
		} else {
			return q.list();
		}
		
	}
	
	
	/**
	 * <p>Retorna a quantidade de materiais dentro de uma faixa de código de barras, obedecendo aos filtros passados.</p>
	 *
	 * <p>Necessário para calcular a página do relatório analítico</p>
	 *
	 */
	public int countTitulosPorCodigoBarraAnalitico(Collection<Integer> idsBibliotecas, 
			Date inicioPeriodo, Date fimPeriodo, String codigoBarra,
			boolean retornarExemplares, boolean retornarFasciculos )
			throws DAOException {
		
		if ( ! retornarExemplares && ! retornarFasciculos )
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido.");
		
		StringBuilder sqlGeral = null;
		StringBuilder sqlExemplar = montarSQLEspecificoAnalitico(idsBibliotecas, inicioPeriodo, fimPeriodo, codigoBarra, true, true, true);
		StringBuilder sqlFasciculo = montarSQLEspecificoAnalitico(idsBibliotecas, inicioPeriodo, fimPeriodo, codigoBarra, true, false, true);
		
		if (retornarExemplares && retornarFasciculos) {
			sqlGeral = new StringBuilder(" SELECT SUM(carrie) FROM (" + sqlExemplar.toString() + " UNION ALL  " + sqlFasciculo.toString() + ") unido ");
		}
		else if (retornarExemplares) {
			sqlGeral = sqlExemplar;
		}
		else if (retornarFasciculos) {
			sqlGeral = sqlFasciculo;
		}
		else {
			throw new IllegalArgumentException("Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido.");
		}
		
		Query q = getSession().createSQLQuery(sqlGeral.toString());

		q.setTimestamp("inicioPeriodo", inicioPeriodo);
		q.setTimestamp("fimPeriodo", fimPeriodo);
		q.setString("codigoBarra", codigoBarra + "%");
		
		Object totalObj =  q.uniqueResult();
		
		int total =  (totalObj instanceof BigDecimal ? (BigDecimal) totalObj : (BigInteger) totalObj).intValue();
		
		return total;	
	}


	/**
	 * Monta o SQL específico do tipo de material (exemplar ou fascículo) da consulta padrão por faixa de código de barras analítica. O tipo
	 * de material é definido pelo parâmetro <i>isExemplar</i>, onde o valor <i>true</i> indica <i>Exemplar</i>, e <i>false</i> indica 
	 * <i>Fascículo</i>. Também é necessário indicar o tipo de projeção (count ou consulta normal) através do parâmetro 
	 * <i>contarQuantidadeResultados</i>.
	 * 
	 * @param idsBibliotecas
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param codigoBarra
	 * @param contarQuantidadeResultados
	 * @param isExemplar
	 * @return
	 */
	private StringBuilder montarSQLEspecificoAnalitico(Collection<Integer> idsBibliotecas, 
			Date inicioPeriodo, Date fimPeriodo, String codigoBarra,
			boolean contarQuantidadeResultados, boolean isExemplar) {
		return montarSQLEspecificoAnalitico(idsBibliotecas, inicioPeriodo, fimPeriodo, codigoBarra, contarQuantidadeResultados, 
				isExemplar, false);
	}


		/**
		 * Monta o SQL específico do tipo de material (exemplar ou fascículo) da consulta padrão por faixa de código de barras analítica. O tipo
		 * de material é definido pelo parâmetro <i>isExemplar</i>, onde o valor <i>true</i> indica <i>Exemplar</i>, e <i>false</i> indica 
		 * <i>Fascículo</i>. Também é necessário indicar o tipo de projeção (count ou consulta normal) através do parâmetro 
		 * <i>contarQuantidadeResultados</i>. O parâmetro <i>isTitulos</i> é utilizado para indicar se a contagem será de títulos ou de 
		 * materiais.
		 * 
		 * @param idsBibliotecas
		 * @param inicioPeriodo
		 * @param fimPeriodo
		 * @param codigoBarra
		 * @param contarQuantidadeResultados
		 * @param isExemplar
		 * @param isTitulos
		 * @return
		 */
		private StringBuilder montarSQLEspecificoAnalitico(Collection<Integer> idsBibliotecas, 
				Date inicioPeriodo, Date fimPeriodo, String codigoBarra,
				boolean contarQuantidadeResultados, boolean isExemplar, boolean isTitulos) {

		StringBuilder sqlEspecifico = new StringBuilder(" SELECT ");
		
		if(contarQuantidadeResultados) {
			sqlEspecifico.append( " count(DISTINCT " + (isTitulos ? "titulo.id_titulo_catalografico" : "material.id_material_informacional") + ") as carrie " );
		} else {
			sqlEspecifico.append("	material.codigo_barras, "+
					"	CASE WHEN cache.autor  IS NULL THEN '' ELSE cache.autor  || '. '  END || " +
					"	CASE WHEN cache.titulo IS NULL THEN '' ELSE cache.titulo || '.- ' END || " +
					"	CASE WHEN cache.edicao IS NULL THEN '' ELSE cache.edicao || '. '  END || " +
					"	CASE WHEN cache.ano    IS NULL THEN '' ELSE cache.ano             END AS descricao, "   +
					"	titulo.classificacao_1, " +
					"	titulo.classificacao_2, " +
					"	titulo.classificacao_3 ");
		}

		sqlEspecifico.append(" FROM biblioteca.titulo_catalografico AS titulo "+
		   " INNER JOIN biblioteca.cache_entidades_marc AS cache ON cache.id_titulo_catalografico = titulo.id_titulo_catalografico ");
		
		if( isExemplar ){
			sqlEspecifico.append(" INNER JOIN biblioteca.exemplar AS exemplar ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			
			sqlEspecifico.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exemplar.id_exemplar ");
		} else {
			sqlEspecifico.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sqlEspecifico.append("INNER JOIN biblioteca.fasciculo AS fasciculo ON fasciculo.id_assinatura = assinatura.id_assinatura  ");
			
			sqlEspecifico.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		}

		sqlEspecifico.append(" INNER JOIN biblioteca.situacao_material_informacional AS situacao " +
		"		ON (situacao.id_situacao_material_informacional = material.id_situacao_material_informacional) ");

		sqlEspecifico.append(" WHERE ");

		sqlEspecifico.append(" titulo.ativo = trueValue() ");
		sqlEspecifico.append(" AND situacao.situacao_de_baixa = falseValue() ");
		sqlEspecifico.append(" AND material.ativo = trueValue() ");
		
		if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
			sqlEspecifico.append(" AND material.id_biblioteca IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
		}
		
		if ( ! StringUtils.isEmpty(codigoBarra) ) {
			sqlEspecifico.append(" AND material.codigo_barras ILIKE :codigoBarra ");
		}
		
		if ( inicioPeriodo != null ) {
			sqlEspecifico.append(" AND material.data_criacao >= :inicioPeriodo ");
		}
		
		if ( fimPeriodo != null ) {
			sqlEspecifico.append(" AND material.data_criacao <= :fimPeriodo ");			
		}
		
		return sqlEspecifico;
	}
}
