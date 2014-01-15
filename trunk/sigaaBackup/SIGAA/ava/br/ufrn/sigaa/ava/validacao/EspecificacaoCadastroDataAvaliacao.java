/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ava.validacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;

/**
 * Classe que especifica os campos necess�rios para o cadastro de uma data de avalia��o.
 */

public class EspecificacaoCadastroDataAvaliacao implements Specification {
	
	/** Guarda a lista de mensagens a serem exibidas na pr�xima resposta ao usu�rio. */
	private Notification notification = new Notification();
	
	public Notification getNotification() {
		return notification;
	}

	/**
	 * Verifica se campos de Descri��o, Data e Hora da data de avalia��o foram preenchidos corretamente.
	 */
	public boolean isSatisfiedBy(Object objeto) {
		DataAvaliacao data = (DataAvaliacao) objeto;
		
		if (isEmpty(data.getDescricao())) notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descri��o");
		if (isEmpty(data.getData())) notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data");
		if (isEmpty(data.getHora())) notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora");
		
		return !notification.hasMessages();
	}
}