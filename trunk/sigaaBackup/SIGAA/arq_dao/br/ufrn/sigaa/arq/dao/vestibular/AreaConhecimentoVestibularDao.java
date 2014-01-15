/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '21/07/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.AreaConhecimentoVestibular;

public class AreaConhecimentoVestibularDao extends GenericSigaaDAO {

	/** Recupera as distintas área de conhecimento dos cursos ofertados para o Processo Seletivo especificado 
	 * @param idFormaIngresso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AreaConhecimentoVestibular> findByFormaIngressoAnoPeriodo(int idFormaIngresso, int ano, int periodo) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder("select distinct oferta.curso.areaVestibular" +
				" from OfertaVagasCurso oferta" +
				" where oferta.formaIngresso.id = :idFormaIngresso" +
				" and oferta.ano = :ano");
		if (periodo > 0)
			hql.append(" and oferta.vagasPeriodo" + periodo+" > 0");
		hql.append(" order by oferta.curso.areaVestibular.descricao");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idFormaIngresso", idFormaIngresso);
		q.setInteger("ano", ano);
		return q.list();
	}
}
