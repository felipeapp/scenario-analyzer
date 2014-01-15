/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/06/2008
 *
 */
package br.ufrn.sigaa.arq.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.UsuarioMobile;

public class UsuarioMobileDao extends GenericSigaaDAO {

	public UsuarioMobile findUsuarioMobileByUsuarioLogado(int idUsuario) throws HibernateException, DAOException {
		String sql  = 
					" select um.id_usuario_mobile from mobile.usuario_mobile um " +
					" inner join comum.usuario u ON u.id_usuario = um.id_usuario " +
					" where u.id_usuario = ? ";
		
		Query q = getSession().createSQLQuery(sql).setInteger(0, idUsuario);
		
		UsuarioMobile usuarioMobile = new UsuarioMobile();
		if (q.uniqueResult() != null)
			usuarioMobile.setId( Integer.parseInt( q.uniqueResult().toString() ));
		
		return usuarioMobile;
	}
	
}
