/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/11/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.sae;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.bolsas.dominio.Bolsista;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.TipoBolsa;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável pelas consultas ao gerar relatórios para o SAE
 * 
 * @author agostinho campos
 *
 */
public class RelatorioSAEDao extends GenericSigaaDAO {

	/**
	 * Localiza os seguintes tipos de bolsa do SIPAC
	 * <br />
	 * <ul>
	 *  <li>BOLSA_MONITORIA = 3;</li>
	 *	<li>BOLSA_EXTENSAO  = 4;</li>
	 *	<li>APOIO_TECNICO   = 5;</li>
	 *	<li>BOLSA_PESQUISA  = 25;</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<TipoBolsa> findTipoBolsaSIPAC() {
	    	int idTipoBolsaExtensao = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_EXTENSAO);
	    	int idTipoBolsaMonitoria = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.BOLSA_MONITORIA);
		
		String sql = "select id, denominacao from bolsas.tipo_bolsa where id in (?, ?, ?, ?)";
	
		return getJdbcTemplate(getDataSource(Sistema.SIPAC)).query(sql, new Object[] {idTipoBolsaMonitoria, idTipoBolsaExtensao, TipoBolsa.APOIO_TECNICO, TipoBolsa.BOLSA_PESQUISA}, new RowMapper() {
			public Object mapRow(ResultSet rs, int arg) throws SQLException {
				TipoBolsa tipoBolsa = new TipoBolsa();
					tipoBolsa.setDescricao(rs.getString("denominacao"));
					tipoBolsa.setId(rs.getInt("id") );
				return tipoBolsa;
			}
		});
	}

	/**
	 * Localiza os bolsistas do SIPAC pelo tipo de bolsa.
	 * 
	 * @param id
	 *  
	 */
	public List<br.ufrn.sigaa.bolsas.dominio.Bolsista> findBolsistasSIPACByTipoBolsa(int id) {
		
		String sql = " select bolsista.aluno_carente, bolsista.matricula, p.nome, tipo.id, tipo.denominacao from bolsas.bolsa bolsa " +
						  " inner join bolsas.tipo_bolsa tp on tp.id = bolsa.id_tipo_bolsa " +
						  "	inner join bolsas.bolsista bolsista on bolsista.id = bolsa.id_bolsista " +
						  "	inner join bolsas.tipo_bolsa tipo on tipo.id = bolsa.id_tipo_bolsa " +
						  " inner join comum.pessoa p on p.id_pessoa = bolsista.id_pessoa " +
						  " where tipo.id = ? and finalizada is false " +
						  " order by nome";
		
		 return getJdbcTemplate(getDataSource(Sistema.SIPAC)).query(sql, new Object[] {id}, new RowMapper() {
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						
						Pessoa p = new Pessoa();
							p.setNome(rs.getString("nome"));
						
						Discente d = new Discente();
							d.setMatricula(rs.getLong("matricula"));
							d.setPessoa(p);
						
							br.ufrn.sigaa.bolsas.dominio.Bolsista bolsista = new Bolsista();
								bolsista.setTipoBolsa(rs.getString("denominacao"));
								bolsista.setDiscente(d);
								bolsista.setCarente(rs.getBoolean("aluno_carente"));
						
						return bolsista;
				}
		});
	}
	
	/**
	 * Retorna o desempenho acadêmico dos discentes que possuem bolsa auxílio.
	 * 
	 * @param id
	 *  
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findDesempenhoDiscentesBolsaAuxilio(Integer anoAntes, Integer periodoAntes,
			Integer anoDepois, Integer periodoDepois, Curso curso, TipoBolsaAuxilio tipoBolsa, Long matricula, String nome, Character nivel){
		
		StringBuilder consulta = new StringBuilder(" SELECT DISTINCT c.nivel, c.nome as curso, d.ano_ingresso, d.periodo_ingresso, d.matricula, p.nome as discente, tba.denominacao as tipoBolsa, "
				+ " CAST( SUM (CASE WHEN cc.id_tipo_componente = " + TipoComponenteCurricular.DISCIPLINA + " AND id_situacao_matricula = " + SituacaoMatricula.APROVADO.getId() + " AND (mc.ano = " + anoAntes + " and mc.periodo = " + periodoAntes + ") THEN 1 ELSE 0 END) AS integer) AS aprovadoSem, "
				+ " CAST( SUM (CASE WHEN cc.id_tipo_componente = " + TipoComponenteCurricular.DISCIPLINA + " AND id_situacao_matricula = " + SituacaoMatricula.TRANCADO.getId() + " AND (mc.ano = " + anoAntes + " and mc.periodo = " + periodoAntes + ") THEN 1 ELSE 0 END) AS integer) AS trancadoSem, "
				+ " CAST( SUM (CASE WHEN cc.id_tipo_componente = " + TipoComponenteCurricular.DISCIPLINA + " AND id_situacao_matricula IN " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesReprovadas()) + " AND (mc.ano = " + anoAntes + " and mc.periodo = " + periodoAntes + ") THEN 1 ELSE 0 END) AS integer) AS reprovadoSem, "
				+ " CAST( SUM (CASE WHEN cc.id_tipo_componente = " + TipoComponenteCurricular.DISCIPLINA + " AND id_situacao_matricula = " + SituacaoMatricula.APROVADO.getId() + " AND (mc.ano = " + anoDepois + " and mc.periodo = " + periodoDepois + ") THEN 1 ELSE 0 END) AS integer) AS aprovadoCom, "
				+ " CAST( SUM (CASE WHEN cc.id_tipo_componente = " + TipoComponenteCurricular.DISCIPLINA + " AND id_situacao_matricula = " + SituacaoMatricula.TRANCADO.getId() + " AND (mc.ano = " + anoDepois + " and mc.periodo = " + periodoDepois + ") THEN 1 ELSE 0 END) AS integer) AS trancadoCom, "
				+ " CAST( SUM (CASE WHEN cc.id_tipo_componente = " + TipoComponenteCurricular.DISCIPLINA + " AND id_situacao_matricula IN " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesReprovadas()) + " AND (mc.ano = " + anoDepois + " and mc.periodo = " + periodoDepois + ") THEN 1 ELSE 0 END) AS integer) AS reprovadoCom ");
		
		consulta.append( " FROM discente d "
					+ " 	INNER JOIN curso c ON d.id_curso = c.id_curso INNER JOIN comum.pessoa p USING (id_pessoa) "
					+ "     JOIN ensino.matricula_componente mc ON (d.id_discente = mc.id_discente) "
					+ "     JOIN ensino.componente_curricular cc ON cc.id_disciplina = mc.id_componente_curricular "
					+ "		LEFT JOIN sae.bolsa_auxilio ba ON ba.id_discente = d.id_discente "
					+ "     LEFT JOIN sae.bolsa_auxilio_periodo bap using (id_bolsa_auxilio) "
					+ "     LEFT JOIN sae.tipo_bolsa_auxilio tba USING (id_tipo_bolsa_auxilio) "
					+ "     INNER JOIN comum.pessoa p2 on (d.id_pessoa = p2.id_pessoa)");
		
		consulta.append(" WHERE  d.status in " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos()) + " AND ba.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA
						+ " AND c.ativo = true AND cc.nivel = c.nivel AND d.nivel = c.nivel  AND c.nivel = c.nivel "
						+ "	AND ((mc.ano <= " + anoDepois + " AND mc.periodo = " + periodoDepois + ") OR (mc.ano = " + anoAntes + " and mc.periodo = " + periodoAntes + ")) "
						+ " AND bap.ano = " + anoDepois + " AND bap.periodo = " + periodoDepois);
		
		if (nivel != null)
			consulta.append(" AND c.nivel = '" +nivel+"'");
				
		if (curso != null)
			consulta.append(" AND c.id_curso = " + curso.getId());
						
		if (matricula != null)
			consulta.append(" AND d.matricula = "+ matricula);
						
		if (nome != null) {
			consulta.append(" AND " + UFRNUtils.convertUtf8UpperLatin9("p2.nome_ascii")
					+ " ilike '" + UFRNUtils.trataAspasSimples(StringUtils.toAscii(nome.toUpperCase()))
					+ "%'");
		}
		
		consulta.append(" GROUP BY c.nome, p.nome, c.nivel , d.matricula, tba.denominacao, d.id_discente, d.ano_ingresso, d.periodo_ingresso ORDER BY p.nome "); 

		return getJdbcTemplate().queryForList(consulta.toString());
		
	}

	/**
	 * Localiza os tipos de bolsas do SIPAC pelo id.
	 * 
	 * @param id
	 *  
	 */
	public TipoBolsa findTipoBolsaSIPACByID(int id) {
		
		return (TipoBolsa) getJdbcTemplate(getDataSource(Sistema.SIPAC)).queryForObject("select id, denominacao from bolsas.tipo_bolsa where id = ?", new Object[] {id},  new RowMapper() {
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						TipoBolsa tipoBolsa = new TipoBolsa();
						tipoBolsa.setDescricao(rs.getString("denominacao"));
					return  tipoBolsa;
				}
			});
	}
	
	/**
	 * Retorna todas as bolsas auxílios contempladas em um determinado ano e período 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<BolsaAuxilio> findContempladosByAnoPeriodo(Curso curso, Integer ano, Integer periodo) 
		throws DAOException{
		
		String projecao = "ba.id, ba.discente.matricula, ba.discente.pessoa.nome, ba.tipoBolsaAuxilio.denominacao"; 
		
		StringBuilder hql = new StringBuilder("SELECT DISTINCT " + projecao +
				" FROM BolsaAuxilioPeriodo bap INNER JOIN bap.bolsaAuxilio ba " +
				" INNER JOIN ba.tipoBolsaAuxilio tba INNER JOIN ba.discente d" +
				" WHERE ba.situacaoBolsa.id = :situacao");
		
		if( curso != null)
			hql.append(" AND d.curso.id = :curso ");
		if( ano != null)
			hql.append(" AND ano = :ano ");
		if( periodo != null)
			hql.append(" AND periodo = :periodo ");
		
		hql.append(" ORDER BY tba.denominacao, ba.discente.pessoa.nome ");
		
		Query q = getSession().createQuery( hql.toString() );
		q.setInteger("situacao", SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA);
		
		if( curso != null)
			q.setInteger("curso", curso.getId());
		if( ano != null)
			q.setInteger("ano", ano);
		if( periodo != null)
			q.setInteger("periodo", periodo);
		
		return HibernateUtils.parseTo( q.list(), projecao, BolsaAuxilio.class, "ba");
		
	}
	/**
	 * Retorna todos os discentes vinculados simultaneamente a um curso de Graduação e Pós.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public  List<Map<String,Object>> findDiscentesVinculadosGraduacaoPos() throws DAOException {
						
		String projecao = "p.nome, d.matricula, c.nome as nome_curso_gra , sub.matricula_pos, sub.nome_curso_pos, " +
				"CASE WHEN sub.nivel = '"+NivelEnsino.DOUTORADO+"' THEN 'DOUTORADO' WHEN sub.nivel = '"+NivelEnsino.MESTRADO+"' THEN 'MESTRADO' WHEN sub.nivel = '"+NivelEnsino.LATO+"' THEN 'LATO' END as nivel_pos ";
				
		
		StringBuilder sql = new StringBuilder("select "+ projecao +
				"from discente d " +
				"inner join comum.pessoa p on p.id_pessoa = d.id_pessoa "+
				"inner join curso c on c.id_curso = d.id_curso " +
				"inner join (select id_pessoa, matricula as matricula_pos, nome as nome_curso_pos, d_pos.nivel " + 
				            "from discente d_pos inner join curso c_pos on c_pos.id_curso = d_pos.id_curso " + 
				            "where d_pos.nivel in "+UFRNUtils.gerarStringIn( new char[] {NivelEnsino.LATO,NivelEnsino.MESTRADO,NivelEnsino.DOUTORADO})+
				            " and d_pos.status = "+StatusDiscente.ATIVO+" and d_pos.tipo = "+Discente.REGULAR+") as sub " +
				            "on (sub.id_pessoa = p.id_pessoa) " +
				            "where d.nivel = '"+NivelEnsino.GRADUACAO+"' and d.status = "+StatusDiscente.ATIVO+" and d.tipo = "+ Discente.REGULAR + 
				            "order by p.nome "
				);  
		
			return getJdbcTemplate().queryForList(sql.toString());
		
	}
	
    /**
     * Método usado para buscar os dados para o relatório.
     * @return
     * @throws SQLException
     */
	public Collection<String> relatorio() throws SQLException{
		
		Connection con = getDataSource(Sistema.SIGAA).getConnection();
		Statement stm = con.createStatement(); 
		
		String sql = "select distinct ba.id_bolsa_auxilio, d.matricula , p.nome, tipoBol.denominacao" +
				" from sae.bolsa_auxilio ba" +
				" inner join sae.tipo_bolsa_auxilio tipoBol on ba.id_tipo_bolsa_auxilio=tipoBol.id_tipo_bolsa_auxilio" +
				" inner join discente d on ba.id_discente=d.id_discente" +
				" inner join curso curso4_ on d.id_curso=curso4_.id_curso" +
				" left join sae.bolsa_auxilio_periodo bap on ( ba.id_bolsa_auxilio = bap.id_bolsa_auxilio ), comum.pessoa p" +
				" where d.id_pessoa=p.id_pessoa" +
				" and ( ba.id_situacao_bolsa = 2" +
				" and tipoBol.id_tipo_bolsa_auxilio in ( 3 )" +
				" and bap.ano = 2012 and bap.periodo = 1 )" +
				" order by tipoBol.denominacao, p.nome";
		
        ResultSet rs = stm.executeQuery(sql);  
        
        while ( rs.next() ) {
        	
        	System.err.println( rs.getInt("id_bolsa_auxilio") );
        	
		}
		
		return null;
	}
	
	/**
	 * Retorna uma coleção com todos os tipos de bolsa encontradas no SIPAC.
	 * 
	 * @return
	 */
	public List<Long> findAllBolsistasPagamentesSipacRU() {
		return getJdbcTemplate(Sistema.SIPAC).queryForList("select p.cpf_cnpj" +
				" from comum.pessoa p" +
				"  join restaurante.cartao_pagamento_pessoa cpp on (cpp.id_pessoa = p.id_pessoa and cpp.ativo = true and tipo_vinculo = 0)" +
				"  join restaurante.cartao_pagamento cp on (cp.id_cartao_pagamento = cpp.id_cartao_pagamento and cp.situacao = 0)" +
				" order by p.nome", Long.class);
	}
	
	/**
     * Método usado para buscar os dados para o relatório.
     * 
     * A = acima de 20 SM ou 12.440 ou mais
     * B = de 10 a 20 SM ou de 6.220 a 12.440 reais
     * C = 4 a 10 SM ou 2.488 a 6.220 reais
     * D = 2 a 4 SM ou 1.244 a 2.488 reais
     * E = até 2 SM ou até 1.244 reais
     * 
     * @return
     * @throws SQLException
     */
	public Map<String, Integer> relatorioPagantesRU() throws SQLException{
		Connection con = getDataSource(Sistema.SIGAA).getConnection();
		Statement stm = con.createStatement(); 

		double salario_minimo = ParametroHelper.getInstance().getParametroDouble(ConstantesParametro.SALARIO_MINIMO);
		
		String sql = "SELECT" +
			" SUM(CASE WHEN (ssed.renda_familiar / ssed.quantidade_membros_familia) > " + 20 * salario_minimo + " THEN 1 ELSE 0 END  ) AS A," +
			" SUM(CASE WHEN (ssed.renda_familiar / ssed.quantidade_membros_familia) > " + 10 * salario_minimo + " AND (ssed.renda_familiar / ssed.quantidade_membros_familia) <= " + 20 * salario_minimo + " THEN 1 ELSE 0 END) AS B," +
			" SUM(CASE WHEN (ssed.renda_familiar / ssed.quantidade_membros_familia) > " + 4 * salario_minimo + " AND (ssed.renda_familiar / ssed.quantidade_membros_familia) <= " + 10 * salario_minimo + "  THEN 1 ELSE 0 END) AS C," +
			" SUM(CASE WHEN (ssed.renda_familiar / ssed.quantidade_membros_familia) > " + 2 * salario_minimo + " AND (ssed.renda_familiar / ssed.quantidade_membros_familia) <= " + 4 * salario_minimo + "  THEN 1 ELSE 0 END) AS D," +
			" SUM(CASE WHEN (ssed.renda_familiar / ssed.quantidade_membros_familia) >= 0   AND (ssed.renda_familiar / ssed.quantidade_membros_familia) <= " + 2 * salario_minimo + "  THEN 1 ELSE 0 END) AS E" +
			" FROM sae.situacao_socio_economica_discente  ssed" +
			" JOIN discente d USING (id_discente)" +
			" JOIN comum.pessoa p USING (id_pessoa)" +
			" WHERE p.cpf_cnpj IN " + UFRNUtils.gerarStringIn(findAllBolsistasPagamentesSipacRU());
		
        ResultSet rs = stm.executeQuery(sql);
        ResultSetMetaData meta = rs.getMetaData();
        int coluna = 1;
        Map<String, Integer> result = new TreeMap();
        while ( rs.next() ) {
        	result.put( (meta.getColumnName(coluna)).toUpperCase(), rs.getInt(meta.getColumnName(coluna++)) );
        	result.put( (meta.getColumnName(coluna)).toUpperCase(), rs.getInt(meta.getColumnName(coluna++)) );
        	result.put( (meta.getColumnName(coluna)).toUpperCase(), rs.getInt(meta.getColumnName(coluna++)) );
        	result.put( (meta.getColumnName(coluna)).toUpperCase(), rs.getInt(meta.getColumnName(coluna++)) );
        	result.put( (meta.getColumnName(coluna)).toUpperCase(), rs.getInt(meta.getColumnName(coluna++)) );
		}
		
		return result;
	}	
	
	
}