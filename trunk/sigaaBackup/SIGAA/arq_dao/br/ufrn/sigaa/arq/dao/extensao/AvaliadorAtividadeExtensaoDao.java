/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '21/02/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AvaliadorAtividadeExtensao;

/***
 * Classe respons�vel por realizar opera��es de busca dos avaliadores de a��es de extens�o.
 * 
 * @author Ilueny Santos
 *
 */
public class AvaliadorAtividadeExtensaoDao extends GenericSigaaDAO {

	
	/**
	 * Informa se o servidor j� est� cadastrado como avaliador de extens�o
	 *
	 * @param idServidor
	 * @param comissao
	 * @return
	 * @throws DAOException
	 */
	public boolean isAvaliadorCadastrado(int idServidor) throws DAOException {
		int count = getJdbcTemplate().queryForInt("select count(a.id_avaliador_atividade_extensao) from extensao.avaliador_atividade_extensao a where a.id_servidor = ? and a.ativo = trueValue() ", new Object[] { idServidor });
		return count > 0;
	}

	
	
	/**
	 * Retorna todos os Avaliadores de determinada �rea tem�tica
	 * 
	 * @param areaTematica
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliadorAtividadeExtensao> findByAreaTematica(Integer  idAreaTematica) throws DAOException {

		String projecao = "av.id, av.dataInicio, av.dataFim, av.ativo, av.servidor.id, av.servidor.pessoa.id, av.servidor.siape, av.servidor.pessoa.nome, av.areaTematica.descricao";
		String hqlQuery = "select " + projecao + " from AvaliadorAtividadeExtensao av where av.ativo = trueValue() ";		
		if (idAreaTematica != null) {
			hqlQuery += "and av.areaTematica.id = :idArea";
		} 

		hqlQuery += " order by av.servidor.pessoa.nome asc ";
		Query query = getSession().createQuery(hqlQuery);
		
		if (idAreaTematica != null) {
			query.setInteger("idArea", idAreaTematica);
		}
		
		return HibernateUtils.parseTo(query.list(), projecao, AvaliadorAtividadeExtensao.class, "av");		
	}

	
	
}