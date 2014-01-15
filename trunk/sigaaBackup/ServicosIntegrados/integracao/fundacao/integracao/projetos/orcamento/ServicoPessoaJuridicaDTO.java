/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos do serviço de pessoa jurídica.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ServicoPessoaJuridicaDTO extends OrcamentoItemDTO{

	/** Indica se o serviço está sendo prestado por uma cooperativa. */
	private Boolean cooperativa;
	
	private Double valorUnitario;

	public void setCooperativa(Boolean cooperativa) {
		this.cooperativa = cooperativa;
	}

	public Boolean getCooperativa() {
		return cooperativa;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}
}
