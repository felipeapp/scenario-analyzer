/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/10/2009
 *
 */	
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;

import br.ufrn.sigaa.ensino.graduacao.dominio.TaxaConclusao;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;
/**
 * Classe responsável por exibir o gráfico de Taxa de Conclusão
 * @author Arlindo Rodrigues
 *
 */
public class GraficoTaxaConclusao implements DatasetProducer, Serializable {

	public static final int UM_SEGUNDO = 1000;
	public static final int TIMEOUT = 5*UM_SEGUNDO;
	
	public static final int CEM_PORCENTO = 100;
	
	
	public String getProducerId() {
		return "TaxaConclusao";
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
	 * Método chamado pela seguinte JSP: /graduação/relatorios/vagas_ofertadas/lista_taxa_conclusao.jsp
	 */	
	@SuppressWarnings("unchecked")
	public Object produceDataset(Map params) throws DatasetProduceException {
		List<TaxaConclusao> taxaConclusao = (List<TaxaConclusao>) params.get("lista");
		
		DefaultCategoryDataset ds = new DefaultCategoryDataset();		
		for (TaxaConclusao lista : taxaConclusao){
			if (lista.getLinhas() > 0)
				ds.addValue(lista.getTaxaAnual() * CEM_PORCENTO, "Taxa de Conclusão", new String( String.valueOf( lista.getAno() )) );
		}		
		return ds;
	}

	

}
