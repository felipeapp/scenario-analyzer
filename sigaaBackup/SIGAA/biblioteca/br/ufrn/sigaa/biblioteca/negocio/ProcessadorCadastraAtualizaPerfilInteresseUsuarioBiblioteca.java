/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 20/12/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.biblioteca.dao.DisseminacaoDaInformacaoDao;
import br.ufrn.sigaa.biblioteca.dominio.PerfilInteresseUsuarioBiblioteca;

/**
 *
 * <p> Processador respons�sal por criar e atualizar o perfil de interesse dos usu�rios da biblioteca. </p>
 * <p> Esse caso de uso faz parte da Dissemina��o Seletiva de Informa��o (DSI) do sistema. </p>
 *
 * <p> <i> Perfil de Interesse cont�m as informa��es das quais o usu�rio gostaria de receber comunicados do sistema.</i> </p>
 * 
 * @author jadson
 * 
 */
public class ProcessadorCadastraAtualizaPerfilInteresseUsuarioBiblioteca extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro movi = (MovimentoCadastro) mov;
		
		PerfilInteresseUsuarioBiblioteca perfil = (PerfilInteresseUsuarioBiblioteca) movi.getObjMovimentado();
		
		DisseminacaoDaInformacaoDao dao = null;
		
		try{
			
			dao = getDAO(DisseminacaoDaInformacaoDao.class, mov);

			// Zera a �rea do informativo se o usu�rio n�o escolheu //
			if(perfil.getAreaDoInformativo() != null && perfil.getAreaDoInformativo().getId() < 0)
				perfil.setAreaDoInformativo(null);
			
			dao.createOrUpdate(perfil); // atualiza o objeto perfil e os seu relacionamentos
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// N�o h� valida��es
	}

}
