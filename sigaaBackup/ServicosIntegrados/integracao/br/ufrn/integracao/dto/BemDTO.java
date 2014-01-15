/*
 * Universidade Federal do Rio Grande do Norte

 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 25/10/2010
 *
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * DTO com informa��es referentes aos bens para servir como comunica��o entre 
 *  o  SIPAC  e um dispositivo m�vel (Coletor de dados), representado pela classe:
 *  /SIPAC/ br.ufrn.sipac.patrimonio.tombamento.dominio.Bem
 * @author Alessandro
 */
public class BemDTO  implements Serializable {
	
	/**
	 * Identificador representado pela classe:  /SIPAC/ br.ufrn.sipac.patrimonio.tombamento.dominio.Bem
	 */
	private int idBem;
	
	/**
	 * Vari�vel que representa a denomina��o de um bem, representado pela classe: 
	 *  /SIPAC/ br.ufrn.sipac.patrimonio.tombamento.dominio.Bem
	 */
	private String denominacao;
	
	/**
	 * Vari�vel que representa o n�mero de tombo de um bem. Representado pela classe
	 *  /SIPAC/ br.ufrn.sipac.patrimonio.tombamento.dominio.Bem
	 */
	private String numeroTombamento;
	
	
	/**
	 * Vari�vel que representa o status do bem, podendo ser alienado, em movimenta��o tempor�ria, etc.
	 * Representado pela classe de constante: /SIPAC/br.ufrn.sipac.patrimonio.tombamento.dominio.TipoStatus
	 */
	private Integer status;
	
	
	/**
	 * Vari�vel que representa o id da localidade do bem. Representado pela classe: /SIPAC/ br.ufrn.sipac.patrimonio.comuns.dominio.LocalBem
	 */
	private Integer idLocal;
	
	/**
	 * Vari�vel que representa o id da unidade respons�vel do bem. Representado pela classe: /SIPAC/ br.ufrn.sipac.cadastro.dominio.Unidade
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
