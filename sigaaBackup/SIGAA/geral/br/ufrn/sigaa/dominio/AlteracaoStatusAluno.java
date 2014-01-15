/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '01/12/2006'
 *
 */
package br.ufrn.sigaa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que registra as altera��es que o aluno sofre de status.
 * @author Gleydson
 */
@Entity
@Table(schema="comum", name = "alteracao_status_aluno")
public class AlteracaoStatusAluno implements PersistDB {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_alteracao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
	 * status do aluno original, antes da altera��o
	 */
	private int status;

	/**
	 * usu�rio que realizou a altera��o
	 */
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	/**
	 * discente q teve o status alterado
	 */
	@ManyToOne
	@JoinColumn(name = "id_discente")
	private Discente discente;

	/**
	 * data da altera��o
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	/** c�digo do movimento que est� alterando o status do aluno */
	private int movimento;

	/**
	 * ano do calend�rio da altera��o
	 */
	private Integer ano;

	/**
	 * per�odo do calend�rio da altera��o
	 */
	private Integer periodo;

	/**
	 * utilizando quando o status eh alterado diretamente pela administra��o do DAE
	 */
	private String observacao;

	/**
	 * @return the ano
	 */
	public Integer getAno() {
		return ano;
	}

	/**
	 * @param ano the ano to set
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/**
	 * @return the periodo
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/**
	 * @param periodo the periodo to set
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMovimento() {
		return movimento;
	}

	public void setMovimento(int movimento) {
		this.movimento = movimento;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
