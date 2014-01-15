
package br.ufrn.sigaa.ead.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Avaliação semanal das disciplinas do discente em ensino
 * a distância
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(schema="ead", name="avaliacao_discente_ead")
public class AvaliacaoDiscenteEad implements PersistDB {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	/** Semana de aula em que foi feita a avaliação */
	private int semana;
	
	@Transient
	private List<ItemAvaliacaoEad> itens;
	
	@OneToMany(mappedBy="avaliacao")
	/** Notas obtidas nessa avaliação */
	private List<NotaItemAvaliacao> notas;
	
	@ManyToOne
	@JoinColumn(name="id_ficha")
	/** Ficha de avaliação na qual constará essa avaliação */
	private FichaAvaliacaoEad ficha;
	
	/** Observações feitas pelo tutor para essa semana */
	private String observacoes;
	
	@Column(name="data_cadastro")
	private Date data;
	
	@ManyToOne
	@JoinColumn(name="id_usuario")
	private Usuario usuario;

	@Transient
	private double[] medias;

	@Transient
	private double[] mediasUnidade2;
	
	/**
	 * Retorna a qual unidade esta avaliação pertence
	 * 
	 * @return
	 */
	public Integer getUnidade() {
		if (ficha.getMetodologia().isUmaProva())
			return 1;
		else if (ficha.getMetodologia().isDuasProvas() && semana <= ficha.getMetodologia().getNumeroAulasPrimeiraUnidade())
			return 1;
		else if (ficha.getMetodologia().isDuasProvas() && semana > ficha.getMetodologia().getNumeroAulasPrimeiraUnidade())
			return 2;
		
		return null;
	}	
	
	/**
	 * @return the data
	 */
	public Date getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Date data) {
		this.data = data;
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
	 * @return the itens
	 */
	public List<ItemAvaliacaoEad> getItens() {
		return itens;
	}

	/**
	 * @param itens the itens to set
	 */
	public void setItens(List<ItemAvaliacaoEad> itens) {
		this.itens = itens;
	}

	/**
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return the ficha
	 */
	public FichaAvaliacaoEad getFicha() {
		return ficha;
	}

	/**
	 * @param ficha the ficha to set
	 */
	public void setFicha(FichaAvaliacaoEad ficha) {
		this.ficha = ficha;
	}

	/**
	 * @return the notas
	 */
	public List<NotaItemAvaliacao> getNotas() {
		return notas;
	}

	/**
	 * @param notas the notas to set
	 */
	public void setNotas(List<NotaItemAvaliacao> notas) {
		this.notas = notas;
	}

	/**
	 * @return the medias
	 */
	public double[] getMedias() {
		return medias;
	}

	/**
	 * @param medias the medias to set
	 */
	public void setMedias(double[] medias) {
		this.medias = medias;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public int getSemana() {
		return semana;
	}

	public void setSemana(int semana) {
		this.semana = semana;
	}

	public double[] getMediasUnidade2() {
		return mediasUnidade2;
	}

	public void setMediasUnidade2(double[] mediasUnidade2) {
		this.mediasUnidade2 = mediasUnidade2;
	}

}
