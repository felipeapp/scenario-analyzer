package br.ufrn.sigaa.ensino_rede.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;

public interface SelecionaDiscente {

	public void setDiscente(DiscenteAssociado discente);
	
	public String selecionaDiscente() throws ArqException;
	
}
