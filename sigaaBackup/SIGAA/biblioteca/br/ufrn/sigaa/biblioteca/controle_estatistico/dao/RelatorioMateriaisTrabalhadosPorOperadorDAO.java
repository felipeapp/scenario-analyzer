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

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 *
 * <p>DAO exclusivo para o relatório de materiais trabalhados por operador.</p>
 *
 * <p><i>Possui dois métodos porque podem ser emitidos dois tipos de relatórios diferentes: a analítica e o sintético.</i></p>
 * 
 * @author felipe
 *
 */
public class RelatorioMateriaisTrabalhadosPorOperadorDAO extends GenericSigaaDAO {

	/** Sintaxe exepecífica no posgtgres */
	public static final String CAST_NULL_TO_INTEGER = " CAST(null AS integer) ";
	
	
	////////////////////////Consultas do Sintético /////////////////////////////
	
	/**
	 * Consulta que gera os dados do relatório sintetico (apenas títulos) de Materiais Catalogados por Operador.
	 * 
	 * @param idsPessoa
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param idsTipoMaterial
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> countTitulosCadastradosPorOperadorSintetico(Collection<Integer> idsBibliotecas, Collection<Integer> idsPessoa, 
			Date inicioPeriodo, Date fimPeriodo, Collection<Integer> idsTipoMaterial, Collection<Integer> idsColecao, 
			FiltroClassificacoesRelatoriosBiblioteca classificacao) throws DAOException {
		
		StringBuilder sqlExemplar = montarSQLEspecificoSintetico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, classificacao, true, true);
		StringBuilder sqlFasciculo = montarSQLEspecificoSintetico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, classificacao, true, false);

		StringBuilder sqlGeral = new StringBuilder("" +
				" SELECT " +
				"	 usuario, " +
				"	 classe, " +
				"	 COUNT (DISTINCT idsTitulos) AS qtd_titulos " +
				" FROM ( " +
					 sqlExemplar.toString() +
				"	 UNION ALL " +
					 sqlFasciculo.toString() +
				" ) AS uniao " +
				" GROUP BY usuario, classe " +
				" ORDER BY usuario, classe ");
		
		Query q = getSession().createSQLQuery(sqlGeral.toString());
		
		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = q.list();
		
		return resultado;
	}
	
	/**
	 * Consulta que gera os dados do relatório sintetico (apenas materiais) de Materiais Catalogados por Operador.
	 * 
	 * @param idsPessoa
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param idsTipoMaterial
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> countMateriaisCadastradosPorOperadorSintetico(Collection<Integer> idsBibliotecas, Collection<Integer> idsPessoa, 
			Date inicioPeriodo, Date fimPeriodo, Collection<Integer> idsTipoMaterial, Collection<Integer> idsColecao, 
			FiltroClassificacoesRelatoriosBiblioteca classificacao  ) throws DAOException {
		
		StringBuilder sqlExemplar = montarSQLEspecificoSintetico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, classificacao, false, true);
		StringBuilder sqlFasciculo = montarSQLEspecificoSintetico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, classificacao, false, false);

		StringBuilder sqlGeral = new StringBuilder("" +
				" SELECT " +
				"	 usuario, " +
				"	 classe, " +
				"	 colecao, " +
				"	 COUNT( DISTINCT exemplares) AS qtd_exemplares, " +
				"	 COUNT( DISTINCT fasciculos) AS qtd_fasciculos " +
				" FROM ( " +
					 sqlExemplar.toString() +
				"	 UNION ALL" +
					 sqlFasciculo.toString() +
				" ) AS uniao " +
				" GROUP BY usuario, classe, colecao " +
				" ORDER BY usuario, classe, colecao ");
		
		Query q = getSession().createSQLQuery(sqlGeral.toString());
		
		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = q.list();
		
		return resultado;
	}

	
	
	
	
	//////////////////////// Consultas do Analítico /////////////////////////////
	
	
	
	
	/**
	 * <p>Consulta que gera a contagem dos dados do relatório analítico (apenas títulos) de Materiais Catalogados por Operador.</p>
	 * 
	 * <p>Esse método é usado para saber a quantidade de páginas total do relatório.</p>
	 * 
	 * @param idsPessoa
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param idsTipoMaterial
	 * @return
	 * @throws DAOException
	 */
	public int countTitulosCadastradosPorOperadorAnalitico(Collection<Integer> idsBibliotecas, Collection<Integer> idsPessoa, 
			Date inicioPeriodo, Date fimPeriodo, Collection<Integer> idsTipoMaterial, Collection<Integer> idsColecao) throws DAOException {
		
		StringBuilder sqlExemplar = montarSQLEspecificoAnalitico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, true, true, true);
		StringBuilder sqlFasciculo = montarSQLEspecificoAnalitico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, true, false, true);

		StringBuilder sqlGeral = new StringBuilder("" +
				" SELECT " +
				"	 COUNT(DISTINCT idsContagem) " + 
				" FROM ( " +
					 sqlExemplar.toString() +
				"	 UNION ALL  " +
					 sqlFasciculo.toString() +
				" ) AS uniao ");
		
		Query q = getSession().createSQLQuery(sqlGeral.toString());
		
		q.setTimestamp("inicioPeriodo", inicioPeriodo);
		q.setTimestamp("fimPeriodo", fimPeriodo);
		
		int resultado = ((BigInteger) q.uniqueResult()).intValue();
		
		return resultado;
	}

	
	
	/**
	 * Consulta que gera os dados do relatório sintetico (apenas títulos) de Materiais Catalogados por Operador.
	 * 
	 * @param idsPessoa
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param idsTipoMaterial
	 * @param paginaAtual
	 * @param limiteRegistrosPorPagina
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> findTitulosCadastradosPorOperadorAnalitico(Collection<Integer> idsBibliotecas, Collection<Integer> idsPessoa, 
			Date inicioPeriodo, Date fimPeriodo, Collection<Integer> idsTipoMaterial, Collection<Integer> idsColecao
			, int paginaAtual, Integer limiteRegistrosPorPagina) throws DAOException {
		
		StringBuilder sqlExemplar = montarSQLEspecificoAnalitico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, true, true, false);
		StringBuilder sqlFasciculo = montarSQLEspecificoAnalitico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, true, false, false);

		StringBuilder sqlGeral = new StringBuilder("" +
				" SELECT DISTINCT " +
				"	 usuario, " + 
				"	 classificacao_1, " +  
				"	 classificacao_2, " +  
				"	 classificacao_3, " +  
				"	 numero_do_sistema,  " + 
				"	 titulo, " +  
				"	 autor, " +  
				"	 edicao, " +  
				"	 ano, " +  
				"	 numero_chamada, " +
				"	 data_criacao " + 
				" FROM ( " +
					 sqlExemplar.toString() +
				"	 UNION ALL  " +
					 sqlFasciculo.toString() +
				" ) AS uniao " +
				" ORDER BY usuario, titulo ");
		
		Query q = getSession().createSQLQuery(sqlGeral.toString());
		
		q.setFirstResult((paginaAtual - 1) * limiteRegistrosPorPagina);
		q.setMaxResults(limiteRegistrosPorPagina);
		
		q.setTimestamp("inicioPeriodo", inicioPeriodo);
		q.setTimestamp("fimPeriodo", fimPeriodo);
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = q.list();
		
		return resultado;
	}

	
	
	/**
	 * Consulta que gera a contagem dos dados do relatório analítico (apenas materiais) de Materiais Catalogados por Operador.
	 * 
	 * <p>Esse método é usado para saber a quantidade de páginas total do relatório.</p>
	 * 
	 * @param idsPessoa
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param idsTipoMaterial
	 * @return
	 * @throws DAOException
	 */
	public int countMateriaisCadastradosPorOperadorAnalitico(Collection<Integer> idsBibliotecas, Collection<Integer> idsPessoa, 
			Date inicioPeriodo, Date fimPeriodo, Collection<Integer> idsTipoMaterial, Collection<Integer> idsColecao ) throws DAOException {
	
		
		StringBuilder sqlExemplar = montarSQLEspecificoAnalitico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, false, true, true);
		StringBuilder sqlFasciculo = montarSQLEspecificoAnalitico(idsBibliotecas, idsPessoa, idsTipoMaterial, idsColecao, false, false, true);

		StringBuilder sqlGeral = new StringBuilder("" +
				" SELECT " +
				"	 COUNT(DISTINCT idsContagem) " + 
				" FROM ( " +
					 sqlExemplar.toString() +
				"	 UNION ALL " +
					 sqlFasciculo.toString() +
				" ) AS uniao ");
		
		Query q = getSession().createSQLQuery(sqlGeral.toString());
		
		q.setTimestamp("inicioPeriodo", inicioPeriodo);
		q.setTimestamp("fimPeriodo", fimPeriodo);
		
		int resultado = ((BigInteger) q.uniqueResult()).intValue();
		
		return resultado;
	}

	/**
	 * Consulta que gera os dados do relatório sintetico (apenas títulos) de Materiais Catalogados por Operador.
	 * 
	 * @param idsPessoa
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param idsTipoMaterial
	 * @param paginaAtual
	 * @param limiteRegistrosPorPagina
	 * @return
	 * @throws DAOException
	 */
	public List<Object[]> findMateriaisCadastradosPorOperadorAnalitico(Collection<Integer> idsBiblioteca, Collection<Integer> idsPessoa, 
			Date inicioPeriodo, Date fimPeriodo, Collection<Integer> idsTipoMaterial, Collection<Integer> idsColecao, 
			int paginaAtual, Integer limiteRegistrosPorPagina) throws DAOException {
		
		
		StringBuilder sqlExemplar = montarSQLEspecificoAnalitico(idsBiblioteca, idsPessoa, idsTipoMaterial, idsColecao, false, true, false);
		StringBuilder sqlFasciculo = montarSQLEspecificoAnalitico(idsBiblioteca, idsPessoa, idsTipoMaterial, idsColecao, false, false, false);

		StringBuilder sqlGeral = new StringBuilder("" +
				" SELECT DISTINCT " +
				"	 usuario, " + 
				"	 codigo_barras, " +  
				"	 classificacao_1, " +  
				"	 classificacao_2, " +  
				"	 classificacao_3, " +  
				"	 numero_do_sistema,  " + 
				"	 titulo, " +  
				"	 autor, " +  
				"	 edicao, " +  
				"	 ano, " +  
				"	 numero_chamada, " +  
				"	 biblioteca, " +  
				"	 data_criacao " + 
				" FROM ( " +
					 sqlExemplar.toString() +
				"	 UNION ALL  " +
					 sqlFasciculo.toString() +
				" ) AS uniao " +
				" ORDER BY usuario, biblioteca, titulo, codigo_barras ");
		
		Query q = getSession().createSQLQuery(sqlGeral.toString());
		
		q.setFirstResult((paginaAtual - 1) * limiteRegistrosPorPagina);
		q.setMaxResults(limiteRegistrosPorPagina);
		
		q.setTimestamp("inicioPeriodo", inicioPeriodo);
		q.setTimestamp("fimPeriodo", fimPeriodo);
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultado = q.list();
		
		return resultado;
	}
	
	
	
	///////////////////////////// métodos internos do DAO ///////////////////////////////

	
	/**
	 * Monta o SQL específico do tipo de material (exemplar ou fascículo) da consulta de materiais trabalhados por operador sintética. 
	 * O tipo de material é definido pelo parâmetro <i>isExemplar</i>, onde o valor <i>true</i> indica <i>Exemplar</i>, e <i>false</i> indica 
	 * <i>Fascículo</i>. O filtro de tipo de acervo indica se a consulta deve pesquisar no acervo de <i>Títulos</i>(<i>true</i>) ou 
	 * <i>Materiais</i>(<i>false</i>).
	 * 
	 * @param idsPessoa
	 * @param idsTipoMaterial
	 * @param classe
	 * @param isTitulos
	 * @param isExemplar
	 * @return
	 */
	private StringBuilder montarSQLEspecificoSintetico(Collection<Integer> idsBibliotecas, Collection<Integer> idsPessoa,
			Collection<Integer> idsTipoMaterial, Collection<Integer> idsColecao, FiltroClassificacoesRelatoriosBiblioteca classificacao, boolean isTitulos, 
			boolean isExemplar) {
		String selectEspecifico = null;
		String joinEspecifico = null;
		
		if (isExemplar) {
			if (isTitulos) {
				selectEspecifico = "	 t.id_titulo_catalografico AS idsTitulos " ;
			} else {
				selectEspecifico = "	 COALESCE(c.descricao, 'Sem coleção') AS colecao, " + 
								  // "	  t.id_titulo_catalografico AS titulos, " + 
								  	"	 mi.id_material_informacional AS exemplares, "+
								  	"	 "+CAST_NULL_TO_INTEGER+" AS fasciculos ";
			}
			
			joinEspecifico = "	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.exemplar AS e ON e.id_titulo_catalografico = t.id_titulo_catalografico " +
							"	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.material_informacional AS mi ON mi.id_material_informacional = e.id_exemplar ";
		} else {
			if (isTitulos) {
				selectEspecifico = "	 t.id_titulo_catalografico AS idsTitulos ";
			} else {
				selectEspecifico = "	 COALESCE(c.descricao, 'Sem coleção') AS colecao, " + 
								 //  "	  t.id_titulo_catalografico AS titulos, " + 
								 	"	 "+CAST_NULL_TO_INTEGER+" AS exemplares, "+
								 	"	 mi.id_material_informacional AS fasciculos ";
			}
			
			joinEspecifico = "	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.assinatura a ON a.id_titulo_catalografico = t.id_titulo_catalografico " +
							"	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.fasciculo f ON f.id_assinatura = a.id_assinatura " +
							"	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.material_informacional AS mi ON mi.id_material_informacional = f.id_fasciculo ";
		}
		
		StringBuilder sqlEspecifico = new StringBuilder("" +
				" SELECT DISTINCT " +
				"	 pessoa.nome AS usuario, " + 
				"	 COALESCE(t." + classificacao.getColunaClassePrincipal() + ", 'Sem classe') AS classe, " + 
					 selectEspecifico +
				" FROM biblioteca.titulo_catalografico t " +
					 joinEspecifico +
				
				// podem ser recuperados títulos em materiais
				(isTitulos ? " LEFT" : " INNER") + " JOIN biblioteca.situacao_material_informacional AS smi ON smi.id_situacao_material_informacional = mi.id_situacao_material_informacional " +
				(isTitulos ? "     " : " LEFT JOIN biblioteca.colecao AS c ON c.id_colecao = mi.id_colecao ") +
						 
				"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = " + (isTitulos ? "t" : "mi") + ".id_registro_criacao " +
				"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " +
				"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " +
				" WHERE t.ativo = trueValue() AND t.catalogado = trueValue() ");
		
		if (isTitulos) {
			sqlEspecifico.append("AND (mi.id_material_informacional is null OR (mi.ativo = trueValue() AND smi.situacao_de_baixa = falseValue())) ");
		} else {
			sqlEspecifico.append("AND mi.ativo = trueValue() ");
			sqlEspecifico.append("AND smi.situacao_de_baixa = falseValue() ");
		}

		if ( idsPessoa != null && ! idsPessoa.isEmpty() ) {
			sqlEspecifico.append(" AND pessoa.id_pessoa IN ("+ StringUtils.join(idsPessoa, ',') +") ");
		}
		
		if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
			sqlEspecifico.append(" AND mi.id_biblioteca IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
			
		}
		
		if ( idsTipoMaterial != null && ! idsTipoMaterial.isEmpty() ) {
			sqlEspecifico.append(" AND mi.id_tipo_material IN ("+ StringUtils.join(idsTipoMaterial, ',') +") ");
		}
		
		if ( idsColecao != null && ! idsColecao.isEmpty() ) {
			sqlEspecifico.append(" AND mi.id_colecao IN ("+ StringUtils.join(idsColecao, ',') +") ");
		}

		sqlEspecifico.append(" AND " + (isTitulos ? "t" : "mi") + ".data_criacao >= :inicioPeriodo ");
		sqlEspecifico.append(" AND " + (isTitulos ? "t" : "mi") + ".data_criacao <= :fimPeriodo ");
		
		return sqlEspecifico;
	}

	
	
	/**
	 * Monta o SQL específico do tipo de material (exemplar ou fascículo) da consulta de materiais trabalhados por operador analítica. 
	 * O tipo de material é definido pelo parâmetro <i>isExemplar</i>, onde o valor <i>true</i> indica <i>Exemplar</i>, e <i>false</i> indica 
	 * <i>Fascículo</i>. O filtro de tipo de acervo indica se a consulta deve pesquisar no acervo de <i>Títulos</i>(<i>true</i>) ou 
	 * <i>Materiais</i>(<i>false</i>). Também é necessário indicar o tipo de projeção (count ou consulta normal) através do parâmetro 
	 * <i>isCount</i>.
	 * 
	 * @param idsPessoa
	 * @param idsTipoMaterial
	 * @param classe
	 * @param isTitulos
	 * @param isExemplar
	 * @param isCount
	 * @return
	 */
	private StringBuilder montarSQLEspecificoAnalitico(Collection<Integer> idsBibliotecas, Collection<Integer> idsPessoa,
			Collection<Integer> idsTipoMaterial, Collection<Integer> idsColecao, boolean isTitulos, 
			boolean isExemplar, boolean isCount) {
		
		String selectEspecifico = null;
		String joinEspecifico = null;
		
		if (isCount) {
			selectEspecifico = "	 DISTINCT " + (isTitulos ? "t.id_titulo_catalografico" : "mi.id_material_informacional") + " AS idsContagem ";
		} else {
			selectEspecifico = "	 pessoa.nome AS usuario, " +  
								(isTitulos ? "": "	 mi.codigo_barras, ") +  
								"	 t.classificacao_1 AS classificacao_1, " +  
								"	 t.classificacao_2 AS classificacao_2, " +  
								"	 t.classificacao_3 AS classificacao_3, " +  
								"	 cem.numero_do_sistema, " +  
								"	 cem.titulo, " +  
								"	 cem.autor, " +  
								"	 cem.edicao, " +  
								"	 cem.ano, " +  
								"	 cem.numero_chamada, " +  
								"	 b.descricao AS biblioteca, " +  
								"	 " + (isTitulos ? "t.data_criacao" : "mi.data_criacao") + " ";
		}
		
		if (isExemplar) {			
			joinEspecifico = "	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.exemplar AS e ON e.id_titulo_catalografico = t.id_titulo_catalografico " +
							"	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.material_informacional AS mi ON mi.id_material_informacional = e.id_exemplar ";
		} else {			
			joinEspecifico = "	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.assinatura a ON a.id_titulo_catalografico = t.id_titulo_catalografico " +
							"	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.fasciculo f ON f.id_assinatura = a.id_assinatura " +
							"	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.material_informacional AS mi ON mi.id_material_informacional = f.id_fasciculo ";
		}
		
		StringBuilder sqlEspecifico = new StringBuilder("" +
				" SELECT " +
					 selectEspecifico +
				" FROM biblioteca.titulo_catalografico t " +
					 joinEspecifico +
				"	 " + (isTitulos ? "LEFT" : "INNER") + " JOIN biblioteca.situacao_material_informacional AS smi ON smi.id_situacao_material_informacional = mi.id_situacao_material_informacional " +
				"	 LEFT JOIN biblioteca.biblioteca b on mi.id_biblioteca = b.id_biblioteca " +
				"	 INNER JOIN biblioteca.cache_entidades_marc cem on cem.id_titulo_catalografico = t.id_titulo_catalografico " +
				"	 INNER JOIN comum.registro_entrada AS registro_entrada ON registro_entrada.id_entrada = " + (isTitulos ? "t" : "mi") + ".id_registro_criacao " +
				"	 INNER JOIN comum.usuario AS usuario ON registro_entrada.id_usuario = usuario.id_usuario " +
				"	 INNER JOIN comum.pessoa AS pessoa ON pessoa.id_pessoa = usuario.id_pessoa " +
				" WHERE t.ativo = trueValue() AND t.catalogado = trueValue() ");
		
		if (isTitulos) {
			 sqlEspecifico.append("AND (mi.id_material_informacional is null OR (mi.ativo = trueValue() AND smi.situacao_de_baixa = falseValue())) ");
		} else {
			// Só recupera materiais ativos não baixados //
			sqlEspecifico.append("AND mi.ativo = trueValue() ");
			sqlEspecifico.append("AND smi.situacao_de_baixa = falseValue() ");
		}

		if ( idsPessoa != null && ! idsPessoa.isEmpty() ) {
			sqlEspecifico.append(" AND pessoa.id_pessoa IN ("+ StringUtils.join(idsPessoa, ',') +") ");
		}
		
		if ( idsBibliotecas != null && ! idsBibliotecas.isEmpty() ) {
			sqlEspecifico.append(" AND mi.id_biblioteca IN ("+ StringUtils.join(idsBibliotecas, ',') +") ");
			
		}
		
		if ( idsTipoMaterial != null && ! idsTipoMaterial.isEmpty() ) {
			sqlEspecifico.append(" AND mi.id_tipo_material IN ("+ StringUtils.join(idsTipoMaterial, ',') +") ");
		}
		
		if ( idsColecao != null && ! idsColecao.isEmpty() ) {
			sqlEspecifico.append(" AND mi.id_colecao IN ("+ StringUtils.join(idsColecao, ',') +") ");
		}
		
		sqlEspecifico.append(" AND " + (isTitulos ? "t" : "mi") + ".data_criacao >= :inicioPeriodo ");
		sqlEspecifico.append(" AND " + (isTitulos ? "t" : "mi") + ".data_criacao <= :fimPeriodo ");
		
		return sqlEspecifico;
	}

	
}
