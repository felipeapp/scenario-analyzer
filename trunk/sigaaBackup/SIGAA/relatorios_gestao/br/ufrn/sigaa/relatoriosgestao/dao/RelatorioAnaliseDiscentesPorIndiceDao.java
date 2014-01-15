/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/04/2011
 */
package br.ufrn.sigaa.relatoriosgestao.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.relatoriosgestao.dominio.RelatorioIndicesTotais;

/**
 * Dao responsável por realizar as consultas para exibição do relatório de Análise de discentes por Índice acadêmico.
 * 
 * @author arlindo
 *
 */
public class RelatorioAnaliseDiscentesPorIndiceDao extends GenericSigaaDAO {
		
	/**
	 * Retorna o total de discentes por faixa de índice
	 * @param ano
	 * @param periodo
	 * @param idCurso
	 * @param nivel
	 * @param idIndice
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<RelatorioIndicesTotais> findTotaisIndicesDiscente(Integer ano, Integer periodo, Integer idCurso, Integer nivel, Integer idIndice) throws HibernateException, DAOException{
		
		StringBuffer hql = new StringBuffer();
		
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.addAll(StatusDiscente.getStatusComVinculo());
		status.add(StatusDiscente.CONCLUIDO);
		
		IndiceAcademico indice = findByPrimaryKey(idIndice, IndiceAcademico.class, "id", "frequenciaDivisaoHistograma");
				
		hql.append(" select ");
		
		String[] faixas = null;
		/* verifica se existe frequencia de divisão para exibição do histograma */
		if (!ValidatorUtil.isEmpty(indice.getFrequenciaDivisaoHistograma())){
			
			faixas = indice.getFrequenciaDivisaoHistograma().split(",");
			
			for (int i = 0; i <= faixas.length-1; i ++){
				
				String faixa = faixas[i];				
				String operador = "";
				/* se for o ultimo elemento verifica existe um '*', se existir,
				 * terá a comparação com os valores maiores que o valor da faixa anterior */ 
				if (faixas.length-1 == i){
					if (faixas[i].equals("*")){
						operador = " > ";
						faixa = faixas[i-1];
					} else
						operador = " = ";
					
					hql.append(" coalesce(cast( sum(case when (i.valor "+operador+faixa+") then 1 else 0 end) as int),0) ");
				} else {
					String proximo = faixas[i + 1];
					operador = " > ";
					
					if (i == 0)
						operador = " >= ";
					
					if (proximo.equals("*")){
						operador = " = ";
						hql.append(" coalesce(cast( sum(case when (i.valor "+operador+faixa+") then 1 else 0 end) as int),0), ");
					} else 									
						hql.append(" coalesce(cast( sum(case when (i.valor "+operador+faixa+" and i.valor <= "+proximo+") then 1 else 0 end) as int),0), ");
				} 
				
			}
			
		} else {
			return null;
		}
				
		hql.append(" from IndiceAcademicoDiscente i ");
		hql.append(" inner join i.discente d ");
		hql.append(" where d.status in "+UFRNUtils.gerarStringIn(status));
		
		if (ano != null && ano > 0)
			hql.append(" and d.anoIngresso = "+ano);
		
		if (periodo != null && periodo > 0)
			hql.append(" and d.periodoIngresso = "+periodo);		
		
		if (idCurso != null && idCurso > 0)
			hql.append(" and d.curso.id = "+idCurso);
		
		if (idIndice != null && idIndice > 0)
			hql.append(" and i.indice.id = "+idIndice);
		
		if (nivel != null && nivel > 0)
			hql.append(" and d.periodoAtual = "+nivel);			
		
		Query q = getSession().createQuery(hql.toString());
		
		Object[] colunas = (Object[]) q.uniqueResult();
		
		if (!ValidatorUtil.isEmpty(colunas)){	
			
				List<RelatorioIndicesTotais> resultado = new ArrayList<RelatorioIndicesTotais>();
				
				float total = 0;
				for (int i = 0; i <= colunas.length-1; i++) 		
					total += (Integer) colunas[i];
				
				for (int i = 0; i <= colunas.length-1; i++) {					
					String proximo = "";
					
					if (i < colunas.length-1 && !faixas[i + 1].equals("*"))
						proximo = " - "+ faixas[i + 1];
					
					RelatorioIndicesTotais item = new RelatorioIndicesTotais();
					item.setPosicaoFaixa(i);
					
					if (faixas[i].equals("*"))
						item.setFaixa(" > "+ faixas[i-1]);
					else
						item.setFaixa(faixas[i] + proximo);
					
					item.setValor((Integer) colunas[i]);
					
					if (total > 0)
						item.setPercentual((item.getValor() / total) * 100);
					else
						item.setPercentual(0);
					
					resultado.add(item);
				}
				
				return resultado;
				
		}
		
		return null;
	}
	
	/**
	 * Retorna os totais de discentes por valor de índice acadêmico
	 * @param ano
	 * @param periodo
	 * @param idCurso
	 * @param nivel
	 * @param idIndice
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findTotaisIndicesContinuo(Integer ano, Integer periodo, Integer idCurso, Integer nivel, Integer idIndice) throws HibernateException, DAOException{
		
		StringBuffer hql = new StringBuffer();
		
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.addAll(StatusDiscente.getStatusComVinculo());
		status.add(StatusDiscente.CONCLUIDO);
		
		hql.append(" select (round(i.valor * 100) / 100), cast(count(d.id) as int) from IndiceAcademicoDiscente i ");
		hql.append(" inner join i.discente d ");
		hql.append(" where d.status in "+UFRNUtils.gerarStringIn(status));
		
		if (ano != null && ano > 0)
			hql.append(" and d.anoIngresso = "+ano);
		
		if (periodo != null && periodo > 0)
			hql.append(" and d.periodoIngresso = "+periodo);		
		
		if (idCurso != null && idCurso > 0)
			hql.append(" and d.curso.id = "+idCurso);
		
		if (idIndice != null && idIndice > 0)
			hql.append(" and i.indice.id = "+idIndice);
		
		if (nivel != null && nivel > 0)
			hql.append(" and d.periodoAtual = "+nivel);		
		
		hql.append(" group by 1 ");		
		hql.append(" order by 1 ");
		
		Query q = getSession().createQuery(hql.toString());
		
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();
    	for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			Map<String, Object> item = new TreeMap<String, Object>();
			
			item.put("valor", (Double) colunas[col++] );
			item.put("total", (Integer) colunas[col++] );

			resultado.add(item);
    	}
		
		return resultado;		
				
		
	}
	
	/**
	 * Busca os discentes conforme o índice acadêmico e faixa informados
	 * @param idIndice
	 * @param faixa
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<IndiceAcademicoDiscente> findDiscenteByIndice(Integer ano, Integer periodo, Integer idCurso, Integer nivel, IndiceAcademico indiceAcademico, Integer indexFaixa) throws HibernateException, DAOException{
		
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.addAll(StatusDiscente.getStatusComVinculo());
		status.add(StatusDiscente.CONCLUIDO);		
		
		StringBuffer hql = new StringBuffer();
		
		String projecao = " i.discente.id, i.discente.matricula, i.discente.pessoa.nome, i.discente.status, i.valor,  " +
				" i.discente.curso.id, i.discente.curso.nome, i.discente.curso.municipio.nome, i.discente.curso.modalidadeEducacao.id ";
		
		hql.append(" select "+projecao + " from IndiceAcademicoDiscente i ");
		hql.append(" inner join i.discente d ");
		hql.append(" inner join i.discente.pessoa p ");
		hql.append(" inner join i.discente.curso c ");
		hql.append(" left join i.discente.curso.modalidadeEducacao ");
		hql.append(" where d.status in "+UFRNUtils.gerarStringIn(status));
		
		if (ano != null && ano > 0)
			hql.append(" and d.anoIngresso = "+ano);
		
		if (periodo != null && periodo > 0)
			hql.append(" and d.periodoIngresso = "+periodo);		
		
		if (idCurso != null && idCurso > 0)
			hql.append(" and d.curso.id = "+idCurso);
		
		if (indiceAcademico != null && indiceAcademico.getId() > 0)
			hql.append(" and i.indice.id = "+indiceAcademico.getId());
		
		if (nivel != null && nivel > 0)
			hql.append(" and d.periodoAtual = "+nivel);
		
		if (indexFaixa != null && indexFaixa >= 0){

			String[] faixas = indiceAcademico.getFrequenciaDivisaoHistograma().split(",");
			
			String operador = "";
			if (faixas.length-1 == indexFaixa){
				
				if (faixas[indexFaixa].equals("*"))
					operador = " > ";
				else
					operador = " = ";
				
				hql.append(" and i.valor "+operador+faixas[indexFaixa-1]);
				
			} else {
				
				String proximo = faixas[indexFaixa + 1];
				operador = " > ";

				if (indexFaixa == 0)
					operador = " >= ";		
				
				if (proximo.equals("*")){
					operador = " = ";
					hql.append(" and i.valor "+operador+faixas[indexFaixa] );
				} else 									
					hql.append(" and i.valor "+operador+faixas[indexFaixa] + " and i.valor <= "+proximo);
				
			}
			
		}
		
		hql.append(" order by i.discente.curso.nome, i.discente.curso.municipio.nome, i.discente.curso.modalidadeEducacao.id, i.discente.pessoa.nome ");		
		
		@SuppressWarnings("unchecked")
		List<IndiceAcademicoDiscente> lista = (List<IndiceAcademicoDiscente>) HibernateUtils.parseTo(getSession().createQuery(hql.toString()).list(), projecao, IndiceAcademicoDiscente.class, "i");
		return lista;
	}

}
