package br.ufrn.sigaa.ensino_rede.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;

@SuppressWarnings("serial")
public class MovimentoAlterarStatusMatriculaRede extends MovimentoCadastro {

	private List<GrupoDiscentesAlterados> gruposDiscentesAlterados;

	public void addGrupo(GrupoDiscentesAlterados grupo) {
		if (gruposDiscentesAlterados == null)
			gruposDiscentesAlterados = new ArrayList<GrupoDiscentesAlterados>();
		
		gruposDiscentesAlterados.add(grupo);
	}
	
	public List<GrupoDiscentesAlterados> getGruposDiscentesAlterados() {
		return gruposDiscentesAlterados;
	}

	public void setGruposDiscentesAlterados(List<GrupoDiscentesAlterados> gruposDiscentesAlterados) {
		this.gruposDiscentesAlterados = gruposDiscentesAlterados;
	}
	
}
