/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.relatorios;

import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Sumário de participações de docentes em projetos de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class ParticipacaoDocenteProjetos {

	/** Docente participante de projeto */
	private Servidor docente;

	/** Total de coordenações */
	private long totalCoordenador;

	/** Total de participações em projetos internos */
	private long totalInternos;

	/** Total de participações em projetos externos */
	private long totalExternos;

	public ParticipacaoDocenteProjetos() {

	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public long getTotalCoordenador() {
		return totalCoordenador;
	}

	public void setTotalCoordenador(long totalCoordenador) {
		this.totalCoordenador = totalCoordenador;
	}

	public long getTotalExternos() {
		return totalExternos;
	}

	public void setTotalExternos(long totalExternos) {
		this.totalExternos = totalExternos;
	}

	public long getTotalInternos() {
		return totalInternos;
	}

	public void setTotalInternos(long totalInternos) {
		this.totalInternos = totalInternos;
	}



}
