/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos de auxilio financeiro a pessoa f�sica, na orienta��o de monografias e supervis�o de trabalhos.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class AuxilioPFOrientacaoMonografiaDTO extends OrcamentoItemDTO{

	/** Representa a quantidade de orienta��es.*/
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
