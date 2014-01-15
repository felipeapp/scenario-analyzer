/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
* Created on 08/07/2011
* 
*/
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.List;

import br.ufrn.sigaa.ensino.dominio.RegraNota;

/**
 * Classe que representa o boletim de um discente do n�vel m�dio. (N�o persistida)
 *
 * @author Arlindo
 */
public class Boletim {
	
	/** Discente a qual o hist�rico se refere */
	private DiscenteMedio discente;
	
	/** Dados da matr�cula do discente */
	private MatriculaDiscenteSerie matriculaSerie;
	
	/** Notas de cada disciplina */
	private List<NotaDisciplina> notas;	
	
	/** Observa��es do discente na s�rie */
	private List<ObservacaoDiscenteSerie> observacoes;
	
	/** Ano letivo */
	private Integer ano;
	
	/** Modelo de Configura��es das notas */
	private List<RegraNota> regraNotas;
	
	/** Par�metro que indica a m�dia m�nima para passar por m�dia */
	private Float mediaMinimaPassarPorMedia;
	
	/** Par�metro que indica a m�dia m�nima para passar na prova final */
	private Float mediaMinimaAprovacao;

	public DiscenteMedio getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteMedio discente) {
		this.discente = discente;
	}

	public MatriculaDiscenteSerie getMatriculaSerie() {
		return matriculaSerie;
	}

	public void setMatriculaSerie(MatriculaDiscenteSerie matriculaSerie) {
		this.matriculaSerie = matriculaSerie;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public List<RegraNota> getRegraNotas() {
		return regraNotas;
	}

	public void setRegraNotas(List<RegraNota> regraNotas) {
		this.regraNotas = regraNotas;
	}

	public List<ObservacaoDiscenteSerie> getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(List<ObservacaoDiscenteSerie> observacoes) {
		this.observacoes = observacoes;
	}

	public List<NotaDisciplina> getNotas() {
		return notas;
	}

	public void setNotas(List<NotaDisciplina> notas) {
		this.notas = notas;
	}

	public Float getMediaMinimaPassarPorMedia() {
		return mediaMinimaPassarPorMedia;
	}

	public void setMediaMinimaPassarPorMedia(Float mediaMinimaPassarPorMedia) {
		this.mediaMinimaPassarPorMedia = mediaMinimaPassarPorMedia;
	}

	public Float getMediaMinimaAprovacao() {
		return mediaMinimaAprovacao;
	}

	public void setMediaMinimaAprovacao(Float mediaMinimaAprovacao) {
		this.mediaMinimaAprovacao = mediaMinimaAprovacao;
	}
}
