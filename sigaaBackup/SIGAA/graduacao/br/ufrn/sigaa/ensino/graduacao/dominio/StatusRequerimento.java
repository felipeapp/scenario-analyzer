/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Representa o status do requerimento
 * 
 * @author Henrique Andre
 */

@Entity
@Table(name = "status_requerimento", schema = "graduacao", uniqueConstraints = {})
public class StatusRequerimento {

	public StatusRequerimento () {
		
	}
	
	public StatusRequerimento(int id) {
		this.id = id;
	}
	// aluno criou e não enviou
	public static final int ABERTO_PELO_ALUNO = 1;
	// aluno criou e enviou
	public static final int SUBMETIDO_PELO_ALUNO = 2;
	// aluno criou e enviou, mas por alguma razão o DAE reabriu
	public static final int REABERTO_PELO_DAE = 3;
	// aluno criou e enviou. requerimento esta sendo analisado
	public static final int EM_ANALISE_PELO_DAE = 4;
	// coloquei por intuicao, nao sei nem se isso existe
	public static final int PROTOCOLADO = 5;
	// requerimento atendido
	public static final int ATENDIDO = 6;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_status_requerimento", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@Column(name = "descricao")
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
	
}
