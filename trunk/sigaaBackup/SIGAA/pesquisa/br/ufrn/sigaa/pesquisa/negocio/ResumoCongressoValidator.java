/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe auxiliar para realizar validações relacionadas
 * a resumos de congresso de iniciação científica
 *
 * @author Ricardo Wendell
 *
 */
public class ResumoCongressoValidator {

	/** Define o número máximo de autores por resumo do CIC */
	public static final int LIMITE_AUTORES = 5;

	/**
	 * Preparar dados dos autores para persistência
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
	 * Verifica se está no período de envio de resumos do Congresso de Iniciação
	 * Científica para o tipo de bolsa do plano de trabalho passado como
	 * argumento.
	 * 
	 * @param plano
	 * @param lista
	 */
	public static void validarPeriodoEnvio(PlanoTrabalho plano, ListaMensagens lista) {
		if(isEmpty(plano))
			throw new RuntimeNegocioException("Não foi possível prosseguir com a operação pois um plano de trabalho deve ser informado.");
		
		if(isEmpty(plano.getTipoBolsa()))
			throw new RuntimeNegocioException("Não foi possível prosseguir com a operação pois o tipo de bolsa do plano de trabalho deve ser informado.");
		
		if( !isDentroPeriodo(plano.getTipoBolsa().getInicioEnvioResumoCongresso(),	plano.getTipoBolsa().getFimEnvioResumoCongresso())) {
			lista.addErro("O período de submissão de resumos não está vigente para planos de trabalho "+ plano.getTipoBolsaString());
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
				lista.addErro("O plano de trabalho em questão não pode submeter um resumo pois não atende a todas as restrições exigidas.");
			
		}
	}

}
