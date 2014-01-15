package br.ufrn.sigaa.ava.jsf;

import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;

import de.laures.cewolf.ChartPostProcessor;

public class EstatisticasNotasPostProcessor implements ChartPostProcessor {

	@Override
	public void processChart(Object chart, @SuppressWarnings("rawtypes") Map arg1) {
		Plot plot = ((JFreeChart) chart).getPlot();  
		
		ValueAxis axis = ((CategoryPlot) plot).getRangeAxis();  
		if (axis instanceof NumberAxis) {  
		    ((NumberAxis) axis).setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		}
	}

}
