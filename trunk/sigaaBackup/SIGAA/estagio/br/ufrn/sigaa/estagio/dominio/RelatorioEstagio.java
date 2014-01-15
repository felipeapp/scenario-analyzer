/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/11/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 * Entidade que contém as informações referente 
 * aos relatórios de estágio.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "relatorio_estagio", schema = "estagio")
public class RelatorioEstagio implements PersistDB {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_relatorio_estagio")
	private int id;	
	
	/** Estágio que será respondido o relatório */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_estagiario")	
	private Estagiario estagio;
		
	/** Questionário ao qual será respondido */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_questionario")	
	private Questionario questionario;
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/** Status atual do Relatório */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status")	
	private StatusRelatorioEstagio status;	
	
	/** Data da Aprovação. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_aprovacao")
	@CriadoEm
	private Date dataAprovacao;
	
	/** Registro entrada de quem Aprovou o Relatório. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_aprovacao")
	private RegistroEntrada registroAprovacao;	
	
	/** Tipo do Relatório */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_relatorio_estagio")	
	private TipoRelatorioEstagio tipo;		
	
	/** Renovação de Estágio vinculado ao relatório, caso o relatório for de Renovação */
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY )
	@JoinColumn(name="id_renovacao_estagio")
	private RenovacaoEstagio renovacaoEstagio;	
	
	/** Respostas de Relatório  */
	@OneToOne(mappedBy = "relatorioEstagio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private RelatorioEstagioRespostas relatorioRespostas;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Estagiario getEstagio() {
		return estagio;
	}

	public void setEstagio(Estagiario estagio) {
		this.estagio = estagio;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}	
	
	public RelatorioEstagioRespostas getRelatorioRespostas() {
		return relatorioRespostas;
	}

	public void setRelatorioRespostas(RelatorioEstagioRespostas relatorioRespostas) {
		this.relatorioRespostas = relatorioRespostas;
	}

	public StatusRelatorioEstagio getStatus() {
		return status;
	}

	public void setStatus(StatusRelatorioEstagio status) {
		this.status = status;
	}

	public TipoRelatorioEstagio getTipo() {
		return tipo;
	}

	public void setTipo(TipoRelatorioEstagio tipo) {
		this.tipo = tipo;
	}

	public Date getDataAprovacao() {
		return dataAprovacao;
	}

	public void setDataAprovacao(Date dataAprovacao) {
		this.dataAprovacao = dataAprovacao;
	}

	public RegistroEntrada getRegistroAprovacao() {
		return registroAprovacao;
	}

	public void setRegistroAprovacao(RegistroEntrada registroAprovacao) {
		this.registroAprovacao = registroAprovacao;
	}

	@Override
	public int hashCode() {		
		return HashCodeUtil.hashAll(id);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}	
	
	public RenovacaoEstagio getRenovacaoEstagio() {
		return renovacaoEstagio;
	}

	public void setRenovacaoEstagio(RenovacaoEstagio renovacaoEstagio) {
		this.renovacaoEstagio = renovacaoEstagio;
	}

	/**
	 * Verifica se o tipo do Relatório é Periódico
	 * @return
	 */
	@Transient
	public boolean isRelatorioPeriodico(){
		if (tipo == null)
			return false;
		return tipo.getId() == TipoRelatorioEstagio.RELATORIO_PERIODICO;
	}
	
	/**
	 * Verifica se o tipo do Relatório é Final
	 * @return
	 */
	@Transient
	public boolean isRelatorioFinal(){
		if (tipo == null)
			return false;
		return tipo.getId() == TipoRelatorioEstagio.RELATORIO_FINAL;
	}	
	
	/**
	 * Verifica se o Relatório está aprovado
	 * @return
	 */
	@Transient
	public boolean isAprovado(){
		if (status == null)
			return false;
		return status.getId() == StatusRelatorioEstagio.APROVADO;
	}		
}
