/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/09/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;

/**
 * DAO para consultas relacionadas a módulos
 *
 * @author David Ricardo
 *
 */
public class ModuloDao extends GenericSigaaDAO {


	/**
	 * Método responsável pelo retorno de uma coleção de Modulo de acordo com a unidade e nível informados.
	 * @param unid
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Modulo> findAll(int unid, char nivel, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from Modulo ");
			if (unid > 0)
				hql.append("where unidade.id = " + unid );
			if (nivel != 0)
				hql.append(" and nivel = '" + nivel + "' " );
			hql.append("order by descricao asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			@SuppressWarnings("unchecked")
			List<Modulo> list = q.list();
			return list;
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Método responsável pelo retorno de uma coleção de Modulo do curso informado.
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<Modulo> findByCursoTecnico(int idCurso) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Modulo.class).
					createCriteria("moduloCurriculares").
					createCriteria("estruturaCurricularTecnica").
					add(Expression.eq("ativa", Boolean.TRUE))
					.add(Expression.eq("cursoTecnico", new CursoTecnico(idCurso)));
			
			@SuppressWarnings("unchecked")
			List<Modulo> list = c.list();
			return list;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna uma coleção de Modulos dos identificadores informados.
	 * 
	 * @param idModulos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Modulo> findByIdModulos(int[] idModulos) throws DAOException {
		return findByIdModulos(idModulos, true);
	}
	
	/**
	 * Retorna uma coleção de Modulos dos identificadores informados.
	 * 
	 * @param idModulos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Modulo> findByIdModulos(int[] idModulos, boolean projecao) throws DAOException {
		Query q = getSession().createQuery((projecao ? "select id, descricao " : "") + " from Modulo where id in "+ UFRNUtils.gerarStringIn(idModulos));
		@SuppressWarnings("unchecked")
		Collection<Modulo> list = projecao ? HibernateUtils.parseTo(q.list(), "id, descricao", Modulo.class) : q.list();
		return list;
	}


	/**
	 * Método responsável pelo retorno de uma coleção de Turna de acordo com o modulo informado.
	 * 
	 * @param idModulo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Turma> findTurmasAtivasByModulo(int idModulo) throws DAOException {
		try {
			String hql = "select distinct t from ModuloDisciplina md, Turma t "
				+ "INNER JOIN FETCH t.horarios " 
				+ "where md.modulo.id = :modulo and md.disciplina.id =  t.disciplina.id and "
				+ "(t.situacaoTurma is null or t.situacaoTurma.id = :situacao) ";
			
			Query q = getSession().createQuery(hql);
			q.setInteger("modulo", idModulo);
			q.setInteger("situacao", SituacaoTurma.ABERTA);

			List<Turma> list = q.list();
			Collections.sort(list, new Comparator<Turma>() {
				@Override
				public int compare(Turma t1, Turma t2) {
					return t1.getDisciplina().getCodigo().compareTo(t2.getDisciplina().getCodigo()) != 0 
						 ? t1.getDisciplina().getCodigo().compareTo(t2.getDisciplina().getCodigo())
						 : t1.getCodigo().compareTo(t2.getCodigo());
				}
			});
			
			return list;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Método responsável pelo retorno do último Modulo de acordo com a unidade e nivel informados.
	 * 
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Modulo findUltimoCodigo(int unidade, char nivel) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Modulo.class);
			c.add(Expression.eq("nivel", nivel));
			c.add(Expression.eq("unidade", new Unidade(unidade)));
			c.addOrder(Order.desc("codigo"));
			c.setMaxResults(1);
			return (Modulo) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Método responsável pelo retorno do Modulo de acordo com o nome, o código, o curso, o nível e a unidade informados. 
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @param nivel
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Modulo> findByCodCursoNome(String nome, String codigo, Integer curso, char nivel, int idUnidade) throws DAOException {
		Collection<Modulo> listaModulo = new ArrayList<Modulo>();
		try {
			StringBuilder consultaSql = new StringBuilder();
			consultaSql.append("select distinct m.id_modulo, m.carga_horaria, m.codigo, m.descricao" +
							   " from tecnico.modulo m");
							  if (curso != null) {
								  consultaSql.append(" inner join tecnico.modulo_curricular modulocurr1_ on m.id_modulo=modulocurr1_.id_modulo" +
								   " inner join tecnico.estrutura_curricular_tecnica estruturac2_ on modulocurr1_.id_estrutura_curricular=estruturac2_.id_estrutura_curricular" +
								   " left outer join tecnico.curso_tecnico cursotecni6_ on estruturac2_.id_curso=cursotecni6_.id_curso" +
								   " where estruturac2_.ativa is true");
							  }else
								  consultaSql.append(" where 1=1 ");
			
			if (nome != null && !nome.equals("")) 
				consultaSql.append(" and m.descricao ilike '%" + nome + "%'");
			if (codigo != null && !codigo.equals(""))
				consultaSql.append(" and m.codigo ilike '" + codigo + "'");
			if (curso != null)
				consultaSql.append(" and estruturac2_.id_curso= " + curso);
			consultaSql.append(" and m.nivel='"+ nivel +"' and m.id_unidade= " + idUnidade);
			consultaSql.append(" order by m.codigo ");
			
			List<Object[]> mult= getSession().createSQLQuery(consultaSql.toString()).list(); 
			for (Object[] item : mult) {
				Modulo linha = new Modulo();
				linha.setId((Integer) item[0]);
				linha.setCargaHoraria((Integer) item[1]);
				linha.setCodigo((String) item[2]);
				linha.setDescricao((String) item[3]);
				listaModulo.add(linha);
			}
			return listaModulo;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}