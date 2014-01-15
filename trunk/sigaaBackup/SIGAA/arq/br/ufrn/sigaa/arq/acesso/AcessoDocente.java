/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 30/09/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.dao.ensino.AvisoFaltaDocenteHomologadaDao;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.extensao.dao.QuestionarioProjetoExtensaoDao;

/**
 * Processamento de permissões para acesso a operações de docentes
 * 
 * @author David Pereira
 *
 */
public class AcessoDocente extends AcessoMenuExecutor {

	/**
	 * Processamento de permissões para acesso dos docentes.
	 * 
	 * @param dados
	 * @param usuario
	 * @param req
	 *   
	 */
	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {

		// Buscar turmas de graduação para verificar se o docente está apto
		// a fazer avaliação institucional
		if (dados.isDocente()) {
			
			AvisoFaltaDocenteHomologadaDao avisoFaltaDao = getDAO(AvisoFaltaDocenteHomologadaDao.class, req);
			OrientacaoAcademicaDao orientacaoAcademicaDAO = getDAO(OrientacaoAcademicaDao.class, req);
			RelatorioAcaoExtensaoDao relatorioAcaoExtensaoDao = getDAO(RelatorioAcaoExtensaoDao.class, req);
			QuestionarioProjetoExtensaoDao questDao = getDAO(QuestionarioProjetoExtensaoDao.class, req);
			
			try {

				Boolean pendenteQuestionarioExtensao = questDao.haQuestionarioNaoRespondido(dados.getUsuario().getPessoa());
				dados.setPendenteQuestionarioExtensao(pendenteQuestionarioExtensao);
				
				Boolean pendenteQuantoAoEnvioRelatorios = relatorioAcaoExtensaoDao.isPendenteRelatorios(dados.getUsuario().getPessoa());
				dados.setPendenteRelatoriosExtensao(pendenteQuantoAoEnvioRelatorios);
				
				dados.setPendentePlanoResposicao(avisoFaltaDao.isPendentePlanoAula(usuario.getServidorAtivo().getId()));			
				List<Character> tipos = orientacaoAcademicaDAO.findTiposOrientacao(usuario.getServidorAtivo().getId());
				
				if(!ValidatorUtil.isEmpty(tipos)){
					if (tipos.contains(OrientacaoAcademica.ACADEMICO)) {
						dados.orientadorAcademico = true;
						usuario.addPapelTemporario(new Papel(SigaaPapeis.ORIENTADOR_ACADEMICO));
					}
					
					if (tipos.contains(OrientacaoAcademica.ORIENTADOR)) {
						dados.orientadorStricto = true;
						usuario.addPapelTemporario(new Papel(SigaaPapeis.ORIENTADOR_STRICTO));
					}
					
					if (tipos.contains(OrientacaoAcademica.CoORIENTADOR)) {
						dados.coOrientadorStricto = true;
						usuario.addPapelTemporario(new Papel(SigaaPapeis.CO_ORIENTADOR_STRICTO));					
					}
				}
				
			} finally {
				avisoFaltaDao.close();
				orientacaoAcademicaDAO.close();
				relatorioAcaoExtensaoDao.close();
			}			
		}
	}
}
