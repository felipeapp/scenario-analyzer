/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/04/2010
 *
 */
package br.ufrn.sigaa.ensino.latosensu.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Movimento que encapsula as informações necessárias para o processamento
 * da conclusão de um curso de lato sensu.
 * 
 * @author Leonardo Campos
 *
 */
public class MovimentoConclusaoCursoLato extends AbstractMovimentoAdapter {

	private CursoLato cursoLato;
	
	private Collection<Discente> discentes;
	
	private int ano;
	
	private int periodo;
	
	public MovimentoConclusaoCursoLato() {
		
	}

	public CursoLato getCursoLato() {
		return cursoLato;
	}

	public void setCursoLato(CursoLato cursoLato) {
		this.cursoLato = cursoLato;
	}

	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	
	

}
