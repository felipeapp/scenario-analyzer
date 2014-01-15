/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/08/2008'
 *
 */
package br.ufrn.sigaa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Status que o atendimento se encontra. Se atendente leu ou respondeu...
 * 
 * @author Henrique Andre
 * 
 */
@Entity
@Table(schema="comum", name = "status_atendimento_aluno")
public class StatusAtendimentoAluno {

	public static final int ALUNO_PERGUNTOU = 1;
	public static final int ATENDENTE_LEU = 2;
	public static final int ATENDENTE_RESPONDEU = 3;
	public static final int ALUNO_LEU = 4;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_status_atendimento")
	private int id;

	/**
	 * Descrição do status
	 */
	@Column(name = "descricao")
	private String descricao;

	public StatusAtendimentoAluno() {
	}

	public StatusAtendimentoAluno(int id) {
		this.id = id;
	}

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

}
