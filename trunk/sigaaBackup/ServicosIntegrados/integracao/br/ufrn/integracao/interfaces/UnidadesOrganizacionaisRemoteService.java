/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendï¿½ncia de Informï¿½tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.interfaces;

import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.ResponsavelDTO;
import br.ufrn.integracao.dto.UnidadeDTO;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Interface contendo serviï¿½os remotos relativos ï¿½s
 * unidades organizacionais dos sistemas.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface UnidadesOrganizacionaisRemoteService {

	/**
	 * Realiza o cadastro de um unidade.
	 * @param unidade
	 * @return
	 */
	public UnidadeDTO cadastrarUnidade(UnidadeDTO unidade);

	/**
	 * Realiza a remoção de uma unidade.
	 * @param unidade
	 */
	public void removerUnidade(UnidadeDTO unidade);

	/**
	 * Realiza um cadastro um responsável por uma unidade.
	 * @param usuario
	 * @param responsavel
	 * @return
	 * @throws NegocioRemotoException 
	 */
	public ResponsavelDTO cadastrarResponsavel(UsuarioDTO usuario, ResponsavelDTO responsavel) throws NegocioRemotoException;
	
	/**
	 * Realiza a remoção de um responsável por uma unidade.
	 * @param usuario
	 * @param responsavel
	 * @return
	 * @throws NegocioRemotoException 
	 */
	public void removerResponsavel(UsuarioDTO usuario, ResponsavelDTO responsavel) throws NegocioRemotoException;
	
	/** MÃ©todo a ser implementado para retornar as unidades que poderÃ£o ter solicitaÃ§Ãµes eletrÃ´nicas encaminhadas */
	public List<UnidadeDTO> getUnidadesSolicitacoes();
	
}
