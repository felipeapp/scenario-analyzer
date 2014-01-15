/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/02/2011
 *
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * @author Leonardo Campos
 *
 */
public class AcessoFormacaoComplementar extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) {
		if (usuario.isUserInSubSistema(SigaaSubsistemas.FORMACAO_COMPLEMENTAR.getId())) {
			dados.setFormacaoComplementar(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.FORMACAO_COMPLEMENTAR, true));
			dados.incrementaTotalSistemas();
		}
	}

}
