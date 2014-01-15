/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 * 
 * Created on 23/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;

/**
 * Movimento para c�lculo do n�mero de bolsas por projeto
 *
 * @author David Ricardo
 *
 */
public class CalculoBolsasMov extends AbstractMovimentoAdapter {

	private EditalMonitoria edital;
	
	private List<ProjetoEnsino> projetos;

	/**
	 * @return the edital
	 */
	public EditalMonitoria getEdital() {
		return edital;
	}

	/**
	 * @param edital the edital to set
	 */
	public void setEdital(EditalMonitoria edital) {
		this.edital = edital;
	}

	/**
	 * @return the projetos
	 */
	public List<ProjetoEnsino> getProjetos() {
		return projetos;
	}

	/**
	 * @param projetos the projetos to set
	 */
	public void setProjetos(List<ProjetoEnsino> projetos) {
		this.projetos = projetos;
	}
	
	
	
}
