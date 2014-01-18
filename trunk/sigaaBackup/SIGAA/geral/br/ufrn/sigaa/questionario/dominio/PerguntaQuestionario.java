/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Nov 14, 2007
 *
 */
package br.ufrn.sigaa.questionario.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validaInt;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.avaliacao.dominio.AlternativaPergunta;

/**
 * Esta entidade é responsável pelas perguntas de um questionário
 *
 * @author Victor Hugo
 */
@Entity
@Table(name = "pergunta", schema = "questionario")
public class PerguntaQuestionario implements Validatable, Cloneable{

	/**
	 * constantes dos tipos de pergunta
	 */
	public static final int UNICA_ESCOLHA = 1; /** o usuário escolhe uma opção entre varias */
	public static final int MULTIPLA_ESCOLHA = 2; /** o usuário pode escolher mais de uma opção */
	public static final int VF = 3; /** resposta verdadeiro ou falso */
	public static final int DISSERTATIVA = 4; /** resposta dissertativa */
	public static final int NUMERICA = 5; /** resposta numérica */
	public static final int UNICA_ESCOLHA_ALTERNATIVA_PESO = 6; /** igual a UNICA_ESCOLHA, mas cada opção tem um peso */
	public static final int ARQUIVO = 7; /** define um arquivo para anexar */

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="questionario.pergunta_questionario_sequence") })
	@Column(name = "id_pergunta", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
	 * tipo da pergunta
	 */
	private int tipo;

	/**
	 * texto da pergunta
	 */
	private String pergunta;

	/**
	 * indica a ordem que a pergunta irá aparecer no questionário. deve iniciar de 1
	 */
	private int ordem;

	/** Questionário a qual a pergunta faz parte. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_questionario", unique = false, nullable = true, insertable = true, updatable = true)
	private Questionario questionario;

	/**
	 * coleção de respostas para caso o tipo da pergunta seja MULTIPLA_ESCOLHA OU MULTIPLA_ESCOLHA_MULTIPLA
	 */
	@OrderBy("ordem")
	@OneToMany(mappedBy = "pergunta", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<Alternativa> alternativas;

	/**
	 * respostas da pergunta
	 */
	@Column(name="gabarito_dissertativa")
	private String gabaritoDissertativa;

	/** Resposta Numérica da pergunta */
	@Column(name="gabarito_numerica")
	private Float gabaritoNumerica;

	/** Armazena a resposta verdadeira ou falsa da pergunta */
	@Column(name="gabarito_vf")
	private Boolean gabaritoVf;

	/** registro entrada do usuário que cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** data que foi cadastrado */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoEm
	private Date dataCadastro;

	/** registro entrada do usuário que desativou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** data que esta pergunta foi desativada */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Informação sobre se a pergunta está em vigor */
	private boolean ativo = true;
	
	/** Se a pergunta é de preenchimento obrigatório */
	private boolean obrigatoria = false;

	/**
	 * Indica o tamanho máximo de caracteres para resposta.
	 * Utilizado somente quando a a pergunta for do tipo Texto.
	 */
	@Column(name="max_caracteres")
	private Integer maxCaracteres;
	
	public PerguntaQuestionario() {
		ordem = 1;
	}
	
	public PerguntaQuestionario(int id) {
		this();
		this.id = id;
	}
	
	public PerguntaQuestionario(int tipo, String pergunta) {
		this();
		this.tipo = tipo;
		this.pergunta = pergunta;
	}
	
	
	/** Verifica se o questionário contém uma pergunta específica */
	public boolean containsAlternativa(Alternativa alternativa) {
		if(alternativas == null)
			return false;
		else{
			return alternativas.contains(alternativa);
		}
	}
	
	/** Adiciona um pergunta ao questionário */
	public void adicionaAlternativa(Alternativa alternativa) {
		if(alternativas == null)
			alternativas = new ArrayList<Alternativa>();
		
		alternativa.setPergunta(this);
		alternativas.add(alternativa);
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTipo() {
		return tipo;
	}

	/** Retorna a descrição do tipo de pergunta */
	public String getTipoString() {
		switch (tipo) {
			case DISSERTATIVA:
				return "Dissertativa";
			case NUMERICA:
				return "Numérica";
			case VF:
				return "Verdadeiro ou falso";
			case MULTIPLA_ESCOLHA:
				return "Multipla escolha";
			case UNICA_ESCOLHA:
				return "Única escolha";
			case UNICA_ESCOLHA_ALTERNATIVA_PESO:
				return "Única escolha com peso na alternativa";			
			case ARQUIVO:
				return "Arquivo";	
			default:
				return "Indefinido";
		}
	}

	/**
	 * atualiza o atributo ordem das alternativas para na sequência que está na lista iniciando de 1
	 */
	public void atualizarOrdemAlternativas(){
		if( (isMultiplaEscolha() || isUnicaEscolha() || isUnicaEscolhaAlternativaPeso()) && alternativas != null ){
			for( int i = 0; i < alternativas.size(); i++ ){
				alternativas.get(i).setOrdem(i+1);
			}
		}
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public List<Alternativa> getAlternativas() {
		return alternativas;
	}

	public void setAlternativas(List<Alternativa> alternativas) {
		this.alternativas = alternativas;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public String getGabaritoDissertativa() {
		return gabaritoDissertativa;
	}

	public void setGabaritoDissertativa(String gabaritoDissertativa) {
		this.gabaritoDissertativa = gabaritoDissertativa;
	}

	public Float getGabaritoNumerica() {
		return gabaritoNumerica;
	}

	public void setGabaritoNumerica(Float gabaritoNumerica) {
		this.gabaritoNumerica = gabaritoNumerica;
	}

	public Boolean getGabaritoVf() {
		return gabaritoVf;
	}

	public void setGabaritoVf(Boolean gabaritoVf) {
		this.gabaritoVf = gabaritoVf;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isVf(){
		return tipo == VF;
	}

	public boolean isUnicaEscolha(){
		return tipo == UNICA_ESCOLHA;
	}

	public boolean isUnicaEscolhaAlternativaPeso(){
		return tipo == UNICA_ESCOLHA_ALTERNATIVA_PESO;
	}
	
	public boolean isMultiplaEscolha(){
		return tipo == MULTIPLA_ESCOLHA;
	}

	public boolean isDissertativa(){
		return tipo == DISSERTATIVA;
	}

	public boolean isNumerica(){
		return tipo == NUMERICA;
	}

	public boolean isMultiplaOuUnicaEscolha(){
		return isMultiplaEscolha() || isUnicaEscolha() || isUnicaEscolhaAlternativaPeso();
	}
	
	public boolean isArquivo(){
		return tipo == ARQUIVO;
	}

	public boolean isObrigatoria() {
		return obrigatoria;
	}

	public void setObrigatoria(boolean obrigatoria) {
		this.obrigatoria = obrigatoria;
	}

	
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	
	
	/**
	 * diz se a pergunta tem gabarito
	 * @return
	 */
	public boolean hasGabarito(){
		if( isDissertativa() )
			return !isEmpty( gabaritoDissertativa );
		if( isNumerica() )
			return !isEmpty( gabaritoNumerica );
		if( isVf() )
			return !isEmpty( gabaritoVf);
		if( isMultiplaOuUnicaEscolha() ){
			if( !isEmpty(alternativas) ){
				for( Alternativa a : alternativas ){
					if( a.isGabarito() )
						return true;
				}
			}
			return false;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<AlternativaPergunta> getAlternativasValidas() {
		return CollectionUtils.select(alternativas,new Predicate() {
			public boolean evaluate(Object alternativa) {
				return alternativa != null && ((Alternativa) alternativa).isAtivo();
			}
		});
	}
	
	@Override
	public String toString() {
		return pergunta;
	}

	/**
	 * valida o cadastro de uma pergunta
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		Collection<MensagemAviso> lista = erros.getMensagens();
		
		validateRequiredId( tipo, "Tipo de Pergunta", lista);
		validateRequired( pergunta, "Pergunta", lista);
		
		if (isDissertativa() || isNumerica())
			validaInt(maxCaracteres, "Quantidade Máxima de Caracter" , lista);

		if ( isMultiplaOuUnicaEscolha() && (isEmpty( alternativas ) || alternativas.size() < 2) ) {
			erros.addErro("É necessário informar pelo menos duas alternativas para esta pergunta");
		}
		
		return erros;
	}

	@Override
	public PerguntaQuestionario clone() {
		PerguntaQuestionario p = null;
		try {
			p = (PerguntaQuestionario) super.clone();
			
			List<Alternativa> alternativasTemp = null;
			for (Alternativa a : alternativas) {
				if (alternativasTemp == null)
					alternativasTemp = new ArrayList<Alternativa>();
				
				alternativasTemp.add(a.clone());
			}

			p.setAlternativas(alternativasTemp);
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	/** Seta todas as informações necessárias de um pergunta */
	public void setPerguntaQuestionario(PerguntaQuestionario p) {
		this.id = p.getId();
		this.tipo = p.getTipo();
		this.pergunta = p.getPergunta();
		this.ordem = p.getOrdem();
		this.questionario = p.getQuestionario();
		this.alternativas = p.getAlternativas();
		this.gabaritoDissertativa = p.getGabaritoDissertativa();
		this.gabaritoNumerica = p.getGabaritoNumerica();
		this.gabaritoVf = p.getGabaritoVf();
		this.registroCadastro = p.getRegistroCadastro();
		this.dataCadastro = p.getDataCadastro();
		this.registroAtualizacao = p.getRegistroAtualizacao();
		this.dataAtualizacao = p.getDataAtualizacao();
		this.ativo = p.isAtivo();
		this.obrigatoria = p.isObrigatoria();
	}

	public boolean isExibeMaxCaracteres(){
		return maxCaracteres != null;
	}
	
	public String getMensagemExibicao(){
		return isDissertativa() ? "( Número máximo de Caracteres: " + maxCaracteres + " )": "( Quantidade máxima de números: " + maxCaracteres + " )";
	}
	
	public Integer getMaxCaracteres() {
		return maxCaracteres;
	}

	public void setMaxCaracteres(Integer maxCaracteres) {
		this.maxCaracteres = maxCaracteres;
	}		
	
}