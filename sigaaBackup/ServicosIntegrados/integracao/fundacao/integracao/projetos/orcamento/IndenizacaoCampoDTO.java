/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos das indenizações informadas no detalhamento do plano de aplicação.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class IndenizacaoCampoDTO extends OrcamentoItemDTO {

	/** Representa a quantidade de indenizações.*/
	private Integer quantidade;
	
	private Double valorUnitario;

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}
}
