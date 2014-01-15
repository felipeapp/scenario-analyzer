/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/10/2009
 *
 */	
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.OfertaVagasCursoDao;
import br.ufrn.sigaa.arq.dao.relatorios.TaxaConclusaoDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.TaxaConclusao;

/**
 * A taxa de conclusão dos cursos de graduação é um indicador calculado
 * anualmente por meio da razão entre os diplomados e ingressos. 
 * @author Arlindo Rodrigues
 *
 */
public class CalculaTaxaConclusao {
	
	/**
	 * Calcula a taxa de conclusão para todos os anos e semestres que estejam no período informado.
	 * @param anoInicio
	 * @param semestreInicio
	 * @param anoFim
	 * @param semestreFim
	 * @return
	 * @throws DAOException
	 */
	public static List<TaxaConclusao> calculaTaxaConclusaoAnos(int anoInicio, int anoFim) throws DAOException {		
		
		TaxaConclusaoDao daoDiscentes = DAOFactory.getInstance().getDAO(TaxaConclusaoDao.class);		
		OfertaVagasCursoDao daoOfertas = DAOFactory.getInstance().getDAO(OfertaVagasCursoDao.class);
		
		List<TaxaConclusao> listaTaxa = new ArrayList<TaxaConclusao>();
		try{			
			List<Map<String, Object>> listaConcluintes = daoDiscentes.findTotalDiscentesConcluidos(anoInicio, anoFim);
			
			for(Map<String, Object> lista : listaConcluintes){
				TaxaConclusao taxaConclusao = new TaxaConclusao();
				taxaConclusao.setAno(Integer.parseInt(""+lista.get("ano")));
				taxaConclusao.setSemestre(Integer.parseInt(""+lista.get("periodo")));
				taxaConclusao.setConcluintes(Integer.parseInt(""+lista.get("total")));
				listaTaxa.add(taxaConclusao);
			}
			NumberFormat df = new DecimalFormat("#,##0.00"); 	
			
			int[] anoIngressoInicio = subtraiAnoSemestre(anoInicio,1, 5);
			int[] anoIngressoFim = subtraiAnoSemestre(anoFim,2, 5);
			List<Map<String, Object>> listaIngressantes = daoDiscentes.findTotalVagasOfertadasTaxaConclusao(anoIngressoInicio[0], anoIngressoFim[0]);			
			int ano = 0;
			int totalIngressantes = 0;
			int totalConcluintes = 0;
			TaxaConclusao taxaAnterior;
			for (TaxaConclusao taxa : listaTaxa){
				int[] anoSemestreIngresso = subtraiAnoSemestre(taxa.getAno(),taxa.getSemestre(), 5);
				taxa.setAnoIngresso(anoSemestreIngresso[0]);
				taxa.setSemestreIngresso(anoSemestreIngresso[1]);
				for(Map<String, Object> listaIngressante : listaIngressantes){
					if ((taxa.getAnoIngresso() == Integer.parseInt(""+listaIngressante.get("ano")))){
						
						if (taxa.getSemestreIngresso() ==  1)
							taxa.setIngressantes(taxa.getIngressantes() + Integer.parseInt(""+listaIngressante.get("periodo1")));
						else
							taxa.setIngressantes(taxa.getIngressantes() + Integer.parseInt(""+listaIngressante.get("periodo2")));
						
					}					
				}		
				
				totalIngressantes += taxa.getIngressantes();
				totalConcluintes += taxa.getConcluintes();
				
				if (ano > 0)
					taxaAnterior = listaTaxa.get(listaTaxa.indexOf(taxa) -1);
				else
					taxaAnterior = null;
				
				if (ano != taxa.getAno()){
					if (ano > 0){
						totalIngressantes = taxa.getIngressantes();
						totalConcluintes = taxa.getConcluintes();
					}						
					taxa.setLinhas(1);
					if (totalIngressantes > 0){
						taxa.setTaxaAnual(Float.parseFloat(df.format(100 * (float) totalConcluintes/totalIngressantes).replace(",", ".")));
						taxa.setConcluintesAnual(totalConcluintes);
						taxa.setIngressantesAnual(totalIngressantes);							
					} else
						taxa.setTaxaAnual(0);
					
					taxa.setConcluintesAnual(totalConcluintes);
					taxa.setIngressantesAnual(totalIngressantes);					
				} else {
					if (taxaAnterior != null){
						taxaAnterior.setLinhas(2);					
						if (totalIngressantes > 0){
							taxaAnterior.setTaxaAnual(Float.parseFloat(df.format(100 * (float) totalConcluintes/totalIngressantes).replace(",", ".")));
							
							taxaAnterior.setConcluintesAnual(totalConcluintes);
							taxaAnterior.setIngressantesAnual(totalIngressantes);								
						} else
							taxaAnterior.setTaxaAnual(0);	
						taxa.setLinhas(0);
						
						
					}				
				}
				ano = taxa.getAno();
			} 
		} finally {
			if (daoDiscentes != null)
				daoDiscentes.close();
			if (daoOfertas != null)
				daoOfertas.close();
		}

		return listaTaxa;		
	}			
		
	/**
	 * Calcula o ano e semestre para obter os ingressos.
	 * @param ano
	 * @param semestre
	 * @param anos
	 * @return
	 */
	public static int[] subtraiAnoSemestre(int ano, int semestre, int anos) {
		
		int[] result = new int[2];
		
		if (semestre == 1){
			result[0] = ano - anos; // desloca a quantidade de anos
			result[1] = 2; // e fica o semestre 2.			
		} else {
			result[0] = ano - (anos - 1); // desloca a quantidade de anos
			result[1] = 1; // e fica o semestre 2.							
		}		
		return result;			
	}
	
	
	
}
