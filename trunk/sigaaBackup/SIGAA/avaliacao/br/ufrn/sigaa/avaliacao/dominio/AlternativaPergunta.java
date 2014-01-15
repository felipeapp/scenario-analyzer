/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 29/04/2008
 */
package br.ufrn.sigaa.avaliacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa uma possibilidade de resposta
 * a uma pergunta do tipo múltipla escolha ou escolha única.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="alternativa_pergunta", schema="avaliacao")
@Cache ( usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
public class AlternativaPergunta implements PersistDB {

	/** Chave primária */
	@Id @GeneratedValue
	private int id;
	
	/** Pergunta ao qual esta alternativa pertence. */
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="id_pergunta")
	private Pergunta pergunta;
	
	/** Descrição da Alternativa. */
	private String descricao;
	
	/** Indica se permite citação. É o caso da alternativa ser do tipo "outros, cite:". */
	@Column(name="permite_citacao")
	private boolean permiteCitacao;

	/** Indica que a alternativa é ativa. */
	private boolean ativa;
	
	/** Ordem da alternativa na pergunta. */
	@Transient
	private int ordem;

	public AlternativaPergunta() {

	}
	
	public AlternativaPergunta(int id) {
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

	public boolean isPermiteCitacao() {
		return permiteCitacao;
	}

	public void setPermiteCitacao(boolean permiteCitacao) {
		this.permiteCitacao = permiteCitacao;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AlternativaPergunta other = (AlternativaPergunta) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

}