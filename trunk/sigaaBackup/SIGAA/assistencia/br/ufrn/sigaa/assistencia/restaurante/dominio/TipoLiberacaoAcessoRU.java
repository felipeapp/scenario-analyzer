/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/02/2009
 *
 */	
package br.ufrn.sigaa.assistencia.restaurante.dominio;

/**
 * Entidade que representa as possibilidades de liberação da 
 * catraca disponíveis no cliente desktop do RU.
 * 
 * @author agostinho campos
 *
 */
public class TipoLiberacaoAcessoRU {
	
	 private static int USUARIO_EVENTUAL = 1;
	 private static int CONVENIADO_NAO_IDENTIFICADO = 2;
	 private static int PAGANTE = 3;
	 private static int OUTRO = 4;
	 private static int PROPRIO_DISCENTE_POR_DIGITAL = 5;
	 private static int PROPRIO_DISCENTE_POR_CARTAO = 6;
	
	private int id;
	private String descricao;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public boolean isUsuarioEventual() {
		if (id == USUARIO_EVENTUAL)
			return true;
		else 
			return false;
	}
	
	public boolean isConveniadoNaoIdentificado() {
		if (id == CONVENIADO_NAO_IDENTIFICADO)
			return true;
		else 
			return false;
	}
	
	public boolean isOutro() {
		if (id == OUTRO)
			return true;
		else 
			return false;
	}
	
	public boolean isPagante() {
		if (id == PAGANTE)
			return true;
		else 
			return false;
	}
	
	public boolean isDiscentePorDigital() {
		if (id == PROPRIO_DISCENTE_POR_DIGITAL)
			return true;
		else 
			return false;
	}
	
	public boolean isDiscentePorCartao() {
		if (id == PROPRIO_DISCENTE_POR_CARTAO)
			return true;
		else 
			return false;
	}

}
