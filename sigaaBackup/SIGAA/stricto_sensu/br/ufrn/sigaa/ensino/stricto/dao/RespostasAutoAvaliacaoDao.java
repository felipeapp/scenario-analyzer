/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.stricto.dominio.RespostasAutoAvaliacao;

/**
 * 
 * Classe respons�vel por consultas espec�ficas �s repostas dadas na Auto Avalia��o da P�s Gradua��o.
 * 
 * @author �dipo Elder F. de Melo
 *
 */
public class RespostasAutoAvaliacaoDao extends GenericSigaaDAO {

	/**
	 * Retorna as respostas dadas na auto avalia��o de uma unidade, para um determinado calend�rio. 
	 * @param idUnidade
	 * @param idCalendarioAutoAvaliacao
	 * @return
	 * @throws DAOException
	 */
	public RespostasAutoAvaliacao findByPrograma(int idUnidade, int idCalendarioAutoAvaliacao) throws DAOException {
		Criteria c = getSession().createCriteria(RespostasAutoAvaliacao.class);
		c.add(Restrictions.eq("unidade.id", idUnidade));
		c.add(Restrictions.eq("calendario.id", idCalendarioAutoAvaliacao));
		return (RespostasAutoAvaliacao) c.uniqueResult();
	}
	
	/**
	 * Retorna as respostas dadas na auto avalia��o de um curso, para um determinado calend�rio. 
	 * @param idUnidade
	 * @param idCalendarioAutoAvaliacao
	 * @return
	 * @throws DAOException
	 */
	public RespostasAutoAvaliacao findByCurso(int idCurso, int idCalendarioAutoAvaliacao) throws DAOException {
		Criteria c = getSession().createCriteria(RespostasAutoAvaliacao.class);
		c.add(Restrictions.eq("curso.id", idCurso));
		c.add(Restrictions.eq("calendario.id", idCalendarioAutoAvaliacao));
		return (RespostasAutoAvaliacao) c.uniqueResult();
	}
	
	/**
	 * Retorna uma cole��o de respostas dadas na auto avalia��o de uma unidade. 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<RespostasAutoAvaliacao> findByPrograma(int idUnidade) throws DAOException {
		Criteria c = getSession().createCriteria(RespostasAutoAvaliacao.class);
		c.add(Restrictions.eq("unidade.id", idUnidade));
		@SuppressWarnings("unchecked")
		Collection<RespostasAutoAvaliacao> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna uma cole��o de respostas dadas na auto avalia��o de uma unidade. 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<RespostasAutoAvaliacao> findByCurso(int idCurso) throws DAOException {
		Criteria c = getSession().createCriteria(RespostasAutoAvaliacao.class);
		c.add(Restrictions.eq("curso.id", idCurso));
		@SuppressWarnings("unchecked")
		Collection<RespostasAutoAvaliacao> lista = c.list();
		return lista;
	}

	/**
	 * Retorna as respostas dadas na auto avalia��o para um determinado calend�rio.
	 * @param idCalendarioAutoAvaliacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<RespostasAutoAvaliacao> findByCalendarioAutoAvaliacao(int idCalendarioAutoAvaliacao) throws DAOException {
		Criteria c = getSession().createCriteria(RespostasAutoAvaliacao.class);
		c.add(Restrictions.eq("calendario.id", idCalendarioAutoAvaliacao));
		@SuppressWarnings("unchecked")
		Collection<RespostasAutoAvaliacao> lista = c.list();
		return lista;
	}

	/** Retorna dados para exibi��o do relat�rio de preenchimento de uma determinada Auto Avalia��o de Stricto Sensu.
	 * @param idCalendarioAutoAvaliacao
	 * @param tipoQuestionario 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<RespostasAutoAvaliacao> findDadosRelatorioPreenchimentoStricto(int idCalendarioAutoAvaliacao) throws HibernateException, DAOException {
		List<RespostasAutoAvaliacao> result = new LinkedList<RespostasAutoAvaliacao>();
		Collection<RespostasAutoAvaliacao> preenchido = findRespostasAutoAvaliacaoPreenchidas(idCalendarioAutoAvaliacao);
		if (!isEmpty(preenchido))
			result.addAll(preenchido);
		// unidades que ainda n�o preencheram a Auto Avalia��o
		String projecao = "unidade.id, unidade.nome";
		String hql = "select " + projecao +
				" from Curso c" +
				" inner join c.unidade unidade" +
				" where c.nivel in " + UFRNUtils.gerarStringIn(NivelEnsino.getNiveisStricto()) +
				" order by unidade.nomeAscii";
		Query r = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		Collection<Unidade> naoPreenchido = HibernateUtils.parseTo(r.list(), projecao, Unidade.class, "unidade");
		for (Unidade unidade : naoPreenchido) {
			boolean contem = false;
			for (RespostasAutoAvaliacao resp : result)
				if (resp.getUnidade().getId() == unidade.getId()) {
					contem = true;
					break;
				}
			if (!contem) {
				RespostasAutoAvaliacao resp = new RespostasAutoAvaliacao();
				resp.setUnidade(unidade);
				resp.setSituacao(null);
				result.add(resp);
			}
		}
		return result;
	}
	
	/** Retorna dados para exibi��o do relat�rio de preenchimento de uma determinada Auto Avalia��o de Lato Sensu.
	 * @param idCalendarioAutoAvaliacao
	 * @param tipoQuestionario 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<RespostasAutoAvaliacao> findDadosRelatorioPreenchimentoLato(int idCalendarioAutoAvaliacao) throws HibernateException, DAOException {
		List<RespostasAutoAvaliacao> result = new LinkedList<RespostasAutoAvaliacao>();
		Collection<RespostasAutoAvaliacao> preenchido = findRespostasAutoAvaliacaoPreenchidas(idCalendarioAutoAvaliacao);
		if (!isEmpty(preenchido))
			result.addAll(preenchido);
		// unidades que ainda n�o preencheram a Auto Avalia��o
		String projecao = "curso.id, curso.nome";
		String hql = "select " + projecao +
				" from Curso curso, CursoLato cursoLato" +
				" where curso.id = cursoLato.id" +
				" and cursoLato.propostaCurso.situacaoProposta.id = " + SituacaoProposta.ACEITA +
				" and curso.dataInicio <= :hoje" +
				" and curso.dataFim >= :hoje"  +
				" order by curso.nomeAscii";
		Query r = getSession().createQuery(hql);
		r.setDate("hoje", new Date());
		@SuppressWarnings("unchecked")
		Collection<Curso> naoPreenchido = HibernateUtils.parseTo(r.list(), projecao, Curso.class, "curso");
		for (Curso curso : naoPreenchido) {
			boolean contem = false;
			for (RespostasAutoAvaliacao resp : result)
				if (resp.getCurso().getId() == curso.getId()) {
					contem = true;
					break;
				}
			if (!contem) {
				RespostasAutoAvaliacao resp = new RespostasAutoAvaliacao();
				resp.setCurso(curso);
				resp.setSituacao(null);
				result.add(resp);
			}
		}
		return result;
	}

	/** Retorna uma cole��o de respostas de auto avalia��o preenchidas de um determinado calend�rio.
	 * @param idCalendarioAutoAvaliacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<RespostasAutoAvaliacao> findRespostasAutoAvaliacaoPreenchidas(
			int idCalendarioAutoAvaliacao) throws DAOException {
		// unidades que preencheram at� o momento
		String projecao = "resp.id, resp.situacao," +
				" c.id as resp.curso.id, c.nome as resp.curso.nome," +
				" u.id as resp.unidade.id, u.nome as resp.unidade.nome";
		String hql = "select " + HibernateUtils.removeAliasFromProjecao(projecao) +
				" from RespostasAutoAvaliacao resp" +
				" left join resp.curso c" +
				" left join resp.unidade u" +
				" where resp.calendario.id = :idCalendarioAutoAvaliacao" +
				" order by u.nomeAscii, c.nomeAscii";
		Query q = getSession().createQuery(hql);
		q.setInteger("idCalendarioAutoAvaliacao", idCalendarioAutoAvaliacao);
		@SuppressWarnings("unchecked")
		Collection<RespostasAutoAvaliacao> preenchido = HibernateUtils.parseTo(q.list(), projecao, RespostasAutoAvaliacao.class, "resp");
		return preenchido;
	}
}
