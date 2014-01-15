/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 16/12/2008 
 *
 */

package br.ufrn.sigaa.processamento.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;

/**
 * Classe para realizar consultas associadas
 * ao processamento de matrículas do ensino
 * a distância.
 * 
 * @author David Pereira
 *
 */

public class ProcessamentoMatriculaEadDao extends GenericSigaaDAO implements ProcessamentoMatriculaDao {

	
	public List<Integer> findAlunosPreProcessamento(int ano, int periodo, boolean rematricula) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");
	}

	public int findCountTurmasProcessar(int ano, int periodo, boolean rematricula) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");
	}

	@SuppressWarnings("unchecked")
	public List<SolicitacaoMatricula> findSolicitacoesMatricula(int ano, int periodo, boolean rematricula) {
		return getJdbcTemplate().query("select sm.id_solicitacao_matricula, sm.ano, sm.periodo, sm.id_turma, cc.id_disciplina, cc.id_detalhe, sm.id_discente, sm.status "  
				+ "from graduacao.solicitacao_matricula sm left join ensino.turma t using (id_turma) " 
				+ "left join ensino.componente_curricular cc using (id_disciplina) left join discente d using (id_discente) "
				+ "where sm.ano = ? and sm.periodo = ? and sm.anulado = falseValue() and sm.status != 9 and sm.rematricula = ? and "
				+ "sm.id_matricula_gerada is null and t.id_polo is not null and d.nivel = 'G'",
				new Object[] { ano, periodo, rematricula }, new RowMapper() {

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				SolicitacaoMatricula sm = new SolicitacaoMatricula();
				sm.setId(rs.getInt("id_solicitacao_matricula"));
				sm.setAno(rs.getInt("ano"));
				sm.setPeriodo(rs.getInt("periodo"));
				sm.setStatus(rs.getInt("status"));
				sm.setTurma(new Turma(rs.getInt("id_turma")));
				sm.getTurma().setDisciplina(new ComponenteCurricular(rs.getInt("id_disciplina")));
				sm.getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes(rs.getInt("id_detalhe")));
				sm.setDiscente(new Discente(rs.getInt("id_discente")));
				return sm;
			}

		});
	}

	public List<Turma> findTurmasProcessar(int ano, int periodo, boolean rematricula) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");
	}

	public Map<MatrizCurricular, ReservaCurso> findInformacoesVagasTurma(int id) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");
	}

	public Map<Integer, List<MatriculaEmProcessamento>> findMatriculasEmOrdemParaProcessamento(Turma turma, Map<MatrizCurricular, ReservaCurso> vagasReservadas, boolean rematricula) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");
	}

	public void registrarProcessamentoTurma(Turma turma, List<MatriculaEmProcessamento> resultadoProcessamento) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");
	}

	public void registrarProcessamentoTurma(int ano, int periodo, boolean rematricula, Turma turma, List<MatriculaEmProcessamento> resultadoProcessamento) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");
	}

	public int findTotalMatriculadosTurma(int idTurma) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");	
	}

	public List<Integer> findDiscentesMatriculadosEmBlocos(int ano, int periodo, boolean rematricula) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");
	}

	public List<Integer> findDiscentesMatriculadosEmCoRequisitos(int ano, int periodo, boolean rematricula) {
		throw new UnsupportedOperationException("Método não implementado no Ensino a Distância.");
	}

	/** 
	 * @see br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao#atualizaMotivoIndeferimento(java.lang.Integer, java.lang.String)
	 */
	public void atualizaMotivoIndeferimento(Integer key, String string) {
		
		
	}

	/**
	 * @see br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao#findMatriculasDiscente(java.lang.Integer, int, int, boolean)
	 */
	public List<MatriculaComponente> findMatriculasDiscente(Integer discente,
			int i, int j, boolean b) {
		return null;
	}

	/**
	 * @see br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao#findMatriculasEmBlocoDiscente(java.lang.Integer, int, int, boolean)
	 */
	public List<MatriculaComponente> findMatriculasEmBlocoDiscente(
			Integer discente, int i, int j, boolean rematricula) {
		return null;
	}

	/**
	 * @see br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao#findMatriculasEmCoRequisitoDiscente(java.lang.Integer, int, int, boolean)
	 */
	public List<MatriculaComponente> findMatriculasEmCoRequisitoDiscente(
			Integer discente, int i, int j, boolean b) {
		return null;
	}

	/**
	 * @see br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao#findResultadoProcessamento(int)
	 */
	public List<MatriculaEmProcessamento> findResultadoProcessamento(int id) {
		return null;
	}

	/**
	 * @see br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao#findTurmasEnsinoIndividualizadoProcessar(int, int, boolean, boolean)
	 */
	public List<Turma> findTurmasEnsinoIndividualizadoProcessar(int ano,
			int periodo, boolean rematricula, boolean turmasRegular) {
		return null;
	}
	
}
