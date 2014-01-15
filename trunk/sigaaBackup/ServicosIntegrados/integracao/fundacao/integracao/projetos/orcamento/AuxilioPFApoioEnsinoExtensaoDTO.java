/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos de auxilio financeiro a pessoa física, no apoio ao ensino ou a extensão
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class AuxilioPFApoioEnsinoExtensaoDTO extends OrcamentoItemDTO{

	/** Quantidade do apoio. */
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
