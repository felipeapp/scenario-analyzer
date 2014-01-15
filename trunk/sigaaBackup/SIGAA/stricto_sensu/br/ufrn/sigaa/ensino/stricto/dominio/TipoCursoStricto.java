/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * tipo que cursos stricto podem ser: Mestrado Acadêmico, Mestrado Profissional, Doutorado
 */
@Entity @Table(name = "tipo_curso_stricto", schema = "stricto_sensu")
public class TipoCursoStricto implements PersistDB {

	public static TipoCursoStricto MESTRADO_PROFISSIONAL = new TipoCursoStricto(1, "MESTRADO PROFISSIONAL");

	public static TipoCursoStricto MESTRADO_ACADEMICO = new TipoCursoStricto(2, "MESTRADO ACADEMICO");

	public static TipoCursoStricto DOUTORADO = new TipoCursoStricto(3, "DOUTORADO");

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_curso_stricto", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** descrição do tipo de curso */
	private String descricao;

	/** nível acadêmico do tipo de curso (E ou D) */
	private char nivel;

	public TipoCursoStricto() {
	}

	public TipoCursoStricto(int i) {
		id = i;
	}

	public TipoCursoStricto(int i, String d) {
		id = i;
		descricao = d;
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

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

}
