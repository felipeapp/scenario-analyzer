/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 06/03/2008
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;

/**
 * Classe para registrar alteração nos cálculos dos alunos 
 * de pós-graduação.
 * @author David Pereira
 */ 
@Entity
@Table(name = "alteracao_discente_stricto", schema = "stricto_sensu")
@PrimaryKeyJoinColumn(name = "id_discente_stricto")
public class AlteracaoDiscenteStricto implements PersistDB {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_alteracao_discente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
	 * discente que teve os cálculos alterados
	 */
	@ManyToOne
	@JoinColumn(name = "id_discente")
	private DiscenteStricto discente;
	
	/**
	 * data que foi realizada a alteração
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	/**
	 * registro entrada do usuário que realizou 
	 */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	/**
	 * código do movimento que disparou a atualização dos cálculos
	 */
	private int operacao;

	/**
	 * guarda os créditos totais que foram integralizados nesta atualização dos cálculos
	 */
	private Short crTotaisIntegralizados;

	/**
	 * guarda os créditos totais obrigatórios desta atualização dos cálculos
	 */
	private Short crTotaisObrigatorios;
	
	/** Total de CH integralizada para esse aluno. */
	@Column(name = "ch_total_integralizada")
	private Short chTotalIntegralizada;
	
	/** CH obrigatória integralizada para esse aluno. */
	@Column(name = "ch_obrigatoria_integralizada")
	private Short chObrigatoriaIntegralizada;
	
	/** CH optativa integralizada para esse aluno. */
	@Column(name = "ch_optativa_integralizada")
	private Short chOptativaIntegralizada;
	

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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public Short getCrTotaisIntegralizados() {
		return crTotaisIntegralizados;
	}

	public void setCrTotaisIntegralizados(Short crTotaisIntegralizados) {
		this.crTotaisIntegralizados = crTotaisIntegralizados;
	}

	public Short getCrTotaisObrigatorios() {
		return crTotaisObrigatorios;
	}

	public void setCrTotaisObrigatorios(Short crTotaisObrigatorios) {
		this.crTotaisObrigatorios = crTotaisObrigatorios;
	}

	public Short getChTotalIntegralizada() {
		return chTotalIntegralizada;
	}

	public void setChTotalIntegralizada(Short chTotalIntegralizada) {
		this.chTotalIntegralizada = chTotalIntegralizada;
	}

	public Short getChObrigatoriaIntegralizada() {
		return chObrigatoriaIntegralizada;
	}

	public void setChObrigatoriaIntegralizada(Short chObrigatoriaIntegralizada) {
		this.chObrigatoriaIntegralizada = chObrigatoriaIntegralizada;
	}

	public Short getChOptativaIntegralizada() {
		return chOptativaIntegralizada;
	}

	public void setChOptativaIntegralizada(Short chOptativaIntegralizada) {
		this.chOptativaIntegralizada = chOptativaIntegralizada;
	}
	
}
