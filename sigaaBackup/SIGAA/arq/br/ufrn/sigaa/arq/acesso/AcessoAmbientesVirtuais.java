/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 30/09/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.ava.cv.dao.ComunidadeVirtualDao;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permiss�es para o gerenciamento de Ambientes Virtuais 
 * 
 * @author agostinho campos
 *
 */
public class AcessoAmbientesVirtuais extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		ComunidadeVirtualDao dao = null;
		
		try {
		
			dao = getDAO(ComunidadeVirtualDao.class, req);
			// Atualmente, s� permite o acesso para servidores ou usu�rios com acesso a comunidade virtuais.
			if ( usuario.getVinculoAtivo().getServidor() != null  ) {
				dados.setAmbientesVirtuais(true);
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.AMBIENTES_VIRTUAIS, false));
				
			} else if ( usuario.isUserInRole(SigaaPapeis.GESTOR_AMBIENTES_VIRTUAIS) ){
				dados.setAmbientesVirtuais(true);
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.AMBIENTES_VIRTUAIS, false));
				dados.incrementaTotalSistemas();
			} else if ( usuario.getVinculoAtivo().getDiscente() != null ) {
				// aluno n�o precisa fazer nada, ele tem acesso pelo portal discente.
				// o if aqui � para ele n�o fazer a consulta abaixo: dao.isMembroComunidade(usuario)
			} else if ( dao.isMembroComunidade(usuario) || dao.findByExactField(PermissaoAva.class, "pessoa.id", usuario.getPessoa().getId()).size() > 0 ) {
				dados.setAmbientesVirtuais(true);
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.AMBIENTES_VIRTUAIS, false));
				dados.incrementaTotalSistemas();
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
	}

}
