/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.AvisoProjeto;

/**
 * dao para acesso aos dados dos avisos de projetos
 *
 * @author Ilueny Santos
 *
 */
public class AvisoProjetoDao extends GenericSigaaDAO {

	
	/**
	 * Retorna avisos do projeto informado
	 * @param classe
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvisoProjeto> findByProjeto(int idProjeto) throws DAOException {

		Criteria c = getCriteria(AvisoProjeto.class);
		c.add(Expression.eq("projeto.id", idProjeto));
		
		return c.list(); 

	}


	
	
}