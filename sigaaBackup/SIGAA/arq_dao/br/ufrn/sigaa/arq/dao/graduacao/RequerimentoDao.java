/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/05/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.Requerimento;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoRequerimento;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * @author Henrique Andre
 *
 */
public class RequerimentoDao extends GenericSigaaDAO {

	/**
	 * Todos os requerimentos do discente
	 * @param discente
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<Requerimento> findAllByDiscente(DiscenteAdapter discente) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();

			hql.append("select r from Requerimento r where r.discente.id = :idDiscente order by r.dataAtualizado desc");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idDiscente", discente.getId());

			return q.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public Collection<Requerimento> FindAllByNomeMatriculaRequerimento(Discente discente, Integer numRequerimento) throws HibernateException, DAOException {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append("select r from Requerimento r where r.tipo.id = :tipo");
		if (discente != null)
			hql.append(" and r.discente.id = :discente");
		if (numRequerimento != null)
			hql.append(" and r.codigoProcesso = :numRequerimento");
		
		hql.append(" order by r.codigoProcesso");
		
		Query q = getSession().createQuery(hql.toString());
		
		if (discente != null)
			q.setInteger("discente", discente.getId());
		if (numRequerimento != null)
			q.setInteger("numRequerimento", numRequerimento);
		
		q.setInteger("tipo", TipoRequerimento.TRANCAMENTO_PROGRAMA);
		
		return q.list();
	}
	
}
