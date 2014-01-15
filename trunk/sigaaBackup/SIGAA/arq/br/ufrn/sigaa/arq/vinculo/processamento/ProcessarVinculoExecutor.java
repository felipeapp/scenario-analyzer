/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.web.struts.AbstractAction;

/**
 * Chain of Responsibility
 * 
 * @author Henrique André
 *
 */
public abstract class ProcessarVinculoExecutor extends AbstractAction {

	/**
	 * Próximo processo a ser executado
	 */
	private ProcessarVinculoExecutor next;
	
	/**
	 * Processa a lógica de negócio
	 * 
	 * @param req
	 * @param dados
	 * @throws ArqException
	 */
	public abstract void processar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException;
	
	public ProcessarVinculoExecutor setNext(ProcessarVinculoExecutor next) {
		this.next = next;
		return next;
	}	
	
	/**
	 * Executa o próxima item da cadeia
	 * 
	 * @param req
	 * @param dados
	 * @throws ArqException
	 */
	public void executar(HttpServletRequest req, DadosProcessamentoVinculos dados) throws ArqException {
		processar(req, dados);

		if (next != null)
			next.executar(req, dados);
	}

}
