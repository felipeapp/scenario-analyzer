/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*******************************************************************************
 * <p>Status para as solicitações de renovação de projetos.</p>
 * 
 * @author David Ricardo
 * 
 ******************************************************************************/
@Entity
@Table(name = "status_solicitacao_renovacao", schema = "monitoria")
public class StatusSolicitacaoRenovacao {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	public static final int AGUARDANDO_APROVACAO = 1;

	public static final int APROVADO = 2;

	public static final int NEGADO = 3;

	public StatusSolicitacaoRenovacao() {

	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao
	 *            the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public static StatusSolicitacaoRenovacao getAguardandoAprovacao() {
		StatusSolicitacaoRenovacao status = new StatusSolicitacaoRenovacao();
		status.id = AGUARDANDO_APROVACAO;
		return status;
	}

	public static StatusSolicitacaoRenovacao getAprovado() {
		StatusSolicitacaoRenovacao status = new StatusSolicitacaoRenovacao();
		status.id = APROVADO;
		return status;
	}

	public static StatusSolicitacaoRenovacao getNegado() {
		StatusSolicitacaoRenovacao status = new StatusSolicitacaoRenovacao();
		status.id = NEGADO;
		return status;
	}

}
