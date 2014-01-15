/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/08/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParticipacaoAcaoExtensao;

public class TipoParticipacaoAcaoExtensaoDao extends GenericSigaaDAO {

	/**
	 * Retorna lista de TipoParticipacaoAcaoExtensao quando ativo e ordenado pela descriÇÃo do tipoaAcaoExrtensao. 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<TipoParticipacaoAcaoExtensao> findAllTipoParticipacaoAtivos() throws DAOException{
		return getSession().createQuery("select t from TipoParticipacaoAcaoExtensao t where ativo = trueValue() ORDER BY  t.descricao").list();
	}
	
	
	/**
	 * Retorna lista de TipoParticipacaoAcaoExtensao que estão associados a algum TipoAtividadeExtensao, 
	 * esses são os tipos de participação que podem ser alterados pelo sistema.
	 *  
	 * @param TipoAtividadeExtensao tipo
	 * @return List<TipoParticipacaoAcaoExtensao>
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<TipoParticipacaoAcaoExtensao> findAllTipoParticipacaoAtivosExclusivoTipoAcaoExtensao(TipoAtividadeExtensao tipo) throws DAOException{
		
		Query q = getSession().createQuery("select t from TipoParticipacaoAcaoExtensao t " 
				+" WHERE t.tipoAcaoExtensao.id = :idtipo AND t.ativo = :true ORDER BY  t.descricao");
		
		q.setInteger("idtipo", tipo.getId());
		q.setBoolean("true", true);
		
		return q.list();
	}
	
	/**
	 * <p>Retorna lista de TipoParticipacaoAcaoExtensao que são fixos no sistema, são fixos aqueles que não estão associado a nenhuma tipo de atividade.</p> 
	 * <p>Esses tipos de participação não podem ser alterados.</p>
	 *  
	 * @param TipoAtividadeExtensao tipo
	 * @return List<TipoParticipacaoAcaoExtensao>
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<TipoParticipacaoAcaoExtensao> findAllTipoParticipacaoAtivosFixosDoSistema() throws DAOException{
		Query q = getSession().createQuery("select t from TipoParticipacaoAcaoExtensao t where t.tipoAcaoExtensao is null AND t.ativo = :true ORDER BY t.descricao");
		q.setBoolean("true", true);
		
		return q.list();
	}
	
}
