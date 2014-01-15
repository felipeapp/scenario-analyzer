/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe que cont�m os m�todo comuns para trabalhar com os usu�rios da biblioteca
 * 
 * @author jadson
 *
 */
public class UsuarioBibliotecaUtil {

	/**
	 * <p>M�todo que retorna o usu�rioBiblioteca �nico da biblioteca no sistema.</p>  
	 *
	 * @param idBiblioteca
	 * @param daoOpcionalParaBusca caso j� tenha aberto o dao, o m�todo vai utilizar o aberto, sen�o ele instancia o dao internamente e fecha no final do m�todo
	 * @return as informa��es:  id, pessoa, senha, bloqueado, ativo
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
					throw new NegocioException("Biblioteca n�o possui cadastro para utilizar os servi�os de empr�stimo.");
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
	 * <p>M�todo que retorna o usu�rioBiblioteca �nico da pessoa no sistema.</p>  
	 *
	 * @param idPessoa
	 * @param daoOpcionalParaBusca caso j� tenha aberto o dao, o m�todo vai utilizar o aberto, sen�o ele instancia o dao internamente e fecha no final do m�todo
	 * @return as informa��es:  id, pessoa, senha, bloqueado, ativo
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
				throw new NegocioException("Usu�rio possui cadastro duplicado no sistema, entre em contado com o suporte para cancelar os cadastros excedentes");
			}else{
			
				if(usuarios.size() == 0){
					throw new NegocioException("Usu�rio n�o possui cadastro para utilizar os servi�os da biblioteca.");
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
	 * Verifica se o usu�rio possui mais de um usu�rio biblioteca ativo n�o quitado por vez no sistema, o que � extremamente proibido.
	 *
	 * @param contasUsuarioBiblioteca
	 * @return Caso s� tenha um usu�rio n�o quitado ativo, retorna esse usu�rio. Caso n�o tenha nenhum retorna nulo, Caso tenha mais de um, lan�a uma exce��o.
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
			throw new NegocioException("Usu�rio possui cadastro duplicado no sistema, entre em contado com o suporte para cancelar os cadastros excedentes");
		}else{
			return ubTemp; 
		}
	}
	
}
