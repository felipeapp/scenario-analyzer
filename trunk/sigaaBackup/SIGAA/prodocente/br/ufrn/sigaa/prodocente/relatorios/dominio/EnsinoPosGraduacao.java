/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/11/2006'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.HashMap;

public class EnsinoPosGraduacao extends AbstractAvaliacaoDocente implements ViewAtividadeBuilder {

	private String nomeDisciplina;

	private int semestre;

	private String turma;

	private Integer cargaHoraria;

	private Boolean remunerado;

	public EnsinoPosGraduacao(){
	}

	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public Boolean getRemunerado() {
		return remunerado;
	}

	public void setRemunerado(Boolean remunerado) {
		this.remunerado = remunerado;
	}

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public String getTurma() {
		return turma;
	}

	public void setTurma(String turma) {
		this.turma = turma;
	}

	public String getNomeDisciplina() {
		return nomeDisciplina;
	}

	public void setNomeDisciplina(String nomeDisciplina) {
		this.nomeDisciplina = nomeDisciplina;
	}

	public String getItemView() {
		return "<td>" + nomeDisciplina + "</td>" +
				"<td>" + semestre + "</td>" +
				"<td>" + turma + "</td>" +
				"<td>" + cargaHoraria+ "</td>"+
				"<td>" + (remunerado ? "Sim" : "Não") + "</td>";
	}

	public String getTituloView() {
		return 	"	    <td>Disciplina</td>" +
				"	    <td>Semestre</td>" +
				"	    <td>Turma</td>" +
				"	    <td>CH</td>" +
				"	    <td>Remunerado</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("nomeDisciplina", null);
		itens.put("semestre", null);
		itens.put("turma", null);
		itens.put("cargaHoraria", null);
		itens.put("remunerado", null);
		return itens;
	}

	public float getQtdBase() {
		return 1;
	}
}
