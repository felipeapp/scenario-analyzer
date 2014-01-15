package br.ufrn.sigaa.ensino.graduacao.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class RetornoDiscentesTrancamentoDao extends GenericSigaaDAO {
	
	/** Define o primeiro semestre. */
	public static final int PRIMEIRO = 1;
	/** Define o segundo semestre. */
	public static final int SEGUNDO = 2;
	/** Define o semestre anterior. */
	public static final int ANTERIOR = 1;

	/**
	 * Buscar discentes com programa trancado no semestre anterior que não
	 * tem trancamento para o semestre atual de acordo com o curso informado.
	 * 
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public List<Discente> findDiscentesRetornoTrancamento(int ano, int periodo, char nivel, Unidade unidade) throws DAOException {
		int anoPassado = 0;
		int periodoPassado = 0;

		if (periodo == SEGUNDO) {
			anoPassado = ano;
			periodoPassado = PRIMEIRO;
		}
		if (periodo == PRIMEIRO) {
			anoPassado = ano - ANTERIOR;
			periodoPassado = SEGUNDO;
		}

		StringBuffer sb = new StringBuffer(
				"select p.nome, d.matricula, d.id_discente, c.nome as nome_curso from discente d "
						+ " inner join comum.pessoa p ON p.id_pessoa = d.id_pessoa "
						+ " inner join curso c ON c.id_curso = d.id_curso "
						+ " where d.status = ? "
						+ " and d.nivel = "
						+ "?");
		
		if(!isEmpty(unidade))
			sb.append(" and d.id_gestora_academica = ? ");
		
		sb.append(		" and exists "
						+ " (select id_movimentacao_aluno from ensino.movimentacao_aluno "
						+ " where id_discente = d.id_discente and ano_referencia = ? "
						+ " and periodo_referencia = ? "
						+ " and data_cadastro_retorno is null "
						+ " and ativo = trueValue() "
						+ " and id_tipo_movimentacao_aluno = ?) "
						+ " and not exists "
						+ " (select id_movimentacao_aluno from ensino.movimentacao_aluno "
						+ " where id_discente = d.id_discente and ano_referencia = ? "
						+ " and periodo_referencia = ? "
						+ " and ativo = trueValue() "
						+ " and id_tipo_movimentacao_aluno = ?"
						+ ") "
						+ " order by p.nome, c.nome");
		
		Object[] args = null;
		String nivelString = ""+nivel;
		
		if(isEmpty(unidade))
			args = new Object[] {StatusDiscente.TRANCADO, nivelString, anoPassado, periodoPassado, TipoMovimentacaoAluno.TRANCAMENTO, ano, periodo, TipoMovimentacaoAluno.TRANCAMENTO};
		else
			args = new Object[] {StatusDiscente.TRANCADO, nivelString, unidade.getId(), anoPassado, periodoPassado, TipoMovimentacaoAluno.TRANCAMENTO, ano, periodo, TipoMovimentacaoAluno.TRANCAMENTO};
		
		@SuppressWarnings("unchecked")
		List<Discente> result = getJdbcTemplate().query(sb.toString(), args,
				new RowMapper() {
					public Object mapRow(ResultSet rs, int line)
							throws SQLException {
		
						Pessoa p = new Pessoa();
						p.setNome(rs.getString("nome"));
		
						Curso c = new Curso();
						c.setNome(rs.getString("nome_curso"));
		
						Discente discente = new Discente();
						discente.setId(rs.getInt("id_discente"));
						discente.setMatricula(rs.getLong("matricula"));
						discente.setPessoa(p);
						discente.setCurso(c);
		
						return discente;
					}
				});

		return result;

	}
}
