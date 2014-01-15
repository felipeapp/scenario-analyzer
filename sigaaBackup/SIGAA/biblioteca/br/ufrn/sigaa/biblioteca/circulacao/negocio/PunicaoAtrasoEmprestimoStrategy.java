/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Interface com os método padrão para o calculo de criação de uma nova punição para os usuários da biblioteca </p>
 * 
 * @author jadson
 *
 */
public abstract class PunicaoAtrasoEmprestimoStrategy {
	
	/**
	 *  Cria uma punicação para o usuário da biblioteca, dependendo da estratégia
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
	 * Verifica se o usuário possui punicoes na biblioteca, dependendo da estratégia
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public abstract SituacaoUsuarioBiblioteca verificaPunicoesUsuario(final Integer idPessoa, final Integer idBiblioteca) throws DAOException;
	
	
	/**
	 *
	 * <p>Desfaz a punição para o usuário da biblioteca, dependendo da estratégia</p>
	 *
	 * <p>@see {@link ProcessadorDesfazOperacao}</p>
	 *
	 * @param idUsuarioBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public abstract void desfazPunicoesUsuario(final int idEmprestimo) throws DAOException;
	
	
	/**
	 *  Retorna o valor da punição formatada de acordo com a estragégia utilizada
	 *
	 * @param valor
	 * @return
	 */
	public abstract String getValorFormatado(Object valor);
	
	
}
