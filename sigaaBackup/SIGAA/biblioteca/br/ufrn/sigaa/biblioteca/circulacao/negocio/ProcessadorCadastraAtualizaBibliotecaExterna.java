/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 24/05/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.jsf.BibliotecaExternaMBean;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 *
 * <p> Implementa as regras para o cadastra de bibliotecas externa do sistema.</p>
 *
 * <p> 
 * 		<i> Ao se criar uma nova biblioteca externa, deve-se criar um UsuarioBiblioteca para ela. 
 *  	Assim será possível realizar empréstimos institucionais. 
 *  	</i> 
 *  </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorCadastraAtualizaBibliotecaExterna extends AbstractProcessador{

	/**
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException,RemoteException {
		
		
		validate(movimento);
		
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		
		Biblioteca biblitoecaExterna = (Biblioteca) mov.getObjMovimentado();
		
		
		BibliotecaDao dao = null;
		UsuarioBibliotecaDao usuarioBibliotecaDao = null;
		
		
		try{
			dao = getDAO(BibliotecaDao.class, mov);
			usuarioBibliotecaDao = getDAO(UsuarioBibliotecaDao.class, mov); 
			
			
			//////// Retira dados transientes  ///////
			if(biblitoecaExterna.getUnidade().getId() == 0)
				biblitoecaExterna.setUnidade(null);
		
			
			// CRIANDO A BIBLIOTECA EXTERNA //
			if(biblitoecaExterna.getId() == 0){
				
				dao.create(biblitoecaExterna.getEndereco());
				dao.create(biblitoecaExterna);
				
				
				// cria o usuário para realizar os empréstimos //
				UsuarioBiblioteca usuarioBiblioteca = new UsuarioBiblioteca();
				usuarioBiblioteca.setVinculo(VinculoUsuarioBiblioteca.BIBLIOTECA_EXTERNA);
				usuarioBiblioteca.setIdentificacaoVinculo(biblitoecaExterna.getId());
				usuarioBiblioteca.setBiblioteca(biblitoecaExterna);
				
				dao.create(usuarioBiblioteca);
				
			}else{
				
				biblitoecaExterna.apagaDadosTransientes(); // tem que apadar o que não foi preenchido pelo usuário, senão dá erro.
				
				dao.update(biblitoecaExterna);
				dao.update(biblitoecaExterna.getEndereco());
				
				List<UsuarioBiblioteca> usuarioBiblioteca  = usuarioBibliotecaDao.findUsuarioBibliotecaAtivoNaoQuitadoByBiblioteca(biblitoecaExterna.getId());
				
				if(usuarioBiblioteca.size() == 0){ // biblitoeca externa não possui cadastra para realizar empréstimos 
					
					UsuarioBiblioteca usuario = new UsuarioBiblioteca();
					usuario.setVinculo(VinculoUsuarioBiblioteca.BIBLIOTECA_EXTERNA);
					usuario.setIdentificacaoVinculo(biblitoecaExterna.getId());
					usuario.setBiblioteca(biblitoecaExterna);
					
					dao.create(usuario);
				}else{
					if(usuarioBiblioteca.size() > 1){ 
						throw new NegocioException("A biblioteca selecionada não possui cadastro duplicado no sistema."); // Erro do sistema, era para estar cadastrado
					}
				}
			}
			
		}finally{
			if(dao != null)  dao.close();
			if(usuarioBibliotecaDao != null)  usuarioBibliotecaDao.close();
			
		}
		
		
			
		
		return null;
	}

	
	
	/**
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		
		Biblioteca biblitoecaExterna = mov.getObjMovimentado();
		
		ListaMensagens erros = new ListaMensagens();
		
		BibliotecaDao dao = null;
		
		
		try{
			dao = getDAO(BibliotecaDao.class, mov);
		
			Collection<Biblioteca> bibliotecasBanco = dao.findAtivosByExactField(Biblioteca.class, "identificador", biblitoecaExterna.getIdentificador());
			
			for (Biblioteca bibliotecaBanco : bibliotecasBanco) {
			
				// Se está cadastrando 
				if(biblitoecaExterna.getId() == 0 ){
					if (  bibliotecaBanco != null && bibliotecaBanco.isAtivo()){
						erros.addErro("Já existe uma biblioteca com o mesmo identificador '"+ biblitoecaExterna.getIdentificador()+"'");
					}
				}else{ // está atualizando 
					
					if (  bibliotecaBanco != null && bibliotecaBanco.getId() != biblitoecaExterna.getId() && bibliotecaBanco.isAtivo() ){
						erros.addErro("Já existe uma biblioteca com o mesmo identificador '"+ biblitoecaExterna.getIdentificador()+"'");
					}
				}
			
			}
			
			
			erros.addAll(biblitoecaExterna.validate());
			
		}finally{
			if(dao != null)  dao.close();
			checkValidation(erros);
		}
		
		
	}

}
