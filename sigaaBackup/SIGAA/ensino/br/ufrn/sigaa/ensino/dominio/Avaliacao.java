/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 21/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

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
import br.ufrn.sigaa.ava.dominio.AtividadeAvaliavel;

/**
 * Esta entidade armazena as avalia��es que o docente quiser cadastrar para uma
 * unidade. Esta avalia��o � importante para a visualiza��o dos alunos.
 *
 * @author Gleydson
 *
 */
@Entity
@Table(name="avaliacao_unidade", schema = "ensino")
public class Avaliacao implements PersistDB {

	/** Chave pirm�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_avaliacao")
	private int id;

	/** Nota da avalia��o */
	@Column(name = "nota", unique = false, nullable = true, insertable = true, updatable = true, precision = 4)
	private Double nota;

	/** Peso da avalia��o */
	private Integer peso;
	
	/** Tipo da entidade que gerou a avalia��o - Tarefa ou Question�rio */
	private Integer entidade; // Tarefa - 1; Question�rio - 2;
	
	/** Unidade que a avalia��o est� associada */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade")
	private NotaUnidade unidade;

	/** Descri��o da avalia��o */
	private String denominacao;

	/** Abrevia��o da avalia��o */
	private String abreviacao;
	
	/** Nota M�xima que a avalia��o pode assumir - usada quando a unidade est� configurada como soma da notas */
	@Column(name="nota_maxima")
	private Double notaMaxima;
	
	/** Atividade Avali�vel que gerou a avalia��o - Tarefa ou Question�rio */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_atividade_que_gerou")
	private AtividadeAvaliavel atividadeQueGerou;
	
	public Avaliacao() {
		
	}
	
	public Avaliacao(int id, Double nota, NotaUnidade unidade, String denominacao, String abreviacao) {
		this.id = id;
		this.nota = nota;
		this.unidade = unidade;
		this.denominacao = denominacao;
		this.abreviacao = abreviacao;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public NotaUnidade getUnidade() {
		return unidade;
	}

	public void setUnidade(NotaUnidade unidade) {
		this.unidade = unidade;
	}

	public Double getNota() {
		return nota;
	}

	public void setNota(Double nota) {
		this.nota = nota;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public String getAbreviacao() {
		return abreviacao;
	}

	public void setAbreviacao(String abreviacao) {
		this.abreviacao = abreviacao;
	}

	public Integer getPeso() {
		return peso;
	}

	public void setPeso(Integer peso) {
		this.peso = peso;
	}

	public Double getNotaMaxima() {
		return notaMaxima;
	}

	public void setNotaMaxima(Double notaMaxima) {
		this.notaMaxima = notaMaxima;
	}

	public void setEntidade(Integer entidade) {
		this.entidade = entidade;
	}

	public Integer getEntidade() {
		return entidade;
	}

	public void setAtividadeQueGerou(AtividadeAvaliavel atividadeQueGerou) {
		this.atividadeQueGerou = atividadeQueGerou;
	}

	public AtividadeAvaliavel getAtividadeQueGerou() {
		return atividadeQueGerou;
	}
}