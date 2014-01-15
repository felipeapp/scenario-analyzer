package br.ufrn.sigaa.ensino.medio.jsf;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.jfree.data.general.DefaultPieDataset;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * Classe responsável pelo DatasetProducer do Cewolf 
 * para exibir o gráfico bimestral de notas dos alunos do médio.
 * @author Suelton Miguel
 *
 */
public class GraficoBimestralNotas implements DatasetProducer, Serializable {

	@Override
	public String getProducerId() {
		return "GraficoBimestralNotas";
	}

	@Override
	public boolean hasExpired(Map params, Date data) {
		return (System.currentTimeMillis() - data.getTime()) > 5000;
	}

	@Override
	public Object produceDataset(Map param) throws DatasetProduceException {
										
		Long maior    = (Long) param.get("maiores");
		Long entre    = (Long) param.get("iguais");
		Long menor    = (Long) param.get("menores");
		Float mediaAprovacao = (Float) param.get("mediaAprovacao");
		Float mediaMinimaAprovacao = (Float) param.get("mediaMinimaAprovacao");
		
		DefaultPieDataset ds = new DefaultPieDataset();
		
		ds.setValue("Menor que "+ mediaMinimaAprovacao +" ("+menor+")", menor);
		ds.setValue("Entre "+mediaMinimaAprovacao+" e "+ mediaAprovacao +" ("+entre+")", entre);
		ds.setValue("Maior ou igual a "+ mediaAprovacao +" ("+maior+")", maior);
		return ds;
	}

}
