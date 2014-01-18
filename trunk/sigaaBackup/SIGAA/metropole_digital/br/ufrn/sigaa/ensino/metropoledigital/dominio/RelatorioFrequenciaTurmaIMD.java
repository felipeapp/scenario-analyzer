package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;

/**
 * Entidade auxiliar que ser� respons�vel por armazenar as informa��es e o processamentos dos dados do
 * relat�rio de frequencia dos discentes do IMD.
 * 
 * ESTA ENTIDADE N�O SER� PERSISTIDA NO BANCO DE DADOS
 * 
 * @author Rafael Barros
 * 
 * **/
public class RelatorioFrequenciaTurmaIMD {
	
	/**Corresponde ao discente no qual o registro do registro pertence**/
	private DiscenteTecnico discente;
	
	/**Corresponde ao somat�rio de cargas hor�rias dos per�odos
	 * de avalia��o em que a frequ�ncia foi informada (frequ�ncia != null)**/
	private Double chExecutada;
	
	/**Corresponde ao somat�rio das cargas hor�rias dos per�odos
	 * de avalia��o em que o discente faltou **/
	private Double chFaltas;
	
	/**Corresponde a carga hor�ria total do cronograma em que o discente pertence **/
	private Double chTotal;
	
	/**Percentual de faltas calculado a partir da CH executada em rela��o a CH de faltas**/
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
