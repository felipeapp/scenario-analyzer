/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 30/03/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do módulo de Ensino a Distância.
 * 
 * @author Rafael Gomes
 *
 */
public interface ParametrosEAD {

	/** Informa se alunos dos cursos de ensino a distância podem realizar o trancamento de matrícula. */
	public static final String PERMITE_TRANCAR_COMPONENTE = "2_11200_2"; 
	
	/** Se o tutor presencial EAD possui acesso total as operações sobre o discente. Se sim, ele poderá avaliar o discente */
	public static final String ACESSO_TOTAL_TUTOR_PRESENCIAL = "2_11200_3"; 

	/** Se o sistema deve utilizar tutoria.  */
	public static final String UTILIZAR_TUTORIA_EAD = "2_11200_4"; 

	/** Indica se o discente vai ser obrigado a cadastrar horario para tutoria */
	public static final String OBRIGATORIEDADE_EM_DEFINIR_HORARIO_PARA_TUTORIA = "2_11200_5"; 
	
}
