/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/08/2007
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * Entidade que mantém os períodos importantes para o módulo de monitoria
 * Através do calendário de monitoria os membros da PROGRAD configuram o SIGAA
 * para o recebimento de relatórios parciais, finais e resumos do seminário de
 * iniciação à docência (SID).
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "calendario_monitoria", schema = "monitoria")
public class CalendarioMonitoria implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_calendario_monitoria", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@Column(name = "ano_referencia")
	private int anoReferencia;

	private boolean ativo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;

	/** Envio de relatórios parciais do projeto */
	@Column(name = "inicio_envio_relatorio_parcial_projeto")
	private Date inicioEnvioRelatorioParcialProjeto;

	@Column(name = "fim_envio_relatorio_parcial_projeto")
	private Date fimEnvioRelatorioParcialProjeto;

	@Column(name = "ano_projeto_relatorio_parcial")
	private int anoProjetoRelatorioParcial;

	/** Submissão de relatórios finais do projeto */
	@Column(name = "inicio_envio_relatorio_final_projeto")
	private Date inicioEnvioRelatorioFinalProjeto;

	@Column(name = "fim_envio_relatorio_final_projeto")
	private Date fimEnvioRelatorioFinalProjeto;

	@Column(name = "ano_projeto_relatorio_final")
	private int anoProjetoRelatorioFinal;

	/** Submissão de relatórios do resumo sid */
	@Column(name = "inicio_envio_resumo_sid")
	private Date inicioEnvioResumoSid;

	@Column(name = "fim_envio_resumo_sid")
	private Date fimEnvioResumoSid;

	@Column(name = "fim_edicao_resumo_sid")
	private Date fimEdicaoResumoSid;

	@Column(name = "ano_projeto_resumo_sid")
	private int anoProjetoResumoSid;

	/** Envio de relatórios parciais do monitor */
	@Column(name = "inicio_envio_relatorio_parcial_monitor")
	private Date inicioEnvioRelatorioParcialMonitor;

	@Column(name = "fim_envio_relatorio_parcial_monitor")
	private Date fimEnvioRelatorioParcialMonitor;

	@Column(name = "ano_projeto_relatorio_parcial_monitor")
	private int anoProjetoRelatorioParcialMonitor;

	/** Envio de relatórios final do monitor */
	@Column(name = "inicio_envio_relatorio_final_monitor")
	private Date inicioEnvioRelatorioFinalMonitor;

	@Column(name = "fim_envio_relatorio_final_monitor")
	private Date fimEnvioRelatorioFinalMonitor;

	@Column(name = "ano_projeto_relatorio_final_monitor")
	private int anoProjetoRelatorioFinalMonitor;
	
	@Column(name = "inicio_confirmacao_monitoria")
	private Date inicioConfirmacaoMonitoria;

	@Column(name = "fim_confirmacao_monitoria")
	private Date fimConfirmacaoMonitoria;

	

	public CalendarioMonitoria() {
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * informa o ano dos projetos que estão liberados para receber os relatórios
	 * finais
	 * 
	 * @return
	 */
	public int getAnoProjetoRelatorioFinal() {
		return anoProjetoRelatorioFinal;
	}

	public void setAnoProjetoRelatorioFinal(int anoProjetoRelatorioFinal) {
		this.anoProjetoRelatorioFinal = anoProjetoRelatorioFinal;
	}

	/**
	 * informa o ano dos projetos que estão liberados para receber os relatórios
	 * parciais
	 * 
	 * @return
	 */
	public int getAnoProjetoRelatorioParcial() {
		return anoProjetoRelatorioParcial;
	}

	public void setAnoProjetoRelatorioParcial(int anoProjetoRelatorioParcial) {
		this.anoProjetoRelatorioParcial = anoProjetoRelatorioParcial;
	}

	public Date getFimEnvioRelatorioFinalProjeto() {
		return fimEnvioRelatorioFinalProjeto;
	}

	public void setFimEnvioRelatorioFinalProjeto(
			Date fimEnvioRelatorioFinalProjeto) {
		this.fimEnvioRelatorioFinalProjeto = fimEnvioRelatorioFinalProjeto;
	}

	public Date getFimEnvioRelatorioParcialProjeto() {
		return fimEnvioRelatorioParcialProjeto;
	}

	public void setFimEnvioRelatorioParcialProjeto(
			Date fimEnvioRelatorioParcialProjeto) {
		this.fimEnvioRelatorioParcialProjeto = fimEnvioRelatorioParcialProjeto;
	}

	public Date getFimEnvioResumoSid() {
		return fimEnvioResumoSid;
	}

	public void setFimEnvioResumoSid(Date fimEnvioResumoSid) {
		this.fimEnvioResumoSid = fimEnvioResumoSid;
	}

	public Date getInicioEnvioRelatorioFinalProjeto() {
		return inicioEnvioRelatorioFinalProjeto;
	}

	public void setInicioEnvioRelatorioFinalProjeto(
			Date inicioEnvioRelatorioFinalProjeto) {
		this.inicioEnvioRelatorioFinalProjeto = inicioEnvioRelatorioFinalProjeto;
	}

	public Date getInicioEnvioRelatorioParcialProjeto() {
		return inicioEnvioRelatorioParcialProjeto;
	}

	public void setInicioEnvioRelatorioParcialProjeto(
			Date inicioEnvioRelatorioParcialProjeto) {
		this.inicioEnvioRelatorioParcialProjeto = inicioEnvioRelatorioParcialProjeto;
	}

	public Date getInicioEnvioResumoSid() {
		return inicioEnvioResumoSid;
	}

	public void setInicioEnvioResumoSid(Date inicioEnvioResumoSid) {
		this.inicioEnvioResumoSid = inicioEnvioResumoSid;
	}

	/**
	 * informa o ano dos projetos que estão liberados para receber os resumos do
	 * sid
	 * 
	 * @return
	 */
	public int getAnoProjetoResumoSid() {
		return anoProjetoResumoSid;
	}

	public void setAnoProjetoResumoSid(int anoProjetoResumoSid) {
		this.anoProjetoResumoSid = anoProjetoResumoSid;
	}

	public Date getFimEnvioRelatorioParcialMonitor() {
		return fimEnvioRelatorioParcialMonitor;
	}

	public void setFimEnvioRelatorioParcialMonitor(
			Date fimEnvioRelatorioParcialMonitor) {
		this.fimEnvioRelatorioParcialMonitor = fimEnvioRelatorioParcialMonitor;
	}

	public Date getInicioEnvioRelatorioParcialMonitor() {
		return inicioEnvioRelatorioParcialMonitor;
	}

	public void setInicioEnvioRelatorioParcialMonitor(
			Date inicioEnvioRelatorioParcialMonitor) {
		this.inicioEnvioRelatorioParcialMonitor = inicioEnvioRelatorioParcialMonitor;
	}

	public int getAnoProjetoRelatorioFinalMonitor() {
		return anoProjetoRelatorioFinalMonitor;
	}

	public void setAnoProjetoRelatorioFinalMonitor(
			int anoProjetoRelatorioFinalMonitor) {
		this.anoProjetoRelatorioFinalMonitor = anoProjetoRelatorioFinalMonitor;
	}

	/**
	 * informa o ano dos projetos que estão liberados para receber os relatórios
	 * de monitor
	 * 
	 * @return
	 */
	public int getAnoProjetoRelatorioParcialMonitor() {
		return anoProjetoRelatorioParcialMonitor;
	}

	public void setAnoProjetoRelatorioParcialMonitor(
			int anoProjetoRelatorioParcialMonitor) {
		this.anoProjetoRelatorioParcialMonitor = anoProjetoRelatorioParcialMonitor;
	}

	public Date getFimEnvioRelatorioFinalMonitor() {
		return fimEnvioRelatorioFinalMonitor;
	}

	public void setFimEnvioRelatorioFinalMonitor(
			Date fimEnvioRelatorioFinalMonitor) {
		this.fimEnvioRelatorioFinalMonitor = fimEnvioRelatorioFinalMonitor;
	}

	public Date getInicioEnvioRelatorioFinalMonitor() {
		return inicioEnvioRelatorioFinalMonitor;
	}

	public void setInicioEnvioRelatorioFinalMonitor(
			Date inicioEnvioRelatorioFinalMonitor) {
		this.inicioEnvioRelatorioFinalMonitor = inicioEnvioRelatorioFinalMonitor;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(inicioEnvioRelatorioFinalMonitor, "Data de Início para Envio de Relatório Final de Monitor: ", lista);
		ValidatorUtil.validateRequired(fimEnvioRelatorioFinalMonitor, "Data de Fim para Envio de Relatório Final de Monitor: ", lista);
		ValidatorUtil.validateRequired(anoProjetoRelatorioFinalMonitor,	"Receber Relatório Final de Monitor de Projetos do ano: ", lista);
		ValidatorUtil.validateRequired(inicioEnvioRelatorioParcialMonitor, "Data de Início para Envio de Relatório Parcial de Monitor: ", lista);
		ValidatorUtil.validateRequired(fimEnvioRelatorioParcialMonitor,	"Data de Fim para Envio de Relatório Parcial de Monitor ", lista);
		ValidatorUtil.validateRequired(anoProjetoRelatorioFinalMonitor,	"Receber Relatório Parcial de Monitor de Projetos do ano: ", lista);
		ValidatorUtil.validateRequired(inicioEnvioRelatorioFinalProjeto, "Data de Início para Envio de Relatório Final de Projeto: ", lista);
		ValidatorUtil.validateRequired(fimEnvioRelatorioFinalProjeto, "Data de Fim para Envio de Relatório Final de Projeto: ", lista);
		ValidatorUtil.validateRequired(inicioEnvioRelatorioParcialProjeto, "Data de Início para Envio de Relatório Parcial de Projeto: ", lista);
		ValidatorUtil.validateRequired(fimEnvioRelatorioParcialProjeto,	"Data de Fim para Envio de Relatório Parcial de Projeto: ", lista);
		ValidatorUtil.validateRequired(inicioEnvioResumoSid, "Data de Início para Envio do Resumo SID: ", lista);
		ValidatorUtil.validateRequired(fimEnvioResumoSid, "Data de Fim para Envio do Resumo SID: ", lista);
		ValidatorUtil.validateRequired(fimEdicaoResumoSid, "Data de Fim para Edição do Resumo SID: ", lista);
		return lista;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public int getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(int anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public Date getFimEdicaoResumoSid() {
		return fimEdicaoResumoSid;
	}

	public void setFimEdicaoResumoSid(Date fimEdicaoResumoSid) {
		this.fimEdicaoResumoSid = fimEdicaoResumoSid;
	}
	
	public Date getInicioConfirmacaoMonitoria() {
	    return inicioConfirmacaoMonitoria;
	}

	public void setInicioConfirmacaoMonitoria(Date inicioConfirmacaoMonitoria) {
	    this.inicioConfirmacaoMonitoria = inicioConfirmacaoMonitoria;
	}

	public Date getFimConfirmacaoMonitoria() {
	    return fimConfirmacaoMonitoria;
	}

	public void setFimConfirmacaoMonitoria(Date fimConfirmacaoMonitoria) {
	    this.fimConfirmacaoMonitoria = fimConfirmacaoMonitoria;
	}

	
	

	/**
	 * ********************* RELATORIO MONITOR ********************
	 * 
	 * Retorna true se estiver no período de recebimento do relatórios finais de
	 * projetos de monitoria.
	 * 
	 * @return
	 */
	public boolean isRelatorioFinalMonitorEmAberto() {
		if (inicioEnvioRelatorioFinalMonitor != null)
			return (this.getInicioEnvioRelatorioFinalMonitor().compareTo(
					DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) <= 0)
					&& (this.getFimEnvioRelatorioFinalMonitor().compareTo(
							DateUtils.truncate(new Date(),
									Calendar.DAY_OF_MONTH)) >= 0);
		else
			return false;
	}

	/**
	 * Retorna true se estiver no período de recebimento do relatórios parciais
	 * de monitores.
	 * 
	 * @return
	 */
	public boolean isRelatorioParcialMonitorEmAberto() {
		if (inicioEnvioRelatorioParcialMonitor != null)
			return (this.getInicioEnvioRelatorioParcialMonitor().compareTo(
					DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) <= 0)
					&& (this.getFimEnvioRelatorioParcialMonitor().compareTo(
							DateUtils.truncate(new Date(),
									Calendar.DAY_OF_MONTH)) >= 0);
		else
			return false;
	}

	/**
	 * ****************** RELATORIO PROJETO************
	 * 
	 * Retorna true se estiver no período de recebimento do relatórios finais
	 * monitores.
	 * 
	 * @return
	 */
	public boolean isRelatorioFinalProjetoEmAberto() {
		if (inicioEnvioRelatorioFinalProjeto != null)
			return (this.getInicioEnvioRelatorioFinalProjeto().compareTo(
					DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) <= 0)
					&& (this.getFimEnvioRelatorioFinalProjeto().compareTo(
							DateUtils.truncate(new Date(),
									Calendar.DAY_OF_MONTH)) >= 0);
		else
			return false;
	}

	/**
	 * Retorna true se estiver no período de recebimento do relatórios parciais
	 * de projetos de monitoria.
	 * 
	 * @return
	 */
	public boolean isRelatorioParcialProjetoEmAberto() {
		if (inicioEnvioRelatorioParcialProjeto != null)
			return (this.getInicioEnvioRelatorioParcialProjeto().compareTo(
					DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) <= 0)
					&& (this.getFimEnvioRelatorioParcialProjeto().compareTo(
							DateUtils.truncate(new Date(),
									Calendar.DAY_OF_MONTH)) >= 0);
		else
			return false;
	}

	/** 
	 * *********************************RESUMO SID ***************************
	 * 
	 * Verifica se é permitido o envio de resumo do sid.
	 *  
	 */ 
	public boolean isEnvioResumoSidEmAberto() {
		if (inicioEnvioResumoSid != null)
			return (this.getInicioEnvioResumoSid().compareTo(
					DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) <= 0)
					&& (this.getFimEnvioResumoSid().compareTo(
							DateUtils.truncate(new Date(),
									Calendar.DAY_OF_MONTH)) >= 0);
		else
			return false;
	}

	public boolean isEditarResumoSid() {
		if (fimEdicaoResumoSid != null)
			return (this.getFimEdicaoResumoSid().compareTo(
					DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) >= 0);
		else
			return false;
	}
	
	
	/**
	 * ********************* CONFIRMAÇÃO DA MONITORIA ********************
	 * 
	 * Retorna true se estiver no período de confirmação de monitoria.
	 * Normalmente os monitores só podem ser efetivados em projetos de monitoria
	 * no período de 01 a 05 de cada mês.
	 * 
	 * @return
	 */
	public boolean isPeriodoConfirmacaoMonitoriaEmAberto() {
		if (inicioConfirmacaoMonitoria != null) {
			return (this.getInicioConfirmacaoMonitoria().compareTo(
					DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) <= 0)
					&& (this.getFimConfirmacaoMonitoria().compareTo( 
						DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)) >= 0);
		} else { 
			return false;
		}
	}


}