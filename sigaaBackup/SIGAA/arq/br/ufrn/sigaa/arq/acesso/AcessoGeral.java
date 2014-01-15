/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 01/10/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões gerais de acesso no SIGAA.
 * 
 * @author David Pereira
 *
 */
public class AcessoGeral extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.ADMINISTRACAO.getId())) {
			dados.setAdministracao(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.ADMINISTRACAO, true));
			dados.incrementaTotalSistemas();
		}

		if (usuario.isUserInSubSistema(SigaaSubsistemas.PROD_INTELECTUAL.getId())) {
			dados.setProdocente(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PROD_INTELECTUAL, true));
			dados.incrementaTotalSistemas();
		}

		if (usuario.isUserInSubSistema(SigaaSubsistemas.PORTAL_PLANEJAMENTO.getId())) {
			dados.setPlanejamento(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_PLANEJAMENTO, true));
			dados.incrementaTotalSistemas();
		}

		if (usuario.isUserInRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL)) {
			dados.setAvaliacao(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL, true));
			dados.incrementaTotalSistemas();
		}
		
	}

}
