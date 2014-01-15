/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Verifica se o usu�rio possui permiss�o para abrir chamados nos sistemas.
 * 
 * @author David Pereira
 *
 */
public class AcessoAbrirChamado extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		if (usuario.getVinculoAtivo().isVinculoDiscente() && dados.totalSistemas <= 2 && !usuario.getVinculoAtivo().isVinculoServidor())
			dados.setAbrirChamado(false);
		else
			dados.setAbrirChamado(true);
	}

}
