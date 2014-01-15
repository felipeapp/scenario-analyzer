/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos de auxilio financeiro a pessoa física, no ensino em disciplinas.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class AuxilioPFEnsinoDisciplinaDTO extends OrcamentoItemDTO{
	
	/** Representa a quantidade de horas. */
	private Integer quantHoras;

	/** Representa o valor unitário da hora.*/
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
