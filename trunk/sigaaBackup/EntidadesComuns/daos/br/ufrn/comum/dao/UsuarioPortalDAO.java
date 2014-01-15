/**
 * 
 */
package br.ufrn.comum.dao;

import java.util.Collection;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.UsuarioPortal;

/**
 * @author Ricardo Wendell
 */
public class UsuarioPortalDAO extends GenericDAOImpl {
	/**
	 * Realizado para pegar todas as permissões que o usuário possui e para saber se o mesmo tem
	 * permissão para acessar o gerenciador de permissões.
	 *  
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public UsuarioPortal findPermissoes(UsuarioGeral usuario) throws DAOException {
		String hql = "FROM UsuarioPortal WHERE usuario.id = " + usuario.getId()+ "";

		return (UsuarioPortal) getSession().createQuery(hql).uniqueResult();
	}

	public boolean exists(UsuarioGeral usuario) throws DAOException {
		return !ValidatorUtil.isEmpty(findPermissoes(usuario));
	}

	/**
	 * Consulta realizada para exibição de todos os detalhes do usuário.
	 * 
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<UsuarioPortal> findDados(int usuario) throws DAOException {
		String hql = "FROM UsuarioPortal WHERE id = " + usuario+ "";

		return getSession().createQuery(hql).list();
	}
	
}