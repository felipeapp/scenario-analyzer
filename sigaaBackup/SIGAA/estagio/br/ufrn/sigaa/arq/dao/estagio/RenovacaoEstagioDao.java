/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/11/2010
 */
package br.ufrn.sigaa.arq.dao.estagio;

import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.RenovacaoEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusRenovacaoEstagio;

/**
 * DAO respons�vel por gerenciar o acesso a dados a Renova��o de Est�gio
 * 
 * @author Arlindo Rodrigues
 */
public class RenovacaoEstagioDao extends GenericSigaaDAO {
	
	/**
	 * Retorna a renova��o que est� em aberta do est�gio informado
	 * @param e
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public RenovacaoEstagio findRenovacaoAbertaByEstagio(Estagiario e) throws HibernateException, DAOException{
		return (RenovacaoEstagio) getSession().createCriteria(RenovacaoEstagio.class)
		.add(Expression.eq("estagio", e))
		.add(Expression.eq("status.id", StatusRenovacaoEstagio.AGUARDANDO_RELATORIO))
		.setMaxResults(1)
		.uniqueResult();		
	}	
	

}
