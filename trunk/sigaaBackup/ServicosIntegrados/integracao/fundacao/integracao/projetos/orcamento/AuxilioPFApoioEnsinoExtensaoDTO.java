/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos de auxilio financeiro a pessoa f�sica, no apoio ao ensino ou a extens�o
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
