/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '17/11/2006'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;


/**
 * Classe responsável por realizar operações relacionadas ao banco de dados
 * para todas as orientações de projetos de monitoria. 
 * 
 * 
 * @author ilueny santos.
 *
 */
public class OrientacaoDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as orientações os projetos de monitoria do docente logado.
	 *
	 * @param servidor Servidor logado
	 * @param coordenador true se coordenador
	 * @return lista de orientações
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	@SuppressWarnings("unchecked")
	public int findOrienatacoesAtivasByEquipeDocente(EquipeDocente equipeDocente, Date inicio, Date fim) throws DAOException {
		String hql = "select count(*) from Orientacao o " +
		"where o.equipeDocente.id = :idEquipeDocente " +
		"and o.ativo = trueValue() and o.dataInicio >= :inicio and o.dataFim <= :fim ";
		Query q = getSession().createQuery(hql);
		q.setInteger("idEquipeDocente", equipeDocente.getId());
		q.setDate("inicio", inicio);
		q.setDate("fim", fim);
		return ((Long)q.uniqueResult()).intValue();
	}
	
	
	
	
	/**
	 * Retorna todas as orientações os projetos  de monitoria do Docent logado.
	 *
	 * @param servidor Servidor logado
	 * @param coordenador true se coordenador
	 * @return lista de orientações
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	@SuppressWarnings("unchecked")
	public Collection<Orientacao> findByServidor(Servidor servidor, Boolean coordenador) throws DAOException {

		if (servidor != null && servidor.getId() != 0) {

			Criteria c = getSession().createCriteria(Orientacao.class);				
			Criteria subC = c.createCriteria("equipeDocente");
				
			if (coordenador != null)
				subC.add(Expression.eq("coordenador", true));
			
			
			subC.createCriteria("servidor").add(Expression.eq("id", servidor.getId()));
			
			return c.list();
			
		}else{
			return null;
		}

	}

	/**
	 * Retorna todas as orientações do projeto de monitoria informado
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	@SuppressWarnings("unchecked")
	public Collection<Orientacao> findByProjetoMonitoria(ProjetoEnsino projeto)
			throws DAOException {

		if (projeto != null && projeto.getId() != 0) {
			
			Criteria c = getSession().createCriteria(Orientacao.class);
			c.createCriteria("equipeDocente").createCriteria("projetoEnsino").add(Expression.eq("id", projeto.getId()));

			return c.list();
			
		} else {
			return null;
		}

	}
	

	/**
	 * Retorna todas as orientações os projetos do Monitor logado
	 *
	 * @param discente Discente logado
	 * @return lista de orientações
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	@SuppressWarnings("unchecked")
	public Collection<Orientacao> findByDiscente(Discente discente) throws DAOException {

		if (discente != null && discente.getId() != 0) {

			Criteria c = getSession().createCriteria(Orientacao.class);
			Criteria subC = c.createCriteria("discenteMonitoria");
			subC.createCriteria("discente").add(Expression.eq("id", discente.getId()));			
			c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			return c.list();
			
		}else{
			return null;
		}

	}
	
}