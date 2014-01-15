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
	 * Realizado para pegar todas as permiss�es que o usu�rio possui e para saber se o mesmo tem
	 * permiss�o para acessar o gerenciador de permiss�es.
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
	 * Consulta realizada para exibi��o de todos os detalhes do usu�rio.
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