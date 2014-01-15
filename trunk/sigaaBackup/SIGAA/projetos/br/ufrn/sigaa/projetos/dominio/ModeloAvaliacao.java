/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 07/06/2010
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
/**
 * Modelo de Avaliação definida pelo gestor.
 * Determina o modelo de avaliação que será utilizado nos projetos, relatórios, etc.
 * 
 * 
 * @author Geyson Karlos
 * @author ilueny
 *
 */
@Entity
@Table(name = "modelo_avaliacao", schema = "projetos")
public class ModeloAvaliacao implements PersistDB {

	/** Representa o identificador único do objeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_modelo_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Nome que identifica o modelo de avaliação. */
	@Column(name = "descricao")
	private String descricao;
	
	/** Informa o tipo de avaliação que o modelo se refere.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_avaliacao")
	private TipoAvaliacao tipoAvaliacao = new TipoAvaliacao();
	
	/** Informa se o modelo de avaliação ainda está sendo utilizado.*/
	@CampoAtivo
	private boolean ativo;
	
	/** 
	 * Indica a qual edital este modelo faz referência.
	 * O modelo utiliza algumas regras que estão presentes no edital. 
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_edital")
	private Edital edital = new Edital();

	/** Questionário que será utilizado na construção do formulário de avaliação. */
	@ManyToOne
	@JoinColumn(name = "id_questionario_avaliacao")
	private QuestionarioAvaliacao questionario = new QuestionarioAvaliacao();
	
	/** Nota mínima para aprovação neste modelo. */
	@Column(name = "media_minima_aprovacao")
	private double mediaMinimaAprovacao;
	
	/** Máxima diferença permitida entre a maior e a menor nota de todas as avaliações realizadas para o projeto. */
	@Column(name = "maxima_discrepancia_avaliacoes")
	private double maximaDiscrepanciaAvaliacoes;

	/** 
	 * Tipo de edital
	 * 
	 * M - Monitoria (Ensino)
	 * I - Inovação (Ensino)
	 * P - Pesquisa		
	 * E - Extensão
	 * A - Associados
	 */
	@Column(name = "tipo")
	private char tipo;

	/** Construtor padrão. */
	public ModeloAvaliacao(){}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public TipoAvaliacao getTipoAvaliacao() {
		return tipoAvaliacao;
	}
	public void setTipoAvaliacao(TipoAvaliacao tipoAvaliacao) {
		this.tipoAvaliacao = tipoAvaliacao;
	}
	
	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

	public String getDescricao() {
	    return descricao;
	}

	public void setDescricao(String descricao) {
	    this.descricao = descricao;
	}

	public Edital getEdital() {
	    return edital;
	}

	public void setEdital(Edital edital) {
	    this.edital = edital;
	}

	public QuestionarioAvaliacao getQuestionario() {
	    return questionario;
	}

	public void setQuestionario(QuestionarioAvaliacao questionario) {
	    this.questionario = questionario;
	}
	
	@Override
	public boolean equals(Object obj) {
	    return EqualsUtil.testEquals(this, obj, "id", "questionario.id", "edital.id");
	}

	@Override
	public int hashCode() {
		if ( edital != null )
			return HashCodeUtil.hashAll(id, questionario.getId(), edital.getId());
		else
			return HashCodeUtil.hashAll(id, questionario.getId());
	}

	public double getMediaMinimaAprovacao() {
		return mediaMinimaAprovacao;
	}

	public void setMediaMinimaAprovacao(double mediaMinimaAprovacao) {
		this.mediaMinimaAprovacao = mediaMinimaAprovacao;
	}

	/**
	 * Discrepância máxima permitida entre avaliações diferentes realizadas para o 
	 * mesmo projeto submetido a este modelo de avaliação.

	 * @return
	 */
	public double getMaximaDiscrepanciaAvaliacoes() {
		return maximaDiscrepanciaAvaliacoes;
	}

	public void setMaximaDiscrepanciaAvaliacoes(double maximaDiscrepanciaAvaliacoes) {
		this.maximaDiscrepanciaAvaliacoes = maximaDiscrepanciaAvaliacoes;
	}

	/**
	 * Informa se é um modelo de avaliação de projeto.
	 * 
	 * @return
	 */
	public boolean isModeloAvaliacaoProjeto() {
		return this.tipoAvaliacao.getId() == TipoAvaliacao.PROJETOS; 
	}

	/**
	 * Informa se é um modelo de avaliação de relatório.
	 * 
	 * @return
	 */
	public boolean isModeloAvaliacaoRelatorio() {
		return this.tipoAvaliacao.getId() == TipoAvaliacao.RELATORIOS; 
	}

	public boolean isAssociado() {
		return tipo == Edital.ASSOCIADO;
	}
	
	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}
	
}
