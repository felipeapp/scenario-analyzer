/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/07/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/** DAO responsável por consultas específicas relativas a oferta de vagas por curso.
 * @author Édipo Elder F. Melo
 *
 */
public class OfertaVagasCursoDao extends GenericSigaaDAO {

	/** Retorna uma coleção de OfertaVagasCurso por ano e forma de ingresso.
	 * @param ano
	 * @param idFormaIngresso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<OfertaVagasCurso> findAllByAnoFormaIngresso(int ano,
			int idFormaIngresso, char nivelEnsino) throws DAOException {
		Criteria c = getSession().createCriteria(OfertaVagasCurso.class);
		c.add(Expression.eq("ano", ano));
		c.createCriteria("formaIngresso").add(
				Expression.eq("id", idFormaIngresso));
		if (nivelEnsino == NivelEnsino.GRADUACAO) {
			Criteria cCurso = c.createCriteria("matrizCurricular").createCriteria("curso");
			Criteria cArea = cCurso.createCriteria("areaVestibular");
			Criteria cMunicipio = cCurso.createCriteria("municipio");
			cMunicipio.addOrder(Order.asc("nome"));
			cArea.addOrder(Order.asc("descricao"));
			cCurso.addOrder(Order.asc("nomeAscii"));
		}
		return c.list();
	}

	/** Retorna uma coleção de OfertaVagasCurso por ano, forma de ingresso e unidade.
	 * @param ano
	 * @param idFormaIngresso
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<OfertaVagasCurso> findAllByAnoFormaIngressoUnidade(
			int ano, int idFormaIngresso, char nivelEnsino, int idUnidade) throws DAOException {
		Criteria c = getSession().createCriteria(OfertaVagasCurso.class);
		c.add(Expression.eq("ano", ano));
		c.createCriteria("formaIngresso").add(
				Expression.eq("id", idFormaIngresso));
		switch(nivelEnsino) {
			case NivelEnsino.GRADUACAO :
				c.createCriteria("matrizCurricular").createCriteria("curso").createCriteria("unidade").add(Expression.eq("id", idUnidade));
				break;
			case NivelEnsino.LATO:
			case NivelEnsino.STRICTO:
			case NivelEnsino.MESTRADO:
			case NivelEnsino.DOUTORADO:
				c.createCriteria("curso").createCriteria("unidade").add(Expression.eq("id", idUnidade));
				break;
		}
		return c.list();
	}

	/** Retorna uma coleção de OfertaVagascurso por ano, forma de ingresso, unidade e processo seletivo.
	 * @param ano
	 * @param idFormaIngresso
	 * @param idUnidade
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<OfertaVagasCurso> findAllByAnoFormaIngressoUnidadeProcessoSeletivo(
			int ano, Integer periodo, int idFormaIngresso, int idUnidade, Integer modalidadeEducacao, Boolean apenasMatrizesAtivas)
			throws DAOException {
		StringBuilder hql = new StringBuilder("select oferta from OfertaVagasCurso oferta" +
				" inner join fetch oferta.formaIngresso formaIngresso" +
				" inner join fetch oferta.matrizCurricular matrizCurricular" +
				" inner join fetch matrizCurricular.curso curso" +
				" inner join fetch curso.unidade unidade" +
				" inner join fetch curso.modalidadeEducacao modalidade" +
				" left join fetch matrizCurricular.habilitacao habilitacao" +
				" left join fetch matrizCurricular.atualizadoPor atualizadoPor" +
				" left join fetch matrizCurricular.criadoPor criadoPor" +
				" left join fetch matrizCurricular.turno turno" +
				" where 1 = 1 ");
			if (idUnidade != 0) 
				hql.append(" and unidade.id = :idUnidade");
			if (idFormaIngresso != 0) 
				hql.append(" and formaIngresso.id = :idFormaIngresso");
			if (ano != 0) 
				hql.append(" and oferta.ano = :ano");
			if ( periodo != null ){
				if (periodo == 1) {
					hql.append(" and oferta.vagasPeriodo1 > 0");
				} else if (periodo == 2) {
					hql.append(" and oferta.vagasPeriodo2 > 0");
				} 
			}
			if (modalidadeEducacao != null)
				hql.append(" and modalidade.id = :idModalidadeEducacao");
			if (apenasMatrizesAtivas != null)
				hql.append(" and matrizCurricular.ativo = :ativo");

		hql.append(" order by curso.municipio.nome, curso.nome, habilitacao.nome, turno.sigla");
		Query q = getSession().createQuery(hql.toString());
		if (idUnidade != 0)
			q.setInteger("idUnidade", idUnidade);
		if (idFormaIngresso != 0)
			q.setInteger("idFormaIngresso", idFormaIngresso);
		if (ano!= 0)
			q.setInteger("ano", ano);
		if (modalidadeEducacao != null)
			q.setInteger("idModalidadeEducacao", modalidadeEducacao);
		if (apenasMatrizesAtivas != null)
			q.setBoolean("ativo", apenasMatrizesAtivas);
		
		@SuppressWarnings("unchecked")
		Collection<OfertaVagasCurso> lista = q.list();
		return lista;
	}

	/** Retorna uma coleção de OfertaVagasCurso por ano, forma de ingresso e área do vestibular.
	 * @param ano
	 * @param idFormaIngresso
	 * @param idAreaVestibular
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<OfertaVagasCurso> findAllByAnoFormaIngressoAreaVestibular(
			int ano, int idFormaIngresso, int idAreaVestibular)
			throws DAOException {
		Criteria c = getSession().createCriteria(OfertaVagasCurso.class);
		c.add(Expression.eq("ano", ano));
		c.createCriteria("formaIngresso").add(
				Expression.eq("id", idFormaIngresso));
		c.createCriteria("matrizCurricular").createCriteria("curso")
				.createCriteria("areaVestibular").add(
						Expression.eq("id", idAreaVestibular));
		return c.list();
	}

	/** Retorna uma coleção de OfertaVagasCurso por ano, forma de ingresso, área de conhecimento do vestibular e município.
	 * @param ano
	 * @param idFormaIngresso
	 * @param idAreaVestibular
	 * @param idMunicipio
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<OfertaVagasCurso> findAllByAnoFormaIngressoAreaVestibularMunicipio(
			int ano, int idFormaIngresso, int idAreaVestibular, int idMunicipio)
			throws DAOException {
		Criteria c = getSession().createCriteria(OfertaVagasCurso.class);
		c.add(Expression.eq("ano", ano));
		c.createCriteria("formaIngresso").add(
				Expression.eq("id", idFormaIngresso));
		c.createCriteria("matrizCurricular").createCriteria("curso").add(
				Expression.eq("municipio.id", idMunicipio)).createCriteria(
				"areaVestibular").add(Expression.eq("id", idAreaVestibular));
		return c.list();
	}

	/** Retorna uma coleção de anos que possuem oferta de vagas cadastradas.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Integer> findAllAnosCadastrados() throws DAOException {
		String hql = "select distinct ano " + " from OfertaVagasCurso"
				+ " order by ano desc";
		Query q = getSession().createQuery(hql);
		return q.list();
	}

	/**
	 * Retorna uma coleção de municípios onde há oferta de vagas para
	 * determinado ano de entrada e determinada forma de ingresso.
	 * 
	 * @param anoEntrada obrigatório
	 * @param periodoEntrada é opcional, caso seja zero, retorna os municípios com entrada para os dois períodos letivos regulares.
	 * @param idFormaIngresso obrigatório
	 * @param ceres, caso nulo, retorna todos municípios. Caso true, retorna apenas os municípios do CERES. Caso false, retorna todos municípios que não são do CERES. 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Municipio> findAllMunicipiosByEntradaFormaIngresso(int anoEntrada, int periodoEntrada, int idFormaIngresso, Boolean ceres, Integer idMunicipio) throws DAOException {
		StringBuilder hql = new StringBuilder("select distinct c.municipio"
				+ " from OfertaVagasCurso ovc"
				+ " inner join ovc.matrizCurricular as m"
				+ " inner join m.curso as c" 
				+ " inner join ovc.formaIngresso formaIngresso"
				+ " where ovc.ano = :anoEntrada"
				+ " and formaIngresso.id = :idFormaIngresso");
		if (periodoEntrada > 0)
			hql.append(" and ovc.vagasPeriodo" + periodoEntrada+" > 0");
		if (ceres != null) {
			if (ceres) {
				hql.append(" and c.unidade.id = " + Unidade.CERES);
			} else {
				hql.append(" and c.unidade.id != " + Unidade.CERES);
			}
		}
		if (idMunicipio != null)
			hql.append(" and c.municipio.id = " + idMunicipio);
		hql.append(" order by c.municipio.nome");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("anoEntrada", anoEntrada);
		q.setInteger("idFormaIngresso", idFormaIngresso);
		return q.list();
	}

	/**
	 * Retorna uma coleção de oferta de vagas de curso por processo seletivo,
	 * área de conhecimento do vestibular, município e ano.
	 * 
	 * @param idFormaIngresso
	 * @param ano
	 * @param periodo
	 * @param idAreaConhecimento
	 * @param idMunicipio
	 * @return
	 * @throws DAOException
	 */
	public Collection<OfertaVagasCurso> findAllByProcessoSeletivoAreaVestibularMunicipio(int idFormaIngresso, int ano, int periodo, 
			int idAreaConhecimento, int idMunicipio) throws DAOException {
		Criteria c = getSession().createCriteria(OfertaVagasCurso.class);
		c.add(Restrictions.eq("ano", ano));
		if (periodo > 0) {
			c.add(Restrictions.gt("vagasPeriodo" + periodo, 0));	
		}
		c.createCriteria("formaIngresso").add(Expression.eq("id", idFormaIngresso));
		Criteria cCurso = c.createCriteria("matrizCurricular").createCriteria("curso");
		Criteria cArea = cCurso.add(
				Expression.eq("municipio.id", idMunicipio)).createCriteria(
				"areaVestibular");
		if (idAreaConhecimento != 0)
			cArea.add(Expression.eq("id", idAreaConhecimento));
		c.setFetchMode("matrizCurricular", FetchMode.JOIN);
		c.setFetchMode("matrizCurricular.curso", FetchMode.JOIN);
		c.setFetchMode("matrizCurricular.areaVestibular", FetchMode.JOIN);
		cArea.addOrder(Order.asc("descricao"));
		cCurso.addOrder(Order.asc("nome"));
		@SuppressWarnings("unchecked")
		Collection<OfertaVagasCurso> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna uma lista de vagas ofertadas em um determinado período 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findByAnoPeriodo(int anoInicio, int anoFim) throws DAOException {			
		String sqlconsulta = 
		" select  ovc.ano, c.id_curso, vagas_periodo_1, vagas_periodo_2, total_vagas"
		+" from  ensino.oferta_vagas_curso ovc"
		+" join curso c on ovc.id_curso = c.id_curso" 
		+" join comum.unidade u on c.id_unidade = u.id_unidade"
		+" join ensino.forma_ingresso fi on ovc.id_forma_ingresso = fi.id_forma_ingresso" 
		+" join graduacao.matriz_curricular mc on ovc.id_matriz_curricular = mc.id_matriz_curricular"
		+" join ensino.turno tu on mc.id_turno = tu.id_turno"
		+" where c.nivel = '"+NivelEnsino.GRADUACAO+"'"
		+" and ano between "+anoInicio+" and "+anoFim
		+" and fi.id_forma_ingresso = "+FormaIngresso.VESTIBULAR.getId()
		+" order by ano, c.nome ";
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}
	
	/** Retorna uma coleção de OfertaVagascurso de uma matrizCurricular 
	 *  do ano atual ou de anos posteriores.
	 * @param ano
	 * @param idMatrzCurricular
	 * @return
	 * @throws DAOException
	 */
	public Collection<OfertaVagasCurso> findOfertasAtuaisByMatrizCurricular(int idMatrizCurricular)
			throws DAOException {
		StringBuilder hql = new StringBuilder("select oferta from OfertaVagasCurso oferta" +
				" where oferta.ano >= " +br.ufrn.arq.util.CalendarUtils.getAnoAtual()+ 
				" and oferta.matrizCurricular.id = " + idMatrizCurricular + 
				" order by oferta.ano");

		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		Collection<OfertaVagasCurso> lista = q.list();
		return lista;
	}

}