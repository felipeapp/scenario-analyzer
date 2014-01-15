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
import java.util.Collections;
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
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAreaCNPqMes;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioCrescimentoPorAreaCNPq;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.FiltroClassificacoesRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AreaConhecimentoCNPqBibliotecaDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * <p>DAO exclusivo para o relat�rio crescimento por �rea CNPq. </p>
 *
 * <p>Realiza a contagem de novos T�tulos e Materiais no Acervo</p>
 *
 * @author felipe
 *
 */
public class RelatorioCrescimentoPorCNPqDAO extends GenericSigaaDAO{

	
	
	
	/**
	 * <p>Retorna o crescimento de materiais no per�odo.<p>
	 * 
	 */
	public List<DadosRelatorioCrescimentoPorAreaCNPq> findCrescimentoMaterialByPeriodo(
			Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo, 
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial,
			Collection<Integer> idsFormaDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos,
			AgrupamentoRelatoriosBiblioteca agrupamento)
			throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT" +
				" EXTRACT(year from material.data_criacao)  as anoMaterial, " +
				" EXTRACT(month from material.data_criacao)  as mesMaterial, " + /* extract � uma fun��o SQL ANSI*/
				agrupamento.campoAgrupamento+" as agrupamento, " +
				" COALESCE(  COALESCE( info.sigla, a.sigla ), 'Sem �rea') AS descricaoAreaTitulo, " +	 
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
		
		
		sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq());
		sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}		

		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 *  sql.append("AND  titulo.ativo = trueValue()AND situacao.situacao_de_baixa = falseValue() ");
		 * 
		 */
		
		sql.append(" WHERE material.ativo = trueValue() ");
		sql.append("AND material.data_criacao >= :dataInicio AND material.data_criacao <= :dataFim ");
		
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

		sql.append(" GROUP BY anoMaterial, mesMaterial, "+agrupamento.campoAgrupamento+", descricaoAreaTitulo" );
		sql.append(" ORDER BY anoMaterial, mesMaterial, "+agrupamento.campoAgrupamento+", descricaoAreaTitulo" );
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		List<DadosRelatorioCrescimentoPorAreaCNPq> retorno = new ArrayList<DadosRelatorioCrescimentoPorAreaCNPq>();
		
		for (Object[] objects : lista) {
			DadosRelatorioCrescimentoPorAreaCNPq dados = new DadosRelatorioCrescimentoPorAreaCNPq((Double)objects[0]);
			if( retorno.contains( dados)){
				dados = retorno.get(retorno.indexOf(dados));
				dados.adicionaDadosInternosPorMes( (Double)objects[1], (String)objects[2], (String)objects[3], ((BigInteger)objects[4]).longValue());
			}else{
				dados.adicionaDadosInternosPorMes( (Double)objects[1], (String)objects[2],  (String)objects[3], ((BigInteger)objects[4]).longValue());
				retorno.add(dados);
			}
			
		}

		AreaConhecimentoCNPqBibliotecaDao dao = null;
		
		try{
			dao = new AreaConhecimentoCNPqBibliotecaDao();
			Collection<AreaConhecimentoCnpq> areas = dao.findGrandeAreasConhecimentoCnpq();
			
			areas.add(new AreaConhecimentoCnpq(99999999, "Sem �rea", null));
			
			for (DadosRelatorioCrescimentoPorAreaCNPq d : retorno) {			
				for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes d2 : d.getDadosInternos()) {
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento d3 : d2.getDadosInternos()) {
						d3.adicionaAreasSemCrescimento(areas);  // Para o relat�rio ter todas as �reas e a quantidade de colunas ficar igual
						Collections.sort(d3.getDadosInternos()); // IMPORANTE:  ordena pela sigla da �rea
					}
				}
			}
		}finally{
			if(dao != null) dao.close();
		}
		
		return retorno;
	}
	
	
	/**
	 * <p>Retorna o crescimento de materiais no per�odo.<p>
	 * 
	 */
	public List<Object[]> findCrescimentoMaterialByPeriodoBasico(
			Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo, 
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial,
			Collection<Integer> idsFormaDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos)
			throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT" +
				" COALESCE(  COALESCE( info.sigla, a.sigla ), 'Sem �rea') AS descricaoAreaTitulo, " +
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
		
		
		sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq());
		sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}		

		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 *  sql.append("AND  titulo.ativo = trueValue()AND situacao.situacao_de_baixa = falseValue() ");
		 * 
		 */
		
		sql.append(" WHERE material.ativo = trueValue() ");
		sql.append("AND material.data_criacao >= :dataInicio AND material.data_criacao <= :dataFim ");
		
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

		sql.append(" GROUP BY descricaoAreaTitulo, "
				+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", "
				+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo);
		sql.append(" ORDER BY descricaoAreaTitulo, "
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
	public List<DadosRelatorioCrescimentoPorAreaCNPq> findCrescimentoTitulosByPeriodo(
			Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo, 
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> idsFormaDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos)throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT consultaInterna.anoTitulo, consultaInterna.mesTitulo, consultaInterna.descricaoAreaTitulo, COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos " 
			+" FROM ( ");
		
			sql.append("SELECT" );
					
			if(! retornarExemplares && ! retornarFasciculos)
				sql.append(" EXTRACT(year from titulo.data_criacao)  as anoTitulo, ");
			else
				sql.append(" EXTRACT(year from  MIN(material.data_criacao) )  as anoTitulo, ");
			
			if(! retornarExemplares && ! retornarFasciculos)
				sql.append(" EXTRACT(month FROM titulo.data_criacao)  as mesTitulo, " ); /* extract � uma fun��o SQL ANSI*/
			else
				sql.append(" EXTRACT(month from  MIN(material.data_criacao) )  as mesTitulo, ");
					
			sql.append(" COALESCE(  COALESCE( info.sigla, a.sigla ), 'Sem �rea') AS descricaoAreaTitulo, " +	
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
			
			if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
				sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
				" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
			}
			
			sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
			
		
			sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq());
			sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
			
			/* Observa��o:
			 * 
			 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
			 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
			 * 
			 *  sql.append("AND  titulo.ativo = trueValue()AND situacao.situacao_de_baixa = falseValue() ");
			 * 
			 */
			
			sql.append("WHERE material.ativo = trueValue() ");
			
			
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
	
			sql.append("GROUP BY titulo.id_titulo_catalografico, descricaoAreaTitulo ");
			
			if(! retornarExemplares && ! retornarFasciculos)
				sql.append(" HAVING titulo.data_criacao >= :dataInicio AND titulo.data_criacao <= :dataFim "); // Se n�o selecionar uma biblioteca espec�fica uma a Data de cria��o do T�tulo
			else	
				sql.append(" HAVING MIN(material.data_criacao) >= :dataInicio AND  MIN(material.data_criacao) <= :dataFim ");  // � considerado a crescimento de T�tulo quando o primeiro material � inclu�do na biblioteca.
			 
			
			sql.append("ORDER BY anoTitulo, mesTitulo, descricaoAreaTitulo");
		
		
		sql.append(" ) as consultaInterna "+
			" GROUP BY anoTitulo, mesTitulo, descricaoAreaTitulo "+
			" ORDER BY anoTitulo, mesTitulo, descricaoAreaTitulo" );
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
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
		
			areas.add(new AreaConhecimentoCnpq(99999999, "Sem �rea", null));
			
			for (DadosRelatorioCrescimentoPorAreaCNPq d : retorno) {			
				for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes d2 : d.getDadosInternos()) {
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento d3 : d2.getDadosInternos()) {
						d3.adicionaAreasSemCrescimento(areas); // Para o relat�rio ter todas as �reas e a quantidade de colunas ficar igual
					}
				}
			}
		}finally{
			if(dao != null) dao.close();
		}
		
		return retorno;
	}
	
	
	
	
	/**
	 * <p>Retorna o crescimento de T�tulos no per�odo.<p>
	 * 
	 */
	public List<Object[]> findCrescimentoTitulosByPeriodoBasico(
			Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo, 
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial,
			Collection<Integer> idsFormaDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos)throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT consultaInterna.descricaoAreaTitulo, COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos " 
			+" FROM ( ");
		
			sql.append("SELECT" );
							
			sql.append( " COALESCE(  COALESCE( info.sigla, a.sigla ), 'Sem �rea') AS descricaoAreaTitulo, " +	
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
			
			if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
				sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
				" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
			}
			
			sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
			
		
			sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq());
			sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
			
			/* Observa��o:
			 * 
			 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
			 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
			 * 
			 *  sql.append("AND  titulo.ativo = trueValue()AND situacao.situacao_de_baixa = falseValue() ");
			 * 
			 */
			
			sql.append("WHERE material.ativo = trueValue() ");
			
			
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
	
			sql.append("GROUP BY titulo.id_titulo_catalografico, descricaoAreaTitulo ");
			
			if(! retornarExemplares && ! retornarFasciculos)
				sql.append(" HAVING titulo.data_criacao >= :dataInicio AND titulo.data_criacao <= :dataFim "); // Se n�o selecionar uma biblioteca espec�fica uma a Data de cria��o do T�tulo
			else	
				sql.append(" HAVING MIN(material.data_criacao) >= :dataInicio AND  MIN(material.data_criacao) <= :dataFim ");  // � considerado a crescimento de T�tulo quando o primeiro material � inclu�do na biblioteca.
			 
			
			sql.append("ORDER BY descricaoAreaTitulo ");
		
		
		sql.append(" ) as consultaInterna "+
			" GROUP BY consultaInterna.descricaoAreaTitulo "+
			" ORDER BY consultaInterna.descricaoAreaTitulo " );
		
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
	public List<DadosRelatorioCrescimentoPorAreaCNPq> findCrescimentoTitulosAgrupadosByPeriodo(
			Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> idsFormaDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos,
			AgrupamentoRelatoriosBiblioteca agrupamento  )throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT consultaInterna.anoTitulo, consultaInterna.mesTitulo, consultaInterna.agrupamento, consultaInterna.descricaoAreaTitulo, COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos " 
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
				" COALESCE(  COALESCE( info.sigla, a.sigla ), 'Sem �rea') AS descricaoAreaTitulo, " +	 
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
		
		if (agrupamento != AgrupamentoRelatoriosBiblioteca.SITUACAO_MATERIAL) { // o join com tipo de material j� � feito.
			sql.append(agrupamento.join);  
		}

		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq());
		sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		sql.append("WHERE material.ativo = trueValue() "); // material.ativo =  true significa que um dia ele existiu de verdade no acervo.
		
		
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
		
		sql.append("GROUP BY titulo.id_titulo_catalografico, "+agrupamento.campoAgrupamento+", descricaoAreaTitulo");
		
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
				" GROUP BY anoTitulo, mesTitulo, consultaInterna.agrupamento, descricaoAreaTitulo "+
				" ORDER BY anoTitulo, mesTitulo, consultaInterna.agrupamento, descricaoAreaTitulo " );		
		
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("dataFim", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		List<DadosRelatorioCrescimentoPorAreaCNPq> retorno = new ArrayList<DadosRelatorioCrescimentoPorAreaCNPq>();
		
		for (Object[] objects : lista) {
			DadosRelatorioCrescimentoPorAreaCNPq dados = new DadosRelatorioCrescimentoPorAreaCNPq((Double)objects[0]);
			if( retorno.contains( dados)){
				dados = retorno.get(retorno.indexOf(dados));
				dados.adicionaDadosInternosPorMes( (Double)objects[1], (String)objects[2], (String)objects[3], ((BigInteger)objects[4]).longValue());
			}else{
				dados.adicionaDadosInternosPorMes( (Double)objects[1], (String)objects[2], (String)objects[3], ((BigInteger)objects[4]).longValue());
				retorno.add(dados);
			}
			
		}

		AreaConhecimentoCNPqBibliotecaDao dao = null;
		
		try{
			dao = new AreaConhecimentoCNPqBibliotecaDao();
			Collection<AreaConhecimentoCnpq> areas = dao.findGrandeAreasConhecimentoCnpq();
		
			areas.add(new AreaConhecimentoCnpq(99999999, "Sem �rea", null));
			
			for (DadosRelatorioCrescimentoPorAreaCNPq d : retorno) {			
				for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes d2 : d.getDadosInternos()) {
					for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento d3 : d2.getDadosInternos()) {
						d3.adicionaAreasSemCrescimento(areas);  // Para o relat�rio ter todas as �reas e a quantidade de colunas ficar igual
					    Collections.sort(d3.getDadosInternos()); // IMPORANTE:  ordena pela sigla da �rea
					}
				}
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return retorno;
	}
	
	
	
	/**
	 * <p>Retorna o crescimento de T�tulos no per�odo com agrupamento.<p>
	 * 
	 */
	public List<Object[]> findCrescimentoTitulosAgrupadosByPeriodoBasico(
			Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial, Collection<Integer> idsFormaDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos )throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT  consultaInterna.descricaoAreaTitulo, " +
				" consultaInterna."+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", " + // Nesse caso vai ser fixo, o usu�rio n�o escolhe
				" consultaInterna."+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo+", " + // Nesse caso vai ser fixo, o usu�rio n�o escolhe		
				" COUNT(consultaInterna.quantidade_titulos) as quantidade_titulos "+ 
				" FROM ( ");
		
		sql.append("SELECT");
		
		
		sql.append(
				" COALESCE(  COALESCE( info.sigla, a.sigla ), 'Sem �rea') AS descricaoAreaTitulo, " +
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
		
		
		sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq());
		sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		sql.append("WHERE material.ativo = trueValue() "); // material.ativo =  true significa que um dia ele existiu de verdade no acervo.
		
		
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
		
		sql.append("GROUP BY titulo.id_titulo_catalografico, descricaoAreaTitulo "+", "
				+AgrupamentoRelatoriosBiblioteca.COLECAO.campoAgrupamento+", " // Nesse caso vai ser fixo, o usu�rio n�o escolhe
				+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.campoAgrupamento+" " // Nesse caso vai ser fixo, o usu�rio n�o escolhe
				);
		
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
		
		
		sql.append(" ) as consultaInterna "
				+" GROUP BY descricaoAreaTitulo, "
				+AgrupamentoRelatoriosBiblioteca.COLECAO.nomeCampo+", "
				+AgrupamentoRelatoriosBiblioteca.TIPO_MATERIAL.nomeCampo+" "
				+" ORDER BY descricaoAreaTitulo, "
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
			Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial,
			Collection<Integer> idsFormaDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos ,
			AgrupamentoRelatoriosBiblioteca agrupamento )
			throws DAOException {
		
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT" +
				" COALESCE(  COALESCE( info.sigla, a.sigla ), 'Sem �rea') AS descricaoAreaTitulo, " +	 
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
		
		
		sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq());
		sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}
		
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 *  sql.append("AND  titulo.ativo = trueValue()AND situacao.situacao_de_baixa = falseValue() ");
		 * 
		 */
		
		sql.append(" WHERE material.ativo = trueValue() ");
		sql.append("AND material.data_criacao < :dataInicio ");
		
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

		sql.append("GROUP BY descricaoAreaTitulo ");
		sql.append("ORDER BY descricaoAreaTitulo");
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();

		Map<String, Long> retorno = new TreeMap<String, Long>();

		for (Object[] objects : lista) {
			retorno.put(String.valueOf(objects[0]), ( (BigInteger) objects[1]).longValue());
		}
		
		return retorno;
	
	}
	
	/**
	 * <p>Retorna a quantidade anterior de T�tulo existentes ao per�odo selecionado.<p>
	 * 
	 */
	public Map<String, Long> findQuantidadeAnteriorTitulosByPeriodo(
			Collection<Integer> idsBiblioteca, Date inicioPeriodo, Date fimPeriodo,
			Collection<Integer> idsColecao, Collection<Integer> idsTipoMaterial,
			Collection<Integer> idsSituacaoMaterial,
			Collection<Integer> idsFormaDocumento,
			FiltroClassificacoesRelatoriosBiblioteca classificacao,
			boolean retornarExemplares, boolean retornarFasciculos )
			throws DAOException {
		
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT" +
				" COALESCE(  COALESCE( info.sigla, a.sigla ), 'Sem �rea') AS descricaoAreaTitulo, " +		 
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
		
		if ( idsFormaDocumento != null && ! idsFormaDocumento.isEmpty() ) {
			sql.append(" INNER JOIN biblioteca.material_informacional_formato_documento mf ON mf.id_material_informacional = material.id_material_informacional "+
			" INNER JOIN biblioteca.forma_documento forma ON mf.id_forma_documento = forma.id_forma_documento ");
		}

		sql.append("INNER JOIN biblioteca.situacao_material_informacional AS situacao ON situacao.id_situacao_material_informacional = material.id_situacao_material_informacional ");
		
		sql.append(" LEFT JOIN comum.area_conhecimento_cnpq AS a ON a.id_area_conhecimento_cnpq = titulo."+classificacao.getColunaAreaConhecimentoCNPq());
		sql.append(" LEFT JOIN biblioteca.informacoes_area_cnpq_biblioteca AS info ON info.id_area_conhecimento_cnpq = a.id_area_conhecimento_cnpq ");
		
		/* Observa��o:
		 * 
		 * Relat�rios de crescimento n�o devem verificar somente os ativos, os que j� existiram algum dia no acervo 
		 * tamb�m devem ser contados, sen�o pode gerar erros ao emitir o mesmo relat�rio tempos depois
		 * 
		 *  sql.append("AND  titulo.ativo = trueValue()AND situacao.situacao_de_baixa = falseValue() ");
		 * 
		 */
		
		sql.append("WHERE material.ativo = trueValue() ");
		
		
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

		sql.append("GROUP BY descricaoAreaTitulo ");
		
		if(! retornarExemplares && ! retornarFasciculos)
			sql.append(" HAVING titulo.data_criacao < :dataInicio "); // Se n�o selecionar uma biblioteca espec�fica uma a Data de cria��o do T�tulo
		else	
			sql.append(" HAVING MIN(material.data_criacao) < :dataInicio ");   // � considerado a crescimento de T�tulo quando o primeiro material � inclu�do na biblioteca.
		
		
		
		sql.append("ORDER BY descricaoAreaTitulo");
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("dataInicio", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		Map<String, Long> retorno = new TreeMap<String, Long>();
		
		for (Object[] objects : lista) {
			retorno.put(String.valueOf(objects[0]), ( (BigInteger) objects[1]).longValue());
		}
		
		return retorno;
	}
	
}

