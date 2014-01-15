/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 06/06/2007 
 */
package br.ufrn.sigaa.arq.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe que representa a execução de alguma tarefa em lote,
 * geralmente executadas de modo assíncrono.
 * @author Ricardo Wendell
 */
@Entity
@Table(schema = "comum", name = "operacao_lote", uniqueConstraints = {})
public class OperacaoLote implements PersistDB {

	public static final int EM_ANDAMENTO = 1;
	public static final int FINALIZADA = 2;
	public static final int ERRO = 3;

	private int id;

	/** código da operação que está sendo executada 
	 * os códigos das operações estão em constantes na classe {@link br.ufrn.sigaa.arq.dominio.CodigoOperacaoLote} */
	private int codigoOperacao;

	/** id do lote que está sendo processado, normalmente coincide com o id do objeto dono do lote */
	private int idLote;

	/** tamanho do lote que será processado */
	private int tamanhoLote;

	/** data de início do processamento do lote */
	private Date inicio;

	/** data de fim do processamento do lote */
	private Date fim;

	/** status do lote: 
	 * EM_ANDAMENTO = 1
	 * FINALIZADA = 2
	 * ERRO = 3*/
	private int status;

	/** armazena o stacktrace caso ocorra algum erro no processamento do lote */
	private String log;

	public OperacaoLote() {

	}

	@Column(name = "codigo_operacao", unique = false, nullable = true, insertable = true, updatable = true)
	public int getCodigoOperacao() {
		return codigoOperacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fim", unique = false, insertable = true, updatable = true)
	public Date getFim() {
		return fim;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_operacao_lote", nullable = false)
	public int getId() {
		return id;
	}

	@Column(name = "id_lote", unique = false, nullable = true, insertable = true, updatable = true)
	public int getIdLote() {
		return idLote;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "inicio", unique = false, insertable = true, updatable = true)
	public Date getInicio() {
		return inicio;
	}

	public String getLog() {
		return log;
	}

	public int getStatus() {
		return status;
	}

	@Column(name = "tamanho_lote", unique = false, nullable = true, insertable = true, updatable = true)
	public int getTamanhoLote() {
		return tamanhoLote;
	}

	public void setCodigoOperacao(int codigoOperacao) {
		this.codigoOperacao = codigoOperacao;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIdLote(int idLote) {
		this.idLote = idLote;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setTamanhoLote(int tamanhoLote) {
		this.tamanhoLote = tamanhoLote;
	}



}
