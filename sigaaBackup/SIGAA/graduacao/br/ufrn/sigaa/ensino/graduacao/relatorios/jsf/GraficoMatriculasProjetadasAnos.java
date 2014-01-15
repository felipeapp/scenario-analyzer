package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * Classe responsável por exibir o gráfico de matriculas projetadas
 * @author Arlindo Rodrigues
 *
 */
public class GraficoMatriculasProjetadasAnos implements DatasetProducer, Serializable {

	public String getProducerId() {
		return "MatriculaProjetada";
	}

	@SuppressWarnings("unchecked")
	public boolean hasExpired(Map arg0, Date data) {
		return (System.currentTimeMillis() - data.getTime()) > 5000;
	}

	/**
	 * Monta o gráfico pegando a lista passada por parâmetro.
	 * Método chamado pela seguinte JSP: /graduação/relatorios/curso/lista_matriculas_projetadas.jsp
	 */
	@SuppressWarnings("unchecked")	
	public Object produceDataset(Map params) throws DatasetProduceException {
		List<Map<String,Object>> listaCurso = (List<Map<String, Object>>) params.get("lista");
		
		DefaultCategoryDataset ds = new DefaultCategoryDataset();			
		for (Map<String, Object> lista : listaCurso){
			ds.addValue( (Number) lista.get("total") , "Matrícula Projetada", new Integer( Integer.parseInt(""+lista.get("ano") ) ));
		}			
		return ds;
			
	}


}
