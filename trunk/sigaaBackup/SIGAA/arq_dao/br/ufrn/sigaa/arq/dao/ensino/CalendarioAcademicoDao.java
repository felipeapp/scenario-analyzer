/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/12/2006
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

/**
 * DAO responsável por consultas específicas à Calendários Acadêmicos
 * @author Andre M Dantas
 *
 */
public class CalendarioAcademicoDao extends GenericSigaaDAO {

	/**
	 * 
	 * Busca calendário acadêmico de acordo com os seguintes parâmetros: ano, período, unidade e nível. 
	 * 
	 * @param ano
	 * @param periodo
	 * @param unidadeId
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public CalendarioAcademico findByAnoPeriodo(Integer ano, Integer periodo, int unidadeId, char nivel) throws DAOException{
		return findByParametros(ano, periodo, new Unidade(unidadeId), nivel, null, null, null, null);
	}

	/**
	 * 
	 * Busca calendário acadêmico de acordo com os seguintes parâmetros: unidade e nível.
	 * 
	 * @param unidadeId
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public CalendarioAcademico findByUnidadeNivel(int unidadeId, char nivel) throws DAOException{
		return findByParametros(null, null, new Unidade(unidadeId), nivel, null, null, null, null);
	}

	/**
	 * 
	 * Busca calendário acadêmico de acordo com os seguintes parâmetros: ano, período, curso e nível.
	 * 
	 * @param ano
	 * @param periodo
	 * @param curso
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public CalendarioAcademico findByCurso(Integer ano, Integer periodo, Curso curso, Character nivel) throws DAOException{
		if( curso.isStricto() || curso.isMedio() )
			return findByParametros(ano, periodo, curso.getUnidade().getGestoraAcademica(), nivel, null, null, curso, null);
		else
			return findByParametros(ano, periodo, null, nivel, null, null, curso, null);
	}

	/**
	 * 
	 * Busca calendário acadêmico de acordo com os seguintes parâmetros: ano, período, unidade gestora, nível e modalidade.
	 * 
	 * @param ano
	 * @param periodo
	 * @param gestoraAcademica
	 * @param nivel
	 * @param modalidade
	 * @return
	 * @throws DAOException
	 */
	public CalendarioAcademico findByModalidade(Integer ano, Integer periodo, Unidade gestoraAcademica, char nivel, ModalidadeEducacao modalidade) throws DAOException{
		return findByParametros(ano, periodo, gestoraAcademica, nivel, modalidade, null, null, null);
	}

	/**
	 * 
	 * Busca calendário acadêmico de acordo com os seguintes parâmetros: ano, período, unidade gestora, nível e convênio.
	 * 
	 * @param ano
	 * @param periodo
	 * @param gestoraAcademica
	 * @param nivel
	 * @param convenio
	 * @return
	 * @throws DAOException
	 */
	public CalendarioAcademico findByConvenio(Integer ano, Integer periodo, Unidade gestoraAcademica, char nivel, ConvenioAcademico convenio) throws DAOException{
		return findByParametros(ano, periodo, gestoraAcademica, nivel, null, convenio, null, null);
	}


	/**
	 * 
	 * Busca calendários acadêmicos para uma unidade e nível específicos. 
	 * 
	 * @param unidadeId
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<CalendarioAcademico> findByUnidade(int unidadeId, char nivel) throws DAOException{
		return findTodosByParametros(unidadeId, nivel, 0, 0, 0);
	}

	/**
	 * 
	 * Busca calendário acadêmico vigente de acordo com os seguintes parâmetros: ano, período, unidade e nível, modalidade, convênio e curso.
	 * 
	 * @param ano
	 * @param periodo
	 * @param unidade
	 * @param nivel
	 * @param modalidade
	 * @param convenio
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public CalendarioAcademico findByParametros(Integer ano, Integer periodo, Unidade unidade, Character nivel,
			ModalidadeEducacao modalidade, ConvenioAcademico convenio, Curso curso, Integer periodoFerias) throws DAOException{
		try {
			StringBuilder hql = new StringBuilder("FROM CalendarioAcademico WHERE ativo=trueValue()  ");
			if (unidade == null)
				hql.append(" AND (unidade is null or unidade.id=" + Unidade.UNIDADE_DIREITO_GLOBAL + ")");
			else
				hql.append(" AND unidade.id ="+unidade.getId());
			
			if (nivel != null && NivelEnsino.isValido(nivel))
				hql.append(" AND nivel='"+nivel+"'");
			
			if( modalidade == null ){
				if(curso == null)
					hql.append(" AND modalidade.id is null ");
				else if(curso.getModalidadeEducacao() != null)
					hql.append(" AND modalidade.id = " + curso.getModalidadeEducacao().getId());
			} else
				hql.append(" AND modalidade.id ="+modalidade.getId());
			
			if (convenio == null)
				hql.append(" AND convenio is null ");
			else
				hql.append(" AND convenio.id ="+convenio.getId());
			
			if (curso == null)
				hql.append(" AND curso is null ");
			else
				hql.append(" AND curso.id ="+curso.getId());
			
			if (ano == null && periodo == null) {
				hql.append(" AND vigente=trueValue() ");
			} else {
				hql.append(" AND ano = " + ano );
				if(periodoFerias == null)
					hql.append(" AND periodo=" + periodo );
				else{
					hql.append(" AND periodoFeriasVigente=" + periodoFerias );
					hql.append(" AND vigente=trueValue() ");					
				}
			}
			return (CalendarioAcademico) getSession().createQuery(hql.toString()).setMaxResults(1).uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * 
	 * Busca todos os calendário acadêmico de acordo com os seguintes parâmetros: unidade, nível, modalidade, curso e convênio.
	 * 
	 * @param unidade
	 * @param nivel
	 * @param modalidade
	 * @param convenio
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<CalendarioAcademico> findTodosByParametros(Unidade unidade, char nivel, ModalidadeEducacao modalidade,
			ConvenioAcademico convenio, Curso curso) throws DAOException{
		int unid = unidade == null?0:unidade.getId();
		int moda = modalidade == null?0:modalidade.getId();
		int conv = convenio == null?0:convenio.getId();
		int cur = curso == null?0:curso.getId();
		return findTodosByParametros(unid, nivel, moda, conv, cur);
	}

	/**
	 * 
	 * Busca todos os calendário acadêmico de acordo com os seguintes parâmetros: ano, período, unidade e nível.
	 * 
	 * @param unidade
	 * @param nivel
	 * @param modalidade
	 * @param convenio
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<CalendarioAcademico> findCalendariosByParametros( Unidade unidade, Character nivel,
			ModalidadeEducacao modalidade, ConvenioAcademico convenio, Curso curso) throws DAOException{
		try {
			StringBuilder hql = new StringBuilder("FROM CalendarioAcademico ");
			hql.append("WHERE ativo=trueValue()");						
				
			if (unidade != null && unidade.getId() > 0)
				hql.append(" AND unidade.id ="+unidade.getId());		
			
			if (NivelEnsino.isValido(nivel))
				if (NivelEnsino.isAlgumNivelStricto(nivel))
					hql.append(" AND nivel='"+NivelEnsino.STRICTO+"'");
				else
					hql.append(" AND nivel='"+nivel+"'");
			
			if (modalidade != null && modalidade.getId() > 0)
				hql.append(" AND ( modalidade.id IS NULL OR  modalidade.id != "+ModalidadeEducacao.PRESENCIAL+ ")");

			if (convenio != null && convenio.getId() > 0)
				hql.append(" AND convenio.id IS NOT NULL");
			else
				hql.append(" AND convenio.id IS NULL");
			
			if (curso != null && curso.getId() > 0)
				hql.append(" AND curso.id ="+curso.getId());
			else
				hql.append(" AND curso IS NULL");
				
			hql.append(" order by ano desc, periodo desc, modalidade desc");
			
			@SuppressWarnings("unchecked")			
			Collection<CalendarioAcademico> lista = getSession().createQuery(hql.toString()).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	/**
	 * 
	 * Busca todos os calendários acadêmicos ativos  de acordo com os seguintes parâmetros:  unidade, nível, modalidade, convênio e curso.
	 * 
	 * @param unidade
	 * @param nivel
	 * @param modalidade
	 * @param convenio
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<CalendarioAcademico> findCalendariosByParametrosDocente( Unidade unidade, Character nivel,
			ModalidadeEducacao modalidade, ConvenioAcademico convenio, Curso curso) throws DAOException{
		try {
			StringBuilder hql = new StringBuilder("FROM CalendarioAcademico ");
			hql.append("WHERE ativo=trueValue()");						
				
			if (unidade != null && unidade.getId() > 0)
				hql.append(" AND unidade.id ="+unidade.getId());		
			
			if (NivelEnsino.isValido(nivel))
				if (NivelEnsino.isAlgumNivelStricto(nivel) && !ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO))
					hql.append(" AND nivel='"+NivelEnsino.STRICTO+"'");
				else
					hql.append(" AND nivel='"+nivel+"'");
			
			if (modalidade != null && modalidade.getId() > 0)
				hql.append(" AND modalidade.id ="+modalidade.getId());

			if (convenio != null && convenio.getId() > 0)
				hql.append(" AND convenio.id ="+convenio.getId());
			
			if (curso != null && curso.getId() > 0)
				hql.append(" AND curso.id ="+curso.getId());

			hql.append(" order by ano desc, periodo desc,  modalidade desc");
			
			@SuppressWarnings("unchecked")			
			Collection<CalendarioAcademico> lista = getSession().createQuery(hql.toString()).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	
	
	/**
	 * 
	 * Busca todos os calendário acadêmicos ativos de acordo com os seguintes parâmetros:  unidade, nível, modalidade, convênio e curso.
	 * 
	 * @param unidadeId
	 * @param nivel
	 * @param modalidade
	 * @param convenio
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public Collection<CalendarioAcademico> findTodosByParametros(int unidadeId, char nivel, int modalidade, int convenio, int curso) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(CalendarioAcademico.class);
			c.add(Expression.eq("nivel", nivel));
			c.add(Expression.eq("ativo", Boolean.TRUE));
			if (unidadeId > 0)
				c.add(Expression.eq("unidade", new Unidade(unidadeId)));
			else
				c.add(Expression.isNull("unidade"));
			if (modalidade > 0)
				c.add(Expression.eq("modalidade", new ModalidadeEducacao(modalidade)));
			else
				c.add(Expression.isNull("modalidade"));
			if (convenio > 0)
				c.add(Expression.eq("convenio", new ConvenioAcademico(convenio)));
			else
				c.add(Expression.isNull("convenio"));
			if (curso > 0 )
				c.add(Expression.eq("curso", new Curso(curso)));
			else
				c.add(Expression.isNull("curso"));
			c.addOrder(Order.desc("ano"));
			c.addOrder(Order.desc("periodo"));
			
			@SuppressWarnings("unchecked")
			Collection<CalendarioAcademico> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * 
	 * Busca o próximo calendário acadêmico para um determinado ano, período, unidade e nível.
	 * 
	 * @param ano
	 * @param periodo
	 * @param unidadeId
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public CalendarioAcademico findProximo(int ano, int periodo, int unidadeId, char nivel, ConvenioAcademico conv) throws DAOException{
		try {
			// busca por períodos do mesmo ano
			Criteria c = getSession().createCriteria(CalendarioAcademico.class);
			c.add(Expression.eq("ano", ano));
			c.add(Expression.gt("periodo", periodo));
			c.add(Expression.eq("unidade", new Unidade(unidadeId)));
			c.add(Expression.isNull("modalidade"));
			c.add(Expression.eq("nivel", nivel));
			c.add(Expression.eq("ativo", Boolean.TRUE));
			if (conv != null)
				c.add(Expression.eq("convenio.id", conv.getId()));
			else
				c.add(Expression.isNull("convenio"));
			c.setMaxResults(1);
			c.addOrder(Order.asc("periodo"));
			Object mesmoAno = c.uniqueResult();
			if (mesmoAno != null)
				return (CalendarioAcademico) mesmoAno;

			// busca pelo primeiro período do próximo ano
			Criteria c2 = getSession().createCriteria(CalendarioAcademico.class);
			c2.add(Expression.gt("ano", ano));
			c2.add(Expression.eq("unidade", new Unidade(unidadeId)));
			c2.add(Expression.isNull("modalidade"));
			if (conv != null)
				c.add(Expression.eq("convenio.id", conv.getId()));
			else
				c.add(Expression.isNull("convenio"));
			c2.add(Expression.eq("nivel", nivel));
			c2.setMaxResults(1);
			c2.addOrder(Order.asc("ano"));
			c2.addOrder(Order.asc("periodo"));
			c.add(Expression.eq("ativo", Boolean.TRUE));
			return (CalendarioAcademico) c2.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * 
	 * Retorna verdadeiro se existir ao menos um calendário ativo para um 
	 * curso e nível específico. 
	 * 
	 * @param idCurso
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public boolean existeCalendarioEspecificoComConvenio(int idCurso, char nivel) throws DAOException{
		try {
			Query query = getSession().createQuery(
					"SELECT COUNT(*) FROM CalendarioAcademico "
							+ "WHERE ativo=trueValue()"
							+ "AND curso.id = "+idCurso
							+" AND nivel='"+nivel+"'");
					
			// resultado
			return (Integer.valueOf(query.uniqueResult().toString()) > 0 );
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}
	
	/**
	 * Retorna verdadeiro se existir ao menos um calendário ativo para a 
	 * unidade global e nível específico, independente da modalidade.
	 * Estes calendários não devem estar associados a cursos ou convênios. 
	 * 
	 * 
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public boolean existeCalendarioUnidadeGlobalByNivel(char nivel) throws DAOException{
		try {
			Query query = getSession().createQuery(
					" SELECT COUNT(*) " +
					" FROM CalendarioAcademico cal " +
					" WHERE cal.ativo = trueValue() and cal.unidade.id = :UNIDADE_GLOBAL " +
					" AND cal.curso is null and cal.convenio is null and cal.nivel='"+nivel+"'");					
			query.setInteger("UNIDADE_GLOBAL", UnidadeGeral.UNIDADE_DIREITO_GLOBAL);			
			return (Integer.valueOf(query.uniqueResult().toString()) > 0 );
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}		
	}
	

}
