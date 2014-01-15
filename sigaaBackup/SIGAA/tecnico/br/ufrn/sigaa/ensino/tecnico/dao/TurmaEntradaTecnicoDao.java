/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/09/2006'
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Dao para consultar informações sobre as turmas de entrada do ensino técnico
 * 
 * @author Andre M Dantas
 *
 */
public class TurmaEntradaTecnicoDao extends GenericSigaaDAO  {

	/**
	 * Retorna as turmas de entrada de um determinado curso baseado na estrutura curricular
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
					" and te.estruturaCurricularTecnica.cursoTecnico.id=:idCurso " +
					" and te.estruturaCurricularTecnica.ativa=trueValue() ";
			
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
	
	public List <SelectItem> findTurmasEntradaDisponiveisParaDiscenteIMD (int idDiscente) throws DAOException {
		ConvocacaoProcessoSeletivoDiscenteTecnico conv = findByExactField(ConvocacaoProcessoSeletivoDiscenteTecnico.class, "discente.id", idDiscente, true);
		
		@SuppressWarnings("unchecked")
		List <Object []> ls = getSession().createSQLQuery("select t.id_turma_entrada, e.descricao, o.descricao_horario, t.capacidade - count(d.id_discente) " +

								"from tecnico.turma_entrada_tecnico t " +
								"join tecnico.especializacao_turma_entrada e using (id_especializacao_turma_entrada) " +
								
								"join tecnico.oferta_modulo_especializacao o using (id_especializacao_turma_entrada) " +
								
								"left join tecnico.discente_tecnico d using (id_turma_entrada) " +
								"where t.id_opcao_polo_grupo = " + conv.getInscricaoProcessoSeletivo().getOpcao().getId() + " " +
								"group by t.id_turma_entrada, e.descricao, o.descricao_horario, t.capacidade " +
								"having t.capacidade - count(d.id_discente) > 0 " +
								"order by descricao").list();
		
		List <SelectItem> rs = new ArrayList <SelectItem> ();
		for (Object [] l : ls)
			rs.add(new SelectItem (l[0], l[1] + " - " + l[2] + " ("+l[3]+" vagas)"));
		
		return rs;
	}
	
	/**
	 * Retorna as turmas de entrada de um determinado currículo
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TurmaEntradaTecnico> findByCurriculo(int idCurriculo) throws DAOException {
		try {
			Query q = getSession().createQuery("from TurmaEntradaTecnico te" +
					" where te.ativo=trueValue() and te.estruturaCurricularTecnica.id=:idCurriculo and " +
					"te.estruturaCurricularTecnica.ativa=trueValue() " +
			"order by te.anoReferencia desc, te.periodoReferencia desc");
			q.setInteger("idCurriculo", idCurriculo);	
			return q.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
		
	}


	/**
	 * Retorna todas as turmas de entrada do ensino técnico podendo filtrar por ano, período, unidade ou nível
	 * @param ano
	 * @param periodo
	 * @param unidadeId
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TurmaEntradaTecnico> findByAnoPeriodo(int ano, int periodo, int unidadeId, char nivel, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from TurmaEntradaTecnico where ativo=trueValue()");
			if (unidadeId > 0)
				hql.append(" and unidade.id = " + unidadeId );
			if (nivel != 0)
				hql.append(" and estruturaCurricularTecnica.cursoTecnico.nivel ='"+nivel+"'");
			if (ano > 0)
				hql.append(" and anoReferencia = " + ano + " ");
			if (periodo > 0)
				hql.append(" and periodoReferencia = " + periodo + " ");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			return q.list();
		} catch (Exception e) {
			 throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Valida a regra de unicidade para turma de entrada do ensino técnico:
	 * não pode haver mais de registro com os valores iguais para
	 * E.C., ano, período, unidade e nível.
	 * @param turmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public boolean validateUniqueRules(TurmaEntradaTecnico turmaEntrada) throws DAOException {
		Criteria c = getSession().createCriteria(TurmaEntradaTecnico.class);
		c.add(Expression.ne("id", turmaEntrada.getId()));
		c.add(Expression.eq("unidade", turmaEntrada.getUnidade()));
		c.add(Expression.eq("especializacao", turmaEntrada.getEspecializacao()));
		c.add(Expression.eq("anoReferencia", turmaEntrada.getAnoReferencia()));
		c.add(Expression.eq("periodoReferencia", turmaEntrada.getPeriodoReferencia()));
		c.add(Expression.eq("estruturaCurricularTecnica", turmaEntrada.getEstruturaCurricularTecnica()));
		c.createCriteria("estruturaCurricularTecnica").createCriteria("cursoTecnico").
		add(Expression.eq("nivel", turmaEntrada.getEstruturaCurricularTecnica().getCursoTecnico().getNivel()));
		Collection<?> col = c.list();
		return (col == null || col.size() == 0);
	}

	/**
	 * Busca todas as turmas de entrada da unidade e nível informados
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
	 * Busca todas as turmas de entrada do curso, Currículo e/ou ano informados.
	 */
	@SuppressWarnings("unchecked")
	public Collection<TurmaEntradaTecnico> findByAnoCursoEspecializacao(Integer idCurso, Integer idCurriculo, Integer ano) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			
			hql.append(" select te " +
					   " from TurmaEntradaTecnico te" +
					   " inner join te.estruturaCurricularTecnica as estr" +
					   " inner join estr.cursoTecnico as curso"+
					   " where te.ativo=trueValue() and curso.ativo = " + Boolean.TRUE);
			
			if (idCurso != null) 
				hql.append(" and te.estruturaCurricularTecnica.cursoTecnico.id=:idCurso " +
						   " and te.estruturaCurricularTecnica.ativa=trueValue() ");
			if (idCurriculo != null) 
				hql.append(" and te.especializacao.id=:idCurriculo ");
			if (ano != null) 
				hql.append(" and te.anoReferencia =:ano");
			
			hql.append(" order by te.anoReferencia desc, te.periodoReferencia desc");
			
			Query q = getSession().createQuery(hql.toString());
			if (idCurso != null) q.setInteger("idCurso", idCurso);
			if (idCurriculo != null) q.setInteger("idCurriculo", idCurriculo);
			if (ano != null) q.setInteger("ano", ano);
			return q.list();
        } catch (Exception e) {
            throw new DAOException(e);
        }

	}

	/**
	 * Verifica se a turm excedeu a capidadade definida.
	 * 
	 * @param turmaEntrada
	 * @return
	 * @throws DAOException
	 */
	public boolean isExcedeuCapacidade(TurmaEntradaTecnico turmaEntrada) throws DAOException {
		Boolean result = (Boolean) getSession().createSQLQuery("select t.capacidade < count(*) + 1" +
				" from tecnico.discente_tecnico dt" +
				" join discente d on (d.id_discente=dt.id_discente)" +
				" join tecnico.turma_entrada_tecnico  t on (dt.id_turma_entrada=t.id_turma_entrada)" +
				" where d.status <> ?" +
				" and dt.id_turma_entrada = ?" +
				" group by t.capacidade").setInteger(0, StatusDiscente.EXCLUIDO).setInteger(1, turmaEntrada.getId()).uniqueResult();
		return result != null ? result.booleanValue() : false;
	}
}