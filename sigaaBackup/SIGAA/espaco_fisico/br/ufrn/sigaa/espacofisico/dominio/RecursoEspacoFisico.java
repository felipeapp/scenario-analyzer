/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/03/2009
 *
 */
package br.ufrn.sigaa.espacofisico.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que representa os recursos auxiliares que um espaço físico possui.
 * Ex.: Computadores, projetor, retroprojetor, etc.
 * 
 * @author wendell
 *
 */
@Entity
@Table(name = "recurso_espaco_fisico", schema = "espaco_fisico")
public class RecursoEspacoFisico implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_recurso_espaco_fisico")
	private int id;
	
	private Integer quantidade;
	
	@ManyToOne
	@JoinColumn(name = "id_espaco_fisico")
	private EspacoFisico espacoFisico;
	
	@ManyToOne
	@JoinColumn(name = "id_tipo_recurso_espaco_fisico")
	private TipoRecursoEspacoFisico tipo;

	private boolean ativo = true;

	public RecursoEspacoFisico() {

	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public TipoRecursoEspacoFisico getTipo() {
		return tipo;
	}

	public void setTipo(TipoRecursoEspacoFisico tipo) {
		this.tipo = tipo;
	}

	public EspacoFisico getEspacoFisico() {
		return espacoFisico;
	}

	public void setEspacoFisico(EspacoFisico espacoFisico) {
		this.espacoFisico = espacoFisico;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(tipo);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "tipo", "espacoFisico", "ativo");
	}

}
