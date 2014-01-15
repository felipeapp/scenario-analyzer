/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.ParseException;

import br.ufrn.arq.util.CalendarUtils;


/**
 * Enumeração dos possíveis tipos de valores que um atributo mapeável pode ter.
 * Em outras palavras, se o atributo é um integer, long, date, char, etc.
 * 
 * @author Édipo Elder F. de Melo
 * @author Fred_Castro
 * 
 */
public enum TipoDadoAtributoTecnico {
	// OBS: NÃO ALTERAR A ORDEM DA ENUMERAÇÃO

	/** Define que o descritor do atributo mapeável é um booleano. */
	BOOLEAN,
	/** Define que o descritor do atributo mapeável é um byte. */
	BYTE,
	/** Define que o descritor do atributo mapeável é um char. */
	CHARACTER, 
	/** Define que o descritor do atributo mapeável é um double. */
	DOUBLE, 
	/** Define que o descritor do atributo mapeável é um float. */
	FLOAT, 
	/** Define que o descritor do atributo mapeável é um integer. */
	INTEGER,
	/** Define que o descritor do atributo mapeável é um long. */
	LONG, 
	/** Define que o descritor do atributo mapeável é um short. */
	SHORT, 
	/** Define que o descritor do atributo mapeável é um String. */
	STRING,
	/** Define que o descritor do atributo mapeável é um Date. */
	DATE;
	
	/** Converte um valor para o tipo de dado correto.
	 * @param valor
	 * @return
	 * @throws ParseException
	 */
	public Object parse(String valor) throws ParseException {
		if (isEmpty(valor))
			return null;
		switch (this) {
			case BOOLEAN:
				if ("N".equals(valor)) valor = "false";
				else if ("S".equals(valor)) valor = "true";
				return Boolean.valueOf(valor);
			case BYTE: return Byte.valueOf(valor);
			case CHARACTER: if (valor.length() == 1) 
				return Character.valueOf(valor.charAt(0));
				else throw new ParseException("O valor informado não é um caractere.", (Integer) null);
			case DOUBLE: return Double.valueOf(valor);
			case FLOAT: return Float.valueOf(valor);
			case INTEGER: return Integer.valueOf(valor);
			case LONG: return Long.valueOf(valor);
			case SHORT: return Short.valueOf(valor);
			case DATE: return CalendarUtils.parseDate(valor, "dd/MM/yyyy");
			default: return valor;
		}
	}
	
	@Override
	public String toString() {
		switch (this) {
		case BOOLEAN: return "LÓGICO (TRUE / FALSE)";
		case BYTE: return "BYTE (0 - 255)";
		case CHARACTER:
		case STRING:
			return "CARACTERE";
		case DOUBLE: return "NUMÉRICO";
		case FLOAT: return "NUMÉRICO";
		case INTEGER: return "INTEIRO";
		case LONG: return "INTEIRO LONGO";
		case SHORT: return "INTEIRO CURSO";
		case DATE: return "DATA (FORMATO: DD/MM/AAAA)";
		default: return "";
	}
	}
}
