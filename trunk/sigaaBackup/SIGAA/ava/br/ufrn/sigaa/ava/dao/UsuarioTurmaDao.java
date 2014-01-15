/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/05/2011
 *
 */
package br.ufrn.sigaa.ava.dao;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;

public class UsuarioTurmaDao extends GenericSigaaDAO {

	/**
	 * Busca usuários que serão exibidos na listagem de destinatários para as mensagens da turma virtual.
	 * 
	 * @param nome
	 * @param login
	 * @return
	 * @throws DAOException
	 */
	public List <UsuarioGeral> findByNomeLogin (String nome, String login) throws DAOException {
		try {
			
			String hqlLogin = !StringUtils.isEmpty(login) ? " and login like '%" + login + "%'" : "";
			String hqlNome = !StringUtils.isEmpty(nome) ? " and pessoa.nomeAscii like '%" + StringUtils.toAsciiAndUpperCase(nome) + "%'" : "";
			
			String projecao = "id, login, pessoa.nome";
			String hql = "select " + projecao + " from Usuario where inativo = falseValue()" + hqlLogin + hqlNome + " order by pessoa.nome";
			
			Query q = getSession().createQuery(hql.toString());
			q.setMaxResults(100);
			
			@SuppressWarnings("unchecked")
			List <UsuarioGeral> rs = (List<UsuarioGeral>) HibernateUtils.parseTo(q.list(), projecao, UsuarioGeral.class);
			
			return rs;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}
	
}
