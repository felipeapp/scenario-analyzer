/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;

/**
 * Movimento utilizado para notificar consultores de avaliações
 *  de projetos de pesquisa pendentes
 *
 * @author Ricardo Wendell
 *
 */
public class MovimentoNotificacaoConsultores extends AbstractMovimentoAdapter {

	private String host;

	private String template;

	/** Construtor padrão */
	public MovimentoNotificacaoConsultores() {

	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
