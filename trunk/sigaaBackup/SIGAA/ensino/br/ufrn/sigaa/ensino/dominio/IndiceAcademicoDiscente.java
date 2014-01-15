/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 * Entidade que registra a associação entre um índice acadêmico
 * e um discente, armazenando o valor atual desse índice para o discente
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
	/** Identificador da associação entre índice acadêmico e discente */
	private int id;
	
	/** Índice acadêmico que possui o valor existente nessa classe */
	@ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="id_indice_academico")
	private IndiceAcademico indice;
	
	/** Discente cujo índice acadêmico possui o valor existente nessa classe */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_discente")
	private Discente discente;

	/** Valor atual do índice para o discente */
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
