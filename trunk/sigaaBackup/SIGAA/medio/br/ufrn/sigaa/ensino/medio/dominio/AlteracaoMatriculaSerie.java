/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 10/11/2011
*/
package br.ufrn.sigaa.ensino.medio.dominio;

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
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;

/**
 * Registro de alteração na situação de uma matrícula de discente em série.
 * 
 * @author Rafael Gomes
 * 
 */
@Entity
@Table(name = "alteracao_matricula_serie", schema = "medio", uniqueConstraints = {})
public class AlteracaoMatriculaSerie implements PersistDB {

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
	 * Matrícula a qual o registro de alteração se refere
	 */
	private MatriculaDiscenteSerie matriculaSerie;

	/**
	 * Situação na qual a matrícula se encontrava antes da alteração
	 */
	private SituacaoMatriculaSerie situacaoAntiga;

	/**
	 * Situação para a qual a matrícula foi alterada
	 */
	private SituacaoMatriculaSerie situacaoNova;

	/**
	 * Código da operação que provocou a alteração na situação da matrícula
	 */
	private Integer codMovimento;

	/**
	 * no caso da alteração ter sido realizada a partir de uma movimentação do aluno
	 * (por exemplo, trancamento e cancelamento de programa)
	 */
	private MovimentacaoAluno movimentacaoAluno;

	/**
	 * Data em que a alteração foi realizada.
	 */
	private Date dataAlteracao;

	// Constructors

	/** default constructor */
	public AlteracaoMatriculaSerie() {
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="medio.alteracao_matricula_serie_seq") })
	@Column(name = "id_alteracao_matricula_serie", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idAlteracaoMatricula) {
		this.id = idAlteracaoMatricula;
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
	@JoinColumn(name = "id_matricula_discente_serie")
	public MatriculaDiscenteSerie getMatriculaSerie() {
		return matriculaSerie;
	}

	public void setMatriculaSerie(MatriculaDiscenteSerie matriculaSerie) {
		this.matriculaSerie = matriculaSerie;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataAlteracao() {
		return this.dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	@ManyToOne (cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_matricula_serie_antiga")
	public SituacaoMatriculaSerie getSituacaoAntiga() {
		return situacaoAntiga;
	}

	public void setSituacaoAntiga(SituacaoMatriculaSerie situacaoAntiga) {
		this.situacaoAntiga = situacaoAntiga;
	}

	@ManyToOne (cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_matricula_serie_nova")
	public SituacaoMatriculaSerie getSituacaoNova() {
		return situacaoNova;
	}

	public void setSituacaoNova(SituacaoMatriculaSerie situacaoNova) {
		this.situacaoNova = situacaoNova;
	}

	public Integer getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(Integer codMovimento) {
		this.codMovimento = codMovimento;
	}

	/**
	 * @return the movimentacaoAluno
	 */
	@ManyToOne
	@JoinColumn(name="id_movimentacao_aluno")
	public MovimentacaoAluno getMovimentacaoAluno() {
		return movimentacaoAluno;
	}

	/**
	 * @param movimentacaoAluno the movimentacaoAluno to set
	 */
	public void setMovimentacaoAluno(MovimentacaoAluno movimentacaoAluno) {
		this.movimentacaoAluno = movimentacaoAluno;
	}

}