/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 04/12/2012
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import fundacao.integracao.academico.CursoDTO;
import fundacao.integracao.comum.UnidadeDTO;

/**
 * Dao responsável pelas consultas de cursos utilizadas no serviço remoto de importação para a fundação.
 * @author Rafael Gomes
 *
 */
public class CursoFundacaoRemoteServiceDao extends GenericSigaaDAO {

	/**
 	 * Busca a lista de todos os Cursos que serão utilizados para importação no sistema da Fundação. 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CursoDTO> findAllCursoFundacao(){
		String sql = 
				"SELECT c.id_curso, c.nome AS nome_curso, c.nivel AS nivel_curso, m.nome AS nome_municipio, mod.descricao AS modalidade " +
				"	, grauStricto.descricao AS grau_stricto, grauLato.descricao AS grau_lato, grauTecnico.descricao AS grau_tecnico " +
				"	, und.id_unidade, und.codigo_unidade, und.nome AS nome_unidade, mUnidade.nome AS nome_municipio_unidade, und.unidade_responsavel, und.id_gestora " + 
				"FROM curso c " + 
				" INNER JOIN comum.municipio m ON m.id_municipio = c.id_municipio " +	
				" INNER JOIN comum.modalidade_educacao mod ON mod.id_modalidade_educacao = c.id_modalidade_educacao " +  
				" INNER JOIN comum.unidade und ON und.id_unidade = c.id_unidade " +
				" LEFT JOIN  comum.municipio mUnidade on mUnidade.id_municipio = und.id_municipio " +
				" LEFT JOIN  stricto_sensu.tipo_curso_stricto grauStricto ON grauStricto.id_tipo_curso_stricto = c.id_tipo_curso_stricto " +
				" LEFT JOIN  lato_sensu.curso_lato cLato ON cLato.id_curso = c.id_curso " + 
				" LEFT JOIN  lato_sensu.tipo_curso_lato grauLato ON grauLato.id_tipo_curso_lato = cLato.id_tipo_curso_lato " +
				" LEFT JOIN  tecnico.curso_tecnico cTecnico ON cTecnico.id_curso = c.id_curso " +
				" LEFT JOIN  tecnico.modalidade_curso_tecnico grauTecnico ON grauTecnico.id_modalidade_curso_tecnico = cTecnico.id_modalidade_curso_tecnico ";
			
		try{
			return getJdbcTemplate().query(sql, new RowMapper(){
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					CursoDTO curso = new CursoDTO();
					curso.setId(rs.getInt("id_curso"));
					curso.setNome(rs.getString("nome_curso"));
					curso.setNivel(rs.getString("nivel_curso"));
					curso.setMunicipio(rs.getString("nome_municipio"));
					curso.setModalidade(rs.getString("modalidade"));
					
					Integer idUnidade = rs.getInt("id_unidade");
					if(!isEmpty(idUnidade)){
						UnidadeDTO unidade = new UnidadeDTO();
						unidade.setId(idUnidade);
						unidade.setCodigo(rs.getString("codigo_unidade"));
						unidade.setNome(rs.getString("nome_unidade"));
						unidade.setMunicipio(rs.getString("nome_municipio_unidade"));
						unidade.setResponsavel(rs.getInt("unidade_responsavel"));
						unidade.setGestora(rs.getInt("id_gestora"));
						
						curso.setUnidade(unidade);
					}
					
					curso.setGrauAcademico(
						isEmpty(rs.getString("grau_stricto"))	? rs.getString("grau_stricto") :
						isEmpty(rs.getString("grau_lato"))		? rs.getString("grau_lato") :
						isEmpty(rs.getString("grau_tecnico")) 	? rs.getString("grau_tecnico") : 
						null);	
					return curso;
				}
			});
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<CursoDTO>();
		}
	}
}
