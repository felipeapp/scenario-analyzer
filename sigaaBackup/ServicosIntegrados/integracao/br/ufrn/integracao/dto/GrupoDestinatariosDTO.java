/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Data transfer object para guardar as informações
 * de destinatários de notificações. Ver classe GrupoDestinatarios
 * do SIGAdmin.
 * 
 * @author David Pereira
 *
 */
public class GrupoDestinatariosDTO implements Serializable {

	private int id;
	
	private String descricao;

	private String sqlDestinatarios;
	
	private boolean ativo;
	
	private boolean memorandoCircular;
	
	private boolean telaAvisoLogon;
	
	private Boolean participaComunidadeVirtual;

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

	public String getSqlDestinatarios() {
		return sqlDestinatarios;
	}

	public void setSqlDestinatarios(String sqlDestinatarios) {
		this.sqlDestinatarios = sqlDestinatarios;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isMemorandoCircular() {
		return memorandoCircular;
	}

	public void setMemorandoCircular(boolean memorandoCircular) {
		this.memorandoCircular = memorandoCircular;
	}

	public boolean isTelaAvisoLogon() {
		return telaAvisoLogon;
	}

	public void setTelaAvisoLogon(boolean telaAvisoLogon) {
		this.telaAvisoLogon = telaAvisoLogon;
	}

	public Boolean getParticipaComunidadeVirtual() {
		return participaComunidadeVirtual;
	}

	public void setParticipaComunidadeVirtual(Boolean participaComunidadeVirtual) {
		this.participaComunidadeVirtual = participaComunidadeVirtual;
	}

}
