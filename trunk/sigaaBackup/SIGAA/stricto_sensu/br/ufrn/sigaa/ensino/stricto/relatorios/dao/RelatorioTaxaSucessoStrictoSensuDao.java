/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 17/02/2011
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.dao;

import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;

/**
 * Dao responsável por realizar a consulta para emissão do relatório 
 * de taxa de Sucesso stricto sensu.
 * 
 * @author arlindo
 *
 */
public class RelatorioTaxaSucessoStrictoSensuDao extends GenericSigaaDAO {
	
	/**
	 * Retorna todas as defesas no ano e programa informado  
	 * @param idPrograma
	 * @param ano
	 * @return
	 */
	public List<Map<String,Object>> findTaxaSucesso(int idPrograma, char nivel, int anoIngresso, int anoDefesa){
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" select distinct c.id_curso, c.nome, d.nivel, ");
		sql.append("    count(distinct d.id_discente) as ingressos, ");
		sql.append("	count(distinct defesa.id_discente) as defesas, ");
		sql.append("    cast((cast(count(distinct defesa.id_discente) as numeric(9,2)) / " +
					"	cast(count(distinct d.id_discente) as numeric(9,2))) * 100 as numeric(9,2)) as taxa_sucesso ");
		sql.append(" from public.discente d  ");
		sql.append(" inner join public.curso c on c.id_curso = d.id_curso ");
		sql.append(" left join ( ");
		sql.append("   select ds.id_discente,bp.tipobanca from stricto_sensu.discente_stricto ds ");
		sql.append("   inner join public.discente d using (id_discente) ");
		sql.append("   inner join stricto_sensu.dados_defesa dd on (dd.id_discente = ds.id_discente) ");
		sql.append("   inner join stricto_sensu.banca_pos bp on (bp.id_dados_defesa = dd.id_dados_defesa)  ");
		sql.append("   where extract(year from bp.data) <= "+anoDefesa);
		sql.append("     and d.status in "+UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinteStricto()));
		sql.append("     and bp.tipobanca = "+BancaPos.BANCA_DEFESA);
		sql.append("     and bp.status = "+BancaPos.ATIVO);
		sql.append("     and d.nivel = '"+nivel+"'");
		sql.append(" ) defesa on defesa.id_discente = d.id_discente ");
		sql.append(" where d.nivel = '"+nivel+"'");
		sql.append("  and d.ano_ingresso = "+anoIngresso);
		sql.append("  and (defesa.tipobanca = "+BancaPos.BANCA_DEFESA+" or defesa.tipobanca is null) ");
		
		if (idPrograma > 0)
			sql.append("   and d.id_gestora_academica = "+idPrograma);
		
		sql.append(" group by c.id_curso, c.nome, d.nivel ");
		sql.append(" order by c.nome ");
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sql.toString());
		return rs;		
		
	}	
	
	/**
	 * Retorna o corpo do SQL que traz os discente ingressantes/defendidos 
	 * @return
	 */
	public String getCorpoSelectDiscentes(){
		StringBuilder sql = new StringBuilder();		
		sql.append(" select distinct c.nome as curso, c.id_curso, c.nivel, d.matricula, d.id_discente, p.nome, bp.data, ");
		sql.append("	case (coalesce(ds.mes_entrada,0))"); 
		sql.append("          when 0 then (");
		sql.append("		case d.periodo_ingresso"); 
		sql.append("			when 1 then 1"); 
		sql.append("		else 7 end) ");
		sql.append("	else ds.mes_entrada end as mes_entrada,"); 
		sql.append("	d.ano_ingresso,");
		sql.append("    cast( ((extract(year from (bp.data)) - d.ano_ingresso) * 12 +"); 
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
		sql.append(" inner join public.curso c on c.id_curso = d.id_curso");
		return sql.toString();
	}
	
	/**
	 * Retorna todos os ingressos do curso, nível e ano informados
	 * @param idCurso
	 * @param nivel
	 * @param anoIngresso
	 * @return
	 */
	public List<Map<String,Object>> findIngressos(int idCurso, char nivel, int anoIngresso){
		StringBuilder sql = new StringBuilder();
		
		sql.append(getCorpoSelectDiscentes());
		
		sql.append(" left join ( ");
		sql.append("   select ds.id_discente,bp.data from stricto_sensu.discente_stricto ds ");
		sql.append("   inner join public.discente d using (id_discente) ");
		sql.append("   inner join stricto_sensu.dados_defesa dd on (dd.id_discente = ds.id_discente) ");
		sql.append("   inner join stricto_sensu.banca_pos bp on (bp.id_dados_defesa = dd.id_dados_defesa)  ");
		sql.append("   where extract(year from bp.data) <= "+(anoIngresso+2));
		sql.append("     and d.status in "+UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinteStricto()));
		sql.append("     and bp.tipobanca = "+BancaPos.BANCA_DEFESA);
		sql.append("     and bp.status = "+BancaPos.ATIVO);
		sql.append("     and d.nivel = '"+nivel+"'");
		sql.append(" ) bp on bp.id_discente = d.id_discente ");
				
		sql.append(" where d.ano_ingresso = "+anoIngresso);
		
		sql.append(" and d.nivel = '"+nivel+"'");
		
		if (idCurso > 0)
			sql.append(" and d.id_curso = "+idCurso);
		
		sql.append(" order by c.nome, c.nivel desc, p.nome");		
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sql.toString());
		return rs;				
	}
	
	/**
	 * Retorna todas as defesas do curso, nível e ano informados
	 * @param idCurso
	 * @param nivel
	 * @param anoIngresso
	 * @param anoDefesa
	 * @return
	 */
	public List<Map<String,Object>> findDefesas(int idCurso, char nivel, int anoIngresso, int anoDefesa){
		StringBuilder sql = new StringBuilder();
		
		sql.append(getCorpoSelectDiscentes());
		
		sql.append(" left join stricto_sensu.dados_defesa dd on (dd.id_discente = d.id_discente)");
		sql.append(" left join stricto_sensu.banca_pos bp on (bp.id_dados_defesa = dd.id_dados_defesa) and bp.status = "+BancaPos.ATIVO);		
		
		sql.append("   where extract(year from bp.data) <= "+anoDefesa);
		sql.append("     and d.status in "+UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinteStricto()));
		sql.append("     and d.nivel = '"+nivel+"'");
		sql.append("     and d.ano_ingresso = "+anoIngresso);
		sql.append("     and (bp.tipobanca = "+BancaPos.BANCA_DEFESA+" or bp.tipobanca is null) ");
		
		if (idCurso > 0)
			sql.append(" and d.id_curso = "+idCurso);
		
		sql.append(" order by c.nome, c.nivel desc, p.nome");		
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sql.toString());
		return rs;				
	}	
}
