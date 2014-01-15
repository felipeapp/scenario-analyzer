/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/10/2007
 */
package br.ufrn.arq.tasks;

import java.util.concurrent.Callable;

import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe abstrata utilizada para as tarefas assincronas que necessitam
 * ter acesso ao FacadeDelegate para executar as operacoes
 *
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 * @param <T>
 */
public abstract class TarefaAssincrona<T> implements Callable<T> {

	private FacadeDelegate facade;

	private UsuarioGeral usuario;

	private Integer sistema;

	/**
	 * Inicializa os atributos necessarios para a execucao da tarefa
	 *
	 * @param facade
	 * @param usuario
	 * @param sistema
	 */
	public void initialize(FacadeDelegate facade, UsuarioGeral usuario, Integer sistema) {
		this.facade = facade;
		this.usuario = usuario;
		this.sistema = sistema;
	}

	public abstract T call() throws Exception;

	public FacadeDelegate getFacade() {
		return facade;
	}

	public void setFacade(FacadeDelegate facade) {
		this.facade = facade;
	}

	public Integer getSistema() {
		return sistema;
	}

	public void setSistema(Integer sistema) {
		this.sistema = sistema;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}



	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}







}
