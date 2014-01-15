/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 31/03/2011
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.CalendarUtils.isDentroPeriodo;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;

/**
 * Classe auxiliar para realizar valida��es relacionadas
 * a relat�rios finais de bolsas de pesquisa
 * 
 * @author Leonardo Campos
 *
 */
public class RelatorioBolsaFinalValidator {

	/**
	 * Verifica se est� no per�odo de envio de relat�rios finais para
	 * o tipo de bolsa do plano de trabalho passado como argumento.
	 * 
	 * @param plano
	 * @param lista
	 */
	public static void validarPeriodoEnvio(PlanoTrabalho plano, ListaMensagens lista) {
		if(isEmpty(plano))
			throw new RuntimeNegocioException("N�o foi poss�vel prosseguir com a opera��o pois um plano de trabalho deve ser informado.");
		
		if(isEmpty(plano.getTipoBolsa()))
			throw new RuntimeNegocioException("N�o foi poss�vel prosseguir com a opera��o pois o tipo de bolsa do plano de trabalho deve ser informado.");
		
		if( !isDentroPeriodo(plano.getTipoBolsa().getInicioEnvioRelatorioFinal(),	plano.getTipoBolsa().getFimEnvioRelatorioFinal())) {
			lista.addErro("Aten��o! O sistema n�o est� aberto para envio de relat�rios finais para este plano de trabalho.");
		}
	}
}
