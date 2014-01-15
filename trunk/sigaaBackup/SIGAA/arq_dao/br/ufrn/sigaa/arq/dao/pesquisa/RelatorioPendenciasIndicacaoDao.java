/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/10/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;


/**
 * Dao responsável por Relatório referente a Pendências de Indicação.
 * @author geyson
 * @author leonardo
 *
 */
public class RelatorioPendenciasIndicacaoDao extends AbstractRelatorioSqlDao {

	/**
	 * Retorna os docentes que possuem pendências de indicação de acordo com o edital informado.
	 *  
	 * @param tipoBolsa
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findPendenciasIndicacao(Integer idEditalPesquisa) throws DAOException {

		StringBuilder sqlconsulta = new StringBuilder("select u2.id_unidade as idcentro, u2.sigla as centro, u.id_unidade as iddepto, u.nome as departamento, p.nome as docente, " +
				" tbp.descricao || case tbp.categoria when 1 then ' (IC)' else ' (IT)' end as modalidade, c.quantidade as recebidas," +
				" (select count(*) from pesquisa.plano_trabalho" +
				"  where plano_trabalho.id_edital = " + idEditalPesquisa +
				"  and plano_trabalho.id_orientador = cd.id_docente" +
				"  and plano_trabalho.tipo_bolsa = tbp.id_tipo_bolsa" +
				"  and plano_trabalho.id_membro_projeto_discente is not null) as indicadas" +
				" from pesquisa.cota_docente cd join pesquisa.cotas c on ( c.id_cota_docente = cd.id )" +
				" join rh.servidor s on ( cd.id_docente = s.id_servidor )" +
				" join comum.pessoa p on ( s.id_pessoa = p.id_pessoa )" +
				" join pesquisa.tipo_bolsa_pesquisa tbp on ( tbp.id_tipo_bolsa = c.id_tipo_bolsa_pesquisa )" +
				" join comum.unidade u on (s.id_unidade = u.id_unidade)" +
				" join comum.unidade u2 on (u2.id_unidade = u.id_gestora)"+
				" where cd.id_edital_pesquisa = " + idEditalPesquisa +
				" and c.quantidade > 0" +
				" and c.quantidade > (select count(*) from pesquisa.plano_trabalho" +
				" where plano_trabalho.id_edital = " + idEditalPesquisa +
				"  and plano_trabalho.id_orientador = cd.id_docente" +
				"  and plano_trabalho.tipo_bolsa = tbp.id_tipo_bolsa" +
				"  and plano_trabalho.id_membro_projeto_discente is not null)" +
				" order by 1, 2, 3, 4");
		
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
