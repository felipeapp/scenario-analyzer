/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.comum.dao;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.ConfiguracaoOperacaoSiged;

/**
 * DAO para buscas relacionadas à entidade de Configurações das Operações do SIGED.
 * 
 * @author Adriana Alves
 *
 */
public class ConfiguracaoOperacaoSigedDAO extends GenericSharedDBDao {

	/**
	 * Consulta pela operação de configuração do SIGED de acordo com a operação informada.
	 * 
	 * @param operacao
	 * @return
	 * @throws DAOException 
	 */
	public ConfiguracaoOperacaoSiged findConfiguracoesByOperacao(String operacao) throws DAOException {
		return (ConfiguracaoOperacaoSiged) getSession().createQuery("from ConfiguracaoOperacaoSiged where nomeOperacao = :operacao").setString("operacao",operacao).uniqueResult();
	}
		
}
