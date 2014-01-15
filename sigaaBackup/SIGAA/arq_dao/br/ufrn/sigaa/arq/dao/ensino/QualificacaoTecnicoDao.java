/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.tecnico.dominio.QualificacaoTecnico;

/**
 * DAO para efetuar consultas nas qualificações de Técnico.
 * 
 * @author Andre M Dantas
 */
public class QualificacaoTecnicoDao extends GenericSigaaDAO {


	public QualificacaoTecnicoDao() {
		daoName = "QualificacaoDao";
	}

	/**
	 * Retorna uma coleção de qualificações técnicas de acordo com a descrição, unidade, nivel informados.
	 * 
	 * @param descricao
	 * @param unid
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<QualificacaoTecnico> findByDescricao(String descricao, int unid, char nivel, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from QualificacaoTecnico where ");
			if (unid > 0)
				hql.append("cursoTecnico.unidade.id = " + unid + " and ");
			if (nivel != 0)
				hql.append("cursoTecnico.nivel = '" + nivel + "' and ");
			hql.append( UFRNUtils.toAsciiUpperUTF8("descricao") + " like" +
					UFRNUtils.toAsciiUTF8("'" + descricao.toUpperCase().trim() + "%'") );
			hql.append("order by descricao asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna uma coleção de qualificações técnicas de acordo com o curso informado.
	 * 
	 * @param idCurso
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<QualificacaoTecnico> findByCurso(int idCurso, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from QualificacaoTecnico where ");
			hql.append("cursoTecnico.id = " + idCurso);
			hql.append("order by descricao asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna uma coleção de qualificações técnicas de acordo com o código, unidade e nível informados.
	 * 
	 * @param codigo
	 * @param unid
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<QualificacaoTecnico> findByCodigo(String codigo, int unid, char nivel, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from QualificacaoTecnico where ");
			if (unid > 0)
				hql.append("cursoTecnico.unidade.id = " + unid + " and ");
			if (nivel != 0)
				hql.append("cursoTecnico.nivel = '" + nivel + "' and ");
			hql.append("codigo = '" + codigo + "' ");
			hql.append("order by descricao asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}
	/**
	 * Retorna uma coleção de qualificações técnicas de acordo com a unidade e nível informados.
	 *  
	 * @param unid
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<QualificacaoTecnico> findAll(int unid, char nivel, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from QualificacaoTecnico where ");
			if (unid > 0)
				hql.append(" cursoTecnico.unidade.id = " + unid );
			if (nivel != 0)
				hql.append(" and cursoTecnico.nivel = '" + nivel + "' ");
			hql.append("order by descricao asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Consulta que retorna as qualificações a que o discente informado tem direito de obter
	 * Utilizado na expedição de certificados por módulo.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<QualificacaoTecnico> findQualificacoesPossiveis(int idDiscente) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			// módulos de uma qualificação que estejam entre os módulos aprovados, ou seja, ...
			hql.append("SELECT mq.qualificacao FROM ModuloQualificacao mq WHERE ");
			hql.append(" mq.modulo in (");

			// ... os módulos em que todas as disciplinas estejam entre ...
			hql.append("SELECT md.modulo FROM ModuloDisciplina md, DiscenteTecnico d WHERE " +
//					"md.modulo.qualificacao cursoTecnico.id = d.curso.id and d.id = :idDiscente and" +
					" md.disciplina in ");
			// ... as disciplinas das turmas aprovadas e aproveitadas de um aluno
			hql.append("( SELECT m.turma.disciplina FROM MatriculaComponente m WHERE " +
					"m.discente.id = :idDiscente and m.situacaoMatricula.id = :sitMatricula ) " +
					"or md.disciplina in " + /// disciplinas aproveitadas
					"(SELECT apr.disciplina FROM AproveitamentoComponente apr WHERE " +
					"apr.discente.id = :idDiscente)" +
					")");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idDiscente", idDiscente);
			q.setInteger("sitMatricula", SituacaoMatricula.APROVADO.getId());
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna uma coleção de qualificações técnicas de acordo com a descrição, curso, unidade e nível informados.
	 *  
	 * @param descricao
	 * @param curso
	 * @param unid
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<QualificacaoTecnico> findByDescricaoCurso(String descricao, Integer curso, int unid, char nivel) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from QualificacaoTecnico where ");
			hql.append("cursoTecnico.nivel = '" + nivel + "'");
			
			if (curso != null)
				hql.append(" and cursoTecnico.id = " + curso);
			if (descricao != null) {
				hql.append(" and " + UFRNUtils.toAsciiUpperUTF8("descricao") + " like" +
						UFRNUtils.toAsciiUTF8("'" + descricao.toUpperCase().trim() + "%'") );
			}
			hql.append(" order by descricao asc");
			Query q = getSession().createQuery(hql.toString());
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}
	
	
}
