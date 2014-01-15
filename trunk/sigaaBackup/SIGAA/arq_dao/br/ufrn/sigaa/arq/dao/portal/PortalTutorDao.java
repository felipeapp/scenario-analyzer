/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '06/04/2010'
 *
 */
package br.ufrn.sigaa.arq.dao.portal;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * DAO responsável por consultas relacionadas ao Portal do Tutor
 * 
 * @author Thalisson Muriel
 *
 */

public class PortalTutorDao extends GenericSigaaDAO{
	
	
	/**
	 * Retorna o Usuário correspondente à Pessoa de acordo com o id passado como parâmetro.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByUsuario(int idPessoa) throws DAOException{
		
		Query query = getSession().createQuery("from Usuario usuario where usuario.pessoa.id = :idPessoa");
		query.setInteger("idPessoa", idPessoa);
		
		return (Usuario) query.uniqueResult();
		
	}

}
