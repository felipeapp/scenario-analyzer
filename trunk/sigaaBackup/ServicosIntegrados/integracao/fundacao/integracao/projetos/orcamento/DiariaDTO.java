/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos de diárias.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class DiariaDTO extends OrcamentoItemDTO{

	/** Representa a finalidade da diária */
	private String finalidade;

	/** Indica se a diária é internacional ou não. */
	private Boolean internacional;
	
	private Integer quantidadeDiarias;
	
	private Double valorUnitario;

	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}

	public String getFinalidade() {
		return finalidade;
	}

	public void setInternacional(Boolean internacional) {
		this.internacional = internacional;
	}

	public Boolean getInternacional() {
		return internacional;
	}

	public void setQuantidadeDiarias(Integer quantidadeDiarias) {
		this.quantidadeDiarias = quantidadeDiarias;
	}

	public Integer getQuantidadeDiarias() {
		return quantidadeDiarias;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}
}
