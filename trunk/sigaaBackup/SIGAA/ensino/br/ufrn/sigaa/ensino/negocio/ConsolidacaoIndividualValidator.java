/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Aug 24, 2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.ConsolidacaoIndividual;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Classe que realiza a valida��o para objetos ConsolidacaoIndividual
 *
 * @author Victor Hugo
 *
 */
public class ConsolidacaoIndividualValidator {

	/**
	 * Valida a submiss�o dos dados espec�ficos do aluno do t�cnico
	 * @throws NegocioException
	 */
	public static void validarConsolidacao(ConsolidacaoIndividual consolidacao, ListaMensagens lista) throws NegocioException {

		if( !consolidacao.getMatricula().getSituacaoMatricula().equals( SituacaoMatricula.MATRICULADO ) )
			lista.addErro("A matr�cula deste aluno j� foi consolidada. N�o � poss�vel consolidar novamente.");

		ValidatorUtil.validateRequired(consolidacao.getMatricula().getNumeroFaltas(), "N�mero de Faltas", lista);
		ValidatorUtil.validateRange(consolidacao.getMatricula().getNumeroFaltas(), 0, consolidacao.getMatricula().getTurma().getDisciplina().getChTotalAula(), "N�mero de Faltas", lista);

		if( consolidacao.getMatricula().getMetodoAvaliacao() == MetodoAvaliacao.NOTA ){
			ValidatorUtil.validateRequired(consolidacao.getMatricula().getMediaFinal(), "M�dia Final", lista);
			if( consolidacao.getMatricula().getMediaFinal() != null && (consolidacao.getMatricula().getMediaFinal() < 0 || consolidacao.getMatricula().getMediaFinal() > 10) ){
				lista.addErro("M�dia final inv�lida. Insira um valor entra 0 (zero) e 10 (dez).");
			}
		} else if( consolidacao.getMatricula().getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO ){
			ValidatorUtil.validateRequired(consolidacao.getMatricula().getConceito(), "Conceito", lista);
			if( consolidacao.getMatricula().getConceito() != null && (consolidacao.getMatricula().getConceito() < 1 || consolidacao.getMatricula().getConceito() > 5) ){
				lista.addErro("Conceito inv�lido. Selecione um conceito v�lido.");
			}
		} else
			throw new NegocioException("M�todo de avalia��o n�o definido");

	}

}
