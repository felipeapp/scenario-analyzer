/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 17/05/2007
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.TipoFiltroCurso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.GrupoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MotivoTrancamento;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.DiscentesBolsas;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.LinhaJustificativaRelatorioQuantitativoTrancamentos;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.LinhaRelatorioQuantitativoTrancamentos;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO que contém as consultas para os relatórios sobre discentes de graduação.
 * 
 * @author Eric Moura
 */
public class RelatorioDiscenteSqlDao extends AbstractRelatorioSqlDao {

	/**
	 * Retorna um relatório de discentes ingressantes.
	 * 
	 * @param ano
	 * @param periodo
	 * @param status
	 * @param forma_ingresso
	 * @param matrizCurricular
	 * @param filtros
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaDiscenteIngressante(int ano,
			int periodo, int status, int forma_ingresso,
			MatrizCurricular matrizCurricular, HashMap<Integer, Boolean> filtros)
			throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select distinct u.sigla as centro, c.id_curso, c.nome as curso_nome, "
						+ " t.id_turno, t.descricao as turno_descricao, t.sigla, "
						+ "  d.matricula,d.ano_ingresso, d.periodo_ingresso, p.nome as aluno_nome, ga.descricao as modalidade_nome, "
						+ "  h.nome as habilitacao_nome, fi.id_forma_ingresso,fi.descricao as forma_ingresso_descricao, "
						+ "   sd.descricao as discente_status, m.nome as municipio_nome, mc.id_matriz_curricular, mpo.nome as municipio_polo "
						+ " from comum.pessoa p, discente d, comum.unidade u, curso c, "
						+ "  ensino.turno t, ensino.forma_ingresso fi, graduacao.matriz_curricular mc left outer join graduacao.habilitacao h using (id_habilitacao), "
						+ "  status_discente sd, graduacao.discente_graduacao dg left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "	 left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ "  , ensino.grau_academico ga, comum.municipio m"
						+ " where p.id_pessoa = d.id_pessoa "
						+ "  and d.id_discente = dg.id_discente_graduacao "
						+ "  and dg.id_matriz_curricular = mc.id_matriz_curricular "
						+ "  and d.id_curso = c.id_curso "
						+ "  and d.id_forma_ingresso = fi.id_forma_ingresso "
						+ "  and c.id_unidade = u.id_unidade "
						+ "  and c.id_curso = mc.id_curso "
						+ "  and c.id_municipio = m.id_municipio "
						+ "  and mc.id_turno = t.id_turno "
						+ "  and sd.status = d.status "
						+ "  and mc.id_grau_academico = ga.id_grau_academico "
						+ "  and c.nivel = 'G' " 
						+ "  and d.status not in ("+""+StatusDiscente.EXCLUIDO+") " );

		if (filtros.get(MATRIZ_CURRICULAR))
			sqlconsulta.append("		and mc.id_matriz_curricular = "
					+ matrizCurricular.getId());

		else if (filtros.get(CURSO))
			sqlconsulta.append("		and c.id_curso = "
					+ matrizCurricular.getCurso().getId());

		else if (filtros.get(UNIDADE))
			sqlconsulta.append("		and u.id_unidade = "
					+ matrizCurricular.getCurso().getUnidade().getId());

		if (ano != 0)
			sqlconsulta.append("		and d.ano_ingresso = " + ano);
		if (periodo != 0)
			sqlconsulta.append("		and d.periodo_ingresso = " + periodo);
		if (status != 0)
			sqlconsulta.append("		and d.status = " + status);
		if (forma_ingresso != 0)
			sqlconsulta.append("		and d.id_forma_ingresso = " + forma_ingresso);
		sqlconsulta.append(" order by u.sigla, c.nome, modalidade_nome, habilitacao_nome, t.sigla, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
		
	/**
	 * Retorna os dados de contato dos alunos. 
	 * @param status
	 * @param matrizCurricular
	 * @param filtros
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaContatoDiscente(int status, 
			int unidade)
			throws DAOException {
		
		String sqlConsulta = 
			"	select distinct p.nome , e.logradouro, e.numero, m.nome as cidade, p.cpf_cnpj, d.matricula, " 
				+ "  uf.sigla as estado, p.telefone_fixo, p.telefone_celular, p.email, u.sigla, c.nivel, sd.descricao as status "
				+ "  from discente d "
				+"   inner join comum.pessoa p on p.id_pessoa = d.id_pessoa "
				+"   inner join status_discente sd on sd.status = d.status "
				+"   inner join curso c on d.id_curso = c.id_curso "
				+"   inner join comum.unidade u on c.id_unidade = u.id_unidade "
				+"   inner join comum.endereco e on e.id_endereco = p.id_endereco_contato "
				+"   inner join comum.municipio m on e.id_municipio = m.id_municipio "
				+"   inner join comum.unidade_federativa uf on uf.id_unidade_federativa = m.id_unidade_federativa "
				+ "  where 1 = 1 " 
				+ (unidade > 0 ? "  and u.id_unidade = "+ unidade : "")
				+ (status > 0 ? "  and d.status = "+ status : "")
				+ " order by c.nivel, u.sigla, p.nome ";
		

		List<Map<String, Object>> query = executeSql(sqlConsulta);  
		return query;		
	}
	
	/**
	 * Retorna os dados para o relatório de alunos reprovados e o tipo de bolsa (caso tenha), 
	 * em um ano, período e unidade especificados.
	 * @param ano
	 * @param periodo
	 * @param unidade
	 * @return
	 * @throws ArqException 
	 */
	public List<Map<String,Object>> findAlunosReprovados(int ano, int periodo, int unidade) throws ArqException {
		String sql ="select distinct d.matricula, p.nome, d.id_discente, c.nivel "
					+" from ensino.matricula_componente m, discente d, comum.pessoa p," 
					+"     ensino.situacao_matricula s, ensino.componente_curricular c"
					+" where m.id_discente = d.id_discente"
					+" and d.id_pessoa = p.id_pessoa"
					+" and m.id_situacao_matricula = s.id_situacao_matricula"
					+" and m.id_componente_curricular = c.id_disciplina"
					+" and c.nivel in ('S','E','D') "
					+" and m.id_situacao_matricula in ("+SituacaoMatricula.REPROVADO.getId()+","+ SituacaoMatricula.REPROVADO_FALTA.getId() +"," + SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId() + ")"
					+" and m.ano = "+ano
					+" and m.periodo = "+periodo
					+" and c.id_unidade = "+unidade
					+" order by c.nivel, p.nome";
		
		// consulta o resultado
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sql).list();
		
		Map<Integer, String> bolsasSIPAC = null;
		if( Sistema.isSipacAtivo() ){
			bolsasSIPAC = IntegracaoBolsas.findBolsistasAtivosDiscente(new java.sql.Date(new java.util.Date().getTime()), new java.sql.Date(new java.util.Date().getTime()));
		}
		
		
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("matricula", obj[0]);
			linha.put("nome", obj[1]);
			linha.put("nivel", obj[3]);
			
			if( Sistema.isSipacAtivo() && bolsasSIPAC != null ){
				linha.put("tipo_bolsa", bolsasSIPAC.get( obj[2] ) );
			}
			
			resultado.add(linha);
		}
		return resultado;				
	}		
	
	
	/**
	 * Retorna os dados para o relatório de alunos não matriculados no período e o tipo de bolsa (caso tenha), 
	 * em um ano, período e unidade especificados.
	 * @param ano
	 * @param periodo
	 * @param unidade
	 * @return
	 * @throws ArqException 
	 */
	public List<Map<String,Object>> findAlunosNaoMatriculadosBolsa(int ano, int periodo, int unidade) throws ArqException {
	
		String strInSituacaoMatricula = UFRNUtils.gerarStringIn( new int[]{ 
				SituacaoMatricula.MATRICULADO.getId(), 
				SituacaoMatricula.APROVADO.getId(),
				SituacaoMatricula.TRANCADO.getId(), SituacaoMatricula.REPROVADO.getId(),
				SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.APROVEITADO_DISPENSADO.getId(),
				SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId()  } );
		
		String sql =
				" select distinct d.matricula, p.nome, d.id_discente, d.tipo, d.nivel, po.nome as orientador, sms.status " +
				" from discente d" +
				" inner join stricto_sensu.discente_stricto ds on( d.id_discente = ds.id_discente ) " +
				" left join graduacao.orientacao_academica o on ( " +
				" 		o.id_discente = ds.id_discente " +
				"		and o.tipoorientacao = '" + OrientacaoAcademica.ORIENTADOR + "'" +
				"		and o.data_finalizacao is null and o.inicio <= now() " +
				"		and ( o.fim IS NULL or o.fim >= now() ) and o.cancelado = falseValue() )"	+
				" left join rh.servidor s on ( s.id_servidor = o.id_servidor ) " +
				" left join ensino.docente_externo de on ( de.id_docente_externo = o.id_docente_externo ) " +
				" left join comum.pessoa po on ( po.id_pessoa = de.id_pessoa or po.id_pessoa = s.id_pessoa ) " +
				" left join graduacao.solicitacao_matricula sms on ( " +
				"		sms.ano = " + ano + " and sms.periodo = " + periodo + 
				"		and ds.id_discente = sms.id_discente " +
				"		and sms.status = " + SolicitacaoMatricula.SUBMETIDA + ") " +
				" inner join comum.pessoa p on ( d.id_pessoa = p.id_pessoa ) " +
				" inner join status_discente sd on (sd.status = d.status) " +
				" where  " +
				" d.nivel in " + gerarStringIn( NivelEnsino.getNiveisStricto() ) +
				" and d.id_gestora_academica = " + unidade +
				" and d.status = " + StatusDiscente.ATIVO  +
				" and not exists (" +
					" select *" +  
					" from ensino.matricula_componente mc"+ 
					" where mc.ano = " + ano +
					" and mc.periodo = " + periodo +
					" and mc.id_discente = d.id_discente"+
					" and mc.id_situacao_matricula in " + strInSituacaoMatricula +
				" ) " +
				//Caso onde a renovação é realizada pelo programa e não existe solicitação de matricula e sim uma matricula. 
				" and not exists (" +
					" select * from stricto_sensu.renovacao_atividade_pos rap " +
					" inner join ensino.matricula_componente mc on (mc.id_matricula_componente = rap.id_matricula_componente)" +
					" where mc.id_discente = d.id_discente and rap.ano = " + ano + " and rap.periodo = " + periodo + 
					" and rap.ativo=true and rap.id_matricula_componente is not null" +
					" ) " +
				//Caso onde a renovação da atividade é realizada pelo discente através da matrícula online, onde só existe uma solicitação de matricula.
				" and not exists (" +
					" select * from graduacao.solicitacao_matricula sm " +
					" inner join ensino.matricula_componente mc on ( mc.id_matricula_componente = sm.id_matricula_gerada )" + 
					" where  sm.ano = " + ano + " and sm.periodo = " + periodo + " and sm.status = " + SolicitacaoMatricula.ATENDIDA +
					" and sm.id_discente = d.id_discente" +
					" ) " +
				"order by d.nivel, p.nome ";
		
		// consulta o resultado
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sql).list();
		
						
		Map<Integer, String> bolsasSIPAC =  new HashMap<Integer, String>();		
		if(Sistema.isSipacAtivo()){		
			bolsasSIPAC = IntegracaoBolsas.findBolsistasAtivosDiscente(new java.sql.Date(new java.util.Date().getTime()), new java.sql.Date(new java.util.Date().getTime()));
		}
					
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("matricula", obj[0]);
			linha.put("nome", obj[1]);
			linha.put("nivel", obj[4]);		
			linha.put("tipo", obj[3]);	
			linha.put("tipo_bolsa", bolsasSIPAC.get(obj[2]) );
			linha.put("orientador", obj[5] );
			if( obj[6] != null)
				linha.put("status", obj[6] );
			
			resultado.add(linha);
		}
		return resultado;				
	}			
	
	/**
	 * Retorna os dados para o relatório de alunos não matriculados após um mês do calendário de matrícula on-line, 
	 * em um ano, período e unidade especificados.
	 * @param ano
	 * @param periodo
	 * @param unidade
	 * @param fimMatricula
	 * @return
	 * @throws ArqException 
	 */
	public List<Map<String,Object>> findAlunosNaoMatriculadosOnLine(int ano, int periodo, int unidade) throws ArqException {		
		String sql =" select distinct d.matricula, p.nome, d.id_discente, c.nivel "
					+" from ensino.matricula_componente m, discente d, comum.pessoa p," 
					+"     ensino.situacao_matricula s, ensino.componente_curricular c, comum.calendario_academico ca "
					+"  where m.id_discente = d.id_discente"
					+"  and d.id_pessoa = p.id_pessoa"
					+"  and m.id_situacao_matricula = s.id_situacao_matricula"
					+"  and m.id_componente_curricular = c.id_disciplina"
					+"  and c.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto())
					+"  and m.ano = "+ano
					+"  and m.periodo = "+periodo
					+"  and c.id_unidade = "+unidade
					+"  and ca.ano = m.ano"
					+"  and ca.periodo = m.periodo"
					+"  and ca.id_unidade = c.id_unidade"		
					+"  and ca.fimrematricula + 30 < NOW()"					
					+"  and d.status in "+ UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())							
					+"  and not exists (select m.id_matricula_componente from ensino.matricula_componente m "
									+ "	where m.id_discente = d.id_discente "
									+ "	and m.id_situacao_matricula in "+ gerarStringIn(SituacaoMatricula.getSituacoesPositivas()) + ")"
					+ " and not exists (select ma.id_movimentacao_aluno from ensino.movimentacao_aluno ma "
									+ " inner join ensino.tipo_movimentacao_aluno tma using(id_tipo_movimentacao_aluno)"
									+ " where ma.id_discente = d.id_discente "
									+ " and tma.grupo = '"+GrupoMovimentacaoAluno.AFASTAMENTO_TEMPORARIO+"'"
									+ " and ma.ano_referencia = " + ano
									+ " and ma.periodo_referencia = " + periodo + ") "
					+" order by c.nivel, p.nome";		
		
		
		// consulta o resultado
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sql).list();
				
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("matricula", obj[0]);
			linha.put("nome", obj[1]);
			linha.put("id", obj[2]);
			linha.put("nivel", obj[3]);		
			
			resultado.add(linha);
		}
		return resultado;				
	}			
	
	
	/**
	 * Retorna os dados dos Alunos com bolsa em um determinado período (data inicio e data fim).
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 * @throws ArqException
	 */
	public List<DiscentesBolsas> findDiscentesBolsas(int unidade, Date dataInicial, Date dataFinal) throws ArqException {
		
		List<HashMap<String, Object>> bolsasSIPAC = IntegracaoBolsas.findBolsistasAtivos(new java.sql.Date(dataInicial.getTime()), new java.sql.Date(dataFinal.getTime()), null, null);
		
		List<DiscentesBolsas> resultado = new ArrayList<DiscentesBolsas>();
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" select distinct d.matricula, p.nome, d.id_discente, c.nivel, sd.descricao"
			+" from ensino.matricula_componente m, discente d, status_discente sd, comum.pessoa p," 
			+"     ensino.situacao_matricula s, ensino.componente_curricular c, curso cu"
			+"  where m.id_discente = d.id_discente"
			+"  and d.status = sd.status"
			+"  and d.status = "+StatusDiscente.ATIVO
			+"  and d.id_pessoa = p.id_pessoa"
			+"  and m.id_situacao_matricula = s.id_situacao_matricula"
			+"  and m.id_componente_curricular = c.id_disciplina"
			+"  and c.nivel in ('S','E','D')");
		if(unidade > 0)
			sql.append("  and c.id_unidade = "+unidade );	
		
		sql.append("  and c.id_unidade = cu.id_unidade"
			+"  and d.id_curso = cu.id_curso"
			+"  order by c.nivel, p.nome");		

		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createSQLQuery(sql.toString()).list();	
		
		for (Object obj[] : lista) {
			DiscentesBolsas db = new DiscentesBolsas();

			db.setIdDiscente(Integer.parseInt( ""+obj[2] ));
			db.setMatricula(String.valueOf(obj[0]));
			db.setNome(String.valueOf(obj[1]));			
			db.setStatusDiscente(String.valueOf(obj[4]));
			db.setNivel(NivelEnsino.getDescricao(String.valueOf(obj[3]).toCharArray()[0]));
			resultado.add(db);
		}				
		
		for (HashMap<String, Object> listaBolsa : bolsasSIPAC){
				
			DiscentesBolsas aux = new DiscentesBolsas();
			aux.setIdDiscente(Integer.parseInt( ""+listaBolsa.get("id_discente") ) );				
			
			if (resultado.indexOf(aux) >= 0){
				DiscentesBolsas db = resultado.get( resultado.indexOf(aux) );
				
				db.setTipoBolsa( String.valueOf( listaBolsa.get("denominacao") ) );
				db.setDataInicio( (Date) listaBolsa.get("inicio") );
				db.setDataFim( (Date) listaBolsa.get("fim")  );		
				db.setDataFinalizacao( (Date) listaBolsa.get("data_finalizacao")  );
				
			}
		}
		
		return resultado;
	}
		
	

	/** Retorna um relatório de totais de solicitações de matrículas.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTotaisSolicitacoesMatricula(
			int ano, int periodo) throws DAOException {
		String sql = "select distinct c.id_curso, c.nome as curso_nome, m.nome as municipio_nome, " +
				" u.nome as nome_unidade, u.sigla as sigla_unidade, " +
				" cast(count(d.id_discente) as NUMERIC(6,2)) as total_alunos, " +
				" cast((select count(distinct s.id_discente) " +
					" from graduacao.solicitacao_matricula s " +
					" left join discente d on s.id_discente=d.id_discente " +
					" where d.id_curso=c.id_curso " +
					" and s.ano=" + ano + " and s.periodo=" + periodo +
					" and s.anulado= falseValue()) as NUMERIC(6,2))  as total_matriculados " +
				" from discente d " +
				" join curso c on c.id_curso=d.id_curso " +
				" join comum.unidade u on (u.id_unidade = c.id_unidade)" +
				" join comum.municipio m on (c.id_municipio=m.id_municipio)" +
				" where d.nivel='G' " +
				" and d.status in (1,2,8) " +
				" and c.id_modalidade_educacao <> " + ModalidadeEducacao.A_DISTANCIA +
				" and c.id_convenio is null " +
				" group by c.nome, m.nome, c.id_curso, u.nome, u.sigla" +
				" order by u.nome, c.nome";
		StringBuilder sqlconsulta = new StringBuilder(sql);
		List<Map<String, Object>> result;
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}

	/**
	 * Retorna o Relatório de Percentual de Carga Horária Cumprida
	 * pelo Aluno.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException 
 	 */
	public List<Map<String, Object>> findListaPercentualCHAluno(Curso curso) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as unidade_sigla, c.id_curso, c.nome as curso_nome,  d.ano_ingresso, st.descricao as status_aluno,"
						+ "       d.periodo_ingresso, d.matricula, p.nome as aluno_nome, dg.ch_total_integralizada, ga.id_grau_academico, "
						+ "       ga.descricao as modalidade_aluno, h.id_habilitacao,h.nome as habilitacao_aluno, "
						+ "       crr.ch_total_minima, cast((cast(dg.ch_total_integralizada as NUMERIC(4)) /   "
						+ "       cast(crr.ch_total_minima as NUMERIC(4)))*100 as numeric(6,2)) as percentualcargacumprida,   "
						+ " 	mpo.nome as municipio_polo from discente d, comum.pessoa p, curso c, comum.unidade u, ensino.grau_academico ga,   "
						+ " 	 graduacao.curriculo crr  , status_discente st, graduacao.discente_graduacao dg  "
						+ " 	left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "		left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ "    ,graduacao.matriz_curricular mc left outer join graduacao.habilitacao h using (id_habilitacao)  "
						+ " where d.id_pessoa = p.id_pessoa    "
						+ "   and d.id_curso = c.id_curso  "
						+ "   and d.id_discente = dg.id_discente_graduacao   "
						+ "   and dg.id_matriz_curricular = mc.id_matriz_curricular            "
						+ "   and mc.id_grau_academico = ga.id_grau_academico  "
						+ "   and u.id_unidade = c.id_unidade    "
						+ "   and d.id_curriculo = crr.id_curriculo  "
						+ "   and st.status = d.status    "
						+ "   and d.status in (1,4,5,8) ");

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("		and c.id_curso = " + curso.getId());

		sqlconsulta
				.append(" order by u.sigla, c.nome, ga.descricao, h.nome, p.nome, mpo.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/** Retorna o relatório para Realização de Eleições de coordenadores, representantes, etc.
	 * 
	 * @param nivel
	 * @param curso
	 * @param matrizCurricular
	 * @param ano
	 * @param periodo
	 * @param filtros
	 * @return
	 * @throws DAOException 
 	 */
	public List<Map<String, Object>> findListaEleicao(char nivel,
			Curso curso, MatrizCurricular matrizCurricular, int ano,
			int periodo, HashMap<Integer, Boolean> filtros) throws DAOException {

		if (curso != null) {
			try {
				initialize(curso);
			} catch (DAOException e) {
			}
		}

		StringBuilder sqlconsulta = new StringBuilder(
				"  select u.sigla as centro, c.id_curso, c.nome||coalesce(' - '||mod.descricao,'') as curso_nome, c.nivel,  m.nome as municipio_nome,"
						+ "        d.ano_ingresso, st.descricao as status_aluno, d.periodo_ingresso, "
						+ "        d.matricula, p.nome as aluno_nome, d.ch_integralizada ");

		if (nivel == NivelEnsino.GRADUACAO) {
			sqlconsulta
					.append(", ga.descricao as modalidade_aluno, h.nome as habilitacao_aluno, mpo.nome as municipio_polo,"
							+ "  t.descricao as turno_descricao, ga.id_grau_academico, h.id_habilitacao, mc.id_matriz_curricular ");
		}

		sqlconsulta.append("from comum.pessoa p," +
				" curso c inner join comum.modalidade_educacao mod on (c.id_modalidade_educacao = mod.id_modalidade_educacao)," +
				" comum.unidade u, comum.municipio m, "
				+ "status_discente st, discente d ");

		if (nivel == NivelEnsino.GRADUACAO) {
			sqlconsulta
					.append(" left join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao) "
							+ "left join ead.polo po on (dg.id_polo = po.id_polo) "
							+ "left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade) "
							+ "left join graduacao.matriz_curricular mc on (mc.id_matriz_curricular = dg.id_matriz_curricular) "
							+ "left join graduacao.habilitacao h on (h.id_habilitacao = mc.id_habilitacao) "
							+ "left join ensino.turno t on (t.id_turno = mc.id_turno) "
							+ "left join ensino.grau_academico as ga on (ga.id_grau_academico = mc.id_grau_academico) ");
		}

		sqlconsulta.append(" where d.id_pessoa = p.id_pessoa "
				+ "   and d.id_curso = c.id_curso "
				+ "   and c.id_municipio = m.id_municipio"
				+ "   and u.id_unidade = c.id_unidade "
				+ "   and st.status = d.status ");

		if (nivel != '0' && nivel != 'S' && nivel != ' ')
			sqlconsulta.append(" and d.nivel = '" + nivel + "'");
		else if (nivel == 'S') {
			sqlconsulta.append(" and d.nivel in ('S', 'E', 'D') ");
		}

		if (filtros.get(ATIVO))
			sqlconsulta.append("   and d.status in " + gerarStringIn( new int[]{StatusDiscente.ATIVO, StatusDiscente.FORMANDO}) );
		else
			sqlconsulta.append("   and d.status = " + StatusDiscente.ATIVO);

		if (filtros.get(MATRICULADO))
			sqlconsulta
					.append("   and exists ("
							+ "        select * from ensino.matricula_componente mcp "
							+ "        where mcp.id_discente = d.id_discente"
							+ "        and mcp.id_situacao_matricula in (1,2,4,5,6,8,7)"
							+ "        and mcp.ano=" + ano
							+ "        and mcp.periodo =" + periodo + " ) ");

		if (filtros.get(MATRIZ_CURRICULAR))
			sqlconsulta.append("		and mc.id_matriz_curricular = "
					+ matrizCurricular.getId());
		else if (filtros.get(CURSO) && curso != null) {
			Curso curso2 = curso;
			sqlconsulta.append("		and c.id_curso = " + curso2.getId());
		} else if (filtros.get(UNIDADE) && curso != null) {
			Curso curso2 = curso;
			sqlconsulta.append("		and u.id_unidade = "
					+ curso2.getUnidade().getId());
		}

		sqlconsulta.append(" order by u.sigla, c.id_curso, "
				+ (nivel == NivelEnsino.GRADUACAO ? "mc.id_matriz_curricular, "
						: "") + "p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório de Alunos Concluintes (GRADUANDOS).
	 * 
	 * @param curso
	 * @return

	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunosConcluintes(Curso curso) throws DAOException {
		return findListaAlunosStatus(curso, StatusDiscente.GRADUANDO);
	}
	
	/**
	 * Retorna o Relatório de Alunos FORMANDOS.
	 * 
	 * @param curso
	 * @return

	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunosFormandos(Curso curso) throws DAOException {
		return findListaAlunosStatus(curso, StatusDiscente.FORMANDO);
	}

	/** Retorna um relatório de alunos dado o status.
	 * 
	 * @param curso
	 * @param statusDiscente
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunosStatus(Curso curso,
			int statusDiscente) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select u.sigla as unidade_sigla, "
						+ " c.nome as curso_nome, c.id_curso as id_curso, t.id_turno,ga.id_grau_academico, ga.descricao as modalidade_nome, "
						+ " t.sigla as turno_sigla,  d.ano_ingresso ,  d.periodo_ingresso, d.matricula,  p.nome as aluno_nome, "
						+ "  (select cast(valor as numeric(6,2)) from ensino.indice_academico_discente where id_discente = d.id_discente " 
						+ "    and id_indice_academico = "+Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.INDICE_ACADEMICO_LAUREADOS_ANTIGO_IRA))+") as ira, "
						+ " sd.descricao as status_aluno, "
						+ " m.nome as municipio, m.id_municipio as id_municipio, mpo.nome as municipio_polo	,  "
						+ "  (select cast(valor as numeric(6,2)) from ensino.indice_academico_discente where id_discente = d.id_discente " 
						+ "    and id_indice_academico = "+Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.ID_MC))+") as mc, " 
						+ "  (select cast(valor as numeric(6,2)) from ensino.indice_academico_discente where id_discente = d.id_discente " 
						+ "    and id_indice_academico = "+Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.ID_MCN))+") as mcn " 
						+ " from discente d, comum.pessoa p, curso c, comum.unidade u, graduacao.curriculo crr, graduacao.discente_graduacao dg "
						+ " left join ead.polo po on (dg.id_polo = po.id_polo) left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade),"
						+ " status_discente sd  , graduacao.matriz_curricular mc, ensino.turno t, ensino.grau_academico ga, comum.municipio m "
						+ " where d.id_pessoa = p.id_pessoa  "
						+ "  and d.id_curso = c.id_curso "
						+ "  and d.id_discente = dg.id_discente_graduacao "
						+ "  and u.id_unidade = c.id_unidade   "
						+ "  and d.id_curriculo = crr.id_curriculo  "
						+ "  and mc.id_matriz_curricular = crr.id_matriz "
						+ "  and c.id_municipio = m.id_municipio "
						+ "  and mc.id_grau_academico = ga.id_grau_academico "
						+ "  and mc.id_turno = t.id_turno "
						+ "  and d.status = sd.status " + " and d.status = "
						+ statusDiscente);
		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("		and c.id_curso = " + curso.getId());
		sqlconsulta
				.append(" order by c.nome, ga.descricao, t.sigla, m.nome , d.ano_ingresso, d.periodo_ingresso, p.nome");
		try {
			return executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna os dados para o relatório de discentes graduandos no ano/período informados. 
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findGraduandosByAnoPeriodo(Curso curso, int ano, int periodo, boolean somenteGraduandosNoAnoPeriodo) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select distinct u.sigla as unidade_sigla," +
				"  c.nome as curso_nome," +
				"  c.id_curso as id_curso," +
				"  t.id_turno," +
				"  ga.id_grau_academico," +
				"  ga.descricao as modalidade_nome," +
				"  t.sigla as turno_sigla," +
				"  d.ano_ingresso ," +
				"  d.periodo_ingresso," +
				"  d.matricula," +
				"  p.nome as aluno_nome," +
				"  (select cast(valor as numeric(6,2)) from ensino.indice_academico_discente where id_discente = d.id_discente " + 
				"    and id_indice_academico = "+Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.INDICE_ACADEMICO_LAUREADOS_ANTIGO_IRA))+") as ira, "+
				"  sd.descricao as status_aluno," +
				"  m.nome as municipio," +
				"  m.id_municipio as id_municipio," +
				"  min (mat.ano) as ano_graduando," +
				"  min (mat.periodo) as periodo_graduando," +
				"  mpo.nome as municipio_polo," +
				// período normal
				"  case when min (mat.ano * 10 + mat.periodo) = " + (ano * 10 + periodo) +
				// período de férias
				"         or min (mat.ano * 10 + mat.periodo) = " + (ano * 10 + periodo + 2) +
				"       then trueValue() " +
				"       else falseValue() end as graduando_no_ano_periodo," +
				"  (select cast(valor as numeric(6,2)) from ensino.indice_academico_discente where id_discente = d.id_discente " +
				"    and id_indice_academico = "+Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.ID_MC))+") as mc, " +
				"  (select cast(valor as numeric(6,2)) from ensino.indice_academico_discente where id_discente = d.id_discente " +
				"    and id_indice_academico = "+Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.ID_MCN))+") as mcn " +
				" from discente d" +
				"  inner join comum.pessoa p using (id_pessoa)" +
				"  inner join curso c using (id_curso)" +
				"  inner join comum.unidade u on (u.id_unidade = c.id_unidade)" +
				"  inner join graduacao.curriculo crr on (d.id_curriculo = crr.id_curriculo)" +
				"  inner join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao)" +
				"  left join ead.polo po on (dg.id_polo = po.id_polo)" +
				"  left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
				"  inner join status_discente sd on (d.status = sd.status)" +
				"  inner join graduacao.matriz_curricular mc on (mc.id_matriz_curricular = crr.id_matriz)" +
				"  inner join ensino.turno t on(mc.id_turno = t.id_turno)" +
				"  inner join ensino.grau_academico ga on (mc.id_grau_academico = ga.id_grau_academico)" +
				"  inner join comum.municipio m on (c.id_municipio = m.id_municipio)" +
				"  left join ensino.matricula_componente mat on (d.id_discente = mat.id_discente)" +
				" where d.status = " + StatusDiscente.GRADUANDO);
		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("		and c.id_curso = " + curso.getId());
		sqlconsulta.append(" group by u.sigla, c.nome, c.id_curso, t.id_turno, ga.id_grau_academico, ga.descricao, t.sigla, d.ano_ingresso , d.periodo_ingresso, d.id_discente, d.matricula, p.nome, dg.ira, sd.descricao, m.nome, m.id_municipio, mpo.nome");
		if (somenteGraduandosNoAnoPeriodo) {
			sqlconsulta.append("  having min (mat.ano * 10 + mat.periodo) = " + (ano * 10 + periodo) +
							   "         or min (mat.ano * 10 + mat.periodo) = " + (ano * 10 + periodo + 2));
		}
		sqlconsulta.append(" order by c.nome, ga.descricao, t.sigla, m.nome , d.ano_ingresso, d.periodo_ingresso, p.nome");
		try {
			return executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna o Relatório Quantitativo de Alunos Concluintes.
	 * 
	 * @param curso
	 * @param filtroCurso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findQuantitativoAlunosConcluintes(
			Curso curso, int filtroCurso, int ano, int periodo) throws DAOException {
		// sql de consulta
		StringBuilder sqlConsulta = new StringBuilder(
				"select u.sigla as centro,c.id_curso, c.nome as curso_nome, d.ano_ingresso, "
						+ " d.periodo_ingresso, sd.descricao as status_aluno, "
						+ " m.id_municipio, upper(m.nome) as municipio_nome, d.status, t.sigla as turno, "
						+ " count(distinct d.id_discente) as qtd, mpo.nome as municipio_polo "
						+ " from discente d, comum.pessoa p, curso c, graduacao.discente_graduacao dg "
						+ " left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade), "
						+ " status_discente sd, comum.unidade u, comum.municipio m,  "
						+ " graduacao.matriz_curricular mc, ensino.turno t "
						+ " where d.id_pessoa = p.id_pessoa   "
						+ " and d.id_curso = c.id_curso "
						+ " and dg.id_matriz_curricular = mc.id_matriz_curricular "
						+ " and mc.id_turno = t.id_turno "
						+ " and d.id_discente = dg.id_discente_graduacao   "
						+ " and c.id_municipio = m.id_municipio "
						+ " and c.id_unidade = u.id_unidade "
						+ " and d.status = sd.status   "
						+ " and d.status in "
						+ UFRNUtils.gerarStringIn(StatusDiscente
								.getStatusConcluinte())
						+ " and d.id_discente in ( select mco.id_discente from ensino.matricula_componente mco   "
						+ "     		  where mco.id_discente = d.id_discente ");
		if (periodo != 0) {
			sqlConsulta.append(" and mco.periodo=" + periodo);
		}

		sqlConsulta.append(" group by mco.id_discente ");

		if (ano != 0) {
			sqlConsulta.append(" having max(mco.ano)=" + ano);
		}

		sqlConsulta.append(" ) ");

		sqlConsulta.append(definirFiltroCurso(filtroCurso, curso));

		sqlConsulta
				.append(" group by u.sigla, c.id_curso, c.nome, m.nome , d.ano_ingresso , d.periodo_ingresso, sd.descricao, m.id_municipio, m.nome,d.status, t.sigla, mpo.nome   ");
		sqlConsulta
				.append(" order by u.sigla, c.nome, m.nome, t.sigla, d.ano_ingresso , d.periodo_ingresso");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlConsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/** Define os filtros utilizados nas consultas.
	 * 
	 * @param filtroCurso
	 * @param curso
	 * @return
	 */
	private String definirFiltroCurso(int filtroCurso, Curso curso) {
		switch (filtroCurso) {
		case TipoFiltroCurso.CURSO_SELECIONADO:
			return " and c.id_curso = " + curso.getId();
		case TipoFiltroCurso.PRESENCIAIS:
			return " and c.id_modalidade_educacao = "
					+ ModalidadeEducacao.PRESENCIAL;
		case TipoFiltroCurso.A_DISTANCIA:
			return " and c.id_modalidade_educacao = "
					+ ModalidadeEducacao.A_DISTANCIA;
		case TipoFiltroCurso.PROBASICA:
			return " and c.id_convenio = " + ConvenioAcademico.PROBASICA;
		default:
			return null;
		}
	}

	/**
	 * Retorna o Relatório de Alunos reprovados e trancados.
	 * 
	 * @param matrizCurricular
	 * @param ano
	 * @param periodo
	 * @param situacaoMatricula
	 * @param filtros
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findListaAlunoReprovadosTrancados(
			MatrizCurricular matrizCurricular, int ano, int periodo,
			SituacaoMatricula situacaoMatricula,
			HashMap<Integer, Boolean> filtros) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro, c.id_curso, c.nome as curso_nome, upper(t.descricao) as turno_desc,   "
						+ "  d.matricula, d.ano_ingresso, d.periodo_ingresso, p.nome as aluno_nome, ccd.nome as componente_nome, ccd.codigo as componente_codigo, "
						+ "  ccd.ch_total as componente_ch, sm.descricao as situacao_matricula, d.id_discente,  "
						+ "  ga.descricao as modalidade_curso, m.nome as municipio_nome, mc.id_matriz_curricular,  "
						+ "  h.nome as habilitacao_nome, mt.media_final, mt.numero_faltas, mpo.nome as municipio_polo  "
						+ " from discente d, graduacao.discente_graduacao dg left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade), comum.pessoa p,  curso c, "
						+ "   graduacao.matriz_curricular mc left outer join graduacao.habilitacao h using (id_habilitacao),     "
						+ "   ensino.turno t,  ensino.matricula_componente mt, comum.unidade u, comum.municipio m,     "
						+ "   ensino.grau_academico ga, ensino.situacao_matricula sm, ensino.componente_curricular_detalhes ccd  "
						+ "  where p.id_pessoa = d.id_pessoa      "
						+ "   and d.id_curso = c.id_curso      "
						+ "   and d.id_discente = dg.id_discente_graduacao      "
						+ "   and dg.id_matriz_curricular = mc.id_matriz_curricular      "
						+ "   and mc.id_turno = t.id_turno      "
						+ "   and d.id_discente = mt.id_discente       "
						+ "   and c.id_unidade = u.id_unidade       "
						+ "   and m.id_municipio = c.id_municipio      "
						+ "   and mc.id_grau_academico = ga.id_grau_academico  "
						+ "   and ccd.id_componente_detalhes = mt.id_componente_detalhes      "
						+ "   and mt.id_situacao_matricula = sm.id_situacao_matricula   ");

		if (filtros.get(ATIVO))
			sqlconsulta.append("		and d.status = 1 ");

		if (filtros.get(CURSO))
			sqlconsulta.append("		and mc.id_curso = "
					+ matrizCurricular.getCurso().getId());

		if (filtros.get(MATRIZ_CURRICULAR))
			sqlconsulta.append("    and mc.id_matriz_curricular= "
					+ matrizCurricular.getId());

		sqlconsulta.append("     and mt.ano = " + ano);
		sqlconsulta.append("     and mt.periodo = " + periodo);

		if (situacaoMatricula.getId() == 5)
			sqlconsulta.append("    and mt.id_situacao_matricula= 5");
		else if (situacaoMatricula.getId() == 6)
			sqlconsulta.append("    and mt.id_situacao_matricula in (6,7)  ");
		else
			sqlconsulta.append("    and mt.id_situacao_matricula in (5,6,7)  ");

		sqlconsulta
				.append("   order by u.sigla, c.id_curso, mc.id_matriz_curricular, p.nome, sm.descricao; 		 ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório de Alunos Ativos e Matriculados.
	 * 
	 * @param matrizCurricular
	 * @param ano
	 * @param periodo
	 * @param filtros
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findListaAlunoMatriculados(
			MatrizCurricular matrizCurricular, int ano, int periodo,
			HashMap<Integer, Boolean> filtros) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro, c.id_curso, c.nome as curso_nome, upper(t.descricao) as turno_desc, "
						+ "  d.matricula, d.ano_ingresso, d.periodo_ingresso, p.nome as aluno_nome, mt.ano, "
						+ "  mt.periodo, count(mt.id_matricula_componente) as qtd_matriculas,  ga.descricao as modalidade_curso, "
						+ "  m.nome as municipio_nome, mc.id_matriz_curricular, h.nome as habilitacao_nome, mpo.nome as municipio_polo,  "
						+ "  sum(case when mt.id_situacao_matricula in (1,2) then 1 else 0 end) as matriculados, "
						+ "  sum(case when mt.id_situacao_matricula = 5 then 1 else 0 end) as trancado, "
						+ "  sum(case when mt.id_situacao_matricula in (4,8,21,22,23) then 1 else 0 end) as aprovacoes, "
						+ "  sum(case when mt.id_situacao_matricula in (6,7) then 1 else 0 end) as reprovacoes   "
						+ " from discente d, graduacao.discente_graduacao dg left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "	 left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade), comum.pessoa p,  curso c, "
						+ "  graduacao.matriz_curricular mc left outer join graduacao.habilitacao h using (id_habilitacao),  "
						+ "  ensino.turno t,  ensino.matricula_componente mt, comum.unidade u, comum.municipio m,  "
						+ "  ensino.grau_academico ga   "
						+ " where p.id_pessoa = d.id_pessoa   "
						+ "  and d.id_curso = c.id_curso   "
						+ "  and d.id_discente = dg.id_discente_graduacao   "
						+ "  and dg.id_matriz_curricular = mc.id_matriz_curricular   "
						+ "  and mc.id_turno = t.id_turno   "
						+ "  and d.id_discente = mt.id_discente    "
						+ "  and c.id_unidade = u.id_unidade    "
						+ "  and m.id_municipio = c.id_municipio   "
						+ "  and mc.id_grau_academico = ga.id_grau_academico   "
						+ "  and mt.id_situacao_matricula in (1,2,4,5,6,7,8,21,22,23) 		 ");

		if (filtros.get(ATIVO))
			sqlconsulta.append("		and d.status = 1 ");

		if (filtros.get(CURSO))
			sqlconsulta.append("		and mc.id_curso = "
					+ matrizCurricular.getCurso().getId());

		if (filtros.get(MATRIZ_CURRICULAR))
			sqlconsulta.append("    and mc.id_matriz_curricular="
					+ matrizCurricular.getId());

		sqlconsulta.append("     and mt.ano = " + ano);
		sqlconsulta.append("     and mt.periodo = " + periodo);

		sqlconsulta
				.append(" group by u.sigla, c.id_curso, c.nome , t.descricao, d.matricula, d.ano_ingresso,  d.periodo_ingresso,  "
						+ "         p.nome, mt.ano, mt.periodo,  ga.descricao, m.nome, h.nome, mc.id_matriz_curricular, mpo.nome  "
						+ "order by u.sigla, c.nome , mc.id_matriz_curricular, mt.ano, mt.periodo, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/** Retorna um relatório de discentes ativos vinculados a uma estrutura curricular.
	 * 
	 * @param matrizCurricular
	 * @param codigo
	 * @param filtros
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findListaVinculadosEstrutura(
			MatrizCurricular matrizCurricular, String codigo,
			HashMap<Integer, Boolean> filtros) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" SELECT u.sigla as centro, c.nome as curso_nome, upper(t.descricao) as turno_desc, "
						+ " d.matricula, d.ano_ingresso, d.periodo_ingresso, p.nome as aluno_nome, "
						+ " ga.descricao as modalidade_curso,   m.nome as municipio_nome, mc.id_matriz_curricular, "
						+ " h.nome as habilitacao_nome, ec.codigo as codigo_curriculo, mpo.nome as municipio_polo	 "
						+ " FROM discente d, graduacao.discente_graduacao dg left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade), comum.pessoa p,  curso c, "
						+ " graduacao.curriculo ec  left outer join graduacao.matriz_curricular mc on (ec.id_matriz = mc.id_matriz_curricular)"
						+ "			left outer join graduacao.habilitacao h using (id_habilitacao)"
						+ "			left outer join ensino.turno t using (id_turno)"
						+ "			left outer join ensino.grau_academico ga using (id_grau_academico),"
						+ " comum.unidade u, comum.municipio m"
						+ " WHERE p.id_pessoa = d.id_pessoa "
						+ " and d.id_curso = c.id_curso "
						+ " and c.id_curso = ec.id_curso "
						+ " and d.id_discente = dg.id_discente_graduacao "
						+ " and c.id_unidade = u.id_unidade "
						+ " and m.id_municipio = c.id_municipio "
						+ " and d.status in " + gerarStringIn(new int[]{StatusDiscente.ATIVO,StatusDiscente.FORMANDO})
						+ " and d.id_curriculo = ec.id_curriculo");

		if (filtros.get(CURSO))
			sqlconsulta.append("		and mc.id_curso = "
					+ matrizCurricular.getCurso().getId());

		if (filtros.get(MATRIZ_CURRICULAR))
			sqlconsulta.append("    and mc.id_matriz_curricular="
					+ matrizCurricular.getId());

		if (filtros.get(CODIGO))
			sqlconsulta.append("    and ec.codigo = '" + codigo + "' ");

		if (filtros.get(ATIVO))
			sqlconsulta.append("		and d.status in (1,2,8,9) ");

		sqlconsulta
				.append(" GROUP BY u.sigla, c.nome , t.descricao, d.matricula, d.ano_ingresso,  d.periodo_ingresso, "
						+ " p.nome, ga.descricao, m.nome, h.nome, mc.id_matriz_curricular,   ec.codigo, mpo.nome "
						+ " ORDER BY u.sigla, c.nome, mc.id_matriz_curricular, ec.codigo, d.ano_ingresso, d.periodo_ingresso, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório de Quantitativos Alunos Ativos e
	 * Matriculados.
	 * 
	 * @param curso
	 * @param filtroCurso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findQuantitativoAlunoMatriculados(
			Curso curso, int filtroCurso, int ano, int periodo) throws DAOException {
		// sql de consulta
		StringBuilder sqlConsulta = new StringBuilder(
				" select u.sigla as centro, c.id_curso, c.nome as curso_nome, t.id_turno, t.sigla as turno_sigla, "
						+ " 	count(*) as qtd_matriculados, mc.id_grau_academico,   "
						+ "   ga.descricao as modalidade_aluno, upper(m.nome) as municipio_nome "
						+ "   "
						+ " from discente d, graduacao.discente_graduacao dg, curso c, ensino.turno t,    "
						+ "      comum.unidade u , ensino.grau_academico ga, comum.municipio m,  "
						+ "      graduacao.matriz_curricular mc "
						+ "   "
						+ " where d.id_curso = c.id_curso   "
						+ "   and d.id_discente = dg.id_discente_graduacao    "
						+ "   and c.id_municipio = m.id_municipio    "
						+ "   and dg.id_matriz_curricular = mc.id_matriz_curricular    "
						+ "   and mc.id_turno = t.id_turno   "
						+ "   and mc.id_grau_academico = ga.id_grau_academico  "
						+ "   and c.id_unidade = u.id_unidade    "
						+ "   and exists ( select * from ensino.matricula_componente mct   "
						+ "     		   where mct.id_discente = d.id_discente  "
						+ "                and mct.id_situacao_matricula in (1,2,4,5,6,7)  ");
		
	    if (filtroCurso != TipoFiltroCurso.PROBASICA)
		   sqlConsulta.append("   and c.id_convenio is null");

		if (ano != 0 && periodo != 0) {
			sqlConsulta.append("     and mct.ano = " + ano);
			sqlConsulta.append("     and mct.periodo = " + periodo);
		}

		sqlConsulta.append(" )");

		sqlConsulta.append(definirFiltroCurso(filtroCurso, curso));

		sqlConsulta
				.append(" group by u.sigla, c.id_curso, c.nome, t.sigla,t.id_turno,mc.id_grau_academico, "
						+ "     ga.descricao, m.nome  "
						+ " order by u.sigla, c.nome, t.sigla");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlConsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório de Alunos Ativos e seus prazos de
	 * conclusão.
	 * 
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findListaAlunoPrazoConclusao(
			Curso curso, int ano, int periodo) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select u.sigla as centro, c.nome as curso_aluno, c.id_curso, mc.id_turno,"
						+ " 	d.matricula, d.ano_ingresso,  d.periodo_ingresso, p.nome as aluno_nome, "
						+ " 	d.prazo_conclusao, sd.descricao as status_aluno, d.status , t.sigla as turno_sigla, "
						+ " 	mpo.nome as municipio_polo"
						+ " from discente d, comum.pessoa p, comum.unidade u, curso c,  "
						+ " 	status_discente sd, graduacao.matriz_curricular mc, graduacao.discente_graduacao dg " 
						+ "		left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "		left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ " , ensino.turno t where d.id_pessoa = p.id_pessoa  "
						+ " 	and d.id_curso = c.id_curso  "
						+ "    and c.id_unidade = u.id_unidade  "
						+ "	 and mc.id_turno = t.id_turno  "
						+ "    and d.status = sd.status  "
						+ "    and c.nivel = 'G'  "
						+ "    and d.id_discente = dg.id_discente_graduacao "
						+ "    and mc.id_matriz_curricular = dg.id_matriz_curricular "
						+ "    and d.status not in (-1,3,6,7) ");

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("   and c.id_curso = " + curso.getId());

		if (ano != 0 & periodo != 0) {
			int prazoConclusao = (ano * 10) + periodo;
			sqlconsulta.append("   and d.prazo_conclusao = " + prazoConclusao);
		}

		sqlconsulta
				.append(" order by u.sigla, c.nome, c.id_curso, t.id_turno, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório de Alunos Ativos por curso.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException 
 	 */
	public List<Map<String, Object>> findListaAlunoAtivo(Curso curso) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select u.sigla as centro, c.nome as curso_aluno, c.id_curso, mc.id_turno,"
						+ " 	d.matricula, d.ano_ingresso,  d.periodo_ingresso, p.nome as aluno_nome, "
						+ " 	d.prazo_conclusao, sd.descricao as status_aluno, d.status , "
						+ "		t.sigla as turno_sigla, mpo.nome as municipio_polo "
						+ " from discente d, comum.pessoa p, comum.unidade u, curso c,  "
						+ " 	status_discente sd, graduacao.matriz_curricular mc, "
						+ "		graduacao.discente_graduacao dg "
						+ "		left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "		left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ " , ensino.turno t where d.id_pessoa = p.id_pessoa  "
						+ " 	and d.id_curso = c.id_curso  "
						+ "    and c.id_unidade = u.id_unidade  "
						+ "	 and mc.id_turno = t.id_turno  "
						+ "    and d.status = sd.status  "
						+ "    and c.nivel = 'G'  "
						+ "    and d.id_discente = dg.id_discente_graduacao "
						+ "    and mc.id_matriz_curricular = dg.id_matriz_curricular "
						+ "    and d.status not in (-1,3,6,7,10) ");

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("   and c.id_curso = " + curso.getId());

		sqlconsulta
				.append(" order by u.sigla, c.nome, c.id_curso, t.id_turno, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
	
	
	/**
	 * Retorna o relatório de alunos com mais de um vinculo ativo.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException 
 	 */
	public List<Map<String, Object>> findListaAlunoMaisVinculoAtivo(Integer anoIngresso) throws DAOException {
		
		StringBuilder statusDiscente =  new StringBuilder( StatusDiscente.ATIVO + "," );
			statusDiscente.append( StatusDiscente.CADASTRADO + "," );
			statusDiscente.append( StatusDiscente.TRANCADO + "," );
			statusDiscente.append( StatusDiscente.FORMANDO );
		
		StringBuilder sqlconsulta = new StringBuilder(" SELECT p.nome, p.cpf_cnpj, vinculos.nome_curso_atual, ");
			sqlconsulta.append(" 	vinculos.municipio AS municipio_curso_atual, vinculos.matricula AS matricula_curso_atual, ");
			sqlconsulta.append(" 	sd.descricao AS status, c.nome AS nome_curso_novo, m.nome AS municipio_curso_novo, ");
			sqlconsulta.append(" 	d.matricula AS matricula_curso_novo, d.ano_ingresso, d.periodo_ingresso, ");
			sqlconsulta.append(" 	fi.descricao AS forma_ingresso, municipio_polo ");
			
			sqlconsulta.append(" FROM discente ");
			sqlconsulta.append(" 	d INNER JOIN comum.pessoa p ON p.id_pessoa = d.id_pessoa ");
			sqlconsulta.append(" 	INNER JOIN curso c ON c.id_curso = d.id_curso ");
			sqlconsulta.append(" 	JOIN ensino.forma_ingresso fi USING(id_forma_ingresso) ");
			sqlconsulta.append(" 	JOIN comum.municipio m USING(id_municipio), ");
			sqlconsulta.append(" 	( SELECT dis.id_pessoa, dis.id_discente, dis.status, dis.id_curso as id_curso_atual, dis.matricula, ");
			sqlconsulta.append("		cur.nome as nome_curso_atual, m.nome as municipio, mpo.nome as municipio_polo FROM discente dis ");
			sqlconsulta.append("		INNER JOIN graduacao.discente_graduacao dg ON dg.id_discente_graduacao = dis.id_discente ");
			sqlconsulta.append("		LEFT JOIN ead.polo po on (dg.id_polo = po.id_polo)");
			sqlconsulta.append("		LEFT JOIN comum.municipio mpo on (mpo.id_municipio = po.id_cidade)");
			sqlconsulta.append("		JOIN curso cur ON cur.id_curso = dis.id_curso ");
			sqlconsulta.append("		JOIN comum.municipio m on (m.id_municipio = cur.id_municipio) ");
			sqlconsulta.append("		WHERE dis.status IN (" + statusDiscente + ") AND dis.nivel = '" + NivelEnsino.GRADUACAO + "'");
			sqlconsulta.append("		ORDER BY dis.id_discente DESC, dis.id_pessoa ASC ) AS vinculos");
			sqlconsulta.append(" 	JOIN status_discente sd ON(vinculos.status = sd.status) ");
			
			sqlconsulta.append(" WHERE p.id_pessoa = vinculos.id_pessoa ");
			sqlconsulta.append(" 	AND d.id_discente <> vinculos.id_discente ");
			sqlconsulta.append(" 	AND c.id_curso <> vinculos.id_curso_atual ");
			sqlconsulta.append(" 	AND d.ano_ingresso = " + anoIngresso);
			sqlconsulta.append(" 	AND d.status IN (" + statusDiscente + ") " );
			sqlconsulta.append(" 	AND d.nivel = '" + NivelEnsino.GRADUACAO + "' ");
			
			sqlconsulta.append(" ORDER BY p.nome ASC; ");

		List<Map<String, Object>> result;
		result = executeSql(sqlconsulta.toString());
	
		return result;
		
	}

	/**
	 * Retorna o Relatório de Alunos Ativos e Não Matriculados por
	 * curso.
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException 
 
	 */
	public List<Map<String, Object>> findListaAlunoAtivoNaoMatriculado(
			Curso curso) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select u.sigla as centro, c.nome as curso_aluno, c.id_curso, mc.id_turno,"
						+ " 	d.matricula, d.ano_ingresso,  d.periodo_ingresso, p.nome as aluno_nome, "
						+ " 	d.prazo_conclusao, sd.descricao as status_aluno, d.status , "
						+ " 	t.sigla as turno_sigla, mpo.nome as municipio_polo "
						+ " from discente d, comum.pessoa p, comum.unidade u, curso c,  "
						+ " 	status_discente sd, graduacao.matriz_curricular mc, graduacao.discente_graduacao dg "
						+ "		left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "		left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ " , ensino.turno t where d.id_pessoa = p.id_pessoa  "
						+ " 	and d.id_curso = c.id_curso  "
						+ "    and c.id_unidade = u.id_unidade  "
						+ "	 and mc.id_turno = t.id_turno  "
						+ "    and d.status = sd.status  "
						+ "    and c.nivel = 'G'  "
						+ "    and d.id_discente = dg.id_discente_graduacao "
						+ "    and mc.id_matriz_curricular = dg.id_matriz_curricular "
						+ "    and d.status not in (-1,3,6,7) ");

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("   and c.id_curso = " + curso.getId());

		sqlconsulta
				.append("   and not exists (select * from ensino.matricula_componente m"
						+ "		where m.id_discente = d.id_discente"
						+ "		and m.id_situacao_matricula = 2" + "		)");

		sqlconsulta
				.append(" order by u.sigla, c.nome, c.id_curso, t.id_turno, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório quantitativo de alunos egressos por sexo.
	 * 
	 * @param unidade
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException 
 
	 */
	public List<Map<String, Object>> findQuantitativosAlunoSexoEgresso(
			Unidade unidade, int ano, int periodo) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select distinct u.sigla as centro, c.nome as curso_nome,c.id_curso, m.nome as municipio,  "
						+ " m.id_municipio,  tma.descricao as movimentacao_aluno,  "
						+ "  sum( case when p.sexo = 'M' then 1 else 0 end) as qtd_masculino,     "
						+ "  sum( case when p.sexo = 'F' then 1 else 0 end) as qtd_feminino     "
						+ " from comum.unidade u, curso c, ensino.movimentacao_aluno ma,  "
						+ "     discente d, comum.pessoa p, graduacao.discente_graduacao dg,    "
						+ "     comum.municipio m, ensino.tipo_movimentacao_aluno tma  "
						+ " where d.id_curso = c.id_curso      "
						+ "   and u.id_unidade = c.id_unidade    "
						+ "   and d.id_pessoa = p.id_pessoa     "
						+ "   and d.id_discente = ma.id_discente  "
						+ "   and ma.id_tipo_movimentacao_aluno = tma.id_tipo_movimentacao_aluno  "
						+ "   and ma.data_retorno is NULL  "
						+ "   and d.status = tma.statusdiscente  "
						+ "   and d.status in (-1,3,6)  "
						+ "   and d.id_discente = dg.id_discente_graduacao  "
						+ "   and c.id_municipio = m.id_municipio");

		if (unidade != null && unidade.getId() != 0)
			sqlconsulta.append("		and c.id_unidade = " + unidade.getId());

		if (ano != 0 && periodo != 0) {
			sqlconsulta.append("     and ma.ano_referencia= " + ano);
			sqlconsulta.append("     and ma.periodo_referencia = " + periodo);
		}

		sqlconsulta
				.append(" group by u.sigla,m.nome,c.id_curso,m.id_municipio,tma.descricao, c.nome     "
						+ " order by u.sigla, c.nome, m.nome, tma.descricao");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório de quantitativos de Alunos por sexo.
	 * 
	 * @param centro
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException 
 
	 */
	public List<Map<String, Object>> findQuantitativosAlunosSexo(
			Unidade centro, int ano, int periodo) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select distinct u.sigla as centro,  c.nome as curso_nome,c.id_curso, "
						+ " f.descricao as ingresso_descricao, m.nome as municipio,  "
						+ " m.id_municipio,  "
						+ " sum( case when p.sexo = 'M' then 1 else 0 end) as qtd_masculino,    "
						+ " sum( case when p.sexo = 'F' then 1 else 0 end) as qtd_feminino    "
						+ " from comum.unidade u, curso c, ensino.forma_ingresso f,   "
						+ "      discente d, comum.pessoa p, graduacao.discente_graduacao dg,   "
						+ "      comum.municipio m     "
						+ " where d.id_curso = c.id_curso      "
						+ "   and u.id_unidade = c.id_unidade    "
						+ "   and d.id_pessoa = p.id_pessoa     "
						+ "   and d.id_forma_ingresso = f.id_forma_ingresso "
						+ "   and d.id_discente = dg.id_discente_graduacao  "
						+ "   and c.id_municipio = m.id_municipio ");

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append("		and c.id_unidade = " + centro.getId());

		if (ano != 0 && periodo != 0) {
			sqlconsulta.append("     and d.ano_ingresso = " + ano);
			sqlconsulta.append("     and d.periodo_ingresso = " + periodo);
		}

		sqlconsulta
				.append(" group by m.nome,c.id_curso,m.id_municipio,u.sigla,f.descricao, c.nome   "
						+ " order by u.sigla, c.nome, m.nome, f.descricao");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório de Alunos cadastrados do Vestibular sem
	 * matrícula em disciplina.
	 * 
	 * @param curso
	 * @param filtroCurso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoAlunoVestibularSemMatricula(
			Curso curso, int filtroCurso, int ano, int periodo)
			throws DAOException {
		// sql de consulta

		StringBuilder sqlconsulta = new StringBuilder(
				"select d.ano_ingresso, d.periodo_ingresso, u.sigla as centro_sigla, "
						+ " c.nome as curso_nome, c.id_curso as id_curso, "
						+ " t.sigla as turno_sigla, d.matricula, p.nome as aluno_nome, sd.descricao as discente_status, "
						+ " m.id_municipio, m.nome as municipio, "
						+ " count(mc.id_matricula_componente) "
						+ " from comum.unidade u, curso c, ensino.turno t,  "
						+ " comum.pessoa p, graduacao.matriz_curricular mr, "
						+ " graduacao.discente_graduacao dg, status_discente sd, "
						+ " comum.municipio m, "
						+ " discente d left join ensino.matricula_componente mc "
						+ "   on (mc.id_discente = d.id_discente and mc.ano = d.ano_ingresso and mc.periodo = d.periodo_ingresso and mc.id_turma is not null)"
						+ " where p.id_pessoa = d.id_pessoa "
						+ " and d.id_curso = c.id_curso"
						+ " and d.status = sd.status"
						+ " and c.id_unidade = u.id_unidade "
						+ " and c.id_municipio = m.id_municipio "
						+ " and d.id_discente = dg.id_discente_graduacao "
						+ " and dg.id_matriz_curricular = mr.id_matriz_curricular "
						+ " and mr.id_turno = t.id_turno"
						+ " and d.id_forma_ingresso = "
						+ FormaIngresso.VESTIBULAR.getId()
						+ " and d.status not in "
						+ UFRNUtils.gerarStringIn(StatusDiscente
								.getAfastamentosPermanentes()));

		sqlconsulta.append(definirFiltroCurso(filtroCurso, curso));

		if (ano != 0 && periodo != 0) {
			sqlconsulta.append(" and d.ano_ingresso = " + ano);
			sqlconsulta.append(" and d.periodo_ingresso = " + periodo);
		}

		sqlconsulta
				.append(" group by d.ano_ingresso, d.periodo_ingresso, "
						+ " u.sigla, c.nome, c.id_curso, t.sigla, d.matricula, p.nome, sd.descricao, "
						+ " m.nome, m.id_municipio "
						+ " having count(mc.id_matricula_componente) = 0 "
						+ " order by d.ano_ingresso, d.periodo_ingresso, u.sigla, c.nome,t.sigla, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório de Alunos pelo seu tipo de saída, seja ela
	 * temporária ou não
	 * 
	 * @param matrizCurricular
	 * @param ano
	 * @param periodo
	 * @param statusDiscente
	 * @param ingresso
	 * @param tipoMovimentacao
	 * @param filtros
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findListaAlunoTipoSaida(
			MatrizCurricular matrizCurricular, int ano, int periodo, Integer statusDiscente,
			FormaIngresso ingresso, TipoMovimentacaoAluno tipoMovimentacao,
			HashMap<Integer, Boolean> filtros) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select tma.descricao as aluno_tipo_saida, ma.data_ocorrencia, u.sigla as centro,  "
						+ "	     c.nome as curso_aluno, d.matricula, d.ano_ingresso, c.id_curso, t.id_turno,    "
						+ "        upper(t.descricao) as turno_descricao, d.periodo_ingresso, p.nome as aluno_nome, mr.id_matriz_curricular,    "
						+ "        sd.descricao as aluno_status, fi.descricao as forma_ingresso, ga.descricao as modalidade_nome,   "
						+ "        upper(m.nome) as  municipio_nome, h.nome as habilitacao_nome, mpo.nome as municipio_polo"
						+ " from discente d, curso c, graduacao.discente_graduacao dg "
						+ " left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ "      ,graduacao.matriz_curricular mr left outer join graduacao.habilitacao h using (id_habilitacao),   "
						+ "      ensino.turno t, ensino.movimentacao_aluno ma, comum.municipio m, ensino.grau_academico ga,     "
						+ "      ensino.tipo_movimentacao_aluno tma, comum.pessoa p, status_discente sd,   "
						+ "      comum.unidade u, ensino.forma_ingresso fi    "
						+ " where d.id_pessoa = p.id_pessoa      "
						+ "   and d.id_curso = c.id_curso     "
						+ "   and d.id_forma_ingresso = fi.id_forma_ingresso    "
						+ "   and d.id_discente = ma.id_discente    "
						+ "   and ma.id_tipo_movimentacao_aluno = tma.id_tipo_movimentacao_aluno    "
						+ "   and d.id_discente = dg.id_discente_graduacao     "
						+ "   and dg.id_matriz_curricular = mr.id_matriz_curricular     "
						+ "   and mr.id_turno = t.id_turno    "
						+ "   and u.id_unidade = c.id_unidade  "
						+ "   and c.id_municipio = m.id_municipio  "
						+ "   and mr.id_grau_academico = ga.id_grau_academico    "
						+ "   and d.status = sd.status  ");

		if (filtros.get(MATRIZ_CURRICULAR))
			sqlconsulta.append("		and mr.id_matriz_curricular = "
					+ matrizCurricular.getId());

		else if (filtros.get(CURSO))
			sqlconsulta.append("		and c.id_curso = "
					+ matrizCurricular.getCurso().getId());

		else if (filtros.get(UNIDADE))
			sqlconsulta.append("		and u.id_unidade = "
					+ matrizCurricular.getCurso().getUnidade().getId());

		if (filtros.get(INGRESSO))
			sqlconsulta.append("		and d.id_forma_ingresso = "
					+ ingresso.getId());

		if (filtros.get(AFASTAMENTO_PERMANENTE))
			sqlconsulta.append(" and tma.grupo = '"	+ GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE + "'");
		
		if (filtros.get(EGRESSO))
			sqlconsulta.append(" and tma.id_tipo_movimentacao_aluno = "
					+ tipoMovimentacao.getId());		
		
		if (filtros.get(ATIVO))
			sqlconsulta.append(" and d.status = " + statusDiscente);

		sqlconsulta.append("     and ma.ano_referencia = " + ano);
		sqlconsulta.append("     and ma.periodo_referencia = " + periodo);
		sqlconsulta.append(
				" and ma.ativo = trueValue() and ma.data_retorno is null " +
				" and ma.id_movimentacao_aluno not in (select id_movimentacao_aluno from ensino.estorno_movimentacao_aluno)" +
				" order by u.sigla,c.nome,c.id_curso, mr.id_matriz_curricular, mpo.nome, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório Quantitativos de Alunos de probásica
	 * concluídos.
	 * 
	 * @param centro
	 * @return
	 * @throws DAOException 
 	 */
	public List<Map<String, Object>> findQuantitativoAlunoProbasicaConcluido(
			Unidade centro) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro, c.id_curso, c.nome as curso_nome , m.id_municipio,"
						+ " upper(m.nome) as municipio_nome, count(d.id_discente) as qtd_discente  "
						+ " from discente d, curso c, comum.municipio m, comum.unidade u  "
						+ " where d.id_curso = c.id_curso   "
						+ "  and c.id_municipio = m.id_municipio    "
						+ "  and c.id_unidade = u.id_unidade   "
						+ "  and d.status = 3   "
						+ "  and c.id_convenio = 1   ");

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append("		and c.id_unidade = " + centro.getId());

		sqlconsulta
				.append(" group by u.sigla, c.id_curso, m.id_municipio,c.nome, m.nome   "
						+ " order by u.sigla, c.nome, m.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório quantitativo de alunos de probásica matriculados.
	 * 
	 * @param centro
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findQuantitativoAlunoProbasicaMatriculados(
			Unidade centro) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro, c.nome as curso_nome, t.id_turno, t.sigla as turno_sigla, "
						+ " 	count(*) as qtd_matriculados, mc.id_grau_academico, mc.id_habilitacao,  "
						+ "     ga.descricao as modalidade_aluno, h.nome as habilitacao_nome, upper(m.nome) as municipio_nome  "
						+ "  "
						+ " from discente d, graduacao.discente_graduacao dg, curso c, ensino.turno t,    "
						+ "      comum.unidade u , ensino.grau_academico ga, comum.municipio m,  "
						+ "      graduacao.matriz_curricular mc left outer join graduacao.habilitacao h using  "
						+ "      (id_habilitacao)  "
						+ "  "
						+ " where d.id_curso = c.id_curso  "
						+ "   and c.id_municipio = m.id_municipio    "
						+ "   and d.id_discente = dg.id_discente_graduacao    "
						+ "   and dg.id_matriz_curricular = mc.id_matriz_curricular    "
						+ "   and mc.id_turno = t.id_turno   "
						+ "   and c.id_convenio =1   "
						+ "   and mc.id_grau_academico = ga.id_grau_academico  "
						+ "   and c.id_unidade = u.id_unidade    "
						+ "   and exists (select * from ensino.matricula_componente mct   "
						+ "  			  where mct.id_discente = d.id_discente "
						+ "              and mct.id_situacao_matricula in (1,2))  ");

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append("		and c.id_unidade = " + centro.getId());

		sqlconsulta
				.append(" group by u.sigla, c.nome, t.sigla,t.id_turno,mc.id_grau_academico, mc.id_habilitacao,  "
						+ "     ga.descricao, h.nome, m.nome  "
						+ " order by u.sigla, c.nome, ga.descricao, h.nome, t.sigla, m.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o Relatório quantitativo de alunos de probásica ativos e sem matrícula.
	 * 
	 * @param centro
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findQuantitativoAlunoProbasicaSemMatricula(
			Unidade centro) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro, c.nome as curso_nome, t.id_turno, t.sigla as turno_sigla,  "
						+ "  count(*) as qtd_nao_matriculados, mc.id_grau_academico, mc.id_habilitacao,         "
						+ "  ga.descricao as modalidade_aluno, h.nome as habilitacao_nome, upper(m.nome) as municipio_nome       "
						+ " from discente d, graduacao.discente_graduacao dg, curso c, ensino.turno t,            "
						+ " comum.unidade u , ensino.grau_academico ga, comum.municipio m,         "
						+ " graduacao.matriz_curricular mc left outer join graduacao.habilitacao h using          "
						+ " (id_habilitacao)      "
						+ " where d.id_curso = c.id_curso       "
						+ " and c.id_municipio = m.id_municipio         "
						+ " and d.id_discente = dg.id_discente_graduacao         "
						+ " and dg.id_matriz_curricular = mc.id_matriz_curricular         "
						+ " and mc.id_turno = t.id_turno        "
						+ " and c.id_convenio =1  "
						+ " and d.status =1        "
						+ " and mc.id_grau_academico = ga.id_grau_academico       "
						+ " and c.id_unidade = u.id_unidade         "
						+ " and not exists (select * from ensino.matricula_componente mct  "
						+ "      			  where mct.id_discente = d.id_discente  "
						+ "                 and mct.id_situacao_matricula in (1,2))     ");

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append("		and c.id_unidade = " + centro.getId());

		sqlconsulta
				.append("  group by u.sigla, c.nome, t.sigla,t.id_turno,mc.id_grau_academico, mc.id_habilitacao,        "
						+ "   ga.descricao, h.nome, m.nome    "
						+ "  order by u.sigla, c.nome, ga.descricao, h.nome, t.sigla, m.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o relatório de alunos com registro em uma determinada
	 * disciplina.
	 * 
	 * @param disciplina
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunoRegistroDisciplina(
			ComponenteCurricular disciplina, Discente discente) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select u.sigla as centro, c.id_curso, c.nome as curso_aluno, d.matricula, "
						+ "		d.ano_ingresso,  d.periodo_ingresso, p.nome as aluno_nome, turn.id_turno,  "
						+ "		turn.sigla as turno_sigla, mco.ano, mco.periodo, mco.media_final,  "
						+ "		sm.descricao as status_matricula, sd.descricao as status_aluno, "
						+ "         ccd.codigo as disciplina_codigo, ccd.nome as disciplina_nome, mpo.nome as municipio_polo "
						+ " from comum.unidade u, ensino.componente_curricular cc,    "
						+ "	ensino.matricula_componente mco, discente d, curso c,   "
						+ "	graduacao.discente_graduacao dg left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "	left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade),"
						+ " graduacao.matriz_curricular mcu,   "
						+ "	ensino.turno turn, comum.pessoa p, ensino.situacao_matricula sm,   "
						+ "	status_discente sd, ensino.componente_curricular_detalhes ccd "
						+ " where p.id_pessoa = d.id_pessoa  "
						+ "	and  d.id_curso = c.id_curso  "
						+ "	and  d.id_discente = mco.id_discente  "
						+ "	and  d.id_discente = dg.id_discente_graduacao  "
						+ "	and  d.status = sd.status  "
						+ "	and  c.id_curso = mcu.id_curso  "
						+ "	and  c.id_unidade = u.id_unidade "
						+ "	and  mcu.id_turno = turn.id_turno  "
						+ "	and  dg.id_matriz_curricular = mcu.id_matriz_curricular  "
						+ "	and  mco.id_componente_curricular = cc.id_disciplina "
						+ "    and  ccd.id_componente_detalhes  = mco.id_componente_detalhes "
						+ "	and  mco.id_situacao_matricula = sm.id_situacao_matricula ");

		if (discente != null && discente.getId() != 0)
			sqlconsulta.append("		and d.id_discente= " + discente.getId());

		if (disciplina != null && disciplina.getId() != 0)
			sqlconsulta
					.append("		and cc.id_disciplina = " + disciplina.getId());

		sqlconsulta
				.append(" order by mco.ano, mco.periodo, c.nome, c.id_curso, turn.id_turno, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o relatório de alunos com trancamento em um
	 * determinado componente curricular.
	 * 
	 * @param disciplina
	 * @param ano
	 * @param periodo
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaTrancamentosComponente(
			ComponenteCurricular disciplina, Integer ano, Integer periodo,
			Unidade programa) throws DAOException {
		
		String sql =
				"SELECT \n" +
				"	cc.codigo           AS disciplina_codigo, \n" +
				"	det.nome            AS disciplina_nome, \n" +
				"	d.matricula, \n" +
				"	p.nome              AS nome_aluno, \n" +
				"	COALESCE(c.nome,'-') AS nome_curso, \n" +
				"	CASE \n" +
				"		WHEN d.nivel = '" + NivelEnsino.DOUTORADO + "' THEN 'DOUTORADO' \n" +
				"		WHEN d.nivel = '" + NivelEnsino.MESTRADO +  "' THEN 'MESTRADO' \n" +
				"		ELSE '-' " +
				"	END AS nivel, \n" +
				"	CASE \n" +
				"		WHEN d.tipo = " + Discente.REGULAR +  " THEN 'REGULAR' \n" +
				"		WHEN d.tipo = " + Discente.ESPECIAL + " THEN 'ESPECIAL' \n" +
				"		ELSE '-' \n" +
				"	END AS tipo \n" +
				"FROM \n" +
				"	ensino.matricula_componente           m, \n" +
				"	ensino.componente_curricular          cc, \n" +
				"	ensino.componente_curricular_detalhes det, \n" +
				"	discente                              d \n" +
				"	LEFT JOIN curso                       c USING (id_curso), \n" +
				"	comum.pessoa                                p \n" +
				"WHERE \n" +
				"	m.id_componente_curricular = cc.id_disciplina \n" +
				"	AND cc.id_detalhe = det.id_componente_detalhes \n" +
				"	AND m.id_discente = d.id_discente \n" +
				"	AND d.id_pessoa = p.id_pessoa \n" +
				"	AND m.id_situacao_matricula = " + SituacaoMatricula.TRANCADO.getId();

		if (ano != null)
			sql += "	AND m.ano = " + ano + " \n";
		if (periodo != null)
			sql += "	AND m.periodo = " + periodo + " \n";

		if (disciplina != null && disciplina.getId() != 0)
			sql += "	AND cc.id_disciplina = " + disciplina.getId() + " \n";
		else if (programa != null && programa.getId() != 0)
			sql += "	AND cc.id_unidade = " + programa.getId() + " \n";

		sql += "ORDER BY det.nome, c.nome, p.nome \n";

		List<Map<String, Object>> result;

		try {
			result = executeSql(sql);
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna a relação de alunos matriculados em disciplina com
	 * carga horária de estágio por disciplina/curso/centro/cpf/data de
	 * nascimento.
	 * 
	 * @param centro
	 * @param curso
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findListaAlunoMatriculadosEstagio(
			Unidade centro, Curso curso) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select cc.codigo as disciplina_codigo, ccd.nome as disciplina_nome, "
						+ "       c.nome as curso_nome, u.sigla as centro, p.nome as nome_aluno,d.matricula, "
						+ " 	    p.cpf_cnpj as cpf, to_char(p.data_nascimento,'DD/MM/YYYY') as data_nascimento, c.id_curso, tur.id_turno, "
						+ "       tur.sigla as sigla_turno, mpo.nome as municipio_polo"
						+ " from comum.pessoa p, discente d, graduacao.discente_graduacao dg "
						+ "  left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "	 left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade),"
						+ "     ensino.matricula_componente mc, ensino.turma t, ensino.turno tur, "
						+ "     ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd, "
						+ "     comum.unidade u, curso c, graduacao.matriz_curricular mcu "
						+ " where p.id_pessoa = d.id_pessoa  "
						+ " and   c.id_curso = d.id_curso  "
						+ " and   c.id_curso = mcu.id_curso "
						+ " and   mcu.id_turno = tur.id_turno "
						+ " and   c.id_unidade = u.id_unidade  "
						+ " and   d.id_discente = mc.id_discente "
						+ " and   d.id_discente = dg.id_discente_graduacao  "
						+ " and   dg.id_matriz_curricular = mcu.id_matriz_curricular "
						+ " and   mc.id_turma = t.id_turma  "
						+ " and   t.id_disciplina = cc.id_disciplina  "
						+ " and   cc.id_detalhe = ccd.id_componente_detalhes  "
						+ " and   ccd.ch_estagio > 0  "
						+ " and   mc.id_situacao_matricula in (1,2)  ");

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append("		and u.id_unidade= " + centro.getId());

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("		and c.id_curso = " + curso.getId());

		sqlconsulta
				.append(" order by  u.sigla,c.nome,c.id_curso,tur.id_turno,ccd.nome, p.nome  ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna a relação de alunos por Curso/Centro com prazo máximo
	 * de conclusão em um ano semestre por carga horária total de curso por
	 * carga horária integralizada.
	 * 
	 * @param centro
	 * @param curso
	 * @param prazoConclusao
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunoPrazoConclusaoCh(
			Unidade centro, Curso curso, int prazoConclusao) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro, c.nome as curso_nome, d.matricula, d.ano_ingresso,  "
						+ "  d.periodo_ingresso, p.nome as aluno_nome, crr.codigo, crr.ch_total_minima as total_curso,   "
						+ "  dg.ch_total_integralizada, d.prazo_conclusao, t.sigla as sigla_turno, c.id_curso, dg.ch_total_pendente,   "
						+ "  mpo.nome as municipio_polo from comum.pessoa p, discente d, comum.unidade u, curso c,   "
						+ "     graduacao.discente_graduacao dg	left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "		left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ "	, graduacao.curriculo crr, graduacao.matriz_curricular mcu, ensino.turno t "
						+ " where p.id_pessoa = d.id_pessoa  "
						+ " and  d.id_curso = c.id_curso   "
						+ " and  c.id_unidade = u.id_unidade   "
						+ " and  d.id_curriculo = crr.id_curriculo  "
						+ " and  d.id_discente = dg.id_discente_graduacao "
						+ " and  c.id_curso = mcu.id_curso "
						+ " and  dg.id_matriz_curricular = mcu.id_matriz_curricular "
						+ " and  mcu.id_turno = t.id_turno "
						+ " and  d.status in (1,4,5,8)");

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append("		and u.id_unidade= " + centro.getId());

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("		and c.id_curso = " + curso.getId());

		if (prazoConclusao != 0)
			sqlconsulta.append("		and d.prazo_conclusao = " + prazoConclusao);

		sqlconsulta
				.append(" order by u.sigla,c.id_curso,t.id_turno,crr.codigo, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o quantitativo de alunos graduandos por curso.
	 * 
	 * @param centro
	 * @param curso
	 * @param filtroCurso
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoAlunoGraduando(
			Unidade centro, Curso curso, int filtroCurso) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro, c.nome as curso_nome, m.nome as municipio_curso, t.descricao as turno_curso,  "
						+ " 	   ga.descricao as modalidade_curso, h.nome as habilitacao_curso, c.id_curso,t.id_turno , m.id_municipio,    "
						+ " 	   ga.id_grau_academico, h.id_habilitacao ,max_semestre.ano_semestre_maximo,  "
						+ " 	   count(d.id_discente) as qtd_discente    "
						+ " from comum.unidade u, curso c, discente d, graduacao.discente_graduacao dg,  "
						+ "  graduacao.matriz_curricular mc left outer join graduacao.habilitacao h using (id_habilitacao),   "
						+ "  ensino.turno t, ensino.grau_academico ga, comum.municipio m,   "
						+ "  (select mct.id_discente, max(ano*10+periodo) as ano_semestre_maximo from ensino.matricula_componente mct  "
						+ "  group by mct.id_discente) as max_semestre  "
						+ "  where u.id_unidade = c.id_unidade     "
						+ "  and   c.id_curso = d.id_curso     "
						+ "  and   d.id_discente = dg.id_discente_graduacao  "
						+ "  and   mc.id_matriz_curricular = dg.id_matriz_curricular  "
						+ "  and   ga.id_grau_academico = mc.id_grau_academico  "
						+ "  and   t.id_turno = mc.id_turno  "
						+ "  and   c.id_municipio = m.id_municipio  "
						+ "  and   max_semestre.id_discente = dg.id_discente_graduacao  "
						+ "  and   d.status = " + StatusDiscente.GRADUANDO);

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append("		and u.id_unidade= " + centro.getId());

		sqlconsulta.append(definirFiltroCurso(filtroCurso, curso));

		sqlconsulta
				.append("  group by u.sigla, c.nome, m.nome, t.descricao ,  "
						+ "	   ga.descricao, h.nome ,c.id_curso,t.id_turno , m.id_municipio,    "
						+ " 	   ga.id_grau_academico, h.id_habilitacao,max_semestre.ano_semestre_maximo  "
						+ "  order by u.sigla,c.id_curso,t.id_turno , m.id_municipio,    "
						+ " 	   ga.id_grau_academico, h.id_habilitacao,max_semestre.ano_semestre_maximo ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna a relação de alunos por cidade de residência.
	 * 
	 * @param centro
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunoCidadeResidencia(
			Unidade centro, Curso curso) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro, c.nome as curso_nome, d.matricula, p.nome as nome_aluno,"
						+ "   upper(m.nome) as cidade, m.id_municipio, mpo.nome as municipio_polo  "
						+ " from comum.unidade u, curso c, discente d, graduacao.discente_graduacao dg  "
						+ " left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ "  	  ,comum.pessoa p, comum.endereco e, comum.municipio m   "
						+ " where u.id_unidade = c.id_unidade   "
						+ " and   c.id_curso = d.id_curso   "
						+ " and   dg.id_discente_graduacao = d.id_discente   "
						+ " and   d.id_pessoa = p.id_pessoa   "
						+ " and   p.id_endereco_contato = e.id_endereco "
						+ " and   e.id_municipio = m.id_municipio "
						+ " and   d.status in (1,4,5,8) ");

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append("		and u.id_unidade= " + centro.getId());

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("		and c.id_curso = " + curso.getId());

		sqlconsulta.append(" order by u.sigla, c.id_curso, m.nome, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna a Relação de aluno/matrícula por curso-turno-cidade
	 * por carga horária total exigida por carga horária disciplina-atividade
	 * obrigatória exigida por carga horária disciplina-atividade obrigatória
	 * integralizada por ch disciplina-atividade complementar exigida por ch
	 * disciplina-atividade complementar integralizado por % total de
	 * integralização pelo produto da ch disciplina-atividade exigida -
	 * (subtraído) carga horária disciplina atividade complementar cumprida.
	 * 
	 * @param centro
	 * @param curso
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findListaAlunoChDetalhada(
			Unidade centro, Curso curso) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro,c.id_curso, c.nome as curso_nome, d.ano_ingresso, d.periodo_ingresso, "
						+ "	   d.matricula, p.nome as aluno_nome, cr.codigo as curriculo_codigo, cr.ch_nao_atividade_obrigatoria, "
						+ "       cr.ch_optativas_minima, cr.ch_total_minima, dg.ch_nao_atividade_obrig_integ, h.id_habilitacao,  "
						+ "	   dg.ch_optativa_integralizada, dg.ch_total_integralizada, dg.ch_nao_atividade_obrig_pendente, "
						+ "       h.nome as habilitacao , t.id_turno, t.sigla as sigla_turno, sd.descricao as status_aluno, "
						+ "	 mpo.nome as municipio_polo from comum.unidade u, curso c, discente d, graduacao.discente_graduacao dg "
						+ " 	left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "		left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ "      ,comum.pessoa p, graduacao.curriculo cr, graduacao.matriz_curricular mcu left outer join graduacao.habilitacao h on"
						+ "(mcu.id_habilitacao = h.id_habilitacao), ensino.turno t, status_discente sd "
						+ " where u.id_unidade = c.id_unidade   "
						+ "  and  c.id_curso = d.id_curso    "
						+ "  and  dg.id_discente_graduacao = d.id_discente   "
						+ "  and  d.id_curriculo = cr.id_curriculo  "
						+ "  and  d.id_pessoa = p.id_pessoa "
						+ "  and  dg.id_matriz_curricular = mcu.id_matriz_curricular "
						+ "  and  mcu.id_turno = t.id_turno     "
						+ "  and  d.status = sd.status "
						+ "  and  d.status in (1,4,5,8)");

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append("		and u.id_unidade= " + centro.getId());

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append("		and c.id_curso = " + curso.getId());

		sqlconsulta
				.append(" order by u.sigla, c.id_curso , t.id_turno, h.id_habilitacao, cr.codigo, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	
	/**
	 * Retorna a Lista de alunos matriculados em uma determinada
	 * atividade (Graduação).
	 * 
	 * @param componente
	 * @param ano
	 * @param periodo
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunoMatriculadoAtividadeGraduacao(
			ComponenteCurricular componente, int ano, int periodo,
			Curso curso, Collection<Character> nivel) throws DAOException {
		

		StringBuilder sql = new StringBuilder("select u.sigla as centro, c.nome as curso_nome, c.id_curso as id_curso,"+  
		" ga.id_grau_academico, dg.ira, ga.descricao as grau_academico_aluno, h.id_habilitacao, h.nome as habilitacao_aluno,"+  
		" d.matricula,  p.nome as nome_aluno, d.ano_ingresso, d.periodo_ingresso, mc.ano, mc.periodo, ccd.codigo as disciplina_codigo,"+ 
		" ccd.nome as disciplina_nome, t.sigla as turno_sigla, t.id_turno, c.nivel as nivel_curso, mpo.nome as municipio_polo"+ 
		" , coalesce(p2.nome, p3.nome) as nome_docente, ia.valor as mc from ensino.matricula_componente mc  "+
		" inner join ensino.componente_curricular cc on (mc.id_componente_curricular = cc.id_disciplina)"+  
		" inner join ensino.componente_curricular_detalhes ccd on (ccd.id_componente_detalhes = cc.id_detalhe)"+  
		" inner join discente d on (mc.id_discente = d.id_discente)"+
		" inner join comum.pessoa p on (d.id_pessoa = p.id_pessoa)"+
		" inner join curso c on (c.id_curso = d.id_curso)"+
		" inner join comum.unidade u on (u.id_unidade = c.id_unidade)"+
		" left join graduacao.discente_graduacao dg on( d.id_discente = dg.id_discente_graduacao )"+ 
		" left join ead.polo po on (dg.id_polo = po.id_polo)" +
		" left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
		" left join graduacao.matriz_curricular mcu using ( id_matriz_curricular ) "+
		" left join ensino.turno t on(t.id_turno = mcu.id_turno) "+
		" left join ensino.grau_academico ga on(ga.id_grau_academico = mcu.id_grau_academico)"+ 
		" left join graduacao.habilitacao h on (mcu.id_habilitacao = h.id_habilitacao)"+
		" left join ensino.registro_atividade ra on (ra.id_matricula_componente = mc.id_matricula_componente)" +
		" left join ensino.orientacao_atividade oa on (oa.id_registro_atividade = ra.id_registro_atividade)" +
		" left join rh.servidor s on (s.id_servidor = oa.id_servidor)" +
		" left join comum.pessoa p2 on (p2.id_pessoa = s.id_pessoa)" +
		" left join ensino.docente_externo de on (id_docente_externo = id_orientador_externo)" +
		" left join comum.pessoa p3 on (de.id_pessoa = p3.id_pessoa)" +
		" left join ensino.indice_academico_discente ia  on (ia.id_discente = d.id_discente and ia.id_indice_academico = 1)"+		
		" where mc.id_situacao_matricula = " + SituacaoMatricula.MATRICULADO.getId() +
		" and cc.id_tipo_componente = "+ TipoComponenteCurricular.ATIVIDADE+
		" and d.status IN " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())+
		" and c.nivel IN " + UFRNUtils.gerarStringIn(nivel));
		
		if (curso != null && curso.getId() != 0)
			sql.append(" and c.id_curso = " + curso.getId());

		if (componente != null && componente.getId() != 0)
			sql.append(" and mc.id_componente_curricular = " + componente.getId());
						
		if (ano != 0 && periodo != 0){
			sql.append(" and mc.ano = " + ano);
			sql.append(" and mc.periodo= " + periodo);
		}
		
		sql.append(" order by u.sigla, ccd.codigo, ccd.nome, c.id_curso, c.nome, ga.descricao, h.nome, t.id_turno, p.nome, mc.ano, mc.periodo");

		List<Map<String, Object>> result;
		
		try {
			result = executeSql(sql.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
		
	/**
	 * Retorna a Lista de alunos matriculados em uma determinada
	 * atividade (Stricto Sensu).
	 * 
	 * @param componente
	 * @param ano
	 * @param periodo
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunoMatriculadoAtividadeStricto(
			ComponenteCurricular componente, int ano, int periodo,
			Unidade unidade, Collection<Character> nivel) throws DAOException {
		
		//MATRICULADOS E RENOVADOS NO ANO-PERIODO PASSADO POR PARAMETRO
		//(1) APENAS OS MATRICULADOS NO ANO PERIODO PASSADO
		StringBuilder sql = new StringBuilder("(");
		sql.append(" SELECT DISTINCT u.sigla as centro, c.nome as curso_nome, c.id_curso as id_curso, " +
				" ga.id_grau_academico, dg.ira, ga.descricao as grau_academico_aluno, h.id_habilitacao, h.nome as habilitacao_aluno, " +
				" d.matricula,  p.nome as nome_aluno, d.ano_ingresso, d.periodo_ingresso, mc.ano, mc.periodo, ccd.codigo as disciplina_codigo," +
				" ccd.nome as disciplina_nome, t.sigla as sigla_turno, t.id_turno, p2.nome as nome_docente, c.nivel as nivel_curso " +
				" , mpo.nome as municipio_polo from ensino.matricula_componente mc " +
				" join ensino.componente_curricular cc on (mc.id_componente_curricular = cc.id_disciplina) " +
				" inner join ensino.componente_curricular_detalhes ccd on (ccd.id_componente_detalhes = cc.id_detalhe) " +
				" join discente d using(id_discente) " +
				" inner join comum.pessoa p on (p.id_pessoa = d.id_pessoa) " +
				" inner join curso c on (d.id_curso = c.id_curso) " +
				" inner join comum.unidade u on (u.id_unidade = c.id_unidade)" +
				" inner join graduacao.orientacao_academica oa on (oa.id_discente = d.id_discente)" +
				" inner join rh.servidor s on (s.id_servidor = oa.id_servidor)" +
				" inner join comum.pessoa p2 on (p2.id_pessoa = s.id_pessoa)" +
				" left join graduacao.discente_graduacao dg on( d.id_discente = dg.id_discente_graduacao ) " +
				" left join ead.polo po on (dg.id_polo = po.id_polo)" +
				" left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
				" left join graduacao.matriz_curricular mcu using ( id_matriz_curricular ) " +
				" left join ensino.turno t on(t.id_turno = mcu.id_turno) " +
				" left join ensino.grau_academico ga on(ga.id_grau_academico = mcu.id_grau_academico) " +
				" left join graduacao.habilitacao h on (mcu.id_habilitacao = h.id_habilitacao) " +
				" where mc.id_situacao_matricula = " + SituacaoMatricula.MATRICULADO.getId() +
				" and cc.id_tipo_componente = "+ TipoComponenteCurricular.ATIVIDADE +
				" and d.status IN " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())+
				" and c.nivel IN " + UFRNUtils.gerarStringIn(nivel)+
				" and oa.tipoorientacao = '"+ OrientacaoAcademica.ORIENTADOR+"'"+
				" and oa.cancelado = falseValue() AND oa.fim IS NULL");

				if (componente != null && componente.getId() != 0)
					sql.append(" and mc.id_componente_curricular = " + componente.getId());
				
				if (!isEmpty(unidade))
					sql.append(" and cc.id_unidade= " + unidade.getId());
		
				if (ano != 0 && periodo != 0)
					sql.append(" and mc.ano = " + ano+" and mc.periodo= " + periodo);
				
				sql.append(" ORDER BY u.sigla, ccd.codigo, ccd.nome, c.id_curso, c.nome, ga.descricao, h.nome, t.id_turno, p.nome, mc.ano, mc.periodo");
				
				sql.append(" )");
				
				//(1) UNIDO COM (2)
				sql.append(" UNION (");
				
				//(2)APENAS OS RENOVADOS NO ANO-PERIODO PASSADO POR PARAMETRO
				sql.append(" SELECT DISTINCT u.sigla as centro, c.nome as curso_nome, c.id_curso as id_curso," +
						" ga.id_grau_academico, dg.ira, ga.descricao as grau_academico_aluno, h.id_habilitacao," +
						" h.nome as habilitacao_aluno, d.matricula,  p.nome as nome_aluno, d.ano_ingresso," +
						" d.periodo_ingresso, mc.ano, mc.periodo, ccd.codigo as disciplina_codigo," +
						" ccd.nome as disciplina_nome, t.sigla as sigla_turno, t.id_turno, p2.nome as nome_docente, c.nivel as nivel_curso" +
						" , mpo.nome as municipio_polo	FROM ensino.matricula_componente mc" +
						" INNER JOIN ensino.componente_curricular cc ON (mc.id_componente_curricular = cc.id_disciplina)" +  
						" INNER JOIN ensino.componente_curricular_detalhes ccd ON (ccd.id_componente_detalhes = cc.id_detalhe)" + 
						" INNER JOIN discente d ON (d.id_discente = mc.id_discente)" +
						" INNER JOIN comum.pessoa p ON (p.id_pessoa = d.id_pessoa)" +
						" INNER JOIN curso c ON (d.id_curso = c.id_curso)" +
						" INNER JOIN comum.unidade u ON (u.id_unidade = c.id_unidade)" +
						" INNER JOIN graduacao.orientacao_academica oa ON (oa.id_discente = d.id_discente)" +
						" INNER JOIN rh.servidor s ON (s.id_servidor = oa.id_servidor)" +
						" INNER JOIN comum.pessoa p2 ON (p2.id_pessoa = s.id_pessoa)" +
						" LEFT JOIN graduacao.discente_graduacao dg ON ( d.id_discente = dg.id_discente_graduacao )" +
						" LEFT JOIN ead.polo po on (dg.id_polo = po.id_polo)" +
						" LEFT JOIN	comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
						" LEFT JOIN graduacao.matriz_curricular mcu using ( id_matriz_curricular )" +
						" LEFT JOIN ensino.turno t on(t.id_turno = mcu.id_turno) " +
						" LEFT JOIN ensino.grau_academico ga on(ga.id_grau_academico = mcu.id_grau_academico)" + 
						" LEFT JOIN graduacao.habilitacao h on (mcu.id_habilitacao = h.id_habilitacao) " +
						" LEFT JOIN stricto_sensu.renovacao_atividade_pos rap ON ( mc.id_matricula_componente = rap.id_matricula_componente )" +
						" LEFT JOIN graduacao.solicitacao_matricula sol ON (sol.id_solicitacao_matricula = rap.id_solicitacao_matricula)" +
						" WHERE mc.id_situacao_matricula  = " + SituacaoMatricula.MATRICULADO.getId() +
						" AND cc.id_tipo_componente = "+ TipoComponenteCurricular.ATIVIDADE +
						" AND d.status IN " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos()) +
						" AND c.nivel IN " + UFRNUtils.gerarStringIn(nivel) +
						" AND (rap.id_matricula_componente = mc.id_matricula_componente OR sol.id_matricula_gerada = mc.id_matricula_componente)" +
						" AND rap.ativo = trueValue()"+ 
						" AND oa.tipoorientacao = '"+ OrientacaoAcademica.ORIENTADOR+"'"+
						" AND oa.cancelado = falseValue() AND oa.fim IS NULL");
					
				if (componente != null && componente.getId() != 0)
					sql.append(" AND mc.id_componente_curricular = " + componente.getId());
				
				if (!isEmpty(unidade))
					sql.append(" AND cc.id_unidade= " + unidade.getId());
				
				if (ano != 0 && periodo != 0)
					sql.append(" AND( (rap.ano = "+ano+" AND rap.periodo = "+periodo+" ) OR (sol.ano = "+ano+" AND sol.periodo = "+periodo+") )");
				
				sql.append(" ORDER BY u.sigla, ccd.codigo, ccd.nome, c.id_curso, c.nome, ga.descricao, h.nome, t.id_turno, p.nome, mc.ano, mc.periodo");

				sql.append(" )");
				
		List<Map<String, Object>> result;
		
		try {
			result = executeSql(sql.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
	
	
	
	/**
	 * Retorna a Lista de alunos matriculados em uma determinada
	 * atividade mas cuja atividade não foi renovada.
	 * 
	 * @param componente
	 * @param ano
	 * @param periodo
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findAlunosMatriculadosEmAtividadesNaoRenovadas(
			ComponenteCurricular componente, int ano, int periodo,
			Unidade unidade, Collection<Character> nivel) throws DAOException {

		// OS MATRICULADOS TIRANDO OS QUE RENOVARAM, OU SEJA, MATRICULADOS E NÃO RENOVADOS:
		//(1) Todos os matriculados
		StringBuilder sql = new StringBuilder("select u.sigla as centro, c.nome as curso_nome, c.id_curso as id_curso, " +
				" ga.id_grau_academico, dg.ira, ga.descricao as grau_academico_aluno, h.id_habilitacao, h.nome as habilitacao_aluno, " +
				" d.matricula,  p.nome as nome_aluno, d.ano_ingresso, d.periodo_ingresso, mc.ano, mc.periodo, ccd.codigo as disciplina_codigo," +
				" ccd.nome as disciplina_nome, t.sigla as sigla_turno, t.id_turno, pessoa2.nome as nome_docente, c.nivel as nivel_curso " +
				" , mpo.nome as municipio_polo from ensino.matricula_componente mc " +
				" join ensino.componente_curricular cc on (mc.id_componente_curricular = cc.id_disciplina) " +
				" inner join ensino.componente_curricular_detalhes ccd on (ccd.id_componente_detalhes = cc.id_detalhe) " +
				" join discente d using(id_discente) " +
				" inner join comum.pessoa p on (p.id_pessoa = d.id_pessoa) " +
				" inner join curso c on (d.id_curso = c.id_curso) " +
				" inner join comum.unidade u on (u.id_unidade = c.id_unidade) " +
				" left join ensino.registro_atividade ra on (ra.id_registro_atividade = mc.id_registro_atividade) " +
				" left join rh.servidor servidor on (servidor.id_servidor = ra.id_coordenador) " +
				" left join comum.pessoa pessoa2 on (pessoa2.id_pessoa = servidor.id_pessoa) " +
				" left join graduacao.discente_graduacao dg on( d.id_discente = dg.id_discente_graduacao ) " +
				" left join ead.polo po on (dg.id_polo = po.id_polo)" +
				" left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
				" left join graduacao.matriz_curricular mcu using ( id_matriz_curricular ) " +
				" left join ensino.turno t on(t.id_turno = mcu.id_turno) " +
				" left join ensino.grau_academico ga on(ga.id_grau_academico = mcu.id_grau_academico) " +
				" left join graduacao.habilitacao h on (mcu.id_habilitacao = h.id_habilitacao) " +
				" where mc.id_situacao_matricula = " + SituacaoMatricula.MATRICULADO.getId() +
				" and cc.id_tipo_componente = "+ TipoComponenteCurricular.ATIVIDADE +
				" and d.status IN " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())+
				" and c.nivel IN " + UFRNUtils.gerarStringIn(nivel)+
				" and (mc.ano !="+ano+" or mc.periodo != "+periodo+")");
		
				if (componente != null && componente.getId() != 0)
					sql.append(" and mc.id_componente_curricular = " + componente.getId());

				if (!isEmpty(unidade))
					sql.append(" and cc.id_unidade= " + unidade.getId());
				//(2)Exceto os que renovaram.
				//(2.1) Discente solicitou a renovação e foi atendido
				sql.append(" and d.matricula NOT IN ("+
						" select d.matricula from discente d"+
						" inner join graduacao.solicitacao_matricula sm on (sm.id_discente = d.id_discente)"+
						" inner join ensino.matricula_componente mc on (mc.id_discente = d.id_discente)"+
						" join ensino.componente_curricular cc on (mc.id_componente_curricular = cc.id_disciplina)"+
						" where  ( (sm.status = "+SolicitacaoMatricula.ATENDIDA+"  and sm.ano = "+ano+" and sm.periodo ="+periodo+")");
				if (!isEmpty(unidade))
						sql.append(" and cc.id_unidade= " + unidade.getId());
				
				sql.append(" and mc.id_situacao_matricula = " + SituacaoMatricula.MATRICULADO.getId() +
					" and cc.id_tipo_componente = "+ TipoComponenteCurricular.ATIVIDADE +
					" and d.status IN " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())+
					") )");
				
				//(2.2) A renovação foi realizada diretamente pela coordenção
				sql.append(" and d.matricula NOT IN ("+
						" select d.matricula from discente d"+
						" inner join ensino.matricula_componente mc on (mc.id_discente = d.id_discente) "+
						" inner join stricto_sensu.renovacao_atividade_pos rap on (mc.id_matricula_componente = rap.id_matricula_componente)"+						
						" inner join ensino.componente_curricular cc on (mc.id_componente_curricular = cc.id_disciplina)"+
						" where  rap.ano = "+ano+" and rap.periodo ="+periodo+" and  rap.ativo = trueValue()");
				
				if (!isEmpty(unidade))
						sql.append(" and cc.id_unidade= " + unidade.getId());
					
				sql.append(" and mc.id_situacao_matricula = " + SituacaoMatricula.MATRICULADO.getId() +
						" and cc.id_tipo_componente = "+ TipoComponenteCurricular.ATIVIDADE +
						" and d.status IN " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())+
						") ");
						 
				//Resultando apenas nos matriculados e não renovados
				sql.append(" order by u.sigla, ccd.codigo, ccd.nome, c.id_curso, c.nome, ga.descricao, h.nome, t.id_turno, p.nome, mc.ano, mc.periodo");

		List<Map<String, Object>> result;
		
		try {
			result = executeSql(sql.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	
	

	/**
	 * Retorna a Lista de aluno com prazo de conclusão no ano
	 * semestre atual.
	 * 
	 * @param anoSemestreAtual
	 * @param centro
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunoPrazoSemestreAtual(
			int anoSemestreAtual, Unidade centro, Curso curso) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"  select u.sigla as centro, c.id_curso, c.nome as curso_nome, "
						+ " ga.id_grau_academico, ga.descricao as modalidade_nome, h.id_habilitacao, h.nome as habilitacao_nome ,  "
						+ " d.matricula,  p.nome as discente_nome, t.id_turno, dg.ch_optativa_pendente,  "
						+ " (dg.ch_nao_atividade_obrig_pendente + dg.ch_atividade_obrig_pendente) as ch_obrig_pendente, "
						+ " t.sigla as sigla_turno, sd.descricao as status_discente, mu.nome as municipio, mpo.nome as municipio_polo "
						+ " from comum.unidade u, curso c, discente d, graduacao.discente_graduacao dg"
						+ "  left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ "  left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ " ,comum.pessoa p, graduacao.matriz_curricular mcu left outer join  graduacao.habilitacao h on (mcu.id_habilitacao = h.id_habilitacao) , "
						+ " ensino.turno t, ensino.grau_academico ga, status_discente sd, comum.municipio mu  "
						+ " where u.id_unidade = c.id_unidade   "
						+ "  and c.id_curso= d.id_curso    "
						+ "  and c.id_municipio = mu.id_municipio "
						+ "  and d.id_pessoa = p.id_pessoa    "
						+ "  and d.status in (1,4,5,8)  "
						+ "  and d.id_discente = dg.id_discente_graduacao "
						+ "  and dg.id_matriz_curricular = mcu.id_matriz_curricular"
						+ "  and ga.id_grau_academico = mcu.id_grau_academico"
						+ "  and mcu.id_turno = t.id_turno"
						+ "  and d.status = sd.status");

		if (anoSemestreAtual != 0)
			sqlconsulta.append(" and   d.prazo_conclusao=" + anoSemestreAtual);

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append(" and   c.id_curso=" + curso.getId());

		if (centro != null && centro.getId() != 0)
			sqlconsulta.append(" and   u.id_unidade=" + centro.getId());

		sqlconsulta
				.append(" order by u.sigla, c.nome, ga.descricao, h.nome, t.sigla, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna a relação de alunos laureados por curso.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunoLaureados(int ano, int periodo, int indiceAcademico, double valorMinimo) throws DAOException {
		// sql de consulta

		int anoPeriodoSeguinte = DiscenteHelper.somaSemestres(ano, periodo, +1);
		int anoSeguinte = anoPeriodoSeguinte / 10;
		int periodoSeguinte = anoPeriodoSeguinte - (anoSeguinte * 10);

		StringBuilder sqlconsulta = new StringBuilder(
				" select u.sigla as centro, c.id_curso, c.nome as curso_nome, ga.descricao as descricao_grau, "
						+ " d.matricula, p.nome as discente_nome, iad.valor as IEAN, m.id_municipio, m.nome as municipio_nome, "
						+ " sd.descricao as status_discente, mpo.nome as municipio_polo    "
						+ " from comum.unidade u" 
						+ "  INNER JOIN curso c ON u.id_unidade = c.id_unidade " 
						+ "  INNER JOIN discente d ON c.id_curso= d.id_curso  "
						+ "  INNER JOIN graduacao.discente_graduacao dgd ON d.id_discente = dgd.id_discente_graduacao "
						+ "  LEFT JOIN ead.polo po on (dgd.id_polo = po.id_polo)"
						+ "  LEFT JOIN comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ "  INNER JOIN graduacao.matriz_curricular mc using (id_matriz_curricular)"
						+ "  INNER JOIN ensino.grau_academico ga on (ga.id_grau_academico = mc.id_grau_academico)"
						+ "  INNER JOIN comum.pessoa p ON d.id_pessoa = p.id_pessoa  "
						+ "  INNER JOIN comum.municipio m ON c.id_municipio = m.id_municipio "       
						+ "  INNER JOIN status_discente sd ON d.status = sd.status "
						+ "  INNER JOIN ensino.indice_academico_discente iad ON iad.id_discente = d.id_discente "
						+ "  INNER JOIN (select cur.id_unidade, cur.id_curso, max(iadis.valor) as iean_max       "
						+ "        		  from graduacao.discente_graduacao dg " 
						+ "				    INNER JOIN discente dis 	ON dis.id_discente = dg.id_discente_graduacao "
						+ "    				INNER JOIN curso cur 		ON dis.id_curso = cur.id_curso "
						+ "    				INNER JOIN ensino.indice_academico_discente iadis ON iadis.id_discente = dis.id_discente "
						+ "        		  where iadis.id_indice_academico = " + indiceAcademico 
						+ "        		  and  iadis.valor >= " + valorMinimo
						+ "               and (dg.cola_grau is null or dg.cola_grau = true)" // somente os discentes que colam grau 
						+ "        		  and  dis.status in " + UFRNUtils.gerarStringIn( new int[]{ StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO } )  // Status (CONCLUIDO OU GRADUANDO)
						+ "        		  and  exists ( select mco.id_discente from ensino.matricula_componente mco  "
						+ "     		  		  		where mco.id_discente = dis.id_discente  ");
		sqlconsulta.append("             		  		and   mco.periodo=" + periodo);
		sqlconsulta.append("              		  		and   mco.id_situacao_matricula <> "
				+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mco.id_discente  ");
		sqlconsulta
				.append("               having max(mco.ano)="
						+ ano
						+ ")"
						+ "       and  not exists ( select mco.id_discente from ensino.matricula_componente mco  "
						+ "     		  where mco.id_discente = dis.id_discente  ");
		sqlconsulta.append("               and   mco.periodo="
				+ periodoSeguinte);
		sqlconsulta.append("               and   mco.id_situacao_matricula <> "
				+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mco.id_discente  ");
		sqlconsulta.append("               having max(mco.ano)=" + anoSeguinte
				+ ")" + "   ");
		sqlconsulta
				.append("       group by cur.id_unidade, cur.id_curso ) " 
						+ " as maxiean   ON  maxiean.id_curso = c.id_curso  AND  maxiean.id_unidade = c.id_unidade      "
						+ "  where iad.id_indice_academico = " + indiceAcademico 
						+ "  and   mc.permite_colacao_grau = true" // lista as matrizes que colam grau
						+ "  and   maxiean.iean_max = iad.valor      "
						+ "  and   iad.valor >= " + valorMinimo
						+ "  and   d.status in " + UFRNUtils.gerarStringIn( new int[]{ StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO } )
						+ "  and   exists ( select mcop.id_discente from ensino.matricula_componente mcop   "
						+ "      		  where mcop.id_discente = d.id_discente   ");
		sqlconsulta.append("               and   mcop.periodo=" + periodo);
		sqlconsulta
				.append("               and   mcop.id_situacao_matricula <> "
						+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mcop.id_discente  ");
		sqlconsulta
				.append("               having max(mcop.ano)="
						+ ano
						+ ") "
						+ "  and  not exists ( select mcop.id_discente from ensino.matricula_componente mcop   "
						+ "      		  where mcop.id_discente = d.id_discente   ");
		sqlconsulta.append("               and   mcop.periodo="
				+ periodoSeguinte);
		sqlconsulta
				.append("               and   mcop.id_situacao_matricula <> "
						+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mcop.id_discente  ");
		sqlconsulta.append("               having max(mcop.ano)=" + anoSeguinte
				+ ") " + "  ");

		sqlconsulta.append(" order by c.nome, u.sigla, m.nome, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
	
	
	/**
	 * Retorna a relação Discentes com IEAN menor que 600,
	 * mas que tiveram maior IEAN do curso.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findAlunoNaoAtingiuIndiceLaureadoMinimo(Integer ano, Integer periodo, int indiceAcademico, double valorMinimo) throws DAOException {
		// Lógica:
		// Relação dos cursos que tiveram discentes que atingiram ou não o IEAN mínimo (se não atingiu, relaciona o discente que chegou mais próximo) [A]
		// Relação dos cursos que tiveram discentes que atingiram o IEAN mínimo [B]
		// Realizando [A] - [B] teremos a relação de cursos em que nenhum aluno atingiu o indice mínimo (relacionando aquele de maior IEAN)  [C].
		int anoPeriodoSeguinte = DiscenteHelper.somaSemestres(ano, periodo, +1);
		int anoSeguinte = anoPeriodoSeguinte / 10;
		int periodoSeguinte = anoPeriodoSeguinte - (anoSeguinte * 10);

		StringBuilder sqlconsulta = new StringBuilder(
				//[A]
				" select u.sigla as centro, c.id_curso, c.nome as curso_nome, ga.descricao as descricao_grau,  "
						+ " d.matricula, p.nome as discente_nome, iad.valor as IEAN, m.id_municipio, "
						+ " m.nome as municipio_nome, sd.descricao as status_discente, mpo.nome as municipio_polo    "
						+ " from comum.unidade u" 
						+ "  INNER JOIN curso c ON u.id_unidade = c.id_unidade " 
						+ "  INNER JOIN discente d ON c.id_curso= d.id_curso  "
						+ "  INNER JOIN graduacao.discente_graduacao dgd ON d.id_discente = dgd.id_discente_graduacao "
						+ "  LEFT JOIN ead.polo po on (dgd.id_polo = po.id_polo)"
						+ "  LEFT JOIN comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ "  INNER JOIN graduacao.matriz_curricular mc using (id_matriz_curricular)"
						+ "  INNER JOIN ensino.grau_academico ga on (ga.id_grau_academico = mc.id_grau_academico)"
						+ "  INNER JOIN comum.pessoa p ON d.id_pessoa = p.id_pessoa  "
						+ "  INNER JOIN comum.municipio m ON c.id_municipio = m.id_municipio "       
						+ "  INNER JOIN status_discente sd ON d.status = sd.status "
						+ "  INNER JOIN ensino.indice_academico_discente iad ON iad.id_discente = d.id_discente "
						+ "  INNER JOIN (select cur.id_unidade, cur.id_curso, max(iadis.valor) as iean_max       "
						+ "        		  from graduacao.discente_graduacao dg " 
						+ "				    INNER JOIN discente dis 	ON dis.id_discente = dg.id_discente_graduacao "
						+ "    				INNER JOIN curso cur 		ON dis.id_curso = cur.id_curso "
						+ "    				INNER JOIN ensino.indice_academico_discente iadis ON iadis.id_discente = dis.id_discente "
						
						//+ "        		  where iadis.id_indice_academico = " + Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.INDICE_ACADEMICO_LAUREADOS)) // índice do IEAN em IndiceAcademico
						+ "        		  where iadis.id_indice_academico = " + indiceAcademico 
						
						+ "        		  and  dis.status in " + UFRNUtils.gerarStringIn( new int[]{ StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO } )  // Status (CONCLUIDO OU GRADUANDO)
						+ "        		  and  exists ( select mco.id_discente from ensino.matricula_componente mco  "
						+ "     		  		  		where mco.id_discente = dis.id_discente  ");
		sqlconsulta.append("             		  		and   mco.periodo=" + periodo);
		sqlconsulta.append("              		  		and   mco.id_situacao_matricula <> "
				+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mco.id_discente  ");
		sqlconsulta
				.append("               having max(mco.ano)="
						+ ano
						+ ")"
						+ "       and  not exists ( select mco.id_discente from ensino.matricula_componente mco  "
						+ "     		  where mco.id_discente = dis.id_discente  ");
		sqlconsulta.append("               and   mco.periodo="
				+ periodoSeguinte);
		sqlconsulta.append("               and   mco.id_situacao_matricula <> "
				+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mco.id_discente  ");
		sqlconsulta.append("               having max(mco.ano)=" + anoSeguinte
				+ ")" + "   ");
		sqlconsulta
				.append("       group by cur.id_unidade, cur.id_curso ) " 
						+ " as maxiean   ON  maxiean.id_curso = c.id_curso  AND  maxiean.id_unidade = c.id_unidade      "
						
						//+ "  where iad.id_indice_academico = " + Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.INDICE_ACADEMICO_LAUREADOS)) // índice do IEAN em IndiceAcademico
						+ "  where iad.id_indice_academico = " + indiceAcademico 
						
						+ "  and   maxiean.iean_max = iad.valor      "
						+ "  and   d.status in " + UFRNUtils.gerarStringIn( new int[]{ StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO } )
						+ "  and   exists ( select mcop.id_discente from ensino.matricula_componente mcop   "
						+ "      		  where mcop.id_discente = d.id_discente   ");
		sqlconsulta.append("               and   mcop.periodo=" + periodo);
		sqlconsulta
				.append("               and   mcop.id_situacao_matricula <> "
						+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mcop.id_discente  ");
		sqlconsulta
				.append("               having max(mcop.ano)="
						+ ano
						+ ") "
						+ "  and  not exists ( select mcop.id_discente from ensino.matricula_componente mcop   "
						+ "      		  where mcop.id_discente = d.id_discente   ");
		sqlconsulta.append("               and   mcop.periodo="
				+ periodoSeguinte);
		sqlconsulta
				.append("               and   mcop.id_situacao_matricula <> "
						+ SituacaoMatricula.EXCLUIDA.getId());
		
		sqlconsulta.append("              group by mcop.id_discente  ");
		sqlconsulta.append("               having max(mcop.ano)=" + anoSeguinte
				+ ") " + "  ");
		
		//[B]
		sqlconsulta.append("and c.id_curso NOT IN("
		+ "  select c.id_curso " 
		+ "  from comum.unidade u"  
		+ "  INNER JOIN curso c ON u.id_unidade = c.id_unidade " 
		+ "  INNER JOIN discente d ON c.id_curso= d.id_curso  "
		+ "  INNER JOIN graduacao.discente_graduacao dgd ON d.id_discente = dgd.id_discente_graduacao " 
		+ "  INNER JOIN comum.pessoa p ON d.id_pessoa = p.id_pessoa  "
		+ "  INNER JOIN comum.municipio m ON c.id_municipio = m.id_municipio "       
		+ "  INNER JOIN status_discente sd ON d.status = sd.status "
		+ "  INNER JOIN ensino.indice_academico_discente iad ON iad.id_discente = d.id_discente "
		+ "  INNER JOIN (select cur.id_unidade, cur.id_curso, max(iadis.valor) as iean_max       "
		+ "        		  from graduacao.discente_graduacao dg " 
		+ "				    INNER JOIN discente dis 	ON dis.id_discente = dg.id_discente_graduacao "
		+ "    				INNER JOIN curso cur 		ON dis.id_curso = cur.id_curso "
		+ "    				INNER JOIN ensino.indice_academico_discente iadis ON iadis.id_discente = dis.id_discente "
		
		//+ "        		  where iadis.id_indice_academico = " + Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.INDICE_ACADEMICO_LAUREADOS)) // índice do IEAN em IndiceAcademico
		//+ "        		  and  iadis.valor >= " + Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.VALOR_MINIMO_INDICE_ACADEMICO_LAUREADOS))
		+ "        		  where iadis.id_indice_academico = " + indiceAcademico 
		+ "        		  and  iadis.valor >= " + valorMinimo
		
		+ "        		  and  dis.status in " + UFRNUtils.gerarStringIn( new int[]{ StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO } )  // Status (CONCLUIDO OU GRADUANDO)
		+ "        		  and  exists ( select mco.id_discente from ensino.matricula_componente mco  "
		+ "     		  		  		where mco.id_discente = dis.id_discente  ");
		sqlconsulta.append("             		  		and   mco.periodo=" + periodo);
		sqlconsulta.append("              		  		and   mco.id_situacao_matricula <> "
		+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mco.id_discente  ");
		sqlconsulta
		.append("               having max(mco.ano)="
				+ ano
				+ ")"
				+ "       and  not exists ( select mco.id_discente from ensino.matricula_componente mco  "
				+ "     		  where mco.id_discente = dis.id_discente  ");
		sqlconsulta.append("               and   mco.periodo="
		+ periodoSeguinte);
		sqlconsulta.append("               and   mco.id_situacao_matricula <> "
		+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mco.id_discente  ");
		sqlconsulta.append("               having max(mco.ano)=" + anoSeguinte
		+ ")" + "   ");
		sqlconsulta
		.append("       group by cur.id_unidade, cur.id_curso ) " 
				+ " as maxiean   ON  maxiean.id_curso = c.id_curso  AND  maxiean.id_unidade = c.id_unidade      "
				
				//+ "  where iad.id_indice_academico = " + Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.INDICE_ACADEMICO_LAUREADOS)) // índice do IEAN em IndiceAcademico
				+ "  where iad.id_indice_academico = " + indiceAcademico 
				
				+ "  and   maxiean.iean_max = iad.valor      "
				
				//+ "  and   iad.valor >= " + Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.VALOR_MINIMO_INDICE_ACADEMICO_LAUREADOS))
				+ "  and   iad.valor >= " + valorMinimo
				
				+ "  and   d.status in " + UFRNUtils.gerarStringIn( new int[]{ StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO } )
				+ "  and   exists ( select mcop.id_discente from ensino.matricula_componente mcop   "
				+ "      		  where mcop.id_discente = d.id_discente   ");
		sqlconsulta.append("               and   mcop.periodo=" + periodo);
		sqlconsulta
		.append("               and   mcop.id_situacao_matricula <> "
				+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mcop.id_discente  ");
		sqlconsulta
		.append("               having max(mcop.ano)="
				+ ano
				+ ") "
				+ "  and  not exists ( select mcop.id_discente from ensino.matricula_componente mcop   "
				+ "      		  where mcop.id_discente = d.id_discente   ");
		sqlconsulta.append("               and   mcop.periodo="
		+ periodoSeguinte);
		sqlconsulta
		.append("               and   mcop.id_situacao_matricula <> "
				+ SituacaoMatricula.EXCLUIDA.getId());
		sqlconsulta.append("              group by mcop.id_discente  ");
		sqlconsulta.append("               having max(mcop.ano)=" + anoSeguinte
		+ ") ) " + "  ");
								
		//[C]
		sqlconsulta.append(" order by c.nome, u.sigla, m.nome, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna o quantitativo de solicitação de trancamentos por
	 * motivos.
	 * 
	 * @param ano
	 * @param periodo
	 * @param matrizCurricular
	 * @param motivoTrancamento
	 * @param filtros
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativosAlunoMotivoTrancamento(
			Integer ano, Integer periodo, MatrizCurricular matrizCurricular,
			MotivoTrancamento motivoTrancamento,
			HashMap<Integer, Boolean> filtros) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" select mct.ano, mct.periodo, u.id_unidade, c.id_curso, ga.id_grau_academico, mc.id_matriz_curricular,h.id_habilitacao, m.id_municipio, t.id_turno, mt.id_motivo_trancamento,"
						+ " c.nome as curso_nome, u.sigla as centro_sigla, u.nome as centro_nome, m.nome as municipio_nome, ga.descricao as modalidade_nome, h.nome as habilitacao, t.descricao as turno_sigla,"
						+ " mt.descricao as motivo_trancamento, count(*) as qtd, mpo.nome as municipio_polo "
						+ ""
						+ " from ensino.motivo_trancamento mt "
						+ " inner join ensino.solicitacao_trancamento_matricula stm on mt.id_motivo_trancamento = stm.id_motivo_trancamento "
						+ " inner join ensino.situacao_solicitacao_trancamento sst on sst.id_situacao_solicitacao_trancamento = stm.situacao  "
						+ " inner join ensino.matricula_componente mct on stm.id_matricula_componente = mct.id_matricula_componente "
						+ " inner join ensino.situacao_matricula sm on sm.id_situacao_matricula = stm.situacao "
						+ " inner join discente d on d.id_discente = mct.id_discente "
						+ " inner join graduacao.discente_graduacao dg on dg.id_discente_graduacao = d.id_discente "
						+ " left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ " inner join graduacao.matriz_curricular mc on mc.id_matriz_curricular = dg.id_matriz_curricular  "
						+ " inner join curso c on c.id_curso = mc.id_curso "
						+ " inner join comum.municipio m on m.id_municipio = c.id_municipio "
						+ " inner join comum.unidade u on u.id_unidade = c.id_unidade "
						+ " inner join ensino.grau_academico ga on ga.id_grau_academico = mc.id_grau_academico"
						+ " inner join ensino.turno t on t.id_turno = mc.id_turno "
						+ " inner join comum.pessoa p on p.id_pessoa = d.id_pessoa "
						+ " left join graduacao.habilitacao h on h.id_habilitacao = mc.id_habilitacao  "
						+ " left join ensino.alteracao_matricula am on am.id_alteracao_matricula = stm.id_alteracao_matricula "
						+ "" + "where trueValue() ");

		if (filtros.get(MATRIZ_CURRICULAR))
			sqlconsulta.append(" and   mc.id_matriz_curricular="
					+ matrizCurricular.getId());
		else if (filtros.get(CURSO))
			sqlconsulta.append(" and   c.id_curso="
					+ matrizCurricular.getCurso().getId());
		else if (filtros.get(CENTRO))
			sqlconsulta.append(" and   u.id_unidade="
					+ matrizCurricular.getCurso().getUnidade().getId());

		if (filtros.get(MOTIVO_TRANCAMENTO))
			sqlconsulta.append(" and   mt.id_motivo_trancamento="
					+ motivoTrancamento.getId());

		if (filtros.get(ANO_PERIODO)) {
			sqlconsulta.append(" and   mct.ano=" + ano);
			sqlconsulta.append(" and   mct.periodo=" + periodo);
		}

		sqlconsulta
				.append(" group by mct.ano, mct.periodo, u.id_unidade, c.id_curso, mc.id_matriz_curricular, ga.id_grau_academico, h.id_habilitacao, m.id_municipio, t.id_turno, mt.id_motivo_trancamento, "
						+ " c.nome, u.sigla,u.nome, m.nome, ga.descricao, h.nome, t.descricao, mt.descricao, mpo.nome "
						+ " order by mct.ano, mct.periodo, u.sigla,u.id_unidade,  c.nome,c.id_curso,mc.id_matriz_curricular, m.nome,m.id_municipio,ga.descricao,  h.nome, mt.descricao");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna a Lista de alunos no ano/período, dado o motivo de trancamento e
	 * matriz curricular.
	 * 
	 * @param ano
	 * @param periodo
	 * @param matrizCurricular
	 * @param motivoTrancamento
	 * @param filtros
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunoMotivoTrancamento(
			Integer ano, Integer periodo, MatrizCurricular matrizCurricular,
			MotivoTrancamento motivoTrancamento,
			HashMap<Integer, Boolean> filtros) throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"  select mct.ano, mct.periodo, am.id_alteracao_matricula, stm.id_solicitacao_trancamento_matricula,  "
						+ " mt.id_motivo_trancamento, d.id_discente, mc.id_matriz_curricular, h.id_habilitacao, "
						+ " m.id_municipio, p.id_pessoa, c.id_curso, t.id_turno, ga.id_grau_academico, mct.id_matricula_componente, "
						+ " p.nome as aluno_nome, d.matricula, d.ano_ingresso, d.periodo_ingresso, u.sigla as centro_sigla, c.nome as curso_nome, "
						+ " m.nome as cidade_curso, t.descricao as turno_descricao, ga.descricao as modalidade_nome, h.nome as habilitacao, "
						+ " sst.descricao as situacao_trancamento,stm.justificativa, stm.replica, mt.descricao as motivo_descricao,"
						+ " ccd.nome as componente_nome, ccd.codigo as componente_codigo, mpo.nome as municipio_polo "
						+ " from ensino.motivo_trancamento mt  "
						+ " inner join ensino.solicitacao_trancamento_matricula stm on mt.id_motivo_trancamento = stm.id_motivo_trancamento "
						+ " inner join ensino.situacao_solicitacao_trancamento sst on sst.id_situacao_solicitacao_trancamento = stm.situacao "
						+ " inner join ensino.matricula_componente mct on stm.id_matricula_componente = mct.id_matricula_componente "
						+ " inner join ensino.componente_curricular_detalhes ccd on ccd.id_componente_detalhes = mct.id_componente_detalhes "
						+ " inner join ensino.situacao_matricula sm on sm.id_situacao_matricula = stm.situacao "
						+ " inner join discente d on d.id_discente = mct.id_discente "
						+ " inner join graduacao.discente_graduacao dg on dg.id_discente_graduacao = d.id_discente  "
						+ " left join ead.polo po on (dg.id_polo = po.id_polo)"
						+ " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
						+ " inner join graduacao.matriz_curricular mc on mc.id_matriz_curricular = dg.id_matriz_curricular "
						+ " inner join curso c on c.id_curso = mc.id_curso  "
						+ " inner join comum.municipio m on m.id_municipio = c.id_municipio "
						+ " inner join comum.unidade u on u.id_unidade = c.id_unidade  "
						+ " inner join ensino.grau_academico ga on ga.id_grau_academico = mc.id_grau_academico  "
						+ " inner join ensino.turno t on t.id_turno = mc.id_turno  "
						+ " inner join comum.pessoa p on p.id_pessoa = d.id_pessoa  "
						+ " left join graduacao.habilitacao h on h.id_habilitacao = mc.id_habilitacao  "
						+ " left join ensino.alteracao_matricula am on am.id_alteracao_matricula = stm.id_alteracao_matricula "
						+ " where trueValue()  ");

		if (filtros.get(MATRIZ_CURRICULAR))
			sqlconsulta.append(" and   mc.id_matriz_curricular="
					+ matrizCurricular.getId());
		else if (filtros.get(CURSO))
			sqlconsulta.append(" and   c.id_curso="
					+ matrizCurricular.getCurso().getId());
		else if (filtros.get(CENTRO))
			sqlconsulta.append(" and   u.id_unidade="
					+ matrizCurricular.getCurso().getUnidade().getId());

		if (filtros.get(MOTIVO_TRANCAMENTO))
			sqlconsulta.append(" and   mt.id_motivo_trancamento="
					+ motivoTrancamento.getId());

		if (filtros.get(ANO_PERIODO)) {
			sqlconsulta.append(" and   mct.ano=" + ano);
			sqlconsulta.append(" and   mct.periodo=" + periodo);
		}

		sqlconsulta
				.append(" order by mct.ano, mct.periodo, ccd.codigo, u.sigla,u.id_unidade, c.nome,c.id_curso, m.nome,m.id_municipio, t.sigla, ga.descricao,  h.nome, p.nome ");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna a Lista de Alunos Concluídos com Créditos Pendentes.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaAlunoConcluidoCreditoPendente() throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(
				" select ano_ingresso, periodo_ingresso, matricula, nome"
						+ " from discente d, graduacao.discente_graduacao dg, comum.pessoa p"
						+ " where status = 3"
						+ " and dg.id_discente_graduacao = d.id_discente"
						+ " and d.id_pessoa = p.id_pessoa"
						+ " and dg.cr_total_pendentes > 0"
						+ " order by ano_ingresso, periodo_ingresso");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna a Lista de Insucessos de Alunos de um curso nas disciplinas.
	 * 
	 * @param ano
	 * @param periodo
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaInsucessosAlunos(Integer ano,
			Integer periodo, Integer idCurso) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(
	  			" select cc.codigo as codigo_disciplina, ccd.nome as nome_disciplina, t.codigo as codigo_turma, "
							+ " d.matricula as matricula_discente, p.nome as nome_discente, m.id_situacao_matricula, "
							+ " sm.descricao as situacao, mpo.nome as municipio_polo "
							+ " from discente d "
							+ " inner join comum.pessoa p using(id_pessoa) "
							+ " inner join ensino.matricula_componente m using(id_discente) "
						    + " inner join ensino.componente_curricular cc on (cc.id_disciplina = m.id_componente_curricular) "
						    + " inner join ensino.componente_curricular_detalhes ccd on (cc.id_disciplina = ccd.id_componente and ccd.id_componente_detalhes = cc.id_detalhe) "
						    + " inner join ensino.situacao_matricula sm on (sm.id_situacao_matricula = m.id_situacao_matricula) "
						    + " inner join ensino.turma t on (m.id_turma = t.id_turma) "
						    + " left join graduacao.discente_graduacao dg on (dg.id_discente_graduacao = d.id_discente) " 
						    + " left join ead.polo po on (dg.id_polo = po.id_polo) "
						    + " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade) " 
						    + " where m.ano = "	+ ano
							+ " and m.periodo = " + periodo
							+ " and m.id_situacao_matricula in (3,5,6,7) "
							+ (!isEmpty(idCurso) ? (" and d.id_curso = "+idCurso) : "")
							+ " group by codigo_disciplina, nome_disciplina, codigo_turma, matricula_discente, "
							+ " nome_discente, m.id_situacao_matricula, situacao, mpo.nome"
							+ " order by nome_disciplina, codigo_turma, nome_discente, m.id_situacao_matricula");
		
		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}


	/** Retorna a quantidade de alunos com reprovação e desnivelados por componente curricular.
	 * @param centroDisciplinas
	 * @param centroAlunos
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoAlunosReprovadosDesnivelados(
			Unidade centroDisciplinas, Unidade centroAlunos) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(
				" select mc.id_componente_curricular,"
				+ " disc.codigo,"
				+ " discd.nome,"
				+ " count(distinct mc.id_discente)"
				+ " from ensino.matricula_componente mc join ensino.componente_curricular disc on mc.id_componente_curricular = disc.id_disciplina"
				+ " join ensino.componente_curricular_detalhes discd on discd.id_componente = disc.id_disciplina"
				+ " join comum.unidade un on disc.id_unidade = un.id_unidade"
				+ " join discente d on mc.id_discente = d.id_discente"
				+ " join graduacao.curriculo c on d.id_curriculo = c.id_curriculo"
				+ " join graduacao.curriculo_componente cc on c.id_curriculo = cc.id_curriculo"
				+ " join curso curso on d.id_curso = curso.id_curso"
				+ " where id_situacao_matricula = " + SituacaoMatricula.REPROVADO.getId());
		if (!ValidatorUtil.isEmpty(centroDisciplinas)) {
			sqlconsulta.append(" and un.hierarquia like '%."+centroDisciplinas.getId()+".%'");
		}
		if (!ValidatorUtil.isEmpty(centroAlunos)) {
			sqlconsulta.append(" and curso.id_unidade = "+centroAlunos.getId());
		}
		sqlconsulta.append(" and mc.id_componente_curricular not in ("
				+ "     select id_componente_curricular"
				+ "     from ensino.matricula_componente mc2"
				+ "     where mc2.id_componente_curricular = mc.id_componente_curricular"
				+ "      and mc2.id_discente = mc.id_discente"
				+ "      and mc2.id_situacao_matricula = "+SituacaoMatricula.APROVADO.getId()+")"
				+ " and cc.obrigatoria = trueValue()"
				+ " and d.status = " + StatusDiscente.ATIVO
				+ " group by mc.id_componente_curricular, disc.codigo, discd.nome"
				+ " order by codigo");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
	
	/**
	 * Retorna a disciplina, o departamento, centro, a justificativa, o ano período e a quantidade de alunos que trancaram
	 * aquela determinada matéria.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Map<String, List<LinhaRelatorioQuantitativoTrancamentos>> findQuantitativoTrancamentoDisciplina(Integer ano, Integer periodo, Integer idCentro) throws DAOException {
		
		Map<String, List<LinhaRelatorioQuantitativoTrancamentos>> relatorio =  new TreeMap<String, List<LinhaRelatorioQuantitativoTrancamentos>>();
		
		StringBuilder sqlconsulta = new StringBuilder(
				" select t.codigo as codigo_turma, cc.codigo as codigo_curricular, ccd.nome, u.id_unidade, u.nome as departamento, u2.id_unidade as id_unidade_gestora, " +
				" u2.nome as centro, justificativa, emt.descricao, count(*)"+
				" from ensino.matricula_componente mc inner join ensino.turma t using (id_turma) inner join ensino.componente_curricular cc using(id_disciplina)"+ 
				" inner join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes inner join comum.unidade u on u.id_unidade = cc.id_unidade"+ 
				" inner join comum.unidade u2 on u2.id_unidade = u.id_gestora "+
				" left join ensino.solicitacao_trancamento_matricula stm on stm.id_matricula_componente = mc.id_matricula_componente"+
				" left join ensino.motivo_trancamento emt on stm.id_motivo_trancamento = emt.id_motivo_trancamento"+
				" where id_situacao_matricula = "+SituacaoMatricula.TRANCADO.getId()+" and stm.situacao = "+SolicitacaoTrancamentoMatricula.TRANCADO+""+
				" and cc.nivel = '"+NivelEnsino.GRADUACAO+"' and mc.ano = "+ano+" and mc.periodo = "+periodo+" ");
				if (idCentro != 0) {
					sqlconsulta.append("and u2.id_unidade = "+idCentro+" ");
				}
				sqlconsulta.append("group by t.codigo, cc.codigo, ccd.nome, u.id_unidade, u.nome, u2.id_unidade, u2.nome, justificativa, emt.descricao" +
				" order by u.nome, t.codigo, cc.codigo, ccd.nome, u.id_unidade, u2.id_unidade, u2.nome");

		Query q = getSession().createSQLQuery(sqlconsulta.toString());
		
		List<?> trancamentoDisciplina = q.list();
		Iterator<?> it = trancamentoDisciplina.iterator();
		
		while(it.hasNext()){
			int col = 0;
			Object[] colunas = (Object[]) it.next();
			
			String codigoTurma = (String) colunas[col++];
			String codigoCurricular = (String) colunas[col++];
			String nome = (String) colunas[col++];
			int idUnidade = (Integer) colunas[col++];
			String departamento = (String) colunas[col++];
			int idUnidadeGestora = (Integer) colunas[col++];
			String centro  = (String) colunas[col++];
			
			String justificativas = (String) colunas[col++];

			if (("").equals(justificativas)) {
				justificativas = (String) colunas[col++];
			}else{
				col++;
			}

			int anoPeriodo = new Integer (ano+""+periodo);
			BigInteger total = (BigInteger)colunas[col++];
			
			LinhaRelatorioQuantitativoTrancamentos linha = new LinhaRelatorioQuantitativoTrancamentos();
			
			linha.setCodigoTurma(codigoTurma);
			linha.setCodigoCurricular(codigoCurricular);
			linha.setNome(nome);
			linha.setIdUnidade(idUnidade);
			linha.setDepartamento(departamento);
			linha.setIdUnidadeGestora(idUnidadeGestora);
			linha.setAnoPeriodo(anoPeriodo);
			linha.setTotal(total);
			
			linha.setCentro(centro);
			
			if (!isEmpty(justificativas)) {
				LinhaJustificativaRelatorioQuantitativoTrancamentos justif = new LinhaJustificativaRelatorioQuantitativoTrancamentos();
				justif.setDescricao(justificativas);
				justif.setQuantidade(total);
				linha.getJustificativas().add(justif);
			}

			List<LinhaRelatorioQuantitativoTrancamentos> list = relatorio.get(departamento);
			if (list == null)
				list = new ArrayList<LinhaRelatorioQuantitativoTrancamentos>();
			
			list.add(linha);			
			
			relatorio.put(departamento, list);
		}

		return relatorio;
	}
	
	/**
	 * Retorna um Quantitativo de turmas não consolidadas por departamento,
	 * tendo como parâmetro de entrada o ano e o período. 
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantitativoTurmaDepartamento(Integer ano, Integer periodo, boolean ead) throws DAOException {
	
		String sqlConsultaMatricula = "ensino.matricula_componente " +
		"		where id_turma = t.id_turma " +
		"		and id_situacao_matricula in " + gerarStringIn( SituacaoMatricula.getSituacoesComVinculoTurma() );
		
		StringBuilder sqlconsulta = new StringBuilder(
					"select u.nome, count(t.id_turma)"+
					" from ensino.turma t join ensino.situacao_turma st on t.id_situacao_turma = st.id_situacao_turma"+
					" join ensino.componente_curricular cc on cc.id_disciplina = t.id_disciplina"+
					" join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes"+
					" join comum.unidade u on cc.id_unidade = u.id_unidade");
			if (ead) {
				sqlconsulta.append(
					" where ano = "+ano+" and periodo = "+periodo+" " + 
					" and t.id_situacao_turma in "+gerarStringIn( SituacaoTurma.getSituacoesAbertas() )+" " +
					" and id_turma in ( select id_turma from ensino.matricula_componente )"+
					" and t.agrupadora = falseValue() "+
					" and exists ( select id_turma from " + sqlConsultaMatricula + " ) " +
					" and cc.nivel = 'G' group by u.nome order by u.nome");
			}else {
				sqlconsulta.append(
					" and t.id_polo is null"+
					" where ano = "+ano+" and periodo = "+periodo+" " +
					" and t.id_situacao_turma in "+gerarStringIn( SituacaoTurma.getSituacoesAbertas() )+" " +
					" and t.agrupadora = falseValue() "+
					" and id_turma in ( select id_turma from ensino.matricula_componente )"+
					" and exists ( select id_turma from " + sqlConsultaMatricula + " ) " +
					" and cc.nivel = 'G' group by u.nome order by u.nome");
			}

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
	
	/** Retorna uma lista de graduandos que possuem empréstimos pendentes na biblioteca. 
	 * @param idCurso Caso seja diferente de 0 (zero) restringirá a consulta ao curso informado.
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findGraduandosComEmprestimoPendenteBiblioteca(int idCurso) throws DAOException{
		
		StringBuilder sqlconsulta = new StringBuilder(" select d.id_discente," +
				" d.matricula," +
				" p.nome," +
				" p.nome_ascii," +
				" count(e.id_emprestimo) as total_pendente," +
				" mpo.nome as municipio_polo" +
				" from discente d" +
				" inner join graduacao.discente_graduacao dg on (d.id_discente =  dg.id_discente_graduacao)" +
				" left join ead.polo po on (dg.id_polo = po.id_polo)" +
				" left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
				" inner join comum.pessoa p using (id_pessoa)" +
				" inner join biblioteca.usuario_biblioteca using (id_pessoa)" +
				" inner join biblioteca.emprestimo e using (id_usuario_biblioteca)" +
				" where d.status = " + StatusDiscente.GRADUANDO +
				(idCurso != 0 ? " and d.id_curso = " + idCurso : "" )+
				" and e.data_devolucao is null" +
				" and e.ativo = trueValue()" +
				" group by d.id_discente, d.matricula, p.nome, p.nome_ascii, mpo.nome" +
				" order by p.nome_ascii");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Retorna um Quantitativo de turmas não consolidadas por departamento,
	 * tendo como parâmetro de entrada o ano e o período. 
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findAlunoIngressanteOutroCurso(Integer ano, Integer periodo, Integer chamada, Integer processoSeletivo) throws DAOException {
		
		StringBuilder sqlconsulta = new StringBuilder(
				"SELECT p.nome, p.cpf_cnpj, d.matricula, c.nome as nome_curso_novo, vinculos.nome_curso_antigo, vinculos.municipio_polo " +
				" from discente d " +
				" inner join vestibular.convocacao_processo_seletivo_discente cpsd ON cpsd.id_discente = d.id_discente" +
				" inner join vestibular.convocacao_processo_seletivo cps ON cps.id_convocacao_processo_seletivo = cpsd.id_convocacao_processo_seletivo" +
				" inner join comum.pessoa p ON p.id_pessoa = d.id_pessoa" +
				" inner join curso c ON c.id_curso = d.id_curso," +
				" ( select dis.id_pessoa, dis.id_discente, dis.id_curso as id_curso_antigo,  cur.nome as nome_curso_antigo, mpo.nome as municipio_polo" + 
				"   from discente dis inner join graduacao.discente_graduacao dg on (dis.id_discente =  dg.id_discente_graduacao)" +
				" left join ead.polo po on (dg.id_polo = po.id_polo)" +
				" left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
			    "	join curso cur ON cur.id_curso = dis.id_curso" + 
				"	left join vestibular.convocacao_processo_seletivo_discente conv ON conv.id_discente = dis.id_discente " +
				"	left join vestibular.convocacao_processo_seletivo conv_ps ON conv_ps.id_convocacao_processo_seletivo = conv.id_convocacao_processo_seletivo ");	
				if (chamada != 0) {
					sqlconsulta.append(" and conv.id_convocacao_processo_seletivo <> "+chamada+" and conv_ps.id_processo_seletivo = " + processoSeletivo);
				}else
					sqlconsulta.append(" and conv_ps.id_processo_seletivo = " + processoSeletivo);
				
		sqlconsulta.append(" where dis.status in " + (UFRNUtils.gerarStringIn(new int[]{StatusDiscente.ATIVO,StatusDiscente.TRANCADO, 
												StatusDiscente.FORMANDO})) +
						   " and dis.nivel = '" + NivelEnsino.GRADUACAO + "'" +
						   " order by dis.id_discente desc, dis.id_pessoa asc) as vinculos" );
		
		sqlconsulta.append(" where p.id_pessoa = vinculos.id_pessoa" +
				  " and d.id_discente <> vinculos.id_Discente " +
				  " and c.id_curso <> vinculos.id_curso_antigo " +
				  " and d.ano_ingresso = " + ano +
				  " and d.periodo_ingresso = " + periodo +
				  " and cps.id_processo_seletivo = " + processoSeletivo);
				if (chamada != 0) {
					sqlconsulta.append(" and cpsd.id_convocacao_processo_seletivo = " + chamada); 
				}  
		sqlconsulta.append(" order by vinculos.nome_curso_antigo, p.nome asc");
		
		StringBuilder sqlconsultaCodMerge = new StringBuilder(
				"select p.nome, p.cpf_cnpj, d.matricula, c.nome as nome_curso_novo,"+
				  " vinculos.nome_curso_antigo, vinculos.municipio_polo "+
				  " from comum.pessoa p, discente d "+
				  " left join graduacao.discente_graduacao dg on (d.id_discente =  dg.id_discente_graduacao)" +
				  " left join ead.polo po on (dg.id_polo = po.id_polo)" +
				  " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade), curso c,"+
				  " ( select dis.id_pessoa, dis.id_curso as id_curso_antigo, " +
				  " cur.nome as nome_curso_antigo, cur.nivel, dis.id_discente, mpo.nome as municipio_polo " +
				  " from discente dis inner join graduacao.discente_graduacao dg on (dis.id_discente =  dg.id_discente_graduacao)" +
				  " left join ead.polo po on (dg.id_polo = po.id_polo)" +
				  " left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade), curso cur"+
				  " where dis.id_curso = cur.id_curso" +
				  " and dis.nivel = '"+ NivelEnsino.GRADUACAO + "' " +
				  " and dis.status in " + ( UFRNUtils.gerarStringIn(new int[]{StatusDiscente.ATIVO,StatusDiscente.TRANCADO, 
												StatusDiscente.FORMANDO})) );
					
		sqlconsultaCodMerge.append(" order by dis.id_discente desc, dis.id_pessoa asc) as vinculos"+
				  " where d.id_pessoa = p.id_pessoa and d.id_curso = c.id_curso"+
				  " and p.id_pessoa = vinculos.id_pessoa" +
				  " and d.id_discente <> vinculos.id_discente");
		if (chamada != 0) {
			sqlconsultaCodMerge.append(" and d.codmergcomperve like '%vest"+ano+periodo+"_"+chamada+"%'");
		}else
			sqlconsultaCodMerge.append(" and d.codmergcomperve like '%vest"+ano+periodo+"%'");
				
		sqlconsultaCodMerge.append(" and c.id_curso <> vinculos.id_curso_antigo"+
				  " order by vinculos.nome_curso_antigo, p.nome asc");
		
		List<Map<String, Object>> result;

		try {
			if ( processoSeletivo != null )
				result = executeSql(sqlconsulta.toString());
			else
				result = executeSql(sqlconsultaCodMerge.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Realiza uma busca por todos os discentes regulares, agrupando os discentes por status. 
	 * 
	 * @param municipio
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTotalAlunoRegular(Integer campus) throws DAOException {
		
		StringBuilder sqlconsulta = new StringBuilder("select sd.descricao, p.sexo, count(*) from discente d" +
				" join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao)" +
				" join comum.pessoa p using(id_pessoa) join curso c on (c.id_curso = d.id_curso)" +
				" join status_discente sd on (d.status = sd.status)"+
				" join comum.municipio m using(id_municipio) where ");
				
				if (campus != 0) 
					sqlconsulta.append("c.id_campus = "+campus+" and");

				sqlconsulta.append(" d.status in " + (UFRNUtils.gerarStringIn(new int[]{StatusDiscente.ATIVO,StatusDiscente.TRANCADO, 
					StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO})) +
				" and c.id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL+
				" and c.id_convenio is null group by d.status, p.sexo, sd.descricao " +
				"order by D.STATUS, p.sexo");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Realiza um busca de todos os discentes regulares por curso, levando em conta o município
	 * informado.
	 * 
	 * @param municipio
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTotalAlunoRegularCurso(Integer campus) throws DAOException {
		
		StringBuilder sqlconsulta = new StringBuilder("select c.nome, m.nome as cidade," +
				" sum(case when p.sexo ='M' then 1 else 0 end) as homens," +
				" sum(case when p.sexo ='F' then 1 else 0 end) as mulheres," +
				" mpo.nome as municipio_polo " +
				" from discente d " +
				" join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao)" +
				" left join ead.polo po on (dg.id_polo = po.id_polo)" +
				" left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
				" join comum.pessoa p using(id_pessoa) " +
				" join curso c on (c.id_curso = d.id_curso)" +
				" join comum.municipio m on (m.id_municipio = c.id_municipio)" +
				" where "); 
					if (campus != 0) 
						sqlconsulta.append("c.id_campus = " + campus +" and");
		
				sqlconsulta.append(" d.status in " + UFRNUtils.gerarStringIn(new int[]{StatusDiscente.ATIVO, 
						StatusDiscente.CADASTRADO, StatusDiscente.FORMANDO}) +
				" and c.id_modalidade_educacao = "
				+ UFRNUtils.gerarStringIn(new int[]{ModalidadeEducacao.PRESENCIAL}) +
				" and c.id_convenio is null group by c.nome, c.id_campus, m.nome, mpo.nome ORDER BY c.nome");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Realiza um busca pelos discentes de graduação ingressantes no ano e período informados, que não possuem registro 
	 * de solicitação de matrícula ou matrículas em espera para o período.
	 * 
	 * @param campus
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findAlunosIngressantesSemMatricula(int ano, int periodo) throws DAOException {
		
		String sqlconsulta = "select distinct fi.descricao as forma_ingresso, d.matricula, d.ano_ingresso || '.' || " +
				"d.periodo_ingresso as ingresso, p.nome, sd.descricao as situacao, c.nome as curso, m.nome as municipio, " +
				"t.sigla as turno, h.nome as habilitacao, me.descricao as modalidade, mpo.nome as municipio_polo " +
				" from discente d left join graduacao.discente_graduacao dg " +
				" left join ead.polo po on (dg.id_polo = po.id_polo)" +
				" left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
				" left join comum.pessoa p on d.id_pessoa = p.id_pessoa join status_discente sd on d.status = sd.status" +
				" left join ensino.forma_ingresso fi using(id_forma_ingresso) left join curso c on c.id_curso = d.id_curso " +
				" left join ensino.turno t on c.id_turno = t.id_turno left join graduacao.habilitacao h on c.id_curso = h.id_curso " +
				" left join comum.modalidade_educacao me on c.id_modalidade_educacao = me.id_modalidade_educacao " +
				" left join comum.municipio m using(id_municipio) where ano_ingresso = "+ano+" and periodo_ingresso = "+periodo+
				" and d.id_discente not in ( select id_discente from graduacao.solicitacao_matricula where ano = "+ano+
				" and periodo = "+periodo+" and status not in " + gerarStringIn( new int[] { SolicitacaoMatricula.NEGADA, 
						SolicitacaoMatricula.EXCLUIDA, SolicitacaoMatricula.NEGADA_OUTRO_PROGRAMA } ) + "and anulado = falseValue() ) " +
				" and d.id_discente not in ( select distinct id_discente from ensino.matricula_componente " +
				" where ano = "+ano+" and periodo = "+periodo+" and id_situacao_matricula in "+ 
				  gerarStringIn( new int[] {SituacaoMatricula.EM_ESPERA.getId(), SituacaoMatricula.MATRICULADO.getId()} ) + ")"+ 
				" and d.nivel = '"+NivelEnsino.GRADUACAO+"' and d.status not in " + 
				  gerarStringIn( new int[] {StatusDiscente.CANCELADO, StatusDiscente.GRADUANDO, StatusDiscente.EXCLUIDO} ) +
				" and c.id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL+" order by fi.descricao, c.nome, m.nome, p.nome";
		
		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta);
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}
	
	/** Retorna um relatório de totais de solicitações de matrículas.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findAllAlunosMobilidadeEstudantil(
			String anoPeriodo, String anoPeriodoFim) throws DAOException {
		
		anoPeriodo = anoPeriodo.equals("nullnull") ? null : anoPeriodo;
		anoPeriodoFim = anoPeriodoFim.equals("nullnull") ? null : anoPeriodoFim;
		
		StringBuilder sqlconsulta = 
			new StringBuilder("select distinct u.sigla as unidade_sigla," +
					"  c.nome as curso_nome," +
					"  c.id_curso as id_curso," +
					"  t.id_turno," +
					"  ga.id_grau_academico," +
					"  ga.descricao as modalidade_nome," +
					"  t.sigla as turno_sigla," +
					"  d.ano_ingresso ," +
					"  d.periodo_ingresso," +
					"  d.matricula," +
					"  p.nome as aluno_nome," +
					"  sd.descricao as status_aluno," +
					"  m.nome as municipio," +
					"  m.id_municipio as id_municipio," +
					"  camp.nome as campus_destino, "+
					"  pais.nome as pais_externa," +
					"  me.tipo as tipo_mobilidade," +
					"  me.subtipo as subtipo_mobilidade," +
					"  me.observacao as obs_mobilidade," +
					"  me.ano as ano_mobilidade," +
					"  me.periodo as periodo_mobilidade," +
					"  cast(replace(to_char(soma_semestres(me.ano,me.periodo,me.numero_periodos-1), '9999G9'),',','.') as numeric) as periodo_mob_final," +
					"  me.ies_externa,"+
					"  case when me.ativo = trueValue() then 'Ativo' else 'Cancelada' end as situacao_mobilidade," +
					"  case when me.tipo = 1 then 'Interna' else 'Externa' end as desc_tipo_mobilidade," +
					"  CASE WHEN me.tipo = 1 THEN CASE WHEN me.subTIpo = 1 THEN 'Compulsória' ELSE 'Voluntária' END " +
					"  ELSE CASE WHEN me.tipo = 2 THEN CASE WHEN me.subTIpo = 1 THEN 'Nacional' ELSE 'Internacional' END " +
					"  END END as subtipo, mpo.nome as municipio_polo" +	
					" from discente d" +
					"  inner join ensino.mobilidade_estudantil me using (id_discente)" +
					"  inner join comum.pessoa p using (id_pessoa)" +
					"  inner join curso c using (id_curso)" +
					"  inner join comum.unidade u on (u.id_unidade = c.id_unidade)" +
					"  left join comum.campus_ies camp on me.id_campus_destino = camp.id_campus" +
					"  left join comum.pais pais on me.id_pais_externa = pais.id_pais" +
					"  inner join graduacao.curriculo crr on (d.id_curriculo = crr.id_curriculo)" +
					"  inner join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao)" +
					"  left join ead.polo po on (dg.id_polo = po.id_polo)" +
					"  left join comum.municipio mpo on (mpo.id_municipio = po.id_cidade)" +
					"  inner join status_discente sd on (d.status = sd.status)" +
					"  inner join graduacao.matriz_curricular mc on (mc.id_matriz_curricular = crr.id_matriz)" +
					"  inner join ensino.turno t on(mc.id_turno = t.id_turno)" +
					"  inner join ensino.grau_academico ga on (mc.id_grau_academico = ga.id_grau_academico)" +
					"  inner join comum.municipio m on (c.id_municipio = m.id_municipio)" +
				" where me.ativo = trueValue() "); 
		if( anoPeriodo != null && anoPeriodoFim != null ){
			sqlconsulta.append(" and not( (me.ano || '' || me.periodo < '"+anoPeriodo+"' and cast(soma_semestres(me.ano,me.periodo,me.numero_periodos-1) as text) < '"+anoPeriodo+"')" +
					" 	or ( me.ano || '' || me.periodo > '"+anoPeriodoFim+"' and cast(soma_semestres(me.ano,me.periodo,me.numero_periodos-1) as text) > '"+anoPeriodoFim+"'))");
		} else if( anoPeriodo != null ){
			sqlconsulta.append(" and not( me.ano || '' || me.periodo < '"+anoPeriodo+"' and cast(soma_semestres(me.ano,me.periodo,me.numero_periodos-1) as text) < '"+anoPeriodo+"')");
		} else if( anoPeriodoFim != null){
			sqlconsulta.append(" and not( me.ano || '' || me.periodo > '"+anoPeriodoFim+"' and cast(soma_semestres(me.ano,me.periodo,me.numero_periodos-1) as text) > '"+anoPeriodoFim+"')");
		}
		sqlconsulta.append(" order by c.nome, ga.descricao, t.sigla, m.nome , d.ano_ingresso, d.periodo_ingresso, p.nome");
		
		List<Map<String, Object>> result;
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return result;
	}
	
	/**
	 * Retorna uma coleção de {@link Discente} especiais populados com suas respectivas disciplinas, nomes, 
	 * emails, níveis e nome dos componentes matriculados no ano e período passados.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findDiscentesEspeciaisDisciplinasByAnoPeriodo(int ano, int periodo, int unidade) throws DAOException {
		List<Discente> discentes = new ArrayList<Discente>();
		
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		String consulta = "SELECT d.matricula, d.nivel, UPPER(p.nome) as nome, p.email, det.codigo, det.nome as nome_cc, " +
							"e.logradouro, e.numero, m.nome as cidade, uf.sigla as estado, p.telefone_fixo, p.telefone_celular " +
							"FROM discente d " +
								"INNER JOIN comum.pessoa p USING (id_pessoa) " +
								"INNER JOIN comum.endereco e ON e.id_endereco = p.id_endereco_contato " +
								"INNER JOIN comum.municipio m ON e.id_municipio = m.id_municipio " +
								"INNER JOIN comum.unidade_federativa uf ON uf.id_unidade_federativa = m.id_unidade_federativa " +
								"INNER JOIN ensino.matricula_componente mc USING (id_discente) " +
								"INNER JOIN ensino.componente_curricular_detalhes det USING (id_componente_detalhes) " +
							"WHERE d.tipo = 2 " +
								"AND d.id_gestora_academica = ? " +
								"AND d.nivel in " + NivelEnsino.getNiveisStrictoString() + " " +
								"AND mc.ano = ? AND mc.periodo = ? " +
							"ORDER BY d.nivel, p.nome, det.codigo ";
		
		try {
			con = Database.getInstance().getSigaaConnection();
			stm = con.prepareStatement(consulta);
			
			int cont = 1;
			stm.setInt(cont++, unidade);
			stm.setInt(cont++, ano);
			stm.setInt(cont++, periodo);
			
			rs = stm.executeQuery();
			
			while(rs.next()) {
				Discente d = new Discente();
				
				String nivel = rs.getString("nivel");
				if(nivel.trim().length() > 0)
					d.setNivel(nivel.charAt(0));
				d.setMatricula(rs.getLong("matricula"));

				d.setPessoa(new Pessoa());
				d.getPessoa().setNome(rs.getString("nome"));
				d.getPessoa().setEmail(rs.getString("email"));
				d.getPessoa().setEndereco(rs.getString("logradouro") + ", " + 
											rs.getString("numero") + ", " + 
											rs.getString("cidade") + "/" + rs.getString("estado"));
				d.getPessoa().setTelefone(rs.getString("telefone_fixo"));
				d.getPessoa().setCelular(rs.getString("telefone_celular"));
				
				boolean contains = false;
				int pos = 0;
				
				for (Discente discente : discentes) {
					pos++;
					if(discente.getMatricula() == d.getMatricula()) {
						contains = true;
						break;
					}
				}
				
				if(!contains) {
					discentes.add(d);
				}
				
				d = discentes.get(pos);
				
				MatriculaComponente mat = new MatriculaComponente();
				mat.setComponente(new ComponenteCurricular());
				mat.getComponente().setDetalhes(new ComponenteDetalhes());
				
				mat.getComponente().setCodigo(rs.getString("codigo"));
				mat.getComponente().getDetalhes().setNome(rs.getString("nome_cc"));
				
				Collection<MatriculaComponente> matriculaDisciplinas = d.getMatriculasDisciplina();
				matriculaDisciplinas.add(mat);

				d.setMatriculasDisciplina(matriculaDisciplinas);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		}  finally {
			closeResultSet(rs);
			closeStatement(stm);
			Database.getInstance().close(con);
		}
		return discentes;
	}


	/** Retorna os dados para o Relatório Sumário de Índices Acadêmicos.
	 * @param anoInicial
	 * @param anoFinal
	 * @param somenteCursosConvenio 
	 * @param idUnidade 
	 * @param idModalidadeEducacao 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioSumarioFormaIngresso(int anoInicial, int anoFinal, int idModalidadeEducacao, int idUnidade, boolean somenteCursosConvenio) throws HibernateException, DAOException {
		int status[] = {StatusDiscente.ATIVO, StatusDiscente.FORMANDO};
		Map<String, Map<String, Object>> mapa = new LinkedHashMap<String, Map<String,Object>>();
		// total de discentes por forma de ingresso
		StringBuilder sqlFormaIngresso = new StringBuilder("SELECT mcu.id_matriz_curricular"
			+ ", c.id_curso "
			+ ", c.nome||coalesce(' - '||ga.descricao, '')||coalesce(' - '||t.sigla,'')||coalesce(' - '||h.nome,'') as curso"
			+ ", m1.nome AS curso_cidade "
			+ ", sum(CASE WHEN d.id_forma_ingresso = :ingressoVestibular THEN 1 ELSE 0 END) AS qtd_forma_ingresso_via_vestibular"
			+ ", sum(CASE WHEN d.id_forma_ingresso = :ingressoTransfVoluntaria THEN 1 ELSE 0 END) AS qtd_forma_ingresso_via_transferencia_voluntaria "
			+ ", sum(CASE WHEN d.id_forma_ingresso = :ingressoPortadorDiploma THEN 1 ELSE 0 END) AS qtd_forma_ingresso_via_portador_diploma"
			+ ", sum(CASE WHEN d.id_forma_ingresso = :ingressoReingressoAutomatico THEN 1 ELSE 0 END) AS qtd_forma_ingresso_via_reingresso_automatico"
			+ ", sum(CASE WHEN d.id_forma_ingresso NOT IN (:ingressoVestibular, :ingressoTransfVoluntaria, :ingressoPortadorDiploma, :ingressoReingressoAutomatico) THEN 1 ELSE 0 END) AS qtd_forma_ingresso_via_outras_formas_de_ingresso"
			+ ", count(d.id_discente) AS qtd_ativos"
			+ ", mpo.nome as municipio_polo"
			+ " FROM discente d"
			+ " INNER JOIN graduacao.discente_graduacao dg ON d.id_discente = dg.id_discente_graduacao"
			+ " LEFT JOIN ead.polo po on (dg.id_polo = po.id_polo)"
			+ " LEFT JOIN comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
			+ " INNER JOIN graduacao.matriz_curricular mcu ON mcu.id_matriz_curricular = dg.id_matriz_curricular"
			+ " LEFT JOIN graduacao.habilitacao h using (id_habilitacao)"
			+ " INNER JOIN ensino.grau_academico ga using (id_grau_academico)"
			+ " LEFT JOIN ensino.turno t ON t.id_turno = mcu.id_turno"
			+ " INNER JOIN curso c ON d.id_curso = c.id_curso  "
			+ " INNER JOIN comum.municipio m1 ON m1.id_municipio = c.id_municipio"
			+ " INNER JOIN ensino.forma_ingresso f ON d.id_forma_ingresso = f.id_forma_ingresso  "
			+ " WHERE d.tipo = :regular"
			+ " AND d.status IN " + UFRNUtils.gerarStringIn(status) // ATIVO E ATIVO FORMANDO
			);
		if (idUnidade > 0)
			sqlFormaIngresso.append(" AND c.id_unidade = :idUnidade");
		if (idModalidadeEducacao > 0)
			sqlFormaIngresso.append(" AND c.id_modalidade_educacao = :idModalidadeEducacao");
		if (somenteCursosConvenio)
			sqlFormaIngresso.append(" AND c.id_convenio is not null");
		sqlFormaIngresso.append(" GROUP BY mcu.id_matriz_curricular, c.id_curso, c.nome, ga.descricao, h.nome, m1.nome, t.sigla, mpo.nome "
			+ " ORDER BY m1.nome, c.nome");
		Query qFormaIngresso = getSession().createSQLQuery(sqlFormaIngresso.toString());
		qFormaIngresso.setInteger("regular", Discente.REGULAR);
		qFormaIngresso.setInteger("ingressoVestibular", FormaIngresso.VESTIBULAR.getId());
		qFormaIngresso.setInteger("ingressoTransfVoluntaria", FormaIngresso.TRANSFERENCIA_VOLUNTARIA.getId());
		qFormaIngresso.setInteger("ingressoPortadorDiploma", FormaIngresso.PORTADOR_DIPLOMA.getId());
		qFormaIngresso.setInteger("ingressoReingressoAutomatico", FormaIngresso.REINGRESSO_AUTOMATICO.getId());
		if (idUnidade > 0)
			qFormaIngresso.setInteger("idUnidade", idUnidade);
		if (idModalidadeEducacao > 0)
			qFormaIngresso.setInteger("idModalidadeEducacao", idModalidadeEducacao);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listaFormaIngresso = qFormaIngresso.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// agrupando por matriz curricular, para realizar merge com resultados posteriores
		for (Map<String, Object> resultado : listaFormaIngresso) {
			Map<String, Object> row = mapa.get(resultado.get("id_matriz_curricular").toString());
			if (row == null) {
				row = new LinkedHashMap<String, Object>();
				mapa.put(resultado.get("id_matriz_curricular").toString(), row);
			}
			for (String key : resultado.keySet()) {
				row.put(key, resultado.get(key));
			}
		}
		return CollectionUtils.toList(mapa.values());
	}
	
	/** Retorna os dados para o Relatório Sumário de Médias dos Índices Acadêmicos
	 * @param anoInicial
	 * @param anoFinal
	 * @param somenteCursosConvenio 
	 * @param idUnidade 
	 * @param idModalidadeEducacao 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<String, Map<String, Object>> relatorioSumarioMediaIndicesAcademicos(int idModalidadeEducacao, int idUnidade, boolean somenteCursosConvenio) throws HibernateException, DAOException {
			int status[] = {StatusDiscente.ATIVO, StatusDiscente.FORMANDO};
			Map<String, Map<String, Object>> mapa = new LinkedHashMap<String, Map<String,Object>>();
		// médias dos índices acadêmicos
		StringBuilder sqlMedias = new StringBuilder("SELECT dg.id_matriz_curricular"
			+ ", c.id_curso "
			+ ", c.nome||coalesce(' - '||ga.descricao, '')||coalesce(' - '||t.sigla,'')||coalesce(' - '||h.nome,'') as curso"
			+ ", m1.nome AS curso_cidade "
			+ ", ia.sigla"
			+ ", round(avg(iaf.valor ),4) AS media"
			+ ", mpo.nome as municipio_polo"
			+ " FROM discente d"
			+ " INNER JOIN graduacao.discente_graduacao dg ON d.id_discente = dg.id_discente_graduacao"
			+ " LEFT JOIN ead.polo po on (dg.id_polo = po.id_polo)"
			+ " LEFT JOIN comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
			+ " INNER JOIN ensino.indice_academico_discente iaf ON d.id_discente  = iaf.id_discente   "
			+ " INNER JOIN ensino.indice_academico ia on iaf.id_indice_academico = ia.id"
			+ " INNER JOIN graduacao.matriz_curricular mcu ON mcu.id_matriz_curricular = dg.id_matriz_curricular"
			+ " LEFT JOIN graduacao.habilitacao h using (id_habilitacao)"
			+ " INNER JOIN ensino.grau_academico ga using (id_grau_academico)"
			+ " INNER JOIN ensino.turno t ON t.id_turno = mcu.id_turno"
			+ " INNER JOIN curso c ON d.id_curso = c.id_curso  "
			+ " INNER JOIN comum.municipio m1 ON m1.id_municipio = c.id_municipio"
			+ " WHERE d.status IN " + UFRNUtils.gerarStringIn(status) // ATIVO E ATIVO FORMANDO
			+ " AND d.tipo = :regular");
			if (idUnidade > 0)
				sqlMedias.append(" AND c.id_unidade = :idUnidade");
			if (idModalidadeEducacao > 0)
				sqlMedias.append(" AND c.id_modalidade_educacao = :idModalidadeEducacao");
			if (somenteCursosConvenio)
				sqlMedias.append(" AND c.id_convenio is not null");
			sqlMedias.append(" GROUP BY dg.id_matriz_curricular, c.id_curso, c.nome, ga.descricao, m1.nome, t.sigla, h.nome, ia.sigla, mpo.nome"
			+ " ORDER BY m1.nome, c.nome, ia.sigla");
		Query qMedias = getSession().createSQLQuery(sqlMedias.toString());
		qMedias.setInteger("regular", Discente.REGULAR);
		if (idUnidade > 0)
			qMedias.setInteger("idUnidade", idUnidade);
		if (idModalidadeEducacao > 0)
			qMedias.setInteger("idModalidadeEducacao", idModalidadeEducacao);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listaMedias = qMedias.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// agrupando por matriz curricular, para realizar merge com resultados posteriores
		for (Map<String, Object> resultado : listaMedias) {
			Map<String, Object> row = mapa.get(resultado.get("id_matriz_curricular").toString());
			if (row == null) {
				row = new LinkedHashMap<String, Object>();
				row.put("id_curso", resultado.get("id_curso"));
				row.put("curso", resultado.get("curso"));
				row.put("curso_cidade", resultado.get("curso_cidade"));
				mapa.put(resultado.get("id_matriz_curricular").toString(), row);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> medias = (Map<String, Object>) row.get("medias");
			if (medias == null) {
				medias = new LinkedHashMap<String, Object>();
				row.put("medias", medias);
			}
			medias.put(resultado.get("sigla").toString(), resultado.get("media"));
		}
		return mapa;
	}
	
	public List<Map<String,Object>> relatorioIndiceAcademicos(int idModalidadeEducacao, int idUnidade, boolean somenteCursosConvenio) throws HibernateException, DAOException{
		return CollectionUtils.toList(relatorioSumarioMediaIndicesAcademicos(idModalidadeEducacao, idUnidade, somenteCursosConvenio).values());
	}
	
	/** Retorna os dados para o Relatório Sumário de Médias dos Índices Acadêmicos
	 * @param anoInicial
	 * @param anoFinal
	 * @param somenteCursosConvenio 
	 * @param idUnidade 
	 * @param idModalidadeEducacao 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioSumarioTrancamentoDisciplinas(int anoInicial, int anoFinal, int idModalidadeEducacao, int idUnidade, boolean somenteCursosConvenio) throws HibernateException, DAOException {
			int status[] = {StatusDiscente.ATIVO, StatusDiscente.FORMANDO};
			Map<String, Map<String, Object>> mapa = new LinkedHashMap<String, Map<String,Object>>();
		// trancamentos de disciplinas
		StringBuilder sqlTrancamentos = new StringBuilder("SELECT dg.id_matriz_curricular"
			+ ", mc.ano"
			+ ", c.id_curso "
			+ ", c.nome||coalesce(' - '||ga.descricao, '')||coalesce(' - '||t.sigla,'')||coalesce(' - '||h.nome,'') as curso"
			+ ", m1.nome AS curso_cidade "
			+ ", count(*) AS qtd_trancamento"
			+ ", mpo.nome as municipio_polo"
			+ " FROM discente d"
			+ " INNER JOIN graduacao.discente_graduacao dg ON d.id_discente = dg.id_discente_graduacao"
			+ " LEFT JOIN ead.polo po on (dg.id_polo = po.id_polo)"
			+ " LEFT JOIN comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
			+ " LEFT JOIN ensino.matricula_componente mc ON mc.id_discente = d.id_discente"
			+ " INNER JOIN graduacao.matriz_curricular mcu ON mcu.id_matriz_curricular = dg.id_matriz_curricular"
			+ " LEFT JOIN graduacao.habilitacao h using (id_habilitacao)"
			+ " INNER JOIN ensino.grau_academico ga using (id_grau_academico)"
			+ " INNER JOIN ensino.turno t ON t.id_turno = mcu.id_turno"
			+ " INNER JOIN curso c ON d.id_curso = c.id_curso  "
			+ " INNER JOIN comum.municipio m1 ON m1.id_municipio = c.id_municipio"
			+ " WHERE d.status IN " + UFRNUtils.gerarStringIn(status) // ATIVO E ATIVO FORMANDO
			+ " AND mc.ano >= :anoInicial"
			+ " and mc.ano <= :anoFinal"
			+ " AND mc.id_situacao_matricula = :matriculaTrancada"
			+ " AND d.tipo = :regular");
			if (idUnidade > 0)
				sqlTrancamentos.append(" AND c.id_unidade = :idUnidade");
			if (idModalidadeEducacao > 0)
				sqlTrancamentos.append(" AND c.id_modalidade_educacao = :idModalidadeEducacao");
			if (somenteCursosConvenio)
				sqlTrancamentos.append(" AND c.id_convenio is not null");
			sqlTrancamentos.append(" GROUP BY dg.id_matriz_curricular, c.id_curso, c.nome, ga.descricao, m1.nome, t.sigla, h.nome, mc.ano, mpo.nome"
					+ " ORDER BY m1.nome, c.nome, mc.ano");
		Query qTrancamentos = getSession().createSQLQuery(sqlTrancamentos.toString());
		qTrancamentos.setInteger("matriculaTrancada", SituacaoMatricula.TRANCADO.getId());
		qTrancamentos.setInteger("regular", Discente.REGULAR);
		qTrancamentos.setInteger("anoInicial", anoInicial);
		qTrancamentos.setInteger("anoFinal", anoFinal);
		if (idUnidade > 0)
			qTrancamentos.setInteger("idUnidade", idUnidade);
		if (idModalidadeEducacao > 0)
			qTrancamentos.setInteger("idModalidadeEducacao", idModalidadeEducacao);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listaTrancamentos = qTrancamentos.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		// agrupando por matriz curricular, para realizar merge com resultados posteriores
		for (Map<String, Object> resultado : listaTrancamentos) {
			Map<String, Object> row = mapa.get(resultado.get("id_matriz_curricular").toString());
			if (row == null) {
				row = new LinkedHashMap<String, Object>();
				row.put("id_curso", resultado.get("id_curso"));
				row.put("curso", resultado.get("curso"));
				row.put("curso_cidade", resultado.get("curso_cidade"));
				mapa.put(resultado.get("id_matriz_curricular").toString(), row);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> trancamentos = (Map<String, Object>) row.get("trancamentos");
			if (trancamentos == null) {
				trancamentos = new LinkedHashMap<String, Object>();
				for (int i = anoInicial; i<= anoFinal; i++)
					trancamentos.put(String.valueOf(i), new Integer(0));
				row.put("trancamentos", trancamentos);
			}
			trancamentos.put(resultado.get("ano").toString(), resultado.get("qtd_trancamento"));
		}
		// trancamento de programas
		if (!isEmpty(mapa)) {
			StringBuilder sqlPrograma = new StringBuilder("SELECT mcu.id_matriz_curricular"
				+ ", c.id_curso "
				+ ", c.nome||coalesce(' - '||ga.descricao, '')||coalesce(' - '||t.sigla,'')||coalesce(' - '||h.nome,'') as curso"
				+ ", m1.nome AS curso_cidade "
				+ ", ma.ano_referencia as ano"
				+ ", count(*) AS qtd_trancamento_programa"
				+ ", mpo.nome as municipio_polo"
				+ " FROM discente d"
				+ " INNER JOIN graduacao.discente_graduacao dg ON d.id_discente = dg.id_discente_graduacao"
				+ " LEFT JOIN ead.polo po on (dg.id_polo = po.id_polo)"
				+ " LEFT JOIN comum.municipio mpo on (mpo.id_municipio = po.id_cidade)"
				+ " LEFT JOIN ensino.movimentacao_aluno ma ON ma.id_discente = d.id_discente "
				+ " INNER JOIN graduacao.matriz_curricular mcu ON mcu.id_matriz_curricular = dg.id_matriz_curricular"
				+ " LEFT JOIN graduacao.habilitacao h using (id_habilitacao)"
				+ " INNER JOIN ensino.grau_academico ga using (id_grau_academico)"
				+ " INNER JOIN ensino.turno t ON t.id_turno = mcu.id_turno"
				+ " INNER JOIN curso c ON d.id_curso = c.id_curso  "
				+ " INNER JOIN comum.municipio m1 ON m1.id_municipio = c.id_municipio"
				+ " WHERE ma.id_tipo_movimentacao_aluno = :tipoMovimentacao"
				+ " AND d.tipo = :regular"
				+ " AND extract (year FROM ma.data_ocorrencia) >= :anoInicial"
				+ " AND extract (year FROM ma.data_ocorrencia) <= :anoFinal"
				+ " AND mcu.id_matriz_curricular in " + UFRNUtils.gerarStringIn(mapa.keySet()));
			if (idUnidade > 0)
				sqlPrograma.append(" AND c.id_unidade = :idUnidade");
			if (idModalidadeEducacao > 0)
				sqlPrograma.append(" AND c.id_modalidade_educacao = :idModalidadeEducacao");
			if (somenteCursosConvenio)
				sqlPrograma.append(" AND c.id_convenio is not null");
			sqlPrograma.append(" GROUP BY mcu.id_matriz_curricular, c.id_curso, c.nome, ga.descricao, m1.nome, t.sigla, h.nome, ano, mpo.nome"
				+ " ORDER BY m1.nome, c.nome, ano");
			Query qPrograma = getSession().createSQLQuery(sqlPrograma.toString());
			qPrograma.setInteger("regular", Discente.REGULAR);
			qPrograma.setInteger("tipoMovimentacao", TipoMovimentacaoAluno.TRANCAMENTO);
			qPrograma.setInteger("anoInicial", anoInicial);
			qPrograma.setInteger("anoFinal", anoFinal);
			if (idUnidade > 0)
				qPrograma.setInteger("idUnidade", idUnidade);
			if (idModalidadeEducacao > 0)
				qPrograma.setInteger("idModalidadeEducacao", idModalidadeEducacao);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> listaPrograma = qPrograma.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			// agrupando por matriz curricular, para realizar merge com resultados posteriores
			for (Map<String, Object> resultado : listaPrograma) {
				Map<String, Object> row = mapa.get(resultado.get("id_matriz_curricular").toString());
				if (row == null) {
					row = new LinkedHashMap<String, Object>();
					row.put("id_curso", resultado.get("id_curso"));
					row.put("curso", resultado.get("curso"));
					row.put("curso_cidade", resultado.get("curso_cidade"));
					mapa.put(resultado.get("id_matriz_curricular").toString(), row);
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> trancamentosPrograma = (Map<String, Object>) row.get("trancamentos_programa");
				if (trancamentosPrograma == null) {
					trancamentosPrograma = new LinkedHashMap<String, Object>();
					for (int i = anoInicial; i<= anoFinal; i++)
						trancamentosPrograma.put(String.valueOf(i), new Integer(0));
					row.put("trancamentos_programa", trancamentosPrograma);
				}
				trancamentosPrograma.put(resultado.get("ano").toString(), resultado.get("qtd_trancamento_programa"));
			}
		}
		return CollectionUtils.toList(mapa.values());
	}
	
	/**
	 * Retorna os dados para a geração do relatório de alunos e seus respectivos orientadores.
	 * @param idCurso, OrdenarPorOrientador
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findAlunosOrientadoresByCurso(int idCurso, boolean OrdenarPorOrientador) throws DAOException {
			
			String projecao = "select d.id_discente,d.matricula,pd.nome as aluno, s.id_servidor,ps.nome as orientador, d.ano_ingresso as anoIngresso from graduacao.orientacao_academica o ";
			
			String sql = projecao 					
					+ "inner join discente d on o.id_discente = d.id_discente "
					+ "inner join comum.pessoa pd on pd.id_pessoa = d.id_pessoa "
					+ "inner join rh.servidor s on s.id_servidor = o.id_servidor "
					+ "inner join comum.pessoa ps on ps.id_pessoa = s.id_pessoa "
					+ "where o.fim is null and o.cancelado = false and d.status = 1 and d.id_curso = " +idCurso
					+ " order by d.ano_ingresso, ";
			
			if (OrdenarPorOrientador)
				sql += "orientador, ";
			
			sql += "aluno ";
					
			List<Map<String, Object>> result;

			try {
				result = executeSql(sql);
			} catch (Exception e) {
				throw new DAOException(e);
			}

			return result;


	}
	
	
}