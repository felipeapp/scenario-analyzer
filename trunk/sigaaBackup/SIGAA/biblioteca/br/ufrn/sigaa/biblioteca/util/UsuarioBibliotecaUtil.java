/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;

/**
 * 
 * Classe que contém os método comuns para trabalhar com os usuários da biblioteca
 * 
 * @author jadson
 *
 */
public class UsuarioBibliotecaUtil {

	/**
	 * <p>Método que retorna o usuárioBiblioteca único da biblioteca no sistema.</p>  
	 *
	 * @param idBiblioteca
	 * @param daoOpcionalParaBusca caso já tenha aberto o dao, o método vai utilizar o aberto, senão ele instancia o dao internamente e fecha no final do método
	 * @return as informações:  id, pessoa, senha, bloqueado, ativo
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static UsuarioBiblioteca retornaUsuarioBibliotecaUnicoDaBiblioteca(int idBiblioteca, UsuarioBibliotecaDao daoOpcionalParaBusca) throws NegocioException, DAOException{
		
		boolean passouDao = false;
		
		if(daoOpcionalParaBusca != null)
			passouDao = true;
			
		try {
			if(! passouDao )
				daoOpcionalParaBusca = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);	
			
			@SuppressWarnings("null")
			List<UsuarioBiblioteca> usuarios = daoOpcionalParaBusca.findUsuarioBibliotecaAtivoNaoQuitadoByBiblioteca( idBiblioteca );
			
			if(usuarios.size() > 1){
				throw new NegocioException("Biblioteca possui cadastro duplicado no sistema, entre em contado com o suporte para cancelar os cadastros excedentes");
			}else{
			
				if(usuarios.size() == 0){
					throw new NegocioException("Biblioteca não possui cadastro para utilizar os serviços de empréstimo.");
				}else{
					return usuarios.get(0);
				}
			}
			
		}finally{
			if(! passouDao && daoOpcionalParaBusca != null)
				daoOpcionalParaBusca.close();
		}
			
	}
	
	
	/**
	 * <p>Método que retorna o usuárioBiblioteca único da pessoa no sistema.</p>  
	 *
	 * @param idPessoa
	 * @param daoOpcionalParaBusca caso já tenha aberto o dao, o método vai utilizar o aberto, senão ele instancia o dao internamente e fecha no final do método
	 * @return as informações:  id, pessoa, senha, bloqueado, ativo
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static UsuarioBiblioteca retornaUsuarioBibliotecaUnicoDaPessoa(int idPessoa, UsuarioBibliotecaDao daoOpcionalParaBusca) throws NegocioException, DAOException{
		
		boolean passouDao = false;
		
		if(daoOpcionalParaBusca != null)
			passouDao = true;
			
		try {
			if(! passouDao )
				daoOpcionalParaBusca = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);	
			
			List<UsuarioBiblioteca> usuarios = daoOpcionalParaBusca.findUsuarioBibliotecaAtivoNaoQuitadoByPessoa( idPessoa );
			
			if(usuarios.size() > 1){
				throw new NegocioException("Usuário possui cadastro duplicado no sistema, entre em contado com o suporte para cancelar os cadastros excedentes");
			}else{
			
				if(usuarios.size() == 0){
					throw new NegocioException("Usuário não possui cadastro para utilizar os serviços da biblioteca.");
				}else{
					return usuarios.get(0);
				}
			}
			
		}finally{
			if(! passouDao && daoOpcionalParaBusca != null)
				daoOpcionalParaBusca.close();
		}
			
	}
	
	
	/**
	 * Verifica se o usuário possui mais de um usuário biblioteca ativo não quitado por vez no sistema, o que é extremamente proibido.
	 *
	 * @param contasUsuarioBiblioteca
	 * @return Caso só tenha um usuário não quitado ativo, retorna esse usuário. Caso não tenha nenhum retorna nulo, Caso tenha mais de um, lança uma exceção.
	 * @throws NegocioException
	 */
	public static UsuarioBiblioteca recuperaUsuarioNaoQuitadosAtivos(List<UsuarioBiblioteca> contasUsuarioBiblioteca) throws NegocioException{
		
		UsuarioBiblioteca ubTemp = null;
		
		int qtdNaoQuitados = 0;
		
		for (UsuarioBiblioteca usuarioBibliotecaTemp : contasUsuarioBiblioteca) {
			if(! usuarioBibliotecaTemp.isQuitado()){
				qtdNaoQuitados++;
				ubTemp = usuarioBibliotecaTemp;
			}
		} 
		
		if(qtdNaoQuitados > 1){
			throw new NegocioException("Usuário possui cadastro duplicado no sistema, entre em contado com o suporte para cancelar os cadastros excedentes");
		}else{
			return ubTemp; 
		}
	}
	
}
