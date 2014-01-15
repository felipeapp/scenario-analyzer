/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 27/10/2010
 *
 */
package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Regras para consolida��o de turmas do metr�pole digital.
 * 
 * @author Leonardo Campos
 *
 */
public class ConsolidacaoMetropoleDigital extends
		AbstractEstrategiaConsolidacao implements EstrategiaConsolidacao {

	
	@Override
	public Double calculaMediaFinal(MatriculaComponente matricula) {
		if (permiteRecuperacao)
			return calculaMediaComRecuperacao(matricula);
		else
			return calculaMediaSemRecuperacao(matricula);
	}

	/**
	 * Calcula M�dia considerando a nota da recupera��o
	 * 
	 * @param matricula
	 * @return
	 */
	private Double calculaMediaComRecuperacao(MatriculaComponente matricula) {
		boolean ficouEmRecuperacao = matricula.isEmRecuperacao() && !matricula.isReprovadoFalta(freqMinima,minutosAulaRegular);
		
		if (!ficouEmRecuperacao || ficouEmRecuperacao && matricula.getRecuperacao() != null) {
	
			// Multiplica por dez para evitar problema de ponto flutuante.
			Double mediaFinal = calculaMediaSemRecuperacao(matricula);
			if (mediaFinal != null && mediaFinal < mediaMinimaPassarPorMedia && ficouEmRecuperacao) {
				Double notaRecuperacao = matricula.getRecuperacao();
				mediaFinal = (mediaFinal * 10D + notaRecuperacao * 10D) / 20D;
			} else if (mediaFinal != null && mediaFinal < mediaMinimaPassarPorMedia && ficouEmRecuperacao && matricula.getRecuperacao() == null) {
				mediaFinal = null;
			}
		
			return (mediaFinal == null ? null : Math.round(mediaFinal * 10D) / 10D);
		} else {
			return null;
		}
	}

	
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

				somaNota += nota.getNota() * 10D * peso;
				somaPeso += peso;
			} else {
				return null;
			}
		}

		return (Math.round((somaNota / (somaPeso == 0 ? 10D : somaPeso * 10D)) * 10D) / 10D);
	}

	
	@Override
	public void consolidar(MatriculaComponente matricula) {
		matricula.setSituacaoMatricula(SituacaoMatricula.APROVADO);
	}

	
	@Override
	public boolean consolidarAlunosAprovadorPorMedia() {
		return true;
	}
	
	@Override
	public boolean isEmRecuperacao(MatriculaComponente matricula) {
		if (permiteRecuperacao)
			return matricula.getMediaParcial() != null && matricula.getMediaParcial() >= mediaMinimaPossibilitaRecuperacao && matricula.getMediaParcial() < mediaMinimaPassarPorMedia;

		return false;
	}

	@Override
	public void validaNotas(Turma turma) throws NegocioException {
		// n�o h� nenhuma implementa��o para a UFRN
	}

}
