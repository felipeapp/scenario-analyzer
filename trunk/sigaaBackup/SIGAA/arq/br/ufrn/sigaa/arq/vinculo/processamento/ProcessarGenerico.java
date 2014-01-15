/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoGenerico;

/**
 * Este vinculo � processado caso o usu�rio n�o possua outro v�nculo
 * 
 * @author Henrique Andr�
 *
 */
public class ProcessarGenerico extends ProcessarVinculoExecutor {

	@Override
	public void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		
		if (dados.getVinculos().isEmpty())
			dados.addVinculo(dados.getUsuario().getUnidade(), true, new TipoVinculoGenerico());
	}

}
