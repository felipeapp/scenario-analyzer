package br.ufrn.rh.dominio;

import java.util.HashMap;

/**
 * Nível de Responsabilidade. Usado em Responsável Unidade.
 *
 * @author Gleydson
 *
 */
public class NivelResponsabilidade {

	/** Sem nível de responsabilidade */
	public static final char NENHUM = ' ';
	
	/** Chefia da Unidade */
	public static final char CHEFE = 'C';

	/** Vice chefia da Unidade */
	public static final char VICE = 'V';

	/** Secretaria da Unidade */
	public static final char SECRETARIA = 'S';
	
	/** O gerente tem as funções de Chefia no sistemas mas não o é de direito */
	public static final char GERENTE = 'G';

	/** Usado para indicar casos onde ele é chefe/diretor para assuntos acadêmicos */
	public static final char SUPERVISOR_DIRETOR_ACADEMICO = 'A';
	
	public static Character[] getNiveis() {
		return new Character[] { CHEFE, VICE, SECRETARIA, GERENTE, SUPERVISOR_DIRETOR_ACADEMICO };
	}
	
	public static HashMap<Character, String> getNiveisDescricao() {
		HashMap<Character, String> niveisDescricao = new HashMap<Character, String>();
		
		niveisDescricao.put(CHEFE, "Chefia/Diretoria");
		niveisDescricao.put(VICE, "Vice-Chefia/Vice-Diretoria");
		niveisDescricao.put(SECRETARIA, "Secretaria");
		niveisDescricao.put(GERENTE, "Gerencial");
		niveisDescricao.put(SUPERVISOR_DIRETOR_ACADEMICO, "Supervisor/Diretor Acadêmico");
		
		return niveisDescricao;
	}
	
	public static String getNivelDescricao(char nivel) {
		return getNiveisDescricao().get(nivel);
	}
	
}
