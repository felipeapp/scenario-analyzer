/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Interface com os servi�os remotos disponibilizados pela parte de circula��o da biblioteca </p>
 * 
 * @author jadson
 *
 */
@WebService
public interface BibliotecaCirculacaoRemoteService {

	
	
	/** 
	 *  <p> M�todo que verifica se o usu�rio possui empr�stimos em atraso por um per�odo maior que o tolerado, neste caso, 
	 *  cabe algumas penalidades</p>
	 * 
	 *  <p>M�todo para ser usado por sistemas remotos, onde n�o � poss�vel garantir que as pessas tenham os ids iguais.</p>
	 * 
	 * @param idPessoa
	 */
	public void verificaUsuarioPossuiInrregularidadeAdministrativa(Long cpf_cnpj) throws NegocioRemotoException;
	
	
	
}
