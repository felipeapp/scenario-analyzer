/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos do servi�o de pessoa jur�dica.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ServicoPessoaJuridicaDTO extends OrcamentoItemDTO{

	/** Indica se o servi�o est� sendo prestado por uma cooperativa. */
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
