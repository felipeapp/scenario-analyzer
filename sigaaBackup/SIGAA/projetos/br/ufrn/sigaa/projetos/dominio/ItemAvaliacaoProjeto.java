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
 * itens de avaliação.
 * 
 * @author Geyson Karlos
 *
 */
@Entity
@Table(name = "item_avaliacao", schema = "projetos")
public class ItemAvaliacaoProjeto implements PersistDB {
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_item_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	private double peso;
	
	@Column(name="nota_maxima")
	private double notaMaxima;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pergunta_avaliacao")
	private PerguntaAvaliacao pergunta;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_questionario_avaliacao")
	private QuestionarioAvaliacao questionario;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_grupo_avaliacao")
	private GrupoAvaliacao grupo;

	@CampoAtivo
	private boolean ativo;

	
	public ItemAvaliacaoProjeto(){}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
	public double getNotaMaxima() {
		return notaMaxima;
	}
	public void setNotaMaxima(double notaMaxima) {
		this.notaMaxima = notaMaxima;
	}
	public PerguntaAvaliacao getPergunta() {
		return pergunta;
	}
	public void setPergunta(PerguntaAvaliacao pergunta) {
		this.pergunta = pergunta;
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

	public GrupoAvaliacao getGrupo() {
	    return grupo;
	}

	public void setGrupo(GrupoAvaliacao grupo) {
	    this.grupo = grupo;
	}

	public QuestionarioAvaliacao getQuestionario() {
	    return questionario;
	}

	public void setQuestionario(QuestionarioAvaliacao questionario) {
	    this.questionario = questionario;
	}
	
	@Override
	public boolean equals(Object obj) {
	    return EqualsUtil.testEquals(this, obj, "id", "pergunta", "grupo");
	}

	@Override
	public int hashCode() {
	    return HashCodeUtil.hashAll(id, pergunta, grupo);
	}

}
