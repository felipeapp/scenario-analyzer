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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;
import br.ufrn.sigaa.processamento.dominio.ProcessamentoMatriculasTurma;

/**
 * Classe para realizar consultas referentes
 * ao processamento de matrículas do ensino
 * técnico de música.
 * 
 * @author David Pereira
 *
 */

public class ProcessamentoMatriculaMusicaDao extends GenericSigaaDAO implements ProcessamentoMatriculaDao {

	private static String SQL_TURMAS_PROCESSAR = 
		"from ensino.turma t left join ensino.matricula_componente mc using (id_turma) left join discente d using(id_discente) "
		+ "left join ensino.componente_curricular cc using (id_disciplina) left join ensino.componente_curricular_detalhes ccd " 
		+ "on (cc.id_detalhe = ccd.id_componente_detalhes) "
		+ "where t.ano = ? and t.periodo = ? and t.id_situacao_turma in (1,2) and t.tipo = 1 and cc.nivel = d.nivel "
		+ "and mc.id_situacao_matricula = 1 and d.nivel = 'T' and d.id_gestora_academica = " + UnidadeGeral.ESCOLA_MUSICA + " "; 
	
	public List<Integer> findAlunosPreProcessamento(int ano, int periodo, boolean rematricula) {
		return null;
	}

	public int findCountTurmasProcessar(int ano, int periodo, boolean rematricula) {
		return getJdbcTemplate().queryForInt("select count(*) " + SQL_TURMAS_PROCESSAR
				+ (rematricula ? "and (t.processada_rematricula is null or t.processada_rematricula = falseValue())" : 
					"and (t.processada is null or t.processada = falseValue())"), 
				new Object[] { ano, periodo });
	}

	@SuppressWarnings("unchecked")
	public List<SolicitacaoMatricula> findSolicitacoesMatricula(int ano, int periodo, boolean rematricula) {
		return getJdbcTemplate().query("select sm.id_solicitacao_matricula, sm.ano, sm.periodo, sm.id_turma, cc.id_disciplina, cc.id_detalhe, sm.id_discente, sm.status " 
				+ "from graduacao.solicitacao_matricula sm left join ensino.turma t using (id_turma) " 
				+ "left join ensino.componente_curricular cc using (id_disciplina) left join discente d using (id_discente) " 
				+ "where sm.ano = ? and sm.periodo = ? and sm.anulado = falseValue() and sm.status != 9 and sm.rematricula = ? and " 
				+ "sm.id_matricula_gerada is null and t.id_polo is null and d.nivel = 'T' and d.id_gestora_academica = " + UnidadeGeral.ESCOLA_MUSICA,
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

	@SuppressWarnings("unchecked")
	public List<Turma> findTurmasProcessar(int ano, int periodo, boolean rematricula) {
		String projecao = "select distinct t.id_turma, t.ano, t.periodo, t.capacidade_aluno, t.codigo as codigo_turma, " 
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula = 2) as total_matriculado, "
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula = 4) as total_aprovado, "
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula = 6) as total_reprovado, "
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula = 7) as total_reprovado_falta, "
			+ "t.codigo, t.descricao_horario, cc.id_disciplina, cc.id_detalhe, ccd.nome, ccd.codigo ";
			
			System.out.println(projecao + SQL_TURMAS_PROCESSAR
					+ (rematricula ? "and (t.processada_rematricula is null or t.processada_rematricula = falseValue())" : 
					"and (t.processada is null or t.processada = falseValue()) order by t.id_turma"));
		
			return getJdbcTemplate().query(projecao + SQL_TURMAS_PROCESSAR
					+ (rematricula ? "and (t.processada_rematricula is null or t.processada_rematricula = falseValue())" : 
					"and (t.processada is null or t.processada = falseValue()) order by t.id_turma"), 
				new Object[] { ano, periodo }, 
				new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						Turma t = new Turma();
						t.setId(rs.getInt("id_turma"));
						t.setAno(rs.getInt("ano"));
						t.setPeriodo(rs.getInt("periodo"));
						t.setCapacidadeAluno(rs.getInt("capacidade_aluno"));
						t.setQtdMatriculados(rs.getInt("total_matriculado"));
						t.setQtdAprovados(rs.getInt("total_aprovado"));
						t.setQtdReprovados(rs.getInt("total_reprovado"));
						t.setQtdReprovadosFalta(rs.getInt("total_reprovado_falta"));
						t.setCodigo(rs.getString("codigo_turma"));
						t.setDescricaoHorario(rs.getString("descricao_horario"));
						t.setDisciplina(new ComponenteCurricular());
						t.getDisciplina().setId(rs.getInt("id_disciplina"));
						t.getDisciplina().setDetalhes(new ComponenteDetalhes());
						t.getDisciplina().getDetalhes().setId(rs.getInt("id_detalhe"));
						t.getDisciplina().getDetalhes().setNome(rs.getString("nome"));
						t.getDisciplina().getDetalhes().setCodigo(rs.getString("codigo"));
						return t;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public Map<MatrizCurricular, ReservaCurso> findInformacoesVagasTurma(int id) {
		String sql = "select rc.id_reserva_curso, rc.id_matriz_curricular, rc.vagas_atendidas, " 
			+ "(select count(*) from ensino.matricula_componente mc, graduacao.discente_graduacao dg where mc.id_turma = rc.id_turma "
			+ "and dg.id_discente_graduacao = mc.id_discente and dg.id_matriz_curricular = rc.id_matriz_curricular and mc.id_situacao_matricula = 2) as vagas_ocupadas, "
			+ "c.id_curso, c.nome, h.nome as habilitacao, t.sigla as turno, g.descricao as grau "
			+ "from graduacao.reserva_curso rc " 
			+ "left join graduacao.matriz_curricular mc using (id_matriz_curricular) "
			+ "left join graduacao.habilitacao h using (id_habilitacao) "
			+ "left join curso c on (c.id_curso = mc.id_curso) "
			+ "left join ensino.turno t on (t.id_turno = mc.id_turno) "
			+ "left join ensino.grau_academico g on (g.id_grau_academico = mc.id_grau_academico) "
			+ "where rc.id_turma = ?";

		List<ReservaCurso> lista = getJdbcTemplate().query(sql, new Object[] { id }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReservaCurso reserva = new ReservaCurso();
				reserva.setId(rs.getInt("id_reserva_curso"));

				short vagasAtendidas = rs.getShort("vagas_atendidas");
				short vagasOcupadas = rs.getShort("vagas_ocupadas");
				short totalVagas = (short) (vagasAtendidas - vagasOcupadas);

				reserva.setVagasSolicitadas(totalVagas);
				reserva.setVagasReservadas(totalVagas);
				reserva.setMatrizCurricular(new MatrizCurricular());
				reserva.getMatrizCurricular().setCurso(new Curso());
				reserva.getMatrizCurricular().setId(rs.getInt("id_matriz_curricular"));
				reserva.getMatrizCurricular().getCurso().setId(rs.getInt("id_curso"));
				reserva.getMatrizCurricular().getCurso().setNome(rs.getString("nome"));
				reserva.getMatrizCurricular().setHabilitacao(new Habilitacao());
				reserva.getMatrizCurricular().getHabilitacao().setNome(rs.getString("habilitacao"));
				reserva.getMatrizCurricular().setTurno(new Turno());
				reserva.getMatrizCurricular().getTurno().setSigla(rs.getString("turno"));
				reserva.getMatrizCurricular().setGrauAcademico(new GrauAcademico());
				reserva.getMatrizCurricular().getGrauAcademico().setDescricao(rs.getString("grau"));
				return reserva;
			}
		});

		Map<MatrizCurricular, ReservaCurso> result = new HashMap<MatrizCurricular, ReservaCurso>();
		for (ReservaCurso reserva : lista) {
			result.put(reserva.getMatrizCurricular(), reserva);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, List<MatriculaEmProcessamento>> findMatriculasEmOrdemParaProcessamento(Turma turma, Map<MatrizCurricular, ReservaCurso> vagasPorReserva, boolean rematricula) {
		String sql = getSqlMatriculasTurma(turma);
		
		List<MatriculaEmProcessamento> resultado = getJdbcTemplate().query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
				MatriculaEmProcessamento matricula = new MatriculaEmProcessamento();

				boolean nivelado = rs.getBoolean("nivelado");
				boolean formando = rs.getBoolean("formando");
				boolean recuperacao = rs.getBoolean("recuperacao");
				boolean adiantado = rs.getBoolean("adiantado");
				boolean eletivo = rs.getBoolean("eletivo");
				boolean vestibular = rs.getBoolean("vestibular");

				int tipo = MatriculaEmProcessamento.OUTROS;
				if (nivelado) tipo = MatriculaEmProcessamento.NIVELADO;
				else if (formando) tipo = MatriculaEmProcessamento.FORMANDO;
				else if (recuperacao) tipo = MatriculaEmProcessamento.RECUPERACAO;
				else if (adiantado) tipo = MatriculaEmProcessamento.ADIANTADO;
				else if (eletivo) tipo = MatriculaEmProcessamento.ELETIVO;
				else if (vestibular) tipo = MatriculaEmProcessamento.VESTIBULAR;

				matricula.setIdMatrizCurricular(rs.getInt("id_matriz_curricular"));
				matricula.setIdDiscente(rs.getInt("id_discente"));
				matricula.setIdMatriculaComponente(rs.getInt("id_matricula_componente"));
				matricula.setTipo(tipo);
				matricula.setIndice(rs.getDouble("ira"));
				matricula.setMatricula(rs.getLong("matricula"));
				matricula.setNome(rs.getString("nome"));
				matricula.setCurso(rs.getString("curso"));

				return matricula;
			}
		});

		// Monta mapa de matrículas de acordo com a ordem de prioridade
		Map<Integer, List<MatriculaEmProcessamento>> matriculas = new HashMap<Integer, List<MatriculaEmProcessamento>>();

		matriculas.put(0, new ArrayList<MatriculaEmProcessamento>()); // Outros que não estão na reserva
		for (MatrizCurricular matriz : vagasPorReserva.keySet()) // Reservas
			matriculas.put(matriz.getId(), new ArrayList<MatriculaEmProcessamento>());

		for (MatriculaEmProcessamento linha : resultado) {
			if (matriculas.containsKey(linha.getIdMatrizCurricular()))
				matriculas.get(linha.getIdMatrizCurricular()).add(linha);
			else
				matriculas.get(0).add(linha);
		}

		for (List<MatriculaEmProcessamento> lista : matriculas.values()) {
			Collections.sort(lista, ProcessamentoMatriculasTurma.PROCESSAMENTO_COMPARATOR);
		}

		return matriculas;
	}
	
	public String getSqlMatriculasTurma(Turma turma) {
		return "select d.id_discente, mc.id_matricula_componente, coalesce((select (sum(mc.media_final * ccd.ch_total) / sum(ccd.ch_total)) "
			+ "from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
			+ "where mc.id_discente = d.id_discente and mc.id_componente_curricular = cc.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes "
			+ "and mc.id_situacao_matricula in (4, 6, 7, 22, 23) "
			+ "and (cc.id_tipo_componente != 1 or (cc.id_tipo_componente = 1 and ccd.ch_total > 0 and cc.id_tipo_atividade in (1, 3) and cc.necessitamediafinal = trueValue()))), 10) "
			+ "as ira, 0 as id_matriz_curricular, d.matricula, c.nome as curso, p.nome, " 
			+ "falseValue() as nivelado, falseValue() as formando, falseValue() as recuperacao, falseValue() as adiantado, falseValue() as eletivo, falseValue() as vestibular "
			+ "from discente d, comum.pessoa p, curso c, ensino.matricula_componente mc "
			+ "where d.nivel = 'T' and d.id_gestora_academica = " + UnidadeGeral.ESCOLA_MUSICA + " and d.id_discente = mc.id_discente "
			+ "and d.id_pessoa = p.id_pessoa and d.id_curso = c.id_curso "
			+ "and mc.ano = " + turma.getAno() + " and mc.periodo = " + turma.getPeriodo() + " and mc.id_situacao_matricula = 1 and mc.id_turma = " + turma.getId();
	}

	public void registrarProcessamentoTurma(int ano, int periodo, boolean rematricula, Turma turma, List<MatriculaEmProcessamento> matriculas) {
		DataSource ds = Database.getInstance().getSigaaDs();
		registrarProcessamentoTurma(ano, periodo, rematricula, turma, matriculas, ds);
	}
	
	public void registrarProcessamentoTurma(int ano, int periodo, boolean rematricula, Turma turma, List<MatriculaEmProcessamento> matriculas, DataSource ds) {

		BatchSqlUpdate alterarStatusMatricula = new BatchSqlUpdate(ds, "update ensino.matricula_componente set id_situacao_matricula=? where id_matricula_componente=?");
		alterarStatusMatricula.declareParameter(new SqlParameter(Types.INTEGER));
		alterarStatusMatricula.declareParameter(new SqlParameter(Types.INTEGER));
		alterarStatusMatricula.compile();

		BatchSqlUpdate setaAlunoCadastradoParaAtivo = new BatchSqlUpdate(ds, "update discente set status=? where id_discente=?");
		setaAlunoCadastradoParaAtivo.declareParameter(new SqlParameter(Types.INTEGER));
		setaAlunoCadastradoParaAtivo.declareParameter(new SqlParameter(Types.INTEGER));
		setaAlunoCadastradoParaAtivo.compile();

		BatchSqlUpdate setaUltimaAtualizacaoTotaisNull = new BatchSqlUpdate(ds, "update graduacao.discente_graduacao set ultima_atualizacao_totais=null where id_discente_graduacao=?");
		setaUltimaAtualizacaoTotaisNull.declareParameter(new SqlParameter(Types.INTEGER));
		setaUltimaAtualizacaoTotaisNull.compile();

		BatchSqlUpdate inserirResultadoProcessamento = new BatchSqlUpdate(ds, "insert into graduacao.resultado_processamento_matricula (id, tipo_resultado, ordem, id_matricula_componente, "
				+ "id_matriz_curricular, indice, data_processamento, ano, periodo, id_situacao_matricula) values ((select nextval('graduacao.resultado_processamento_seq')), ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		inserirResultadoProcessamento.declareParameter(new SqlParameter(Types.INTEGER));
		inserirResultadoProcessamento.declareParameter(new SqlParameter(Types.INTEGER));
		inserirResultadoProcessamento.declareParameter(new SqlParameter(Types.INTEGER));
		inserirResultadoProcessamento.declareParameter(new SqlParameter(Types.INTEGER));
		inserirResultadoProcessamento.declareParameter(new SqlParameter(Types.DECIMAL));
		inserirResultadoProcessamento.declareParameter(new SqlParameter(Types.DATE));
		inserirResultadoProcessamento.declareParameter(new SqlParameter(Types.INTEGER));
		inserirResultadoProcessamento.declareParameter(new SqlParameter(Types.INTEGER));
		inserirResultadoProcessamento.declareParameter(new SqlParameter(Types.INTEGER));
		inserirResultadoProcessamento.compile();

		for (MatriculaEmProcessamento matricula : matriculas) {
			alterarStatusMatricula.update(new Object[] { matricula.getSituacao().getId(), matricula.getIdMatriculaComponente() });
			if (SituacaoMatricula.MATRICULADO.equals(matricula.getSituacao())) {
				int status = getJdbcTemplate().queryForInt("select status from discente where id_discente = ?", new Object[] { matricula.getIdDiscente() });
				if (status == StatusDiscente.CADASTRADO)
					setaAlunoCadastradoParaAtivo.update(new Object[] { StatusDiscente.ATIVO, matricula.getIdDiscente() });

				setaUltimaAtualizacaoTotaisNull.update(new Object[] { matricula.getIdDiscente() });
			}
			inserirResultadoProcessamento.update(new Object[] { matricula.getTipo(), matricula.getOrdem(), matricula.getIdMatriculaComponente(), matricula.getIdMatrizReserva() == 0 ? null : matricula.getIdMatrizReserva(), matricula.getIndice(), new Date(), ano, periodo, matricula.getSituacao().getId() });
		}

		alterarStatusMatricula.flush();
		setaUltimaAtualizacaoTotaisNull.flush();
		inserirResultadoProcessamento.flush();

		if (rematricula)
			getJdbcTemplate().update("update ensino.turma set processada_rematricula=trueValue() where id_turma=?", new Object[] { turma.getId() });
		else
			getJdbcTemplate().update("update ensino.turma set processada=trueValue() where id_turma=?", new Object[] { turma.getId() });
	}

	public int findTotalMatriculadosTurma(int idTurma) {
		return getJdbcTemplate().queryForInt("select count(*) from ensino.matricula_componente where id_turma = ? and id_situacao_matricula = 2", idTurma);
	}

	public List<Integer> findDiscentesMatriculadosEmBlocos(int ano, int periodo, boolean rematricula) {
		return null;
	}

	public List<Integer> findDiscentesMatriculadosEmCoRequisitos(int ano, int periodo, boolean rematricula) {
		return null;
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
