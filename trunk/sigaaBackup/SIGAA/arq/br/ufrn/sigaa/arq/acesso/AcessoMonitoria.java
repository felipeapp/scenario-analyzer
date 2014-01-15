/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 30/09/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para acesso a monitoria
 * 
 * @author David Pereira
 *
 */
public class AcessoMonitoria extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.MONITORIA.getId())) {
			dados.setMonitoria(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.MONITORIA, true));
			dados.incrementaTotalSistemas();
		}
		
		if (usuario.isUserInRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA)) {
			dados.setComissaoMonitoria(true);
		}
		
		if (usuario.isUserInRole(SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA)) {
			dados.setComissaoCientificaMonitoria(true);
		}

		if (usuario.isUserInRole(SigaaPapeis.MEMBRO_CPDI)) {
			dados.setCpdi(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_CPDI, true));
			dados.incrementaTotalSistemas();
		}

		if (usuario.getVinculoAtivo().isVinculoServidor()) {
			ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class, req);
			dados.coordenadorMonitoria = dao.isCoordenadorProjeto(usuario.getServidorAtivo());
			dados.autorProjetoMonitoria = dao.isAutorProjetoMonitoria(usuario);
		}
		
	}
}
