/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos de despeza de Serviço de Pessoa Física, relacionado a obrigações patronais.
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
