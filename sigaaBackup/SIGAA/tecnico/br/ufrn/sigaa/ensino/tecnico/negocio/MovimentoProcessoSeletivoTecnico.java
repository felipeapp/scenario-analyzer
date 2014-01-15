/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/04/2009
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;

/**
 * Classe respons�vel pelo encapsulamento dos objetos para o cadastro de
 * Processos Seletivos de n�vel T�cnico.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class MovimentoProcessoSeletivoTecnico extends AbstractMovimentoAdapter {

	/** Processo Seletivo. */
	private ProcessoSeletivoTecnico processoSeletivo;
	
	/** Retorna o Processo Seletivo. 
	 * @return
	 */
	public ProcessoSeletivoTecnico getProcessoSeletivo() {
		return processoSeletivo;
	}

	/** Seta o Processo Seletivo.
	 * @param processoSeletivo
	 */
	public void setProcessoSeletivo(ProcessoSeletivoTecnico processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

}
