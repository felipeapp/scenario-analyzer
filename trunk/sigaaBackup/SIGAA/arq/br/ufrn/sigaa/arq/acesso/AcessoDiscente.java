/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/09/2008
 *
 */
package br.ufrn.sigaa.arq.acesso;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalHelper;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ead.negocio.EADHelper;
import br.ufrn.sigaa.ensino.dao.NotificacaoAcademicaDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.extensao.dao.QuestionarioProjetoExtensaoDao;

/**
 * Processamento de permissões para acesso a operações dos discentes 
 * 
 * @author David Pereira
 * 
 */
public class AcessoDiscente extends AcessoMenuExecutor {

	@Override
	public void processar(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {

		if (usuario.getVinculoAtivo().isVinculoDiscente()) {
			processarDiscenteUsuario(dados, usuario, req);
			
			DiscenteAdapter discente = usuario.getDiscenteAtivo();
			CalendarioAvaliacao calAvaliacao = AvaliacaoInstitucionalHelper.getCalendarioAvaliacaoAtivo(discente);
			boolean podeAvaliar;
			if (calAvaliacao == null)
				podeAvaliar = false;
			else
				podeAvaliar =  AvaliacaoInstitucionalHelper.aptoPreencherAvaliacaoVigente(discente, calAvaliacao.getFormulario());
			dados.setDiscenteAptoAvaliacaoInstitucional( podeAvaliar );
		}
	}

	/**
	 * Configura o usuário para utilizar o discente escolhido.
	 */
	public void processarDiscenteUsuario(DadosAcesso dados, Usuario usuario, HttpServletRequest req) throws ArqException {

		QuestionarioProjetoExtensaoDao questDao = getDAO(QuestionarioProjetoExtensaoDao.class, req);
		NotificacaoAcademicaDao notificacaoDao = getDAO(NotificacaoAcademicaDao.class, req);
		
		try {
			usuario.setNivelEnsino(usuario.getDiscenteAtivo().getNivel());
			req.getSession().setAttribute("nivel", usuario.getDiscenteAtivo().getNivel());
	
			dados.setDiscente(true);
			
			dados.getDadosSubsistemaPrincipal().add(new DadosSubsistemaPrincipal(SigaaSubsistemas.PORTAL_DISCENTE, true));
			dados.incrementaTotalSistemas();
			dados.setAcessibilidade((Boolean) req.getSession().getAttribute("acessibilidade"));
	
			Boolean pendenteQuestionarioExtensao = questDao.haQuestionarioNaoRespondido(dados.getUsuario().getPessoa());
			dados.setPendenteQuestionarioExtensao(pendenteQuestionarioExtensao);

			/** SETANDO TUTOR ORIENTADOR */
			if (usuario.getDiscenteAtivo().getCurso() != null && usuario.getDiscenteAtivo().getCurso().isADistancia()) {
				if ( usuario.getDiscenteAtivo() instanceof DiscenteGraduacao ) {
					DiscenteGraduacao grad = (DiscenteGraduacao) usuario.getDiscenteAtivo();
					ArrayList<TutorOrientador> t = EADHelper.findTutoresByAluno(grad);
					if (t != null) {
						grad.setTutores(t);
					}
				}
			}
	
			/** SETANDO ORIENTADOR ACADEMICO */
			if (usuario.getDiscenteAtivo().isGraduacao()) {
				DiscenteGraduacao grad = (DiscenteGraduacao) usuario.getDiscenteAtivo();
				OrientacaoAcademica oa = getDAO(OrientacaoAcademicaDao.class, req).findOrientadorAcademicoAtivoByDiscente(grad.getId());
				if (oa != null) {
					oa.getServidor().getNome();
					grad.setOrientacaoAcademica(oa);
				}
			}
			
			/** VERIFICANDO SE O DISCENTE É DE ENSINO MÉDIO*/
			if (usuario.getDiscenteAtivo().isMedio()) {
				dados.setDiscente(false);
				dados.setDiscenteMedio(true);
			}
		
			
			dados.setPendenteNotificacaoAcademica(notificacaoDao.isPendenteNotificacao(usuario.getDiscenteAtivo().getId(),null));
				
		} finally {
			notificacaoDao.close();
			questDao.close();
		}
	}

}
