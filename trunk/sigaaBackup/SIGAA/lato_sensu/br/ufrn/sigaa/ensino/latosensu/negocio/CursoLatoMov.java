/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Esta classe encapsula os objetos necessários para o movimento de cadastro
 * de um curso de lato sensu.
 *
 * @author Leonardo
 *
 */
public class CursoLatoMov extends MovimentoCadastro { 

	/** Indica que a situação da proposta está completa */
	private boolean completa;
	/** Indica que a situação da proposta está em andamento */
	private boolean andamento;

	/** Coleção de publico Alvo do curso para remoção */
	private Collection<PublicoAlvoCurso> removePublicosAlvo;
	/** Coleção de Formas de Seleção do curso para remoção */
	private Collection<FormaSelecaoProposta> removeFormasSelecao;
	/** Coleção de Formas de Avaliação do curso para remoção */
	private Collection<FormaAvaliacaoProposta> removeFormasAvaliacao;
	/** Coleção de Corpo Docente Curso Lato do curso para remoção */
	private Collection<CorpoDocenteCursoLato> removeCursosServidores;
	/** Coleção de Componentes Curso Lato do curso para remoção */
	private Collection<ComponenteCursoLato> removeComponentesCursoLato;

	public CursoLatoMov(){
		
	}
	
	public CursoLatoMov(CursoLato curso, Comando comando){
		super();
		setCodMovimento(comando);
		setObjMovimentado(curso);
	}

	/**
	 * Retornar o Público Alvo para remoção
	 */
	public Collection<PublicoAlvoCurso> getRemovePublicosAlvo() {
		return removePublicosAlvo;
	}

	/**
	 * Seta o Público Alvo para remoção
	 */
	public void setRemovePublicosAlvo(
			Collection<PublicoAlvoCurso> removePublicosAlvo) {
		this.removePublicosAlvo = removePublicosAlvo;
	}

	/**
	 * Retornar as Formas de Avaliação para remoção
	 */
	public Collection<FormaAvaliacaoProposta> getRemoveFormasAvaliacao() {
		return removeFormasAvaliacao;
	}

	/**
	 * Seta as Formas de Avaliação para remoção
	 */
	public void setRemoveFormasAvaliacao(
			Collection<FormaAvaliacaoProposta> removeFormasAvaliacao) {
		this.removeFormasAvaliacao = removeFormasAvaliacao;
	}

	/**
	 * Retornar as Formas de Seleção para remoção
	 */
	public Collection<FormaSelecaoProposta> getRemoveFormasSelecao() {
		return removeFormasSelecao;
	}

	/**
	 * Seta as Formas de Seleção para remoção
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
