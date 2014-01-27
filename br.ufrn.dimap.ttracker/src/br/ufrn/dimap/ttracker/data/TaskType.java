package br.ufrn.dimap.ttracker.data;

import java.io.Serializable;

public enum TaskType implements Serializable {
	VERIFICACAO("VERIFICACAO"),ERRO("ERRO"),APRIMORAMENTO("APRIMORAMENTO"),ERRONEGOCIOVALIDACAO("ERRO DE NEG�CIO/VALIDA��O"),OTHER("OTHER");
	
	private String name;
	
	TaskType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static TaskType getTaskTypeByName(String name) {
		switch(name) {
		case "VERIFICACAO":
			return VERIFICACAO;
		case "ERRO":
			return ERRO;
		case "APRIMORAMENTO":
			return APRIMORAMENTO;
		case "ERRO DE NEG�CIO/VALIDA��O":
			return ERRONEGOCIOVALIDACAO;
		default:
			return OTHER;
		}
	}
	
}
