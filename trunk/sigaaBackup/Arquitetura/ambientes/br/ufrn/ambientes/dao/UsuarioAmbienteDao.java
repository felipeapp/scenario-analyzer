/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2010
 */
package br.ufrn.ambientes.dao;

import static org.hibernate.criterion.Restrictions.eq;

import org.hibernate.Criteria;

import br.ufrn.ambientes.dominio.UsuarioAmbiente;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;

/**
 * DAO para buscas relacionadas a usuários dos
 * diversos tipos de ambientes disponibilizados. 
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class UsuarioAmbienteDao extends GenericDAOImpl {

	public UsuarioAmbienteDao() {
		setSistema(Sistema.AMBIENTES);
	}
	
	/**
	 * Busca um usuário pelo seu login e tipo de ambiente que está tentando
	 * acessar.
	 * @param login
	 * @return
	 * @throws DAOException
	 */
	public UsuarioAmbiente findByLogin(String login, int ambiente) throws DAOException {
		Criteria c = getSession().createCriteria(UsuarioAmbiente.class);
		c.add(eq("login", login)).add(eq("ativo", true));
		c.add(eq("ambiente.id", ambiente));
		return (UsuarioAmbiente) c.uniqueResult();
	}
	
}
