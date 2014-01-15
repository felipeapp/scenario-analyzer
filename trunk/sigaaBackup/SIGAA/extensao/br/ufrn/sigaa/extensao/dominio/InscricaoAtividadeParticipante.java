/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2009
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

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

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.sigaa.extensao.timer.VerificaPagamentosCursosEventosExtensaoTimer;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;

/**
 * <p>Representa as inscrições online para participantes em Cursos ou Eventos de Extensão através do portal público do SIGAA. 
 * As inscrições são disponibilizadas dentro de um período de inscrição definido para que membros externos ou internos à 
 * instituição possam participar das atividades desenvolvidas pela UFRN.</p>
 * 
 * <p>Cada nova inscrição realizada por uma pessoa é criada uma nova entidade dessa no banco. </p>
 * 
 * <p> <strong> 
 * ***************************************************************************************<br/>
 * Deve guardar apenas as informações de uma inscrição específica de um participante. 
 * Os dados comuns do participante são criados apenas 1 vez na entidade <code>CadastroParticipanteAtividadeExtensao</code>
 * para evitar duplicações e o usuário ter que informar todos os seus dados pessoas novamente a cada inscrição.
 * ***************************************************************************************<br/> 
 * </strong>
 * </p>
 * 
 * @author Daniel Augusto
 * @author Jadson
 * @version 2.0 - Jadson - Removendo tudo que é informações pessoais do participante, essa entidade 
 * deve guardar apenas os dados exclusivos de uma inscrição do da pessoa.
 */
@Entity
@Table(schema = "extensao", name = "inscricao_atividade_participante")
public class InscricaoAtividadeParticipante implements Validatable {

	/**
	 * O padrão utilizado no sistema para valores monetários da multa
	 */
	public static final String PATTERN_DEFAULT = "¤ ###,###,##0.00";
	
	
	/** Atributo utilizado para representar o ID da inscrição */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="extensao.inscricao_atividade_participante_sequence") })
	@Column(name = "id_inscricao_atividade_participante", unique = true, nullable = false)
    private int id;
	
	
	/** Instituição do participante inscrito, informada no momento da inscrição. 
	 * Ele pode se inscre como instituições diferentes em cursos ou eventos diferentes.*/
	@Column(name = "instituicao", nullable=false)
	private String instituicao;

	////////////////////////Informações de auditoria  ////////////////////////////////
    /** Data da inscrição. */
    @CriadoEm
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    private Date dataCadastro;
    
    
    /** Registro de entrada do responsável pelo cadastro do participante na ação de extensão. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	
	/** Data de cadastro. */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_ultima_atualizacao")
	private Date dataUltimaAtualizacao;

	/** Registro de entrada do responsável pelo cadastro da inscrição. */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	private RegistroEntrada registroUltimaAtualizacao;

	
	/** Registro de entrada do responsável pelo estorno do pagamento. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_estorno")
	private RegistroEntrada registroEstorno;
	
	/** Registro de estorno do pagamento. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_estorno")
	private Date dataEstorno;
	
	////////////////////////Informações de auditoria  ////////////////////////////////
    
    
//    /** Guarda a informação do vinculo de discente que o usuário possui, se ele for discente da instituição. */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_discente")
//    private Discente discente;
//
//    /** Guarda a informação do vinculo de servidor que o usuário possui, se ele for servidor da instituição. */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_servidor")
//    private Servidor servidor;
    
    /** Situação da inscrição do participante */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status_inscricao_participante", nullable=false)
    private StatusInscricaoParticipante statusInscricao;
    
    /**  
     * <p>Partipande Extensão criado a partir dessa inscrição. </p>
     * 
     *  <p>Quando o coordenador confirma a inscrição do usuário, é criado uma participante
     *  na atividade de extensão para qual feita a inscrição, a referência é mantida aqui. </p>
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_participante_acao_extensao", nullable=true)
    private ParticipanteAcaoExtensao participanteExtensao;
    
    
    
    /** Para qual período de inscrição foi feita a inscrição do participante, liga a inscrição com os dados do curso ou evento. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscricao_atividade")
    private InscricaoAtividade inscricaoAtividade = new InscricaoAtividade();
    
    
    /** 
     * <p>Os dados cadastrais dos participantes que se inscreveram no curso ou evento de extensão.</p>
     * 
     * <p>Identifica o participante, pois ele deve passar a se cadastrar apenas 1 vez.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cadastro_participante_atividade_extensao", nullable=false)
    private CadastroParticipanteAtividadeExtensao cadastroParticipante;
    
    
    
	/** Arquivo que pode ser associado a inscrição. Permite que o candidato envie trabalhos, currículos, comprovantes,  etc. */
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
	
	/** Descrição do arquivo informada pelo usuário no momento de se inscrever. */
	@Column(name = "descricao_arquivo")
	private String descricaoArquivo;	
    
	
	
	
	/**  O valor a ser pago pelo usuário quando há cobrança de taxa. 
	 *   Gerado a partir da modalidade de participante escolhida pelo usuário. 
	 */
	@Column(name = "valor_taxa_matricula", nullable=true)
	private BigDecimal valorTaxaMatricula = new BigDecimal(0);
	
	
	/** 
	 *  <p> Guarda uma referência a GRU que foi gerada para pagamento dessa inscrição.</p>
	 *  
	 *  <p> <strong>Não tem um mapeamento com GuiaRecolhimentoUniao porque essa informação está no bando comum. <strong> </p>
	 *  <br/>
	 *  <p> <strong> A GRU SÓ É CRIADA E SEU ID SALVO, QUANDO O USUÁRIO TENTA IMPRIMIR A GRU. <strong> </p>
	 */
	@Column(name = "id_gru_pagamento", nullable=true)
	private Integer idGRUPagamento;
	
	
	/** 
	 * <p>Indica o status do pagamento da inscriação do curso ou evento foi confirmado no sistema.</p> 
	 *
	 * <p>Geramente a confirmação do pagamento vai ser realizada automaticamente a partir de um timer.  @see {@link VerificaPagamentosCursosEventosExtensaoTimer} </p>
	 * 
	 * <p>Mas pode ser confirmado manualmente mediante apresentação da GRU paga pelo usuário caso o usuário não queria esperar a confirmação automático do pagamento.</p>
	 *
	 * <p> <strong>IMPORTANTE: ESSA INFORMAÇÃO SÓ É VÁLIDA PARA ATIVIDAS QUE COMBRAM TAXA, SE NÃO DEVE SER IGNORADA.</strong> </p>
	 *
	 */
	@Column(name = "status_pagamento", nullable=true)
	private StatusPagamentoInscricao statusPagamento;
	
	
	/** Modalidade escolhida pelo participante na realizaçãod a inscrição. 
	 *  É essa molalidade escolhida que define o valor a ser pago no curso ou evento.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modalidade_participante_periodo_inscricao_atividade", nullable=true)
	private ModalidadeParticipantePeriodoInscricaoAtividade molidadeParticipante;
	
	
//	/** 
//	 * Indica se a inscrição está ativa no sistema.
//	 */
//	private boolean ativo = true;
//    
//	
//	
	
    /** CPF do participante inscrito. */
    //private Long cpf;
    
    /** Passaporte do participante caso seja estrangeiro */
    //@Column(name = "passaporte", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
    //private String passaporte;

    /** Nome do participante inscrito. */
    //private String nome;

    /** Data de nascimento do participante inscrito. */
   // @Temporal(TemporalType.DATE)
    //@Column(name = "data_nascimento")
    //private Date dataNascimento;

 
    /** Logradouro do participante inscrito. */
    //private String logradouro;
    
    /** Número do participante inscrito. */
    //private String numero;

    /** Bairro do participante inscrito. */    
    //private String bairro;
    
    /** Município do participante inscrito. */
   // @ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "id_municipio")
    //private Municipio municipio = new Municipio();
    
    /** Unidade Federativa onde reside o participante inscrito. */
   // @ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "id_unidade_federativa")
    //private UnidadeFederativa unidadeFederativa;

    /** CEP do local onde reside o participante inscrito. */
    //private String cep;

    /** Email do participante inscrito */
    //private String email;
    
    /** Telefone do participante inscrito.*/
	//private String telefone;
	
	/** Celular do participante inscrito.*/
	//private String celular;

 
    
    /**
     * Senha para o usuário ter acesso a parte onde ele pode gerenciar as suas incrições
     * Essa senha é cridada pelo sistema e envia para o usuário por email no ato da inscrição. 
     */
    //private String senha;
    
    /** Código de acesso */
    //@Column(name="codigo_acesso")
    //private String codigoAcesso;

   
	
	
	 /** Guarda a GRU de forma temporária para exibir os dados para o usuário. */
    @Transient
    private GuiaRecolhimentoUniao gru;
    
    
    /** Guarda de forma temporária as respostas do usuário no momento da inscrição da ação de extensão. */
    @Transient
    private QuestionarioRespostas questionarioRespostas;
    
    /** Atributo utilizado para informar se o certificado está autorizado */
    @Transient
    private boolean autorizacaoCertificado;

    /** Atributo utilizado para informar se a declaração está autorizada */
    @Transient
    private boolean autorizacaoDeclaracao;
    
    /** Atributo utilizado para informar se está marcado */
    @Transient
	private boolean marcado;
    
    /** Atributo utilizado para informar se a inscrição é internacional */
    @Transient
    private boolean internacional;
    
    /** Atributo utilizado para representar o arquivo a ser anexado a inscrição na view. */
    @Transient
    private UploadedFile file;

	

    /**
     * Construtor padrão
     */
	public InscricaoAtividadeParticipante() {
		statusInscricao = new StatusInscricaoParticipante(StatusInscricaoParticipante.INSCRITO);
	}
	
	
	
	
	@Override
	public String toString() {
		return "InscricaoAtividadeParticipante [nome ="+ ( cadastroParticipante != null ? cadastroParticipante.getNome() : " " )
				+", "+"instituicao=" + instituicao
				+ ", dataCadastro=" + dataCadastro
				+ ", statusInscricao=" + statusInscricao
				+ ", statusPagamento=" + statusPagamento.getDescricao()
				+ "]";
	}




	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setStatusInscricao(StatusInscricaoParticipante statusInscricao) {
		this.statusInscricao = statusInscricao;
	}

	public StatusInscricaoParticipante getStatusInscricao() {
		return statusInscricao;
	}

	public void setInscricaoAtividade(InscricaoAtividade inscricaoAtividade) {
		this.inscricaoAtividade = inscricaoAtividade;
	}

	public InscricaoAtividade getInscricaoAtividade() {
		return inscricaoAtividade;
	}

	public String getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}


	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public boolean isAutorizacaoCertificado() {
		return autorizacaoCertificado;
	}

	public void setAutorizacaoCertificado(boolean autorizacaoCertificado) {
		this.autorizacaoCertificado = autorizacaoCertificado;
	}

	public boolean isAutorizacaoDeclaracao() {
		return autorizacaoDeclaracao;
	}

	public void setAutorizacaoDeclaracao(boolean autorizacaoDeclaracao) {
		this.autorizacaoDeclaracao = autorizacaoDeclaracao;
	}

	public void setMarcado(boolean marcado) {
		this.marcado = marcado;
	}

	public boolean isInternacional() {
	    return internacional;
	}

	public void setInternacional(boolean internacional) {
	    this.internacional = internacional;
	}

	public boolean isMarcado() {
		return marcado;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
//	/**
//	 * Método utilizado para informar o HashCode
//	 * @return
//	 */
//	public String getCodigoHash() {
//	    	if(isInternacional()){
//	    	return UFRNUtils.toSHA1Digest("IP" + getId() + getPassaporte());
//	    	}
//	    	else
//		return UFRNUtils.toSHA1Digest("IP" + getId() + getCpf());
//	}

	/**
	 * Método utilizao para validar os atributos
	 */
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if(this.getInscricaoAtividade() != null && this.getInscricaoAtividade().isCobrancaTaxaMatricula()){
			if(molidadeParticipante == null){
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Modalidade de Participação");	
			}
			
			if(valorTaxaMatricula == null){
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Valor da Taxa de Matrícula");
			}
		}
		
		if (inscricaoAtividade != null && inscricaoAtividade.isEnvioArquivoObrigatorio()) {
			if ( getFile() == null) {
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Arquivo");
			}
			ValidatorUtil.validateRequired(descricaoArquivo, "Descrição do Arquivo", lista);
		}
		
		if(StringUtils.isEmpty(instituicao)){
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Instituição");
		}

		return lista;
	}


	public void setParticipanteExtensao(ParticipanteAcaoExtensao participanteExtensao) {
		this.participanteExtensao = participanteExtensao;
	}

	public ParticipanteAcaoExtensao getParticipanteExtensao() {
		return participanteExtensao;
	}

//	public void setDiscente(Discente discente) {
//		this.discente = discente;
//	}
//
//	public Discente getDiscente() {
//		return discente;
//	}
//
//	public void setServidor(Servidor servidor) {
//		this.servidor = servidor;
//	}
//
//	public Servidor getServidor() {
//		return servidor;
//	}
	
	@Override
	public boolean equals(Object obj) {
	    return EqualsUtil.testEquals(this, obj, "id");
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}
	
	/** Informa se esta inscrição está cancelada. */
	public boolean isCancelado() {
		return statusInscricao.getId() == StatusInscricaoParticipante.CANCELADO;
	}

	public Integer getIdGRUPagamento() {
		return idGRUPagamento;
	}

	public void setIdGRUPagamento(Integer idGRUPagamento) {
		this.idGRUPagamento = idGRUPagamento;
	}

	public StatusPagamentoInscricao getStatusPagamento() {
		return statusPagamento;
	}

	public void setStatusPagamento(StatusPagamentoInscricao statusPagamento) {
		this.statusPagamento = statusPagamento;
	}

	public GuiaRecolhimentoUniao getGru() {
		return gru;
	}

	public void setGru(GuiaRecolhimentoUniao gru) {
		this.gru = gru;
	}

	public QuestionarioRespostas getQuestionarioRespostas() {
		return questionarioRespostas;
	}

	public void setQuestionarioRespostas(QuestionarioRespostas questionarioRespostas) {
		this.questionarioRespostas = questionarioRespostas;
	}

	public CadastroParticipanteAtividadeExtensao getCadastroParticipante() {
		return cadastroParticipante;
	}

	public void setCadastroParticipante(CadastroParticipanteAtividadeExtensao cadastroParticipante) {
		this.cadastroParticipante = cadastroParticipante;
	}

	public BigDecimal getValorTaxaMatricula() {
		return valorTaxaMatricula;
	}

	/***
	 * Retorna o valor da taxa de matrícula formata para exibição para o usuário de acordo 
	 * com o padrão: ¤ ###,###,##0.00 
	 *
	 * @return
	 */
	public String getValorTaxaMatriculaFormatado() {
		DecimalFormat formatador  = new DecimalFormat();
		formatador.applyPattern(PATTERN_DEFAULT);
		return formatador.format(valorTaxaMatricula);
	}
	
	public void setValorTaxaMatricula(BigDecimal valorTaxaMatricula) {
		this.valorTaxaMatricula = valorTaxaMatricula;
	}

	public ModalidadeParticipantePeriodoInscricaoAtividade getMolidadeParticipante() {
		return molidadeParticipante;
	}

	public void setMolidadeParticipante(ModalidadeParticipantePeriodoInscricaoAtividade molidadeParticipante) {
		this.molidadeParticipante = molidadeParticipante;
	}

	
}