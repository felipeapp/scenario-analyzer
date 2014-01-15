
package br.ufrn.sigaa.ead.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Representa uma nota de um discente em um item de avaliação
 * em uma disciplina
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(schema="ead", name="nota_item_avaliacao_ead")
public class NotaItemAvaliacao implements PersistDB {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_avaliacao")
	/** Avaliação que foi realizada */
	private AvaliacaoDiscenteEad avaliacao;
	
	@ManyToOne
	@JoinColumn(name="id_componente")
	/** Componente curricular avaliado */
	private ComponenteCurricular componente;
	
	/** Nota recebida pelo aluno */
	private double nota;
	
	@ManyToOne
	@JoinColumn(name="id_item")
	/** Item de avaliação que está sendo avaliado */
	private ItemAvaliacaoEad item;

	/**
	 * @return the avaliacao
	 */
	public AvaliacaoDiscenteEad getAvaliacao() {
		return avaliacao;
	}

	/**
	 * @param avaliacao the avaliacao to set
	 */
	public void setAvaliacao(AvaliacaoDiscenteEad avaliacao) {
		this.avaliacao = avaliacao;
	}

	/**
	 * @return the componente
	 */
	public ComponenteCurricular getComponente() {
		return componente;
	}

	/**
	 * @param componente the componente to set
	 */
	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the item
	 */
	public ItemAvaliacaoEad getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(ItemAvaliacaoEad item) {
		this.item = item;
	}

	/**
	 * @return the nota
	 */
	public double getNota() {
		return nota;
	}

	/**
	 * @param nota the nota to set
	 */
	public void setNota(double nota) {
		this.nota = nota;
	}

		
}
