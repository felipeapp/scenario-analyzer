/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package fundacao.integracao.projetos.orcamento;

import java.io.Serializable;
import java.util.List;


/**
 * Data Transfer Object para informações dos planos de aplicação.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class OrcamentoRubricaDTO implements Serializable {

	/** Representa o elemento de despesa.*/
	private String elementoDespesa;

	/** Representa o valor de despesa.*/
	private Double valor;

	/** Representa as planilhas orçamentárias que detalham o plano de aplicação.*/
	private List<OrcamentoItemDTO> planilhasOrcamentaria;

	public void setElementoDespesa(String elementoDespesa) {
		this.elementoDespesa = elementoDespesa;
	}

	public String getElementoDespesa() {
		return elementoDespesa;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getValor() {
		return valor;
	}

	public void setPlanilhasOrcamentaria(List<OrcamentoItemDTO> planilhasOrcamentaria) {
		this.planilhasOrcamentaria = planilhasOrcamentaria;
	}

	public List<OrcamentoItemDTO> getPlanilhasOrcamentaria() {
		return planilhasOrcamentaria;
	}
	
	
}
