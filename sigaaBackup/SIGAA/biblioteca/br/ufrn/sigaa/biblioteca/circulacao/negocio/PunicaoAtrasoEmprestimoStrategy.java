/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.Date;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PunicaoAtrasoEmprestimoBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;

/**
 *
 * <p> Interface com os m�todo padr�o para o calculo de cria��o de uma nova puni��o para os usu�rios da biblioteca </p>
 * 
 * @author jadson
 *
 */
public abstract class PunicaoAtrasoEmprestimoStrategy {
	
	/**
	 *  Cria uma punica��o para o usu�rio da biblioteca, dependendo da estrat�gia
	 *
	 * @param e
	 * @param prazoDevolucao
	 * @param dataQueFoiDevolvido
	 * @return
	 * @throws DAOException
	 */
	public abstract PunicaoAtrasoEmprestimoBiblioteca criarPunicaoAutomatica(final Emprestimo e, final  Date prazoDevolucao, final Date dataQueFoiDevolvido) throws DAOException;
	
	/**
	 * 
	 * Verifica se o usu�rio possui punicoes na biblioteca, dependendo da estrat�gia
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public abstract SituacaoUsuarioBiblioteca verificaPunicoesUsuario(final Integer idPessoa, final Integer idBiblioteca) throws DAOException;
	
	
	/**
	 *
	 * <p>Desfaz a puni��o para o usu�rio da biblioteca, dependendo da estrat�gia</p>
	 *
	 * <p>@see {@link ProcessadorDesfazOperacao}</p>
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public abstract void desfazPunicoesUsuario(final int idEmprestimo) throws DAOException;
	
	
	/**
	 *  Retorna o valor da puni��o formatada de acordo com a estrag�gia utilizada
	 *
	 * @param valor
	 * @return
	 */
	public abstract String getValorFormatado(Object valor);
	
	
}
