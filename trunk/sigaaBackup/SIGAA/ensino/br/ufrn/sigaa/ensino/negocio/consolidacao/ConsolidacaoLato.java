/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 24/03/2010
 */
package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Regras para consolida��o de turmas de p�s gradua��o
 * lato sensu.
 * 
 * @author David Pereira
 *
 */
public class ConsolidacaoLato extends AbstractEstrategiaConsolidacao implements EstrategiaConsolidacao {

	/**
	 * Realizar o calculo da m�dia final.
	 */
	@Override
	public Double calculaMediaFinal(MatriculaComponente matricula) {
		return calculaMediaSemRecuperacao(matricula);
	}

	/**
	 * Realizar o calculo da m�dia final sem a recupera��o.
	 */
	@Override
	public Double calculaMediaSemRecuperacao(MatriculaComponente matricula) {
		double somaNota = 0.0;
		int somaPeso = 0;
		
		for (NotaUnidade nota : matricula.getNotas()) {
			if (!nota.isRecuperacao() && nota.getNota() != null) {
				int peso = 0;
				if (nota.getPeso() != null) {
					peso = Integer.parseInt(nota.getPeso());
				}

				somaNota += nota.getNota() * peso;
				somaPeso += peso;
			} else {
				return null;
			}
		}

		return (Math.round((somaNota / (somaPeso == 0 ? 1 : somaPeso)) * 10) / 10.0);
	}

	/** Realizar a consolida��o os discentes de acordo a medial Final. */
	@Override
	public void consolidar(MatriculaComponente matricula) {
		SituacaoMatricula situacao = SituacaoMatricula.APROVADO;

		boolean reprovadoNota = false;
		if (matricula.getMediaFinal() < mediaMinimaAprovacao) {
			situacao = SituacaoMatricula.REPROVADO;
			reprovadoNota = true;
		}
		
		if (matricula.getNumeroFaltas() != null && matricula.getNumeroFaltas() > matricula.getMaximoFaltas(freqMinima,minutosAulaRegular)) {
			if (!reprovadoNota)
				situacao =  SituacaoMatricula.REPROVADO_FALTA;
			else
				situacao = SituacaoMatricula.REPROVADO_MEDIA_FALTA;
		}

		matricula.setSituacaoMatricula(situacao);
		
	}

	/** Retornar um Boleano informando se o discente est� ou n�o em recupera��o */
	@Override
	public boolean isEmRecuperacao(MatriculaComponente matricula) {
		return false;
	}

	/** Retornar um Boleano informando se deve consolidar o discente aprovado por f�rias ou n�o. */
	@Override
	public boolean consolidarAlunosAprovadorPorMedia() {
		return false;
	}

	@Override
	public void validaNotas(Turma turma) throws NegocioException {
		// n�o h� nenhuma implementa��o para a UFRN
	}

}