/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 14/09/2010
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

/**
 * Interface que define uma observação anotada na Avaliação Institucional. Por
 * exemplo, uma observação sobre um trancamento, ou sobre uma turma.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public interface ObservacaoAvaliacaoInstitucional {
	
	/** Retorna as observações realizadas acerca do objeto de avaliação. */
	public String getObservacoes();
	/** Seta as observações realizadas acerca do objeto de avaliação. 
	 * @param observacoes
	 */
	public void setObservacoes(String observacoes);
	/** Retorna as observações moderadas acerca do objeto de avaliação. */
	public String getObservacoesModeradas();
	/** Seta as observações moderadas acerca do objeto de avaliação.
	 * @param observacoesModeradas
	 */
	public void setObservacoesModeradas(String observacoesModeradas);

}
