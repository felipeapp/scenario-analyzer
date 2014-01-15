/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.integracao.dto.MaterialInformacionalDTO;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * <p>DAO responsável pela realização de todas as consultas que se encontram no MBean
 * RelatoriosBibliotecaMBean.</p>
 * 
 * <p>
 * <strong>Observação: Usar essa classe apenas para consultas comuns aos relatórios. 
 * Estava ficando difícil a sua manuntenção.  Utilizar daos específicos para cada relatório.
 *  </strong>
 * </p>
 * 
 * @author Fred Castro
 * @author Jadson
 * @author Bráulio
 */
public class RelatoriosBibliotecaDao extends GenericSigaaDAO {

	


	/**
	 * Método que retorna uma coleção contendo os objetos com a contagem de títulos,
	 * exemplares e fascículos por coleção e bibliotecas.
	 */
	public Map<String, Integer[]> findColecaoAcervoByBiblioteca (
			Collection<Integer> bibliotecas, boolean retornarExemplares, boolean retornarFasciculos )
			throws DAOException{
		
		if ( ! ( retornarExemplares || retornarFasciculos ) )
			throw new DAOException( new IllegalArgumentException(
					"Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido.") );
		
		String sqlBiblioteca = "";
		if ( bibliotecas != null && ! bibliotecas.isEmpty() )
			sqlBiblioteca = " AND mat.id_biblioteca IN (" + StringUtils.join(bibliotecas, ", ") + ") ";
		
		String sql =
				"SELECT " +
				"	col.descricao AS nome_colecao, " +
				"	count(DISTINCT tit.id_titulo_catalografico) AS titulos, " +
				( retornarExemplares ?
						"	count(DISTINCT exe.id_exemplar) AS exemplares, " :
						"	0 AS exemplares, " ) +
				( retornarFasciculos ?
						"	count(DISTINCT fasc.id_fasciculo) AS fasciculos "  :
						"	0 as fasciculos " ) +
				"FROM " +
				"	biblioteca.colecao AS col " +
				"	INNER JOIN biblioteca.material_informacional AS mat " +
				"		ON mat.id_colecao = col.id_colecao " +
				( retornarExemplares ?
							"	LEFT JOIN biblioteca.exemplar AS exe " +
							"		ON exe.id_exemplar = mat.id_material_informacional " : "" )+
				( retornarFasciculos ?
						"	LEFT JOIN biblioteca.fasciculo AS fasc "  +
						"		ON fasc.id_fasciculo = mat.id_material_informacional " +
						"	LEFT JOIN biblioteca.assinatura AS asnt " +
						"		ON asnt.id_assinatura = fasc.id_assinatura " : "" ) +
				"	INNER JOIN biblioteca.titulo_catalografico AS tit ";
		
		if ( retornarExemplares && retornarFasciculos )
			sql += 	"		ON tit.id_titulo_catalografico = COALESCE(exe.id_titulo_catalografico, asnt.id_titulo_catalografico) ";
		else if ( retornarExemplares )
			sql += 	"		ON tit.id_titulo_catalografico = exe.id_titulo_catalografico ";
		else if ( retornarFasciculos )
			sql += 	"		ON tit.id_titulo_catalografico = asnt.id_titulo_catalografico ";

		sql +=
				"	INNER JOIN biblioteca.situacao_material_informacional AS sit " +
				"		ON sit.id_situacao_material_informacional = mat.id_situacao_material_informacional " +
				"WHERE " +
				"	mat.ativo = trueValue() " +
				"	AND " +
				"	sit.situacao_de_baixa = falseValue() " +
					sqlBiblioteca +
				"GROUP BY col.descricao, col.ativo " +
				"ORDER BY col.descricao ";

		Query q = getSession().createSQLQuery(sql);
		
		@SuppressWarnings("unchecked")
		List<Object[]> rr = q.list();
		
		Map<String, Integer[]> resultados = ArrayUtils.agrupar(rr , String.class, Integer[].class );
		
		return resultados;
	}

	/**
	 * <p>Método que retorna uma lista contendo as contagens de títulos, materiais, exemplares e
	 * fascículos agrupados por tipo de material e área do CNPq, filtrados por bibliotecas.</p>
	 */
	public List <Object []> findMaterialEspecialCNPQAcervoByBiblioteca(
			Collection<Integer> bibliotecas, boolean retornarExemplares, boolean retornarFasciculos )
			throws DAOException{
		
		if ( ! retornarExemplares && ! retornarFasciculos )
			throw new DAOException(new IllegalArgumentException(
					"Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido."));
		
		String sqlBiblioteca = "";
		if ( bibliotecas != null && !!! bibliotecas.isEmpty() )
			sqlBiblioteca = "			AND material.id_biblioteca IN (" + StringUtils.join( bibliotecas, ", " ) +  ") ";

		String sql =
				"SELECT " +
				"	materiais.tipo, " +
				"	a.sigla_biblioteca, " +
				"	COUNT(DISTINCT materiais.id_material_informacional) AS materiais, " +
				"	COUNT(CASE WHEN materiais.exemplar  THEN materiais.id_material_informacional END) AS exemplares, " +
				"	COUNT(CASE WHEN materiais.fasciculo THEN materiais.id_material_informacional END) As fasciculos, " +
				"	COUNT(DISTINCT titulo.id_titulo_catalografico) AS titulos " +
				"FROM " +
				"	biblioteca.titulo_catalografico   AS titulo " +
				"	LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo.id_area_conhecimento_cnpq " +
				"	JOIN ( " +
				"		SELECT " +
				"			tipo.descricao AS tipo, " +
				"			material.id_material_informacional, " +
				"			COALESCE( " +
								( retornarFasciculos ? "assinatura.id_titulo_catalografico" : "null" ) + ", " +
								( retornarExemplares ? "exemplar.id_titulo_catalografico" : "null" ) + " ) AS id_titulo_catalografico, " +
				( retornarExemplares ?
						"			(CASE " +
						"				WHEN (material.id_material_informacional = exemplar.id_exemplar) THEN trueValue() " +
						"				ELSE falseValue() " +
						"			END) AS exemplar, " :
						"			falseValue() AS exemplar, "
				) +
				( retornarFasciculos ?
					"			(CASE " +
					"				WHEN (material.id_material_informacional = fasciculo.id_fasciculo) THEN trueValue() " +
					"				ELSE falseValue() " +
					"			END) AS fasciculo " :
					"			falseValue() AS fasciculo "
				) +
				"		FROM " +
				"			biblioteca.material_informacional material " +
				"			JOIN biblioteca.tipo_material tipo                       ON tipo.id_tipo_material = material.id_tipo_material " +
				"			JOIN biblioteca.situacao_material_informacional situacao ON (situacao.id_situacao_material_informacional = material.id_situacao_material_informacional) " +
				( retornarFasciculos ?
						"			LEFT JOIN biblioteca.fasciculo fasciculo                 ON (material.id_material_informacional = fasciculo.id_fasciculo) " +
						"			LEFT JOIN biblioteca.assinatura assinatura               ON (fasciculo.id_assinatura = assinatura.id_assinatura) " : ""
				) +
				( retornarExemplares ?
						"			LEFT JOIN biblioteca.exemplar exemplar                   ON ( material.id_material_informacional = exemplar.id_exemplar ) " : ""
				) +
				"		WHERE " +
				"			situacao.situacao_de_baixa = falseValue() AND material.ativo = trueValue() " +
							sqlBiblioteca +
				"	) AS materiais USING (id_titulo_catalografico) " +
				"GROUP BY " +
				"	materiais.tipo, " +
				"	a.sigla_biblioteca ";

		Query q = getSession().createSQLQuery(sql);

		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return lista;
	}

//	/**
//	 * <p>Método que retorna uma lista contendo as contagens de títulos e materiais agrupados por coleção e
//	 * área do CNPq, filtrados por bibliotecas. </p>
//	 */
//	public List<RelatorioBiblioteca> findColecaoCNPQAcervoByBiblioteca(
//			Collection<Integer> bibliotecas, boolean retornarExemplares, boolean retornarFasciculos )
//			throws DAOException {
//		
//		if ( ! retornarExemplares && ! retornarFasciculos )
//			throw new DAOException(new IllegalArgumentException(
//					"Pelo menos um entre 'retornarExemplares' e 'retornarFasciculos' deve ser escolhido."));
//		
//		List <RelatorioBiblioteca> resultado = new ArrayList <RelatorioBiblioteca> ();
//		
//		String sqlBibliotecas = "";
//		if ( bibliotecas != null && !!! bibliotecas.isEmpty() )
//			sqlBibliotecas = "	AND m.id_biblioteca IN ( " + StringUtils.join(bibliotecas, ", ") + ") ";
//
//		String sql =
//				"SELECT \n" +
//				"	COALESCE(c.descricao, 'Sem coleção')        AS colecao, " +
//				"	COALESCE(cnpq.sigla_biblioteca, '-')        AS area, " +
//				"	COUNT(DISTINCT m.id_material_informacional) AS materiais, " +
//				"	COUNT(DISTINCT t.id_titulo_catalografico)   AS titulos " +
//				( retornarExemplares ?
//						",	COUNT(DISTINCT e.id_exemplar) AS exemplares " :
//						",	0 AS exemplares "
//				) +
//				( retornarFasciculos ?
//						",	COUNT(DISTINCT f.id_fasciculo) AS fasciculos " :
//						",	0 AS fasciculos "
//				) +
//				"FROM " +
//				"	biblioteca.material_informacional           AS m " +
//				"	LEFT JOIN biblioteca.colecao                AS c ON (m.id_colecao = c.id_colecao) " +
//				( retornarExemplares ?
//						"	LEFT JOIN biblioteca.exemplar               AS e ON (m.id_material_informacional = e.id_exemplar) " : ""
//				) +
//				( retornarFasciculos ?
//						"	LEFT JOIN biblioteca.fasciculo              AS f ON (m.id_material_informacional = f.id_fasciculo) " +
//						"	LEFT JOIN biblioteca.assinatura             AS a ON (f.id_assinatura = a.id_assinatura) " : ""
//				) +
//				"	LEFT JOIN biblioteca.titulo_catalografico   AS t ON " +
//				"		t.id_titulo_catalografico = COALESCE(" +
//						( retornarExemplares ? " e.id_titulo_catalografico " : " null " ) + ", " +
//						( retornarFasciculos ? " a.id_titulo_catalografico " : " null " ) + " )" +
//				"		AND t.ativo = trueValue() " +
//				"	INNER JOIN biblioteca.situacao_material_informacional AS sit ON " +
//				"		(m.id_situacao_material_informacional = sit.id_situacao_material_informacional " +
//				"		AND sit.situacao_de_baixa = falseValue() ) " +
//				"	LEFT JOIN comum.area_conhecimento_cnpq      AS cnpq ON " +
//				"		(t.id_area_conhecimento_cnpq = cnpq.id_area_conhecimento_cnpq " +
//				"		AND cnpq.id_area_conhecimento_cnpq = cnpq.id_grande_area ) " +
//				"WHERE " +
//				"	m.ativo = trueValue() " +
//				"	AND sit.situacao_de_baixa = falseValue() " +
//					sqlBibliotecas +
//				"GROUP BY " +
//				"	c.descricao, " +
//				"	cnpq.sigla_biblioteca " +
//				"ORDER BY " +
//				"	c.descricao, " +
//				"	cnpq.sigla_biblioteca ";
//
//		// Cria a query
//		Query q = getSession().createSQLQuery(sql);
//
//		@SuppressWarnings("unchecked")
//		List <Object []> rs = q.list();
//		Iterator <Object []> it = rs.iterator();
//
//		// Se a consulta retornou alguma coisa.
//		
//		String colecao = "-1";
//		
//		RelatorioBiblioteca rb = null;
//		
//		RelatorioBiblioteca rbTotal = new RelatorioBiblioteca ();
//		rbTotal.setNome("Total");
//		rbTotal.setTotal(true);
//		
//		rbTotal.getAreasCnpqT().put("Total", 0);
//		rbTotal.getAreasCnpqE().put("Total", 0);
//		rbTotal.getAreasCnpqF().put("Total", 0);
//		
//		int t = 0, e = 0, f = 0;
//		
//		while (it.hasNext()){
//			
//			Object [] linha = it.next();
//
//			if (!colecao.equals(linha[0])){
//				if (rb != null){
//					rb.getAreasCnpqT().put("Total", t);
//					rb.getAreasCnpqE().put("Total", e);
//					rb.getAreasCnpqF().put("Total", f);
//					resultado.add(rb);
//				}
//				rb = null;
//			}
//			
//			if (rb == null) {
//				rb = new RelatorioBiblioteca();
//				t = 0;
//				e = 0;
//				f = 0;
//				rb.setNome(""+ linha[0]);
//				colecao = rb.getNome();
//			}
//			
//			Integer tAtual = rb.getAreasCnpqT().get(""+ linha[1]);
//			if (tAtual == null) tAtual = 0;
//			
//			Integer eAtual = rb.getAreasCnpqE().get(""+ linha[1]);
//			if (eAtual == null) eAtual = 0;
//			
//			Integer fAtual = rb.getAreasCnpqF().get(""+ linha[1]);
//			if (fAtual == null) fAtual = 0;
//			
//			rb.getAreasCnpqT().put(""+ linha[1], tAtual + ((Number) linha[3]).intValue());
//			rb.getAreasCnpqE().put(""+ linha[1], eAtual + ((Number) linha[4]).intValue());
//			rb.getAreasCnpqF().put(""+ linha[1], fAtual + ((Number) linha[5]).intValue());
//			
//			t += ((Number) linha[3]).intValue();
//			e += ((Number) linha[4]).intValue();
//			f += ((Number) linha[5]).intValue();
//			
//			Integer i;
//			
//			i = rbTotal.getAreasCnpqT().remove(linha[1]);
//			if (i == null) i = 0;
//			rbTotal.getAreasCnpqT().put(""+ linha[1], i+((Number) linha[3]).intValue());
//			
//			i = rbTotal.getAreasCnpqE().remove(linha[1]);
//			if (i == null) i = 0;
//			rbTotal.getAreasCnpqE().put(""+ linha[1], i+((Number) linha[4]).intValue());
//			
//			i = rbTotal.getAreasCnpqF().remove(linha[1]);
//			if (i == null) i = 0;
//			rbTotal.getAreasCnpqF().put(""+ linha[1], i+((Number) linha[5]).intValue());
//			
//			i = rbTotal.getAreasCnpqT().remove("Total");
//			rbTotal.getAreasCnpqT().put("Total", i+((Number) linha[3]).intValue());
//
//			i = rbTotal.getAreasCnpqE().remove("Total");
//			rbTotal.getAreasCnpqE().put("Total", i+((Number) linha[4]).intValue());
//			
//			i = rbTotal.getAreasCnpqF().remove("Total");
//			rbTotal.getAreasCnpqF().put("Total", i+((Number) linha[5]).intValue());
//						
//		}
//		
//		if (rb != null){
//			rb.getAreasCnpqT().put("Total", t);
//			rb.getAreasCnpqE().put("Total", e);
//			rb.getAreasCnpqF().put("Total", f);
//			resultado.add(rb);
//		}
//		
//		resultado.add(rbTotal);
//
//		return resultado;
//	}
	

	
	
	/**
	 *    Recupera os dados que são mostrados no relatório do inventário do acervo para os exemplares.
	 *
	 * @return uma lista de arrays, onde em cada posição do array tem os dados da projeção. <br/>
	 *               [0] número do sistema          <br/>
	 *               [1] codigoBarras               <br/>
	 *               [2] número patrimônio          <br/>
	 *               [3] título                     <br/>
	 *               [4] autor                      <br/>
	 *               [5] edição                     <br/>
	 *               [6] ano                        <br/>
	 *               [7] localização                <br/>
	 */
	public List<Object[]> findInventarioExemplares(
			Collection<Integer> bibliotecas, Collection<Integer> idColecaoList, Collection<Integer> idTipoMaterialList, 
			Collection<Integer> idSituacaoMaterialInformacionalList,
			String numeroChamada, boolean somenteEmprestados )
			throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" select c.numero_do_sistema, m.codigo_barras, e.numero_patrimonio, c.titulo, c.autor, c.edicao, c.ano, m.numero_chamada, t.id_titulo_catalografico ");
		sql.append(" from biblioteca.material_informacional m ");
		sql.append(" inner join biblioteca.situacao_material_informacional s on m.id_situacao_material_informacional = s.id_situacao_material_informacional ");
		sql.append(" inner join biblioteca.exemplar e on e.id_exemplar = m.id_material_informacional ");
		sql.append(" inner join biblioteca.titulo_catalografico t on e.id_titulo_catalografico = t.id_titulo_catalografico ");
		sql.append(" inner join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = t.id_titulo_catalografico ");
		
		if (bibliotecas != null && ! bibliotecas.isEmpty()) {
			sql.append(" inner join biblioteca.biblioteca b on b.id_biblioteca = m.id_biblioteca ");
		}

		if (idColecaoList != null && idColecaoList.size() > 0) {
			sql.append(" inner join biblioteca.colecao col on col.id_colecao = m.id_colecao ");
		}

		if (idTipoMaterialList != null && idTipoMaterialList.size() > 0) {
			sql.append(" inner join biblioteca.tipo_material tipo on tipo.id_tipo_material = m.id_tipo_material ");
		}
		
		if (idSituacaoMaterialInformacionalList != null && idSituacaoMaterialInformacionalList.size() > 0) {
			sql.append(" inner join biblioteca.situacao_material_informacional smi on smi.id_situacao_material_informacional = m.id_situacao_material_informacional ");			
		}
		
		if (somenteEmprestados) {
			sql.append(" inner join biblioteca.emprestimo em on m.id_material_informacional = em.id_material ");
		}
		
		sql.append(" where m.ativo = trueValue() and s.situacao_de_baixa = falseValue() " );

		
		if(bibliotecas != null && ! bibliotecas.isEmpty()) {
			sql.append( " AND b.id_biblioteca IN (" + StringUtils.join(bibliotecas, ',') + ") " );
		}

		if (idColecaoList != null && idColecaoList.size() > 0) {
			sql.append(" AND col.id_colecao IN (" + StringUtils.join(idColecaoList, ',') + ") ");
		}

		if (idTipoMaterialList != null && idTipoMaterialList.size() > 0) {
			sql.append(" AND tipo.id_tipo_material IN (" + StringUtils.join(idTipoMaterialList, ',') + ") ");
		}

		if (idSituacaoMaterialInformacionalList != null && idSituacaoMaterialInformacionalList.size() > 0) {
			sql.append(" AND smi.id_situacao_material_informacional IN (" + StringUtils.join(idSituacaoMaterialInformacionalList, ',') + ") ");
		}
		
		if( StringUtils.notEmpty(numeroChamada) ){
			sql.append(" AND m.numero_chamada like :numeroChamada ");
		}
		
		if (somenteEmprestados) {
			sql.append(
					" AND em.ativo = trueValue() " +
					" AND em.situacao =  " +Emprestimo.EMPRESTADO );
		}
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		if(StringUtils.notEmpty(numeroChamada)) q.setString("numeroChamada", numeroChamada+"%");
		
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.list();
			return list;
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
		
	}
	
	/**
	 *    Recupera os dados que são mostrados no relatório do inventário do acervo para os fascículos
	 *
	 * @return uma lista de arrays, onde em cada posição do array tem os dados da projeção. <br/>
	 *               [0] número do sistema          <br/>
	 *               [1] codigoBarras               <br/>
	 *               [2] título                     <br/>
	 *               [3] autor                      <br/>
	 *               [4] edição                     <br/>
	 *               [5] ano                        <br/>
	 *               [6] localização                <br/>
	 */
	public List<Object[]> findInventarioFasiculos(
			Collection<Integer> bibliotecas, Collection<Integer> idColecaoList, Collection<Integer> idTipoMaterialList,
			Collection<Integer> idSituacaoMaterialInformacionalList,
			String cdu, boolean somenteEmprestados )
			throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" select c.numero_do_sistema, m.codigo_barras, c.titulo, c.autor, c.edicao, c.ano, m.numero_chamada, t.id_titulo_catalografico ");
		sql.append(" from biblioteca.material_informacional m  ");
		sql.append(" inner join biblioteca.situacao_material_informacional s on m.id_situacao_material_informacional = s.id_situacao_material_informacional  ");
		sql.append(" inner join biblioteca.fasciculo f on f.id_fasciculo = m.id_material_informacional ");
		sql.append(" inner join biblioteca.assinatura a on a.id_assinatura = f.id_assinatura ");
		sql.append(" inner join biblioteca.titulo_catalografico t on a.id_titulo_catalografico = t.id_titulo_catalografico ");
		sql.append(" inner join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = t.id_titulo_catalografico ");
		
		
		if (bibliotecas != null && ! bibliotecas.isEmpty()) {
			sql.append(" inner join biblioteca.biblioteca b on b.id_biblioteca = m.id_biblioteca ");
		}

		if (idColecaoList != null && idColecaoList.size() > 0) {
			sql.append(" inner join biblioteca.colecao col on col.id_colecao = m.id_colecao ");
		}

		if (idTipoMaterialList != null && idTipoMaterialList.size() > 0) {
			sql.append(" inner join biblioteca.tipo_material tipo on tipo.id_tipo_material = m.id_tipo_material ");
		}
		
		if (idSituacaoMaterialInformacionalList != null && idSituacaoMaterialInformacionalList.size() > 0) {
			sql.append(" inner join biblioteca.situacao_material_informacional smi on smi.id_situacao_material_informacional = m.id_situacao_material_informacional ");			
		}
		
		if (somenteEmprestados) {
			sql.append(" inner join biblioteca.emprestimo em on m.id_material_informacional = em.id_material ");
		}
		
		sql.append(" where f.incluido_acervo = trueValue() AND m.ativo = trueValue() AND  s.situacao_de_baixa = falseValue() ");

		
		if (bibliotecas != null && ! bibliotecas.isEmpty()) {
			sql.append( " AND b.id_biblioteca IN (" + StringUtils.join(bibliotecas, ',') + ") " );
		}

		if (idColecaoList != null && idColecaoList.size() > 0) {
			sql.append(" AND col.id_colecao IN (" + StringUtils.join(idColecaoList, ',') + ") ");
		}

		if (idTipoMaterialList != null && idTipoMaterialList.size() > 0) {
			sql.append(" AND tipo.id_tipo_material IN (" + StringUtils.join(idTipoMaterialList, ',') + ") ");
		}

		if (idSituacaoMaterialInformacionalList != null && idSituacaoMaterialInformacionalList.size() > 0) {
			sql.append(" AND smi.id_situacao_material_informacional IN (" + StringUtils.join(idSituacaoMaterialInformacionalList, ',') + ") ");
		}
		
		if( StringUtils.notEmpty(cdu) ){
			sql.append(" AND m.numero_chamada like :CDU ");
		}
		
		if (somenteEmprestados) {
			sql.append(
					" AND em.ativo = trueValue() " +
					" AND em.data_devolucao IS NULL " +
					" AND em.data_estorno IS NULL ");
		}
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		if(StringUtils.notEmpty(cdu)) q.setString("CDU", cdu+"%");
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		return list;
		
	}
	
	

	/**
	 * Retorna um hashmap contendo as ids dos títulos catalográficos e a quantidade de materiais ativos que estes contém.
	 */
	public List<MaterialInformacionalDTO> findQuantidadeMateriaisAtivosPorTitulo(List <Integer> ids) throws DAOException {
		
		String stringIds = "";
		
		for (Integer id : ids)
			stringIds += (stringIds.equals("") ? "" : ",") + id;
		
		String sql = "select id_titulo_catalografico, count (id_exemplar) "
			+" from biblioteca.exemplar e "
			+" inner join biblioteca.material_informacional m on m.id_material_informacional = e.id_exemplar "
			+" inner join biblioteca.situacao_material_informacional s on m.id_situacao_material_informacional = s.id_situacao_material_informacional "
			+" where m.ativo = trueValue() and s.situacao_de_baixa = falseValue() and e.id_titulo_catalografico in ("+stringIds+") group by id_titulo_catalografico";
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = getSession().createSQLQuery(sql).list();
		
		List<MaterialInformacionalDTO> lista = new ArrayList<MaterialInformacionalDTO>();
		
		for (Object [] l : rs){
			MaterialInformacionalDTO mat = new MaterialInformacionalDTO();
			
			mat.setIdMaterialInformacional((Integer)l[0]);
			mat.setQtdAcervo(((BigInteger)l[1]).intValue());
			
			lista.add(mat);
		}
		
		return lista;
	}
	
	
	
	
	
	/**
	 * Retorna um intervalo de datas no formato ISO, separadas por um AND para serem usadas
	 * num BETWEEN do SQL
	 */
	public static String formataIntervalo( Date inicio, Date fim ) {
		return "'"+ CalendarUtils.format(inicio, "yyyy-MM-dd") + " 00:00:00' AND '"+
				CalendarUtils.format(fim, "yyyy-MM-dd") +" 23:59:59' ";
	}

	/**
	 * Retorna uma pessoa pelo seu nome, caso este inicie com o nome passado como parâmetro. 
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Pessoa> findPessoaByNome(String nome) throws DAOException {
		Criteria c = getSession().createCriteria(Pessoa.class);
		
		c.setProjection(Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("nome"))
				.add(Projections.property("cpf_cnpj"))
				);
		
		//c.add(Expression.sql("upper(nome_ascii) like upper('"+ StringUtils.toAscii(StringUtils.escapeBackSlash(nome)) + "%')"));
		c.add(Restrictions.ilike("nomeAscii", StringUtils.toAsciiAndUpperCase(nome), MatchMode.ANYWHERE));
		c.add(Restrictions.isNotNull("cpf_cnpj"));
		c.addOrder(Order.asc("nome"));
		
		List<Pessoa> resultado = new ArrayList<Pessoa>();
		
		for (Object[] linha : (List<Object[]>)c.list()) {
			Pessoa pessoa = new Pessoa();

			pessoa.setId((Integer) linha[0]);
			pessoa.setNome((String) linha[1]);
			pessoa.setCpf_cnpj((Long) linha[2]);
			
			resultado.add(pessoa);
		}
		
		return resultado;
	}
	
}
