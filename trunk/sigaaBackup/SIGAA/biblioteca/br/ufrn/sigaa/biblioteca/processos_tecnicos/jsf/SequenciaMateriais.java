package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

public class SequenciaMateriais {
	String primeiroCodigoBarras = null;
	String ultimoCodigoBarras = null;
	String situacaoAtual = null;
	MaterialInformacional material = null;
	public String getPrimeiroCodigoBarras() {
		return primeiroCodigoBarras;
	}
	public void setPrimeiroCodigoBarras(String primeiroCodigoBarras) {
		this.primeiroCodigoBarras = primeiroCodigoBarras;
	}
	public String getUltimoCodigoBarras() {
		return ultimoCodigoBarras;
	}
	public void setUltimoCodigoBarras(String ultimoCodigoBarras) {
		this.ultimoCodigoBarras = ultimoCodigoBarras;
	}
	public String getSituacaoAtual() {
		return situacaoAtual;
	}
	public void setSituacaoAtual(String situacaoAtual) {
		this.situacaoAtual = situacaoAtual;
	}
	public MaterialInformacional getMaterial() {
		return material;
	}
	public void setMaterial(MaterialInformacional material) {
		this.material = material;
	}
}