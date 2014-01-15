/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2010
 */
package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Interface utilizada na implementa��o das diversas
 * estrat�gias de consolida��o de turmas. Devem existir
 * diversas implementa��es dessa interface como, por exemplo,
 * para a gradua��o, para ensino a dist�ncia, para t�cnico, etc. 
 * 
 * @author David Pereira
 *
 */
public interface EstrategiaConsolidacao {

	public void consolidar(MatriculaComponente matricula);

	public Double calculaMediaSemRecuperacao(MatriculaComponente matricula);
	
	public Double calculaMediaFinal(MatriculaComponente matricula);
	
	public boolean isEmRecuperacao(MatriculaComponente matricula);
	
	public String getDescricaoSituacao(MatriculaComponente matricula);
	
	public String getMediaFinalDesc(MatriculaComponente matricula);

	public boolean consolidarAlunosAprovadorPorMedia(); 

	public void setMediaMinimaAprovacao(double mediaMinimaAprovacao);
	
	public void setMediaMinimaPassarPorMedia(double mediaMinimaPassarPorMedia);
	
	public void setMediaMinimaPossibilitaRecuperacao(double mediaMinimaPossibilitaRecuperacao);
	
	public void setFrequenciaMinima(float frequenciaMinima);

	public boolean isReprovacaoBloco();
	
	public void setReprovacaoBloco(boolean reprovacaoBloco);
	
	public void validaNotas(Turma turma) throws NegocioException;

	public void setMinutosAulaRegular(float minutosAulaRegular);
}
