/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/08/2009
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

/** Interface que define algumas constantes da Justificativa de Ausência do fiscal.
 * @author Édipo Elder F. Melo
 *
 */
public interface StatusJustificativaAusencia {

	/** Justificativa não analisada.*/
	public final char NAO_ANALISADO = 'N';
	/** Justificativa em análise.*/
	public final char EM_ANALISE = 'A';
	/** Justificativa indeferida.*/
	public final char INDEFERIDO = 'I';
	/** Justificativa deferida.*/
	public final char DEFERIDO = 'D';
}
