/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/10/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;

import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * Classe que gera o conjunto de dados utilizado para gerar o gr�fico no relat�rio de
 * Crescimento de Peri�dicos por �rea do CNPq num per�odo dado.
 *
 * @author Felipe Rivas
 */
public class GraficoRelatorioCrescimentoPeriodicosPorCNPqPeriodo implements
		DatasetProducer {
	
	/** Timeout do cache do gr�fico. */
	private static final int TIMEOUT = 5000; // ms

	@Override
	public String getProducerId() {
		return "graficoRelatorioCrescimentoPeriodicosPorCNPqPeriodo";
	}

	@Override
	public boolean hasExpired( @SuppressWarnings("rawtypes") Map params, Date data ) {
		return (System.currentTimeMillis() - data.getTime()) >= TIMEOUT;
	}

	@Override
	public Object produceDataset( @SuppressWarnings("rawtypes") Map params ) throws DatasetProduceException {
		
		@SuppressWarnings("unchecked")
		Map<String, Long> antes = (Map<String, Long>) params.get("antes");
		
		@SuppressWarnings("unchecked")
		Map<String, Long> depois = (Map<String, Long>) params.get("depois");
		
		@SuppressWarnings("unchecked")
		List<AreaConhecimentoCnpq> areas = (List<AreaConhecimentoCnpq>) params.get("areas");
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		int cont = 1;
		
		for ( AreaConhecimentoCnpq area : areas ) {

			long a =  antes.get( area.getSigla() ) != null ? (Long) antes.get( area.getSigla() ) : 0;
			long d = depois.get( area.getSigla() ) != null ? (Long) depois.get( area.getSigla() ) : 0;
			
			dataset.addValue(a, "Quantidade anterior", cont + " - " + area.getSigla());
			dataset.addValue(d - a, "Crescimento no Per�odo", cont + " - " + area.getSigla());
			
			cont++;
			
		}
		return dataset;
	}

}
