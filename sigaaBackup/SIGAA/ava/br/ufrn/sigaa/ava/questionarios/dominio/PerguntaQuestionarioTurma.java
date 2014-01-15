/* 
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Criado em: 28/11/2010
 */
package br.ufrn.sigaa.ava.questionarios.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
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
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Entidade que representa uma pergunta do questionário da turma virtual.
 * 
 * @author Fred_Castro
 *
 */

@Entity @Table(name="pergunta_questionario_turma", schema="ava")
public class PerguntaQuestionarioTurma implements PersistDB {

	/**
	 * Constantes dos tipos de pergunta
	 */
	public static final int UNICA_ESCOLHA = 1; /** O discente escolhe uma opção entre varias */
	public static final int MULTIPLA_ESCOLHA = 2; /** o discente pode escolher mais de uma opção */
	public static final int VF = 3; /** resposta verdadeiro ou falso */
	public static final int DISSERTATIVA = 4; /** resposta dissertativa */
	public static final int NUMERICA = 5; /** resposta numérica */

	
	/** contantes do tipo de limitacao da resposta dissertativa*/
	public static final int NAO_LIMITAR = 1;/**Constante para nao limitar a resposta de uma pergunta dissertativa*/
	public static final int LIMITAR_CARACTERES = 2; /**Constante para limitar a resposta de uma pergunta dissertativa por caracteres*/ 
	public static final int LIMITAR_PALAVRAS = 3;/**Constante para limitar a resposta de uma pergunta dissertativa por palavras*/
	
	
	
	
	/** O identificador do objeto */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	@Column(name = "id_pergunta_questionario_turma")
	private int id;

	/**
	 * tipo da pergunta
	 */
	private int tipo;

	/** texto da pergunta */
	private String pergunta;
	
	/** texto da pergunta com fórmula latex*/
	@Column(name="pergunta_formula")
	private String perguntaFormula;
	
	/** Nome da pergunta para uso interno. */
	private String nome;

	/**
	 * indica a ordem que a pergunta irá aparecer no questionário. deve iniciar de 1
	 */
	private int ordem;

	/** O quetionário ao qual pertence esta pergunta */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_questionario_turma", unique = false, nullable = true, insertable = true, updatable = true)
	private QuestionarioTurma questionarioTurma;

	/**
	 * coleção de respostas para caso o tipo da pergunta seja MULTIPLA_ESCOLHA OU MULTIPLA_ESCOLHA_MULTIPLA
	 */
	@OneToMany(mappedBy = "pergunta", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<AlternativaPerguntaQuestionarioTurma> alternativas;

	// Respostas da pergunta
	
	/** Resposta dissertativa correta */
	@Column(name="gabarito_dissertativa")
	private String gabaritoDissertativa;
	
	/**tipo da limitacao da resposta dissertativa */
	@Column(name="tipo_limitador_dissertativa")
	private Integer tipoLimitadorDissertativa = NAO_LIMITAR;
	
	/**valor da limitacao maxima em caracteres ou palavras*/
	@Column(name="valor_limitador_dissertativa")
	private Integer valorLimitadorDissertativa;

	/** Resposta numérica correta */
	@Column(name="gabarito_numerica")
	private Float gabaritoNumerica;

	/** Resposta booleana correta */
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

	/** Indica se o objeto está ativo */
	private boolean ativo = true;
	
	/** O usuário ao qual esta pergunta pertence. Só deve ser usado quando a pergunta está no banco de questões. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dono", unique = false, nullable = true, insertable = true, updatable = true)
	private Usuario dono;
	
	/** A categoria à qual esta pergunta está associada no banco de questões. */
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria", unique = false, nullable = true, insertable = true, updatable = true)
	/** Categoria à qual pertence esta pergunta */
	private CategoriaPerguntaQuestionarioTurma categoria = new CategoriaPerguntaQuestionarioTurma();
	
	/** Texto a ser exibido quando o discente acerta a pergunta. */
	@Column(name="feedback_acerto")
	private String feedbackAcerto;
	
	/** Texto a ser exibido quando o discente erra a pergunta. */
	@Column(name="feedback_erro")
	private String feedbackErro;
	
	/** Variável para auxiliar os casos de uso, indicando se a pergunta foi selecionada. */
	@Transient
	private boolean selecionada = false;
	
	public PerguntaQuestionarioTurma () {
		ordem = 1;
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

	/**
	 * Retorna o tipo da pergunta, em string
	 * @return
	 */
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
			default:
				return "Indefinido";
		}
	}
	/**
	 * Retorna o ícone do tipo da pergunta
	 * @return
	 */
	public String getTipoIcone() {
		switch (tipo) {
			case DISSERTATIVA:
				return "discursiva.gif";
			case NUMERICA:
				return "numerica.gif";
			case VF:
				return "verdadeiro_falso.gif";
			case MULTIPLA_ESCOLHA:
				return "multipla_escolha.gif";
			case UNICA_ESCOLHA:
				return "unica_escolha.gif";
		}
		
		return "";
	}

	/**
	 * atualiza o atributo ordem das alternativas para na sequência que está na lista iniciando de 1
	 */
	public void atualizarOrdemAlternativas(){
		if( (isMultiplaEscolha() || isUnicaEscolha()) && alternativas != null ){
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

	/**
	 * Retorna apenas as alternativas ativas de uma pergunta.
	 * @return
	 */
	public List<AlternativaPerguntaQuestionarioTurma> getAlternativas() {
		
		List<AlternativaPerguntaQuestionarioTurma> alternativasRemover = new ArrayList<AlternativaPerguntaQuestionarioTurma>();
		
		if ( alternativas != null && !alternativas.isEmpty() ) {
			for(AlternativaPerguntaQuestionarioTurma a : alternativas) {
				if (a != null && !a.isAtivo()) {
					alternativasRemover.add(a);
				}
			}
			
			Collections.sort(alternativas,new Comparator<AlternativaPerguntaQuestionarioTurma>() {
				public int compare(AlternativaPerguntaQuestionarioTurma o1,
						AlternativaPerguntaQuestionarioTurma o2) {
					return o1.getOrdem()-o2.getOrdem();
				}				
			});
			
			for (AlternativaPerguntaQuestionarioTurma a : alternativasRemover )
				alternativas.remove(a);
		}	
		return alternativas;
	}

	public void setAlternativas(List<AlternativaPerguntaQuestionarioTurma> alternativas) {
		this.alternativas = alternativas;
	}

	public QuestionarioTurma getQuestionarioTurma() {
		return questionarioTurma;
	}

	public void setQuestionario(QuestionarioTurma questionarioTurma) {
		this.questionarioTurma = questionarioTurma;
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
	
	public Integer getTipoLimitadorDissertativa() {
		return tipoLimitadorDissertativa;
	}

	public void setTipoLimitadorDissertativa(Integer tipoLimitadorDissertativa) {
		this.tipoLimitadorDissertativa = tipoLimitadorDissertativa;
	}
	
	public Integer getValorLimitadorDissertativa() {
		return valorLimitadorDissertativa;
	}

	public void setValorLimitadorDissertativa(Integer valorLimitadorDissertativa) {
		this.valorLimitadorDissertativa = valorLimitadorDissertativa;
	}

	/**
	 * Retorna True caso o tipo de limitacao da resposta dissertativa seja por caracteres e false caso contrário.
	 * @return boolean
	 */
	public boolean isLimitarCaracteres(){
		return tipoLimitadorDissertativa == LIMITAR_CARACTERES;
	}
	
	/**
	 * Retorna True caso o tipo de limitacao da resposta dissertativa seja por palavras e false caso contrário.
	 * @return boolean
	 */
	public boolean islimitarPalavras(){
		return tipoLimitadorDissertativa == LIMITAR_PALAVRAS;
	}
	
	/**
	 * Retorna True caso não seja para limitar a resposta dissertativa false caso contrário.
	 * @return boolean
	 */
	public boolean isNaoLimitar(){	
		if (tipoLimitadorDissertativa == null)
			return true;
		return tipoLimitadorDissertativa == NAO_LIMITAR;
	}
	
	
	public Float getGabaritoNumerica() {
		return gabaritoNumerica;
	}

	public void setGabaritoNumerica(Float gabaritoNumerica) {
		this.gabaritoNumerica = gabaritoNumerica;
	}
	
	/**
	 * Retorna o gabarito de uma pergunta numerica ja comvertido em uma String.
	 * @return
	 */
	public String getGabaritoNumericaString() {
		if (this.gabaritoNumerica == null) {
			return null;
		}
		else {
			String gabaritoNumericaString = this.gabaritoNumerica .toString();
			gabaritoNumericaString = gabaritoNumericaString.replace('.', ',');
			if (gabaritoNumericaString.endsWith(",0")) {
				gabaritoNumericaString = gabaritoNumericaString.substring(0,gabaritoNumericaString.length()-2 );
			}
			return  gabaritoNumericaString;
		}
	}

	/**
	 * Modifica o gabarito de uma pergunta numerica recebendo uma String como parametro.
	 * @param gabaritoNumerica
	 */
	public void setGabaritoNumericaString(String gabaritoNumerica) {
		if (!isEmpty(gabaritoNumerica)) {
			this.gabaritoNumerica = new Float(0);
			gabaritoNumerica = gabaritoNumerica.replace(',','.');
			this.gabaritoNumerica = Float.parseFloat(gabaritoNumerica);
		} else {
			this.gabaritoNumerica = null;
		}
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
		return isMultiplaEscolha() || isUnicaEscolha();
	}

	public Usuario getDono() {
		return dono;
	}

	public void setDono(Usuario dono) {
		this.dono = dono;
	}

	public CategoriaPerguntaQuestionarioTurma getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaPerguntaQuestionarioTurma categoria) {
		this.categoria = categoria;
	}

	public void setQuestionarioTurma(QuestionarioTurma questionarioTurma) {
		this.questionarioTurma = questionarioTurma;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getFeedbackAcerto() {
		return feedbackAcerto;
	}

	public void setFeedbackAcerto(String feedbackAcerto) {
		this.feedbackAcerto = feedbackAcerto;
	}

	public String getFeedbackErro() {
		return feedbackErro;
	}

	public void setFeedbackErro(String feedbackErro) {
		this.feedbackErro = feedbackErro;
	}

	/**
	 * Diz se a pergunta tem gabarito
	 * @return
	 */
	public boolean hasGabarito(){
		if( isDissertativa() )
			return !isEmpty( gabaritoDissertativa );
		if( isNumerica() )
			return !isEmpty( gabaritoNumerica ) || gabaritoNumerica != null;
		if( isVf() )
			return !isEmpty( gabaritoVf);
		if( isMultiplaOuUnicaEscolha() ){
			if( !isEmpty(alternativas) ){
				for( AlternativaPerguntaQuestionarioTurma a : alternativas ){
					if( a.isGabarito() )
						return true;
				}
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Retorna as alternativas ativas da pergunta.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<AlternativaPerguntaQuestionarioTurma> getAlternativasValidas() {
		Collection<AlternativaPerguntaQuestionarioTurma> res = CollectionUtils.select(alternativas, new Predicate() {
			public boolean evaluate(Object alternativa) {
				return ((AlternativaPerguntaQuestionarioTurma) alternativa).isAtivo();
			}
		});
		
		Collections.sort((List<AlternativaPerguntaQuestionarioTurma>) res,new Comparator<AlternativaPerguntaQuestionarioTurma>() {
			public int compare(AlternativaPerguntaQuestionarioTurma o1,
					AlternativaPerguntaQuestionarioTurma o2) {
				return o1.getOrdem()-o2.getOrdem();
			}				
		});
		
		return res;
	}
	
	/**
	 * Retorna as alternativas ativas da pergunta de forma desordenada. Utilizada quando as alternativas são misturadas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<AlternativaPerguntaQuestionarioTurma> getAlternativasDesordenadas() {
		Collection<AlternativaPerguntaQuestionarioTurma> res = CollectionUtils.select(alternativas, new Predicate() {
			public boolean evaluate(Object alternativa) {
				return ((AlternativaPerguntaQuestionarioTurma) alternativa).isAtivo();
			}
		});
				
		return res;
	}
	
	/**
	 * Retorna as alternativas válidas de uma pergunta em formato de combo.
	 * @return
	 */
	public List<SelectItem> getAlternativasValidasCombo() {
		List<SelectItem> listaCombo = new ArrayList<SelectItem>();
		Collection<AlternativaPerguntaQuestionarioTurma> lista = getAlternativasValidas();
		for(AlternativaPerguntaQuestionarioTurma alternativa:lista) {
			SelectItem aux = new SelectItem(alternativa);
			listaCombo.add(aux); 
		}
		
		return listaCombo;
	}
	
	/**
	 * Retorna a representação textual desde objeto.
	 */
	public String toString() {
		return pergunta;
	}

	/**
	 * Valida o cadastro de uma pergunta
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		validateRequiredId( tipo, "Tipo de Pergunta", erros);
		validateRequired( nome, "Pergunta", erros);

		if ( isMultiplaOuUnicaEscolha() && (isEmpty( alternativas ) || alternativas.size() < 2) )
			erros.addErro("É necessário informar pelo menos duas alternativas para esta pergunta");
		
		return erros;
	}

	/**
	 * Retorna um clone do objeto atual
	 */
	public PerguntaQuestionarioTurma clone() {
		PerguntaQuestionarioTurma p = null;
		try {
			p = (PerguntaQuestionarioTurma) super.clone();
			
			List<AlternativaPerguntaQuestionarioTurma> alternativasTemp = null;
			for (AlternativaPerguntaQuestionarioTurma a : alternativas) {
				if (alternativasTemp == null)
					alternativasTemp = new ArrayList<AlternativaPerguntaQuestionarioTurma>();
				
				alternativasTemp.add(a.clone());
			}

			p.setAlternativas(alternativasTemp);
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	/**
	 * Preenche todos os dados da pergunta atual com os dados da pergunta passada por parâmetro
	 * @param p
	 */
	public void setPerguntaQuestionario(PerguntaQuestionarioTurma p) {
		this.id = p.getId();
		this.tipo = p.getTipo();
		this.pergunta = p.getPergunta();
		this.ordem = p.getOrdem();
		this.questionarioTurma = p.getQuestionarioTurma();
		this.alternativas = p.getAlternativas();
		this.gabaritoDissertativa = p.getGabaritoDissertativa();
		this.gabaritoNumerica = p.getGabaritoNumerica();
		this.gabaritoVf = p.getGabaritoVf();
		this.registroCadastro = p.getRegistroCadastro();
		this.dataCadastro = p.getDataCadastro();
		this.registroAtualizacao = p.getRegistroAtualizacao();
		this.dataAtualizacao = p.getDataAtualizacao();
		this.ativo = p.isAtivo();
	}
	
	@Override
	public boolean equals(Object o){
		return EqualsUtil.testEquals(this, o, "id");
	}
	
	@Override
	public int hashCode(){
		return HashCodeUtil.hashAll(id);
	}

	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	public void setPerguntaFormula(String perguntaFormula) {
		this.perguntaFormula = perguntaFormula;
	}

	public String getPerguntaFormula() {
		return perguntaFormula;
	}
}
