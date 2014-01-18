/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 25/06/2013
 * Autor: Diego Jácome
 */
package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.integracao.dto.dados_discentes.DiscenteDTO;
import br.ufrn.integracao.dto.dados_discentes.PessoaDTO;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ConstantesTipoMovimentacaoAluno;
import fundacao.integracao.academico.CursoDTO;
import fundacao.integracao.comum.UnidadeDTO;

/**
 * Dao responsável pelas consultas de discentes utilizadas no serviço remoto para operações de busca de discentes.
 * @author Diego Jácome
 *
 */
public class DadosDiscentesRemoteServiceDao extends GenericSigaaDAO {

	/**
 	 * Busca a lista de todos os Cursos que serão utilizados para importação no sistema da Fundação. 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PessoaDTO> findDadosDiscentesByIdsPessoa(List<Integer> idsPessoa){
		
		ArrayList<Integer> movimentosFinalizados = new ArrayList();
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.CONCLUSAO);
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.ABANDONO);
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.ABANDONO_NENHUMA_MATRICULA);
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.ABANDONO_NENHUMA_INTEGRALIZACAO);
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.PRAZO_MAXIMO);
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.NAO_CONFIRMACAO_VINCULO);
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.EXCLUIDO);
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.CANCELAMENTO_POR_UPGRADE_NIVEL);
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.INTEGRALIZACAO_DE_DISCENTE);
		movimentosFinalizados.add(ConstantesTipoMovimentacaoAluno.CANCELAMENTO_POR_EFETIVACAO_NOVO_CADASTRO);
		
		String sql = 
				" select distinct d.id_discente, d.matricula, d.status, d.nivel, d.ano_ingresso, d.periodo_ingresso, d.data_cadastro, d.data_colacao_grau, ma.ano_referencia, ma.periodo_referencia,"+
				" p.id_pessoa, p.nome as nome_discente, p.cpf_cnpj, p.passaporte, p.data_nascimento, p.sexo, "+
				" c.id_curso, c.nome as nome_curso, c.nivel as nivel_curso, m.nome as nome_municipio, me.descricao as modalidade, "+
				" u.id_unidade, u.nome as nome_unidade, u.codigo_unidade, u.unidade_responsavel, u.id_gestora, mu.nome as nome_municipio_unidade, "+ 
				" grau.descricao AS grau_graduacao, grauStricto.descricao AS grau_stricto, grauLato.descricao AS grau_lato, grauTecnico.descricao AS grau_tecnico "+
				" from discente d "+
				" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa "+
				" left join ensino.movimentacao_aluno ma on ma.id_discente = d.id_discente and ma.ativo = trueValue() and ma.id_tipo_movimentacao_aluno in "+UFRNUtils.gerarStringIn(movimentosFinalizados)+" "+
				" inner join curso c on c.id_curso = d.id_curso "+
				" left join comum.municipio m on m.id_municipio = c.id_municipio "+
				" left join comum.modalidade_educacao me on me.id_modalidade_educacao = c.id_modalidade_educacao "+
				" left join  graduacao.discente_graduacao dg ON dg.id_discente_graduacao = d.id_discente "+
				" left join  graduacao.matriz_curricular mc ON mc.id_matriz_curricular = dg.id_matriz_curricular "+ 
				" left join  ensino.grau_academico grau ON grau.id_grau_academico = mc.id_grau_academico "+
				" left join  stricto_sensu.tipo_curso_stricto grauStricto ON grauStricto.id_tipo_curso_stricto = c.id_tipo_curso_stricto "+
				" left join  lato_sensu.curso_lato cLato ON cLato.id_curso = c.id_curso "+
				" left join  lato_sensu.tipo_curso_lato grauLato ON grauLato.id_tipo_curso_lato = cLato.id_tipo_curso_lato "+
				" left join  tecnico.curso_tecnico cTecnico ON cTecnico.id_curso = c.id_curso "+
				" left join  tecnico.modalidade_curso_tecnico grauTecnico ON grauTecnico.id_modalidade_curso_tecnico = cTecnico.id_modalidade_curso_tecnico  "+
				" left join comum.unidade u on u.id_unidade = c.id_unidade "+
				" left join comum.municipio mu on mu.id_municipio = u.id_municipio "+
				" where c.nivel='G' and p.id_pessoa in "+UFRNUtils.gerarStringIn(idsPessoa)+
				" order by p.id_pessoa, d.ano_ingresso, d.periodo_ingresso ";
			
		try{
			return (List<PessoaDTO>) getJdbcTemplate().query(sql, new ResultSetExtractor(){
				public Object extractData(ResultSet rs) throws SQLException {
					
					ArrayList<PessoaDTO> pessoas = new ArrayList<PessoaDTO>();
					ArrayList<DiscenteDTO> discentes = new ArrayList<DiscenteDTO>();
					int idAntigo = 0;
					int idNovo = 0;
					
					while(rs.next()) {
						
						Integer idPessoa = rs.getInt("id_pessoa");
						idNovo = idPessoa != null ? idPessoa : 0;
											
						DiscenteDTO discente = new DiscenteDTO();
						discente.setIdDiscente(rs.getInt("id_discente"));
						discente.setMatricula(rs.getString("matricula"));
						discente.setStatus(rs.getInt("status"));
						discente.setNivel(rs.getString("nivel"));
						discente.setNome(rs.getString("nome_discente"));
						discente.setCpf(rs.getLong("cpf_cnpj"));
						discente.setPassaporte(rs.getString("passaporte"));
						discente.setDataNascimento(rs.getDate("data_nascimento"));
						discente.setSexo(rs.getString("sexo").charAt(0));
						discente.setAnoIngresso(rs.getInt("ano_ingresso"));
						discente.setPeriodoIngresso(rs.getInt("periodo_ingresso"));
						discente.setDataColacaoGrau(rs.getDate("data_colacao_grau"));
						discente.setAnoSaida(rs.getInt("ano_referencia"));
						discente.setPeriodoSaida(rs.getInt("periodo_referencia"));
						
						CursoDTO curso = new CursoDTO();
						curso.setId(rs.getInt("id_curso"));
						curso.setNome(rs.getString("nome_curso"));
						curso.setNivel(rs.getString("nivel_curso"));
						curso.setMunicipio(rs.getString("nome_municipio"));
						curso.setModalidade(rs.getString("modalidade"));
						
						discente.setCurso(curso);
						
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
							!isEmpty(rs.getString("grau_graduacao"))	? rs.getString("grau_graduacao") :
							!isEmpty(rs.getString("grau_stricto"))	? rs.getString("grau_stricto") :
							!isEmpty(rs.getString("grau_lato"))		? rs.getString("grau_lato") :
							!isEmpty(rs.getString("grau_tecnico")) 	? rs.getString("grau_tecnico") : 
							null);	
						
						if (idAntigo != idNovo) {
							discentes = new ArrayList<DiscenteDTO>();
							discentes.add(discente);

							PessoaDTO pessoa = new PessoaDTO();
							pessoa.setIdPessoa(idPessoa);
							pessoa.setDiscentes(discentes);
							pessoas.add(pessoa);
						
						} else {
							discentes.add(discente);
						}	

						idAntigo = idNovo;
					}
					return pessoas;
				}
			});
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<PessoaDTO>();
		}
	}
	
}
