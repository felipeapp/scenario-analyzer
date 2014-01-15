/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 09/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permiss�es para acesso ao m�dulo de Tradu��o de Documentos. 
 * 
 * @author Rafael Gomes
 *
 */
public class AcessoRelacoesInternacionais extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) {
		if (usuario.isUserInSubSistema(SigaaSubsistemas.RELACOES_INTERNACIONAIS.getId())) {
			dados.setRelacoesInternacionais(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.RELACOES_INTERNACIONAIS, true));
			dados.incrementaTotalSistemas();
		}
		
	}

}
