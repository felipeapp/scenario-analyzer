package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.dao.projetos.AvaliadorProjetoDao;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processamento de permissões para acesso ao Módulo de Ações Acadêmicas Associadas. 
 * 
 * @author Ilueny Santos
 *
 */
public class AcessoAcoesAssociadas extends AcessoMenuExecutor {

	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		if (usuario.isUserInSubSistema(SigaaSubsistemas.ACOES_ASSOCIADAS.getId())) {
			dados.setAcoesAssociadas(true);
			
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.ACOES_ASSOCIADAS, true));
			dados.incrementaTotalSistemas();
		}
		
		AvaliadorProjetoDao avaliacaoDao = getDAO(AvaliadorProjetoDao.class, req);
		if (usuario != null && avaliacaoDao.isAvaliadorCadastrado(usuario.getId())) {
			usuario.addPapelTemporario(new Papel(SigaaPapeis.AVALIADOR_ACOES_ASSOCIADAS));
			dados.setAvaliadorAcoesAssociadas(true);
			dados.setAcoesAssociadas(true);
		}

	}

}
