/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/08/2010
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.jsf.AbstractControllerNotificacoes;
import br.ufrn.sigaa.ensino.graduacao.dao.NotificaParticipantesCursoDao;

/**
 * MBean Responsável por enviar Mensagens de Notificação para os Participantes (Discentes e Docentes) 
 * do Curso do Coordenador de Graduação Logado.  
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("notificarParticipantesCurso") @Scope("session")
public class NotificaParticipantesCursoMBean extends AbstractControllerNotificacoes {
	
	/** 
	 * Indica quem será notificado. 
	 * 0 - TODOS
	 * 1 - ALUNOS
	 * 2 - DOCENTES
	 */
	private int tipoNotificacao;
	
	/** Constante que indica que serão notificados todos os participantes */
	private static final int NOTIFICA_TODOS = 1;
	/** Constante que indica que serão notificados todos os discentes participantes */
	private static final int NOTIFICA_DISCENTES = 2;
	/** Constante que indica que serão notificados todos os docentes participantes */
	private static final int NOTIFICA_DOCENTES = 3;
	
	/**
	 * Inicializa os Objetos.
	 * <br/> Método não invocado por JSP's 
	 */
	@Override
	public void clear() {
		super.clear();
		String titulo = "Mensagem enviada Pelo Coordenador do Curso de "+getCursoAtualCoordenacao().getDescricao();
		obj.setTitulo(titulo);		
		obj.setEnviarEmail(true);
		obj.setEnviarMensagem(true);		
		obj.setNomeRemetente(getUsuarioLogado().getNome());
		obj.setContentType(MailBody.HTML);
		tipoNotificacao = 0;
	}

	/**
	 * Caminho do Formulário de envio de Mensagem.
	 * <br/><br/>
	 * Método não chamado por JSP.
	 */
	@Override
	public String getCaminhoFormulario() {
		return "/graduacao/notifica_participantes_curso/enviar_mensagem.jsp";
	}

	/**
	 * Papel que pode acessar o Caso de Uso.
	 */	
	@Override
	public int[] getPapeis() {
		return new int[] {SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO};
	}	

	/**
	 * Popula os Destinatários que receberão a notificação.
	 * <br/><br/>
	 * Método não chamado por JSP. 
	 */
	@Override
	public void popularDestinatarios() throws ArqException {
		
		if (tipoNotificacao == 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Informe quem será notificado");
			return;
		}
		
		NotificaParticipantesCursoDao dao = getDAO(NotificaParticipantesCursoDao.class);
		try {
			if (tipoNotificacao != NOTIFICA_DOCENTES){
				List<UsuarioGeral> discentes = dao.findUsuarioDiscentesByCurso(getCursoAtualCoordenacao().getId());
				if (isEmpty(discentes))
					addMensagemErro("Não discentes ativos para o curso atual.");
				for (UsuarioGeral u : discentes){
					Destinatario d = new Destinatario(u.getNome(), u.getEmail());
					d.setIdusuario(u.getId());
					obj.adicionarDestinatario(d);
				}				
			}
			
			if (tipoNotificacao != NOTIFICA_DISCENTES){
				List<UsuarioGeral> usuariosDocentes = dao.findUsuariosDocentesByCursoPeriodo(getCursoAtualCoordenacao().getId(), 
						getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());	
				if (isEmpty(usuariosDocentes))
					addMensagemErro("Não há turmas com docentes para o ano período atual.");
				for (UsuarioGeral u : usuariosDocentes){
					Destinatario d = new Destinatario(u.getNome(), u.getEmail());
					d.setIdusuario(u.getId());
					obj.adicionarDestinatario(d);
				}
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}
	
	/**
	 * Retorna um Combo com os tipos de notificação.
	 * <br/><br/>
	 * Método não chamado pela seguinte JSP.
	 * <ul>
	 *   <li>/sigaa.war/graduacao/notifica_participantes_curso/enviar_mensagem.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getTiposNotificacaoCombo(){
		List<SelectItem> i = new ArrayList<SelectItem>();
		i.add(new SelectItem(NOTIFICA_TODOS, "TODOS"));
		i.add(new SelectItem(NOTIFICA_DISCENTES, "DISCENTES"));
		i.add(new SelectItem(NOTIFICA_DOCENTES, "DOCENTES"));
		return i;				
	}

	public int getTipoNotificacao() {
		return tipoNotificacao;
	}

	public void setTipoNotificacao(int tipoNotificacao) {
		this.tipoNotificacao = tipoNotificacao;
	}	
}
