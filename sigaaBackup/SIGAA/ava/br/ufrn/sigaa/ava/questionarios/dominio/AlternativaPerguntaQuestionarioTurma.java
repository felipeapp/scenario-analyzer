/* 
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Criado em: 28/11/2010
 */
package br.ufrn.sigaa.ava.questionarios.dominio;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Entidade que representa uma alternativa para as perguntas dos questionários da turma virtual
 * 
 * @author Fred_Castro
 *
 */
@Entity @Table (name="alternativa_pergunta_questionario_turma", schema="ava")
public class AlternativaPerguntaQuestionarioTurma implements PersistDB {

	/** Identificador do objeto */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_alternativa_pergunta_questionario_turma", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Texto com a resposta */
	private String alternativa;
	
	/** O Texto a ser exibido quando o discente selecionar esta alternativa. */
	private String feedback;

	/** pergunta a qual esta resposta pertence
	 * esta pergunta deve ser do tipo MULTIPLA_ESCOLHA ou MULTIPLA_ESCOLHA_MULTIPLA
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pergunta_questionario_turma", unique = false, nullable = true, insertable = true, updatable = true)
	private PerguntaQuestionarioTurma pergunta;

	/** indica a ordem que a alternativa deve aparecer na exibição do questionário */
	private int ordem;

	/**
	 * Este atributo indica se esta alternativa é o gabarito da questão.
	 * Observe que para questões de única escolha pode haver APENAS UMA alternativa correta.
	 * enquanto que para questões de múltipla escolha podem haver varias.
	 */
	private boolean gabarito = false;

	/**
	 * Indica se o objeto está ativo, podendo aparecer nos casos de uso do sistema.
	 */
	private boolean ativo = true;
	
	/** registro entrada do usuário que cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** data que foi cadastrado */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoEm
	private Date dataCadastro;
	
	/** utilizado para controle na jsp */
	@Transient
	private boolean selecionado;

	public AlternativaPerguntaQuestionarioTurma () {
		
	}
	
	public AlternativaPerguntaQuestionarioTurma (int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlternativa() {
		return alternativa;
	}

	public void setAlternativa(String alternativa) {
		this.alternativa = alternativa;
	}

	public PerguntaQuestionarioTurma getPergunta() {
		return pergunta;
	}

	public void setPergunta(PerguntaQuestionarioTurma pergunta) {
		this.pergunta = pergunta;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public boolean isGabarito() {
		return gabarito;
	}

	public void setGabarito(boolean gabarito) {
		this.gabarito = gabarito;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getLetraAlternativa(){
		return UFRNUtils.inteiroToAlfabeto(ordem, false) + ")";
	}

	@Override
	public String toString() {
		return alternativa;
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
	 * Retorna um clone do objeto atual
	 */
	public AlternativaPerguntaQuestionarioTurma clone() {
		AlternativaPerguntaQuestionarioTurma a = null;
		try {
			a = (AlternativaPerguntaQuestionarioTurma) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return a;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
}
