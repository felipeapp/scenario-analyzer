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

@Entity
@Table(name = "tipo_requerimento", schema = "graduacao", uniqueConstraints = {})
public class TipoRequerimento {
	public static final int RETIFICACAO_DE_NOTA = 1;
	public static final int SOLICITACAO_DE_EQUIVALENCIA = 2;
	public static final int MOBILIDADE_ESTUDANTIL = 3;
	public static final int TRANCAMENTO_PROGRAMA = 4;

	public TipoRequerimento() {
	}		
	
	public TipoRequerimento(int id) {
		this.id = id;
	}	
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_requerimento", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	/*
	 * P = requerimento padrão
	 * T = trancamento
	 */
	@Column(name = "especializacao")
	private String especializacao;
	
	public String getEspecializacao() {
		return especializacao;
	}

	public void setEspecializacao(String especializacao) {
		this.especializacao = especializacao;
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
