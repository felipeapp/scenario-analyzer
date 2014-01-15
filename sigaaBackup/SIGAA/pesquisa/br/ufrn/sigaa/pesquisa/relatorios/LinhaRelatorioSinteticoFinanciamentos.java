/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/04/2007
 * 
 */
package br.ufrn.sigaa.pesquisa.relatorios;

import java.util.Map;
import java.util.TreeMap;

/**
 * Classe auxiliar utilizada para a geração de relatórios sintéticos de financiamentos
 * de projetos
 *
 * @author ricardo
 *
 */
public class LinhaRelatorioSinteticoFinanciamentos {

	private int ano;

	private Map<String, Long> financiamentos = new TreeMap<String, Long>();

	private long totalInternos;

	public LinhaRelatorioSinteticoFinanciamentos() {

	}

	public int getAno() {
		return ano;
	}

	public Map<String, Long> getFinanciamentos() {
		return financiamentos;
	}


	public void setAno(int ano) {
		this.ano = ano;
	}

	public void setFinanciamentos(Map<String, Long> financiamentos) {
		this.financiamentos = financiamentos;
	}

	public long getTotal() {
		return getTotalExternos() + totalInternos;
	}

	public long getTotalExternos() {
		int totalExternos = 0;
		if ( financiamentos != null ) {
			for (long i : financiamentos.values() ) {
				totalExternos += i;
			}
		}
		return totalExternos;
	}

	public long getTotalInternos() {
		return totalInternos;
	}

	public void setTotalInternos(long totalInternos) {
		this.totalInternos = totalInternos;
	}

}