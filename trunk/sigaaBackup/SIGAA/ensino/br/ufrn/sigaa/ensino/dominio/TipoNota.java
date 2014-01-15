/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/07/2011
 * Autor: Arlindo Rodrigues
 */
package br.ufrn.sigaa.ensino.dominio;

/**
 * Enum de Tipos de Notas que serão utilizados nas regras de consolidação (Nível médio).
 * @author Arlindo
 *
 */
public enum TipoNota {
	
	/** Indica que a nota é regular no bimestre o semestre */
	REGULAR, 
	/** Indica que a nota é de recuperação */
	RECUPERACAO, 
	/** Indica que a nota é de recuperação final (prova final) */
	PROVA_FINAL;

}
