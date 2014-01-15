/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos do servi�o de consultoria.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ServicoConsultoriaDTO extends OrcamentoItemDTO{

	/** Representa a finalidade do servi�o.*/
	private String finalidade;
	
	private Double valorUnitario;

	public void setFinalidade(String finalidade) {
		this.finalidade = finalidade;
	}

	public String getFinalidade() {
		return finalidade;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}
}
