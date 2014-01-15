/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 09/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ConstanteTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.EntidadeTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ItemTraducaoElementos;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.TraducaoElemento;

/**
 * DAO responsável por gerenciar o acesso e consulta a dados do {@link TraducaoElemento}.
 * 
 * @author Rafael Gomes
 *
 */
public class TraducaoElementoDao  extends GenericSigaaDAO{

	/**
	 * Retorna a entidade de tradução pela classe informada por parâmetro. 
	 * @param classe
	 * @return
	 * @throws DAOException
	 */
	public EntidadeTraducao findEntidadeByClasse(String classe) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(EntidadeTraducao.class);
			c.add(Restrictions.eq("classe", classe));
			
			return (EntidadeTraducao) c.setMaxResults(1).uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna a entidade de tradução pelo simpleName da classe informada por parâmetro. 
	 * @param classe
	 * @return
	 * @throws DAOException
	 */
	public EntidadeTraducao findEntidadeByNomeClasse(String nomeClasse) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(EntidadeTraducao.class);
			c.add(Restrictions.eq("nomeClasse", nomeClasse));
			
			return (EntidadeTraducao) c.setMaxResults(1).uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna os atributos de tradução pela classe informada por parâmetro. 
	 * @param classe
	 * @return
	 * @throws DAOException
	 */
	public Collection<ItemTraducao> findItensByClasse(String classe, Order order) throws DAOException {
		
		try {
			
			Criteria c = getSession().createCriteria(ItemTraducao.class);
			Criteria entidade = c.createCriteria("entidade");
			entidade.add(Restrictions.eq("classe", classe));
			c.addOrder(order);
			
			@SuppressWarnings("unchecked")
			List<ItemTraducao> lista = c.list();
			
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna os elementos de tradução pelo atributo e elemento informado por parâmetro. 
	 * @param classe
	 * @return
	 * @throws DAOException
	 */
	public Collection<TraducaoElemento> findByItemTraducaoAndElemento(ItemTraducao itemTraducao, Integer idElemento) throws DAOException {
		
		try {
			
			Criteria c = getSession().createCriteria(TraducaoElemento.class);
			c.add(Restrictions.eq("itemTraducao", itemTraducao));
			c.add(Restrictions.eq("idElemento", idElemento));
			
			@SuppressWarnings("unchecked")
			List<TraducaoElemento> lista = c.list();
			
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna o elemento de tradução pelo atributo, elemento e idioma informados por parâmetro. 
	 * @param itemTraducao
	 * @param idElemento
	 * @param idioma
	 * @return
	 * @throws DAOException
	 */
	public TraducaoElemento findByAtributoAndElementoIdioma(String classe, String atributo, Integer idElemento, String idioma) throws DAOException {
		
		try {
			
			Criteria c = getSession().createCriteria(TraducaoElemento.class);
			Criteria itemTraducao = c.createCriteria("itemTraducao");
			Criteria entidadeTraducao = itemTraducao.createCriteria("entidade");
			entidadeTraducao.add(Restrictions.eq("classe", classe));
			itemTraducao.add(Restrictions.eq("atributo", atributo));
			c.add(Restrictions.eq("idElemento", idElemento));
			c.add(Restrictions.eq("idioma", idioma));
			
			return (TraducaoElemento) c.setMaxResults(1).uniqueResult();
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna o elemento de tradução pelo atributo, idioma e lista de elementos informados por parâmetro. 
	 * @param itemTraducao
	 * @param idsElemento
	 * @param idioma
	 * @return
	 * @throws DAOException
	 */
	public Collection<TraducaoElemento> findByAtributoAndElementoIdioma(String classe, String atributo, List<Integer> idsElemento, String idioma) throws DAOException {
		
		try {
			
			Criteria c = getSession().createCriteria(TraducaoElemento.class);
			Criteria itemTraducao = c.createCriteria("itemTraducao");
			Criteria entidadeTraducao = itemTraducao.createCriteria("entidade");
			entidadeTraducao.add(Restrictions.eq("classe", classe));
			itemTraducao.add(Restrictions.eq("atributo", atributo));
			c.add(Restrictions.in("idElemento", idsElemento));
			c.add(Restrictions.eq("idioma", idioma));
			
			@SuppressWarnings("unchecked")
			List<TraducaoElemento> lista = c.list();
			
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna as entidades de tradução por área de documento. 
	 * @param classe
	 * @return
	 * @throws DAOException
	 */
	public Collection<EntidadeTraducao> findEntidadeByIds(List<Integer> ids) throws DAOException {
		
		try {
			
			if (ids.isEmpty())
				return null;
			
			Criteria c = getSession().createCriteria(EntidadeTraducao.class);
			c.add(Restrictions.in("id", ids));
			
			@SuppressWarnings("unchecked")
			List<EntidadeTraducao> lista = c.list();
			
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna uma coleção de traduções de constantes por entidade e idioma.
	 * @param entidade
	 * @param idioma
	 * @return
	 * @throws DAOException
	 */
	public ConstanteTraducao findConstanteByEntidadeIdiomaConstante( String idioma, String constante, String classe) throws DAOException {
		
		Criteria c = getSession().createCriteria(ConstanteTraducao.class);
		Criteria entidadeTraducao = c.createCriteria("entidade");
		entidadeTraducao.add(Restrictions.eq("classe", classe));
		c.add(Restrictions.eq("idioma", idioma));
		c.add(Restrictions.eq("constante", constante));
		
		return (ConstanteTraducao) c.setMaxResults(1).uniqueResult();
	}
	
	/**
	 * Retorna traduções de constantes por entidade e idioma.
	 * @param entidade
	 * @param idioma
	 * @param constante
	 * @return
	 * @throws DAOException
	 */
	public Collection<ConstanteTraducao> findConstanteByEntidadeIdioma( String idioma, String... classe) throws DAOException {
		
		try {
			
			Criteria c = getSession().createCriteria(ConstanteTraducao.class);
			Criteria entidadeTraducao = c.createCriteria("entidade");
			entidadeTraducao.add(Restrictions.in("classe", classe));
			c.add(Restrictions.eq("idioma", idioma));
			
			@SuppressWarnings("unchecked")
			List<ConstanteTraducao> lista = c.list();
			
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna as entidades de tradução por área de documento. 
	 * @param classe
	 * @return
	 * @throws DAOException
	 */
	public Collection<EntidadeTraducao> findEntidadeByAreaDocumento(Integer idAreaDocumento) throws DAOException {
		
		try {
			String projecao = "ent.id as entidade.id, " +
					" ent.nome as entidade.nome," +
					" ent.classe as entidade.classe," +
					" ent.nomeClasse as entidade.nomeClasse," +
					" ent.ativo as entidade.ativo," +
					" ent.dataCadastro as entidade.dataCadastro";
					
			StringBuffer hql = new StringBuffer();
			hql.append("select ");
			hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
			hql.append(" from AreaDocumentoEntidade areaEnt "+
					   " inner join areaEnt.areaDocumento area " +
					   " inner join areaEnt.entidadeTraducao ent " +
					   " where area.id = :idAreaDocumento ");
			
			Query q = getSession().createQuery(hql.toString());
			
			q.setInteger("idAreaDocumento", idAreaDocumento);
			
			@SuppressWarnings("unchecked")
			Collection<EntidadeTraducao> lista = HibernateUtils.parseTo(q.list(), projecao, EntidadeTraducao.class, "entidade");
			
			List<Integer> ids = new ArrayList<Integer>();
			for (EntidadeTraducao et : lista) {
				ids.add(et.getId());
			}
			
			return findEntidadeByIds(ids);
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna um mapa utilizando o identificador componente curricular como chave, e o valor
	 * sendo uma lista de atributos e suas traduções.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, List<ItemTraducaoElementos>> findComponentesTraducao(List<ComponenteCurricular> listComponentes) throws DAOException {
		
		List<Integer> ids = new ArrayList<Integer>();
		for (ComponenteCurricular cc : listComponentes) {
			ids.add(cc.getId());
		}
		
		EntidadeTraducao entTraducao = findEntidadeByClasse(ComponenteCurricular.class.getName());
		if(entTraducao == null){
			throw new RuntimeNegocioException("Não há registro de entidade cadastrada para a tradução de "+ ComponenteCurricular.class.getSimpleName());
		}
			
		Map<Integer, List<ItemTraducaoElementos>> mapa = new HashMap<Integer, List<ItemTraducaoElementos>>();
		
		if ( ids.size() > 0 ){
			String projecao = "cc.id, cc.codigo, ccd.nome, " +
							  "ent.id, ent.nome, ent.nomeClasse, ent.classe, " +
							  "it.id, it.atributo, it.nome, it.tipoAreaTexto, " +
							  "te.id, te.idioma, te.valor, te.revisado";
			StringBuffer hql = new StringBuffer();
			hql.append( "select "+projecao+
						" from TraducaoElemento as te " +
						" inner join te.itemTraducao it " +
						" inner join it.entidade ent, " +
						" ComponenteCurricular cc " +
						" inner join cc.detalhes ccd " +
						" where ent.id = :idEntidade " );
			hql.append(	" and cc.id = te.idElemento ");
			hql.append(	" and it.ativo = trueValue() ");
			hql.append(	" and te.idElemento in " + UFRNUtils.gerarStringIn(ids));
			hql.append( " order by ent.nome, cc.codigo, it.atributo ");		
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idEntidade", entTraducao.getId());
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
			
			ComponenteCurricular componente = new ComponenteCurricular();
			ComponenteCurricular componenteAnterior = new ComponenteCurricular();
			EntidadeTraducao entidadeTraducao = new EntidadeTraducao(); 
			ItemTraducao itemTraducao = new ItemTraducao();
			TraducaoElemento traducao = new TraducaoElemento();
			
			List<ItemTraducaoElementos> listItemTraducaoElementos = new ArrayList<ItemTraducaoElementos>();
			Collection<TraducaoElemento> elementos = new ArrayList<TraducaoElemento>();
			
			boolean componenteAlterado = false;
			
			for (Object[] linha : resultado) {
				int idComponente = (Integer) linha[0];
				if (componente.getId() != idComponente){
					if(componente.getId() != 0){
						componenteAnterior = componente;
						componenteAlterado = true;
					}
					
					componente = new ComponenteCurricular();
					componente.setId(idComponente);
					componente.setCodigo((String) linha[1]);
					componente.setNome((String) linha[2]);
				} else {
					componenteAlterado = false;
				}
				
				int idEntidadeTraducao = (Integer) linha[3];
				if (entidadeTraducao.getId() != idEntidadeTraducao){
					entidadeTraducao = new EntidadeTraducao(); 
					entidadeTraducao.setId(idEntidadeTraducao);
					entidadeTraducao.setNome((String) linha[4]);
					entidadeTraducao.setNomeClasse((String) linha[5]);
					entidadeTraducao.setClasse((String) linha[6]);
				}
				
				int idItemTraducao = ((Integer) linha[7]);
				if (itemTraducao.getId() != idItemTraducao || componenteAlterado){
					if(itemTraducao.getId() != 0){
						listItemTraducaoElementos.add(new ItemTraducaoElementos(itemTraducao, elementos));
						if (componenteAlterado) {
							mapa.put(componenteAnterior.getId(), listItemTraducaoElementos);
							listItemTraducaoElementos = new ArrayList<ItemTraducaoElementos>();
						}
						elementos = new ArrayList<TraducaoElemento>();
					}		
					
					itemTraducao = new ItemTraducao();
					itemTraducao.setId(idItemTraducao);
					itemTraducao.setAtributo((String) linha[8]);
					itemTraducao.setNome((String) linha[9]);
					itemTraducao.setTipoAreaTexto((Boolean) linha[10]);
					itemTraducao.setEntidade(entidadeTraducao);
				}
				
				traducao = new TraducaoElemento((Integer) linha[11]);
				traducao.setIdioma((String) linha[12]);
				traducao.setValor((String) linha[13]);
				traducao.setRevisado((Boolean) linha[14]);
				traducao.setIdElemento(idComponente);
				traducao.setItemTraducao(itemTraducao);
				
				elementos.add(traducao);
			}
			
			if (componente.getId() != 0 && !elementos.isEmpty()){
				listItemTraducaoElementos.add(new ItemTraducaoElementos(itemTraducao, elementos));
				mapa.put(componente.getId(), listItemTraducaoElementos);
			}
			
		}
		return mapa;		
	}

	/**
	 * Retorna um mapa utilizando o identificador do elemento da aba como chave, e o valor
	 * sendo uma lista de atributos e suas traduções.
	 * @param listaElementosBusca
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Map<Integer, List<ItemTraducaoElementos>> findAtributosElemento(List<TraducaoElemento> listaTraducaoElemento) throws HibernateException, DAOException {
		
		List<Integer> idsEntidadeTraducao = new ArrayList<Integer>();
		List<Integer> idsElemento = new ArrayList<Integer>();
		
		for (TraducaoElemento t : listaTraducaoElemento) {
			idsEntidadeTraducao.add(t.getItemTraducao().getEntidade().getId());
			if (t.getIdElemento() != null && t.getIdElemento() > 0)
				idsElemento.add(t.getIdElemento());
		}
		
		Map<Integer, List<ItemTraducaoElementos>> mapa = new HashMap<Integer, List<ItemTraducaoElementos>>();
		
		if ( idsEntidadeTraducao.size() > 0 ){
			String projecao = "te.idElemento, te.dataCadastro, te.dataAtualizacao, " +
							  "ent.id, ent.nome, ent.nomeClasse, ent.classe, " +
							  "it.id, it.atributo, it.nome, it.tipoAreaTexto, " + 
							  "te.id, te.idioma, te.valor, te.revisado";
			StringBuffer hql = new StringBuffer();
			hql.append( "select "+projecao+
						" from TraducaoElemento as te " +
						" inner join te.itemTraducao it " +
						" inner join it.entidade ent " +
						" where ent.id in " + UFRNUtils.gerarStringIn(idsEntidadeTraducao));
			hql.append(	" and it.ativo = trueValue() ");
			if (!idsElemento.isEmpty())
				hql.append(	" and te.idElemento in " + UFRNUtils.gerarStringIn(idsElemento));
			hql.append( " order by ent.nome, it.atributo, te.idElemento ");		
			
			Query q = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
			
			int idElemento = 0; 
			int idElementoAnterior = 0;
			EntidadeTraducao entidadeTraducao = new EntidadeTraducao(); 
			ItemTraducao itemTraducao = new ItemTraducao();
			TraducaoElemento traducao = new TraducaoElemento();
			
			List<ItemTraducaoElementos> listItemTraducaoElementos = new ArrayList<ItemTraducaoElementos>();
			Collection<TraducaoElemento> elementos = new ArrayList<TraducaoElemento>();
			
			boolean elementoAlterado = false;
			
			for (Object[] linha : resultado) {
				
				int idEntidadeTraducao = (Integer) linha[3];
				idElemento = (Integer) linha[0];
				if (idElementoAnterior == 0) idElementoAnterior = idElemento;
				if (entidadeTraducao.getId() != idEntidadeTraducao || idElementoAnterior != idElemento){
					if(entidadeTraducao.getId() != 0){
						elementoAlterado = true;
					}
					entidadeTraducao = new EntidadeTraducao(); 
					entidadeTraducao.setId(idEntidadeTraducao);
					entidadeTraducao.setNome((String) linha[4]);
					entidadeTraducao.setNomeClasse((String) linha[5]);
					entidadeTraducao.setClasse((String) linha[6]);
				} else {
					elementoAlterado = false;
				}
				
				int idItemTraducao = ((Integer) linha[7]);
				if (itemTraducao.getId() != idItemTraducao || elementoAlterado){
					if(itemTraducao.getId() != 0){
						listItemTraducaoElementos.add(new ItemTraducaoElementos(itemTraducao, elementos));
						if (elementoAlterado) {
							mapa.put(idElementoAnterior, listItemTraducaoElementos);
							listItemTraducaoElementos = new ArrayList<ItemTraducaoElementos>();
							idElementoAnterior = idElemento;
						}
						elementos = new ArrayList<TraducaoElemento>();
					}		
					
					itemTraducao = new ItemTraducao();
					itemTraducao.setId(idItemTraducao);
					itemTraducao.setAtributo((String) linha[8]);
					itemTraducao.setNome((String) linha[9]);
					itemTraducao.setTipoAreaTexto((Boolean) linha[10]);
					itemTraducao.setEntidade(entidadeTraducao);
				}
				
				traducao = new TraducaoElemento((Integer) linha[11]);
				traducao.setIdioma((String) linha[12]);
				traducao.setValor((String) linha[13]);
				traducao.setRevisado((Boolean) linha[14]);
				traducao.setIdElemento(idElemento);
				traducao.setItemTraducao(itemTraducao);
				
				elementos.add(traducao);
			}
			
			if (idElemento != 0 && !elementos.isEmpty()){
				listItemTraducaoElementos.add(new ItemTraducaoElementos(itemTraducao, elementos));
				mapa.put(idElemento, listItemTraducaoElementos);
			}
			
		}
		return mapa;	
	}
	
	/**
	 * Método auxiliar utilizado para preencher um mapa com a listagem de ítens de tradução.  
	 * */
	public Map<Integer, List<ItemTraducaoElementos>> preencherMapaItemTraducaoElemento(List<Object[]> resultado){
		
		Map<Integer, List<ItemTraducaoElementos>> mapa = new HashMap<Integer, List<ItemTraducaoElementos>>();
		
		ComponenteCurricular componente = new ComponenteCurricular();
		ComponenteCurricular componenteAnterior = new ComponenteCurricular();
		EntidadeTraducao entidadeTraducao = new EntidadeTraducao(); 
		ItemTraducao itemTraducao = new ItemTraducao();
		TraducaoElemento traducao = new TraducaoElemento();
		
		List<ItemTraducaoElementos> listItemTraducaoElementos = new ArrayList<ItemTraducaoElementos>();
		Collection<TraducaoElemento> elementos = new ArrayList<TraducaoElemento>();
		
		boolean componenteAlterado = false;
		
		for (Object[] linha : resultado) {
			int idComponente = (Integer) linha[0];
			if (componente.getId() != idComponente){
				if(componente.getId() != 0){
					componenteAnterior = componente;
					componenteAlterado = true;
				}
				
				componente = new ComponenteCurricular();
				componente.setId(idComponente);
				componente.setCodigo((String) linha[1]);
				componente.setNome((String) linha[2]);
			} else {
				componenteAlterado = false;
			}
			
			int idEntidadeTraducao = (Integer) linha[3];
			if (entidadeTraducao.getId() != idEntidadeTraducao){
				entidadeTraducao = new EntidadeTraducao(); 
				entidadeTraducao.setId(idEntidadeTraducao);
				entidadeTraducao.setNome((String) linha[4]);
				entidadeTraducao.setNomeClasse((String) linha[5]);
				entidadeTraducao.setClasse((String) linha[6]);
			}
			
			int idItemTraducao = ((Integer) linha[7]);
			if (itemTraducao.getId() != idItemTraducao || componenteAlterado){
				if(itemTraducao.getId() != 0){
					listItemTraducaoElementos.add(new ItemTraducaoElementos(itemTraducao, elementos));
					if (componenteAlterado) {
						mapa.put(componenteAnterior.getId(), listItemTraducaoElementos);
						listItemTraducaoElementos = new ArrayList<ItemTraducaoElementos>();
					}
					elementos = new ArrayList<TraducaoElemento>();
				}		
				
				itemTraducao = new ItemTraducao();
				itemTraducao.setId(idItemTraducao);
				itemTraducao.setAtributo((String) linha[8]);
				itemTraducao.setNome((String) linha[9]);
				itemTraducao.setTipoAreaTexto((Boolean) linha[10]);
				itemTraducao.setEntidade(entidadeTraducao);
			}
			
			traducao = new TraducaoElemento((Integer) linha[11]);
			traducao.setIdioma((String) linha[12]);
			traducao.setValor((String) linha[13]);
			traducao.setRevisado((Boolean) linha[14]);
			traducao.setIdElemento(idComponente);
			traducao.setItemTraducao(itemTraducao);
			
			elementos.add(traducao);
		}
		
		if (componente.getId() != 0 && !elementos.isEmpty()){
			listItemTraducaoElementos.add(new ItemTraducaoElementos(itemTraducao, elementos));
			mapa.put(componente.getId(), listItemTraducaoElementos);
		}
		
		return mapa;
	}
	
}
