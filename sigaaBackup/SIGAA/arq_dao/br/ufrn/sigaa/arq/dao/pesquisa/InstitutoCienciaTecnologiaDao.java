/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '18/08/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pesquisa.dominio.InstitutoCienciaTecnologia;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao Institutos Nacionais de Ci�ncia e Tecnologia
 * @author Geyson Karlos
 *
 */

public class InstitutoCienciaTecnologiaDao extends GenericSigaaDAO{
	
	/**
	 * retorna todos os institutos de ci�ncia e tecnologia por unidade federativa.
	 * @param unidadeFederativa
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InstitutoCienciaTecnologia> findByUnidadeFederativa(UnidadeFederativa unidadeFederativa) throws DAOException
	{
		Criteria c = getSession().createCriteria(InstitutoCienciaTecnologia.class);
		c.add(Restrictions.eq("unidadeFederativa", unidadeFederativa));
		c.add(Restrictions.or(Restrictions.eq("ativo", true), Restrictions.isNull("ativo")));
		return c.list();
	}
	
	/**
	 * retorna todos os institutos de ci�ncia e tecnologia por coordenador.
	 * @param servidor
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InstitutoCienciaTecnologia> findByCoordenador(Servidor servidor) throws DAOException
	{
		Criteria c = getSession().createCriteria(InstitutoCienciaTecnologia.class);
		c.add(Restrictions.eq("coordenador", servidor));
		c.add(Restrictions.or(Restrictions.eq("ativo", true), Restrictions.isNull("ativo")));
		return c.list();
	}
	
	/**
	 * retorna todos os institutos de ci�ncia e tecnologia por coordenador e unidade federativa
	 * @param servidor
	 * @param unidadeFederativa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<InstitutoCienciaTecnologia> findByCoordenadorUnidadeFederativa(Servidor servidor, UnidadeFederativa unidadeFederativa) throws DAOException
	{
		Criteria c = getSession().createCriteria(InstitutoCienciaTecnologia.class);
		c.add(Restrictions.eq("coordenador", servidor));
		c.add(Restrictions.eq("unidadeFederativa", unidadeFederativa));
		c.add(Restrictions.or(Restrictions.eq("ativo", true), Restrictions.isNull("ativo")));
		return c.list();
	}
	

}
