/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/10/2011'
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;

/**
 * Classe responsável pelas consultas referentes as siglas das Unidades de Pesquisa.
 * 
 * @author Jean Guerethes
 */
public class SiglaUnidadePesquisaDao extends GenericSigaaDAO {
	
	@SuppressWarnings("unchecked")
	public Collection<SiglaUnidadePesquisa> findSiglasUnidadesCadastradas( SiglaUnidadePesquisa siglaUnid ) throws HibernateException, DAOException {
    	String hql = "FROM SiglaUnidadePesquisa sup WHERE ";

    	if ( !isEmpty( siglaUnid.getUnidade() ) )
    		hql += " sup.unidade.id = :unidade";
    	if ( !isEmpty( siglaUnid.getSigla() ) )
    		hql += (" or sup.sigla = :sigla");

    	Query query = getSession().createQuery(hql.toString());

    	if ( !isEmpty( siglaUnid.getUnidade() ) )
    		query.setInteger("unidade", siglaUnid.getUnidade().getId());
    	if ( !isEmpty( siglaUnid.getSigla() ) )
    		query.setCharacter("sigla", siglaUnid.getSigla() );

    	return query.list();
	}

}