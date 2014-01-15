/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/02/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgregadorBolsas;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.ParametroBuscaAgregadorBolsas;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.RestricoesBuscaAgregadorBolsas;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO responsável por gerenciar a oportunidade de bolsa que vem do SIPAC principalmente
 * 
 * @author Henrique Andre
 *
 */
public class AgregadorBolsasDao extends GenericSigaaDAO {

	/**
	 * Busca todas as bolsas de Apoio Técnico do SIPAC
	 * 
	 * @param restricoes
	 * @param parametros
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AgregadorBolsas> findBolsasSIPAC(RestricoesBuscaAgregadorBolsas restricoes, ParametroBuscaAgregadorBolsas parametros) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate(Sistema.SIPAC);
	
		
		List<Object> values = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		
		Date dataAtual = new Date();
		sql.append("select oe.descricao, oe.id_oportunidade, oe.email, oe.id_usuario, tb.denominacao, tb.valor, c.denominacao as curso, uni.nome as dpto, uni.sigla " +
				" from bolsas.oportunidade_estagio oe " +
				" inner join bolsas.tipo_bolsa tb on ( tb.id = oe.id_tipo_bolsa ) " +
				" left join bolsas.oportunidade_cursos_alvos ca on ( ca.id_oportunidade = oe.id_oportunidade ) " +
				" left join academico.curso c on ( ca.id_curso = c.id_curso ) " +
				" inner join comum.unidade uni on ( uni.id_unidade = oe.id_departamento ) " +
				" where oe.id_solicitacao_bolsa is null and oe.data_inativacao is null");
		
		if (restricoes.isBuscaPalavraChave()) {
			sql.append(" and descricao like ?");
			values.add("%" + parametros.getPalavraChave() + "%");
		}
		
		if (restricoes.isBuscaUnidadeAdm()) {
			sql.append(" and id_departamento = ?");
			values.add(parametros.getUnidadeAdm());
		}
		
		if (restricoes.isBuscaVoltadasAoCurso()) {
			sql.append(" and c.id_curso = ?");
			values.add(parametros.getVoltadasAoCurso());
		}
		
		sql.append(" and ( oe.data_maxima_inscricao >= ? or oe.data_maxima_inscricao is null )");
		values.add(dataAtual);

		return (List<AgregadorBolsas>) jdbcTemplate.query(sql.toString(), values.toArray(), new ResultSetExtractor(){

			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				
				List resultado = null;
				AgregadorBolsas ag = new AgregadorBolsas();
				ag.setId(0);
				while (rs.next()) {
					
					int idAtual = rs.getInt("id_oportunidade");
					
					if (ag.getId().intValue() != idAtual) {
						ag = new AgregadorBolsas();
						ag.setDescricao(rs.getString("descricao"));
						ag.setId(rs.getInt("id_oportunidade"));
						ag.setEmailContato(rs.getString("email"));
						ag.setTipoBolsa(rs.getString("denominacao"));
						ag.setBolsaValor(rs.getString("valor"));
						ag.setIdUsuario(rs.getInt("id_usuario"));
						
						ag.setUnidade(new Unidade());
						ag.getUnidade().setNome(rs.getString("dpto"));
						ag.getUnidade().setSigla(rs.getString("sigla"));
						
						if (resultado == null)
							resultado = new ArrayList();
						
						resultado.add(ag);
					}

					if (rs.getString("curso") != null)
						ag.getCursos().add(rs.getString("curso"));
					
				}
				return resultado;
			}
			
		});
	}

	/**
	 * Busca uma bolsa de Apoio Técnico no SIPAC pelo id
	 * 
	 * @param id
	 * @return
	 */
	public AgregadorBolsas findApoioTecnicoById(int id) {
		JdbcTemplate jdbcTemplate = getJdbcTemplate(Sistema.SIPAC);
	
		
		List<Object> values = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		
		sql.append("select p.nome, oe.email, oe.data_anuncio, oe.id_oportunidade, sb.matricula, oe.id_usuario, tb.denominacao ,oe.descricao, case when id_solicitacao_bolsa is null then 'AGUARDANDO INDICAÇÃO DE BOLSISTA' when id_solicitacao_bolsa is not null then 'EM ANDAMENTO' end as status " +
				" from bolsas.tipo_bolsa tb " +
				"   inner join bolsas.oportunidade_estagio oe on (tb.id = oe.id_tipo_bolsa) " +
				"   left join bolsas.solicitacao_bolsa sb on (sb.id = oe.id_solicitacao_bolsa) " +
				"   inner join comum.usuario u on (u.id_usuario = oe.id_usuario) " +
				"   inner join comum.pessoa p on (p.id_pessoa = u.id_pessoa) " +
				" where oe.id_oportunidade = ? and oe.data_inativacao is null");
		
		values.add(id);
		
		AgregadorBolsas resultado = null;
		
		try {
			resultado = (AgregadorBolsas) jdbcTemplate.queryForObject(sql.toString(), values.toArray(), new RowMapper(){

				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					AgregadorBolsas ag = new AgregadorBolsas();
					ag.setDescricao(rs.getString("descricao"));
					ag.setStatusAtualBolsa(rs.getString("status"));
					ag.setTipoBolsa(rs.getString("denominacao"));
					ag.setIdUsuario((rs.getInt("id_usuario")));
					ag.setMatricula(rs.getLong("matricula"));
					ag.setIdDetalhe(rs.getInt("id_oportunidade"));
					ag.setDataAnuncio(rs.getDate("data_anuncio"));
					ag.setEmailContato(rs.getString("email"));
					Servidor s = new Servidor();
					s.setPessoa(new Pessoa());
					s.getPessoa().setNome(rs.getString("nome"));
					ag.setResponsavelProjeto(s);
					return ag;
				}
				
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		
		return resultado; 
	}

	/**
	 * Retorna as unidades que anunciaram vaga de estágio
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Unidade> findUnidadesAnunciantes() {
		String sql = "select distinct u.id_unidade, u.nome from comum.unidade u inner join bolsas.oportunidade_estagio oe on ( u.id_unidade = oe.id_departamento ) where oe.id_solicitacao_bolsa is null and oe.data_inativacao is null";
		
		JdbcTemplate jdbcTemplate = getJdbcTemplate(Sistema.SIPAC);
		
		return jdbcTemplate.query(sql, new RowMapper(){

			public Unidade mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Unidade u = new Unidade();
				u.setId(rs.getInt("id_unidade"));
				u.setNome(rs.getString("nome"));
				
				return u;
			}
			
		});
		
	}	
	
	
}
