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

/**
 * Processamento de permissões para acesso ao módulo
 * de infra-estrutura física
 * 
 * @author Gleydson Lima
 * @author Ricardo Wendell
 *
 */
public class AcessoInfraEstruturaFisica extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		

		/* ***************************************************************************************  
		 *  Qualquer Usuário vai ter acesso ao módulo de infra estrutura para poder visualizar
		 *  as reservas dos espaços físicos. O controle de acesso vai ser feito em cada link do módulo.
		 * ***************************************************************************************/
		dados.setEspacoFisico(true);
		dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.INFRA_ESTRUTURA_FISICA, false));
		dados.incrementaTotalSistemas();
		
		//GestorEspacoFisicoDao gestorDao = null;

		//try {
			
			//gestorDao = getDAO(GestorEspacoFisicoDao.class, req);
		
		
			/* ***************************************************************************************  
			 *  Se possui qualquer papel do módulo de infra-estrutura física pode acessá-lo.
			 * ***************************************************************************************/
		
		//	if ( usuario.isUserInSubSistema(SigaaSubsistemas.INFRA_ESTRUTURA_FISICA.getId() ) ) {
				
		//	}
			
			
//			/*
//			 *  Se o usuário é chefe da unidade ou diretor do centro ou está cadastrado para gerir algum espaço físico completo
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
//			 *  Se o usuário está cadastrado para gerir apenas as reservas de um espaço físico.
//			 *  
//			 *  Recebe o papel temporariamente de "resposável por reserva"
//			 */
//			if ( gestorDao.isGestorEspacoFisico(usuario, TipoGestorEspacoFisico.GESTAO_RESERVAS) ) {
//				usuario.addPapelTemporario(new Papel(SigaaPapeis.RESPONSAVEL_RESERVA_ESPACO_FISICO, SigaaSubsistemas.INFRA_ESTRUTURA_FISICA));
//				acessoEspacoFisico = true;
//			}
			
			
			/*
			 * Se o usuário se encaixou em algunas das regras anteriores, libera o módulo de espaço físico para ele 
			 */
//			if (acessoEspacoFisico) {
//				
//			}
			
//		} finally {
//			//if(gestorDao!= null) gestorDao.close();
//		}
	}
	
}