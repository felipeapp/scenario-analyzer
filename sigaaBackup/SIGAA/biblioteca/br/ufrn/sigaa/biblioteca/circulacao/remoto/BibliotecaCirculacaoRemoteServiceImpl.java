/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 05/07/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.remoto;

import javax.jws.WebService;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.BibliotecaCirculacaoRemoteService;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;

/**
 * <p>Implementa os servi�os remotos para outros sistemas que a parte de circula��o da biblioteca disponibiliza.</p>
 * 
 * @author jadson
 *
 */
@Component("bibliotecaCirculacaoRemoteService")
@Scope("singleton") 
@WebService
public class BibliotecaCirculacaoRemoteServiceImpl implements BibliotecaCirculacaoRemoteService{

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.integracao.interfaces.BibliotecaCirculacaoRemoteService#verificaUsuarioPossuiInregularidadeAdministrativa(long)
	 */
	@Override
	public void verificaUsuarioPossuiInrregularidadeAdministrativa(Long cpf_cnpj) throws NegocioRemotoException {
		
		try {
			if(cpf_cnpj != null){
				VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiInrregularidadeAdministrativa(cpf_cnpj);
			}else{
				throw new NegocioRemotoException("N�o p�de verificar a situa��o do usu�rio na biblioteca, pois o usu�rio n�o possui CPF"); // Era para ser obrigat�rio
			}
		}catch (NegocioException ne) {
			throw new NegocioRemotoException(ne.getMessage());
		} 
		catch (DAOException daoe) {
			throw new NegocioRemotoException(daoe.getMessage());
		}
	}

	
}
