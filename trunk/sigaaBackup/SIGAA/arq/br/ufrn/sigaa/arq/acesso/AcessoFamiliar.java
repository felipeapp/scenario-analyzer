/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 26/09/2011
 *
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Action que controla o acesso ao portal do familiar.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class AcessoFamiliar extends AcessoMenuExecutor {

	/**
	 * Processa o acesso
	 */
    @Override
    public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
    	if (usuario.getVinculoAtivo().getTipoVinculo().isFamiliar()) {
			dados.setPortalFamiliar(true);			
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_FAMILIAR, true));
			dados.incrementaTotalSistemas();    	
			
			req.getSession().setAttribute("nivel", usuario.getFamiliarAtivo().getDiscenteMedio().getNivel());
			
			usuario.addPapelTemporario(SigaaPapeis.FAMILIAR_MEDIO);
    	}
    	
    }


}
