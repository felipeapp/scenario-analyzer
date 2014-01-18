/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.dao;

import java.util.HashMap;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.dominio.SolicitacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.StatusSolicitacaoDocenteRede;

/**
 * Dao responsável por consultas específicas as solicitações de docentes de ensino em rede.
 * @author Diego Jácome
 */
public class SolicitacaoDocenteRedeDao  extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public HashMap<CampusIes,Integer> countNumSolicitacoesCampusByPrograma ( ProgramaRede programa ) throws HibernateException, DAOException {
		
		String sql = " select c.id_campus , c.nome , i.sigla , " +
						" ( "+
						" 	select count(*) from ensino_rede.solicitacao_docente_rede s "+
						" 	inner join ensino_rede.docente_rede dr on s.id_docente_rede = dr.id_docente_rede "+
						" 	inner join ensino_rede.dados_curso_rede dc on dc.id_dados_curso_rede = dr.id_dados_curso_rede "+
						" 	where dc.id_campus = c.id_campus and s.status = "+StatusSolicitacaoDocenteRede.AGUARDANDO_ANALISE+
						" ) as num_sol "+
						" from comum.campus_ies c "+
						" inner join comum.instituicoes_ensino i on i.id = c.id_ies "+
						" order by i.sigla , c.nome";
		
		List <Object []> result = getSession().createSQLQuery (sql).list();
		HashMap<CampusIes,Integer> hash = new HashMap<CampusIes,Integer>();
		
		for (Object [] linha : result) {
			
			int i = 0;
			CampusIes c = new CampusIes();
			
			c.setId((Integer)linha[i++]);
			c.setNome((String)linha[i++]);
			c.setInstituicao(new InstituicoesEnsino());
			c.getInstituicao().setSigla((String)linha[i++]);
			Integer numSol = ((Number)linha[i++]).intValue();
			
			if (numSol > 0)
				hash.put(c, numSol);
		}	
		
		return hash;
	}

	@SuppressWarnings("unchecked")
	public List<SolicitacaoDocenteRede> findSolicitacoesByCampusPrograma ( Integer idCampus , Integer idPrograma ) throws HibernateException, DAOException {
		
		String hql = " select s from SolicitacaoDocenteRede s "+
					 " join fetch s.usuario u "+
					 " join fetch u.pessoa coord "+
					 " join fetch s.docente dr "+
					 " join fetch dr.pessoa p "+
					 " join fetch dr.dadosCurso dc "+
					 " join dc.campus c "+
					 " where dc.programaRede.id = "+idPrograma+ " and c.id = "+idCampus+ " and s.status.id = "+StatusSolicitacaoDocenteRede.AGUARDANDO_ANALISE+
					 " order by s.dataSolicitacao , p.nome ";
		
		Query q = getSession().createQuery(hql.toString());
		
		return q.list();
	}
	
	public Integer countSolicitacoesByPrograma ( ProgramaRede programa ) throws HibernateException, DAOException {
		
		String hql = " select count(s) from SolicitacaoDocenteRede s "+
					 " join s.docente dr "+
					 " join dr.dadosCurso dc "+
					 " where dc.programaRede.id = "+programa.getId()+" and s.status.id = "+StatusSolicitacaoDocenteRede.AGUARDANDO_ANALISE;
		
		Query q = getSession().createQuery(hql.toString());
		q.setMaxResults(1);
		Integer res = ((Number) q.uniqueResult()).intValue();
		
		return res;
	}
	
}
