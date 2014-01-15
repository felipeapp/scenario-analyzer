package br.ufrn.sigaa.biblioteca.circulacao.dominio;


/**
 * Guarda as informa��es sobre os tipos das prorroga��es dos Empr�stimos.
 * 
 * @author Fred_Castro
 *
 */

public class TipoProrrogacaoEmprestimo{
	public static final int FERIADO = 1;
	public static final int FIM_DE_SEMANA = 2;
	public static final int INTERRUPCAO_BIBLIOTECA = 3;
	public static final int PERDA_DE_MATERIAL = 4;
	public static final int RENOVACAO = 5;
}