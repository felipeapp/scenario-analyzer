/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos das obras/instalações.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ObraInstalacaoDTO extends OrcamentoItemDTO{

	/** Representa a discriminação da obra/instalação.*/
	private String discriminacao;
	
	/** Representa a finalidade da obra/instalação. */
	private String finalidade;
	
	private Double valorUnitario;
	
	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}

	public String getFinalidade() {
		return finalidade;
	}

	public void setDiscriminacao(String discriminacao) {
		this.discriminacao = discriminacao;
	}

	public String getDiscriminacao() {
		return discriminacao;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}
}
