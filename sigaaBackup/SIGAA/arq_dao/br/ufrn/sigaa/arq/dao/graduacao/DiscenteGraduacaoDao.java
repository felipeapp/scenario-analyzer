/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/04/2009
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MudancaCurricular;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao para consulta sobre a entidade DiscenteGraduacao
 * @see DiscenteGraduacao
 * @author Victor Hugo
 */
public class DiscenteGraduacaoDao extends GenericSigaaDAO {

	/**
	 * Retorna os discente (Ativos e Inativos) com ano-período de entrada nos cursos especificados
	 * 
	 * @param anoIngresso
	 * @param periodoIngresso
	 * @param cursos
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findByCursoAnoPeriodo(int anoIngresso, int periodoIngresso, Collection<Curso> cursos) throws DAOException {
		return findByCursoAnoPeriodo(anoIngresso, periodoIngresso, cursos, false);
	}

	/**
	 * Retorna os discentes de graduação de um ou mais curso(s) a partir de um
	 * ano-período de ingresso
	 * 
	 * @param anoIngresso
	 * @param periodoIngresso
	 * @param cursos
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteGraduacao> findByCursoAnoPeriodo(int anoIngresso, int periodoIngresso, Collection<Curso> cursos, boolean somenteAtivos) throws DAOException {
		Criteria c = getSession().createCriteria(DiscenteGraduacao.class);
		Criteria sc = c.createCriteria("discente");
		
		sc.add(Restrictions.in("curso", cursos));
		sc.add(Restrictions.eq("anoIngresso", anoIngresso));
		sc.add(Restrictions.eq("periodoIngresso", periodoIngresso));
		if (somenteAtivos) {
			sc.add(Restrictions.ne("status", StatusDiscente.CANCELADO));
			sc.add(Restrictions.ne("status", StatusDiscente.CONCLUIDO));
			sc.add(Restrictions.ne("status", StatusDiscente.JUBILADO));
			sc.add(Restrictions.ne("status", StatusDiscente.EXCLUIDO));
		}
		Criteria cPessoa = sc.createCriteria("pessoa");
		cPessoa.addOrder(Order.asc("nome"));
		return c.list();
	}

	/**
	 * Busca os discentes de graduação ativos da pessoa passada por parâmetro
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteGraduacao> findAtivoByPessoa(int idPessoa) throws DAOException {
		Criteria c = getSession().createCriteria(DiscenteGraduacao.class);
		Criteria sc = c.createCriteria("discente");
		
		sc.add(Restrictions.eq("pessoa.id", idPessoa));

		sc.add(Restrictions.ne("status", StatusDiscente.CANCELADO));
		sc.add(Restrictions.ne("status", StatusDiscente.CONCLUIDO));
		sc.add(Restrictions.ne("status", StatusDiscente.JUBILADO));
		sc.add(Restrictions.ne("status", StatusDiscente.CADASTRADO));
		sc.add(Restrictions.ne("status", StatusDiscente.AFASTADO));
		sc.add(Restrictions.ne("status", StatusDiscente.TRANCADO));
		sc.add(Restrictions.ne("status", StatusDiscente.EXCLUIDO));

		return c.list();
	}

	/**
	 * Retorna todas mudanças de matriz ou currículo de um aluno.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MudancaCurricular> findMudancasMatriz(DiscenteGraduacao discente) throws DAOException {
		Criteria c = getSession().createCriteria(MudancaCurricular.class);
		c.add(Restrictions.eq("discente.id", discente.getId()));
		c.add(Restrictions.eq("ativo", true));
		c.addOrder(Order.asc("data"));
		return c.list();
	}


	/** Gera um SQL comum para consultas neste DAO
	 *  
	 * @param discente
	 * @param verificarAtividades
	 * @param situacoes
	 * @param tiposIntegralizacao
	 * @return
	 */
	private String gerarSQL(DiscenteAdapter discente, Boolean verificarAtividades, Collection<SituacaoMatricula> situacoes, String... tiposIntegralizacao) {
		StringBuilder sql = new StringBuilder();
		sql.append(" FROM ensino.matricula_componente mc " +
						"	inner join ensino.componente_curricular cc on (cc.id_disciplina = mc.id_componente_curricular)" +
						"	inner join ensino.componente_curricular_detalhes cd on (cc.id_detalhe = cd.id_componente_detalhes) " +
						" WHERE cc.id_bloco_subunidade is null " +
						" 	and mc.id_discente =  " + discente.getId());
		
		if (tiposIntegralizacao != null && tiposIntegralizacao.length > 0)
			sql.append(" and mc.tipo_integralizacao in " + gerarStringIn(tiposIntegralizacao));
		
		if (verificarAtividades != null) {
			if (verificarAtividades)
				sql.append(" and (cc.id_tipo_componente = " + TipoComponenteCurricular.ATIVIDADE + ") ");
			else
				sql.append(" and cc.id_tipo_componente <> " + TipoComponenteCurricular.ATIVIDADE + " ");
		}
		
		if (situacoes != null)
			sql.append(" and mc.id_situacao_matricula in " + gerarStringIn(situacoes));
		
		return sql.toString();
	}

	/**
	 * Calcula integralização de disciplinas extras curriculares
	 * 
	 * @param discente
	 * @param situacoes
	 * @throws DAOException
	 */
	public void calcularIntegralizacaoExtras(DiscenteGraduacao discente,
			Collection<SituacaoMatricula> situacoes) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			/** total de créditos e CH das atividades */
			String projecao = "SELECT (sum(cd.cr_aula) + sum(cd.cr_laboratorio)  + sum(cd.cr_estagio) + sum(cd.cr_ead)  ) as cr_total, "
					+ "sum(cd.ch_total) as ch_total";
			st = con.prepareStatement(projecao
					+ gerarSQL(discente, null, situacoes,
							TipoIntegralizacao.EXTRA_CURRICULAR));
			rs = st.executeQuery();
			if (rs.next()) {
				discente.setCrExtraIntegralizados((short) rs
						.getLong("cr_total"));
				discente.incChOptativaIntegralizada((int) (rs
						.getLong("ch_total")));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Calcula integralização de disciplinas obrigatórias do currículo
	 * 
	 * @param discente
	 * @param situacoes
	 * @throws DAOException
	 */
	public void calcularIntegralizacaoCurriculo(DiscenteGraduacao discente,
			Collection<SituacaoMatricula> situacoes) throws DAOException {
		String tipoIntegralizacao, projecao;
		Boolean verificarAtividades;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			/* total de CH das atividades */
			projecao = "SELECT sum(cd.ch_total) as ch_total ";
			verificarAtividades = true;
			tipoIntegralizacao = TipoIntegralizacao.OBRIGATORIA;
			st = con.prepareStatement(projecao
					+ gerarSQL(discente, verificarAtividades, situacoes,
							tipoIntegralizacao));
			rs = st.executeQuery();
			if (rs.next())
				discente.setChAtividadeObrigInteg((short) rs
						.getLong("ch_total"));

			/** total de créditos e CH das não-atividades */
			projecao = "SELECT sum(cd.ch_total) as ch_total, "
				+ "(sum(cd.cr_aula) + sum(cd.cr_laboratorio)  + sum(cd.cr_estagio) + sum(cr_ead)) as cr_total ";
			verificarAtividades = false;
			tipoIntegralizacao = TipoIntegralizacao.OBRIGATORIA;
			st = con.prepareStatement(projecao
					+ gerarSQL(discente, verificarAtividades, situacoes,
							tipoIntegralizacao));
			rs = st.executeQuery();
			if (rs.next()) {
				discente.setChNaoAtividadeObrigInteg((short) rs
						.getLong("ch_total"));
				discente.setCrNaoAtividadeObrigInteg((short) rs
						.getLong("cr_total"));
			}
			// não vai mais precisar distinguir atividade e não-atividade nas
			// demais consultas
			verificarAtividades = null;

			/* total de créditos e CH de aula, laboratório e estágio */
			projecao = "SELECT sum(cd.cr_aula) as CR_AULA, sum(cd.cr_laboratorio) as CR_LAB, sum(cd.cr_estagio) as CR_ESTAGIO , "
					+ "sum(cd.ch_aula) as CH_AULA, sum(cd.ch_laboratorio) as CH_LAB, sum(cd.ch_estagio) as CH_ESTAGIO ";
			st = con.prepareStatement(projecao
					+ gerarSQL(discente, verificarAtividades, situacoes));
			rs = st.executeQuery();
			if (rs.next()) {
				discente.setChAulaIntegralizada((short) rs.getLong("CH_AULA"));
				discente.setChLabIntegralizada((short) rs.getLong("CH_LAB"));
				discente.setChEstagioIntegralizada((short) rs
						.getLong("CH_ESTAGIO"));
				discente.setCrLabIntegralizado((short) rs.getLong("CR_LAB"));
				discente.setCrEstagioIntegralizado((short) rs
						.getLong("CR_ESTAGIO"));
				discente.setCrAulaIntegralizado((short) rs.getLong("CR_AULA"));
			}

			// calcular CH optativas SOMENTE DO CURRÍCULO (as equivalentes não
			// são consideradas)
			projecao = "SELECT sum(cd.ch_total) as ch_total ";
			st = con.prepareStatement(projecao
					+ gerarSQL(discente, verificarAtividades, situacoes,
							TipoIntegralizacao.OPTATIVA_DA_GRADE,
							TipoIntegralizacao.OPTATIVA_CURSO_CIDADE));
			rs = st.executeQuery();
			if (rs.next()) {
				discente.setChOptativaIntegralizada((short) rs
						.getLong("ch_total"));
			}

			int chAproveitada = getJdbcTemplate().queryForInt("select coalesce(sum(ccd.ch_total),0) from ensino.matricula_componente mc, ensino.componente_curricular_detalhes ccd where " 
					+ "mc.id_componente_detalhes = ccd.id_componente_detalhes and " 
					+ "mc.id_discente = ? and mc.tipo_integralizacao in ('OB', 'OP', 'OC', 'EC') and mc.id_situacao_matricula in (22, 23);", new Object[] { discente.getId() });
			discente.setChIntegralizadaAproveitamentos((short) chAproveitada);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Atualiza a integralização do discente
	 * 
	 * @param discente
	 * @throws DAOException
	 */
	public void atualizaTotaisIntegralizados(DiscenteGraduacao discente) throws DAOException {
		String sql = "UPDATE graduacao.discente_graduacao SET ch_total_integralizada = ?, ch_total_pendente = ?, "
			+ "ch_optativa_integralizada = ?, ch_optativa_pendente = ?, ch_nao_atividade_obrig_integ = ?, "
			+ "ch_nao_atividade_obrig_pendente = ?, ch_atividade_obrig_integ = ?, ch_atividade_obrig_pendente = ?, "
			+ "ch_aula_integralizada = ?, ch_aula_pendente = ?, ch_lab_integralizada = ?, ch_lab_pendente = ?, "
			+ "ch_estagio_integralizada = ?, ch_estagio_pendente = ?, cr_total_integralizados = ?, cr_total_pendentes = ?, "
			+ "cr_extra_integralizados = ?, cr_nao_atividade_obrig_integralizado = ?, cr_nao_atividade_obrig_pendente = ?, "
			+ "cr_lab_integralizado = ?, cr_lab_pendente = ?, cr_estagio_integralizado = ?, cr_estagio_pendente = ?, "
			+ "cr_aula_integralizado = ?, cr_aula_pendente = ? , ch_integralizada_aproveitamentos = ?," 
			+ "total_atividades_pendentes = ?,total_grupos_optativas_pendentes=?,ultima_atualizacao_totais = now() "
			+ "WHERE id_discente_graduacao = ?";
			
		update(sql, new Object[] { 
				discente.getChTotalIntegralizada(), discente.getChTotalPendente(),
				discente.getChOptativaIntegralizada(), discente.getChOptativaPendente(),
				discente.getChNaoAtividadeObrigInteg(), discente.getChNaoAtividadeObrigPendente(),
				discente.getChAtividadeObrigInteg(), discente.getChAtividadeObrigPendente(),
				discente.getChAulaIntegralizada(), discente.getChAulaPendente(),
				discente.getChLabIntegralizada(), discente.getChLabPendente(),
				discente.getChEstagioIntegralizada(), discente.getChEstagioPendente(),
				discente.getCrTotalIntegralizados(), discente.getCrTotalPendentes(),
				discente.getCrExtraIntegralizados(), discente.getCrNaoAtividadeObrigInteg(),
				discente.getCrNaoAtividadeObrigPendente(), discente.getCrLabIntegralizado(),
				discente.getCrLabPendente(), discente.getCrEstagioIntegralizado(),
				discente.getCrEstagioPendente(), discente.getCrAulaIntegralizado(),
				discente.getCrAulaPendente(), discente.getChIntegralizadaAproveitamentos(),discente.getTotalGruposOptativasPendentes(),
				discente.getTotalAtividadesPendentes() ,
				discente.getId()											
											
		});
	}

	/**
	 * Retorna os discentes de graduação que solicitaram turma de férias para o
	 * curso, ano e período indicados.
	 * 
	 * @param idCurso
	 *            id do curso do aluno
	 * @param ano
	 *            ano da turma solicitada
	 * @param periodo
	 *            Período da turma solicitada
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscentesSolicitacao> findBySolicitouTurmaFerias(int idCurso, Integer ano, Integer periodo, Integer tipoTurma) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		hql.append(" SELECT solicitacao FROM DiscentesSolicitacao solicitacao ");
		hql.append(" WHERE solicitacao.discenteGraduacao.discente.curso.id = :idCurso ");
		if (ano != null)
			hql.append(" AND solicitacao.solicitacaoTurma.ano = :ano ");
		if (periodo != null)
			hql.append(" AND solicitacao.solicitacaoTurma.periodo = :periodo ");
		if (tipoTurma != null)
			hql.append(" AND solicitacao.solicitacaoTurma.tipo = :tipo ");

		Query q = getSession().createQuery(hql.toString());

		q.setInteger("idCurso", idCurso);
		if (ano != null)
			q.setInteger("ano", ano);
		if (periodo != null)
			q.setInteger("periodo", periodo);
		if (tipoTurma != null)
			q.setInteger("tipo", tipoTurma);

		return q.list();
	}
	
	/**
	 * Busca discentes de ead que se enquadrem nos parâmetros informados.
	 * 
	 * @param polo
	 * @param curso
	 * @param nome
	 * @param matricula
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findDiscentesEad(Polo polo, Curso curso,String nome, Long matricula, Integer ano, Integer periodo) throws DAOException {
		StringBuffer hql = new StringBuffer();
		
		hql.append("select new DiscenteGraduacao(dg.id, d.matricula, p.nome, d.status, d.nivel) " +
					"from DiscenteGraduacao dg " +
						"left join dg.discente d " +
						"left join d.pessoa p " +
					"where d.matricula is not null ");
		
		if(isNotEmpty(polo))
			hql.append("and dg.polo.id = :polo ");
		if(isNotEmpty(curso))
			hql.append("and dg.discente.curso.id = :curso ");
		if(isNotEmpty(nome))
			hql.append("and p.nomeAscii like '" + StringUtils.toAsciiAndUpperCase(nome) + "%' ");
		if(isNotEmpty(matricula))
			hql.append("and d.matricula = :matricula ");
		if(isNotEmpty(ano))
			hql.append("and d.anoIngresso = :ano ");
		if(isNotEmpty(periodo))
			hql.append("and d.periodoIngresso = :periodo ");
		
		hql.append(" order by p.nome asc");
		Query q = getSession().createQuery(hql.toString());
		
		if(isNotEmpty(polo))
			q.setInteger("polo", polo.getId());
		if(isNotEmpty(curso))
			q.setInteger("curso", curso.getId());
		if(isNotEmpty(matricula))
			q.setLong("matricula", matricula);
		if(isNotEmpty(ano))
			q.setInteger("ano", ano);
		if(isNotEmpty(periodo))
			q.setInteger("periodo", periodo);

		@SuppressWarnings("unchecked")
		List<DiscenteGraduacao> discentes = q.list();
		
		return discentes;
	}

	/*
	 * Transforma o ResultSet retornado pelas consultas
	 * findComponentesDoCurriculoByDiscente e
	 * findComponentesSubUnidadesDoCurriculoByDiscente em um TreeSet<Integer>.
	 */
	
	/**
	 * Retorna os id's dos componentes curriculares
	 */
	private ResultSetExtractor idsComponentesExtractor = new ResultSetExtractor() {
		public Object extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			TreeSet<Integer> componentes = new TreeSet<Integer>();
			while (rs.next())
				componentes.add(rs.getInt("id_disciplina"));
			return componentes;
		}
	};

	/**
	 * Retorna um conjunto com os ids dos componentes do currículo
	 * 
	 * @param discente
	 * @param obrigatorios
	 *            Se os componentes são obrigatórios ou não no currículo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TreeSet<Integer> findComponentesDoCurriculoByDiscente(
			DiscenteAdapter discente, boolean obrigatorios) {
		String sql = "select cc.id_disciplina from graduacao.curriculo_componente cu, ensino.componente_curricular cc, discente d "
				+ "where cu.id_curriculo = d.id_curriculo and d.id_discente = ? and "
				+ "cu.obrigatoria = ? and cu.id_componente_curricular = cc.id_disciplina ";
		return (TreeSet<Integer>) getJdbcTemplate().query(sql,
				new Object[] { discente.getId(), obrigatorios },
				idsComponentesExtractor);
	}

	/**
	 * Retorna um conjunto com os ids dos componentes que são sub-unidades de
	 * componentes de bloco do currículo de um discente
	 * 
	 * @param discente
	 * @param obrigatorios
	 *            Se o componente é obrigatório no currículo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TreeSet<Integer> findComponentesSubUnidadesDoCurriculoByDiscente(
			DiscenteGraduacao discente, boolean obrigatorios) {
		String sql = "select id_disciplina from ensino.componente_curricular where id_bloco_subunidade in ( "
				+ "select distinct cu.id_componente_curricular from graduacao.curriculo_componente cu, ensino.componente_curricular cc, discente d "
				+ "where cu.obrigatoria = ? and cu.id_componente_curricular = cc.id_disciplina "
				+ "and cc.id_tipo_componente = ? and cu.id_curriculo = d.id_curriculo and d.id_discente = ? )";

		return (TreeSet<Integer>) getJdbcTemplate().query(
				sql,
				new Object[] { obrigatorios, TipoComponenteCurricular.BLOCO,
						discente.getId() }, idsComponentesExtractor);
	}

	/**
	 * Retorna os ID dos componentes curriculares do curso
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public TreeSet<Integer> findComponentesDoCursoByDiscente(int curso)
			throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT cc.id_componente_curricular "
					+ " FROM graduacao.curriculo_componente cc join graduacao.curriculo cur on (cc.id_curriculo=cur.id_curriculo)"
					+ " WHERE cur.id_curso = ? ";
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, curso);
			rs = st.executeQuery();

			TreeSet<Integer> componentes = new TreeSet<Integer>();
			while (rs.next()) {
				componentes.add(rs.getInt("id_componente_curricular"));
			}
			return componentes;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}
	
	/**
	 * Retorna um mapa com os IDs dos componentes curriculares do currículo do aluno
	 * e suas respectivas expressões de equivalência.
	 * 
	 * @param idCurriculo
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, String> findMapaComponentesEquivalenciaDoCurriculo(int idCurriculo)
	throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT cd.id_componente, cd.equivalencia "
				+ " FROM graduacao.curriculo_componente cc " +
						"join ensino.componente_curricular c on (cc.id_componente_curricular=c.id_disciplina) " +
						"join ensino.componente_curricular_detalhes cd on (c.id_detalhe=cd.id_componente_detalhes) "
				+ " WHERE cc.id_curriculo = ? ";
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, idCurriculo);
			rs = st.executeQuery();
			
			Map<Integer, String> componentes = new TreeMap<Integer, String>();
			while (rs.next()) {
				componentes.put(rs.getInt("id_componente"), rs.getString("equivalencia"));
			}
			return componentes;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Retorna os ID dos componentes curriculares baseados no nível de ensino e programa
	 * 
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public TreeSet<Integer> findComponentesDoCursoByNivelPrograma(char nivel, int programa)
			throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT cc.id_componente_curricular "
					+ " FROM graduacao.curriculo_componente cc join graduacao.curriculo cur on (cc.id_curriculo=cur.id_curriculo)"
					+ " join curso c using (id_curso)"
					+ " WHERE c.nivel = ? and c.id_unidade = ? ";
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setString(1, String.valueOf(nivel));
			st.setInt(2, programa);
			rs = st.executeQuery();

			TreeSet<Integer> componentes = new TreeSet<Integer>();
			while (rs.next()) {
				componentes.add(rs.getInt("id_componente_curricular"));
			}
			return componentes;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}	
	

	/**
	 * Busca por alunos graduandos que tiveram alguma matrícula num semestre
	 * específico
	 * 
	 * @param idCurso
	 * @param anoPeriodos
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteGraduacao> findGraduandosParaColacao(int idCurso, int... anoPeriodos) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT d FROM DiscenteGraduacao d, MatriculaComponente mc "
					+ " WHERE d.id = mc.discente.id and d.discente.status = :status and d.discente.curso.id = :curso and (");
			
			for (int i = 0; i < anoPeriodos.length; i++) {
				int ano = anoPeriodos[i] / 10;
				int periodo = anoPeriodos[i] - (ano * 10);
				hql.append("(mc.ano=" + ano + " AND mc.periodo=" + periodo + ")");
				if (i + 1 == anoPeriodos.length)
					hql.append(" ) ");
				else
					hql.append(" OR ");
			}
			
			hql.append(" ORDER BY d.discente.pessoa.nome asc ");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("status", StatusDiscente.GRADUANDO);
			q.setInteger("curso", idCurso);
			q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca por alunos concluintes do curso e períodos passados por parâmetro
	 * 
	 * @param idCurso
	 * @param anoPeriodos
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteGraduacao> findConcluintesbyCursoPeriodo(int idCurso,
			int... anoPeriodos) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql
					.append("SELECT d"
							+ " FROM DiscenteGraduacao d, MatriculaComponente mc "
							+ " WHERE d.id = mc.discente.id and d.discente.status = :status and d.discente.curso.id = :curso and (");
			for (int i = 0; i < anoPeriodos.length; i++) {
				int ano = anoPeriodos[i] / 10;
				int periodo = anoPeriodos[i] - (ano * 10);
				hql.append("(mc.ano=" + ano + " AND mc.periodo=" + periodo
						+ ")");
				if (i + 1 == anoPeriodos.length)
					hql.append(" ) ");
				else
					hql.append(" OR ");
			}
			hql.append(" ORDER BY d.discente.pessoa.nome asc ");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("status", StatusDiscente.CONCLUIDO);
			q.setInteger("curso", idCurso);
			q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	

	
	/**
	 * Realiza uma busca e retorna uma lista de discentes de um curso que
	 * participaram de uma Colação de Grau coletiva, isto é, possuem a mesma
	 * data de colação de grau.
	 * 
	 * @param idCurso
	 * @param dataColacao
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteGraduacao> findConcluidosByCursoDataColacao(int idCurso, Date dataColacao, Polo polo) throws DAOException {
		String hql = "select discenteGraduacao from DiscenteGraduacao discenteGraduacao" +
				" inner join fetch discenteGraduacao.discente discente" +
				" inner join fetch discente.curso curso" +
				" inner join fetch discente.pessoa pessoa" +
				" where curso.id = :idCurso" +
				(!isEmpty(polo) ? " and discenteGraduacao.polo.id = :idPolo" : "") +
				" and discente.dataColacaoGrau = :dataColacao" +
				" order by pessoa.nome";
		Query q = getSession().createQuery(hql);
		q.setInteger("idCurso", idCurso);
		q.setDate("dataColacao", dataColacao);
		if (!isEmpty(polo))
			q.setInteger("idPolo", polo.getId());
		return q.list();
	}
	
	
	/**
	 * Realiza uma busca e retorna uma lista de discentes de um curso que
	 * participaram de uma Colação de Grau coletiva, isto é, possuem a mesma
	 * data de movimentação de saída.
	 * 
	 * @param idCurso
	 * @param ano Ano da colação coletiva.
	 * @param semestre 1, caso a colação tenha sido entre janeiro e junho (inclusive). 2, caso tenha sido entre julho e dezembro (inclusive).
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findTurmasColacaoByCursoAnoPeriodoConclusao(int idCurso, int ano, int periodo) throws DAOException {
		StringBuilder sql = new StringBuilder(
				"select curso.id_curso,"
				+ " curso.nome as curso,"
				+ " municipio_polo.nome as municipio_polo,"
				+ " turno.sigla as turno,"
				+ " grau_academico.descricao as grau_academico,"
				+ " municipio.nome as municipio,"
				+ " modalidade_educacao.descricao as modalidade,"
				+ " unidade.nome as unidade,"
				+ " unidade.sigla as sigla,"
				+ " data_colacao_grau," 
				+ " coalesce(polo.id_polo, 0) as id_polo,"
				+ " count(*) as quantidade"
				+ " from discente"
				+ " inner join ensino.movimentacao_aluno using (id_discente) "
				+ " inner join graduacao.curriculo using (id_curriculo)"
				+ " inner join curso on (curriculo.id_curso = curso.id_curso) "
				+ " inner join graduacao.matriz_curricular on (id_matriz = id_matriz_curricular)"
				+ " left join ensino.grau_academico on (matriz_curricular.id_grau_academico = grau_academico.id_grau_academico) "
				+ " left join ensino.turno on (curso.id_turno = turno.id_turno) "
				+ " left join comum.modalidade_educacao using (id_modalidade_educacao) "
				+ " left join comum.municipio using (id_municipio) "
				+ " left join comum.unidade using (id_unidade)"
				+ " inner join graduacao.discente_graduacao on (id_discente = id_discente_graduacao)"
				+ " left join ead.polo using (id_polo)"
				+ " left join comum.municipio municipio_polo on (polo.id_cidade = municipio_polo.id_municipio)"
				+ " where data_colacao_grau is not null"
				+ " and movimentacao_aluno.id_tipo_movimentacao_aluno = :tipoMovimentacao"
				+ " and movimentacao_aluno.ativo = trueValue()");
		if (idCurso > 0) {
			sql.append(" and curso.id_curso = :idCurso");
		}
		if (ano != 0 && periodo != 0) {
			sql.append(" and ano_referencia = :ano");
			sql.append(" and periodo_referencia = :periodo");
		}
		sql.append(" group by curso.id_curso, curso.nome, municipio_polo.nome, turno.sigla, grau_academico.descricao, municipio.nome, modalidade_educacao.descricao, unidade.nome, unidade.sigla, data_colacao_grau, polo.id_polo");
		sql.append("  having count(*) > 1");
		sql.append(" order by curso.nome, municipio_polo.nome, curso.id_curso, data_colacao_grau desc");
		SQLQuery q = getSession().createSQLQuery(sql.toString());
		q.setInteger("tipoMovimentacao", TipoMovimentacaoAluno.CONCLUSAO);
		if (ano != 0 && periodo != 0) {
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
		}
		if (idCurso > 0) {
			q.setInteger("idCurso", idCurso);
		}
		q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP);
		return q.list();
	}

	/**
	 * Busca por alunos graduandos que tiveram alguma matrícula num semestre
	 * específico ordenado por habilitação e modalidade
	 * 
	 * @param idCurso
	 * @param anoPeriodos
	 * @return
	 * @throws DAOException
	 */
	public Map<DiscenteGraduacao, String> findGraduandosParaListaAssinaturaColacao(
			int idCurso, int idPolo, int... anoPeriodos) throws DAOException {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select distinct discente_graduacao.id_discente_graduacao,"
							+ " discente.matricula, pessoa.nome, pessoa.nome_oficial, "
							+ " grau.descricao as descricaoGrau, habilitacao.nome as nomeHabilitacao,"
							+ " participacao_ingressante.participacao_pendente as participacao_pendente_ingressante,"
							+ " participacao_ingressante.descricao as participacao_ingressante_descricao,"
							+ " participacao_concluinte.participacao_pendente as participacao_pendente_concluinte,"
							+ " participacao_concluinte.descricao as participacao_concluinte_descricao,"
							+ " pendencia_biblioteca.identificacao_vinculo,"
							+ " case when (");
			// ano-período
			for (int i = 0; i < anoPeriodos.length; i++) {
				sql.append("f_discente_graduando_ano_periodo(graduacao.discente_graduacao.id_discente_graduacao) = '"+ anoPeriodos[i] + "'");
				if (i + 1 == anoPeriodos.length)
					sql.append(" ) ");
				else
					sql.append(" OR ");
			}
			sql.append(" then null else f_discente_graduando_ano_periodo(graduacao.discente_graduacao.id_discente_graduacao) end as outroPeriodo"
							+ " from graduacao.discente_graduacao"
							+ " inner join discente on (discente.id_discente = id_discente_graduacao)"
							+ " inner join comum.pessoa using (id_pessoa)"
							+ " inner join ensino.matricula_componente on (matricula_componente.id_discente = id_discente_graduacao)"
							+ " inner join graduacao.matriz_curricular using (id_matriz_curricular)"
							+ " left join ensino.grau_academico grau using (id_grau_academico)"
							+ " left join graduacao.habilitacao habilitacao using (id_habilitacao)"
							+ " left join graduacao.participacao_enade participacao_ingressante on (discente_graduacao.id_enade_ingressante = participacao_ingressante.id_participacao_enade)"
							+ " left join graduacao.participacao_enade participacao_concluinte on  (discente_graduacao.id_enade_concluinte  = participacao_concluinte.id_participacao_enade )"
							+ " left join (select identificacao_vinculo  " +
									   "   from biblioteca.emprestimo" +
									   "   inner join biblioteca.usuario_biblioteca using (id_usuario_biblioteca)" +
									   "   where emprestimo.ativo" +
									   "   and usuario_biblioteca.vinculo = :vinculo" +
									   "   and emprestimo.situacao = :situacaoEmprestimo) pendencia_biblioteca on  (identificacao_vinculo = discente.id_discente)"
							+ " where matriz_curricular.permite_colacao_grau = trueValue()"
							+ " and discente.status = :status"
							+ " and discente.id_curso = :curso"
							+ " and (discente_graduacao.cola_grau is null or discente_graduacao.cola_grau = trueValue())");
			// pólo
			if (idPolo > 0)
				sql.append(" and discente_graduacao.id_polo = :idPolo");
			// remove da lista os discentes que possuem pendências na biblioteca
			sql.append( " ORDER BY pessoa.nome_oficial" );
			
			Query q = getSession().createSQLQuery(sql.toString());
			
			q.setInteger("status", StatusDiscente.GRADUANDO);
			q.setInteger("curso", idCurso);
			q.setInteger("situacaoEmprestimo", Emprestimo.EMPRESTADO);
			q.setInteger("vinculo", VinculoUsuarioBiblioteca.ALUNO_GRADUACAO.getValor());
			if (idPolo > 0)
				q.setInteger("idPolo", idPolo);
			Map<DiscenteGraduacao, String> result = new LinkedHashMap<DiscenteGraduacao, String>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			for (Object[] obj : lista) {
				int i = 0;
				DiscenteGraduacao dg = new DiscenteGraduacao();
				dg.setId((Integer) obj[i++]);
				dg.setMatricula(((BigInteger) obj[i++]).longValue());
				dg.setPessoa( new Pessoa() );
				dg.getPessoa().setNome( (String) obj[i++]);
				dg.getPessoa().setNomeOficial( (String) obj[i++]);
				dg.setMatrizCurricular(new MatrizCurricular());
				String descricaoGrau = (String) obj[i++];
				String nomeHabilitacao = (String) obj[i++];
				Boolean participacaoIngressantePendente = (Boolean) obj[i++]; 
				String participacaoIngressanteDescricao = (String) obj[i++];
				Boolean participacaoConcluintePendente = (Boolean) obj[i++]; 
				String participacaoConcluinteDescricao = (String) obj[i++];
				boolean pendenciaBiblioteca = obj[i++] != null;
				String outroPeriodo = (String) obj[i++];
				if( descricaoGrau != null ){
					dg.getMatrizCurricular().setGrauAcademico(new  GrauAcademico());
					dg.getMatrizCurricular().getGrauAcademico().setDescricao( descricaoGrau );
				}
				if( nomeHabilitacao != null ){
					dg.getMatrizCurricular().setHabilitacao(new Habilitacao());
					dg.getMatrizCurricular().getHabilitacao().setNome(nomeHabilitacao);
				}
				// pendências que impedem de colar grau
				StringBuilder motivo = new StringBuilder();
				if (participacaoIngressantePendente == null) {
					motivo.append("Participação no ENADE Ingressante não definida; ");
				} else if (participacaoIngressantePendente.booleanValue()) {
					motivo.append(participacaoIngressanteDescricao).append("; ");
				}
				if (participacaoConcluintePendente == null) {
					motivo.append("Participação no ENADE Concluínte não definida; ");
				} else if (participacaoConcluintePendente.booleanValue()) {
					motivo.append(participacaoConcluinteDescricao).append("; ");
				}
				if (pendenciaBiblioteca){
					motivo.append("Pendência em uma ou mais Bibliotecas; ");
				}
				if (outroPeriodo != null){
					motivo.append("Concluiu em ").append(outroPeriodo.substring(0, 4)).append(".").append(outroPeriodo.substring(4)).append("; ");
				}
				dg.setSelecionado(false);
				if (motivo.length() == 0)
					dg.setSelecionado(true);
				result.put(dg, motivo.toString());
			}
			return result;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Localiza a matriz com ID fornecido e que os discentes possuam o status também fornecido
	 * 
	 * @see StatusDiscente
	 * @param id
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findByMatriz(int id, int... status)
			throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT d.id_discente, pe.nome as DISCENTE_NOME, d.matricula, dg.id_polo, mun.nome as CIDADE_NOME"
					+ " FROM discente d inner join graduacao.discente_graduacao dg on (d.id_discente=dg.id_discente_graduacao) "
					+ " left join ead.polo p on (dg.id_polo=p.id_polo) "
					+ " left join comum.municipio mun on (p.id_cidade = mun.id_municipio) "
					+ " inner join comum.pessoa pe on (d.id_pessoa = pe.id_pessoa) "
					+ " WHERE dg.id_matriz_curricular = ? and d.status in "
					+ gerarStringIn(status)
					+ " ORDER BY mun.nome asc, pe.nome asc";

			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, id);
			rs = st.executeQuery();
			List<DiscenteGraduacao> discentes = new ArrayList<DiscenteGraduacao>(
					0);
			while (rs.next()) {
				DiscenteGraduacao dg = new DiscenteGraduacao(rs
						.getInt("id_discente"));
				dg.setMatricula(rs.getLong("matricula"));
				dg.getPessoa().setNome(rs.getString("discente_nome"));
				dg.setPolo(new Polo(rs.getInt("id_polo")));
				dg.getPolo().setCidade(new Municipio());
				dg.getPolo().getCidade().setNome(rs.getString("cidade_nome"));
				discentes.add(dg);
			}
			return discentes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Busca todos os alunos do ensino a distancia com base no ano/período de ingresso e no status
	 * 
	 * @param status
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<DiscenteGraduacao> findDiscentesEADByStatusAnoPeriodoEntradaCurso(StatusDiscente status, int ano, int periodo, int idCurso) throws DAOException {
		String hql = "select d from DiscenteGraduacao d " +
				"inner join fetch d.polo " +
				"inner join fetch d.discente discente " +
				"inner join fetch discente.curriculo " +
				"inner join fetch d.matrizCurricular " +
				"inner join fetch discente.pessoa " +
				"where " +
				"d.discente.anoIngresso = :ano and " +
				"d.discente.periodoIngresso = :periodo and " +
				"d.discente.status = :status and " +
				"d.discente.nivel = :nivelEnsino and " +
				"d.discente.curso.id = :idCurso and " + 
				"d.discente.curso.modalidadeEducacao = :modalidade";
		
		Query query = getSession().createQuery(hql);
		
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
		query.setInteger("status", status.getId());
		query.setInteger("idCurso", idCurso);
		query.setCharacter("nivelEnsino", NivelEnsino.GRADUACAO);
		query.setInteger("modalidade", ModalidadeEducacao.A_DISTANCIA);
		
		return query.list();
	}	

	/**
	 * Retorna os ids de todos os FORMANDOS e também todos os alunos que estão com a data dos últimos cálculos nulos do curso informado
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteGraduacao> findIdsByCursoParaRecalculo(int idCurso) throws DAOException{
		Query q = getSession().createQuery(
				"SELECT dg FROM DiscenteGraduacao dg " +
				"	join dg.discente d" +
				"	join d.curso c "
						+ " WHERE c.id = :curso " 
						+ " AND ( d.status in " + gerarStringIn( new int[]{StatusDiscente.GRADUANDO, StatusDiscente.FORMANDO} )  
						+ " OR ( dg.ultimaAtualizacaoTotais is null AND d.status = :ativo  ) )" );
		q.setInteger("curso", idCurso);
		q.setInteger("ativo", StatusDiscente.ATIVO);
		return q.list();
	}
	
	/**
	 * Retorna o número de ids de todos os FORMANDOS e também todos os alunos que estão com a data dos últimos cálculos nulos do curso informado
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public int findCountByCursoParaRecalculo(int idCurso) throws DAOException{
		Query q = getSession().createQuery(
				"SELECT count(d.id) FROM DiscenteGraduacao d  "
						+ " WHERE d.discente.curso.id = :curso " 
						+ " AND ( d.discente.status in " + gerarStringIn( new int[]{StatusDiscente.GRADUANDO, StatusDiscente.FORMANDO} )  
						+ " OR ( d.ultimaAtualizacaoTotais is null AND d.discente.status = :ativo  ) )" );
		q.setInteger("curso", idCurso);
		q.setInteger("ativo", StatusDiscente.ATIVO);
		return ((Number) q.uniqueResult()).intValue();
	}
	
	/**
	 * Zera a data da última atualização dos cálculos dos discentes ativos, formandos e graduandos associados ao currículo
	 * @param curriculo
	 * @throws DAOException
	 */
	public void zerarUltimasAtualizacoes(Curriculo curriculo) throws DAOException {
		if( curriculo.getCurso().getNivel() != NivelEnsino.GRADUACAO )
			return;
			String sql = " UPDATE graduacao.discente_graduacao SET ultima_atualizacao_totais = null " 
				+ " WHERE ultima_atualizacao_totais is not null AND id_discente_graduacao in( "
				+ " SELECT DISTINCT d.id_discente "
				+ " FROM discente d "
				+ " join graduacao.discente_graduacao dg on  dg.id_discente_graduacao = d.id_discente "
				+ " WHERE d.nivel = '" + NivelEnsino.GRADUACAO + "' "
				+ " AND d.status in " + gerarStringIn( StatusDiscente.getStatusComVinculo())
				+ " and d.id_curriculo = " + curriculo.getId()
				+ " ) ";
			update(sql);
	}
	

	/** Seta os tipos de integralização das matrículas do discentes indicados no parâmetro para nulo.  
	 * @param idDiscente
	 */
	public void zerarIntegralizacoes(int idDiscente) {
		getJdbcTemplate().update("update ensino.matricula_componente set tipo_integralizacao = null where id_discente = ?", new Object[] { idDiscente });
	}

	/**
	 * Retorna um {@link DiscenteGraduacao} apenas com o ID e os totais integralizados preenchidos.
	 * Utilizado nos cálculos de discentes.
	 * 
	 * @param idDiscenteGraduacao
	 * @return
	 */
	public DiscenteGraduacao findTotaisIntegralizadosByDiscente(final int idDiscenteGraduacao) {
		return (DiscenteGraduacao) getJdbcTemplate().queryForObject("select * from graduacao.discente_graduacao where id_discente_graduacao = ?", new Object[] { idDiscenteGraduacao }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DiscenteGraduacao dg = new DiscenteGraduacao();
				dg.setId(idDiscenteGraduacao);
				dg.setChTotalIntegralizada(rs.getShort("ch_total_integralizada"));
				dg.setChTotalPendente(rs.getShort("ch_total_pendente"));
				dg.setChOptativaIntegralizada(rs.getShort("ch_optativa_integralizada"));
				dg.setChOptativaPendente(rs.getShort("ch_optativa_pendente"));
				dg.setChNaoAtividadeObrigInteg(rs.getShort("ch_nao_atividade_obrig_integ"));
				dg.setChNaoAtividadeObrigPendente(rs.getShort("ch_nao_atividade_obrig_pendente"));
				dg.setChAtividadeObrigInteg(rs.getShort("ch_atividade_obrig_integ"));
				dg.setChAtividadeObrigPendente(rs.getShort("ch_atividade_obrig_pendente"));
				dg.setChAulaIntegralizada(rs.getShort("ch_aula_integralizada"));
				dg.setChAulaPendente(rs.getShort("ch_aula_pendente"));
				dg.setChLabIntegralizada(rs.getShort("ch_lab_integralizada"));
				dg.setChLabPendente(rs.getShort("ch_lab_pendente"));
				dg.setChEstagioIntegralizada(rs.getShort("ch_estagio_integralizada"));
				dg.setChEstagioPendente(rs.getShort("ch_estagio_pendente"));
				dg.setCrTotalIntegralizados(rs.getShort("cr_total_integralizados"));
				dg.setCrTotalPendentes(rs.getShort("cr_total_pendentes"));
				dg.setCrExtraIntegralizados(rs.getShort("cr_extra_integralizados"));
				dg.setCrNaoAtividadeObrigInteg(rs.getShort("cr_nao_atividade_obrig_integralizado"));
				dg.setCrNaoAtividadeObrigPendente(rs.getShort("cr_nao_atividade_obrig_pendente"));
				dg.setCrLabIntegralizado(rs.getShort("cr_lab_integralizado"));
				dg.setCrLabPendente(rs.getShort("cr_lab_pendente"));
				dg.setCrEstagioIntegralizado(rs.getShort("cr_estagio_integralizado"));
				dg.setCrEstagioPendente(rs.getShort("cr_estagio_pendente"));
				dg.setCrAulaIntegralizado(rs.getShort("cr_aula_integralizado"));
				dg.setCrAulaPendente(rs.getShort("cr_aula_pendente"));
				dg.setChIntegralizadaAproveitamentos(rs.getShort("ch_integralizada_aproveitamentos"));
				return dg;
			}
		});
	}

	/** Retorna uma coleção de discentes por matrícula.
	 * @param matriculas
	 * @return
	 * @throws DAOException 
	 */
	public Collection<DiscenteGraduacao> findByMatriculas(Collection<Long> matriculas) throws DAOException {
		Criteria c = getSession().createCriteria(DiscenteGraduacao.class);
		c.createCriteria("discente").add(Restrictions.in("matricula", matriculas));
		@SuppressWarnings("unchecked")
		Collection<DiscenteGraduacao> lista = c.list();
		return lista;
	}
	
}
