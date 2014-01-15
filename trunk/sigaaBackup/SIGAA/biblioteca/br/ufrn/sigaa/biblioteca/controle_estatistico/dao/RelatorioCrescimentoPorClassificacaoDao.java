/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 16/06/2011
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
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.AgrupamentoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAgrupamento;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorMes;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioCrescimentoPorClassificacao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;

/**
 * <p>Dao exclusivo para o relat�rio crescimento por classifica��o. </p>
 *
 * <p>Ralializa a contagem de novos T�tulos e Materiais no Acervo</p>
 *
 * @author jadson
 *
 */
public class RelatorioCrescimentoPorClassificacaoDao extends GenericSigaaDAO{

	
	
	
	/**
	 * <p>Retorna o crescimento de materiais no per�odo.<p>
	 * 
	 */
	public List<DadosRelatorioCrescimentoPorClassificacao> findCrescimentoMaterialByPeriodo(
			Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> formasDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos,
			AgrupamentoRelatoriosBiblioteca agrupamento )
			throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT" +
				" EXTRACT(year from material.data_criacao)  as anoMaterial, " +
				" EXTRACT(month from material.data_criacao)  as mesMaterial, " + /* extract � uma fun��o SQL ANSI*/
				agrupamento.campoAgrupamento+" as agrupamento, " +
				" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe'), " +	 
				" COUNT(distinct material.id_material_informacional) as quantidade_materiais " );
		
		if(retornarExemplares){ 
			sql.append("FROM  biblioteca.exemplar AS exemplar ");
			sql.append("INNER JOIN biblioteca.titulo_catalografico AS titulo ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exemplar.id_exemplar ");
		}
		
		if(retornarFasciculos){ 
			sql.append("FROM 	biblioteca.fasciculo AS fasciculo ");
			sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_assinatura = fasciculo.id_assinatura ");
			sql.append("INNER JOIN biblioteca.titulo_catalografico AS titulo ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		}
		
		if (agrupamento != AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL) {
			sql.append(agrupamento.join);
		}
		
		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		
		sql.append(" WHERE  material.ativo = trueValue() "
				+" AND material.data_criacao >= :dataInicio AND material.data_criacao <= :dataFim ");
		
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 * titulo.ativo = trueValue() AND situacao.situacao_de_baixa = falseValue() "
		 * 
		 */
		
		
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
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
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
		}
		
		sql.append(" GROUP BY anoMaterial, mesMaterial, "+agrupamento.campoAgrupamento+", "+classificacao.getColunaClassePrincipal() );
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		List<DadosRelatorioCrescimentoPorClassificacao> retorno = new ArrayList<DadosRelatorioCrescimentoPorClassificacao>();
		
		for (Object[] objects : lista) {
			DadosRelatorioCrescimentoPorClassificacao dados = new DadosRelatorioCrescimentoPorClassificacao((Double)objects[0]);
			if( retorno.contains( dados)){
				dados = retorno.get(retorno.indexOf(dados));
				dados.adicionaDadosInternosPorMes( (Double)objects[1], (String)objects[2], (String)objects[3], ((BigInteger)objects[4]).longValue());
			}else{
				dados.adicionaDadosInternosPorMes( (Double)objects[1], (String)objects[2], (String)objects[3], ((BigInteger)objects[4]).longValue());
				retorno.add(dados);
			}
			
			
		}
		
		for (DadosRelatorioCrescimentoPorClassificacao d : retorno) {
			
			for (DadosInternosRelatorioCrescimentoPorMes d2 : d.getDadosInternos()) {
				for (DadosInternosRelatorioCrescimentoPorAgrupamento d3 : d2.getDadosInternos()) {
					d3.adicionaClassificacoesSemCrescimento(classificacao);  // Para o relat�rio ter todas a classifica��es e a quantidade de colunas ficar igual
				}
			}
		}
		
		return retorno;
	}
	
	
	
	
	
	/**
	 * <p>Retorna o crescimento de materiais no per�odo.<p>
	 * 
	 * <p>Nesse caso agrupa apenas por tipo de material, cole��o e classifica��o bibliogr�fica. 
	 * N�o agrupando por ano e m�s como no caso de detalhado.<p>
	 * 
	 */
	public List<Object[]> findCrescimentoMaterialByPeriodoBasico(
			Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> formasDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos )
			throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT" +
				" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe'), " +	 
				AgrupamentoRelatoriosBiblioteca.COLECAO.campoAgrupamento+" as "+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", " + // Nesse caso vai ser fixo, o usu�rio n�o escolhe
				AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.campoAgrupamento+" as "+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo+", " + // Nesse caso vai ser fixo, o usu�rio n�o escolhe		
				" COUNT(distinct material.id_material_informacional) as quantidade_materiais " );
		
		if(retornarExemplares){ 
			sql.append("FROM  biblioteca.exemplar AS exemplar ");
			sql.append("INNER JOIN biblioteca.titulo_catalografico AS titulo ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exemplar.id_exemplar ");
		}
		
		if(retornarFasciculos){ 
			sql.append("FROM 	biblioteca.fasciculo AS fasciculo ");
			sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_assinatura = fasciculo.id_assinatura ");
			sql.append("INNER JOIN biblioteca.titulo_catalografico AS titulo ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		}
		
		
		sql.append(AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.join);
		sql.append(AgrupamentoRelatoriosBiblioteca.COLECAO.join);
		
		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		
		sql.append(" WHERE  material.ativo = trueValue() "
				+" AND material.data_criacao >= :dataInicio AND material.data_criacao <= :dataFim ");
		
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 * titulo.ativo = trueValue() AND situacao.situacao_de_baixa = falseValue() "
		 * 
		 */
		
		
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
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
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
		}
		
		sql.append(" GROUP BY "
				+classificacao.getColunaClassePrincipal()+", "
				+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", "
				+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo);
		
		sql.append(" ORDER BY "
				+classificacao.getColunaClassePrincipal()+", "
				+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", "
				+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo);
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}
	
	
	
	
	
	
	/**
	 * <p>Retorna o crescimento de T�tulos no per�odo.<p>
	 * 
	 */
	public List<DadosRelatorioCrescimentoPorClassificacao> findCrescimentoTitulosByPeriodo(
			Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> formasDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos )throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT consultaInterna.anoTitulo, consultaInterna.mesTitulo, consultaInterna.classificacao, COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos " 
				+" FROM ( ");
		
		sql.append("SELECT");
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" EXTRACT(year from titulo.data_criacao)  as anoTitulo, ");
		else
			sql.append(" EXTRACT(year from  MIN(material.data_criacao) )  as anoTitulo, ");
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" EXTRACT(month FROM titulo.data_criacao)  as mesTitulo, " ); /* extract � uma fun��o SQL ANSI*/
		else
			sql.append(" EXTRACT(month from  MIN(material.data_criacao) )  as mesTitulo, ");
		
		
		sql.append(" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe') as classificacao, " +	 
				   " COUNT(distinct titulo.id_titulo_catalografico) as quantidade_titulos " );
		
		sql.append("FROM biblioteca.titulo_catalografico titulo ");  
		
		if(retornarExemplares){ 
			sql.append("INNER JOIN biblioteca.exemplar AS exemplar ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exemplar.id_exemplar ");
		}
		
		if(retornarFasciculos){ 
			sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.fasciculo AS fasciculo ON fasciculo.id_assinatura = assinatura.id_assinatura  ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		}
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		sql.append(" WHERE 1 = 1 ");
			
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 * titulo.ativo = trueValue() 
		 * 
		 */
		
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
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
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
		}
		
		sql.append("GROUP BY titulo.id_titulo_catalografico, "+classificacao.getColunaClassePrincipal());
		
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" HAVING titulo.data_criacao >= :dataInicio AND titulo.data_criacao <= :dataFim "); // Se n�o selecionar uma biblioteca espec�fica uma a Data de cria��o do T�tulo
		else	
			sql.append(" HAVING MIN(material.data_criacao) >= :dataInicio AND MIN(material.data_criacao) <= :dataFim ");  // � considerado a crescimento de T�tulo quando o primeiro material � inclu�do na biblioteca.
		
		
		sql.append(" ) as consultaInterna "+
				" GROUP BY anoTitulo, mesTitulo, classificacao "+
				" ORDER BY anoTitulo, mesTitulo, classificacao" );
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		List<DadosRelatorioCrescimentoPorClassificacao> retorno = new ArrayList<DadosRelatorioCrescimentoPorClassificacao>();
		
		for (Object[] objects : lista) {
			DadosRelatorioCrescimentoPorClassificacao dados = new DadosRelatorioCrescimentoPorClassificacao((Double)objects[0]);
			if( retorno.contains( dados)){
				dados = retorno.get(retorno.indexOf(dados));
				dados.adicionaDadosInternosPorMes((Double)objects[1], "", (String)objects[2], ((BigInteger)objects[3]).longValue());
			}else{
				dados.adicionaDadosInternosPorMes((Double)objects[1], "", (String)objects[2], ((BigInteger)objects[3]).longValue());
				retorno.add(dados);
			}
			
		}
		
		for (DadosRelatorioCrescimentoPorClassificacao d : retorno) {
			
			for (DadosInternosRelatorioCrescimentoPorMes d2 : d.getDadosInternos()) {
				for (DadosInternosRelatorioCrescimentoPorAgrupamento d3 : d2.getDadosInternos()) {
					d3.adicionaClassificacoesSemCrescimento(classificacao); // Para o relat�rio ter todas a classifica��es e a quantidade de colunas ficar igual
				}
			}
		}
		
		return retorno;
	}
	
	
	

	/**
	 * <p>Retorna o crescimento de T�tulos no per�odo para o relat�rio b�sico.<p>
	 * 
	 */
	public List<Object[]> findCrescimentoTitulosByPeriodoBasico(
			Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> formasDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos )throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT consultaInterna.classificacao, COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos " 
				+" FROM ( ");
		
		sql.append("SELECT");
		
		sql.append(" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe') as classificacao, " +	 
				   " COUNT(distinct titulo.id_titulo_catalografico) as quantidade_titulos " );
		
		sql.append("FROM biblioteca.titulo_catalografico titulo ");  
		
		if(retornarExemplares){ 
			sql.append("INNER JOIN biblioteca.exemplar AS exemplar ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exemplar.id_exemplar ");
		}
		
		if(retornarFasciculos){ 
			sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.fasciculo AS fasciculo ON fasciculo.id_assinatura = assinatura.id_assinatura  ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		}
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		sql.append(" WHERE 1 = 1 ");
			
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 * titulo.ativo = trueValue() 
		 * 
		 */
		
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
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
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
		}
		
		sql.append("GROUP BY titulo.id_titulo_catalografico, "+classificacao.getColunaClassePrincipal());
		
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" HAVING titulo.data_criacao >= :dataInicio AND titulo.data_criacao <= :dataFim "); // Se n�o selecionar uma biblioteca espec�fica uma a Data de cria��o do T�tulo
		else	
			sql.append(" HAVING MIN(material.data_criacao) >= :dataInicio AND MIN(material.data_criacao) <= :dataFim ");  // � considerado a crescimento de T�tulo quando o primeiro material � inclu�do na biblioteca.
		
		
		sql.append(" ) as consultaInterna "+
				" GROUP BY classificacao "+
				" ORDER BY classificacao" );
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
	}
	
	
	
	
	
	
	
	
	/**
	 * <p>Retorna o crescimento de T�tulos no per�odo com agrupamento.<p>
	 * 
	 */
	public List<DadosRelatorioCrescimentoPorClassificacao> findCrescimentoTitulosAgrupadosByPeriodo(
			Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> formasDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos,
			AgrupamentoRelatoriosBiblioteca agrupamento  )throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT consultaInterna.anoTitulo, consultaInterna.mesTitulo, consultaInterna.agrupamento, consultaInterna.classificacao, COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos " 
				+" FROM ( ");
		
		sql.append("SELECT");
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" EXTRACT(year from titulo.data_criacao)  as anoTitulo, ");
		else
			sql.append(" EXTRACT(year from  MIN(material.data_criacao) )  as anoTitulo, ");
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" EXTRACT(month FROM titulo.data_criacao)  as mesTitulo, " ); /* extract � uma fun��o SQL ANSI*/
		else
			sql.append(" EXTRACT(month from  MIN(material.data_criacao) )  as mesTitulo, ");
		
		sql.append(agrupamento.campoAgrupamento+" as agrupamento, " +
				" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe') as classificacao, " +	 
				" COUNT(distinct titulo.id_titulo_catalografico) as quantidade_titulos " );
		
		sql.append("FROM biblioteca.titulo_catalografico titulo ");
		
		if(retornarExemplares){ 
			sql.append("INNER JOIN biblioteca.exemplar AS exemplar ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exemplar.id_exemplar ");
		}
		
		if(retornarFasciculos){ 
			sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.fasciculo AS fasciculo ON fasciculo.id_assinatura = assinatura.id_assinatura  ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		}
		
		if (agrupamento != AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL) {
			sql.append(agrupamento.join);  
		}
		
		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		sql.append("WHERE material.ativo = trueValue() "); // material.ativo =  true significa que um dia ele existiu de verdade no acervo.
		
		
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
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
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
		}
		
		sql.append("GROUP BY titulo.id_titulo_catalografico, "+agrupamento.campoAgrupamento+", "+classificacao.getColunaClassePrincipal());
		
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 * titulo.ativo = trueValue() AND situacao.situacao_de_baixa = falseValue() "
		 * 
		 */
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" HAVING titulo.data_criacao >= :dataInicio AND titulo.data_criacao <= :dataFim "); // Se n�o selecionar uma biblioteca espec�fica uma a Data de cria��o do T�tulo
		else	
			sql.append(" HAVING MIN(material.data_criacao) >= :dataInicio AND MIN(material.data_criacao) <= :dataFim ");  // � considerado a crescimento de T�tulo quando o primeiro material � inclu�do na biblioteca.
		
		
		sql.append(" ) as consultaInterna "+
				" GROUP BY anoTitulo, mesTitulo, consultaInterna.agrupamento, classificacao "+
				" ORDER BY anoTitulo, mesTitulo, consultaInterna.agrupamento, classificacao " );		
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		List<DadosRelatorioCrescimentoPorClassificacao> retorno = new ArrayList<DadosRelatorioCrescimentoPorClassificacao>();
		
		for (Object[] objects : lista) {
			DadosRelatorioCrescimentoPorClassificacao dados = new DadosRelatorioCrescimentoPorClassificacao((Double)objects[0]);
			if( retorno.contains( dados)){
				dados = retorno.get(retorno.indexOf(dados));
				dados.adicionaDadosInternosPorMes((Double)objects[1], (String)objects[2], (String)objects[3], ((BigInteger)objects[4]).longValue());
			}else{
				dados.adicionaDadosInternosPorMes((Double)objects[1], (String)objects[2], (String)objects[3], ((BigInteger)objects[4]).longValue());
				retorno.add(dados);
			}
			
		}
		
		for (DadosRelatorioCrescimentoPorClassificacao d : retorno) {
			
			for (DadosInternosRelatorioCrescimentoPorMes d2 : d.getDadosInternos()) {
				for (DadosInternosRelatorioCrescimentoPorAgrupamento d3 : d2.getDadosInternos()) {
					d3.adicionaClassificacoesSemCrescimento(classificacao); // Para o relat�rio ter todas a classifica��es e a quantidade de colunas ficar igual
				}
			}
		}
		
		return retorno;
	}
	
	
	
	/**
	 * <p>Retorna o crescimento de T�tulos no per�odo com agrupamento.<p>
	 * 
	 */
	public List<Object[]> findCrescimentoTitulosAgrupadosByPeriodoBasico(
			Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> formasDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos )throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT consultaInterna.classificacao, " +
				"consultaInterna."+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", " + // Nesse caso vai ser fixo, o usu�rio n�o escolhe
				"consultaInterna."+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo+", " + // Nesse caso vai ser fixo, o usu�rio n�o escolhe		
				" COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos " 
				+" FROM ( ");
		
		sql.append("SELECT");
		
		
		sql.append(
				" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe') as classificacao, " +
				AgrupamentoRelatoriosBiblioteca.COLECAO.campoAgrupamento+" as "+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", " + // Nesse caso vai ser fixo, o usu�rio n�o escolhe
				AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.campoAgrupamento+" as "+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo+", " + // Nesse caso vai ser fixo, o usu�rio n�o escolhe		
				" COUNT(distinct titulo.id_titulo_catalografico) as quantidade_titulos " );
		
		sql.append("FROM biblioteca.titulo_catalografico titulo ");
		
		if(retornarExemplares){ 
			sql.append("INNER JOIN biblioteca.exemplar AS exemplar ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exemplar.id_exemplar ");
		}
		
		if(retornarFasciculos){ 
			sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.fasciculo AS fasciculo ON fasciculo.id_assinatura = assinatura.id_assinatura  ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		}
		
		sql.append(AgrupamentoRelatoriosBiblioteca.COLECAO.join); 
		sql.append(AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.join); 
				

		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		sql.append("WHERE material.ativo = trueValue() "); // material.ativo =  true significa que um dia ele existiu de verdade no acervo.
		
		
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
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
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
		}
		
		sql.append("GROUP BY titulo.id_titulo_catalografico"+", "
				+AgrupamentoRelatoriosBiblioteca.COLECAO.campoAgrupamento+", " // Nesse caso vai ser fixo, o usu�rio n�o escolhe
				+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.campoAgrupamento+", " // Nesse caso vai ser fixo, o usu�rio n�o escolhe
				+classificacao.getColunaClassePrincipal());
		
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 * titulo.ativo = trueValue() AND situacao.situacao_de_baixa = falseValue() "
		 * 
		 */
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" HAVING titulo.data_criacao >= :dataInicio AND titulo.data_criacao <= :dataFim "); // Se n�o selecionar uma biblioteca espec�fica uma a Data de cria��o do T�tulo
		else	
			sql.append(" HAVING MIN(material.data_criacao) >= :dataInicio AND MIN(material.data_criacao) <= :dataFim ");  // � considerado a crescimento de T�tulo quando o primeiro material � inclu�do na biblioteca.
		
		
		sql.append(" ) as consultaInterna "+
				" GROUP BY classificacao, "
				+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", "
				+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo+" "
				+" ORDER BY classificacao, "
				+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", "
				+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo);		
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		return lista;
		
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// Consultas para a quantidade anterior ao crescimento /////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	/**
	 * <p>Retorna a quantidade anterior de materiais existentes ao per�odo selecionado.<p>
	 * 
	 */
	public Map<String, Long> findQuantidadeAnteriorMaterialByPeriodo(
			Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> formasDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos ,
			AgrupamentoRelatoriosBiblioteca agrupamento )
			throws DAOException {
		
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT" +
				" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe'), " +	 
				" COUNT(distinct material.id_material_informacional) as quantidade_materiais " );
		
		if(retornarExemplares){ 
			sql.append("FROM  biblioteca.exemplar AS exemplar ");
			sql.append("INNER JOIN biblioteca.titulo_catalografico AS titulo ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exemplar.id_exemplar ");
		}
		
		if(retornarFasciculos){ 
			sql.append("FROM 	biblioteca.fasciculo AS fasciculo ");
			sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_assinatura = fasciculo.id_assinatura ");
			sql.append("INNER JOIN biblioteca.titulo_catalografico AS titulo ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		}
		
		if (agrupamento != AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL) {
			sql.append(agrupamento.join);
		}
		
		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		
		sql.append(" WHERE material.ativo = trueValue() "
				+" AND material.data_criacao < :dataInicio ");
		
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 * titulo.ativo = trueValue() AND situacao.situacao_de_baixa = falseValue() "
		 * 
		 */
		
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
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
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
		}
		
		sql.append(" GROUP BY "+classificacao.getColunaClassePrincipal() );
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		Map<String, Long> retorno = new TreeMap<String, Long>();
		
		for (Object[] objects : lista) {
			retorno.put((String)objects[0], ((BigInteger)objects[1]).longValue());
		}
		
		return retorno;
	
	}
	
	/**
	 * <p>Retorna a quantidade anterior de T�tulo existentes ao per�odo selecionado.<p>
	 * 
	 */
	public Map<String, Long> findQuantidadeAnteriorTitulosByPeriodo(
			Collection<Integer> bibliotecas, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> formasDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos )
			throws DAOException {
		
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT" +
				" COALESCE( titulo."+classificacao.getColunaClassePrincipal()+", 'Sem Classe'), " +	 
				" COUNT(distinct titulo.id_titulo_catalografico) as quantidade_titulos " );
		
		sql.append("FROM biblioteca.titulo_catalografico titulo ");  
		
		if(retornarExemplares){ 
			sql.append("INNER JOIN biblioteca.exemplar AS exemplar ON exemplar.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = exemplar.id_exemplar ");
		}
		
		if(retornarFasciculos){ 
			sql.append("INNER JOIN biblioteca.assinatura AS assinatura ON assinatura.id_titulo_catalografico = titulo.id_titulo_catalografico ");
			sql.append("INNER JOIN biblioteca.fasciculo AS fasciculo ON fasciculo.id_assinatura = assinatura.id_assinatura  ");
			sql.append("INNER JOIN biblioteca.material_informacional AS material ON material.id_material_informacional = fasciculo.id_fasciculo ");
		}
		
		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 * titulo.ativo = trueValue()
		 * 
		 */
		
		sql.append("WHERE 1 = 1 ");
		
		if ( bibliotecas != null && ! bibliotecas.isEmpty() ) {
			sql.append(" AND material.id_biblioteca IN ("+ StringUtils.join(bibliotecas, ',') +") ");
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
		
		if ( formasDocumento != null && ! formasDocumento.isEmpty() ) {
			sql.append(" AND forma.id_forma_documento IN ("+ StringUtils.join(formasDocumento, ',') +") ");
		}
		
		sql.append("GROUP BY "+classificacao.getColunaClassePrincipal());
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" HAVING titulo.data_criacao < :dataInicio "); // Se n�o selecionar uma biblioteca espec�fica uma a Data de cria��o do T�tulo
		else	
			sql.append(" HAVING MIN(material.data_criacao) < :dataInicio ");  // � considerado a crescimento de T�tulo quando o primeiro material � inclu�do na biblioteca.
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		Map<String, Long> retorno = new TreeMap<String, Long>();
		
		for (Object[] objects : lista) {
			retorno.put((String)objects[0], ((BigInteger)objects[1]).longValue());
		}
		
		return retorno;
	}
	
}

