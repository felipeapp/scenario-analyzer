/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/09/2010
 *
 */
package br.ufrn.sigaa.avaliacao.dominio;

/** Interface que define os tipos de Avaliações Institucionais.
 * Por exemplo: Avaliação do Docente, Avaliação do Discente, Avaliação da Docência Assistida. 
 * @author Édipo Elder F. Melo
 *
 */
public interface TipoAvaliacaoInstitucional {

	/** Avaliação sem tipo definido. */
	public final int INDEFINIDO = 0;
	/** Avaliação Institucional do docente de graduação. */
	public final int AVALIACAO_DOCENTE_GRADUACAO = 1;
	/** Avaliação Institucional do discente de graduação. */
	public final int AVALIACAO_DISCENTE_GRADUACAO = 2;
	/** Avaliação Institucional do bolsista de docência assistida. */
	public final int AVALIACAO_DOCENCIA_ASSISTIDA = 3;
	/** Avaliação Institucional pelo tutor de ensino à distância. */
	public final int AVALIACAO_TUTOR_EAD = 4;
}
