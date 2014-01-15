/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/04/2009'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Classe auxiliar utilizada na construção da view do relatório 
 * para avaliação de grupos de pesquisa.
 * Representa uma linha com a pontuação referente ao item 
 * e a quantidade de produções de cada docente no período considerado.
 * 
 * @author Leonardo Campos
 *
 */
public class LinhaRelatorioGrupoPesquisa {

	private int pontuacao;
	
	private Map<String, Long> docentes;
	
	public LinhaRelatorioGrupoPesquisa() {
		this.docentes = new TreeMap<String, Long>();
	}

	public LinhaRelatorioGrupoPesquisa(int pontuacao, Set<String> docentes) {
		this.pontuacao = pontuacao;
		this.docentes = new TreeMap<String, Long>();
		for(String nome: docentes)
			this.docentes.put(nome, new Long(0));
	}

	public int getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(int pontuacao) {
		this.pontuacao = pontuacao;
	}

	public Map<String, Long> getDocentes() {
		return docentes;
	}

	public void setDocentes(Map<String, Long> docentes) {
		this.docentes = docentes;
	}
	
	
}
