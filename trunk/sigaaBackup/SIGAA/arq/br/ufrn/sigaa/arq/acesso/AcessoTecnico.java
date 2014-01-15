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
 * Processamento de permissões para acesso ao ensino Técnico 
 * 
 * @author David Pereira
 *
 */
public class AcessoTecnico extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) {
		if (usuario.isUserInSubSistema(SigaaSubsistemas.TECNICO.getId())) {
			dados.setTecnico(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.TECNICO, true));
			
			dados.incrementaTotalSistemas();
		}
	}

}
