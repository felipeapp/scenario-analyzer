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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAreaCNPqMes;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioCrescimentoPorAreaCNPq;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AreaConhecimentoCNPqBibliotecaDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 *
 * <p>DAO utilizado exclusivamente para o relatório de crescimento de periódicos por área do CNPq. </p>
 * 
 * @author Felipe Rivas
 *
 */
public class RelatorioCrescimentoPeriodicosPorCNPqDAO extends GenericSigaaDAO {
	

	/**
	 * <p>Retorna o crescimento de fascículos no período.<p>
	 * 
	 */
	public List<DadosRelatorioCrescimentoPorAreaCNPq> findCrescimentoFasciculosByPeriodo(
			Collection<Integer> idBibliotecaList, Date inicioPeriodo, Date fimPeriodo,
			int tipoDeTombamento, FiltroClassificacoesRelatoriosBiblioteca classificacao) throws DAOException {
		
		
		List<Object[]> lista = findTotalFasciculosByPeriodo(idBibliotecaList, inicioPeriodo, fimPeriodo, 
				tipoDeTombamento, classificacao, true);

		List<DadosRelatorioCrescimentoPorAreaCNPq> retorno = new ArrayList<DadosRelatorioCrescimentoPorAreaCNPq>();
		
		for (Object[] objects : lista) {
			DadosRelatorioCrescimentoPorAreaCNPq dados = new DadosRelatorioCrescimentoPorAreaCNPq((Double)objects[0]);
			if( retorno.contains( dados)){
				dados = retorno.get(retorno.indexOf(dados));
				dados.adicionaDadosInternosPorMes( (Double)objects[1], String.valueOf(objects[2]), (String)objects[3], ((BigInteger)objects[4]).longValue());
			}else{
				dados.adicionaDadosInternosPorMes( (Double)objects[1], String.valueOf(objects[2]), (String)objects[3], ((BigInteger)objects[4]).longValue());
				retorno.add(dados);
			}
				
		}

		AreaConhecimentoCNPqBibliotecaDao dao = null;
		
		try{
			dao = new AreaConhecimentoCNPqBibliotecaDao();
			Collection<AreaConhecimentoCnpq> areas = dao.findGrandeAreasConhecimentoCnpq();
			
			areas.add(new AreaConhecimentoCnpq(99999999, "Sem Área", null));
			
			for (DadosRelatorioCrescimentoPorAreaCNPq d : retorno) {
				
				for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes d2 : d.getDadosInternos()) {
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento d3 : d2.getDadosInternos()) {
						d3.adicionaAreasSemCrescimento(areas);  // Para o relatório ter todas a classificações e a quantidade de colunas ficar igual
					}
				}
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		
		return retorno;
	}

	/**
	 * <p>Retorna a quantidade anterior de fascículos existentes ao período selecionado.<p>
	 * 
	 */
	public Map<String, Long> findQuantidadeAnteriorFasciculosByPeriodo(
			Collection<Integer> idBibliotecaList, Date inicioPeriodo, Date fimPeriodo,
			int tipoDeTombamento, FiltroClassificacoesRelatoriosBiblioteca classificacao) throws DAOException {
		
		List<Object[]> lista = findTotalFasciculosByPeriodo(idBibliotecaList, inicioPeriodo, fimPeriodo, 
				tipoDeTombamento, classificacao, false);

		Map<String, Long> retorno = new TreeMap<String, Long>();
		
		for (Object[] objects : lista) {
			retorno.put(String.valueOf(objects[0]), ((BigInteger)objects[1]).longValue() );
		}
		
		return retorno;
	}
	
	/**
	 * Retorna o total de fascículos para um dado período
	 * 
	 * @param idBibliotecaList
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param tipoDeTombamento
	 * @param isClassificacaoBlack
	 * @param isCrescimento
	 * @return
	 * @throws DAOException
	 */
	private List<Object[]> findTotalFasciculosByPeriodo(
			Collection<Integer> idBibliotecaList, Date inicioPeriodo, Date fimPeriodo,
			int tipoDeTombamento, FiltroClassificacoesRelatoriosBiblioteca classificacao, boolean isCrescimento) throws DAOException {		
		StringBuilder sql = new StringBuilder();
		
		String sqlDiferencaSelect = null;
		String sqlDiferencaWhere = null;
		String sqlDiferencaGroupBy = null;
		
		if (isCrescimento) {
			sqlDiferencaSelect = " EXTRACT(year from material.data_criacao)  as anoMaterial, " +
								" EXTRACT(month from material.data_criacao)  as mesMaterial, " + 
								" CASE WHEN assinatura.internacional = trueValue() THEN 'Internacional' ELSE 'Nacional' END as origem, ";
			sqlDiferencaWhere = "AND material.data_criacao >= :dataInicio AND material.data_criacao <= :dataFim ";
			sqlDiferencaGroupBy = " GROUP BY anoMaterial, mesMaterial, origem, descricaoAreaTitulo ";
		} else {
			sqlDiferencaSelect = "";
			sqlDiferencaWhere = "AND material.data_criacao < :dataInicio ";
			sqlDiferencaGroupBy = " GROUP BY descricaoAreaTitulo ";
		}
		
		sql.append("SELECT" +
				sqlDiferencaSelect +
				" COALESCE(  COALESCE( info.sigla, area.sigla ), 'Sem Área') AS descricaoAreaTitulo, " +	
				" COUNT(distinct material.id_material_informacional) as quantidade_materiais " );
		
		sql.append("FROM biblioteca.fasciculo AS fasciculo ");
		sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_assinatura = fasciculo.id_assinatura ");
		sql.append("INNER JOIN biblioteca.titulo_catalografico AS titulo ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
		sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		
		sql.append("LEFT JOIN comum.area_conhecimento_cnpq AS area ON area.id_area_conhecimento_cnpq = titulo." + classificacao.getColunaAreaConhecimentoCNPq() + " ");
		sql.append("LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = area.id_area_conhecimento_cnpq ");
		
		
		/* Observação:
		 * 
		 * Relatórios de crescimento não devem verificar somente os ativos, os que já existiram algum dia no acervo 
		 * também devem ser contados, senão pode gerar erros ao emitir o mesmo relatório tempos depois
		 * 
		 *  sql.append("AND  titulo.ativo = trueValue()AND situacao.situacao_de_baixa = falseValue() ");
		 * 
		 */
		sql.append(" WHERE material.ativo = trueValue() "
				+ sqlDiferencaWhere);
		
		if ( idBibliotecaList != null && ! idBibliotecaList.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(idBibliotecaList, ',') +") ");
		}
		
		if ( tipoDeTombamento != -1 ) {
			sql.append(" AND assinatura.modalidade_aquisicao = " + tipoDeTombamento + " ");
		}
		
		sql.append(sqlDiferencaGroupBy);
		sql.append("ORDER BY descricaoAreaTitulo");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		
		if (isCrescimento) {
			q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}

	/**
	 * <p>Retorna o crescimento de Títulos no período.<p>
	 * 
	 */
	public List<DadosRelatorioCrescimentoPorAreaCNPq> findCrescimentoTitulosByPeriodo(
			Collection<Integer> idBibliotecaList, Date inicioPeriodo, Date fimPeriodo,
			int tipoDeTombamento, FiltroClassificacoesRelatoriosBiblioteca classificacao) throws DAOException {
		
		List<Object[]> lista = findTotalTituloByPeriodo(idBibliotecaList, inicioPeriodo, fimPeriodo, 
				tipoDeTombamento, classificacao, true);
		
		List<DadosRelatorioCrescimentoPorAreaCNPq> retorno = new ArrayList<DadosRelatorioCrescimentoPorAreaCNPq>();
		
		for (Object[] objects : lista) {
			DadosRelatorioCrescimentoPorAreaCNPq dados = new DadosRelatorioCrescimentoPorAreaCNPq((Double)objects[0]);
			if( retorno.contains( dados)){
				dados = retorno.get(retorno.indexOf(dados));
				dados.adicionaDadosInternosPorMes((Double)objects[1], "", (String)objects[2], ((BigInteger)objects[3]).longValue());
			}else{
				dados.adicionaDadosInternosPorMes((Double)objects[1], "", (String)objects[2], ((BigInteger)objects[3]).longValue());
				retorno.add(dados);
			}
			
		}

		AreaConhecimentoCNPqBibliotecaDao dao = null;
		
		try{
			dao = new AreaConhecimentoCNPqBibliotecaDao();
			Collection<AreaConhecimentoCnpq> areas = dao.findGrandeAreasConhecimentoCnpq();
		
			areas.add(new AreaConhecimentoCnpq(99999999, "Sem Área", null));
			
			for (DadosRelatorioCrescimentoPorAreaCNPq d : retorno) {
				
				for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes d2 : d.getDadosInternos()) {
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento d3 : d2.getDadosInternos()) {
						d3.adicionaAreasSemCrescimento(areas); // Para o relatório ter todas a classificações e a quantidade de colunas ficar igual
					}
				}
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return retorno;
	}

	/**
	 * <p>Retorna a quantidade anterior de Título existentes ao período selecionado.<p>
	 * 
	 */
	public Map<String, Long> findQuantidadeAnteriorTitulosByPeriodo(
			Collection<Integer> idBibliotecaList, Date inicioPeriodo, Date fimPeriodo,
			int tipoDeTombamento, FiltroClassificacoesRelatoriosBiblioteca classificacao) throws DAOException {
		
		List<Object[]> lista = findTotalTituloByPeriodo(idBibliotecaList, inicioPeriodo, fimPeriodo, 
				tipoDeTombamento, classificacao, false);

		Map<String, Long> retorno = new TreeMap<String, Long>();
		
		for (Object[] objects : lista) {
			retorno.put(String.valueOf(objects[0]), ((BigInteger)objects[1]).longValue() );
		}
		
		return retorno;
	}

	/**
	 * Retorna o total de títulos para um dado período
	 * 
	 * @param idBibliotecaList
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @param tipoDeTombamento
	 * @param isClassificacaoBlack
	 * @param isCrescimento
	 * @return
	 * @throws DAOException
	 */
	private List<Object[]> findTotalTituloByPeriodo(
			Collection<Integer> idBibliotecaList, Date inicioPeriodo, Date fimPeriodo,
			int tipoDeTombamento, FiltroClassificacoesRelatoriosBiblioteca classificacao, boolean isCrescimento) throws DAOException {
		StringBuilder sql = new StringBuilder();
		
		String sqlDiferencaSelect = null;
		String sqlDiferencaGroupBy = null;
		String sqlDiferencaHaving = null;
		
		if (isCrescimento) {
			sql.append(" SELECT consultaInterna.anoTitulo as anoTitulo2, consultaInterna.mesTitulo as mesTitulo2" +
					", consultaInterna.descricaoAreaTitulo as descricaoAreaTitulo2, COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos2 " 
				+" FROM ( ");
		}else{
			sql.append(" SELECT consultaInterna.descricaoAreaTitulo as descricaoAreaTitulo2, COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos2 " 
				+" FROM ( ");
		}
		
		if (isCrescimento) {
			sqlDiferencaSelect = " EXTRACT( year from  MIN(material.data_criacao)  )  as anoTitulo, " + 
								" EXTRACT ( month FROM  MIN(material.data_criacao)  )  as mesTitulo, ";
			
			sqlDiferencaGroupBy = "GROUP BY titulo.id_titulo_catalografico, descricaoAreaTitulo ";
			sqlDiferencaHaving = " HAVING MIN(material.data_criacao) >= :dataInicio AND  MIN(material.data_criacao) <= :dataFim ";
		} else {
			sqlDiferencaSelect = "";
			sqlDiferencaGroupBy = "GROUP BY descricaoAreaTitulo ";
			sqlDiferencaHaving = " HAVING MIN(material.data_criacao) < :dataInicio ";
		}
		
		sql.append("SELECT" +
				sqlDiferencaSelect +
				" COALESCE(  COALESCE( info.sigla, area.sigla ), 'Sem Área') AS descricaoAreaTitulo, " +	 
				" COUNT(distinct titulo.id_titulo_catalografico) as quantidade_titulos " );
		
		sql.append("FROM biblioteca.titulo_catalografico titulo ");  
		
		sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
		sql.append("INNER JOIN biblioteca.fasciculo AS fasciculo ON fasciculo.id_assinatura = assinatura.id_assinatura  ");
		sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		
		sql.append("LEFT JOIN comum.area_conhecimento_cnpq AS area ON area.id_area_conhecimento_cnpq = titulo." + classificacao.getColunaAreaConhecimentoCNPq() + " ");
		sql.append("LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = area.id_area_conhecimento_cnpq ");
		
		/* Observação:
		 * 
		 * Relatórios de crescimento não devem verificar somente os ativos, os que já existiram algum dia no acervo 
		 * também devem ser contados, senão pode gerar erros ao emitir o mesmo relatório tempos depois
		 * 
		 *  sql.append("AND  titulo.ativo = trueValue() ");
		 * 
		 */
		
		sql.append("WHERE material.ativo = trueValue()  ");
	
		
		if ( idBibliotecaList != null && ! idBibliotecaList.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(idBibliotecaList, ',') +") ");
		}
		
		if ( tipoDeTombamento != -1 ) {
			sql.append(" AND assinatura.modalidade_aquisicao = " + tipoDeTombamento + " ");
		}
		
		sql.append(sqlDiferencaGroupBy);
		
		sql.append(sqlDiferencaHaving);
		
		sql.append(" ) as consultaInterna ");
		
		if (isCrescimento) 
			sql.append(" GROUP BY anoTitulo2, mesTitulo2, descricaoAreaTitulo2 " +
					" ORDER BY anoTitulo2, mesTitulo2, descricaoAreaTitulo2" );
		else
			sql.append(" GROUP BY descricaoAreaTitulo2 ORDER BY descricaoAreaTitulo2 " );
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		
		
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		
		if (isCrescimento) {
			q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}
	

}
