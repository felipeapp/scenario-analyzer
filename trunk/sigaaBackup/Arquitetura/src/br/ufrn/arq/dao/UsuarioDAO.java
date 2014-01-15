/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 27/09/2004
 */
package br.ufrn.arq.dao;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * DAO que mapeia os métodos de busca do Usuariio.
 *
 * @author Gleydson Lima
 *
 */
public interface UsuarioDAO extends GenericDAO, PaginableDAO {


	/**
	 * Busca usuário pelo login
	 *
	 * @param login
	 * @param apenasAtivos
	 *            restringe a busca usuários ativos
	 * @return
	 * @throws DAOException
	 */
	public UsuarioGeral findByLogin(String login, boolean apenasAtivos,
			boolean apenasAutorizados) throws DAOException;

	/**
	 * Busca usuário pelo login
	 *
	 * @param login
	 * @return
	 * @throws DAOException
	 */
	public UsuarioGeral findByLogin(String login) throws DAOException;

	/**
	 * 
	 * @param login
	 * @return
	 * @throws DAOException
	 */
	public UsuarioGeral findByLoginOtimizado(String login) throws DAOException;
	
	/**
	 * Busca usuário pelo nome
	 *
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByNome(String nome, UnidadeGeral unidade)
			throws DAOException;

	/**
	 * Busca o usuário pela matrícula passada
	 *
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByMatricula(int matricula)
			throws DAOException;

	/**
	 * Busca o usuário pelo CPF
	 *
	 * @param cpf_cnpj
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByCpf(long cpf_cnpj,Boolean ativo) throws DAOException;

	/**
	 * Busca usuário pelo nome, unidade e papel e hierarquia de sua unidade.
	 *
	 * @param nome
	 * @param papel
	 * @param unidade
	 * @param hierarquia
	 *            id da unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByNome(String nome, UnidadeGeral unidade,
			int hierarquia) throws DAOException;

	/**
	 * Busca usuário pelo nome, unidade e papel e hierarquia de sua unidade.
	 *
	 * @param nome
	 * @param unidade
	 * @param hierarquia
	 * @param apenasAtivos
	 * @param apenasAutorizados
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByNome(String nome, UnidadeGeral unidade,
			int hierarquia, boolean apenasAtivos, boolean apenasAutorizados)
			throws DAOException;

	/**
	 * Busca usuário pelo papel, unidade e hierarquia de sua unidade.
	 *
	 * @param papel
	 * @param unidade
	 * @param hierarquia
	 *            id da unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByPapel(Papel papel, UnidadeGeral unidade,
			int hierarquia,boolean ativo) throws DAOException;

	/**
	 * Busca usuário pelo papel, unidade e hierarquia de sua unidade.
	 *
	 * @param papel
	 * @param unidade
	 * @param gestora
	 * @param hierarquia
	 *            id da unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByPapel(Papel papel, UnidadeGeral unidade,
			UnidadeGeral gestora, int hierarquia,boolean ativos) throws DAOException;

	/**
	 * Procura usuários pela sua Unidade
	 *
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByUnidade(UnidadeGeral unidade)
			throws DAOException;

	/**
	 * Procura usuários pela sua Unidade
	 *
	 * @param unidade
	 * @param apenasAtivos
	 * @param apenasAutorizados
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByUnidade(UnidadeGeral unidade,
			boolean apenasAtivos, boolean apenasAutorizados)
			throws DAOException;

	/**
	 * Busca usuários pertencentes às unidades na hierarquia da unidade
	 * informada. Unidade null, busca todos os usuários.
	 *
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findAllByHierarquia(UnidadeGeral unidade)
			throws DAOException;

	/**
	 * Busca usuários pertencentes às unidades na hierarquia da unidade
	 * informada. Unidade null, busca todos os usuários.
	 *
	 * @param unidade
	 * @param apenasAutorizados
	 * @param apenasAtivos
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findAllByHierarquia(UnidadeGeral unidade,
			boolean apenasAtivos, boolean apenasAutorizados)
			throws DAOException;
	/**
	 * Busca os usuários por nome, login, unidade e ativos ou não.
	 * 
	 * @param unidade
	 * @param nome
	 * @param login
	 * @param apenasAtivos
	 * @param apenasAutorizados
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByNomeLogin(UnidadeGeral unidade, String nome, String login,
			boolean apenasAtivos, boolean apenasAutorizados)throws DAOException;

	public Collection<UsuarioGeral> findPendenteAutorizacao(boolean ativo) throws DAOException;

	/**
	 * Busca todos os usuários ativos
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findAllAtivos() throws DAOException;

	/**
	 * Atualiza data de expirar senha para data atual.
	 *
	 * @param usuario
	 */
	public void expiraSenha(UsuarioGeral usuario) throws DAOException;

	/**
	 * Busca todos os usuário que já solicitaram em determinado registro de
	 * preços
	 *
	 * @param idProcessoCompra
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findByRegistroPreco(int idProcessoCompra)
			throws DAOException;

	/**
	 * Busca todos os usu‡rios do sipac trazendo apenas os ativos.
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findBySipac() throws DAOException ;

	/**
	 * Busca usuarios pelo id.
	 * @param idUsuario
	 * @return
	 * @throws DAOException
	 */
	public UsuarioGeral findById(int idUsuario) throws DAOException ;

	/**
	 * faz a busca de usuario na base do sigaa.
	 *
	 * @param login
	 * @return
	 * @throws DAOException
	 */
	public Object[] findByLoginOnSigaa(String login) throws DAOException ;

	/**
	 * faz busca do usuario pelo login e ainda traz a matricula siape
	 * @param login
	 * @return
	 * @throws DAOException
	 */
	public Object[] findByLoginServidor(String login) throws DAOException ;
	
	/**
	 * Busca os servidores  ativos a partir de um pessoa.
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<UsuarioGeral> findServidorByPessoa(PessoaGeral pessoa) throws DAOException ;

	/**
	 * Busca um usuário de acordo com o id da pessoa passado como parâmetro.
	 * @param idPessoa
	 * @return
	 * @throws DAOException 
	 */
	public List<UsuarioGeral> findByPessoa(int idPessoa) throws DAOException;
	

}