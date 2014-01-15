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
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Interface utilizada na implementação das diversas
 * estratégias de consolidação de turmas. Devem existir
 * diversas implementações dessa interface como, por exemplo,
 * para a graduação, para ensino a distância, para técnico, etc. 
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
