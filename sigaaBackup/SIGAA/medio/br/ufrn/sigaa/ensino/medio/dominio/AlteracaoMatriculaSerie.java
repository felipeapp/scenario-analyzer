/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
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
 * Registro de altera��o na situa��o de uma matr�cula de discente em s�rie.
 * 
 * @author Rafael Gomes
 * 
 */
@Entity
@Table(name = "alteracao_matricula_serie", schema = "medio", uniqueConstraints = {})
public class AlteracaoMatriculaSerie implements PersistDB {

	// Fields

	/**
	 * Chave prim�ria
	 */
	private int id;

	/**
	 *  Usu�rio que fez a altera��o
	 */
	private Usuario usuario;

	/**
	 * Matr�cula a qual o registro de altera��o se refere
	 */
	private MatriculaDiscenteSerie matriculaSerie;

	/**
	 * Situa��o na qual a matr�cula se encontrava antes da altera��o
	 */
	private SituacaoMatriculaSerie situacaoAntiga;

	/**
	 * Situa��o para a qual a matr�cula foi alterada
	 */
	private SituacaoMatriculaSerie situacaoNova;

	/**
	 * C�digo da opera��o que provocou a altera��o na situa��o da matr�cula
	 */
	private Integer codMovimento;

	/**
	 * no caso da altera��o ter sido realizada a partir de uma movimenta��o do aluno
	 * (por exemplo, trancamento e cancelamento de programa)
	 */
	private MovimentacaoAluno movimentacaoAluno;

	/**
	 * Data em que a altera��o foi realizada.
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