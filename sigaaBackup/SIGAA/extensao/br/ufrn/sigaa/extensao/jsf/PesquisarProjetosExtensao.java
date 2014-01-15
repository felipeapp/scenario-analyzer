/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/02/2013
 * 
 */
package br.ufrn.sigaa.extensao.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.projetos.dominio.Projeto;


/**
 * Interface para ser implementada por quem precisa realizar a busca de atividades de extens�o. 
 * E diminuir a duplica��o de c�digo no sistema.
 *
 * Geralmente usada pelos gestores de extens�o. 
 *
 * @author jadson - jadson@info.ufrn.br
 *
 */
public interface PesquisarProjetosExtensao {

	
	/**
	 * <p>M�todo usado pelo MBean da busca de projetos para passar para o MBean do caso de uso o projeto selecionado.</p>
	 *
	 * @param usuario
	 */
	public void setProjetoExtensao(Projeto projeto);
	
	
	/**
	 *  <p>M�todo usado pelo MBean da busca de projetos para retornar o fluxo do caso de uso para o MBean chamador, depois
	 *  que o projeto foi setado na tela de busca anterior.</p>
	 *
	 * @return
	 */
	public String selecionouProjetExtensao() throws ArqException;

	
	/**
	 *  <p>M�todo que deve ser sobre escrito para implementar funcionalidade do bot�o cancelar da busca.</p>
	 *
	 * @return
	 */
	public String cancelarBuscaProjetExtensao();
	
}
