package br.ufrn.sigaa.ensino_rede.negocio;

import java.util.HashSet;
import java.util.Set;

import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;

public class GrupoDiscentesAlterados {
	
	private Set<DiscenteAssociado> discentes;
	private StatusDiscenteAssociado statusAntigo;
	private StatusDiscenteAssociado statusNovo;
	
	public void addDiscente(DiscenteAssociado discente) {
		if (discentes == null)
			discentes = new HashSet<DiscenteAssociado>();
		
		discentes.add(discente);
	}
	
	public Set<DiscenteAssociado> getDiscentes() {
		return discentes;
	}
	public void setDiscentes(Set<DiscenteAssociado> discentes) {
		this.discentes = discentes;
	}

	public StatusDiscenteAssociado getStatusAntigo() {
		return statusAntigo;
	}

	public void setStatusAntigo(StatusDiscenteAssociado statusAntigo) {
		this.statusAntigo = statusAntigo;
	}

	public StatusDiscenteAssociado getStatusNovo() {
		return statusNovo;
	}

	public void setStatusNovo(StatusDiscenteAssociado statusNovo) {
		this.statusNovo = statusNovo;
	}
	
	

	
	
}
