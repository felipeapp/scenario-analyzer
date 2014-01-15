/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/08/2011
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
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 *
 * <p>Classe com as consultas específicas do relatório de materiais baixados </p>
 * 
 * @author jadson
 *
 */
public class RelatorioMateriaisBaixadosDao  extends GenericSigaaDAO{

	
	/* ********************************* Analítico **************************************** */
	
	
	/**
	 *    Recupera os dados que são mostrados no relatório de materiais baixados do acervo. (Analítico)
	 *
	 * @return uma lista de arrays, onde em cada posição do array tem os dados da projeção. <br/>
	 * [0] = id do material <br/>
	 * [1] = código de barras do material <br/>
	 * [2] = motivo da baixa <br/>
	 * [3] = o id da biblioteca <br/>
	 * [4] = a descrição da biblioteca <br/>
	 * [5] = quem fez a baixa <br/>
	 * [6] = quando foi feito a baixa <br/>
	 * [7] = numero do sistema, autor e título <br/>
	 * [8] = número do patrimônio <br/>
	 * [9] = classificacao 1 <br/>
	 * [10] = classificacao 2 <br/>
	 * [11] = classificacao 3 <br/>
	 * [12] = coleção <br/>
	 * [13] = tipo de material </br>
	 * [14] = id to título
	 */
	public List<Object[]> findMateriaisBaixadosPorPeriodoAnalitico(Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			boolean retornarExemplares, boolean retornarFasciculos, FiltroClassificacoesRelatoriosBiblioteca classificacao,  String classeInicial, String classeFinal, String sqlModalidadeAquisicaoExemplares, String sqlModalidadeAquisicaoFasciculos )  throws DAOException {
		
		List<Object[]> list = new ArrayList<Object[]>();
		
		if ( retornarExemplares ) {
			
			StringBuilder sql = new StringBuilder();
			
			sql.append(" SELECT m.id_material_informacional, m.codigo_barras, m.motivo_baixa, biblioteca.id_biblioteca, " +
					"biblioteca.descricao AS d_biblioteca, p.nome, m.data_ultima_atualizacao, m.informacoes_titulo_material_baixado, e.numero_patrimonio, " +
					"tit.classificacao_1, tit.classificacao_2, tit.classificacao_3, col.descricao AS d_colecao, tpm.descricao AS d_tipo_material," +
					"e.id_titulo_catalografico ");
			sql.append(" FROM biblioteca.material_informacional m ");
			sql.append(" INNER JOIN biblioteca.biblioteca biblioteca on biblioteca.id_biblioteca = m.id_biblioteca ");
			sql.append(" INNER JOIN biblioteca.situacao_material_informacional s on m.id_situacao_material_informacional = s.id_situacao_material_informacional ");
			sql.append(" INNER JOIN biblioteca.exemplar e on e.id_exemplar = m.id_material_informacional ");
			sql.append("	INNER JOIN biblioteca.colecao AS col ON col.id_colecao = m.id_colecao ");
			sql.append("	INNER JOIN biblioteca.tipo_material AS tpm ON tpm.id_tipo_material = m.id_tipo_material ");
			sql.append("	INNER JOIN biblioteca.titulo_catalografico AS tit ON tit.id_titulo_catalografico = e.id_titulo_catalografico ");
			
			// Infelizmente left join para suportar os materiais migrados, que não possuem registro de quem fez a baixa
			
			sql.append(" LEFT JOIN comum.registro_entrada reg on reg.id_entrada = m.id_registro_ultima_atualizacao");
			sql.append(" LEFT JOIN comum.usuario u on reg.id_usuario = u.id_usuario");
			sql.append(" LEFT JOIN comum.pessoa p on p.id_pessoa = u.id_pessoa ");
			
			sql.append(" WHERE s.situacao_de_baixa = trueValue() AND m.ativo = trueValue() AND ( m.data_ultima_atualizacao between :inicioPeriodo AND :fimPeriodo ) ");
			
			if ( bibliotecas != null && ! bibliotecas.isEmpty() )
				sql.append("  AND  biblioteca.id_biblioteca in "+UFRNUtils.gerarStringIn(bibliotecas));
			
			if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
				sql.append(" AND tit."+classificacao.getColunaClassificacao()+" >= :classeInicial AND tit."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
			}
			
			sql.append(sqlModalidadeAquisicaoExemplares);
			
			Query q = getSession().createSQLQuery(sql.toString());
			q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
			q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999) );
			
			if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
				q.setString("classeInicial", classeInicial );
				q.setString("classeFinal", classeFinal);
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> listTemp = q.list();
			
			list.addAll( listTemp );
			
		}
		
		
		if ( retornarFasciculos ) {
			
			StringBuilder sql = new StringBuilder();
			
			sql.append(" SELECT m.id_material_informacional, m.codigo_barras, m.motivo_baixa, biblioteca.id_biblioteca, " +
					" biblioteca.descricao AS d_biblioteca, p.nome, m.data_ultima_atualizacao, m.informacoes_titulo_material_baixado, CAST(NULL AS varchar) AS numero_patrimonio, " +
					" tit.classificacao_1, tit.classificacao_2, tit.classificacao_3, col.descricao AS d_colecao, tpm.descricao AS d_tipo_material," +
					" a.id_titulo_catalografico ");
			sql.append(" FROM biblioteca.material_informacional m ");
			sql.append(" INNER JOIN biblioteca.biblioteca biblioteca on biblioteca.id_biblioteca = m.id_biblioteca ");
			sql.append(" INNER JOIN biblioteca.situacao_material_informacional s on m.id_situacao_material_informacional = s.id_situacao_material_informacional ");
			sql.append(" INNER JOIN biblioteca.fasciculo f on f.id_fasciculo = m.id_material_informacional ");
			sql.append(" INNER JOIN biblioteca.assinatura a on a.id_assinatura = f.id_assinatura ");
			sql.append("	INNER JOIN biblioteca.colecao AS col ON col.id_colecao = m.id_colecao ");
			sql.append("	INNER JOIN biblioteca.tipo_material AS tpm ON tpm.id_tipo_material = m.id_tipo_material ");
			sql.append("	INNER JOIN biblioteca.titulo_catalografico AS tit ON tit.id_titulo_catalografico = a.id_titulo_catalografico ");
			
			// Infelizmente left join para suportar os materiais migrados, que não possuem registro de quem fez a baixa
			
			sql.append(" LEFT JOIN comum.registro_entrada reg on reg.id_entrada = m.id_registro_ultima_atualizacao");
			sql.append(" LEFT JOIN comum.usuario u on reg.id_usuario = u.id_usuario");
			sql.append(" LEFT JOIN comum.pessoa p on p.id_pessoa = u.id_pessoa ");
			
			sql.append(" WHERE s.situacao_de_baixa = trueValue() AND m.ativo = trueValue() AND ( m.data_ultima_atualizacao between :inicioPeriodo AND :fimPeriodo ) ");
			
			if ( bibliotecas != null && ! bibliotecas.isEmpty() )
				sql.append("  AND  biblioteca.id_biblioteca in "+UFRNUtils.gerarStringIn(bibliotecas));
			
			if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
				sql.append(" AND tit."+classificacao.getColunaClassificacao()+" >= :classeInicial AND tit."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
			}
			
			sql.append(sqlModalidadeAquisicaoFasciculos);
			
			Query q = getSession().createSQLQuery(sql.toString());
			q.setTimestamp("inicioPeriodo",  CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0));
			q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999) );
		
			if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
				q.setString("classeInicial", classeInicial );
				q.setString("classeFinal", classeFinal);
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> listTemp = q.list();
			
			list.addAll(listTemp);
		
		}
		
		return list;
		
	}
	
	
	
	
	/* ********************************* Sintético **************************************** */
	
	
	
	/**
	 * Retorna a quantidade de materiais baixados, agrupados pela coleção.
	 */
	public List<Object[]> findQtdMateriaisBaixadosSintetico(Collection<Integer> idBibliotecas, Date inicioPeriodo, Date fimPeriodo,
			boolean retornarExemplares, boolean retornarFasciculos,  FiltroClassificacoesRelatoriosBiblioteca classificacao,  String classeInicial, String classeFinal
			, int tipoModalidadeEscolhida, AgrupamentoRelatoriosBiblioteca agrupamento1,
			String sqlModalidadeAquisicaoExemplares, String sqlModalidadeAquisicaoFasciculos)
			throws DAOException {
		
		agrupamento1.setNomeColunaDataMesAno(" m.data_ultima_atualizacao ");
		
		StringBuilder sql = new StringBuilder(" SELECT ");
		
		
		if(agrupamento1 == AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_1 
				|| agrupamento1 == AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_2 
				|| agrupamento1 == AgrupamentoRelatoriosBiblioteca.CLASSIFICACAO_3 
				|| agrupamento1 == AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL
				|| agrupamento1 == AgrupamentoRelatoriosBiblioteca.COLECAO
				|| agrupamento1 == AgrupamentoRelatoriosBiblioteca.MES){
			sql.append(" COALESCE( " + agrupamento1.getCampoAgrupamento() + ", '" + agrupamento1.substituiValoresNull + "' ) AS "+agrupamento1.nomeCampo+" , ");
		}
		
		sql.append("	count(m.id_material_informacional) AS mats, ");
		
		if( retornarExemplares && retornarFasciculos ){
			sql.append("	count(DISTINCT COALESCE(e.id_titulo_catalografico, a.id_titulo_catalografico) ) AS qtdTitulos " );
		}else{
			if( retornarExemplares  ){
				sql.append("	count(DISTINCT e.id_titulo_catalografico ) AS qtdTitulos " );
			
			}else{
				if(  retornarFasciculos ){
					sql.append("	count(DISTINCT a.id_titulo_catalografico ) AS qtdTitulos " );
				}
			}
		}
		
		sql.append(" FROM biblioteca.material_informacional m ");
		sql.append(" INNER JOIN biblioteca.biblioteca biblioteca on biblioteca.id_biblioteca = m.id_biblioteca ");
		sql.append(" INNER JOIN biblioteca.situacao_material_informacional s on m.id_situacao_material_informacional = s.id_situacao_material_informacional ");
		
		if(agrupamento1 == AgrupamentoRelatoriosBiblioteca.COLECAO )
			sql.append(" INNER JOIN biblioteca.colecao colecao on colecao.id_colecao = m.id_colecao ");
		
		if(agrupamento1 == AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL )
			sql.append(" INNER JOIN biblioteca.tipo_material tipoMaterial on tipoMaterial.id_tipo_material = m.id_tipo_material ");
		
		if(retornarExemplares && retornarFasciculos){
			sql.append(" LEFT JOIN biblioteca.exemplar e on e.id_exemplar = m.id_material_informacional ");
			sql.append(" LEFT JOIN biblioteca.fasciculo f on f.id_fasciculo = m.id_material_informacional ");
			sql.append(" LEFT JOIN biblioteca.assinatura a on a.id_assinatura = f.id_assinatura ");
			sql.append(" INNER JOIN biblioteca.titulo_catalografico AS titulo ON titulo.id_titulo_catalografico = COALESCE( e.id_titulo_catalografico, a.id_titulo_catalografico ) ");
			
		}else{
			if(retornarExemplares){
				sql.append(" INNER JOIN biblioteca.exemplar e on e.id_exemplar = m.id_material_informacional ");
				sql.append(" INNER JOIN biblioteca.titulo_catalografico AS titulo ON titulo.id_titulo_catalografico = e.id_titulo_catalografico ");
			}else{
				if(retornarFasciculos){
					sql.append(" INNER JOIN biblioteca.fasciculo f on f.id_fasciculo = m.id_material_informacional ");
					sql.append(" INNER JOIN biblioteca.assinatura a on a.id_assinatura = f.id_assinatura ");
					sql.append("  INNER JOIN biblioteca.titulo_catalografico AS tit ON tit.id_titulo_catalografico = a.id_titulo_catalografico ");
				}
			}
		}
		
		
		sql.append(" WHERE s.situacao_de_baixa = trueValue() AND m.ativo = trueValue() AND ( m.data_ultima_atualizacao between :inicioPeriodo AND :fimPeriodo ) ");
		
		
		if ( idBibliotecas != null && ! idBibliotecas.isEmpty() )
			sql.append("  AND  biblioteca.id_biblioteca in "+UFRNUtils.gerarStringIn(idBibliotecas));
		
		if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
			sql.append(" AND titulo."+classificacao.getColunaClassificacao()+" >= :classeInicial AND titulo."+classificacao.getColunaClassificacao()+" <= :classeFinal ");
		}
		
		if(retornarExemplares && retornarFasciculos){
			sql.append( sqlModalidadeAquisicaoExemplares );
			sql.append( sqlModalidadeAquisicaoFasciculos );
			
		}else{
			if(retornarExemplares){
				sql.append( sqlModalidadeAquisicaoExemplares );
			}else{
				if(retornarFasciculos){
					sql.append( sqlModalidadeAquisicaoFasciculos );
				}
			}
		}
		
		
		sql.append(" GROUP BY "+agrupamento1.nomeCampo);
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999) );
		
		if(StringUtils.notEmpty(classeInicial) && StringUtils.notEmpty(classeFinal)){
			q.setString("classeInicial", classeInicial );
			q.setString("classeFinal", classeFinal);
		}
		
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}
	
}
