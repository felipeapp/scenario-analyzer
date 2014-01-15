/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/11/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.Curso;
import br.ufrn.academico.dominio.ProgramaEducacaoTutorial;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * DAO para consultas de Grupos PET
 * 
 * @author wendell
 *
 */
public class ProgramaEducacaoTutorialDao extends GenericSigaaDAO {

	/**
	 * Buscar todos os programas ativos
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProgramaEducacaoTutorial> findAllAtivos() {
		RowMapper petMapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProgramaEducacaoTutorial pet = new ProgramaEducacaoTutorial();
				pet.setId( rs.getInt("id_pet") );
				pet.setDescricao( rs.getString("descricao"));
				
				Integer idCurso = (Integer) rs.getObject("id_curso");
				if (idCurso != null) {
					Curso curso = new Curso();
					curso.setId( idCurso );
					curso.setNome( rs.getString("nome_curso") );
					pet.setCurso(curso);
					pet.setIdCurso(idCurso);
				}
				
				Integer idAreaConhecimentoCnpq = (Integer) rs.getObject("id_area_conhecimento_cnpq");
				if (idAreaConhecimentoCnpq != null) {
					pet.setIdAreaConhecimentoCnpq(idAreaConhecimentoCnpq);
					pet.setNomeAreaConhecimentoCnpq(rs.getString("nome_area"));
				}
				
				return pet;
			}
		};
		
		return getJdbcTemplate().query("select p.id_pet, p.descricao, " +
				"	c.id_curso, c.nome as nome_curso, " +
				"	a.id_area_conhecimento_cnpq, a.nome as nome_area " +
				" from prodocente.pet p " +
				" left join curso c using(id_curso) " +
				" left join comum.area_conhecimento_cnpq a using(id_area_conhecimento_cnpq) " +
				" where p.ativo = trueValue() ", petMapper);
	}
	
}
