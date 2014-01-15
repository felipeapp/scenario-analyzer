package br.ufrn.sigaa.vestibular.dominio;


/**
 * Representa as linhas do question�rio S�cio Econ�mico do Vestibular. 
 * 
 * @author Jean Guerethes
 */
public class LinhaQuestionarioRespostas {

	/** Ordem da pergunta do questionario*/
	private int ordem;
	
	/** Descri��o da pergunta do question�rio */
	private String pergunta;
	
	/** Decri��o da pergunta */
	private String alternativa;
	
	/** Total Parcial das respostas por pergunta */
	private Double totalParcial;
	
	/** Total Geral das respostas por pergunta */
	private Double totalGeral;
	
	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public String getAlternativa() {
		return alternativa;
	}

	public void setAlternativa(String alternativa) {
		this.alternativa = alternativa;
	}

	public Double getTotalParcial() {
		return totalParcial;
	}

	public void setTotalParcial(Double totalParcial) {
		this.totalParcial = totalParcial;
	}

	public Double getTotalGeral() {
		return totalGeral;
	}

	public void setTotalGeral(Double totalGeral) {
		this.totalGeral = totalGeral;
	}

}