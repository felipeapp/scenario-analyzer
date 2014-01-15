package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe respons�vel pelo valida��o para acesso ao menu de Conv�nios de Est�gio
 * @author arlindo
 *
 */
public class AcessoConveniosEstagio extends AcessoMenuExecutor {
	
	/** 
	 * Processa as permiss�es
	 * @see br.ufrn.sigaa.arq.acesso.AcessoMenuExecutor#processar(br.ufrn.sigaa.arq.acesso.DadosAcesso, br.ufrn.sigaa.dominio.Usuario, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		if (usuario.isUserInSubSistema(SigaaSubsistemas.CONVENIOS_ESTAGIO.getId())) {
			dados.setModuloConvenioEstagio(true);			
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.CONVENIOS_ESTAGIO, true));
			dados.incrementaTotalSistemas();
		}
	}	

}
