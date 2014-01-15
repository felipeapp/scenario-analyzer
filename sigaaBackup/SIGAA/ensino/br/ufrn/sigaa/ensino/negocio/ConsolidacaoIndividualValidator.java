/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
 * Classe que realiza a validação para objetos ConsolidacaoIndividual
 *
 * @author Victor Hugo
 *
 */
public class ConsolidacaoIndividualValidator {

	/**
	 * Valida a submissão dos dados específicos do aluno do técnico
	 * @throws NegocioException
	 */
	public static void validarConsolidacao(ConsolidacaoIndividual consolidacao, ListaMensagens lista) throws NegocioException {

		if( !consolidacao.getMatricula().getSituacaoMatricula().equals( SituacaoMatricula.MATRICULADO ) )
			lista.addErro("A matrícula deste aluno já foi consolidada. Não é possível consolidar novamente.");

		ValidatorUtil.validateRequired(consolidacao.getMatricula().getNumeroFaltas(), "Número de Faltas", lista);
		ValidatorUtil.validateRange(consolidacao.getMatricula().getNumeroFaltas(), 0, consolidacao.getMatricula().getTurma().getDisciplina().getChTotalAula(), "Número de Faltas", lista);

		if( consolidacao.getMatricula().getMetodoAvaliacao() == MetodoAvaliacao.NOTA ){
			ValidatorUtil.validateRequired(consolidacao.getMatricula().getMediaFinal(), "Média Final", lista);
			if( consolidacao.getMatricula().getMediaFinal() != null && (consolidacao.getMatricula().getMediaFinal() < 0 || consolidacao.getMatricula().getMediaFinal() > 10) ){
				lista.addErro("Média final inválida. Insira um valor entra 0 (zero) e 10 (dez).");
			}
		} else if( consolidacao.getMatricula().getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO ){
			ValidatorUtil.validateRequired(consolidacao.getMatricula().getConceito(), "Conceito", lista);
			if( consolidacao.getMatricula().getConceito() != null && (consolidacao.getMatricula().getConceito() < 1 || consolidacao.getMatricula().getConceito() > 5) ){
				lista.addErro("Conceito inválido. Selecione um conceito válido.");
			}
		} else
			throw new NegocioException("Método de avaliação não definido");

	}

}
