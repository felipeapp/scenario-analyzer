/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 06/08/2013
 * 
 */
package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Curso do ensino em rede o qual o discente está cursando.
 * 
 * @author Henrique André
 *
 */
@Entity
@Table(schema="ensino_rede", name = "curso_associado")
public class CursoAssociado implements PersistDB  {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_curso_associado", nullable = false)
	private int id;

	/** Nome do curso. */
	@Column(nullable = false)
	private String nome;

	/** Nivel de ensino do curso. */
	@Column(nullable = false)
	private char nivel;
	
	/** Situação do curso */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_curso_associado", nullable=false)
	private SituacaoCursoAssociado situacao;
	
	/** Situação do curso */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_programa_rede", nullable=false)
	private ProgramaRede programa;
	
	/** Construtor padrão. */
	public CursoAssociado() {
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

	public SituacaoCursoAssociado getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoCursoAssociado situacao) {
		this.situacao = situacao;
	}

	public ProgramaRede getPrograma() {
		return programa;
	}

	public void setPrograma(ProgramaRede programa) {
		this.programa = programa;
	}

}
