/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 16/06/2009
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAvisoFaltaHomologado;
import br.ufrn.sigaa.ensino.dominio.PlanoReposicaoAula;

/**
 * DAO responsável por acessar os dados de PlanoReposicaoAula
 * 
 * @author Henrique André
 *
 */
public class PlanoReposicaoAulaDao extends GenericSigaaDAO {
	
	/**
	 * Busca os planos que não tem parecer de um departamento
	 * 
	 * @param idDepartamento
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<PlanoReposicaoAula> findPlanosPendentesByDepartamento(int idDepartamento) throws DAOException {
		
		String hql = "select pra from PlanoReposicaoAula pra " +
				" where pra.faltaHomologada.dadosAvisoFalta.docente.unidade.id = " + idDepartamento + " and " +
				" pra.parecer is null and " +
				" pra.faltaHomologada.movimentacao.id <> " + MovimentacaoAvisoFaltaHomologado.ESTORNADO.getId() +
				" order by pra.faltaHomologada.dadosAvisoFalta.dataAula";

		Query query = getSession().createQuery(hql);
		return query.list();
	}
	
	/**
	 * Localiza todos os Planos de Aula que possuem origem uma falta homologada
	 *  
	 * @param idServidor
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	@SuppressWarnings("unchecked")
	public List<PlanoReposicaoAula> findAllPlanosByDocente(int idServidor) throws DAOException {
		
		String hql = " select pra from PlanoReposicaoAula pra " +
					 " where pra.faltaHomologada.dadosAvisoFalta.docente = :idServidor" +
					 " order by pra.faltaHomologada.dadosAvisoFalta.dataAula";
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idServidor", idServidor);
		
		return query.list();
	}
	
	
}
