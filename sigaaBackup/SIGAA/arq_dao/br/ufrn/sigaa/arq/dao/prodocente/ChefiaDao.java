/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '03/05/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * DAO para consultas relacionadas a chefias. 
 * Onde é possível para as chefias a realização das seguintes buscas;
 * <p> Buscar todas as chefias cadastradas para um determinado Servidor ou para um determinada Unidade </p> 
 * <p> Buscar as chefias cadastradas para um determinado servidor de uma determinada Unidade e que ainda estejam ativa
 * e que a sua data final não seja superior a data atual.</p>
 * 
 * @author Ricardo Wendell
 *
 */
public class ChefiaDao extends GenericSigaaDAO{

	/**
	 * Busca as chefias cadastradas para servidores lotados em uma determinada unidade.
	 * Opcionalmente é possível buscar de um servidor específico
	 *
	 * @param atividade
	 * @param unidade
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<?> findByLotacaoServidor(Class<?> atividade, Unidade unidade, int idServidor) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append(" from " + atividade.getSimpleName() + " p " );
		hql.append(" where servidor.unidade.id = " + unidade.getId());
		hql.append(" AND (p.ativo is null OR ativo = trueValue()) ");
		if (idServidor != -1) {
			hql.append(" and servidor.id = " + idServidor);
		}
		hql.append(" order by servidor.pessoa.nome asc, dataPublicacao desc");

		return getSession().createQuery(hql.toString()).list();

	}
	
	/**
	 * Busca as chefias cadastradas para servidores lotados em uma determinada unidade.
	 * 
	 * @param atividade
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<?> findAllUnidade(Class<?> atividade, Unidade unidade) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append(" from " + atividade.getSimpleName() + " p " );
		hql.append(" where servidor.unidade.id = " + unidade.getId());
		hql.append(" AND (p.ativo is null OR ativo = trueValue()) ");
		hql.append(" AND (p.dataFinal > now() or p.dataFinal is null)");
		hql.append(" order by dataPublicacao desc, servidor.pessoa.nome asc");

		return getSession().createQuery(hql.toString()).list();
	}
	
}