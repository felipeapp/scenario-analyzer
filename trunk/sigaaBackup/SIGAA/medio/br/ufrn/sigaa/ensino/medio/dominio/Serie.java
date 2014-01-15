/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 24/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Entidade referentes as s�ries relacionadas ao ensino m�dio. 
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "serie", schema = "medio", uniqueConstraints = {})
public class Serie implements Validatable{

	/** Chave Prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_serie", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Atributo da descri��o da s�rie de ensino m�dio.*/
	@Column(name = "descricao")
	private String descricao;
	
	/** Atributo referente a ao ordinal da S�rie. Ex: 1, 2 e 3.*/
	@Column(name = "numero", nullable = false)
	private Integer numero;
	
	/** Objeto de relacionamento com a unidade gestora da Serie. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_gestora_academica", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade gestoraAcademica;
	
	/** Objeto de relacionamento com o curso da Serie. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	private CursoMedio cursoMedio;
	
	/** Indica se a s�rie est� ativa, permitindo sua utiliza��o no SIGAA. */
	@Column(name = "ativo", nullable = false)
	private boolean ativo = true;
	
	/** Define os dados  de entrada do usu�rio no sistema para inser��o da s�rie. */
	@CriadoPor
	@ManyToOne (cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	/** Define a data em que a s�rie foi cadastrada. */
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Turmas da s�rie. */
	@OneToMany(mappedBy = "serie", fetch = FetchType.LAZY)
	private List<TurmaSerie> turmas = new ArrayList<TurmaSerie>();	
	
	
	// Constructors

	/** default constructor */
	public Serie() {
	}
	
	/** default minimal constructor 
	 * @param id
	 */
	public Serie(int id) {
		super();
		this.id = id;
	}

	/**
	 * minimal constructor
	 * @param id
	 * @param descricao
	 * @param numero
	 */
	public Serie(int id, String descricao, Integer numero) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.numero = numero;
	}

	@Transient
	public String getNumeroSerieOrdinal() {
		return this.numero + "�";
	}

	@Transient
	public String getNomeCurso(){
		return this.cursoMedio.getNome();
	}
	
	
	// Getters and Setters
	
	/** Recupera a chave prim�ria
	/** @return the id */
	public int getId() {
		return id;
	}

	/** Seta a Chave Primaria
	/** @param id the id to set */
	public void setId(int id) {
		this.id = id;
	}

	/** recupera o Atributo da descri��o da s�rie de ensino m�dio.
	/** @return the descricao */
	public String getDescricao() {
		return descricao;
	}

	/** Seta o Atributo da descri��o da s�rie de ensino m�dio.
	/** @param descricao the descricao to set */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Recupera o Atributo referente a ao ordinal da S�rie. Ex: 1, 2 e 3.
	/** @return the numero */
	public Integer getNumero() {
		return numero;
	}

	/** Seta o Atributo referente a ao ordinal da S�rie. Ex: 1, 2 e 3.
	/** @param numero the numero to set */
	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	/** recupera o Objeto de relacionamento com a unidade gestora da Serie.
	/** @return the gestoraAcademica */
	public Unidade getGestoraAcademica() {
		return gestoraAcademica;
	}

	/** Setar o Objeto de relacionamento com a unidade gestora da Serie.
	/** @param gestoraAcademica the gestoraAcademica to set */
	public void setGestoraAcademica(Unidade gestoraAcademica) {
		this.gestoraAcademica = gestoraAcademica;
	}

	/** recupera o Objeto de relacionamento com o curso da Serie.
	/** @return the curso */
	public CursoMedio getCursoMedio() {
		return cursoMedio;
	}

	/** seta o Objeto de relacionamento com o curso da Serie.
	/** @param curso the curso to set */
	public void setCursoMedio(CursoMedio cursoMedio) {
		this.cursoMedio = cursoMedio;
	}

	/** indica se est� ativo ou n�o  
	 * @return the ativo */
	public boolean isAtivo() {
		return ativo;
	}

	/** seta o ativo 
	 * @param ativo the ativo to set */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna a descri��o da situa��o do objeto.*/
	@Transient
	public String getAtivoToString(){
		return isAtivo() ? "ATIVO" : "INATIVO";
	}
	
	/** Retorna a descri��o completa da S�rie, contemplando o n�mero ordinal e a descri��o.*/
	@Transient
	public String getDescricaoCompleta() {
		StringBuffer descricao = new StringBuffer();
		descricao.append(this.numero + "� " + this.descricao);
		return descricao.toString();
	}	
	
	/**
	 * Valida os atributos
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validaInt(numero, "N�mero", lista);
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		ValidatorUtil.validateRequired(cursoMedio, "Curso", lista);
		return lista;
	}

	public List<TurmaSerie> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<TurmaSerie> turmas) {
		this.turmas = turmas;
	}
}
