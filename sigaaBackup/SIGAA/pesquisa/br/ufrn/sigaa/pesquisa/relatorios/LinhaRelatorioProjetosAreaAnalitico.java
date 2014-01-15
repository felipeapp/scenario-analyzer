/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 20/07/2007
 * 
 */
package br.ufrn.sigaa.pesquisa.relatorios;

import java.util.Map;
import java.util.TreeMap;

/**
 * Classe auxiliar pra gerar relatório analítico de projetos por
 * área de conhecimento.
 * @author leonardo
 *
 */
public class LinhaRelatorioProjetosAreaAnalitico {

	private int siape;
	
	private Long cpf;
	
	private String nome;
	
	private String lotacao;
	
	private String area;
	
	private int num_projetos;
	
	private Map<String, LinhaRelatorioProjetosAreaAnalitico> linhas = new TreeMap<String, LinhaRelatorioProjetosAreaAnalitico>();
	
	public LinhaRelatorioProjetosAreaAnalitico(){
		
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getLotacao() {
		return lotacao;
	}

	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getNum_projetos() {
		return num_projetos;
	}

	public void setNum_projetos(int num_projetos) {
		this.num_projetos = num_projetos;
	}

	public int getSiape() {
		return siape;
	}

	public void setSiape(int siape) {
		this.siape = siape;
	}

	public Map<String, LinhaRelatorioProjetosAreaAnalitico> getLinhas() {
		return linhas;
	}

	public void setLinhas(Map<String, LinhaRelatorioProjetosAreaAnalitico> linhas) {
		this.linhas = linhas;
	}
	
	
}