/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 10/08/2011
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
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 * 
 * <p>
 * Dao espec�fico para o relat�rio de empr�stimos por classifica��o
 * </p>
 * 
 * 
 * @author jadson
 * 
 */
public class RelatorioEmprestimosPorClassificacaoDao extends GenericSigaaDAO {

	
	
	/**
	 * Retorna o quantitativo de empr�stimos por classifica��o (CDU ou Black).
	 * Pode ser agrupado por cole��o ao tipo de material.
	 * 
	 * @author Br�ulio Bezerra
	 * @author Jadson
	 * @since 08/12/2009
	 * @version 1.0 Cria��o
	 * @version 1.1 Br�ulio: corrigida para levar em conta somente registros
	 *          ativos e tamb�m as renova��es
	 * @version 2.0 Jadson: corrigindo a contagem com a utiliza��o do UNION ALL. Adicionando a possibilidade do usu�rio escolher os agrupamentos: 
	 * por classifica��o (CDU ou Black)  ou por  Tipo de Material ou Cole��o. Adicionando o filtro cole��es e tipos de materiais.
	 * 
	 */
	public List<Object[]> countEmprestimosPorClassificacao(
			Collection<Integer> idBibliotecas, Collection<Integer> idsColecoes, Collection<Integer> idsTiposDeMaterial
			, Date inicioPeriodo, Date fimPeriodo, FiltroClassificacoesRelatoriosBiblioteca classificacao, AgrupamentoRelatoriosBiblioteca agrupamento1) throws DAOException {

		
		StringBuilder sql = new StringBuilder(
				" SELECT interna.classificacao as classificacao, interna."+agrupamento1.nomeCampo+" as agrupamento_relatorio, sum(interna.total) as total FROM ( "+
						 " SELECT  "
						+" COALESCE( tit."+classificacao.getColunaClassePrincipal()+", 'Sem Classe'  ) AS classificacao , "
						+" COALESCE( " + agrupamento1.campoAgrupamento + ", '" + agrupamento1.substituiValoresNull + "' ) AS "+agrupamento1.nomeCampo+" , "
						+" COUNT (distinct emprestimo.id_emprestimo) AS total" 
						+" FROM  biblioteca.emprestimo AS emprestimo  "
						+" INNER JOIN biblioteca.material_informacional AS material ON emprestimo.id_material = material.id_material_informacional  "
						+" LEFT JOIN biblioteca.exemplar                AS exe ON emprestimo.id_material = exe.id_exemplar  "
						+" LEFT JOIN biblioteca.fasciculo               AS fas ON emprestimo.id_material = fas.id_fasciculo  "
						+" LEFT JOIN biblioteca.assinatura              AS ass ON ass.id_assinatura = fas.id_assinatura  "
						+" INNER JOIN biblioteca.titulo_catalografico   AS tit ON tit.id_titulo_catalografico = COALESCE( exe.id_titulo_catalografico, ass.id_titulo_catalografico )  "
						+" INNER JOIN biblioteca.tipo_material tipoMaterial on  tipoMaterial.id_tipo_material = material.id_tipo_material "
						+" INNER JOIN biblioteca.colecao colecao on  colecao.id_colecao = material.id_colecao "
						+" WHERE  tit.ativo = trueValue() AND tit.catalogado = trueValue() AND emprestimo.ativo = trueValue()  ");

		sql.append(" AND  ( emprestimo.data_emprestimo BETWEEN :dataInicio AND :dataFim ) ");
			
		if (idBibliotecas != null && idBibliotecas.size() > 0) {
			sql.append("	AND material.id_biblioteca in "+ UFRNUtils.gerarStringIn(idBibliotecas) + "  ");
		}

		if (idsColecoes != null && idsColecoes.size() > 0) {
			sql.append("	AND colecao.id_colecao in "+ UFRNUtils.gerarStringIn(idsColecoes) + "  ");
		}

		if (idsTiposDeMaterial != null && idsTiposDeMaterial.size() > 0) {
			sql.append("	AND tipoMaterial.id_tipo_material in "+ UFRNUtils.gerarStringIn(idsTiposDeMaterial) + "  ");
		}
		
		sql.append("GROUP BY "+agrupamento1.campoAgrupamento+", tit."+classificacao.getColunaClassePrincipal()+"   ");
		sql.append( "UNION ALL ( " );
		
		sql.append( " SELECT  "
				+" COALESCE( tit."+classificacao.getColunaClassePrincipal()+", 'Sem Classe'  ) AS classificacao , "
				+" COALESCE( " + agrupamento1.campoAgrupamento + ", '" + agrupamento1.substituiValoresNull + "' ) AS "+agrupamento1.nomeCampo+" , "
				+" COUNT (distinct porrogacaoEmprestimo.id_prorrogacao_emprestimo ) AS total "
				+" FROM biblioteca.prorrogacao_emprestimo porrogacaoEmprestimo "
				+" INNER JOIN biblioteca.emprestimo emprestimo on emprestimo.id_emprestimo = porrogacaoEmprestimo.id_emprestimo "
				+" INNER JOIN biblioteca.material_informacional AS material ON emprestimo.id_material = material.id_material_informacional  "
				+" LEFT JOIN biblioteca.exemplar                AS exe ON emprestimo.id_material = exe.id_exemplar  "
				+" LEFT JOIN biblioteca.fasciculo               AS fas ON emprestimo.id_material = fas.id_fasciculo  "
				+" LEFT JOIN biblioteca.assinatura              AS ass ON ass.id_assinatura = fas.id_assinatura  "
				+" INNER JOIN biblioteca.titulo_catalografico   AS tit ON tit.id_titulo_catalografico = COALESCE( exe.id_titulo_catalografico, ass.id_titulo_catalografico )  "
				+" INNER JOIN biblioteca.tipo_material tipoMaterial on  tipoMaterial.id_tipo_material = material.id_tipo_material "
				+" INNER JOIN biblioteca.colecao colecao on  colecao.id_colecao = material.id_colecao "
				+" WHERE tit.ativo = trueValue() AND tit.catalogado = trueValue() AND emprestimo.ativo = trueValue()  ");
		
		sql.append(" AND porrogacaoEmprestimo.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+"AND  ( porrogacaoEmprestimo.data_cadastro BETWEEN :dataInicio AND :dataFim ) ");
		
		if (idBibliotecas != null && idBibliotecas.size() > 0) {
			sql.append("	AND material.id_biblioteca in "+ UFRNUtils.gerarStringIn(idBibliotecas) + "  ");
		}

		if (idsColecoes != null && idsColecoes.size() > 0) {
			sql.append("	AND colecao.id_colecao in "+ UFRNUtils.gerarStringIn(idsColecoes) + "  ");
		}

		if (idsTiposDeMaterial != null && idsTiposDeMaterial.size() > 0) {
			sql.append("	AND tipoMaterial.id_tipo_material in "+ UFRNUtils.gerarStringIn(idsTiposDeMaterial) + "  ");
		}
		
		sql.append("GROUP BY "+agrupamento1.campoAgrupamento+", tit."+classificacao.getColunaClassePrincipal()+"   ");
		
		
		sql.append( " )  ) as interna " );
		
		sql.append("GROUP BY classificacao, agrupamento_relatorio ");
		sql.append("ORDER BY classificacao, agrupamento_relatorio ");

		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		
		/**
		 * [0] = classifica��o
		 * [1] = descri��o Agrupamento
		 * [2] = quantidade de empr�stimos
		 */
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();

		return dados;
	}
	
	/**
	 * Retorna uma listagem com T�tulos(n�, autor, titulo, ano, edi��o) e suas quantidades de empr�timos. Al�m da classifia��o (CDU ou Black)
	 * Pode ser agrupado por cole��o ao tipo de material.
	 * 
	 * @author Br�ulio Bezerra
	 * @since 08/12/2009
	 * @version 1.0 Cria��o
	 * @version 1.1 Br�ulio: corrigida para levar em conta somente registros
	 *          ativos e tamb�m as renova��es
	 */
	public int countEmprestimosAnaliticoPorClassificacao(
			Collection<Integer> idBibliotecas, Collection<Integer> idsColecoes, Collection<Integer> idsTiposDeMaterial
			, Date inicioPeriodo, Date fimPeriodo) throws DAOException {
		
		String sqlBiblioteca = " ";
		String sqlColecao = " ";
		String sqlTipoMaterial = " ";
		
		if (idBibliotecas != null && idBibliotecas.size() > 0) {
			sqlBiblioteca = "	AND material.id_biblioteca in "+ UFRNUtils.gerarStringIn(idBibliotecas) + "  ";
		}

		if (idsColecoes != null && idsColecoes.size() > 0) {
			sqlColecao = "	AND colecao.id_colecao in "+ UFRNUtils.gerarStringIn(idsColecoes) + "  ";
		}

		if (idsTiposDeMaterial != null && idsTiposDeMaterial.size() > 0) {
			sqlTipoMaterial = "	AND tipoMaterial.id_tipo_material in "+ UFRNUtils.gerarStringIn(idsTiposDeMaterial) + "  ";
		}
			
			
			
		StringBuilder sql = new StringBuilder(
			" SELECT COUNT (distinct idTitulo ) AS qtd " +
			" FROM ( ");
		
			sql.append(" SELECT  "
			+" distinct tit.id_titulo_catalografico AS idTitulo " 
			+" FROM  biblioteca.emprestimo AS emprestimo  "
			+" INNER JOIN biblioteca.material_informacional AS material ON emprestimo.id_material = material.id_material_informacional  "
			+" LEFT JOIN biblioteca.exemplar                AS exe ON emprestimo.id_material = exe.id_exemplar  "
			+" LEFT JOIN biblioteca.fasciculo               AS fas ON emprestimo.id_material = fas.id_fasciculo  "
			+" LEFT JOIN biblioteca.assinatura              AS ass ON ass.id_assinatura = fas.id_assinatura  "
			+" INNER JOIN biblioteca.titulo_catalografico   AS tit ON tit.id_titulo_catalografico = COALESCE( exe.id_titulo_catalografico, ass.id_titulo_catalografico )  "
			+" INNER JOIN biblioteca.tipo_material tipoMaterial on  tipoMaterial.id_tipo_material = material.id_tipo_material "
			+" INNER JOIN biblioteca.colecao colecao on  colecao.id_colecao = material.id_colecao "
			+" WHERE tit.ativo = trueValue() AND tit.catalogado = trueValue() AND emprestimo.ativo = trueValue() "
			+" AND  ( emprestimo.data_emprestimo BETWEEN :dataInicio AND :dataFim ) "
			+sqlBiblioteca
			+sqlColecao
			+sqlTipoMaterial
			);
			
		sql.append( "UNION ALL ( " );
		
		sql.append( " SELECT  "
				+" distinct tit.id_titulo_catalografico AS idTitulo "
				+" FROM biblioteca.prorrogacao_emprestimo porrogacaoEmprestimo "
				+" INNER JOIN biblioteca.emprestimo emprestimo on emprestimo.id_emprestimo = porrogacaoEmprestimo.id_emprestimo "
				+" INNER JOIN biblioteca.material_informacional AS material ON emprestimo.id_material = material.id_material_informacional  "
				+" LEFT JOIN biblioteca.exemplar                AS exe ON emprestimo.id_material = exe.id_exemplar  "
				+" LEFT JOIN biblioteca.fasciculo               AS fas ON emprestimo.id_material = fas.id_fasciculo  "
				+" LEFT JOIN biblioteca.assinatura              AS ass ON ass.id_assinatura = fas.id_assinatura  "
				+" INNER JOIN biblioteca.titulo_catalografico   AS tit ON tit.id_titulo_catalografico = COALESCE( exe.id_titulo_catalografico, ass.id_titulo_catalografico )  "
				+" INNER JOIN biblioteca.tipo_material tipoMaterial on  tipoMaterial.id_tipo_material = material.id_tipo_material "
				+" INNER JOIN biblioteca.colecao colecao on  colecao.id_colecao = material.id_colecao "
				+" WHERE tit.ativo = trueValue() AND tit.catalogado = trueValue() AND emprestimo.ativo = trueValue()  "
				+" AND porrogacaoEmprestimo.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+" AND  ( porrogacaoEmprestimo.data_cadastro BETWEEN :dataInicio AND :dataFim ) "
				+sqlBiblioteca
				+sqlColecao
				+sqlTipoMaterial
				);
		
		
		sql.append( " )  " );
		sql.append( " ) as interna " );
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		return ((BigInteger) q.uniqueResult()).intValue();

	}
	
	
	/**
	 * Retorna uma listagem com T�tulos(n�, autor, titulo, ano, edi��o) e suas quantidades de empr�timos. Al�m da classifia��o (CDU ou Black)
	 * Pode ser agrupado por cole��o ao tipo de material.
	 * 
	 * @author Br�ulio Bezerra
	 * @since 08/12/2009
	 * @version 1.0 Cria��o
	 * @version 1.1 Br�ulio: corrigida para levar em conta somente registros
	 *          ativos e tamb�m as renova��es
	 */
	public List<Object[]> listaEmprestimosAnaliticoPorClassificacao(
			Collection<Integer> idBibliotecas, Collection<Integer> idsColecoes, Collection<Integer> idsTiposDeMaterial
			, Date inicioPeriodo, Date fimPeriodo, FiltroClassificacoesRelatoriosBiblioteca classificacao, int pagina, int limite) throws DAOException {
		
		String sqlBiblioteca = " ";
		String sqlColecao = " ";
		String sqlTipoMaterial = " ";
		
		if (idBibliotecas != null && idBibliotecas.size() > 0) {
			sqlBiblioteca = "	AND material.id_biblioteca in "+ UFRNUtils.gerarStringIn(idBibliotecas) + "  ";
		}

		if (idsColecoes != null && idsColecoes.size() > 0) {
			sqlColecao = "	AND colecao.id_colecao in "+ UFRNUtils.gerarStringIn(idsColecoes) + "  ";
		}

		if (idsTiposDeMaterial != null && idsTiposDeMaterial.size() > 0) {
			sqlTipoMaterial = "	AND tipoMaterial.id_tipo_material in "+ UFRNUtils.gerarStringIn(idsTiposDeMaterial) + "  ";
		}
			
			
			
		StringBuilder sql = new StringBuilder(
			" SELECT interna.numero_do_sistema_titulo as numero_do_sistema_titulo, interna.id_titulo as id_titulo" +
			", interna.classificacao as classificacao, sum(interna.totalEmprestimos) as totalEmprestimos, sum(interna.totalRenovacoes) as totalRenovacoes " +
			", 0 as totalGeral "+
			"FROM ( ");
		
			sql.append(" SELECT  "
			+" tit.numero_do_sistema as numero_do_sistema_titulo, "
			+" tit.id_titulo_catalografico as id_titulo, "
			+" COALESCE( tit."+classificacao.getColunaClassificacao()+", 'Sem Classe'  ) AS classificacao , "
			+" COUNT (distinct emprestimo.id_emprestimo) AS totalEmprestimos, " 
			+" 0 AS totalRenovacoes "
			+" FROM  biblioteca.emprestimo AS emprestimo  "
			+" INNER JOIN biblioteca.material_informacional AS material ON emprestimo.id_material = material.id_material_informacional  "
			+" LEFT JOIN biblioteca.exemplar                AS exe ON emprestimo.id_material = exe.id_exemplar  "
			+" LEFT JOIN biblioteca.fasciculo               AS fas ON emprestimo.id_material = fas.id_fasciculo  "
			+" LEFT JOIN biblioteca.assinatura              AS ass ON ass.id_assinatura = fas.id_assinatura  "
			+" INNER JOIN biblioteca.titulo_catalografico   AS tit ON tit.id_titulo_catalografico = COALESCE( exe.id_titulo_catalografico, ass.id_titulo_catalografico )  "
			+" INNER JOIN biblioteca.tipo_material tipoMaterial on  tipoMaterial.id_tipo_material = material.id_tipo_material "
			+" INNER JOIN biblioteca.colecao colecao on  colecao.id_colecao = material.id_colecao "
			+" WHERE tit.ativo = trueValue() AND tit.catalogado = trueValue() AND emprestimo.ativo = trueValue() "
			+" AND  ( emprestimo.data_emprestimo BETWEEN :dataInicio AND :dataFim ) "
			+sqlBiblioteca
			+sqlColecao
			+sqlTipoMaterial
			+" GROUP BY numero_do_sistema_titulo, id_titulo, classificacao "
			);
			
		sql.append( "UNION ALL ( " );
		
		sql.append( " SELECT  "
				+" tit.numero_do_sistema as numero_do_sistema_titulo, "
				+" tit.id_titulo_catalografico as id_titulo, "
				+" COALESCE( tit."+classificacao.getColunaClassificacao()+", 'Sem Classe'  ) AS classificacao , "
				+" 0 AS totalEmprestimos, " 
				+" COUNT (distinct porrogacaoEmprestimo.id_prorrogacao_emprestimo ) AS totalRenovacoes "
				+" FROM biblioteca.prorrogacao_emprestimo porrogacaoEmprestimo "
				+" INNER JOIN biblioteca.emprestimo emprestimo on emprestimo.id_emprestimo = porrogacaoEmprestimo.id_emprestimo "
				+" INNER JOIN biblioteca.material_informacional AS material ON emprestimo.id_material = material.id_material_informacional  "
				+" LEFT JOIN biblioteca.exemplar                AS exe ON emprestimo.id_material = exe.id_exemplar  "
				+" LEFT JOIN biblioteca.fasciculo               AS fas ON emprestimo.id_material = fas.id_fasciculo  "
				+" LEFT JOIN biblioteca.assinatura              AS ass ON ass.id_assinatura = fas.id_assinatura  "
				+" INNER JOIN biblioteca.titulo_catalografico   AS tit ON tit.id_titulo_catalografico = COALESCE( exe.id_titulo_catalografico, ass.id_titulo_catalografico )  "
				+" INNER JOIN biblioteca.tipo_material tipoMaterial on  tipoMaterial.id_tipo_material = material.id_tipo_material "
				+" INNER JOIN biblioteca.colecao colecao on  colecao.id_colecao = material.id_colecao "
				+" WHERE tit.ativo = trueValue() AND tit.catalogado = trueValue() AND emprestimo.ativo = trueValue()  "
				+" AND porrogacaoEmprestimo.tipo = "+TipoProrrogacaoEmprestimo.RENOVACAO+"AND  ( porrogacaoEmprestimo.data_cadastro BETWEEN :dataInicio AND :dataFim ) "
				+sqlBiblioteca
				+sqlColecao
				+sqlTipoMaterial
				+" GROUP BY numero_do_sistema_titulo, id_titulo, classificacao "
				);
		
		
		sql.append( " )  " );
		sql.append( " ) as interna " );
		
		sql.append(" GROUP BY numero_do_sistema_titulo, id_titulo, classificacao "
				  +" ORDER BY totalEmprestimos  DESC, totalRenovacoes DESC ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		q.setFirstResult((pagina - 1) * limite);
		q.setMaxResults(limite);
		
		/**
		 * [0] = classifica��o
		 * [1] = descri��o Agrupamento
		 * [2] = quantidade de empr�stimos
		 */
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();

		return dados;
	}
	
	
	
	

}
