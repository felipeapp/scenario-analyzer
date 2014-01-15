/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Processamento de permiss�es para acesso ao m�dulo
 * de Registro de Diplomas
 * 
 * @author �dipo Elder F. Melo
 *
 */public class AcessoRegistroDiploma extends AcessoMenuExecutor {

	/** Processa as permiss�es
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
