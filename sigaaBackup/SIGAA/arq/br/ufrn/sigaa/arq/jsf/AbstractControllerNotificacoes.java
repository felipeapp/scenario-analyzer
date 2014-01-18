/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Criado em: 17/05/2008
 * 
 */
package br.ufrn.sigaa.arq.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.DtoUtils;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.comum.dominio.notificacoes.GrupoDestinatarios;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.integracao.dto.DestinatarioDTO;
import br.ufrn.integracao.dto.GrupoDestinatariosDTO;
import br.ufrn.integracao.dto.NotificacaoDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.EnvioNotificacoesRemoteService;
import br.ufrn.integracao.siged.dto.ArquivoDocumento;

/**
 * Controlador com as funcionalidades básicas para o envio de notificações via email ou mensagens pelo sistema.
 * Os controladores específicos de cadas módulo deverão estendê-lo para controlar os
 * formulários que definirão os destinatários.
 *
 * @author Ricardo Wendell
 *
 */
public abstract class AbstractControllerNotificacoes extends SigaaAbstractController<Notificacao> {

	/**
	 * Método que inicia o envio de uma notificação.
	 * É necessário que o caminho do formulário a ser utilizado tenha sido informado
	 * pelo controlador filho.
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		clear();

		prepararFormulario();
		
		if ( obj.isAutorizado() ) {
			return forward(getCaminhoFormulario());
		} else {
			return null;
		}
	}

	/**
	 * Realiza as verificações de permissões e prepara o movimento para as notificações
	 * @throws ArqException
	 */
	public void prepararFormulario() throws ArqException {
		// Verificar permissões
		if (getPapeis() != null){
			if ( !getUsuarioLogado().isUserInRole(getPapeis()) ) {
				addMensagemErro("Você não possui permissão para enviar notificações!");
			} else {
				obj.setAutorizado(true);
			}			
		} else
			obj.setAutorizado(true);
	}

	/**
	 * Enviar notificações para os destinatários selecionados no formulário. 
	 *
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 * @throws NegocioRemotoException 
	 */
	public String enviar() throws ArqException, NegocioException {
		
		// Salva a mensagem original para caso de ter que exibi-la em razão de algum retorno de validação.
		String mensagemSemFormatacao = new String( obj.getMensagem() );
		
		// Popular os destinatários da notificação
		popularDestinatarios();

		// Validar dados da notificação
		ListaMensagens erros = obj.validate();
		if (!erros.isEmpty()) {
			addMensagens(erros);
			obj.setMensagem( mensagemSemFormatacao );
			return null;
		}

		NotificacaoDTO notificacao = criarDto(obj);
		
		// Preparar cadastro
		try {
			EnvioNotificacoesRemoteService notificador = getMBean("envioNotificacoesInvoker");
			notificador.enviar(DtoUtils.deUsuarioParaDTO(getUsuarioLogado()), notificacao);
		} catch (NegocioRemotoException e) {
			obj.setMensagem( mensagemSemFormatacao );
			throw new NegocioException(e.getMessage());
		}
		
		definirMensagemConfirmacao();

		return cancelar();
	}

	/** Cria um objeto {@link NotificacaoDTO} a partir de um objeto {@link Notificacao}.
	 * @param obj
	 * @return
	 */
	private NotificacaoDTO criarDto(Notificacao obj) {
		NotificacaoDTO not = new NotificacaoDTO();
		not.setAutorizado(obj.isAutorizado());
		not.setContentType(obj.getContentType());
		if(obj.isEnviarEmail()) 
			not.setDestinatarios(criarDestinatarios(obj.getDestinatariosEmail()));
		if(obj.isEnviarMensagem())
			not.setDestinatariosMsg(criarDestinatariosMsg(obj.getDestinatariosMensagem()));
		not.setEnviarEmail(obj.isEnviarEmail());
		not.setEnviarMensagem(obj.isEnviarMensagem());
		not.setGruposDestinatarios(criarGrupos(obj.getGruposDestinatarios()));
		not.setMensagem(obj.getMensagem());
		not.setNomeRemetente(obj.getNomeRemetente());
		not.setTitulo(obj.getTitulo());
		not.setReplyTo(obj.getEmailRespostas());
		
		if (obj.getAnexo() != null) {
			try {
				not.setAnexo(new ArquivoDocumento(obj.getAnexo()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return not;
	}

	/** Cria uma lista de {@link GrupoDestinatariosDTO} a partir de uma lista de {@link GrupoDestinatarios}.
	 * @param grupos
	 * @return
	 */
	private List<GrupoDestinatariosDTO> criarGrupos(Collection<GrupoDestinatarios> grupos) {
		List<GrupoDestinatariosDTO> dtos = new ArrayList<GrupoDestinatariosDTO>();
		if (grupos != null)
			for (GrupoDestinatarios grupo : grupos) {
				GrupoDestinatariosDTO dto = new GrupoDestinatariosDTO();
				dto.setId(grupo.getId());
				dto.setDescricao(grupo.getDescricao());
				dto.setSqlDestinatarios(grupo.getSqlDestinatarios());
				dto.setAtivo(grupo.isAtivo());
				dto.setMemorandoCircular(grupo.isMemorandoCircular());
				dto.setTelaAvisoLogon(grupo.isTelaAvisoLogon());
				dto.setParticipaComunidadeVirtual(grupo.isParticipaComunidadeVirtual());
				grupos.add(grupo);
			}
		return dtos;
	}

	/** Cria uma lista de {@link DestinatarioDTO} a partir de uma lista de {@link Destinatario}.
	 * @param destinatarios
	 * @return
	 */
	private List<DestinatarioDTO> criarDestinatarios(Collection<Destinatario> destinatarios) {
		List<DestinatarioDTO> dtos = new ArrayList<DestinatarioDTO>();
		for (Destinatario destinatario : destinatarios) {
			DestinatarioDTO dto = new DestinatarioDTO();
			dto.setEmail(destinatario.getEmail());
			dto.setNome(destinatario.getNome());
			dtos.add(dto);
		}
		return dtos;
	}

	/** Cria uma lista de {@link DestinatarioDTO} a partir de uma lista de {@link Destinatario}.
	 * @param destinatarios
	 * @return
	 */
	private List<DestinatarioDTO> criarDestinatariosMsg(Collection<Destinatario> destinatarios) {
		List<DestinatarioDTO> dtos = new ArrayList<DestinatarioDTO>();
		for (Destinatario destinatario : destinatarios) {
			DestinatarioDTO dto = new DestinatarioDTO();
			dto.setUsuario(DtoUtils.deUsuarioParaDTO(destinatario.getUsuario()));
			dtos.add(dto);
		}
		return dtos;
	}	
	
	/**
	 * Mensagem de confirmação que será apresentada ao usuário
	 */
	private void definirMensagemConfirmacao() {
		StringBuilder mensagem = new StringBuilder("Envio de notificações realizado com sucesso: ");
		if (obj.isEnviarEmail()) {
			mensagem.append(obj.getDestinatariosEmail().size() + (obj.getDestinatariosEmail().size() > 1 ? " e-mails" : " e-mail"));
		}
		if (obj.isEnviarEmail() && obj.isEnviarMensagem()) {
			mensagem.append(" e ");
		}
		if (obj.isEnviarMensagem()) {
			mensagem.append(obj.getDestinatariosMensagem().size() + (obj.getDestinatariosMensagem().size() > 1 ? " mensagens" : " mensagem"));
		}
		addMensagemInformation(mensagem.toString());
	}

	/**
	 * Limpa os dados do controlador
	 */
	public void clear() {
		obj = new Notificacao();
	}

	/**
	 * Retorna os papéis que podem enviar notificações.
	 *
	 * @return
	 */
	public abstract int[] getPapeis();

	/**
	 * Retorna no caminho para a página que contém o formulário
	 *
	 * @return
	 */
	public abstract String getCaminhoFormulario();

	/**
	 * Popula os destinatários da notificação
	 */
	public abstract void popularDestinatarios() throws ArqException;

}
