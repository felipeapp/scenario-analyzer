/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '07/01/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
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
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.processamento.dominio.MatriculaEmProcessamento;

/**
 * Consultas para o processamento de matrícula.
 *
 * @author David Pereira
 *
 */
public class ProcessamentoMatriculaDAO extends GenericSigaaDAO {

	/**
	 * Retorna um mapa contendo o número de vagas reservadas para uma turma
	 * por matriz curricular
	 */
	public Map<MatrizCurricular, ReservaCurso> findInformacoesVagasTurma(int idTurma, final boolean rematricula, boolean resultado) {

		String sql = "select rc.id_reserva_curso, rc.id_matriz_curricular, rc.vagas_atendidas, rc.vagas_atendidas_ingressantes, " 
			+ (resultado ? 
			"(select count(*) from ensino.matricula_componente mc, graduacao.discente_graduacao dg where mc.id_turma = rc.id_turma "
			+ "and dg.id_discente_graduacao = mc.id_discente and dg.id_matriz_curricular = rc.id_matriz_curricular and mc.id_situacao_matricula = 2) as vagas_ocupadas, "
			: "(select count(*) from ensino.matricula_componente mc, graduacao.discente_graduacao dg where mc.id_turma = rc.id_turma "
				+ "and dg.id_discente_graduacao = mc.id_discente and dg.id_matriz_curricular = rc.id_matriz_curricular and mc.id_situacao_matricula = 2 and mc.rematricula = falseValue()) as vagas_ocupadas, ")
			+ "c.id_curso, c.nome_ascii as nome, h.nome as habilitacao, t.sigla as turno, g.descricao as grau "
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
			st.setInt(1, idTurma);
			rs = st.executeQuery();
			
			List<ReservaCurso> lista = new ArrayList<ReservaCurso>();
			while (rs.next()) {
				ReservaCurso reserva = new ReservaCurso();
				reserva.setId(rs.getInt("id_reserva_curso"));

				short vagasAtendidas = rs.getShort("vagas_atendidas");
				short vagasAtendidasIngressantes = rs.getShort("vagas_atendidas_ingressantes");
				short vagasOcupadas = (rematricula ? rs.getShort("vagas_ocupadas") : 0);
				short totalVagas = (short) (vagasAtendidas - vagasOcupadas);

				reserva.setVagasSolicitadas(totalVagas < 0 ? 0 : totalVagas);
				reserva.setVagasReservadas(totalVagas < 0 ? 0 : totalVagas);
				reserva.setVagasReservadasIngressantes(vagasAtendidasIngressantes);
				reserva.setMatrizCurricular(new MatrizCurricular());
				reserva.getMatrizCurricular().setCurso(new Curso());
				reserva.getMatrizCurricular().setId(rs.getInt("id_matriz_curricular"));
				reserva.getMatrizCurricular().getCurso().setId(rs.getInt("id_curso"));
				reserva.getMatrizCurricular().getCurso().setNome(rs.getString("nome"));
				reserva.getMatrizCurricular().setHabilitacao(new Habilitacao());
				reserva.getMatrizCurricular().getHabilitacao().setNome(br.ufrn.arq.util.StringUtils.toAscii(rs.getString("habilitacao")));
				reserva.getMatrizCurricular().setTurno(new Turno());
				reserva.getMatrizCurricular().getTurno().setSigla(rs.getString("turno"));
				reserva.getMatrizCurricular().setGrauAcademico(new GrauAcademico());
				reserva.getMatrizCurricular().getGrauAcademico().setDescricao(br.ufrn.arq.util.StringUtils.toAscii(rs.getString("grau")));
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


	/**
	 * Identifica se a senha de processamento passada é igual a cadastrada no banco
	 */
	public boolean autenticacaoProcessamento(String senhaProcessamento) throws DAOException {
		String senha = ParametroHelper.getInstance().getParametro(ConstantesParametro.SENHA_PROCESSAMENTO_MATRICULA);
		return senha.equals(UFRNUtils.toMD5(senhaProcessamento));
	}

	/**
	 * 
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @param rematricula
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findAlunosAPreProcessar(int ano, int periodo, char nivel, boolean rematricula) {
		return getJdbcTemplate().queryForList("select distinct d.id_discente from ensino.matricula_componente mc left join discente d using (id_discente) " 
				+ "where ano = ? and periodo = ? and d.nivel = 'G' and mc.id_situacao_matricula = 1 and d.status in (1,2,8) " 
				+ "and mc.rematricula = ?",
				new Object[] { ano, periodo, rematricula }, Integer.class);
	}

	/**
	 * 
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @param rematricula
	 * @param ead 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SolicitacaoMatricula> buscarSolicitacoesMatricula(int ano, int periodo, char nivel, boolean rematricula, boolean ead) {
		return getJdbcTemplate().query("select sm.id_solicitacao_matricula, sm.ano, sm.periodo, sm.id_turma, cc.id_disciplina, cc.id_detalhe, sm.id_discente, sm.status " 
				+ "from graduacao.solicitacao_matricula sm, ensino.turma t, ensino.componente_curricular cc, discente d where " 
				+ "sm.id_turma = t.id_turma and t.id_disciplina = cc.id_disciplina and sm.anulado = falseValue() and d.id_discente = sm.id_discente and "
				+ "sm.ano = ? and sm.periodo = ? and sm.status != 9 and sm.id_matricula_gerada is null and d.nivel = ? and sm.rematricula = ? and t.id_polo is "
				+ (ead ? "not null" : "null"), 
				new Object[] { ano, periodo, String.valueOf(nivel), rematricula }, new RowMapper() {

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

	public List<MatriculaEmProcessamento> findIndeferimentos(int ano, int periodo, Curso curso) {
		return findIndeferimentos(ano, periodo, null, null, curso);
	}	
	
	public List<MatriculaEmProcessamento> findIndeferimentos(int ano, int periodo, Unidade unidade) {
		return findIndeferimentos(ano, periodo, unidade, null, null);
	}

	public List<MatriculaEmProcessamento> findIndeferimentos(int ano, int periodo, Unidade unidade, Servidor orientador, Curso curso) {

		StringBuilder sql = new StringBuilder("select d.matricula, d.tipo, p.nome, cc.codigo, c.nome as curso "
			+ "from discente d left join curso c on (d.id_curso = c.id_curso), comum.pessoa p, ensino.matricula_componente mc, ensino.componente_curricular cc "
			+ "where d.id_discente = mc.id_discente and d.id_pessoa = p.id_pessoa "
			+ "and mc.id_situacao_matricula = " + SituacaoMatricula.INDEFERIDA.getId()
			+ " and ano = ? and periodo = ? "
			+ "and d.nivel = 'G' and mc.id_componente_curricular = cc.id_disciplina ");

		if (unidade != null) {
			if (unidade.getId() == -1) {
				sql.append("and d.tipo = 2 ");
			} else if (unidade.getId() > 0) {
				sql.append("and d.tipo = 1 and c.id_unidade = " + unidade.getId() + " ");
			}
		}
		if (orientador != null) {
			sql.append("and d.id_discente in (select id_discente from graduacao.orientacao_academica where id_servidor = " + orientador.getId() + " and fim <= now()) ");
		}

		if (curso != null) {
			sql.append(" and d.tipo = 1 and c.id_curso = " + curso.getId());
		}
		
		sql.append("order by p.nome asc");

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql.toString(), new Object[] { ano, periodo });
		List<MatriculaEmProcessamento> matriculas = new ArrayList<MatriculaEmProcessamento>();

		for (Map<String, Object> item : result) {
			MatriculaEmProcessamento m = new MatriculaEmProcessamento();
			m.setMatricula((Long) item.get("matricula"));
			boolean encontrou = false;

			for (MatriculaEmProcessamento mats : matriculas) {
				if (mats.getMatricula() == m.getMatricula()) {
					m = mats;
					encontrou = true;
					break;
				}
			}

			if (!encontrou) {
				m.setNome((String) item.get("nome"));
				int tipo = (Integer) item.get("tipo");
				if (tipo == Discente.REGULAR)
					m.setCurso((String) item.get("curso"));
				else
					m.setCurso("ALUNO ESPECIAL");
				m.setIndeferimentos(new ArrayList<String>());
				matriculas.add(m);
			}

			m.getIndeferimentos().add((String) item.get("codigo"));
		}

		return matriculas;
	}

	/**
	 * 
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @param rematricula
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findDiscentesMatriculadosEmBlocos(int ano, int periodo, char nivel, boolean rematricula) {
		String sql = "select distinct id_discente from ensino.matricula_componente where id_turma in ( "
			+ "select t.id_turma "
			+ "from ensino.turma t, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd " 
			+ "where t.id_disciplina = cc.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes and t.id_turma in " 
			+ "(select distinct sm.id_turma "
			+ "from graduacao.solicitacao_matricula sm, ensino.turma t, ensino.componente_curricular cc, discente d where " 
			+ "sm.id_turma = t.id_turma and t.id_disciplina = cc.id_disciplina and sm.anulado = falseValue() and d.id_discente = sm.id_discente and "
			+ "sm.ano = ? and sm.periodo = ? and sm.status != 9 and sm.id_matricula_gerada is not null and d.nivel = ? and sm.rematricula = ?) "
			+ "and t.processada = trueValue() and cc.id_bloco_subunidade is not null)";
		
		return getJdbcTemplate().queryForList(sql, new Object[] { ano, periodo, String.valueOf(nivel), rematricula }, Integer.class);
	}

	/**
	 * 
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MatriculaComponente> findMatriculasEmBlocoDiscente(int idDiscente, int ano, int periodo) {
		String sql = "select * from ensino.matricula_componente mc, ensino.componente_curricular cc "
			+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina "
			+ "and cc.id_bloco_subunidade is not null and mc.ano = ? and mc.periodo = ?";
		
		return getJdbcTemplate().query(sql, new Object[] { idDiscente, ano, periodo }, new ParameterizedRowMapper<MatriculaComponente>() {
			public MatriculaComponente mapRow(ResultSet rs, int rowNum) throws SQLException {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("id_situacao_matricula")));
				mc.setComponente(new ComponenteCurricular(rs.getInt("id_disciplina")));
				mc.getComponente().setBlocoSubUnidade(new ComponenteCurricular(rs.getInt("id_bloco_subunidade")));
				return mc;
			}
		});
	}	
	
	public List<MatriculaEmProcessamento> findResultadoProcessamento(int turma, boolean rematricula) {
		String sql = "select distinct rpm.ordem, " +
				" coalesce(rpm.tipo_resultado, case when ano_ingresso = mc.ano and periodo_ingresso = mc.periodo then 7 end) as tipo_resultado," +
				" coalesce(rpm.id_matriz_curricular, dg.id_matriz_curricular) as id_matriz_curricular, " +
				" d.matricula, p.nome_ascii as nome, " +
				" coalesce(c.nome_ascii, 'ESPECIAL (' || fi.descricao || ')') as curso, " +
				" sm.descricao as situacao, " +
				" (select valor from ensino.indice_academico_discente where id_indice_academico = 6 and id_discente = d.id_discente) as iea, rpm.motivo " +
			" from ensino.turma t  " +
			" join ensino.matricula_componente mc on (t.id_turma = mc.id_turma ) " +
			" join discente d on (mc.id_discente = d.id_discente)" +
			" join comum.pessoa p on (d.id_pessoa = p.id_pessoa) " +
			" join ensino.situacao_matricula sm on (mc.id_situacao_matricula = sm.id_situacao_matricula) " +
			" join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao) " +
			" left join graduacao.resultado_processamento_matricula rpm on (mc.id_matricula_componente = rpm.id_matricula_componente) " +
			" left join curso c on (c.id_curso = d.id_curso) " +
			" left join ensino.forma_ingresso fi on (fi.id_forma_ingresso = d.id_forma_ingresso)" +
			" where t.id_turma = ? " +
			" and (mc.rematricula is null or mc.rematricula = ?) " +
			" order by sm.descricao desc, ordem asc, p.nome_ascii asc ";
		
		System.out.println( sql );

		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, turma);
			st.setBoolean(2, rematricula);
			rs = st.executeQuery();
			
			List<MatriculaEmProcessamento> result = new ArrayList<MatriculaEmProcessamento>();
			while (rs.next()) {
				MatriculaEmProcessamento m = new MatriculaEmProcessamento();
				m.setCurso(rs.getString("curso"));
				m.setNome(rs.getString("nome"));
				m.setIdMatrizReserva(rs.getInt("id_matriz_curricular"));
				m.setIndice(rs.getDouble("iea"));
				m.setOrdem(rs.getInt("ordem"));
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

	/**
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @param rematricula
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findDiscentesMatriculadosEmCoRequisitos(int ano, int periodo, char nivel, boolean rematricula) {
		String sql = "select distinct id_discente from ensino.matricula_componente where id_turma in (57491606) "; 
				//+ "select t.id_turma "
				//+ "from ensino.turma t, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd " 
				//+ "where t.id_disciplina = cc.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes and t.id_turma = ) " 
//				+ "(select distinct sm.id_turma  "
//				+ "from graduacao.solicitacao_matricula sm, ensino.turma t, ensino.componente_curricular cc, discente d where " 
//				+ "sm.id_turma = t.id_turma and t.id_disciplina = cc.id_disciplina and sm.anulado = falseValue() and d.id_discente = sm.id_discente and " 
//				+ "sm.ano = ? and sm.periodo = ? and sm.status != 9 and sm.id_matricula_gerada is not null and d.nivel = ? and sm.rematricula = ?) " 
				//+ "and t.processada = trueValue() and ccd.co_requisito is not null) ";
		return getJdbcTemplate().queryForList(sql, Integer.class);
	}

	/**
	 * @param discente
	 * @param i
	 * @param j
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MatriculaComponente> findMatriculasEmCoRequisitoDiscente(Integer discente, int ano, int periodo) {
		String sql = "select * from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
			+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes "
			+ "and ccd.co_requisito is not null and mc.ano = ? and mc.periodo = ? and mc.data_cadastro > '2008-08-01'";
		
		return getJdbcTemplate().query(sql, new Object[] { discente, ano, periodo }, new ParameterizedRowMapper<MatriculaComponente>() {
			public MatriculaComponente mapRow(ResultSet rs, int rowNum) throws SQLException {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("id_situacao_matricula")));
				mc.setComponente(new ComponenteCurricular(rs.getInt("id_disciplina")));
				mc.getComponente().setCoRequisito(rs.getString("co_requisito"));
				return mc;
			}
		});
	}

	/**
	 * @param discente
	 * @param i
	 * @param j
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MatriculaComponente> findMatriculasDiscente(Integer discente, int ano, int periodo) {
		String sql = "select * from ensino.matricula_componente mc, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd "
			+ "where mc.id_discente = ? and mc.id_componente_curricular = cc.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes "
			+ "and mc.ano = ? and mc.periodo = ?";
		
		return getJdbcTemplate().query(sql, new Object[] { discente, ano, periodo }, new ParameterizedRowMapper<MatriculaComponente>() {
			public MatriculaComponente mapRow(ResultSet rs, int rowNum) throws SQLException {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("id_situacao_matricula")));
				mc.setComponente(new ComponenteCurricular(rs.getInt("id_disciplina")));
				mc.getComponente().setCoRequisito(rs.getString("co_requisito"));
				mc.setDataCadastro(rs.getDate("data_cadastro"));
				return mc;
			}
		});
	}

	/**
	 * Verifica se uma matrícula de um discente em uma determinada turma já existe com o mesmo status
	 * que se deseja matricular
	 * 
	 * @param matricula
	 * @return
	 */
	public boolean existeMatricula(MatriculaComponente matricula) {
		//return false;
		String sql = "select count(*) from ensino.matricula_componente mc " +
				" where id_discente = ? and id_turma = ?  and id_situacao_matricula = ?";

		//int numeroMatriculas =	getJdbcTemplate().queryForInt(sql, 
		//		new Object[] {matricula.getDiscente().getId(), matricula.getTurma().getId(), matricula.getSituacaoMatricula().getId()});
			
		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, matricula.getDiscente().getId());
			st.setInt(2, matricula.getTurma().getId());
			st.setInt(3, matricula.getSituacaoMatricula().getId());
			rs = st.executeQuery();
			
			if (rs.next()) {
				int numero = rs.getInt(1);
				return numero > 0;
			} else {
				return false;
			}
		} catch(SQLException e) {
			return false;
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			closeConnection(con);
		}
	}
	
	/**
	 * @param key
	 * @param string
	 */
	public void atualizaMotivoIndeferimento(Integer idMatricula, String motivo) {
		update("update graduacao.resultado_processamento_matricula set motivo=? where id_matricula_componente=?", new Object[] { motivo, idMatricula });
	}

	/**
	 * @param id
	 * @return
	 */
	public List<MatriculaEmProcessamento> findDesistenciasTurma(int id) {
		Connection con = null;
		PreparedStatement st = null; 
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement("select p.nome, d.matricula, s.descricao, c.nome as curso from ensino.matricula_componente mc left join ensino.situacao_matricula s using (id_situacao_matricula) left join discente d using (id_discente) left join comum.pessoa p using (id_pessoa) left join curso c using (id_curso) where mc.id_turma = ? and mc.id_situacao_matricula = 12");
			st.setInt(1, id);
			rs = st.executeQuery();
			
			List<MatriculaEmProcessamento> result = new ArrayList<MatriculaEmProcessamento>();
			while (rs.next()) {
				MatriculaEmProcessamento mat = new MatriculaEmProcessamento();
				mat.setNome(rs.getString("nome"));
				mat.setCurso(rs.getString("curso"));
				mat.setMatricula(rs.getLong("matricula"));
				mat.setSituacao(new SituacaoMatricula(rs.getString("descricao")));
				result.add(mat);
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
	
}
