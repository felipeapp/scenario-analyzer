/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 10/02/2011
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.stricto.relatorios.jsf.RelatorioConcluintePos;

/**
 * Dao responsável por realizar a consulta para emissão dos relatórios de
 * concluíntes
 * 
 * @author arlindo
 * 
 */
public class RelatoriosConcluintesDao extends GenericSigaaDAO {
	
	/**
	 * Retorna todos os formandos que são potenciais concluíntes
	 * 
	 * @param idCentro
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public List<Map<String,Object>> findPotenciaisConcluintes(int idCentro, int ano, int periodo){
		StringBuilder sql = new StringBuilder();
		
		sql.append(" select c.id_curso, c.nome as curso, mu.nome as cidade, ");
		sql.append("        d.matricula, p.nome, p.sexo, p.data_nascimento, d.id_discente, ");
		sql.append("        CASE ");
		sql.append("          WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN 'PROBASICA' ");		
		sql.append("        ELSE upper(ME.DESCRICAO) ");
		sql.append("        END AS ME_MODALIDADE, s.descricao as status, u.sigla ");		
		sql.append(" FROM PUBLIC.DISCENTE D ");  
		sql.append(" INNER JOIN COMUM.PESSOA P ON P.ID_PESSOA = D.ID_PESSOA "); 
		sql.append(" INNER JOIN PUBLIC.CURSO C ON C.ID_CURSO = D.ID_CURSO "); 
		sql.append(" INNER JOIN COMUM.MUNICIPIO MU ON MU.ID_MUNICIPIO = C.ID_MUNICIPIO ");
		sql.append(" INNER JOIN COMUM.UNIDADE  U ON U.ID_UNIDADE = C.ID_UNIDADE ");
		sql.append(" INNER JOIN COMUM.MODALIDADE_EDUCACAO ME   ON ME.ID_MODALIDADE_EDUCACAO=C.ID_MODALIDADE_EDUCACAO ");
		sql.append(" LEFT  JOIN PUBLIC.STATUS_DISCENTE S ON S.STATUS=D.STATUS ");
		sql.append(" where d.status = "+StatusDiscente.FORMANDO+" and d.nivel = '"+NivelEnsino.GRADUACAO+"' ");
		
		if (idCentro > 0)
			sql.append(" and u.id_unidade = "+idCentro);
		
		sql.append(" and exists ( ");
		sql.append(" 	  select mc.id_discente from ensino.matricula_componente mc ");
		sql.append("	  where mc.id_discente = d.id_discente ");
		sql.append(" 	  and mc.ano = "+ano);
		sql.append(" 	  and mc.periodo = "+periodo);
		sql.append("	  and mc.id_situacao_matricula in "+UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesPositivas()));
		sql.append(" ) ");
		sql.append(" and not exists ( ");
		sql.append(" 	  select mc.id_discente from ensino.matricula_componente mc "); 
		sql.append("      where mc.id_discente = d.id_discente ");
		sql.append("      and (mc.ano > "+ano+" or (mc.ano = "+ano+" and mc.periodo > "+periodo+"))");
		sql.append(" ) ");
		sql.append(" order by u.sigla, c.nome, mu.nome, p.nome ");					
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sql.toString());
		return rs;		
		
	}
	
	
	/**
	 * Retorna todos os concluíntes da pós-graduação do ano informado no parametro
	 * 
	 * @param idCentro
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public List<RelatorioConcluintePos> findConcluintesPosGraduacao(int ano){
		
		StringBuilder sql = new StringBuilder();
		sql.append("  ");
		
		
		sql.append(" SELECT DISTINCT U.ID_UNIDADE, U.NOME,  ");
		sql.append(" (SELECT COUNT(*)  ");
		sql.append(" 	FROM ENSINO.MOVIMENTACAO_ALUNO MA2 ");
		sql.append(" 	INNER JOIN PUBLIC.DISCENTE D2 ON D2.ID_DISCENTE=MA2.ID_DISCENTE ");
		sql.append(" 	INNER JOIN PUBLIC.CURSO C2 ON C2.ID_CURSO=D2.ID_CURSO ");
		sql.append(" 	WHERE MA2.ano_referencia = " + ano);
		sql.append(" 	AND MA2.periodo_referencia IN (1,3) ");
		sql.append(" 	AND MA2.ID_TIPO_MOVIMENTACAO_ALUNO=1 ");
		sql.append(" 	AND C2.ID_CURSO IN ( SELECT C_UNI.ID_CURSO FROM CURSO C_UNI WHERE ID_UNIDADE = U.ID_UNIDADE AND NIVEL = 'E' ) ");
		sql.append(" 	AND C2.NIVEL='E' ");
		sql.append(" ) AS MESTRADO_1_SEM, ");
		
		sql.append(" (SELECT COUNT(*) "); 
		sql.append(" 	FROM ENSINO.MOVIMENTACAO_ALUNO MA2 ");
		sql.append(" 	INNER JOIN PUBLIC.DISCENTE D2 ON D2.ID_DISCENTE=MA2.ID_DISCENTE ");
		sql.append(" 	INNER JOIN PUBLIC.CURSO C2 ON C2.ID_CURSO=D2.ID_CURSO ");
		sql.append(" 	WHERE MA2.ano_referencia = " + ano);
		sql.append(" 	AND MA2.periodo_referencia IN (1,3) ");
		sql.append(" 	AND MA2.ID_TIPO_MOVIMENTACAO_ALUNO=1 ");
		sql.append(" 	AND C2.ID_CURSO IN ( SELECT C_UNI.ID_CURSO FROM CURSO C_UNI WHERE ID_UNIDADE = U.ID_UNIDADE AND NIVEL = 'D' ) ");
		sql.append(" 	AND C2.NIVEL='D' ");
		sql.append(" ) AS DOUTORADO_1_SEM, ");
		
		sql.append(" (SELECT COUNT(*) "); 
		sql.append(" 	FROM ENSINO.MOVIMENTACAO_ALUNO MA2 ");
		sql.append(" 	INNER JOIN PUBLIC.DISCENTE D2 ON D2.ID_DISCENTE=MA2.ID_DISCENTE ");
		sql.append(" 	INNER JOIN PUBLIC.CURSO C2 ON C2.ID_CURSO=D2.ID_CURSO ");
		sql.append(" 	WHERE MA2.ano_referencia = " + ano);
		sql.append(" 	AND MA2.periodo_referencia IN (2,4) ");
		sql.append(" 	AND MA2.ID_TIPO_MOVIMENTACAO_ALUNO=1 ");
		sql.append(" 	AND C2.ID_CURSO IN ( SELECT C_UNI.ID_CURSO FROM CURSO C_UNI WHERE ID_UNIDADE = U.ID_UNIDADE AND NIVEL = 'E' ) ");
		sql.append(" 	AND C2.NIVEL='E' ");
		sql.append(" ) AS MESTRADO_2_SEM, ");
		
		sql.append(" (SELECT COUNT(*) "); 
		sql.append(" 	FROM ENSINO.MOVIMENTACAO_ALUNO MA2 ");
		sql.append(" 	INNER JOIN PUBLIC.DISCENTE D2 ON D2.ID_DISCENTE=MA2.ID_DISCENTE ");
		sql.append(" 	INNER JOIN PUBLIC.CURSO C2 ON C2.ID_CURSO=D2.ID_CURSO ");
		sql.append(" 	WHERE MA2.ano_referencia = " + ano);
		sql.append(" 	AND MA2.periodo_referencia IN (2,4) ");
		sql.append(" 	AND MA2.ID_TIPO_MOVIMENTACAO_ALUNO=1 ");
		sql.append(" 	AND C2.ID_CURSO IN ( SELECT C_UNI.ID_CURSO FROM CURSO C_UNI WHERE ID_UNIDADE = U.ID_UNIDADE AND NIVEL = 'D' ) ");
		sql.append(" 	AND C2.NIVEL='D' ");
		sql.append(" ) AS DOUTORADO_2_SEM ");
		
		sql.append(" FROM PUBLIC.CURSO C ");
		sql.append(" 	INNER JOIN COMUM.UNIDADE U ON C.ID_UNIDADE = U.ID_UNIDADE ");
		sql.append(" 	WHERE C.NIVEL IN ('D','E') ");
		sql.append(" 	AND U.ID_UNIDADE IN ( ");
		sql.append(" SELECT DISTINCT U.ID_UNIDADE ");
		sql.append(" 	FROM ENSINO.MOVIMENTACAO_ALUNO MA ");
		sql.append(" 	INNER JOIN PUBLIC.DISCENTE D ON D.ID_DISCENTE=MA.ID_DISCENTE ");
		sql.append(" 	INNER JOIN PUBLIC.CURSO C ON C.ID_CURSO=D.ID_CURSO ");
		sql.append(" 	INNER JOIN COMUM.UNIDADE U ON C.ID_UNIDADE = U.ID_UNIDADE ");
		sql.append(" 	WHERE MA.ano_referencia = " + ano);
		sql.append(" 	AND MA.ID_TIPO_MOVIMENTACAO_ALUNO=1 ");
		sql.append(" 	AND C.NIVEL IN ('D','E') ");
		sql.append(" ) ");
		sql.append(" ORDER BY U.NOME "); 
		
		
		List<RelatorioConcluintePos> relatorio = new ArrayList<RelatorioConcluintePos>();
		List<Map<String, Object>> rs = getJdbcTemplate().queryForList(sql.toString());
		
		int i = 0;
		for( Map<String, Object> linha : rs ){
			
			RelatorioConcluintePos rcp = new RelatorioConcluintePos();
			rcp.setIdUnidade( (Integer) linha.get("id_unidade") );
			rcp.setNome( (String) linha.get("nome") );
			
			rcp.setMestrado1sem( (Long) linha.get("MESTRADO_1_SEM") );
			rcp.setMestrado2sem( (Long) linha.get("DOUTORADO_1_SEM") );
			rcp.setDoutorado1sem( (Long) linha.get("MESTRADO_2_SEM") );
			rcp.setDoutorado2sem( (Long) linha.get("DOUTORADO_2_SEM") );
			
			relatorio.add(rcp);
			
		}
		
		
		return relatorio;		
		
	}
}

