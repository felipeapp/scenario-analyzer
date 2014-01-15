/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 01/10/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;

/**
 * Processamento de permissões para acesso a operações de docentes externos 
 * 
 * @author David Pereira
 *
 */
public class AcessoDocenteExterno extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		/** DOCENTES EXTERNOS */
		if (usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			DocenteExterno externo = usuario.getVinculoAtivo().getDocenteExterno();

			// Verifica se o usuário é Docente Externo
			dados.setDocente(true);
			usuario.addPapelTemporario(new Papel(SigaaPapeis.DOCENTE));
			usuario.setDocenteExterno(externo);
			usuario.setNivelEnsino(externo.getNivel() != null ? externo.getNivel() : NivelEnsino.GRADUACAO);
			dados.setNivelDocente(externo.getNivel() != null ? String.valueOf(externo.getNivel()) : String.valueOf(NivelEnsino.GRADUACAO));

			OrientacaoAcademicaDao oaDao = getDAO(OrientacaoAcademicaDao.class, req);
			dados.setOrientacoesStricto(oaDao.findTotalOrientandosAtivos(externo));
			if (dados.getOrientacoesStricto() > 0) {
				dados.setOrientadorStricto(true);
				usuario.addPapelTemporario(new Papel(SigaaPapeis.ORIENTADOR_STRICTO));
			}
			
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_DOCENTE, true));
			dados.incrementaTotalSistemas();
		}

	}

}
