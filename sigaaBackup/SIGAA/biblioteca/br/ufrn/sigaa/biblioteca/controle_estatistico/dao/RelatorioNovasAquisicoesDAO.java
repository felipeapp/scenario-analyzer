/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>DAO utilizado exclusivamente para o relat�rio de novas aquisi��es. </p>
 * 
 * @author Felipe Rivas
 *
 */
public class RelatorioNovasAquisicoesDAO extends GenericSigaaDAO {

	/**
	 * Define o limite m�ximo de resultados da consulta. Mais que 1000 a gera��o do formato de refer�ncia fica invi�vel
	 */
	public static final int MAXIMO_RESULTADOS = 1000;
	
	/**
	 * M�todo que retorna uma lista de t�tulos catalogr�ficos referente �s aquisi��es feitas por bibliotecas em um certo per�odo de tempo 
	 * e para uma dada �rea de conhecimento.
	 * 
	 * @param bibliotecas As bibliotecas cujas aquisi��es (t�tulos) se deseja saber. Se nulo ou uma lista vazia, todas as bibliotecas 
	 * ser�o consideradas.
	 * @param areaCNPQ A �rea de conhecimento que se deseja filtrar para os t�tulos. Se nulo, todas as �reas ser�o consideradas.
	 * @param inicioPeriodo O in�cio do per�odo que se deseja ter conhecimento dos t�tulos adquiridos.
	 * @param fimPeriodo O fim do per�odo que se deseja ter conhecimento dos t�tulos adquiridos.
	 * @return A lista com os t�tulos catalogr�ficos referentes �s aquisi��es de acordo com os filtros especificados.
	 * @throws DAOException
	 */
	public List<TituloCatalografico> buscarAquisicoes(List<Biblioteca> bibliotecas, AreaConhecimentoCnpq areaCNPQ,
			Date inicioPeriodo, Date fimPeriodo) throws DAOException {
		
		if(inicioPeriodo == null || fimPeriodo == null)
			throw new IllegalArgumentException("O per�odo da consulta precisa ser passado!!!");
		
		
		StringBuilder sqlExemplar = new StringBuilder(
			" SELECT " + 
			"	tc.id_titulo_catalografico, " + 
			"	cem.titulo " +                    // Recupera o T�tulo apenas para ordenar a consulta
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
		
			sqlExemplar.append(" ORDER BY mi.data_criacao DESC "+BDUtils.limit(MAXIMO_RESULTADOS));   // retorna os 1000 T�tulos com os materiais mais recentes do acervo
			
		
		
			StringBuilder sqlFasciculo =  new StringBuilder(
				" SELECT " +  
				"	tc.id_titulo_catalografico, " +
				"	cem.titulo " +                      // Recupera o T�tulo apenas para ordenar a consulta
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
			
			sqlFasciculo.append(" ORDER BY mi.data_criacao DESC "+BDUtils.limit(MAXIMO_RESULTADOS));   // retorna os 1000 T�tulos com os materiais mais recentes do acervo
			
		
		List<TituloCatalografico> titulosComMateriaisMaisRecentes = new ArrayList<TituloCatalografico>();
		
		

		StringBuffer sqlGeral = new StringBuffer(
				" SELECT DISTINCT " +
				" id_titulo_catalografico, " +
				" case when titulo is null or titulo = '' then '$' else titulo end as titulo " +  // Recupera o T�tulo apenas para ordenar a consulta
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
	 * M�todo que retorna o n�mero de chamada de uma lista de t�tulos catalogr�ficos atrav�s do objeto CacheEntidadesMarc.
	 * 
	 * @param titulos Lista de t�tulos cujos n�meros de chamada se deseja saber.
	 * @return O n�mero de chamada do t�tulo catalogr�fico.
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
