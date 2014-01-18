package br.ufrn.sigaa.ensino.metropoledigital.dao;

import java.util.Collection;
import java.util.Collections;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.PeriodoAvaliacao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;


/**
 * Dao responsável pelas consultas relacionadas as turmas de entrada do curso técnico do IMD.
 * 
 * @author Rafael Silva
 * @author Rafael Barros
 *
 */
public class TurmaEntradaTecnicoDao extends GenericSigaaDAO{
	
	/**
	 * Retorna todas as turmas de entradada da unidade informada. 
	 * 
	 * @param unidadeId
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TurmaEntradaTecnico> findAll(int unidadeId, char nivel, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from TurmaEntradaTecnico where ativo = trueValue()");
			if (unidadeId > 0)
				hql.append(" and unidade.id = " + unidadeId );
			if (nivel != 0)
				hql.append(" and estruturaCurricularTecnica.cursoTecnico.nivel ='"+nivel+"'");
			hql.append(" order by anoReferencia desc, periodoReferencia desc, estruturaCurricularTecnica.cursoTecnico.nome asc ");
			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna a listagem dos Discentes Tecnico que estão vinculados a Turma de Entrada na qual a frequência será realizada.
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
			cDiscente.add(Expression.eq("status", StatusDiscente.ATIVO));
			
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
	 * Retorna a listagem dos Períodos de Avaliação que estão vinculados ao Cronograma de Execução da Turma de Entrada na qual a frequência será realizada.
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
	 * Lista as turmas de entrada da especialização informada.
	 * 
	 * @param idEspecializacao
	 * @return
	 * @throws DAOException
	 */
	public TurmaEntradaTecnico findTurmaEntradaByEspecializacao(int idEspecializacao) throws DAOException{
		Criteria c = getSession().createCriteria(TurmaEntradaTecnico.class);
		Criteria cEsp = c.createCriteria("especializacao");
		Criteria cdadosTurmaIMD = c.createCriteria("dadosTurmaIMD");
		Criteria cCrono= cdadosTurmaIMD.createCriteria("cronograma");
		cEsp.add(Expression.eq("id", idEspecializacao));
		return (TurmaEntradaTecnico) c.uniqueResult();
	}
	
	/**
	 * Retorna as turmas de entrada de um determinado curso baseado na estrutura curricular.
	 * ativa desse curso
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TurmaEntradaTecnico> findByCursoTecnico(int idCurso, int turmaEntrada, Integer anoReferencia, Integer periodoReferencia) throws DAOException {
		try {
			String hql = "from TurmaEntradaTecnico te" +
					" where te.ativo=trueValue() " +
					" and te.cursoTecnico.id=:idCurso ";
			
					if (turmaEntrada != 0) {
						hql += " and te.id <> :idTurmaEntrada";
						hql += " and te.especializacao is not null";
					}

					if (anoReferencia != null && anoReferencia != 0)
						hql += " and te.anoReferencia = :ano";

					if (periodoReferencia != null && periodoReferencia != 0)
						hql += " and te.periodoReferencia = :periodo";
					
					hql += " order by te.anoReferencia desc, te.periodoReferencia desc";

			Query q = getSession().createQuery(hql);
			q.setInteger("idCurso", idCurso);
			if (turmaEntrada > 0)
				q.setInteger("idTurmaEntrada", turmaEntrada);
			if (anoReferencia != null && anoReferencia != 0)
				q.setInteger("ano", anoReferencia);
			if (periodoReferencia != null && periodoReferencia != 0)
				q.setInteger("periodo", periodoReferencia);
			return q.list();
        } catch (Exception e) {
            throw new DAOException(e);
        }

	}
	
	
	/**
	 * Retorna as turmas de entrada de um determinado curso baseado na estrutura curricular.
	 * ativa desse curso
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TurmaEntradaTecnico> findByCursoTecnico(int idCurso, int turmaEntrada, Integer anoReferencia, Integer periodoReferencia, int idCronograma) throws DAOException {
		try {
			String hql = "select  te from TurmaEntradaTecnico te" +
					" inner join te.dadosTurmaIMD dt" +
					" where te.ativo=trueValue() " +
					" and te.cursoTecnico.id=:idCurso ";
			
					if (turmaEntrada != 0) {
						hql += " and te.id <> :idTurmaEntrada";
						hql += " and te.especializacao is not null";
					}

					if (anoReferencia != null && anoReferencia != 0)
						hql += " and te.anoReferencia = :ano";

					if (periodoReferencia != null && periodoReferencia != 0)
						hql += " and te.periodoReferencia = :periodo";
					
					if (idCronograma!=0) {
						hql += " and dt.cronograma.id= :idCronograma";
					}
					
					hql += " order by te.anoReferencia desc, te.periodoReferencia desc";

			Query q = getSession().createQuery(hql);
			q.setInteger("idCurso", idCurso);
			if (turmaEntrada > 0)
				q.setInteger("idTurmaEntrada", turmaEntrada);
			if (anoReferencia != null && anoReferencia != 0)
				q.setInteger("ano", anoReferencia);
			if (periodoReferencia != null && periodoReferencia != 0)
				q.setInteger("periodo", periodoReferencia);
			if (idCronograma != 0)
				q.setInteger("idCronograma", idCronograma);
			return q.list();
        } catch (Exception e) {
            throw new DAOException(e);
        }

	}
	
	
	/**
	 * Retorna a listagem das Turmas de Entrada que estão vinculadas a OpcaoPoloGrupo informada.
	 * 
	 * @param idOpcaoPoloGrupo
	 * @return Coleção de Turmas que estão vinculadas a OpcaoPoloGrupo informada
	 * @throws DAOException
	 */
	public Collection<TurmaEntradaTecnico> findTurmaByOpcaoPoloGrupo(int idOpcaoPoloGrupo) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(TurmaEntradaTecnico.class);
			Criteria cOpcaoPolo = c.createCriteria("opcaoPoloGrupo");
			c.createCriteria("dadosTurmaIMD");
			c.createCriteria("especializacao");
			
			cOpcaoPolo.add(Expression.eq("id",idOpcaoPoloGrupo));
			
			Collection<TurmaEntradaTecnico> listTurmas = null;
			
			if(c.list() != null)
			{
				listTurmas = c.list();
			} else {
				listTurmas = Collections.emptyList();
			}
			
			return listTurmas;
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	
	/**
	 * Retorna a listagem das Turmas de Entrada que estão vinculadas a OpcaoPoloGrupo, Módulo e periodo/ ano referencia informados.
	 * 
	 * @param idOpcaoPoloGrupo, idModulo, anoReferencia, periodoReferencia
	 * @return Coleção de Turmas que estão vinculadas a OpcaoPoloGrupo, Módulo e periodo/ ano referencia informados.
	 * @throws DAOException
	 */
	public Collection<TurmaEntradaTecnico> findTurmaByOpcaoPoloGrupoAndModuloAndAnoPeriodo(int idOpcaoPoloGrupo, Integer idModulo, Integer anoReferencia, Integer periodoReferencia) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(TurmaEntradaTecnico.class);
			Criteria cOpcaoPolo = c.createCriteria("opcaoPoloGrupo");
			Criteria cDados = c.createCriteria("dadosTurmaIMD");
			Criteria cCrono = cDados.createCriteria("cronograma");
			Criteria cModulo = cCrono.createCriteria("modulo");
			c.createCriteria("especializacao");
			
			cOpcaoPolo.add(Expression.eq("id",idOpcaoPoloGrupo));
			
			if(idModulo != null && idModulo > 0) {
				cModulo.add(Expression.eq("id",idModulo));
			}
			
			if(anoReferencia != null && anoReferencia > 0) {
				c.add(Expression.eq("anoReferencia", anoReferencia));
			}
			
			if(periodoReferencia != null && periodoReferencia > 0) {
				c.add(Expression.eq("periodoReferencia", periodoReferencia));
			}
			
			Collection<TurmaEntradaTecnico> listTurmas = null;
			
			if(c.list() != null)
			{
				listTurmas = c.list();
			} else {
				listTurmas = Collections.emptyList();
			}
			
			return listTurmas;
		} catch (Exception e) {
	        throw new DAOException(e);
	    }
	}
	
	
	/**
	 * Retorna a listagem dos discentes que não estão vinculadas a uma turma e possuem vinculo com a OpcaoPoloGrupo informada.
	 * 
	 * @param idOpcaoPoloGrupo
	 * @return Coleção de Discentes não enturmados que estão vinculadas a OpcaoPoloGrupo informada
	 * @throws DAOException
	 */
	public Collection<DiscenteTecnico> findDiscentesSemTurmaByOpcaoPoloGrupo(int idOpcaoPoloGrupo) throws DAOException{
		try {
			Criteria c = getSession().createCriteria(DiscenteTecnico.class);
			Criteria cOpcaoPolo = c.createCriteria("opcaoPoloGrupo");
			Criteria cDisc = c.createCriteria("discente");
			cDisc.createCriteria("pessoa");
			
			cOpcaoPolo.add(Expression.eq("id",idOpcaoPoloGrupo));
			
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
	
}
