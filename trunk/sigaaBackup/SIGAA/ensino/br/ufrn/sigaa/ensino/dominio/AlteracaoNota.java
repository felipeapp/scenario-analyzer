/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 05/04/2011
*/
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Registro de alteração na NotaUnidade de uma MatriculaComponente
 * 
 * @author Igor Linnik
 * 
 */
@Entity
@Table(name = "alteracao_nota", schema = "ensino", uniqueConstraints = {})
public class AlteracaoNota implements PersistDB {

	// Fields

	/**
	 * Chave primária
	 */
	private int id;

	/**
	 *  Usuário que fez a alteração
	 */
	private Usuario usuario;
	/**
	 * NotaUnidade a qual o registro de alteração se refere
	 */
	private NotaUnidade notaUnidade;

	/**
	 * Nota Antiga
	 */
	private Double notaAntiga;

	/**
	 * Nova Nota
	 */
	private Double notaNova;	

	/**
	 * Código da operação que provocou a alteração na nota
	 */
	private Integer codMovimento;

	/**
	 * Data em que a alteração foi realizada.
	 */
	private Date dataAlteracao;	

	// Constructors

	/** default constructor */
	public AlteracaoNota() {
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="ensino.alteracao_nota_seq") })
	@Column(name = "id_alteracao_nota", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idAlteracaoNota) {
		this.id = idAlteracaoNota;
	}

	@ManyToOne (cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario")
	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario idUsuario) {
		this.usuario = idUsuario;
	}

	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn(name = "id_nota_unidade")
	public NotaUnidade getNotaUnidade() {
		return notaUnidade;
	}

	public void setNotaUnidade(NotaUnidade notaUnidade) {
		this.notaUnidade = notaUnidade;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataAlteracao() {
		return this.dataAlteracao;
	}

	@Column(name = "nota_antiga")
	public Double getNotaAntiga() {
		return notaAntiga;
	}

	public void setNotaAntiga(Double notaAntiga) {
		this.notaAntiga = notaAntiga;
	}

	@Column(name = "nota_nova")
	public Double getNotaNova() {
		return notaNova;
	}

	public void setNotaNova(Double notaNova) {
		this.notaNova = notaNova;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	

	public Integer getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(Integer codMovimento) {
		this.codMovimento = codMovimento;
	}
}