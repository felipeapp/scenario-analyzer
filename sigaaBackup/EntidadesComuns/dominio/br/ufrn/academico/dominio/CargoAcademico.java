package br.ufrn.academico.dominio;

import br.ufrn.arq.dominio.GenericTipo;

/**
 * Entidade que armazena os cargos acad�micos dispon�veis para defini��o dos cursos
 * 
 * @author M�rio Melo
 */

public class CargoAcademico extends GenericTipo{
	
	// Constantes usadas pra facilitar as buscas
	/**
	 * Constante para coordena��o.
	 */
	public static final int COORDENACAO = 1;
	
	/**
	 * Constante para vice-coordena��o.
	 */
	public static final int VICE_COORDENACAO = 2;
	
	/**
	 * Constante para secretaria.
	 */
	public static final int SECRETARIA = 3;
	
}
