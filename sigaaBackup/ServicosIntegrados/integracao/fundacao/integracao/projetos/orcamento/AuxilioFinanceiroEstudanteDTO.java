/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos de aux�lio financeiro a estudante.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class AuxilioFinanceiroEstudanteDTO extends OrcamentoItemDTO{

	/** Representa a quantidade de meses do aux�lio.*/
	private Integer quantidadeMeses;
	
	private Integer quantidade;

	private Double valorUnitario;

	public void setQuantidadeMeses(Integer quantidadeMeses) {
		this.quantidadeMeses = quantidadeMeses;
	}

	public Integer getQuantidadeMeses() {
		return quantidadeMeses;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
}
