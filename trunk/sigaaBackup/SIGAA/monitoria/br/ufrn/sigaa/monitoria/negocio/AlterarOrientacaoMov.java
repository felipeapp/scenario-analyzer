/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/03/2007
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;

/**
 * Movimento para realizar a altera��o dos orientadores de monitores
 * 
 * @author Victor Hugo
 *
 */
public class AlterarOrientacaoMov extends MovimentoCadastro {

	private Collection<Orientacao> orientacoesAdicionar; //cole��o de orienta��es a serem adicionadas
	private Collection<Orientacao> orientacoesAtualizar; //cole��o de orienta��es a serem 
	
	public Collection<Orientacao> getOrientacoesAdicionar() {
		return orientacoesAdicionar;
	}
	public void setOrientacoesAdicionar(Collection<Orientacao> orientacoesAdicionar) {
		this.orientacoesAdicionar = orientacoesAdicionar;
	}
	public Collection<Orientacao> getOrientacoesAtualizar() {
	    return orientacoesAtualizar;
	}
	public void setOrientacoesAtualizar(Collection<Orientacao> orientacoesAtualizar) {
	    this.orientacoesAtualizar = orientacoesAtualizar;
	}
	
}
