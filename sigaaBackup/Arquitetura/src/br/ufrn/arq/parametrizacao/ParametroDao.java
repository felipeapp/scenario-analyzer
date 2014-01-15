/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/03/2009
 */
package br.ufrn.arq.parametrizacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.Parametro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;

/**
 * DAO para busca de parâmetros no banco comum.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@SuppressWarnings("unchecked")
public class ParametroDao extends GenericSharedDBDao {

	// Consulta base para buscas de parâmetros
	private static final String PARAMETRO_QUERY = "select p.codigo, p.nome, p.descricao, p.valor, p.id_sistema, p.tempo_maximo, "
		+ "s.descricao as nome_sistema, p.id_subsistema, ss.nome as nome_subsistema, p.tipo, p.padrao, p.valor_minimo, p.valor_maximo from comum.parametro p "
		+ "left outer join comum.sistema s on (p.id_sistema = s.id) left outer join comum.subsistema ss on (p.id_subsistema = ss.id) ";
	
	// RowMapper para transformar o resultado de uma consulta em um objeto do tipo Parametro
	private static final RowMapper PARAMETRO_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Parametro p = new Parametro();
			p.setCodigo(rs.getString("codigo"));
			p.setNome(rs.getString("nome"));
			p.setDescricao(rs.getString("descricao"));
			p.setValor(rs.getString("valor"));
			p.setSistema(new Sistema(rs.getInt("id_sistema")));
			p.getSistema().setNome(rs.getString("nome_sistema"));
			p.setSubSistema(new SubSistema(rs.getInt("id_subsistema")));
			p.getSubSistema().setNome(rs.getString("nome_subsistema"));
			
			int tipo = rs.getInt("tipo");
			p.setTipo( tipo == -1 ? null : tipo );
			p.setPadrao(rs.getString("padrao"));
			p.setValorMinimo(rs.getBigDecimal("valor_minimo"));
			p.setValorMaximo(rs.getBigDecimal("valor_maximo"));
			
			int tempoMaximo = rs.getInt("tempo_maximo");
			p.setTempoMaximo(tempoMaximo == -1 ? null : tempoMaximo);
			return p;
		}
	};
	
	public String getParametro(String codigo) {
		Parametro parametro = findByPrimaryKey(codigo);
		if (parametro==null){
			return null;
		}
		return parametro.getValor();
	}
	
	public int getAsInt(String codigo) {
		String param = getParametro(codigo);
		return Integer.parseInt(param);
	}
	
	public double getAsDouble(String codigo) {
		String param = getParametro(codigo);
		return Double.parseDouble(param);
	}
	
	public Date getAsDate(String codigo) {
		String param = getParametro(codigo);
		return new Date(Long.parseLong(param));
	}

	public boolean getAsBoolean(String codigo) {
		return new Boolean(getParametro(codigo));
	}

	/** Busca parêmetro pela chave primária */
	public Parametro findByPrimaryKey(String codigo) {
		try {
			return (Parametro) getJdbcTemplate().queryForObject(PARAMETRO_QUERY + " where p.codigo = ?", new Object[] { codigo }, PARAMETRO_MAPPER);
		}  catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/** Busca por subSistema e retorna uma lista de parâmetros */
	public List<Parametro> findBySubSistema(SubSistema subSistema) {
		try {
			return getJdbcTemplate().query(PARAMETRO_QUERY + " where p.id_subsistema = ?", new Object[] { subSistema.getId() }, PARAMETRO_MAPPER);
		}  catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/** Busca por sistema e retorna uma lista de parâmetros */
	public List<Parametro> findBySistema(Sistema sistema) {
		try {
			return getJdbcTemplate().query(PARAMETRO_QUERY + " where p.id_sistema = ? order by p.id_subsistema, nome", new Object[] { sistema.getId() }, PARAMETRO_MAPPER);
		}  catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/** Busca por sistema e sub-sistema, retornando uma lista de parâmetros */
	public List<Parametro> findBySistemaSubSistema(Sistema sistema, SubSistema subSistema) {
		StringBuilder where = new StringBuilder(" where p.id_sistema = ? ");
		
		List<Object> params = new ArrayList<Object>();
		params.add(sistema.getId());
		
		if (!isEmpty(subSistema)) {
			where.append(" and p.id_subsistema = ? ");
			params.add(subSistema.getId());
		}
		
		try {
			return getJdbcTemplate().query(PARAMETRO_QUERY + where + " order by p.id_subsistema, nome", params.toArray(), PARAMETRO_MAPPER);
		}  catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/** Busca por sistema e sub-sistema, retornando uma lista de parâmetros */
	public List<Parametro> buscaParametros(Sistema sistema, SubSistema subSistema, String nome, String codigo) {
		StringBuilder where = new StringBuilder(" where 1 = 1 ");
		
		List<Object> params = new ArrayList<Object>();
		
		if (!isEmpty(sistema)) {
			where.append(" and p.id_sistema = ? ");
			params.add(sistema.getId());
		}
		
		if (!isEmpty(subSistema)) {
			where.append(" and p.id_subsistema = ? ");
			params.add(subSistema.getId());
		}
		
		if (!isEmpty(nome)) {
			where.append(" and upper(p.nome) like upper(?) ");
			params.add("%" + nome + "%");
		}
		
		if (!isEmpty(codigo)) {
			where.append(" and p.codigo = ? ");
			params.add(codigo);
		}
		
		try {
			return getJdbcTemplate().query(PARAMETRO_QUERY + where + " order by p.nome", params.toArray(), PARAMETRO_MAPPER);
		}  catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/** Atualiza ou cria novo parâmetro */
	public void salvar(Parametro param) throws DAOException {
		Integer idSistema = param.getSistema().getId();
		Integer idSubSistema = isEmpty(param.getSubSistema()) ? null : param.getSubSistema().getId();
			
		if(getJdbcTemplate().queryForInt("select count(*) from comum.parametro where codigo = ?", new Object[] { param.getCodigo() }) > 0)
			getJdbcTemplate().update("update comum.parametro set nome = ?, descricao = ?, valor = ?, id_subsistema = ?, tempo_maximo = ?, tipo = ?, padrao = ?, valor_minimo = ?, valor_maximo = ? where codigo = ?", 
					new Object[] { param.getNome(), param.getDescricao(), param.getValor(), idSubSistema, param.getTempoMaximo(), param.getTipo(), param.getPadrao(), param.getValorMinimo(), param.getValorMaximo(), param.getCodigo() });
		else
			getJdbcTemplate().update("insert into comum.parametro (nome, descricao, valor, id_subsistema, id_sistema, tempo_maximo, codigo, tipo, padrao, valor_minimo, valor_maximo) values " +
					"(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[] { param.getNome(), param.getDescricao(), param.getValor(), idSubSistema, idSistema, param.getTempoMaximo(), param.getCodigo(), param.getTipo(), param.getPadrao(), param.getValorMinimo(), param.getValorMaximo() });
	}

	/**
	 * Retorna todas as informações de um parâmetro como um mapa onde a chave
	 * do mapa é o nome da coluna no banco e o valor do mapa é o valor armazenado na coluna.
	 * @param codigo
	 * @return
	 */
	public Map<String, Object> getParametroAsMap(String codigo) {
		try {
			return getJdbcTemplate().queryForMap("select * from comum.parametro where codigo = ?", new Object[] { codigo });
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate(Database.getInstance().getComumDs());
	}

	/**
	 * Remove um parâmetro do banco de dados de acordo com o seu código. 
	 * @param param
	 */
	public void remover(Parametro param) {
		getJdbcTemplate().update("delete from comum.parametro where codigo = ?", new Object[] { param.getCodigo() });
	}
}
