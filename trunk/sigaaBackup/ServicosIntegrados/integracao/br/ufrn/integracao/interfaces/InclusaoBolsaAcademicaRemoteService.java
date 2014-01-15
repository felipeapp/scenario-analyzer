/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
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
 * Exposta através do Spring HTTP Invoker (Spring Remotting). 
 * @author Itamir Filho
 * 
 */
@WebService
public interface InclusaoBolsaAcademicaRemoteService extends Serializable {

	
	
	/**
	 * Chama o processador do SIPAC e realiza a inclusão de uma bolsa a partir de informações no sigaa.
	 * Retorna o id da solicitação cadastrada.
	 * @param inclusaoBolsa
	 * @return
	 * @throws NegocioRemotoException 
	 */
	public int cadastrarSolicitacaoInclusao(InclusaoBolsaAcademicaDTO inclusaoBolsa) throws NegocioRemotoException;
	
	/**
	 * Realiza o update da solicitação de uma bolsa do tipo Alimentação/Residência que tenha sido solicitada pelo SAE. 
	 * O SAE costuma alterar essas bolsas constantemente, esse método serve para evitar a criação de várias solicitações 
	 * na listagem de bolsas do SIPAC na seção Solicitações Atendidas.
	 * 
	 * @param inclusaoBolsa
	 * @return
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public int atualizaSolicitacaoInclusao(InclusaoBolsaAcademicaDTO inclusaoBolsa) throws NegocioRemotoException;
	
	/** Chama o processador do SIPAC e realiza uma solicitação de exclusão de bolsa, retornando o IS da solicitação cadastrada. 
	 * @param ExclusaoBolsa
	 * @return número de bolsas canceladas no SIPAC
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public int cadastrarSolicitacaoExclusao(InclusaoBolsaAcademicaDTO inclusaoBolsa) throws NegocioRemotoException;
	
	/**
	 * Método invocado para o cadastro de uma bolsa no sistema administrativo a partir de uma bolsa do SAE cadastrada pelo sistema acadêmico.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não chamado por JSP(s).</li>
	 * </ul>
	 * @param dadosBolsaAcademica
	 * @return
	 * @throws NegocioRemotoException
	 */
	public int cadastrarBolsa(InclusaoBolsaAcademicaDTO dadosBolsaAcademica) throws NegocioRemotoException;
	
	/**
	 * Método que atualiza uma bolsa acadêmica no sistema administrativo.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não chamado por JSP(s).</li>
	 * </ul>
	 * @param dadosBolsaAcademica
	 * @return
	 * @throws NegocioRemotoException
	 */
	public int atualizarBolsa(InclusaoBolsaAcademicaDTO dadosBolsaAcademica) throws NegocioRemotoException; 
	
	/**
	 * Método que finaliza uma bolsa acadêmica no sistema administrativo.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não chamado por JSP(s).</li>
	 * </ul>
	 * @param dadosBolsaAcademica
	 * @throws NegocioRemotoException
	 */
	public void finalizarBolsa(InclusaoBolsaAcademicaDTO dadosBolsaAcademica) throws NegocioRemotoException; 
	
	/**
	 * Método que finaliza todas a bolsa acadêmicas ativas de um aluno.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não chamado por JSP(s).</li>
	 * </ul>
	 * @param dadosBolsaAcademica
	 * @return
	 * @throws NegocioRemotoException
	 */
	public int finalizarBolsas(InclusaoBolsaAcademicaDTO dadosBolsaAcademica) throws NegocioRemotoException; 
	
	/**
	 * Obtém os tipos de bolsas administrativas correspondentes aos tipos de bolsas acadêmicas do SAE.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não chamado por JSP(s).</li>
	 * </ul>
	 * @param idTipoAcademico
	 * @return
	 * @throws NegocioRemotoException
	 */
	public List<TipoBolsaDTO> listarTipoBolsa(Integer idTipoAcademico) throws NegocioRemotoException;
}
