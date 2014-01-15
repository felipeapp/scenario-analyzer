/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.academico.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.model.SelectItem;

/**
 * Classe que contém as constantes do nível de ensino
 *
 * @author Gleydson
 *
 */
public class NivelEnsino {

	public static Map<Character, Integer> tabela = new HashMap<Character, Integer>();

	public static final char INFANTIL = 'I';

	public static final char MEDIO = 'M';

	public static final char TECNICO = 'T';

	// Compreende os níveis infantil, médio e técnico
	// (Utilizado para geração de matrículas)
	public static final char BASICO = 'B';

	public static final char GRADUACAO = 'G';

	public static final char LATO = 'L';

	public static final char STRICTO = 'S';

	public static final char MESTRADO = 'E';

	public static final char DOUTORADO = 'D';
	
	public static final char RESIDENCIA = 'R';
	
	public static final char FORMACAO_COMPLEMENTAR = 'F';

	/**
	 * Array de SelectItems para ser utilizado em comboboxes
	 * em páginas com JSF.
	 */
	private static SelectItem[] niveisCombo = new SelectItem[] {
			new SelectItem(INFANTIL + "", "INFANTIL"),
			new SelectItem(MEDIO + "", "MÉDIO"),
			new SelectItem(TECNICO + "", "TÉCNICO"),
			new SelectItem(FORMACAO_COMPLEMENTAR + "", "FORMAÇÃO COMPLEMENTAR"),
			new SelectItem(GRADUACAO + "", "GRADUAÇÃO"),
			new SelectItem(LATO + "", "LATO SENSU - ESPECIALIZAÇÃO"),
			new SelectItem(RESIDENCIA + "", "LATO SENSU - RESIDÊNCIA"),
			new SelectItem(STRICTO + "", "STRICTO SENSU"),
			new SelectItem(MESTRADO + "", "MESTRADO"),
			new SelectItem(DOUTORADO + "", "DOUTORADO") };

	static {
		tabela.put(INFANTIL, 1);
		tabela.put(MEDIO, 2);
		tabela.put(TECNICO, 3);
		tabela.put(GRADUACAO, 4);
		tabela.put(LATO, 5);
		tabela.put(STRICTO, 6);
		tabela.put(RESIDENCIA, 7);
		tabela.put(FORMACAO_COMPLEMENTAR, 8);
	}

	public static String getDescricao(char nivel) {

		switch (nivel) {
		case GRADUACAO:
			return "GRADUAÇÃO";
		case TECNICO:
			return "TÉCNICO";
		case LATO:
			return "LATO SENSU";
		case INFANTIL:
			return "INFANTIL";
		case MEDIO:
			return "MÉDIO";
		case MESTRADO:
			return "MESTRADO";
		case DOUTORADO:
			return "DOUTORADO";
		case STRICTO:
			return "STRICTO SENSU";
		case BASICO:
			return "BÁSICO";
		case RESIDENCIA:
			return "RESIDÊNCIA MÉDICA/MULTI-PROFISSIONAL";
		case FORMACAO_COMPLEMENTAR:
			return "FORMAÇÃO COMPLEMENTAR";
		default:
			return "DESCONHECIDO";
		}

	}

	public static SelectItem[] getNiveisCombo() {
		return niveisCombo;
	}

	/**
	 * Verifica se o nível é infantil, médio ou técnico
	 *
	 * @param nivel
	 * @return
	 */
	public static boolean isEnsinoBasico(Character nivel) {
		return nivel != null
				&& (nivel.charValue() == INFANTIL || nivel.charValue() == MEDIO
						|| nivel.charValue() == TECNICO || nivel.charValue() == FORMACAO_COMPLEMENTAR 
						|| nivel.charValue() == BASICO);
	}
	
	public static boolean isResidenciaMedica(Character nivel) {
		return nivel != null
		&& (nivel.charValue() == RESIDENCIA);
	}

	/**
	 * Verifica se o nível é graduação ou algum tipo de pós
	 *
	 * @param nivel
	 * @return
	 */
	public static boolean isEnsinoSuperior(Character nivel) {
		return nivel != null
				&& (nivel.charValue() == LATO || nivel.charValue() == STRICTO
						|| nivel.charValue() == MESTRADO || nivel.charValue() == DOUTORADO);
	}

	public static boolean isAlgumNivelStricto(Character nivel) {
		return nivel != null
			&& (nivel.charValue() == STRICTO
				|| nivel.charValue() == MESTRADO || nivel.charValue() == DOUTORADO);
	}

	public static boolean isGraduacao(Character nivel) {
		return nivel != null && nivel.charValue() == GRADUACAO;
	}
	
	public static Collection<Character> getNiveisStricto() {
		Collection<Character> niveis = new ArrayList<Character>(0);
		niveis.add(STRICTO);
		niveis.add(DOUTORADO);
		niveis.add(MESTRADO);
		return niveis;
	}
	
	public static boolean isLato(Character nivel) {
		return nivel != null && nivel.charValue() == LATO;
	}

	/**
	 * Retorna a coleção de níveis stricto em uma string neste formato: <tt>('S','D','M')</tt>
	 * @return
	 */
	public static String getNiveisStrictoString(){
		StringBuilder retorno = new StringBuilder("( ");
		for( char c : getNiveisStricto() ){
			retorno.append( ",'" + c + "'" );
		}
		retorno.append( " )" );
		return retorno.toString().replaceFirst(",", "");
	}

	/**
	 * Informa se o nível é válido.
	 * @param nivel
	 * @return
	 */
	public static boolean isValido( char nivel ){
		return !getDescricao(nivel).equalsIgnoreCase("DESCONHECIDO");
	}

}
