/*
 * BibliotecaCatalogacaoRemoteService.java
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

import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.biblioteca.CampoRemotoDTO;
import br.ufrn.integracao.dto.biblioteca.ExemplarResumidoDTO;
import br.ufrn.integracao.dto.biblioteca.TituloResumidoDTO;

/**
 *
 * Interface que contem os metodos remotos que o SIPAC vai chamar no SIGAA. 
 * Sempre nessa ordem SIPAC --> SIGAA. Se quiser SIGAA --> SIPAC um a interface BibliotecaComprasRemoteService
 *
 * @author jadson
 * @since 04/03/2009
 * @version 1.0 criacao da classe
 *
 */
@WebService
public interface BibliotecaCatalogacaoRemoteService {

	/**
	 * 
	 *     Faz a busca por multi campo e encapsula o retornao do metodo para o sistema remoto pode 
	 * acessar já que nao existe a entidade TituloCatalograficoResumido no sistema remoto.
	 *
	 * @param titulo
	 * @param assunto
	 * @param autor
	 * @param localPublicacao
	 * @param editora
	 * @param anoInicial
	 * @param anoFinal
	 * @return
	 * @throws Exception
	 */
	public List<TituloResumidoDTO> buscaMultiCampoTituloRemota(String titulo, String assunto, String autor
			, String localPublicacao, String editora, Integer anoInicial, Integer anoFinal);
	
	
	/**
	 * 
	 * Mostra no SIPAC os exemplares que existem no acervo da biblioteca. 
	 * Se o titulo for de pariodico mostra os fasciculos;
	 *
	 * @param idTitulo
	 * @return
	 */
	public List<ExemplarResumidoDTO> buscaExemplaresPorTitulo(int idTitulo);
	
	
	/**
	 *   Retorna as informacoes completas de um titulo em formato MARC. Retorna uma string com os 
	 * dados montados para visualizacao para o sistema remoto nao precisar ficar sabendo o que sao 
	 * campos e subcampos.
	 *
	 * @param idTitulo
	 * @return
	 */
	public List<CampoRemotoDTO> mostaInformacoesCompletasTitulo(int idTitulo);
	
}
