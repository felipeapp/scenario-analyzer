/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/08/2008
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterialEtiqueta;
/**
 *
 * DAO que contem os m�todos de busca para a classe intermedi�ria  que faz o relacionamento entre
 * uma etiqueta e o formato de material.
 * 
 *
 * @author jadson
 * @since 15/08/2008
 * @version 1.0 criacao da classe
 *
 */
public class FormatoMaterialEtiquetaDao extends GenericSigaaDAO{
	
	
	
	
	/**
	 *   M�todo que busca a classe intermedi�ria entre as classes Etiqueta e FormatoMaterial
	 *   � importante porque nessa classe est�o os valores para validar os campos 006 e 008
	 *
	 * @param e a etiqueta
	 * @param t o tipo de material
	 * @return TipoMaterialCatalograficoEtiqueta
	 * @throws DAOException
	 */
	public FormatoMaterialEtiqueta buscaFormatoMaterialEtiqueta(Etiqueta e, FormatoMaterial f) throws DAOException {
		
		String hql = "FROM FormatoMaterialEtiqueta formato WHERE formato.etiqueta.id = :idEtiqueta AND " 
			+"formato.formatoMaterial.id = :idFormato";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("idEtiqueta", e.getId());
		
		q.setInteger("idFormato", f.getId());
		
		// s� era para existir um cadastrado para uma determinada etiqueta e formato de material 
		
		return (FormatoMaterialEtiqueta) q.uniqueResult();
		
	}
	
	/**
	 *    M�todo que busca o formato material etiqueta no caso daquelas etiquetas que n�o possuem 
	 *  formato material. � o caso da etiquetas de autoridades e a etiqueta l�der bibliogr�fica.
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public FormatoMaterialEtiqueta buscaFormatoMaterialEtiquetaQueNaoPossuemFormatoMaterial(Etiqueta e) throws DAOException {
		
		String hql = "FROM FormatoMaterialEtiqueta formato WHERE formato.etiqueta.id = :idEtiqueta AND formato.formatoMaterial is null";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("idEtiqueta", e.getId());
		
		// s� era para existir um cadastrado para uma determinada etiqueta e formato de material 
		
		return (FormatoMaterialEtiqueta) q.uniqueResult();
		
	}
	
}
