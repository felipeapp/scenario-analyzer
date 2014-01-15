/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos do serviço de pessoa física.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ServicoPessoaFisicaDTO extends OrcamentoItemDTO{

	/** Representa a finalidade do serviço.*/
	private String finalidade;
	
	/** Indica a quantidade de pessoas que irá receber pelo serviço. */
	private Double quantidade;
	
	private Double valorUnitario;

	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}

	public String getFinalidade() {
		return finalidade;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public Double getQuantidade() {
		return quantidade;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}
}
