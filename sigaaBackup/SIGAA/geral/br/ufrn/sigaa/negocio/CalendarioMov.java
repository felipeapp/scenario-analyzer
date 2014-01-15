/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '29/12/2006'
 *
 */
package br.ufrn.sigaa.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.dominio.EventoExtraSistema;

/**
 * Movimento para atualizar registros de calend�rios acad�micos
 * @author amdantas
 *
 */
public class CalendarioMov extends MovimentoCadastro {

	
	private Collection<EventoExtraSistema> extrasRemovidos;

	public Collection<EventoExtraSistema> getExtrasRemovidos() {
		return extrasRemovidos;
	}

	public void setExtrasRemovidos(Collection<EventoExtraSistema> extrasRemovidos) {
		this.extrasRemovidos = extrasRemovidos;
	}
	
	
}
