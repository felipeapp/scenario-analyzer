/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * <p>Representa as inscri��es online para participantes em Cursos ou Eventos de Extens�o atrav�s do portal p�blico do SIGAA. 
 * As inscri��es s�o disponibilizadas dentro de um per�odo de inscri��o definido para que membros externos ou internos � 
 * institui��o possam participar das atividades desenvolvidas pela UFRN.</p>
 * 
 * <p>Cada nova inscri��o realizada por uma pessoa � criada uma nova entidade dessa no banco. </p>
 * 
 * <p> <strong> 
 * ***************************************************************************************<br/>
 * Deve guardar apenas as informa��es de uma inscri��o espec�fica de um participante. 
 * Os dados comuns do participante s�o criados apenas 1 vez na entidade <code>CadastroParticipanteAtividadeExtensao</code>
 * para evitar duplica��es e o usu�rio ter que informar todos os seus dados pessoas novamente a cada inscri��o.
 * ***************************************************************************************<br/> 
 * </strong>
 * </p>
 * 
 * @author Daniel Augusto
 * @author Jadson
 * @version 2.0 - Jadson - Removendo tudo que � informa��es pessoais do participante, essa entidade 
 * deve guardar apenas os dados exclusivos de uma inscri��o do da pessoa.
 */
@Entity
@Table(schema = "extensao", name = "inscricao_atividade_participante")
public class InscricaoAtividadeParticipante implements Validatable {

	/**
	 * O padr�o utilizado no sistema para valores monet�rios da multa
	 */
	public static final String PATTERN_DEFAULT = "� ###,###,##0.00";
	
	
	/** Atributo utilizado para representar o ID da inscri��o */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="extensao.inscricao_atividade_participante_sequence") })
	@Column(name = "id_inscricao_atividade_participante", unique = true, nullable = false)
    private int id;
	
	
	/** Institui��o do participante inscrito, informada no momento da inscri��o. 
	 * Ele pode se inscre como institui��es diferentes em cursos ou eventos diferentes.*/
	@Column(name = "instituicao", nullable=false)
	private String instituicao;

	////////////////////////Informa��es de auditoria  ////////////////////////////////
    /** Data da inscri��o. */
    @CriadoEm
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    private Date dataCadastro;
    
    
    /** Registro de entrada do respons�vel pelo cadastro do participante na a��o de extens�o. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	
	/** Data de cadastro. */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_ultima_atualizacao")
	private Date dataUltimaAtualizacao;

	/** Registro de entrada do respons�vel pelo cadastro da inscri��o. */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	private RegistroEntrada registroUltimaAtualizacao;

	
	/** Registro de entrada do respons�vel pelo estorno do pagamento. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_estorno")
	private RegistroEntrada registroEstorno;
	
	/** Registro de estorno do pagamento. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_estorno")
	private Date dataEstorno;
	
	////////////////////////Informa��es de auditoria  ////////////////////////////////
    
    
//    /** Guarda a informa��o do vinculo de discente que o usu�rio possui, se ele for discente da institui��o. */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_discente")
//    private Discente discente;
//
//    /** Guarda a informa��o do vinculo de servidor que o usu�rio possui, se ele for servidor da institui��o. */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_servidor")
//    private Servidor servidor;
    
    /** Situa��o da inscri��o do participante */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status_inscricao_participante", nullable=false)
    private StatusInscricaoParticipante statusInscricao;
    
    /**  
     * <p>Partipande Extens�o criado a partir dessa inscri��o. </p>
     * 
     *  <p>Quando o coordenador confirma a inscri��o do usu�rio, � criado uma participante
     *  na atividade de extens�o para qual feita a inscri��o, a refer�ncia � mantida aqui. </p>
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_participante_acao_extensao", nullable=true)
    private ParticipanteAcaoExtensao participanteExtensao;
    
    
    
    /** Para qual per�odo de inscri��o foi feita a inscri��o do participante, liga a inscri��o com os dados do curso ou evento. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscricao_atividade")
    private InscricaoAtividade inscricaoAtividade = new InscricaoAtividade();
    
    
    /** 
     * <p>Os dados cadastrais dos participantes que se inscreveram no curso ou evento de extens�o.</p>
     * 
     * <p>Identifica o participante, pois ele deve passar a se cadastrar apenas 1 vez.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cadastro_participante_atividade_extensao", nullable=false)
    private CadastroParticipanteAtividadeExtensao cadastroParticipante;
    
    
    
	/** Arquivo que pode ser associado a inscri��o. Permite que o candidato envie trabalhos, curr�culos, comprovantes,  etc. */
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
	
	/** Descri��o do arquivo informada pelo usu�rio no momento de se inscrever. */
	@Column(name = "descricao_arquivo")
	private String descricaoArquivo;	
    
	
	
	
	/**  O valor a ser pago pelo usu�rio quando h� cobran�a de taxa. 
	 *   Gerado a partir da modalidade de participante escolhida pelo usu�rio. 
	 */
	@Column(name = "valor_taxa_matricula", nullable=true)
	private BigDecimal valorTaxaMatricula = new BigDecimal(0);
	
	
	/** 
	 *  <p> Guarda uma refer�ncia a GRU que foi gerada para pagamento dessa inscri��o.</p>
	 *  
	 *  <p> <strong>N�o tem um mapeamento com GuiaRecolhimentoUniao porque essa informa��o est� no bando comum. <strong> </p>
	 *  <br/>
	 *  <p> <strong> A GRU S� � CRIADA E SEU ID SALVO, QUANDO O USU�RIO TENTA IMPRIMIR A GRU. <strong> </p>
	 */
	@Column(name = "id_gru_pagamento", nullable=true)
	private Integer idGRUPagamento;
	
	
	/** 
	 * <p>Indica o status do pagamento da inscria��o do curso ou evento foi confirmado no sistema.</p> 
	 *
	 * <p>Geramente a confirma��o do pagamento vai ser realizada automaticamente a partir de um timer.  @see {@link VerificaPagamentosCursosEventosExtensaoTimer} </p>
	 * 
	 * <p>Mas pode ser confirmado manualmente mediante apresenta��o da GRU paga pelo usu�rio caso o usu�rio n�o queria esperar a confirma��o autom�tico do pagamento.</p>
	 *
	 * <p> <strong>IMPORTANTE: ESSA INFORMA��O S� � V�LIDA PARA ATIVIDAS QUE COMBRAM TAXA, SE N�O DEVE SER IGNORADA.</strong> </p>
	 *
	 */
	@Column(name = "status_pagamento", nullable=true)
	private StatusPagamentoInscricao statusPagamento;
	
	
	/** Modalidade escolhida pelo participante na realiza��od a inscri��o. 
	 *  � essa molalidade escolhida que define o valor a ser pago no curso ou evento.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modalidade_participante_periodo_inscricao_atividade", nullable=true)
	private ModalidadeParticipantePeriodoInscricaoAtividade molidadeParticipante;
	
	
//	/** 
//	 * Indica se a inscri��o est� ativa no sistema.
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
    
    /** N�mero do participante inscrito. */
    //private String numero;

    /** Bairro do participante inscrito. */    
    //private String bairro;
    
    /** Munic�pio do participante inscrito. */
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
     * Senha para o usu�rio ter acesso a parte onde ele pode gerenciar as suas incri��es
     * Essa senha � cridada pelo sistema e envia para o usu�rio por email no ato da inscri��o. 
     */
    //private String senha;
    
    /** C�digo de acesso */
    //@Column(name="codigo_acesso")
    //private String codigoAcesso;

   
	
	
	 /** Guarda a GRU de forma tempor�ria para exibir os dados para o usu�rio. */
    @Transient
    private GuiaRecolhimentoUniao gru;
    
    
    /** Guarda de forma tempor�ria as respostas do usu�rio no momento da inscri��o da a��o de extens�o. */
    @Transient
    private QuestionarioRespostas questionarioRespostas;
    
    /** Atributo utilizado para informar se o certificado est� autorizado */
    @Transient
    private boolean autorizacaoCertificado;

    /** Atributo utilizado para informar se a declara��o est� autorizada */
    @Transient
    private boolean autorizacaoDeclaracao;
    
    /** Atributo utilizado para informar se est� marcado */
    @Transient
	private boolean marcado;
    
    /** Atributo utilizado para informar se a inscri��o � internacional */
    @Transient
    private boolean internacional;
    
    /** Atributo utilizado para representar o arquivo a ser anexado a inscri��o na view. */
    @Transient
    private UploadedFile file;

	

    /**
     * Construtor padr�o
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
//	 * M�todo utilizado para informar o HashCode
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
	 * M�todo utilizao para validar os atributos
	 */
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if(this.getInscricaoAtividade() != null && this.getInscricaoAtividade().isCobrancaTaxaMatricula()){
			if(molidadeParticipante == null){
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Modalidade de Participa��o");	
			}
			
			if(valorTaxaMatricula == null){
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Valor da Taxa de Matr�cula");
			}
		}
		
		if (inscricaoAtividade != null && inscricaoAtividade.isEnvioArquivoObrigatorio()) {
			if ( getFile() == null) {
				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Arquivo");
			}
			ValidatorUtil.validateRequired(descricaoArquivo, "Descri��o do Arquivo", lista);
		}
		
		if(StringUtils.isEmpty(instituicao)){
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Institui��o");
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
	
	/** Informa se esta inscri��o est� cancelada. */
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
	 * Retorna o valor da taxa de matr�cula formata para exibi��o para o usu�rio de acordo 
	 * com o padr�o: � ###,###,##0.00 
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