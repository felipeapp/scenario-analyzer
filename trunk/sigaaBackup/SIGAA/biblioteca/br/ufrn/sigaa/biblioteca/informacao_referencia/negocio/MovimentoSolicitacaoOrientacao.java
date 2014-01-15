/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/11/2010
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import java.util.Date;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 * Movimento para acesso ao processador de a��es relacionadas � Cataloga��o.
 *
 * @author Felipe Rivas
 */
public class MovimentoSolicitacaoOrientacao extends MovimentoCadastro {

	/** A data e hor�rio de in�cio do agendamento, definido no atendimento da solicita��o pelo bibliotec�rio */
	private Date dataInicio;
	
	/** O hor�rio de t�rmino do agendamento, definido no atendimento da solicita��o pelo bibliotec�rio */
	private Date dataFim;
	
	/**  Os coment�rios feitos pelo bibliotec�rio no atendimento da solicita��o */
	private String comentariosBibliotecario;
	
	/** A biblioteca origem, utilizado no caso de uso de tranfer�ncia de solicita��es entre bibliotecas */
	private Biblioteca bibliotecaOrigem;
	
	/** A biblioteca destino, utilizado no caso de uso de tranfer�ncia de solicita��es entre bibliotecas */
	private Biblioteca bibliotecaDestino;
	
	/** O motivo informado pelo bibliotec�rio para cancelar a solicita��o.*/
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
