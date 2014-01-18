package br.ufrn.sigaa.ensino.metropoledigital.dominio;

/**
 * Entidade utilizada na integra��o das notas de Participa��o Virtual, do Aluno, no moodle;
 * 
 * @author Rafael Barros
 * @author Rafael Silva
 *
 */
public class RetornoNotaPVIntegracaoMoodle {
	/**Matr�cula do Discente*/
	private String matriculaDiscente;
	/**C�digo de Itegra��o com o moodle*/
	private String codigoIntegracaoPeriodo;
	/**Nota de Participa��o Virtual do Aluno no moodle;*/
	private Double notaPv;
	
	public RetornoNotaPVIntegracaoMoodle(String matriculaDiscente, String codigoIntegracaoPeriodo, Double notaPv){
		this.matriculaDiscente = matriculaDiscente;
		this.codigoIntegracaoPeriodo = codigoIntegracaoPeriodo;
		this.notaPv = notaPv;
	}
	
	public String getMatriculaDiscente() {
		return matriculaDiscente;
	}
	public void setMatriculaDiscente(String matriculaDiscente) {
		this.matriculaDiscente = matriculaDiscente;
	}
	public String getCodigoIntegracaoPeriodo() {
		return codigoIntegracaoPeriodo;
	}
	public void setCodigoIntegracaoPeriodo(String codigoIntegracaoPeriodo) {
		this.codigoIntegracaoPeriodo = codigoIntegracaoPeriodo;
	}
	public Double getNotaPv() {
		return notaPv;
	}
	public void setNotaPv(Double notaPv) {
		this.notaPv = notaPv;
	}
	
	
	
}
