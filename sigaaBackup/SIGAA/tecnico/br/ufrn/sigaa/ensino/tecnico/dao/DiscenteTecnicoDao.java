/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/09/2006'
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * DAO para buscas relacionadas aos Discentes dos cursos técnicos.
 * @author Dalton
 *
 */
public class DiscenteTecnicoDao extends DiscenteDao {

	/**
	 * Método responsável pelo retorno de uma coleção de discente por registro de turma de entrada.
	 * @param idTE
	 * @param unid
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Discente> findByTurmaEntrada(int idTE, int unid, char nivel) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select d from DiscenteTecnico dt join dt.discente d WHERE ");
			if (unid > 0)
				hql.append("d.gestoraAcademica.id = "+unid+" and ");
			if (nivel != '0')
				hql.append("d.nivel = '"+nivel+"' and ");
			hql.append(" dt.turmaEntradaTecnico.id = " + idTE);
			hql.append(" and d.status not in "+ UFRNUtils.gerarStringIn(new int[] { StatusDiscente.EXCLUIDO }) );
			hql.append(" order by d.pessoa.nome asc");

			return getSession().createQuery(hql.toString()).list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Método responsável pelo retorno de uma coleção de discente por registro de turma de entrada com teste de matrícula do discente.
	 * @param idTE
	 * @param unid
	 * @param nivel
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteAdapter> findByTurmaEntrada(int idTE, int unid, char nivel, boolean matricula) throws DAOException {
		
		String projecao = "id, discente.id, discente.pessoa.nome, discente.matricula";
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT " + projecao + " ");
			hql.append("FROM DiscenteTecnico WHERE ");
			if (unid > 0)
				hql.append("discente.gestoraAcademica.id = "+unid+" and ");
			if (nivel != '0')
				hql.append("discente.nivel = '"+nivel+"' and ");
			hql.append(" turmaEntradaTecnico.id = " + idTE);
			if (matricula)
				hql.append(" and discente.status in "+ UFRNUtils.gerarStringIn(new int[] { StatusDiscente.ATIVO, StatusDiscente.CADASTRADO }) );
			hql.append(" order by discente.pessoa.nome asc");
			
			Query q = getSession().createQuery(hql.toString());
			Collection<DiscenteAdapter> lista = HibernateUtils.parseTo(q.list(), projecao, DiscenteTecnico.class);
			
			return lista;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Carrega os discente já matriculados em uma turma, para auxiliar no cadastro dos discentes de uma turma de entrada em uma turma. 
	 * 
	 * 
	 * @param idTE
	 * @param ano
	 * @param periodo
	 * @param situacaoMatricula
	 * @param unid
	 * @param nivel
	 * @param idComponente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteAdapter> findByDiscentesMatriculados(int idTE, int situacaoMatricula, int unid, char nivel, int idComponente) throws DAOException {
			String sql = "select dt.id_discente" +
					" FROM tecnico.discente_tecnico dt" +
					" JOIN discente d using ( id_discente ) " +
					" JOIN comum.pessoa p using ( id_pessoa )" +
					" join ensino.matricula_componente mc using ( id_discente )" +
					" where dt.id_turma_entrada = " + idTE +
					" and mc.id_situacao_matricula = " + situacaoMatricula +
					" and mc.id_componente_curricular =  " +  idComponente +
					" and d.status in " + UFRNUtils.gerarStringIn(new int[]{ StatusDiscente.ATIVO, StatusDiscente.CADASTRADO })  +
					" and d.id_gestora_academica = " + unid +
					" and d.nivel = '" + nivel + "'";
			
			List<DiscenteAdapter> discentes = getJdbcTemplate().query(sql, new Object[] {}, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					DiscenteTecnico dt = new DiscenteTecnico();
					dt.setId(rs.getInt("id_discente"));
					return dt;
				}
			});

			return discentes;
	}
	
	/**
	 * Método responsável por retornar as disciplinas pertencentes a estrutura curricular do curso técnico.
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public TreeSet<Integer> findComponentesDoCursoByDiscente(int curso)	throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT md.id_disciplina as disciplina_modulo, dc.id_disciplina as disciplina_complementar "
					+ " FROM tecnico.estrutura_curricular_tecnica ect " +
							"left outer join tecnico.modulo_curricular mc on (ect.id_estrutura_curricular=mc.id_estrutura_curricular) " +
							"left outer join tecnico.modulo_disciplina md on (mc.id_modulo=md.id_modulo) " +
							"left outer join tecnico.disciplina_complementar dc on (ect.id_estrutura_curricular=dc.id_estrutura_curricular) "
					+ " WHERE ect.id_curso = ? "
					+ " AND ect.ativa = trueValue() ";
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, curso);
			rs = st.executeQuery();
		
			TreeSet<Integer> componentes = new TreeSet<Integer>();
			while (rs.next()) {
				Integer disciplinaModulo = rs.getInt("disciplina_modulo");
				Integer disciplinaComplementar = rs.getInt("disciplina_complementar");
				if(disciplinaModulo != null)
					componentes.add(disciplinaModulo);
				if(disciplinaComplementar != null)
					componentes.add(disciplinaComplementar);
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
	 * Atualiza os totais de integralização de um determinado discente 
	 * ATENÇÃO: Deve ser chamado somente a partir de um processador!
	 * 
	 * @param d
	 */
	public void atualizaTotaisIntegralizados(DiscenteTecnico d) {
		update("update tecnico.discente_tecnico set " +
				"	ch_optativa_integralizada=?, " +
				"	ch_optativa_pendente=?," +
				"	ch_obrigatoria_integralizada=?," +
				"	ch_obrigatoria_pendente=?" +
				"where id_discente=?",
				new Object[] {
					d.getChOptativaIntegralizada(), 
					d.getChOptativaPendente(),
					d.getChObrigatoriaIntegralizada(),
					d.getChObrigatoriaPendente(),
					d.getId() } );
	}
	
	/**
	 * Traz os componentes pendentes de um aluno do Técnico sem calcular os componentes equivalentes já pagos.
	 * 
	 * @param id
	 * @param disciplinas - Disciplinas que ele já pagou
	 * @param equivalenciasDiscente
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ComponenteCurricular> findComponentesPendentesTecnicoSemEquivalentes(int id, List<MatriculaComponente> disciplinas) {
		String sql = "select cc.id_disciplina, cc.qtd_max_matriculas, ccd.codigo, ccd.nome, ccd.ch_total, ccd.equivalencia from tecnico.estrutura_curricular_tecnica  ect, tecnico.modulo_curricular mc, "
				+ "tecnico.modulo_disciplina md, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd, discente d, tecnico.discente_tecnico dt "
				+ "where d.id_discente=dt.id_discente and dt.id_estrutura_curricular=ect.id_estrutura_curricular and ect.id_estrutura_curricular=mc.id_estrutura_curricular "
				+ "and mc.id_modulo = md.id_modulo and ect.ativa = trueValue() and md.id_disciplina = cc.id_disciplina "
				+ "and cc.id_detalhe = ccd.id_componente_detalhes and d.id_discente=?";

		final List<Integer> idsDisciplinas = new ArrayList<Integer>();
		final List<Integer> idsMatriculadas = new ArrayList<Integer>();
		for (MatriculaComponente mc : disciplinas) {
			if (mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVADO)
					|| mc.getSituacaoMatricula().equals(
							SituacaoMatricula.APROVEITADO_TRANSFERIDO)
					|| mc.getSituacaoMatricula().equals(
							SituacaoMatricula.APROVEITADO_CUMPRIU)
					|| mc.getSituacaoMatricula().equals(
							SituacaoMatricula.APROVEITADO_DISPENSADO)) {
				idsDisciplinas.add(mc.getComponente().getId());
			} else if (mc.getSituacaoMatricula().equals(
					SituacaoMatricula.MATRICULADO)
					|| mc.getSituacaoMatricula().equals(
							SituacaoMatricula.EM_ESPERA)) {
				idsMatriculadas.add(mc.getComponente().getId());
			}
		}

		final List<ComponenteCurricular> pendentes = new ArrayList<ComponenteCurricular>();
		getJdbcTemplate().query(sql, new Object[] { id }, new RowMapper() {			/** Mapeia o resultado da busca para um objeto ComponenteCurricular
			 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
			 */
			public Object mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setId(rs.getInt("id_disciplina"));
				cc.setQtdMaximaMatriculas(rs.getInt("qtd_max_matriculas"));
				cc.setCodigo(rs.getString("codigo"));
				cc.setDetalhes(new ComponenteDetalhes());
				cc.getDetalhes().setCodigo(cc.getCodigo());
				cc.getDetalhes().setNome(rs.getString("nome"));
				cc.getDetalhes().setChTotal(rs.getInt("ch_total"));
				cc.getDetalhes().setEquivalencia(rs.getString("equivalencia"));

				// Se a disciplina pendente esta na lista de matricula, então marca como matriculado
				if (idsMatriculadas.contains(cc.getId())) {
					cc.setMatriculado(true);
					} 
				if (!idsDisciplinas.contains(cc.getId()))
					pendentes.add(cc);
				else if (Collections.frequency(idsDisciplinas, cc.getId()) < cc.getQtdMaximaMatriculas()) 
					pendentes.add(cc);		 
				
				return cc;
			}
		});
		
		return pendentes;
	}
}