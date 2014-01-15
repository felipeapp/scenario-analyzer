package br.ufrn.sigaa.ensino.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class AbandonoFormacaoComplementarQueryDao extends GenericSigaaDAO implements JubilamentoQuery {


	@Override
	public Collection<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, Boolean ead,Boolean filtroMatriculados, Unidade unidade, Boolean reprovacoesComp) throws DAOException {
		
		//Pegar o menor ano-período de referência da lista
		Integer ano = anosPeriodo.get(0)[0];
		Integer periodo = anosPeriodo.get(0)[1];		
		for(Integer[] anoPeriodo : anosPeriodo) {			
			if( ( anoPeriodo[0] + anoPeriodo[1] ) < ( ano + periodo )  ) {
				ano = anoPeriodo[0];
				periodo = anoPeriodo[1];
			}			
		}
		
		return findAlunosPassiveisJubilamento(anosPeriodo, ano, periodo, unidade.getId());
	}
	
	
	/**
	 * Buscar discentes ativos que não se matricularam no semestre anterior nem
	 * tem trancamentos de programa. Restrito aos alunos do ensino técnico.
	 * 
	 * @param anosPeriodo
	 * @param ano
	 * @param periodo
	 * @param ead
	 * @return
	 */
	public List<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, int ano, int periodo, int idUnidadeGestora) {

		StringBuilder sql = new StringBuilder("select d.id_discente, d.matricula, p.nome, d.status, d.nivel, d.id_gestora_academica, c.id_curso, c.nome as curso, "
			+ "(select ano from ensino.matricula_componente where id_discente = d.id_discente and id_situacao_matricula not in (3, 10, 12) order by ano desc, periodo desc " + BDUtils.limit(1) + ") as ano, "
			+ "(select periodo from ensino.matricula_componente where id_discente = d.id_discente and id_situacao_matricula not in (3, 10, 12) order by ano desc, periodo desc " + BDUtils.limit(1) + ") as periodo, "
			+ "(select ano||'.'||periodo from ensino.matricula_componente mc "
			+ "   inner join ensino.situacao_matricula sm ON sm.id_situacao_matricula = mc.id_situacao_matricula "
			+ "   where mc.id_discente = d.id_discente and sm.matricula_valida_no_semestre = trueValue() "
			+ "   order by ano desc, periodo desc  limit 1) as ano_periodo "
			+ "from discente d, tecnico.discente_tecnico dt, comum.pessoa p, curso c "
			+ "where d.ano_ingresso || '' || d.periodo_ingresso < ? " +
			  "and d.id_discente = dt.id_discente and d.id_pessoa = p.id_pessoa "
			+ "and d.id_curso = c.id_curso and (d.ano_ingresso != ? or (d.ano_ingresso = ? and d.periodo_ingresso != ?)) "
			+ "and d.status in (1,8) and d.nivel = 'F' and c.id_unidade = " + idUnidadeGestora + " "
			+ "and not exists (select * from ensino.matricula_componente m where m.id_discente = d.id_discente and ((m.ano = "+ano+" and m.periodo="+periodo+") or ");
		
		
		for (Iterator<Integer[]> it = anosPeriodo.iterator(); it.hasNext(); ) {
			Integer[] ap = it.next();
			sql.append(" (m.ano=" + ap[0] + " and m.periodo=" + ap[1] + ") ");
			if (it.hasNext())
				sql.append(" or ");
		}
		
		sql.append(")) and c.nivel = 'F' "
			+ "order by c.nome asc, p.nome asc");
		
		@SuppressWarnings("unchecked")
		List<Discente> lista = getJdbcTemplate().query(sql.toString(), new Object[] {ano + "" + periodo, ano, ano, periodo }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Discente d = new Discente();
				d.setId(rs.getInt("id_discente"));
				d.setMatricula(rs.getLong("matricula"));
				d.setPessoa(new Pessoa());
				d.getPessoa().setNome(rs.getString("nome"));
				d.setStatus(rs.getInt("status"));
				d.setNivel(rs.getString("nivel").charAt(0));
				d.setGestoraAcademica(new Unidade(rs.getInt("id_gestora_academica")));
				d.setCurso(new Curso());
				d.getCurso().setId(rs.getInt("id_curso"));
				d.getCurso().setNome(rs.getString("curso"));
				d.setAnoIngresso(rs.getInt("ano"));
				d.setPeriodoIngresso(rs.getInt("periodo"));
				d.setAnoUltimaMatriculaValida( Integer.parseInt( rs.getString("ano_periodo") != null ? rs.getString("ano_periodo").substring(0,4) : "0" ) );
				d.setPeriodoUltimaMatriculaValida( Integer.parseInt( rs.getString("ano_periodo") != null ? rs.getString("ano_periodo").substring(5, 6) : "0"  ) );

				return d;
			}
		});
		return lista;
	}

	
}
