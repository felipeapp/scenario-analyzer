/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 16/12/2008 
 *
 */

package br.ufrn.sigaa.processamento.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.CategoriaDiscenteEspecial;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;
import br.ufrn.sigaa.processamento.dominio.ProcessamentoMatriculasTurma;

/**
 * Implementação do DAO de processamento de matrícula com
 * consultas para o processamento de turmas de graduação.
 * 
 * @author David Pereira
 *
 */
@SuppressWarnings("unchecked")
public class ProcessamentoMatriculaGraduacaoDao extends GenericSigaaDAO implements ProcessamentoMatriculaDao {

	private static String SQL_TURMAS_PROCESSAR = 
		"from ensino.turma t, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
		+ "where t.ano = ? and t.periodo = ? " 
		+ "and t.id_situacao_turma in (1, 2) " // aberta ou a definir docente
		+ "and t.tipo = 1 " // Regular (não e de férias nem ensino individual)
		//+ "and t.tipo = 2 " // TURMA DE FÈRIAS
		+ "and t.id_disciplina = cc.id_disciplina and cc.nivel = ? " // Nível de ensino
		+ "and cc.id_detalhe = ccd.id_componente_detalhes "; // Matrícula ou rematrícula
	
	public int findCountTurmasProcessar(int ano, int periodo, boolean rematricula) {
		return getJdbcTemplate().queryForInt("select count(*) " + SQL_TURMAS_PROCESSAR
				+ (rematricula ? "and (t.processada_rematricula is null or t.processada_rematricula = falseValue())" : 
					"and (t.processada is null or t.processada = falseValue())"), 
				new Object[] { ano, periodo, String.valueOf(NivelEnsino.GRADUACAO) });
	}
	
	public List<Turma> findTurmasProcessar(int ano, int periodo, boolean rematricula) {
		String projecao = "select t.id_turma, t.ano, t.periodo, t.capacidade_aluno, t.codigo as codigo_turma, " 
		+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula = 2) as total_matriculado, "
		+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula = 4) as total_aprovado, "
		+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula = 6) as total_reprovado, "
		+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula = 7) as total_reprovado_falta, "
		+ "t.codigo, t.descricao_horario, cc.id_disciplina, cc.id_detalhe, ccd.nome, ccd.codigo ";
		
		System.out.println( projecao + SQL_TURMAS_PROCESSAR
				+ (rematricula ? "and (t.processada_rematricula is null or t.processada_rematricula = falseValue())" : 
				"and (t.processada is null or t.processada = falseValue())" ) );
		return getJdbcTemplate().query(projecao + SQL_TURMAS_PROCESSAR
				+ (rematricula ? "and (t.processada_rematricula is null or t.processada_rematricula = falseValue())" : 
				"and (t.processada is null or t.processada = falseValue())"), 
			new Object[] { ano, periodo, String.valueOf(NivelEnsino.GRADUACAO) }, 
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
	
	public List<Turma> findTurmasProcessadas(int ano, int periodo, boolean rematricula) {
		String projecao = "select t.id_turma, t.ano, t.periodo, t.capacidade_aluno, t.codigo as codigo_turma, " 
//			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula in (2, 4, 6, 7, 12) and rematricula = falseValue()) as total_matriculados, "
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula =  2 and rematricula = falseValue()) as total_matriculado, "
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula =  4 and rematricula = falseValue()) as total_aprovado, "
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula =  5 and rematricula = falseValue()) as total_trancado, "
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula =  6 and rematricula = falseValue()) as total_reprovado, "
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula =  7 and rematricula = falseValue()) as total_reprovado_falta, "
			+ "(select count(*) from ensino.matricula_componente where id_turma = t.id_turma and id_situacao_matricula = 12 and rematricula = falseValue()) as total_desistencias, "
			+ "t.codigo, t.descricao_horario, cc.id_disciplina, cc.id_detalhe, ccd.nome, ccd.codigo as codigo_componente ";
			
			return getJdbcTemplate().query(projecao + SQL_TURMAS_PROCESSAR
					+ (rematricula ? "and t.processada_rematricula = trueValue() " : 
					"and (t.processada = trueValue() and (t.processada_rematricula is null or t.processada_rematricula = falseValue()))"), 
				new Object[] { ano, periodo, String.valueOf(NivelEnsino.GRADUACAO) }, 
				new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						Turma t = new Turma();
						t.setId(rs.getInt("id_turma"));
						t.setAno(rs.getInt("ano"));
						t.setPeriodo(rs.getInt("periodo"));
						t.setCapacidadeAluno(rs.getInt("capacidade_aluno"));
						t.setQtdMatriculados(rs.getInt("total_matriculado"));
						t.setQtdAprovados(rs.getInt("total_aprovado"));
						t.setQtdTrancados(rs.getInt("total_trancado"));
						t.setQtdReprovados(rs.getInt("total_reprovado"));
						t.setQtdReprovadosFalta(rs.getInt("total_reprovado_falta"));
						t.setTotalDesistencias(rs.getInt("total_desistencias"));
						t.setCodigo(rs.getString("codigo_turma"));
						t.setDescricaoHorario(rs.getString("descricao_horario"));
						t.setDisciplina(new ComponenteCurricular());
						t.getDisciplina().setId(rs.getInt("id_disciplina"));
						t.getDisciplina().setDetalhes(new ComponenteDetalhes());
						t.getDisciplina().getDetalhes().setId(rs.getInt("id_detalhe"));
						t.getDisciplina().getDetalhes().setNome(rs.getString("nome"));
						t.getDisciplina().getDetalhes().setCodigo(rs.getString("codigo_componente"));
						return t;
					}
				});
	}

	public List<SolicitacaoMatricula> findSolicitacoesMatricula(int ano, int periodo, boolean rematricula) {
		return getJdbcTemplate().query("select distinct sm.id_solicitacao_matricula, sm.ano, sm.periodo, sm.id_turma, cc.id_disciplina, cc.id_detalhe, sm.id_discente, sm.status "  
				+ "from graduacao.solicitacao_matricula sm left join ensino.turma t using (id_turma) " 
				+ "left join ensino.componente_curricular cc using (id_disciplina) left join discente d using (id_discente) "
				+ "where sm.ano = ? and sm.periodo = ? and sm.anulado = falseValue() and sm.status not in (6,9) and sm.rematricula = ? and "
				+ "sm.id_matricula_gerada is null and t.id_polo is null and d.nivel = 'G'",
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

	public List<Integer> findAlunosPreProcessamento(int ano, int periodo, boolean rematricula) {
		getJdbcTemplate().update("update graduacao.discente_graduacao set possivel_formando = null");
		return getJdbcTemplate().queryForList("select distinct d.id_discente from ensino.matricula_componente mc left join discente d using (id_discente) left join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao) " +
				"where ano = ? and periodo = ? and d.nivel = 'G' and mc.id_situacao_matricula = 1 and d.status in (1,8) " +
				"and mc.rematricula = ?",
				new Object[] { ano, periodo, rematricula }, Integer.class);
	}

	public Map<MatrizCurricular, ReservaCurso> findInformacoesVagasTurma(int id) {
		String sql = "select rc.id_reserva_curso, rc.id_matriz_curricular, rc.vagas_atendidas, rc.vagas_atendidas_ingressantes, " 
			+ "(select count(*) from ensino.matricula_componente mc, graduacao.discente_graduacao dg, discente d where mc.id_turma = rc.id_turma "
			+ "	and dg.id_discente_graduacao = d.id_discente and dg.id_discente_graduacao = mc.id_discente "
			+ "	and mc.id_situacao_matricula = 2 "
			+ " and not (dg.id_matriz_curricular = rc.id_matriz_curricular and d.ano_ingresso = mc.ano and d.periodo_ingresso = mc.periodo and d.id_forma_ingresso in (34110, 51252808)) ) as vagas_ocupadas, "
			
			+ "(select count(*) from ensino.matricula_componente mc, graduacao.discente_graduacao dg, discente d where mc.id_turma = rc.id_turma "
			+ "and dg.id_discente_graduacao = d.id_discente and dg.id_discente_graduacao = mc.id_discente and dg.id_matriz_curricular = rc.id_matriz_curricular "
			+ "and mc.id_situacao_matricula = 2 and d.ano_ingresso = mc.ano and d.periodo_ingresso = mc.periodo and d.id_forma_ingresso in (34110, 51252808) ) as vagas_ocupadas_ingressantes, "
			
			+ "c.id_curso, c.nome, h.nome as habilitacao, t.sigla as turno, g.descricao as grau "
			+ "from graduacao.reserva_curso rc " 
			+ "left join graduacao.matriz_curricular mc using (id_matriz_curricular) "
			+ "left join graduacao.habilitacao h using (id_habilitacao) "
			+ "left join curso c on (c.id_curso = mc.id_curso) "
			+ "left join ensino.turno t on (t.id_turno = mc.id_turno) "
			+ "left join ensino.grau_academico g on (g.id_grau_academico = mc.id_grau_academico) "
			+ "where rc.id_turma = ?";

		
		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, id);
			rs = st.executeQuery();
			
			List<ReservaCurso> lista = new ArrayList<ReservaCurso>();
			while (rs.next()) {
				ReservaCurso reserva = new ReservaCurso();
				reserva.setId(rs.getInt("id_reserva_curso"));

				short vagasAtendidas = rs.getShort("vagas_atendidas");
				short vagasOcupadas = rs.getShort("vagas_ocupadas");
				short totalVagas = (short) (vagasAtendidas - vagasOcupadas);
				short vagasAtendidasIngressantes = rs.getShort("vagas_atendidas_ingressantes");
				short vagasOcupadasIngressantes = rs.getShort("vagas_ocupadas_ingressantes");
				short totalVagasIngressantes = (short) (vagasAtendidasIngressantes - vagasOcupadasIngressantes);

				reserva.setVagasSolicitadas(totalVagas);
				reserva.setVagasReservadas(totalVagas);
				reserva.setVagasSolicitadasIngressantes(totalVagasIngressantes);
				reserva.setVagasReservadasIngressantes(totalVagasIngressantes);
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
				lista.add(reserva);
			}
			
			Map<MatrizCurricular, ReservaCurso> result = new HashMap<MatrizCurricular, ReservaCurso>();
			for (ReservaCurso reserva : lista) {
				result.put(reserva.getMatrizCurricular(), reserva);
			}

			return result;
			
		} catch(SQLException e) {
			return new HashMap<MatrizCurricular, ReservaCurso>();
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(con);
		}
	}

	
	
	public Map<Integer, List<MatriculaEmProcessamento>> findMatriculasEmOrdemParaProcessamento(Turma turma, Map<MatrizCurricular, ReservaCurso> vagasPorReserva, boolean rematricula) {
		String sql = getSqlMatriculasTurma(turma, rematricula);
		
		System.out.println(sql);
		
		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			rs = st.executeQuery();
			
			List<MatriculaEmProcessamento> resultado = new ArrayList<MatriculaEmProcessamento>();
			while (rs.next()) {
				MatriculaEmProcessamento matricula = new MatriculaEmProcessamento();

				boolean nivelado = rs.getBoolean("nivelado");
				boolean formando = rs.getBoolean("formando");
				boolean recuperacao = rs.getBoolean("recuperacao");
				boolean adiantado = rs.getBoolean("adiantado");
				boolean eletivo = rs.getBoolean("eletivo");
				boolean vestibular = rs.getBoolean("vestibular");

				int tipo = MatriculaEmProcessamento.OUTROS;
				if (vestibular) tipo = MatriculaEmProcessamento.VESTIBULAR;
				else if (nivelado) tipo = MatriculaEmProcessamento.NIVELADO;
				else if (formando) tipo = MatriculaEmProcessamento.FORMANDO;
				else if (recuperacao) tipo = MatriculaEmProcessamento.RECUPERACAO;
				else if (adiantado) tipo = MatriculaEmProcessamento.ADIANTADO;
				else if (eletivo) tipo = MatriculaEmProcessamento.ELETIVO;

				matricula.setIdMatrizCurricular(rs.getInt("id_matriz_curricular"));
				matricula.setIdDiscente(rs.getInt("id_discente"));
				matricula.setIdMatriculaComponente(rs.getInt("id_matricula_componente"));
				matricula.setTipo(tipo);
				matricula.setIndice(rs.getDouble("iea"));
				matricula.setMatricula(rs.getLong("matricula"));
				matricula.setNome(rs.getString("nome"));
				matricula.setCurso(rs.getString("curso"));
				
				resultado.add(matricula);
			}
			
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
		} catch(SQLException e) {
			e.printStackTrace();
			return new HashMap<Integer, List<MatriculaEmProcessamento>>();
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(con);
		}
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
				int status = getStatusDiscente(matricula.getIdDiscente());
				if (status == StatusDiscente.CADASTRADO)
					setaAlunoCadastradoParaAtivo.update(new Object[] { StatusDiscente.ATIVO, matricula.getIdDiscente() });

				setaUltimaAtualizacaoTotaisNull.update(new Object[] { matricula.getIdDiscente() });
			}
			inserirResultadoProcessamento.update(new Object[] { matricula.getTipo(), matricula.getOrdem(), matricula.getIdMatriculaComponente(), matricula.getIdMatrizReserva() == 0 ? null : matricula.getIdMatrizReserva(), matricula.getIndice(), new Date(), ano, periodo, matricula.getSituacao().getId() });
		}

		alterarStatusMatricula.flush();
		setaUltimaAtualizacaoTotaisNull.flush();
		inserirResultadoProcessamento.flush();

		String sqlUpdate = null;
		if (rematricula)
			sqlUpdate = "update ensino.turma set processada_rematricula=trueValue() where id_turma=?";
		else
			sqlUpdate = "update ensino.turma set processada=trueValue() where id_turma=?";
		
		Connection con = null;
		PreparedStatement st = null; 
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sqlUpdate);
			st.setInt(1, turma.getId());
			st.executeUpdate();
		} catch(SQLException e) {
			
		} finally {
			closeStatement(st);
			closeConnection(con);
		}
	}
	
	private int getStatusDiscente(int idDiscente) {
		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement("select status from discente where id_discente = ?");
			st.setInt(1, idDiscente);
			rs = st.executeQuery();
			
			if (rs.next()) {
				int status = rs.getInt(1);
				return status;
			}
			
			return StatusDiscente.ATIVO;
		} catch(SQLException e) {
			return StatusDiscente.ATIVO;
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(con);
		}
	}
	
	public String getSqlMatriculasTurma(Turma turma, boolean rematricula) {
		return "select distinct d.id_discente, mc.id_matricula_componente, (select valor from ensino.indice_academico_discente where id_indice_academico = 6 and id_discente = d.id_discente) as iea, "
			+ "d.id_matriz_curricular, d.matricula, d.curso, d.nome, "
			+ "(coalesce((d.periodo_atual = cc.semestre_oferta), falseValue()) or coalesce((d.id_categoria_discente_especial = "+CategoriaDiscenteEspecial.EM_MOBILIDADE+"),falseValue()) ) as nivelado, "
			+ "(coalesce(d.possivel_formando, falseValue()) or coalesce((d.id_categoria_discente_especial = "+CategoriaDiscenteEspecial.EM_COMPLEMENTACAO_ESTUDOS+"),falseValue()) ) as formando, "
			+ "coalesce((d.periodo_atual > cc.semestre_oferta), falseValue()) as recuperacao, "
			+ "coalesce((d.periodo_atual < cc.semestre_oferta), falseValue()) as adiantado, "
			+ "cc.semestre_oferta is null as eletivo, "
			+ "(d.id_forma_ingresso in " + gerarStringIn( FormaIngresso.getFormaIngressoAlunoIngressante() ) + " and cc.semestre_oferta = 1 and d.ano_ingresso = mc.ano and d.periodo_ingresso = mc.periodo) as vestibular "
			+ "from ensino.matricula_componente mc, "
			+ "(select d.id_discente, d.id_curriculo, d.id_forma_ingresso, d.matricula, c.nome as curso, p.nome, (select valor from ensino.indice_academico_discente where id_indice_academico = 6 and id_discente = d.id_discente) as iea, " 
			+ "dg.possivel_formando, dg.id_matriz_curricular, fi.id_categoria_discente_especial, cat_esp.processa_matricula, cat_esp.processa_rematricula, d.ano_ingresso, d.periodo_ingresso, " 
			+ "greatest(("+turma.getAno()+" - d.ano_ingresso) * 2 "
			+ "+ ("+turma.getPeriodo()+" - d.periodo_ingresso + 1) "
			+ "- (select count(*) from ensino.movimentacao_aluno "
			+ "where ativo = trueValue() and id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.TRANCAMENTO + " and id_discente = d.id_discente), 0) + dg.perfil_inicial as periodo_atual "
			+ "from discente d left outer join curso c on (c.id_curso = d.id_curso) inner join ensino.forma_ingresso fi on (fi.id_forma_ingresso = d.id_forma_ingresso) "
			+ "left join ensino.categoria_discente_especial cat_esp on (cat_esp.id_categoria_discente_especial = fi.id_categoria_discente_especial), "
			+ "graduacao.discente_graduacao dg, ensino.matricula_componente mc, ensino.turma t, comum.pessoa p "
			+ "where d.id_discente = dg.id_discente_graduacao and mc.id_discente = d.id_discente and mc.id_turma = t.id_turma and d.id_pessoa = p.id_pessoa "
			+ "and t.id_turma = "+turma.getId()+" and d.status in (1, 2, 8)) as d left join "
			+ "graduacao.curriculo c on (d.id_curriculo = c.id_curriculo) left join "
			+ "((select * from ensino.turma t, graduacao.curriculo_componente cc where t.id_disciplina = cc.id_componente_curricular and t.id_turma = "+turma.getId()+") union "
			+ "(select t.*, cuc.* from ensino.turma t, graduacao.curriculo_componente cuc, ensino.componente_curricular cc where t.id_disciplina = cc.id_disciplina and cc.id_bloco_subunidade = cuc.id_componente_curricular and t.id_turma = "+turma.getId()+")"
			+ ") as cc "
			+ "on (c.id_curriculo = cc.id_curriculo) "
			+ "where mc.id_turma = "+turma.getId()+" and mc.id_discente = d.id_discente and mc.id_situacao_matricula = 1 " 
			+ "and (d.id_categoria_discente_especial is null OR ( d.id_categoria_discente_especial is not null and (d.processa_matricula = "+!rematricula+" or d.processa_rematricula = "+rematricula+") ) ) ";
	}

	public int findTotalMatriculadosTurma(int idTurma) {
		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement("select count(*) from ensino.matricula_componente where id_turma = ? and id_situacao_matricula = 2");
			st.setInt(1, idTurma);
			rs = st.executeQuery();
			
			if (rs.next()) {
				int numero = rs.getInt(1);
				return numero;
			} else {
				return 0;
			}
		} catch(SQLException e) {
			return 0;
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(con);
		}
	}

	public List<Integer> findDiscentesMatriculadosEmBlocos(int ano, int periodo, boolean rematricula) {
		String sql = "select distinct mc.id_discente " 
			+ "from ensino.matricula_componente mc, discente d, ensino.componente_curricular cc " 
			+ "where mc.ano = ? and mc.periodo = ? and mc.id_discente = d.id_discente and d.nivel = 'G' " 	
			+ "and mc.id_componente_curricular = cc.id_disciplina and cc.id_bloco_subunidade is not null " 
			+ "and mc.id_situacao_matricula = 2 "
			+ "and mc.id_discente in ( "
			+ "select id_discente from ensino.matricula_componente mc, ensino.componente_curricular cc " 
			+ "where mc.id_componente_curricular = cc.id_disciplina " 
			+ "and cc.id_bloco_subunidade is not null and mc.ano = ? and mc.periodo = ? and mc.rematricula = ? "
			+ "group by id_discente, id_situacao_matricula)"; 
		
		return getJdbcTemplate().queryForList(sql, new Object[] { ano, periodo, ano, periodo, rematricula }, Integer.class);
	}

	public List<Integer> findDiscentesMatriculadosEmCoRequisitos(int ano, int periodo, boolean rematricula) {
		return getJdbcTemplate().queryForList("select distinct mc.id_discente " +
				"from ensino.matricula_componente mc, discente d, ensino.componente_curricular_detalhes ccd	" +
				"where mc.ano = ? and mc.periodo = ? and mc.rematricula = ? and mc.id_discente = d.id_discente and d.nivel = 'G' " +
				"and mc.id_componente_detalhes = ccd.id_componente_detalhes and ccd.co_requisito is not null " +
				"and mc.id_situacao_matricula = 2 and d.tipo = "+Discente.REGULAR, new Object[] { ano, periodo, rematricula }, Integer.class);
	}

	public void atualizaMotivoIndeferimento(Integer idMatricula, String motivo) {
		Connection con = null;
		PreparedStatement st = null; 
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement("update graduacao.resultado_processamento_matricula set motivo=?, id_situacao_matricula = 11 where id_matricula_componente=?");
			st.setString(1, motivo);
			st.setInt(2, idMatricula);
			st.executeUpdate();
		} catch(SQLException e) {

		} finally {
			closeStatement(st);
			closeConnection(con);
		}
	}

	public List<MatriculaComponente> findMatriculasDiscente(Integer discente, int ano, int periodo, boolean rematricula) {
		String sql = "select * from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
			+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes "
			+ "and mc.ano = ? and mc.periodo = ? and mc.rematricula = ?";
		
		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, discente);
			st.setInt(2, ano);
			st.setInt(3, periodo);
			st.setBoolean(4, rematricula);
			
			rs = st.executeQuery();

			List<MatriculaComponente> result = new ArrayList<MatriculaComponente>();
			while (rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("id_situacao_matricula")));
				mc.setComponente(new ComponenteCurricular(rs.getInt("id_disciplina")));
				mc.getComponente().setCoRequisito(rs.getString("co_requisito"));
				mc.setDataCadastro(rs.getDate("data_cadastro"));
				result.add(mc);
			}
			
			return result;
		} catch(SQLException e) {
			return new ArrayList<MatriculaComponente>();
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(con);
		}
	}

	public List<MatriculaComponente> findMatriculasEmBlocoDiscente(Integer discente, int ano, int periodo, boolean rematricula) {
		String sql = "select * from ensino.matricula_componente mc, ensino.componente_curricular cc "
			+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina "
			+ "and cc.id_bloco_subunidade is not null and mc.ano = ? and mc.periodo = ? and mc.rematricula = ?";

		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, discente);
			st.setInt(2, ano);
			st.setInt(3, periodo);
			st.setBoolean(4, rematricula);
			
			rs = st.executeQuery();

			List<MatriculaComponente> result = new ArrayList<MatriculaComponente>();
			while (rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("id_situacao_matricula")));
				mc.setComponente(new ComponenteCurricular(rs.getInt("id_disciplina")));
				mc.getComponente().setBlocoSubUnidade(new ComponenteCurricular(rs.getInt("id_bloco_subunidade")));
				result.add(mc);
			}
			
			return result;
		} catch(SQLException e) {
			return new ArrayList<MatriculaComponente>();
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(con);
		}
	}

	public List<MatriculaComponente> findMatriculasEmCoRequisitoDiscente(Integer discente, int ano, int periodo, boolean rematricula) {
		String sql = "select * from ensino.matricula_componente mc, discente d, ensino.componente_curricular_detalhes ccd "
			+ "where mc.ano = ? and mc.periodo = ? and mc.id_discente = d.id_discente and d.id_discente = ? and mc.rematricula = ? "
			+ "and mc.id_componente_detalhes = ccd.id_componente_detalhes and ccd.co_requisito is not null "
			+ "and mc.id_situacao_matricula = 2";
		
		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, ano);
			st.setInt(2, periodo);
			st.setInt(3, discente);
			st.setBoolean(4, rematricula);
			
			rs = st.executeQuery();

			List<MatriculaComponente> result = new ArrayList<MatriculaComponente>();
			while (rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("id_situacao_matricula")));
				mc.setComponente(new ComponenteCurricular(rs.getInt("id_componente_curricular")));
				mc.getComponente().setCoRequisito(rs.getString("co_requisito"));
				result.add(mc);
			}
			
			return result;
		} catch(SQLException e) {
			return new ArrayList<MatriculaComponente>();
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(con);
		}
	}

	public List<MatriculaEmProcessamento> findResultadoProcessamento(int id) {
		String sql = "select distinct rpm.ordem, d.matricula, p.nome, c.nome as curso, sm.descricao as situacao, rpm.tipo_resultado, rpm.id_matriz_curricular, (select valor from ensino.indice_academico_discente where id_indice_academico = 6 and id_discente = d.id_discente) as iea, rpm.motivo "
			+ "from ensino.turma t, ensino.matricula_componente mc, graduacao.resultado_processamento_matricula rpm, " 
			+ "discente d, comum.pessoa p, ensino.situacao_matricula sm, curso c, graduacao.discente_graduacao dg "
			+ "where t.id_turma = mc.id_turma and mc.id_matricula_componente = rpm.id_matricula_componente "
			+ "and t.id_turma = ? and mc.id_discente = d.id_discente and d.id_pessoa = p.id_pessoa "
			+ "and mc.id_situacao_matricula = sm.id_situacao_matricula and d.id_curso = c.id_curso and rpm.data_processamento > '2008-08-01' "
			+ "and d.id_discente = dg.id_discente_graduacao order by sm.descricao desc, ordem asc";
		
		System.out.println( sql );
		
		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, id);
			rs = st.executeQuery();

			List<MatriculaEmProcessamento> result = new ArrayList<MatriculaEmProcessamento>();
			while (rs.next()) {
				MatriculaEmProcessamento m = new MatriculaEmProcessamento();
				m.setCurso(rs.getString("curso"));
				m.setNome(rs.getString("nome"));
				m.setIdMatrizReserva(rs.getInt("id_matriz_curricular"));
				m.setIndice(rs.getDouble("iea"));
				m.setMatricula(rs.getLong("matricula"));
				m.setTipo(rs.getInt("tipo_resultado"));
				m.setSituacao(new SituacaoMatricula(rs.getString("situacao")));
				m.setMotivo(rs.getString("motivo"));
				result.add(m);
			}
			
			return result;
		} catch(SQLException e) {
			return new ArrayList<MatriculaEmProcessamento>();
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(con);
		}
	}
	
	public List<Turma> findTurmasEnsinoIndividualizadoProcessar(int ano, int periodo, boolean rematricula ,boolean turmasRegular) {
		String projecao = "select t.id_turma, t.ano, t.periodo, t.capacidade_aluno, t.codigo as codigo_turma, "
			+ "cast(sum( case when mc.id_situacao_matricula = 2 then 1 else 0 end) as double precision) as total_matriculado, " 
			+ "count(mc.id_matricula_componente), t.tipo, "
			+ "t.descricao_horario, cc.id_disciplina, cc.id_detalhe, ccd.nome, ccd.codigo ";
			
		StringBuilder sql = new StringBuilder();
		
		sql.append(" from ensino.turma t  " 
				+ " join ensino.situacao_turma st on t.id_situacao_turma = st.id_situacao_turma " 
				+ " join ensino.componente_curricular cc on t.id_disciplina = cc.id_disciplina "
				+ " join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes "
				+ "	join ensino.matricula_componente mc on mc.id_turma = t.id_turma ");
		sql.append(" where t.ano = ?" +
				   " and t.periodo = ?" +
				   " and t.id_situacao_turma IN (?,?)" + 
				   " and cc.nivel = ?" +
				   " and t.tipo = ? " +
				   " and exists(" +
				   "	select id_matricula_componente from ensino.matricula_componente " +
				   "	where id_matricula_componente = mc.id_matricula_componente and id_situacao_matricula = "+SituacaoMatricula.MATRICULADO.getId()+")"); 
		sql.append(" group by t.id_turma, t.ano, t.periodo, t.capacidade_aluno, t.codigo," +
				   "          t.tipo, t.descricao_horario, cc.id_disciplina, cc.id_detalhe," +
				   "		  ccd.nome, ccd.codigo ");
		sql.append(turmasRegular ? " having count(mc.id_matricula_componente) <= ? " : " having count(mc.id_matricula_componente) > ? " );
		sql.append(" order by t.id_turma");
		
		int QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL);
		int tipoTurma = turmasRegular ? Turma.REGULAR : Turma.ENSINO_INDIVIDUAL;
		
		return getJdbcTemplate().query(projecao + sql.toString(), 
			new Object[] { ano, periodo, SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE,
						   String.valueOf(NivelEnsino.GRADUACAO),tipoTurma,QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL }, 
			new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					Turma t = new Turma();
					t.setId(rs.getInt("id_turma"));
					t.setAno(rs.getInt("ano"));
					t.setPeriodo(rs.getInt("periodo"));
					t.setCapacidadeAluno(rs.getInt("capacidade_aluno"));
					t.setQtdMatriculados(rs.getInt("total_matriculado"));
					//t.setQtdAprovados(rs.getInt("total_aprovado"));
					//t.setQtdReprovados(rs.getInt("total_reprovado"));
					//t.setQtdReprovadosFalta(rs.getInt("total_reprovado_falta"));
					t.setCodigo(rs.getString("codigo_turma"));
					t.setDescricaoHorario(rs.getString("descricao_horario"));
					t.setTipo(rs.getInt("tipo"));
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

	public List<Discente> buscarAlunosCadastradosParaAtivar(int ano, int periodo) {
		return getJdbcTemplate().query("select * from discente where status = 2 and ano_ingresso = ? and periodo_ingresso = ? "
				+ "and id_discente in (select id_discente from ensino.matricula_componente where ano = ? and periodo = ? and id_situacao_matricula = 2)",
				new Object[] { ano, periodo, ano, periodo }, new RowMapper() {
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
						Discente d = new Discente();
						d.setId(rs.getInt("id_discente"));
						d.setStatus(rs.getInt("status"));
						return d;
					}
				});
	}
	

	public List<Integer> buscarAlunosComMaisDeUmVinculo() {
		return getJdbcTemplate().queryForList("select p.id_pessoa "
				+ "from comum.pessoa p, discente d left join (select * from curso where id_modalidade_educacao = 1 and nivel = 'G' and ativo = true) as c using (id_curso) "  
				+ "where p.id_pessoa = d.id_pessoa and d.status in (1,2) and d.nivel = 'G' "
				+ "group by p.id_pessoa, p.nome having count(id_discente) > 1 order by p.nome asc", Integer.class);
	}

	public Discente buscarVinculoAtivoMaisAntigo(Integer pessoa) throws DAOException {
		return (Discente) getSession().createQuery("select d from Discente d where d.pessoa.id = ? and d.status in (1,2) and d.nivel = 'G' order by d.anoIngresso asc, d.periodoIngresso asc, d.dataCadastro asc").setInteger(0, pessoa).setMaxResults(1).uniqueResult();
	}
	
	public boolean vinculoMaisNovoPossuiMatriculas(Integer pessoa) throws DAOException {
		return getJdbcTemplate().queryForInt("select count(id_matricula_componente) from ensino.matricula_componente where id_discente = ("
				+ "select d.id_discente from discente d where d.id_pessoa = "+pessoa+" and d.status in (1,2) and d.nivel = 'G' order by d.ano_ingresso desc, d.periodo_ingresso desc, d.data_cadastro desc limit 1) "
				+ "and id_situacao_matricula not in (10, 21, 22, 23)") > 0;
	}

	public Discente buscarVinculoAtivoMaisNovo(Integer pessoa) throws DAOException {
		return (Discente) getSession().createQuery("select d from Discente d where d.pessoa.id = ? and d.status in (1,2) and d.nivel = 'G' order by d.anoIngresso desc, d.periodoIngresso desc, d.dataCadastro desc").setInteger(0, pessoa).setMaxResults(1).uniqueResult();
	}
	
}
