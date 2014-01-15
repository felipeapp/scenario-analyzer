/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package fundacao.integracao.projetos;

import java.io.Serializable;


/**
 * Data Transfer Object para informações do etapa da meta do projeto.
 * 
 * @author Eduardo Costa (UFRN)
 */
public class EtapaDTO implements Serializable {
	
	/** Número da etapa. */
	private String numeroEtapa;

	/** Etapa. */
	private Integer etapa;

	/** Mês de iníco da etapa. */
	private Integer mesInicio;

	/** Ano de iníco da etapa. */
	private Integer anoInicio;

	/** Mês de término da etapa. */
	private Integer mesFim;

	/** Ano de término da etapa. */
	private Integer anoFim;

	/** Especificação da etapa. */
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
