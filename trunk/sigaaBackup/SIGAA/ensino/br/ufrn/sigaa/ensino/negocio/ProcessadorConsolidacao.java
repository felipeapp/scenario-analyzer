/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 24/07/2008 
 */
package br.ufrn.sigaa.ensino.negocio;

import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;

/**
 * Processador com métodos comuns a consolidação de turma e consolidação individual.
 * 
 * @author David Pereira
 *
 */
public abstract class ProcessadorConsolidacao extends AbstractProcessador {

	/**
	 * Realiza a consolidação do bloco, caso a matrícula seja de uma sub-unidade
	 * de um bloco.
	 * 
	 * @param mov
	 * @param matricula
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public void processarMatriculasBloco(Movimento mov, MatriculaComponente matricula) throws ArqException, NegocioException {
		MatriculaComponenteDao mdao = getDAO(MatriculaComponenteDao.class , mov);
		
		try {
			ComponenteCurricular componente = mdao.refresh( matricula.getTurma().getDisciplina() ); 
			
			ComponenteCurricular bloco = mdao.findByPrimaryKey(componente.getBlocoSubUnidade().getId(), ComponenteCurricular.class);
			int totalComponentes = bloco.getSubUnidades().size();
			
			SituacaoMatricula situacao = SituacaoMatricula.APROVADO;
			int totalConsolidados = 0;
			
			// Verifica quantas matrículas foram consolidadas e se existem reprovadas
			
			List<MatriculaComponente> matriculasBloco = mdao.findMatriculasSubUnidadesByBloco(matricula.getDiscente(), matricula.getTurma().getAno(), matricula.getTurma().getPeriodo(), bloco);
			for (MatriculaComponente mc : matriculasBloco) {
				mc.getSituacaoMatricula().getId();
				
				if (mc.isConsolidada()) {
					totalConsolidados++;
					if (!matricula.getEstrategia().isReprovacaoBloco() && mc.getSituacaoMatricula().getId() != SituacaoMatricula.APROVADO.getId())
						situacao = SituacaoMatricula.REPROVADO;
				}
			}
			
			if (matricula.getEstrategia().isReprovacaoBloco() && totalConsolidados == totalComponentes){
				matricula.getTurma().setDisciplina(matricula.getComponente());
				ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(matricula.getTurma());
				
				situacao = SituacaoMatricula.APROVADO;
				boolean reprovadoNota = false;
				if(mediaFinalBloco(matriculasBloco) < param.getMediaMinimaAprovacao()){
					situacao = SituacaoMatricula.REPROVADO;
					reprovadoNota = true;
				}
				
				if (numeroFaltasBloco(matriculasBloco) > getMaximoFaltas(param.getFrequenciaMinima(), param.getMinutosAulaRegular(), bloco.getChTotal())) {
					if (!reprovadoNota)
						situacao = SituacaoMatricula.REPROVADO_FALTA;
					else
						situacao = SituacaoMatricula.REPROVADO_MEDIA_FALTA;
				}
				
				// Altera a situação das matrículas em todas as subUnidades do Bloco
				for (MatriculaComponente mc : matriculasBloco) {
					MatriculaComponenteHelper.alterarSituacaoMatricula(mc, situacao, mov, mdao);
				}
			}
			
			MatriculaComponente matriculaNoBloco = mdao.findMatriculaByComponenteDiscenteAnoPeriodo(bloco, matricula.getDiscente().getDiscente(), matricula.getTurma().getAno(), matricula.getTurma().getPeriodo());
			
			// Se todas tiverem sido consolidadas
			if (totalConsolidados == totalComponentes) {
				// criar matrícula para o bloco
				
				if (matriculaNoBloco == null) {
					MatriculaComponente mc = new MatriculaComponente();
					mc.setDiscente(matricula.getDiscente());
					mc.setTurma(null);
					mc.setComponente(bloco);
					mc.setDataCadastro(new Date());
					mc.setSituacaoMatricula(situacao);
					mc.setAno((short) matricula.getTurma().getAno());
					mc.setPeriodo((byte) matricula.getTurma().getPeriodo());
					mc.setNumeroFaltas(numeroFaltasBloco(matriculasBloco));
					mc.setMediaFinal(mediaFinalBloco(matriculasBloco));
					mc.setTipoIntegralizacao(TipoIntegralizacao.OBRIGATORIA);
					mc.setDetalhesComponente(bloco.getDetalhes());
					
					mdao.create(mc);
				}
				else {
					matriculaNoBloco.setSituacaoMatricula(situacao);
					matriculaNoBloco.setNumeroFaltas(numeroFaltasBloco(matriculasBloco));
					matriculaNoBloco.setMediaFinal(mediaFinalBloco(matriculasBloco));
					matriculaNoBloco.setTipoIntegralizacao(TipoIntegralizacao.OBRIGATORIA);
					
					mdao.update(matriculaNoBloco);
				}
			}
		} finally {
			mdao.close();
		}

	}

	/**
	 * Calcula a média final de um bloco fazendo uma média ponderada das médias
	 * finais de cada sub-unidade.
	 * 
	 * @param matriculasBloco
	 * @return
	 */
	private Double mediaFinalBloco(List<MatriculaComponente> matriculasBloco) {
		double media = 0.0;
		int chTotal = 0;
		
		for (MatriculaComponente mc : matriculasBloco) {
			media += mc.getMediaFinal() * mc.getComponenteCHTotal();
			chTotal += mc.getComponenteCHTotal();
		}

		return Math.round((media / chTotal) * 10.0) / 10.0;
	}

	/**
	 * Calcula o número de faltas de um bloco baseado no número de faltas de cada sub-unidade
	 * 
	 * @param matriculasBloco
	 * @return
	 */
	private Integer numeroFaltasBloco(List<MatriculaComponente> matriculasBloco) {
		int faltas = 0;
		for (MatriculaComponente mc : matriculasBloco) {
			if (mc.getNumeroFaltas() != null)
				faltas += mc.getNumeroFaltas();
		}
		return faltas;
	}

	/**
	 * Retorna o número máximo de faltas 
	 * @param frequenciaMinima
	 * @param minutosAulaRegular
	 * @param chTotal
	 * @return
	 */
	private int getMaximoFaltas(double frequenciaMinima, double minutosAulaRegular, int chTotal){
		
		int totalAulas =  (int) (chTotal * 60 / minutosAulaRegular);
		return (int) (totalAulas - (totalAulas * frequenciaMinima/100.0));
	}
}
