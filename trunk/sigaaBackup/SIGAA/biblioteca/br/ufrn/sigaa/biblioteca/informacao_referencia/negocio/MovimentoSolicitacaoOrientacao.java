/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 22/11/2010
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import java.util.Date;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 * Movimento para acesso ao processador de ações relacionadas à Catalogação.
 *
 * @author Felipe Rivas
 */
public class MovimentoSolicitacaoOrientacao extends MovimentoCadastro {

	/** A data e horário de início do agendamento, definido no atendimento da solicitação pelo bibliotecário */
	private Date dataInicio;
	
	/** O horário de término do agendamento, definido no atendimento da solicitação pelo bibliotecário */
	private Date dataFim;
	
	/**  Os comentários feitos pelo bibliotecário no atendimento da solicitação */
	private String comentariosBibliotecario;
	
	/** A biblioteca origem, utilizado no caso de uso de tranferência de solicitações entre bibliotecas */
	private Biblioteca bibliotecaOrigem;
	
	/** A biblioteca destino, utilizado no caso de uso de tranferência de solicitações entre bibliotecas */
	private Biblioteca bibliotecaDestino;
	
	/** O motivo informado pelo bibliotecário para cancelar a solicitação.*/
	private String motivoCancelamento;
	
	
	//  sets e gets //
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public String getComentariosBibliotecario() {
		return comentariosBibliotecario;
	}
	public void setComentariosBibliotecario(String comentariosBibliotecario) {
		this.comentariosBibliotecario = comentariosBibliotecario;
	}
	public Biblioteca getBibliotecaOrigem() {
		return bibliotecaOrigem;
	}
	public void setBibliotecaOrigem(Biblioteca bibliotecaOrigem) {
		this.bibliotecaOrigem = bibliotecaOrigem;
	}
	public Biblioteca getBibliotecaDestino() {
		return bibliotecaDestino;
	}
	public void setBibliotecaDestino(Biblioteca bibliotecaDestino) {
		this.bibliotecaDestino = bibliotecaDestino;
	}
	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}
	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}
	
}
