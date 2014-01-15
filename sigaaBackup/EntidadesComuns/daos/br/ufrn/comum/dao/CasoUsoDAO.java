/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.comum.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.CasoUso;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;

/**
 * DAO para buscas relacionadas à entidade CasoUso.
 * 
 * @author David Pereira
 *
 */
public class CasoUsoDAO extends GenericSharedDBDao {

	/**
	 * Busca todos os casos de uso que estão associados ao sub-sistema
	 * passado como parâmetro.
	 * @param idSubSistema
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<CasoUso> findCasosUsoBySubSistema(int idSubSistema) throws DAOException {
		Criteria c = getCriteria(CasoUso.class);
		c.add(Restrictions.eq("subSistema.id", idSubSistema));
		c.addOrder(Order.asc("nome"));
		return c.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<CasoUso> findByCasoUso(CasoUso casoUso) throws DAOException {
		Criteria c = getCriteria(CasoUso.class);
		if(!ValidatorUtil.isEmpty(casoUso.getCodigo()))
			c.add(Restrictions.ilike("codigo", "%"+casoUso.getCodigo()+"%"));
		
		if(!ValidatorUtil.isEmpty(casoUso.getNome()))
			c.add(Restrictions.ilike("nome", "%"+casoUso.getNome()+"%"));
		
		if(casoUso.getSistema()!=null && casoUso.getSistema().getId()>0)
			c.add(Restrictions.eq("sistema.id", casoUso.getSistema().getId()));
		
		if(casoUso.getSubSistema()!=null && casoUso.getSubSistema().getId()>0)
			c.add(Restrictions.eq("subSistema.id", casoUso.getSubSistema().getId()));
		
		c.addOrder(Order.asc("nome"));
		return c.list();
	}
	
	
	/**
	 * Retorna a lista de todos os casos de uso agrupados por sistema
	 * e sub-sistema.
	 * @return
	 */
	public Map<Sistema, Map<SubSistema, List<CasoUso>>> findCasosUsoPorSistema(CasoUso casoUsoFiltro) throws DAOException {
		Map<Sistema, Map<SubSistema, List<CasoUso>>> casosUsoPorSistema = new ConcurrentSkipListMap<Sistema, Map<SubSistema,List<CasoUso>>>(new Comparator<Sistema>() {
			public int compare(Sistema s1, Sistema s2) {
				return s1.getNome().compareTo(s2.getNome());
			}
		});
		
		Collection<CasoUso> casosUso = findByCasoUso(casoUsoFiltro);
		
		for (CasoUso casoUso : casosUso) {
			if (!casosUsoPorSistema.containsKey(casoUso.getSistema())) {
				casosUsoPorSistema.put(casoUso.getSistema(), new ConcurrentSkipListMap<SubSistema, List<CasoUso>>(new Comparator<SubSistema>() {
					public int compare(SubSistema s1, SubSistema s2) {
						return s1.getNome().compareTo(s2.getNome());
					}
				}));
			} 
			
			Map<SubSistema, List<CasoUso>> subSistemas = casosUsoPorSistema.get(casoUso.getSistema());
			if (casoUso.getSubSistema() != null) {
				if (!subSistemas.containsKey(casoUso.getSubSistema())) {
					subSistemas.put(casoUso.getSubSistema(), new ArrayList<CasoUso>());
				}
				
				List<CasoUso> lista = subSistemas.get(casoUso.getSubSistema());
				lista.add(casoUso);
			}
			
		}
		
		return casosUsoPorSistema;
	}

	/**
	 * Retorna a lista de todos os casos de uso agrupados por sistema
	 * e sub-sistema.
	 * @return
	 */
	public Map<Sistema, Map<SubSistema, List<CasoUso>>> findCasosUsoPorSistema() throws DAOException {
		Map<Sistema, Map<SubSistema, List<CasoUso>>> casosUsoPorSistema = new ConcurrentSkipListMap<Sistema, Map<SubSistema,List<CasoUso>>>(new Comparator<Sistema>() {
			public int compare(Sistema s1, Sistema s2) {
				return s1.getNome().compareTo(s2.getNome());
			}
		});
		
		Collection<CasoUso> casosUso = findAll(CasoUso.class);
		
		for (CasoUso casoUso : casosUso) {
			if (!casosUsoPorSistema.containsKey(casoUso.getSistema())) {
				casosUsoPorSistema.put(casoUso.getSistema(), new ConcurrentSkipListMap<SubSistema, List<CasoUso>>(new Comparator<SubSistema>() {
					public int compare(SubSistema s1, SubSistema s2) {
						return s1.getNome().compareTo(s2.getNome());
					}
				}));
			} 
			
			Map<SubSistema, List<CasoUso>> subSistemas = casosUsoPorSistema.get(casoUso.getSistema());
			if (!subSistemas.containsKey(casoUso.getSubSistema())) {
				subSistemas.put(casoUso.getSubSistema(), new ArrayList<CasoUso>());
			}
			
			List<CasoUso> lista = subSistemas.get(casoUso.getSubSistema());
			lista.add(casoUso);
		}
		
		return casosUsoPorSistema;
	}
	
}
