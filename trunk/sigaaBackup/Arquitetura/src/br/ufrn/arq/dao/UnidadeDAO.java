/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 15/09/2004
 */
package br.ufrn.arq.dao;

import java.util.Collection;


import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;


/**
 * DAO de unidade. Implementa os métodos de busca relacionados com unidade.
 *
 * @author Gleydson Lima
 *
 */
public interface UnidadeDAO extends GenericDAO, PaginableDAO {

	
		
	/**
	 * Procura pelas unidades filhas de uma unidade pai. Caso os parâmetros sejam passados busca somente
     * as de uma determinada categoria, orçamentária e/ou tipo (Fato ou Direito)
     *
	 * @param pai
	 * @param categoria
	 * @param tipo
	 *
	 * @return
	 * @throws DAOException
	 */
    public Collection<UnidadeGeral> findBySubUnidades(UnidadeGeral pai, int categoria, boolean orcamentaria, int tipo) throws DAOException;

    /**
     * Busca as unidades que podem requisitar material a um almoxarifado
     * @param status
     * @param unidade
     * @return
     * @throws Exception
     */
    public Collection<UnidadeGeral> findByUnidadeRequisitante(UnidadeGeral unidade)	throws DAOException;

    /**
     * Procura as unidades pelo nome
     *
     * @param nome
     * @return @throws
     *         DAOException
     */
    public Collection<UnidadeGeral> findByNome(String nome) throws DAOException;

    /**
     * Procura as unidades pelo nome e pela hierarquia
     * @param nome
     * @param hierarquia código da unidade ou -1 para nenhuma
     * @return
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findByNome(String nome, int hierarquia)
			throws DAOException;


    /**
     * Busca por todas as unidades de custo
     *
     * @return @throws
     *         DAOException
     */
    public Collection<UnidadeGeral> findAllUnidadeOrcamentaria() throws DAOException;

    /**
     * Busca todas as unidades de direito (gestoras)
     * @return
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findAllUnidadeDireito(int categoria) throws DAOException;

   /**

    /**
     * Busca uma Unidade pelo código
     *
     * @param codigo
     * @return @throws
     *         DAOException
     */
    public UnidadeGeral findByCodigo(long codigo) throws DAOException;

    /**
     * Retorna o id da unidade com o nome exatamente igual ao passado como
     * parâmetro ou 0 caso nenhuma unidade for encontrada.
     *
     * @param nome
     *            nome da unidade pesquisada.
     * @return id da unidade ou 0 se unidade não for encontrada.
     * @throws DAOException
     */
    public int nomeExists(String nome) throws DAOException;

    /**
     * Retorna todas as unidades pertencentes a categoria informada
     *
     * @param categoria
     *            categoria desejada
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findByCategoria(int categoria) throws DAOException;

    /**
     * Retorna uma coleção de objetos unidades com Id, Nome e Nome Abreviado
     * @return
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findAllIdNome() throws DAOException;

    /**
     * Busca todas as unidades que são filhas da unidade especificada.
     * Filhas diretas ou indiretas.
     * @return
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findByHierarquia(int unidade,Boolean orcamentaria) throws DAOException;

    /**
     * Retorna uma coleção de objetos unidades com Id, Nome e Nome Abreviado
     * contendo todas as unidades que são filhas da unidade especificada. Filhas
     * diretas ou indiretas.
     *
     * @param unidade id da unidade
     * @return @throws
     *         DAOException
     */
    public Collection<UnidadeGeral> findAllIdNomeByHierarquia(int unidade) throws DAOException;

    /**
     * Retorna uma coleção de objetos unidades com Id, Código Nome e Nome Abreviado
     * contendo todas as unidades de direito do SIPAC.
     *
     * @return @throws
     *         DAOException
     */
    public Collection<UnidadeGeral> findAllIdNomeByRaiz() throws DAOException;

    /**
     * Retorna uma coleção de unidades buscando pelo código da unidade
     * da hierarquia.
     * @param codigoUnidade
     * @return
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findAllByCodigoHierarquia(int codigoUnidade) throws DAOException;

    /**
     * Busca unidades para a exibição na tag de unidade
     * @return
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findAllTagUnidade() throws DAOException;

    /**
     * busca todas unidades associadas a um usuário.
     *
     * @param usuario
     * @return
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findUnidadesByUsuario(UsuarioGeral usuario)throws DAOException;

    /**
     * Retorna filhas organizacionais
     * @param idUnidade
     * @return
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findFilhasOrganizacionais(int idUnidade, boolean organizacional) throws DAOException;

    /**
     * Busca todas as unidades sem ser os centros ou hospitais.
     * @param pai
     * @param categoria
     * @param orcamentaria
     * @param tipo
     * @return
     * @throws DAOException
     */
    public Collection<UnidadeGeral> findByUnidadesFilhas(int categoria,int tipo) throws DAOException;

    /**
	* Método que busca todas as unidades com permissão para comprar
	*/
    public Collection<UnidadeGeral> findAllCompradoras() throws DAOException ;
    
	/**
	 * Busca todas unidades que possuem transportes
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findUnidadesByTransportes(int codigoUnidade) throws DAOException;


	/**
	 * Busca as unidade organizacionais.
	 */
	public Collection<UnidadeGeral> findAllUnidadesOrganizacionais() throws DAOException;
	
}