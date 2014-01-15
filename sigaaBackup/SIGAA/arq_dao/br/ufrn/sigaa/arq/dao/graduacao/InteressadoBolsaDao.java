/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '31/03/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.InteressadoBolsa;

/**
 * DAO que gerencia os registros de interesse dos alunos em pesquisa, extensão, monitoria e apoio técnico
 * 
 * @author Henrique André
 *
 */
public class InteressadoBolsaDao extends GenericSigaaDAO {

	/**
	 * Busca todos os registros de interesse que um aluno cadastrou
	 * 
	 * @return
	 * @throws DAOException
	 */	
	public List<InteressadoBolsa> findAllByDiscente(int idDiscente)	throws DAOException {
		Criteria c = getSession().createCriteria(InteressadoBolsa.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("ativo", Boolean.TRUE));

		@SuppressWarnings("unchecked")
		List<InteressadoBolsa> lista = c.list();
		
		return lista;
	}
	
	/**
	 * Traz o registro de interesse em bolsa de um discente.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public InteressadoBolsa findDiscente(int idDiscente)	throws DAOException {
		Criteria c = getSession().createCriteria(InteressadoBolsa.class);
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("ativo", Boolean.TRUE));

		
		
		return (InteressadoBolsa) c.uniqueResult() ;
	}
	
	/**
	 * Traz o registro de interesse em bolsa de um discente.
	 * 
	 * @param idOportunidade
	 * @param tipoBolsa
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public InteressadoBolsa findByDiscente(Integer idOportunidade, Integer tipoBolsa, Integer idDiscente) throws DAOException {
		Criteria c = getSession().createCriteria(InteressadoBolsa.class);
		c.add(Restrictions.eq("idEstagio", idOportunidade));
		c.add(Restrictions.eq("tipoBolsa.id", tipoBolsa));
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		
		return (InteressadoBolsa) c.uniqueResult();
	}
	
	/**
	 * Retorna o nome e email de todos os interessados de um plano de trabalho informado
	 * @param idOportunidade
	 * @return
	 */
	public List<Map<String, Object>> findEmailInteressadosByOportunidade(Integer idOportunidade){
		StringBuilder sqlconsulta = new StringBuilder(				
			 " select distinct u.id_usuario, p.nome, u.email, d.id_discente, d.matricula from graduacao.interessado_bolsa i "
				+" inner join discente d on (d.id_discente = i.id_discente) "
				+" inner join comum.pessoa p on (p.id_pessoa = d.id_pessoa) "				
				+" inner join comum.usuario u on (u.id_pessoa = p.id_pessoa) "


		    +" where id_oportunidade = "+idOportunidade
			+" order by p.nome ");

		@SuppressWarnings("unchecked")
		List <Map<String, Object>> result = getJdbcTemplate().queryForList(sqlconsulta.toString());			

		return result;
	}
	
}
