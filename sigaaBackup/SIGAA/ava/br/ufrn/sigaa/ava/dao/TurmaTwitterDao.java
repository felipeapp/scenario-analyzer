/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2011
 * 
 */
package br.ufrn.sigaa.ava.dao;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.TurmaTwitter;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;

/**
 * Implementação de TurmaTwitterDAO
 *
 * @author Diego Jácome
 *
 */
public class TurmaTwitterDao extends GenericSigaaDAO {

	/**
	 * Busca lista de chats por turma informada.
	 *
	 * @throws  
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<TurmaTwitter> findTurmasTwitterByIds(List<Integer> idsTurma) throws DAOException  {
		try {
			
			String hql = "select turma from TurmaTwitter as turma where turma.turma.id = " + UFRNUtils.gerarStringIn(idsTurma);

		    Query q = getSession().createQuery(hql);		
			List<TurmaTwitter> lista = q.list();
			return lista;
			
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

}