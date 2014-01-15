/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/07/2011
 *
 */	
package br.ufrn.sigaa.ensino.medio.dao;

import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RegraNota;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.NotaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;

/**
 * DAO com consultas relativas à entidade NotaSerie.
 * 
 * @author Arlindo
 *
 */
public class NotaSerieDao extends GenericSigaaDAO {
	
	/**
	 * Retorna as notas de cada disciplina do discente e matricula informados 
	 * @param discenteMedio
	 * @return
	 * @throws DAOException
	 */
	public List<NotaSerie> findNotasByDiscente(DiscenteMedio discenteMedio, TurmaSerie turmaSerie ) throws DAOException {
				
		StringBuffer hql = new StringBuffer();
		String projecao = " ns.notaUnidade.matricula.id, ns.regraNota.id, " +
				" ns.regraNota.ordem, ns.notaUnidade.faltas, ns.notaUnidade.recuperacao ";
		
		// Precisa fazer o cast pois senão o hibernate traz o valor da nota errado, por exemplo: 4.2 virará 4.19999...
		String projecaoSelect = projecao + ", cast(ns.notaUnidade.nota as float) as nota ";
		// Note que como está sendo utilizado o parse do HibernateUtils é preciso fazer outra projeção e desfazer o cast na lista de objetos.
		String projecaoParse = projecao + ", ns.notaUnidade.nota ";
		
		hql.append(" select "+projecaoSelect+" from NotaSerie ns ");
		
		hql.append(" inner join ns.regraNota rn ");
	    hql.append(" inner join ns.notaUnidade nu ");
	    hql.append(" inner join ns.notaUnidade.matricula m ");
	    hql.append(" inner join m.turma t ");
	    
	    hql.append(" where nu.ativo = trueValue() and m.discente.id = "+discenteMedio.getId());
	    hql.append(" and t.id in ( ");
	    hql.append("  	select tsa.turma.id from TurmaSerieAno tsa ");
	    hql.append("  	inner join tsa.turma t ");
	    hql.append("  	inner join tsa.turmaSerie ts ");
	    hql.append("  	where ts.id = "+turmaSerie.getId());
	    hql.append("  ) ");	    
	    
	    hql.append(" order by m.id, rn.ordem ");
	    
	    @SuppressWarnings("unchecked")
	    List<Object[]> valores = getSession().createQuery(hql.toString()).list();

	    // Desfazendo o cast na lista de objetos para o parse do HibernateUtils funcionar.
	    if (valores != null){
	    	for ( Object[] ob : valores ){
	    		Integer posNota = ob.length-1;
	    		if (ob[posNota] != null)
	    			ob[posNota] = Double.valueOf(ob[posNota].toString());	 
	    	}	
	    }

	    List<NotaSerie> lista = (List<NotaSerie>) HibernateUtils.parseTo(valores, projecaoParse, NotaSerie.class, "ns");
        return lista;
		
	}		
	
	/**
	 * Retorna as notas por disciplina do discente e matricula informados 
	 * @param discenteMedio
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<NotaSerie> findNotasByDisciplina(DiscenteMedio discenteMedio, MatriculaComponente matricula ) throws DAOException {
				
		StringBuffer hql = new StringBuffer();
		
		String projecao = " ns.id, ns.notaUnidade.matricula.id, ns.regraNota.id, " +
				" ns.regraNota.ordem, ns.regraNota.tipo, ns.notaUnidade.id, " +
				" ns.notaUnidade.faltas, ns.notaUnidade.recuperacao, ns.notaUnidade.unidade ";
		
		// Precisa fazer o cast pois senão o hibernate traz o valor da nota errado, por exemplo: 4.2 virará 4.19999...
		String projecaoSelect = projecao + ", cast(ns.notaUnidade.nota as float) as nota ";
		// Note que como está sendo utilizado o parse do HibernateUtils é preciso fazer outra projeção e desfazer o cast na lista de objetos.
		String projecaoParse = projecao + ", ns.notaUnidade.nota ";

		hql.append(" select "+projecaoSelect+" from NotaSerie ns ");
		
		hql.append(" inner join ns.regraNota rn ");
	    hql.append(" inner join ns.notaUnidade nu ");
	    hql.append(" inner join ns.notaUnidade.matricula m ");
	    hql.append(" inner join m.turma t ");
	    
	    hql.append(" where nu.ativo = trueValue() and m.discente.id = "+discenteMedio.getId());
	    hql.append(" and m.id = "+matricula.getId());
	    
	    hql.append(" order by m.id, rn.ordem ");
	    
	    List<Object[]> valores = getSession().createQuery(hql.toString()).list();
	    
	    // Desfazendo o cast na lista de objetos para o parse do HibernateUtils funcionar.
	    if (valores != null){
	    	for ( Object[] ob : valores ){
	    		Integer posNota = ob.length-1;
	    		if (ob[posNota] != null)
	    			ob[posNota] = Double.valueOf(ob[posNota].toString());	 
	    	}	
	    }
	    		
	    List<NotaSerie> lista = (List<NotaSerie>) HibernateUtils.parseTo(valores, projecaoParse, NotaSerie.class, "ns");
        return lista;
		
	}		

	/**
	 * Retorna as notas da matricula componente e regra informada 
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public List<NotaSerie> findNotasByMatriculaComponente(MatriculaComponente matricula, RegraNota regra ) throws DAOException {
				
		StringBuffer hql = new StringBuffer();
		String projecao = " ns.notaUnidade.matricula.id, ns.regraNota.id, ns.regraNota.tipo, ns.notaUnidade.unidade, " +
				" ns.regraNota.ordem, ns.notaUnidade.nota, ns.notaUnidade.faltas, ns.notaUnidade.recuperacao ";
		
		hql.append(" select "+projecao+" from NotaSerie ns ");
		
		hql.append(" inner join ns.regraNota rn ");
	    hql.append(" inner join ns.notaUnidade nu ");
	    hql.append(" inner join ns.notaUnidade.matricula m ");
	    hql.append(" inner join m.turma t ");
	    
	    hql.append(" where nu.ativo = trueValue() and m.id = "+matricula.getId());
	    
	    if (ValidatorUtil.isNotEmpty(regra))
	    	hql.append(" and rn.id = "+regra.getId());
	    
	    hql.append(" order by m.id, rn.ordem, nu.unidade ");
	    
	    @SuppressWarnings("unchecked")
	    List<NotaSerie> lista = (List<NotaSerie>) HibernateUtils.parseTo(
	    		getSession().createQuery(hql.toString()).list(), projecao, NotaSerie.class, "ns");
        return lista;
		
	}	
}
