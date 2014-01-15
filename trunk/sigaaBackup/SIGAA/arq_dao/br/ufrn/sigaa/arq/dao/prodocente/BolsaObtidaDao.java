/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '23/11/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoBolsaProdocente;
import br.ufrn.sigaa.prodocente.producao.dominio.BolsaObtida;

/**
 * @author Ricardo Wendell
 *
 */
public class BolsaObtidaDao extends GenericSigaaDAO {

	/**
	 * Buscar bolsas de produtividade cadastradas
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<BolsaObtida> findBolsistasProdutividade(Unidade departamento, Servidor docente, Integer ano) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append(" select b.id, b.servidor.id, b.servidor.pessoa.nome, b.anoReferencia, b.periodoInicio, b.periodoFim, b.tipoBolsaProdocente.descricao");
		hql.append(" from BolsaObtida b ");
		hql.append(" where b.tipoBolsaProdocente.produtividade = trueValue() and (b.ativo is null or b.ativo != falseValue()) ");

		if ( departamento != null ) {
			hql.append(" and b.servidor.unidade.id = " + departamento.getId());
		}
		if ( docente != null ) {
			hql.append(" and b.servidor.id = " + docente.getId());
		}
		if ( ano != null ) {
			hql.append(" and year(b.periodoInicio) <= " + ano + " and year(b.periodoFim) >= " + ano);
		}

		hql.append(" order by b.servidor.pessoa.nome, b.periodoInicio, b.periodoFim");

		List lista = getSession().createQuery(hql.toString()).list();

    	Collection<BolsaObtida> bolsas = new ArrayList<BolsaObtida>();
    	for (int a = 0; a < lista.size(); a++) {
    		BolsaObtida bolsa = new BolsaObtida();
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);

			bolsa.setId( (Integer) colunas[col++] );
			bolsa.setServidor( new Servidor((Integer) colunas[col++]) );
			bolsa.getServidor().getPessoa().setNome( (String) colunas[col++] );
			bolsa.setAnoReferencia( (Integer) colunas[col++] );
			bolsa.setPeriodoInicio( (Date) colunas[col++] );
			bolsa.setPeriodoFim( (Date) colunas[col++] );
			bolsa.setTipoBolsaProdocente( new TipoBolsaProdocente() );
			bolsa.getTipoBolsaProdocente().setDescricao( (String) colunas[col++] );

			bolsas.add(bolsa);
    	}
    	return bolsas;
	}

	/**
	 * Verificar se um servidor é um bolsista de produtividade ativo
	 *
	 * @param servidor
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean isBolsistaProdutividade(Servidor servidor) throws HibernateException, DAOException {
		String hql = "select bo.id from BolsaObtida bo " +
				" where bo.servidor.id = " +  servidor.getId() +
				" and bo.tipoBolsaProdocente.produtividade = trueValue() " +
				" and bo.periodoInicio <= :hoje and bo.periodoFim >= :hoje";

		return !getSession().createQuery(hql)
			.setDate("hoje", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
			.list().isEmpty();
	}
	
	/**
	 * Verificar se um servidor é um bolsista de produtividade ativo
	 *
	 * @param servidor
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Integer> bolsistasProdutividade() throws HibernateException, DAOException {
		String hql = "select id_servidor " +
				"from prodocente.bolsa_obtida " +
				"left join prodocente.tipo_bolsa tb using ( id_tipo_bolsa ) " +
				"where periodo_inicio >= :hoje and periodo_fim <= :hoje " +
				"and tb.produtividade is true";

		return getSession().createSQLQuery(hql)
			.setDate("hoje", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)).list();
	}
	
	

}
