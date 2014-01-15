/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 25/10/2010
 *
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * DTO com informações referentes aos bens para servir como comunicação entre 
 *  o  SIPAC  e um dispositivo móvel (Coletor de dados), representado pela classe:
 *  /SIPAC/ br.ufrn.sipac.patrimonio.tombamento.dominio.Bem
 * @author Alessandro
 */
public class BemDTO  implements Serializable {
	
	/**
	 * Identificador representado pela classe:  /SIPAC/ br.ufrn.sipac.patrimonio.tombamento.dominio.Bem
	 */
	private int idBem;
	
	/**
	 * Variável que representa a denominação de um bem, representado pela classe: 
	 *  /SIPAC/ br.ufrn.sipac.patrimonio.tombamento.dominio.Bem
	 */
	private String denominacao;
	
	/**
	 * Variável que representa o número de tombo de um bem. Representado pela classe
	 *  /SIPAC/ br.ufrn.sipac.patrimonio.tombamento.dominio.Bem
	 */
	private String numeroTombamento;
	
	
	/**
	 * Variável que representa o status do bem, podendo ser alienado, em movimentação temporária, etc.
	 * Representado pela classe de constante: /SIPAC/br.ufrn.sipac.patrimonio.tombamento.dominio.TipoStatus
	 */
	private Integer status;
	
	
	/**
	 * Variável que representa o id da localidade do bem. Representado pela classe: /SIPAC/ br.ufrn.sipac.patrimonio.comuns.dominio.LocalBem
	 */
	private Integer idLocal;
	
	/**
	 * Variável que representa o id da unidade responsável do bem. Representado pela classe: /SIPAC/ br.ufrn.sipac.cadastro.dominio.Unidade
	 */
	private Integer idUnidade;
	
	
	
	
	

	public Integer getIdLocal() {
		return idLocal;
	}

	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}

	public Integer getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(Integer idUnidade) {
		this.idUnidade = idUnidade;
	}

	public int getIdBem() {
		return idBem;
	}

	public void setIdBem(int idBem) {
		this.idBem = idBem;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public String getNumeroTombamento() {
		return numeroTombamento;
	}

	public void setNumeroTombamento(String numeroTombamento) {
		this.numeroTombamento = numeroTombamento;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
	

}
