package br.ufrn.shared.wireless.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.wireless.AutorizacaoUsersExt;

/**
 * 
 * DAO responsável pelo gerenciamento de Visitante Externo a rede Wireless 
 *   
 * @author agostinho
 *
 */
public class VisitanteExternoWirelessDAO extends GenericDAOImpl {

	public VisitanteExternoWirelessDAO() {
		setSistema(Sistema.COMUM);
	}
	
	/**
	 * Localiza o cadastro do Visitante pelo CPF.
	 * Sempre retorna o último cadastro.
	 * 
	 * @param cpf
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public AutorizacaoUsersExt findAutorizacaoUserExtByCPF(Long cpf) throws HibernateException, DAOException  {
		Session s = getSession();
					
		List<AutorizacaoUsersExt> lista = s.createQuery("select visitante from AutorizacaoUsersExt visitante where visitante.cpf = " + cpf + " order by dataCadastro").list();
		
		if (lista.size() == 0)
			return null;
		else if (lista.size() >= 1)
			return lista.get(lista.size()-1);
		
		return null;
	}

	/**
	 * Retorna lista dos visitantes externos que foram cadastrados no sistema para acessarem
	 * a rede Wireless e chegaram a acessar a rede ao menos uma vez (criando sua senha de acesso).
	 * 
	 * @param idUsuario 
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AutorizacaoUsersExt> findUsuariosAcessoWireless(int idUsuario) throws HibernateException, DAOException  {
		return getSession().createQuery("select autorizacao from " +
				"AutorizacaoUsersExt autorizacao where autorizacao.usuario.id = ?  order by autorizacao.dataCadastro, autorizacao.autenticacao.nome")
				.setInteger(0, idUsuario)
				.list();
	}

	@SuppressWarnings("unchecked")
	public AutorizacaoUsersExt findAutorizacaoUserExtByPassaporte(String passaporte) throws DAOException {
		Session s = getSession();
		
		List<AutorizacaoUsersExt> lista = s.createQuery("select visitante from AutorizacaoUsersExt visitante where visitante.passaporte = " + "'" + (passaporte.trim()) + "'" + " order by dataCadastro").list();
		
		if (lista.size() == 0)
			return null;
		else if (lista.size() >= 1)
			return lista.get(lista.size()-1);
		
		return null;
	}
	
}