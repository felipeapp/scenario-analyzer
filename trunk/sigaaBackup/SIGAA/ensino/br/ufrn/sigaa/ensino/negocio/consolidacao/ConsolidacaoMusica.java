/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/05/2010
 */
package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Regras para consolida��o de turmas de ensino t�cnico
 * de m�sica.
 * 
 * @author David Pereira
 *
 */
public class ConsolidacaoMusica extends AbstractEstrategiaConsolidacao implements EstrategiaConsolidacao {

	@Override
	public Double calculaMediaFinal(MatriculaComponente matricula) {
		
		boolean ficouEmRecuperacao = matricula.isEmRecuperacao() && !matricula.isReprovadoFalta(freqMinima,minutosAulaRegular);
		
		if (!ficouEmRecuperacao || ficouEmRecuperacao && matricula.getRecuperacao() != null) {
	
			Double mediaFinal = calculaMediaSemRecuperacao(matricula);
			if (mediaFinal != null && mediaFinal < mediaMinimaPassarPorMedia && ficouEmRecuperacao) {
				Double notaRecuperacao = matricula.getRecuperacao();
				mediaFinal = (mediaFinal  * pesosMediaRecuperacao[0] * 10 + notaRecuperacao * pesosMediaRecuperacao[1] * 10) / ((pesosMediaRecuperacao[0] + pesosMediaRecuperacao[1]) * 10);
			} else if (mediaFinal != null && mediaFinal < mediaMinimaPassarPorMedia && ficouEmRecuperacao && matricula.getRecuperacao() == null) {
				mediaFinal = null;
			}
		
			// Multiplica por dez para evitar erros de ponto flutuante
			return (mediaFinal == null ? null : Math.round(mediaFinal * 10.0) / 10.0);
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

				somaNota += nota.getNota() * peso;
				somaPeso += peso;
			} else {
				return null;
			}
		}

		return (Math.round((somaNota / (somaPeso == 0 ? 1 : somaPeso)) * 10) / 10.0);
	}

	@Override
	public void consolidar(MatriculaComponente matricula) {
		
		SituacaoMatricula situacao = SituacaoMatricula.APROVADO;

		if (matricula.getMediaFinal() < mediaMinimaAprovacao) {
			situacao = SituacaoMatricula.REPROVADO;
		}
		
		if (matricula.getNumeroFaltas()!= null && matricula.getNumeroFaltas() > matricula.getMaximoFaltas(freqMinima,minutosAulaRegular)) {
			if (matricula.getMediaFinal() < 8.0)
				matricula.setSituacaoMatricula(SituacaoMatricula.REPROVADO_FALTA);
		}

		matricula.setSituacaoMatricula(situacao);
	}

	@Override
	public boolean consolidarAlunosAprovadorPorMedia() {
		return false;
	}

	@Override
	public boolean isEmRecuperacao(MatriculaComponente matricula) {
		if(matricula.getMetodoAvaliacao() == MetodoAvaliacao.NOTA && isPermiteRecuperacao())		
			return matricula.getMediaParcial() != null && matricula.getMediaParcial() >= mediaMinimaPossibilitaRecuperacao && matricula.getMediaParcial() < mediaMinimaPassarPorMedia;
		else{
			return false;
		}
	}

	@Override
	public void validaNotas(Turma turma) throws NegocioException {
		// n�o h� nenhuma implementa��o para a UFRN
	}

}
