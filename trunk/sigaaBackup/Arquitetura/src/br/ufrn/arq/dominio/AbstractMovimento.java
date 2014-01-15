/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 29/10/2004
 */
package br.ufrn.arq.dominio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import br.ufrn.arq.dao.ClosableResource;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe Abstrata pai de todos os ativadores de comando. Esta Action serve para
 * abstrair as funcionalidades que todos os ativadores de comando devem ter.
 *
 * @author Gleydson Lima
 *
 */
public abstract class AbstractMovimento implements Movimento {

	/** Lista de recursos que serão abertos no processador e que podem ser fechados após a execução do processador. */
	private List<ClosableResource> closableResources;
	
	/** Referência ao application context do Spring. Necessário para pegar beans do Spring nos processadores */
	private ApplicationContext applicationContext;
	
	/** Usuário que está logado no Sistema - Setado pela Arquitetura * */
	private UsuarioGeral usuarioLogado;

	private List<Comando> comandosBloqueados;
	
	private ListaMensagens mensagens;
	
	/** Usuário que cadastrou o movimento */
	private UsuarioGeral usuario;

	/**
	 * Faz com que a requisição não seja enviada novamente. Serve para eliminar
	 * problemas de submit duplicados.
	 */
	private boolean lockSubmit;

	private Comando codMovimento;

	private int subsistema;

	private int sistema;

	public int getSistema() {
		return sistema;
	}

	public void setSistema(int sistema) {
		this.sistema = sistema;
	}

	/**
	 * @return Retorna usuarioRequisitor.
	 */
	public final UsuarioGeral getUsuarioLogado() {
		return usuarioLogado;
	}

	/**
	 * @param usuarioRequisitor
	 *            The usuarioRequisitor to set.
	 */
	public final void setUsuarioLogado(UsuarioGeral usuario) {
		this.usuarioLogado = usuario;
	}

	/**
	 * @return Retorna lockSubmit.
	 */
	public final boolean isLockSubmit() {
		return lockSubmit;
	}

	/**
	 * @param lockSubmit
	 *            The lockSubmit to set.
	 */
	public final void setLockSubmit(boolean lockSubmit) {
		this.lockSubmit = lockSubmit;
	}

	/**
	 * @return Retorna codMovimento.
	 */
	public final Comando getCodMovimento() {
		return codMovimento;
	}

	/**
	 * @param codMovimento
	 *            The codMovimento to set.
	 */
	public final void setCodMovimento(Comando codMovimento) {
		this.codMovimento = codMovimento;
	}

	/**
	 * @return Retorna usuario.
	 */
	public UsuarioGeral getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario
	 *            The usuario to set.
	 */
	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public int getSubsistema() {
		return subsistema;
	}

	public void setSubsistema(int subsistema) {
		this.subsistema = subsistema;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public List<Comando> getComandosBloqueados() {
		return comandosBloqueados;
	}

	public void setComandosBloqueados(List<Comando> comandosBloqueados) {
		this.comandosBloqueados = comandosBloqueados;
	}

	public ListaMensagens getMensagens() {
		if (mensagens == null) mensagens = new ListaMensagens();
		return mensagens;
	}

	public void setMensagens(ListaMensagens mensagens) {
		this.mensagens = mensagens;
	}

	public List<ClosableResource> getClosableResources() {
		return closableResources;
	}
	
	public void addClosableResource(ClosableResource resource) {
		if (closableResources == null)
			closableResources = new ArrayList<ClosableResource>();
		closableResources.add(resource);
	}
	
}