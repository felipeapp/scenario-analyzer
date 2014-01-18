/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 01/10/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;

/**
 * Processamento de permiss�es para acesso a biblioteca 
 * 
 * @author David Pereira
 *
 */
public class AcessoBiblioteca extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {

		/* ****************************************************************************************************
		 *  Tendo algum papel na biblioteca, ou sendo servidor pode ter o subsistema da biblioteca            *
		 *  habilitado, pois o m�dulo da biblioteca para os servidores � uma aba do subsistema da biblioteca. *
		 * ****************************************************************************************************/
		
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.BIBLIOTECA.getId()) ) {
			
			ativaEntradaModuloBiblioteca(dados, usuario);
			
		}else{
			
			// Se for servidor, estando ativo ou n�o pode entrar no m�dulo da biblioteca 
			if(usuario.getVinculos() != null )
			for (VinculoUsuario vinculo : usuario.getVinculos()) {
				if ( vinculo.isVinculoServidor() ) {
					ativaEntradaModuloBiblioteca(dados, usuario);
					break;
				}
			}
			
		}
		
	}

	
	/**
	 * Ativa a entrada no m�dulo
	 * @param dados
	 * @param usuario
	 *  
	 * <p> Criado em:  11/11/2013  </p>
	 * 
	 * <strong>M�todo n�o chamado de nenhuma p�gina JSP</strong>.
	 *
	 * 
	 */
	public void ativaEntradaModuloBiblioteca(DadosAcesso dados, Usuario usuario) {
		
		dados.setModuloBiblioteca(true);
		dados.incrementaTotalSistemas();
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.BIBLIOTECA.getId()))
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.BIBLIOTECA, true));
	}

}
