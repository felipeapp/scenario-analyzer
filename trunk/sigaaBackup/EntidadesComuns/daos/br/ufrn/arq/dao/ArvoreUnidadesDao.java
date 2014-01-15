/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.arq.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.comum.dominio.UnidadeGeral;

/**
 * DAO para buscas associadas ao componente da árvore de unidades.
 * 
 * @author David Pereira
 *
 */
public class ArvoreUnidadesDao extends GenericSharedDBDao {

	private static final RowMapper ARVORE_UNIDADES_MAPPER = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			UnidadeGeral unidade = new UnidadeGeral();
			unidade.setId(rs.getInt("id_unidade"));
			unidade.setNome(rs.getString("nome"));
			unidade.setSigla(rs.getString("sigla"));
			unidade.setCodigo(rs.getLong("codigo_unidade"));
			unidade.setAtivo(rs.getBoolean("possui_filhos"));
			unidade.setUnidadeResponsavel(new UnidadeGeral(rs.getInt("id_unidade_responsavel")));
			return unidade;
		}
	};

	/**
	 * Retorna a lista de unidades organizacionais para uso no componente da árvore de unidades.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UnidadeGeral> findUnidadesOrganizacionaisComponenteArvore() {
		return getJdbcTemplate().query("select u.id_unidade, u.nome, u.sigla, u.codigo_unidade, u.id_unid_resp_org as id_unidade_responsavel, (select count(id_unidade) from comum.unidade where id_unid_resp_org = u.id_unidade) > 0 as possui_filhos from comum.unidade u where u.organizacional = true and u.ativo = true order by u.nome", ARVORE_UNIDADES_MAPPER);
	}

	/**
	 * Retorna a lista de unidades orçamentárias para uso no componente da árvore de unidades.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UnidadeGeral> findUnidadesOrcamentariasComponenteArvore() {
		return getJdbcTemplate().query("select u.id_unidade, u.nome, u.sigla, u.codigo_unidade, u.unidade_responsavel as id_unidade_responsavel, (select count(id_unidade) from comum.unidade where unidade_responsavel = u.id_unidade) > 0 as possui_filhos from comum.unidade u where u.unidade_orcamentaria = true and u.ativo = true order by u.nome", ARVORE_UNIDADES_MAPPER);
	}

	/**
	 * Retorna a lista de unidades acadêmicas para uso no componente da árvore de unidades.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UnidadeGeral> findUnidadesAcademicasComponenteArvore() {
		return getJdbcTemplate().query("select u.id_unidade, u.nome, u.sigla, u.codigo_unidade, coalesce(u.id_gestora_academica, ?) as id_unidade_responsavel, (select count(id_unidade) from comum.unidade where id_gestora_academica = u.id_unidade) > 0 as possui_filhos from comum.unidade u where (u.unidade_academica = true or u.id_unidade = ?) and u.ativo = true order by u.nome", 
				new Object[] { UnidadeGeral.UNIDADE_DIREITO_GLOBAL, UnidadeGeral.UNIDADE_DIREITO_GLOBAL }, ARVORE_UNIDADES_MAPPER);
	}
	

	/**
	 * Retorna a unidade raiz de toda a hierarquia de unidades para uso no componente da árvore de unidades.
	 * @return
	 */
	public UnidadeGeral findUnidadeRaizComponenteArvore() {
		return (UnidadeGeral) getJdbcTemplate().queryForObject("select id_unidade, nome, sigla, codigo_unidade, id_unidade as id_unidade_responsavel, true as possui_filhos from comum.unidade where id_unidade = ?", 
				new Object[] { UnidadeGeral.UNIDADE_DIREITO_GLOBAL }, ARVORE_UNIDADES_MAPPER);
	}
	
}
