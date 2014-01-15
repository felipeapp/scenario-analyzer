/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/10/2010
 * 
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;

/**
 * <p> Dao para consultas em informações sobre formato do material </p>
 *
 * 
 * @author jadson
 *
 */
public class FormatoMaterialDao extends GenericSigaaDAO{

	/**
	 *  Retorna apenas os ids do formatos do materiais dos títulos passados. Utilizado para saber se os títulos são de periódicos
	 *  de forma otimizada para uma quantidade de títulos muito grande. Nessa situação não é possível fazer uma consulta para cada título
	 *  
	 *  @return um arrays onde: [0] = o id do título <br/>
	 *                          [1] = o id do formato de material do titulo <br/> 
	 *  
	 * @return
	 */
	public List<Object> findFormatosMaterialDosTitulo(List<Integer> idsTitulo) throws DAOException{

		String hql = " SELECT t.id, t.formatoMaterial.id FROM  TituloCatalografico t where t.id in ( :idsTitulos ) and t.catalogado = trueValue() and t.ativo = trueValue() ";

		Query q1 = getSession().createQuery(hql);
		q1.setParameterList("idsTitulos", idsTitulo);
		@SuppressWarnings("unchecked")
		List<Object> list = q1.list();
		return  list;
	}
	
	
	
	/**
	 *   Retorna apenas o id do formato do material do título passado. Utilizado para saber se o título é de periódico.
	 *    
	 * @return
	 */
	public FormatoMaterial findFormatoMaterialDoTitulo(int idTitulo) throws DAOException{

		String hql = " SELECT t.formatoMaterial.id FROM  TituloCatalografico t where t.id = "+idTitulo+" and t.catalogado = trueValue() and t.ativo = trueValue() ";

		Query q1 = getSession().createQuery(hql);
		Integer idFormato = (Integer) q1.uniqueResult();
		if(idFormato != null)
			return new FormatoMaterial( (Integer) q1.uniqueResult());
		else
			return null;
	}
	
}
