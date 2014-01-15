/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe que representa a região física, que contempla os campus para matrícula.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(schema="comum", name = "regiao_matricula", uniqueConstraints = {})
public class RegiaoMatricula implements Validatable{

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_regiao_matricula", nullable = false)
	private int id;
	
	/** Nome da região de matrículas */
	private String nome;
	
	/** Nível do curso (Graduação - G, Mestrado E, etc */
	private char nivel; 
	
	/** Campus que a região possui */
	@OneToMany(mappedBy = "regiaoMatricula", fetch=FetchType.LAZY)
	private Collection<RegiaoMatriculaCampus> regioesMatriculaCampus;

	
	/** Default constructor */
	public RegiaoMatricula() {
		super();
	}
	
	@Transient
	public String getNivelDesc() {
		return NivelEnsino.getDescricao(nivel);
	}
	
	/**
	 * Minimal Constructor
	 * @param id
	 */
	public RegiaoMatricula(int id) {
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

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public Collection<RegiaoMatriculaCampus> getRegioesMatriculaCampus() {
		return regioesMatriculaCampus;
	}

	public void setRegioesMatriculaCampus(
			Collection<RegiaoMatriculaCampus> regioesMatriculaCampus) {
		this.regioesMatriculaCampus = regioesMatriculaCampus;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(nome, "Nome", erros);
		ValidatorUtil.validateRequired(nivel, "Nível de Ensino", erros);
		return erros;
	}
	
}