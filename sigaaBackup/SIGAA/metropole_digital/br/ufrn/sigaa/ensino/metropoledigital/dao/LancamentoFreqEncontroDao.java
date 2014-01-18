/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 22/05/2013
 *
 */
package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.springframework.core.annotation.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.ead.TurmaEadDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;


import br.ufrn.sigaa.ensino.metropoledigital.dominio.AcompanhamentoSemanalDiscente;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.DadosTurmaIMD;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;


/**
 * 
 * DAO com consultas utilizadas no caso de uso lançar frequencias das turmas do IMD
 * 
 * @author Rafael Barros
 *
 */

public class LancamentoFreqEncontroDao extends GenericSigaaDAO{

	/**
	 * Retorna a listagem dos Discentes Tecnico que estão vinculados a Turma de Entrada na qual a frequência será realizada
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteTecnico> findDiscentesByTurmaEntrada(int idTurmaEntrada) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(DiscenteTecnico.class);
			Criteria cTurma = c.createCriteria("turmaEntradaTecnico");
			Criteria cDiscente = c.createCriteria("discente");
			Criteria cPessoa = cDiscente.createCriteria("pessoa");
			cTurma.add(Expression.eq("id",idTurmaEntrada));
			cPessoa.addOrder(org.hibernate.criterion.Order.asc("nome"));
			
			Collection<DiscenteTecnico> listaDiscentes = null;
			
			if(c.list() != null)
			{
				listaDiscentes = c.list();
			} else {
				listaDiscentes = Collections.emptyList();
			}
			
		
			
			return listaDiscentes;
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	
	
	/**
	 * Retorna a listagem dos Períodos de Avaliação que estão vinculados ao Cronograma de Execução da Turma de Entrada na qual a frequência será realizada
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public Collection<PeriodoAvaliacao> findPeriodosByTurmaEntrada(int idTurmaEntrada) throws DAOException{
		try {
			
			TurmaEntradaTecnico turma = null;
			turma = findByPrimaryKey(idTurmaEntrada, TurmaEntradaTecnico.class);			
			
			Criteria cPeriodo = getSession().createCriteria(PeriodoAvaliacao.class);
			Criteria cCrono = cPeriodo.createCriteria("cronogramaExecucaoAulas");
			
			cCrono.add(Expression.eq("id", turma.getDadosTurmaIMD().getCronograma().getId()));
			cPeriodo.addOrder(org.hibernate.criterion.Order.asc("dataInicio"));
			
			Collection<PeriodoAvaliacao> listaPeriodos = null;
			
			if(cPeriodo.list() != null)
			{
				listaPeriodos = cPeriodo.list();
			} else {
				listaPeriodos = Collections.emptyList();
			}
			
		
			
			return listaPeriodos;
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	
	
	/**
	 * Retorna VERDADEIRO se os registros da frequencia da turma de entrada informada já estiverem sido criados
	 * Caso contrário retorna FALSO para que sejam criados esses registros da frequencia
	 * 
	 * @param idPeriodoAvaliacao
	 * @return
	 * @throws DAOException
	 */
	public boolean existeRegistrosFrequencia(int idPeriodoAvaliacao) throws DAOException{
		try {	
			
			Criteria cAcomp = getSession().createCriteria(AcompanhamentoSemanalDiscente.class);
			Criteria cPeriodo = cAcomp.createCriteria("periodoAvaliacao");
			
			cPeriodo.add(Expression.eq("id", idPeriodoAvaliacao));
	
			if(cAcomp.list().isEmpty())
			{
				return false;
			} else {
				Collection<AcompanhamentoSemanalDiscente> lista = cAcomp.list();
				return true;
			}
		
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	

	/**
	 * Retorna VERDADEIRO se os registros da frequencia de um determinado discente informado já estiverem sido criados
	 * Caso contrário retorna FALSO para que sejam criados esses registros da frequencia
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public boolean existeRegistrosFrequenciaPorDiscente(int idDiscente) throws DAOException{
		try {	
			
			Criteria cAcomp = getSession().createCriteria(AcompanhamentoSemanalDiscente.class);
			Criteria cDiscente = cAcomp.createCriteria("discente");
			
			cDiscente.add(Expression.eq("id", idDiscente));
	
			if(cAcomp.list().isEmpty())
			{
				return false;
			} else {
				Collection<AcompanhamentoSemanalDiscente> lista = cAcomp.list();
				return true;
			}
		
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	
	
	/**
	 * Retorna a listagem dos Acompanhamentos Semanais a partir da Turma de Entrada 
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoSemanalDiscente> findAcompanhamentosByTurma(int idTurmaEntrada) throws DAOException{
		try {
			
			TurmaEntradaTecnico turma = null;
			turma = findByPrimaryKey(idTurmaEntrada, TurmaEntradaTecnico.class);			
			
			Criteria cAcomp = getSession().createCriteria(AcompanhamentoSemanalDiscente.class);
			Criteria cPeriodo = cAcomp.createCriteria("periodoAvaliacao");
			Criteria cCrono = cPeriodo.createCriteria("cronogramaExecucaoAulas");
			
			cCrono.add(Expression.eq("id", turma.getDadosTurmaIMD().getCronograma().getId()));	
			cAcomp.addOrder(org.hibernate.criterion.Order.asc("id"));
			cPeriodo.addOrder(org.hibernate.criterion.Order.asc("numeroPeriodo"));
			
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
	 * Retorna a listagem dos Acompanhamentos Semanais a partir da Turma de Entrada ordenados pelos Discentes
	 * 
	 * @param idTurmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public Collection<AcompanhamentoSemanalDiscente> findAcompanhamentosByTurmaOrderDiscente(int idTurmaEntrada) throws DAOException{
		try {
			
			TurmaEntradaTecnico turma = null;
			turma = findByPrimaryKey(idTurmaEntrada, TurmaEntradaTecnico.class);			
			
			Criteria cAcomp = getSession().createCriteria(AcompanhamentoSemanalDiscente.class);
			Criteria cPeriodo = cAcomp.createCriteria("periodoAvaliacao");
			Criteria cCrono = cPeriodo.createCriteria("cronogramaExecucaoAulas");
			Criteria cDiscenteTec = cAcomp.createCriteria("discente");
			Criteria cDiscente = cDiscenteTec.createCriteria("discente");
			Criteria cPessoa = cDiscente.createCriteria("pessoa");
			
			cCrono.add(Expression.eq("id", turma.getDadosTurmaIMD().getCronograma().getId()));	
			cPessoa.addOrder(org.hibernate.criterion.Order.asc("nome"));
			cPeriodo.addOrder(org.hibernate.criterion.Order.asc("numeroPeriodo"));
			
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
	
	
}
