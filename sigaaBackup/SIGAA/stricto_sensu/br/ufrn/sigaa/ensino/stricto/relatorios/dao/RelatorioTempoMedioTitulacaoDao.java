/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 08/02/2011
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;

/**
 * Dao responsável por realizar a consulta para emissão do relatório 
 * de tempo médio de titulação de discente stricto.
 * 
 * @author arlindo
 *
 */
public class RelatorioTempoMedioTitulacaoDao extends GenericSigaaDAO {
	
	/**
	 * Retorna todas as defesas no ano e programa informado  
	 * @param idPrograma
	 * @param ano
	 * @return
	 */
	public List<Map<String,Object>> findDefesasTempoMedioTitulacao(int idPrograma, Date dataInicio, Date dataFim){
		StringBuilder sql = new StringBuilder();
		
		sql.append("select distinct c.nome as curso, c.id_curso, c.nivel, d.matricula, p.nome, bp.data, ");
		sql.append("	case (coalesce(ds.mes_entrada,0))"); 
		sql.append("          when 0 then (");
		sql.append("		case d.periodo_ingresso"); 
		sql.append("			when 1 then 1"); 
		sql.append("		else 7 end) ");
		sql.append("	else ds.mes_entrada end as mes_entrada,"); 
		sql.append("	d.ano_ingresso,");
		sql.append("      cast( ((extract(year from (bp.data)) - d.ano_ingresso) * 12 +"); 
		sql.append("      (extract(month from (bp.data)) -"); 
		sql.append("      case (coalesce(ds.mes_entrada,0))"); 
		sql.append("          when 0 then (");
		sql.append("		case d.periodo_ingresso"); 
		sql.append("			when 1 then 1"); 
		sql.append("		else 7 end)"); 
		sql.append("	else ds.mes_entrada end)) + 1 as integer) as meses");
		sql.append(" from public.discente d");
		sql.append(" inner join stricto_sensu.discente_stricto ds on (ds.id_discente = d.id_discente)");
		sql.append(" inner join comum.pessoa p on (p.id_pessoa = d.id_pessoa)");
		sql.append(" inner join stricto_sensu.dados_defesa dd on (dd.id_discente = d.id_discente)");
		sql.append(" inner join stricto_sensu.banca_pos bp on (bp.id_dados_defesa = dd.id_dados_defesa)");
		sql.append(" inner join public.curso c on c.id_curso = d.id_curso");
		sql.append(" where bp.data between ? and ?");
		sql.append(" and d.status in "+UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinteStricto()));
		sql.append(" and bp.tipobanca = "+BancaPos.BANCA_DEFESA);
		
		String programa = "";
		
		if (idPrograma > 0)
			programa = " and d.id_gestora_academica = "+idPrograma;
		
		sql.append(programa);
		
		sql.append(" order by c.nivel desc, c.nome, p.nome");
		
		StringBuilder sqlTotal = new StringBuilder();
		
		sqlTotal.append("  select distinct c.id_curso, c.nome, c.nivel, ");
		sqlTotal.append(" cast(sum(cast(((extract(year from (bp.data)) - d.ano_ingresso) * 12 + (extract(month from (bp.data)) - "); 
		sqlTotal.append("  	case (coalesce(ds.mes_entrada,0)) "); 
		sqlTotal.append(" when 0 then (case d.periodo_ingresso when 1 then 1 else 7 end) "); 
		sqlTotal.append(" else ds.mes_entrada end)) + 1 as integer))/count(ds.id_discente) as integer) as meses ");
		sqlTotal.append(" from public.discente d ");
		sqlTotal.append(" inner join stricto_sensu.discente_stricto ds on (ds.id_discente = d.id_discente) ");
		sqlTotal.append(" inner join stricto_sensu.dados_defesa dd on (dd.id_discente = d.id_discente) ");
		sqlTotal.append(" inner join stricto_sensu.banca_pos bp on (bp.id_dados_defesa = dd.id_dados_defesa) ");
		sqlTotal.append(" inner join public.curso c on c.id_curso = d.id_curso ");
		sqlTotal.append(" where d.status in "+UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinteStricto()));
		sqlTotal.append(" and bp.tipobanca = "+BancaPos.BANCA_DEFESA);
		sqlTotal.append(programa);
		sqlTotal.append(" group by c.id_curso, c.nivel, c.nome ");
		sqlTotal.append(" order by c.nivel desc, c.nome ");
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sql.toString(), new Object[] { dataInicio, dataFim });
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rsTotal = getJdbcTemplate().queryForList(sqlTotal.toString());

		Integer idCurso = 0;
		Integer meses = 0;
		for ( Map<String,Object> lista : rs){
			
			lista.put("nivel", NivelEnsino.getDescricao(lista.get("nivel").toString().toCharArray()[0]));
			
			Integer id = (Integer) lista.get("id_curso");
			
			if (!idCurso.equals(id)){
				idCurso = id;
				for (Map<String,Object> total : rsTotal){
					Integer idCursoTotal = (Integer) total.get("id_curso");
					if (idCurso.equals(idCursoTotal)){						
						meses = (Integer) total.get("meses");
						break;
					}
				}				
			}
			
			idCurso = id;
			
			lista.put("totalgeral", meses);
		}
		
		return rs;		
		
	}
	
	/**
	 * Retorna todas as defesas por orientador no período e programa informado  
	 * @param idPrograma
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public List<Map<String,Object>> findDefesasTempoMedioTitulacaoOrientador(int idPrograma, Date dataInicio, Date dataFim){
		StringBuilder sql = new StringBuilder();
		
		sql.append("select distinct c.nome as curso, c.id_curso, c.nivel, p.nome as orientador, ");
		sql.append("    count(distinct d.id_discente) as totalAlunos, ");
		sql.append("    sum( cast( ((extract(year from (bp.data)) - d.ano_ingresso) * 12 + "); 
		sql.append("      (extract(month from (bp.data)) -"); 
		sql.append("      case (coalesce(ds.mes_entrada,0))"); 
		sql.append("          when 0 then (");
		sql.append("		case d.periodo_ingresso"); 
		sql.append("			when 1 then 1"); 
		sql.append("		else 7 end)"); 
		sql.append("	else ds.mes_entrada end)) + 1 as integer)) / count(distinct d.id_discente) as meses ");
		sql.append(" from public.discente d");
		sql.append(" inner join stricto_sensu.discente_stricto ds on (ds.id_discente = d.id_discente) ");
		sql.append(" inner join stricto_sensu.dados_defesa dd on (dd.id_discente = d.id_discente) ");
		sql.append(" inner join stricto_sensu.banca_pos bp on (bp.id_dados_defesa = dd.id_dados_defesa) ");
		sql.append(" inner join public.curso c on c.id_curso = d.id_curso ");
		sql.append(" inner join (" +
					"	select case (coalesce(s.id_servidor,0)) "+
					"		  when 0 then de.id_pessoa "+
				 	" 			else s.id_pessoa "+ 
					"	       end as id_pessoa, "+
					"	       o.id_discente "+
					"	from graduacao.orientacao_academica o "+ 
					"	left join rh.servidor s ON o.id_servidor = s.id_servidor "+ 
					"	left join ensino.docente_externo de ON o.id_docente_externo = de.id_docente_externo "+
					"	where o.tipoorientacao = '" + OrientacaoAcademica.ORIENTADOR + "' and o.cancelado = falseValue() "+ 
					" ) o on o.id_discente = d.id_discente "); 				
		sql.append(" inner join comum.pessoa p ON o.id_pessoa = p.id_pessoa ");

		sql.append(" where bp.data between ? and ?");
		sql.append(" and d.status in "+UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinteStricto()));
		sql.append(" and bp.tipobanca = "+BancaPos.BANCA_DEFESA);
		
		String programa = "";
		
		if (idPrograma > 0)
			programa = " and d.id_gestora_academica = "+idPrograma;
		
		sql.append(programa);
		
		sql.append(" group by p.nome, c.nome, c.nivel, c.id_curso ");
		
		sql.append(" order by c.nivel desc, c.nome,  p.nome ");		
		
		StringBuilder sqlTotal = new StringBuilder();
		
		sqlTotal.append("  select distinct c.id_curso, c.nome, c.nivel, ");
		sqlTotal.append(" cast(sum(cast(((extract(year from (bp.data)) - d.ano_ingresso) * 12 + (extract(month from (bp.data)) - "); 
		sqlTotal.append("  	case (coalesce(ds.mes_entrada,0)) "); 
		sqlTotal.append(" when 0 then (case d.periodo_ingresso when 1 then 1 else 7 end) "); 
		sqlTotal.append(" else ds.mes_entrada end)) + 1 as integer))/count(ds.id_discente) as integer) as meses ");
		sqlTotal.append(" from public.discente d ");
		sqlTotal.append(" inner join stricto_sensu.discente_stricto ds on (ds.id_discente = d.id_discente) ");
		sqlTotal.append(" inner join stricto_sensu.dados_defesa dd on (dd.id_discente = d.id_discente) ");
		sqlTotal.append(" inner join stricto_sensu.banca_pos bp on (bp.id_dados_defesa = dd.id_dados_defesa) ");
		sqlTotal.append(" inner join public.curso c on c.id_curso = d.id_curso ");
		sqlTotal.append(" where d.status in "+UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinteStricto()));
		sqlTotal.append(" and bp.tipobanca = "+BancaPos.BANCA_DEFESA);
		sqlTotal.append(" and bp.data between ? and ?");
		sqlTotal.append(programa);
		sqlTotal.append(" group by c.id_curso, c.nivel, c.nome ");
		sqlTotal.append(" order by c.nivel desc, c.nome ");
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sql.toString(), new Object[] { dataInicio, dataFim });
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rsTotal = getJdbcTemplate().queryForList(sqlTotal.toString(), new Object[] { dataInicio, dataFim });

		Integer idCurso = 0;
		Integer meses = 0;
		for ( Map<String,Object> lista : rs){
			
			lista.put("nivel", NivelEnsino.getDescricao(lista.get("nivel").toString().toCharArray()[0]));
			
			Integer id = (Integer) lista.get("id_curso");
			
			if (!idCurso.equals(id)){
				idCurso = id;
				for (Map<String,Object> total : rsTotal){
					Integer idCursoTotal = (Integer) total.get("id_curso");
					if (idCurso.equals(idCursoTotal)){						
						meses = (Integer) total.get("meses");
						break;
					}
				}				
			}
			
			idCurso = id;
			
			lista.put("totalgeral", meses);
		}
		
		return rs;				
		
	}	

}
