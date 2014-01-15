/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Nov 14, 2007
 *
 */
package br.ufrn.sigaa.questionario.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateEmptyCollection;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 *	Esta entidade representa uma resposta a uma pergunta de um questionário
 * Cada RespostaPergunta só pode possuir uma resposta que vai variar de acordo com o tipo da pergunta.
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "resposta", schema = "questionario")
public class Resposta implements Validatable, Comparable<Resposta> {

	/** Chave primária das respostas */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_resposta")
	private int id;

	/** Faz referência as respostas do questionário do usuário */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_questionario_respostas")
	private QuestionarioRespostas questionarioRespostas;
	
	/** pergunta que esta sendo respondida */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pergunta")
	private PerguntaQuestionario pergunta;

	/** resposta da pergunta caso a pergunta seja do tipo {@link PerguntaQuestionario#UNICA_ESCOLHA} */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_alternativa")
	private Alternativa alternativa;

	/** respostas da pergunta caso a pergunta seja do tipo {@link PerguntaQuestionario#MULTIPLA_ESCOLHA} */
	@ManyToMany(targetEntity = Alternativa.class)
	@JoinTable(name="resposta_pergunta_multipla", schema="questionario",
			joinColumns ={ @JoinColumn(name = "id_resposta")}, 
			inverseJoinColumns ={ @JoinColumn(name = "id_alternativa_pergunta")})
	private Collection<Alternativa> alternativas;

	/** resposta da pergunta caso a pergunta seja do tipo {@link PerguntaQuestionario#DISSERTATIVA} */
	@Column(name="resposta_dissertativa")
	private String respostaDissertativa;

	/** resposta da pergunta caso a pergunta seja do tipo {@link PerguntaQuestionario#NUMERICA} */
	@Column(name="resposta_numerica")
	private Float respostaNumerica;

	/** resposta da pergunta caso a pergunta seja do tipo {@link PerguntaQuestionario#VF} */
	@Column(name="resposta_vf")
	private Boolean respostaVf;
	
	/** o id do arquivo para a resposta da pergunta do tipo {@link PerguntaQuestionario#ARQUIVO} */
	@Column(name="resposta_arquivo")
	private Integer respostaArquivo;
	
	/**
	 * arquivo selecionado para a resposta da pergunta do tipo {@link PerguntaQuestionario#ARQUIVO}
	 * Utilizado somente no cadastro. 
	 */
	@Transient
	private UploadedFile arquivo;
	
	/**
	 * Construtor padrão
	 */
	public Resposta() {
	}
	
	/**
	 * Construtor que recebe PerguntaQuestionario
	 * @param pergunta
	 */
	public Resposta(PerguntaQuestionario pergunta) {
		this.pergunta = pergunta;
		if (pergunta.isUnicaEscolha()) {
			this.alternativa = new Alternativa();
		}
		if (pergunta.isUnicaEscolhaAlternativaPeso()) {
			this.alternativa = new Alternativa();
		}		
		if (pergunta.isMultiplaEscolha()) {
			this.alternativas = new ArrayList<Alternativa>();
		}
	}

	/**
	 * Construtor que recebe QuestionarioRespostas e PerguntaQuestionario
	 *  
	 * @param questionarioRespostas
	 * @param pergunta
	 */
	public Resposta(QuestionarioRespostas questionarioRespostas, PerguntaQuestionario pergunta) {
		this(pergunta);
		this.questionarioRespostas = questionarioRespostas;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resposta other = (Resposta) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PerguntaQuestionario getPergunta() {
		return pergunta;
	}

	public void setPergunta(PerguntaQuestionario pergunta) {
		this.pergunta = pergunta;
	}


	public String getRespostaDissertativa() {
		return respostaDissertativa;
	}

	public void setRespostaDissertativa(String respostaDissertativa) {
		this.respostaDissertativa = respostaDissertativa;
	}

	public Float getRespostaNumerica() {
		return respostaNumerica;
	}

	public void setRespostaNumerica(Float respostaNumerica) {
		this.respostaNumerica = respostaNumerica;
	}
	
	/**
	 * Retorna a resposta numérica pegando um FLoat e convertendo para String. 
	 * @return
	 */
	public String getRespostaNumericaString( ) {
		if (this.respostaNumerica != null ) { 
			String rn = this.respostaNumerica.toString();
			DecimalFormat d = new DecimalFormat("###,###,###,##0.00");
			rn = d.format(this.respostaNumerica);
			return rn;
		}
		else {
			return null;
		}
		
	}
	
	
	/**
	 * Seta a reposta numérica recebendo uma String e convertendo para Float.
	 * @param respostaNumerica
	 */
	public void setRespostaNumericaString(String respostaNumerica) {
		//remove todos os '.' e substitui ', por '.''
		respostaNumerica = respostaNumerica.replace(".", "");
		respostaNumerica = respostaNumerica.replace(",", ".");
		
		if (!isEmpty(respostaNumerica)) {
			this.respostaNumerica = Float.parseFloat(respostaNumerica);
		}
		else {
			respostaNumerica = null;
		}
		
	}
	
	/** Retorna a descrição da respota boolean */
	public String getRespostaVfString() {
		if(respostaVf == null)
			return "";
		return respostaVf ? "SIM" : "NÃO";
	}
	
	
	public Boolean getRespostaVf() {
		return respostaVf;
	}

	public void setRespostaVf(Boolean respostaVf) {
		this.respostaVf = respostaVf;
	}

	public Integer getRespostaArquivo() {
		return respostaArquivo;
	}

	public void setRespostaArquivo(Integer respostaArquivo) {
		this.respostaArquivo = respostaArquivo;
	}
	
	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public Alternativa getAlternativa() {
		return alternativa;
	}

	public void setAlternativa(Alternativa alternativa) {
		this.alternativa = alternativa;
	}

	public Collection<Alternativa> getAlternativas() {
		return alternativas;
	}

	public void setAlternativas(Collection<Alternativa> alternativas) {
		this.alternativas = alternativas;
	}

	public QuestionarioRespostas getQuestionarioRespostas() {
		return questionarioRespostas;
	}

	public void setQuestionarioRespostas(QuestionarioRespostas questionarioRespostas) {
		this.questionarioRespostas = questionarioRespostas;
	}

	/**
	 * Adiciona uma alternativa à resposta multi escolha.
	 *
	 * @param alt
	 */
	public void adicionaAlternativa(Alternativa alt) {
		if(this.alternativas == null)
			alternativas = new ArrayList<Alternativa>();
		alternativas.add(alt);
	}
	
	
	/** Comparação da ordem das perguntas do questionário */
	public int compareTo(Resposta outra) {
		return this.pergunta.getOrdem() - outra.pergunta.getOrdem();
	}

	/** (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens listaMensagens = new ListaMensagens();
		
		String mensagem = "Resposta da pergunta " + pergunta.getOrdem();
		
		if ( pergunta.isVf() ) {
			validateRequired(respostaVf, mensagem, listaMensagens);
		}
		
		if ( pergunta.isNumerica() ) {
			validateRequired(respostaNumerica, mensagem, listaMensagens);
		}
		
		if ( pergunta.isDissertativa() ) {
			validateRequired(respostaDissertativa, mensagem, listaMensagens);
		}
		
		if ( pergunta.isUnicaEscolha() ) {
			validateRequired(alternativa, mensagem, listaMensagens);
		}
		
		if ( pergunta.isUnicaEscolhaAlternativaPeso() ) {
			validateRequired(alternativa, mensagem, listaMensagens);
		}		
		
		if ( pergunta.isMultiplaEscolha() ) {
			validateEmptyCollection(mensagem + ": Selecione pelo menos uma das alternativas", alternativas, listaMensagens);
		}
				
		if ( pergunta.isArquivo() ) {
			if( isEmpty(getArquivo()) )
				validateRequired(arquivo, mensagem, listaMensagens);
			/*else{
				if ( !getArquivo().getContentType().equalsIgnoreCase("application/pdf")
						&& !getArquivo().getContentType().equalsIgnoreCase("application/x-pdf") )
					listaMensagens.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, mensagem);
			}*/
		}
		
		
		return listaMensagens;
	}

	/**
	 * Marca as alternativas da pergunta de acordo com as opções
	 * marcadas durante o preenchimento das respostas
	 * 
	 */
	public void marcarAlternativasPergunta() {
		
		if (pergunta != null){
		
			for ( Alternativa alternativa : pergunta.getAlternativas() ) {
				
				if (alternativa != null){
				
					alternativa.setGabarito(false);
					
					if (pergunta.isUnicaEscolha() &&  alternativa.equals(this.alternativa)) {
						alternativa.setGabarito(true);
					}
					if (pergunta.isUnicaEscolhaAlternativaPeso() &&  alternativa.equals(this.alternativa)) {
						alternativa.setGabarito(true);
					}
					if (pergunta.isMultiplaEscolha() &&  alternativas.contains(alternativa)) {
						alternativa.setGabarito(true);
					}
					
				}
					
			}
			
		}
		
	}

}
