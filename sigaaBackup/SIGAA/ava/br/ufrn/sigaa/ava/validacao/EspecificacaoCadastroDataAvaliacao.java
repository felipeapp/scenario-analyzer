/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.validacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;

/**
 * Classe que especifica os campos necessários para o cadastro de uma data de avaliação.
 */

public class EspecificacaoCadastroDataAvaliacao implements Specification {
	
	/** Guarda a lista de mensagens a serem exibidas na próxima resposta ao usuário. */
	private Notification notification = new Notification();
	
	public Notification getNotification() {
		return notification;
	}

	/**
	 * Verifica se campos de Descrição, Data e Hora da data de avaliação foram preenchidos corretamente.
	 */
	public boolean isSatisfiedBy(Object objeto) {
		DataAvaliacao data = (DataAvaliacao) objeto;
		
		if (isEmpty(data.getDescricao())) notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
		if (isEmpty(data.getData())) notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data");
		if (isEmpty(data.getHora())) notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora");
		
		return !notification.hasMessages();
	}
}