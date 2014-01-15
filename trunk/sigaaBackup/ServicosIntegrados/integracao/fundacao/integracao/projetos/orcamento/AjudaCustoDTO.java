/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos da ajuda de custo.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class AjudaCustoDTO extends OrcamentoItemDTO{

	/** Representa o favorecido.*/
	private String favorecido;

	/** Representa a quantidade da ajuda de custo. */
	private Integer quantidade;
	
	private Double valorUnitario;

	public void setFavorecido(String favorecido) {
		this.favorecido = favorecido;
	}

	public String getFavorecido() {
		return favorecido;
	}

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
