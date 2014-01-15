/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/06/2009
 *
 */
package br.ufrn.sigaa.diploma.dao;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.diploma.dominio.FolhaRegistroDiploma;
import br.ufrn.sigaa.diploma.dominio.LivroRegistroDiploma;

/** 
 * Classe responsável por consultas especializadas à folhas de registro de diplomas.
 * @author Édipo Elder F. Melo
 *
 */
public class FolhaRegistroDiplomaDao extends GenericSigaaDAO {
	

	/** Retorna a coleção de folhas de registro do livro especificado.
	 * @param livro
	 * @return
	 * @throws DAOException
	 */
	public Collection<FolhaRegistroDiploma> findByLivro(LivroRegistroDiploma livro) throws DAOException {
		String hql = "select distinct frd" +
				" from FolhaRegistroDiploma frd" +
				" inner join fetch frd.registros" +
				" where frd.livro.id = :idLivro" +
				" order by frd.numeroFolha";
		Query q = getSession().createQuery(hql);
		q.setInteger("idLivro", livro.getId());
		
		@SuppressWarnings("unchecked")
		Collection<FolhaRegistroDiploma> lista = q.list();
		return lista;
	}
}
