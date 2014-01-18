package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class FiltroDiscentesRecalculoStricto implements FiltroListaDiscentesCalcular<Integer> {


	public FiltroDiscentesRecalculoStricto(String sqlRestricao) {
		this.sqlRestricao = sqlRestricao;
	}
	
	String sqlRestricao;
	
	@Override
	public List<Integer> execute() {
		
		List<Integer> status = new ArrayList<Integer>();
		status.add(StatusDiscente.ATIVO);
		status.add(StatusDiscente.DEFENDIDO);
		
		if (ValidatorUtil.isEmpty(sqlRestricao)) {
			sqlRestricao = " ultima_atualizacao_totais is null";
		}
		
		String sql = "select d.id_discente " 
				+ " from stricto_sensu.discente_stricto ds " 
				+ " join discente d on (d.id_discente = ds.id_discente) " 
				+ " where d.status in " + UFRNUtils.gerarStringIn(status)
				+ " and d.tipo <> " + Discente.ESPECIAL
				+ " and " + sqlRestricao;
		
		List<Integer> discentesBuscados = new JdbcTemplate(Database.getInstance().getSigaaDs()).queryForList(sql, Integer.class);
		
		return discentesBuscados;
	}
}
