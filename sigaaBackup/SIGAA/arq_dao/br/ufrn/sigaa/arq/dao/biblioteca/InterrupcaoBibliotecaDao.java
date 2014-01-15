/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/06/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InterrupcaoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 * DAO que recupera informa��es sobre os feriados das bibliotecas da UFRN.
 * @author Fred_Castro
 *
 */
public class InterrupcaoBibliotecaDao extends GenericSigaaDAO{
	
	
	
	
	/**
	 * 	 <p>M�todo que conta todas as prorroga��es de empr�stimos geradas pela interrup��o passada<br/> 
	 * 	 ou seja, conta TODAS as interrup��o geradas por <code>TipoProrrogacaoEmprestimo.INTERRUPCAO_BIBLIOTECA</code> <br/>
	 *   ativas onde a data anterior da prorroga��o seja igual a data da interru��o. 
	 *   E a biblioteca do material do empr�stimo da prorroga��o seja a mesma biblioteca da interrup��o.
	 *   </p>
	 *
	 * @param interrupcao
	 * @return
	 * @throws DAOException
	 */
	public Integer countAllProrrogacoesGeradasPelaInterrupcaoByBiblioteca(InterrupcaoBiblioteca interrupcao, int idBiblioteca) throws DAOException{
		Integer quantidade = 0;
				
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT COUNT(DISTINCT p.id) FROM ProrrogacaoEmprestimo p ");
		hql.append(" WHERE p.tipo = "+TipoProrrogacaoEmprestimo.INTERRUPCAO_BIBLIOTECA);
		
		// Between porque a data da interrupcao � data � DATE, na prorroga��o � TIMESTAMP
		hql.append(" AND ( p.dataAnterior between :dataInterrupcao and :finalDiaInterrupcao ) ");
		hql.append(" AND p.emprestimo.ativo  =  trueValue() AND p.emprestimo.dataDevolucao is null AND p.emprestimo.dataEstorno is null ");
		hql.append(" AND p.emprestimo.material.biblioteca.id = :idBiblioteca ");
	
		
		Query q = getSession().createQuery(hql.toString());
		q.setDate("dataInterrupcao", interrupcao.getData());
		q.setTimestamp("finalDiaInterrupcao", CalendarUtils.configuraTempoDaData(interrupcao.getData(), 23, 59, 59, 999));
		q.setInteger("idBiblioteca", 	idBiblioteca);
		quantidade = ((Long) q.uniqueResult()).intValue();
		
		return quantidade;
	}
	
	
	
	
	/**
	 * Retorna todos os feriados ativos da biblioteca que acontecer�o depois da data atual.
	 * Se a biblioteca n�o for especificada, n�o filtra por biblioteca.
	 * 
	 * @param biblioteca A biblioteca cujos feriados ser�o retornados.
	 * @return
	 * @throws DAOException
	 */
	public List <InterrupcaoBiblioteca> findInterrupcoesAtivasFuturasByBiblioteca (Biblioteca biblioteca) throws DAOException{
		
		String projecao = " id, data ";
		
		Criteria c = getSession().createCriteria(InterrupcaoBiblioteca.class);
		c.setProjection(Projections.projectionList().add(Projections.property("id")).add(Projections.property("data")) );
		c.add(Restrictions.ge("data", CalendarUtils.descartarHoras(new Date())));
		c.add(Restrictions.eq("ativo", true));                                       // Se a Interrup��o for removida n�o � para contar
		// Se a biblioteca n�o for especificada, n�o filtra por biblioteca.
		if (biblioteca != null && biblioteca.getId() > 0){
			c.add(Restrictions.sqlRestriction("id_interrupcao_biblioteca in (select id_interrupcao_biblioteca from biblioteca.biblioteca_interrupcao_biblioteca where id_biblioteca = " + biblioteca.getId()+")"));
		}
		c.addOrder(Order.asc("data"));
		
		@SuppressWarnings("unchecked")
		List <Object[]> lista = c.list();
		
		return new ArrayList<InterrupcaoBiblioteca>(  HibernateUtils.parseTo(lista, projecao, InterrupcaoBiblioteca.class)   );
		
	}
	
	
	/**
	 * Retorna todos as interrup��es ativas cadastradas.<br/>
	 * 
	 * Interrup��es ativas s�o aquelas cuja a data � maior ou igual a hoje e que n�o foi removida (ativo = false)
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> findAllInterrupcoesAtivasCadastradas() throws DAOException{
		
		String projecao = " i.id, biblioteca.id, biblioteca.descricao, i.data, i.motivo, i.registroCadastro.usuario.pessoa.nome ";
		
		String hql = " SELECT "+projecao+" FROM InterrupcaoBiblioteca i " +
		" INNER JOIN i.bibliotecas biblioteca "+		
		" WHERE i.data > :hoje AND i.ativo = :true";
		
		Query q = getSession().createQuery(hql);
		q.setDate("hoje", CalendarUtils.descartarHoras( new Date() )  );
		q.setBoolean("true", true  );
		
		@SuppressWarnings("unchecked")
		List <Object[]> lista = q.list();
		
		return lista;
	}
	
	
	
	/**
	 * Retorna todos as interrup��es ativas cadastradas.<br/>
	 * 
	 * Interrup��es ativas s�o aquelas cuja a data � maior ou igual a hoje e que n�o foi removida (ativo = false)
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List <Object[]> findAllInterrupcoesAtivasCadastradasNoPeriodo(Integer idBibliotecaHistorico, Date dataInicio, Date dataFim) throws DAOException{
		
		
		String projecao = " i.id,  biblioteca.id, biblioteca.descricao, i.data, i.motivo, i.registroCadastro.usuario.pessoa.nome ";
		
		String hql = " SELECT "+projecao+" FROM InterrupcaoBiblioteca i " +
		" INNER JOIN i.bibliotecas biblioteca "+
		" WHERE ( i.data between :inicio AND :fim ) AND i.ativo = :true ";
		
		if(idBibliotecaHistorico != null && idBibliotecaHistorico > 0 )
			hql += " AND biblioteca.id = :idBiblioteca ";
		
		Query q = getSession().createQuery(hql);
		q.setDate("inicio", CalendarUtils.descartarHoras( dataInicio )  );
		q.setDate("fim", CalendarUtils.descartarHoras( dataFim )  );
		
		if(idBibliotecaHistorico != null && idBibliotecaHistorico > 0 )
			q.setInteger("idBiblioteca", idBibliotecaHistorico);
		
		q.setBoolean("true", true  );
		
		@SuppressWarnings("unchecked")
		List <Object[]> lista = q.list();
		return lista;
	}
	
	
	/**
	 * Retorna todos as interrup��es cadastradas para as bibliotecas passadas.<br/>
	 * 
	 * Interrup��es ativas s�o aquelas cuja a data � maior ou igual a hoje e que n�o foi removida (ativo = false)
	 * 
	 * @return  uma array contendo:
	 *         [0] data da interrup��o
     *         [1] id da biblioteca da interrup��o
	 * @throws DAOException
	 */
	
	public List<Object[]> findAllDatasInterrupcoesCadastradasParaAsBibliotecasNoPerioco(List<Integer> idsBibliotecas,
			Date dataInicio, Date dataFim) throws DAOException{
		
		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT i.data, b.id FROM InterrupcaoBiblioteca i ");
		hql.append(" INNER JOIN i.bibliotecas as b  ");
		hql.append(" WHERE i.ativo = trueValue() ");
		hql.append(" AND b.id  in ( :idBibliotecas) ");
		
		if(dataFim  == null)
			hql.append(" AND i.data = :dataInicio ");
		else
			hql.append(" AND i.data between :dataInicio AND :dataFim");
		
	
		Query q = getSession().createQuery( hql.toString());
		q.setParameterList("idBibliotecas", idsBibliotecas);
		
		
		if(dataFim  == null)
			q.setDate("dataInicio", dataInicio);
		else{
			q.setDate("dataInicio", dataInicio);
			q.setDate("dataFim", dataFim);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.list();
		return list;
	}
	
	
	
	
	/**
	 * <p>Retorna todos os empr�stimos ativos das bibliotecas passadas no per�odo passado realizando um lock persimista n�s empr�stimos..</p>
	 * 
	 * <p>Utilizado na parte do cadastro de <strong>interrup��o da biblioteca</strong>, enquando os empr�stimos est�o 
	 * sendo prorrogados n�o podem ser renovados ou devolvidos.;</p>
	 * 
	 * @param bibliotecas
	 * @return
	 * @throws DAOException
	 */
	public List <Emprestimo> findAllEmprestimosAtivosByBibliotecasEPeriodo(List <Biblioteca> bibliotecas, Date dataInicio, Date dataFim) throws DAOException {
		
		SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
		
		String hql = "SELECT e FROM Emprestimo e WHERE e.material.biblioteca.id IN " + gerarStringIn(bibliotecas) + 
					" AND e.prazo >= '"+sdf.format(dataInicio)+" 00:00:00.000'" +
					" AND e.prazo <= '"+sdf.format(dataFim == null ? dataInicio : dataFim)+" 23:59:59.999'" +
					" AND e.ativo = trueValue() " +
					" AND e.situacao = "+Emprestimo.EMPRESTADO;
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List<Emprestimo> lista =  q.list();
		return lista;
	}
	
}
