/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 20/01/2010
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.RestricaoInscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.TipoRestricaoInscricaoVestibular;

/**
 * Classe responsável por consultas específicas as restrições de inscrição ao
 * vestibular.
 * @see RestricaoInscricaoVestibular
 * @author Édipo Elder F. Melo
 * 
 */
public class RestricaoInscricaoVestibularDao extends GenericSigaaDAO {

	/**
	 * Retorna uma coleção de restrições à inscrição do vestibular. 
	 * @param idMatrizCurricular opcional. Caso seja zero, não restringirá a busca.
	 * @param idProcessoSeletivo opcional. Caso seja zero, não restringirá a busca.
	 * @return
	 * @throws DAOException
	 */
	public Collection<RestricaoInscricaoVestibular> findByProcessoSeletivoMatrizCurricular(int idProcessoSeletivo, int idMatrizCurricular) throws DAOException {
		Criteria c = getSession().createCriteria(RestricaoInscricaoVestibular.class);
		c.setFetchMode("processoSeletivoVestibular", FetchMode.JOIN);
		if (idProcessoSeletivo > 0)
			c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		if (idMatrizCurricular > 0)
			c.createCriteria("matrizCurricular").add(Restrictions.eq("id", idMatrizCurricular));
		@SuppressWarnings("unchecked")
		Collection<RestricaoInscricaoVestibular>  lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna uma restrição de inscrição do vestibular 
	 * @param idProcessoSeletivo
	 * @param idMatrizCurricular
	 * @param tipoRestricao
	 * @return
	 * @throws DAOException
	 */
	public RestricaoInscricaoVestibular findByProcessoSeletivoMatrizCurricularTipoRestricao(int idProcessoSeletivo, int idMatrizCurricular, int tipoRestricao) throws DAOException {
		Criteria c = getSession().createCriteria(RestricaoInscricaoVestibular.class);
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		c.createCriteria("matrizCurricular").add(Restrictions.eq("id", idMatrizCurricular));
		c.add(Restrictions.eq("tipoRestricao", tipoRestricao));
		return (RestricaoInscricaoVestibular) c.uniqueResult();
	}
	
	/**
	 * Retorna as restrições de exceção de inscrição de um CPF em um processo seletivo. 
	 * @param cpf
	 * @param idProcessoSeletivo 
	 * @return
	 * @throws DAOException
	 */
	public Collection<RestricaoInscricaoVestibular> findAllExcetoAByProcessoSeletivoCpf(int idProcessoSeletivo, Long cpf) throws DAOException {
		Criteria c = getSession().createCriteria(RestricaoInscricaoVestibular.class);
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		c.add(Restrictions.eq("tipoRestricao", TipoRestricaoInscricaoVestibular.EXCETO_A));
		@SuppressWarnings("unchecked")
		Collection<RestricaoInscricaoVestibular> lista = c.list();
		if (lista != null) {
			Iterator<RestricaoInscricaoVestibular> iterator = lista.iterator();
			while (iterator.hasNext()) {
				if (!iterator.next().getCpfs().contains(cpf))
					iterator.remove();
			}
		}
		return lista;
	}
	
	/**
	 * Retorna as restrições para a inscrição exclusivas. 
	 * @param idProcessoSeletivo 
	 * @return
	 * @throws DAOException
	 */
	public Collection<RestricaoInscricaoVestibular> findAllExclusivoAByProcessoSeletivo(int idProcessoSeletivo) throws DAOException {
		Criteria c = getSession().createCriteria(RestricaoInscricaoVestibular.class);
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		c.add(Restrictions.eq("tipoRestricao", TipoRestricaoInscricaoVestibular.EXCLUSIVO_A));
		@SuppressWarnings("unchecked")
		Collection<RestricaoInscricaoVestibular> lista = c.list();
		return lista;
	}

}
