/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/03/2010
 */
package br.ufrn.sigaa.ensino.dominio;

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
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade que registra a associa��o entre um �ndice acad�mico
 * e um discente, armazenando o valor atual desse �ndice para o discente
 * associado.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="indice_academico_discente", schema="ensino")
public class IndiceAcademicoDiscente implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="graduacao.indice_academico_discente_seq") })
	/** Identificador da associa��o entre �ndice acad�mico e discente */
	private int id;
	
	/** �ndice acad�mico que possui o valor existente nessa classe */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_indice_academico")
	private IndiceAcademico indice;
	
	/** Discente cujo �ndice acad�mico possui o valor existente nessa classe */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_discente")
	private Discente discente;

	/** Valor atual do �ndice para o discente */
	private double valor;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public IndiceAcademico getIndice() {
		return indice;
	}

	public void setIndice(IndiceAcademico indice) {
		this.indice = indice;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
	
}
