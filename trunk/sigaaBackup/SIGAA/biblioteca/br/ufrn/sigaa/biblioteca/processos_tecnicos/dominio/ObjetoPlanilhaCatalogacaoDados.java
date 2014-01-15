/*
 * ObjetoPlanilhaCamposDados.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

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
import br.ufrn.arq.util.StringUtils;

/**
 *
 *        Classe que guarda as informações dos campos de dados de uma planilha de catalogação.
 *        
 * @author jadson
 * @since 11/09/2008
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "objeto_planilha_catalogacao_dados", schema = "biblioteca")
public class ObjetoPlanilhaCatalogacaoDados implements Validatable,  Comparable<ObjetoPlanilhaCatalogacaoDados>{


	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_objeto_planilha_catalogacao_dados", nullable = false)
	private int id;


	/**  Guarda apenas a tag da etiqueta que o campos de controle possuirá */
	@Column(name = "tag_etiqueta", nullable = false)
	private String tagEtiqueta;


	/** Guarda os valores default dos indicadores se eles existirem */
	@Column(name = "indicador1", nullable = false)
	private Character indicador1 = new Character(' ');

	@Column(name = "indicador2", nullable = false)
	private Character indicador2 = new Character(' ');


	/**
	 *   Guarda as informações de quais subcampos devem já aparecer criados na tela de catalogacão
	 * para o campo de dados que esse objeto irá virá, na forma de "$a$b$c$d", depois a idéia é 
	 * separar a string por '$' e cria um new subCampo() com o codigo 'a', 'b', 'c' e 'd' nesse
	 * exemplo.
	 */
	@Column(name = "sub_campos", nullable = false)
	private String subCampos;



	/** Referencia a planilha que ele pertence para torna o relacionamento bidirecional */
	@ManyToOne
	@JoinColumn(name = "id_planilha_catalogacao", referencedColumnName = "id_planilha_catalogacao", nullable=false)
	private PlanilhaCatalogacao planilha;



	/**
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if(StringUtils.isEmpty(tagEtiqueta)){
			lista.addErro("É preciso informar a etiqueta do campos de controle");
		}

		return lista;
	}
	
	
	/**
	 * Método que compara dois objetos de dados da planilha pela tag.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ObjetoPlanilhaCatalogacaoDados o) {
	
		if(o == null)
			return -1; // o atual aparece primeiro
		else{
			return this.tagEtiqueta.compareTo(o.getTagEtiqueta());
		}
	}
	
	
	

	// sets e gets indesejados

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTagEtiqueta() {
		return tagEtiqueta;
	}

	public void setTagEtiqueta(String tagEtiqueta) {
		this.tagEtiqueta = tagEtiqueta;
	}

	public Character getIndicador1() {
		return indicador1;
	}

	public void setIndicador1(Character indicador1) {
		this.indicador1 = indicador1;
	}

	public Character getIndicador2() {
		return indicador2;
	}

	public void setIndicador2(Character indicador2) {
		this.indicador2 = indicador2;
	}

	public String getSubCampos() {
		return subCampos;
	}

	public void setSubCampos(String subCampos) {
		this.subCampos = subCampos;
	}

	public PlanilhaCatalogacao getPlanilha() {
		return planilha;
	}

	public void setPlanilha(PlanilhaCatalogacao planilha) {
		this.planilha = planilha;
	}



}
