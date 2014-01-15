/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 01/10/2010
 *
 */
package br.ufrn.sigaa.ensino.relatorios;

import java.math.BigInteger;

import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe auxiliar para geração do relatório de entrada de notas.
 * 
 * @author Leonardo Campos
 *
 */
public class LinhaRelatorioEntradaNotas implements Comparable<LinhaRelatorioEntradaNotas> {

	private Turma turma;
	
	private BigInteger matriculados, unidade1, unidade2, unidade3;
	
	public LinhaRelatorioEntradaNotas() {
		turma = new Turma();
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public BigInteger getMatriculados() {
		return matriculados;
	}

	public void setMatriculados(BigInteger matriculados) {
		this.matriculados = matriculados;
	}

	public BigInteger getUnidade1() {
		return unidade1;
	}

	public void setUnidade1(BigInteger unidade1) {
		this.unidade1 = unidade1;
	}

	public BigInteger getUnidade2() {
		return unidade2;
	}

	public void setUnidade2(BigInteger unidade2) {
		this.unidade2 = unidade2;
	}

	public BigInteger getUnidade3() {
		return unidade3;
	}

	public void setUnidade3(BigInteger unidade3) {
		this.unidade3 = unidade3;
	}

	@Override
	public int compareTo(LinhaRelatorioEntradaNotas arg0) {
		return this.getTurma().getCodigo().compareTo(arg0.getTurma().getCodigo()) == 0
				? this.getTurma().getDisciplina().getCodigo().compareTo(arg0.getTurma().getDisciplina().getCodigo())
				: this.getTurma().getCodigo().compareTo(arg0.getTurma().getCodigo());
	}
	
}
