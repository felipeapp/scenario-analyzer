/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 17/02/2009 
 */
package br.ufrn.sigaa.arq.acesso;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/**
 * Processamento de permissões para acesso dos membros de comissão
 * 
 * @author David Pereira
 * 
 */
public class AcessoMembroComissao extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		MembroComissaoDao dao = getDAO(MembroComissaoDao.class, req);

		if (usuario.getServidorAtivo() != null) {

			List<Integer> tipos = dao.findTiposComissaoPorServidor(usuario.getServidorAtivo());

			if (tipos.contains(MembroComissao.MEMBRO_COMISSAO_MONITORIA)) {
				dados.setComissaoMonitoria(true);
				usuario.addPapelTemporario(SigaaPapeis.MEMBRO_COMITE_MONITORIA);
			}

			if (tipos.contains(MembroComissao.MEMBRO_COMISSAO_CIENTIFICA)) {
				dados.setComissaoCientificaMonitoria(true);
				usuario.addPapelTemporario(SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA);
			}

			/** PESQUISA **/
			if (tipos.contains(MembroComissao.MEMBRO_COMISSAO_PESQUISA)) {
				dados.setComissaoPesquisa(true);
				usuario.addPapelTemporario(new Papel(SigaaPapeis.MEMBRO_COMITE_PESQUISA));
			}

			/** EXTENSAO **/
			if (tipos.contains(MembroComissao.MEMBRO_COMISSAO_EXTENSAO)) {
				dados.setComissaoExtensao(true);
				usuario.addPapelTemporario(new Papel(SigaaPapeis.MEMBRO_COMITE_EXTENSAO));
			}

			/** AÇÕES ACADÊMICAS ASSOCIADAS **/
			if (tipos.contains(MembroComissao.MEMBRO_COMISSAO_INTEGRADA)) {
				dados.setComissaoIntegrada(true);
				dados.setAcoesAssociadas(true);
				usuario.addPapelTemporario(new Papel(SigaaPapeis.MEMBRO_COMITE_INTEGRADO));
			}

		}
	}

}
