package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;

/*******************************************************************************
 * <p>
 * Nota recebidas pelo avaliador de sobre um determinado item da avaliação de um
 * projeto.
 * </p>
 * 
 ******************************************************************************/
@Entity
@Table(name = "nota_item", schema = "monitoria")
public class NotaItemMonitoria implements Validatable {

	// Fields

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_nota_item")
	private int id;

	@Column(name = "nota", precision = 4)
	private double nota;

	@ManyToOne
	@JoinColumn(name = "id_avaliacao" , nullable = true)
	private AvaliacaoMonitoria avaliacao;
	
	@Column(name="desconsiderar")
	private boolean desconsiderar;	
	
	@ManyToOne
	@JoinColumn(name = "id_avaliacao_atividade", nullable = true)
	private AvaliacaoAtividade avaliacaoAtividade;

	@ManyToOne
	@JoinColumn(name = "id_item_avaliacao")
	private ItemAvaliacaoMonitoria itemAvaliacao;

	// Constructors

	/** default constructor */
	public NotaItemMonitoria() {
	}

	/** full constructor */
	public NotaItemMonitoria(int idNotaItem, float nota,
			AvaliacaoMonitoria avaliacao, ItemAvaliacaoMonitoria itemAvaliacao) {
		this.id = idNotaItem;
		this.nota = nota;
		this.avaliacao = avaliacao;
		this.itemAvaliacao = itemAvaliacao;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int idNotaItem) {
		this.id = idNotaItem;
	}

	public double getNota() {
		return this.nota;
	}

	public void setNota(double nota) {
		this.nota = nota;
	}

	public AvaliacaoMonitoria getAvaliacao() {
		return this.avaliacao;
	}

	public void setAvaliacao(AvaliacaoMonitoria avaliacao) {
		this.avaliacao = avaliacao;
	}

	public ItemAvaliacaoMonitoria getItemAvaliacao() {
		return this.itemAvaliacao;
	}

	public void setItemAvaliacao(ItemAvaliacaoMonitoria itemAvaliacao) {
		this.itemAvaliacao = itemAvaliacao;
	}

	public ListaMensagens validate() {
		return null;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((avaliacao == null) ? 0 : avaliacao.hashCode());
		result = PRIME * result
				+ ((itemAvaliacao == null) ? 0 : itemAvaliacao.hashCode());
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
		final NotaItemMonitoria other = (NotaItemMonitoria) obj;
		if (avaliacao == null) {
			if (other.avaliacao != null)
				return false;
		} else if (!avaliacao.equals(other.avaliacao))
			return false;
		if (itemAvaliacao == null) {
			if (other.itemAvaliacao != null)
				return false;
		} else if (!itemAvaliacao.equals(other.itemAvaliacao))
			return false;
		return true;
	}

	public AvaliacaoAtividade getAvaliacaoAtividade() {
		return avaliacaoAtividade;
	}

	public void setAvaliacaoAtividade(AvaliacaoAtividade avaliacaoAtividade) {
		this.avaliacaoAtividade = avaliacaoAtividade;
	}

	public boolean isDesconsiderar() {
		return desconsiderar;
	}

	public void setDesconsiderar(boolean desconsiderar) {
		this.desconsiderar = desconsiderar;
	}
	
	@Override
	public String toString() {
	    return String.valueOf(nota);
	}
	

}
