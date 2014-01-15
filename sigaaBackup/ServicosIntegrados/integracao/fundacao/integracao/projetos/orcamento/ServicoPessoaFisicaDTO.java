/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos do servi�o de pessoa f�sica.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ServicoPessoaFisicaDTO extends OrcamentoItemDTO{

	/** Representa a finalidade do servi�o.*/
	private String finalidade;
	
	/** Indica a quantidade de pessoas que ir� receber pelo servi�o. */
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
