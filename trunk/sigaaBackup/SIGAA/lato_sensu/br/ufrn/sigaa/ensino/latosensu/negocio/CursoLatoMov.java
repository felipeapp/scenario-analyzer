/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 09/10/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.latosensu.dominio.ComponenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaAvaliacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaSelecaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.PublicoAlvoCurso;

/**
 * Esta classe encapsula os objetos necess�rios para o movimento de cadastro
 * de um curso de lato sensu.
 *
 * @author Leonardo
 *
 */
public class CursoLatoMov extends MovimentoCadastro { 

	/** Indica que a situa��o da proposta est� completa */
	private boolean completa;
	/** Indica que a situa��o da proposta est� em andamento */
	private boolean andamento;

	/** Cole��o de publico Alvo do curso para remo��o */
	private Collection<PublicoAlvoCurso> removePublicosAlvo;
	/** Cole��o de Formas de Sele��o do curso para remo��o */
	private Collection<FormaSelecaoProposta> removeFormasSelecao;
	/** Cole��o de Formas de Avalia��o do curso para remo��o */
	private Collection<FormaAvaliacaoProposta> removeFormasAvaliacao;
	/** Cole��o de Corpo Docente Curso Lato do curso para remo��o */
	private Collection<CorpoDocenteCursoLato> removeCursosServidores;
	/** Cole��o de Componentes Curso Lato do curso para remo��o */
	private Collection<ComponenteCursoLato> removeComponentesCursoLato;

	public CursoLatoMov(){
		
	}
	
	public CursoLatoMov(CursoLato curso, Comando comando){
		super();
		setCodMovimento(comando);
		setObjMovimentado(curso);
	}

	/**
	 * Retornar o P�blico Alvo para remo��o
	 */
	public Collection<PublicoAlvoCurso> getRemovePublicosAlvo() {
		return removePublicosAlvo;
	}

	/**
	 * Seta o P�blico Alvo para remo��o
	 */
	public void setRemovePublicosAlvo(
			Collection<PublicoAlvoCurso> removePublicosAlvo) {
		this.removePublicosAlvo = removePublicosAlvo;
	}

	/**
	 * Retornar as Formas de Avalia��o para remo��o
	 */
	public Collection<FormaAvaliacaoProposta> getRemoveFormasAvaliacao() {
		return removeFormasAvaliacao;
	}

	/**
	 * Seta as Formas de Avalia��o para remo��o
	 */
	public void setRemoveFormasAvaliacao(
			Collection<FormaAvaliacaoProposta> removeFormasAvaliacao) {
		this.removeFormasAvaliacao = removeFormasAvaliacao;
	}

	/**
	 * Retornar as Formas de Sele��o para remo��o
	 */
	public Collection<FormaSelecaoProposta> getRemoveFormasSelecao() {
		return removeFormasSelecao;
	}

	/**
	 * Seta as Formas de Sele��o para remo��o
	 */
	public void setRemoveFormasSelecao(
			Collection<FormaSelecaoProposta> removeFormasSelecao) {
		this.removeFormasSelecao = removeFormasSelecao;
	}

	public Collection<CorpoDocenteCursoLato> getRemoveCursosServidores() {
		return removeCursosServidores;
	}

	public void setRemoveCursosServidores(
			Collection<CorpoDocenteCursoLato> removeCursosServidores) {
		this.removeCursosServidores = removeCursosServidores;
	}

	public boolean isCompleta() {
		return completa;
	}

	public void setCompleta(boolean completa) {
		this.completa = completa;
	}

	public boolean isAndamento() {
		return andamento;
	}

	public void setAndamento(boolean andamento) {
		this.andamento = andamento;
	}

	public Collection<ComponenteCursoLato> getRemoveComponentesCursoLato() {
		return removeComponentesCursoLato;
	}

	public void setRemoveComponentesCursoLato(
			Collection<ComponenteCursoLato> removeComponentesCursoLato) {
		this.removeComponentesCursoLato = removeComponentesCursoLato;
	}

}
