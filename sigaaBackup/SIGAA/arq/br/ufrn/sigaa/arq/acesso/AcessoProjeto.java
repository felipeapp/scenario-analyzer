package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permiss�es para acesso ao M�dulo de Projetos. 
 * 
 * @author Ilueny Santos
 *
 */
public class AcessoProjeto extends AcessoMenuExecutor {
    
	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		//TODO: processar permis�es de projetos...
	}
	    

}
