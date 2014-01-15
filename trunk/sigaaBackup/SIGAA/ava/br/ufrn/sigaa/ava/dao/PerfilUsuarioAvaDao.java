/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/08/2010
 * 
 */
package br.ufrn.sigaa.ava.dao;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.PerfilUsuarioAva;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO que gerencia as informações sobre os perfis de usuários na turma virtual.
 * 
 * @author Fred_Castro
 *
 */
public class PerfilUsuarioAvaDao extends GenericSigaaDAO {

	/**
	 * Retorna o perfil do usuário para a turma passada.
	 * 
	 * @param pessoa
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public PerfilUsuarioAva findPerfilByPessoaTurma (Pessoa pessoa, Turma turma) throws DAOException {
		
		Query q = getSession().createQuery("select pua from PerfilUsuarioAva pua where pessoa.id = :idPessoa and turma.id = :idTurma");
		q.setInteger("idPessoa", pessoa.getId());
		q.setInteger("idTurma", turma.getId());
		
		PerfilUsuarioAva pda = (PerfilUsuarioAva) q.uniqueResult();
		return pda;
	}
}