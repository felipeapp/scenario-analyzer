/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/01/2008
 * 
 */
package br.ufrn.sigaa.ava.dao;

import static org.hibernate.criterion.Order.desc;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * DAO para consultas de tópicos de aula.
 * 
 * @author David Pereira
 *
 */
public class TopicoAulaDao extends GenericSigaaDAO {
	
	/**
	 * Retorna uma lista com os tópicos de aula referentes à turma indicada.
	 * 
	 * @param turma
	 * @return
	 */
	public List<TopicoAula> findByTurma(Turma turma) {
		@SuppressWarnings("unchecked")
		List<TopicoAula> lista = getHibernateTemplate().find("select new TopicoAula(top1.id, top1.descricao, top1.conteudo, top1.data, top1.fim, tur, pai, top1.visivel, top1.aulaCancelada, top1.cor) "
				+ "from TopicoAula top1 left join top1.turma tur, "
				+ "TopicoAula top2 left join top2.topicoPai pai where top1.id = top2.id and top1.ativo = trueValue() and "
				+ "tur.id = ? order by top1.data asc", new Object[] { turma.getId() });
		return lista;
	}

	/**
	 * Retorna o tópico ativo na turma que possua a descrição informada.
	 * 
	 * @param t
	 * @param descricao
	 * @return
	 */
	public TopicoAula findByDescricao(Turma t, String descricao) {
		DetachedCriteria dc = DetachedCriteria.forClass(TopicoAula.class);
		dc.add(eq("descricao", descricao));
		dc.add(eq("ativo",true));
		dc.add(eq("turma", t));
		return (TopicoAula) getHibernateTemplate().uniqueResult(dc);
	}

	/**
	 * Exclui os docenteTurma passados de todos tópicos de aula. 
	 * Isto deve ser realizado quando o docenteTurma é excluído do banco de dados.
	 * 
	 * @param docentes
	 */
	public void removeDocentesTurma(Collection<DocenteTurma> docentes) {
		if(docentes == null || docentes.size() == 0)
			return;
		
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (DocenteTurma docente : docentes) {
			sb.append(docente.getId());
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(")");
		
		getJdbcTemplate().update("delete from ava.topico_aula_docente_turma where id_docente_turma in " + sb.toString());
	}
	
	/**
	 * Retorna as datas dos tópicos cujo as aulas foram canceladas.
	 * 
	 * @param docentes
	 */
	@SuppressWarnings("unchecked")
	public List<Date> findDatasDeTopicosSemAula ( int idTurma ) throws DAOException {
		
		try  {
			String hql = " SELECT t.data FROM TopicoAula t where t.turma.id = " +idTurma+ " and t.aulaCancelada = trueValue() and t.ativo=trueValue() order by t.data";
			Query q = getSession().createQuery(hql); 	
			List<Date> datas = q.list();
			return datas;
		} catch (Exception e) {
			throw new DAOException(e);		
		}
		
	}
	
	/**
	 * Retorna os tópicos cujo as aulas foram canceladas.
	 * 
	 * @param idTurma
	 */
	@SuppressWarnings("unchecked")
	public List<TopicoAula> findTopicosSemAula ( int idTurma ) throws DAOException {
		
		try  {
			String hql = " SELECT t FROM TopicoAula t where t.turma.id = " +idTurma+ " and t.aulaCancelada = trueValue() and t.ativo=trueValue() order by t.data ";
			Query q = getSession().createQuery(hql); 	
			List<TopicoAula> topicos = q.list();
			return topicos;
		} catch (Exception e) {
			throw new DAOException(e);		
		}
		
	}
	
	/**
	 * Retorna os tópicos cujo as aulas foram canceladas.
	 * 
	 * @param idTurma
	 * @param dataSelecionada
	 */
	@SuppressWarnings("unchecked")
	public List<TopicoAula> findTopicosSemAula ( int idTurma , Date dataSelecionada ) throws DAOException {
		
		DetachedCriteria c = DetachedCriteria.forClass(TopicoAula.class);
		c.add(eq("turma.id", idTurma));
		c.add(eq("data", dataSelecionada));
		c.add(eq("aulaCancelada", true));
		c.addOrder(desc("dataCadastro"));
		c.add(eq("ativo",true));
		
		List<TopicoAula> lista = getHibernateTemplate().findByCriteria(c);
		return lista;
	}
	
	/**
	 * Retorna o primeiro tópico da turma
	 * 
	 * @param idTurma
	 */
	public TopicoAula findPrimeiroTopicoTurma ( int idTurma ) throws DAOException {
		
		DetachedCriteria c = DetachedCriteria.forClass(TopicoAula.class);
		c.add(eq("turma.id", idTurma));
		c.addOrder(Order.asc("data"));
		c.add(eq("ativo",true));
		
		TopicoAula topico = (TopicoAula) getHibernateTemplate().uniqueResult(c);
		return topico;
	}
}
