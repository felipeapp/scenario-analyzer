/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * DAO para efetuar consultas nas qualifica��es de T�cnico.
 * 
 * @author Andre M Dantas
 */
public class QualificacaoTecnicoDao extends GenericSigaaDAO {


	public QualificacaoTecnicoDao() {
		daoName = "QualificacaoDao";
	}

	/**
	 * Retorna uma cole��o de qualifica��es t�cnicas de acordo com a descri��o, unidade, nivel informados.
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
	 * Retorna uma cole��o de qualifica��es t�cnicas de acordo com o curso informado.
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
	 * Retorna uma cole��o de qualifica��es t�cnicas de acordo com o c�digo, unidade e n�vel informados.
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
	 * Retorna uma cole��o de qualifica��es t�cnicas de acordo com a unidade e n�vel informados.
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
	 * Consulta que retorna as qualifica��es a que o discente informado tem direito de obter
	 * Utilizado na expedi��o de certificados por m�dulo.
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<QualificacaoTecnico> findQualificacoesPossiveis(int idDiscente) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			// m�dulos de uma qualifica��o que estejam entre os m�dulos aprovados, ou seja, ...
			hql.append("SELECT mq.qualificacao FROM ModuloQualificacao mq WHERE ");
			hql.append(" mq.modulo in (");

			// ... os m�dulos em que todas as disciplinas estejam entre ...
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
	 * Retorna uma cole��o de qualifica��es t�cnicas de acordo com a descri��o, curso, unidade e n�vel informados.
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
