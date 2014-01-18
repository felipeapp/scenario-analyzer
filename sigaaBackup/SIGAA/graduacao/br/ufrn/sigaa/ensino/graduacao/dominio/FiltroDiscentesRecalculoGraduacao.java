package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * 
 * @author Henrique André
 *
 */
public class FiltroDiscentesRecalculoGraduacao implements FiltroListaDiscentesCalcular<Integer> {

	public FiltroDiscentesRecalculoGraduacao(String sqlRestricao) {
		this.sqlRestricao = sqlRestricao;
	}
	
	String sqlRestricao;
	
	@Override
	public List<Integer> execute() {
		
		if (ValidatorUtil.isEmpty(sqlRestricao)) {
			sqlRestricao = " ultima_atualizacao_totais is null";
		}
		
		String sql = "select d.id_discente " 
				+ " from graduacao.discente_graduacao dg " 
				+ " join discente d on (d.id_discente = dg.id_discente_graduacao) " 
				+ " where d.status in " + UFRNUtils.gerarStringIn(StatusDiscente.getStatusComVinculo())
				+ " and d.tipo <> " + Discente.ESPECIAL
				+ " and " + sqlRestricao;
		
		List<Integer> discentesBuscados = new JdbcTemplate(Database.getInstance().getSigaaDs()).queryForList(sql, Integer.class);
		
		return discentesBuscados;
	}

}
