package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;


/**
 * Interface que serve para implementa��o da condi��o de acesso do inscrito a um processo seletivo
 * @author M�rio Rizzi
 *
 */
public interface RestricaoInscricaoValidator {
	
	public void validate(InscricaoSelecao inscricao, ListaMensagens lista);
	
}
