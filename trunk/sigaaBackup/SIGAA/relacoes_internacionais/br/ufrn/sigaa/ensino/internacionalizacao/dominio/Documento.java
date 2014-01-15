/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 30/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Objeto responsável pela manutenção dos documentos da instituição a serem traduzidos no sistema.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(schema = "internacionalizacao", name = "documento")
public class Documento implements Validatable{

	/** Identificador */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_documento", unique = true, nullable = false)
	private int id;
	
	/** Nome legível da Entidade do atributo traduzido.*/
	@Column
	private String nome;
	
	/** Indica se a entidade do elemento para tradução encontra-se ativa ou não. */
	@CampoAtivo(true)
	private Boolean ativo = true;

	
	/**
	 * Construtor
	 */
	public Documento() {
		super();
	}

	/**
	 * Construtor
	 * @param id
	 */
	public Documento(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
}
