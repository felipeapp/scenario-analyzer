/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Classe que define os tipos de comunidades virtuais.
 * 
 * @author Agostinho
 */

@Entity 
@Table(name="tipo_comunidade_virtual", schema="cv")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoComunidadeVirtual implements Validatable {

	public static int PRIVADA = 1;
	public static int PUBLICA = 2;
	public static int RESTRITO_GRUPO = 3;
	public static int MODERADA = 4;
	
	public static int BUSCAR_TODOS_TIPOS_COMUNIDADE_VIRTUAL = 5;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id_tipo_comunidade_virtual")
	private int id;
	private String descricao;
	
	/** Define se as comunidades deste tipo podem ser criadas por todos ou somente por gestores de comunidades */
	@Column(name="criacao_restrita_gestores")
	private boolean criacaoRestritaGestores;
	
	public TipoComunidadeVirtual() {
	}
	
	public TipoComunidadeVirtual(Integer idTipoComunidadeVirtual) {
		id = idTipoComunidadeVirtual;
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

	public ListaMensagens validate() {
		return null;
	}
	
	
	public boolean isPublica() {
		return id == PUBLICA ? true : false;	
	}
	
	public boolean isPrivada() {
		return id == PRIVADA ? true : false;
	}
	
	public boolean isModerada() {
		return id == MODERADA ? true : false;
	}
	
	public boolean isRestritoGrupo() {
		return id == RESTRITO_GRUPO ? true : false;
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
		TipoComunidadeVirtual other = (TipoComunidadeVirtual) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void setCriacaoRestritaGestores(boolean criacaoRestritaGestores) {
		this.criacaoRestritaGestores = criacaoRestritaGestores;
	}

	public boolean isCriacaoRestritaGestores() {
		return criacaoRestritaGestores;
	}

}
