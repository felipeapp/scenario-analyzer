/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos.orcamento;


/**
 * DTO que cont�m os dados espec�ficos de auxilio financeiro a pessoa f�sica, atividade de extens�o.
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
