package br.ufrn.dimap.ttracker.data;

public enum TaskType {
	VERIFICACAO("VERIFICACAO"),ERRO("ERRO"),APRIMORAMENTO("APRIMORAMENTO"),ERRONEGOCIOVALIDACAO("ERRONEGOCIOVALIDACAO");
	
	private String name;
	
	TaskType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
