/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 15/07/2011
 * Autor: Arlindo Rodrigues
 */
package br.ufrn.sigaa.ensino.dominio;

/**
 * Enum de Tipos de Notas que ser�o utilizados nas regras de consolida��o (N�vel m�dio).
 * @author Arlindo
 *
 */
public enum TipoNota {
	
	/** Indica que a nota � regular no bimestre o semestre */
	REGULAR, 
	/** Indica que a nota � de recupera��o */
	RECUPERACAO, 
	/** Indica que a nota � de recupera��o final (prova final) */
	PROVA_FINAL;

}
