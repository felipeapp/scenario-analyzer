package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.CronogramaExecucaoAulas;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;


/**
 * Dao com consultas sobre o acompanhamento semanal discente
 * 
 * @author Rafael Barros
 * @author Rafael Silva
 * 
 */
public class AcompanhamentoSemanalDiscenteDao extends GenericSigaaDAO {
	
	/**
	 * Retorna a listagem dos Acompanhamentos Semanais a partir da Turma de Entrada 
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoSemanalDiscente> findAcompanhamentosByTurmaEntrada(int idTurmaEntrada) throws DAOException{
		try {
			
			TurmaEntradaTecnico turma = null;
			turma = findByPrimaryKey(idTurmaEntrada, TurmaEntradaTecnico.class);			
			
			Criteria cAcomp = getSession().createCriteria(AcompanhamentoSemanalDiscente.class);
			Criteria cPeriodo = cAcomp.createCriteria("periodoAvaliacao");
			Criteria cCrono = cPeriodo.createCriteria("cronogramaExecucaoAulas");
			
			cCrono.add(Expression.eq("id", turma.getDadosTurmaIMD().getCronograma().getId()));	
			cAcomp.addOrder(org.hibernate.criterion.Order.asc("id"));
			
			Collection<AcompanhamentoSemanalDiscente> listaAcompanhamentos = null;
			
			if(cAcomp.list() != null)
			{
				listaAcompanhamentos = cAcomp.list();
			} else {
				listaAcompanhamentos = Collections.emptyList();
			}
			
			return listaAcompanhamentos;
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	
	/**
	 * Retorna a listagem dos Acompanhamentos Semanais a partir da Turma de Entrada por uma consulta projetada
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoSemanalDiscente> findAcompanhamentosByTurmaEntradaProjetado(int idTurmaEntrada) throws DAOException{
			
			String projecao = "id, discente.id, discente.discente.matricula, discente.discente.pessoa.nome, participacaoPresencial, frequencia, " +
					"participacaoVirtual, pvSincronizada, periodoAvaliacao.id, " +
					"periodoAvaliacao.numeroPeriodo, periodoAvaliacao.dataInicio, periodoAvaliacao.datafim, periodoAvaliacao.codigoIntegracao, periodoAvaliacao.chTotalPeriodo";
			
			String hql = "select " + projecao +  " from AcompanhamentoSemanalDiscente  where " +
					"  discente.turmaEntradaTecnico.id = " + idTurmaEntrada +" and (discente.discente.status= "+ StatusDiscente.ATIVO +")";
			
			hql = hql + " order by nome, periodoAvaliacao.id";
			
			Query q = getSession().createQuery(hql);

			return HibernateUtils.parseTo(q.list(), projecao, AcompanhamentoSemanalDiscente.class);
			
	
	}
	
	/**
	 * Retorna a listagem dos Acompanhamentos Semanais a partir da Turma de Entrada por uma consulta projetada ordenando pelo nome discentes
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoSemanalDiscente> findAcompanhamentosByTurmaEntradaProjetadoOrdenado(int idTurmaEntrada) throws DAOException{
			
			String projecao = "id, discente.id, discente.discente.matricula, discente.discente.pessoa.nome, participacaoPresencial, frequencia, " +
					"participacaoVirtual, pvSincronizada, periodoAvaliacao.id, " +
					"periodoAvaliacao.numeroPeriodo, periodoAvaliacao.dataInicio, periodoAvaliacao.datafim, periodoAvaliacao.codigoIntegracao, periodoAvaliacao.chTotalPeriodo";
			
			String hql = "select " + projecao +  " from AcompanhamentoSemanalDiscente  where " +
					"  discente.turmaEntradaTecnico.id = " + idTurmaEntrada +" and (discente.discente.status= "+ StatusDiscente.ATIVO +")";
			
			hql = hql + " order by discente.discente.pessoa.nome";
			
			Query q = getSession().createQuery(hql);

			return HibernateUtils.parseTo(q.list(), projecao, AcompanhamentoSemanalDiscente.class);
			
	
	}
	
	
	/**
	 * Retorna a listagem dos Acompanhamentos Semanais a partir de uma lista de turmas de entrada por uma consulta projetada ordenando pelo nome discentes
	 * 
	 * @param Collection<TurmaEntradaTecnico> listaTurmas
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoSemanalDiscente> findAcompanhamentosByListaTurmasProjetadoOrdenado(Collection<TurmaEntradaTecnico> listaTurmas) throws DAOException{
		   
		   String projecao = "id, discente.id, discente.discente.matricula, discente.discente.pessoa.nome, discente.turmaEntradaTecnico.id, participacaoPresencial, frequencia,  " +
		     "participacaoVirtual, periodoAvaliacao.id, discente.turmaEntradaTecnico.especializacao.descricao, " +
		     "periodoAvaliacao.numeroPeriodo, periodoAvaliacao.dataInicio, periodoAvaliacao.datafim, periodoAvaliacao.codigoIntegracao, periodoAvaliacao.chTotalPeriodo";
		   
		   String hql = "SELECT " + projecao +  " FROM AcompanhamentoSemanalDiscente  WHERE ";

		   int contador = 0;
		   for(TurmaEntradaTecnico turma: listaTurmas){ 
		    if(contador == 0){
		     hql += "(discente.turmaEntradaTecnico.id = " + turma.getId() +" AND discente.discente.status= "+ StatusDiscente.ATIVO +")";
		    } else {
		     hql += " OR (discente.turmaEntradaTecnico.id = " + turma.getId() +" AND discente.discente.status= "+ StatusDiscente.ATIVO + ")";
		    }
		    contador++;
		   }
		   
		   hql = hql + " ORDER BY discente.turmaEntradaTecnico.especializacao.descricao, discente.discente.pessoa.nome";
		   
		   Query q = getSession().createQuery(hql);

		   return HibernateUtils.parseTo(q.list(), projecao, AcompanhamentoSemanalDiscente.class);
		   
		 
		 }
	
	/**
	 * Retorna a listagem dos Acompanhamentos Semanais a partir da Turma de Entrada por uma consulta projetada ordenando pelo nome discentes
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoSemanalDiscente> findAcompanhamentosByTurmaEntradaProjetadoOrdenadoPeriodo(int idTurmaEntrada) throws DAOException{
			
			String projecao = "id, discente.id, discente.discente.matricula, discente.discente.pessoa.nome, participacaoPresencial, frequencia, " +
					"participacaoVirtual, pvSincronizada, periodoAvaliacao.id, " +
					"periodoAvaliacao.numeroPeriodo, periodoAvaliacao.dataInicio, periodoAvaliacao.datafim, periodoAvaliacao.codigoIntegracao, periodoAvaliacao.chTotalPeriodo";
			
			String hql = "select distinct " + projecao +  " from AcompanhamentoSemanalDiscente  where " +
					"  discente.turmaEntradaTecnico.id = " + idTurmaEntrada +" and (discente.discente.status= "+ StatusDiscente.ATIVO +")";
			
			hql = hql + " order by periodoAvaliacao.numeroPeriodo";
			
			Query q = getSession().createQuery(hql);

			return HibernateUtils.parseTo(q.list(), projecao, AcompanhamentoSemanalDiscente.class);
			
	
	}
	
	/**
	 * Retorna os discentes que não possuem acompanhamento semanal
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<DiscenteTecnico> findDiscentesSemAcompanhamento(int idTurmaEntrada) throws HibernateException, DAOException{
		String projecao = " id ";
		
		String hql = "Select"+ projecao + "from DiscenteTecnico d where d.turmaEntradaTecnico.id =  "+ idTurmaEntrada+" and d.discente.id not in (select a.discente.id from AcompanhamentoSemanalDiscente a where a.discente.turmaEntradaTecnico.id ="+idTurmaEntrada +")"+
		" and (discente.discente.status= "+ StatusDiscente.ATIVO +")"; 
		
		Query q = getSession().createQuery(hql);

		return (List<DiscenteTecnico>) HibernateUtils.parseTo(q.list(), projecao, DiscenteTecnico.class);
	}
	
	
	
	/**
	 * Retorna a listagem dos Acompanhamentos Semanais a partir da Turma de Entrada e um discente tecnico
	 * 
	 * @param idTurmaEntrada
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoSemanalDiscente> findAcompanhamentosByDiscenteTurmaEntrada(int idTurmaEntrada, int idDiscente) throws DAOException{
		try {
			
			TurmaEntradaTecnico turma = null;
			turma = findByPrimaryKey(idTurmaEntrada, TurmaEntradaTecnico.class);			
			
			Criteria cAcomp = getSession().createCriteria(AcompanhamentoSemanalDiscente.class);
			Criteria cDiscente = cAcomp.createCriteria("discente");
			Criteria cPeriodo = cAcomp.createCriteria("periodoAvaliacao");
			Criteria cCrono = cPeriodo.createCriteria("cronogramaExecucaoAulas");
			
			cCrono.add(Expression.eq("id", turma.getDadosTurmaIMD().getCronograma().getId()));
			cDiscente.add(Expression.eq("id", idDiscente));
			
			cAcomp.addOrder(org.hibernate.criterion.Order.asc("id"));
			
			Collection<AcompanhamentoSemanalDiscente> listaAcompanhamentos = null;
					
			if(cAcomp.list() != null)
			{
				listaAcompanhamentos = cAcomp.list();
			} else {
				listaAcompanhamentos = Collections.emptyList();
			}
			
			return listaAcompanhamentos;
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	
	
	/**
	 * Retorna a listagem dos Acompanhamentos Semanais a partir da Turma de Entrada por uma consulta projetada
	 * 
	 * @param idTurmaEntrada, dataInicioInformada, dataFimInformada
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoSemanalDiscente> findAcompanhamentosByTurmaEntradaProjetadoRelatorioConf(int idTurmaEntrada, Date dataInicioInformada, Date dataFimInformada) throws DAOException{
			
			String projecao = "id, discente.id, discente.discente.matricula, discente.discente.pessoa.nome, participacaoPresencial, frequencia, " +
					"participacaoVirtual, pvSincronizada, periodoAvaliacao.id, " +
					"periodoAvaliacao.numeroPeriodo, periodoAvaliacao.dataInicio, periodoAvaliacao.datafim, periodoAvaliacao.codigoIntegracao, periodoAvaliacao.chTotalPeriodo";
			
			String hql = "select " + projecao +  " from AcompanhamentoSemanalDiscente  where " +
					"  discente.turmaEntradaTecnico.id = " + idTurmaEntrada + " AND periodoAvaliacao.dataInicio >= " + dataInicioInformada + 
					"  AND periodoAvaliacao.datafim <= " + dataFimInformada +
					" and (discente.discente.status= "+ StatusDiscente.ATIVO;
			
			hql = hql + " order by id";
			
			Query q = getSession().createQuery(hql);

			return HibernateUtils.parseTo(q.list(), projecao, AcompanhamentoSemanalDiscente.class);
			
	
	}


	
}
