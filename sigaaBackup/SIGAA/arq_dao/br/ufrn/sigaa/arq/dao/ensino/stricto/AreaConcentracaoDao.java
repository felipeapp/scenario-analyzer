/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on May 2, 2008
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;

/**
 *
 * @author Victor Hugo
 */
public class AreaConcentracaoDao extends GenericSigaaDAO {

	/**
	 * retorna as areas do programa / nível informado
	 * @param idPrograma
	 * @param nivel parâmetro opcional
	 * @return
	 * @throws DAOException
	 */
	public Collection<AreaConcentracao> findByProgramaNivel( int idPrograma, Character nivel ) throws DAOException{
		StringBuffer hql = new StringBuffer("SELECT area FROM AreaConcentracao area ");
		hql.append( " WHERE area.programa.id = :programa " );
		if( nivel != null )
			hql.append( " AND area.nivel = '" + nivel + "' " );

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("programa", idPrograma);

		return q.list();
	}
	
	/**
	 * retorna as área de concentração junto com suas linhas de pesquisa
	 * @param idPrograma
	 * @return
	 * @throws DAOException
	 */
	public Collection<AreaConcentracao> findByPrograma(int idPrograma) throws DAOException{
		
		StringBuffer hql = new StringBuffer("SELECT a.id,a.denominacao,a.nivel");
		hql.append( " FROM AreaConcentracao a");
		hql.append( " WHERE a.programa.id = :programa " );
		hql.append( " ORDER BY a.nivel,a.denominacao" );
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("programa", idPrograma);
		
		List<Object[]> lista = q.list();
		Collection<AreaConcentracao> areas = new ArrayList<AreaConcentracao>(0);
		for (Object[] object : lista) {
			AreaConcentracao area = new AreaConcentracao();
			area.setId((Integer) object[0]);
			area.setDenominacao((String) object[1]);
			area.setNivel((Character) object[2]);
			
			StringBuffer hql2 = new StringBuffer(" FROM LinhaPesquisaStricto l ");
			hql2.append( " WHERE l.area.id = :area " );
			Query q2 = getSession().createQuery(hql2.toString());
			q2.setInteger("area", (Integer) object[0]);
			area.setLinhasPesquisaStricto(q2.list());
		
			areas.add(area);
		}
		return areas;
		
	}
	
}
