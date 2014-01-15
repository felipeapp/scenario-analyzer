/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos de auxilio financeiro a pessoa f�sica, no ensino em disciplinas.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class AuxilioPFEnsinoDisciplinaDTO extends OrcamentoItemDTO{
	
	/** Representa a quantidade de horas. */
	private Integer quantHoras;

	/** Representa o valor unit�rio da hora.*/
	private Double valorHora;

	/** Representa o identificador da disciplina. */
	private Integer idDisciplina;

	/** Representa a disciplina. */
	private String disciplina;

	public void setQuantHoras(Integer quantHoras) {
		this.quantHoras = quantHoras;
	}

	public Integer getQuantHoras() {
		return quantHoras;
	}

	public void setValorHora(Double valorHora) {
		this.valorHora = valorHora;
	}

	public Double getValorHora() {
		return valorHora;
	}

	public void setIdDisciplina(Integer idDisciplina) {
		this.idDisciplina = idDisciplina;
	}

	public Integer getIdDisciplina() {
		return idDisciplina;
	}

	public void setDisciplina(String disciplina) {
		this.disciplina = disciplina;
	}

	public String getDisciplina() {
		return disciplina;
	}

}
