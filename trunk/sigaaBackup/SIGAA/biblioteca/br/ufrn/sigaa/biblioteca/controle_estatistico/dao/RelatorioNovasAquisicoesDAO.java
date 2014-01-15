/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 28/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 *
 * <p>DAO utilizado exclusivamente para o relatório de novas aquisições. </p>
 * 
 * @author Felipe Rivas
 *
 */
public class RelatorioNovasAquisicoesDAO extends GenericSigaaDAO {

	/**
	 * Define o limite máximo de resultados da consulta. Mais que 1000 a geração do formato de referência fica inviável
	 */
	public static final int MAXIMO_RESULTADOS = 1000;
	
	/**
	 * Método que retorna uma lista de títulos catalográficos referente às aquisições feitas por bibliotecas em um certo período de tempo 
	 * e para uma dada área de conhecimento.
	 * 
	 * @param bibliotecas As bibliotecas cujas aquisições (títulos) se deseja saber. Se nulo ou uma lista vazia, todas as bibliotecas 
	 * serão consideradas.
	 * @param areaCNPQ A área de conhecimento que se deseja filtrar para os títulos. Se nulo, todas as áreas serão consideradas.
	 * @param inicioPeriodo O início do período que se deseja ter conhecimento dos títulos adquiridos.
	 * @param fimPeriodo O fim do período que se deseja ter conhecimento dos títulos adquiridos.
	 * @return A lista com os títulos catalográficos referentes às aquisições de acordo com os filtros especificados.
	 * @throws DAOException
	 */
	public List<TituloCatalografico> buscarAquisicoes(List<Biblioteca> bibliotecas, AreaConhecimentoCnpq areaCNPQ,
			Date inicioPeriodo, Date fimPeriodo) throws DAOException {
		
		if(inicioPeriodo == null || fimPeriodo == null)
			throw new IllegalArgumentException("O período da consulta precisa ser passado!!!");
		
		
		StringBuilder sqlExemplar = new StringBuilder(
			" SELECT " + 
			"	tc.id_titulo_catalografico, " + 
			"	cem.titulo " +                    // Recupera o Título apenas para ordenar a consulta
			" FROM biblioteca.titulo_catalografico tc " + 
			" INNER JOIN biblioteca.cache_entidades_marc cem            ON (tc.id_titulo_catalografico = cem.id_titulo_catalografico) " + 
			" INNER JOIN biblioteca.exemplar e                          ON (tc.id_titulo_catalografico = e.id_titulo_catalografico) " + 
			" INNER JOIN biblioteca.material_informacional mi           ON (e.id_exemplar = mi.id_material_informacional) " + 
			" INNER JOIN biblioteca.situacao_material_informacional smi ON (mi.id_situacao_material_informacional = smi.id_situacao_material_informacional) " + 
			" WHERE mi.ativo = trueValue()" + 
			"   AND mi.data_criacao >= :inicioPeriodo AND mi.data_criacao <= :fimPeriodo "+
			"	AND smi.situacao_de_baixa = falseValue() " + 
			"	AND tc.ativo = trueValue() ");
		
			if (bibliotecas != null && bibliotecas.size() > 0) {
				sqlExemplar.append( " AND mi.id_biblioteca IN (:bibliotecasID) ");
			}
			
			if (areaCNPQ != null) {
				sqlExemplar.append( " AND ( tc.id_area_conhecimento_cnpq_classificacao_1 = :areaCNPQ " +
											" OR tc.id_area_conhecimento_cnpq_classificacao_2 = :areaCNPQ " +
											" OR tc.id_area_conhecimento_cnpq_classificacao_3 = :areaCNPQ    ) " );
			}
		
			sqlExemplar.append(" ORDER BY mi.data_criacao DESC "+BDUtils.limit(MAXIMO_RESULTADOS));   // retorna os 1000 Títulos com os materiais mais recentes do acervo
			
		
		
			StringBuilder sqlFasciculo =  new StringBuilder(
				" SELECT " +  
				"	tc.id_titulo_catalografico, " +
				"	cem.titulo " +                      // Recupera o Título apenas para ordenar a consulta
				" FROM biblioteca.titulo_catalografico tc " + 
				" INNER JOIN biblioteca.cache_entidades_marc cem            ON (tc.id_titulo_catalografico = cem.id_titulo_catalografico) " + 
				" INNER JOIN biblioteca.assinatura a                        ON (tc.id_titulo_catalografico = a.id_titulo_catalografico) " + 
				" INNER JOIN biblioteca.fasciculo f 	                    ON (a.id_assinatura = f.id_assinatura) " + 
				" INNER JOIN biblioteca.material_informacional mi           ON (f.id_fasciculo = mi.id_material_informacional) " + 
				" INNER JOIN biblioteca.situacao_material_informacional smi ON (mi.id_situacao_material_informacional = smi.id_situacao_material_informacional) " + 
				" WHERE mi.ativo = trueValue() " + 
				"   AND mi.data_criacao >= :inicioPeriodo AND mi.data_criacao <= :fimPeriodo "+
				"	AND  smi.situacao_de_baixa = falseValue() " + 
				"	AND tc.ativo = trueValue() ");
			
			if (bibliotecas != null && bibliotecas.size() > 0) {
				sqlFasciculo.append(" AND mi.id_biblioteca IN (:bibliotecasID) ");
			}
			
			if (areaCNPQ != null) {
				sqlFasciculo.append(" AND  ( tc.id_area_conhecimento_cnpq_classificacao_1 = :areaCNPQ " +
											" OR tc.id_area_conhecimento_cnpq_classificacao_2 = :areaCNPQ " +
											" OR tc.id_area_conhecimento_cnpq_classificacao_3 = :areaCNPQ    )  ");
			}
			
			sqlFasciculo.append(" ORDER BY mi.data_criacao DESC "+BDUtils.limit(MAXIMO_RESULTADOS));   // retorna os 1000 Títulos com os materiais mais recentes do acervo
			
		
		List<TituloCatalografico> titulosComMateriaisMaisRecentes = new ArrayList<TituloCatalografico>();
		
		

		StringBuffer sqlGeral = new StringBuffer(
				" SELECT DISTINCT " +
				" id_titulo_catalografico, " +
				" case when titulo is null or titulo = '' then '$' else titulo end as titulo " +  // Recupera o Título apenas para ordenar a consulta
				" FROM ( " + 
					"( " +  
					sqlExemplar.toString()
					+  ")"+      
				"	UNION ALL ( " +  
						sqlFasciculo.toString()+
					")"+
				") as uniao " + 
				"ORDER BY titulo ASC "+
				BDUtils.limit(MAXIMO_RESULTADOS));
		
		
			
		Query q = getSession().createSQLQuery(sqlGeral.toString());
		if (bibliotecas != null && bibliotecas.size() > 0) {
			q.setParameterList("bibliotecasID", bibliotecas);
		}

		if (areaCNPQ != null) {
			q.setInteger("areaCNPQ", areaCNPQ.getId());
		}
		
		q.setTimestamp("inicioPeriodo", CalendarUtils.configuraTempoDaData(inicioPeriodo, 0, 0, 0, 0) );
		q.setTimestamp("fimPeriodo", CalendarUtils.configuraTempoDaData(fimPeriodo, 23, 59, 59, 999));

		@SuppressWarnings("unchecked")
		List<Object[]> novasAquisicoesIDs = q.list();
		
		for (Object[] registro : novasAquisicoesIDs) {
			titulosComMateriaisMaisRecentes.add(new TituloCatalografico((Integer)registro[0]));
		}
		
		return titulosComMateriaisMaisRecentes;
		
	}

	
	/**
	 * Método que retorna o número de chamada de uma lista de títulos catalográficos através do objeto CacheEntidadesMarc.
	 * 
	 * @param titulos Lista de títulos cujos números de chamada se deseja saber.
	 * @return O número de chamada do título catalográfico.
	 * @throws DAOException
	 */
	public Map<Integer, String> buscarNumeroChamada(Collection<TituloCatalografico> titulos) throws DAOException {
		try {
			Query q = getSession().createQuery("SELECT cem.idTituloCatalografico, cem.numeroChamada FROM CacheEntidadesMarc cem WHERE cem.idTituloCatalografico IN (:idTituloCatalografico)");
			
			List<Integer> titulosID = new ArrayList<Integer>();
			
			for (TituloCatalografico titulo : titulos) {
				titulosID.add(titulo.getId());
			}
			
			q.setParameterList("idTituloCatalografico", titulosID);
			
			Map<Integer, String> resultado = new HashMap<Integer, String>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			for (Object[] registro : lista) {
				resultado.put((Integer)registro[0], (String)registro[1]);
			}
			
			return resultado;
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
	}
	
}
