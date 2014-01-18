/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '01/11/2006'
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;

/**
 * Dao com consultas sobre os currículos dos cursos técnicos
 * 
 * @author Andre M Dantas
 * @author Leonardo Campos
 *
 */
public class EstruturaCurricularTecDao extends GenericSigaaDAO {

	public EstruturaCurricularTecDao() {
	}

	/**
	 * Retorna todos os currículos de acordo com os parâmetros informados.
	 * @param unidId
	 * @param nivel
	 * @param ativa
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EstruturaCurricularTecnica> findAll(int unidId, char nivel, Boolean ativa, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from EstruturaCurricularTecnica where 1=1 ");
			if (unidId > 0)
				hql.append(" and cursoTecnico.unidade.id = " + unidId );
			if (nivel != 0)
				hql.append(" and cursoTecnico.nivel = '" + nivel + "' ");
			if (ativa != null)
				hql.append(" and ativa = " + ativa);
			
			hql.append(" and cursoTecnico.ativo = " + Boolean.TRUE	);
			hql.append(" order by cursoTecnico.nome	");
			
			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

	
	/**
	 * Retorna todos os currículos ativos do curso informado
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EstruturaCurricularTecnica> findByCursoTecnico(int idCurso) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from EstruturaCurricularTecnica where cursoTecnico.id="+idCurso);
			hql.append(" and ativa = trueValue()");
			
			return getSession().createQuery(hql.toString()).list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca expressão de pré-requisitos SE uma disciplina estiver contida na E.C.
	 * Caso não esteja presente na EC, retorna NULO.
	 * @param ecId
	 * @param disciplinaId
	 * @return
	 * @throws DAOException
	 */
	public boolean contemDisciplina(int ecId, int disciplinaId) throws DAOException {
		try {

		StringBuffer hql = new StringBuffer();
		hql.append("SELECT mc.id ");
		hql.append("FROM ModuloCurricular mc, ModuloDisciplina md WHERE ");
		hql.append(" mc.estruturaCurricularTecnica.id=:estrutura and " +
				" mc.modulo.id=md.modulo.id and" +
				" md.disciplina.id=:disciplina ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("estrutura", ecId);
		q.setInteger("disciplina", disciplinaId);
		if (q.uniqueResult() != null)
			return true;

		hql = new StringBuffer();
		hql.append("SELECT id ");
		hql.append("FROM DisciplinaComplementar WHERE ");
		hql.append("estruturaCurricularTecnica.id = :estrutura and disciplina.id = :disciplina");
		q = getSession().createQuery(hql.toString());
		q.setInteger("estrutura", ecId);
		q.setInteger("disciplina", disciplinaId);
		if (q.uniqueResult() != null)
			return true;

		return false;
		} catch (Exception e) {
			e.printStackTrace();
			 throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna o currículo atual do discente informado.
	 * @param discenteId
	 * @return
	 * @throws DAOException
	 */
	public EstruturaCurricularTecnica findByDiscente(int discenteId) throws DAOException {
		try {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT d.estruturaCurricularTecnica ");
		hql.append("FROM DiscenteTecnico d WHERE ");
		hql.append("d.id = :discente");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("discente", discenteId);
		return (EstruturaCurricularTecnica) q.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			 throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna o currículo atual do discente informado.
	 * @param idModulo
	 * @return
	 * @throws DAOException
	 */
	public Collection<EstruturaCurricularTecnica> findByModulo(int idModulo) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" SELECT ect.id, ect.chTotalModulos, ect.ativa "+
					   " FROM ModuloCurricular mc " +
					   " INNER JOIN mc.estruturaCurricularTecnica ect ");
			hql.append(" WHERE mc.modulo.id = :idModulo");
			
			Query q = getSession().createQuery(hql.toString());
			
			q.setInteger("idModulo", idModulo);
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			Collection<EstruturaCurricularTecnica> result = new ArrayList<EstruturaCurricularTecnica>();
			for (Object[] linha : lista) {
				int atributo = 0;

				EstruturaCurricularTecnica ect = new EstruturaCurricularTecnica();
				
				ect.setId((Integer) linha[atributo++]);
				ect.setChTotalModulos((Integer) linha[atributo++]);
				ect.setAtiva((Boolean)linha[atributo++]);
				
				result.add(ect);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			 throw new DAOException(e.getMessage(), e);
		}
	}


}
