/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.caixa_postal.ASyncMsgDelegate;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

public class ProcessadorAgregadorBolsas extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		
		MovimentoAgregadorBolsas movAB = (MovimentoAgregadorBolsas) mov;
		
		if ( mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_MENSAGEM_COORDENAOR)) {
			enviarMensagem(movAB);
		}
		
		return null;
	}

	private void enviarMensagem(MovimentoAgregadorBolsas mov) {
		Mensagem mensagem = mov.getMensagem();
		
		
		
		Collection<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();
		
		usuarios.add(new UsuarioGeral(mensagem.getUsuarioDestino().getId()));
		
		ASyncMsgDelegate.getInstance().enviaMensagemUsuarios(mensagem, mensagem.getUsuario(), usuarios );
		
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
