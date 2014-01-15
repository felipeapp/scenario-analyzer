/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 25/10/2010
 *
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permiss�es para acesso ao Portal de Concedente de Est�gio 
 * 
 * @author Arlindo Rodrigues
 *
 */
public class AcessoPortalConcedenteEstagio extends AcessoMenuExecutor  {

	/** 
	 * Processa as permiss�es
	 * @see br.ufrn.sigaa.arq.acesso.AcessoMenuExecutor#processar(br.ufrn.sigaa.arq.acesso.DadosAcesso, br.ufrn.sigaa.dominio.Usuario, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		if (usuario.getVinculoAtivo().isVinculoConcedenteEstagio() ||
				// O papel de coordenador de est�gio est� como sendo do subsistema Portal Concedente de Est�gio, neste caso, n�o deve habilitar o portal
				(usuario.isUserInSubSistema(SigaaSubsistemas.PORTAL_CONCEDENTE_ESTAGIO.getId()) && !usuario.isUserInRole(SigaaPapeis.COORDENADOR_ESTAGIOS)) ) {
			dados.setPortalConcedenteEstagio(true);			
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_CONCEDENTE_ESTAGIO, true));
			dados.incrementaTotalSistemas();
		}
	}		
}
