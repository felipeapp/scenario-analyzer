/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Nov 14, 2007
 *
 */
package br.ufrn.sigaa.questionario.dominio;

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
 * Representa as opções de resposta de uma pergunta.
 * Apenas pergunta do tipo MULTIPLA_ESCOLHA e MULTIPLA_ESCOLHA_MULTIPLA possuem um conjunto de resposta
 * @author Victor Hugo
 *
 */
@Entity
@Table(name = "alternativa_pergunta", schema = "questionario")
public class Alternativa implements PersistDB, Cloneable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_alternativa_pergunta", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** texto com a resposta */
	private String alternativa;

	/** pergunta a qual esta resposta pertence
	 * esta pergunta deve ser do tipo MULTIPLA_ESCOLHA ou MULTIPLA_ESCOLHA_MULTIPLA
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pergunta", unique = false, nullable = true, insertable = true, updatable = true)
	private PerguntaQuestionario pergunta;

	/** indica a ordem que a alternativa deve aparecer na exibição do questionário */
	private int ordem;

	/**
	 * Caso particular usado no SAE, onde cada alternativa tem diferentes pesos
	 */
	private Integer peso;
	
	/**
	 * Este atributo indica se esta alternativa é o gabarito da questão.
	 * Observe que para questões de única escolha pode haver APENAS UMA alternativa correta.
	 * enquanto que para questões de múltipla escolha podem haver varias.
	 */
	private boolean gabarito = false;

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

	public Alternativa() {
		
	}
	
	public Alternativa(int id) {
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

	public String getAlternativaComPeso() {
		return alternativa+" - (Peso: "+peso+") ";
	}
	
	public void setAlternativa(String alternativa) {
		this.alternativa = alternativa;
	}

	public PerguntaQuestionario getPergunta() {
		return pergunta;
	}

	public void setPergunta(PerguntaQuestionario pergunta) {
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
		return !getPergunta().getQuestionario().isProcessoSeletivo() ? UFRNUtils.inteiroToAlfabeto(ordem, false) + ")" : "";
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

	public Integer getPeso() {
		return peso;
	}

	public void setPeso(Integer peso) {
		this.peso = peso;
	}

	public Alternativa clone() {
		Alternativa a = null;
		try {
			a = (Alternativa) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return a;
	}
}
