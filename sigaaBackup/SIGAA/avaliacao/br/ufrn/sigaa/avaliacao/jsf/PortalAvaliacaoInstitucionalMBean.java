/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 20/01/2009 
 */
package br.ufrn.sigaa.avaliacao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Managed bean para o portal da avalia��o institucional
 * 
 * @author David Pereira
 *
 */
@Component @Scope("request")
public class PortalAvaliacaoInstitucionalMBean extends SigaaAbstractController<Object> {

	public PortalAvaliacaoInstitucionalMBean() {
	}
	
}
