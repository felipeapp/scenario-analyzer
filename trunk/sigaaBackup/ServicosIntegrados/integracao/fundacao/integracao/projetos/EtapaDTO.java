/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package fundacao.integracao.projetos;

import java.io.Serializable;


/**
 * Data Transfer Object para informa��es do etapa da meta do projeto.
 * 
 * @author Eduardo Costa (UFRN)
 */
public class EtapaDTO implements Serializable {
	
	/** N�mero da etapa. */
	private String numeroEtapa;

	/** Etapa. */
	private Integer etapa;

	/** M�s de in�co da etapa. */
	private Integer mesInicio;

	/** Ano de in�co da etapa. */
	private Integer anoInicio;

	/** M�s de t�rmino da etapa. */
	private Integer mesFim;

	/** Ano de t�rmino da etapa. */
	private Integer anoFim;

	/** Especifica��o da etapa. */
	private String especificacao;

	/** Unidade de medida da etapa. */
	private String unidadeMedida;

	/** Quantidade da etapa. */
	private Double quantidade;

	/** Valor da etapa. */
	private Double valor;

	public void setNumeroEtapa(String numeroEtapa) {
		this.numeroEtapa = numeroEtapa;
	}

	public String getNumeroEtapa() {
		return numeroEtapa;
	}

	public void setEtapa(Integer etapa) {
		this.etapa = etapa;
	}

	public Integer getEtapa() {
		return etapa;
	}

	public void setMesInicio(Integer mesInicio) {
		this.mesInicio = mesInicio;
	}

	public Integer getMesInicio() {
		return mesInicio;
	}

	public void setAnoInicio(Integer anoInicio) {
		this.anoInicio = anoInicio;
	}

	public Integer getAnoInicio() {
		return anoInicio;
	}

	public void setMesFim(Integer mesFim) {
		this.mesFim = mesFim;
	}

	public Integer getMesFim() {
		return mesFim;
	}

	public void setAnoFim(Integer anoFim) {
		this.anoFim = anoFim;
	}

	public Integer getAnoFim() {
		return anoFim;
	}

	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public String getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public Double getQuantidade() {
		return quantidade;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getValor() {
		return valor;
	}

	public void setEspecificacao(String especificacao) {
		this.especificacao = especificacao;
	}

	public String getEspecificacao() {
		return especificacao;
	}
}
