/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos dos itens das obrigações tributárias.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class TributacaoElementoDespesaDTO extends OrcamentoItemDTO{

	/** Representa o identificador do tipo do tributo.*/
	private Integer idTipoTributo;

	/** Representa o tipo do tributo.*/
	private String tipoTributo;

	/** Percentual do tributo inserido */
	private Double valorPercentual;

	public void setIdTipoTributo(Integer idTipoTributo) {
		this.idTipoTributo = idTipoTributo;
	}

	public Integer getIdTipoTributo() {
		return idTipoTributo;
	}

	public void setTipoTributo(String tipoTributo) {
		this.tipoTributo = tipoTributo;
	}

	public String getTipoTributo() {
		return tipoTributo;
	}

	public void setValorPercentual(Double valorPercentual) {
		this.valorPercentual = valorPercentual;
	}

	public Double getValorPercentual() {
		return valorPercentual;
	}
}
