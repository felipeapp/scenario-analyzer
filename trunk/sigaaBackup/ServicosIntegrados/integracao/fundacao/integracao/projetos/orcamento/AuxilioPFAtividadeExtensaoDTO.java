/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que contém os dados específicos de auxilio financeiro a pessoa física, atividade de extensão.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class AuxilioPFAtividadeExtensaoDTO extends OrcamentoItemDTO{

	/** Representa a quantidade de horas.*/
	private Integer quantHoras;
	
	/** Representa valor da bolsa.*/
	private Double valorBolsa;

	public void setQuantHoras(Integer quantHoras) {
		this.quantHoras = quantHoras;
	}

	public Integer getQuantHoras() {
		return quantHoras;
	}

	public void setValorBolsa(Double valorBolsa) {
		this.valorBolsa = valorBolsa;
	}

	public Double getValorBolsa() {
		return valorBolsa;
	}
}
