/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 24/04/2009
 */
package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Restrictions.eq;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.MaterialTreinamento;
import br.ufrn.comum.dominio.TipoMaterialTreinamento;

/**
 * DAO para consultas relativas à entidade MaterialTreinamento
 *  
 * @author Júlio César
 * @author Jean Guerethes
 * @author Gleydson Lima
 */
public class MaterialTreinamentoDAO extends GenericSharedDBDao {

	@SuppressWarnings("unchecked")
	public List<MaterialTreinamento> findBySubSistema(int idSubSistema) throws DAOException {
		if (!isEmpty(idSubSistema)) {
			return getSession().createQuery("select mt from MaterialTreinamento mt where mt.subSistema.id = ? order by nome asc").setInteger(0, idSubSistema).list();
		} else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * Usada para buscar todos os materias.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MaterialTreinamento> findByMateriais() throws DAOException {
		String sql = "select * from tipos_materiais_treinamento";

		return getJdbcTemplate().query(sql, new Object[] {}, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				MaterialTreinamento material = new MaterialTreinamento();

				material.getTipoMaterialTreinamento().setDenominacao(rs.getString("denominacao"));
				
				return material;
			}
		});
	}

	/**
	 * Usada para fazer o autocomplete dos materias.
	 * 
	 *  JSP: busca.jsp
	 * 
	 * JSP: busca.jsp
	 * @param nome
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MaterialTreinamento> findMateriais(String nome) {
		return getJdbcTemplate().query("select distinct nome_material from comum.material_treinamento mt where ativo = 't' and upper(mt.nome_material) like upper(?) " 
				+ " order by mt.nome_material asc", new Object[] { nome + "%" }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				MaterialTreinamento mt = new MaterialTreinamento();
				mt.setNome(rs.getString("nome_material"));
				return mt;
			}
		});
	}
	
	/**
	 * Método que realiza a consulta sql para um relatório, e retorna uma Lista
	 * das linhas da consulta
	 *
	 * @param consultaSql
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SQLException
	 */
	@SuppressWarnings("deprecation")
	public List<Map<String, Object>> executeSql(String consultaSql)
			throws SQLException, HibernateException, DAOException {

		PreparedStatement prepare = getSession().connection().prepareStatement(
				consultaSql);
		System.out.println("Relatório: " + consultaSql);
		ResultSet rs = prepare.executeQuery();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			result.add(UFRNUtils.resultSetToHashMap(rs));
		}
		return result;
	}

	/**
	 * Usada para pegar todos os dados referente ao nome passado como parametro.
	 * 
	 * JSP: busca.jsp
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findDados(String nome) throws DAOException {
		String sqlconsulta = "select id_material_treinamento as id, nome_material, descricao, endereco, tmt.denominacao from comum.material_treinamento mt, comum.tipo_material_treinamento tmt" +
				" where nome_material="+"'"+""+nome+""+"'"+" and tmt.id_tipo_material = mt.id_tipo_material" ;
		
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();

		try {
			result = executeSql(sqlconsulta);
		} catch (HibernateException e) {

			e.printStackTrace();
		} catch (DAOException e) {

			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<MaterialTreinamento> findbyMaterial(TipoMaterialTreinamento tmt) throws DAOException{
		 Query query = getSession().createQuery("select mt from MaterialTreinamento mt where mt.tipoMaterialTreinamento.id=:tmt");
		 query.setInteger("tmt", tmt.getId());
		 return query.list(); 
		 
	}

	@SuppressWarnings("unchecked")
	public List<MaterialTreinamento> findByCodigoUc(String codigo) throws DAOException {
		Criteria c = getSession().createCriteria(MaterialTreinamento.class);
		c.add(eq("codigoUc", codigo)).add(eq("ativo", true));
		return c.list();
	} 
}
