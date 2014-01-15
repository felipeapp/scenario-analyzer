package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * DAO respons�vel pelas consultas relacionadas � mudan�a curricular coletiva.
 * 
 * @author Bernardo
 *
 */
public class MudancaCurricularColetivaDao extends GenericSigaaDAO {

	/**
	 * Retorna os discentes de um curr�culo a partir de um ano-per�odo
	 * de ingresso
	 * 
	 * @param idCurriculo
	 * @param anoIngresso
	 * @param periodoIngresso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteGraduacao> findByCurriculoAnoPeriodo(int idCurriculo, Integer anoIngresso, Integer periodoIngresso) throws DAOException {
		Criteria c = getSession().createCriteria(DiscenteGraduacao.class);
		Criteria sc = c.createCriteria("discente");
		
		sc.add(Restrictions.eq("curriculo.id", idCurriculo));
		sc.add(Restrictions.in("status", new Integer[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.TRANCADO}));
		
		if(anoIngresso != null && anoIngresso > 0 && periodoIngresso != null && periodoIngresso > 0){
			sc.add(Restrictions.eq("anoIngresso", anoIngresso));
			sc.add(Restrictions.eq("periodoIngresso", periodoIngresso));
		}
		
		return c.list();
	}
	
	/**
	 * Retorna os discentes de gradua��o por matriz e curr�culo.
	 * @param idMatriz
	 * @param idCurriculo
	 * @param anoIngresso
	 * @param periodoIngresso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<DiscenteGraduacao> findDiscentesByMatriz(Integer idMatriz, Integer idCurriculo, Integer anoIngresso, Integer periodoIngresso) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		hql.append("select discenteGraduacao from DiscenteGraduacao discenteGraduacao" +
					" inner join fetch discenteGraduacao.discente discente" +
					" inner join fetch discente.curso curso" +
					" inner join fetch discente.pessoa pessoa" +
					" inner join fetch discenteGraduacao.matrizCurricular matriz" +
					" inner join fetch discente.curriculo curriculo" +
					" where discente.status in "+ UFRNUtils.gerarStringIn(StatusDiscente.getAtivos()) );
		hql.append(" and matriz.id = :idMatriz" );
		if ( idCurriculo != null && idCurriculo > 0 )
			hql.append(" and curriculo.id = :idCurriculo" );
		if ( anoIngresso != null && anoIngresso > 0)
			hql.append(" and discente.anoIngresso = :anoIngresso" );
		if ( periodoIngresso != null && periodoIngresso > 0 )
			hql.append(" and discente.periodoIngresso = :periodoIngresso" );
		
		hql.append(" order by pessoa.nome" );
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idMatriz", idMatriz);
		if ( idCurriculo != null && idCurriculo > 0 )
			q.setInteger("idCurriculo", idCurriculo);
		if ( anoIngresso != null && anoIngresso > 0 )
			q.setInteger("anoIngresso", anoIngresso);
		if ( periodoIngresso != null && periodoIngresso > 0 )
			q.setInteger("periodoIngresso", periodoIngresso);
		return q.list();
	}
	
}
