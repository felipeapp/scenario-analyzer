/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Classe de domínio auxiliar que agrega as informações necessárias
 * para a matrículas em turmas de componentes equivalentes 
 * a componentes do currículo do aluno
 * 
 * @author wendell
 *
 */
public class SugestaoMatriculaEquivalentes {

	/** {@link CurriculoComponente} referente a sugestao. */
	private CurriculoComponente curriculoComponente;
	
	/** Sugestões para o {@link #curriculoComponente}. */
	private Collection<SugestaoMatricula> sugestoes;
	
	/** Indica se o discente já cumpriu disciplina equivalente a do {@link #curriculoComponente}. */
	private boolean cumpriuEquivalente;
	/** Indica se o discente está matriculado em uma disciplina equivalente a do {@link #curriculoComponente}. */
	private boolean matriculadoEquivalente;	
	
	public SugestaoMatriculaEquivalentes() {
	}
	
	public SugestaoMatriculaEquivalentes(ComponenteCurricular componente) {
		this.curriculoComponente = new CurriculoComponente(componente);
		sugestoes = new ArrayList<SugestaoMatricula>();
	}
	
	
	public SugestaoMatriculaEquivalentes(CurriculoComponente curriculoComponente) {
		this.curriculoComponente = curriculoComponente;
		sugestoes = new ArrayList<SugestaoMatricula>();
	}

	public CurriculoComponente getCurriculoComponente() {
		return curriculoComponente;
	}

	public void setCurriculoComponente(CurriculoComponente curriculoComponente) {
		this.curriculoComponente = curriculoComponente;
	}

	public Collection<SugestaoMatricula> getSugestoes() {
		return sugestoes;
	}

	public void setSugestoes(Collection<SugestaoMatricula> sugestoes) {
		this.sugestoes = sugestoes;
	}
	
	public ComponenteCurricular getComponente() {
		if (curriculoComponente == null) return null;
		return curriculoComponente.getComponente();
	}
	
	public boolean isCumpriuEquivalente() {
		return cumpriuEquivalente;
	}

	public void setCumpriuEquivalente(boolean cumpriuEquivalente) {
		this.cumpriuEquivalente = cumpriuEquivalente;
	}

	public boolean isMatriculadoEquivalente() {
		return matriculadoEquivalente;
	}

	public void setMatriculadoEquivalente(boolean matriculadoEquivalente) {
		this.matriculadoEquivalente = matriculadoEquivalente;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((curriculoComponente == null) ? 0 : curriculoComponente
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "curriculoComponente");
	}

	/**
	 * Adiciona uma nova {@link SugestaoMatricula} na lista de {@link #sugestoes}.
	 * 
	 * @param sugestao
	 */
	public void adicionaSugestao(SugestaoMatricula sugestao) {
		if (sugestoes == null) {
			sugestoes = new ArrayList<SugestaoMatricula>();
		}
		sugestoes.add(sugestao);
	}

	public String getEquivalencia() {
		return getComponente().getEquivalencia();
	}
	
	/**
	 * Retorna <code>true</code> caso a sugestão passada já esteja adicionada
	 * ou <code>false</code> caso contrário.
	 * 
	 * @param sugestao
	 * @return
	 */
	public boolean hasSugestao(SugestaoMatricula sugestao) {
		for (SugestaoMatricula sug : sugestoes) {
			if(sug.compareByDisciplinaTo(sugestao) == 0)
				return true;
		}
		return false;
	}
}
