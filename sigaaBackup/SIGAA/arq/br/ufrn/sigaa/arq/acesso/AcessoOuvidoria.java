/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 17/05/2011
 *
 */
package br.ufrn.sigaa.arq.acesso;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ouvidoria.dao.DelegacaoUsuarioRespostaDao;

/**
 * Action que controla o acesso ao módulu.
 * 
 * @author Bernardo
 *
 */
public class AcessoOuvidoria extends AcessoMenuExecutor {

    @Override
    public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		if (usuario.isUserInSubSistema(SigaaSubsistemas.OUVIDORIA.getId())) {
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.OUVIDORIA, true));
			if(!possuiAcessoOuvidoria(dados, usuario, req)) {
				liberarAcessoOuvidoria(dados);
			}
		}
		if(possuiAcessoOuvidoria(dados, usuario, req)) {
			liberarAcessoOuvidoria(dados);
		}
    }

    /**
     * Adiciona o módulo de ouvidoria na lista de módulos que o usuário pode acessar.
     * 
     * @param dados
     */
	private void liberarAcessoOuvidoria(DadosAcesso dados) {
		dados.setOuvidoria(true);
		dados.incrementaTotalSistemas();
	}

    /**
     * Verifica se o usuário tem acesso ao módulo.
     * 
     * @param dados
     * @param usuario
     * @return
     * @throws ArqException 
     */
	private boolean possuiAcessoOuvidoria(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		boolean responsavelUnidade = false, designado = false;
		
		if(isNotEmpty(usuario.getServidor())) {
			UnidadeDAOImpl dao = getDAO(UnidadeDAOImpl.class, req);
			dao.setSistema( Sistema.SIGAA );
			
			try {
				Collection<Responsavel> responsabilidades = dao.findResponsabilidadeUnidadeByServidor(usuario.getServidor().getId(), NivelResponsabilidade.getNiveis());
				responsavelUnidade = isNotEmpty(responsabilidades);
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		Long delegacoes = null;
		DelegacaoUsuarioRespostaDao delegacaoDao = getDAO(DelegacaoUsuarioRespostaDao.class, req);
		try {
		 	delegacoes = delegacaoDao.countDelegacoesByPessoa(usuario.getPessoa().getId());
		} finally {
			if (delegacaoDao != null)
				delegacaoDao.close();
		}
		
		designado = delegacoes != null && delegacoes > 0;
		
		return dados.isChefeUnidade() || responsavelUnidade || designado;
	}

}
