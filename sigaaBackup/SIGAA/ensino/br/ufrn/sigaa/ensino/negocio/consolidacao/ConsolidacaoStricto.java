/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2010
 */
package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Regras para consolidação de turmas de pós graduação
 * stricto sensu.
 * 
 * @author David Pereira
 *
 */
public class ConsolidacaoStricto extends AbstractEstrategiaConsolidacao implements EstrategiaConsolidacao {

	@Override
	public Double calculaMediaFinal(MatriculaComponente matricula) {
		if(matricula.getMetodoAvaliacao() == MetodoAvaliacao.NOTA) {
			if (!matricula.isEmRecuperacao() || matricula.isEmRecuperacao() && matricula.getRecuperacao() != null) {
				boolean ficouEmRecuperacao = matricula.isEmRecuperacao();
		
				// Multiplica por dez para evitar problema de ponto flutuante.
				Double mediaFinal = calculaMediaSemRecuperacao(matricula);
				if (mediaFinal != null && mediaFinal < mediaMinimaPassarPorMedia && ficouEmRecuperacao) {
					Double notaRecuperacao = matricula.getRecuperacao();
					mediaFinal = (mediaFinal * (matricula.getPesoMedia()*10D) + notaRecuperacao * (matricula.getPesoRecuperacao())*10D) / ( (matricula.getPesoMedia() + matricula.getPesoRecuperacao())*10D );
				} else if (mediaFinal != null && mediaFinal < mediaMinimaPassarPorMedia && matricula.isEmRecuperacao() && matricula.getRecuperacao() == null) {
					mediaFinal = null;
				}
			
				return (mediaFinal == null ? null : Math.round(mediaFinal * 10D) / 10D);
			} else {
				return null;
			}
		}
		else {
			return matricula.getMediaFinal();
		}
		
		
	}

	@Override
	public Double calculaMediaSemRecuperacao(MatriculaComponente matricula) {
		
		
		if(matricula.getMetodoAvaliacao() == MetodoAvaliacao.NOTA) {
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

			double rs = Math.round((somaNota / (somaPeso == 10 ? 1 : somaPeso * 10D)) * 10D ) / 10D;
			return rs;
		}
		else{		
			return matricula.getMediaFinal();
		}
	}

	@Override
	public void consolidar(MatriculaComponente matricula) {
		SituacaoMatricula situacao = SituacaoMatricula.APROVADO;

		boolean reprovadoNota = false;
		if (matricula.getMediaFinal() < mediaMinimaAprovacao) {
			situacao = SituacaoMatricula.REPROVADO;
			reprovadoNota = true;
		}
		
		if (matricula.getNumeroFaltas()!= null &&  matricula.getNumeroFaltas() > matricula.getMaximoFaltas(freqMinima,minutosAulaRegular)) {
			if (!reprovadoNota)
				situacao = SituacaoMatricula.REPROVADO_FALTA;
			else
				situacao =  SituacaoMatricula.REPROVADO_MEDIA_FALTA;
		}

		matricula.setSituacaoMatricula(situacao);
	}

	@Override
	public boolean isEmRecuperacao(MatriculaComponente matricula) {
		if(matricula.getMetodoAvaliacao() == MetodoAvaliacao.NOTA && permiteRecuperacao){
			return matricula.getMediaParcial() != null && matricula.getMediaParcial() >= mediaMinimaPossibilitaRecuperacao && matricula.getMediaParcial() < mediaMinimaPassarPorMedia;			
		}
		else {
			return false;
		}
	}

	@Override
	public boolean consolidarAlunosAprovadorPorMedia() {
		return false;
	}

	@Override
	public void validaNotas(Turma turma) throws NegocioException {
		// não há nenhuma implementação para a UFRN
	}

}
