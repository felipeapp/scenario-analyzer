/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 30/08/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;
import java.util.List;

import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * Classe que representa o histórico de um discente do nível médio. (Não persistida)
 *
 * @author Arlindo Rodrigues
 */
public class HistoricoMedio {
	
	/** Discente a qual o histórico se refere */
	private DiscenteMedio discente;
	
	/** Notas de cada disciplina */
	private List<NotaDisciplina> disciplinas;		
	
	/** Lista de observações sobre o discente */
	private List<ObservacaoDiscente> observacoesDiscente;	
	
	/** Data do histórico */
	private Date dataHistorico;	
	
	/** Média do discente a qual o histórico se refere */
	private float mediaDiscente;

	public DiscenteMedio getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteMedio discente) {
		this.discente = discente;
	}

	public List<NotaDisciplina> getDisciplinas() {
		return disciplinas;
	}

	public void setDisciplinas(List<NotaDisciplina> disciplinas) {
		this.disciplinas = disciplinas;
	}

	public List<ObservacaoDiscente> getObservacoesDiscente() {
		return observacoesDiscente;
	}

	public void setObservacoesDiscente(List<ObservacaoDiscente> observacoesDiscente) {
		this.observacoesDiscente = observacoesDiscente;
	}

	public float getMediaDiscente() {
		return mediaDiscente;
	}

	public void setMediaDiscente(float mediaDiscente) {
		this.mediaDiscente = mediaDiscente;
	}

	public Date getDataHistorico() {
		return dataHistorico;
	}

	public void setDataHistorico(Date dataHistorico) {
		this.dataHistorico = dataHistorico;
	}
}
