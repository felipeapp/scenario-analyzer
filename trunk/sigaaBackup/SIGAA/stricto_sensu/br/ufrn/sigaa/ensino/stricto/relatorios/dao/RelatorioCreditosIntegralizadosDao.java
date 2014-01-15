/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 20/10/2011
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.dao;

import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Dao responsável por realizar a consulta para emissão do relatório 
 * de créditos integralizados stricto sensu.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class RelatorioCreditosIntegralizadosDao extends GenericSigaaDAO {
	
	/**
	 * Retorna todos os discentes ativos com crétitos integralizados e obrigatórios
	 * @param idPrograma
	 * @param nivel
	 * @return
	 */
	public List<Map<String,Object>> findCreditosIntegralizados(Integer idPrograma, char nivel){
		
		StringBuilder sql = new StringBuilder();
		
		
		sql.append(" select D.ID_DISCENTE, d.matricula, p.nome,  ");

		sql.append(" ((select coalesce(sum(ccd.ch_total), 0) / 15 ");
		sql.append(" from ensino.matricula_componente mc ");
		sql.append(" inner join ensino.componente_curricular cc on mc.id_componente_curricular = cc.id_disciplina "); 
		sql.append("inner join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes "); 
		sql.append(" where mc.id_discente=d.id_discente "); 
		sql.append(" and mc.id_situacao_matricula in "+UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesPagas())+") + "); 
		
		sql.append(" (select coalesce(sum(creditos), 0) "); 
		sql.append(" from stricto_sensu.aproveitamento_credito "); 
		sql.append(" where id_discente=d.id_discente and ativo = trueValue())) as integralizados, ");
		
		sql.append(" coalesce((select cr_total_minimo "); 
		sql.append(" from graduacao.curriculo "); 
		sql.append(" where id_curriculo = (select id_curriculo from discente where id_discente=d.id_discente)),0) as obrigatorios ");
		
		sql.append(" from public.discente d ");
		sql.append(" inner join stricto_sensu.discente_stricto ds using (id_discente) ");
		sql.append(" inner join comum.pessoa p using (id_pessoa) ");
		sql.append(" where d.nivel = '"+nivel+"'");
		sql.append(" and d.status = "+StatusDiscente.ATIVO);
		
		if (ValidatorUtil.isNotEmpty(idPrograma))
			sql.append(" and d.id_gestora_academica = "+idPrograma);		
		
		sql.append(" order by p.nome, d.nivel ");
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sql.toString());
		return rs;		
		
	}		

}
