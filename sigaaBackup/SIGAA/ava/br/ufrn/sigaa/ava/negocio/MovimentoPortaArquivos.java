/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ava.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 * Movimento para a��es do porta-arquivos
 * 
 * @author David Pereira
 *
 */
public class MovimentoPortaArquivos extends MovimentoCadastro {

	private int origem;
	
	private int destino;

	public int getOrigem() {
		return origem;
	}

	public void setOrigem(int origem) {
		this.origem = origem;
	}

	public int getDestino() {
		return destino;
	}

	public void setDestino(int destino) {
		this.destino = destino;
	}
	
}
