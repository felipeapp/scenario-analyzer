package br.ufrn.sigaa.vestibular.dominio;
/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em 20/01/2010
 *
 */

/** Tipos de restri��es de inscri��o para o vestibular.
 * @author �dipo Elder F. Melo
 *
 */
public interface TipoRestricaoInscricaoVestibular {

	/** Constante que define uma restri��o de inscri��o para somente o grupo indicado. */
	public static final int EXCLUSIVO_A = 1;
	
	/** Constante que define uma restri��o de n�o permiss�o de inscri��o ao grupo indicado. */
	public static final int EXCETO_A = 2;
	

}