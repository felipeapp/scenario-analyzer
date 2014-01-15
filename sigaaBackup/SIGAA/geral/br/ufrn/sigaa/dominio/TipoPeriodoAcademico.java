/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 21/03/2013
*/

package br.ufrn.sigaa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Esta classe define os tipos de períodos existentes
 * 
 * @author Henrique André
 *
 */
@Entity
@Table(schema="comum", name = "tipo_periodo_academico", uniqueConstraints = {})
public class TipoPeriodoAcademico {

	public static int REGULAR = 1;
	public static int INTERVALAR  = 2;
	
	/**
	 * Id
	 */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo")
	private int id;
	
	/**
	 * Informação textual descrevendo o tipo de período
	 */
	@Column(name = "descricao")
	private String descricao;
	
	/**
	 * Indica se o objeto esta ativo
	 */
	@Column(name = "ativo")
	private boolean ativo = true;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	
}
