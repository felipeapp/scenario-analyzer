/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/01/2010
 */
package br.ufrn.arq.email;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Armazena informações sobre os erros que aconteceram
 * ao enviar e-mails pelos sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Entity @Table(name="emails_com_erro", schema="infra")
public class ErroEnvioEmail implements PersistDB {

	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="infra.seq_email_enviado") })
	private int id;
	
	@Column(name="para_email")
	private String paraEmail;
	
	@Column(name="para_nome")
	private String paraNome;
	
	private String assunto;
	
	private String conteudo;
	
	@Column(name="enviado_em")
	@Temporal(TemporalType.TIMESTAMP)
	private Date enviadoEm;
	
	private String excecao;
	
	@Transient
	private Date dataInicio;

	@Transient
	private Date dataFim;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getParaEmail() {
		return paraEmail;
	}

	public void setParaEmail(String paraEmail) {
		this.paraEmail = paraEmail;
	}

	public String getParaNome() {
		return paraNome;
	}

	public void setParaNome(String paraNome) {
		this.paraNome = paraNome;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Date getEnviadoEm() {
		return enviadoEm;
	}

	public void setEnviadoEm(Date enviadoEm) {
		this.enviadoEm = enviadoEm;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public String getExcecao() {
		return excecao;
	}

	public void setExcecao(String excecao) {
		this.excecao = excecao;
	}
	
}
