/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/04/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.Date;

import javax.persistence.Column;
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
import br.ufrn.arq.seguranca.log.CriadoEm;

/**
 * Entidade que representa uma mensagem de {@link OrientacaoAcademica} enviada pelo orientador ao orientando.
 * 
 * @author bernardo
 *
 */
@Entity
@Table(name = "mensagem_orientacao", schema = "graduacao")
public class MensagemOrientacao implements PersistDB {

	/** Chave primária da entidade. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	@Column(name = "id_mensagem_orientacao")
	private int id;
	
	/** Orientação acadêmica que armazena o docente e o discente da mensagem. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_orientacao_academica")
	private OrientacaoAcademica orientacaoAcademica;
	
	/** Assunto da mensagem. */
	private String assunto;
	
	/** Corpo da mensagem. */
	private String mensagem;
	
	/** Data de cadastro da entidade. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OrientacaoAcademica getOrientacaoAcademica() {
		return orientacaoAcademica;
	}

	public void setOrientacaoAcademica(OrientacaoAcademica orientacaoAcademica) {
		this.orientacaoAcademica = orientacaoAcademica;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
}
