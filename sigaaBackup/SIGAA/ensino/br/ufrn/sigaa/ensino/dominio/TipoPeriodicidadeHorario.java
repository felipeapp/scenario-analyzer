/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 21/01/2011
 *
 */
package br.ufrn.sigaa.ensino.dominio;

/**
 * Classe que indica os tipos da periodicidade de um horário.
 * 
 * @author Bernardo
 *
 */
public class TipoPeriodicidadeHorario {

	/** Indica que o horário é semanal. */
	public static final int SEMANAL = 1;
	/** Indica que o horário é quinzenal. */
	public static final int QUINZENAL = 2;
	/** Indica que o horário é mensal. */
	public static final int MENSAL = 3;
	/** Indica que o horário possui outro tipo de periodicidade. */
	public static final int PERSONALIZADO = 4;
}
