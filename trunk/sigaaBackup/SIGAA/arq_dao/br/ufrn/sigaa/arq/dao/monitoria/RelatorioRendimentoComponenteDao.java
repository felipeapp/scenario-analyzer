/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/02/2010'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;


import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Dao responsável pela Análise de Rendimento dos discentes com projetos de monitoria
 * @author geyson
 */
public class RelatorioRendimentoComponenteDao extends AbstractRelatorioSqlDao {

	/**
	 * Relatório de rendimentos de componentes curriculares por departamentos.
	 * @param congressoIniciacaoCientifica
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> relatorioRedemintosComponentes(Integer idDepartamento, Integer anoInicio, Integer anoFim, Integer idDocente, String codigoComponente) throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(" select distinct cc.codigo as codigo, dc.nome as nome, " +
				" p.nome as prof, t.ano as ano_turma, t.periodo as periodo_turma, t.codigo as codigo_turma, " +
				"(select count(*) from ensino.matricula_componente where id_situacao_matricula = "+ SituacaoMatricula.APROVADO.getId() +" and id_componente_curricular = cc.id_disciplina and id_turma = t.id_turma) as ap, " +
				"(select count(*) from ensino.matricula_componente where id_situacao_matricula = "+ SituacaoMatricula.REPROVADO.getId() +" and id_componente_curricular = cc.id_disciplina and id_turma = t.id_turma) as rp, " +
				"(select count(*) from ensino.matricula_componente where id_situacao_matricula = "+ SituacaoMatricula.REPROVADO_FALTA.getId() +" and id_componente_curricular = cc.id_disciplina and id_turma = t.id_turma) as rf, " +
				"(select count(*) from ensino.matricula_componente where id_situacao_matricula = "+ SituacaoMatricula.TRANCADO.getId() +" and id_componente_curricular = cc.id_disciplina and id_turma = t.id_turma) as tr, " +
				
				"(select count(*) from ensino.matricula_componente " +
				"	where id_situacao_matricula in("+ SituacaoMatricula.APROVADO.getId() +","+ SituacaoMatricula.REPROVADO.getId() +","	+ SituacaoMatricula.REPROVADO_FALTA.getId() +","+ SituacaoMatricula.TRANCADO.getId() +") " +
							"and id_componente_curricular = cc.id_disciplina and id_turma = t.id_turma) as total, " +
				
				"(select avg(media_final) from ensino.matricula_componente where id_situacao_matricula in ("+ SituacaoMatricula.APROVADO.getId() +") and id_componente_curricular = cc.id_disciplina and id_turma = t.id_turma) as media_turma, " +
				
				// Quantidade de projetos de monitoria disponíveis no programa para este componente no mesmo ano em que a turma foi oferecida.
				"(select count(*) from monitoria.componente_curricular_monitoria " +
				"	join monitoria.projeto_monitoria using (id_projeto_monitoria) join projetos.projeto using (id_projeto) " +
				"	where id_disciplina = cc.id_disciplina and ano = t.ano) as ccm " +
				
				"from ensino.componente_curricular cc " +
				"inner join ensino.componente_curricular_detalhes dc on (cc.id_detalhe = dc.id_componente_detalhes) " + 
				"inner join ensino.turma t using (id_disciplina) " +
				"inner join ensino.docente_turma dt on (t.id_turma = dt.id_turma) " +
				"inner join rh.servidor s on (dt.id_docente = s.id_servidor) " +
				"inner join comum.pessoa p using (id_pessoa) " +
				"where cc.id_unidade = "+ idDepartamento +" and t.id_situacao_turma = 3 " +
						"and ((t.ano >= " + anoInicio + " and t.ano <= " + anoFim +" ) )");

		if (idDocente > 0){
			sqlconsulta.append(" and dt.id_docente = " + idDocente + "");
		}
		
		if(codigoComponente != null){
			if(!codigoComponente.isEmpty()){
				sqlconsulta.append(" and cc.codigo = '"+ codigoComponente+"' ");
			}
		}
		
		sqlconsulta.append(" order by  cc.codigo, t.ano, t.periodo, t.codigo ");
		List<Map<String, Object>> result;
		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return result;
	}
	
}
