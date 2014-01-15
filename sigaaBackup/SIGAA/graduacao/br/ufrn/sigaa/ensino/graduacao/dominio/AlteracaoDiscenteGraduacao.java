/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

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
 *
 * @author Gleydson
 */
@Entity
@Table(name = "alteracao_discente_graduacao", schema = "graduacao")
@PrimaryKeyJoinColumn(name = "id_discente_graduacao")
public class AlteracaoDiscenteGraduacao implements PersistDB {


	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_alteracao_discente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente;

	// total CH
	@Column(name = "ch_total_integralizada")
	private Short chTotalIntegralizada;
	
	@Column(name = "ch_integralizada_aproveitamentos")
	private Short chIntegralizadaAproveitamentos;

	@Column(name = "ch_total_pendente")
	private Short chTotalPendente;

	@Column(name = "ch_optativa_integralizada")
	private Short chOptativaIntegralizada;

	@Column(name = "ch_optativa_pendente")
	private Short chOptativaPendente;

	@Column(name = "ch_nao_atividade_obrig_integ")
	private Short chNaoAtividadeObrigInteg;

	@Column(name = "ch_nao_atividade_obrig_pendente")
	private Short chNaoAtividadeObrigPendente;

	@Column(name = "ch_atividade_obrig_integ")
	private Short chAtividadeObrigInteg;

	@Column(name = "ch_atividade_obrig_pendente")
	private Short chAtividadeObrigPendente;

	@Column(name = "ch_aula_integralizada")
	private Short chAulaIntegralizada;

	@Column(name = "ch_aula_pendente")
	private Short chAulaPendente;

	@Column(name = "ch_lab_integralizada")
	private Short chLabIntegralizada;

	@Column(name = "ch_lab_pendente")
	private Short chLabPendente;

	@Column(name = "ch_estagio_integralizada")
	private Short chEstagioIntegralizada;

	@Column(name = "ch_estagio_pendente")
	private Short chEstagioPendente;

	// TOTAIS DE CRÉDITOS
	@Column(name = "cr_total_integralizados")
	private Short crTotalIntegralizados;

	@Column(name = "cr_total_pendentes")
	private Short crTotalPendentes;

	@Column(name = "cr_extra_integralizados")
	private Short crExtraIntegralizados;

	@Column(name = "cr_nao_atividade_obrig_integralizado")
	private Short crNaoAtividadeObrigInteg;

	@Column(name = "cr_nao_atividade_obrig_pendente")
	private Short crNaoAtividadeObrigPendente;

	@Column(name = "cr_lab_integralizado")
	private Short crLabIntegralizado;

	@Column(name = "cr_lab_pendente")
	private Short crLabPendente;

	@Column(name = "cr_estagio_integralizado")
	private Short crEstagioIntegralizado;

	@Column(name = "cr_estagio_pendente")
	private Short crEstagioPendente;

	@Column(name = "cr_aula_integralizado")
	private Short crAulaIntegralizado;

	@Column(name = "cr_aula_pendente")
	private Short crAulaPendente;

	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	private int operacao;

	public AlteracaoDiscenteGraduacao() {
	}

	public Short getChIntegralizadaAproveitamentos() {
		return chIntegralizadaAproveitamentos;
	}

	public void setChIntegralizadaAproveitamentos(Short chIntegralizadaAproveitamentos) {
		this.chIntegralizadaAproveitamentos = chIntegralizadaAproveitamentos;
	}

	public Short getChAtividadeObrigInteg() {
		return chAtividadeObrigInteg;
	}

	public void setChAtividadeObrigInteg(Short chAtividadeObrigInteg) {
		this.chAtividadeObrigInteg = chAtividadeObrigInteg;
	}

	public Short getChAtividadeObrigPendente() {
		return chAtividadeObrigPendente;
	}

	public void setChAtividadeObrigPendente(Short chAtividadeObrigPendente) {
		this.chAtividadeObrigPendente = chAtividadeObrigPendente;
	}

	public Short getChAulaIntegralizada() {
		return chAulaIntegralizada;
	}

	public void setChAulaIntegralizada(Short chAulaIntegralizada) {
		this.chAulaIntegralizada = chAulaIntegralizada;
	}

	public Short getChAulaPendente() {
		return chAulaPendente;
	}

	public void setChAulaPendente(Short chAulaPendente) {
		this.chAulaPendente = chAulaPendente;
	}

	public Short getChEstagioIntegralizada() {
		return chEstagioIntegralizada;
	}

	public void setChEstagioIntegralizada(Short chEstagioIntegralizada) {
		this.chEstagioIntegralizada = chEstagioIntegralizada;
	}

	public Short getChEstagioPendente() {
		return chEstagioPendente;
	}

	public void setChEstagioPendente(Short chEstagioPendente) {
		this.chEstagioPendente = chEstagioPendente;
	}

	public Short getChLabIntegralizada() {
		return chLabIntegralizada;
	}

	public void setChLabIntegralizada(Short chLabIntegralizada) {
		this.chLabIntegralizada = chLabIntegralizada;
	}

	public Short getChLabPendente() {
		return chLabPendente;
	}

	public void setChLabPendente(Short chLabPendente) {
		this.chLabPendente = chLabPendente;
	}

	public Short getChNaoAtividadeObrigInteg() {
		return chNaoAtividadeObrigInteg;
	}

	public void setChNaoAtividadeObrigInteg(Short chNaoAtividadeObrigInteg) {
		this.chNaoAtividadeObrigInteg = chNaoAtividadeObrigInteg;
	}

	public Short getChNaoAtividadeObrigPendente() {
		return chNaoAtividadeObrigPendente;
	}

	public void setChNaoAtividadeObrigPendente(Short chNaoAtividadeObrigPendente) {
		this.chNaoAtividadeObrigPendente = chNaoAtividadeObrigPendente;
	}

	public Short getChOptativaIntegralizada() {
		return chOptativaIntegralizada;
	}

	public void setChOptativaIntegralizada(Short chOptativaIntegralizada) {
		this.chOptativaIntegralizada = chOptativaIntegralizada;
	}

	public Short getChOptativaPendente() {
		return chOptativaPendente;
	}

	public void setChOptativaPendente(Short chOptativaPendente) {
		this.chOptativaPendente = chOptativaPendente;
	}

	public Short getChTotalIntegralizada() {
		return chTotalIntegralizada;
	}

	public void setChTotalIntegralizada(Short chTotalIntegralizada) {
		this.chTotalIntegralizada = chTotalIntegralizada;
	}

	public Short getChTotalPendente() {
		return chTotalPendente;
	}

	public void setChTotalPendente(Short chTotalPendente) {
		this.chTotalPendente = chTotalPendente;
	}

	public Short getCrAulaIntegralizado() {
		return crAulaIntegralizado;
	}

	public void setCrAulaIntegralizado(Short crAulaIntegralizado) {
		this.crAulaIntegralizado = crAulaIntegralizado;
	}

	public Short getCrAulaPendente() {
		return crAulaPendente;
	}

	public void setCrAulaPendente(Short crAulaPendente) {
		this.crAulaPendente = crAulaPendente;
	}

	public Short getCrEstagioIntegralizado() {
		return crEstagioIntegralizado;
	}

	public void setCrEstagioIntegralizado(Short crEstagioIntegralizado) {
		this.crEstagioIntegralizado = crEstagioIntegralizado;
	}

	public Short getCrEstagioPendente() {
		return crEstagioPendente;
	}

	public void setCrEstagioPendente(Short crEstagioPendente) {
		this.crEstagioPendente = crEstagioPendente;
	}

	public Short getCrExtraIntegralizados() {
		return crExtraIntegralizados;
	}

	public void setCrExtraIntegralizados(Short crExtraIntegralizados) {
		this.crExtraIntegralizados = crExtraIntegralizados;
	}

	public Short getCrLabIntegralizado() {
		return crLabIntegralizado;
	}

	public void setCrLabIntegralizado(Short crLabIntegralizado) {
		this.crLabIntegralizado = crLabIntegralizado;
	}

	public Short getCrLabPendente() {
		return crLabPendente;
	}

	public void setCrLabPendente(Short crLabPendente) {
		this.crLabPendente = crLabPendente;
	}

	public Short getCrNaoAtividadeObrigInteg() {
		return crNaoAtividadeObrigInteg;
	}

	public void setCrNaoAtividadeObrigInteg(Short crNaoAtividadeObrigInteg) {
		this.crNaoAtividadeObrigInteg = crNaoAtividadeObrigInteg;
	}

	public Short getCrNaoAtividadeObrigPendente() {
		return crNaoAtividadeObrigPendente;
	}

	public void setCrNaoAtividadeObrigPendente(Short crNaoAtividadeObrigPendente) {
		this.crNaoAtividadeObrigPendente = crNaoAtividadeObrigPendente;
	}

	public Short getCrTotalIntegralizados() {
		return crTotalIntegralizados;
	}

	public void setCrTotalIntegralizados(Short crTotalIntegralizados) {
		this.crTotalIntegralizados = crTotalIntegralizados;
	}

	public Short getCrTotalPendentes() {
		return crTotalPendentes;
	}

	public void setCrTotalPendentes(Short crTotalPendentes) {
		this.crTotalPendentes = crTotalPendentes;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

}
