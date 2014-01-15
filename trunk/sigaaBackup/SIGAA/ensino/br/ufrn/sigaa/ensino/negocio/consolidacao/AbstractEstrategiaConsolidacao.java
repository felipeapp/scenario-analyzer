/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/05/2010
 */
package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Classe contendo métodos comuns às estratégias de consolidação.
 *  
 * @author David Pereira
 *
 */
public class AbstractEstrategiaConsolidacao {

	/** Média mínima para o aluno ser aprovado após a recuperação */
	protected double mediaMinimaAprovacao;
	
	/** Média mínima para o aluno ser aprovado por média */
	protected double mediaMinimaPassarPorMedia;

	/** Média mínima para o aluno poder fazer recuperação */
	protected double mediaMinimaPossibilitaRecuperacao;
	
	/** Frequência mínima para o aluno ser aprovado */
	protected float freqMinima;
	
	/** Quantidade de minutos de uma aula */
	protected float minutosAulaRegular;
	
	/** Indica se a estratégia utiliza ou não reprovação por bloco */
	protected boolean reprovacaoBloco;

	/** Indica se a estratégia utiliza ou não recuperação */
	protected boolean permiteRecuperacao;
	
	/** Indica os pesos para o cálculo da nota final quando o discente ficou em recuperação e o cálculo é com média ponderada. */
	protected int [] pesosMediaRecuperacao;
	
	/**
	 * Retorna a descrição da situação da matrícula passada como parâmetro.
	 * 
	 * @param matricula
	 * @return
	 */
	public String getDescricaoSituacao(MatriculaComponente matricula) {
		if (matricula.isConsolidada())
			return getSiglaSituacao(matricula);

		if (matricula.isTrancado())
			return "TRAN";

		boolean reprovadoFaltas = false;
		boolean reprovadoMedia = false;
		
		if (matricula.isReprovadoFalta(freqMinima,minutosAulaRegular)) {
			reprovadoFaltas = true;
		}
		
		if (!reprovadoFaltas && matricula.getMediaParcial() == null) {
			return "--";
		} else if (!reprovadoFaltas && matricula.getMediaParcial() >= mediaMinimaPassarPorMedia) {
			return "APR";
		} else if (!reprovadoFaltas && matricula.isEmRecuperacao() && matricula.getRecuperacao() == null) {
			return "REC";
		} else if (matricula.getMediaParcial() != null && matricula.getMediaParcial() < mediaMinimaPossibilitaRecuperacao) {
			reprovadoMedia = true;
		} else if (!reprovadoFaltas && ((matricula.isEmRecuperacao() && matricula.getRecuperacao() != null) || !matricula.isEmRecuperacao()) && (matricula.getMediaFinal() != null && matricula.getMediaFinal() >= mediaMinimaAprovacao)) {
			return "APR";
		} else if (((matricula.isEmRecuperacao() && matricula.getRecuperacao() != null) || !matricula.isEmRecuperacao()) && (matricula.getMediaFinal() != null && matricula.getMediaFinal() < mediaMinimaAprovacao)) {
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
	 * Retorna uma sigla representando a situação da matrícula no componente curricular. Ex.: APR, REP, REPF. 
	 * 
	 * @param matricula 
	 * @return
	 */
	protected String getSiglaSituacao(MatriculaComponente matricula) {
		if (matricula.isAprovado())
			return "APR";
		else if (matricula.getSituacaoMatricula().equals(SituacaoMatricula.REPROVADO))
			return "REP";
		else if (matricula.getSituacaoMatricula().equals(SituacaoMatricula.REPROVADO_FALTA))
			return "REPF";
		else if (matricula.getSituacaoMatricula().equals(SituacaoMatricula.REPROVADO_MEDIA_FALTA))
			return "REMF";
		else
			return "";
	}

	/**
	 * Retorna a descrição da média final da matrícula.
	 * 
	 * @param matricula
	 * @return
	 */
	public String getMediaFinalDesc(MatriculaComponente matricula) {
		if (matricula.getMetodoAvaliacao() != null && matricula.getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO) {
			return String.valueOf(matricula.getConceitoChar());
		} else {
			return String.valueOf(matricula.getMediaFinal());
		}
	}

	public void setMediaMinimaAprovacao(double mediaMinimaAprovacao) {
		this.mediaMinimaAprovacao = mediaMinimaAprovacao;
	}

	public void setMediaMinimaPassarPorMedia(double mediaMinimaPassarPorMedia) {
		this.mediaMinimaPassarPorMedia = mediaMinimaPassarPorMedia;
	}

	public void setMediaMinimaPossibilitaRecuperacao(double mediaMinimaPossibilitaRecuperacao) {
		this.mediaMinimaPossibilitaRecuperacao = mediaMinimaPossibilitaRecuperacao;
	}

	public void setFrequenciaMinima(float freqMinima) {
		this.freqMinima = freqMinima;
	}

	public void setMinutosAulaRegular(float minutosAulaRegular) {
		this.minutosAulaRegular = minutosAulaRegular;
	}

	public boolean isReprovacaoBloco() {
		return reprovacaoBloco;
	}

	public void setReprovacaoBloco(boolean reprovacaoBloco) {
		this.reprovacaoBloco = reprovacaoBloco;
	}

	public boolean isPermiteRecuperacao() {
		return permiteRecuperacao;
	}

	public void setPermiteRecuperacao(boolean permiteRecuperacao) {
		this.permiteRecuperacao = permiteRecuperacao;
	}
	
		/** 
	 * Recebe a string com os pesos dos parametros e converte para números para serem utilizados nos cálculos.
	 **/
	public void setPesosMediaRecuperacao(String pesoMediaRecuperacaoString) {
		
		pesosMediaRecuperacao = new int [2];
		
		if (pesoMediaRecuperacaoString != null && pesoMediaRecuperacaoString.contains(",")){
			String [] pesos = pesoMediaRecuperacaoString.split(",");
			
			if (pesos.length > 1){
				pesosMediaRecuperacao[0] = Integer.parseInt(pesos[0]);
				pesosMediaRecuperacao[1] = Integer.parseInt(pesos[1]);
			}
		}
	}
}
