/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '31/03/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.vestibular.dominio.EscolaInep;

/** DAO respons�vel pelas consultas espec�ficas a base de dados de EscolasInep
 * @author �dipo Elder F. Melo
 *
 */
public class EscolaInepDao extends GenericSigaaDAO {
	
	/** Retorna uma cole��o de Munic�pios da determinada UF, onde possuem escolas cadastradas.
	 * @param idUnidadeFederativa
	 * @return Cole��o de Munic�pios
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Municipio> findMunicipiosByUF(int idUnidadeFederativa) throws DAOException {
		String hql = "select distinct escolaInep.endereco.municipio" +
				" from EscolaInep escolaInep" +
				" where escolaInep.endereco.unidadeFederativa.id = :idUnidadeFederativa" +
				" order by escolaInep.endereco.municipio.nome";
		Query q = getSession().createQuery(hql);
		q.setInteger("idUnidadeFederativa", idUnidadeFederativa);
		return q.list();
	}
	
	/** Retorna uma cole��o de EscolasInep, dados os Munic�pio e UF.
	 * @param idMunicipio
	 * @param idUnidadeFederativa
	 * @return Cole��o de EscolasInep
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EscolaInep> findByMunicipioUF(int idMunicipio, int idUnidadeFederativa) throws DAOException {
		String hql = "select escolaInep" +
				" from EscolaInep escolaInep" +
				" where 1 = 1" +
				(idUnidadeFederativa > 0 ? " and escolaInep.endereco.unidadeFederativa.id = :idUnidadeFederativa":"") +
				(idMunicipio > 0 ? " and escolaInep.endereco.municipio.id = :idMunicipio":"") +
				" order by escolaInep.nome";
		Query q = getSession().createQuery(hql);
		q.setInteger("idUnidadeFederativa", idUnidadeFederativa);
		q.setInteger("idMunicipio", idMunicipio);
		return q.list();
	}
	
	/** Retorna uma cole��o de EscolasInep de acordo com o nome especificado.
	 * @param nome
	 * @return Cole��o de EscolasInep
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EscolaInep> findByNome(String nome) throws DAOException {
		Criteria c = getSession().createCriteria(EscolaInep.class);
		c.add(Restrictions.ilike("nomeAscii", StringUtils.toAscii(nome), MatchMode.ANYWHERE));
		c.createCriteria("endereco").createCriteria("unidadeFederativa").addOrder(Order.asc("sigla"));
		c.addOrder(Order.asc("nome"));
		return c.list();
	}
	
	/** Retorna uma cole��o de EscolasInep de uma determinada Unidade Federativa, de acordo com o nome especificado.
	 * @param nome da escola a procurar
	 * @param idUnidadeFederativa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EscolaInep> findByNomeUF(String nome, int idUnidadeFederativa) throws DAOException {
		Criteria c = getSession().createCriteria(EscolaInep.class);
		c.add(Restrictions.ilike("nomeAscii", StringUtils.toAscii(nome), MatchMode.ANYWHERE));
		c.createCriteria("endereco").createCriteria("unidadeFederativa").add(
				Restrictions.eq("id", idUnidadeFederativa)).addOrder(
				Order.asc("sigla"));
		c.addOrder(Order.asc("nome"));
		return c.list();
	}
	
}
