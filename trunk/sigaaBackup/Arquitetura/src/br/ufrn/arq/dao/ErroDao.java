/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/05/2009
 */
package br.ufrn.arq.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.gerencia.Erro;
import br.ufrn.arq.erros.gerencia.ErroOcorrencia;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * DAO de busca de erros. A herança de GenericSharedDBDao é proposital para usar
 * o SessionFactory de Comum.
 * 
 * @author Gleydson Lima
 *
 */
@SuppressWarnings("unchecked")
public class ErroDao extends GenericSharedDBDao {

	private static final RowMapper ERRO_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Erro e = new Erro();
			e.setId(rs.getInt("id_erro"));
			e.setTraceCompleto(rs.getString("trace_completo"));
			e.setTraceGerador(rs.getString("trace_gerador"));
			e.setData(rs.getTimestamp("data"));
			e.setSistema(new Sistema(rs.getInt("id_sistema")));
			e.getSistema().setNome(rs.getString("descricao"));
			e.setSubSistema(new SubSistema(rs.getInt("id_sub_sistema")));
			e.getSubSistema().setNome(rs.getString("nome"));
			e.setDeployProducao(rs.getInt("id_deploy_producao"));
			e.setExcecao(rs.getString("excecao"));
			
			return e;
		}
	};
	
	private static final RowMapper OCORRENCIA_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			ErroOcorrencia eo = new ErroOcorrencia();
			eo.setId(rs.getInt("id_erro_ocorrencia"));
			eo.setIdRegistroEntrada(rs.getInt("id_registro_entrada"));
			eo.setIdUsuario(rs.getInt("id_usuario"));
			eo.setData(rs.getTimestamp("data"));
			eo.setErro(new Erro(rs.getInt("id_erro")));
			eo.setUsuario(new UsuarioGeral(rs.getInt("id_usuario")));
			eo.getUsuario().setPessoa(new PessoaGeral());
			eo.getUsuario().getPessoa().setNome(rs.getString("nome"));
			eo.getUsuario().setLogin(rs.getString("login"));
			return eo;
		}
	};
	
	private static final String SQL_ERRO = "select * from infra.erro e left join comum.sistema s on (e.id_sistema = s.id) "
			+ "left join comum.subsistema ss on (e.id_sub_sistema = ss.id) where 1 = 1 ";
	
	private static final String SQL_OCORRENCIA = "select eo.*, u.login, p.nome from infra.erro_ocorrencia eo, comum.usuario u, comum.pessoa p " 
			+ "where eo.id_usuario = u.id_usuario and u.id_pessoa = p.id_pessoa and eo.id_erro = ?";
	
	public Erro findByPrimaryKey(int id) {
		return (Erro) getJdbcTemplate().queryForObject(SQL_ERRO + " and id_erro = ?", new Object[] { id }, ERRO_MAPPER);
	}
	
	/**
	 * Busca de Erros de acordo com os parâmetros passados
	 * 
	 * @param inicio
	 * @param fim
	 * @param login
	 * @param sistema
	 * @param subsistema
	 * @return
	 * @throws DAOException
	 */
	public List<Erro> findGeral(Date inicio, Date fim, String login, Integer sistema, Integer subsistema, String erro, PagingInformation paging ) {
		
		List<Object> parametros = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		
		if ( inicio != null ) {
			sql.append("and e.data >= ? ");
			parametros.add(inicio);
		}
		
		if ( fim != null ) {
			sql.append("and e.data <= ? ");
			parametros.add(fim);
		}
		
		if ( login != null && (!login.trim().isEmpty()) ) {
			sql.append("and e.id_erro in ( select id_erro from infra.erro_ocorrencia where id_usuario = (select id_usuario from comum.usuario where login = ?) ) ");
			parametros.add(login);
		}
		
		if ( sistema != null && sistema > 0) {
			sql.append("and e.id_sistema = ? ");
			parametros.add(sistema);
		}
		
		if ( subsistema != null && subsistema > 0) {
			sql.append("and e.id_sub_sistema = ? ");
			parametros.add(subsistema);
		}
		
		if(!ValidatorUtil.isEmpty(erro)){
			sql.append("and e.trace_gerador like ? ");
			parametros.add("%" + erro + "%");			
		}
		
		int count = getJdbcTemplate().queryForInt("select count(*) from infra.erro e left join comum.sistema s on (e.id_sistema = s.id) "
				+ "left join comum.subsistema ss on (e.id_sub_sistema = ss.id) where 1 = 1 " + sql, parametros.toArray());
		paging.setTotalRegistros(count);
		
		return getJdbcTemplate().query(SQLDialect.limit(SQL_ERRO + sql.toString() + " order by e.data desc",
				paging.getTamanhoPagina(),paging.getPaginaAtual() * paging.getTamanhoPagina()), parametros.toArray(),ERRO_MAPPER);
	}

	/**
	 * Busca de Erros de acordo com os parâmetros passados
	 * 
	 * @param inicio
	 * @param fim
	 * @param login
	 * @param sistema
	 * @param subsistema
	 * @return
	 * @throws DAOException
	 */
	public List<Erro> findGeral(Date inicio, Date fim, String login, Integer sistema, Integer subsistema, PagingInformation paging ) {
		
		return findGeral(inicio, fim, login, sistema, subsistema, null, paging);
		
	}	
	
	/**
	 * Busca as Ocorrência para um determinado Erro passado como parâmetro
	 * 
	 * @param idErro
	 * @return
	 * @throws DAOException
	 */
	public List<ErroOcorrencia> findOcorrencias(int idErro) throws DAOException {
		return getJdbcTemplate().query(SQL_OCORRENCIA, new Object[] { idErro }, OCORRENCIA_MAPPER);
	}
	
	
	/**
	 * Retorna um data source
	 */
	public JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(Database.getInstance().getComumDs());
	}

	/**
	 * Cadastra um novo Erro
	 * @param erro
	 */
	public void create(Erro erro) {
		int id = getJdbcTemplate().queryForInt("select nextval('erro_seq')");
		getJdbcTemplate().update("insert into infra.erro (id_erro, trace_completo, trace_gerador, data, "
				+ "id_sistema, id_sub_sistema, id_deploy_producao, excecao) values (?, ?, ?, ?, ?, ?, ?, ?)", 
				new Object[] { id, erro.getTraceCompleto(), erro.getTraceGerador(), erro.getData(), (erro.getSistema() == null ? null : erro.getSistema().getId()), 
						(erro.getSubSistema() == null ? null : erro.getSubSistema().getId()), 
						(erro.getDeployProducao() == null ? null : erro.getDeployProducao()), erro.getExcecao() });
		erro.setId(id);
	}

	/**
	 * Cadastra um nova Ocorrencia
	 * @param ocorrencia
	 */
	public void create(ErroOcorrencia ocorrencia) {
		int id = getJdbcTemplate().queryForInt("select nextval('erro_seq')");
		getJdbcTemplate().update("insert into infra.erro_ocorrencia (id_erro_ocorrencia, data, id_usuario, id_registro_entrada, id_erro) values (?, ?, ?, ?, ?)", 
				new Object[] { id, ocorrencia.getData(), ocorrencia.getIdUsuario(), ocorrencia.getIdRegistroEntrada(), ocorrencia.getErro().getId() });
		ocorrencia.setId(id);
	}
	
}
