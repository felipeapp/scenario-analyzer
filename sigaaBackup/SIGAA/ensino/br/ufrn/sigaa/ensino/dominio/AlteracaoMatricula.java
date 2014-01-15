/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
 * Created on 17/10/2006
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
 * Registro de altera��o na situa��o de uma matr�cula em componente curricular
 * 
 * @author Ricardo Wendell
 * 
 */
@Entity
@Table(name = "alteracao_matricula", schema = "ensino", uniqueConstraints = {})
public class AlteracaoMatricula implements PersistDB {

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
	private MatriculaComponente matricula;

	/**
	 * Situa��o na qual a matr�cula se encontrava antes da altera��o
	 */
	private SituacaoMatricula situacaoAntiga;

	/**
	 * Situa��o para a qual a matr�cula foi alterada
	 */
	private SituacaoMatricula situacaoNova;

	/**
	 * Tipo de integraliza��o que possu�a antes da altera��o.
	 */
	private String tipoIntegralizacaoAntigo;

	/**
	 * Tipo de integraliza��o que possui ap�s a altera��o (quando modificada).
	 */
	private String tipoIntegralizacaoNovo;

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

	/**
	 * Express�o de equival�ncia caso a altera��o tenha provocado uma 
	 * mudan�a no tipo de integraliza��o. 
	 */
	private String expressao;

	// Constructors

	/** default constructor */
	public AlteracaoMatricula() {
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="ensino.alteracao_matricula_seq") })
	@Column(name = "id_alteracao_matricula", unique = true, nullable = false, insertable = true, updatable = true)
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
	@JoinColumn(name = "id_matricula_disciplina")
	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
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
	@JoinColumn(name = "id_situacao_antiga")
	public SituacaoMatricula getSituacaoAntiga() {
		return situacaoAntiga;
	}

	public void setSituacaoAntiga(SituacaoMatricula situacaoAntiga) {
		this.situacaoAntiga = situacaoAntiga;
	}

	@ManyToOne (cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_nova")
	public SituacaoMatricula getSituacaoNova() {
		return situacaoNova;
	}

	public void setSituacaoNova(SituacaoMatricula situacaoNova) {
		this.situacaoNova = situacaoNova;
	}

	public Integer getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(Integer codMovimento) {
		this.codMovimento = codMovimento;
	}

	/**
	 * @return the tipoIntegralizacaoAntigo
	 */
	public String getTipoIntegralizacaoAntigo() {
		return tipoIntegralizacaoAntigo;
	}

	/**
	 * @param tipoIntegralizacaoAntigo the tipoIntegralizacaoAntigo to set
	 */
	public void setTipoIntegralizacaoAntigo(String tipoIntegralizacaoAntigo) {
		this.tipoIntegralizacaoAntigo = tipoIntegralizacaoAntigo;
	}

	/**
	 * @return the tipoIntegralizacaoNovo
	 */
	public String getTipoIntegralizacaoNovo() {
		return tipoIntegralizacaoNovo;
	}

	/**
	 * @param tipoIntegralizacaoNovo the tipoIntegralizacaoNovo to set
	 */
	public void setTipoIntegralizacaoNovo(String tipoIntegralizacaoNovo) {
		this.tipoIntegralizacaoNovo = tipoIntegralizacaoNovo;
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

	/**
	 * @return the expressao
	 */
	public String getExpressao() {
		return expressao;
	}

	/**
	 * @param expressao the express�o to set
	 */
	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}

}