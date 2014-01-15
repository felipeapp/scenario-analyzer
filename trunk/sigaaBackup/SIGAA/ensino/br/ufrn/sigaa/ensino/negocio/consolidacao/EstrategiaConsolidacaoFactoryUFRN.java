/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/05/2010
 */
package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.metropoledigital.negocio.MetropoleDigitalHelper;

/**
 * F�brica de estrat�gias de consolida��o. Serve
 * para instanciar a implementa��o correta da interface
 * {@link EstrategiaConsolidacao} de acordo com as informa��es
 * da turma a ser consolidada. 
 * 
 * @author David Pereira
 *
 */
public class EstrategiaConsolidacaoFactoryUFRN implements EstrategiaConsolidacaoFactory{
	
	
	/**
	 * Retorna a estrat�gia adequada de acordo com o discente e os par�metros
	 * passados como argumentos para o m�todo.
	 * @param discente
	 * @param params 
	 * @return
	 * @throws DAOException
	 */
	public EstrategiaConsolidacao getEstrategia(DiscenteAdapter discente, ParametrosGestoraAcademica params) {
		if (discente.isLato()) {
			return getEstrategiaLato(discente.getCurso(),params);
		} else if (discente.isGraduacao()) {
			if (!discente.isRegular()) {
				return getEstrategiaGraduacaoPresencial(params);
			} else if (discente.getCurso().isADistancia()) {
				return getEstrategiaEadBacharelado(params);
			} else {
				return getEstrategiaGraduacaoPresencial(params);
			}
		} else if (discente.isStricto()) {
			return getEstrategiaStricto(params);
		} else if (discente.isTecnico() || discente.isFormacaoComplementar()) {
			if (params.getMetodoAvaliacao() != null && params.getMetodoAvaliacao() == MetodoAvaliacao.COMPETENCIA) {
				return getEstrategiaCompetencia(params);
			} else if(MetropoleDigitalHelper.isMetropoleDigital(discente)){
				return getEstrategiaMetropoleDigital(params);
			} else {
				return getEstrategiaMusica(params);
			}
		} else if (discente.isResidencia()) {
			return getEstrategiaResidencia(params);
		} else if (discente.isMedio()) {
			return getEstrategiaMedio(params);
		}
		return null;		
		
	}
	
	/**
	 * Retorna a estrat�gia adequada de acordo com a turma e os par�metros
	 * passados como argumentos para o m�todo.
	 * @param turma
	 * @param param 
	 * @return
	 * @throws DAOException
	 */
	public EstrategiaConsolidacao getEstrategia(Turma turma, ParametrosGestoraAcademica params) throws DAOException {
		if (turma.getDisciplina().isLato()) {
			return getEstrategiaLato(turma.getCurso(),params);
		} else if (turma.getDisciplina().isGraduacao()) {
			if (turma.isEad()) {
				
				if(turma.getDisciplina().isEstagio())
					return getEstrategiaGraduacaoPresencial(params);
				
				return getEstrategiaEadBacharelado(params);
			} else {
				return getEstrategiaGraduacaoPresencial(params);
			}
		} else if (turma.getDisciplina().isStricto()) {
			return getEstrategiaStricto(params);
		} else if (turma.getDisciplina().isTecnico() || turma.getDisciplina().isFormacaoComplementar()) {
			if (params.getMetodoAvaliacao() != null && params.getMetodoAvaliacao() == MetodoAvaliacao.COMPETENCIA) {
				return getEstrategiaCompetencia(params);
			} else if(MetropoleDigitalHelper.isMetropoleDigital(turma)){
				return getEstrategiaMetropoleDigital(params);
			} else {
				return getEstrategiaMusica(params);
			}
		} else if (turma.getDisciplina().isMedio()) {
			return getEstrategiaMedio(params);
		} else if (turma.isResidenciaMedica()) {
			return getEstrategiaResidencia(params);
		}
		
		return null;
	}

	/**
	 * Instancia a estrat�gia por compet�ncia, usada no ensino t�cnico de enfermagem, por exemplo.
	 * 
	 * @param param
	 * @return
	 */
	private static EstrategiaConsolidacao getEstrategiaCompetencia(ParametrosGestoraAcademica param) {
		ConsolidacaoCompetencia consolidacao = new ConsolidacaoCompetencia();
		consolidacao.setFrequenciaMinima(param.getFrequenciaMinima());
		consolidacao.setMinutosAulaRegular(param.getMinutosAulaRegular());
		consolidacao.setPermiteRecuperacao(param.isPermiteRecuperacao());
		return consolidacao;
	}

	/**
	 * Instancia a estrat�gia de ensino t�cnico de m�sica
	 * 
	 * @param param
	 * @return
	 */
	private static EstrategiaConsolidacao getEstrategiaMusica(ParametrosGestoraAcademica param) {
		ConsolidacaoMusica consolidacao = new ConsolidacaoMusica();
		consolidacao.setMediaMinimaAprovacao(param.getMediaMinimaAprovacao());
		consolidacao.setMediaMinimaPassarPorMedia(param.getMediaMinimaPassarPorMedia());
		consolidacao.setFrequenciaMinima(param.getFrequenciaMinima());
		consolidacao.setMinutosAulaRegular(param.getMinutosAulaRegular());
		consolidacao.setPermiteRecuperacao(param.isPermiteRecuperacao());
		consolidacao.setPesosMediaRecuperacao(param.getPesoMediaRecuperacao());
		return consolidacao;
	}

	/**
	 * Instancia a estrat�gia do metr�pole digital
	 * 
	 * @param param
	 * @return
	 */
	private static EstrategiaConsolidacao getEstrategiaMetropoleDigital(ParametrosGestoraAcademica param) {
		ConsolidacaoMetropoleDigital consolidacao = new ConsolidacaoMetropoleDigital();
		consolidacao.setMediaMinimaAprovacao(param.getMediaMinimaAprovacao());
		consolidacao.setMediaMinimaPassarPorMedia(param.getMediaMinimaPassarPorMedia());
		consolidacao.setFrequenciaMinima(param.getFrequenciaMinima());
		consolidacao.setMinutosAulaRegular(param.getMinutosAulaRegular());
		consolidacao.setReprovacaoBloco(param.isReprovacaoBloco());
		consolidacao.setMediaMinimaPossibilitaRecuperacao(param.getMediaMinimaPossibilitaRecuperacao());
		consolidacao.setPermiteRecuperacao(param.isPermiteRecuperacao());
		return consolidacao;
	}

	/**
	 * Instancia a estrat�gia de p�s gradua��o stricto sensu
	 * 
	 * @param param
	 * @return
	 */
	private static EstrategiaConsolidacao getEstrategiaStricto(ParametrosGestoraAcademica param) {
		ConsolidacaoStricto consolidacao = new ConsolidacaoStricto();
		consolidacao.setMediaMinimaAprovacao(param.getMediaMinimaAprovacao());
		consolidacao.setMediaMinimaPassarPorMedia(param.getMediaMinimaPassarPorMedia());
		consolidacao.setFrequenciaMinima(param.getFrequenciaMinima());
		consolidacao.setMinutosAulaRegular(param.getMinutosAulaRegular());
		consolidacao.setPermiteRecuperacao(param.isPermiteRecuperacao());
		return consolidacao;
	}

	/**
	 * Instancia a estrat�gia de EAD bacharelado
	 * 
	 * @param param
	 * @return
	 */
	private static EstrategiaConsolidacao getEstrategiaEadBacharelado(ParametrosGestoraAcademica param) {
		ConsolidacaoEad consolidacao = new ConsolidacaoEad();
		consolidacao.setMediaMinimaAprovacao(param.getMediaMinimaAprovacao());
		consolidacao.setMediaMinimaPassarPorMedia(param.getMediaMinimaPassarPorMedia());
		consolidacao.setFrequenciaMinima(param.getFrequenciaMinima());
		consolidacao.setMinutosAulaRegular(param.getMinutosAulaRegular());
		consolidacao.setPermiteRecuperacao(param.isPermiteRecuperacao());
		consolidacao.setMediaMinimaPossibilitaRecuperacao(param.getMediaMinimaPossibilitaRecuperacao());
		return consolidacao;
	}

	/**
	 * Instancia a estrat�gia de gradua��o presencial
	 * 
	 * @param param
	 * @return
	 */
	private static EstrategiaConsolidacao getEstrategiaGraduacaoPresencial(ParametrosGestoraAcademica param) {
		ConsolidacaoGraduacao estrategia = new ConsolidacaoGraduacao();
		estrategia.setMediaMinimaPassarPorMedia(param.getMediaMinimaPassarPorMedia());
		estrategia.setMediaMinimaAprovacao(param.getMediaMinimaAprovacao());
		estrategia.setFrequenciaMinima(param.getFrequenciaMinima());
		estrategia.setMinutosAulaRegular(param.getMinutosAulaRegular());
		estrategia.setMediaMinimaPossibilitaRecuperacao(param.getMediaMinimaPossibilitaRecuperacao());
		estrategia.setPermiteRecuperacao(param.isPermiteRecuperacao());
		return estrategia;
	}

	/**
	 * Instancia a estrat�gia de p�s gradua��o lato sensu
	 * 
	 * @param curso
	 * @return
	 */
	private static EstrategiaConsolidacao getEstrategiaLato(Curso curso,ParametrosGestoraAcademica param) {
		CursoLato cursoLato = (CursoLato) curso;
		float frequenciaMinima = cursoLato.getPropostaCurso().getFreqObrigatoria();
		double mediaMinimaAprovacao = cursoLato.getPropostaCurso().getMediaMinimaAprovacao();
		
		EstrategiaConsolidacao estrategia = new ConsolidacaoLato();
		estrategia.setMediaMinimaAprovacao(mediaMinimaAprovacao);
		estrategia.setMediaMinimaPassarPorMedia(mediaMinimaAprovacao);
		estrategia.setFrequenciaMinima(frequenciaMinima);
		estrategia.setMinutosAulaRegular(param.getMinutosAulaRegular());

		return estrategia;
	}

	/**
	 * Instancia a estrat�gia de Resid�ncia M�dica
	 * 
	 * @param param
	 * @return
	 */
	private static EstrategiaConsolidacao getEstrategiaResidencia(ParametrosGestoraAcademica param) {
		ConsolidacaoResidencia consolidacao = new ConsolidacaoResidencia();
		consolidacao.setMediaMinimaAprovacao(param.getMediaMinimaAprovacao());
		consolidacao.setMediaMinimaPassarPorMedia(param.getMediaMinimaPassarPorMedia());
		consolidacao.setFrequenciaMinima(param.getFrequenciaMinima());
		consolidacao.setMinutosAulaRegular(param.getMinutosAulaRegular());
		consolidacao.setPermiteRecuperacao(param.isPermiteRecuperacao());
		consolidacao.setMediaMinimaPossibilitaRecuperacao(param.getMediaMinimaPossibilitaRecuperacao());
		return consolidacao;
	}
	
	/**
	 * Instancia a estrat�gia de Ensino M�dio
	 * 
	 * @param param
	 * @return
	 */
	private static EstrategiaConsolidacao getEstrategiaMedio(ParametrosGestoraAcademica param) {
		ConsolidacaoMedio consolidacao = new ConsolidacaoMedio();
		consolidacao.setMediaMinimaAprovacao(param.getMediaMinimaAprovacao());
		consolidacao.setMediaMinimaPassarPorMedia(param.getMediaMinimaPassarPorMedia());
		consolidacao.setFrequenciaMinima(param.getFrequenciaMinima());
		consolidacao.setMinutosAulaRegular(param.getMinutosAulaRegular());
		consolidacao.setPermiteRecuperacao(param.isPermiteRecuperacao());
		consolidacao.setMediaMinimaPossibilitaRecuperacao(param.getMediaMinimaPossibilitaRecuperacao());
		return consolidacao;
	}

}
