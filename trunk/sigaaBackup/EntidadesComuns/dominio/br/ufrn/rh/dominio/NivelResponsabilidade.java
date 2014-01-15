package br.ufrn.rh.dominio;

import java.util.HashMap;

/**
 * N�vel de Responsabilidade. Usado em Respons�vel Unidade.
 *
 * @author Gleydson
 *
 */
public class NivelResponsabilidade {

	/** Sem n�vel de responsabilidade */
	public static final char NENHUM = ' ';
	
	/** Chefia da Unidade */
	public static final char CHEFE = 'C';

	/** Vice chefia da Unidade */
	public static final char VICE = 'V';

	/** Secretaria da Unidade */
	public static final char SECRETARIA = 'S';
	
	/** O gerente tem as fun��es de Chefia no sistemas mas n�o o � de direito */
	public static final char GERENTE = 'G';

	/** Usado para indicar casos onde ele � chefe/diretor para assuntos acad�micos */
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
		niveisDescricao.put(SUPERVISOR_DIRETOR_ACADEMICO, "Supervisor/Diretor Acad�mico");
		
		return niveisDescricao;
	}
	
	public static String getNivelDescricao(char nivel) {
		return getNiveisDescricao().get(nivel);
	}
	
}
