package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;

/**
 * Entidade auxiliar que será responsável por armazenar as informações e o processamentos dos dados do
 * relatório de frequencia dos discentes do IMD.
 * 
 * ESTA ENTIDADE NÃO SERÁ PERSISTIDA NO BANCO DE DADOS
 * 
 * @author Rafael Barros
 * 
 * **/
public class RelatorioFrequenciaTurmaIMD {
	
	/**Corresponde ao discente no qual o registro do registro pertence**/
	private DiscenteTecnico discente;
	
	/**Corresponde ao somatório de cargas horárias dos períodos
	 * de avaliação em que a frequência foi informada (frequência != null)**/
	private Double chExecutada;
	
	/**Corresponde ao somatório das cargas horárias dos períodos
	 * de avaliação em que o discente faltou **/
	private Double chFaltas;
	
	/**Corresponde a carga horária total do cronograma em que o discente pertence **/
	private Double chTotal;
	
	/**Percentual de faltas calculado a partir da CH executada em relação a CH de faltas**/
	private Double percentualFaltas;
	
	/**Quantidade de faltas**/
	private Double qtdFaltas;
	
	/**Campo textual que corresponde a percentualFaltas que possui a quantidade limitada de duas casas decimais**/
	private String percentualFaltasTexto;
	
	public RelatorioFrequenciaTurmaIMD(){
		discente = new DiscenteTecnico();
	}
	
	public DiscenteTecnico getDiscente() {
		return discente;
	}
	public void setDiscente(DiscenteTecnico discente) {
		this.discente = discente;
	}
	public Double getChExecutada() {
		return chExecutada;
	}
	public void setChExecutada(Double chExecutada) {
		this.chExecutada = chExecutada;
	}
	public Double getChFaltas() {
		return chFaltas;
	}
	public void setChFaltas(Double chFaltas) {
		this.chFaltas = chFaltas;
	}
	public Double getChTotal() {
		return chTotal;
	}
	public void setChTotal(Double chTotal) {
		this.chTotal = chTotal;
	}
	public Double getPercentualFaltas() {
		return percentualFaltas;
	}
	public void setPercentualFaltas(Double percentualFaltas) {
		this.percentualFaltas = percentualFaltas;
	}

	public String getPercentualFaltasTexto() {
		return percentualFaltasTexto;
	}

	public void setPercentualFaltasTexto(String percentualFaltasTexto) {
		this.percentualFaltasTexto = percentualFaltasTexto;
	}

	public Double getQtdFaltas() {
		return qtdFaltas;
	}

	public void setQtdFaltas(Double qtdFaltas) {
		this.qtdFaltas = qtdFaltas;
	}
	
	
	
	
}
