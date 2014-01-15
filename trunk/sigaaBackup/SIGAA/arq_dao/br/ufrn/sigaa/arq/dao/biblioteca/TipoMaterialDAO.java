/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/07/2011
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;

/**
 *
 * Dao para os tipos dos materiais informacionais.
 *
 * @author Felipe Rivas
 * @since 04/07/2011
 * @version 1.0 criacao da classe
 *
 */
public class TipoMaterialDAO extends GenericSigaaDAO {

	/**
	 * Busca a descrição de tipos de materiais ativos a partir de seus id's. 
	 * 
	 * @param idTipoMaterialList Lista de id's de tipos de materiais
	 * @return Lista das descrições dos tipos de materiais
	 * @throws DAOException
	 */
	public List<String> findDescricaoTiposAtivos(Collection<Integer> idTipoMaterialList) throws DAOException {
		Criteria c = getSession().createCriteria(TipoMaterial.class);
		
		c.add(Restrictions.eq("ativo", true));
		
		if (idTipoMaterialList != null && idTipoMaterialList.size() > 0) {
			c.add(Restrictions.in("id", idTipoMaterialList));
		}
		
		c.setProjection(Projections.property("descricao"));
		
		c.addOrder(Order.asc("id"));
		
		@SuppressWarnings("unchecked")
		List<String> lista = c.list();
		
		return lista;
	}
	
}
