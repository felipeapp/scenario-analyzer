/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/03/2011
 */
package br.ufrn.sigaa.relatoriosgestao.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * Dao que gera as consultas para geração do relatório de Planos de Cursos Cadastrados
 * 
 * @author arlindo
 *
 */
public class RelatorioPlanosCursosDao extends GenericSigaaDAO {
	
	/**
	 * Retorna os totais referente aos planos de curso cadastrados.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTotaisPlanoCursos(int ano, int periodo) throws HibernateException, DAOException{
		
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT u.id_unidade, u.nome AS unidade, g.id_gestora, g.sigla,  ");
		sql.append("   cast( count(DISTINCT t.id_turma) as integer) AS total_turmas,  ");
		sql.append("   cast( count(DISTINCT pc.id_plano_curso) as integer)  AS total_planos_cursos,   ");
		sql.append("   cast( count(DISTINCT t.id_turma) - count(DISTINCT pc.id_plano_curso) as integer)  AS faltam, ");
		sql.append("   cast( count(DISTINCT ir.id_indicacao_referencia) as integer)  AS total_referencia_indicadas,   ");
		sql.append("   cast( CASE WHEN count(DISTINCT ir.id_indicacao_referencia) = 0 THEN 0 ");
		sql.append("    WHEN count(DISTINCT pc.id_plano_curso) = 0 THEN 0   ");
		sql.append("   ELSE  (count(DISTINCT ir.id_indicacao_referencia) / count(DISTINCT pc.id_plano_curso))  ");
		sql.append("   END as integer)  AS media_referencia_por_plano ");
		
		sql.append(" FROM ensino.turma t ");		
		sql.append(" LEFT JOIN ensino.plano_curso pc ON pc.id_turma = t.id_turma and pc.finalizado = trueValue() ");
		sql.append(" INNER JOIN ensino.componente_curricular cc ON cc.id_disciplina = t.id_disciplina ");
		sql.append(" LEFT JOIN ava.indicacao_referencia ir ON ir.id_turma = t.id_turma  ");
		sql.append(" INNER JOIN comum.unidade u ON u.id_unidade = cc.id_unidade ");
		sql.append(" inner join comum.unidade g ON g.id_unidade = u.id_gestora ");
		
		sql.append(" WHERE t.ano = "+ano);
		sql.append(" AND t.periodo = "+periodo);
		sql.append(" AND cc.nivel = '"+NivelEnsino.GRADUACAO+"'");
		sql.append(" GROUP BY  u.id_unidade, u.nome, g.id_gestora, g.sigla");
		sql.append(" order BY g.sigla, u.nome ");
				
		Query q = getSession().createSQLQuery(sql.toString());
		
		List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
		
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();
    	for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			Map<String, Object> item = new HashMap<String, Object>();
			
			item.put("id_unidade", (Integer) colunas[col++] );
			item.put("unidade", (String) colunas[col++] );
			item.put("id_gestora", (Integer) colunas[col++] );
			item.put("sigla", (String) colunas[col++] );			
			item.put("total_turmas", (Integer) colunas[col++]);
			item.put("total_planos_cursos", (Integer) colunas[col++] );
			item.put("faltam", (Integer) colunas[col++] );
			item.put("total_referencia_indicadas", (Integer) colunas[col++] );
			item.put("media_referencia_por_plano", (Integer) colunas[col++] );

			resultado.add(item);
    	}
		
		return resultado;
	}	
	
	
	/**
	 * Retorna as turmas conforme ano, periodo e unidade informados
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTurmasByParametros(int ano, int periodo, int idUnidade, int idGestora, boolean apenasCadastrados) throws HibernateException, DAOException{
			
			StringBuffer sql = new StringBuffer();
			
			sql.append(" select u.id_unidade, u.nome as unidade, t.codigo as codTurma, ");
			sql.append("  cc.codigo, ccd.nome as disciplina, pc.id_plano_curso, ");
			sql.append("  pc.metodologia, pc.procedimento_avaliacao_aprendizagem, pc.finalizado, g.nome as centro, g.sigla ");			
			sql.append("   from ensino.turma t ");
			sql.append("   inner join ensino.componente_curricular cc ON cc.id_disciplina = t.id_disciplina   ");
			sql.append("   inner join ensino.componente_curricular_detalhes ccd on ccd.id_componente_detalhes = cc.id_detalhe ");
			sql.append("   inner join comum.unidade u ON u.id_unidade = cc.id_unidade ");
			sql.append("   inner join comum.unidade g ON g.id_unidade = u.id_gestora ");		
			sql.append("   left join ensino.plano_curso pc ON pc.id_turma = t.id_turma and pc.finalizado = trueValue() ");
			
			sql.append(" WHERE t.ano = "+ano);
			sql.append(" AND t.periodo = "+periodo);
			sql.append(" AND cc.nivel = '"+NivelEnsino.GRADUACAO+"'");
			
			if (idUnidade > 0)
				sql.append(" AND u.id_unidade = "+idUnidade);
			
			if (idGestora > 0)
				sql.append(" AND u.id_gestora = "+idGestora);			
			
			if (apenasCadastrados)
				sql.append(" and pc.id_plano_curso is not null ");
			else
				sql.append(" and pc.id_plano_curso is null ");
			
			sql.append(" order BY g.sigla, u.nome, ccd.nome;");
					
			Query q = getSession().createSQLQuery(sql.toString());
			
			List<Map<String, Object>> resultado = new ArrayList<Map<String,Object>>();
			
			@SuppressWarnings("unchecked")
			List<Object> lista = q.list();
	    	for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				
				Map<String, Object> item = new HashMap<String, Object>();
				
				item.put("id_unidade", (Integer) colunas[col++] );
				item.put("unidade", (String) colunas[col++] );
				item.put("codturma", (String) colunas[col++] );
				item.put("codcomponente", (String) colunas[col++] );
				item.put("nome", (String) colunas[col++] );
				item.put("id_plano_curso", (Integer) colunas[col++] );
				item.put("metodologia", (String) colunas[col++] );
				item.put("procedimentos", (String) colunas[col++] );
				item.put("finalizado", (Boolean) colunas[col++] );
				item.put("gestora", (String) colunas[col++] );
				item.put("sigla", (String) colunas[col++] );		

				resultado.add(item);
	    	}
			
			return resultado;
		}	

}
