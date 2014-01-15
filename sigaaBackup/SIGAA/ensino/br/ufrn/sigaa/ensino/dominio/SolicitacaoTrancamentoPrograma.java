/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/04/2010 
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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Representa uma solicitação de trancamento de programa
 *
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name = "solicitacao_trancamento_programa", schema = "ensino")
public class SolicitacaoTrancamentoPrograma implements Validatable {

	/**
	 * Chave primária
	 */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })	
	@Column(name = "id_solicitacao_trancamento_programa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;	
	
	/**
	 * Discente que solicitou o trancamento
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente")
	private Discente discente;	
	
	/**
	 * Ano letivo referente ao trancamento.
	 */
	private int ano;
	
	/**
	 * Periodo letivo referente ao trancamento.
	 */
	private int periodo;
	
	/**
	 * Data de início do trancamento
	 * (Usado para Stricto Sensu)
	 */
	@Column(name = "inicio_trancamento")
	private Date inicioTrancamento;
	
	/**
	 * Quantidade de meses que o programa será trancado
	 * (Usado para Stricto Sensu)
	 */
	@Column(name = "numero_meses")
	private Integer numeroMeses;
	
	/**
	 * situação da solicitação
	 */
	@Column(name = "situacao")
	private int situacao;

	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/**
	 * data do atendimento da solicitação
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atendimento", unique = false, insertable = true, updatable = true)
	private Date dataAtendimento;

	/**
	 * registro de entrada de quem atendeu a solicitação
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atendedor", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroAtendendor;

	/**
	 * Observação do Indeferimento
	 */
	private String observacao;	

	/**
	 * Data do cancelamento (caso o discente desistir do trancamento)
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cancelamento", unique = false, insertable = true, updatable = true)
	private Date dataCancelamento;
	
	/**Indica se a solicitação de trancamento é a posteriori ou não.*/
	private boolean posteriori;

	public ListaMensagens validate() {
		return null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}	

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtendendor() {
		return registroAtendendor;
	}

	public void setRegistroAtendendor(RegistroEntrada registroAtendendor) {
		this.registroAtendendor = registroAtendendor;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	/**
	 * Retorna a descrição da situação atual do trancamento
	 * @return
	 */
	public String getSituacaoString() {
		return StatusSolicitacaoTrancamentoPrograma.getSituacaoDescricao(situacao);
	}

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/**
	 * Indica se a solicitação está com o status SOLICITADO.
	 * @return
	 */
	@Transient
	public boolean isSolicitado(){
		return situacao == StatusSolicitacaoTrancamentoPrograma.SOLICITADO;
	}

	/**
	 *Indica se a solicitação está com o status TRANCADO.
	 * @return
	 */
	@Transient
	public boolean isTrancado() {
		return situacao == StatusSolicitacaoTrancamentoPrograma.TRANCADO;
	}
	
	/**
	 * Indica se a solicitação está com o status CANCELADO.
	 * @return
	 */
	@Transient
	public boolean isCancelado(){
		return situacao == StatusSolicitacaoTrancamentoPrograma.CANCELADO;
	}
	
	/**
	 * Indica se a solicitação está com o status INDEFERIDO.
	 * @return
	 */
	@Transient
	public boolean isIndeferido(){
		return situacao == StatusSolicitacaoTrancamentoPrograma.INDEFERIDO;
	}	

	/**
	 * Auxilia na exibição dos anexos. 
	 * Verifica se o Curso do discente é Comunicação Social
	 * @return
	 */
	@Transient
	public boolean isComunicacaoSocial(){
		int id = Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.ID_CURSO_COMUNICACAO_SOCIAL));
		return (discente.getCurso().getId() == id);
	}
	
	/**
	 * Auxilia na exibição dos anexos. 
	 * Verifica se o Curso do discente é Psicologia
	 * @return
	 */
	@Transient
	public boolean isPsicologia(){
		int id = Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.ID_CURSO_PSICOLOGIA));
		return (discente.getCurso().getId() == id);
	}
	
	/**
	 * Auxilia na exibição dos anexos. 
	 * Verifica se o curso do discente é da área biomédica.
	 * @return
	 */
	@Transient
	public boolean isBiomedica(){
		int id = Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosGraduacao.ID_AREA_CCS));
		return (discente.getCurso().getAreaCurso().getId() == id);
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getInicioTrancamento() {
		return inicioTrancamento;
	}

	public void setInicioTrancamento(Date inicioTrancamento) {
		this.inicioTrancamento = inicioTrancamento;
	}

	public Integer getNumeroMeses() {
		return numeroMeses;
	}

	public void setNumeroMeses(Integer numeroMeses) {
		this.numeroMeses = numeroMeses;
	}

	public boolean isPosteriori() {
		return posteriori;
	}

	public void setPosteriori(boolean posteriori) {
		this.posteriori = posteriori;
	}
	
}
