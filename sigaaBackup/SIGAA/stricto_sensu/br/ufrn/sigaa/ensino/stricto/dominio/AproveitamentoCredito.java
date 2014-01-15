/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Entidade que registra o aproveitamento de credito lan�ado para um discente de stricto
 * @author Victor Hugo
 */
@Entity
@Table(name = "aproveitamento_credito", schema = "stricto_sensu")
public class AproveitamentoCredito implements PersistDB{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_aproveitamento_credito", nullable = false)
	private int id;

	/**
	 * discente de p�s stricto que est� realizando o aproveitamento
	 */
	@ManyToOne()
	@JoinColumn(name = "id_discente")
	private DiscenteStricto discente;

	/**
	 * total de cr�ditos aproveitados
	 */
	private int creditos;

	/**
	 * alguma observa��o que o usu�rio que est� lan�ando o aproveitamento pode colocar
	 * Normalmente fala a origem dos cr�ditos aproveitados
	 */
	private String observacao;

	/**
	 * registro de entrada de quem lan�ou o aproveitamento
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/**
	 * data da realiza��o do aproveitamento
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/**
	 * define se este aproveitamento est� ativo ou n�o.
	 * apenas aproveitamentos de cr�ditos ativos ser�o considerados no hist�rico
	 */
	@Column(name = "ativo")
	private boolean ativo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteStricto getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	public int getCreditos() {
		return creditos;
	}

	public void setCreditos(int creditos) {
		this.creditos = creditos;
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

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/*public static void main(String[] args) {
		System.out.println( HibernateUtils.generateDDL( AproveitamentoCredito.class ) );
	}*/

}
