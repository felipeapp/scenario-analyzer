/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/03/2010
 * Autor:     David Pereira
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que registra as altera��es nos �ndices acad�micos de alunos de gradua��o
 * O nome da tabela � atualiza��o pois ela grava todas as atualiza��es e n�o s� as 
 * altera��es de m�dia.�
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="atualizacao_indice_academico_discente", schema="ensino")
public class AtualizacaoIndiceAcademicoDiscente implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="graduacao.atualizacao_indice_academico_discente_seq") })
	private int id;
	
	/** �ndice que foi atualizado */
	@ManyToOne @JoinColumn(name="id_indice_academico_discente")
	private IndiceAcademicoDiscente indice;
	
	/** Data da atualiza��o */
	private Date data;
	
	/** Novo valor do �ndice */
	private double valor;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public IndiceAcademicoDiscente getIndice() {
		return indice;
	}

	public void setIndice(IndiceAcademicoDiscente indice) {
		this.indice = indice;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
	
}
