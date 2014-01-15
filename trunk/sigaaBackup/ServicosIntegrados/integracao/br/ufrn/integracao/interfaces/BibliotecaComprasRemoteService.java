/*
 * BibliotecaComprasRemoteService.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

import br.ufrn.integracao.dto.biblioteca.InformacoesTombamentoMateriaisDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoBibliotecaException;


/**
 *
 *    <p>Interface que contem os metodos remotos da parte de compras da biblioteca.</p>
 *    <p> Usada para o SIGAA chamar metodos no SIPAC.</p> 
 *    <p>Sempre nessa ordem SIGAA --> SIPAC. Se quiser SIPAC --> SIGAA um a interface BibliotecaCatalogacaoRemoteService</p>
 *
 * @author jadson
 * @since 11/03/2009
 * @version 1.0 criacao da classe
 * @version 1.1 09/02/2011 adpta��o da busca para usar o" numero de c�digo de barras" de um bem em v�z do "n�mero de tombamento". Para o contexto da UFRN 
 * n�o muda nada, NCodBarras = NTombamento.   Mas o NTombamento n�o vai mais identificar unicamente o Bem.
 *
 */
@WebService
public interface BibliotecaComprasRemoteService {

	/**
	 *    <p>M�todo que obt�m informa��es do bem tombado no SIPAC. A partir do n�mero de tombamento de algum bem do termo de responsabilidade. </p> 
	 *    <p>Serve principalmente para pegar os n�meros dos tombamentos dos bens que ser�o usados para
	 * gera��o dos c�digos de barras dos materiais informacionais da biblioteca, n�o necessitando que 
	 * o usu�rio os digite, evitando erros de digita��o.</p> 
	 *
	 * <p>N�o � chamado por nenhuma JSP.</p>
	 * 
	 * @param numeroPatrimonio  o n�mero de patrim�nio digitado pelo usu�rio na tela de busca antes de iniciar a cataloga��o.
	 * @return informa��es necess�rias para realizar a cataloga��o, como os n�meros de tombamento, os tipos de tombamento e as unidades para onde os bens foram tombados.
	 * @throws NegocioRemotoBibliotecaException caso o n�mero digitado n�o existe, n�o seja de um livro ou o tombamento esteja anulado.
	 */
	public InformacoesTombamentoMateriaisDTO buscaInformacoesFinanceirasPorNumeroPatrimonio(Long numeroPatrimonio) throws NegocioRemotoBibliotecaException;
	
}
