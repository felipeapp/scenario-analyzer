package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dominio.AbstractMovimentoAdapter;

public class MovimentoAgregadorBolsas extends AbstractMovimentoAdapter {
	
	private Mensagem mensagem;

	public Mensagem getMensagem() {
		return mensagem;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

}
