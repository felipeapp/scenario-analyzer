/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/04/2010
 */
package br.ufrn.integracao.servicos;

import org.springframework.beans.factory.annotation.Autowired;

import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;
import br.ufrn.integracao.interfaces.TokenAutenticacaoRemoteService;

/**
 * Implementação do serviço remoto de geração do token
 * de autenticação.
 * 
 * @author David Pereira
 *
 */
public class TokenAutenticacaoRemoteServiceImpl implements TokenAutenticacaoRemoteService {

	@Autowired
	private TokenGenerator generator;
	
	@Override
	public Object[] registerToken(Integer idRegistroEntrada, int sistema, String... info) {
		TokenAutenticacao token = generator.generateToken(idRegistroEntrada, sistema, info);
		generator.registerToken(token);
		return new Object[] { token.getId(), token.getKey() };
	}

}
