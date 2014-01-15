/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/05/2010
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;
import java.util.List;

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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Entidade que representa a Solicitação de Reposição de Prova enviado pelo Discente.
 * 
 * Art. 101. Impedido de participar de qualquer avaliação, por motivo de caso fortuito ou força
 * maior devidamente comprovado e justificado, o aluno tem direito de realizar avaliação de
 * reposição. 
 * 
 * @author Arlindo Rodrigues
 * 
 */
@Entity
@Table(name="solicitacao_reposicao_avaliacao", schema="ensino")
public class SolicitacaoReposicaoAvaliacao implements Validatable {
	
	/** Construtor Padrão */
	public SolicitacaoReposicaoAvaliacao() {		
	}
		
	/** Chave primária da indicação. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_solicitacao_reposicao_avaliacao")
	private int id;	
	
	/** Discente que realizou a solicitação. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente;	
	
	/** Avaliação que foi solicitada a reposição. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_avaliacao_data")	
	private DataAvaliacao dataAvaliacao;	
	
	/** Avaliação que foi solicitada a reposição. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")	
	private Turma turma;		
	
	/** Anexo enviado pelo discente */
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
	/** Justificativa do parecer */
	private String justificativa;
	
	/** Indica se a solicitação está ativa */
	private boolean ativo;
	
	/** Status da solicitação da reposição, modificado pelo chefe do departamento */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status")		
	private StatusReposicaoAvaliacao status;

	/** Parecer do docente sobre a solicitação da reposição. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status_parecer")	
	private StatusReposicaoAvaliacao statusParecer;
	
	/** Data da Prova Sugerida pelo Professor */
	@Column(name = "data_prova_sugerida")
	private Date dataProvaSugerida;
	
	/** Observação escrita pelo professor ao dar o parecer */
	@Column(name = "observacao_docente")
	private String observacaoDocente;
	
	/** Observação escrita pelo Chefe do Departamento ao homologar o parecer */
	@Column(name = "observacao_homologacao")
	private String observacaoHomologacao;
	
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
	
	/** Data do cancelamento. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cancelamento")
	private Date dataCancelamento;

	/** Registro entrada de quem cancelou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_cancelamento")
	private RegistroEntrada registroCancelamento;
	
	/** Local de realização da prova */
	@Column(name="local_prova")
	private String localProva;
	
	/** Indica se está ou não homologado pelo chefe */
	private boolean homologado;
	
	/** Atributo que auxilia na seleção para homologação de reposição de prova */
	@Transient
	private boolean selecionado;
	
	/** Lista de avaliações da turma da solicitação */
	@Transient
	private List<DataAvaliacao> avaliacoes;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
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
	
	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}
	
	public Date getDataProvaSugerida() {
		return dataProvaSugerida;
	}

	public void setDataProvaSugerida(Date dataProvaSugerida) {
		this.dataProvaSugerida = dataProvaSugerida;
	}

	public String getObservacaoDocente() {
		return observacaoDocente;
	}

	public void setObservacaoDocente(String observacaoDocente) {
		this.observacaoDocente = observacaoDocente;
	}	

	public DataAvaliacao getDataAvaliacao() {
		return dataAvaliacao;
	}

	public void setDataAvaliacao(DataAvaliacao dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	public String getObservacaoHomologacao() {
		return observacaoHomologacao;
	}

	public void setObservacaoHomologacao(String observacaoHomologacao) {
		this.observacaoHomologacao = observacaoHomologacao;
	}

	@Transient
	public String getDescricaoStatus() {
		return (status != null ? status.getDescricao() : null);
	}

	/**
	 * Retorna o status da solicitação verificando se ela foi submetida para chefia.
	 * @return
	 */
	@Transient
	public String getDescricaoStatusDocente() {
		if (status != null && statusParecer != null && status.isCadastrada() && !isHomologado())
			return "SUBMETIDO À CHEFIA";
		return (status != null ? status.getDescricao() : null);
	}
	
	@Transient
	public boolean isCadastrada(){
		return (status != null ? status.isCadastrada() : false);
	}	
	
	@Transient
	public boolean isDeferido(){
		return (status != null ? status.isDeferido() : false);
	}	
	
	@Transient
	public boolean isIndeferido(){
		return (status != null ? status.isIndeferido() : false);
	}
	
	@Transient
	public boolean isCancelado(){
		return (status != null ? status.isCancelado() : false);
	}	

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(discente, "Discente", lista);		
		ValidatorUtil.validateRequired(dataAvaliacao, "Avaliação", lista);		
		ValidatorUtil.validateRequired(justificativa, "Justificativa", lista);
		
		return lista;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getLocalProva() {
		return localProva;
	}

	public void setLocalProva(String localProva) {
		this.localProva = localProva;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public StatusReposicaoAvaliacao getStatus() {
		return status;
	}

	public void setStatus(StatusReposicaoAvaliacao status) {
		this.status = status;
	}

	public StatusReposicaoAvaliacao getStatusParecer() {
		return statusParecer;
	}

	public void setStatusParecer(StatusReposicaoAvaliacao statusParecer) {
		this.statusParecer = statusParecer;
	}

	public boolean isHomologado() {
		return homologado;
	}

	public void setHomologado(boolean homologado) {
		this.homologado = homologado;
	}

	public void setAvaliacoes(List<DataAvaliacao> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public List<DataAvaliacao> getAvaliacoes() {
		return avaliacoes;
	}
}
