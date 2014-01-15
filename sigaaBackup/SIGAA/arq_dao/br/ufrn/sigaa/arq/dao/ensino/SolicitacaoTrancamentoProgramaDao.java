/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/04/2010 
 */
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoPrograma;
import br.ufrn.sigaa.ensino.dominio.StatusSolicitacaoTrancamentoPrograma;

/**
 * Dao responsável por realizar consulta referente a Solicitação de Trancamento de Programa.
 * @author Arlindo
 *
 */
public class SolicitacaoTrancamentoProgramaDao extends GenericSigaaDAO {
	
	/**
	 * Verifica se o discente possui algum trancamento no ano e período informado.
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException  
	 */
	public Long quantSolicitacoesTrancamentoByDiscente(DiscenteAdapter discente, int ano, int periodo) throws DAOException {
		String hql = "select count(tp.id) from SolicitacaoTrancamentoPrograma tp "+
				    " where tp.discente.id = :discente and tp.situacao not in ("+
				    StatusSolicitacaoTrancamentoPrograma.CANCELADO+","+StatusSolicitacaoTrancamentoPrograma.INDEFERIDO+")";
		
		if(!isEmpty(ano) && ano > 0)
			hql += " AND tp.ano = :ano ";

		if(!isEmpty(periodo) && periodo > 0)
			hql += " AND tp.periodo = :periodo ";		
				    	
		Query q = getSession().createQuery(hql);
		
		q.setParameter("discente", discente.getId());
		
		if(!isEmpty(ano))
			q.setInteger("ano", ano);

		if(!isEmpty(periodo))
			q.setInteger("periodo", periodo);
		
		return (Long) q.uniqueResult();
	}		
	
	/**
	 * Retorna todas as solicitações de trancamento de programa do discente informado.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoTrancamentoPrograma> findSolicitacoesByDiscente(DiscenteAdapter discente) throws DAOException{
		String projecao = "tp.id, tp.discente.matricula, tp.discente.pessoa.nome, tp.discente.curso.nome, " +
				"tp.ano, tp.periodo, tp.numeroMeses, tp.inicioTrancamento, tp.situacao, tp.dataCadastro, " +
				"tp.observacao ";
		
		String hql = "select " +projecao+
				" from SolicitacaoTrancamentoPrograma tp where tp.discente.id = :discente order by tp.dataCadastro desc ";
		
		Query q = getSession().createQuery(hql);		
		q.setParameter("discente", discente.getId());
		
		@SuppressWarnings("unchecked")
		List<SolicitacaoTrancamentoPrograma> lista = (List<SolicitacaoTrancamentoPrograma>) HibernateUtils.parseTo(q.list(), projecao, SolicitacaoTrancamentoPrograma.class, "tp");
		
		return lista;
	}

}
