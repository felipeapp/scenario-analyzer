/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.HashMap;
import java.util.Map;

/** Interface com tipos de mudanças curriculares.
 * @author Édipo Elder F. Melo
 *
 */
public interface TipoMudancaCurricular {

	/** Define o tipo de mudança de currículo. */
	public static final int MUDANCA_CURRICULO = 1;
	@Deprecated
	/** Define o tipo de mudança de matriz. */
	public static final int MUDANCA_MATRIZ = 2;
	/** Define o tipo de mudança de curso. */
	public static final int MUDANCA_CURSO = 3;
	/** Define o tipo de mudança por erro. */
	public static final int MUDANCA_POR_ERRO = 9;
	
	// mudanças de matriz
	/** Define o tipo de mudança de modalidade. */
	public static final int MUDANCA_MODALIDADE = 21;
	/** Define o tipo de mudança de habilitação. */
	public static final int MUDANCA_HABILITACAO = 22;
	/** Define o tipo de mudança de turno. */
	public static final int MUDANCA_TURNO = 23;
	/** Define o tipo de mudança de grau acadêmico. Por exemplo: mudando de Bacharelado para Licenciatura. */
	public static final int MUDANCA_GRAU_ACADEMICO = 24;
	/** Define o tipo de mudança de ênfase. */
	public static final int MUDANCA_ENFASE= 25;
	
	/** Descrição textual dos tipos de mudança. */
	public static final Map<Integer, String> descricaoTipoMudanca = new HashMap<Integer, String>(){{
		put(MUDANCA_CURRICULO,"Mudança de Currículo");
		put(MUDANCA_POR_ERRO, "Mudança por Erro");
		put(MUDANCA_MODALIDADE,"Mudança de Modalidade");
		put(MUDANCA_HABILITACAO,"Mudança de Habilitação");
		put(MUDANCA_TURNO,"Mudança de Turno");
		put(MUDANCA_CURSO,"Mudança de Curso");
		put(MUDANCA_GRAU_ACADEMICO, "Mudança de Grau Acadêmico");
		put(MUDANCA_ENFASE, "Mudança de Ênfase");
	}};
	
}