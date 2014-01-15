/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/02/2009
 */
package br.ufrn.comum.dao;

import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Restrictions.eq;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;

/**
 * DAO responsável por realizar buscas relacionadas aos sistemas. 
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class SistemaDao extends GenericSharedDBDao {

	private static Map<Integer, Boolean> cache = new HashMap<Integer, Boolean>();
	
	private static Map<Integer, Boolean> cacheCaixaPostal = new HashMap<Integer, Boolean>();
	
	private static Map<Integer, Boolean> cacheMemorandos = new HashMap<Integer, Boolean>();
	
	private static Map<Integer, Long> cacheTime = new HashMap<Integer, Long>();
	
	private static final int CACHE_TIME = 5*60*1000;
	
	public static final RowMapper SISTEMA_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Sistema sistema = new Sistema();
			sistema.setAtivo(rs.getBoolean("ativo"));
			sistema.setCaixaPostalAtiva(rs.getBoolean("caixa_postal_ativa"));
			sistema.setCaminhoArquivoConfiguracaoHibernate(rs.getString("caminho_config_hibernate"));
			sistema.setControlarUsuariosOnline(rs.getBoolean("controlar_usuarios_online"));
			sistema.setId(rs.getInt("id"));
			sistema.setManutencao(rs.getBoolean("manutencao"));
			sistema.setMemorandosEletronicosAtivos(rs.getBoolean("memorandos_eletronicos_ativos"));
			sistema.setNome(rs.getString("descricao"));
			sistema.setNomeDataSourceJndi(rs.getString("nome_datasource_jndi"));
			return sistema;
		}
	};
	
	/**
	 * Busca todos os subsistemas associados a um sistema passado como parâmetro.
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SubSistema> findSubsistemasBySistema(int id) {
		DetachedCriteria dc = DetachedCriteria.forClass(SubSistema.class);
		dc.add(eq("sistema.id", id)).addOrder(asc("nome"));
		return getHibernateTemplate().findByCriteria(dc);
	}

	/**
	 * Retorna os sistemas que estão marcados como ativo = true.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<Sistema> findSistemasAtivos() {
		return getJdbcTemplate(Sistema.COMUM).query("select * from comum.sistema s where s.ativo = trueValue() order by s.descricao asc", SISTEMA_MAPPER);
	}
	
	/**
	 * Identifica se um sistema está ativo ou não. Possui cache para evitar
	 * buscas desnecessárias ao banco de dados.
	 * @param sistema
	 * @return
	 */
	public boolean isSistemaAtivo(int sistema) {
		Long tempo = cacheTime.get(sistema);

		if ( cache.get(sistema) == null || tempo == null || System.currentTimeMillis() - tempo > CACHE_TIME ) {
			Boolean ativo = (Boolean) getComumTemplate().queryForObject("select ativo from comum.sistema where id = ?", new Object[] { sistema }, Boolean.class);
			cache.put(sistema, ativo);
			cacheTime.put(sistema, System.currentTimeMillis());
		}
		
		return cache.get(sistema);
	}

	/**
	 * Atualiza as colunas ativo e manutenção de um sistema passado como parâmetro para 
	 * os valores passados como parâmetro.
	 * @param id
	 * @param ativo
	 * @param manutencao
	 */
	public void atualizarConfiguracoes(int id, boolean ativo, boolean manutencao, boolean caixaPostal, boolean memorandos) {
		getJdbcTemplate().update("update comum.sistema set ativo=?, manutencao=?, caixa_postal_ativa = ?, memorandos_eletronicos_ativos = ? where id = ?", 
				new Object[] { ativo, manutencao, caixaPostal, memorandos, id });
	}

	/**
	 * Identifica se os memorandos eletrônicos estão ativos para o sistema passado como parâmetro
	 * @param sistema
	 * @return
	 */
	public boolean isMemorandosAtivos(int sistema) {
		Long tempo = cacheTime.get(sistema);
		
		if ( cacheMemorandos.get(sistema) == null || tempo == null || System.currentTimeMillis() - tempo > CACHE_TIME ) {
			Boolean ativo = (Boolean) getComumTemplate().queryForObject("select memorandos_eletronicos_ativos from comum.sistema where id = ?", new Object[] { sistema }, Boolean.class);
			cacheMemorandos.put(sistema, ativo);
			cacheTime.put(sistema, System.currentTimeMillis());
		}
		
		return cacheMemorandos.get(sistema);
	}

	/**
	 * Identifica se a caixa postal está ativa para o sistema passado como parâmetro
	 * @param sistema
	 * @return
	 */
	public boolean isCaixaPostalAtiva(int sistema) {
		Long tempo = cacheTime.get(sistema);

		if ( cacheCaixaPostal.get(sistema) == null || tempo == null || System.currentTimeMillis() - tempo > CACHE_TIME ) {
			Boolean ativo = (Boolean) getComumTemplate().queryForObject("select caixa_postal_ativa from comum.sistema where id = ?", new Object[] { sistema }, Boolean.class);
			cacheCaixaPostal.put(sistema, ativo);
			cacheTime.put(sistema, System.currentTimeMillis());
		}
		
		return cacheCaixaPostal.get(sistema);
	}

	/**
	 * Retorna o caminho do arquivo de configuração do Hibernate para
	 * o sistema passado como parâmetro.
	 * @param sistema
	 * @return
	 */
	public String findConfigHibernateSistema(int sistema) {
		return (String) getComumTemplate().queryForObject("select caminho_config_hibernate from comum.sistema where id = ?", new Object[] { sistema }, String.class);
	}

	/**
	 * Busca a lista de sistemas que possuem o atributo controlarUsuariosOnline igual a
	 * true.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Sistema> buscarSistemasControleUsuariosOnline() {
		return getComumTemplate().query("select * from comum.sistema where controlar_usuarios_online = true and ativo = true", SISTEMA_MAPPER);
	}
	

	/*
	 * Retorna um JdbcTemplate apontando para o banco comum.
	 */
	private JdbcTemplate getComumTemplate() {
		return new JdbcTemplate(Database.getInstance().getComumDs());
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Boolean> findMapaSistemasAtivos() {
		return (Map<Integer, Boolean>) getComumTemplate().query("select id, ativo from comum.sistema", MAP_EXTRACTOR);
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Boolean> findMapaCaixaPostalAtiva() {
		return (Map<Integer, Boolean>) getComumTemplate().query("select id, caixa_postal_ativa from comum.sistema", MAP_EXTRACTOR);
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Boolean> findMapaMemorandosAtivos() {
		return (Map<Integer, Boolean>) getComumTemplate().query("select id, memorandos_eletronicos_ativos from comum.sistema", MAP_EXTRACTOR);
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Boolean> findMapaQuestionariosAtivos() {
		return (Map<Integer, Boolean>) getComumTemplate().query("select id, questionarios_ativos from comum.sistema", MAP_EXTRACTOR);
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Boolean> findMapaPortalPublicoAtivo() {
		return (Map<Integer, Boolean>) getComumTemplate().query("select id, portal_publico_ativo from comum.sistema", MAP_EXTRACTOR);
	}
	
	/**
	 * Retorna os sistemas que possuem controle de usuário para consulta de logs.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<Sistema> findSistemasControleUsuarios() {
		return getJdbcTemplate(Sistema.COMUM).query("select * from comum.sistema s where s.ativo = trueValue() and s.controlar_usuarios_online = trueValue() order by s.descricao asc", SISTEMA_MAPPER);
	}
	
}
