/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/05/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorCampoControle;

/**
 *
 *      Dao para busca de descritores tanto de campo de controle quanto para campos de dados.
 *      Descritores é onde estão as informações tando para validar os dados dos campos MARC, quando
 * para montar a ajuda para o usuário.
 *
 * @author jadson
 * @since 12/05/2009
 * @version 1.0 criacao da classe
 *
 */
public class DescritoresDao  extends GenericSigaaDAO{

	
	/**
	 *    Encontra os descritores dos campos de controle que não dependem do Formato do material.
	 *    
	 *    Líder Bibliográfico. Líder Autoridades e 008 Autoridades 
	 *
	 * @param idEtiqueta
	 * @return
	 * @throws DAOException
	 */
	public List<DescritorCampoControle> findDescritoresCampoControleNaoDependemFormatoMaterial(int idEtiqueta) throws DAOException{

		StringBuilder hql = new StringBuilder("SELECT d FROM DescritorCampoControle d WHERE d.formatoMaterialEtiqueta.etiqueta.id = :idEtiqueta ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEtiqueta", idEtiqueta);

		@SuppressWarnings("unchecked")
		List<DescritorCampoControle> lista = q.list();
		return lista;
	}
	
	
	/**
	 *  Encontra os descritores dos campos de controle de dependem do formato do material.
	 *  006 Bibliográfico e 008 Bibliográfico.
	 *
	 * @param idEtiqueta
	 * @return
	 * @throws DAOException
	 */
	public List<DescritorCampoControle> findDescritoresCampoControleDependemFormatoMaterial(int idEtiqueta, int idFormatoMaterial) throws DAOException{

		StringBuilder hql = new StringBuilder("SELECT d FROM DescritorCampoControle d WHERE d.formatoMaterialEtiqueta.etiqueta.id = :idEtiqueta AND d.formatoMaterialEtiqueta.formatoMaterial.id = :idFormatoMaterial");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEtiqueta", idEtiqueta);
		q.setInteger("idFormatoMaterial", idFormatoMaterial);
		
		@SuppressWarnings("unchecked")
		List<DescritorCampoControle> lista = q.list();
		return lista;
	}
	
	
	/** 
	 * Encontra os descritores dos campos de controle de dependem da categoria do material.
	 * 007 Bibliográfico.
	 *
	 * @param idEtiqueta
	 * @return
	 * @throws DAOException
	 */
	public List<DescritorCampoControle> findDescritoresCampoControleDependemCategoriaMaterial(int idEtiqueta, String codigoCategoriaMaterial) throws DAOException{

		StringBuilder hql = new StringBuilder("SELECT d FROM DescritorCampoControle d WHERE d.categoriaMaterial.codigo = :codigoCategoriaMaterial AND d.categoriaMaterial.etiqueta.id = :idEtiqueta");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idEtiqueta", idEtiqueta);
		q.setString("codigoCategoriaMaterial", codigoCategoriaMaterial);
		
		@SuppressWarnings("unchecked")
		List<DescritorCampoControle> lista = q.list();
		return lista;
	}
	
}
