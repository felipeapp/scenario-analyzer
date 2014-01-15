/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/09/2006
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Dao responsável pelas consultas relativas a horários.
 * 
 * @author Gleydson
 *
 */
public class HorarioDao extends GenericSigaaDAO {

	
	/**
	 * Retorna todos os horários ativos de uma dada unidade. Se não for informada
	 * unidade, busca todos os horarios de um dado nivel de ensino.
	 *
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<Horario> findAtivoByUnidade(int unidade, char nivel) throws DAOException {
		if (NivelEnsino.isAlgumNivelStricto(nivel)) {
			nivel = NivelEnsino.STRICTO;
		}
		Criteria c = getSession().createCriteria(Horario.class);
		if (unidade > 0)
			c.add(Restrictions.eq("unidade", new Unidade(unidade)));
		c.add(Restrictions.eq("nivel", nivel));
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		c.addOrder(Order.asc("inicio"));

		@SuppressWarnings("unchecked")
		Collection<Horario> lista = c.list();
		return lista;

	}
	
	/** Retorna todos os horários ativos de uma dada unidade. Se não for informada
	 * unidade, busca todos os horarios de um dado nivel de ensino.
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<Horario> findAtivoByUnidade(Unidade unidade, char nivel) throws DAOException {
		if (unidade != null)
			return findAtivoByUnidade(unidade.getId(), nivel);
		else
			return findAtivoByUnidade(0, nivel);
	}

	/** Retorna todos os horários ativos de uma dada unidade. Se não for informada
	 * unidade, busca todos os horarios de um dado nivel de ensino.
	 * 
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<Horario> findByUnidadeOtimizado(int unidade, char nivel) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT id_horario, inicio, fim, ordem, tipo, ativo" +
					" FROM ensino.horario" +
					" WHERE ativo = trueValue()");
			if(NivelEnsino.isValido(nivel))  {
				if (NivelEnsino.getNiveisStricto().contains(nivel)) {
					nivel = NivelEnsino.STRICTO;
				}
				sql.append(" AND  nivel='"+nivel+"'");
			}
			if(unidade > 0)
				sql.append(" AND id_unidade="+unidade);
			sql.append(" order by inicio");
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql.toString());
			rs = st.executeQuery();

			List<Horario> horarios = new ArrayList<Horario>(0);
			while(rs.next()) {
				Horario h = new Horario(rs.getInt("ID_HORARIO"));
				h.setInicio(rs.getTime("INICIO"));
				h.setFim(rs.getTime("FIM"));
				h.setOrdem(rs.getShort("ORDEM"));
				h.setTipo(rs.getShort("TIPO"));
				h.setAtivo(rs.getBoolean("ATIVO"));
				horarios.add(h);
			}

			return horarios;
		} catch(Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/** Retorna o horário correspondente ao turno/ordem especificados.
	 * @param unidade
	 * @param nivel
	 * @param turno
	 * @param ordem
	 * @return
	 * @throws DAOException
	 */
	public Horario findByUnidade(int unidade, char nivel, char turno, char ordem) throws DAOException {
		short tipo = Horario.MANHA;
		if (turno == 'T')
			tipo = Horario.TARDE;
		else if (turno == 'N')
			tipo = Horario.NOITE;

		Criteria c = getSession().createCriteria(Horario.class);
		c.add(Restrictions.eq("unidade", new Unidade(unidade)));
		c.add(Restrictions.eq("nivel", nivel));
		c.add(Restrictions.eq("tipo", tipo));
		c.add(Restrictions.eq("ordem", new Short(String.valueOf(ordem))));
		c.add(Restrictions.eq("ativo", true));
		return (Horario) c.uniqueResult();
	}

	/** Verifica se o horário especificado choca com outro previamente cadastrado. 
	 * @param horario
	 * @return
	 * @throws DAOException
	 */
	public boolean verificaChoqueHorario(Horario horario) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Horario.class);
			c.add(Restrictions.ne("id", horario.getId()));
			c.add(Restrictions.eq("unidade", horario.getUnidade()));
			c.add(Restrictions.eq("nivel", horario.getNivel()));
			c.add(Restrictions.eq("ativo", Boolean.TRUE));
			c.add(Restrictions.lt("inicio", horario.getFim()));
			c.add(Restrictions.gt("fim", horario.getInicio()));
			c.setProjection(Projections.property("id"));
			return c.list() != null && c.list().size() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}

	}

	/** Indica se permite a alteração no horário, isto é, se não existe turma com o horário especificado.
	 * @param horario
	 * @return
	 * @throws DAOException
	 */
	public boolean permiteAlteracao(Horario horario) throws DAOException {
		Criteria c = getSession().createCriteria(HorarioTurma.class);
		c.add(Restrictions.eq("horario", horario));
		c.setProjection(Projections.property("id"));
		@SuppressWarnings("unchecked")
		Collection<HorarioTurma> res = c.list();
		return  res == null || res.isEmpty();
	}

	/** Retorna uma lista de horários de turmas de acordo com as matrículas especificadas.
	 * @param matriculas
	 * @return
	 * @throws DAOException
	 */
	public List<HorarioTurma> findByMatriculas(List<MatriculaComponente> matriculas) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			Collection<Turma> turmas = new ArrayList<Turma>(0);
			for (MatriculaComponente mc : matriculas) {
				if (mc.getTurma() != null)
					turmas.add(mc.getTurma());
			}
			if (turmas.isEmpty())  {
				return null;
			}
			String sql = "SELECT ht.dia, ht.id_horario, cc.codigo," +
					" h.inicio, h.fim, h.ordem, h.tipo, h.ativo" +
					" FROM ensino.horario_turma ht " +
					" inner join ensino.turma t using (id_turma) " +
					" inner join ensino.componente_curricular cc using (id_disciplina) " +
					" inner join ensino.horario h using (id_horario) " +
					" WHERE  ht.id_turma in " + gerarStringIn(turmas) ;

			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			rs = st.executeQuery();

			List<HorarioTurma> horarios = new ArrayList<HorarioTurma>(0);
			while(rs.next()) {
				HorarioTurma ht = new HorarioTurma();
				Horario horario = new Horario(rs.getInt("ID_HORARIO"));
				horario.setInicio(rs.getTime("INICIO"));
				horario.setFim(rs.getTime("FIM"));
				horario.setOrdem(rs.getShort("ORDEM"));
				horario.setTipo(rs.getShort("TIPO"));
				horario.setAtivo(rs.getBoolean("ATIVO"));
				ht.setHorario(horario);
				if (rs.getString("DIA") != null)
					ht.setDia(rs.getString("DIA").charAt(0));
				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setCodigo(rs.getString("CODIGO"));
				Turma turma = new Turma();
				turma.setDisciplina(cc);
				ht.setTurma(turma);
				horarios.add(ht);
			}

			return horarios;
		} catch(Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

}
