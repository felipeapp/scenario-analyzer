/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 30/09/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para acesso a Graduação 
 * 
 * @author David Pereira
 *
 */
public class AcessoGraduacao extends AcessoMenuExecutor {

	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) {
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.GRADUACAO.getId())) {
			dados.setGraduacao(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.GRADUACAO, true));
			dados.incrementaTotalSistemas();

			dados.setCdp(usuario.isUserInRole(SigaaPapeis.CDP));
			dados.setAdministradorDAE(usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE));
			dados.setDae(usuario.isUserInRole(SigaaPapeis.DAE));
			dados.setCoordenacaoProbasica(usuario.isUserInRole(SigaaPapeis.GESTOR_PROBASICA));
		}		
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.CONSULTA.getId())) {
			dados.setConsulta(true);
			dados.setGraduacao(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.CONSULTA, true));
			dados.incrementaTotalSistemas();
		}
		
	}

}
