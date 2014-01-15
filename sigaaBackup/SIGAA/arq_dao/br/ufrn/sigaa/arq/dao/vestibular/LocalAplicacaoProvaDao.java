/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '07/05/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

/**
 * DAO utilizado na busca de informações referentes a um local de aplicação de
 * prova utilizado em um processo seletivo.
 * 
 * @author Édipo Elder
 * 
 */
public class LocalAplicacaoProvaDao extends GenericSigaaDAO {

	/** Construtor padrão. */
	public LocalAplicacaoProvaDao() {
		daoName = "LocalAplicacaoProvaDao";
	}

	/** Retorna uma lista de locais de aplicação de provas cadastrados para um processo seletivo.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<LocalAplicacaoProva> findByProcessoSeletivo(
			int idProcessoSeletivo) throws DAOException {
		Criteria criteria = getSession().createCriteria(LocalAplicacaoProvaProcessoSeletivo.class);
		criteria.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		criteria.createCriteria("localAplicacaoProva").addOrder(Order.asc("nome"));
		List<LocalAplicacaoProva> lista = new ArrayList<LocalAplicacaoProva>();
		Collection<LocalAplicacaoProvaProcessoSeletivo> listaPS = criteria.list();
		for (LocalAplicacaoProvaProcessoSeletivo localPS : listaPS) {
			lista.add(localPS.getLocalAplicacaoProva());
		}
		return lista;
	}

	/** Retorna uma lista de local de aplicação de prova de um município.
	 * @param municipio
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<LocalAplicacaoProva> findByMunicipio(Municipio municipio)
			throws DAOException {
		Criteria criteria = getSession().createCriteria(
				LocalAplicacaoProva.class);
		criteria.createCriteria("endereco").createCriteria("municipio").add(
				Restrictions.eq("id", municipio.getId()));
		criteria.addOrder(Order.asc("nome"));
		return criteria.list();
	}

	/** Retorna a lista de municípios onde há locais de aplicação de prova cadastrados.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Municipio> findAllMunicipios() throws DAOException {
		String hql = "select distinct localAplicacaoProva.endereco.municipio"
				+ " from LocalAplicacaoProva localAplicacaoProva"
				+ " order by localAplicacaoProva.endereco.municipio.nome";
		Query q = getSession().createQuery(hql);
		return q.list();
	}

	/** Retorna uma lista de locais de aplicação de prova de um município associadas a um processo seletivo.  
	 * @param idProcessoSeletivo
	 * @param idMunicipio
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<LocalAplicacaoProva> findByProcessoSeletivoMunicipio(
			int idProcessoSeletivo, int idMunicipio) throws DAOException {
		
		Criteria criteria = getSession().createCriteria(
				LocalAplicacaoProvaProcessoSeletivo.class);
		
		criteria.createCriteria("processoSeletivoVestibular").add(
				Restrictions.eq("id", idProcessoSeletivo));
		Criteria municipio =  criteria.createCriteria("localAplicacaoProva")
				.createCriteria("endereco").createCriteria("municipio");
		municipio.add(
				Restrictions.eq("id", idMunicipio));
		
		List<LocalAplicacaoProva> locais = new ArrayList<LocalAplicacaoProva>();
		Collection<LocalAplicacaoProvaProcessoSeletivo> locaisProcesso = municipio.list();
		for (LocalAplicacaoProvaProcessoSeletivo localPS : locaisProcesso){
			locais.add(localPS.getLocalAplicacaoProva());
		}
		Collections.sort(locais);
		
		return locais;
	}

}
