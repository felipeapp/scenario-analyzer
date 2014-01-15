/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '17/06/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.prodocente.atividades.dominio.CHDedicadaResidenciaMedica;

/**
 * DAO para buscas relacionadas à residência Médica.
 *
 * @author Jean Guerethes
 *
 */

public class CHDedicadaResidenciaMedicaDao extends GenericSigaaDAO{

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
	public List<HashMap<String, Object>> executeSql(String consultaSql)
			throws SQLException, HibernateException, DAOException {

		PreparedStatement prepare = getSession().connection().prepareStatement(
				consultaSql);
		System.out.println("Relatório: " + consultaSql);
		ResultSet rs = prepare.executeQuery();
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		while (rs.next()) {
			result.add(UFRNUtils.resultSetToHashMap(rs));
		}
		return result;
	}
	
	/**
	 * Relatório das residências Médicas baseando-se no ano e período.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findResidenciaMedicaPorAno(Integer ano, Integer periodo) throws DAOException {
		String sqlconsulta = "select rm.id, p.nome as nome_servidor, prm.nome,un.nome as hospital, rm.ano,rm.semestre, rm.ch_semanal"+ 
				" from prodocente.residencia_medica as rm, rh.servidor as serv, comum.pessoa as p, prodocente.programa_residencia_medica as prm, comum.unidade as un"+
				" where rm.ano = "+ano+" and rm.semestre = "+periodo+" and rm.id_servidor = serv.id_servidor and un.id_unidade = prm.id_hospital"+
				" and p.id_pessoa = serv.id_pessoa and rm.id_programa_residencia_medica = prm.id_programa_residencia_medica and rm.ativo = 't'";

		List result = new ArrayList<HashMap<String,Object>>();

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

	/**
	 * Relatório das residências Médicas baseando-se id do programa da residência Médica.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findResidenciaMedicaPorPrograma(Integer id) throws DAOException {
		String sqlconsulta = "select rm.id, p.nome as nome_servidor, prm.nome,un.nome as hospital, rm.ano,rm.semestre, rm.ch_semanal"+ 
						" from prodocente.residencia_medica as rm, rh.servidor as serv, comum.pessoa as p, prodocente.programa_residencia_medica as prm, comum.unidade as un"+ 
						" where rm.id_programa_residencia_medica = "+id+" and rm.id_servidor = serv.id_servidor and un.id_unidade = prm.id_hospital and p.id_pessoa = serv.id_pessoa and rm.id_programa_residencia_medica = prm.id_programa_residencia_medica and rm.ativo = 't'";

		List result = new ArrayList<HashMap<String,Object>>();

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

	/**
	 * Relatório das residências Médicas, tendo como entrada o ano, período e o programa
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findResidenciaMedica(Integer id, Integer ano, Integer periodo) throws DAOException {
		String sqlconsulta = "select rm.id, p.nome as nome_servidor, prm.nome, und.nome as hospital, rm.ano,rm.semestre, rm.ch_semanal"+
					" from prodocente.residencia_medica as rm, prodocente.programa_residencia_medica as prm, comum.unidade as und, rh.servidor as serv, comum.pessoa as p"+
					" where rm.id_programa_residencia_medica = "+id+" and rm.id_programa_residencia_medica = prm.id_programa_residencia_medica"+
					" and prm.id_hospital = und.id_unidade and rm.ano = "+ano+" and rm.semestre = "+periodo+" and rm.id_servidor = serv.id_servidor and und.id_unidade = prm.id_hospital"+
					" and p.id_pessoa = serv.id_pessoa and rm.id_programa_residencia_medica = prm.id_programa_residencia_medica and rm.ativo = 't'";

		List result = new ArrayList<HashMap<String,Object>>();

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

	/**
	 * Serve para mostrar de forma detalhada as residências Medicas.
	 * 
	 * @param id
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HashMap<String,Object>> findResidenciaMedicaDetalhada(Integer id) throws DAOException {
		String sqlconsulta = "select rm.id, p.nome as nome_servidor, prm.nome, und.nome as hospital, rm.ano,rm.semestre, rm.ch_semanal, rm.observacoes"+
					" from prodocente.residencia_medica as rm, prodocente.programa_residencia_medica as prm, comum.unidade as und, rh.servidor as serv, comum.pessoa as p"+
					" where rm.id = "+id+" and rm.id_programa_residencia_medica = prm.id_programa_residencia_medica"+
					" and prm.id_hospital = und.id_unidade and rm.id_servidor = serv.id_servidor and und.id_unidade = prm.id_hospital"+
					" and p.id_pessoa = serv.id_pessoa and rm.id_programa_residencia_medica = prm.id_programa_residencia_medica and rm.ativo = 't'";

		List result = new ArrayList<HashMap<String,Object>>();

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

	/**
	 * Serve para pegar o nome do Programa.
	 * 
	 * jsp -- buscar.jsp
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String[] findNome(Integer id) throws DAOException {
		String sqlconsulta = "select prm.nome, und.nome  from prodocente.residencia_medica as rm, prodocente.programa_residencia_medica as prm, comum.unidade as und"+  
					" where rm.id_programa_residencia_medica = "+id+" and rm.id_programa_residencia_medica = prm.id_programa_residencia_medica"+
					" and prm.id_hospital = und.id_unidade";
		
		Query q = getSession().createSQLQuery(sqlconsulta);
		List<Object[]> l = q.list();
		String[] result = new String[2];
		for(Object[] o: l){
			result[0] = (String) o[0];
			result[1] = (String) o[1];
		}
		return result;
	}
	
	/**
	 * Retorna maiores infromações da caraga horária de residência médica
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public List<HashMap<String, Object>> detalhes(int id) throws DAOException {
		String hql = "FROM CHDedicadaResidenciaMedica WHERE id = " + id+ "";

		return getSession().createQuery(hql).list();
	}

	/**
	 * Retorna a CHDedicadaResidenciaMedica do servidor e ano/período informado
	 * 
	 * @param id
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<CHDedicadaResidenciaMedica> findByServidorAnoPeriodo(int idServidor, int ano, int periodo) throws HibernateException, DAOException {
		Query q = getSession().createQuery("select ch from CHDedicadaResidenciaMedica ch " +
				" where ch.servidor.id = ? and ch.ano = ? and ch.semestre = ?" +
				" and ch.ativo = trueValue() ")
				.setInteger(0, idServidor).setInteger(1, ano).setInteger(2, periodo);
		return q.list();
	}
	
	/**
	 * Retorna a CHDedicadaResidenciaMedica do servidor e ano informado
	 * 
	 * @param id
	 * @param ano
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<CHDedicadaResidenciaMedica> findByServidorAno(int idServidor, int ano) throws HibernateException, DAOException {
		Query q = getSession().createQuery("from CHDedicadaResidenciaMedica ch " +
										   " where ch.servidor.id = ? and ch.ano = ?" +
										   " and ch.ativo = trueValue() ")
			.setInteger(0, idServidor).setInteger(1, ano);
		return q.list();
	}
	
	/**
	 * Inativa todas as cargas horárias cadastradas para o programa de Residência Médica.
	 * @param idsAvaliacao
	 * @throws DAOException
	 */
	public void inativarCargaHorariaResidenciaMedica(List <Integer> idsAvaliacao) throws DAOException{
		update("UPDATE prodocente.residencia_medica SET ativo = falseValue() WHERE id in "+UFRNUtils.gerarStringIn(idsAvaliacao)+"");
	}
	
}