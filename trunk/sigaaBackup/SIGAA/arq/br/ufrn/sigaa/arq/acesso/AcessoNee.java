/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/10/2010
 *
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para acesso ao NEE. 
 * 
 * @author Rafael Gomes
 *
 */
public class AcessoNee extends AcessoMenuExecutor {

	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) {
		if (usuario.isUserInSubSistema(SigaaSubsistemas.NEE.getId())) {
			dados.setNee(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.NEE, true));
			dados.incrementaTotalSistemas();
		}
		
	}

}
