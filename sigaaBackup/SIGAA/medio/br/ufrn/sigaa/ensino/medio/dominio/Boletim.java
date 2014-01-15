/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 08/07/2011
* 
*/
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.List;

import br.ufrn.sigaa.ensino.dominio.RegraNota;

/**
 * Classe que representa o boletim de um discente do nível médio. (Não persistida)
 *
 * @author Arlindo
 */
public class Boletim {
	
	/** Discente a qual o histórico se refere */
	private DiscenteMedio discente;
	
	/** Dados da matrícula do discente */
	private MatriculaDiscenteSerie matriculaSerie;
	
	/** Notas de cada disciplina */
	private List<NotaDisciplina> notas;	
	
	/** Observações do discente na série */
	private List<ObservacaoDiscenteSerie> observacoes;
	
	/** Ano letivo */
	private Integer ano;
	
	/** Modelo de Configurações das notas */
	private List<RegraNota> regraNotas;
	
	/** Parâmetro que indica a média mínima para passar por média */
	private Float mediaMinimaPassarPorMedia;
	
	/** Parâmetro que indica a média mínima para passar na prova final */
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
