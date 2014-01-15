package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável por retornar os discentes que tenham excedido o número de reprovações.
 * 
 * @author Jean Guerethes
 */
public class ReprovacaoExcedidaTecnicoQueryDao extends GenericSigaaDAO implements JubilamentoQuery {

	@Override
	public Collection<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, Boolean ead, Boolean filtroMatriculados, Unidade unidade, Boolean reprovacoesComp) throws DAOException {
		return findAlunosPassiveisJubilamento(null, null, null, unidade.getId(), reprovacoesComp);
	}
	
	private Collection<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, Integer ano, Integer periodo, int id, Boolean mesmoComp) {
		 String sql = "select d.id_discente, d.matricula, p.nome, d.ano_ingresso, d.periodo_ingresso";
			
			 sql += " FROM ensino.matricula_componente mc" +
				    " JOIN discente d using ( id_discente )" +
					" JOIN curso c using ( id_curso )" +
					" JOIN comum.pessoa p using ( id_pessoa )";
		
		 if (mesmoComp)
			sql += "join ensino.componente_curricular cc on ( cc.id_disciplina = mc.id_componente_curricular )";
			 
			sql += " WHERE id_situacao_matricula in " + gerarStringIn( new int[] { SituacaoMatricula.REPROVADO.getId(), 
						SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId() }) +
					" AND d.nivel = '" + NivelEnsino.TECNICO + "'" +
					" AND c.id_unidade = " + id +
					" AND d.status = " + StatusDiscente.ATIVO +
					" GROUP BY d.id_discente, d.matricula, p.nome, mc.id_discente, d.ano_ingresso, d.periodo_ingresso";
			
		if (mesmoComp)
			sql += ", cc.codigo";
			
			sql += " having count(*) >= 3 ORDER BY p.nome";
		
		return getSimpleJdbcTemplate().query(sql, new ParameterizedRowMapper<Discente>() {

			public Discente mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				int col = 1;
				
				Discente discente = new Discente();
				discente.setPessoa(new Pessoa());
				
				discente.setId(rs.getInt(col++));
				discente.setMatricula(rs.getLong(col++));
				discente.getPessoa().setNome(rs.getString(col++));
				discente.setAnoIngresso(rs.getInt(col++));
				discente.setPeriodoIngresso(rs.getInt(col++));
					
				return discente;
			}
		});
	}
	
}