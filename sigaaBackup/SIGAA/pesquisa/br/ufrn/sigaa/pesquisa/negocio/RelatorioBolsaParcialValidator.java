/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe auxiliar para realizar validações relacionadas
 * a relatórios parciais de bolsas de pesquisa
 * 
 * @author Leonardo Campos
 *
 */
public class RelatorioBolsaParcialValidator {

	/**
	 * Verifica se está no período de envio de relatórios parciais para
	 * o tipo de bolsa do plano de trabalho passado como argumento.
	 * 
	 * @param plano
	 * @param lista
	 */
	public static void validarPeriodoEnvio(PlanoTrabalho plano, ListaMensagens lista) {
		if(isEmpty(plano))
			throw new RuntimeNegocioException("Não foi possível prosseguir com a operação pois um plano de trabalho deve ser informado.");
		
		if(isEmpty(plano.getTipoBolsa()))
			throw new RuntimeNegocioException("Não foi possível prosseguir com a operação pois o tipo de bolsa do plano de trabalho deve ser informado.");
		
		if( !isDentroPeriodo(plano.getTipoBolsa().getInicioEnvioRelatorioParcial(),	plano.getTipoBolsa().getFimEnvioRelatorioParcial())) {
			lista.addErro("Atenção! O sistema não está aberto para envio de relatórios parciais para este plano de trabalho.");
		}
	}
}
