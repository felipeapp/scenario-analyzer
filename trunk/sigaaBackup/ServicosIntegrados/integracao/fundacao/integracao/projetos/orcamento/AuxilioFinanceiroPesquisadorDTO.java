/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos do auxílio financeiro a pesquisador.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class AuxilioFinanceiroPesquisadorDTO extends OrcamentoItemDTO{

	/** Representa o identificador do tipo de auxilio.*/
	private Integer idTipoAuxilio;

	/** Representa o tipo de auxilio.*/
	private String tipoAuxilio;

	/** Indica se o auxílio é mensal. */
	private Boolean mensal;

	/** Representa o valor da hora.*/
	private Double valorHora;

	/** Representa a quantidade de bolsas. */
	private Integer quantidadeBolsa;

	public void setIdTipoAuxilio(Integer idTipoAuxilio) {
		this.idTipoAuxilio = idTipoAuxilio;
	}

	public Integer getIdTipoAuxilio() {
		return idTipoAuxilio;
	}

	public void setTipoAuxilio(String tipoAuxilio) {
		this.tipoAuxilio = tipoAuxilio;
	}

	public String getTipoAuxilio() {
		return tipoAuxilio;
	}

	public void setMensal(Boolean mensal) {
		this.mensal = mensal;
	}

	public Boolean getMensal() {
		return mensal;
	}

	public void setValorHora(Double valorHora) {
		this.valorHora = valorHora;
	}

	public Double getValorHora() {
		return valorHora;
	}

	public void setQuantidadeBolsa(Integer quantidadeBolsa) {
		this.quantidadeBolsa = quantidadeBolsa;
	}

	public Integer getQuantidadeBolsa() {
		return quantidadeBolsa;
	}
}
