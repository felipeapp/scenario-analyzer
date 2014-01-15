package br.ufrn.academico.dominio;

import br.ufrn.arq.dominio.GenericTipo;

/**
 * Entidade que armazena os cargos acadêmicos disponíveis para definição dos cursos
 * 
 * @author Mário Melo
 */

public class CargoAcademico extends GenericTipo{
	
	// Constantes usadas pra facilitar as buscas
	/**
	 * Constante para coordenação.
	 */
	public static final int COORDENACAO = 1;
	
	/**
	 * Constante para vice-coordenação.
	 */
	public static final int VICE_COORDENACAO = 2;
	
	/**
	 * Constante para secretaria.
	 */
	public static final int SECRETARIA = 3;
	
}
