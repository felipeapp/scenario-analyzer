package br.ufrn.sigaa.arq.dao.prodocente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.hibernate.HibernateException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.prodocente.atividades.dominio.ProgramaResidenciaMedica;

/**
 * DAO para busca dos programas de Residência Médica.
 * 
 * @author Jean Guerethes
 */
public class ProgramaResidenciaMedicaDao extends GenericSigaaDAO {

	/**
	 * Retorna o programa de Residência Médica, com o id informado.
	 */
	public ProgramaResidenciaMedica findById(Integer idProgramaResidenciaMedica) throws DAOException {
		String sql = " select prm.id_programa_residencia_medica, prm.nome, prm.id_hospital, " +
					 " prm.ativo, prm.id_unidade_programa " +
					 " from prodocente.programa_residencia_medica prm " +
					 " where prm.id_programa_residencia_medica = ?";

		 	return (ProgramaResidenciaMedica) getJdbcTemplate().queryForObject(sql, new Object[] { idProgramaResidenciaMedica }, 
				new RowMapper(){
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				
				ProgramaResidenciaMedica programa = new ProgramaResidenciaMedica();
				programa.setId(rs.getInt("id_programa_residencia_medica"));
				programa.setNome(rs.getString("nome"));
				programa.setHospital( new Unidade(rs.getInt("id_hospital")) );
				programa.setAtivo(rs.getBoolean("ativo"));
				programa.setUnidadePrograma( new Unidade(rs.getInt("id_unidade_programa")) );
				return programa;
			}
		});
	}
	
	/**
	 * Retorna todos os programa que o servidor é o coordenador.
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProgramaResidenciaMedica> findAllProgramaCoordenador(int servidor) throws HibernateException, DAOException{
		String sql = "select prm.id_programa_residencia_medica, prm.nome "+
					 "from ensino.coordenacao_curso cc "+
					 "inner join prodocente.programa_residencia_medica prm on (cc.id_unidade = prm.id_unidade_programa) "+
					 "inner join comum.unidade u on (cc.id_unidade = u.id_unidade) "+
					 "where cc.id_servidor = ? " +
					 "and cc.ativo = trueValue() and cc.data_inicio_mandato <= ? "+
					 "and cc.data_fim_mandato >= ?" +
					 "and prm.ativo = ?";

		return getJdbcTemplate().query(sql, new Object[] { servidor, new Date(), new Date(), Boolean.TRUE}, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProgramaResidenciaMedica programa = new ProgramaResidenciaMedica();
				programa.setId(rs.getInt("id_programa_residencia_medica"));
				programa.setNome(rs.getString("nome"));
				
				return programa;
			}
			
		});
	}
}