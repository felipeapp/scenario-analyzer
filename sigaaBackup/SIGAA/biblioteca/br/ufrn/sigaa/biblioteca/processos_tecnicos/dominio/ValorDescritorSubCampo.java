/*
 * ValorDescritorSubCampo.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/07/2008
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import static br.ufrn.arq.util.StringUtils.unescapeHTML;

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

import br.ufrn.arq.dominio.PersistDB;

/**
 *
 *      <p>Guarda o conjunto de valores que um subcampo pode assumir. Usado apenas para alguns 
 * subcampos que possuem valores pré definidos.</p> 
 * <p>
 * 		<i> Exemplo: subcampos com a etiqueta 046$a, segundo o padrão MARC, só deve possuir os valores: <br/>
 * 
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp  <strong>c</strong> - Data atual/ data de copuright <br/>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp  <strong>i</strong> - Datas abrangendo uma coleção <br/>
 * &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp  etc... <br/><br/>
 *          Diferentemente dos subcampos 245$a que possuem o título da obra e podem ficar com qualquer texto digitado pelo usuário.<br/>
 * 		</i>
 * </p> 
 * 
 * @author jadson
 * @since 28/07/2008
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "valor_descritor_sub_campo", schema = "biblioteca")
public class ValorDescritorSubCampo  implements PersistDB{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_valor_descritor_sub_campo")
	private int id;

	/** Um dos possíveis valores que os dados de um subcampo pode assumir, para os casos em que os subcampos tem valores pré determinados pelo padrão MARC.*/
	@Column(name = "valor", nullable=false)
	private String valor;

	/** O que o valor significa, para mostrar na ajuda da catalogação */
	@Column(name = "descricao", nullable=false)
	private String descricao;

	/** Alguma informação extra sobre o valor para mostrar na ajuda da catalogação. <i>(Opcional)</i>*/
	private String info;

	/** A que descritor do subcampo que esse valorDescritor pertence.*/
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_descritor_sub_campo", referencedColumnName="id_descritor_sub_campo")
	private DescritorSubCampo descritorSubCampo;
	
	
	
	public ValorDescritorSubCampo(){
	}
	
	
	public ValorDescritorSubCampo(String valor) {
		this.valor = valor;
	}
	
	// sets e gets

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;		
	}

	public String getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		return result;
	}

	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = unescapeHTML(info);
	}


	public DescritorSubCampo getDescritorSubCampo() {
		return descritorSubCampo;
	}


	public void setDescritorSubCampo(DescritorSubCampo descritorSubCampo) {
		this.descritorSubCampo = descritorSubCampo;
	}


	public void setValor(String valor) {
		this.valor = unescapeHTML(valor);
	}


	public void setDescricao(String descricao) {
		this.descricao = unescapeHTML(descricao);
	}

	/**
	 * Igual pelo valor
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValorDescritorSubCampo other = (ValorDescritorSubCampo) obj;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}

	
	
}
