 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/11/2010
 *
 */
package br.ufrn.sigaa.ava.questionarios.dominio;


import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateEmptyCollection;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.List;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;


/**
 * Classe de domínio que representa um questionário da Turma Virtual.
 * 
 * @author Fred_Castro
 */
@Entity @Table(name="resposta_pergunta_questionario_turma", schema="ava")
public class RespostaPerguntaQuestionarioTurma implements Validatable {

	/** O identificador do objeto */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	@Column(name = "id_resposta_pergunta_questionario_turma")
	private int id;
	
	/** O objeto agrupador de respostas para um envio do discente para o questionário */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_envio_respostas_questionario_turma")
	private EnvioRespostasQuestionarioTurma envio;
	
	/** A pergunta à qual pertence esta resposta */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_pergunta_questionario_turma")
	private PerguntaQuestionarioTurma pergunta;
	
	/** A alternativa escolhida para a resposta */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_alternativa_pergunta_questionario_turma")
	private AlternativaPerguntaQuestionarioTurma alternativaEscolhida;
	
	/** Variável que indica se o docente assinalou a resposta como correta. Usada no caso de resposta dissertativa. */
	@Column(name="assinalada_como_correta")
	private boolean assinaladaComoCorreta;
	
	/** O texto digitado pelo docente no ato da correção. */
	@Column(name="correcao_dissertativa")
	private String correcaoDissertativa;
	
	/** respostas da pergunta caso a pergunta seja do tipo MULTIPLA_ESCOLHA */
	@ManyToMany(targetEntity = AlternativaPerguntaQuestionarioTurma.class)
	@JoinTable(name="resposta_pergunta_multipla", schema="ava",
			joinColumns ={ @JoinColumn(name = "id_resposta_pergunta_questionario_turma")}, 
			inverseJoinColumns ={ @JoinColumn(name = "id_alternativa_pergunta_questionario_turma")})
	private List <AlternativaPerguntaQuestionarioTurma> alternativasEscolhidas;
	
	/** A resposta dissertativa */
	@Column(name="resposta_dissertativa")
	private String respostaDissertativa;
	
	/** A resposta numérica */
	@Column(name="resposta_numerica")
	private Float respostaNumerica;
	
	/** A resposta booleana */
	@Column(name="resposta_vf")
	private Boolean respostaVF;
	
	/** Nota de uma questão dissertativa. */
	@Column(name="porcentagem_nota")
	private Integer porcentagemNota;

	/** Construtor padrão */
	public RespostaPerguntaQuestionarioTurma () {
		
	}
	
	/** Construtor que recebe uma pergunta */
	public RespostaPerguntaQuestionarioTurma (PerguntaQuestionarioTurma pergunta) {
		this.pergunta = pergunta;
		if (pergunta.isUnicaEscolha())
			this.alternativaEscolhida = new AlternativaPerguntaQuestionarioTurma ();

		if (pergunta.isMultiplaEscolha())
			this.alternativasEscolhidas = new ArrayList<AlternativaPerguntaQuestionarioTurma>();
	}

	/**
	 * Construtor que recebe QuestionarioRespostas e PerguntaQuestionario
	 *  
	 * @param questionarioRespostas
	 * @param pergunta
	 */
	public RespostaPerguntaQuestionarioTurma (EnvioRespostasQuestionarioTurma envio, PerguntaQuestionarioTurma pergunta) {
		this(pergunta);
		this.envio = envio;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EnvioRespostasQuestionarioTurma getEnvio() {
		return envio;
	}

	public void setEnvio(EnvioRespostasQuestionarioTurma envio) {
		this.envio = envio;
	}

	public PerguntaQuestionarioTurma getPergunta() {
		return pergunta;
	}

	public void setPergunta(PerguntaQuestionarioTurma pergunta) {
		this.pergunta = pergunta;
	}

	public AlternativaPerguntaQuestionarioTurma getAlternativaEscolhida() {
		return alternativaEscolhida;
	}

	public void setAlternativaEscolhida(
			AlternativaPerguntaQuestionarioTurma alternativaEscolhida) {
		this.alternativaEscolhida = alternativaEscolhida;
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
	 * Retorna a resposta numerica em forma de string. 
	 * @return
	 */
	public String getRespostaNumericaString() {
		if (this.respostaNumerica == null) {
			return null;
		}
		else {
			String respostaNumericaString = this.respostaNumerica.toString();
			respostaNumericaString = respostaNumericaString.replace('.', ',');
			if (respostaNumericaString.endsWith(",0")) {
				respostaNumericaString = respostaNumericaString.substring(0,respostaNumericaString.length()-2 );
			}
			return  respostaNumericaString;
		}
	}
	
	/**
	 * Seta a resposta numerica a partir de uma String  
	 * @param respostaNumerica
	 */
	public void setRespostaNumericaString(String respostaNumerica) {
		if (!isEmpty(respostaNumerica)) {
			this.respostaNumerica = new Float(0);
			respostaNumerica = respostaNumerica.replace(',','.');
			this.respostaNumerica = Float.parseFloat(respostaNumerica);
		} else {
			this.respostaNumerica = null;
		}
		
	}
	
	public Boolean getRespostaVF() {
		return respostaVF;
	}
	
	public Boolean isRespostaVF () {
		return respostaVF;
	}

	public void setRespostaVF(Boolean respostaVF) {
		this.respostaVF = respostaVF;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens listaMensagens = new ListaMensagens();
		
		String mensagem = "Resposta da pergunta " + pergunta.getOrdem();
		
		if (pergunta.isVf())
			validateRequired(respostaVF, mensagem, listaMensagens);
		
		if (pergunta.isNumerica())
			validateRequired(respostaNumerica, mensagem, listaMensagens);
		
		if (pergunta.isDissertativa())
			validateRequired(respostaDissertativa, mensagem, listaMensagens);
		
		if (pergunta.isUnicaEscolha())
			validateRequired(alternativaEscolhida, mensagem, listaMensagens);
		
		if (pergunta.isMultiplaEscolha())
			validateEmptyCollection(mensagem + ": Selecione pelo menos uma das alternativas", alternativasEscolhidas, listaMensagens);
		
		return listaMensagens;
	}

	public List <AlternativaPerguntaQuestionarioTurma> getAlternativasEscolhidas() {
		return alternativasEscolhidas;
	}

	public void setAlternativasEscolhidas(
			List <AlternativaPerguntaQuestionarioTurma> alternativasEscolhidas) {
		this.alternativasEscolhidas = alternativasEscolhidas;
	}
	
	public boolean isAssinaladaComoCorreta() {
		return assinaladaComoCorreta;
	}

	public void setAssinaladaComoCorreta(boolean assinaladaComoCorreta) {
		this.assinaladaComoCorreta = assinaladaComoCorreta;
	}

	/**
	 * Indica se a resposta está correta.
	 * 
	 * @return
	 */
	public boolean isCorreta () {
		
		if (pergunta.isDissertativa())
			return isCorretaDissertativa();
		else
			return isCorretaAlternativa();
	}
	
	/**
	 * Indica se a resposta com alternativas está correta.
	 * 
	 * @return
	 */
	public boolean isCorretaAlternativa () {
		
		if (pergunta.isMultiplaEscolha()) {
			boolean correta = true;
			for (AlternativaPerguntaQuestionarioTurma a : pergunta.getAlternativasValidas()){
				boolean marcou = false;
				for (AlternativaPerguntaQuestionarioTurma ae : alternativasEscolhidas)
					if (a.getId() == ae.getId() )
						marcou =  true;
				// O Discente marcou uma resposta errada.
				if (!a.isGabarito() && marcou)
					correta = false;
				// O Discente deixou de marcar uma resposta correta.
				if (a.isGabarito() && !marcou)
					correta = false;
				// A resposta está errada então saia do laço.
				if (!correta)
					break;
			}	
			return correta;
				
		} else if (pergunta.isNumerica())
			return pergunta.getGabaritoNumerica().equals(respostaNumerica); 
		else if (pergunta.isUnicaEscolha()){
			for (AlternativaPerguntaQuestionarioTurma a : pergunta.getAlternativasValidas())
				if (a.isGabarito())
					return alternativaEscolhida != null && a.getId() == alternativaEscolhida.getId();
		} else if (pergunta.isVf())
			return pergunta.getGabaritoVf().equals(respostaVF);

		return false;
	}

	/**
	 * Indica se a resposta dissertativa está correta.
	 * 
	 * @return
	 */
	public boolean isCorretaDissertativa () {
		
		if (pergunta.isDissertativa()) 
			if ( (getPorcentagemNota() != null && getPorcentagemNota() == 100) || 
			  ( pergunta.getGabaritoDissertativa() != null && respostaDissertativa != null && pergunta.getGabaritoDissertativa().trim().equalsIgnoreCase(respostaDissertativa.trim())) )
						return true;			
		return false;
	}
	
	public String getCorrecaoDissertativa() {
		return correcaoDissertativa;
	}

	public void setCorrecaoDissertativa(String correcaoDissertativa) {
		this.correcaoDissertativa = correcaoDissertativa;
	}

	/**
	 * Caso o parametro seja diferente -1 muda o valor da porcentegem para o recebido como parametro. Caso contrario muda para null.
	 * @param porcentagemNota
	 */
	public void setPorcentagemNota(Integer porcentagemNota) {
		if ( porcentagemNota == -1 )
			porcentagemNota = null;
		this.porcentagemNota = porcentagemNota;
	}

	public Integer getPorcentagemNota() {
		return porcentagemNota;
	}
}