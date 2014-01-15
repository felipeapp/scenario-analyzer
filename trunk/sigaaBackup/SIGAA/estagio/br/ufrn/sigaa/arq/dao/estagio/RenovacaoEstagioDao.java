/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * DAO responsável por gerenciar o acesso a dados a Renovação de Estágio
 * 
 * @author Arlindo Rodrigues
 */
public class RenovacaoEstagioDao extends GenericSigaaDAO {
	
	/**
	 * Retorna a renovação que está em aberta do estágio informado
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
