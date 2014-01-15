/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.validacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.ava.dominio.Enquete;
import br.ufrn.sigaa.ava.dominio.EnqueteResposta;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;

/**
 * Classe que especifica os campos necessários para o cadastro de uma enquete.
 */

public class EspecificacaoCadastroEnquete implements Specification {

	private Notification notification = new Notification();
	
	public Notification getNotification() {
		return notification;
	}
	
	/**
	 * Método verificar se todos os campos obrigatórios foram preenchidos corretamente, 
	 * para exibir ou não as mensagens informativas.
	 */
	public boolean isSatisfiedBy(Object objeto) {
		Enquete e = (Enquete) objeto;
		if (isEmpty(e.getPergunta())) notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Pergunta");
		if (e.getRespostas() == null || e.getRespostas().size() < 2) {
			notification.addMensagem(MensagensTurmaVirtual.ENQUETE_UMA_OU_MAIS_RESPOSTAS);
		} else {
			for (int i = 0; i < e.getRespostas().size(); i++) {
				EnqueteResposta item = e.getRespostas().get(i);
				if (isEmpty(item.getResposta()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resposta " + (i+1));
			}
		}
	
		return !notification.hasMessages();
	}
	
}
