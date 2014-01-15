/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

import br.ufrn.integracao.dto.GrupoDestinatariosDTO;
import br.ufrn.integracao.dto.NotificacaoDTO;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Interface para serviço de envio de notificações através
 * dos sistemas.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface EnvioNotificacoesRemoteService {

	public void enviar(UsuarioDTO usuario, NotificacaoDTO notificacao) throws NegocioRemotoException;

	public GrupoDestinatariosDTO buscarGrupoDestinatarios(int idGrupo);

}
