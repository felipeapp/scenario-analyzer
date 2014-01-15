/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 19/01/2010
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;
/**
 * Classe responsável por exibir o gráfico de Quantitativo de Defesas por Ano
 * @author Arlindo Rodrigues
 *
 */
public class GraficoQuantitativoDefesasAno implements DatasetProducer, Serializable {

	public static final int UM_SEGUNDO = 1000;
	public static final int TIMEOUT = 5*UM_SEGUNDO;
	
	
	public String getProducerId() {
		return "QuantitativoDefesasAno";
	}

	/**
	 * Verifica o Timeout
	 */
	@SuppressWarnings("unchecked")
	public boolean hasExpired(Map arg0, Date data) {
		return (System.currentTimeMillis() - data.getTime()) > TIMEOUT;
	}

	/**
	 * Monta o gráfico pegando a lista passada por parâmetro.
	 * Método chamado pela seguinte JSP: /stricto/relatorios/quantitativo_defesas_por_ano.jsp
	 */	
	@SuppressWarnings("unchecked")
	public Object produceDataset(Map params) throws DatasetProduceException {
		List<Map<String, Object>>  resultados = (List<Map<String, Object>>) params.get("lista");
		
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		
		for (Map<String, Object> lista : resultados) {
			ds.addValue((Number) lista.get("total"), "Quantitativo de Defesas", new String( String.valueOf( lista.get("ano") ) ) );
		}			
		
		return ds;
	}

	

}
