/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Criação: 30/09/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.web.LogonProgress;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Chain of Responsibility para executar o processamento de permissões
 * dos AcessoMenu em cadeias sequenciais.
 * 
 * @author David Pereira
 *
 */
public abstract class AcessoMenuExecutor extends AbstractAction {

	private AcessoMenuExecutor next;

	public void executar(DadosAcesso dados, HttpServletRequest req, LogonProgress progress) throws ArqException {
		Usuario usuario = dados.getUsuario();
		
		processar(dados, usuario, req);
		progress.increment();
		if (next != null)
			next.executar(dados, req, progress);
	}
	
	public abstract void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException;
	
	public AcessoMenuExecutor getNext() {
		return next;
	}

	public AcessoMenuExecutor setNext(AcessoMenuExecutor next) {
		this.next = next;
		return next;
	}
	
}
