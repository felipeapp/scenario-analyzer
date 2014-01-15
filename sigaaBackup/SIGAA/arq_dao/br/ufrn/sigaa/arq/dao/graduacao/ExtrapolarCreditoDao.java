/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '07/07/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ExtrapolarCredito;
import br.ufrn.sigaa.pessoa.dominio.Discente;

public class ExtrapolarCreditoDao extends GenericSigaaDAO {

	@SuppressWarnings("deprecation") 
	public ExtrapolarCredito findPermissaoAtivo(Discente discente, int ano, int periodo) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append("select" +
				" ec.id, ec.discente.id," +
				" ec.extrapolarMaximo," +
				" ec.crMaximoExtrapolado," +
				" ec.crMinimoExtrapolado" +
				" from ExtrapolarCredito ec" +
				" where ec.discente.id = :idDiscente" +
				" and ano = :ano" +
				" and periodo = :periodo" +
				" and ativo = :ativo");

		Query query = getSession().createQuery(hql.toString());

		query.setInteger("idDiscente", discente.getId());
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
		query.setBoolean("ativo", new Boolean(true));

		Object[] resultado = (Object[]) query.uniqueResult();
		
		ExtrapolarCredito extrapolar = null;
		if (resultado != null) {
			int cont = 0;
			extrapolar = new ExtrapolarCredito();
			extrapolar.setId((Integer) resultado[cont++]);
			extrapolar.setDiscente(new DiscenteGraduacao());
			extrapolar.getDiscente().setId((Integer) resultado[cont++]);
			extrapolar.setExtrapolarMaximo((Boolean) resultado[cont++]);
			extrapolar.setCrMaximoExtrapolado((Integer) resultado[cont++]);
			extrapolar.setCrMinimoExtrapolado((Integer) resultado[cont++]);
		}
		
		return extrapolar;

	}

	@SuppressWarnings("unchecked")
	public Collection<ExtrapolarCredito> findByDiscenteAtivo(DiscenteAdapter discente) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select ec from ExtrapolarCredito ec where ec.discente.id = :idDiscente and ec.ativo = trueValue()");

		Query q = getSession().createQuery(hql.toString());

		q.setInteger("idDiscente", discente.getId());

		return q.list();
	}

}
