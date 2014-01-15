/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 30/09/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para acesso ao Vestibular 
 * 
 * @author David Pereira
 *
 */
public class AcessoVestibular extends AcessoMenuExecutor {

	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) {
		if (usuario.isUserInSubSistema(SigaaSubsistemas.VESTIBULAR.getId())) {
			dados.setVestibular(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.VESTIBULAR, true));
			dados.incrementaTotalSistemas();
		} else if (usuario.getVinculoAtivo().isVinculoServidor()) {
			dados.setVestibular(true);
			dados.incrementaTotalSistemas();
		}
		
	}

}
