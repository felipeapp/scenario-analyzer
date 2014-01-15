/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 21/03/2005
 */
package br.ufrn.arq.seguranca.log;

import java.util.Date;

/**
 * Esta classe armazena as informações sobre o Log de Movimentos. Tudo que é
 * executado pelos processadores é logado nesta tabela.
 *
 * @author Gleydson Lima
 *
 */
public class LogMovimento implements LogUFRN {

	/** Identificador */
	private int id;

	/** Código do processador que se está realizando a operação.*/
	private int codMovimento;

	/** Registro de entrada do usuário que faz a operação */
	private int registroEntrada;

	/** Data da alteração */
	private Date data;

	private int idMovimento;

	/** Sistema em que se realiza a operação. 1 - SIPAC, 2 - PROTOCOLO, 3 - SCO, 4 - SIGAA */
	private int sistema;

	public int getSistema() {
		return sistema;
	}

	public void setSistema(int sistema) {
		this.sistema = sistema;
	}

	public int getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(int codMovimento) {
		this.codMovimento = codMovimento;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdMovimento() {
		return idMovimento;
	}

	public void setIdMovimento(int idMovimento) {
		this.idMovimento = idMovimento;
	}

	public int getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(int registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

}