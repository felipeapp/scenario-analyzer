/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/05/2008
 *
 */	
package br.ufrn.sigaa.assistencia.dominio;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.restaurante.dominio.DiasAlimentacao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe que representa uma SOLICITAÇÃO de Bolsa Auxílio por parte do estudante.
 * As bolsas são solicitadas a Secretaria de Apóio ao Estudante através do portal do discente 
 * pelos próprios alunos quando está no período de inscrição ou pelo portal do SAE.   
 * 
 * @author agostinho campos
 * 
 */

@Entity
@Table(name = "bolsa_auxilio", schema = "sae")
public class BolsaAuxilio implements PersistDB, Validatable {

	public static final String APENAS_UM_TURNO = "TU";
	
	/** Chave primária da bolsa de auxilio */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_bolsa_auxilio")
	private int id;

	/** Estados que uma Bolsa Auxílio pode se encontrar. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_situacao_bolsa")
	private SituacaoBolsaAuxilio situacaoBolsa;

	/** Referencia a adesão ao cadastro único da bolsa em questão. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_adesao")
	private AdesaoCadastroUnicoBolsa adesaoCadUnico;
	
	/** Custo mensal que o discente tem mensalmente com transporte. */
	@Column(name="custo_mensal_transporte")
	private Double custoMensalTransporte = new Double(0);
	
	/** Turno que o discente frequenta a universidade (manha/tarde, tarde/noite ou manha/tarde/noite) */
	@Column(name="turno_atividade")
	private String turnoAtividade;
	
	/** Discente que solicitou a bolsa de auxilio */
	@OneToOne
	@JoinColumn(name = "id_discente")
	private Discente discente;
	
	/** Armazena o número do comprovante da solicitação da bolsa feita pelo discente */
	@Column(name="numero_comprovante")
	private Integer numeroComprovante;
	
	/** Armazena o hash de autenticação da solicitação */
	@Column(name="hash_autenticacao")
	private String hashAutenticacao;

	/**
	 * Representa os dias que esse aluno tem direito a refeição no R.U.
	 */
	@OneToMany(cascade=CascadeType.ALL, mappedBy = "bolsaAuxilio")
	@JoinColumn(name = "id_bolsa_auxilio")
	private List<DiasAlimentacao> diasAlimentacao = new ArrayList<DiasAlimentacao>();
	
	/** Armazena o período no qual do discente solicitou o período */
	@OneToMany(mappedBy = "bolsaAuxilio", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinColumn(name = "id_bolsa_auxilio_periodo")
	private List<BolsaAuxilioPeriodo> bolsaAuxilioPeriodo;
	
	/** Documentos entregues pelos discentes ao SAE */
	@ManyToMany
	@JoinTable(schema="sae", name="bolsa_documentos_entregues",
			joinColumns = {@JoinColumn(name="id_bolsa_auxilio")},
			inverseJoinColumns = {@JoinColumn(name="id_documento_entregue")})
	private List<DocumentosEntreguesSAE> documentosEntregues;
	
	/** Meios de transporte que o discente utiliza para acessar a Instituição */
	@ManyToMany()
	@JoinTable(schema="sae", name="bolsa_meio_transporte",
	joinColumns = {@JoinColumn(name = "id_bolsa_auxilio")},
	inverseJoinColumns = {@JoinColumn(name = "id_meio_transporte")})
	private List<TipoMeioTransporte> tipoMeioTransporte;
	
	/** Preenchido no caso da bolsa ser do tipo residência informando qual residência
	 * o aluno escolheu. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_residencia")
	private ResidenciaUniversitaria residencia;

	/** Usuário que solicitou a bolsa de Auxilio */ 
	@CriadoPor
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	/** Representa os tipos de bolsa auxílio disponíveis */
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name = "id_tipo_bolsa_auxilio")
	private TipoBolsaAuxilio tipoBolsaAuxilio;
	
	/** Bolsa do discente no SIPAC. */
	@Column(name="id_tipo_bolsa_sipac")
	private Integer tipoBolsaSIPAC;
	
	/** Bolsa do discente no SIPAC. */
	@CampoAtivo(true)
	@Column(name="bolsa_ativa_sipac")
	private boolean bolsaAtivaSIPAC;

	/** Descrição da bolsa utilizada no SIPAC */
	@Transient
	private String descricaoTipoBolsaSIPAC;
	
	/** Data de cadastro da solicitação da bolsa */
	@CriadoEm
	@Column(name = "data_solicitacao")
	private Date dataSolicitacao;
	
	/** Data da renovação da bolsa de auxilio */
	@Column(name = "data_renovacao")
	private Date dataRenovacao;
	
	/** Justificativa realizada pelo aluno quando o mesmo solicita bolsa; */
	@Column(name = "justificativa_requerimento")
	private String justificativaRequerimento;

	/** Parecer do serviço social, explicando a situação do aluno. */
	@Column(name = "parecer_servico_social")
	private String parecerServicoSocial;
	
	/** Indica se o discente recebeu do pais de Origem do mesmo */
	@Column(name = "recebeu_auxilio")
	private boolean recebeuAuxilio;
	
	/** Indica se a situação do mesmo está regular */
	@Column(name = "situacao_regular")
	private boolean situacaRegular;
	
	/** Armazena o número do registro no caso das bolsas PROMISAES */
	@Column(name = "rne")
	private String registroNacionalEstrangeiro;
	
	/** Indica se o discente foi selecionado para receber bolsa auxílio. */
	@Transient
	private boolean selecionado;
	
	/** Indica se o discente concorda os termos. */
	@Transient
	private boolean termoConcordancia;
	
	/** Indica se essa é uma nova solicitação de bolsa auxílio. */
	@Transient
	private boolean novaSolicitacao;
	
	@Column(name = "solicitada_renovacao")
	private Boolean solicitadaRenovacao;
	
	/** Serve para indicar se é um processo de renovação, 
	 * nesse caso a data não deve ser considerada. */
	@Transient
	private boolean renovacao;

	@Transient
	private int ano;

	@Transient
	private int periodo;
	
	/** Bolsa utilizada para a renovação das bolsas. */
	@Transient
	private BolsaAuxilio bolsaAuxilioOriginal;
	
	public BolsaAuxilio() {
	}
	
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public String getJustificativaRequerimento() {
		return justificativaRequerimento;
	}

	public void setJustificativaRequerimento(String justificativaRequerimento) {
		this.justificativaRequerimento = justificativaRequerimento;
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
	
	public List<DiasAlimentacao> getDiasAlimentacao() {
		return diasAlimentacao;
	}

	public void setDiasAlimentacao(List<DiasAlimentacao> diasAlimentacao) {
		this.diasAlimentacao = diasAlimentacao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getParecerServicoSocial() {
		return parecerServicoSocial;
	}

	public void setParecerServicoSocial(String parecerServicoSocial) {
		this.parecerServicoSocial = parecerServicoSocial;
	}

	public List<BolsaAuxilioPeriodo> getBolsaAuxilioPeriodo() {
		return bolsaAuxilioPeriodo;
	}

	public void setBolsaAuxilioPeriodo(
			List<BolsaAuxilioPeriodo> bolsaAuxilioPeriodo) {
		this.bolsaAuxilioPeriodo = bolsaAuxilioPeriodo;
	}

	public ResidenciaUniversitaria getResidencia() {
		return residencia;
	}

	public void setResidencia(ResidenciaUniversitaria residencia) {
		this.residencia = residencia;
	}

	public Date getDataRenovacao() {
		return dataRenovacao;
	}

	public void setDataRenovacao(Date dataRenovacao) {
		this.dataRenovacao = dataRenovacao;
	}

	public String getTurnoAtividade() {
		return turnoAtividade;
	}
	
	/** Retorna a descrição do turno escolhido */
	public String getDescricaoTurnoAtividade() {
		if( turnoAtividade!= null){
			if (turnoAtividade.equals("MN"))
				return "Manhã e Tarde";
			if (turnoAtividade.equals("TN"))
				return "Tarde e Noite";
			if (turnoAtividade.equals("MTN"))
				return "Manhã, Tarde e Noite";
			if (turnoAtividade.equals("TU"))
				return "Apenas um Turno";
		}
		
		return "";
	}

	public void setTurnoAtividade(String turnoAtividade) {
		this.turnoAtividade = turnoAtividade;
	}

	/** Retorna o custo mensal do discente utilizado no transporte */
	public double getCustoMensalTransporte() {
		if (custoMensalTransporte == null)
			return 0;
		else
			return custoMensalTransporte;
	}

	public void setCustoMensalTransporte(double custoMensalTransporte) {
		this.custoMensalTransporte = custoMensalTransporte;
	}
	
	public SituacaoBolsaAuxilio getSituacaoBolsa() {
		return situacaoBolsa;
	}

	public void setSituacaoBolsa(SituacaoBolsaAuxilio situacaoBolsa) {
		this.situacaoBolsa = situacaoBolsa;
	}

	public TipoBolsaAuxilio getTipoBolsaAuxilio() {
		return tipoBolsaAuxilio;
	}

	public void setTipoBolsaAuxilio(TipoBolsaAuxilio tipoBolsaAuxilio) {
		this.tipoBolsaAuxilio = tipoBolsaAuxilio;
	}
	
	public List<TipoMeioTransporte> getTipoMeioTransporte() {
		return tipoMeioTransporte;
	}

	public void setTipoMeioTransporte(List<TipoMeioTransporte> tipoMeioTransporte) {
		this.tipoMeioTransporte = tipoMeioTransporte;
	}

	public AdesaoCadastroUnicoBolsa getAdesaoCadUnico() {
		return adesaoCadUnico;
	}

	public void setAdesaoCadUnico(AdesaoCadastroUnicoBolsa adesaoCadUnico) {
		this.adesaoCadUnico = adesaoCadUnico;
	}

	public List<DocumentosEntreguesSAE> getDocumentosEntregues() {
		return documentosEntregues;
	}

	public void setDocumentosEntregues(
			List<DocumentosEntreguesSAE> documentosEntregues) {
		this.documentosEntregues = documentosEntregues;
	}

	public Integer getNumeroComprovante() {
		return numeroComprovante;
	}

	public void setNumeroComprovante(Integer numeroComprovante) {
		this.numeroComprovante = numeroComprovante;
	}
	
	public String getHashAutenticacao() {
		return hashAutenticacao;
	}

	public void setHashAutenticacao(String hashAutenticacao) {
		this.hashAutenticacao = hashAutenticacao;
	}

	public Integer getTipoBolsaSIPAC() {
		return tipoBolsaSIPAC;
	}

	public void setTipoBolsaSIPAC(Integer tipoBolsaSIPAC) {
		this.tipoBolsaSIPAC = tipoBolsaSIPAC;
	}
	
	public void setCustoMensalTransporte(Double custoMensalTransporte) {
		this.custoMensalTransporte = custoMensalTransporte;
	}

	public String getDescricaoTipoBolsaSIPAC() {
		return descricaoTipoBolsaSIPAC;
	}

	public void setDescricaoTipoBolsaSIPAC(String descricaoTipoBolsaSIPAC) {
		this.descricaoTipoBolsaSIPAC = descricaoTipoBolsaSIPAC;
	}
	
	public boolean isRecebeuAuxilio() {
		return recebeuAuxilio;
	}

	public void setRecebeuAuxilio(boolean recebeuAuxilio) {
		this.recebeuAuxilio = recebeuAuxilio;
	}

	public String getRegistroNacionalEstrangeiro() {
		return registroNacionalEstrangeiro;
	}

	public void setRegistroNacionalEstrangeiro(String registroNacionalEstrangeiro) {
		this.registroNacionalEstrangeiro = registroNacionalEstrangeiro;
	}
	
	public boolean isSituacaRegular() {
		return situacaRegular;
	}

	public void setSituacaRegular(boolean situacaRegular) {
		this.situacaRegular = situacaRegular;
	}

	public boolean isPermiteDefinirDiasAlimentacao() {
		return ( tipoBolsaAuxilio.isAlimentacao() && situacaoBolsa.isContemplada() ) || ( tipoBolsaAuxilio.isResidenciaGraduacao() && situacaoBolsa.isAuxilioMoradia() );
	}
	
	public ListaMensagens validate() {
		return null;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isTermoConcordancia() {
		return termoConcordancia;
	}

	public void setTermoConcordancia(boolean termoConcordancia) {
		this.termoConcordancia = termoConcordancia;
	}

	public boolean isNovaSolicitacao() {
		return novaSolicitacao;
	}

	public void setNovaSolicitacao(boolean novaSolicitacao) {
		this.novaSolicitacao = novaSolicitacao;
	}

	public boolean isBolsaAtivaSIPAC() {
		return bolsaAtivaSIPAC;
	}

	public void setBolsaAtivaSIPAC(boolean bolsaAtivaSIPAC) {
		this.bolsaAtivaSIPAC = bolsaAtivaSIPAC;
	}


	public boolean isRenovacao() {
		return renovacao;
	}

	public void setRenovacao(boolean renovacao) {
		this.renovacao = renovacao;
	}

	public Boolean getSolicitadaRenovacao() {
		return solicitadaRenovacao;
	}

	public void setSolicitadaRenovacao(Boolean solicitadaRenovacao) {
		this.solicitadaRenovacao = solicitadaRenovacao;
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

	public BolsaAuxilio getBolsaAuxilioOriginal() {
		return bolsaAuxilioOriginal;
	}

	public void setBolsaAuxilioOriginal(BolsaAuxilio bolsaAuxilioOriginal) {
		this.bolsaAuxilioOriginal = bolsaAuxilioOriginal;
	}
}