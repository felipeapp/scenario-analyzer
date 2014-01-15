/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 02/07/2007
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe auxiliar para geração de relatórios de disciplinas com mais reprovações.
 * @author leonardo
 *
 */
public class LinhaRelatorioReprovacoesDisciplinas {

	private String nome;
	
	private String codigo;
	
	private Map<String,Long> discentes = new TreeMap<String, Long>();
	
	private long total;
	
	private long matriculados;
	
	private long reprovados;
	
	private float percentual;
	
	private int id;
	
	private Collection<Turma> turmas;
	
	private int totalEad;
	private int totalPresencial;
	
	public LinhaRelatorioReprovacoesDisciplinas() {
	}

	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public Map<String, Long> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Map<String, Long> discentes) {
		this.discentes = discentes;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getMatriculados() {
		return matriculados;
	}

	public void setMatriculados(long matriculados) {
		this.matriculados = matriculados;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public int getTotalEad() {
		return totalEad;
	}

	public void setTotalEad(int totalEad) {
		this.totalEad = totalEad;
	}

	public int getTotalPresencial() {
		return totalPresencial;
	}

	public void setTotalPresencial(int totalPresencial) {
		this.totalPresencial = totalPresencial;
	}

}
