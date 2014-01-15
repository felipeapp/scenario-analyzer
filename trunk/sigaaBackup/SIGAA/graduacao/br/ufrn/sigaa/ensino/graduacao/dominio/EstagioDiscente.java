/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 *	Created on 10/01/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entity class EstagioDiscente
 *
 * @author Gleydson
 */
@Deprecated
@Entity
@Table(name = "estagio_discente", schema="graduacao")
public class EstagioDiscente implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_estagio_discente", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_coordenador")
	private Servidor coordenador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_orientador")
	private Servidor orientador;

	@Column (name="supervisor_campo")
	private String supervisorCampo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricular componenteCurricular;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente;

	/** Creates a new instance of EstagioDiscente */
	public EstagioDiscente() {
	}

	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}

	public void setComponenteCurricular(
			ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	public Servidor getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
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

	public Servidor getOrientador() {
		return orientador;
	}

	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	public String getSupervisorCampo() {
		return supervisorCampo;
	}

	public void setSupervisorCampo(String supervisorCampo) {
		this.supervisorCampo = supervisorCampo;
	}

	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

}
