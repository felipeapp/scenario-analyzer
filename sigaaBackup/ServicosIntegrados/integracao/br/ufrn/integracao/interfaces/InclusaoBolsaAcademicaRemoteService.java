/*
 * Universidade Federal do Rio Grande do Norte

 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 10/02/2010
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.integracao.dto.InclusaoBolsaAcademicaDTO;
import br.ufrn.integracao.dto.TipoBolsaDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;


/**
 * Interface Remota para o SIGAA se comunicar com o SIPAC. 
 * Exposta atrav�s do Spring HTTP Invoker (Spring Remotting). 
 * @author Itamir Filho
 * 
 */
@WebService
public interface InclusaoBolsaAcademicaRemoteService extends Serializable {

	
	
	/**
	 * Chama o processador do SIPAC e realiza a inclus�o de uma bolsa a partir de informa��es no sigaa.
	 * Retorna o id da solicita��o cadastrada.
	 * @param inclusaoBolsa
	 * @return
	 * @throws NegocioRemotoException 
	 */
	public int cadastrarSolicitacaoInclusao(InclusaoBolsaAcademicaDTO inclusaoBolsa) throws NegocioRemotoException;
	
	/**
	 * Realiza o update da solicita��o de uma bolsa do tipo Alimenta��o/Resid�ncia que tenha sido solicitada pelo SAE. 
	 * O SAE costuma alterar essas bolsas constantemente, esse m�todo serve para evitar a cria��o de v�rias solicita��es 
	 * na listagem de bolsas do SIPAC na se��o Solicita��es Atendidas.
	 * 
	 * @param inclusaoBolsa
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public int atualizaSolicitacaoInclusao(InclusaoBolsaAcademicaDTO inclusaoBolsa) throws NegocioRemotoException;
	
	/** Chama o processador do SIPAC e realiza uma solicita��o de exclus�o de bolsa, retornando o IS da solicita��o cadastrada. 
	 * @param ExclusaoBolsa
	 * @return n�mero de bolsas canceladas no SIPAC
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public int cadastrarSolicitacaoExclusao(InclusaoBolsaAcademicaDTO inclusaoBolsa) throws NegocioRemotoException;
	
	/**
	 * M�todo invocado para o cadastro de uma bolsa no sistema administrativo a partir de uma bolsa do SAE cadastrada pelo sistema acad�mico.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>M�todo n�o chamado por JSP(s).</li>
	 * </ul>
	 * @param dadosBolsaAcademica
	 * @return
	 * @throws NegocioRemotoException
	 */
	public int cadastrarBolsa(InclusaoBolsaAcademicaDTO dadosBolsaAcademica) throws NegocioRemotoException;
	
	/**
	 * M�todo que atualiza uma bolsa acad�mica no sistema administrativo.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>M�todo n�o chamado por JSP(s).</li>
	 * </ul>
	 * @param dadosBolsaAcademica
	 * @return
	 * @throws NegocioRemotoException
	 */
	public int atualizarBolsa(InclusaoBolsaAcademicaDTO dadosBolsaAcademica) throws NegocioRemotoException; 
	
	/**
	 * M�todo que finaliza uma bolsa acad�mica no sistema administrativo.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>M�todo n�o chamado por JSP(s).</li>
	 * </ul>
	 * @param dadosBolsaAcademica
	 * @throws NegocioRemotoException
	 */
	public void finalizarBolsa(InclusaoBolsaAcademicaDTO dadosBolsaAcademica) throws NegocioRemotoException; 
	
	/**
	 * M�todo que finaliza todas a bolsa acad�micas ativas de um aluno.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>M�todo n�o chamado por JSP(s).</li>
	 * </ul>
	 * @param dadosBolsaAcademica
	 * @return
	 * @throws NegocioRemotoException
	 */
	public int finalizarBolsas(InclusaoBolsaAcademicaDTO dadosBolsaAcademica) throws NegocioRemotoException; 
	
	/**
	 * Obt�m os tipos de bolsas administrativas correspondentes aos tipos de bolsas acad�micas do SAE.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>M�todo n�o chamado por JSP(s).</li>
	 * </ul>
	 * @param idTipoAcademico
	 * @return
	 * @throws NegocioRemotoException
	 */
	public List<TipoBolsaDTO> listarTipoBolsa(Integer idTipoAcademico) throws NegocioRemotoException;
}
