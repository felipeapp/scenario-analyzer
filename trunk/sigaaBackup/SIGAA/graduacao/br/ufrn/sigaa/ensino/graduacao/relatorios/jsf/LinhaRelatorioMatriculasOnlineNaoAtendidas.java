/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * 01/06/2007
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.Map;
import java.util.TreeMap;

/**
 * Classe auxiliar para gerar relat�rio de matr�culas on-line ainda n�o atendidas
 * @author leonardo
 *
 */
public class LinhaRelatorioMatriculasOnlineNaoAtendidas {

	private int ano;
	
	private Map<String,Long> discentes = new TreeMap<String, Long>();
	
	private long total;
	
	public LinhaRelatorioMatriculasOnlineNaoAtendidas(){
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Map<String, Long> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Map<String, Long> discentes) {
		this.discentes = discentes;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
	
	
}