package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class AbandonoGraduacaoQueryDao extends GenericSigaaDAO implements JubilamentoQuery {

	
	@Override
	public Collection<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, Boolean ead, Boolean filtroMatriculados, Unidade unidade, Boolean reprovacoesComp) throws DAOException {
		
		//Pegar o menor ano-período de referência da lista
		Integer ano = anosPeriodo.get(0)[0];
		Integer periodo = anosPeriodo.get(0)[1];		
		for(Integer[] anoPeriodo : anosPeriodo) {			
			if( ( anoPeriodo[0] + anoPeriodo[1] ) < ( ano + periodo )  ) {
				ano = anoPeriodo[0];
				periodo = anoPeriodo[1];
			}			
		}
		
		return findAlunosPassiveisJubilamento(anosPeriodo, ano, periodo, ead, filtroMatriculados);		
	}
	
	
	/**
	 * Método responsável por buscar discentes de graduação ativos que não se matricularam no(s) semestre(s) passados por parâmetro,
	 * que possui trancamento de matrícula ou reprovação em todos os componentes curriculares nos quais o aluno esteja matriculado, 
	 * sem que haja trancamento de programa.
	 * @param anosPeriodo
	 * @param ano
	 * @param periodo
	 * @param ead
	 * @param filtroMatriculados
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, int ano, int periodo, boolean ead, boolean filtroMatriculados) throws HibernateException, DAOException {

		Collection<SituacaoMatricula> situacoesAtivas = new ArrayList<SituacaoMatricula>();
		situacoesAtivas.add(SituacaoMatricula.MATRICULADO);
		situacoesAtivas.add(SituacaoMatricula.APROVADO);
		
		String situacoesPermanencia = "";
		if (filtroMatriculados) 
			situacoesPermanencia = gerarStringIn(new int[]{ SituacaoMatricula.APROVADO.getId() , SituacaoMatricula.APROVEITADO_DISPENSADO.getId() });
		else {
			String sqlSituacoes = "select id_situacao_matricula from ensino.situacao_matricula where matricula_valida_no_semestre = trueValue() ";
			@SuppressWarnings("unchecked")
			List<Integer> listaSituacoes = getJdbcTemplate().query(sqlSituacoes, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getInt("id_situacao_matricula");
				}
			});
			situacoesPermanencia = UFRNUtils.gerarStringIn(listaSituacoes);
		}
		StringBuilder sql = new StringBuilder(""
				+ " SELECT c.nome as curso, p.nome, p.id_pessoa, d.id_discente, d.matricula,  d.status, d.nivel, d.id_gestora_academica, " +
						" d.ano_ingresso, d.periodo_ingresso, c.id_curso, c.id_modalidade_educacao,  "
				+ "  (select ano||'.'||periodo from ensino.matricula_componente mc "
				+ "   inner join ensino.situacao_matricula sm ON sm.id_situacao_matricula = mc.id_situacao_matricula "
				+ "   where mc.id_discente = d.id_discente and sm.matricula_valida_no_semestre = trueValue() "
				+ "   order by ano desc, periodo desc  limit 1) as ano_periodo, falseValue() as matriculado " 
				+ " FROM discente d " 	 
				+ " INNER JOIN graduacao.discente_graduacao dg ON d.id_discente = dg.id_discente_graduacao " 	 
				+ " INNER JOIN comum.pessoa p ON d.id_pessoa = p.id_pessoa "
				+ " JOIN curso c ON d.id_curso = c.id_curso "
				+ " WHERE  d.ano_ingresso || '' || d.periodo_ingresso <= ? "
				+ " AND d.status in " + gerarStringIn(new int[]{ StatusDiscente.ATIVO,StatusDiscente.FORMANDO }) 
				+ " AND d.nivel = '"+NivelEnsino.GRADUACAO +"' AND c.nivel = '"+NivelEnsino.GRADUACAO+"' " 
				+ " AND c.id_convenio is null "
				+ " AND c.id_modalidade_educacao = " + (ead ? ModalidadeEducacao.A_DISTANCIA : ModalidadeEducacao.PRESENCIAL) ); 
				
		/* Verifica se possui mobilidade nos anos passados como parâmetro, se possuir não é para sair na lista de abandono */
		sql.append(" AND not exists ( select me.id_mobilidade_estudantil " 
				                       +" from ensino.mobilidade_estudantil me " 
				                       +" where me.id_discente = d.id_discente and (");
				for (Iterator<Integer[]> it = anosPeriodo.iterator(); it.hasNext(); ) {
					Integer[] ap = it.next();
					sql.append(" ("+ap[0]+ap[1]+" between soma_semestres(ano,periodo,0) and soma_semestres(ano,periodo,numero_periodos-1)) ");
					if (it.hasNext())
						sql.append(" or ");
				}		
		sql.append(")) ");
		
		// Verifica se não existem trancamentos de período
		sql.append(" AND not exists (select id_movimentacao_aluno from ensino.movimentacao_aluno ma "
					+ " where ma.id_discente = d.id_discente "
					+ " and ma.id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.TRANCAMENTO	 
					+ " and (");
				for (Iterator<Integer[]> it = anosPeriodo.iterator(); it.hasNext(); ) {
					Integer[] ap = it.next();
					sql.append(" (ma.ano_referencia =" + ap[0] + " and ma.periodo_referencia =" + ap[1] + ") ");
					if (it.hasNext())
						sql.append(" or ");
				}	
		sql.append(")) ");

		// Verifica as matrículas no semestre, dependendo se devem ser consideradas as aprovações ou não
		sql.append(" AND not exists ( select mc.id_matricula_componente " +
				" from ensino.matricula_componente mc " +
				" where mc.id_discente = d.id_discente " +
				" and mc.id_situacao_matricula in " + situacoesPermanencia);
		
		sql.append(" and (");
		for (Iterator<Integer[]> it = anosPeriodo.iterator(); it.hasNext(); ) {
			Integer[] ap = it.next();
			sql.append(" (mc.ano=" + ap[0] + " and mc.periodo=" + ap[1] + ") ");
			if (it.hasNext())
				sql.append(" or ");
		}
		sql.append(")) ");		
		
		
		sql.append(" GROUP BY c.nome, p.nome, p.id_pessoa, d.id_discente, d.matricula, d.status, d.nivel, " +
				" d.id_gestora_academica, c.id_curso, c.id_modalidade_educacao, d.ano_ingresso, d.periodo_ingresso ");
		sql.append(" ORDER BY curso, nome");
		@SuppressWarnings("unchecked")
		List<Discente> lista = getJdbcTemplate().query(sql.toString(), new Object[] { ano + "" + periodo }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Discente d = new Discente();
				d.setId(rs.getInt("id_discente"));
				d.setMatricula(rs.getLong("matricula"));
				d.setPessoa(new Pessoa());
				d.getPessoa().setId(rs.getInt("id_pessoa"));
				d.getPessoa().setNome(rs.getString("nome"));
				d.setStatus(rs.getInt("status"));
				d.setNivel(rs.getString("nivel").charAt(0));
				d.setGestoraAcademica(new Unidade(rs.getInt("id_gestora_academica")));
				d.setCurso(new Curso());
				d.getCurso().setId(rs.getInt("id_curso"));
				d.getCurso().setNome(rs.getString("curso"));
				d.getCurso().setModalidadeEducacao(new ModalidadeEducacao(rs.getInt("id_modalidade_educacao")));
				d.setAnoUltimaMatriculaValida( Integer.parseInt( rs.getString("ano_periodo") != null ? rs.getString("ano_periodo").substring(0,4) : "0" ) );
				d.setPeriodoUltimaMatriculaValida( Integer.parseInt( rs.getString("ano_periodo") != null ? rs.getString("ano_periodo").substring(5, 6) : "0"  ) );
				d.setAnoIngresso( rs.getInt("ano_ingresso") );
				d.setPeriodoIngresso( rs.getInt("periodo_ingresso") );
				d.setMatricular(rs.getBoolean("matriculado"));
				return d;
			}
		});
		return lista;
	}

	

}
