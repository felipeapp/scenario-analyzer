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
 * @version 1.1 09/02/2011 adptação da busca para usar o" numero de código de barras" de um bem em véz do "número de tombamento". Para o contexto da UFRN 
 * não muda nada, NCodBarras = NTombamento.   Mas o NTombamento não vai mais identificar unicamente o Bem.
 *
 */
@WebService
public interface BibliotecaComprasRemoteService {

	/**
	 *    <p>Método que obtém informações do bem tombado no SIPAC. A partir do número de tombamento de algum bem do termo de responsabilidade. </p> 
	 *    <p>Serve principalmente para pegar os números dos tombamentos dos bens que serão usados para
	 * geração dos códigos de barras dos materiais informacionais da biblioteca, não necessitando que 
	 * o usuário os digite, evitando erros de digitação.</p> 
	 *
	 * <p>Não é chamado por nenhuma JSP.</p>
	 * 
	 * @param numeroPatrimonio  o número de patrimônio digitado pelo usuário na tela de busca antes de iniciar a catalogação.
	 * @return informações necessárias para realizar a catalogação, como os números de tombamento, os tipos de tombamento e as unidades para onde os bens foram tombados.
	 * @throws NegocioRemotoBibliotecaException caso o número digitado não existe, não seja de um livro ou o tombamento esteja anulado.
	 */
	public InformacoesTombamentoMateriaisDTO buscaInformacoesFinanceirasPorNumeroPatrimonio(Long numeroPatrimonio) throws NegocioRemotoBibliotecaException;
	
}
