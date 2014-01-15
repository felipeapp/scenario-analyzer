/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos de auxilio financeiro a pessoa física, na orientação de monografias e supervisão de trabalhos.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class AuxilioPFOrientacaoMonografiaDTO extends OrcamentoItemDTO{

	/** Representa a quantidade de orientações.*/
	private Integer quantidadeOrientandos;
	
	private Double valorUnitario;

	public void setQuantidadeOrientandos(Integer quantidadeOrientandos) {
		this.quantidadeOrientandos = quantidadeOrientandos;
	}

	public Integer getQuantidadeOrientandos() {
		return quantidadeOrientandos;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

}
