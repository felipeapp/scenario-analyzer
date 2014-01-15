/**
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Relacionamento entre MODULO  e QUALIFICAÇÃO
 * @author Andre M Dantas
 *
 */
@Entity
@Table(name = "modulo_qualificacao", schema = "tecnico", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"id_modulo", "id_qualificacao" }) })
public class ModuloQualificacao implements PersistDB{

	/** Chave primária. */
	private int id;

	/** Indica o Módulo associado. */
	private Modulo modulo;

	/** Indica a Qualificação associada. */
	private QualificacaoTecnico qualificacao;

	public ModuloQualificacao() {

	}

	public ModuloQualificacao(int id) {
		this.id = id;
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_modulo_qualificacao", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_modulo", unique = false, nullable = false, insertable = true, updatable = true)
	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_qualificacao", unique = false, nullable = false, insertable = true, updatable = true)
	public QualificacaoTecnico getQualificacao() {
		return qualificacao;
	}

	public void setQualificacao(QualificacaoTecnico qualificacao) {
		this.qualificacao = qualificacao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "modulo", "qualificacao");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(modulo, qualificacao);
	}


}
