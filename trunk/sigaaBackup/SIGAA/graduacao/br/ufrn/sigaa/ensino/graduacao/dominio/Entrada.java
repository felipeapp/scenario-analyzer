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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.TipoEntrada;

/**
 * Entity class Entrada
 *
 * @author Gleydson
 */
@Entity
@Table(name = "entrada", schema="graduacao")
public class Entrada implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_entrada", nullable = false)
	private int id;

	@Column(name = "ano")
	private Integer ano;

	@Column(name = "periodo")
	private Integer periodo;

	@Column(name = "id_tipo_entrada")
	private TipoEntrada tipoEntrada;

	@JoinColumn(name = "id_discente_graduacao")
	@ManyToOne
	private DiscenteGraduacao discente;

	/** Creates a new instance of Entrada */
	public Entrada() {
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public TipoEntrada getTipoEntrada() {
		return tipoEntrada;
	}

	public void setTipoEntrada(TipoEntrada tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}

	public ListaMensagens validate() {
		return null;
	}

}
