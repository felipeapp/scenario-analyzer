/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos do valor vencimento e vantagens f�sicas.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class VencimentoVantagensFixasDTO extends OrcamentoItemDTO{

	/** Refere-se ao cargo a ser desempenhado detalhado na rubrica */
	private String cargo;

	/** Refere-se a fun��o a ser desempenhada detalhada na planilha or�ament�ria */
	private String funcaoDesempenhada;

	/** Quantidade de meses que os funcion�rios contratados exercer�o as fun��es informadas */
	private Integer quantidadeMeses;

	/** Quantidade da planilha or�ament�ria. */
	private Integer quantidade;
	
	private Double valorUnitario;

	public String getFuncaoDesempenhada() {
		return funcaoDesempenhada;
	}

	public void setFuncaoDesempenhada(String funcaoDesempenhada) {
		this.funcaoDesempenhada = funcaoDesempenhada;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public void setQuantidadeMeses(Integer quantidadeMeses) {
		this.quantidadeMeses = quantidadeMeses;
	}

	public Integer getQuantidadeMeses() {
		return quantidadeMeses;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

}
