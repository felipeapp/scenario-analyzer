/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Interface para unificar a forma de acesso externo aos managed beans que tratam objetos do tipo solicitação de serviço.
 * 
 * @author Felipe Rivas
 */
public interface ISolicitacaoServicoDocumentoMBean<T extends SolicitacaoServicoDocumento> {

	/**
	 * Exibe as informações completa da solicitação.
	 * 
	 * Não utilizado em JSPs
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException 
	 */
	public String visualizarDadosSolicitacao() throws ArqException;
	/**
	 * Exibe as informações completas da solicitação para o bibliotecário.
	 * 
	 * Não utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String visualizarDadosSolicitacaoAtendimento() throws ArqException;
	
	/**
	 * Altera os dados da solicitação feita. 
	 * O usuário pode alterar solicitações que estão com status "Solicitado".
	 * 
	 * Não utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String alterarSolicitacao() throws ArqException;
	/**
	 * Remove a solicitação selecionada.
	 * O usuário pode alterar solicitações que estão com status "Solicitado".
	 * 
	 * Não utilizado em JSPs
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String removerSolicitacao() throws DAOException;

	
	/**
	 * Atende uma solicitação de serviço.
	 * 
	 * Não utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String atenderSolicitacao() throws ArqException;
	/**
	 * Cancela uma solicitação de serviço.
	 * 
	 * Não utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cancelarSolicitacao() throws ArqException;
	
	/**
	 * Redireciona o fluxo de navegação para a tela de exibição do comprovante de solicitação.
	 * 
	 * Não utilizado em JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String telaComprovante() throws DAOException;
	
	/**
	 * Retorna o tipo de serviço que é tratado no managed bean.
	 * 
	 * @return
	 */
	public TipoServicoInformacaoReferencia getTipoServico();
}
