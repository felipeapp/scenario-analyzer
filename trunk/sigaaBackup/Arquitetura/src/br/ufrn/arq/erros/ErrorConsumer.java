/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 15/08/2011
 */
package br.ufrn.arq.erros;

import java.util.Queue;

import br.ufrn.arq.erros.gerencia.MovimentoErro;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.FacadeDelegate;

/**
 * Classe consumidora dos erros pendentes. Ela retira objetos da fila
 * de erros e os insere no banco de dados para posterior consulta.
 * 
 * @author David Pereira
 *
 */
public class ErrorConsumer extends Thread {

	private Queue<MovimentoErro> erros;
	
	private FacadeDelegate facade;
	
	public ErrorConsumer(FacadeDelegate facade, Queue<MovimentoErro> erros) {
		this.facade = facade;
		this.erros = erros;
	}

	@Override
	public void run() {

		while (true) {
			MovimentoErro erro = null;
			synchronized (erros) {
				if (!erros.isEmpty()) {
					erro = erros.poll();
				} else {
					try {
						erros.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (erro != null) {
				writeError(erro);
			}
		}

	}

	/**
	 * Insere um erro no banco de dados.
	 */
	private void writeError(MovimentoErro erro) {
		try {
			facade.prepare(ArqListaComando.PROCESSAR_ERRO.getId(), erro.getUsuarioLogado(), erro.getSistema());
			facade.execute(erro, erro.getUsuarioLogado(), erro.getSistema());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public synchronized void setErros(Queue<MovimentoErro> erros) {
		this.erros = erros;
	}

}
