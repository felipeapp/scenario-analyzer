/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 23/11/2012
 *
 */
package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/** Classe responsável por consultas específicas ao Plano de Matrícula de Ingressantes.
 * 
 * @author Édipo Elder F. de Melo
 *
 */
public class PlanoMatriculaIngressantesDao extends GenericSigaaDAO {

	/** Atualiza a reserva de vagas de ingressantes nas turmas que compõe um Plano de Matrícula.
	 * As vagas serão aumentadas para no mínimo a capacidade indicada no plano. Nunca será diminuído. 
	 * @param idPlanoMatricula
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public void atualizaReservaVagasIngressantes(PlanoMatriculaIngressantes planoMatricula) throws HibernateException, DAOException {
		// o delta é a diferença para mais ou para menos da reserva de vagas de ingressantes quando o plano for cadastrado ou atualizado.
		int delta = 0;
		// se cadastrando, as vagas serão incrementadas com a capacidade do plano
		if (planoMatricula.getId() == 0)
			delta = planoMatricula.getCapacidade();
		else { // caso contrário, será a diferença entre nova capacidade e a cadastrada 
			PlanoMatriculaIngressantes planoBD = findByPrimaryKey(planoMatricula.getId(), PlanoMatriculaIngressantes.class);
			detach(planoBD);
			delta = planoMatricula.getCapacidade() - planoBD.getCapacidade();
		}
		// consulta o somatório das capacidades
		String sql = "select rc.id_reserva_curso, rc.vagas_atendidas_ingressantes, rc.vagas_atendidas" +
				" from graduacao.reserva_curso rc" +
				" inner join ensino.turma t using (id_turma)" +
				" where id_matriz_curricular = :idMatrizCurricular" +
				" and t.id_turma in " + UFRNUtils.gerarStringIn(planoMatricula.getTurmas());
		SQLQuery q = getSession().createSQLQuery(sql);
		q.setInteger("idMatrizCurricular", planoMatricula.getMatrizCurricular().getId());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		if (!isEmpty(lista)){
			for (Object[] obj : lista) {
				int id = (Integer) obj[0];
				int novoValor = ((Integer) obj[1]) + delta;
				updateField(ReservaCurso.class, id, "vagasReservadasIngressantes", novoValor);
				novoValor = ((Integer) obj[2]) - delta;
				updateField(ReservaCurso.class, id, "vagasReservadas", novoValor);
			}
		}
	}

	/** Verifica e atualiza a capacidade nas turmas que compõe um Plano de Matrícula.
	 * A capcidade será aumentada em razão do aumento das vagas nas reservas das turmas.
	 * @param idPlanoMatricula
	 * @param registroEntrada
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Turma> verificaEstouroCapacidadeTurma(PlanoMatriculaIngressantes planoMatricula) throws HibernateException, DAOException {
		// o delta é a diferença para mais ou para menos da reserva de vagas de ingressantes quando o plano for cadastrado ou atualizado.
		int delta = 0;
		// se cadastrando, as vagas serão incrementadas com a capacidade do plano
		if (planoMatricula.getId() == 0)
			delta = planoMatricula.getCapacidade();
		else { // caso contrário, será a diferença entre nova capacidade e a cadastrada 
			PlanoMatriculaIngressantes planoBD = findByPrimaryKey(planoMatricula.getId(), PlanoMatriculaIngressantes.class);
			detach(planoBD);
			delta = planoMatricula.getCapacidade() - planoBD.getCapacidade();
		}
		// consulta as reservas atualmente cadastradas
		// OBS: o hibernate não consegue tratar tem quando as colunas tem o mesmo nome, no caso, t.codigo e cc.codigo
		// por isso usar uma projecao para consulta e um alias para o parseTo():
		String projecao = "t.id_turma, t.ano, t.periodo, t.codigo as codigo_turma, t.tipo, cc.codigo as codigo_componente, ccd.nome, t.capacidade_aluno";
		String alias = "t.id, t.ano, t.periodo, t.codigo, t.tipo,disciplina.codigo, disciplina.nome, t.capacidadeAluno";
		String sql = "select " + projecao +
				" from graduacao.reserva_curso rc" +
				" inner join ensino.turma t using (id_turma)" +
				" inner join ensino.componente_curricular cc using (id_disciplina)" +
				" inner join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes)" +
				" where t.id_turma in " + UFRNUtils.gerarStringIn(planoMatricula.getTurmas()) +
				" and rc.id_matriz_curricular = :idMatrizCurricular" +
				" group by t.id_turma, t.ano, t.periodo, t.codigo, t.tipo, t.tipo, cc.codigo, ccd.nome, t.capacidade_aluno, rc.vagas_atendidas, rc.vagas_atendidas_ingressantes" +
				" having (t.capacidade_aluno) < sum(rc.vagas_atendidas + rc.vagas_atendidas_ingressantes) " +
				" or rc.vagas_atendidas - :delta < 0" +
				" or rc.vagas_atendidas_ingressantes + :delta < 0";
		SQLQuery q = getSession().createSQLQuery(sql);
		q.setInteger("delta", delta);
		q.setInteger("idMatrizCurricular", planoMatricula.getMatrizCurricular().getId());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		Collection<Turma> turmas = HibernateUtils.parseTo(lista, alias, Turma.class, "t");
		return turmas;
	}
	
	/** Retorna os planos de matrículas de uma matriz curricular.
	 * @param idMatrizCurricular
	 * @return
	 * @throws DAOException
	 */
	public Collection<PlanoMatriculaIngressantes> findByMatrizCurricular(int idMatrizCurricular, int ano, int periodo) throws DAOException {
		Criteria c = getSession().createCriteria(PlanoMatriculaIngressantes.class);
		if (ano > 0)
			c.add(Restrictions.eq("ano", ano));
		if (periodo > 0)
			c.add(Restrictions.eq("periodo", periodo));
		c.createCriteria("matrizCurricular").add(Restrictions.eq("id", idMatrizCurricular));
		c.addOrder(Order.asc("descricao"));
		@SuppressWarnings("unchecked")
		List<PlanoMatriculaIngressantes> lista = c.list();
		return lista;
	}
	
	/** Retorna em qual plano de matrícula o discente foi atendido.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public PlanoMatriculaIngressantes findByDiscente(int idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(PlanoMatriculaIngressantes.class);
		c.createCriteria("discentesAtendidos").add(Restrictions.eq("id", idDiscente));
		return (PlanoMatriculaIngressantes) c.uniqueResult();
	}
	
	/** REtorna uma coleção de sugestões de matrícula com turmas que possuam reserva para uma determinada matriz curricular.
	 * @param idMatrizCurricular
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SugestaoMatricula> findSugestoesMatriculaGraduacao(int idMatrizCurricular, int ano, int periodo, int ...situacoesTurma) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		DiscenteDao ddao = null;
		Collection<SugestaoMatricula> sugestoes = new ArrayList<SugestaoMatricula>();
		try {
			con = Database.getInstance().getSigaaConnection();
			String projecao = "t.id_turma, t.codigo as cod_turma, t.descricao_horario,t.local, t.id_situacao_turma, dt.id_docente_turma, "
					+ " p1.nome as nome_docente, p2.nome as nome_docente_externo,  "
					+ " c.id_disciplina, c.codigo as cod_componente, cd.nome, "
					+ " case when cur.id_matriz is null then 0 else cc.semestre_oferta end as semestre_oferta, "
					+ " cd.co_requisito, cd.pre_requisito, " 
					+ " case when cur.id_matriz is null then false else cc.obrigatoria end as OBRIGATORIA,"
					+ " rc.vagas_atendidas, c.codigo";
			String order = " ORDER BY semestre_oferta, c.codigo, t.codigo, t.id_turma";
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT "
					+ projecao
					+ " FROM ensino.turma t "
					+ " join ensino.componente_curricular c on (t.id_disciplina = c.id_disciplina)"
					+ " join graduacao.curriculo_componente cc on (cc.id_componente_curricular = c.id_disciplina or cc.id_componente_curricular = c.id_bloco_subunidade)"
					+ " join graduacao.curriculo cur using (id_curriculo)"
					+ " join ensino.componente_curricular_detalhes cd on (c.id_detalhe = cd.id_componente_detalhes)"
					+ " join graduacao.reserva_curso rc on (rc.id_turma=t.id_turma and rc.id_matriz_curricular = cur.id_matriz)"
					+ " left join ensino.docente_turma dt on (t.id_turma=dt.id_turma) "
					+ " left join rh.servidor s on (s.id_servidor = dt.id_docente) "
					+ " left join comum.pessoa p1 on (s.id_pessoa = p1.id_pessoa) "
					+ " left join ensino.docente_externo de on (de.id_docente_externo = dt.id_docente_externo) "
					+ " left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) "
					+ " WHERE cur.id_matriz = ? "
					+ " AND t.tipo=" + Turma.REGULAR
					+ (isEmpty(situacoesTurma) ? "" : " AND t.id_situacao_turma in " + UFRNUtils.gerarStringIn(situacoesTurma))
					+ " AND t.agrupadora = falseValue()"
					+ " AND t.ano = ? AND t.periodo = ? "
					+ " AND t.id_turma_bloco is null "
					+ " AND c.matriculavel = trueValue() "
					+ order);
			// união com turmas que não são do currículo, mas possuem reserva para a matriz curricular.
			StringBuffer sqlExtraCurricular = new StringBuffer("SELECT "
					+ projecao
					+ " FROM ensino.turma t  "
					+ " join ensino.componente_curricular c on (t.id_disciplina = c.id_disciplina) "
					+ " left join graduacao.curriculo_componente cc on (cc.id_componente_curricular = c.id_disciplina or cc.id_componente_curricular = c.id_bloco_subunidade) "
					+ " left join graduacao.curriculo cur on (cc.id_curriculo = cur.id_curriculo and cur.id_matriz = ?)"
					+ " join ensino.componente_curricular_detalhes cd on (c.id_detalhe = cd.id_componente_detalhes) "
					+ " join graduacao.reserva_curso rc on (rc.id_turma=t.id_turma and rc.id_matriz_curricular = ?) "
					+ " left join ensino.docente_turma dt on (t.id_turma=dt.id_turma)  "
					+ " left join rh.servidor s on (s.id_servidor = dt.id_docente)  "
					+ " left join comum.pessoa p1 on (s.id_pessoa = p1.id_pessoa)  "
					+ " left join ensino.docente_externo de on (de.id_docente_externo = dt.id_docente_externo)  "
					+ " left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) 				"
					+ " WHERE cur.id_curriculo is null"
					+ " AND t.tipo=" + Turma.REGULAR
					+ (isEmpty(situacoesTurma) ? "" : " AND t.id_situacao_turma in " + UFRNUtils.gerarStringIn(situacoesTurma))
					+ " AND t.agrupadora = falseValue() "
					+ " AND t.ano = ? AND t.periodo = ? "
					+ " AND t.id_turma_bloco is null"
					+ " AND c.matriculavel = trueValue() "
					+ order);

			st = con.prepareStatement(sql.toString());

			// Setar parâmetros
			int i = 1;
			st.setInt(i++, idMatrizCurricular);
			st.setInt(i++, ano);
			st.setInt(i++, periodo);
			
			rs = st.executeQuery();

			// Percorrer ResultSet e criar objetos de domínio
			rsToSugestoes(rs, sugestoes);
			
			// consulta as extracurriculares
			st = con.prepareStatement(sqlExtraCurricular.toString());

			// Setar parâmetros
			i = 1;
			st.setInt(i++, idMatrizCurricular);
			st.setInt(i++, idMatrizCurricular);
			st.setInt(i++, ano);
			st.setInt(i++, periodo);
			rs = st.executeQuery();

			// Percorrer ResultSet e criar objetos de domínio
			rsToSugestoes(rs, sugestoes);
			
			return sugestoes;
		} catch (Exception e) {
			throw new DAOException(e);
		}  finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
			if (ddao !=null) ddao.close();
		}
	}

	private void rsToSugestoes(ResultSet rs,
			Collection<SugestaoMatricula> sugestoes) throws SQLException {
		SugestaoMatricula sugestao = new SugestaoMatricula();
		while (rs.next()) {
			boolean adicionar = false;
			if (sugestao.getTurma().getId() != rs.getInt("ID_TURMA")) {
				sugestao = new SugestaoMatricula();
				adicionar = true;
			}
			sugestao.getTurma().setId(rs.getInt("ID_TURMA"));
			sugestao.getTurma().setSituacaoTurma(new SituacaoTurma(rs.getInt("ID_SITUACAO_TURMA")));
			sugestao.getTurma().setCodigo(rs.getString("COD_TURMA"));
			sugestao.getTurma().setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
			sugestao.getTurma().setLocal(rs.getString("LOCAL"));
			if (rs.getInt("vagas_atendidas") > 0) {
				sugestao.getTurma().setReservas(new ArrayList<ReservaCurso>());
				ReservaCurso rc = new ReservaCurso();
				rc.setVagasReservadas((short) rs.getInt("vagas_atendidas"));
				sugestao.getTurma().getReservas().add(rc);
			}

			ComponenteCurricular componente = new ComponenteCurricular();
			componente.setId(rs.getInt("ID_DISCIPLINA"));
			componente.setCoRequisito(rs.getString("CO_REQUISITO"));
			componente.setPreRequisito(rs.getString("PRE_REQUISITO"));
			componente.setNome(rs.getString("NOME"));
			componente.setCodigo(rs.getString("COD_COMPONENTE"));

			sugestao.setObrigatoria(rs.getBoolean("OBRIGATORIA"));
			sugestao.getTurma().setDisciplina(componente);
			
			Integer nivel = rs.getInt("SEMESTRE_OFERTA");
			if (nivel != null && nivel > 0)
				sugestao.setNivel(nivel + "º Nível");
			else
				sugestao.setNivel("Extracurricular");

			String nomeDocente = rs.getString("NOME_DOCENTE");
			if (nomeDocente == null) {
				nomeDocente = rs.getString("NOME_DOCENTE_EXTERNO");
			}
			
			sugestao.addDocentesNomes(rs.getInt("ID_DOCENTE_TURMA"), nomeDocente );
			if (adicionar && !sugestoes.contains(sugestao))
				sugestoes.add(sugestao);
		}
	}

	/** Retorna uma coleção de anos-períodos que há Plano de Matrícula cadastrado para o curso especificado.
	 * Caso não especificado o curso, retornar o ano-período global.
	 * @param idCurso
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<Integer[]> findAllAnoPeriodo(int idCurso) throws HibernateException, DAOException {
		String hql = "select distinct ano, periodo" +
				" from PlanoMatriculaIngressantes" +
				(idCurso >0 ? " where curso.id = " + idCurso : "") +
				" order by ano desc, periodo desc";
		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<Integer[]> lista = q.list();
		Collection<Integer[]> anosPeriodos = new LinkedList<Integer[]>();
		if (lista != null)
			for (Object[] obj : lista) {
				Integer[] anoPeriodo = {(Integer) obj[0], (Integer) obj[1]};
				anosPeriodos.add(anoPeriodo);
			}
		return anosPeriodos;
	}
	
	/**
	 * Retorna a quantidade de planos de matrícula existentes para o ano, período e matriz curricular informados
	 *
	 * @param idComponente
	 * @param ano
	 * @param periodo
	 * @return A quantidade de turmas existentes
	 * @throws DAOException
	 */
	public Collection<Integer> findAllCodigosByAnoPeriodoMatriz(int ano, int periodo, int idMatrizCurricular) throws DAOException{
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT cast(descricao as integer) as codigo "
					+ " FROM PlanoMatriculaIngressantes pmi "
					+ " WHERE pmi.matrizCurricular.id = :idMatrizCurricular "
					+ " AND pmi.ano = :ano " + " AND pmi.periodo = :periodo ");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idMatrizCurricular", idMatrizCurricular );
			q.setInteger("ano", ano );
			q.setInteger("periodo", periodo );

			@SuppressWarnings("unchecked")
			List<Integer> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/** Retorna os planos de um curso em determinado ano-período.
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<PlanoMatriculaIngressantes> findByCurso(int idCurso, int ano, int periodo) throws DAOException {
		StringBuilder hql = new StringBuilder("select pmi.id_plano_matricula_ingressantes," +
				" pmi.ano," +
				" pmi.periodo," +
				" pmi.descricao," +
				" pmi.capacidade," +
				" mc.id_matriz_curricular," +
				" c.nome as nomeCurso," +
				" grau_academico.descricao as grau," +
				" turno.sigla as turno," +
				" habilitacao.nome as habilitacao," +
				" municipio.nome as municipio," +
				" enfase.nome as enfase," +
				" t.id_turma," +
				" cc.id_disciplina," +
				" cc.codigo," +
				" ccd.nome," +
				" t.codigo as codigoTurma," +
				" d.id_discente," +
				" d.matricula," +
				" p.nome," +
				" d.status" +
				" from ensino.plano_matricula_ingressantes pmi" +
				" inner join curso c using (id_curso)" +
				" inner join ensino.plano_matricula_turma using (id_plano_matricula_ingressantes)" +
				" inner join ensino.turma t using (id_turma)" +
				" inner join ensino.componente_curricular cc using (id_disciplina)" +
				" inner join ensino.componente_curricular_detalhes ccd on (id_detalhe = id_componente_detalhes)" +
				" left join ensino.plano_matricula_discentes using (id_plano_matricula_ingressantes)" +
				" left join discente d using (id_discente)" +
				" left join comum.pessoa p using (id_pessoa)" +
				" left join graduacao.matriz_curricular mc using (id_matriz_curricular)" +
				" left join ensino.turno on (turno.id_turno = mc.id_turno)" +
				" left join graduacao.habilitacao using (id_habilitacao)" +
				" left join graduacao.enfase using (id_enfase)" +
				" left join ensino.grau_academico on (grau_academico.id_grau_academico = mc.id_grau_academico)" +
				" left join comum.municipio using (id_municipio)" +
				" where c.id_curso = :idCurso");
		if (ano > 0)
			hql.append(" and pmi.ano = :ano");
		if (periodo > 0)
			hql.append(" and pmi.periodo = :periodo");
		Query q = getSession().createSQLQuery(hql.toString());
		q.setInteger("idCurso", idCurso);
		if (ano > 0)
			q.setInteger("ano", ano);
		if (periodo > 0)
			q.setInteger("periodo", periodo);
		Map<Integer, PlanoMatriculaIngressantes> mapa = new TreeMap<Integer, PlanoMatriculaIngressantes>();
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		if (lista != null) {
			for (Object[] obj : lista) {
				int i = 0;
				Integer id = (Integer) obj[i++];
				Integer anoPmi = (Integer) obj[i++];
				Integer periodoPmi = (Integer) obj[i++];
				String descricao = (String) obj[i++];
				Integer capacidade = (Integer) obj[i++];
				Integer idMatrizCurricular = (Integer) obj[i++];
				String nomeCurso = (String) obj[i++];
				String grau = (String) obj[i++];
				String turno = (String) obj[i++];
				String habilitacao = (String) obj[i++];
				String municipio = (String) obj[i++];
				String enfase = (String) obj[i++];
				Integer idTurma = (Integer) obj[i++];
				Integer idComponenteCurricular = (Integer) obj[i++];
				String codigoComponente = (String) obj[i++];
				String nomeComponente = (String) obj[i++];
				String codigoTurma = (String) obj[i++]; 
				Integer idDiscente = (Integer) obj[i++];
				BigInteger matricula = (BigInteger) obj[i++];
				String nomeDiscente = (String) obj[i++];
				Short status = (Short) obj[i++];
				PlanoMatriculaIngressantes pmi = mapa.get(id);
				if (pmi == null) {
					pmi = new PlanoMatriculaIngressantes(id);
					pmi.setAno(anoPmi);
					pmi.setPeriodo(periodoPmi);
					pmi.setDescricao(descricao);
					pmi.setCapacidade(capacidade);
					if (idMatrizCurricular != null)
						pmi.setMatrizCurricular(new MatrizCurricular(idMatrizCurricular));
					pmi.setTurmas(new LinkedList<Turma>());
					pmi.setDiscentesAtendidos(new LinkedList<Discente>());
					mapa.put(id, pmi);
				}
				Turma turma = new Turma(idTurma);
				turma.setDisciplina(new ComponenteCurricular(idComponenteCurricular, codigoComponente, nomeComponente));
				turma.setCodigo(codigoTurma);
				if (!pmi.getTurmas().contains(turma))
					pmi.addTurma(turma);
				if (idMatrizCurricular != null) {
					MatrizCurricular matriz = new MatrizCurricular(idMatrizCurricular);
					Curso curso = new Curso(idCurso);
					curso.setNome(nomeCurso);
					matriz.setCurso(curso);
					matriz.setGrauAcademico(new GrauAcademico(grau));
					matriz.setTurno(new Turno(turno));
					if (habilitacao != null)
						matriz.setHabilitacao(new Habilitacao(habilitacao));
					matriz.getCurso().setMunicipio(new Municipio(municipio));
					if (enfase != null) {
						Enfase e = new Enfase();
						e.setNome(enfase);
						matriz.setEnfase(e);
					}
					pmi.setMatrizCurricular(matriz);
				}
				if (idDiscente != null) {
					Discente discente = new Discente(idDiscente,(matricula != null ?  matricula.longValue() : 0), nomeDiscente);
					discente.setStatus(status);
					if (!pmi.getDiscentesAtendidos().contains(discente))
						pmi.addDiscente(discente);
				}
			}
		}
		return mapa.values();
	}
	
	/**
	 * Localiza todos os planos que possuem a turma requisitada
	 * 
	 * @param t
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<PlanoMatriculaIngressantes> findByTurma(Turma t) throws DAOException {
		String hql = "select plano from PlanoMatriculaIngressantes plano " +
				"	join plano.turmas t " +
				"	where t.id = " + t.getId();
		
		Query query = getSession().createQuery(hql);
		return query.list();
	}
}
