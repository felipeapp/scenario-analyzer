/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 29/07/2013
 *
 */
package br.ufrn.sigaa.vestibular.dao;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.vestibular.dominio.EfetivacaoCadastramentoReserva;

/**
 * DAO respnosável por consultas específicas à Efetivação de Cadastramento de Discentes
 * @author Édipo Elder F. de Melo
 *
 */
public class EfetivacaoCadastramentoReservaDao extends GenericDAOImpl {

	/** Retorna uma coleção de {@link EfetivacaoCadastramentoReserva} de um determinado processo seletivo.
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<EfetivacaoCadastramentoReserva> findByProcessoSeletivo(int idProcessoSeletivo) throws HibernateException, DAOException {
		String projecao = "distinct ecr.id_efetivacao_cadastramento_reserva as id , ecr.id_registro_entrada as efetuadoPor.id, ecr.data_cadastro as dataCadastro";
		String sql = "select " + HibernateUtils.removeAliasFromProjecao(projecao) +
				" from vestibular.convocacao_efetivada_cadastramento_reserva" +
				" inner join vestibular.efetivacao_cadastramento_reserva ecr using (id_efetivacao_cadastramento_reserva)" +
				"inner join vestibular.convocacao_processo_seletivo_discente cpsd using (id_convocacao_processo_seletivo_discente)" +
				"inner join vestibular.convocacao_processo_seletivo cps using (id_convocacao_processo_seletivo)" +
				"where id_processo_seletivo = :idProcessoSeletivo";
		Query q = getSession().createSQLQuery(sql).setInteger("idProcessoSeletivo", idProcessoSeletivo);
		@SuppressWarnings("unchecked")
		Collection<EfetivacaoCadastramentoReserva> lista = HibernateUtils.parseTo(q.list(), projecao, EfetivacaoCadastramentoReserva.class);
		return lista;
	}

}
