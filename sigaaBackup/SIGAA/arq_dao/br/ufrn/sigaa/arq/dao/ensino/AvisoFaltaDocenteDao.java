/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 11/06/2008
 *
 */		
package br.ufrn.sigaa.arq.dao.ensino;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocente;
import br.ufrn.sigaa.ensino.dominio.DadosAvisoFalta;
import br.ufrn.sigaa.ensino.graduacao.relatorios.dominio.RelatorioAutomaticoFaltaDocente;

/**
 * DAO responsável por fazer o acesso aos dados de AvisoFaltaDocente
 * 
 * @author Henrique André
 */
public class AvisoFaltaDocenteDao extends GenericSigaaDAO {

	/**
	 * Retorna uma lista de {@link AvisoFaltaDocente} de acordo com o id do {@link DadosAvisoFalta} passado.
	 * 
	 * @param idDados
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AvisoFaltaDocente> findByDadosAviso(int idDados) throws DAOException {
		
		Query q = getSession().createQuery("select aviso.observacao as observacao from AvisoFaltaDocente aviso where aviso.dadosAvisoFalta.id = :idDados");
		q.setInteger("idDados", idDados);
		q.setResultTransformer(new AliasToBeanResultTransformer(AvisoFaltaDocente.class));
		return q.list();
	}
	
	/**
	 * Todos os avisos de falta por departamento
	 * 
	 * @param idDepartamento
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<RelatorioAutomaticoFaltaDocente> findByDepartamento(int idDepartamento, int ano, int periodo) throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select p.nome as docente_nome, u.nome as unidade_nome, data_aula, count(s.id_servidor) as numero_faltas from graduacao.falta_docente ft " +
				"		inner join rh.servidor s  on (s.id_servidor = ft.id_docente) " +
				"		inner join comum.pessoa p using (id_pessoa) " +
				"		inner join comum.unidade u on (s.id_unidade = u.id_unidade) " +
				"		inner join ensino.turma t on (ft.id_turma = t.id_turma) " +
				"	where u.id_unidade = ? and t.ano = ? and t.periodo = ? " +
				"	group by p.nome, u.nome,data_aula " +
				"	order by u.nome, p.nome");
		
		return getJdbcTemplate().query(sql.toString(), new Object[] { idDepartamento, ano, periodo } ,new RowMapper(){

			public RelatorioAutomaticoFaltaDocente mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				RelatorioAutomaticoFaltaDocente rel = new RelatorioAutomaticoFaltaDocente();
				rel.getPessoa().setNome(rs.getString("docente_nome"));
				rel.getDepartamento().setNome(rs.getString("unidade_nome"));
				rel.setDataAula(rs.getDate("data_aula"));
				rel.setNumeroFaltas(rs.getLong("numero_faltas"));
				
				return rel;
			}
		});
	}

	/**
	 * Todos os avisos de falta por centro
	 * 
	 * @param idDepartamento
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<RelatorioAutomaticoFaltaDocente> findByCentro(int idCentro, int ano, int periodo) throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select p.nome as docente_nome, u.nome as unidade_nome, (select nome from unidade un where un.id_unidade = u.id_gestora) as unidade_gestora, data_aula, count(s.id_servidor) as numero_faltas, u.id_unidade, u.id_gestora from graduacao.falta_docente ft " +
				"		inner join rh.servidor s  on (s.id_servidor = ft.id_docente) " +
				"		inner join comum.pessoa p using (id_pessoa) " +
				"		inner join comum.unidade u on (s.id_unidade = u.id_unidade) " +
				"		inner join ensino.turma t on (ft.id_turma = t.id_turma) " +
				"	where u.id_gestora = ? and t.ano = ? and t.periodo = ?" +
				"	group by p.nome, u.nome,data_aula,u.id_unidade, u.id_gestora " +
				"	order by u.nome, p.nome");
		
		return getJdbcTemplate().query(sql.toString(), new Object[] { idCentro, ano, periodo } ,new RowMapper(){

			public RelatorioAutomaticoFaltaDocente mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				RelatorioAutomaticoFaltaDocente rel = new RelatorioAutomaticoFaltaDocente();
				rel.getPessoa().setNome(rs.getString("docente_nome"));
				rel.getDepartamento().setId(rs.getInt("id_unidade"));
				rel.getDepartamento().setNome(rs.getString("unidade_nome"));
				rel.getDepartamento().setGestora(new Unidade());
				rel.getDepartamento().getGestora().setId(rs.getInt("id_gestora"));
				rel.getDepartamento().getGestora().setNome(rs.getString("unidade_gestora"));
				rel.setDataAula(rs.getDate("data_aula"));
				rel.setNumeroFaltas(rs.getLong("numero_faltas"));
				
				return rel;
			}
			
		});
	}
	
}