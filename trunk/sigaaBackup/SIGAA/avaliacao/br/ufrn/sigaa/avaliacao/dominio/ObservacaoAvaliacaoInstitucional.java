/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 14/09/2010
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

/**
 * Interface que define uma observa��o anotada na Avalia��o Institucional. Por
 * exemplo, uma observa��o sobre um trancamento, ou sobre uma turma.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public interface ObservacaoAvaliacaoInstitucional {
	
	/** Retorna as observa��es realizadas acerca do objeto de avalia��o. */
	public String getObservacoes();
	/** Seta as observa��es realizadas acerca do objeto de avalia��o. 
	 * @param observacoes
	 */
	public void setObservacoes(String observacoes);
	/** Retorna as observa��es moderadas acerca do objeto de avalia��o. */
	public String getObservacoesModeradas();
	/** Seta as observa��es moderadas acerca do objeto de avalia��o.
	 * @param observacoesModeradas
	 */
	public void setObservacoesModeradas(String observacoesModeradas);

}
