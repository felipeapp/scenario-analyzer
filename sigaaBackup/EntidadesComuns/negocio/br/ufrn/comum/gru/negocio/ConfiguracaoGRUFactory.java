/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 18/05/2011
 *
 */
package br.ufrn.comum.gru.negocio;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.gru.dominio.ConfiguracaoGRU;
import br.ufrn.comum.gru.dominio.GrupoEmissaoGRU;

public class ConfiguracaoGRUFactory {
	
	/** Cria uma instância deste factory. */
	protected static ConfiguracaoGRUFactory instance = new ConfiguracaoGRUFactory();
	
	/** Construtor Padrão privado. */
	private ConfiguracaoGRUFactory() {
	}
	
	/** Retorna a instância deste factory.
	 * @return
	 */
	public static ConfiguracaoGRUFactory getInstance() {
		return instance;
	}

	/**
	 * Recupera uma lista de grupos de emissão de GRU.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoEmissaoGRU> getGruposEmissaoGRU() throws DAOException { 
		Collection<GrupoEmissaoGRU> lista;
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			Criteria q = dao.getSession().createCriteria(GrupoEmissaoGRU.class)
					.add(Restrictions.eq("ativo", true));
			q.addOrder(Order.asc("id"));
			@SuppressWarnings("unchecked")
			Collection<GrupoEmissaoGRU> l = q.list();
			lista = l;
		} finally {
			dao.close();
		}
   	 	return lista;
	}
	
	/**
	 * Recupera uma lista de configurações de GRU para um determinado tipo de
	 * arrecadação. Caso seja especificado um tipo de arrecadação nulo (id =
	 * zero), será retornado todas configurações ativas.
	 * 
	 * @param idTipoArrecadacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<ConfiguracaoGRU> getConfiguracoesGRUByTipoArrecadacao(int idTipoArrecadacao) throws DAOException {
		Collection<ConfiguracaoGRU> lista;
   	 GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
   	 try {
   		 Criteria q = dao.getSession().createCriteria(ConfiguracaoGRU.class)
   		 .add(Restrictions.eq("ativo", true));
   		 if (idTipoArrecadacao > 0)
   			 q.add(Restrictions.eq("tipoArrecadacao.id", idTipoArrecadacao));
   		 q.addOrder(Order.asc("descricao"));
   		 @SuppressWarnings("unchecked")
   		 Collection<ConfiguracaoGRU> l = q.list();
   		 lista = l;
   	 } finally {
   		 dao.close();
   	 }
   	 return lista;
	}
	
	/** Recupera do banco de dados uma GRU com o ID especificado.
	 * @param idConfiguracaoGRU
	 * @return
	 * @throws DAOException
	 */
	public ConfiguracaoGRU getConfiguracaoGRUById(int idConfiguracaoGRU) throws DAOException {
		ConfiguracaoGRU config;
   	 GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
   	 try {
   		 Criteria q = dao.getSession().createCriteria(ConfiguracaoGRU.class)
   		 .add(Restrictions.eq("id", idConfiguracaoGRU));
   		 config = (ConfiguracaoGRU) q.uniqueResult();
   	 } finally {
   		 dao.close();
   	 }
   	 return config;
	}
	
	/** Cria (persiste) ou atualiza uma Configuração de GRU. 
	 * @param configuracaoGRU
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public ConfiguracaoGRU createOrUpdate(ConfiguracaoGRU configuracaoGRU) throws DAOException, NegocioException {
		ListaMensagens lista = configuracaoGRU.validate();
		if (lista.isErrorPresent())
			throw new NegocioException(lista);
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			dao.createOrUpdate(configuracaoGRU);
		} finally {
			dao.close();
		}
		return configuracaoGRU;
	}
}
