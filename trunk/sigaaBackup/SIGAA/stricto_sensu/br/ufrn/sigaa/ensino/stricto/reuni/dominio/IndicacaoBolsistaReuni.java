/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/02/2010
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 * Entidade que representa uma "Indicação de Bolsa REUNI" para um discente de pós-graduação
 * para um plano de trabalho.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name="indicacao_bolsista_reuni", schema="stricto_sensu")
public class IndicacaoBolsistaReuni implements Validatable {
	
	/**
	 * Chave primária da indicação.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_indicacao_bolsista_reuni")
	private int id;
	
	/**
	 * Plano de trabalho que o discente foi indicado.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_plano_trabalho_reuni")
	private PlanoTrabalhoReuni planoTrabalho;
		
	/**
	 * Discente que foi indicado ao plano de trabalho.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private DiscenteStricto discente;
	
	/**
	 * Indica se a indicação está ativa ou não.
	 */
	private boolean ativo;
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Data da última atualização. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;

	/** Registro entrada da última atualização. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAlteracao;
	
	/** Data de cancelamento. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cancelamento")
	private Date dataCancelamento;
	
	/** Registro entrada do cancelamento. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cancelamento")
	private RegistroEntrada registroCancelamento;	
	
	/** Período da indicação */
	@OneToMany(mappedBy = "indicacaoBolsistaReuni", cascade = CascadeType.ALL, fetch = FetchType.EAGER)	
	private List<PeriodoIndicacaoReuni> periodosIndicacao = new ArrayList<PeriodoIndicacaoReuni>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PlanoTrabalhoReuni getPlanoTrabalho() {
		return planoTrabalho;
	}

	public void setPlanoTrabalho(PlanoTrabalhoReuni planoTrabalho) {
		this.planoTrabalho = planoTrabalho;
	}

	public DiscenteStricto getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public RegistroEntrada getRegistroAlteracao() {
		return registroAlteracao;
	}

	public void setRegistroAlteracao(RegistroEntrada registroAlteracao) {
		this.registroAlteracao = registroAlteracao;
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(planoTrabalho, "Plano de Trabalho", lista);
		
		ValidatorUtil.validateRequired(discente, "Discente", lista);
		
		ValidatorUtil.validateRequired(periodosIndicacao, "Periodo(s) de Indicação", lista);
		
		return lista;
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public RegistroEntrada getRegistroCancelamento() {
		return registroCancelamento;
	}

	public void setRegistroCancelamento(RegistroEntrada registroCancelamento) {
		this.registroCancelamento = registroCancelamento;
	}

	public List<PeriodoIndicacaoReuni> getPeriodosIndicacao() {
		return periodosIndicacao;
	}

	public void setPeriodosIndicacao(List<PeriodoIndicacaoReuni> periodosIndicacao) {
		this.periodosIndicacao = periodosIndicacao;
	}
}
