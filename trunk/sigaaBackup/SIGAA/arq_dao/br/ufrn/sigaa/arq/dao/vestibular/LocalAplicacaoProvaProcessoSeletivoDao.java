/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/09/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

/** Classe responsável por consultas especializadas à locais de aplicação de prova de um processo seletivo. 
 * @author Édipo Elder F. de Melo
 * 
 */
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProvaProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

public class LocalAplicacaoProvaProcessoSeletivoDao extends GenericSigaaDAO {
	
	/** Retorna uma coleção de locais de aplicação de prova associadas ao processo seletivo.
	 * @param municipio
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public List<LocalAplicacaoProvaProcessoSeletivo> findAllByMunicipioProcessoSeletivo(
			Municipio municipio, ProcessoSeletivoVestibular processoSeletivo)
			throws DAOException {
		Criteria criteria = getSession().createCriteria(
				LocalAplicacaoProvaProcessoSeletivo.class);
		criteria.createCriteria("localAplicacaoProva").addOrder(
				Order.asc("nome")).createCriteria("endereco").createCriteria(
				"municipio").add(Restrictions.eq("id", municipio.getId()));
		criteria.createCriteria("processoSeletivoVestibular").add(
				Restrictions.eq("id", processoSeletivo.getId()));
		@SuppressWarnings("unchecked")
		List<LocalAplicacaoProvaProcessoSeletivo> lista = criteria.list();
		return lista;
	}

	/** Retorna uma o local de aplicação de prova associadas ao processo seletivo.
	 * @param processoSeletivo
	 * @param local
	 * @return
	 * @throws DAOException
	 */
	public LocalAplicacaoProvaProcessoSeletivo findByProcessoSeletivoLocalAplicacao(
			ProcessoSeletivoVestibular processoSeletivo,
			LocalAplicacaoProva local) throws DAOException {
		Criteria criteria = getSession().createCriteria(
				LocalAplicacaoProvaProcessoSeletivo.class);
		criteria.createCriteria("localAplicacaoProva").add(
				Restrictions.eq("id", local.getId()));
		criteria.createCriteria("processoSeletivoVestibular").add(
				Restrictions.eq("id", processoSeletivo.getId()));
		return (LocalAplicacaoProvaProcessoSeletivo) criteria.uniqueResult();
	}
	
	/** Retorna a lista de municípios onde há locais de aplicação de prova de um processo seletivo.
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Municipio> findMunicipiosByProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) throws DAOException{
		String hql = "select distinct lapps.localAplicacaoProva.endereco.municipio" +
				" from LocalAplicacaoProvaProcessoSeletivo lapps" +
				" where lapps.processoSeletivoVestibular.id = :idProcessoSeletivo";
		Query q = getSession().createQuery(hql);
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		@SuppressWarnings("unchecked")
		Collection<Municipio> lista = q.list();
		return lista;
	}
}
