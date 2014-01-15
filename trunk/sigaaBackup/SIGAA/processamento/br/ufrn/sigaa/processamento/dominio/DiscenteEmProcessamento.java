/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 10/11/2010
 *
 */
package br.ufrn.sigaa.processamento.dominio;

import br.ufrn.sigaa.ensino.dominio.MatriculaHorario;

/**
 * Classe para comparar os discentes durante o processamento da matrícula por horário.
 * 
 * @author Leonardo Campos
 *
 */
public class DiscenteEmProcessamento implements Comparable<DiscenteEmProcessamento>{

	private int idDiscente;
	
	private Double mediaFinal;
	
	private Double notaSelecao;
	
	private MatriculaHorario solicitacaoMatricula;
	
	private int PoloDiscente;
	
	public DiscenteEmProcessamento() {
	}

	public int getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}

	public Double getMediaFinal() {
		return mediaFinal;
	}

	public void setMediaFinal(Double mediaFinal) {
		this.mediaFinal = mediaFinal;
	}

	public Double getNotaSelecao() {
		return notaSelecao;
	}

	public void setNotaSelecao(Double notaSelecao) {
		this.notaSelecao = notaSelecao;
	}

	public MatriculaHorario getSolicitacaoMatricula() {
		return solicitacaoMatricula;
	}

	public void setSolicitacaoMatricula(MatriculaHorario solicitacaoMatricula) {
		this.solicitacaoMatricula = solicitacaoMatricula;
	}

	public int getPoloDiscente() {
		return PoloDiscente;
	}

	public void setPoloDiscente(int poloDiscente) {
		PoloDiscente = poloDiscente;
	}

	@Override
	public int compareTo(DiscenteEmProcessamento o) {
		int result = o.getMediaFinal().compareTo(mediaFinal);
		if(result == 0)
			result = o.getNotaSelecao().compareTo(notaSelecao);
		if(result == 0)
			result = solicitacaoMatricula.getDataCadastro().compareTo(o.getSolicitacaoMatricula().getDataCadastro());
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idDiscente;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DiscenteEmProcessamento other = (DiscenteEmProcessamento) obj;
		if (idDiscente != other.idDiscente)
			return false;
		return true;
	}
}
