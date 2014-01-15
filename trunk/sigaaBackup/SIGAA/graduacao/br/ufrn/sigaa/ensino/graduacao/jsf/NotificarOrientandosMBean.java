/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.jsf.AbstractControllerNotificacoes;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.DestinatarioMensagemOrientacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.NotificacaoOrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;

/**
 * MBean responsável pelas notificações feitas a orientandos.
 * 
 * @author henrique
 * @author bernardo
 *
 */
@Component("notificarOrientandos") @Scope("session")
public class NotificarOrientandosMBean extends AbstractControllerNotificacoes {
	
	/** Notificação trabalhada. */
	private NotificacaoOrientacaoAcademica notificacaoOrientacaoAcademica;

	/** Se o orientador deve receber uma cópia da mensagem. */
	private boolean receberCopia;
	
	@Override
	public void clear() {
		super.clear();
		obj.setTitulo("Mensagem do seu Orientador Academico");
		obj.setEnviarEmail(true);
		obj.setEnviarMensagem(true);
		notificacaoOrientacaoAcademica = new NotificacaoOrientacaoAcademica();
	}
	
	@Override
	public void popularDestinatarios() throws ArqException {
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		Collection<OrientacaoAcademica> orientandos = dao.findAtivoByServidorCursoTipo(null, null, getServidorUsuario().getId(), OrientacaoAcademica.ACADEMICO);			
		StringBuilder naoNotificados = new StringBuilder();
		for (OrientacaoAcademica orientacao : orientandos) {
			if (isEmpty(orientacao.getDiscente().getUsuario())) {
				if (naoNotificados.length() > 0) naoNotificados.append(", ");
				naoNotificados.append(orientacao.getDiscente().getMatriculaNome());
				continue;
			}
			String nome = orientacao.getDiscente().getPessoa().getNome();
			String email = orientacao.getDiscente().getPessoa().getEmail();
			Usuario usuario = orientacao.getDiscente().getUsuario();
			
			DestinatarioMensagemOrientacao destinatario = new DestinatarioMensagemOrientacao(nome, email);
			destinatario.setIdusuario(usuario.getId());
			destinatario.setUsuario(usuario);
			destinatario.setOrientacaoAcademica(orientacao);
			if(StatusDiscente.getStatusComVinculo().contains(orientacao.getDiscente().getStatus())) {
				notificacaoOrientacaoAcademica.addDestinatarioMensagemOrientacao(destinatario);
				obj.adicionarDestinatario(destinatario);
			}
		}	
		
		if (receberCopia) {
			// Envia cópia do e-mail pra si próprio.
			String nome = getUsuarioLogado().getNome();
			String email = getUsuarioLogado().getEmail();
			Usuario usuario = getUsuarioLogado();
			
			Destinatario destinatario = new DestinatarioMensagemOrientacao(nome, email);
			destinatario.setIdusuario(usuario.getId());
			destinatario.setUsuario(usuario);
			obj.adicionarDestinatario(destinatario);
		}	
		if (naoNotificados.length() > 0)
			addMensagemWarning("Os seguintes discentes não possuem usuário ativo e não serão notificados: " + naoNotificados);
	}
	
	@Override
	public String iniciar() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_MENSAGEM_TODOS_ORIENTANDOS);
		return super.iniciar();
	}

	@Override
	public String enviar() throws ArqException, NegocioException {
		notificacaoOrientacaoAcademica.setTitulo(obj.getTitulo());
		notificacaoOrientacaoAcademica.setMensagem(obj.getMensagem());
		obj.getDestinatariosEmail();
		super.enviar();
		
		if(hasErrors())
			return null;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_MENSAGEM_TODOS_ORIENTANDOS);
		mov.setObjMovimentado(notificacaoOrientacaoAcademica);
		
		try {
			execute(mov);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
		
		return cancelar();
	}

	@Override
	public String getCaminhoFormulario() {
		return "/graduacao/orientacao_academica/enviar_mensagem.jsp";
	}
	
	@Override
	public int[] getPapeis() {
		return new int[] {SigaaPapeis.ORIENTADOR_ACADEMICO};
	}

	public void setReceberCopia(boolean receberCopia) {
		this.receberCopia = receberCopia;
	}

	public boolean isReceberCopia() {
		return receberCopia;
	}
}
