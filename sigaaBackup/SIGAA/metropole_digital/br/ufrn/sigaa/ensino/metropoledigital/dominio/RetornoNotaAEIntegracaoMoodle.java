package br.ufrn.sigaa.ensino.metropoledigital.dominio;


/**
 * Entidade utilizada na integra��o das notas das Atividades Executadas, do aluno, no moodle
 * 
 * @author Rafael Barros
 * @author Rafael Silva
 *
 */
public class RetornoNotaAEIntegracaoMoodle {
	/**Matr�cula do Discente*/
	private String matriculaDiscente;
	/**C�digo de Itegra��o com o moodle*/
	private String codigoIntegracaoDisciplina;
	/**M�dia das Atividades Executadas, do aluno, no moodle*/
	private Double notaAe;
	
	public RetornoNotaAEIntegracaoMoodle(String matriculaDiscente, String codigoIntegracaoDisciplina, Double notaAe){
		this.matriculaDiscente = matriculaDiscente;
		this.codigoIntegracaoDisciplina = codigoIntegracaoDisciplina;
		this.notaAe = notaAe;
	}
	
	public String getMatriculaDiscente() {
		return matriculaDiscente;
	}
	public void setMatriculaDiscente(String matriculaDiscente) {
		this.matriculaDiscente = matriculaDiscente;
	}
	public String getCodigoIntegracaoDisciplina() {
		return codigoIntegracaoDisciplina;
	}
	public void setCodigoIntegracaoDisciplina(String codigoIntegracaoDisciplina) {
		this.codigoIntegracaoDisciplina = codigoIntegracaoDisciplina;
	}
	public Double getNotaAe() {
		return notaAe;
	}
	public void setNotaAe(Double notaAe) {
		this.notaAe = notaAe;
	}
	
	
	
}
