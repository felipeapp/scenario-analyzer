/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '14/05/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * DAO responsável pelas consultas a Tipos de Bolsa de Pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class TipoBolsaPesquisaDao extends GenericSigaaDAO {

	/**
	 * Busca um mapa de tipos de bolsa ativos onde a chave é o id e o valor
	 * é a descrição da bolsa com a quantidade de bolsistas ativos.
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> findMapaTiposBolsa() {
		Map<Integer, String> mapa = new LinkedHashMap<Integer, String>();

		String sql = "select id_tipo_bolsa as id, descricao, tb.categoria, " +
				" count(p.id_plano_trabalho) as total" +
				" from pesquisa.tipo_bolsa_pesquisa tb, pesquisa.plano_trabalho p " +
				" where p.tipo_bolsa = tb.id_tipo_bolsa" +
				" and p.id_membro_projeto_discente is not null" +
				" and p.status = " + TipoStatusPlanoTrabalho.EM_ANDAMENTO +
				" and tb.ativo = trueValue()" +
				" and tb.id_tipo_bolsa != " + TipoBolsaPesquisa.A_DEFINIR +
				" group by tb.categoria, tb.id_tipo_bolsa, tb.descricao" +
				" order by tb.categoria, tb.id_tipo_bolsa";
		
		List<Map> resultado = getJdbcTemplate().queryForList(sql, Map.class);

		// Preparar resultado
		for (Map map : resultado) {
			String descricao = map.get("descricao")
				+ " - " + TipoBolsaPesquisa.getDescricaoCategoria((Integer) map.get("categoria"))
				+ " (" + map.get("total") + ")";
			mapa.put( (Integer) map.get("id"), descricao);
		}

		return mapa;
	}
	
	/**
	 *  Busca um mapa de todos os tipos de bolsa onde a chave é o id e o valor é a descrição da bolsa.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> findTiposBolsa() {
		Map<Integer, String> mapa = new LinkedHashMap<Integer, String>();
		
		List<Map<String,Object>> resultado = getJdbcTemplate().queryForList("select t.id_tipo_bolsa as id, t.descricao, t.categoria " +
				" from pesquisa.tipo_bolsa_pesquisa t where t.id_tipo_bolsa <> 100" +
				" group by t.categoria, t.id_tipo_bolsa, t.descricao " +
				" order by t.descricao, t.categoria, t.id_tipo_bolsa ");

		for (Map map : resultado) {
			String descricao = map.get("descricao")
				+ " - " + TipoBolsaPesquisa.getDescricaoCategoria((Integer) map.get("categoria"));
			mapa.put( (Integer) map.get("id"), descricao);
		}

		return mapa;
	}

	/**
	 * Traz todas as bolsas de pesquisa que são pagas pelo SIPAC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoBolsaPesquisa> findPagasSipac() throws DAOException {
		return getSession().createCriteria(TipoBolsaPesquisa.class).add(Restrictions.isNotNull("tipoBolsaSipac")).list();
	}
	
	/**
	 * Traz todas as bolsas ativas que não são vinculadas a um período de cota. 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
    public Collection<TipoBolsaPesquisa> findBolsasFluxoContinuo() throws DAOException {
		String projecao = "ef.id_entidade_financiadora, ef.nome, tbp.id_tipo_bolsa, tbp.descricao, tbp.categoria";
		String sql = "SELECT " + projecao + " from pesquisa.tipo_bolsa_pesquisa tbp " +
				" JOIN projetos.entidade_financiadora ef using ( id_entidade_financiadora )" +
				" WHERE  tbp.vinculado_cota = falseValue() and tbp.ativo = trueValue() " +
				" order by ef.nome";
		return getJdbcTemplate().query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int arg) throws SQLException {
				TipoBolsaPesquisa tbp = new TipoBolsaPesquisa();
				tbp.setEntidadeFinanciadora(new EntidadeFinanciadora());
				tbp.getEntidadeFinanciadora().setId(rs.getInt("id_entidade_financiadora"));
				tbp.getEntidadeFinanciadora().setNome(rs.getString("nome"));
				tbp.setId(rs.getInt("id_tipo_bolsa"));
				tbp.setDescricao(rs.getString("descricao"));
				tbp.setCategoria(rs.getInt("categoria"));
				return tbp;
			}
		});
	} 
	
	/**
	 * Retorna todas as bolsas que não são vinculadas à cotas.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoBolsaPesquisa> findBolsasSemCota() throws DAOException {		
		return getSession().createCriteria(TipoBolsaPesquisa.class).add(Restrictions.eq("vinculadoCota", false)).list();
	}

	/**
	 * Retorna um mapa de todos os tipos de bolsa do órgão financiador informado
	 * onde a chave é o id e o valor é a descrição da bolsa.
	 * 
	 * @param idOrgaoFinanciador
	 * @return
	 */
	public Map<Integer, String> findByOrgaoFinanciador(int idOrgaoFinanciador) {
		Map<Integer, String> mapa = new LinkedHashMap<Integer, String>();

		List<Map<String, Object>> resultado = getJdbcTemplate().queryForList("select t.id_tipo_bolsa as id, t.descricao, t.categoria " +
				" from pesquisa.tipo_bolsa_pesquisa t " +
				" where t.ativo = trueValue() " +
				" and t.id_tipo_bolsa <> ? " +
				" and t.id_entidade_financiadora = ? " + 
				" group by t.categoria, t.id_tipo_bolsa, t.descricao " +
				" order by t.categoria, t.id_tipo_bolsa ", new Object[]{TipoBolsaPesquisa.A_DEFINIR, idOrgaoFinanciador});

		for (Map<String, Object> map : resultado) {
			String descricao = map.get("descricao")
				+ " - " + TipoBolsaPesquisa.getDescricaoCategoria((Integer) map.get("categoria"));
			mapa.put( (Integer) map.get("id"), descricao);
		}

		return mapa;
	}
	
	/**
	 * Retorna as Bolsas de pesquisa de acordo com as id's informados.
	 * 
	 * @param bolsasPesquisa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<TipoBolsaPesquisa> carregarBolsasPesquisa( Integer... integers ) throws HibernateException, DAOException{
		String projecao = "ef.id_entidade_financiadora, ef.nome, tbp.id_tipo_bolsa, tbp.descricao, tbp.categoria";
		String sql = "SELECT " + projecao + " FROM pesquisa.tipo_bolsa_pesquisa tbp " +
				" JOIN projetos.entidade_financiadora ef USING ( id_entidade_financiadora )";
				
				if (  integers.length > 0 )
					sql += " WHERE tbp.id_tipo_bolsa in " + UFRNUtils.gerarStringIn( integers );
				
				sql += " ORDER BY ef.nome";
				
		return getJdbcTemplate().query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int arg) throws SQLException {
				TipoBolsaPesquisa tbp = new TipoBolsaPesquisa();
				tbp.setEntidadeFinanciadora(new EntidadeFinanciadora());
				tbp.getEntidadeFinanciadora().setId(rs.getInt("id_entidade_financiadora"));
				tbp.getEntidadeFinanciadora().setNome(rs.getString("nome"));
				tbp.setId(rs.getInt("id_tipo_bolsa"));
				tbp.setDescricao(rs.getString("descricao"));
				tbp.setCategoria(rs.getInt("categoria"));
				return tbp;
			}
		});
	} 
	
	/**
	 * verifica se ja existe algum tipo de bolsa igual a passada por parametro.
	 * 
	 * @param obj
	 * @return
	 * @throws DAOException
	 */
	public boolean existsTipoBolsa(TipoBolsaPesquisa obj) throws DAOException {
		return getSession().createSQLQuery("select id_tipo_bolsa from pesquisa.tipo_bolsa_pesquisa where descricao = ? and categoria = ? and id_tipo_bolsa != ? and ativo")
							.setString(0, obj.getDescricao())
							.setInteger(1, obj.getCategoria())
							.setInteger(2, obj.getId())
							.list()
							.size() > 0;
	}
	
	/**
	 * Verificar se foi cadastrada, no sistema acadêmico, uma indicação de bolsa para
	 * o aluno informado.
	 *
	 * @param idDiscente
	 * @return
	 * @throws ArqException
	 */
	public boolean verificarIndicacaoPesquisa(long matricula) throws ArqException{
		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			if (con == null)
				throw new ArqException("Erro ao obter conexão com " + RepositorioDadosInstitucionais.get("siglaSigaa") + "!");


			Statement st = con.createStatement();

			String sql = "SELECT pt.id_plano_trabalho " +
					" FROM pesquisa.plano_trabalho pt, pesquisa.membro_projeto_discente m, " +
					" 		pesquisa.tipo_bolsa_pesquisa t, discente d" +
					" WHERE pt.id_plano_trabalho = m.id_plano_trabalho" +
					" AND m.id_discente = d.id_discente" +
					" AND d.matricula = " + matricula +
					" AND pt.status = " + TipoStatusPlanoTrabalho.EM_ANDAMENTO +
					" AND pt.tipo_bolsa = t.id_tipo_bolsa " +
					" AND t.id_tipo_bolsa IN" + UFRNUtils.gerarStringIn(TipoBolsaPesquisa.getBolsasNaoPermiteAcumular());

			ResultSet rs = st.executeQuery(sql);

			return (rs.next());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}
	}
}