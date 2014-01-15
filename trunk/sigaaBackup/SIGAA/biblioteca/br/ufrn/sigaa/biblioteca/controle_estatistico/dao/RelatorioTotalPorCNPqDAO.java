/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 28/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 *
 * <p>DAO utilizado exclusivamente para o relatório de novas aquisições. </p>
 * 
 * @author Felipe Rivas
 *
 */
public class RelatorioTotalPorCNPqDAO extends GenericSigaaDAO {
	
	
	
	/**
	 * Método que retorna um objeto contendo a contagem de todos os títulos e exemplares, separados pela
	 * área do CNPq, das bibliotecas passadas.
	 * @throws DAOException 
	 */
	public List<Object[]> findTotalTitulosMateriaisByAreasCNPQ(Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo, 
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial, Collection<Integer> idsSituacaoMaterial, 
			Collection<Integer> idsFormaDocumento, FiltroClassificacoesRelatoriosBiblioteca classificacao, 
			AgrupamentoRelatoriosBiblioteca agrupamento,
			boolean retornarExemplares, boolean retornarFasciculos, boolean retornarDigitais) throws DAOException {
		
		if ( ! retornarExemplares && ! retornarFasciculos && ! retornarDigitais )
			throw new DAOException(new IllegalArgumentException(
					"Pelo menos um entre 'retornarExemplares', 'retornarFasciculos' e 'retornarDigitais' deve ser escolhido."));
		
		if (retornarDigitais && !retornarExemplares && !retornarFasciculos) {
			agrupamento = AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO;
		}

		StringBuilder sqlSelect = new StringBuilder("SELECT ");
		sqlSelect.append(" COALESCE(  COALESCE( info.sigla, a.sigla ), 'Sem Área') AS area, ");
		sqlSelect.append(agrupamento != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? "COALESCE(" + agrupamento.campoAgrupamento + ", '" + agrupamento.substituiValoresNull + "') AS " + agrupamento.nomeCampo + ", " : "");
		sqlSelect.append(" COUNT(DISTINCT titulo.id_titulo_catalografico) AS titulos, ");
		sqlSelect.append(retornarExemplares || retornarFasciculos ? "COUNT(DISTINCT material.id_material_informacional) AS materiais, " : " 0 AS materiais, ");
		sqlSelect.append(retornarExemplares ? "COUNT(DISTINCT exemplar.id_exemplar) AS exemplares, " : " 0 AS exemplares, ");
		sqlSelect.append(retornarFasciculos ? "COUNT(DISTINCT fasciculo.id_fasciculo ) AS fasciculos, " : " 0 AS fasciculos, ");
		sqlSelect.append(" 0 AS digitais ");
		
		sqlSelect.append("FROM biblioteca.titulo_catalografico AS titulo ");
		
		sqlSelect.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq()); // LEFT JOIN porque pode exitir Título sem àrea
		sqlSelect.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		
		sqlSelect.append(" LEFT JOIN biblioteca.assinatura AS assinatura ON (titulo.id_titulo_catalografico = assinatura.id_titulo_catalografico) "); 			
		sqlSelect.append(" LEFT JOIN biblioteca.fasciculo  AS fasciculo  ON (assinatura.id_assinatura = fasciculo.id_assinatura) "); 			
		sqlSelect.append(" LEFT JOIN biblioteca.exemplar   AS exemplar   ON (exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico) "); 
		sqlSelect.append(" INNER JOIN biblioteca.material_informacional material  ON material.id_material_informacional =  COALESCE(exemplar.id_exemplar, fasciculo.id_fasciculo) "); 		
		sqlSelect.append(" INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional "); 
		
		if(agrupamento == AgrupamentoRelatoriosBiblioteca.COLECAO)
			sqlSelect.append(" INNER JOIN biblioteca.colecao AS colecao ON colecao.id_colecao = material.id_colecao "); 
		
		if(agrupamento == AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL)
			sqlSelect.append(" INNER JOIN biblioteca.tipo_material AS tipoMaterial ON tipoMaterial.id_tipo_material = material.id_tipo_material "); 
		
		if(agrupamento == AgrupamentoRelatoriosBiblioteca.BIBLIOTECA)
			sqlSelect.append(" INNER JOIN biblioteca.biblioteca AS biblioteca ON biblioteca.id_biblioteca = material.id_biblioteca "); 
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sqlSelect.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional ");
			sqlSelect.append(" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		// Where
		sqlSelect.append("WHERE titulo.ativo = trueValue() AND material.ativo = trueValue() AND situacao.situacao_de_baixa = falseValue() ");
		
		
		if ( idsBiblioteca != null && ! idsBiblioteca.isEmpty() ) {
			sqlSelect.append(" AND material.id_biblioteca IN ("+ StringUtils.join(idsBiblioteca, ',') +") ");
		}
		
		if ( idsColecao != null && ! idsColecao.isEmpty() ) {
			sqlSelect.append(" AND material.id_colecao IN ( " + StringUtils.join(idsColecao, ", ") + ") ");
		}
		
		if ( idsTipoMaterial != null && !!! idsTipoMaterial.isEmpty() ) {
			sqlSelect.append(" AND material.id_tipo_material IN ( " + StringUtils.join(idsTipoMaterial, ", ") + ") ");
		}
		
		if ( idsSituacaoMaterial != null && ! idsSituacaoMaterial.isEmpty() ) {
			sqlSelect.append(" AND situacao.id_situacao_material_informacional IN ( " + StringUtils.join(idsSituacaoMaterial, ", ") + ") ");
		}
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sqlSelect.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(idsFormaDocumento, ',') +") ");
		}
		
		
		if(retornarExemplares && !retornarFasciculos)
			sqlSelect.append(" AND  exemplar.id_exemplar IS NOT NULL ");
		if(retornarFasciculos && !retornarExemplares)
			sqlSelect.append(" AND  fasciculo.id_fasciculo IS NOT NULL 	");		
		
		if (inicioPeriodo != null)
			sqlSelect.append(" AND material.data_criacao >=  :inicioPeriodo "); 			
		if (fimPeriodo != null)
			sqlSelect.append(" AND material.data_criacao <= :fimPeriodo "); 
		
		// Group By
		sqlSelect.append("GROUP BY area" + (agrupamento != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", " + agrupamento.nomeCampo + " " : " "));
		
		// Order By
		sqlSelect.append("ORDER BY area" + (agrupamento != AgrupamentoRelatoriosBiblioteca.SEM_AGRUPAMENTO ? ", " + agrupamento.nomeCampo + " " : " "));
		
		
		Query q = getSession().createSQLQuery(sqlSelect.toString());

		if (inicioPeriodo != null) {
			q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		}
		
		if (fimPeriodo != null) {
			q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return lista;
		
	}

	
	
	
	
	
	/**
	 * <p> Retorna a lista dos títulos cadastrados no período especificado, agrupados por Área do CNPq.<p>
	 * 
	 * @param idsBiblioteca
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param idsColecao
	 * @param idsTipoMaterial
	 * @param idsSituacaoMaterial
	 * @param idsFormaDocumento
	 * @param retornarExemplares
	 * @param retornarFasciculos
	 * @param retornarDigitais
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> findTotalTitulosCNPq(Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo, 
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial, Collection<Integer> idsSituacaoMaterial, 
			Collection<Integer> idsFormaDocumento, FiltroClassificacoesRelatoriosBiblioteca classificacao, boolean retornarExemplares, boolean retornarFasciculos, 
			boolean retornarDigitais) throws DAOException {
		
		
		StringBuilder sql = new StringBuilder();
		
		
		sql.append(" SELECT COALESCE( COALESCE( info.sigla, a.sigla ), 'Sem Área' ) AS area, COUNT(DISTINCT titulo.id_titulo_catalografico )");
		sql.append(" FROM 	biblioteca.titulo_catalografico titulo "); 	
		
		sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq()); // LEFT JOIN porque pode exitir Título sem àrea
		sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		
		// Faz os JOINs para aplicar aos Títulos os filtros dos Materiais, case a mesma busca de materias, sendo que lá pode agrupar 
		// pelos dados dos materiais ai o resultado fica diferente.
		//
		//Aqui usa LEFTs JOIN para recuperar mesmo os Títulos que não possuem materiais.
		//
		sql.append(" LEFT JOIN biblioteca.assinatura AS assinatura ON (titulo.id_titulo_catalografico = assinatura.id_titulo_catalografico) "); 			
		sql.append(" LEFT JOIN biblioteca.fasciculo  AS fasciculo  ON (assinatura.id_assinatura = fasciculo.id_assinatura) "); 			
		sql.append(" LEFT JOIN biblioteca.exemplar   AS exemplar   ON (exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico) "); 
		sql.append(" LEFT JOIN biblioteca.material_informacional material  ON material.id_material_informacional =  COALESCE(exemplar.id_exemplar, fasciculo.id_fasciculo) "); 		
		sql.append(" INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional "); 
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sql.append(" LEFT JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional ");
			sql.append(" LEFT JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		sql.append(" WHERE titulo.ativo = trueValue() AND material.ativo = trueValue() AND situacao.situacao_de_baixa = falseValue() ");
		
		if ( idsBiblioteca != null && ! idsBiblioteca.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(idsBiblioteca, ',') +") ");
		}
		
		if ( idsColecao != null && ! idsColecao.isEmpty() ) {
			sql.append(" AND material.id_colecao IN ( " + StringUtils.join(idsColecao, ", ") + ") ");
		}
		
		if ( idsTipoMaterial != null && !!! idsTipoMaterial.isEmpty() ) {
			sql.append(" AND material.id_tipo_material IN ( " + StringUtils.join(idsTipoMaterial, ", ") + ") ");
		}
		
		if ( idsSituacaoMaterial != null && ! idsSituacaoMaterial.isEmpty() ) {
			sql.append(" AND situacao.id_situacao_material_informacional IN ( " + StringUtils.join(idsSituacaoMaterial, ", ") + ") ");
		}
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(idsFormaDocumento, ',') +") ");
		}
		
		
		if(retornarExemplares && !retornarFasciculos)
			sql.append(" AND  exemplar.id_exemplar IS NOT NULL ");
		if(retornarFasciculos && !retornarExemplares)
			sql.append(" AND  fasciculo.id_fasciculo IS NOT NULL 	");		
		
		if (inicioPeriodo != null)
			sql.append(" AND titulo.data_criacao >=  :dataInicio "); 			
		if (fimPeriodo != null)
			sql.append(" AND titulo.data_criacao <= :dataFim "); 
		
		sql.append(" GROUP BY area ORDER BY area "); 	
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		if (inicioPeriodo != null) {
			q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		}
		
		if (fimPeriodo != null) {
			q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
		
	}
	
	
	/**
	 * Retorna a quantidade de materiais digitais para o período passado.
	 * 
	 * Materiais Digitais são considerados no sistema os Títulos que não possuem materiais físicos mas possuem o endereço eletrônico.
	 * 
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param classificacao
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> findTotalMateriaisDigitais(Date inicioPeriodo, Date fimPeriodo,  FiltroClassificacoesRelatoriosBiblioteca classificacao) throws DAOException{
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT COALESCE( COALESCE( info.sigla, a.sigla ), 'Sem Área' ) AS area, COUNT(DISTINCT cem.id_titulo_catalografico ) ");
		sql.append(" FROM 	biblioteca.cache_entidades_marc cem ");			
		sql.append(" INNER JOIN biblioteca.titulo_catalografico titulo ON (titulo.id_titulo_catalografico = cem.id_titulo_catalografico) ");
		sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq()  );  // podem existir títulos sem área	 
		sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq "); 		
		
		sql.append(" WHERE titulo.ativo = trueValue() AND cem.id_titulo_catalografico IS NOT NULL ");
		sql.append(" AND ( cem.quantidade_materiais_ativos_titulo = 0 AND ( cem.localizacao_endereco_eletronico IS NOT NULL AND cem.localizacao_endereco_eletronico != '' )  ) ");		
		
		if (inicioPeriodo != null)
			sql.append(" AND titulo.data_criacao >= :inicioPeriodo  ");			
		if (fimPeriodo != null)
			sql.append(" AND titulo.data_criacao <= :fimPeriodo "); 
		
		sql.append(" GROUP BY area ORDER BY area ");
		
		Query q = getSession().createSQLQuery(sql.toString());

		if (inicioPeriodo != null) {
			q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		}
		
		if (fimPeriodo != null) {
			q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}
	
	
	
	
	
}
