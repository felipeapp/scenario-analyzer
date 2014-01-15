package br.ufrn.sigaa.ensino.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.parametros.dominio.ParametrosFormaIngresso;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class PrazoMaximoGraduacaoQueryDao extends GenericSigaaDAO implements JubilamentoQuery {

	@Override
	public Collection<Discente> findAlunosPassiveisJubilamento(List<Integer[]> anosPeriodo, Boolean ead, Boolean filtroMatriculados, Unidade unidade, Boolean reprovacoesComp) throws DAOException {		
		return findDiscentesPrazoEsgotado(anosPeriodo.iterator().next()[0],anosPeriodo.iterator().next()[1]);
	}
	
	/**
	 * Todos os alunos que passaram do prazo.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public Collection<Discente> findDiscentesPrazoEsgotado(int ano, int periodo) {
		String sql = "select d.matricula, d.ano_ingresso as anoIngresso, d.periodo_ingresso as periodoIngresso, p.nome, "
			+ " d.prazo_conclusao, d.status as StatusAluno, f.descricao as FormaIngresso, c.id_curso, c.nome as Curso, d.id_discente, p.id_pessoa, " 
			+ " modalidade.id_modalidade_educacao "
			+ " from discente d "
			+ " inner join graduacao.discente_graduacao dg on (dg.id_discente_graduacao = d.id_discente) "
			+ " inner join comum.pessoa p on (p.id_pessoa = d.id_pessoa) "
			+ " inner join curso c on (d.id_curso = c.id_curso) "
			+ " inner join comum.modalidade_educacao modalidade on (c.id_modalidade_educacao = modalidade.id_modalidade_educacao) "
			+ " inner join ensino.forma_ingresso f on (d.id_forma_ingresso = f.id_forma_ingresso) "
			+ " where status in " + UFRNUtils.gerarStringIn(new int[] { StatusDiscente.ATIVO, StatusDiscente.FORMANDO})
			+ " and d.prazo_conclusao <= " + ano + "" + periodo + " "
			+ " and d.nivel = '"+NivelEnsino.GRADUACAO+"' "
			+ " and f.id_forma_ingresso <> " + ParametroHelper.getInstance().getParametroInt(ParametrosFormaIngresso.FORMA_INGRESSO_JUDICIAL)
			+ " order by d.matricula ";

		@SuppressWarnings("unchecked")
		Collection<Discente> lista = getJdbcTemplate().query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Discente d = new Discente();
				d.setId(rs.getInt("id_discente"));
				d.setMatricula(rs.getLong("matricula"));
				d.setPessoa(new Pessoa());
				d.getPessoa().setNome(rs.getString("nome"));
				d.getPessoa().setId(rs.getInt("id_pessoa"));
				d.setStatus(rs.getInt("StatusAluno"));
				d.setAnoIngresso(rs.getInt("anoIngresso"));
				d.setPeriodoIngresso(rs.getInt("periodoIngresso"));
				d.setNivel(NivelEnsino.GRADUACAO);
				d.setGestoraAcademica(new Unidade(Unidade.UNIDADE_DIREITO_GLOBAL));
				d.setCurso(new Curso());
				d.getCurso().setId(rs.getInt("id_curso"));
				d.getCurso().setNome(rs.getString("curso"));
				d.setPrazoConclusao(rs.getInt("prazo_conclusao"));
				d.getCurso().setModalidadeEducacao(new ModalidadeEducacao());
				d.getCurso().getModalidadeEducacao().setId(rs.getInt("id_modalidade_educacao"));
				return d;
			}
		});
		return lista;
	}

}
