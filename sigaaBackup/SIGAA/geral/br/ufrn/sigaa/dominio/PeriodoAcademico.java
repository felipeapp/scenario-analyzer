/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
* Created on 21/03/2013
*/

package br.ufrn.sigaa.dominio;

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

/**
 * Esta classe representa os per�odos que fazem parte do Calend�rio Acad�mico
 * 
 * @author Henrique Andr�
 *
 */
@Entity
@Table(schema="comum", name = "periodo_academico", uniqueConstraints = {})
public class PeriodoAcademico {

	/**
	 * Id
	 */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_periodo_academico")
	private int id;
	
	/**
	 * Valor do Periodo Academico
	 */
	@Column(name = "periodo")
	private int periodo;
	
	/**
	 * Ordem deste periodo em rela��o a todos os per�odos
	 */
	@Column(name = "ordem")
	private int ordem;
	
	/**
	 * Indica se o objeto esta ativo
	 */
	@Column(name = "ativo")
	private boolean ativo = true;
	
	/**
	 * Informa o tipo do periodo
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo")
	private TipoPeriodoAcademico tipo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public TipoPeriodoAcademico getTipo() {
		return tipo;
	}

	public void setTipo(TipoPeriodoAcademico tipo) {
		this.tipo = tipo;
	}
	
	public boolean isRegular() {
		return tipo != null && tipo.getId() == TipoPeriodoAcademico.REGULAR;
	}

	public boolean isIntervalar() {
		return tipo != null && tipo.getId() == TipoPeriodoAcademico.INTERVALAR;
	}	
	
}
