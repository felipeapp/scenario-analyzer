/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package fundacao.integracao.projetos.orcamento;

import java.io.Serializable;
import java.util.List;


/**
 * Data Transfer Object para informa��es dos planos de aplica��o.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class OrcamentoRubricaDTO implements Serializable {

	/** Representa o elemento de despesa.*/
	private String elementoDespesa;

	/** Representa o valor de despesa.*/
	private Double valor;

	/** Representa as planilhas or�ament�rias que detalham o plano de aplica��o.*/
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
