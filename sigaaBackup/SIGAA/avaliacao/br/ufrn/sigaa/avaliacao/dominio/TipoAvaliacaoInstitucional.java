/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/09/2010
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

/** Interface que define os tipos de Avalia��es Institucionais.
 * Por exemplo: Avalia��o do Docente, Avalia��o do Discente, Avalia��o da Doc�ncia Assistida. 
 * @author �dipo Elder F. Melo
 *
 */
public interface TipoAvaliacaoInstitucional {

	/** Avalia��o sem tipo definido. */
	public final int INDEFINIDO = 0;
	/** Avalia��o Institucional do docente de gradua��o. */
	public final int AVALIACAO_DOCENTE_GRADUACAO = 1;
	/** Avalia��o Institucional do discente de gradua��o. */
	public final int AVALIACAO_DISCENTE_GRADUACAO = 2;
	/** Avalia��o Institucional do bolsista de doc�ncia assistida. */
	public final int AVALIACAO_DOCENCIA_ASSISTIDA = 3;
	/** Avalia��o Institucional pelo tutor de ensino � dist�ncia. */
	public final int AVALIACAO_TUTOR_EAD = 4;
}
