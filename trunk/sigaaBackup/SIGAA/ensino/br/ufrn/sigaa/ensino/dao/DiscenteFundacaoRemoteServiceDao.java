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
import fundacao.integracao.academico.DiscenteDTO;
import fundacao.integracao.comum.UnidadeDTO;

/**
 * Dao responsável pelas consultas de discentes utilizadas no serviço remoto para operações de importação.
 * @author Rafael Gomes
 *
 */
public class DiscenteFundacaoRemoteServiceDao extends GenericSigaaDAO {

	/**
 	 * Busca a lista de todos os Discente que serão utilizados para importação no sistema da Fundação. 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DiscenteDTO> findAllDiscenteFundacao(){
		String sql = 
				"SELECT d.id_discente, d.matricula, p.cpf_cnpj AS cpf, p.passaporte, p.nome AS nome_discente, p.data_nascimento, p.sexo " +
				"   , p.ultima_atualizacao AS ultima_atualizacao_pessoa, p.data_cadastro AS data_cadastro_pessoa "+
				"   , d.status, d.nivel AS nivel_discente " +
				"   , coalesce( d.data_cadastro, p.data_cadastro) AS data_cadastro_discente " +
				"   , coalesce((select max(data) from comum.alteracao_status_aluno where id_discente = d.id_discente), d.data_cadastro) AS data_alteracao_status " +
				"	, c.id_curso, c.nome AS nome_curso, c.nivel AS nivel_curso, m.nome AS nome_municipio_curso, mod.descricao AS modalidade "+
				"	, grau.descricao AS grau_graduacao, grauStricto.descricao AS grau_stricto, grauLato.descricao AS grau_lato, grauTecnico.descricao AS grau_tecnico "+
				"	, und.id_unidade, und.codigo_unidade, und.nome AS nome_unidade, mUnidade.nome AS nome_municipio_unidade, und.unidade_responsavel, und.id_gestora "+
				" FROM discente d "+
				"  INNER JOIN comum.pessoa p ON p.id_pessoa = d.id_pessoa "+  
				"  INNER JOIN curso c ON c.id_curso = d.id_curso "+
				"  INNER JOIN comum.municipio m ON m.id_municipio = c.id_municipio "+	
				"  INNER JOIN comum.modalidade_educacao mod ON mod.id_modalidade_educacao = c.id_modalidade_educacao "+  
				"  INNER JOIN comum.unidade und ON und.id_unidade = c.id_unidade "+
				"  LEFT JOIN  comum.municipio mUnidade on mUnidade.id_municipio = und.id_municipio "+
				"  LEFT JOIN  graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente "+
				"  LEFT JOIN  graduacao.matriz_curricular mc ON mc.id_matriz_curricular = dg.id_matriz_curricular "+
				"  LEFT JOIN  ensino.grau_academico grau ON grau.id_grau_academico = mc.id_grau_academico "+
				"  LEFT JOIN  stricto_sensu.tipo_curso_stricto grauStricto ON grauStricto.id_tipo_curso_stricto = c.id_tipo_curso_stricto "+
				"  LEFT JOIN  lato_sensu.curso_lato cLato ON cLato.id_curso = c.id_curso "+
				"  LEFT JOIN  lato_sensu.tipo_curso_lato grauLato ON grauLato.id_tipo_curso_lato = cLato.id_tipo_curso_lato "+
				"  LEFT JOIN  tecnico.curso_tecnico cTecnico ON cTecnico.id_curso = c.id_curso "+
				"  LEFT JOIN  tecnico.modalidade_curso_tecnico grauTecnico ON grauTecnico.id_modalidade_curso_tecnico = cTecnico.id_modalidade_curso_tecnico "+
				" WHERE ( p.cpf_cnpj IS NOT NULL  OR  p.passaporte IS NOT NULL )";
			
		try{
			return getJdbcTemplate().query(sql, new RowMapper(){
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					DiscenteDTO d = new DiscenteDTO();
					d.setIdDiscente(rs.getInt("id_discente"));
					d.setMatricula(rs.getString("matricula"));
					d.setCpf(rs.getLong("cpf"));
					d.setPassaporte(rs.getString("passaporte"));
					d.setNome(rs.getString("nome_discente"));
					d.setDataNascimento(rs.getDate("data_nascimento"));
					d.setSexo(rs.getString("sexo").charAt(0)); 
					d.setStatus(rs.getInt("status"));
					d.setNivel(rs.getString("nivel_discente"));
					d.setDataCadastro(rs.getDate("data_cadastro_discente"));
					d.setDataAlteracaoStatus(rs.getDate("data_alteracao_status"));
					d.setUltimaAtualizacao(rs.getDate("ultima_atualizacao_pessoa"));
					
					Integer idCurso = rs.getInt("id_curso");
					if(!isEmpty(idCurso)){
						CursoDTO curso = new CursoDTO();
						curso.setId(idCurso);
						curso.setNome(rs.getString("nome_curso"));
						curso.setNivel(rs.getString("nivel_curso"));
						curso.setMunicipio(rs.getString("nome_municipio_curso"));
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
								isEmpty(rs.getString("grau_graduacao")) ? rs.getString("grau_graduacao") : 
								isEmpty(rs.getString("grau_stricto")) 	? rs.getString("grau_stricto") :
								isEmpty(rs.getString("grau_lato")) 		? rs.getString("grau_lato") :
								isEmpty(rs.getString("grau_tecnico")) 	? rs.getString("grau_tecnico") : 
								null);	
						
						d.setCurso(curso);
					}
					return d;
				}
			});
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<DiscenteDTO>();
		}
	}
}
