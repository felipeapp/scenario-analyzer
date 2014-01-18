package br.ufrn.sigaa.ensino.metropoledigital.dominio;

/**
 * Entidade utilizada na integração das notas de Participação Virtual, do Aluno, no moodle;
 * 
 * @author Rafael Barros
 * @author Rafael Silva
 *
 */
public class RetornoNotaPVIntegracaoMoodle {
	/**Matrícula do Discente*/
	private String matriculaDiscente;
	/**Código de Itegração com o moodle*/
	private String codigoIntegracaoPeriodo;
	/**Nota de Participação Virtual do Aluno no moodle;*/
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
