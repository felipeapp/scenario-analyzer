/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.HashMap;
import java.util.Map;

/** Interface com tipos de mudan�as curriculares.
 * @author �dipo Elder F. Melo
 *
 */
public interface TipoMudancaCurricular {

	/** Define o tipo de mudan�a de curr�culo. */
	public static final int MUDANCA_CURRICULO = 1;
	@Deprecated
	/** Define o tipo de mudan�a de matriz. */
	public static final int MUDANCA_MATRIZ = 2;
	/** Define o tipo de mudan�a de curso. */
	public static final int MUDANCA_CURSO = 3;
	/** Define o tipo de mudan�a por erro. */
	public static final int MUDANCA_POR_ERRO = 9;
	
	// mudan�as de matriz
	/** Define o tipo de mudan�a de modalidade. */
	public static final int MUDANCA_MODALIDADE = 21;
	/** Define o tipo de mudan�a de habilita��o. */
	public static final int MUDANCA_HABILITACAO = 22;
	/** Define o tipo de mudan�a de turno. */
	public static final int MUDANCA_TURNO = 23;
	/** Define o tipo de mudan�a de grau acad�mico. Por exemplo: mudando de Bacharelado para Licenciatura. */
	public static final int MUDANCA_GRAU_ACADEMICO = 24;
	/** Define o tipo de mudan�a de �nfase. */
	public static final int MUDANCA_ENFASE= 25;
	
	/** Descri��o textual dos tipos de mudan�a. */
	public static final Map<Integer, String> descricaoTipoMudanca = new HashMap<Integer, String>(){{
		put(MUDANCA_CURRICULO,"Mudan�a de Curr�culo");
		put(MUDANCA_POR_ERRO, "Mudan�a por Erro");
		put(MUDANCA_MODALIDADE,"Mudan�a de Modalidade");
		put(MUDANCA_HABILITACAO,"Mudan�a de Habilita��o");
		put(MUDANCA_TURNO,"Mudan�a de Turno");
		put(MUDANCA_CURSO,"Mudan�a de Curso");
		put(MUDANCA_GRAU_ACADEMICO, "Mudan�a de Grau Acad�mico");
		put(MUDANCA_ENFASE, "Mudan�a de �nfase");
	}};
	
}