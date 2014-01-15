/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 11/10/2010
 */
package br.ufrn.sigaa.arq.dao.estagio;

import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagio;
import br.ufrn.sigaa.estagio.dominio.ConcedenteEstagioPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO respons�vel por gerenciar o acesso a dados de Concedente de Est�gio
 * 
 * @author Arlindo Rodrigues
 */
 
public class ConcedenteEstagioDao extends GenericSigaaDAO {
	
	/**
	 * Retorna a pessoa do Concedente de est�gio informado conforme a fun��o informada.
	 * @param p
	 * @param concedente
	 * @param funcao
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public ConcedenteEstagioPessoa findPessoaByConcedenteFuncao(Pessoa p, ConcedenteEstagio concedente, Integer funcao) throws HibernateException, DAOException{
		String hql = "select p from ConcedenteEstagioPessoa p " +
				" where p.pessoa.id = "+p.getId()+
				"   and p.concedente.id = "+concedente.getId()+
				"   and p.funcao.id = "+funcao;			
		return (ConcedenteEstagioPessoa) getSession().createQuery(hql.toString()).setMaxResults(1).uniqueResult();
	}

}
