/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 29/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;

/**
 * <p>Interface a ser implementada por quem quer realiza a busca padrão de participantes de extensão. </p>
 * 
 * @author jadson
 * 
 * @see BuscaPadraoParticipanteExtensaoMBean
 *
 */
public interface PesquisarParticipanteExtensao {
	
	/**
	 * <p>Método usado pelo MBean da busca de participantes para passar para o MBean do caso de uso o participante selecionado.</p>
	 *
	 * @param usuario
	 */
	public void setParticipanteExtensao(CadastroParticipanteAtividadeExtensao participante);
	
	
	/**
	 *  <p>Método usado pelo MBean da busca de participantes para retornar o fluxo do caso de uso para o MBean chamador, depois
	 *  que o participantes foi setado na tela de busca anterior.</p>
	 *
	 * @return
	 */
	public String selecionouParticipanteExtensao() throws ArqException;

	
	/**
	 *  <p>Método que deve ser sobre escrito para implementar funcionalidade do botão cancelar da busca.</p>
	 *
	 * @return
	 */
	public String cancelarPesquiasParticipanteExtensao();
}
