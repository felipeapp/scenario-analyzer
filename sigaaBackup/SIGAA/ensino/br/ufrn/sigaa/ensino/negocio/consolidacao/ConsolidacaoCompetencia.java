/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 31/05/2010
 */
package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Regras de consolida��o para metodo de avalia��o por compet�ncia.
 * 
 * @author David Pereira
 *
 */
public class ConsolidacaoCompetencia extends AbstractEstrategiaConsolidacao implements EstrategiaConsolidacao {

	@Override
	public Double calculaMediaFinal(MatriculaComponente matricula) {
		return null;
	}

	@Override
	public Double calculaMediaSemRecuperacao(MatriculaComponente matricula) {
		return null;
	}

	@Override
	public void consolidar(MatriculaComponente matricula) {
		if (matricula.getApto() && !matricula.isReprovadoFalta(freqMinima,minutosAulaRegular))
			matricula.setSituacaoMatricula(SituacaoMatricula.APROVADO);
		else if (matricula.getApto() && matricula.isReprovadoFalta(freqMinima,minutosAulaRegular))
			matricula.setSituacaoMatricula(SituacaoMatricula.REPROVADO_FALTA);
		else
			matricula.setSituacaoMatricula(SituacaoMatricula.REPROVADO);
	}

	@Override
	public boolean consolidarAlunosAprovadorPorMedia() {
		return false;
	}

	@Override
	public boolean isEmRecuperacao(MatriculaComponente matricula) {
		return false;
	}
	
	@Override
	public String getDescricaoSituacao(MatriculaComponente matricula) {
		if (matricula.getApto() == null)
			return "--";
		if (matricula.getApto() && !matricula.isReprovadoFalta(freqMinima,minutosAulaRegular))
			return "APR";
		else if (matricula.getApto() && matricula.isReprovadoFalta(freqMinima,minutosAulaRegular))
			return "REMF";
		else if (!matricula.getApto() && matricula.isReprovadoFalta(freqMinima,minutosAulaRegular))
			return "REPF";
		else
			return "REP";
		
	}

	@Override
	public void validaNotas(Turma turma) throws NegocioException {
		// n�o h� nenhuma implementa��o para a UFRN
	}

}
