package br.ufrn.sigaa.vestibular.dominio;
/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 20/01/2010
 *
 */

/** Tipos de restrições de inscrição para o vestibular.
 * @author Édipo Elder F. Melo
 *
 */
public interface TipoRestricaoInscricaoVestibular {

	/** Constante que define uma restrição de inscrição para somente o grupo indicado. */
	public static final int EXCLUSIVO_A = 1;
	
	/** Constante que define uma restrição de não permissão de inscrição ao grupo indicado. */
	public static final int EXCETO_A = 2;
	

}