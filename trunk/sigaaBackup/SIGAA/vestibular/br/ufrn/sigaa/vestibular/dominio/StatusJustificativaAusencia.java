/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/08/2009
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

/** Interface que define algumas constantes da Justificativa de Aus�ncia do fiscal.
 * @author �dipo Elder F. Melo
 *
 */
public interface StatusJustificativaAusencia {

	/** Justificativa n�o analisada.*/
	public final char NAO_ANALISADO = 'N';
	/** Justificativa em an�lise.*/
	public final char EM_ANALISE = 'A';
	/** Justificativa indeferida.*/
	public final char INDEFERIDO = 'I';
	/** Justificativa deferida.*/
	public final char DEFERIDO = 'D';
}
