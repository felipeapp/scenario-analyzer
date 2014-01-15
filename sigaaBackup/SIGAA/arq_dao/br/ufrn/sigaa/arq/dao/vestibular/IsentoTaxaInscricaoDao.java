/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 19/01/2010
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.vestibular.dominio.IsentoTaxaVestibular;

/** Classe responsável por consultas específicas ao cadastro de isentos.
 * @author Édipo Elder F. Melo
 *
 */
public class IsentoTaxaInscricaoDao extends GenericSigaaDAO {
	
	/** Indica se o CPF está cadastrado como isento para o determinado Processo Seletivo.
	 * @param cpf
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException 
	 */
	public boolean isCpfInscrito(Long cpf, int idProcessoSeletivo) throws DAOException {
		Collection<IsentoTaxaVestibular> lista = findByCpfProcessoSeletivo(cpf, idProcessoSeletivo);
		return lista != null && lista.size() > 0;
	}
	
	/**
	 * Retorna uma coleção de isenções da Taxa do Vestibular.
	 * @param cpf opcional. Caso seja zero, não restringirá a busca.
	 * @param idProcessoSeletivo opcional. Caso seja zero, não restringirá a busca.
	 * @return
	 * @throws DAOException
	 */
	public Collection<IsentoTaxaVestibular> findByCpfProcessoSeletivo(Long cpf, int idProcessoSeletivo) throws DAOException {
		return findByCpfProcessoSeletivo(cpf, idProcessoSeletivo, null);
	}

	/**
	 * Retorna uma coleção de isenções da Taxa do Vestibular de forma paginada. 
	 * @param cpf opcional. Caso seja zero, não restringirá a busca.
	 * @param idProcessoSeletivo opcional. Caso seja zero, não restringirá a busca.
	 * @return
	 * @throws DAOException
	 */
	public Collection<IsentoTaxaVestibular> findByCpfProcessoSeletivo(Long cpf, int idProcessoSeletivo, PagingInformation paging) throws DAOException {
		Criteria c = getSession().createCriteria(IsentoTaxaVestibular.class);
		c.setFetchMode("processoSeletivoVestibular", FetchMode.JOIN);
		if (idProcessoSeletivo > 0)
			c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivo));
		if (cpf > 0)
			c.add(Restrictions.eq("cpf", cpf));
		if (paging != null) {
			c.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			c.setMaxResults(paging.getTamanhoPagina());
		}
		c.addOrder(Order.asc("cpf"));
		@SuppressWarnings("unchecked")
		Collection<IsentoTaxaVestibular>  lista = c.list();
		return lista;
	}
	
    /**
	 * Retorna a quantidade de Candidatos Isentos no processo seletivo informado.
	 * 
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Integer quantCandidatosBeneficiadoIsencao(int idProcessoSeletivo) throws DAOException {
		Query q = getSession().createSQLQuery("select count(cpf) " +
				"from vestibular.isento_taxa_vestibular where id_processo_seletivo = " + idProcessoSeletivo);
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}
	
	/**
	 * Retorna a quantidade de Cadidatos Isentos cadastrados como Candidatos Pagantes.
	 * 
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Integer quatIsentoCadPagante(int idProcessoSeletivo) throws DAOException {
		Query q = getSession().createSQLQuery("select count(*) from vestibular.isento_taxa_vestibular itv " +
				"inner join vestibular.pessoa_vestibular pv on (itv.cpf = pv.cpf_cnpj) " +
				"inner join vestibular.inscricao_vestibular iv on (pv.id_pessoa = iv.id_pessoa) " +
				"where itv.id_processo_seletivo = iv.id_processo_seletivo " +
				"and iv.id_processo_seletivo = " + idProcessoSeletivo + " and iv.valor_inscricao > 0");
		return Integer.valueOf( String.valueOf( q.uniqueResult() ));
	}

	
}