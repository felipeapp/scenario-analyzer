/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/10/2007
 */
package br.ufrn.arq.dominio;

import java.util.HashMap;
import java.util.List;

/**
 * Estrutura genérica de relatório usado para várias consultas genéricas
 * jogando para uma única JSP
 *
 * @author Gleydson Lima
 *
 */
public class RelatorioGenerico {

	// título do relatório
	private String titulo;

	// colunas do título
	private List<String> colunas;

	// linhas do resultado
	List<HashMap<String, Object>> linhas;

	public List<String> getColunas() {
		return colunas;
	}

	public void setColunas(List<String> colunas) {
		this.colunas = colunas;
	}

	public List<HashMap<String, Object>> getLinhas() {
		return linhas;
	}

	public void setLinhas(List<HashMap<String, Object>> linhas) {
		this.linhas = linhas;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}
