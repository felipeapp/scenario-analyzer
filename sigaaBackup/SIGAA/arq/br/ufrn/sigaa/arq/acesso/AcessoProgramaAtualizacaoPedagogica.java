package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.dominio.Usuario;


/**
 * Processamento de permissões para acesso ao Módulo do Programa de Atualização Pedagógica
 * 
 * @author 	Mário Rizzi
 *
 */
public class AcessoProgramaAtualizacaoPedagogica  extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario,
			HttpServletRequest req) throws ArqException {
		
		if (usuario.isUserInSubSistema(SigaaSubsistemas.PROGRAMA_ATUALIZACAO_PEDAGOGICA.getId())) {
			dados.setProgramaAtualizacaoPedagogica(true);
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PROGRAMA_ATUALIZACAO_PEDAGOGICA, true));
			dados.incrementaTotalSistemas();
		}
		
	}

}
