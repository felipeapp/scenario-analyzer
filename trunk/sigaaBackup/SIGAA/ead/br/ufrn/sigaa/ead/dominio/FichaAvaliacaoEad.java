
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Representa uma ficha de avaliação de discente
 * de ensino a distância.
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(schema="ead", name="ficha_avaliacao_ead")
public class FichaAvaliacaoEad implements PersistDB {

	/** Define a quantidade de {@link SemanaAvaliacao} que um componente com carga horária menor que 100 possui. */
	public static final int QTD_SEMANAS_COMPONENTE_CURTO = 4;
    
	/** Define a quantidade de {@link SemanaAvaliacao} que um componente com carga horária igual ou superior a 100 possui. */
	public static final int QTD_SEMANAS_COMPONENTE_LONGO = 8;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_discente")
	/** Discente avaliado */
	private Discente discente;

	@OneToMany(mappedBy="ficha") @OrderBy("semana")
	/** Avaliações dessa ficha */
	private List<AvaliacaoDiscenteEad> avaliacoes;

	/** Período da avaliação */
	private int periodo;
	
	/** Ano da avaliação */
	private int ano;

	@ManyToOne
	@JoinColumn(name="id_componente_avaliacao")
	/** Componente curricular sendo avaliado (utilizado apenas em caso de curso do tipo bacharelado) */
	private ComponenteCurricular componente;
	
	@Column(name="data_cadastro")
	private Date data;
	
	@ManyToOne
	@JoinColumn(name="id_usuario")
	private Usuario usuario;
	
	@Transient
	private double[] medias;
	
	@Transient
	private double[] mediasUnidade2;
	
	@Transient
	private MetodologiaAvaliacao metodologia;
	
	/**
	 * @return the avaliacoes
	 */
	public List<AvaliacaoDiscenteEad> getAvaliacoes() {
		return avaliacoes;
	}

	/**
	 * @param avaliacoes the avaliacoes to set
	 */
	public void setAvaliacoes(List<AvaliacaoDiscenteEad> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	/**
	 * @return the discente
	 */
	public Discente getDiscente() {
		return discente; 
	}

	/**
	 * @param discente the discente to set
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
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
	 * @return the ano
	 */
	public int getAno() {
		return ano;
	}

	/**
	 * @param ano the ano to set
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/**
	 * @return the periodo
	 */
	public int getPeriodo() {
		return periodo;
	}

	/**
	 * @param periodo the periodo to set
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
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

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	public double[] getMediasUnidade2() {
		return mediasUnidade2;
	}

	public void setMediasUnidade2(double[] mediasUnidade2) {
		this.mediasUnidade2 = mediasUnidade2;
	}

	public MetodologiaAvaliacao getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(MetodologiaAvaliacao metodologia) {
		this.metodologia = metodologia;
	}
	
}
