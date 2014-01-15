package fundacao.integracao.projetos.orcamento;


/**
 * Classe que contém os dados especíicos de material permanente 
 * 
 * @author sig-gleydson
 *
 */
public class MaterialPermanenteDTO extends OrcamentoItemDTO {
	
	private Integer quantidade;
	
	private Double valorUnitario;
	
	private String destinacao;
	
	/** Indica se o material permanente será ou não importado */
	private Boolean internacional;

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setInternacional(Boolean internacional) {
		this.internacional = internacional;
	}

	public Boolean getInternacional() {
		return internacional;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setDestinacao(String destinacao) {
		this.destinacao = destinacao;
	}

	public String getDestinacao() {
		return destinacao;
	}

	
	
}
