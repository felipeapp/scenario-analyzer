/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/02/2011
 *
 */	
package br.ufrn.sigaa.arq.dao.relatorios;

import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;

/**
 * Dao Responsável por consultas referente aos relatórios de Taxa de Conclusão  
 * 
 * @author arlindo
 *
 */
public class TaxaConclusaoDao extends GenericSigaaDAO {
	
	/** Não contabiliza os cursos que possuem a forma de ingresso contidas na tabela */
	private String NAO_CONTABILIZAR_CURSO_E_FORMA_INGRESSO = " ( select distinct id_forma_ingresso " +
													  " from ensino.nao_contabilizar_taxa_conclusao "+ 
											          " where id_forma_ingresso = fi.id_forma_ingresso and id_curso = d.id_curso) ";
	
	/** Não contabiliza os cursos que estão na tabela */
	private String NAO_CONTABILIZAR_CURSOS = " ( select distinct id_curso " +
													  " from ensino.nao_contabilizar_taxa_conclusao "+ 
												      " where id_forma_ingresso is null) ";	
	
	/** Não contabiliza as formas de ingresso que estão na tabela */
	private String NAO_CONTABILIZAR_FORMAS_INGRESSO = " ( select distinct id_forma_ingresso " +
													  " from ensino.nao_contabilizar_taxa_conclusao "+ 
												      " where id_curso is null) ";	
	
	/**
	 * Critérios formas de ingresso que não serão contabilizadas
	 * 
	 * @return
	 */
	private String getCriterioFormasIngresso(){
	    String criterioFormaIngresso = "  and fi.id_forma_ingresso not in "+ NAO_CONTABILIZAR_CURSO_E_FORMA_INGRESSO;
	    criterioFormaIngresso += "   and d.id_curso not in "+ NAO_CONTABILIZAR_CURSOS;
	    criterioFormaIngresso += "   and fi.id_forma_ingresso not in "+ NAO_CONTABILIZAR_FORMAS_INGRESSO;	
	    return criterioFormaIngresso;
	}
	
	/**
	 * Retorna a taxa de conclusão por curso em determinado ano.
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTaxaConclusaoPorCurso(int ano) throws DAOException {	
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("select id_unidade, sigla, id_curso,  nome || ' - ' || grau || ' - ' || cidade || ' - ' || turno  as nome , id_grau_academico, id_turno, ");
		sql.append("       cast(sum(ingressos) as integer) as ingressos, cast(sum(concluintes) as integer) + cast(sum(graduandos) as integer) as concluintes,");
		sql.append("       case (sum(ingressos))");
		sql.append("          when 0 then 0");
		sql.append("       else");
		sql.append("           (sum(concluintes) + sum(graduandos))/sum(ingressos)");
		sql.append("       end as taxa");
		sql.append(" from (");
		sql.append("     select  U.SIGLA, U.ID_UNIDADE, d.id_curso, d.id_grau_academico, d.id_turno, d.NOME, d.grau, d.cidade, d.turno, "); 
		sql.append("       coalesce( case (d.tipo)"); 
		sql.append("         when 'I' then sum(total)");
		sql.append("       end,0) as ingressos ,");
		sql.append("       coalesce(case (d.tipo)"); 
		sql.append("         when 'C' then sum(total)");
		sql.append("       end,0) as concluintes,");     
		sql.append("       coalesce(case (d.tipo)"); 
		sql.append("         when 'G' then sum(total)");
		sql.append("       end,0) as graduandos");   		
		sql.append(" from  (");	
		//-- INGRESSANTES\n "
		
		sql.append(" select sum(vagas_periodo_1) as total, c.id_curso, c.nome, ga.id_grau_academico, t.id_turno, " +
				" ga.descricao as grau, m.nome as cidade, c.id_unidade, t.sigla as turno, 'I' as tipo "); 		
		
		sql.append(" from ensino.oferta_vagas_curso ovc  ");
		sql.append(" join curso c on ovc.id_curso = c.id_curso ");
		sql.append(" left join graduacao.matriz_curricular mc on ovc.id_matriz_curricular = mc.id_matriz_curricular  ");
		sql.append(" left join ensino.turno t on mc.id_turno = t.id_turno  ");
		sql.append(" left join ensino.grau_academico ga on ga.id_grau_academico = mc.id_grau_academico ");		
		sql.append(" join ensino.forma_ingresso fi on ovc.id_forma_ingresso = fi.id_forma_ingresso ");
		sql.append(" join comum.unidade u on c.id_unidade = u.id_unidade ");
		sql.append(" inner join comum.municipio m  on m.id_municipio = c.id_municipio ");		
		sql.append(" where c.nivel = '"+NivelEnsino.GRADUACAO+"'");
		sql.append(" and ovc.ano = "+(ano-4));
		sql.append(" and c.id_convenio is null ");
		sql.append(" and c.id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL);
		sql.append(" group by c.id_curso, c.id_curso, c.nome, ga.id_grau_academico, t.id_turno, ga.descricao, m.nome, tipo, c.id_unidade, t.sigla 	");
		
		sql.append("		union all ");
		
		sql.append(" select sum(vagas_periodo_2) as total, c.id_curso, c.nome, ga.id_grau_academico, t.id_turno, " +
				" ga.descricao as grau, m.nome as cidade, c.id_unidade, t.sigla as turno, 'I' as tipo "); 		
		
		sql.append(" from ensino.oferta_vagas_curso ovc  ");
		sql.append(" join curso c on ovc.id_curso = c.id_curso ");
		sql.append(" left join graduacao.matriz_curricular mc on ovc.id_matriz_curricular = mc.id_matriz_curricular  ");
		sql.append(" left join ensino.turno t on mc.id_turno = t.id_turno  ");
		sql.append(" left join ensino.grau_academico ga on ga.id_grau_academico = mc.id_grau_academico ");		
		sql.append(" join ensino.forma_ingresso fi on ovc.id_forma_ingresso = fi.id_forma_ingresso ");
		sql.append(" join comum.unidade u on c.id_unidade = u.id_unidade ");
		sql.append(" inner join comum.municipio m  on m.id_municipio = c.id_municipio ");		
		sql.append(" where c.nivel = '"+NivelEnsino.GRADUACAO+"'");
		sql.append(" and ovc.ano = "+(ano-5));
		sql.append(" and c.id_convenio is null ");
		sql.append(" and c.id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL);
		sql.append(" group by c.id_curso, c.id_curso, c.nome, ga.id_grau_academico, t.id_turno, ga.descricao, m.nome, tipo, c.id_unidade, t.sigla 	");
		
		sql.append("		union all ");
		
		//-- CONCLUINTES\n
		sql.append(" select count(d.id_discente) as total, c.id_curso, c.nome, ga.id_grau_academico, t.id_turno, " +
		" ga.descricao as grau, m.nome as cidade, c.id_unidade, t.sigla as turno, 'C' as tipo "); 
		sql.append("	from graduacao.discente_graduacao dg ");
		sql.append(" inner join public.discente d on (d.id_discente = dg.id_discente_graduacao) ");
		sql.append(" inner join ensino.forma_ingresso fi on (fi.id_forma_ingresso = d.id_forma_ingresso) ");
		sql.append(" inner join graduacao.matriz_curricular mc on mc.id_matriz_curricular = dg.id_matriz_curricular ");
		sql.append(" inner join public.curso c on c.id_curso = mc.id_curso ");
		sql.append(" inner join ensino.grau_academico ga on ga.id_grau_academico = mc.id_grau_academico ");
		sql.append(" inner join comum.municipio m  on m.id_municipio = c.id_municipio ");
		sql.append(" inner join ensino.turno t on t.id_turno = mc.id_turno "); 
		sql.append("		where d.nivel = '"+NivelEnsino.GRADUACAO+"'");

		sql.append(" and d.status = "+StatusDiscente.CONCLUIDO);
		sql.append(" and d.id_curso in ( select id_curso from curso");
		sql.append(" where id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL);
		sql.append("  and id_convenio is null ");
		sql.append("  and nivel = '"+NivelEnsino.GRADUACAO+"')" );
		sql.append(" and ( id_discente in ( select id_discente from ensino.movimentacao_aluno");
		sql.append("       where  ano_referencia = "+ano);
		sql.append("		       and ativo = trueValue() and ( apostilamento = "+SQLDialect.FALSE+" or apostilamento is null ) " +
				                 " AND ID_TIPO_MOVIMENTACAO_ALUNO = "+TipoMovimentacaoAluno.CONCLUSAO+")  )"); 
		sql.append(" group by d.id_curso, c.id_curso, c.nome, ga.id_grau_academico, t.id_turno, ga.descricao, m.nome, tipo, c.id_unidade, t.sigla 	");
		
		sql.append("		union all ");
		
		sql.append(" select count(d.id_discente) as total, c.id_curso, c.nome, ga.id_grau_academico, t.id_turno, " +
		" ga.descricao as grau, m.nome as cidade, c.id_unidade, t.sigla as turno, 'G' as tipo "); 
		sql.append("	from graduacao.discente_graduacao dg ");
		sql.append(" inner join public.discente d on (d.id_discente = dg.id_discente_graduacao) ");
		sql.append(" inner join ensino.forma_ingresso fi on (fi.id_forma_ingresso = d.id_forma_ingresso) ");
		sql.append(" inner join graduacao.matriz_curricular mc on mc.id_matriz_curricular = dg.id_matriz_curricular ");
		sql.append(" inner join public.curso c on c.id_curso = mc.id_curso ");
		sql.append(" inner join ensino.grau_academico ga on ga.id_grau_academico = mc.id_grau_academico ");
		sql.append(" inner join comum.municipio m  on m.id_municipio = c.id_municipio ");
		sql.append(" inner join ensino.turno t on t.id_turno = mc.id_turno ");		
		sql.append(" where d.nivel = '"+NivelEnsino.GRADUACAO+"'");

		sql.append(" and d.status = "+StatusDiscente.GRADUANDO);
		sql.append(" and dg.cola_grau = "+SQLDialect.TRUE +" and d.id_curso in ( select id_curso from curso"); 
		sql.append(" where id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL);
		sql.append("  and id_convenio is null ");
		sql.append("  and nivel = '"+NivelEnsino.GRADUACAO+"')");
		sql.append(" and ((f_discente_graduando(id_discente,"+ano+", 1, null) = trueValue())"); 
		sql.append(" or (f_discente_graduando(id_discente, "+ano+", 2, null) = trueValue()))"); 	
		sql.append(" group by d.id_curso, c.id_curso, c.nome, ga.id_grau_academico, t.id_turno, ga.descricao, m.nome, tipo, c.id_unidade, t.sigla 	");
		sql.append(" ) as d");  
		sql.append(" inner join comum.unidade u  on u.id_unidade = d.id_unidade");
		sql.append(" group by d.tipo, u.sigla, u.id_unidade, d.id_curso, d.id_grau_academico, d.id_turno, d.nome, d.grau, d.cidade, d.turno ");
		sql.append(" ) as a ");  
		sql.append(" group by sigla, id_unidade, id_curso, id_grau_academico, id_turno, nome, grau, cidade, turno ");
		sql.append(" order by sigla, nome");		

		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sql.toString());
		return rs;
	}	
	
	/**
	 * Retorna a lista de discentes com situação determinada no atributo tipo.
	 * Tipo = (1-Concluintes, 2-Ingressos)
	 * Usado no Relatório de Taxa de Conclusão.
	 * @param ano
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaDiscentes(int ano, int idCurso, int idUnidade, int tipo, int idGrauAcademico, int idTurno) throws DAOException {	
		String sqlconsulta = 		
			"	SELECT * FROM ( "
			+"			SELECT DISTINCT MC.ID_MATRIZ_CURRICULAR,U.ID_UNIDADE,U.SIGLA,C.ID_CURSO, "
			+"		       ME.DESCRICAO AS ME_MODALIDADE, "
			+"		       C.NOME,G.DESCRICAO AS MODALIDADE,H.NOME AS HABILITACAO,MU.NOME AS CIDADE, "
			+"		       T.SIGLA AS TURNO,D.ID_DISCENTE,D.MATRICULA,P.NOME AS DISCENTE_NOME,P.SEXO,S.DESCRICAO AS STATUS, FI.DESCRICAO AS FORMA_INGRESSO, D.PERIODO_INGRESSO, "
			+"			CASE "
			+"				WHEN (D.STATUS="+StatusDiscente.CONCLUIDO+" AND (SELECT COUNT(*) " 
			+"		                             FROM ENSINO.MOVIMENTACAO_ALUNO MA " 
			+"		                            WHERE MA.ID_DISCENTE=D.ID_DISCENTE "
			+"		                              AND MA.ano_referencia="+ano
			+"					      AND MA.periodo_referencia=1 " 
			+"		                              AND MA.ID_TIPO_MOVIMENTACAO_ALUNO=1 " 
			+"		                              AND ((MA.ATIVO) OR (MA.ATIVO IS NULL)) "
			+"		                           )>0 "
			+"		           		) THEN 1 "
			+"				WHEN (D.STATUS="+StatusDiscente.CONCLUIDO+" AND (SELECT COUNT(*) " 
			+"		                             FROM ENSINO.MOVIMENTACAO_ALUNO MA " 
			+"		                            WHERE MA.ID_DISCENTE=D.ID_DISCENTE "
			+"		                              AND MA.ano_referencia="+ano
			+"					      AND MA.periodo_referencia=2 " 
			+"		                              AND MA.ID_TIPO_MOVIMENTACAO_ALUNO="+TipoMovimentacaoAluno.CONCLUSAO
			+"		                              AND ((MA.ATIVO) OR (MA.ATIVO IS NULL)) "
			+"		                           )>0 "
			+"		           		) THEN 2 "
			+"				ELSE (SELECT PERIODO " 
			+"					FROM ENSINO.MATRICULA_COMPONENTE " 
			+"					WHERE ID_DISCENTE=D.ID_DISCENTE " 
			+"					AND ID_SITUACAO_MATRICULA IN ( "+SituacaoMatricula.APROVADO.getId()+" ) "
			+"					ORDER BY ANO DESC, PERIODO DESC "
			+"					LIMIT 1) "
			+"			END AS SEMESTRE_SAIDA, P.DATA_NASCIMENTO "
			+"		 FROM PUBLIC.DISCENTE D " 
			+"		 LEFT  JOIN PUBLIC.STATUS_DISCENTE S		ON S.STATUS=D.STATUS "
			+"		 LEFT  JOIN ENSINO.FORMA_INGRESSO FI		ON FI.ID_FORMA_INGRESSO=D.ID_FORMA_INGRESSO "
			+"		 INNER JOIN COMUM.PESSOA P 			    ON P.ID_PESSOA=D.ID_PESSOA "
			+"		 INNER JOIN PUBLIC.CURSO C 				    ON C.ID_CURSO=D.ID_CURSO "
			+"		 INNER JOIN COMUM.MUNICIPIO MU             ON MU.ID_MUNICIPIO=C.ID_MUNICIPIO "
			+"		 INNER JOIN COMUM.UNIDADE  U 				ON U.ID_UNIDADE=C.ID_UNIDADE "
			+"		 INNER JOIN COMUM.MODALIDADE_EDUCACAO ME   ON ME.ID_MODALIDADE_EDUCACAO=C.ID_MODALIDADE_EDUCACAO "
			+"		 INNER JOIN GRADUACAO.DISCENTE_GRADUACAO DG   ON DG.ID_DISCENTE_GRADUACAO=D.ID_DISCENTE "
			+"		 INNER JOIN GRADUACAO.MATRIZ_CURRICULAR MC  ON MC.ID_MATRIZ_CURRICULAR=DG.ID_MATRIZ_CURRICULAR "
			+"		  LEFT  JOIN ENSINO.GRAU_ACADEMICO G		ON G.ID_GRAU_ACADEMICO=MC.ID_GRAU_ACADEMICO "
			+"		  LEFT  JOIN GRADUACAO.HABILITACAO H		ON H.ID_HABILITACAO=MC.ID_HABILITACAO "
			+"		  INNER  JOIN ENSINO.TURNO T					ON T.ID_TURNO=MC.ID_TURNO "        
			+"		 WHERE C.NIVEL='"+NivelEnsino.GRADUACAO+"' "+(idUnidade > 0 ? " and u.id_unidade = "+idUnidade : " ") 	
			+        (idCurso > 0 ? " and c.id_curso = "+idCurso : "")
			+ "      and ME.ID_MODALIDADE_EDUCACAO = "+ModalidadeEducacao.PRESENCIAL
			+"      and c.id_convenio is null ";
					
			if (tipo == 1) {					
				sqlconsulta += "		 and ((d.status = "+StatusDiscente.CONCLUIDO   
				+"		      and d.id_discente in ( select id_discente from ensino.movimentacao_aluno where  ANO_referencia = "+ano 
				+" and PERIODO_referencia in (1,3) and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ativo = trueValue()" +
						"and ( apostilamento = "+SQLDialect.FALSE+" or apostilamento is null )) or "  
				+"		      d.status = "+StatusDiscente.GRADUANDO +"  and f_discente_graduando(id_discente, "+ano+", 1, null) = trueValue() " +
						" and dg.cola_grau = "+SQLDialect.TRUE+") or  "
				+"		      (d.status = "+StatusDiscente.CONCLUIDO
				+"			and d.id_discente in ( select id_discente from ensino.movimentacao_aluno where  ANO_referencia = "+ano
				+" and PERIODO_referencia in (2,4) and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ativo = trueValue()" +
						" and ( apostilamento = "+SQLDialect.FALSE+" or apostilamento is null )) or "  
				+"			    d.status = "+StatusDiscente.GRADUANDO  
				+"			and f_discente_graduando(id_discente, "+ano+", 2, null) = trueValue() and dg.cola_grau = "+SQLDialect.TRUE+") " 
				+"		      ) ";
			} else {
				sqlconsulta += "  and ((ano_ingresso = "+(ano-5)+" and periodo_ingresso = 2) " 
				+"			    or (ano_ingresso = "+(ano-4)+" and periodo_ingresso = 1)) ";	
			}
			
			if (idGrauAcademico > 0)
				sqlconsulta += " and G.ID_GRAU_ACADEMICO = "+idGrauAcademico;
			
			if (idTurno > 0)
				sqlconsulta += " and T.ID_TURNO = "+idTurno;			
			
			sqlconsulta += "		) A "
			+"		 ORDER BY ASCII(ME_MODALIDADE),ME_MODALIDADE,SIGLA,CIDADE,NOME,MODALIDADE,HABILITACAO,TURNO,DISCENTE_NOME ";
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}			

	
	/**
	 * Retorna a quantidade de discentes concluídos por ano e período.
	 * @param anoInicio
	 * @param periodoInicio
	 * @param anoFim
	 * @param periodoFim
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTotalDiscentesConcluidos(int anoInicio, int anoFim) throws DAOException {	
		String sqlconsulta = "select sum(total) as total, ano, periodo from  (	";
		
		String sql = "";
		for (int i = anoInicio; i <= anoFim; i++){					
			for (int j = 1; j <= 2; j++){	// faz para cada semestre
				if (!sql.isEmpty())
					sql += " union all ";
				sql += 
					"  select count(id_discente) as total, "+i+" as ano, "+j+" as periodo, 'C' as tipo "
					+"	from discente d "
					+"  join graduacao.discente_graduacao dg on d.id_discente = dg.id_discente_graduacao "
					+"  join curso c using (id_curso) "
					+"  join ensino.movimentacao_aluno ma using (id_discente) "
					+"	join ensino.forma_ingresso fi using (id_forma_ingresso) "
					+"	where d.nivel = '"+NivelEnsino.GRADUACAO+"'"
					+"	and d.status = "+StatusDiscente.CONCLUIDO
					+"  and c.id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL
					+"  and c.id_convenio is null "
					+"  and c.nivel = '"+NivelEnsino.GRADUACAO+"'" // regras do curso
					+"  and ma.id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ma.ativo = "+SQLDialect.TRUE 
					+"  and ma.ano_referencia = "+i+" and ma.periodo_referencia = "+j 
					+"  and ( ma.apostilamento = "+SQLDialect.FALSE+" or ma.apostilamento is null ) "// regras da movimentacao
					+"  and fi.id_forma_ingresso in (34110,34132,34117,51252808) " // VESTIBULAR / REINGRESSO DE GRADUADO / PEC-G / SiSU
					+"	union all "
					+"	select count(id_discente) as total, "+i+" as ano, "+j+" as periodo, 'G' as tipo"
					+"	from discente d "					
					+" join graduacao.discente_graduacao dg on d.id_discente = dg.id_discente_graduacao "
					+" join curso c using (id_curso) "
					+" join ensino.forma_ingresso fi using (id_forma_ingresso) "
					+" where d.nivel = '"+NivelEnsino.GRADUACAO+"' and d.status = "+StatusDiscente.GRADUANDO// regras do discente
					+" and c.id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL
					+" and c.id_convenio is null and c.nivel = '"+NivelEnsino.GRADUACAO+"'"// -- regras do curso
					+" and f_discente_graduando(id_discente, "+i+", "+j+", null) = "+SQLDialect.TRUE
					+" and dg.cola_grau = "+SQLDialect.TRUE
					+" and fi.id_forma_ingresso in (34110,34132,34117,51252808) ";	// VESTIBULAR / REINGRESSO DE GRADUADO / PEC-G / SiSU				
			}			
		}				
	    sqlconsulta += sql + ") as a group by a.ano, a.periodo order by a.ano, a.periodo";

		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}	
	
	/**
	 * Retorna a quantidade de vagas ofertadas por ano e período.
	 * @param anoInicio
	 * @param anoFim
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTotalVagasOfertadasTaxaConclusao(int anoInicio, int anoFim) throws DAOException {	
		String sqlconsulta = 		
		"	select ovc.ano, sum(vagas_periodo_1) as periodo1, sum(vagas_periodo_2) as periodo2 "
		+"	from ensino.oferta_vagas_curso ovc  "
		+"	join curso c on ovc.id_curso = c.id_curso"  
		+"	join comum.unidade u on c.id_unidade = u.id_unidade"  
		+"	join ensino.forma_ingresso fi on ovc.id_forma_ingresso = fi.id_forma_ingresso"  
		+"	left join graduacao.matriz_curricular mc on ovc.id_matriz_curricular = mc.id_matriz_curricular"  
		+"	left join ensino.turno tu on mc.id_turno = tu.id_turno"  
		+"	where c.nivel = '"+NivelEnsino.GRADUACAO+"'" 
		+"	and ovc.ano between "+anoInicio+" and "+anoFim
		+"	and c.id_convenio is null"
		+"	and c.id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL
		+"  and fi.id_forma_ingresso in (34110,34132,34117,51252808) "
		+"	group by ovc.ano"
		+"	order by ovc.ano";				
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}	
	
	/**
	 * Retorna a lista de vagas ofertadas por ano.
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaVagasOfertadasTaxaConclusao(int ano) throws DAOException {	
		String sqlconsulta = 		
		"	select ovc.ano as ano, c.nome as nome, "  
		+"  u.sigla as unidade_sigla, tu.sigla as turno_sigla , fi.id_forma_ingresso, fi.descricao as forma_descricao,  "
		+"  vagas_periodo_1 as vagas1, vagas_periodo_2 as vagas2 " 
		+"	from ensino.oferta_vagas_curso ovc  "
		+"	join curso c on ovc.id_curso = c.id_curso"  
		+"	join comum.unidade u on c.id_unidade = u.id_unidade"  
		+"	join ensino.forma_ingresso fi on ovc.id_forma_ingresso = fi.id_forma_ingresso"  
		+"	left join graduacao.matriz_curricular mc on ovc.id_matriz_curricular = mc.id_matriz_curricular"  
		+"	left join ensino.turno tu on mc.id_turno = tu.id_turno"  
		+"	where c.nivel = '"+NivelEnsino.GRADUACAO+"'" 
		+"	and ovc.ano = "+ano
		+"	and c.id_convenio is null"
		+"	and c.id_modalidade_educacao = "+ModalidadeEducacao.PRESENCIAL
		+"	order by ovc.ano, fi.descricao desc, c.nome ";				
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}	


	/**
	 * Retorna a quantidade de discentes concluídos por ano e período.
	 * @param anoInicio
	 * @param anoFim
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTotalDiscentesIngressantesTaxaConclusao(int anoInicio, int anoFim) throws DAOException {	
		String sqlconsulta = 		
		 " select count(id_discente) as total, ano_ingresso as ano, periodo_ingresso as periodo " 
		+" from discente d "
		+" inner join ensino.forma_ingresso fi on (fi.id_forma_ingresso = d.id_forma_ingresso) "
		+"  where d.nivel = '"+NivelEnsino.GRADUACAO+"'" 
		+" and  d.ano_ingresso between "+anoInicio+" and "+anoFim
		+ getCriterioFormasIngresso()
		+" and d.id_curso in ( select id_curso from curso " 
		+"  where id_modalidade_educacao =  "+ModalidadeEducacao.PRESENCIAL
		+"   and id_convenio is null "
		+"   and nivel = '"+NivelEnsino.GRADUACAO+"') "
		+" group by ano, periodo order by ano, periodo ";		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}
	
	/**
	 * Retorna o quantitativos de alunos concluintes por Semestre, Turno e Sexo
	 * @param ano
	 * @param idUnidade
	 * @return
	 */
	public List<Map<String, Object>> findQuantitativoConcluintes(int ano, int idUnidade){		
		String sqlconsulta = 
		"	select IDMODALIDADE, ME_MODALIDADE, id_unidade, sigla, id_curso, nome, periodo_turno, sexo, semestre, sum(quant_discentes) as total "
		+"	from ( "			
		+"	SELECT distinct  "
		+"       CASE "
		+"           WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN 2 "		
		+"           WHEN ME.ID_MODALIDADE_EDUCACAO = "+ModalidadeEducacao.A_DISTANCIA+" THEN 1 "		
		+"           WHEN ME.ID_MODALIDADE_EDUCACAO = "+ModalidadeEducacao.PRESENCIAL+" THEN 4 "		
		+"           ELSE 3 "
		+"       END AS INDICE,	" 
		+"       CASE "
		+"           WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN -1 "		
		+"           ELSE ME.ID_MODALIDADE_EDUCACAO "
		+"       END AS IDMODALIDADE, "
		+"       CASE "
		+"           WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN 'PROBASICA' "		
		+"           ELSE upper(ME.DESCRICAO) "
		+"       END AS ME_MODALIDADE, U.ID_UNIDADE, U.SIGLA, C.ID_CURSO, "
		+"       CASE " 
		+"           WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN C.NOME ||' - '|| M.nome "
		+"           ELSE C.NOME "
		+"       END AS nome, "
		+"       CASE "
		+"           WHEN t.periodo <> 'N' THEN 'D' "
		+"           ELSE 'N' "
		+"       END AS periodo_turno, p.sexo, cast(1 as integer) as semestre, "
		+"       count( distinct d.id_discente) as quant_discentes "
		+" FROM GRADUACAO.MATRIZ_CURRICULAR MC "
		+"  INNER JOIN PUBLIC.CURSO C 		   ON C.ID_CURSO = MC.ID_CURSO "
		+"  INNER JOIN COMUM.MODALIDADE_EDUCACAO ME  ON ME.ID_MODALIDADE_EDUCACAO = C.ID_MODALIDADE_EDUCACAO "
		+"  INNER JOIN COMUM.UNIDADE  U 		   ON U.ID_UNIDADE = C.ID_UNIDADE "
		+"  INNER JOIN DISCENTE D 	   	   ON D.ID_CURSO = C.ID_CURSO "
		+"  INNER JOIN COMUM.PESSOA P 		   ON P.ID_PESSOA = D.ID_PESSOA "
		+"  INNER JOIN GRADUACAO.CURRICULO CU	        ON CU.ID_CURRICULO=D.ID_CURRICULO "
		+"  INNER JOIN GRADUACAO.MATRIZ_CURRICULAR MC2	ON MC2.ID_MATRIZ_CURRICULAR=CU.ID_MATRIZ "  
		+"  INNER JOIN ENSINO.TURNO T		   ON T.ID_TURNO=MC2.ID_TURNO "
		+"  left join comum.municipio m on (m.id_municipio = c.id_municipio) "
		+"  inner join ensino.forma_ingresso fi on (fi.id_forma_ingresso = d.id_forma_ingresso) "
		+"  join graduacao.discente_graduacao dg on d.id_discente = dg.id_discente_graduacao "
		+" where c.nivel = '"+NivelEnsino.GRADUACAO+"' "+(idUnidade > 0 ? " and u.id_unidade = "+idUnidade : " ")
		+" and c.id_convenio is null "
		+" and (d.status = "+StatusDiscente.CONCLUIDO 
		+" and d.id_discente in ( select id_discente from ensino.movimentacao_aluno ma " +
		" 				where  ANO_referencia = "+ano+" and PERIODO_referencia in (1,3) " +
					"	and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" " +
							" and ativo = trueValue() and ( ma.apostilamento = "+SQLDialect.FALSE+" or ma.apostilamento is null )) or " 
		+"    d.status = "+StatusDiscente.GRADUANDO
		+" and f_discente_graduando(id_discente, "+ano+", 1, null) = trueValue() and dg.cola_grau = "+SQLDialect.TRUE+") "
		+" group by INDICE, IDMODALIDADE, ME.ID_MODALIDADE_EDUCACAO, ME_MODALIDADE, U.ID_UNIDADE, U.SIGLA, C.ID_CURSO, m.nome, C.NOME, periodo_turno, p.sexo, semestre "
		+" union "
		+" SELECT distinct  "
		+"       CASE "
		+"           WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN 2 "		
		+"           WHEN ME.ID_MODALIDADE_EDUCACAO = "+ModalidadeEducacao.A_DISTANCIA+" THEN 1 "		
		+"           WHEN ME.ID_MODALIDADE_EDUCACAO = "+ModalidadeEducacao.PRESENCIAL+" THEN 4 "		
		+"           ELSE 3 "
		+"       END AS INDICE,	"
		+"       CASE "
		+"           WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN -1 "		
		+"           ELSE ME.ID_MODALIDADE_EDUCACAO "
		+"       END AS IDMODALIDADE, "		
		+"       CASE "
		+"           WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN 'PROBASICA' "	
		+"           ELSE upper(ME.DESCRICAO) "
		+"       END AS ME_MODALIDADE, U.ID_UNIDADE, U.SIGLA, C.ID_CURSO, "
		+"       CASE " 
		+"           WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN C.NOME ||' - '|| M.nome "
		+"           ELSE C.NOME "
		+"       END AS nome, "		
		+"       CASE "
		+"           WHEN t.periodo <> 'N' THEN 'D' "
		+"           ELSE t.periodo "
		+"       END AS periodo_turno, p.sexo, cast(2 as integer) as semestre, "
		+"       count( distinct d.id_discente) as quant_discentes "
		+" FROM GRADUACAO.MATRIZ_CURRICULAR MC "
		+"  INNER JOIN PUBLIC.CURSO C 		   ON C.ID_CURSO = MC.ID_CURSO "
		+"  INNER JOIN COMUM.MODALIDADE_EDUCACAO ME  ON ME.ID_MODALIDADE_EDUCACAO = C.ID_MODALIDADE_EDUCACAO "
		+"  INNER JOIN COMUM.UNIDADE  U 		   ON U.ID_UNIDADE = C.ID_UNIDADE "
		+"  INNER JOIN DISCENTE D 	   	   ON D.ID_CURSO = C.ID_CURSO "
		+"  INNER JOIN COMUM.PESSOA P 		   ON P.ID_PESSOA = D.ID_PESSOA "
		+"  INNER JOIN GRADUACAO.CURRICULO CU	        ON CU.ID_CURRICULO=D.ID_CURRICULO "
		+"  INNER JOIN GRADUACAO.MATRIZ_CURRICULAR MC2	ON MC2.ID_MATRIZ_CURRICULAR=CU.ID_MATRIZ "  
		+"  LEFT  JOIN ENSINO.TURNO T		   ON T.ID_TURNO=MC2.ID_TURNO "
		+"  left join comum.municipio m on (m.id_municipio = c.id_municipio) "
		+"  inner join ensino.forma_ingresso fi on (fi.id_forma_ingresso = d.id_forma_ingresso) "
		+"  join graduacao.discente_graduacao dg on d.id_discente = dg.id_discente_graduacao "
		+" where c.nivel = '"+NivelEnsino.GRADUACAO+"' "+(idUnidade > 0 ? " and u.id_unidade = "+idUnidade : " ") 
		+"  and c.id_convenio is null "
		+" and (d.status = "+StatusDiscente.CONCLUIDO 
		+" and d.id_discente in ( select id_discente from ensino.movimentacao_aluno ma " +
		" 				where  ANO_referencia = "+ano+" and PERIODO_referencia in (2,4) " +
					"	and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" " +
							" and ativo = trueValue() and ( ma.apostilamento = "+SQLDialect.FALSE+" or ma.apostilamento is null )) or "				
		+"	    d.status = "+StatusDiscente.GRADUANDO 
		+"	and f_discente_graduando(id_discente, "+ano+", 2, null) = trueValue() and dg.cola_grau = "+SQLDialect.TRUE+") "
		+"	group by INDICE, IDMODALIDADE, ME.ID_MODALIDADE_EDUCACAO, ME_MODALIDADE, U.ID_UNIDADE, U.SIGLA, C.ID_CURSO, m.nome, C.NOME, periodo_turno, p.sexo, semestre "
		+"	order by nome, semestre "
		+"	) as a "
		+"	group by IDMODALIDADE, INDICE, ME_MODALIDADE, ID_UNIDADE, SIGLA, ID_CURSO, NOME, periodo_turno, sexo, semestre "
		+" order by INDICE, sigla, NOME, id_curso, Semestre, periodo_turno, sexo desc ";			
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}
	
	/**
	 * Retorna a lista de alunos concluintes conforme os parâmetros passados.
	 * (Também é chamado pelo relatório : 3.7.1  RelatoriosPlanejamentoMBean.exibirRelatorioConcluintesDetalhes())
	 * @param ano
	 * @param idUnidade
	 * @param idCurso
	 * @param sexo
	 * @param turno
	 * @param semestre
	 * @param idModalidadeEnsino
	 * @return
	 */
	public List<Map<String, Object>> findConcluintes(int ano, int idUnidade, int idCurso, String sexo, String turno, int semestre, int idModalidadeEnsino){
		String sqlconsulta =
		"	SELECT * FROM ( "
		+"			SELECT DISTINCT MC.ID_MATRIZ_CURRICULAR,U.ID_UNIDADE,U.SIGLA,C.ID_CURSO, "
		+"			       CASE "
		+"		            WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN ' PROBASICA' "
		+"		            ELSE ME.DESCRICAO "
		+"		       END AS ME_MODALIDADE, "
		+"		       C.NOME,G.DESCRICAO AS MODALIDADE,H.NOME AS HABILITACAO,MU.NOME AS CIDADE, "
		+"		       T.SIGLA AS TURNO,D.ID_DISCENTE,D.MATRICULA,P.NOME AS DISCENTE_NOME,P.SEXO, "
		+"			   S.DESCRICAO AS STATUS, FI.DESCRICAO AS FORMA_INGRESSO, D.PERIODO_INGRESSO, MPO.NOME AS MUNICIPIO_POLO, "
		+"			CASE "
		+"				WHEN (D.STATUS="+StatusDiscente.CONCLUIDO+" AND (SELECT COUNT(*) " 
		+"		                             FROM ENSINO.MOVIMENTACAO_ALUNO MA " 
		+"		                            WHERE MA.ID_DISCENTE=D.ID_DISCENTE "
		+"		                              AND MA.ano_referencia="+ano
		+"					      AND MA.periodo_referencia=1 " 
		+"		                              AND MA.ID_TIPO_MOVIMENTACAO_ALUNO= "+TipoMovimentacaoAluno.CONCLUSAO 
		+"		                              AND ((MA.ATIVO) OR (MA.ATIVO IS NULL)) "
		+"		                           )>0 "
		+"		           		) THEN 1 "
		+"				WHEN (D.STATUS="+StatusDiscente.CONCLUIDO+" AND (SELECT COUNT(*) " 
		+"		                             FROM ENSINO.MOVIMENTACAO_ALUNO MA " 
		+"		                            WHERE MA.ID_DISCENTE=D.ID_DISCENTE "
		+"		                              AND MA.ano_referencia="+ano
		+"					      AND MA.periodo_referencia=2 " 
		+"		                              AND MA.ID_TIPO_MOVIMENTACAO_ALUNO="+TipoMovimentacaoAluno.CONCLUSAO
		+"		                              AND ((MA.ATIVO) OR (MA.ATIVO IS NULL)) "
		+"		                           )>0 "
		+"		           		) THEN 2 "
		+"				ELSE (SELECT PERIODO " 
		+"					FROM ENSINO.MATRICULA_COMPONENTE " 
		+"					WHERE ID_DISCENTE=D.ID_DISCENTE " 
		+"					AND ID_SITUACAO_MATRICULA IN ( "+SituacaoMatricula.APROVADO.getId()+" ) "
		+"					ORDER BY ANO DESC, PERIODO DESC "
		+"					LIMIT 1) "
		+"			END AS SEMESTRE_SAIDA, P.DATA_NASCIMENTO "
		+"		 FROM PUBLIC.DISCENTE D " 
		+"		 LEFT  JOIN PUBLIC.STATUS_DISCENTE S		ON S.STATUS=D.STATUS "
		+"		 LEFT  JOIN ENSINO.FORMA_INGRESSO FI		ON FI.ID_FORMA_INGRESSO=D.ID_FORMA_INGRESSO "
		+"		 INNER JOIN COMUM.PESSOA P 			    ON P.ID_PESSOA=D.ID_PESSOA "
		+"		 INNER JOIN PUBLIC.CURSO C 				    ON C.ID_CURSO=D.ID_CURSO "
		+"		 INNER JOIN COMUM.MUNICIPIO MU             ON MU.ID_MUNICIPIO=C.ID_MUNICIPIO "
		+"		 INNER JOIN COMUM.UNIDADE  U 				ON U.ID_UNIDADE=C.ID_UNIDADE "
		+"		 INNER JOIN COMUM.MODALIDADE_EDUCACAO ME   ON ME.ID_MODALIDADE_EDUCACAO=C.ID_MODALIDADE_EDUCACAO "
		+"		 INNER JOIN GRADUACAO.DISCENTE_GRADUACAO DG   ON DG.ID_DISCENTE_GRADUACAO=D.ID_DISCENTE "
		+"  	 LEFT JOIN EAD.POLO PO on (DG.ID_POLO = PO.ID_POLO)"
		+"  	 LEFT JOIN COMUM.MUNICIPIO MPO on (MPO.ID_MUNICIPIO = PO.ID_CIDADE)"
		+"		 INNER JOIN GRADUACAO.MATRIZ_CURRICULAR MC  ON MC.ID_MATRIZ_CURRICULAR=DG.ID_MATRIZ_CURRICULAR "
		+"		  LEFT  JOIN ENSINO.GRAU_ACADEMICO G		ON G.ID_GRAU_ACADEMICO=MC.ID_GRAU_ACADEMICO "
		+"		  LEFT  JOIN GRADUACAO.HABILITACAO H		ON H.ID_HABILITACAO=MC.ID_HABILITACAO "
		+"		  INNER  JOIN ENSINO.TURNO T					ON T.ID_TURNO=MC.ID_TURNO "        
		+"		 WHERE C.NIVEL='"+NivelEnsino.GRADUACAO+"' "+(idUnidade > 0 ? " and u.id_unidade = "+idUnidade : " ") 	
		+        (idCurso > 0 ? " and c.id_curso = "+idCurso : "")
		+        (sexo != null ? " and p.sexo = '"+sexo+"'" : "")
		+"       and c.id_convenio is null "
		
		+		 (turno != null ? (turno.equals("N") ? "and t.periodo = 'N'" : "and t.periodo <> 'N'" ) : "") 
		+        (idModalidadeEnsino > 0 ? " and ME.ID_MODALIDADE_EDUCACAO = "+idModalidadeEnsino : "")				
		+		(semestre > 0 ? 
				"	 and ((d.status = "+StatusDiscente.CONCLUIDO   
				+"	     and d.id_discente in ( select id_discente from ensino.movimentacao_aluno where  ANO_referencia = "+ano+" and PERIODO_referencia in ("+(semestre == 1 ? "1,3" : "2,4")+") and ( apostilamento = "+SQLDialect.FALSE+" or apostilamento is null ) and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ativo = trueValue()) or "  
				+"	      d.status = "+StatusDiscente.GRADUANDO +"  and f_discente_graduando(id_discente, "+ano+", "+semestre+", null) = trueValue() and dg.cola_grau = "+SQLDialect.TRUE+")) "
				:
		"		 and ((d.status = "+StatusDiscente.CONCLUIDO   
		+"		      and d.id_discente in ( select id_discente from ensino.movimentacao_aluno where  ANO_referencia = "+ano+" and PERIODO_referencia in (1,3) and ( apostilamento = "+SQLDialect.FALSE+" or apostilamento is null ) and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ativo = trueValue()) or "  
		+"		      d.status = "+StatusDiscente.GRADUANDO +"  and f_discente_graduando(id_discente, "+ano+", 1, null) = trueValue() and dg.cola_grau = "+SQLDialect.TRUE+") or  "
		+"		      (d.status = "+StatusDiscente.CONCLUIDO
		+"			and d.id_discente in ( select id_discente from ensino.movimentacao_aluno where  ANO_referencia = "+ano+" and PERIODO_referencia in (2,4) and ( apostilamento = "+SQLDialect.FALSE+" or apostilamento is null )  and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ativo = trueValue()) or "  
		+"			    d.status = "+StatusDiscente.GRADUANDO  
		+"			and f_discente_graduando(id_discente, "+ano+", 2, null) = trueValue() and dg.cola_grau = "+SQLDialect.TRUE+") " 
		+"		      ) "  ) // fim semestre
		+"		) A "
		+"		 ORDER BY ASCII(ME_MODALIDADE),ME_MODALIDADE,SIGLA,CIDADE,NOME,MODALIDADE,HABILITACAO,TURNO,DISCENTE_NOME ";
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}
	
	/**
	 * Retorna um relatório de discentes ingressantes para o relatório de taxa de conclusão.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaIngressantesTaxaConclusao(int ano,	int periodo)
			throws DAOException {
		// sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				"select distinct u.sigla as centro, c.id_curso, c.nome as curso_nome, "
						+ " t.id_turno, t.descricao as turno_descricao, t.sigla, "
						+ "  d.matricula,d.ano_ingresso, d.periodo_ingresso, p.nome as aluno_nome, ga.descricao as modalidade_nome, "
						+ " h.nome as habilitacao_nome, fi.id_forma_ingresso, d.status, "
						+ "  fi.descricao as forma_ingresso_descricao, sd.descricao as discente_status, m.nome as municipio_nome, mc.id_matriz_curricular "
						+ " from comum.pessoa p, discente d, comum.unidade u, curso c, "
						+ "  ensino.turno t, ensino.forma_ingresso fi, graduacao.matriz_curricular mc left outer join graduacao.habilitacao h using (id_habilitacao), "
						+ "  status_discente sd, graduacao.discente_graduacao dg, ensino.grau_academico ga, comum.municipio m"
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
						+ "  and c.nivel = '"+NivelEnsino.GRADUACAO+"'");

		sqlconsulta.append(getCriterioFormasIngresso());
		
		sqlconsulta.append(" and d.ano_ingresso = " + ano);
		sqlconsulta.append(" and d.periodo_ingresso = " + periodo);
		sqlconsulta.append(
				" and c.id_curso in ( select id_curso from curso " 
				+"  where id_modalidade_educacao =  "+ModalidadeEducacao.PRESENCIAL
				+"   and id_convenio is null "
				+"   and nivel = '"+NivelEnsino.GRADUACAO+"') "						
		);
		
		sqlconsulta.append(" order by u.sigla, c.nome, modalidade_nome, habilitacao_nome, t.sigla, p.nome ");
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}	
}
