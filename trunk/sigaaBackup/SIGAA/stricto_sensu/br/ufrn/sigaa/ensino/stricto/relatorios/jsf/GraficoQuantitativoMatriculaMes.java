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
 * Classe responsável por exibir o gráfico de Quantitativo de Matrículas por Mês
 * @author Arlindo Rodrigues
 *
 */
public class GraficoQuantitativoMatriculaMes implements DatasetProducer, Serializable {

	public static final int UM_SEGUNDO = 1000;
	public static final int TIMEOUT = 5*UM_SEGUNDO;
	
	
	public String getProducerId() {
		return "QuantitativoMatriculaMes";
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
	 * Método chamado pela seguinte JSP: /stricto/relatorios/quantitativo_matriculados_por_mes.jsp
	 */	
	@SuppressWarnings("unchecked")
	public Object produceDataset(Map params) throws DatasetProduceException {
		List<Map<String, Object>>  resultados = (List<Map<String, Object>>) params.get("lista");
		
		String[] meses = { "Jan", "Fev", "Mar", "Abr",
				"Mai", "Jun", "Jul", "Ago", "Set", "Out",
				"Nov", "Dez" };
		
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		
		boolean adicionou;
		int total = 0;
		int total2 = 0;
		int ativo = 0;
		for (int i = 1; i<= 12; i++){
			adicionou = false;
			for (Map<String, Object> lista : resultados) {
				if ((Integer) lista.get("mes") == i){
					total += ((Number) lista.get("total")).intValue();
					total2 = ((Number) lista.get("total")).intValue();
					ativo = ((Number) lista.get("ativo")).intValue();
					ds.addValue(total,  "Quantitativo Acumulado de Matrículas", new String( meses[ (Integer) lista.get("mes") -1] ) );
					ds.addValue(total2, "Quantitativo de Matrículas", new String( meses[ (Integer) lista.get("mes") -1] ) );
					ds.addValue(ativo,  "Quantitativo de Alunos Ativos", new String( meses[ (Integer) lista.get("mes") -1] ) );
					adicionou = true;					
				}
			}			
			if (!adicionou) {
				ds.addValue((Number) 0, "Quantitativo Acumulado de Matrículas", new String( meses[ i - 1 ] ) );
				ds.addValue((Number) 0, "Quantitativo de Matrículas", new String( meses[ i - 1 ] ) );
				ds.addValue((Number) 0, "Quantitativo de Alunos Ativos", new String( meses[ i - 1 ] ) );
			}
		}	
		
		return ds;
	}

	

}
