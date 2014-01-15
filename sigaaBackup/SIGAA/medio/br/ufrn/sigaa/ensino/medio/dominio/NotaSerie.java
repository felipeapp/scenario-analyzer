/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 19/07/2011
* 
*/
package br.ufrn.sigaa.ensino.medio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.RegraNota;

/**
 * Classe que representa as notas do discente de cada unidade
 *
 * @author Arlindo
 */
@Entity
@Table(name = "nota_serie", schema = "medio")
public class NotaSerie implements PersistDB, Comparable<NotaSerie> {
	
	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_nota_serie", nullable = false)
	private int id;
	
	/** Dados da nota da unidade */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_nota_unidade")
	private NotaUnidade notaUnidade;
	
	/** Regras das notas do curso do discente */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_regra_nota")
	private RegraNota regraNota;

	/** Atributo responsável por verificar se a nota série estará habilitada para sua inserção ou alteração. */
	@Transient
	private boolean podeEditar = true;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public NotaUnidade getNotaUnidade() {
		return notaUnidade;
	}

	public void setNotaUnidade(NotaUnidade notaUnidade) {
		this.notaUnidade = notaUnidade;
	}

	public RegraNota getRegraNota() {
		return regraNota;
	}

	public void setRegraNota(RegraNota regraNota) {
		this.regraNota = regraNota;
	}

	public boolean isPodeEditar() {
		return podeEditar;
	}

	public void setPodeEditar(boolean podeEditar) {
		this.podeEditar = podeEditar;
	}

	@Override
	public int compareTo(NotaSerie ns) {
		return (notaUnidade.getNota()).compareTo(ns.notaUnidade.getNota());
	}



}
