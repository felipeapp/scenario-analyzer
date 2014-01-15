/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 16/02/2009
 */
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/** 
 * Dao para realizar consultas sobre a entidade HomologacaoTrabalhoFinal
 * @author Victor Hugo
 */
public class HomologacaoTrabalhoFinalDao extends GenericSigaaDAO {

	/**
	 * Busca as Solicitações de homologação dos discentes que estejam com a situação informada e da unidade informada
	 * @param idUnidade unidade que o discente deve pertencer
	 * @param statusDiscente status do discente que deve filtrar as solicitações de homologação
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<HomologacaoTrabalhoFinal> findByUnidadeStatusDiscente( int idUnidade, int statusDiscente ) throws DAOException{

		StringBuilder hql = new StringBuilder( " SELECT htf FROM HomologacaoTrabalhoFinal htf " );
		hql.append( " WHERE htf.banca.dadosDefesa.discente.discente.gestoraAcademica.id = :idUnidade " );
		hql.append( " AND htf.banca.dadosDefesa.discente.discente.status = :status " );
		hql.append( " ORDER BY htf.banca.dadosDefesa.discente.discente.status, htf.anoProcesso, htf.banca.dadosDefesa.discente.discente.pessoa.nome " );
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idUnidade", idUnidade);
		q.setInteger("status", statusDiscente);
		
		return q.list();
	}
	
	/** Retorna a última homologação de trabalho final do discente especificado.
	 * @param idDiscente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public HomologacaoTrabalhoFinal findUltimoByDiscente(int idDiscente) throws HibernateException, DAOException {
		Criteria c = getSession().createCriteria(HomologacaoTrabalhoFinal.class)
		.createCriteria("banca").addOrder(Order.desc("data"))
		.createCriteria("dadosDefesa")
		.createCriteria("discente")
		.add(Restrictions.eq("id", idDiscente));
		return (HomologacaoTrabalhoFinal) c.setMaxResults(1).uniqueResult();
	}
	
	/**
	 * Retorna as homologações dos trabalhos finais dos alunos
	 * @param discentes
	 * @return
	 * @throws DAOException 
	 * @throws DAOException
	 */
	public HashMap<Discente,Collection<HomologacaoTrabalhoFinal>> findByDiscentes(Collection<Discente> discentes) throws DAOException  {

		try{
			
			String sql = " select d.id_discente , h.id , h.criado_em "+
							" from stricto_sensu.homologacao_trabalho_final h  "+
							" join stricto_sensu.banca_pos b on h.id_banca = b.id_banca_pos "+
							" join stricto_sensu.dados_defesa d on b.id_dados_defesa = d.id_dados_defesa "+
							" where d.id_discente in "+UFRNUtils.gerarStringIn(discentes)+" "+
							" order by d.id_discente , h.data desc ";
			
			Query  q = getSession().createSQLQuery(sql);
			Integer oldIdDiscente = null;
			Integer newIdDiscente = null;
			HashMap<Discente,Collection<HomologacaoTrabalhoFinal>> map = new HashMap<Discente,Collection<HomologacaoTrabalhoFinal>>();
			Collection<HomologacaoTrabalhoFinal> trabalhos = new ArrayList<HomologacaoTrabalhoFinal>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();		
					
			if ( result != null )
			{	
				for (Object[] linha : result) {
				
					Integer i = 0;
					HomologacaoTrabalhoFinal t = new HomologacaoTrabalhoFinal();
					newIdDiscente = (Integer)linha[i++];
					
					if ( !newIdDiscente.equals(oldIdDiscente) ){
						
						Discente d = new Discente();
						trabalhos = new ArrayList<HomologacaoTrabalhoFinal>();
						
						d.setId(newIdDiscente);
						t.setId((Integer)linha[i++]);
						t.setCriadoEm((Date)linha[i++]);
						trabalhos.add(t);
						map.put(d,trabalhos);
					} else if (newIdDiscente.equals(oldIdDiscente)){
						t.setId((Integer)linha[i++]);
						t.setCriadoEm((Date)linha[i++]);
						trabalhos.add(t);
					}
				
					oldIdDiscente = newIdDiscente;
				}	
			}
			
			return map;

		}catch (Exception e) {
			throw new DAOException(e);
		}	

	}
}
