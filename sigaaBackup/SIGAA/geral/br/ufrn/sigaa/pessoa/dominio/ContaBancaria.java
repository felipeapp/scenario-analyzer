/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '21/03/2007'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe que mapeia uma conta bancária no sistema
 * @author Ricardo Wendell
 */
@Entity
@Table(schema="comum", name = "conta_bancaria")
public class ContaBancaria implements PersistDB {

	private int id;

	/** número da conta bancária */
	private String numero;

	/** número da agencia do banco */
	private String agencia;

	/** número da operação da conta bancária */
	private String operacao;
	
	/** banco */
	private Banco banco;
	
	/** varação da conta */
	private String variacao;

	public ContaBancaria() {
	    banco = new Banco();
	}

	@Column(name = "agencia", unique = false, nullable = false, insertable = true, updatable = true)
	public String getAgencia() {
		return agencia;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_banco", unique = false, nullable = false, insertable = true, updatable = true)
	public Banco getBanco() {
		return banco;
	}

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_conta_bancaria", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	@Column(name = "numero", unique = false, nullable = false, insertable = true, updatable = true)
	public String getNumero() {
		return numero;
	}

	@Column(name = "variacao", unique = false, nullable = true, insertable = true, updatable = true)
	public String getVariacao() {
		return variacao;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setVariacao(String variacao) {
		this.variacao = variacao;
	}

	@Transient
	public boolean isEmpty() {
		return ValidatorUtil.isEmpty(agencia) && ValidatorUtil.isEmpty(numero) && ValidatorUtil.isEmpty(variacao) &&
				(banco == null || banco.getId() <= 0);
		
	}

	@Column(name = "operacao")
	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

}
