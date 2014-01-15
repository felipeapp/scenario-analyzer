/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/07/2009
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.relatorios.LinhaDadosIndiceAcademico;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/** DAO responsável por consultas relativas ao acompanhamento de bolsas.
 * 
 * @author Édipo Elder F. Melo
 *
 */
public class AcompanhamentoBolsasDao extends GenericSigaaDAO {

	/**
	 * Lista alunos regulares, bolsistas, da UFRN, matriculados com o percentual 
	 * da carga horária prevista para o nível da estrutura curricular
	 * correspondente ao período atual do aluno.
	 *
	 * @param ano
	 * @param periodo
	 * @return formato: {matricula, denominacao_tipo_bolsa, nome, periodo_atual, ch_total, ch_matriculada, perc_ch_matriculado} 
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioBolsistasCargaHoraria(int ano, int periodo) throws DAOException {
		Date hoje = new Date((new java.util.Date()).getTime());
		geraTabelaBolsistasSIPAC(hoje, hoje);
		
		// consultas
		String sqlPeriodoAtual = "update bolsistas_sipac"
				+ " set periodo_atual = t.periodo_atual"
				+ " from (select d.id_discente, greatest(" 
				+ "        (" + ano + " - d.ano_ingresso) * 2  + (" + periodo	+ " - d.periodo_ingresso  + 1) - "
				+ "          (select count(*) "
				+ "           from ensino.movimentacao_aluno "
				+ "           where ativo = trueValue()"
				+ "           and id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.TRANCAMENTO
				+ "           and id_discente = d.id_discente "
				+ "           and (ano_referencia < " + ano
				+ "                or (ano_referencia = " + ano + " and periodo_referencia <= " + periodo + ")"
				+ "               )"
				+ "          ), 0) as periodo_atual "
				+ "       from graduacao.discente_graduacao dg, discente d"
				+ "       where d.id_discente = dg.id_discente_graduacao"
				+ "      ) t"
				+ " where bolsistas_sipac.id_discente = t.id_discente";
		
		String sqlRelatorio = "select matricula," +
				" bolsistas_sipac.denominacao_tipo_bolsa," +
				" pessoa.nome," +
				" bolsistas_sipac.periodo_atual," +
				" bolsistas_sipac.ch_total," +
				" bolsistas_sipac.ch_matriculada," +
				" bolsistas_sipac.perc_ch_matriculado" +
				" from discente" +
				" inner join bolsistas_sipac using (id_discente)" +
				" inner join comum.pessoa using (id_pessoa)" +
				" where discente.nivel = '" + NivelEnsino.GRADUACAO +"'"+
				" order by pessoa.nome_ascii";
		
		String sqlUpdateCH = "update bolsistas_sipac set "
				+ " ch_matriculada = t.ch_matriculada,"
				+ " ch_total = t.ch_total, "
				+ " perc_ch_matriculado = t.perc_ch_matriculado"
				+ " from ( "
				+ "     select discente.id_discente,"
				+ "     sum (case when curriculo_componente.semestre_oferta = bolsistas_sipac.periodo_atual then componente_curricular_detalhes.ch_total else 0 end) as ch_total,"
				+ "     sum (case when matricula_componente.ano = " + ano
				+ "          and matricula_componente.periodo = " + periodo 
				+ "          and id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido())
				+ "     then componente_curricular_detalhes.ch_total else 0 end) as ch_matriculada,"
				+ "     100 * sum (case when id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido())
				+ "     then componente_curricular_detalhes.ch_total else 0 end) / sum(componente_curricular_detalhes.ch_total) as perc_ch_matriculado"
				+ "     from discente"
				+ "     inner join bolsistas_sipac using (id_discente)"
				+ "     inner join graduacao.discente_graduacao on (discente.id_discente = id_discente_graduacao)"
				+ "     inner join graduacao.curriculo using (id_curriculo)"
				+ "     inner join graduacao.curriculo_componente using (id_curriculo)"
				+ "     inner join ensino.componente_curricular on (id_disciplina = id_componente_curricular)"
				+ "     inner join ensino.componente_curricular_detalhes on (id_detalhe = id_componente_detalhes)"
				+ "     left join ensino.matricula_componente using (id_discente, id_componente_curricular)"
				+ "     left join ensino.situacao_matricula using (id_situacao_matricula)"
				+ "     where discente.tipo = " + Discente.REGULAR
				+ "     and semestre_oferta = bolsistas_sipac.periodo_atual"
				+ "     group by discente.id_discente"
				+ " ) t"
				+ " where bolsistas_sipac.id_discente = t.id_discente";
		
		// atualizando os períodos atuais
		getJdbcTemplate().execute(sqlPeriodoAtual);
		// atualizando as cargas horárias matriculadas do semestre atual de cada discente
		getJdbcTemplate().execute(sqlUpdateCH);

		// consulta o resultado
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sqlRelatorio).list();
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("matricula", obj[0]);
			linha.put("denominacao_tipo_bolsa", obj[1]);
			linha.put("nome", obj[2]);
			linha.put("periodo_atual", obj[3]);
			linha.put("ch_total", obj[4]);
			linha.put("ch_matriculada", obj[5]);
			linha.put("perc_ch_matriculado", obj[6]);
			resultado.add(linha);
		}
		removeTabelaBolsistasSIPAC();
		return resultado;
	}
	
	public Integer findLastConsolidacaoRealizada() throws HibernateException, DAOException{
		String sql = "select ano_referencia from sae.dados_indice_academico where ativo order by ano_referencia desc limit 1";
		return (Integer) getSession().createSQLQuery(sql).uniqueResult();
	}
	
	/**
	 * Lista alunos bolsistas com que tenham desempenho acadêmico satisfatório.
	 * 
	 * @throws DAOException
	 */
	@SuppressWarnings("static-access")
	public List<LinhaDadosIndiceAcademico> bolsistasDesempenhoAcademico(int anoInicioBolsa, int periodoInicioBolsa, 
			int anoBolsa, int periodoBolsa, int idTipoBolsa, int anoConsolidacao, int situacaoBolsa ) throws DAOException {
		String sql = "SELECT distinct d.matricula, p.nome, tba.denominacao, ia.id, iad.valor, dia.iech, dia.iepl, ia.sigla" +
				" FROM sae.bolsa_auxilio ba" +
				" JOIN sae.bolsa_auxilio_periodo bap on (ba.id_bolsa_auxilio = bap.id_bolsa_auxilio)" +
				" JOIN ensino.indice_academico_discente iad using ( id_discente )" +
				" JOIN graduacao.discente_graduacao dg on ( iad.id_discente = dg.id_discente_graduacao )" +
				" JOIN discente d on ( d.id_discente = dg.id_discente_graduacao )" +
				" JOIN comum.pessoa p using ( id_pessoa )" +
				" JOIN ensino.indice_academico ia on ( iad.id_indice_academico = ia.id )" +
				" JOIN graduacao.matriz_curricular mc on ( dg.id_matriz_curricular = mc.id_matriz_curricular )" +
				" JOIN sae.dados_indice_academico dia on ( dg.id_matriz_curricular = dia.id_matriz and dia.ano_referencia = " + anoConsolidacao + " )" +
				" JOIN sae.tipo_bolsa_auxilio tba on ( ba.id_tipo_bolsa_auxilio = tba.id_tipo_bolsa_auxilio ) " +
				" WHERE iad.id_indice_academico in " + UFRNUtils.gerarStringIn(new int[] {IndiceAcademico.IECH, IndiceAcademico.IEPL}) + 
				" and d.status = " + StatusDiscente.ATIVO; 
		
				if ( situacaoBolsa > 0 )
					sql += " and ba.id_situacao_bolsa = " + situacaoBolsa; 
				
				if ( idTipoBolsa > 0 )
					sql += " and ba.id_tipo_bolsa_auxilio = " + idTipoBolsa;
				
				if ( anoInicioBolsa > 0 && periodoInicioBolsa > 0 )
					sql += " and bap.ano = " + anoInicioBolsa + " and bap.periodo = " + periodoInicioBolsa;

				if ( anoBolsa > 0 && periodoBolsa > 0 ) 
					sql += " and bap.ano <= " + anoBolsa + " and bap.periodo <= " + periodoBolsa;
				
				sql += " ORDER BY p.nome, tba.denominacao";

		List<Object[]> lista = getSession().createSQLQuery(sql).list();
		List<LinhaDadosIndiceAcademico> resultado = new ArrayList<LinhaDadosIndiceAcademico>();
		int count;
		int matriculaDiscente = 0;
		String bolsaDiscente = "";
		LinhaDadosIndiceAcademico linha = null;
		for (Object[] objects : lista) {
			count = 0;
			if ( ((BigInteger) objects[count]).intValue() != matriculaDiscente || !(((String) objects[2]).equals(bolsaDiscente)) ) {
				linha = new LinhaDadosIndiceAcademico();
				linha.setDiscente(new Discente());
				linha.getDiscente().setMatricula( ((BigInteger) objects[count++]).longValue() );
				linha.getDiscente().setPessoa(new Pessoa());
				linha.getDiscente().getPessoa().setNome( (String) objects[count++] );
				linha.setTipoBolsa( (String) objects[count++] );
				if ( linha.isIEPL((Integer) objects[count++]) )
					linha.setIeplDiscente( ((BigDecimal) objects[count++]).doubleValue() );
				else
					linha.setIechDiscente( ((BigDecimal) objects[count++]).doubleValue() );
				linha.setIechCurso( (Double) objects[count++] );
				linha.setIeplCurso( (Double) objects[count++] );
			} else {
				count = 3;
				if ( linha.isIEPL((Integer) objects[count++]) )
					linha.setIeplDiscente( ((BigDecimal) objects[count]).doubleValue() );
				else
					linha.setIechDiscente( ((BigDecimal) objects[count]).doubleValue() );
				
				resultado.add(linha);
			}
			
			matriculaDiscente = ((BigInteger) objects[0]).intValue();  
			bolsaDiscente = ((String) objects[2]);
			System.out.println( bolsaDiscente );
		}
		
		return resultado;
	}

	/**
	 * Lista alunos com vínculo empregatício ou beneficiário de outra bolsa,
	 * exceto alimentação e transporte, ou qualquer tipo de ajuda financeira
	 * proveniente de órgãos públicos ou privados;
	 * @throws DAOException 
	 */
	public List<Map<String, Object>>  relatorioBolsistaDuploOuVinculo() throws DAOException {
		Date hoje = new Date((new java.util.Date()).getTime());
		geraTabelaBolsistasSIPAC(hoje, hoje);
		
		String sql = "select"
				+ " matricula,"
				+ " nome,"
				+ " array_to_string(array(select denominacao_tipo_bolsa from bolsistas_sipac where id_discente = discente.id_discente)) as denominacao_bolsas"
				+ " from discente"
				+ " inner join comum.pessoa using (id_pessoa)"
				+ " inner join graduacao.discente_graduacao on (id_discente_graduacao = id_discente)"
				+ " where id_discente in ("
				+ "   select id_discente"
				+ "   from bolsistas_sipac "
				+ "   group by id_discente"
				+ "   having count(*) > 1)"
				+ " order by pessoa.nome_ascii";
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sql).list();
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("matricula", obj[0]);
			linha.put("nome", obj[1]);
			linha.put("denominacao_bolsas", obj[2]);
			resultado.add(linha);
		}
		removeTabelaBolsistasSIPAC();
		return resultado;
	}
	
	/**
	 * Método que remove a tabela de bolsistas do SIPAC
	 * 
	 * @throws DAOException 
	 * @throws DataAccessException
	 */
	private void removeTabelaBolsistasSIPAC() throws DAOException, DataAccessException {
		String sqlDropTempTable = "drop table bolsistas_sipac";
		getJdbcTemplate().execute(sqlDropTempTable);
	}

	/**
	 * Busca a lista de bolsistas no SIPAC e cria uma tabela temporária para
	 * consultas no SIGAA. A tabela é mantida até o fim da sessão.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 * @throws ArqException
	 */
	@SuppressWarnings("cast")
	public void geraTabelaBolsistasSIPAC (Date dataInicial, Date dataFinal) throws DAOException{
		Integer idDiscente = 0;
		
		try {
			List<HashMap<String, Object>> bolsasSIPAC =  IntegracaoBolsas.findBolsistasAtivos(dataInicial, dataFinal,null,null);
			
			// consultas
			String sqlCreateTempTable = "create table bolsistas_sipac (" +
					"id_discente integer," +
					"id_tipo_bolsa integer," +
					"periodo_atual integer," +
					"denominacao_tipo_bolsa varchar," +
					"ch_total integer," +
					"ch_matriculada integer," +
					"perc_ch_matriculado numeric(5,2))";
			String sqlInsert = "insert into bolsistas_sipac (id_discente, id_tipo_bolsa, denominacao_tipo_bolsa) values (?, ?, ?)";
			String sqlCreateIndex = "create index bolsistas_sipac_idx on bolsistas_sipac(id_discente)";
			
			// criando a tabela
			getJdbcTemplate().execute(sqlCreateTempTable);
			
			// inserindo os valores da consulta
			for (HashMap<String, Object> bolsa : bolsasSIPAC ) {
				idDiscente = (Integer) bolsa.get("id_discente");
				Object args[] = new Object[3];
				args[0] = idDiscente;
				args[1] = (Integer) bolsa.get("id_tipo_bolsa");
				args[2] = (String) bolsa.get("denominacao");
				getJdbcTemplate().update(sqlInsert, args);
			}
			
			// criando um índice para acelerar as consultas
			getJdbcTemplate().execute(sqlCreateIndex);
			
		} catch (Exception e) {
			System.out.println("id = " + idDiscente);
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
}
