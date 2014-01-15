/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 31/05/2011
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.projetos.dominio.ModalidadeBolsaExterna;

/**
 * DAO para consultas relacionadas ao Atividades de Plano de Docência Assistida.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class AtividadesDocenciaAssistidaDao extends GenericSigaaDAO  {
	
	
	/**
	 * Retorna a quantidade de planos por atividades
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQuantAtividades(Integer ano, Integer periodo, Unidade programa, Character nivel, 
			ModalidadeBolsaExterna modalidade) throws HibernateException, DAOException{
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" select fa.descricao, count(p.id) as total from AtividadeDocenciaAssistida atv ");
		hql.append(" left join atv.formaAtuacao fa ");
		hql.append(" inner join atv.planoDocenciaAssistida p ");
		hql.append(" INNER JOIN p.discente.discente as d ");
		hql.append(" LEFT JOIN p.modalidadeBolsa as mb ");
		hql.append(" INNER JOIN d.gestoraAcademica as ga ");		
		
		hql.append(" where p.ano = :ano ");
		hql.append(" and p.periodo = :periodo ");
		
		if (programa != null && programa.getId() > 0)
			hql.append(" and ga.id = "+programa.getId());
		
		if (!ValidatorUtil.isEmpty(nivel) && nivel != '0')
			hql.append(" and d.nivel = '"+nivel+"'");
		
		if (modalidade != null && modalidade.getId() > 0)
			hql.append(" and mb.id = "+modalidade.getId());		
		
		hql.append(" group by fa.descricao ");
		hql.append(" order by count(p.id) desc ");
		
		Query q = getSession().createQuery(hql.toString());
		
   		q.setInteger("ano", ano);    
   		q.setInteger("periodo", periodo);
   		
   		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();   		
   		return lista;
	}
	
	/**
	 * Retorna Outras Atividades informadas nos planos no ano e período informados
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findOutrasAtividades(Integer ano, Integer periodo, Unidade programa, Character nivel, 
			ModalidadeBolsaExterna modalidade) throws HibernateException, DAOException{
		
		StringBuilder hql = new StringBuilder();

		hql.append(" select atv.outraAtividade, count(p.id) as total from AtividadeDocenciaAssistida atv ");
		hql.append(" inner join atv.planoDocenciaAssistida p ");
		hql.append(" INNER JOIN p.discente.discente as d ");
		hql.append(" LEFT JOIN p.modalidadeBolsa as mb ");
		hql.append(" INNER JOIN d.gestoraAcademica as ga ");
		
		hql.append(" where p.ano = :ano ");
		hql.append(" and p.periodo = :periodo ");
		hql.append(" and atv.formaAtuacao is null");
		
		if (programa != null && programa.getId() > 0)
			hql.append(" and ga.id = "+programa.getId());
		
		if (!ValidatorUtil.isEmpty(nivel) && nivel != '0')
			hql.append(" and d.nivel = '"+nivel+"'");
		
		if (modalidade != null && modalidade.getId() > 0)
			hql.append(" and mb.id = "+modalidade.getId());				
		
		hql.append(" group by atv.outraAtividade ");
		hql.append(" order by atv.outraAtividade ");
		
		Query q = getSession().createQuery(hql.toString());
		
   		q.setInteger("ano", ano);    
   		q.setInteger("periodo", periodo);
   		
   		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();   		
   		return lista;
	}
	

}
