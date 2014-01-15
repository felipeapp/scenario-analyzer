/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '16/10/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.ItemAvaliacao;

/**
 * DAO para consultas de itens de avaliação de pesquisa
 * 
 * @author leonardo
 * 
 */
public class ItemAvaliacaoDao extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public Collection<ItemAvaliacao> findByTipo(int tipo) throws DAOException {
		return getSession().createQuery(
				"from ItemAvaliacao where dataDesativacao is null and tipo = "
						+ tipo + " order by ordem asc").list();
	}
}
