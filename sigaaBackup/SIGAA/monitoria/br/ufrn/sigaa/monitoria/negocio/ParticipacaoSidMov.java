/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 29/03/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.util.Collection;
import java.util.HashSet;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.monitoria.dominio.ParticipacaoSid;

/**
 * Movimento para distribuição de projetos de monitoria e resumos do SID.
 *
 * @author ilueny santos
 *
 */
public class ParticipacaoSidMov extends MovimentoCadastro {
	
	private Collection<ParticipacaoSid> participacoes =  new HashSet<ParticipacaoSid>();

	public Collection<ParticipacaoSid> getParticipacoes() {
		return participacoes;
	}

	public void setParticipacoes(Collection<ParticipacaoSid> participacoes) {
		this.participacoes = participacoes;
	}

		
}