/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 26/03/2010
 *
 */	

package br.ufrn.sigaa.biblioteca.util;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BuscaPessoaBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;

/**
 * <p>Classe que auxilia na busca de usu�rios da biblioteca, permitindo a utiliza��o unificada de um m�todo tanto para
 * buscas pelo sistema web quanto pelo sistema desktop.</p>
 * 
 * @author Fred_Castro
 */

public class BuscaUsuarioBibliotecaHelper {
	
	
	
	
	/**
	 * <p>Realiza a busca por usu�rios da biblioteca de acordo com os par�metros passados.</p>
	 * 
	 * @param tipoUsuario
	 * @param cpf
	 * @param matricula
	 * @param siape
	 * @param nome
	 * @param idBibliotecaComoUsuarioCirculacao
	 * @param operacao
	 * @param verificaSituacaoUsuario
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static List <Object []> buscaPadraoUsuarioBiblioteca (boolean buscarUsuariosComuns, boolean buscarUsuariosExternos, boolean buscarBibliotecas
			, String cpf,  String passaporte, String matricula, String siape, String nome, Integer idBibliotecaComoUsuarioCirculacao
			, boolean buscarAsPessoasSemCadastroBiblioteca
			, boolean buscaQuantidadeEmprestimoUsuario
			, boolean buscaLoginUsuario) throws NegocioException, DAOException{
		
		if(buscarUsuariosComuns == false && buscarUsuariosExternos == false && buscarBibliotecas == false)
			throw new IllegalArgumentException("O Tipo de usu�rio a ser busca n�o foi passado");
		
		try{
			if(StringUtils.notEmpty(matricula))  Long.parseLong(matricula);
		}catch(NumberFormatException nfe){
			throw new NegocioException("O campo MATR�CULA precisa ser num�rico.");
		}
		
		try{
			if(StringUtils.notEmpty(siape)) Integer.parseInt(siape);
		}catch(NumberFormatException nfe){
			throw new NegocioException("O campo SIAPE precisa ser num�rico.");
		}
		
		
		BuscaPessoaBibliotecaDao dao = null;
		UsuarioBibliotecaDao daoUsuario = null;
		EmprestimoDao daoEmprestimo = null;
		
		List <Object []> infoPessoas = new ArrayList <Object []> ();
		
		try {
			
			dao = DAOFactory.getInstance().getDAO(BuscaPessoaBibliotecaDao.class);
			daoUsuario = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
			daoEmprestimo = DAOFactory.getInstance().getDAO(EmprestimoDao.class);
			
			if(buscarBibliotecas ){
				if (idBibliotecaComoUsuarioCirculacao <= 0)
					throw new NegocioException ("Selecione uma biblioteca.");

				infoPessoas =  dao.findBibliotecaComUsuarioBibliotecaAtivo(idBibliotecaComoUsuarioCirculacao, buscarAsPessoasSemCadastroBiblioteca);
				
			}else{
				
				if(buscarAsPessoasSemCadastroBiblioteca){  // Caso especial, deve busca at� quem nunca utilizou o sistema de bibliotecas.
					infoPessoas = dao.findPessoasBiblioteca( (StringUtils.notEmpty(cpf)?  cpf.replaceAll("\\D","") : cpf), passaporte, matricula, siape, nome);
				}else{
					if(buscarUsuariosComuns)
						infoPessoas = dao.findPessoasComUsuarioBibliotecaAtivo( (StringUtils.notEmpty(cpf)?  cpf.replaceAll("\\D","") : cpf), passaporte, matricula, siape, nome);
					else{
						if(buscarUsuariosExternos)
							infoPessoas = dao.findPessoaComUsuarioExternoBibliotecaAtivo( (StringUtils.notEmpty(cpf)?  cpf.replaceAll("\\D","") : cpf), passaporte,  nome);
					}
				}
			}
			
			return infoPessoas;
			
		} finally {
			if (dao != null)
				dao.close();
			
			if (daoUsuario != null)
				daoUsuario.close();
			
			if (daoEmprestimo != null)
				daoEmprestimo.close();
		}
	}
}