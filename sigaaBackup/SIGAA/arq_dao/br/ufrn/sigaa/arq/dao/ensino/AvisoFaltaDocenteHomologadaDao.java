/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/06/2009
 *
 */		
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.AvisoFaltaDocenteHomologada;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DadosAvisoFalta;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAvisoFaltaHomologado;
import br.ufrn.sigaa.ensino.dominio.StatusParecerPlanoReposicaoAula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO responsável por fazer o acesso aos dados de FaltaDocenteHomologada
 * 
 * @author Henrique André
 *
 */
public class AvisoFaltaDocenteHomologadaDao extends GenericSigaaDAO {

	/**
	 * Indica se o professor possui alguma falta homologada que não possui plano de aula ou que possui mas foi negada
	 * 
	 * @param idServidor
	 * @return
	 */
	public boolean isPendentePlanoAula(int idServidor) {
	
		String sql =	" select exists (select homologada.id_falta_homologada "  + 
						" from ensino.aviso_falta_docente_homologada homologada " + 
						" 	inner join ensino.dados_aviso_falta dados on (dados.id_dados_aviso_falta = homologada.id_dados_aviso) " + 
						" 	left join ( " +
						" 		select h.id_falta_homologada, (select subplano.id_plano_reposicao_aula from ensino.plano_reposicao_aula subplano where id_falta_homologada = h.id_falta_homologada order by subplano.data_cadastro desc limit 1) as id_plano_reposicao_aula " +
						" 		from ensino.aviso_falta_docente_homologada h " +
						" 			inner join ensino.dados_aviso_falta dados on (dados.id_dados_aviso_falta = h.id_dados_aviso) " +  
						" 		where h.id_movimentacao in  " + MovimentacaoAvisoFaltaHomologado.getMovimentacoesSemPlano() + 
						" 			and h.id_plano_aprovado is null " + 
						" 			and dados.id_docente = ?) as sub on (sub.id_falta_homologada = homologada.id_falta_homologada) " +
						" 	left join ensino.plano_reposicao_aula plano on (plano.id_plano_reposicao_aula = sub.id_plano_reposicao_aula) " +
						" 	left join ensino.parecer_plano_reposicao_aula parecer on (parecer.id_parecer = plano.id_parecer) " + 
						" 	left join ensino.status_parecer_plano_reposicao_aula status using(id_status_parecer) " + 
						" where homologada.id_movimentacao in " + MovimentacaoAvisoFaltaHomologado.getMovimentacoesSemPlano() +  
						" 	and homologada.id_plano_aprovado is null " + 
						" 	and dados.id_docente = ? " +
						" 	and (plano.id_plano_reposicao_aula is null or status.id_status_parecer = " + StatusParecerPlanoReposicaoAula.NEGADO + ") " + 						
						" )";
		return (Boolean) getJdbcTemplate().queryForObject(sql, new Object[]{idServidor, idServidor}, Boolean.class);
	}		
	
	/**
	 * Retorna as faltas homologadas para o docente passado como argumento
	 * 
	 * @param idServidor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AvisoFaltaDocenteHomologada> findHomologacoesComPlanosPendentesByServidor(int idServidor) {

		String sql = 	" select homologada.id_falta_homologada, dados.data_aula, t.codigo as codigo_turma, detalhes.nome, cc.codigo as codigo_disciplina " + 
						" from ensino.aviso_falta_docente_homologada homologada " + 
						" 	inner join ensino.dados_aviso_falta dados on (dados.id_dados_aviso_falta = homologada.id_dados_aviso) " + 
						" 	inner join ensino.turma t on (t.id_turma = dados.id_turma) " +
						" 	inner join ensino.componente_curricular cc on (cc.id_disciplina = t.id_disciplina) " + 
						" 	inner join ensino.componente_curricular_detalhes detalhes on (cc.id_detalhe = detalhes.id_componente_detalhes) " +
						" 	left join ( " +
						" 		select h.id_falta_homologada, (select subplano.id_plano_reposicao_aula from ensino.plano_reposicao_aula subplano where id_falta_homologada = h.id_falta_homologada order by subplano.data_cadastro desc limit 1) as id_plano_reposicao_aula " +
						"		from ensino.aviso_falta_docente_homologada h " +
						" 			inner join ensino.dados_aviso_falta dados on (dados.id_dados_aviso_falta = h.id_dados_aviso)" + 
						" 		where h.id_movimentacao in " + MovimentacaoAvisoFaltaHomologado.getMovimentacoesSemPlano() + 
						" 			and h.id_plano_aprovado is null " + 
						" 			and dados.id_docente = ?) as sub on (sub.id_falta_homologada = homologada.id_falta_homologada) " +
						" 	left join ensino.plano_reposicao_aula plano on (plano.id_plano_reposicao_aula = sub.id_plano_reposicao_aula) " +
						" 	left join ensino.parecer_plano_reposicao_aula parecer on (parecer.id_parecer = plano.id_parecer) " + 
						" 	left join ensino.status_parecer_plano_reposicao_aula status using(id_status_parecer) " + 
						" 	where homologada.id_movimentacao in " + MovimentacaoAvisoFaltaHomologado.getMovimentacoesSemPlano() + 
						" 		and homologada.id_plano_aprovado is null " + 
						" 		and dados.id_docente = ? " +
						" 		and (plano.id_plano_reposicao_aula is null or status.id_status_parecer = " + StatusParecerPlanoReposicaoAula.NEGADO + ")"; 
		
		return getJdbcTemplate().query(sql, new Object[] { idServidor, idServidor }, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				AvisoFaltaDocenteHomologada fh = new AvisoFaltaDocenteHomologada();
				fh.setId(rs.getInt("id_falta_homologada"));
				fh.setDadosAvisoFalta(new DadosAvisoFalta());
				fh.getDadosAvisoFalta().setDataAula(rs.getDate("data_aula"));
				
				fh.getDadosAvisoFalta().setTurma(new Turma());
				fh.getDadosAvisoFalta().getTurma().setCodigo(rs.getString("codigo_turma"));
				fh.getDadosAvisoFalta().getTurma().setDisciplina(new ComponenteCurricular());
				fh.getDadosAvisoFalta().getTurma().getDisciplina().setCodigo(rs.getString("codigo_disciplina"));
				fh.getDadosAvisoFalta().getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes());
				fh.getDadosAvisoFalta().getTurma().getDisciplina().getDetalhes().setNome(rs.getString("nome"));
				
				return fh;
			}
			
		});
	}

	/**
	 * Retorna as faltas homologadas de todos os docentes do departamento
	 * 
	 * @param idUnidade
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AvisoFaltaDocenteHomologada> findHomologacoesComPlanosPendentesByDepartamento(int idUnidade) {
		
		String sql = 	" select p.nome as nome_pessoa, homologada.id_falta_homologada, homologada.id_movimentacao, dados.data_aula, homologada.data_cadastro, t.codigo as codigo_turma, detalhes.nome, cc.codigo as codigo_disciplina, s.id_servidor, t.id_turma " + 
						" from ensino.aviso_falta_docente_homologada homologada " + 
						" 	inner join ensino.dados_aviso_falta dados on (dados.id_dados_aviso_falta = homologada.id_dados_aviso) " + 
						" 	inner join rh.servidor s on (s.id_servidor = dados.id_docente) " +
						" 	inner join comum.pessoa p on (p.id_pessoa = s.id_pessoa) " +
						" 	inner join ensino.turma t on (t.id_turma = dados.id_turma) " +
						" 	inner join ensino.componente_curricular cc on (cc.id_disciplina = t.id_disciplina) " + 
						" 	inner join ensino.componente_curricular_detalhes detalhes on (cc.id_detalhe = detalhes.id_componente_detalhes) " +
						" 	left join ( " +
						" 		select h.id_falta_homologada, (select subplano.id_plano_reposicao_aula from ensino.plano_reposicao_aula subplano where id_falta_homologada = h.id_falta_homologada order by subplano.data_cadastro desc limit 1) as id_plano_reposicao_aula " +
						" 		from ensino.aviso_falta_docente_homologada h " +
						" 			inner join ensino.dados_aviso_falta dados on (dados.id_dados_aviso_falta = h.id_dados_aviso) " +
						" 			inner join rh.servidor s on (s.id_servidor = dados.id_docente) " +
						" 		where h.id_movimentacao in " + MovimentacaoAvisoFaltaHomologado.getMovimentacoesPendentes() + 
						" 			and h.id_plano_aprovado is null " + 
						" 			and s.id_unidade = ?) as sub on (sub.id_falta_homologada = homologada.id_falta_homologada) " +
						" 	left join ensino.plano_reposicao_aula plano on (plano.id_plano_reposicao_aula = sub.id_plano_reposicao_aula) " +
						" 	left join ensino.parecer_plano_reposicao_aula parecer on (parecer.id_parecer = plano.id_parecer) " + 
						" 	left join ensino.status_parecer_plano_reposicao_aula status using(id_status_parecer) " + 
						" 	where homologada.id_movimentacao in " + MovimentacaoAvisoFaltaHomologado.getMovimentacoesPendentes() +
						" 		and homologada.id_plano_aprovado is null " + 
						" 		and s.id_unidade = ? " +
						" 		and (plano.id_plano_reposicao_aula is null or status.id_status_parecer = " + StatusParecerPlanoReposicaoAula.NEGADO + ")";
		
		
		return getJdbcTemplate().query(sql, new Object[] { idUnidade, idUnidade }, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				AvisoFaltaDocenteHomologada fh = new AvisoFaltaDocenteHomologada();
				fh.setId(rs.getInt("id_falta_homologada"));
				try {
					fh.setMovimentacao(findByPrimaryKey(rs.getInt("id_movimentacao"), MovimentacaoAvisoFaltaHomologado.class));
				} catch (DAOException e) {
					fh.setMovimentacao(null);
				}
				fh.setDadosAvisoFalta(new DadosAvisoFalta());
				fh.getDadosAvisoFalta().setDataAula(rs.getDate("data_aula"));
				fh.setDataCadastro(rs.getDate("data_cadastro"));
				
				fh.getDadosAvisoFalta().setDocente(new Servidor());
				fh.getDadosAvisoFalta().getDocente().setId(rs.getInt("id_servidor"));
				fh.getDadosAvisoFalta().getDocente().setPessoa(new Pessoa());
				fh.getDadosAvisoFalta().getDocente().getPessoa().setNome(rs.getString("nome_pessoa"));
				fh.getDadosAvisoFalta().setTurma(new Turma(rs.getInt("id_turma")));
				fh.getDadosAvisoFalta().getTurma().setCodigo(rs.getString("codigo_turma"));
				fh.getDadosAvisoFalta().getTurma().setDisciplina(new ComponenteCurricular());
				fh.getDadosAvisoFalta().getTurma().getDisciplina().setCodigo(rs.getString("codigo_disciplina"));
				fh.getDadosAvisoFalta().getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes());
				fh.getDadosAvisoFalta().getTurma().getDisciplina().getDetalhes().setNome(rs.getString("nome"));
				
				return fh;
			}
			
		});
	}	
	
	/**
	 * Verifica se já existe uma falta homologada ativa, ou seja, que não tenha sido estornada
	 * @see MovimentacaoAvisoFaltaHomologado
	 * 
	 * @param avisoFaltaHomologada
	 * @return
	 * @throws DAOException
	 */
	public boolean isExists(AvisoFaltaDocenteHomologada avisoFaltaHomologada) throws DAOException {
		
		String hql = "select count(homologada.id) from AvisoFaltaDocenteHomologada homologada where " +
				" homologada.dadosAvisoFalta.id = " + avisoFaltaHomologada.getDadosAvisoFalta().getId() +
				" and ativo = trueValue()";
		
		Long result = (Long) getSession().createQuery(hql).uniqueResult();
		
		if (isEmpty(result))
			return false;
		
		return true;
		
	}	
	
}
