/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/10/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;


/**
 * DAO responsável pelas consultas a Cota Bolsas
 *
 * @author Jean Guerethes
 */
public class CotaBolsasDao extends GenericSigaaDAO {
	
	/**
	 * Retorna o total das Cotas de Bolsas ativas no sistema.
	 */
	public long countCotaBolsasAtivas() throws DAOException {
		StringBuilder sql = new StringBuilder("SELECT count(cota) ");
		sql.append(" FROM pesquisa.cota_bolsas cota");
		sql.append(" WHERE cota.ativo = trueValue() ");
		return ((BigInteger) getSession().createSQLQuery(sql.toString()).uniqueResult()).longValue();
	}

	/**
	 * Verifica se há alguma cota de bolsa aberta para a solicitação de plano voluntário.
	 */
	public boolean hasCotaBolsasVoluntariaAberta() throws DAOException {
		StringBuilder sql = new StringBuilder("SELECT count(cota) ");
		sql.append(" FROM pesquisa.cota_bolsas cota");
		sql.append(" WHERE cota.inicio_cadastro_plano_voluntario <= '" + new Date() + "'");
		sql.append(" AND cota.fim_cadastro_plano_voluntario >= '" + new Date() + "'");
		return ((BigInteger) getSession().createSQLQuery(sql.toString()).uniqueResult()).longValue() > 0;
	}
	
	/**
	 * Retorna as cotas de bolsa aberta para a solicitação de plano voluntário.
	 */
	@SuppressWarnings("unchecked")
	public Collection<CotaBolsas> cotaBolsasVoluntariaAberta() throws DAOException {
		Criteria c = getSession().createCriteria(CotaBolsas.class);
		c.add(Restrictions.le("inicioCadastroPlanoVoluntario", new Date()) );
		c.add(Restrictions.ge("fimCadastroPlanoVoluntario", new Date()) );
		List <CotaBolsas> rs = c.list();
		return rs;
	}

}