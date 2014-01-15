/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 13/04/2011
 *
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.jsf;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServicoDocumento;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;

/**
 * Interface para unificar a forma de acesso externo aos managed beans que tratam objetos do tipo solicita��o de servi�o.
 * 
 * @author Felipe Rivas
 */
public interface ISolicitacaoServicoDocumentoMBean<T extends SolicitacaoServicoDocumento> {

	/**
	 * Exibe as informa��es completa da solicita��o.
	 * 
	 * N�o utilizado em JSPs
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException 
	 */
	public String visualizarDadosSolicitacao() throws ArqException;
	/**
	 * Exibe as informa��es completas da solicita��o para o bibliotec�rio.
	 * 
	 * N�o utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String visualizarDadosSolicitacaoAtendimento() throws ArqException;
	
	/**
	 * Altera os dados da solicita��o feita. 
	 * O usu�rio pode alterar solicita��es que est�o com status "Solicitado".
	 * 
	 * N�o utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String alterarSolicitacao() throws ArqException;
	/**
	 * Remove a solicita��o selecionada.
	 * O usu�rio pode alterar solicita��es que est�o com status "Solicitado".
	 * 
	 * N�o utilizado em JSPs
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String removerSolicitacao() throws DAOException;

	
	/**
	 * Atende uma solicita��o de servi�o.
	 * 
	 * N�o utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String atenderSolicitacao() throws ArqException;
	/**
	 * Cancela uma solicita��o de servi�o.
	 * 
	 * N�o utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cancelarSolicitacao() throws ArqException;
	
	/**
	 * Redireciona o fluxo de navega��o para a tela de exibi��o do comprovante de solicita��o.
	 * 
	 * N�o utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String telaComprovante() throws DAOException;
	
	/**
	 * Retorna o tipo de servi�o que � tratado no managed bean.
	 * 
	 * @return
	 */
	public TipoServicoInformacaoReferencia getTipoServico();
}
