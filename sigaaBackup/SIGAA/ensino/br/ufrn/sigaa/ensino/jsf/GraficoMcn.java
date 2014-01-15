/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/06/2010
 */
package br.ufrn.sigaa.ensino.jsf;

import java.awt.Color;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import br.ufrn.arq.util.UFRNUtils;
import de.laures.cewolf.ChartPostProcessor;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * Classe para renderizar o gráfico de MCN no relatório 
 * de índices acadêmicos do portal do discente. 
 * 
 * @author David Pereira
 *
 */
@SuppressWarnings("rawtypes")
public class GraficoMcn implements DatasetProducer, ChartPostProcessor, Serializable {

	@Override
	public String getProducerId() {
		return "McnId";
	}

	@Override
	public boolean hasExpired(Map params, Date data) {
		return (System.currentTimeMillis() - data.getTime()) > 5000;
	}

	@Override
	public Object produceDataset(Map params) throws DatasetProduceException {
		double mc = Double.parseDouble(params.get("mc").toString());
		double maiorMc = Double.parseDouble(params.get("maiorMc").toString());
		double menorMc = Double.parseDouble(params.get("menorMc").toString());
		double mean = Double.parseDouble(params.get("mean").toString());
		double std = Double.parseDouble(params.get("std").toString());
		
		NormalDistributionFunction2D gaussian = new NormalDistributionFunction2D(mean, std);

		XYSeriesCollection dataset = (XYSeriesCollection) DatasetUtilities.sampleFunction2D(gaussian, menorMc, maiorMc, 10000, "Relação com a média");
		XYSeries serie = (XYSeries) UFRNUtils.deepCopy(dataset.getSeries(0));
		serie.setKey("Distribuição estatística da MC");

		double maior = Math.max(mc, mean);
		double menor = Math.min(mc, mean);
		int inicio = getIndice(serie, menor);
		int fim = getIndice(serie, maior);

		for(int i = inicio; i <= fim; i++)
			serie.update(i, new Double(0.0));

		dataset.addSeries(serie);

		return dataset;
	}

	private int getIndice(XYSeries serie, double valor) {
		int indice = 0;
		double menorDiferenca = Double.MAX_VALUE;
		
		for (int i = 0; i < serie.getItemCount(); i++) {
			Double y = (Double) serie.getX(i);
			double diferencaAtual = Math.abs(valor - y);
			if (diferencaAtual < menorDiferenca) {
				menorDiferenca = diferencaAtual;
				indice = i;
			} else {
				break;
			}
		}
		
		return indice;
	}

	@Override
	public void processChart(Object obj, Map map) {
		JFreeChart chart = (JFreeChart) obj;
		XYPlot plot = chart.getXYPlot();
		plot.setRenderer(new XYDifferenceRenderer(Color.RED, Color.BLUE, false));
	}
	
}