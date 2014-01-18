/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/10/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;

/*******************************************************************************
 * Entidade que armazena os participantes de uma a��o de extens�o <br/>
 * 
 * Os participantes s�o, por exemplo, os alunos de um curso de extens�o, ou os
 * inscritos de um evento. P�blico alvo atendido.
 * 
 * @author Gleydson
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "participante_acao_extensao")
public class ParticipanteAcaoExtensao implements Validatable, Comparable<ParticipanteAcaoExtensao> {

	/** Atributo utilizado para representar o tipo de participante Discente da UFRN */
	public static final int DISCENTE_INTERNO = 1;
	/** Atributo utilizado para representar o tipo de participante Servidor da UFRN */
	public static final int SERVIDOR_INTERNO = 2;
	/** Atributo utilizado para representar o tipo de participante Outros */
	public static final int OUTROS = 3;
	///** Atributo utilizado para representar o tipo de participante Discente */
	//public static final int DISCENTES = 4;

	/** Atributo utilizado para representar o ID */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="extensao.participante_acao_extensao_sequence") })
	@Column(name = "id_participante_acao_extensao", unique = true, nullable = false)
	private int id;

	
	/** Representa o tipo de participa��o do participante. 
	 * 
	 *  Se for participante de uma inscri��o, todos vem como aluno, mas no cadastro o cooordenador pode cadastrar os outros tipo, ou mudar um tipo na existente. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_participacao")
	private TipoParticipacaoAcaoExtensao tipoParticipacao;
	

	
	/** 
	 *  <p>A inscri��o a partir da qual foi gerada esse participante. Essa associa��o era 
	 *  unidirecional, a inscri��o sabia quem era o participante, mas o participante n�o sabia de 
	 *  qual inscri��o veio. Agora vai ser direcional para ficar mais f�cil as consultas, j� que 
	 *  todas as informa��es do participantes s�o recuperadas pela inscri��o</p>
	 *  
	 *  <p>
	 *  <strong>***IMPORTANTE:***</strong> Os participantes podem ser cadastros diretamente mesmo sem t� associados a uma inscri��o. 
	 *  Ent�o nesse caso essa entidade � nula.
	 *  </p>
	 *    
	 *  <p><strong> ATRAV�S DESSA INSCRI��O PODEM SER RECUPERADOS OS DADOS DO PARTICIPANTE: CPF, NOME, EMAIL, ETC... </strong> </p>
	 *    
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_inscricao_atividade_participante", nullable=true)
	private InscricaoAtividadeParticipante inscricaoAtividadeParticipante;

	
	
	 /** 
     * <p>Os dados cadastrais dos participantes que se inscreveram no curso ou evento de extens�o.</p>
     * 
     * <p>Identifica o participante, pois ele deve passar a se cadastrar apenas 1 vez.</p>
     * 
     * <p>No caso dos participantes que n�o se inscreveram, a �nica forma de recuperar a informa��es � atravez dessa informa��o.</p>
     * 
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cadastro_participante_atividade_extensao", nullable=false)
    private CadastroParticipanteAtividadeExtensao cadastroParticipante;

	
    
    
    
    
	/////////////////////////////////////////////////////////////////////////////
	
	/** A atividade de extens�o onde o participante est� inscrito. 
	 *  <strong>***IMPORTANTE:***</strong> O participante n�o pode ser da atividade e da sub atividade ao mesmo tempo. OU � de uma ou � da outra. 
	 *  Existe uma constante no banco para isso.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_acao_extensao")
	private AtividadeExtensao atividadeExtensao;	
	

	/** A SubA��o de extens�o onde o participante est� inscrito.
	 *  <strong>***IMPORTANTE:***</strong> O participante n�o pode ser da atividade e da sub atividade ao mesmo tempo. OU � de uma ou � da outra. 
	 *  Existe uma constante no banco para isso. 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sub_atividade_extensao")
	private SubAtividadeExtensao subAtividade;

	
	/////////////////////////////////////////////////////////////////////////////

	
//	/** Guarda a informa��o do vinculo de discente que o usu�rio possui, se ele for discente da institui��o.  */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_discente")
//    private Discente discente;
//
//    
//    /**  Guarda a informa��o do vinculo de servidor que o usu�rio possui, se ele for servidor da institui��o. */
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "id_servidor")
//	private Servidor servidor;
	
	
	
	////////////////////////  Informa��es de auditoria  ////////////////////////////////
	
	/** Date de cadastro do participante na a��o */
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
	
	
	////////////////////////////////////////////////////////////////////////////////
	
	
	/** TODO n�o t� claro ainda como usar isso. Tipo de participante */
	@Column(name = "tipo_participante")
	private Integer tipoParticipante = DISCENTE_INTERNO; // valor padr�o

	
	/** Frequ�ncia do participante na a��o de extens�o. (  Informada pelo coordenador ) */
	private Integer frequencia = 0;
	
	
	/** Informa se o participante tem direito a emiss�o de certificado.  (  Informada pelo coordenador )  */
	@Column(name = "autorizacao_certificado")
	private boolean autorizacaoCertificado;

	
	/** Informa se o participante tem direito a emiss�o de declara��o. (  Informada pelo coordenador ) */
	@Column(name = "autorizacao_declaracao")
	private boolean autorizacaoDeclaracao;

	
	/** Atributo de texto livre para o coordenador inserir uma informa��o adicional que desejar que 
	 * seja impressa no certificado, como classifica��o ou premia��o.*/
	@Column(name = "observacao_certificado")
	private String observacaoCertificado;
	
	
	/** <p>Informa se o Participante esta ativo no sistema.</p>
	 *  <p>Um participante � removido do sistema, quando por exemplo, o coordenador aprovou a sua inscri��o e ele depois cancela a inscri��o.</p>
	 *  
	 *  <p>Participante removidos n�o aparecem na listagem para o coordendor, n�o entram em nenhuma contagem para a atividade.</p>
	 *  
	 */	
	private boolean ativo = true;
	
	
	
	/** Atributo utilizado para informar se o participante est� ou n�o selecionado */
	@Transient
	private boolean selecionado;

	/** Descreve o tipo da atividade ou sub-atividade de extens�o. */
	@Transient
	private String descricaoTipoAtividade;
	
	
	/** CPF do participante */
	//private Long cpf;
	
	/** Passaporte do participante (estrangeiro) */
	//@Column(name = "passaporte", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	//private String passaporte;

	/** Nome do participante */
	//private String nome;

	/** Data de nascimento do participante */
	//@Temporal(TemporalType.DATE)
	//@Column(name = "data_nascimento")
	//private Date dataNascimento;

	/** Institui��o onde esta lotado o participante */
	//private String instituicao;

	/** Endere�o do participante */
	//private String endereco;
	
    /** Munic�pio do participante inscrito. */
   // @ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "id_municipio")
   // private Municipio municipio = new Municipio();
    
    /** Unidade Federativa onde reside o participante inscrito. */
   // @ManyToOne(fetch = FetchType.EAGER)
	//@JoinColumn(name = "id_unidade_federativa")
   // private UnidadeFederativa unidadeFederativa;

	/** CEP onde reside o participante */
	//private String cep;

	/** Email do participante */
	//private String email;
	
	
	/**
	 * construtor Padr�o
	 */
	public ParticipanteAcaoExtensao() {
		//unidadeFederativa = new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO);
	}

	/**
	 * construtor Padr�o
	 */
	public ParticipanteAcaoExtensao(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public TipoParticipacaoAcaoExtensao getTipoParticipacao() {
		return tipoParticipacao;
	}

	public void setTipoParticipacao(
			TipoParticipacaoAcaoExtensao tipoParticipacao) {
		this.tipoParticipacao = tipoParticipacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * lista de tipos de participantes ver constantes.
	 * 
	 * DISCENTE_UFRN = 1, SERVIDOR_UFRN = 2, OUTROS = 3.
	 * 
	 * @return
	 */
	 public Integer getTipoParticipante() {
		 return tipoParticipante;
	 }

	 public void setTipoParticipante(Integer tipoParticipante) {
		 this.tipoParticipante = tipoParticipante;
	 }
	 
	 

	 /**
	  * Retorna a String correspondente ao c�digo do tipo de participante.
	  * 
	  * 
	  * @return
	  */
	 public String getTipoParticipanteString() {
		 String result = "";
		 switch (tipoParticipante) {
		 case DISCENTE_INTERNO:
			 result = "DISCENTE ("+RepositorioDadosInstitucionais.getSiglaInstituicao()+")";
			 break;
		 case SERVIDOR_INTERNO:
			 result = "SERVIDOR ("+RepositorioDadosInstitucionais.getSiglaInstituicao()+")";
			 break;
		 case OUTROS:
			 result = "OUTROS (COMUNIDADE EXTERNA)";
			 break;
		 default:
			 result = "N�O INFORMADO";
			 break;
		 }
		 return result;
	 }

	 public ListaMensagens validate() {

		 ListaMensagens lista = new ListaMensagens();
		 
		 
		 if(ValidatorUtil.isEmpty(atividadeExtensao) && ValidatorUtil.isEmpty(subAtividade)) {
			 lista.addErro("Participante n�o foi associado a uma a��o de exten��o ou a uma mini atividade.");
		 }		 
		 
		 ValidatorUtil.validateRequired(tipoParticipacao,"Tipo de Participa��o", lista);
		 ValidatorUtil.validateRequired(tipoParticipante,"Tipo de Participante", lista);
		 ValidatorUtil.validateMaxValue(frequencia, 100, "Frequ�ncia", lista);
		 ValidatorUtil.validateMinValue(frequencia, 0, "Frequ�ncia", lista);

		 return lista;
	 }

	 public Integer getFrequencia() {
		 return frequencia;
	 }

	 public void setFrequencia(Integer frequencia) {
		 this.frequencia = frequencia;
	 }

	 public boolean isAutorizacaoCertificado() {
		 return autorizacaoCertificado;
	 }

	 public void setAutorizacaoCertificado(boolean autorizacaoCertificado) {
		 this.autorizacaoCertificado = autorizacaoCertificado;
	 }
	 
	 
	 /**
	  * Compara os participantes por nome
	  * 
	  */
	 public int compareTo(ParticipanteAcaoExtensao o) {
		 //return StringUtils.toAscii(this.getNome()).toLowerCase().compareTo(StringUtils.toAscii(o.getNome()).toLowerCase());
		 return 0;
	 }

	 public boolean isAutorizacaoDeclaracao() {
		 return autorizacaoDeclaracao;
	 }

	 public void setAutorizacaoDeclaracao(boolean autorizacaoDeclaracao) {
		 this.autorizacaoDeclaracao = autorizacaoDeclaracao;
	 }

	 /**
	  * Retorna a carga hor�ria que dever� ser informada no certificado/declara��o do participante.
	  * Resultado do produto da carga hor�ria e a freq��ncia informado pelo coordenador da a��o. 
	  * 
	  * @return
	  */
	 public Integer getChCertificadoDeclaracao() {
		 if ((atividadeExtensao != null) && (atividadeExtensao.getCursoEventoExtensao() != null)) {
			 return new Double(atividadeExtensao.getCursoEventoExtensao().getCargaHoraria() * (frequencia / 100.0)).intValue();
		 } else if( ! ValidatorUtil.isEmpty(getSubAtividade()) ) {
			 return new Double(getSubAtividade().getCargaHoraria() * (frequencia / 100.0)).intValue();
		 }
		 return 0;
	 }

	 /**
	  * Retorna a carga hor�ria total da atividade.
	  * N�o invocada por JSP.
	  * @return
	  */
	 public Integer getChTotalCertificadoDeclaracao(){
		 if ((atividadeExtensao != null) && (atividadeExtensao.getCursoEventoExtensao() != null)) {
			 return new Double(atividadeExtensao.getCursoEventoExtensao().getCargaHoraria() ).intValue();
		 } else if( ! ValidatorUtil.isEmpty(getSubAtividade()) ) {
			 return new Double(getSubAtividade().getCargaHoraria()).intValue();
		 }
		 return 0;
	 }

	 //// Metodos para serem chamados na interface gr�fica para habilitar ou desabilitar alguma coisa ////
	 
	/**
	  * <p>Informa se participante pode emitir certificado.</p>
	  * 
	  * <p>S� pode emitir o certificado se e semente se: </p>
	  *   <ol>
	  *    <li>o projeto est� conclu�do e atividade ou sub atividade finalizada.</li>
	  *    <li>ou N�O est� finalizado MAS o gestor do projeto autorizou emitir certificado antes do tempo</li>
	  *    <li>o participante possuir frequencia</li>
	  *    <li>o coordenador autorizou a emiss�o</li>
	  *  </ol>
	  * @return
	 * @throws NegocioException 
	  */
	 public boolean isPassivelEmissaoCertificadoParticipante() throws NegocioException {
		  
		 try {
			verificaEmissaoCertificadoParticipante();
			return true;  // se chegou at� aqui sem lan�ar nenhuma exce��o � porque pode emitir
		} catch (NegocioException e) {
			return false;
		}
	 }	
	 
	 /**
	  * <p>Informa se coordenador pode emitir certificado.</p>
	  * 
	  * <p>S� pode emitir o certificado se e semente se: </p>
	  *   <ol>
	  *    <li>o projeto est� conclu�do e atividade ou sub atividade finalizada.</li>
	  *    <li>ou N�O est� finalizado MAS o gestor do projeto autorizou emitir certificado antes do tempo</li>
	  *    <li>o participante possuir frequencia</li>
	  *  </ol>
	  * @return
	  */
	 public boolean isPassivelEmissaoCertificadoCoordenador() {
		 try {
			 	verificaEmissaoCertificadoCoordenador();
				return true;  // se chegou at� aqui sem lan�ar nenhuma exce��o � porque pode emitir
		 } catch (NegocioException e) {
				return false;
		}
	 }
	
	 
	 /**
	  * <p>Informa se o participante do projeto pode emitir declara��o.</p> 
	  * 
	  * <p>S� pode emitir o declara��o se e semente se: </p>
	  *   <ol>
	  *    <li> Atividade ou sub atividade N�O est� finalizada</li>
	  *    <li>o participante foi autorizado a emitir certificao pelo coordenador.</li>
	  *  </ol>
	  * 
	  * @return
	  */
	 public boolean isPassivelEmissaoDeclaracaoParticipante()  throws NegocioException {
		 
	 	try {
	 		verificaEmissaoDeclaracaoParticipante();
			return true;  // se chegou at� aqui sem lan�ar nenhuma exce��o � porque pode emitir
		} catch (NegocioException e) {
			return false;
		}
	 }
	
	 
	 /**
	  * <p>Informa se o coordenador do projeto pode emitir declara��o.</p> 
	  * 
	  * <p>S� pode emitir o declara��o se e semente se: </p>
	  *   <ol>
	  *    <li>Atividade ou sub atividade N�O est� finalizada</li>
	  *  </ol>
	  * 
	  * @return
	  */
	 public boolean isPassivelEmissaoDeclaracaoCoordenador() {
		 
		try {
			verificaEmissaoDeclaracaoCoordenador();
			return true;  // se chegou at� aqui sem lan�ar nenhuma exce��o � porque pode emitir
		} catch (NegocioException e) {
			return false;
		}
		 
	 }
	 
	 
	 //// Metodos para serem chamados na hora da emiss�o para mostrar a mensagem para o usu�rio. ////
	 
	 /**
	  * <p>Informa se participante pode emitir certificado.</p>
	  * 
	  * <p>S� pode emitir o certificado se e semente se: </p>
	  *   <ol>
	  *    <li>o projeto est� conclu�do e atividade ou sub atividade finalizada.</li>
	  *    <li>ou N�O est� finalizado MAS o gestor do projeto autorizou emitir certificado antes do tempo</li>
	  *    <li>o participante possuir frequencia</li>
	  *    <li>o coordenador autorizou a emiss�o</li>
	  *  </ol>
	  * @return
	 * @throws NegocioException 
	  */
	 public void verificaEmissaoCertificadoParticipante() throws NegocioException {
		  
		verificaEmissaoCertificadoCoordenador();
		 
		if(! this.isAutorizacaoCertificado() ){
			throw new NegocioException("O coordenador da a��o ainda n�o autorizou a emiss�o de certificados.");
		}
		 
	 }	
	 
	
	 
	 /**
	  * <p>Informa se coordenador pode emitir certificado.</p>
	  * 
	  * <p>S� pode emitir o certificado se e semente se: </p>
	  *   <ol>
	  *    <li>o projeto est� conclu�do e atividade ou sub atividade finalizada.</li>
	  *    <li>ou N�O est� finalizado MAS o gestor do projeto autorizou emitir certificado antes do tempo</li>
	  *    <li>o participante possuir frequencia</li>
	  *  </ol>
	  * @return
	  */
	 public void verificaEmissaoCertificadoCoordenador()  throws NegocioException  {
		 
		
		 
		 if( isParticipanteAtividadeExtensao() ) {
			 
			 Integer frequenciaMinima = 0;
			 
			 // Segundo a resolu��o 053/2008 do CONSEPE:  Para cursos a frequ�ncia m�nima seria de 75%
			 if(this.getAtividadeExtensao().getTipoAtividadeExtensao().isCurso())
				 frequenciaMinima = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.FREQUENCIA_MINIMA_CERTIFICADOS_CURSOS_EXTENSAO);
			 
			 // Segundo a resolu��o 053/2008 do CONSEPE:  Para eventos a frequ�ncia m�nima seria de 90%
			 if(this.getAtividadeExtensao().getTipoAtividadeExtensao().isEvento())
				 frequenciaMinima = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.FREQUENCIA_MINIMA_CERTIFICADOS_EVENTOS_EXTENSAO);
			 
			 if(frequenciaMinima == null)  frequenciaMinima = 0;
			 
			 if( this.getFrequencia() == null || this.getFrequencia() <= 0){
				 throw new NegocioException("O participante ainda n�o possui frequ�ncia para emiss�o de certificados.");
			 }else{
				 if(this.getFrequencia() < frequenciaMinima){
					 throw new NegocioException("O participante deve possuir pelo menos uma frequ�ncia m�nima de: "+frequenciaMinima+"%"+" para emiss�o de certificados.");
				 }else{
					 if( ! this.getAtividadeExtensao().isFinalizada() && ! this.getAtividadeExtensao().getProjeto().isAutorizarCertificadoGestor() ){
						 throw new NegocioException("O certificado de extens�o s� pode ser emitido para atividades finalizadas.");
					 }else{
						 if(! this.getAtividadeExtensao().getProjeto().isConcluido()  && ! this.getAtividadeExtensao().getProjeto().isAutorizarCertificadoGestor() ){
							 throw new NegocioException("O certificado de extens�o s� pode ser emitido para projetos conclu�dos.");
						 }
					 }
				 }
			 }
			 
			
		 } else {
			 
			 Integer frequenciaMinima = 0;
			 
			 // Segundo a resolu��o 053/2008 do CONSEPE:  Para cursos a frequ�ncia m�nima seria de 75%
			 if( this.getSubAtividade().getAtividade().getTipoAtividadeExtensao().isCurso())
				 frequenciaMinima = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.FREQUENCIA_MINIMA_CERTIFICADOS_CURSOS_EXTENSAO);
			 
			 // Segundo a resolu��o 053/2008 do CONSEPE:  Para eventos a frequ�ncia m�nima seria de 90%
			 if( this.getSubAtividade().getAtividade().getTipoAtividadeExtensao().isEvento())
				 frequenciaMinima = ParametroHelper.getInstance().getParametroInt(ParametrosExtensao.FREQUENCIA_MINIMA_CERTIFICADOS_EVENTOS_EXTENSAO);
			 
			 if(frequenciaMinima == null)  frequenciaMinima = 0;
			 
			 if( this.getFrequencia() == null || this.getFrequencia() <= 0){
				 throw new NegocioException("O participante ainda n�o possui frequ�ncia para emiss�o de certificados.");
			 }else{
				 if(this.getFrequencia() < frequenciaMinima){
					 throw new NegocioException("O participante deve possuir pelo menos uma frequ�ncia m�nima de: "+frequenciaMinima+"%"+" para emiss�o de certificados.");
				 }else{
					 if( ! this.getSubAtividade().isFinalizada() && ! this.getSubAtividade().getAtividade().getProjeto().isAutorizarCertificadoGestor() ){
						 throw new NegocioException("O certificado de extens�o s� pode ser emitido para mini atividades finalizadas.");
					 }
				 }
			 }
			 	
		 }
	 }	
	 
	 
	
	 
	 
	 /**
	  * <p>Informa se o participante do projeto pode emitir declara��o.</p> 
	  * 
	  * <p>S� pode emitir o declara��o se e semente se: </p>
	  *   <ol>
	  *    <li> Atividade ou sub atividade N�O est� finalizada</li>
	  *    <li>o participante foi autorizado a emitir certificao pelo coordenador.</li>
	  *  </ol>
	  * 
	  * @return
	  */
	 public void verificaEmissaoDeclaracaoParticipante()  throws NegocioException {
		 
		 verificaEmissaoDeclaracaoCoordenador();
		 
		if( ! this.isAutorizacaoDeclaracao() ){
			 throw new NegocioException("O coordenador da a��o ainda n�o autorizou a emiss�o de declara��es.");
		}
		
	 }
	
	
	 /**
	  * <p>Informa se o coordenador do projeto pode emitir declara��o.</p> 
	  * 
	  * <p>S� pode emitir o declara��o se e semente se: </p>
	  *   <ol>
	  *    <li>Atividade ou sub atividade N�O est� finalizada</li>
	  *  </ol>
	  * 
	  * @return
	  */
	 public void verificaEmissaoDeclaracaoCoordenador() throws NegocioException {
		 
		 if(this.isParticipanteAtividadeExtensao() ) {
			 if( this.getAtividadeExtensao().isFinalizada() ){
				 throw new NegocioException("A declara��o de extens�o s� pode ser emitida para atividades n�o finalizadas.");
			 }
		 } else {
			 if( this.getSubAtividade().isFinalizada() ){
				 throw new NegocioException("A declara��o de extens�o s� pode ser emitida para atividades n�o finalizadas.");
			 }
		 }
	 }
	 
	 
	 /**retorna uma expressao no formato (codigo daa��o)-(titulo da a��o)*/
	 @Transient
	 public String getCodigoTitulo() {
		if ( !ValidatorUtil.isEmpty(subAtividade) )
			return subAtividade.getTitulo();
		else
			return atividadeExtensao.getCodigoTitulo();
     }
	 
	 /////////////////////////////////////////////////////////////////////////////////////////////////
	 
	 
	@Override
	public int hashCode() {
		 return super.hashCode();
	}

	public boolean isSelecionado() {
	    return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
	    this.selecionado = selecionado;
	}
	
	/** Verifica se  o participante � de uma atividade ou de uma sub atividade. N�O PODE SER DOS DOIS AO MEMSO TEMPO. */
	public boolean isParticipanteAtividadeExtensao() {
		return atividadeExtensao != null && subAtividade == null;
	}
	
	/** Verifica se o participante � de uma atividade ou de uma sub atividade. N�O PODE SER DOS DOIS AO MEMSO TEMPO. */
	public boolean isParticipanteSubAtividadeExtensao() {
		return atividadeExtensao == null && subAtividade != null;
	}
	

//	public boolean isInternacional() {
//	    return internacional;
//	}
//
//	public void setInternacional(boolean internacional) {
//	    this.internacional = internacional;
//	}

//	public String getCep() {
//		return cep;
//	}
//
//	public void setCep(String cep) {
//		this.cep = cep;
//	}
//	
//	public UnidadeFederativa getUnidadeFederativa() {
//		return unidadeFederativa;
//	}
//
//	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
//		this.unidadeFederativa = unidadeFederativa;
//	}
//
//	public Municipio getMunicipio() {
//		return municipio;
//	}
//
//	public void setMunicipio(Municipio municipio) {
//		this.municipio = municipio;
//	}

//	public SubAtividadeExtensao getSubAtividade() {
//		return subAtividade;
//	}
//
//	public void setSubAtividade(SubAtividadeExtensao subAtividade) {
//		this.subAtividade = subAtividade;
//	}

	public String getObservacaoCertificado() {
		return observacaoCertificado;
	}

	public void setObservacaoCertificado(String observacaoCertificado) {
		this.observacaoCertificado = observacaoCertificado;
	}

	public AtividadeExtensao getAtividadeExtensao() {
		return atividadeExtensao;
	}

	public void setAtividadeExtensao(AtividadeExtensao atividadeExtensao) {
		this.atividadeExtensao = atividadeExtensao;
	}

	public SubAtividadeExtensao getSubAtividade() {
		return subAtividade;
	}

	public void setSubAtividade(SubAtividadeExtensao subAtividade) {
		this.subAtividade = subAtividade;
	}

	public InscricaoAtividadeParticipante getInscricaoAtividadeParticipante() {
		return inscricaoAtividadeParticipante;
	}

	public void setInscricaoAtividadeParticipante(InscricaoAtividadeParticipante inscricaoAtividadeParticipante) {
		this.inscricaoAtividadeParticipante = inscricaoAtividadeParticipante;
	}

	public CadastroParticipanteAtividadeExtensao getCadastroParticipante() {
		return cadastroParticipante;
	}

	public void setCadastroParticipante(CadastroParticipanteAtividadeExtensao cadastroParticipante) {
		this.cadastroParticipante = cadastroParticipante;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public String getDescricaoTipoAtividade() {
		return descricaoTipoAtividade;
	}

	public void setDescricaoTipoAtividade(String descricaoTipoAtividade) {
		this.descricaoTipoAtividade = descricaoTipoAtividade;
	}
	
	
	
}
