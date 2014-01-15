/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '23/05/2011'
 *
 */
package br.ufrn.sigaa.ava.dao;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * DAO para busca de materiais dos tópicos de aula de turmas do SIGAA.
 * 
 * @author Ilueny Santos
 *  
 */
public class MaterialTurmaDao extends GenericSigaaDAO {

	
	/**
	 * Monta um mapa dos materiais da turma para cada tópico de aula.
	 * Utilizado na view para listar todos os materiais do na página principal da
	 * Turma. 
	 * 
	 * @param turma
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<TopicoAula, List<AbstractMaterialTurma>> findOrdemMaterialTurma(Turma turma, Map<TopicoAula, List<AbstractMaterialTurma>> materiaisBuscados) {
		Map<TopicoAula, List<AbstractMaterialTurma>> result = new HashMap<TopicoAula, List<AbstractMaterialTurma>>();
		
		try {
			
			String projecao = " id, idMaterial, turma.id, topicoAula.id, tipoMaterial, ordem, nivel ";
			Query q = getSession().createQuery("SELECT " + projecao + " FROM MaterialTurma mt " +
							"WHERE mt.ativo = trueValue() AND mt.turma.id = :idTurma " +
							"ORDER BY mt.topicoAula.id, ordem ");
			q.setInteger("idTurma", turma.getId());
			List<MaterialTurma> materiais = (List<MaterialTurma>) HibernateUtils.parseTo(q.list(), projecao, MaterialTurma.class);
			
			
			for (MaterialTurma m : materiais) {
				if (m.getTopicoAula() != null) {
					if (result.get(m.getTopicoAula()) == null) {
						result.put(m.getTopicoAula(), new ArrayList<AbstractMaterialTurma>());
					}
					//Localizando o material e incluindo na lista
					List<AbstractMaterialTurma> materiaisBuscadosDesteTopico = materiaisBuscados.get(m.getTopicoAula());
					if(isNotEmpty(materiaisBuscadosDesteTopico)) {
						for (AbstractMaterialTurma material : materiaisBuscadosDesteTopico) {
							if (material.getId() == m.getIdMaterial()) {
								result.get(m.getTopicoAula()).add(material);
								break;
							}
						}
					}
				}
			}
			
			return result;
			
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
		
	}

	/**
	 * Retorna a lista de materiais do Tópico de Aula informado.
	 * 
	 * @param topico
	 * @return
	 */
	public List<MaterialTurma> findMateriaisByTopicoAula(TopicoAula topico) {
		return findMateriaisByTopicoAulaOrdem(topico, null, null);
	}
	
	/**
	 * Retorna um subconjunto da lista de materiais do tópico 
	 * informado situados entre as ordemOrigem e ordemDestino.
	 * 
	 * @param topico
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MaterialTurma> findMateriaisByTopicoAulaOrdem(TopicoAula topico, Integer ordemOrigem, Integer ordemDestino) {
		try {

			int inicio = 0;
			int fim = 0;

			if (ordemOrigem != null && ordemDestino != null) {
				inicio = ordemOrigem < ordemDestino ? ordemOrigem : ordemDestino;
				fim = ordemOrigem > ordemDestino ? ordemOrigem : ordemDestino;
			}
			
			String projecao = " id, turma.id, topicoAula.id, tipoMaterial, ordem, nivel ";
			StringBuilder hql = new StringBuilder("SELECT " + projecao + " FROM MaterialTurma mt " +
					"WHERE mt.ativo = trueValue() AND mt.topicoAula.id = :idTopico ");			
			if (ordemOrigem != null && ordemDestino != null) {
				hql.append(" AND (mt.ordem >= :inicio AND mt.ordem <= :fim) ");
			}
			hql.append(" ORDER BY ordem ");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idTopico", topico.getId());			
			if (ordemOrigem != null && ordemDestino != null) {
				q.setInteger("inicio", inicio);
				q.setInteger("fim", fim);
			}
			
			return (List<MaterialTurma>) HibernateUtils.parseTo(q.list(), projecao, MaterialTurma.class);
			
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}		
	}
	
	/** Retorna a quantidade de materiais cadastrados para o tópico informado. */
	public int countMateriaisByTopico(TopicoAula topicoAula) {
		return count("from ava.material_turma where ativo = trueValue() and id_topico_aula = " + topicoAula.getId());
	}
	
	/**
	 * Retorna o material próximo ou anterior ao material informado de acordo com a ordem.
	 * 
	 * @param topico
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MaterialTurma findMaterialByVizinho(MaterialTurma material, String vizinho) {
		List<MaterialTurma> result = new ArrayList<MaterialTurma>();
		
		try {
			
			String projecao = " id, turma.id, topicoAula.id, tipoMaterial, ordem ";
			StringBuffer hql = new StringBuffer("SELECT " + projecao + " FROM MaterialTurma mt " +
					"WHERE mt.ativo = trueValue() AND mt.topicoAula.id = :idTopico ");
			
			if (vizinho.equalsIgnoreCase("superior")) {
				hql.append("AND mt.ordem < :ordem ORDER BY mt.topicoAula.id, ordem desc ");
			}else if (vizinho.equalsIgnoreCase("inferior")) {
				hql.append("AND mt.ordem > :ordem ORDER BY mt.topicoAula.id, ordem ");
			}
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idTopico", material.getTopicoAula().getId());
			q.setInteger("ordem", material.getOrdem());
			q.setMaxResults(1);
			result = (List<MaterialTurma>) HibernateUtils.parseTo(q.list(), projecao, MaterialTurma.class);
			
			if (result != null && result.size() > 0) {
				return result.iterator().next();
			}else {
				return null;
			}
			
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}		
	}

}
