/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 23/08/2010
 *
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para acesso ao módulo
 * de Registro de Diplomas
 * 
 * @author Édipo Elder F. Melo
 *
 */public class AcessoRegistroDiploma extends AcessoMenuExecutor {

	/** Processa as permissões
	 * @see br.ufrn.sigaa.arq.acesso.AcessoMenuExecutor#processar(br.ufrn.sigaa.arq.acesso.DadosAcesso, br.ufrn.sigaa.dominio.Usuario, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		if (usuario.isUserInSubSistema(SigaaSubsistemas.REGISTRO_DIPLOMAS.getId())) {
			dados.setModuloDiplomas(true);
			
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.REGISTRO_DIPLOMAS, true));
			dados.incrementaTotalSistemas();
		}
	}

}
