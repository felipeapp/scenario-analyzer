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
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para acesso a pesquisa 
 * 
 * @author David Pereira
 *
 */
public class AcessoPesquisa extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.PESQUISA.getId())) {
			dados.setPesquisa(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PESQUISA, true));
			dados.incrementaTotalSistemas();
		}
		
		if (usuario.isUserInRole(SigaaPapeis.NIT)) {
			dados.setNit(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PESQUISA, true));
			dados.incrementaTotalSistemas();
		}
		
		if (usuario.getServidor() != null && getDAO(ProjetoPesquisaDao.class, req).isCoordenadorPesquisa(usuario.getServidor())) {
			dados.setCoordPesquisa(true);
		}

		if (usuario.getServidor() != null) {
			usuario.addPapelTemporario(SigaaPapeis.PESQUISA_TECNICO_ADMINISTRATIVO);
			dados.setPesquisa(true);
		}
	}

}
