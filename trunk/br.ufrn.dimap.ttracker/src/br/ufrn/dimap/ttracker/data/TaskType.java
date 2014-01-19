package br.ufrn.dimap.ttracker.data;

import java.io.Serializable;

public enum TaskType implements Serializable {
	VERIFICACAO("VERIFICACAO"),ERRO("ERRO"),APRIMORAMENTO("APRIMORAMENTO"),ERRONEGOCIOVALIDACAO("ERRONEGOCIOVALIDACAO");
	
	private String name;
	
	TaskType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
