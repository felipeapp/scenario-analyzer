/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/11/2008 
 *
 */
package br.ufrn.sigaa.portal.dominio;

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
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Informação do aluno a serem exibidas no
 * portal discente
 *
 * @author David Ricardo
 *
 */
@Entity
@Table(name="perfil_discente", schema="ava")
public class PerfilDiscente implements Validatable {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_discente")
	private Discente discente;

	private String descricao;

	@Column(name="areas_interesse")
	private String areasInteresse;

	/**
	 * @return the aluno
	 */
	public Discente getDiscente() {
		return discente;
	}

	/**
	 * @param aluno the aluno to set
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the areasInteresse
	 */
	public String getAreasInteresse() {
		return areasInteresse;
	}

	/**
	 * @param areasInteresse the areasInteresse to set
	 */
	public void setAreasInteresse(String areasInteresse) {
		this.areasInteresse = areasInteresse;
	}

	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}



}
