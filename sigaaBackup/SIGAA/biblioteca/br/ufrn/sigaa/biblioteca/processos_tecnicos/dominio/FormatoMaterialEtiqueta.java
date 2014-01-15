/*
 * FormatoMaterialEtiqueta.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.List;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 *
 *    Classe intermediaria que guarda o conjunto de descritores de uma etiqueta para 
 * cada formato de titulo existente, porque para alguns campos de controle o conjunto de descritores 
 * depende do formato do titulo
 *
 * @author jadson
 * @since 14/08/2008
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "formato_material_etiqueta", schema = "biblioteca")
public class FormatoMaterialEtiqueta implements PersistDB{


	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })	
	@Column(name="id_formato_material_etiqueta")
	private int id;


	/* O formato do titulo catalografico = LIVRO, MUSICA etc. So possui esse relacionamento se a 
	 * etiqueta for a 006 ou 008, cujos descritores variam dependendo do formato */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_formato_material", referencedColumnName="id_formato_material")
	private FormatoMaterial formatoMaterial;


	/* A Etiqueta a que se refere */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_etiqueta", referencedColumnName="id_etiqueta")
	private Etiqueta etiqueta;

	
    /* Apenas para facilitar as consultas feitas em sql, para não precisa consultar a tabela etiqueta,
     * nem o formato material para saber a quem se refere. Não é usado no sistema.*/
	@SuppressWarnings("unused")
	private String descricao;
	
	
	/*
	 * Possui varios descritores que informam onde comeca e onde termina e o que significa os 
	 * dados que estão todos juntos no campo de dados do registro de controle. Ele estão aqui
	 * porque os descritores dependem do tipo de material
	 */
	@OneToMany(mappedBy="formatoMaterialEtiqueta", cascade={CascadeType.REMOVE})
	private List<DescritorCampoControle> descritoresCampoControle;

	
	
	/* 
	 * Para cada formato material e etiqueta vai existir um conjunto de valores padrao para campos de controle.
	 * 
	 * Usado no 006 e 008 bibliografico, 008 de autoridade e lider de autoridade.
	 *  
	 * */
	@SuppressWarnings("unused")
	@OneToMany(mappedBy="formatoMaterialEtiqueta", cascade={CascadeType.REMOVE})
	private List<ValorPadraoCampoControle> valoresPadraoControle;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<DescritorCampoControle> getDescritoresCampoControle() {
		return descritoresCampoControle;
	}

	public FormatoMaterial getFormatoMaterial() {
		return formatoMaterial;
	}

	public void setFormatoMaterial(FormatoMaterial formatoMaterial) {
		this.formatoMaterial = formatoMaterial;
	}

	public Etiqueta getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}

	public void setDescritoresCampoControle(List<DescritorCampoControle> descritoresCampoControle) {
		this.descritoresCampoControle = descritoresCampoControle;
	}

}
