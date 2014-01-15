package br.ufrn.sigaa.ouvidoria.dao;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ouvidoria.dominio.DelegacaoUsuarioResposta;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Dao responsável por consultas realizadas em {@link DelegacaoUsuarioResposta}
 * 
 * @author Bernardo
 *
 */
public class DelegacaoUsuarioRespostaDao extends GenericSigaaDAO {

	/**
	 * Retorna uma coleção contendo todas as delegações feitas em uma determinada manifestação.
	 * 
	 * @param manifestacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<DelegacaoUsuarioResposta> findAllDelegacoesByManifestacao(Integer manifestacao) throws DAOException {
		String hql = "select d " +
						"from DelegacaoUsuarioResposta d " +
							"inner join d.historicoManifestacao h " +
							"inner join h.manifestacao m " +
						"where m.id = :manifestacao " +
						"order by d.dataCadastro ";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("manifestacao", manifestacao);
		
		@SuppressWarnings("unchecked")
		Collection<DelegacaoUsuarioResposta> list = q.list();
		
		return list;
	}
	
	/**
	 * Retorna uma coleção contendo todas as delegações associadas a pessoa.
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public Long countDelegacoesByPessoa(Integer pessoa) throws DAOException {
		String hql = "select count(d.id) " +
						"from DelegacaoUsuarioResposta d " +
							"inner join d.pessoa p " +
						"where p.id = :pessoa " +
							"and d.ativo = true ";
		
		Query q = getSession().createQuery(hql);
		
		q.setInteger("pessoa", pessoa);
		
		return (Long) q.uniqueResult();
	}
	
	/**
	 * Método reponsável por busca os usuário por nome que sejam de uma determinada unidade. 
	 * @param nome
	 * @param unidade
	 * @param somenteAtivos
	 * @return Collection<Pessoa>
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Pessoa> findByNome(String nome, Unidade unidade, boolean somenteAtivos) throws DAOException {
	
		String sql = "select p.id_pessoa, p.nome from comum.usuario u " +
			  "join comum.pessoa p using (id_pessoa) " +
		      "left join rh.servidor s using (id_pessoa) " +
		      "left join comum.unidade un on un.id_unidade = s.id_unidade_lotacao " +
		      "where u.inativo = false " +
		      "and p.nome ilike :nome " +
		      "and ((u.id_servidor is null and u.id_unidade = :unidade) or " +
		      "    (s.id_unidade_lotacao = :unidade and s.id_ativo = 1) or " +
		      "    (un.hierarquia_organizacional like '%.'||:unidade||'.%' and s.id_ativo = 1)) " +
		      "order by p.nome"; 
		
		
		Query q = getSession().createSQLQuery(sql);

		q.setString("nome", nome.toUpperCase() + "%");
		q.setInteger("unidade", unidade.getId());
		List<Object[]> result = q.list();
		Collection<Pessoa> pessoas = new ArrayList<Pessoa>();
		for (Object obj[]:result) {
			int i = 0;
			Pessoa p = new Pessoa();
			p.setId((Integer) obj[i++]);
			p.setNome((String) obj[i++]);
			pessoas.add(p);
		}
		
		return pessoas;
	}
}
