/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/07/2011
 * 
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 *
 * <p> Interface com os serviços remotos disponibilizados pela parte de circulação da biblioteca </p>
 * 
 * @author jadson
 *
 */
@WebService
public interface BibliotecaCirculacaoRemoteService {

	
	
	/** 
	 *  <p> Método que verifica se o usuário possui empréstimos em atraso por um período maior que o tolerado, neste caso, 
	 *  cabe algumas penalidades</p>
	 * 
	 *  <p>Método para ser usado por sistemas remotos, onde não é possível garantir que as pessas tenham os ids iguais.</p>
	 * 
	 * @param idPessoa
	 */
	public void verificaUsuarioPossuiInrregularidadeAdministrativa(Long cpf_cnpj) throws NegocioRemotoException;
	
	
	
}
