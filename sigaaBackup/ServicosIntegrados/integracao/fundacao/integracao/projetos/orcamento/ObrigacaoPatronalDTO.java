/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos de despeza de Servi�o de Pessoa F�sica, relacionado a obriga��es patronais.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ObrigacaoPatronalDTO extends OrcamentoItemDTO{
	
	private Double valorUnitario;

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}
}
