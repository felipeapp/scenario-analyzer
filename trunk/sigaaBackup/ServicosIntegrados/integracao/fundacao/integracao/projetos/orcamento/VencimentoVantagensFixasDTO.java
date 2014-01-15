/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos do valor vencimento e vantagens físicas.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class VencimentoVantagensFixasDTO extends OrcamentoItemDTO{

	/** Refere-se ao cargo a ser desempenhado detalhado na rubrica */
	private String cargo;

	/** Refere-se a função a ser desempenhada detalhada na planilha orçamentária */
	private String funcaoDesempenhada;

	/** Quantidade de meses que os funcionários contratados exercerão as funções informadas */
	private Integer quantidadeMeses;

	/** Quantidade da planilha orçamentária. */
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
