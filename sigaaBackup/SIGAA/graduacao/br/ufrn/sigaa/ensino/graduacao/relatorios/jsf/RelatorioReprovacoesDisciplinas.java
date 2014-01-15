/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/11/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.io.Serializable;
import java.util.List;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe auxiliar para geração de relatórios de disciplinas com mais reprovações.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class RelatorioReprovacoesDisciplinas implements Serializable {
	
	private Unidade centro;
	
	private Unidade departamento;
	
	private ComponenteCurricular disciplina;
	
	private List<Turma> turmas;
	
	private long total;
	
	private long trancados;
	
	private long aprovados;
	
	private long reprovados;
	
	private float percentual;
	
	/** 
	 * Compara este objeto com o especificado, comparando o atribudo: id
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "disciplina.id");
	}

	/** Retorna o código HASH correspondente à este objeto.
	 * @see br.ufrn.comum.dominio.UnidadeGeral#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(disciplina.getId());
	}	

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public Unidade getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getTrancados() {
		return trancados;
	}

	public void setTrancados(long trancados) {
		this.trancados = trancados;
	}

	public long getAprovados() {
		return aprovados;
	}

	public void setAprovados(long aprovados) {
		this.aprovados = aprovados;
	}

	public long getReprovados() {
		return reprovados;
	}

	public void setReprovados(long reprovados) {
		this.reprovados = reprovados;
	}

	public float getPercentual() {
		return percentual;
	}

	public void setPercentual(float percentual) {
		this.percentual = percentual;
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}


}
