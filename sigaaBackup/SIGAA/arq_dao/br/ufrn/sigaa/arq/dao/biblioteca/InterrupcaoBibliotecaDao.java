/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * DAO que recupera informações sobre os feriados das bibliotecas da UFRN.
 * @author Fred_Castro
 *
 */
public class InterrupcaoBibliotecaDao extends GenericSigaaDAO{
	
	
	
	
	/**
	 * 	 <p>Método que conta todas as prorrogações de empréstimos geradas pela interrupção passada<br/> 
	 * 	 ou seja, conta TODAS as interrupção geradas por <code>TipoProrrogacaoEmprestimo.INTERRUPCAO_BIBLIOTECA</code> <br/>
	 *   ativas onde a data anterior da prorrogação seja igual a data da interrução. 
	 *   E a biblioteca do material do empréstimo da prorrogação seja a mesma biblioteca da interrupção.
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
		
		// Between porque a data da interrupcao é data é DATE, na prorrogação é TIMESTAMP
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
	 * Retorna todos os feriados ativos da biblioteca que acontecerão depois da data atual.
	 * Se a biblioteca não for especificada, não filtra por biblioteca.
	 * 
	 * @param biblioteca A biblioteca cujos feriados serão retornados.
	 * @return
	 * @throws DAOException
	 */
	public List <InterrupcaoBiblioteca> findInterrupcoesAtivasFuturasByBiblioteca (Biblioteca biblioteca) throws DAOException{
		
		String projecao = " id, data ";
		
		Criteria c = getSession().createCriteria(InterrupcaoBiblioteca.class);
		c.setProjection(Projections.projectionList().add(Projections.property("id")).add(Projections.property("data")) );
		c.add(Restrictions.ge("data", CalendarUtils.descartarHoras(new Date())));
		c.add(Restrictions.eq("ativo", true));                                       // Se a Interrupção for removida não é para contar
		// Se a biblioteca não for especificada, não filtra por biblioteca.
		if (biblioteca != null && biblioteca.getId() > 0){
			c.add(Restrictions.sqlRestriction("id_interrupcao_biblioteca in (select id_interrupcao_biblioteca from biblioteca.biblioteca_interrupcao_biblioteca where id_biblioteca = " + biblioteca.getId()+")"));
		}
		c.addOrder(Order.asc("data"));
		
		@SuppressWarnings("unchecked")
		List <Object[]> lista = c.list();
		
		return new ArrayList<InterrupcaoBiblioteca>(  HibernateUtils.parseTo(lista, projecao, InterrupcaoBiblioteca.class)   );
		
	}
	
	
	/**
	 * Retorna todos as interrupções ativas cadastradas.<br/>
	 * 
	 * Interrupções ativas são aquelas cuja a data é maior ou igual a hoje e que não foi removida (ativo = false)
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
	 * Retorna todos as interrupções ativas cadastradas.<br/>
	 * 
	 * Interrupções ativas são aquelas cuja a data é maior ou igual a hoje e que não foi removida (ativo = false)
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
	 * Retorna todos as interrupções cadastradas para as bibliotecas passadas.<br/>
	 * 
	 * Interrupções ativas são aquelas cuja a data é maior ou igual a hoje e que não foi removida (ativo = false)
	 * 
	 * @return  uma array contendo:
	 *         [0] data da interrupção
     *         [1] id da biblioteca da interrupção
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
	 * <p>Retorna todos os empréstimos ativos das bibliotecas passadas no período passado realizando um lock persimista nós empréstimos..</p>
	 * 
	 * <p>Utilizado na parte do cadastro de <strong>interrupção da biblioteca</strong>, enquando os empréstimos estão 
	 * sendo prorrogados não podem ser renovados ou devolvidos.;</p>
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
