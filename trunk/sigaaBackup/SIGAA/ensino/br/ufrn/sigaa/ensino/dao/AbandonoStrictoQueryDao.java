package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class AbandonoStrictoQueryDao extends GenericSigaaDAO implements JubilamentoQuery {

	
	@Override
	public Collection<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, Boolean ead,Boolean filtroMatriculados, Unidade unidade, Boolean reprovacoesComp)	throws DAOException {
		
		//Pegar o menor ano-período de referência da lista
		Integer ano = anosPeriodo.get(0)[0];
		Integer periodo = anosPeriodo.get(0)[1];		
		for(Integer[] anoPeriodo : anosPeriodo) {			
			if( ( anoPeriodo[0] + anoPeriodo[1] ) < ( ano + periodo )  ) {
				ano = anoPeriodo[0];
				periodo = anoPeriodo[1];
			}			
		}
		
		return findAlunosPassiveisJubilamento(anosPeriodo, ano,periodo,filtroMatriculados, unidade);		
	}
	
	
	/**
	 * Retornas os Discente Não Matriculados nos anos e períodos informados;
	 * Usado para o Cancelamento por Abandono.
	 * @param anosPeriodo
	 * @param ano
	 * @param periodo
	 * @param filtroMatriculados
	 * @return
	 */
	public List<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, int ano, int periodo, boolean filtroMatriculados,Unidade unidade) {
		Collection<SituacaoMatricula> situacoesAtivas = new ArrayList<SituacaoMatricula>();
		situacoesAtivas.add(SituacaoMatricula.MATRICULADO);
		situacoesAtivas.add(SituacaoMatricula.APROVADO);
				
		StringBuilder sql = new StringBuilder("" 
				+ "SELECT distinct d.id_discente, d.matricula, d.ano_ingresso, d.periodo_ingresso, p.nome, d.status, d.nivel, d.id_gestora_academica, u.nome as curso, "
				+ "(select ano||'.'||periodo from ensino.matricula_componente mc "
						+ "   inner join ensino.situacao_matricula sm ON sm.id_situacao_matricula = mc.id_situacao_matricula "
						+ "   where mc.id_discente = d.id_discente and sm.matricula_valida_no_semestre = trueValue() "
						+ "   order by ano desc, periodo desc  limit 1) as ano_periodo"
				+ "	FROM discente d "
				+ "	 INNER JOIN stricto_sensu.discente_stricto ds ON d.id_discente = ds.id_discente "
				+ "	 INNER JOIN comum.pessoa p ON d.id_pessoa = p.id_pessoa "
				+ "	 inner join comum.unidade u on (u.id_unidade = d.id_gestora_academica) "
				+ "	WHERE " 
				+ "	 d.ano_ingresso || '' || d.periodo_ingresso < ? AND "
				+ "  d.status in "+ gerarStringIn(new int[]{ StatusDiscente.ATIVO}) +" AND "
				+ "  d.nivel in "+gerarStringIn( NivelEnsino.getNiveisStricto()) +" AND " 
		+" not exists (select m.id_matricula_componente from ensino.matricula_componente m where m.id_discente = d.id_discente ");

		if( filtroMatriculados )
			sql.append("	and m.id_situacao_matricula in " + gerarStringIn( situacoesAtivas )	); 
		
		sql.append(" 		and (");
		for (Iterator<Integer[]> it = anosPeriodo.iterator(); it.hasNext(); ) {
			Integer[] ap = it.next();
			sql.append(" (m.ano=" + ap[0] + " and m.periodo=" + ap[1] + ") ");
			if (it.hasNext())
				sql.append(" or ");
		}
		
		sql.append(")) AND " 
				+ " not exists (select id_movimentacao_aluno from ensino.movimentacao_aluno ma where ma.id_discente = d.id_discente  "
				+ "						and ma.id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.TRANCAMENTO	 
				+ " 					and (");
			for (Iterator<Integer[]> it = anosPeriodo.iterator(); it.hasNext(); ) {
				Integer[] ap = it.next();
				sql.append(" (ma.ano_referencia =" + ap[0] + " and ma.periodo_referencia =" + ap[1] + ") ");
				if (it.hasNext())
					sql.append(" or ");
			}			
		sql.append("))  "+				
			" and not exists (" +
				" select rap.id_renovacao_atividade_pos from stricto_sensu.renovacao_atividade_pos rap "+
			    " inner join ensino.matricula_componente mc using (id_matricula_componente) " + 
				" where mc.id_discente = d.id_discente "+
				" and rap.ativo = trueValue() and rap.id_matricula_componente is not null and (");
		
		for (Iterator<Integer[]> it = anosPeriodo.iterator(); it.hasNext(); ) {
			Integer[] ap = it.next();
			sql.append(" (rap.ano =" + ap[0] + " and rap.periodo =" + ap[1] + ") ");
			if (it.hasNext())
				sql.append(" or ");
		}		
		sql.append("))  ");
		
		if (!ValidatorUtil.isEmpty(unidade))
			sql.append(" and u.id_unidade = " + unidade.getId());
		
		sql.append(" order by u.nome asc, p.nome asc");
		
		System.out.println("## SQL: "+ sql.toString());
		
		@SuppressWarnings("unchecked")
		List<Discente> lista = getJdbcTemplate().query(sql.toString(), new Object[] { ano + "" + periodo }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Discente d = new Discente();
				d.setId(rs.getInt("id_discente"));
				d.setMatricula(rs.getLong("matricula"));
				d.setAnoIngresso(rs.getInt("ano_ingresso"));
				d.setPeriodoIngresso(rs.getInt("periodo_ingresso"));
				d.setPessoa(new Pessoa());
				d.getPessoa().setNome(rs.getString("nome"));
				d.setStatus(rs.getInt("status"));
				d.setNivel(rs.getString("nivel").charAt(0));
				d.setGestoraAcademica(new Unidade(rs.getInt("id_gestora_academica")));
				d.setCurso(new Curso());
				d.getCurso().setId(rs.getInt("id_gestora_academica"));
				d.getCurso().setNome(rs.getString("curso"));
				d.setAnoUltimaMatriculaValida( Integer.parseInt( rs.getString("ano_periodo") != null ? rs.getString("ano_periodo").substring(0,4) : "0" ) );
				d.setPeriodoUltimaMatriculaValida( Integer.parseInt( rs.getString("ano_periodo") != null ? rs.getString("ano_periodo").substring(5, 6) : "0"  ) );
				return d;
			}
		});				
		return lista;
	}

	

}
