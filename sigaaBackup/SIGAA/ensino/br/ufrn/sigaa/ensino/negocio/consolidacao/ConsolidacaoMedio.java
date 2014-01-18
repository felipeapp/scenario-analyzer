/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 27/07/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.negocio.consolidacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino.dao.RegraNotaDao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.RegraNota;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dao.NotaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.NotaSerie;

/**
 * Regras para consolidação de turmas de ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class ConsolidacaoMedio extends AbstractEstrategiaConsolidacao implements EstrategiaConsolidacao{

	/**
	 * Calcula a média final da disciplina informada
	 */
	@Override
	public Double calculaMediaFinal(MatriculaComponente matricula) {
		Double mediaFinalSemRecuperacao = calculaMediaSemRecuperacao(matricula);
		boolean ficouEmRecuperacao = matricula.isEmRecuperacao() && !matricula.isReprovadoFalta(freqMinima,minutosAulaRegular);
		return calculaMediaFinalNotaSerie(matricula, mediaFinalSemRecuperacao, freqMinima, mediaMinimaPassarPorMedia, ficouEmRecuperacao);
	}

	/**
	 * Calcula a média final sem a recuperação
	 */
	@Override
	public Double calculaMediaSemRecuperacao(MatriculaComponente matricula) {		
		if (matricula.getNotaDisciplina() != null && matricula.getNotaDisciplina().getNotas() != null)
			return calculaMediaNotaSerie(matricula.getNotaDisciplina().getNotas()) ;
		else
			return calculaMediaNotaSerie(prepararNotasSerie(matricula)) ;
	}

	/**
	 * Consolida todos os alunos aprovados por média
	 */
	@Override
	public boolean consolidarAlunosAprovadorPorMedia() {
		return true;
	}

	/**
	 * Verifica se está em recuperação
	 */
	@Override
	public boolean isEmRecuperacao(MatriculaComponente matricula) {
		
		Double mediaParcial = getMediaParcial(matricula);
		
		return mediaParcial != null 
				&& mediaParcial < mediaMinimaPassarPorMedia;
	}

	/**
	 * Valida notas
	 */
	@Override
	public void validaNotas(Turma turma) throws NegocioException {
		// não há nenhuma implementação para a Instituição.
		
	}
	
	/**
	 * Método responsável por preparar as notas das disciplinas, 
	 * conforme as notas unidades e regra de notas relacionadas.
	 * 
	 * @param matricula
	 * @return
	 */
	public List<NotaSerie> prepararNotasSerie(MatriculaComponente matricula){
		
		NotaSerieDao notasDao = new NotaSerieDao(); 
		RegraNotaDao regraNotaDao = new RegraNotaDao();
		
		try {
			List<NotaSerie> notas = notasDao.findNotasByDisciplina(new DiscenteMedio(matricula.getDiscente().getId()),matricula);
			List<RegraNota> regras = regraNotaDao.findByCurso(matricula.getSerie().getCursoMedio());
			
			boolean insereNotaSerie = true;
			Iterator<RegraNota> it = regras.iterator(); 
			while (it.hasNext()) {
				RegraNota rn = it.next();
				for (NotaSerie ns : notas) {
					if ( (ns.getRegraNota().getId() == rn.getId()) ){
						ns.setRegraNota(rn);
						insereNotaSerie = false;
					 	break;
					}	
				}
				if (insereNotaSerie) {
					NotaSerie novaNotaSerie = new NotaSerie();
					novaNotaSerie.setRegraNota(rn);
					novaNotaSerie.setNotaUnidade(new NotaUnidade());
					novaNotaSerie.getNotaUnidade().setMatricula(matricula);
					notas.add(novaNotaSerie);
				}
				insereNotaSerie = true;
			}
			return notas;
		} catch (DAOException e) {
			e.printStackTrace();
			return null;
		} finally {
			notasDao.close();
			regraNotaDao.close();
		}
	}

	/**
	 * Método responsável pelo calculo da média final de todas as notas série de uma disciplina do ensino médio.
	 * 
	 * @return
	 */
	public static Double calculaMediaFinalNotaSerie(
			MatriculaComponente matricula, Double mediaFinal, float freqMinima, double mediaMinimaPassarPorMedia, boolean ficouEmRecuperacao){
		
		if ( !ficouEmRecuperacao || (ficouEmRecuperacao && matricula.getRecuperacao() != null) ) {
	
			// Multiplica por dez para evitar problema de ponto flutuante.
			if (mediaFinal != null && mediaFinal < mediaMinimaPassarPorMedia && ficouEmRecuperacao) {
				Double notaRecuperacao = matricula.getRecuperacao();
				mediaFinal = ((mediaFinal * 2 + notaRecuperacao) / 3);
//				mediaFinal = (mediaFinal * (matricula.getPesoMedia()*10D) + notaRecuperacao * (matricula.getPesoRecuperacao())*10D) / ( (matricula.getPesoMedia() + matricula.getPesoRecuperacao())*10D );
			} else if (mediaFinal != null && mediaFinal < mediaMinimaPassarPorMedia && ficouEmRecuperacao && matricula.getRecuperacao() == null) {
				mediaFinal = null;
			}
		
			return (mediaFinal == null ? null : Math.round(mediaFinal * 10D) / 10D);
		} else {
			return null;
		}
	}
	
	/**
	 * Método responsável por fazer o cálculo da média das notas de série, sem levar em consideração a recuperação final,
	 * levando em consideração apenas as recuperações bimestrais.
	 * @param notasSerie
	 * @return
	 * @throws DAOException 
	 */
	public static Double calculaMediaNotaSerie(List<NotaSerie> notasSerie){
		double somaNota = 0.0;
		List<NotaSerie> notasCalculadas = new ArrayList<NotaSerie>();
		List<NotaSerie> notasSubstituidasRec = new ArrayList<NotaSerie>();
		RegraNotaDao regraNotaDao = new RegraNotaDao();
		int qtdUnidadeNota = 0;
		int qtdNotasInseridas = 0;
		try {
		
		for (NotaSerie notaSerie : notasSerie) {
			if( notaSerie.getRegraNota().isNota() ) 
				qtdUnidadeNota++;
			if( notaSerie.getRegraNota().isNota() && notaSerie.getNotaUnidade().getNota() != null) 
				qtdNotasInseridas++;
			if( (notaSerie.getRegraNota().isNota() || notaSerie.getRegraNota().isRecuperacao()) 
					&& notaSerie.getNotaUnidade().getNota() != null )
				notasCalculadas.add(notaSerie);
		
			if( notaSerie.getRegraNota().isRecuperacao() ){

				String[] refRec = notaSerie.getRegraNota().getRefRec().trim().split(",");
				List<Integer> idsRegraRefRec = new ArrayList<Integer>();
				
				for (int i = 0; i < refRec.length; i++) {
					idsRegraRefRec.add(Integer.parseInt(refRec[i]));
				}
				List<NotaSerie> notasRefComRec = new ArrayList<NotaSerie>();
				for (NotaSerie notaRefRec : notasSerie) {
					if ( idsRegraRefRec.contains(new Integer(notaRefRec.getRegraNota().getId())) 
							&& notaRefRec.getNotaUnidade().getNota() != null ){
						notasRefComRec.add(notaRefRec);
					}
				}
				if (notaSerie.getNotaUnidade().getNota() != null) {
					notasRefComRec.add(notaSerie);
					Collections.sort(notasRefComRec);
					notasSubstituidasRec.add(notasRefComRec.get(0));
				}	
			}
			
		}

		Iterator<NotaSerie> it = notasCalculadas.iterator();
		while (it.hasNext()){
			NotaSerie ns = it.next();
			if (notasSubstituidasRec.contains(ns))
				it.remove();
		}
		
		for (NotaSerie nota : notasCalculadas) {
			if (nota.getNotaUnidade().getNota() != null) 
				somaNota += nota.getNotaUnidade().getNota();
		}

		double rs = Math.round( (somaNota / qtdUnidadeNota ) * 10D) / 10D;
		return qtdNotasInseridas >= qtdUnidadeNota ? rs : null;
		
		} finally {
			regraNotaDao.close();
		}
	}
	
	/**
	 * Método responsável por fazer o cálculo de faltas de série.
	 * @param notasSerie
	 * @return
	 */
	public static Integer calculaFaltasSerie(List<NotaSerie> notasSerie){
		short somaFalta = 0;
		for (NotaSerie notaSerie : notasSerie) {
			if( notaSerie.getRegraNota().isNota() ) 
				somaFalta += notaSerie.getNotaUnidade().getFaltas();
		}
		return new Integer(somaFalta);
	}
	
	/**
	 * Consolida disciplina
	 */
	@Override
	public void consolidar(MatriculaComponente matricula) {
		SituacaoMatricula situacao = SituacaoMatricula.APROVADO;

		boolean reprovadoNota = false;
		if ( matricula.getMediaFinal() == null || matricula.getMediaFinal() < mediaMinimaAprovacao) {
			situacao = SituacaoMatricula.REPROVADO;
			reprovadoNota = true;
		}
		
		if (matricula.getNumeroFaltas() != null && matricula.getNumeroFaltas() > matricula.getMaximoFaltas(freqMinima,minutosAulaRegular)) {
			if (!reprovadoNota)
				situacao = SituacaoMatricula.REPROVADO_FALTA;
			else
				situacao = SituacaoMatricula.REPROVADO_MEDIA_FALTA;
		}

		matricula.setSituacaoMatricula(situacao);
		
	}
	
	/**
	 * Retorna a descrição da situação da matrícula passada como parâmetro.
	 * 
	 * @param matricula
	 * @return
	 */
	@Override
	public String getDescricaoSituacao(MatriculaComponente matricula) {
		
		if (getSiglaSituacaoAproveitamento(matricula) != null)
			return getSiglaSituacaoAproveitamento(matricula);
			
		if (matricula.isConsolidada())
			return getSiglaSituacao(matricula);

		if (matricula.isTrancado())
			return "TRAN";

		boolean reprovadoFaltas = false;
		boolean reprovadoMedia = false;
		
		if (matricula.isReprovadoFalta(freqMinima,minutosAulaRegular)) {
			reprovadoFaltas = true;
		}
		
		Double mediaParcial = getMediaParcial(matricula);
		
		if (!reprovadoFaltas && mediaParcial == null) {
			return "--";
		} else if (!reprovadoFaltas && mediaParcial >= mediaMinimaPassarPorMedia) {
			return "APR";
		} else if (!reprovadoFaltas && matricula.isEmRecuperacao() && matricula.getRecuperacao() == null) {
			return "REC";
		} else if (!reprovadoFaltas 
				&& (
					(matricula.isEmRecuperacao() 
					&& matricula.getRecuperacao() != null 
					&& matricula.getMediaFinal() != null 
					&& matricula.getMediaFinal() >= mediaMinimaAprovacao) 
					)) {
			return "APR";
		} else if (mediaParcial != null && mediaParcial < mediaMinimaAprovacao) {
			reprovadoMedia = true;
		} else if (((matricula.isEmRecuperacao() && matricula.getRecuperacao() != null) 
				|| !matricula.isEmRecuperacao()) && (matricula.getMediaFinal() != null 
						&& matricula.getMediaFinal() < mediaMinimaAprovacao)) {
			reprovadoMedia = true;
		}
		
		if (reprovadoMedia && !reprovadoFaltas)
			return "REP";
		else if (reprovadoFaltas && !reprovadoMedia)
			return "REPF";
		else if (reprovadoMedia && reprovadoFaltas)
			return "REMF";
		else
			return "--";
	}

	/**
	 * Retorna a média parcial
	 * @param matricula
	 * @return
	 */
	private Double getMediaParcial(MatriculaComponente matricula) {
		Double mediaParcial = null;
		
		if (matricula.getNotaDisciplina() != null && matricula.getNotaDisciplina().getNotas() != null)
			mediaParcial = matricula.getNotaDisciplina().calculaMedia();	
		else
			mediaParcial = matricula.getMediaParcial();
		return mediaParcial;
	}
	
	/** 
	 * Retorna uma sigla representando a situação da matrícula do tipo aproveitamento. 
	 * 
	 * @param matricula 
	 * @return
	 */
	protected String getSiglaSituacaoAproveitamento(MatriculaComponente matricula) {
		if (matricula.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_DISPENSADO))
			return "DISP";
		else if (matricula.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_CUMPRIU))
			return "CUMPRIU";
		else if (matricula.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_TRANSFERIDO))
			return "TRANSF";
		else
			return null;
	}
	
}
