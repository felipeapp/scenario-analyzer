/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 21/01/2011
 *
 */
package br.ufrn.sigaa.ensino.dominio;

/**
 * Classe que indica os tipos da periodicidade de um hor�rio.
 * 
 * @author Bernardo
 *
 */
public class TipoPeriodicidadeHorario {

	/** Indica que o hor�rio � semanal. */
	public static final int SEMANAL = 1;
	/** Indica que o hor�rio � quinzenal. */
	public static final int QUINZENAL = 2;
	/** Indica que o hor�rio � mensal. */
	public static final int MENSAL = 3;
	/** Indica que o hor�rio possui outro tipo de periodicidade. */
	public static final int PERSONALIZADO = 4;
}
