/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoGenerico;

/**
 * Este vinculo é processado caso o usuário não possua outro vínculo
 * 
 * @author Henrique André
 *
 */
public class ProcessarGenerico extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		
		if (dados.getVinculos().isEmpty())
			dados.addVinculo(dados.getUsuario().getUnidade(), true, new TipoVinculoGenerico());
	}

}
