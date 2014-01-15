/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 30/09/2008 
 */
package br.ufrn.sigaa.arq.acesso;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dao.QuestionarioProjetoExtensaoDao;
import br.ufrn.sigaa.rh.dominio.Designacao;


/**
 * Processamento de permissões para acesso a operações dos servidores 
 * 
 * @author David Pereira
 *
 */
public class AcessoServidor extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		
		QuestionarioProjetoExtensaoDao questDao = getDAO(QuestionarioProjetoExtensaoDao.class, req);
		RelatorioAcaoExtensaoDao relatorioAcaoExtensaoDao = getDAO(RelatorioAcaoExtensaoDao.class, req);
		try {
			Boolean pendenteQuestionarioExtensao = questDao.haQuestionarioNaoRespondido(dados.getUsuario().getPessoa());
			dados.setPendenteQuestionarioExtensao(pendenteQuestionarioExtensao);
			
			Boolean pendenteQuantoAoEnvioRelatorios = relatorioAcaoExtensaoDao.isPendenteRelatorios(dados.getUsuario().getPessoa());
			dados.setPendenteRelatoriosExtensao(pendenteQuantoAoEnvioRelatorios);
			
		} finally {
			questDao.close();
			relatorioAcaoExtensaoDao.close();
		}
		
		if (usuario.getVinculoAtivo().isVinculoServidor()) {
			
			dados.setSigrh(true);
			dados.setSigpp(true);
			
			// Docentes
			if (usuario.getVinculoAtivo().getServidor().isDocente()) {

				
				dados.setDocente(true);
				dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_DOCENTE, true));
				dados.incrementaTotalSistemas();
				
				dados.setDocenteUFRN(true);
				
				usuario.addPapelTemporario(new Papel(SigaaPapeis.DOCENTE));
				
				// verifica cargo do docente
				if (Cargo.DOCENTE_MEDIO_TECNICO.contains(usuario.getServidor().getCargo().getId())) {
					usuario.setNivelEnsino(NivelEnsino.TECNICO);
					dados.nivelDocente = String.valueOf(NivelEnsino.TECNICO);
				}
				if (Cargo.DOCENTE_SUPERIOR.contains(usuario.getServidor().getCargo().getId())) {
					// graduacao = true;
					usuario.setNivelEnsino(NivelEnsino.GRADUACAO);
					dados.nivelDocente = String.valueOf(NivelEnsino.GRADUACAO);
				}

				if (dados.nivelDocente == null) {
					dados.nivelDocente = String.valueOf(NivelEnsino.GRADUACAO);
					usuario.setNivelEnsino(NivelEnsino.GRADUACAO);
				}				
			}
		}
		
		if (usuario.getVinculoAtivo().getTipoVinculo().isResponsavel()) {
			// Supervisor acadêmico da escola de música, funcionando
			// como chefe de departamento
			processaResponsavelAcademico(dados, usuario, req);
			
			// Seta as permissões de chefia
			processaDesignacao(dados, usuario, req);

		}
	}

	/**
	 * Processa as designações do usuário
	 * 
	 * @param dados
	 * @param usuario
	 * @param req
	 * @throws ArqException
	 * @throws DAOException
	 */
	private void processaDesignacao(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException, DAOException {
		ServidorDao dao = getDAO(ServidorDao.class, req);
		
		try {
			// Verifica as designações do servidor
			Collection<Designacao> designacoes = dao.findDesignacoesAtivas(usuario.getServidor());
			
			for (Designacao d : designacoes) {
				
				// chefe de departamento
				if (AtividadeServidor.CHEFE_DEPARTAMENTO.contains(d.getAtividade().getCodigoRH())) {
					dados.setChefeDepartamento(true);
					usuario.addPapelTemporario(new Papel(SigaaPapeis.CHEFE_DEPARTAMENTO));
				}
				
				// esse caso é específico para diretor da escola de música
				if (AtividadeServidor.DIRETOR_UNIDADE.contains(d.getAtividade().getCodigoRH())
						&& usuario.getUnidade().getGestoraAcademica() != null
						&& usuario.getUnidade().getTipoAcademica() != null
						&& (usuario.getUnidade().getTipoAcademica().equals(TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA) 
								|| usuario.getUnidade().getTipoAcademica().equals(TipoUnidadeAcademica.ESCOLA))) {
					
					dados.setChefeDepartamento(true);
					dados.setUnidadeEspecializada(true);
					usuario.addPapelTemporario(new Papel(SigaaPapeis.CHEFE_DEPARTAMENTO));
					usuario.addPapelTemporario(new Papel(SigaaPapeis.DIRETOR_CENTRO));
				}
				
				if ( AtividadeServidor.DIRETOR_MUSEU.contains( d.getAtividade().getCodigoRH() ) ) {
					dados.setChefeDepartamento(true);
					dados.setUnidadeEspecializada(true);
					
					usuario.addPapelTemporario(new Papel(SigaaPapeis.CHEFE_DEPARTAMENTO));
				}
				
				if ( AtividadeServidor.DIRETOR_CENTRO.contains(d.getAtividade().getCodigoRH())
						|| AtividadeServidor.VICE_DIRETOR_CENTRO.contains(d.getAtividade().getCodigoRH())) {
					dados.diretorCentro = true;
					usuario.addPapelTemporario(new Papel(SigaaPapeis.DIRETOR_CENTRO));
					dados.planejamento = true;
					usuario.addPapelTemporario(new Papel(SigaaPapeis.PORTAL_PLANEJAMENTO));
				}
				
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Se o usuário é responsável acadêmico e a unidade dele for acadêmica, dar
	 * as prerrogativas de chefe de departamento.
	 * 
	 * @param dados
	 * @param usuario
	 * @throws ArqException 
	 */
	private void processaResponsavelAcademico(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {
		if (usuario.getVinculoAtivo().getUnidade() != null && 
				usuario.getVinculoAtivo().getUnidade().isUnidadeAcademica()) {
			if (usuario.getVinculoAtivo().getResponsavel().isSupervisorDiretorAcademico() || 
					usuario.getVinculoAtivo().getResponsavel().isViceChefeUnidade() || 
					usuario.getVinculoAtivo().getResponsavel().isChefeUnidade()) {
				dados.setChefeDepartamento(true);
				
				boolean isEscolaOuUnidadeEspecializada = usuario.getVinculoAtivo().getUnidade().isUnidadeAcademicaEspecializada() || usuario.getVinculoAtivo().getUnidade().getTipoAcademica().equals(TipoUnidadeAcademica.ESCOLA);
				dados.setUnidadeEspecializada(isEscolaOuUnidadeEspecializada );
				
				usuario.addPapelTemporario(new Papel(SigaaPapeis.CHEFE_DEPARTAMENTO));
				usuario.addPapelTemporario(new Papel(SigaaPapeis.DIRETOR_CENTRO));
			}
		}
	}
}
