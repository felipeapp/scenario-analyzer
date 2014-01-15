/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;


/**
 * Tipo de trabalho de conclusão de curso que o discente pode cumprir
 * 
 * @author Ricardo Wendell
 */
@Entity
@Table(name = "tipo_trabalho_conclusao", schema = "ensino", uniqueConstraints = {})
public class TipoTrabalhoConclusao implements PersistDB {
	/** Id do tipo Monografia */
	public static final int MONOGRAFIA = 10; 
	/** Chave primária */
	private int id;
    /** Descrição do Tipo de trabalho de Conclusão */
	private String descricao;

	// Níveis aos quais o tipo de trabalho de conclusão se aplica
	private boolean doutorado;
	private boolean mestrado;
	private boolean latoSensu;
	private boolean graduacao;
	private boolean tecnico;

	// Constructors

	/** default constructor */
	public TipoTrabalhoConclusao() {
	}

	/** default minimal constructor */
	public TipoTrabalhoConclusao(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public TipoTrabalhoConclusao(int idTipoTrabalhoConclusao, String descricao) {
		this.id = idTipoTrabalhoConclusao;
		this.descricao = descricao;
	}

	/** full constructor */
	public TipoTrabalhoConclusao(int idTipoTrabalhoConclusao, String descricao,
			Set<CursoLato> cursoLatos) {
		this.id = idTipoTrabalhoConclusao;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_trabalho_conclusao", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoTrabalhoConclusao) {
		this.id = idTipoTrabalhoConclusao;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isDoutorado() {
		return this.doutorado;
	}

	public void setDoutorado(boolean doutorado) {
		this.doutorado = doutorado;
	}

	public boolean isGraduacao() {
		return this.graduacao;
	}

	public void setGraduacao(boolean graduacao) {
		this.graduacao = graduacao;
	}

	@Column(name = "lato_sensu")
	public boolean isLatoSensu() {
		return this.latoSensu;
	}

	public void setLatoSensu(boolean latoSensu) {
		this.latoSensu = latoSensu;
	}

	public boolean isMestrado() {
		return this.mestrado;
	}

	public void setMestrado(boolean mestrado) {
		this.mestrado = mestrado;
	}

	public boolean isTecnico() {
		return this.tecnico;
	}

	public void setTecnico(boolean tecnico) {
		this.tecnico = tecnico;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	@Transient
	public boolean isMonografia(){
		return id == MONOGRAFIA;
	}

}
