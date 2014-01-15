/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '18/05/2007'
 *
 */
package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Movimento para realizar o auto-cadastro dos discentes
 * do SIGAA
 * 
 * @author David Pereira
 *
 */
public class MovimentoAutoCadastroDiscente extends MovimentoCadastro {

	private boolean internacional;
	
	private Usuario usr;
	
	private String ip;

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the usr
	 */
	public Usuario getUsr() {
		return usr;
	}

	/**
	 * @param usr the usr to set
	 */
	public void setUsr(Usuario usr) {
		this.usr = usr;
	}

	public boolean isInternacional() {
		return internacional;
	}

	public void setInternacional(boolean internacional) {
		this.internacional = internacional;
	}
	
}
