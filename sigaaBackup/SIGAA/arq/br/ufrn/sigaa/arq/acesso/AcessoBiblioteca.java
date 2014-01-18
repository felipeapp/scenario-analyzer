/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 01/10/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;

/**
 * Processamento de permissões para acesso a biblioteca 
 * 
 * @author David Pereira
 *
 */
public class AcessoBiblioteca extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {

		/* ****************************************************************************************************
		 *  Tendo algum papel na biblioteca, ou sendo servidor pode ter o subsistema da biblioteca            *
		 *  habilitado, pois o módulo da biblioteca para os servidores é uma aba do subsistema da biblioteca. *
		 * ****************************************************************************************************/
		
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.BIBLIOTECA.getId()) ) {
			
			ativaEntradaModuloBiblioteca(dados, usuario);
			
		}else{
			
			// Se for servidor, estando ativo ou não pode entrar no módulo da biblioteca 
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
	 * Ativa a entrada no módulo
	 * @param dados
	 * @param usuario
	 *  
	 * <p> Criado em:  11/11/2013  </p>
	 * 
	 * <strong>Método não chamado de nenhuma página JSP</strong>.
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
