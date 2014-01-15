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
import br.ufrn.comum.dominio.Papel;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.AvaliadorAtividadeExtensaoDao;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permiss�es para acesso a extens�o 
 * 
 * @author David Pereira
 *
 */
public class AcessoExtensao extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {

		AvaliadorAtividadeExtensaoDao avaliadorDao = getDAO(AvaliadorAtividadeExtensaoDao.class, req);
		AtividadeExtensaoDao atividadeExtensaoDao = getDAO(AtividadeExtensaoDao.class, req);
		
		if (usuario.getServidor() != null && avaliadorDao.isAvaliadorCadastrado(usuario.getServidor().getId())) {
			dados.setPareceristaExtensao(true);
			usuario.addPapelTemporario(new Papel(SigaaPapeis.PARECERISTA_EXTENSAO));
		}

		if (usuario.isUserInRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO)) {
			dados.setPresidenteComiteExtensao(true);
		}
	
		if (usuario.isUserInSubSistema(SigaaSubsistemas.EXTENSAO.getId())) {
			dados.setExtensao(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.EXTENSAO, true));
			dados.incrementaTotalSistemas();
		}
	
		// Permiss�es extens�o

		// permite que t�cnicos administrativos tenham acesso ao menu de extens�o
		if (usuario.getServidor() != null && usuario.getServidor().getCategoria().getId() == Categoria.TECNICO_ADMINISTRATIVO) {
			usuario.addPapelTemporario(SigaaPapeis.EXTENSAO_TECNICO_ADMINISTRATIVO);
			dados.setExtensao(true);
		}

		//Procurar pela pessoa, pois se o usu�rio tiver um v�nculo de Servidor que n�o pode ser selecionado 
		//na tela inicial de login(inativo), n�o poder� visualizar as a��es de extens�o que ele � coordenador.
		if (usuario.getServidor() != null) {
			dados.setCoordenadorExtensao(atividadeExtensaoDao.isCoordenadorAtividade(usuario.getServidor().getPessoa()));
		}
			
	}

}
