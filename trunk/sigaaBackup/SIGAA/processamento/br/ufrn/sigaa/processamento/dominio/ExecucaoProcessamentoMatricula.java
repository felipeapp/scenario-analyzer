/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 29/08/2011
 */
package br.ufrn.sigaa.processamento.dominio;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;

/**
 * Classe que armazena informações sobre os momentos em que os 
 * processamentos de matrícula foram realizados.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="execucao_processamento_matricula", schema="graduacao")
public class ExecucaoProcessamentoMatricula implements PersistDB {

	/** Identificador */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="graduacao.execucao_processamento_matricula_seq")})
	private int id;
	
	/** Data da realização do processamento. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	/** Em que modo o processamento de matrícula foi realizado. Graduação, EAD, etc. */
	private String modo;
	
	/** Tipo de processamento: pré-processamento, processamento, pós-processamento, etc. */
	private int tipo;
	
	/** Ano para o qual foi realizado o processamento. */
	private int ano;
	
	/** Período para o qual foi realizado o processamento. */
	private int periodo;
	
	/** Se o processamento é de matrícula ou rematrícula. */
	private boolean rematricula;
	
	/** Registro de entrada do usuário que realizou o processamento de matrícula. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada registroEntrada;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getModo() {
		return modo;
	}

	public void setModo(String modo) {
		this.modo = modo;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public boolean isRematricula() {
		return rematricula;
	}

	public void setRematricula(boolean rematricula) {
		this.rematricula = rematricula;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	public int getTipo() {
		return tipo;
	}
	
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
}
