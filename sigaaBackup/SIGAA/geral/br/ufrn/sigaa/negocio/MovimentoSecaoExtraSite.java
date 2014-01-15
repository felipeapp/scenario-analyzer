/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Aug 26, 2008
 *
 */
package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.sites.dominio.SecaoExtraSite;

/**
 * Classe que define o movimento dos dados envolvidos no caso de uso de cadastrar
 * ou alterar seções extras dos sites.
 * @author Mário Rizzi
 */
public class MovimentoSecaoExtraSite extends AbstractMovimentoAdapter {

	private SecaoExtraSite secaoExtraSite;

	public SecaoExtraSite getSecaoExtraSite() {
		return secaoExtraSite;
	}

	public void setSecaoExtraSite(SecaoExtraSite secaoExtraSite) {
		this.secaoExtraSite = secaoExtraSite;
	}

}
