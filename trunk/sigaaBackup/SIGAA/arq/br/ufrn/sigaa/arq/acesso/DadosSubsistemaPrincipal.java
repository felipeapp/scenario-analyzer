package br.ufrn.sigaa.arq.acesso;

import br.ufrn.comum.dominio.SubSistema;

public class DadosSubsistemaPrincipal {

	public DadosSubsistemaPrincipal(SubSistema subSistemaPrincipal, boolean entrarAutomaticamente) {
		this.subSistemaPrincipal = subSistemaPrincipal;
		this.entrarAutomaticamente = entrarAutomaticamente;
	}
	
	/**
	 * Subsistema que o usuário tem acesso
	 */
	private SubSistema subSistemaPrincipal;

	/**
	 * Indica se esse substima deve ser considerado para entrar automaticamente
	 */
	private boolean entrarAutomaticamente;

	public SubSistema getSubSistemaPrincipal() {
		return subSistemaPrincipal;
	}

	public void setSubSistemaPrincipal(SubSistema subSistemaPrincipal) {
		this.subSistemaPrincipal = subSistemaPrincipal;
	}

	public boolean isEntrarAutomaticamente() {
		return entrarAutomaticamente;
	}

	public void setEntrarAutomaticamente(boolean entrarAutomaticamente) {
		this.entrarAutomaticamente = entrarAutomaticamente;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((subSistemaPrincipal == null) ? 0 : subSistemaPrincipal
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DadosSubsistemaPrincipal other = (DadosSubsistemaPrincipal) obj;
		if (subSistemaPrincipal == null) {
			if (other.subSistemaPrincipal != null)
				return false;
		} else if (!subSistemaPrincipal.equals(other.subSistemaPrincipal))
			return false;
		return true;
	}
	
}
