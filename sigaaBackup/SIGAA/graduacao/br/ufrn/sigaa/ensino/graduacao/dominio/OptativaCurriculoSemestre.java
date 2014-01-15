/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 28/11/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que tem como objetivo identificar a carga horária
 * de componentes optativos que um currículo exige que seja
 * paga por semestre.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="optativa_curriculo_semestre", schema="graduacao")
public class OptativaCurriculoSemestre implements PersistDB {

	@Id @GeneratedValue
	private int id;
	
	@ManyToOne @JoinColumn(name="id_curriculo")
	private Curriculo curriculo;
	
	private int semestre;
	
	private int ch;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Curriculo getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public int getCh() {
		return ch;
	}

	public void setCh(int ch) {
		this.ch = ch;
	}
	
}
