/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 20/01/2009 
 */
package br.ufrn.sigaa.avaliacao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Managed bean para o portal da avaliação institucional
 * 
 * @author David Pereira
 *
 */
@Component @Scope("request")
public class PortalAvaliacaoInstitucionalMBean extends SigaaAbstractController<Object> {

	public PortalAvaliacaoInstitucionalMBean() {
	}
	
}
