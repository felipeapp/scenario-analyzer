/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 01/10/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.MembroApoioDocenciaAssistida;

/**
 * Processamento de permissões para acesso a Stricto Sensu 
 * 
 * @author David Pereira
 *
 */
public class AcessoStricto extends AcessoMenuExecutor {

	/** Processa as permissões para o acesso ao subsistema stricto sensu.
	 * @see br.ufrn.sigaa.arq.acesso.AcessoMenuExecutor#processar(br.ufrn.sigaa.arq.acesso.DadosAcesso, br.ufrn.sigaa.dominio.Usuario, javax.servlet.http.HttpServletRequest)
	 */
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {

		if (usuario.isUserInSubSistema(SigaaSubsistemas.STRICTO_SENSU.getId())) {
			dados.setStricto(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.STRICTO_SENSU, true));
			
			dados.setPpg(usuario.isUserInRole(SigaaPapeis.PPG));
			
			// Verifica se o usuário é administrador da PPG (Pró-reitoria de Pós-graduação)
			dados.setAdministradorStricto(isUserInRole(SigaaPapeis.ADMINISTRADOR_STRICTO, req));

			dados.incrementaTotalSistemas();
		}
		
		if (!ValidatorUtil.isEmpty( getGenericDAO(req).findByExactField(MembroApoioDocenciaAssistida.class, 
				"usuario.id", usuario.getId()))){
		
			dados.setStricto(true);
			
			dados.setMembroApoioDocenciaAssistida(true);
			
			usuario.addPapelTemporario(new Papel(SigaaPapeis.MEMBRO_APOIO_DOCENCIA_ASSISTIDA));
			
		}
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.CONSULTA.getId())) {
			dados.setConsulta(true);
			dados.setStricto(true);			
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.CONSULTA, true));
			dados.incrementaTotalSistemas();
		}
		

	}

}
