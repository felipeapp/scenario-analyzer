package br.ufrn.sigaa.assistencia.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * DAO responsável pelas consultas relacionadas a calendários de solicitação de bolsas de 
 * auxílio ao estudante.
 * 
 * @author wendell
 *
 */
public class CalendarioBolsaAuxilioDao extends GenericSigaaDAO {

	/**
	 * Retorna se existe algum calendário definido para o tipo de bolsa auxílio e municípios informados no ano corrente.
	 * 
	 * @param tipoBolsaAuxilio
	 * @param municipio
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public boolean isCalendarioDefinido(Integer tipoBolsaAuxilio, Municipio municipio) throws DAOException {
		String hql = "select count(c.id) from CalendarioBolsaAuxilio c" +
				" where c.tipoBolsaAuxilio.id = " + tipoBolsaAuxilio +
				" and c.municipio.id = " + municipio.getId() +
				" and year(c.inicio) = " + CalendarUtils.getAnoAtual();
		Query q = getSession().createQuery(hql);
		return ((Long) q.uniqueResult()) > 0;
	}

	@SuppressWarnings("unchecked")
	public Collection<AnoPeriodoReferenciaSAE> findAllPeriodosCadatrados() throws DAOException {
		String projecao = " ano.id, ano.ano, ano.periodo"; 

		String hql = "SELECT DISTINCT " + 
				HibernateUtils.removeAliasFromProjecao(projecao) + 
				" FROM AnoPeriodoReferenciaSAE ano" +
				" WHERE ano.ativo = trueValue() " +
				" order by ano.ano desc, ano.periodo desc ";
		
		Query q = getSession().createQuery(hql);
		return (List<AnoPeriodoReferenciaSAE>) HibernateUtils.parseTo(q.list(), projecao, AnoPeriodoReferenciaSAE.class, "ano");
	}

	public void inativarCalendarios(AnoPeriodoReferenciaSAE ano) {
		update("update sae.ano_periodo_referencia set vigente=? where id <> ?",
				new Object[] { Boolean.FALSE, ano.getId() });
	}

}