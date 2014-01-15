package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;

@Entity(name="br.ufrn.sigaa.projetos.dominio.NotaItemAvaliacao")
@Table(name = "nota_item_avaliacao", schema = "projetos")
public class NotaItemAvaliacao implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_nota_item_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_item_avaliacao")
	private ItemAvaliacaoProjeto itemAvaliacao = new ItemAvaliacaoProjeto();

	@ManyToOne
	@JoinColumn(name = "id_avaliacao")
	private Avaliacao avaliacao = new Avaliacao();

	private double nota;
	
	@CampoAtivo
	private boolean ativo;
	
	public NotaItemAvaliacao() {
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

	public ItemAvaliacaoProjeto getItemAvaliacao() {
	    return itemAvaliacao;
	}

	public void setItemAvaliacao(ItemAvaliacaoProjeto itemAvaliacao) {
	    this.itemAvaliacao = itemAvaliacao;
	}

	public Avaliacao getAvaliacao() {
	    return avaliacao;
	}

	public void setAvaliacao(Avaliacao avaliacao) {
	    this.avaliacao = avaliacao;
	}

	public double getNota() {
	    return nota;
	}

	public void setNota(double nota) {
	    this.nota = nota;
	}

}
