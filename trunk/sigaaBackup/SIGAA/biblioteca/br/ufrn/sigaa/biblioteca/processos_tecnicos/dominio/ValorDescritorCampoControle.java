/*
 * ValorDescritorCampoControle.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
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
 *      Os valores que os dados apontados pelo descritor podem assumir com as suas 
 * respectivas descrições
 *
 * @author jadson
 * @since 28/07/2008
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "valor_descritor_campo_controle", schema = "biblioteca")
public class ValorDescritorCampoControle implements PersistDB{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_valor_descritor_campo_controle")
	private int id;

	/** O valor em si*/
	@Column(name = "valor", nullable=false)
	private String valor;

	/** As informações resumidas que todo valor tem*/
	@Column(name = "descricao", nullable=false)
	private String descricao;

	/** Algumas informações extras que o valor pode possuir */
	private String info;
	
	
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_descritor_campo_controle", referencedColumnName="id_descritor_campo_controle")
	private DescritorCampoControle descritorCampoControle;

	


	
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

	public DescritorCampoControle getDescritorCampoControle() {
		return descritorCampoControle;
	}

	public void setDescritorCampoControle(
			DescritorCampoControle descritorCampoControle) {
		this.descritorCampoControle = descritorCampoControle;
	}

	public void setValor(String valor) {
		this.valor = unescapeHTML(valor);
	}

	public void setDescricao(String descricao) {
		this.descricao = unescapeHTML(descricao);
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = unescapeHTML(info);
	}

}
