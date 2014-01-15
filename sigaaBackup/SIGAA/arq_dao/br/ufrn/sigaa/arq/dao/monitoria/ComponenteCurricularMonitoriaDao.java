/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/03/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;

/**
 * DAO responsável por realizar consultas sobre a entidade ComponenteCurricularMonitoria
 * 
 * @author Victor Hugo
 *
 */
public class ComponenteCurricularMonitoriaDao extends GenericSigaaDAO {

	/**
	 * este método retorna a quantidade de disciplinas obrigatórias de terem sido cursadas pelos alunos que 
	 * serão monitores do projeto de monitoria da prova passada por parâmetro
	 *  
	 * @param projeto
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int findQtdComponentesMonitoriaObrigatoriasByProva( int idProvaSeletiva, boolean obrigatorio ) throws HibernateException, DAOException{
		
		String hql = " select count( distinct p.id ) from ProvaSelecaoComponenteCurricular p " +
				" where p.provaSelecao.id = :idProva and p.obrigatorio = :obrigatorio";
		
		Query query = getSession().createQuery(hql);
		
		query.setInteger( "idProva", idProvaSeletiva );
		query.setBoolean( "obrigatorio" , obrigatorio);
		
		return ( (Number) query.uniqueResult() ).intValue();
		
	}
	
	/**
	 * Retorna uma coleção de componentes curriculares da prova que são obrigatórios de serem cursadas
	 * pelos candidatos a monitores, mas que não foram cursadas pelo discente informado.
	 * 
	 * @param prova
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findDisciplinasProvaNaoCursadas(
			ProvaSelecao prova, DiscenteAdapter discente) throws DAOException {

		String sql = "SELECT cc.id_disciplina, cc.codigo, ccd.nome, ccd.equivalencia "
			+ " FROM monitoria.prova_selecao_componente_curricular pscc "
			+ " JOIN monitoria.componente_curricular_monitoria ccm ON pscc.id_componente_curricular_monitoria = ccm.id_componente_curricular_monitoria "
			+ " JOIN ensino.componente_curricular cc ON cc.id_disciplina = ccm.id_disciplina "
			+ " JOIN ensino.componente_curricular_detalhes ccd ON cc.id_detalhe = ccd.id_componente_detalhes "
			
			+ " WHERE pscc.id_prova_selecao = ? "		
			+ " AND cc.id_disciplina NOT IN ( SELECT mc.id_componente_curricular " 
											+ " FROM ensino.matricula_componente mc " 
											+		" WHERE mc.id_discente = ? " +
														" AND " +
														" ( (mc.id_situacao_matricula IN (?, ?, ?)) OR" +
														"   (mc.id_situacao_matricula = ? AND mc.media_final >= ?))" 
										 + ")";

		Connection con = null;

		try {
			con = Database.getInstance().getSigaaConnection();

			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, prova.getId());
			st.setInt(2, discente.getId());
			st.setInt(3, SituacaoMatricula.APROVEITADO_CUMPRIU.getId());
			st.setInt(4, SituacaoMatricula.APROVEITADO_DISPENSADO.getId());
			st.setInt(5, SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId());
			st.setInt(6, SituacaoMatricula.APROVADO.getId());
			st.setDouble(7, prova.getProjetoEnsino().getEditalMonitoria().getNotaMinimaAprovacaoSelecaoMonitora());

			List<ComponenteCurricular> result = new ArrayList<ComponenteCurricular>();
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				ComponenteCurricular cc = new ComponenteCurricular(rs
						.getInt("id_disciplina"), rs.getString("codigo"), rs
						.getString("nome"));
				cc.setEquivalencia(rs.getString("equivalencia"));
				result.add(cc);
			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			Database.getInstance().close(con);
		}

	}


	/**
	 * Verifica se a prova seletiva possui algum componente curricular
	 * sem Turmas criadas na situação informada, ou seja, verifica se a prova
	 * é composta de algum componente curricular novo.
	 *  
	 * @param projeto
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean isProvaPossuiAlgumComponenteSemTurmaBySituacao(int idProvaSeletiva, int[] situacoesTurma) throws HibernateException, DAOException{
		try {
			String hql = "SELECT COUNT(*) " 
			+ "	FROM ProvaSelecaoComponenteCurricular pscc " +
					"WHERE pscc.provaSelecao.id = :idProva " +
					"AND (SELECT COUNT(*) FROM Turma t " +
							" WHERE t.disciplina.id = pscc.componenteCurricularMonitoria.disciplina.id" +
								" AND t.situacaoTurma.id IN " + gerarStringIn(situacoesTurma) + ") = 0";

            Query q = getSession().createQuery(hql);
            q.setInteger("idProva", idProvaSeletiva);
            return ((Long) q.uniqueResult()).intValue() > 0;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
		
	}
	
	
	/***
	 * Verifica quais os componentes curriculares pagos pelo discente com média 
	 * satisfatória para a prova seletiva informada.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesCurricularesPagos(DiscenteAdapter discente, ProvaSelecao prova) throws DAOException {
		try {
			String hql = "SELECT mc.componente.id, mc.componente.detalhes.equivalencia, mc.componente.detalhes.nome" 
					+ " FROM MatriculaComponente mc "
					+ " WHERE mc.discente.id = ? " 
					+ " AND ( " +
							"(mc.situacaoMatricula.id IN (?, ?, ?)) " 
					+  	"  OR (mc.situacaoMatricula.id = ? AND mc.mediaFinal >= ?))"; 
			
			Query q = getSession().createQuery(hql);
			q.setInteger(0, discente.getDiscente().getId());
			q.setInteger(1, SituacaoMatricula.APROVEITADO_CUMPRIU.getId());
			q.setInteger(2, SituacaoMatricula.APROVEITADO_DISPENSADO.getId());
			q.setInteger(3, SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId());
			q.setInteger(4, SituacaoMatricula.APROVADO.getId());
			q.setDouble(5, prova.getProjetoEnsino().getEditalMonitoria().getNotaMinimaAprovacaoSelecaoMonitora());
			
			@SuppressWarnings("unchecked")
			Collection<Object[]> res = q.list();
			Collection<ComponenteCurricular> ccs = new ArrayList<ComponenteCurricular>(0);
			if (res != null) {
				for (Object[] reg : res) {
					ComponenteCurricular cmp = new ComponenteCurricular(
							(Integer) reg[0]);
					cmp.setEquivalencia((String) reg[1]);
					cmp.setNome((String) reg[2]);
					ccs.add(cmp);
				}
			}
			return ccs;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
}
