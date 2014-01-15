/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 30/09/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para o gerenciamento do Portal de Relatórios
 * @author Arlindo Rodrigues
 *
 */
public class AcessoRelatorios extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.PORTAL_RELATORIOS.getId())) {
			dados.setRelatorios(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_RELATORIOS, true));
			dados.incrementaTotalSistemas();
		}
		
	}

}
