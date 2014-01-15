/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 05/08/2013 
 */
package br.ufrn.sigaa.pesquisa.dao;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;

/**
 * DAO com consultas sobre planos de trabalho de pesquisa.
 * 
 * @author Leonardo Campos
 *
 */
public class PlanoTrabalhoDao extends GenericSigaaDAO {

	/**
	 * Retorna o plano de trabalho de pesquisa que o discente informado possua em andamento.
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public PlanoTrabalho findAtivoByIdDiscente(int idDiscente) throws DAOException {
		return (PlanoTrabalho) getSession()
				.createQuery(
						"select p from PlanoTrabalho p join p.membroProjetoDiscente m where p.status = ? "
								+ " and m.inativo = ? and m.discente.id = ? ")
				.setInteger(0, TipoStatusPlanoTrabalho.EM_ANDAMENTO)
				.setBoolean(1, Boolean.FALSE).setInteger(2, idDiscente)
				.setMaxResults(1).uniqueResult();
	}
}
