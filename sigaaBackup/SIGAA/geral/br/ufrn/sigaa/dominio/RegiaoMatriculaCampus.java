/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 09/02/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.dominio;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe respons�vel pela associa��o de Regi�o de Matr�culas com os Campus.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(schema="comum", name = "regiao_matricula_campus")
public class RegiaoMatriculaCampus implements PersistDB {

	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_regiao_matricula_campus", nullable = false)
	private int id;
	
	/** Regi�o de Matr�cula que cont�m os campus. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_regiao_matricula")
	private RegiaoMatricula regiaoMatricula;

	/** Campus associado a regi�o de matr�cula. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_campus")
	private CampusIes campusIes;

	
	/** Default constructor */
	public RegiaoMatriculaCampus() {
		super();
	}

	/**
	 * Minimal Constructor
	 * @param id
	 */
	public RegiaoMatriculaCampus(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegiaoMatricula getRegiaoMatricula() {
		return regiaoMatricula;
	}

	public void setRegiaoMatricula(RegiaoMatricula regiaoMatricula) {
		this.regiaoMatricula = regiaoMatricula;
	}

	public CampusIes getCampusIes() {
		return campusIes;
	}

	public void setCampusIes(CampusIes campusIes) {
		this.campusIes = campusIes;
	}
	
	/** 
	 * Compara as regi�es de matr�cula e campus.
	 */
	public static boolean compateTo(Collection<RegiaoMatriculaCampus> regioesMatriculaCampus, Integer regiaoMatriculaCampus) {
		for (RegiaoMatriculaCampus regiaoCampus : regioesMatriculaCampus) {
			if ( regiaoCampus.getRegiaoMatricula().getId() == regiaoMatriculaCampus ) {
				return true;
			}
		}
		return false;
	}
	
}