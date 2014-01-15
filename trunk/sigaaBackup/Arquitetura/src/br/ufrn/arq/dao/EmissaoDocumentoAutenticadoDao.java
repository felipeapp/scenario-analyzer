/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/06/2007
 */
package br.ufrn.arq.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;

/**
 * DAO que contém buscas relacionadas à entidade
 * {@link EmissaoDocumentoAutenticado}.
 * 
 * @author Gleydson Lima
 *
 */
public class EmissaoDocumentoAutenticadoDao extends GenericSharedDBDao {

	/**
	 * Dadas as informações de identificador do documento, código de segurança, data e tipo de documento,
	 * retorna a emissão que foi registrada com essas informações.
	 * @param identificador
	 * @param codigo
	 * @param adataEmissaotaEmissao
	 * @param tipoDoc
	 * @return
	 * @throws DAOException
	 */
	public EmissaoDocumentoAutenticado findByEmissao(String identificador, String codigo, java.sql.Date adataEmissaotaEmissao, int tipoDoc) throws DAOException {

		Criteria c = getSession().createCriteria(EmissaoDocumentoAutenticado.class);
		c.add(Expression.eq("identificador",identificador));
		c.add(Expression.eq("codigoSeguranca",codigo));
		c.add(Expression.eq("dataEmissao",adataEmissaotaEmissao));
		c.add(Expression.eq("tipoDocumento",tipoDoc));

		c.setMaxResults(1); // teve uns que gerou duplicados, eliminar erro do uniqueResult

		return (EmissaoDocumentoAutenticado)c.uniqueResult();

	}
	
	/**
	 * Verifica a emissão de documentos utilizando 
	 * o campo dadosAuxiliares.
     * Utilizado para documentos onde o identificador não está 
     * disponível para o usuário.
	 * 
	 * @param codigo
	 * @param dataEmissao
	 * @param tipoDoc
	 * @param dadosAuxiliares
	 * @return
	 * @throws DAOException
	 */
	public EmissaoDocumentoAutenticado findByEmissao(String numeroDocumento, String codigo, java.sql.Date dataEmissao, int tipoDoc, Integer subTipoDoc) throws DAOException {

		Criteria c = getSession().createCriteria(EmissaoDocumentoAutenticado.class);
		c.add(Expression.eq("numeroDocumento",numeroDocumento));
		c.add(Expression.eq("codigoSeguranca",codigo));
		c.add(Expression.eq("dataEmissao",dataEmissao));
		c.add(Expression.eq("tipoDocumento",tipoDoc));
		c.add(Expression.eq("subTipoDocumento",subTipoDoc));

		c.setMaxResults(1); // teve uns que gerou duplicados, eliminar erro do uniqueResult

		return (EmissaoDocumentoAutenticado)c.uniqueResult();

	}

	/**
	 * Verifica a emissão de documentos autenticados utilizando 
	 * subtipo de documento.
	 * 
	 * @param identificador
	 * @param codigo
	 * @param subTipoDoc
	 * @param dataEmissao
	 * @return
	 * @throws DAOException
	 */
	public EmissaoDocumentoAutenticado findByEmissao(String identificador, String codigo, int tipoDoc, Integer subTipoDoc) throws DAOException {

		Criteria c = getSession().createCriteria(EmissaoDocumentoAutenticado.class);
		c.add(Expression.eq("identificador",identificador));
		c.add(Expression.eq("codigoSeguranca",codigo));
		c.add(Expression.eq("tipoDocumento",tipoDoc));
		c.add(Expression.eq("subTipoDocumento",subTipoDoc));
		
		c.setMaxResults(1); // teve uns que gerou duplicados, eliminar erro do uniqueResult

		return (EmissaoDocumentoAutenticado)c.uniqueResult();

	}
	
	/**
	 * Dadas as informações de identificador do documento, código de segurança, tipo de documento e sub-tipo,
	 * retorna a emissão que foi registrada com essas informações utilizando SQL.
	 * @param identificador
	 * @param codigo
	 * @param adataEmissaotaEmissao
	 * @param tipoDoc
	 * @return
	 * @throws DAOException
	 */
	public EmissaoDocumentoAutenticado findByEmissaoSql(String identificador, String codigo, int tipoDoc, Integer subTipoDoc) throws DAOException {

		StringBuilder sql = new StringBuilder();
		sql.append(" select id_emissao, tipo_documento, codigo_seguranca, identificador, data_emissao, " +
				" hora_emissao, prng, auxiliar, numero_documento, sub_tipo_documento" +
				" from comum.emissao_documento_autenticado where id_emissao > 0");
		
		if(tipoDoc > 0){
			sql.append("  and tipo_documento = " + tipoDoc + "");
		}
		if(!identificador.isEmpty()){
			sql.append(" and identificador = '"+identificador+"'");
		}
		if(!codigo.isEmpty()){
			sql.append(" and codigo_seguranca = '"+codigo+"'");
		}
		if(subTipoDoc > 0){
			sql.append(" and sub_tipo_documento = " + subTipoDoc + " ");
		}

		Query c = getSession().createSQLQuery(sql.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = c.list();
		List<EmissaoDocumentoAutenticado> resultado = new ArrayList<EmissaoDocumentoAutenticado>();
		for (Iterator<Object[]> iterator = lista.iterator(); iterator.hasNext();) {
			int count  = 0;
			Object[] objects = iterator.next();
			EmissaoDocumentoAutenticado emissao = new EmissaoDocumentoAutenticado();
			emissao.setId( (Integer) objects[count++]);
			emissao.setTipoDocumento( (Integer) objects[count++]);
			emissao.setCodigoSeguranca( (objects[count++].toString()));
			emissao.setIdentificador((String) objects[count++]);
			emissao.setDataEmissao((Date) objects[count++]);
			emissao.setHoraEmissao((Date) objects[count++]);
			emissao.setPrng( objects[count++].toString());
			emissao.setDadosAuxiliares((String) objects[count++]);
			emissao.setNumeroDocumento((String) objects[count++]);
			emissao.setSubTipoDocumento((Integer) objects[count++]);
			resultado.add(emissao);
		}
		if(resultado.size() > 0){
			return resultado.get(0);
		}
		return null;
	}
	

}
