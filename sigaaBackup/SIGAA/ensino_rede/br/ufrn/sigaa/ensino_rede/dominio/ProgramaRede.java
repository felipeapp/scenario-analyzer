/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 06/08/2013
 * 
 */package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Programas em rede s�o cursos ofertados em outras universidades
 * 
 * @author Henrique
 *
 */
@Entity
@Table(schema="ensino_rede", name = "programa_rede")
public class ProgramaRede implements PersistDB  {

	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_programa_rede", nullable = false)
	private int id;
	
	/** Descri��o do programa da rede de ensino. */
	@Column(name = "descricao", nullable = false)
	private String descricao;

	/**
	 * Construtor padr�o. 
	 */
	public ProgramaRede() {
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

}
