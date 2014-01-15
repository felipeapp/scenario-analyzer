/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.CalendarUtils.isDentroPeriodo;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.pesquisa.dominio.AutorResumoCongresso;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.RestricaoEnvioResumoCIC;

/**
 * Classe auxiliar para realizar valida��es relacionadas
 * a resumos de congresso de inicia��o cient�fica
 *
 * @author Ricardo Wendell
 *
 */
public class ResumoCongressoValidator {

	/** Define o n�mero m�ximo de autores por resumo do CIC */
	public static final int LIMITE_AUTORES = 5;

	/**
	 * Preparar dados dos autores para persist�ncia
	 *
	 * @param autores
	 */
	public static void prepararDadosAutores(Collection<AutorResumoCongresso> autores) {
		for (AutorResumoCongresso autor : autores) {
			switch (autor.getCategoria()) {
				case AutorResumoCongresso.DOCENTE:
					autor.setDiscente(null);
				break;
				case AutorResumoCongresso.DISCENTE:
					autor.setDocente(null);
					break;
				case AutorResumoCongresso.EXTERNO:
					autor.setDocente(null);
					autor.setDiscente(null);
					break;
			}
		}
	}

	/**
	 * Verifica se est� no per�odo de envio de resumos do Congresso de Inicia��o
	 * Cient�fica para o tipo de bolsa do plano de trabalho passado como
	 * argumento.
	 * 
	 * @param plano
	 * @param lista
	 */
	public static void validarPeriodoEnvio(PlanoTrabalho plano, ListaMensagens lista) {
		if(isEmpty(plano))
			throw new RuntimeNegocioException("N�o foi poss�vel prosseguir com a opera��o pois um plano de trabalho deve ser informado.");
		
		if(isEmpty(plano.getTipoBolsa()))
			throw new RuntimeNegocioException("N�o foi poss�vel prosseguir com a opera��o pois o tipo de bolsa do plano de trabalho deve ser informado.");
		
		if( !isDentroPeriodo(plano.getTipoBolsa().getInicioEnvioResumoCongresso(),	plano.getTipoBolsa().getFimEnvioResumoCongresso())) {
			lista.addErro("O per�odo de submiss�o de resumos n�o est� vigente para planos de trabalho "+ plano.getTipoBolsaString());
		}
	}

	public static void validarRestricoes(PlanoTrabalho plano, CongressoIniciacaoCientifica congresso, ListaMensagens lista) {
		if ( !congresso.getRestricoes().isEmpty() ) {
			
			boolean podeSubmeterResumo = false;
			for (RestricaoEnvioResumoCIC restricao : congresso.getRestricoes()) {
				if ( (!isEmpty(plano.getTipoBolsa()) && !isEmpty(restricao.getTipoBolsa()) && 
						plano.getTipoBolsa().getId() == restricao.getTipoBolsa().getId() && 
							CalendarUtils.compareTo(plano.getDataInicio(), restricao.getDataInicial()) >= 0 && 
								CalendarUtils.compareTo(plano.getDataFim(), restricao.getDataFinal()) <= 0) || 
					( !isEmpty(plano.getCota()) && !isEmpty(restricao.getCotaBolsa()) && plano.getCota().getId() == restricao.getCotaBolsa().getId() )){
					
					podeSubmeterResumo = true;
					break;
				}
			}
			
			if ( !podeSubmeterResumo )
				lista.addErro("O plano de trabalho em quest�o n�o pode submeter um resumo pois n�o atende a todas as restri��es exigidas.");
			
		}
	}

}
