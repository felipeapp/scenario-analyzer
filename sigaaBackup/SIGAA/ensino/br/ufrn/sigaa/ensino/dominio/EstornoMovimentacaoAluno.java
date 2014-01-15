/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 03/08/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

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
import br.ufrn.arq.dominio.RegistroEntrada;

/**
 * Entidade que representa o registro de um estorno realizado em uma movimentação de aluno
 * 
 * @author Andre Dantas
 * 
 */
@Entity
@Table(name = "estorno_movimentacao_aluno", schema = "ensino", uniqueConstraints = {})
public class EstornoMovimentacaoAluno implements PersistDB {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_estorno_movimentacao_aluno")
	private int id;

	@ManyToOne
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada registroEntrada;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_estorno")
	private Date dataEstorno;

	@ManyToOne
	@JoinColumn(name="id_movimentacao_aluno")
	private MovimentacaoAluno movimentacao;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the registroEntrada
	 */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/**
	 * @param registroEntrada
	 *            the registroEntrada to set
	 */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/**
	 * @return the dataEstorno
	 */
	public Date getDataEstorno() {
		return dataEstorno;
	}

	/**
	 * @param dataEstorno
	 *            the dataEstorno to set
	 */
	public void setDataEstorno(Date dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

	/**
	 * @return the movimentacao
	 */
	public MovimentacaoAluno getMovimentacao() {
		return movimentacao;
	}

	/**
	 * @param movimentacao
	 *            the movimentacao to set
	 */
	public void setMovimentacao(MovimentacaoAluno movimentacao) {
		this.movimentacao = movimentacao;
	}

	/**
	 * @param id
	 */
	public EstornoMovimentacaoAluno(int id) {
		super();
		this.id = id;
	}

	public EstornoMovimentacaoAluno() {
		super();
	}

}
