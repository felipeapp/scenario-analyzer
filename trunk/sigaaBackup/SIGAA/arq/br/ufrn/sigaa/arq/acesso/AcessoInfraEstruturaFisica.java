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

/**
 * Processamento de permiss�es para acesso ao m�dulo
 * de infra-estrutura f�sica
 * 
 * @author Gleydson Lima
 * @author Ricardo Wendell
 *
 */
public class AcessoInfraEstruturaFisica extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		

		/* ***************************************************************************************  
		 *  Qualquer Usu�rio vai ter acesso ao m�dulo de infra estrutura para poder visualizar
		 *  as reservas dos espa�os f�sicos. O controle de acesso vai ser feito em cada link do m�dulo.
		 * ***************************************************************************************/
		dados.setEspacoFisico(true);
		dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.INFRA_ESTRUTURA_FISICA, false));
		dados.incrementaTotalSistemas();
		
		//GestorEspacoFisicoDao gestorDao = null;

		//try {
			
			//gestorDao = getDAO(GestorEspacoFisicoDao.class, req);
		
		
			/* ***************************************************************************************  
			 *  Se possui qualquer papel do m�dulo de infra-estrutura f�sica pode acess�-lo.
			 * ***************************************************************************************/
		
		//	if ( usuario.isUserInSubSistema(SigaaSubsistemas.INFRA_ESTRUTURA_FISICA.getId() ) ) {
				
		//	}
			
			
//			/*
//			 *  Se o usu�rio � chefe da unidade ou diretor do centro ou est� cadastrado para gerir algum espa�o f�sico completo
//			 *  
//			 *  Recebe o papel temporariamente de "gestor de infra estrutura"
//			 *  
//			 */
//			if ( dados.isChefeUnidade() || dados.isDiretorCentro() || gestorDao.isGestorEspacoFisico(usuario, TipoGestorEspacoFisico.GESTAO_COMPLETA)) {
//				usuario.addPapelTemporario(new Papel(SigaaPapeis.GESTOR_INFRA_ESTRUTURA_FISICA, SigaaSubsistemas.INFRA_ESTRUTURA_FISICA));
//				usuario.addPapelTemporario(new Papel(SigaaPapeis.RESPONSAVEL_RESERVA_ESPACO_FISICO, SigaaSubsistemas.INFRA_ESTRUTURA_FISICA));
//				acessoEspacoFisico = true;
//			}
//			
//			
//			/*
//			 *  Se o usu�rio est� cadastrado para gerir apenas as reservas de um espa�o f�sico.
//			 *  
//			 *  Recebe o papel temporariamente de "respos�vel por reserva"
//			 */
//			if ( gestorDao.isGestorEspacoFisico(usuario, TipoGestorEspacoFisico.GESTAO_RESERVAS) ) {
//				usuario.addPapelTemporario(new Papel(SigaaPapeis.RESPONSAVEL_RESERVA_ESPACO_FISICO, SigaaSubsistemas.INFRA_ESTRUTURA_FISICA));
//				acessoEspacoFisico = true;
//			}
			
			
			/*
			 * Se o usu�rio se encaixou em algunas das regras anteriores, libera o m�dulo de espa�o f�sico para ele 
			 */
//			if (acessoEspacoFisico) {
//				
//			}
			
//		} finally {
//			//if(gestorDao!= null) gestorDao.close();
//		}
	}
	
}