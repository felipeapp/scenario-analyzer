package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.sigaa.dominio.Curso;

public class FiltroDiscentesColacaoGrau implements FiltroListaDiscentesCalcular<Integer>{

	
	public FiltroDiscentesColacaoGrau(Curso curso) {
		this.curso = curso;
	}
	
	private Curso curso;

	@Override
	public List<Integer> execute() {
		
		String sql = "select id_discente from discente d " +
				"	join graduacao.discente_graduacao dg on (dg.id_discente_graduacao = d.id_discente) " +
				"   where d.id_curso=? and (d.status in (? , ?) or (dg.ultima_atualizacao_totais is null) and d.status=?)";
		
		List<Integer> discentesBuscados = new JdbcTemplate(Database.getInstance().getSigaaDs()).queryForList(sql, 
				Integer.class, 
				new Object[] {curso.getId(), StatusDiscente.GRADUANDO, StatusDiscente.FORMANDO, StatusDiscente.ATIVO});
		
		return discentesBuscados;
	}

}
