/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '04/10/2011'
 *
 */
package br.ufrn.sigaa.mobile.touch.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe responsável por por consultas específicas às Turmas para dispositivos móveis
 * sensíveis ao toque.
 * 
 * @author ilueny santos
 *
 */
public class TurmaTouchDao extends GenericSigaaDAO {

	
	/**
	 * Retorna todas as turmas que possuem o discente, situações de matrícula e situações da turma informados
	 *
	 * @param discente
	 * @param situacoesMatricula
	 * @param situacoesTurma
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findAllByDiscente(Discente discente, Integer ano, Integer periodo, SituacaoMatricula[] situacoesMatricula, SituacaoTurma[] situacoesTurma) throws DAOException {
		try {
			
			String projecao = "md.turma.id as turma.id," +
					" md.turma.disciplina.detalhes.nome as turma.disciplina.detalhes.nome," +
					" md.turma.disciplina.codigo as turma.disciplina.codigo," +
					" md.turma.disciplina.detalhes.chTotal as turma.disciplina.detalhes.chTotal," +
					" md.turma.disciplina.detalhes.crAula as turma.disciplina.detalhes.crAula," +
					" md.turma.disciplina.detalhes.crLaboratorio as turma.disciplina.detalhes.crLaboratorio," +
					" md.turma.disciplina.detalhes.crEstagio as turma.disciplina.detalhes.crEstagio," +
					" md.turma.disciplina.nivel as turma.disciplina.nivel," +
					" md.turma.disciplina.unidade.id as turma.disciplina.unidade.id," +
					" md.turma.descricaoHorario as turma.descricaoHorario," +
					" md.turma.distancia as turma.distancia," +
					" md.turma.ano as turma.ano," +
					" md.turma.periodo as turma.periodo," +
					" md.turma.codigo as turma.codigo," +
					" md.turma.local as turma.local," +
					" md.turma.turmaAgrupadora.id as turma.turmaAgrupadora.id";
			
			
			StringBuffer hql = new StringBuffer();
			hql.append("select ");
			hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
			hql.append(" from MatriculaComponente md where ");
			
			if (situacoesMatricula != null) {
				hql.append(" md.situacaoMatricula.id in " + gerarStringIn(situacoesMatricula) + " and ");
			}
			hql.append(" md.discente.id = :discenteId ");
			hql.append(" and md.turma.situacaoTurma.id in " + gerarStringIn(situacoesTurma));
			
			if (ano != null){
				hql.append (" and md.turma.ano = " + ano) ;
				hql.append (" and md.turma.periodo = " + periodo) ;
			}
			
			hql.append(" order by md.turma.disciplina.nivel, md.turma.ano desc, md.turma.periodo desc, md.turma.disciplina.detalhes.nome asc ");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("discenteId", discente.getId());

			@SuppressWarnings("unchecked")
			List<Turma> turmas = (List<Turma>) HibernateUtils.parseTo(q.list(), projecao, Turma.class, "turma");

			return turmas;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Busca os horários de uma turma
	 *
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<HorarioTurma> findHorariosByTurma(Turma turma) throws DAOException {
		Collection<Turma> turmas = new ArrayList<Turma>(1);
		turmas.add(turma);
		return findHorariosByTurmas(turmas);
	}


	/**
	 * Busca otimizada de horários por turma
	 *
	 * @param turmas
	 * @return
	 * @throws DAOException
	 */
	public List<HorarioTurma> findHorariosByTurmas(Collection<Turma> turmas) throws DAOException {
		if (turmas == null || turmas.size() == 0)
			return null;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT ht.dia, ht.id_horario, cc.codigo, det.nome, ht.data_inicio, ht.data_fim, h.inicio, h.fim, h.ordem, h.tipo, h.ativo ");
			sql.append("FROM  ensino.horario_turma ht, ensino.horario h, ensino.turma t, ensino.componente_curricular cc " +
					" join ensino.componente_curricular_detalhes det on cc.id_detalhe=det.id_componente_detalhes ");
			sql.append("WHERE ht.id_turma=t.id_turma and t.id_disciplina=cc.id_disciplina and ht.id_horario = h.id_horario and" +
					" ht.id_turma in " + gerarStringIn(turmas));

			st = con.prepareStatement(sql.toString());
			rs = st.executeQuery();
			ArrayList<HorarioTurma> horarios = new ArrayList<HorarioTurma>(0);
			while (rs.next()) {
				Horario horario = new Horario(rs.getInt("ID_HORARIO"));
				horario.setInicio(rs.getTime("INICIO"));
				horario.setFim(rs.getTime("FIM"));
				horario.setOrdem(rs.getShort("ORDEM"));
				horario.setTipo(rs.getShort("TIPO"));
				horario.setAtivo(rs.getBoolean("ATIVO"));
				HorarioTurma h = new HorarioTurma(horario, rs.getString("DIA").charAt(0));
				h.setTurma(new Turma());
				h.getTurma().setDisciplina(new ComponenteCurricular());
				h.getTurma().getDisciplina().setCodigo(rs.getString("CODIGO"));
				h.getTurma().getDisciplina().setNome(rs.getString("NOME"));
				h.setDataInicio(rs.getDate("DATA_INICIO"));
				h.setDataFim(rs.getDate("DATA_FIM"));
				horarios.add(h);
			}
			return horarios;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}
	
	/**
	 * Retorna as possíveis subturmas existentes da turma informada
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findSubturmasByTurmaFetchDocentes( Turma turma ) throws DAOException{
		if( turma == null || turma.getId() == 0 )
			return new ArrayList<Turma>();

		String hql = " SELECT DISTINCT t FROM Turma t LEFT JOIN FETCH t.docentesTurmas where t.turmaAgrupadora.id = :idTurma AND t.situacaoTurma.id not in "
			+ gerarStringIn( new int[] {SituacaoTurma.EXCLUIDA} ) + " ORDER BY t.codigo ";
		@SuppressWarnings("unchecked")
		List<Turma> lista = getSession().createQuery(hql).setInteger("idTurma", turma.getId()).list();
		return lista;
	}


}
