/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 25/03/2008
 */
package br.ufrn.comum.dao;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.util.UFRNUtils;

/**
 * DAO para busca de turmas do SIGAA pelos outros sistemas
 * 
 * @author David Pereira
 *
 */
@SuppressWarnings("unchecked")
public class TurmaDAO extends GenericDAOImpl {

	/**
	 * Retorna a lista de disciplinas já ministradas de um docente.
	 * 
	 * @param idServidor 
	 * @return Uma lista na qual cada elemento é um mapa contendo o nome da coluna e o valor retornado para essa coluna.
	 * 		   As chaves são id_disciplina, codigo_componente, nome_componente
	 */
	public List<Map<String, Object>> findDisciplinasByDocente(int idServidor) {
		
		
		String sql = "select distinct t.id_disciplina, cc.codigo as codigo_componente, ccd.nome as nome_componente "
			+ "from ensino.turma t, ensino.docente_turma dt, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd " 
			+ "where t.id_turma = dt.id_turma and t.id_disciplina = cc.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes " 
			+ "and dt.id_docente = " + idServidor + " order by cc.codigo";
		
		return getJdbcTemplate(Database.getInstance().getSigaaDs()).queryForList(sql);
		
	}

	/**
	 * Retorna a lista de turmas abertas do docente.
	 * 
	 * @param idServidor 
	 * @return Uma lista com os ids das turmas abertas
	 */
	public List<Integer> findTurmasAbertasByDocente(int idServidor) {
		String sql = "select distinct t.id_turma where t.id_turma = dt.id_turma and t.id_situacao_turma = 1 " 
			+ "and dt.id_docente="+idServidor;
		
		return getJdbcTemplate(Database.getInstance().getSigaaDs()).queryForList(sql, Integer.class);
		
	}
	
	/**
	 * Retorna id da turma, código da disciplina e nome das disciplinas cujos
	 * ids foram passados como parâmetro. 
	 * @param ids
	 * @return
	 */
	public List<Map<String, Object>> findDisciplinasByPrimaryKey(int ids[]){
		
		String sql = "select distinct t.id_disciplina, cc.codigo as codigo_componente, ccd.nome as nome_componente "
			+ "from ensino.turma t, ensino.docente_turma dt, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd " 
			+ "where t.id_turma = dt.id_turma and t.id_disciplina = cc.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes " 
			+ "and (cc.id_disciplina in "+UFRNUtils.gerarStringIn(ids)+")";
		
		return getJdbcTemplate(Database.getInstance().getSigaaDs()).queryForList(sql);
		
	}
	
}
