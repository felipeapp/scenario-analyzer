/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 05/03/2013 
 */
package br.ufrn.sigaa.prodocente.lattes.dominio;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que associa uma pessoa do SIGAA ao identificador do seu CV na plataforma Lattes do CNPq.
 * Utilizada pelo cliente Web Service para extra��o de curr�culos lattes.
 * 
 * @author Leonardo
 *
 */
@Entity
@Table(name = "pessoa_lattes", schema = "prodocente")
public class PessoaLattes implements Validatable {

	/**
	 * Chave prim�ria.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="prodocente.pessoa_lattes_id_seq") })
	@Column(name = "id")
	private int id;
	
	/**
	 * Pessoa que possui um curr�culo lattes.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;
	
	/**
	 * Pessoa que possui um curr�culo lattes.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;
	
	/**
	 * Identificador do curr�culo lattes da pessoa no CNPq.
	 */
	@Column(name = "id_cnpq")
	private String idCnpq;
	
	/**
	 * Indica se a pessoa autorizou o acesso do sistema ao seu curr�culo lattes.
	 */
	@Column(name = "autoriza_acesso")
	private Boolean autorizaAcesso;
	
	/**
	 * Instante da �ltima atualiza��o realizada no curr�culo lattes.
	 */
	@Column(name = "ultima_atualizacao")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date ultimaAtualizacao;
	
	/**
	 * Instante da �ltima verifica��o realizada pela rotina de importa��o.
	 */
	@Column(name = "ultima_verificacao")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date ultimaVerificacao;
	
	/**
	 * Ano de refer�ncia para a importa��o autom�tica.
	 * S�o importadas todas as produ��es cujo ano seja igual ou maior que o ano de refer�ncia.
	 * 
	 */
	@Column(name = "ano_referencia")
	private Integer anoReferencia;
	
	/**
	 * Utilizado somente durante a rotina de importa��o para
	 * identificar se o curr�culo da pessoa considerada deve ser atualizado ou n�o.
	 */
	@Transient
	private Boolean atualizar;
	
	/**
	 * Utilizado somente durante a rotina de importa��o para
	 * registrar mensagens de observa��es e ocorr�ncias relatadas por email ao administrador do sistema.
	 */
	@Transient
	private String observacoes;
	
	/**
	 * Utilizado somente durante a rotina de importa��o para
	 * armazenar temporariamente o arquivo do exportado da plataforma lattes. 
	 */
	@Transient
	private byte[] cvZipado;
	
	/**
	 * Construtor padr�o.
	 */
	public PessoaLattes() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getIdCnpq() {
		return idCnpq;
	}

	public void setIdCnpq(String idCnpq) {
		this.idCnpq = idCnpq;
	}

	public Boolean getAutorizaAcesso() {
		return autorizaAcesso;
	}

	public void setAutorizaAcesso(Boolean autorizaAcesso) {
		this.autorizaAcesso = autorizaAcesso;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public ListaMensagens validate() {
		return null;
	}

	public Boolean getAtualizar() {
		return atualizar;
	}

	public void setAtualizar(Boolean atualizar) {
		this.atualizar = atualizar;
	}

	public byte[] getCvZipado() {
		return cvZipado;
	}

	public void setCvZipado(byte[] cvZipado) {
		this.cvZipado = cvZipado;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Date getUltimaVerificacao() {
		return ultimaVerificacao;
	}

	public void setUltimaVerificacao(Date ultimaVerificacao) {
		this.ultimaVerificacao = ultimaVerificacao;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(Integer anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

}
